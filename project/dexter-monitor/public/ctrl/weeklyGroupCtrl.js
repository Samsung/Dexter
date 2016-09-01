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

monitorApp.controller("WeeklyGroupCtrl", function($scope, $http, $log, $q, DefectService) {

    let minYear;
    let maxYear;
    $scope.years = [];

    let currentFileName = function() {
        return DEFECT_FILENAME_PREFIX + '-' + $scope.curYear + '-' + $scope.curWeek;
    };

    const columnDefs = [
        {field:'year',              displayName:'Year',         width: '8%',    headerTooltip: 'Year'},
        {field:'week',              displayName:'Week',         width: '8%',    headerTooltip: 'Week'},
        {field:'groupName',         displayName:'Group',        width: '20%',   headerTooltip: 'Group'},
        {field:'userCount',         displayName:'User',         width: '8%',    headerTooltip: 'Number of users'},
        {field:'projectCount',      displayName:'Project',      width: '8%',    headerTooltip: 'Number of projects'},
        {field:'allDefectCount',    displayName:'Total',        width: '16%',   headerTooltip: 'Number of defects'},
        {field:'allFix',            displayName:'Fixed',        width: '16%',   headerTooltip: 'Number of fixed defects'},
        {field:'allDis',            displayName:'Dismissed',    width: '16%',   headerTooltip: 'Number of dismissed defects'}
    ];

    initialize();

    function initialize() {
        $scope.gridOptions = createGrid(columnDefs);
        loadDateRange()
            .then(() => {
                $scope.curYear = maxYear;
                $scope.curWeek = $scope.maxWeekOfCurYear;
                loadDefectListByGroup($scope.curYear, $scope.curWeek);
                setGridExportingFileNames($scope.gridOptions, currentFileName());
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
                $scope.years.push(i);
            }
            return DefectService.getMaxWeek(maxYear)
                .then((week) => {
                    $scope.maxWeekOfCurYear = week;
                });
        });
    }

    $scope.setCurrentYearAndReloadData = function(year) {
        $scope.curYear = year;
        DefectService.getMaxWeek($scope.curYear)
            .then((week) => {
                $scope.maxWeekOfCurYear = week;
                $scope.curWeek = $scope.maxWeekOfCurYear;
            })
            .then(() => {
                loadDefectListByGroup($scope.curYear, $scope.curWeek);
                setGridExportingFileNames($scope.gridOptions, currentFileName());
            });
    };

    $scope.setCurrentWeekAndReloadData = function() {
        loadDefectListByGroup($scope.curYear, $scope.curWeek);
        setGridExportingFileNames($scope.gridOptions, currentFileName());
    };

    function loadDefectListByGroup(year, week) {
        $http.get('/api/v2/defect/group/' + year + '/' + week)
            .then((res) => {
                if (!isHttpResultOK(res)) {
                    $log.error('Failed to load defect list');
                    return;
                }

                $scope.gridOptions.data = res.data.rows;
                resizeHeightOfGrid('weeklyGroupGrid', res.data.rows.length);

                drawChart();
            })
            .catch((err) => {
                $log.error(err);
            });
    }

    function drawChart() {
        destroyAllCharts();
        const gridData = $scope.gridOptions.data;
        if (!gridData || gridData.length <= 0) {
            return;
        }

        const backgroundColor = getColorArray(gridData.length);
        drawUserChart(gridData, backgroundColor);
        drawProjectChart(gridData, backgroundColor);
        drawDefectChart(gridData);
    }

    let userChart;
    let projectChart;
    let defectChart;

    function destroyAllCharts() {
        if (userChart) {
            userChart.destroy();
        }
        if (projectChart) {
            projectChart.destroy();
        }
        if (defectChart) {
            defectChart.destroy();
        }
    }

    function drawUserChart(gridData, backgroundColor) {
        userChart = new Chart($("#weekly-group-user"), {
            type: 'pie',
            data: {
                labels: _.map(gridData, 'groupName'),
                datasets: [{
                    data: _.map(gridData, 'userCount'),
                    backgroundColor: backgroundColor
                }]
            },
            options: {
                title: {
                    display: true,
                    text: 'Number of users'
                }
            }
        });
    }

    function drawProjectChart(gridData, backgroundColor) {
        projectChart = new Chart($("#weekly-group-project"), {
            type: 'pie',
            data: {
                labels: _.map(gridData, 'groupName'),
                datasets: [{
                    data: _.map(gridData, 'projectCount'),
                    backgroundColor: backgroundColor
                }]
            },
            options: {
                title: {
                    display: true,
                    text: 'Number of projects'
                }
            }
        });
    }

    function drawDefectChart(gridData) {
        defectChart = new Chart($("#weekly-group-defect"), {
            type: 'radar',
            data: {
                labels: _.map(gridData, 'groupName'),
                datasets: [{
                    label: 'Total',
                    data: _.map(gridData, 'allDefectCount'),
                    backgroundColor: "rgba(179,181,198,0.2)",
                    borderColor: "rgba(179,181,198,1)",
                    pointBackgroundColor: "rgba(179,181,198,1)",
                    pointBorderColor: "#fff",
                    pointHoverBackgroundColor: "#fff",
                    pointHoverBorderColor: "rgba(179,181,198,1)"
                },{
                    label: 'Removal (Fixed+Dismissed)',
                    data: _.map(gridData, (data) => {
                        return data.allFix + data.allDis;
                    }),
                    backgroundColor: "rgba(255,99,132,0.2)",
                    borderColor: "rgba(255,99,132,1)",
                    pointBackgroundColor: "rgba(255,99,132,1)",
                    pointBorderColor: "#fff",
                    pointHoverBackgroundColor: "#fff",
                    pointHoverBorderColor: "rgba(255,99,132,1)"
                }]
            },
            options: {
                title: {
                    display: true,
                    text: 'Number of defects'
                }
            }
        });
    }
});