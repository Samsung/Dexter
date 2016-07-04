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

monitorApp.controller("DefectByGroupCtrl", function($scope, $http, $log, $q, DefectService) {
    let defect = this;
    let minYear;
    let maxYear;
    defect.years = [];

    let currentFileName = function() {
        return DEFECT_FILENAME_PREFIX + '-' + defect.curYear + '-' + defect.curWeek;
    };

    const columnDefs = [
        {field:'year',              displayName:'Year',         width: 80,      headerTooltip: 'Year'},
        {field:'week',              displayName:'Week',         width: 80,      headerTooltip: 'Week'},
        {field:'groupName',         displayName:'Group',        width: 185,     headerTooltip: 'Group'},
        {field:'accountCount',      displayName:'Accout',       width: 80,      headerTooltip: 'Number of accounts'},
        {field:'projectCount',      displayName:'Project',      width: 80,      headerTooltip: 'Number of projects'},
        {field:'allDefectCount',    displayName:'Total',        width: 160,     headerTooltip: 'Number of defects'},
        {field:'allFix',            displayName:'Fixed',        width: 160,     headerTooltip: 'Number of fixed defects'},
        {field:'allExc',            displayName:'Excluded',     width: 160,     headerTooltip: 'Number of excluded defects'}
    ];

    initialize();

    function initialize() {
        defect.gridOptions = createGrid(columnDefs);
        loadDateRange()
            .then(() => {
                defect.curYear = maxYear;
                defect.curWeek = defect.maxWeekOfCurYear;
                loadDefectListByGroup(defect.curYear, defect.curWeek);
                setGridExportingFileNames(defect.gridOptions, currentFileName());
            });
    }

    function loadDateRange() {
        let promises = [];

        promises.push(function() {
            let deferred = $q.defer();
            DefectService.getMinYear()
                .then((year) => {
                    if (year < 0) {
                        alert('Failed to load the minimum year value from server.\n' +
                            'Please contact the system administrator.');
                        deferred.reject();
                    } else {
                        minYear = year;
                        deferred.resolve();
                    }
                });
            return deferred.promise;
        }());
        promises.push(function() {
            let deferred = $q.defer();
            DefectService.getMaxYear()
                .then((year) => {
                    maxYear = year;
                    deferred.resolve();
                });
            return deferred.promise;
        }());

        return $q.all(promises).then(() => {
            for(let i=minYear ; i<=maxYear ; i++) {
                defect.years.push(i);
            }
            return DefectService.getMaxWeek(maxYear)
                .then((week) => {
                    defect.maxWeekOfCurYear = week;
                });
        });
    }

    defect.yearChanged = function(year) {
        defect.curYear = year;
        DefectService.getMaxWeek(defect.curYear)
            .then((week) => {
                defect.maxWeekOfCurYear = week;
                defect.curWeek = defect.maxWeekOfCurYear;
            })
            .then(() => {
                loadDefectListByGroup(defect.curYear, defect.curWeek);
                setGridExportingFileNames(defect.gridOptions, currentFileName());
            });
    };

    defect.weekChanged = function() {
        loadDefectListByGroup(defect.curYear, defect.curWeek);
        setGridExportingFileNames(defect.gridOptions, currentFileName());
    };

    function loadDefectListByGroup(year, week) {
        $http.get('/api/v2/defect/group/' + year + '/' + week)
            .then((res) => {
                if (!isHttpResultOK(res)) {
                    $log.error('Failed to load defect list');
                    return;
                }

                defect.gridOptions.data = res.data.rows;
            })
            .catch((err) => {
                $log.error(err);
            });
    }
});