/**
 * Copyright (c) 2016 Samsung Electronics, Inc.,
 * All rights reserved.
 *
 * Redistribution and use in source and binary forms, with or without
 * modification, are permitted provided that the following conditions are met:
 *
 * * Redistributions of source code must retain the above copyright notice, this
 *   list of conditions and the following disclaimer.
 *
 * * Redistributions in binary form must reproduce the above copyright notice,
 *   this list of conditions and the following disclaimer in the documentation
 *   and/or other materials provided with the distribution.
 *
 * THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS "AS IS"
 * AND ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE
 * IMPLIED WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE ARE
 * DISCLAIMED. IN NO EVENT SHALL THE COPYRIGHT HOLDER OR CONTRIBUTORS BE LIABLE
 * FOR ANY DIRECT, INDIRECT, INCIDENTAL, SPECIAL, EXEMPLARY, OR CONSEQUENTIAL
 * DAMAGES (INCLUDING, BUT NOT LIMITED TO, PROCUREMENT OF SUBSTITUTE GOODS OR
 * SERVICES; LOSS OF USE, DATA, OR PROFITS; OR BUSINESS INTERRUPTION) HOWEVER
 * CAUSED AND ON ANY THEORY OF LIABILITY, WHETHER IN CONTRACT, STRICT LIABILITY,
 * OR TORT (INCLUDING NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY OUT OF THE USE
 * OF THIS SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY OF SUCH DAMAGE.
 */
"use strict";

monitorApp.service('ProjectService', function($http, $log, $q, ServerStatusService) {

    this.getProjectList = function() {
        return $http.get('/api/v2/project-list')
            .then((res) => {
                if (!isHttpResultOK(res)) {
                    $log.error('Failed to get project list');
                    return [];
                }

                return res.data.rows;
            })
            .catch((err) => {
                $log.error(err);
                return [];
            });
    };

    function setUserCount(row, projectName) {
        let deferred = $q.defer();

        $http.get('/api/v2/user-count/' + projectName)
            .then((res) => {
                if (!isHttpResultOK(res)) {
                    $log.error('Failed to get user count of ' + projectName);
                    deferred.resolve();
                    return;
                }
                row.userCount = res.data.value;
                deferred.resolve();
            })
            .catch((err) => {
                $log.error(err);
                deferred.reject();
            });

        return deferred.promise;
    }

    function setDefectCount(row, projectName) {
        let deferred = $q.defer();

        $http.get('/api/v2/defect-status-count/' + projectName)
            .then((res) => {
                if (!isHttpResultOK(res)) {
                    $log.error('Failed to get defect status count of ' + projectName);
                    deferred.resolve();
                    return;
                }
                row.defectCountTotal = res.data.values.defectCountTotal;
                row.defectCountFixed = res.data.values.defectCountFixed;
                row.defectCountDismissed = res.data.values.defectCountDismissed;
                deferred.resolve();
            })
            .catch((err) => {
                $log.error(err);
                deferred.reject();
            });

        return deferred.promise;
    }

    this.getAllCurrentStatusList = function(activeServerList) {
        let promises = [];

        return this.getProjectList()
            .then((rows) => {
                const activeProjectNames = _.map(activeServerList, 'projectName');
                rows.forEach((row) => {
                    if (_.includes(activeProjectNames, row.projectName)) {
                        promises.push(setUserCount(row, row.projectName));
                        promises.push(setDefectCount(row, row.projectName));
                        row.serverStatus = 'Active';
                    } else {
                        row.serverStatus = 'Inactive';
                    }
                });

                return $q.all(promises)
                    .then(() => {
                        rows = _.sortBy(rows, (row) => row.projectName.toLowerCase());
                        return rows;
                    })
                    .catch((err) => {
                        $log.error(err);
                        return rows;
                    });
            });
    };

    this.getCurrentStatusByGroup = function() {
        let promises = [];
        let statusListByProject = [];

        return ServerStatusService.getActiveServerList()
            .then((activeServerList) => {
                statusListByProject = activeServerList;
                statusListByProject.forEach((row) => {
                    promises.push(setUserCount(row, row.projectName));
                    promises.push(setDefectCount(row, row.projectName));
                });

                return $q.all(promises)
                    .then(() => {
                        return createStatusListByGroup();
                    })
                    .catch((err) => {
                        $log.error(err);
                        return [];
                    });
            })
            .catch((err) => {
                $log.error(err);
                return [];
            });


        function createStatusListByGroup() {
            let statusListByGroup = [];
            const groupNameList = _.uniq(_.map(statusListByProject, 'groupName'));

            groupNameList.forEach((groupName) => {
                const groupNameFilteredStatusList = _.filter(statusListByProject, (o) => o.groupName == groupName);
                let userCount = 0;
                let defectCountTotal = 0;
                let defectCountFixed = 0;
                let defectCountDismissed = 0;

                groupNameFilteredStatusList.forEach((row) => {
                    userCount += row.userCount;
                    defectCountTotal += row.defectCountTotal;
                    defectCountFixed += row.defectCountFixed;
                    defectCountDismissed += row.defectCountDismissed;
                });

                statusListByGroup.push({
                    groupName: groupName,
                    projectCount: groupNameFilteredStatusList.length,
                    userCount: (_.isNaN(userCount) ? '' : userCount),
                    defectCountTotal: (_.isNaN(defectCountTotal) ? '' : defectCountTotal),
                    defectCountFixed: (_.isNaN(defectCountFixed) ? '' : defectCountFixed),
                    defectCountDismissed: (_.isNaN(defectCountDismissed) ? '' : defectCountDismissed)
                });
            });

            return _.sortBy(statusListByGroup, 'groupName');
        }
    };

    this.getSnapshotSummary = function() {
        return $http.get('/api/v2/snapshot-summary')
            .then((res) => {
                if (!isHttpResultOK(res)) {
                    $log.error('Failed to get snapshot summary');
                    return [];
                }

                return res.data.rows;
            })
            .catch((err) => {
                $log.error(err);
                return [];
            });
    };
});