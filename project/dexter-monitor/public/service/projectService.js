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

monitorApp.service('ProjectService', function($http, $log, $q) {

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
                row.accountCount = res.data.value;
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
                row.defectCountExcluded = res.data.values.defectCountExcluded;
                deferred.resolve();
            })
            .catch((err) => {
                $log.error(err);
                deferred.reject();
            });

        return deferred.promise;
    }

    this.getAllCurrentStatusList = function() {
        let promises = [];

        return this.getProjectList()
            .then((rows) => {
                rows.forEach((row) => {
                    promises.push(setUserCount(row, row.projectName));
                    promises.push(setDefectCount(row, row.projectName));
                });

                return $q.all(promises)
                    .then(() => {
                        rows = _.sortBy(rows, (row) => {
                            return row.groupName.toLowerCase();
                        });
                        return rows;
                    })
                    .catch((err) => {
                        $log.error(err);
                        return rows;
                    });
            });
    };

});