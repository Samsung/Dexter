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

monitorApp.controller("CurrentProjectCtrl", function($scope, $http, $log, $interval, $window, ProjectService, ServerStatusService, uiGridConstants) {

    const columnDefs = [
        {field:'projectName',           displayName:'Project',          width: '19%',
            headerTooltip: 'Project name', cellTooltip: true},
        {field:'groupName',             displayName:'Group',            width: '17%',   cellClass: 'grid-align',
            headerTooltip: 'Group name', cellTooltip: true},
        {field:'userCount',             displayName:'User',             width: '10%',   cellClass: 'grid-align',
            headerTooltip: 'Number of users', aggregationType: uiGridConstants.aggregationTypes.sum},
        {field:'defectCountTotal',      displayName:'Total',            width: '9%',    cellClass: 'grid-align',
            headerTooltip: 'Number of all defects', aggregationType: uiGridConstants.aggregationTypes.sum},
        {field:'defectCountFixed',      displayName:'Fixed',            width: '9%',    cellClass: 'grid-align',
            headerTooltip: 'Number of fixed defects', aggregationType: uiGridConstants.aggregationTypes.sum},
        {field:'defectCountDismissed',  displayName:'Dismissed',        width: '9%',    cellClass: 'grid-align',
            headerTooltip: 'Number of dismissed defects', aggregationType: uiGridConstants.aggregationTypes.sum},
        {field:'resolvedRatio',         displayName:'Resolved ratio',   width: '11%',   cellClass: 'grid-align',
            headerTooltip: '(Fixed + Dismissed) / Total * 100', footerCellClass: 'grid-align',
            aggregationType: () => `${$scope.resolvedRatioTotal}`,
            cellTemplate:'<div class="ui-grid-cell-contents">{{grid.appScope.getResolvedRatio(row.entity)}}</div>'},
        {field:'serverStatus',          displayName:'Server Status',    width: '16%',   cellClass: 'grid-align',
            headerTooltip: 'Server status', footerCellClass: 'grid-align',
            aggregationType: () => `Active: ${$scope.activeServerCount} / Inactive: ${$scope.allServerCount-$scope.activeServerCount}`,
            cellTemplate:'<div class="ui-grid-cell-contents"' +
                        ' ng-class="{\'server-status-active\':COL_FIELD == \'Active\',' +
                        '            \'server-status-inactive\':COL_FIELD == \'Inactive\',' +
                        '            \'server-status-active-timeout\':COL_FIELD == \'Active (Timed out)\'}">{{COL_FIELD}}</div>'}
    ];

    initialize();

    function initialize() {
        $scope.resolvedRatioTotal = '';
        $scope.allServerCount = 0;
        $scope.activeServerCount = 0;
        $scope.gridOptions = createGrid(columnDefs);
        $scope.gridOptions.showColumnFooter = true;
        getStatusRefreshInterval()
            .then(refreshData)
            .then(setRefreshDataTimer);
    }

    let refreshInterval = 60;

    function getStatusRefreshInterval() {
        return $http.get('/api/v1/server/config')
            .then((res) => {
                if (!res.data || !res.data.projectStatusRefreshInterval) {
                    $log.error('Failed to load refresh time value');
                    return;
                }
                refreshInterval = res.data.projectStatusRefreshInterval;
                $log.info(`Set refresh interval to ${refreshInterval} secs`);
            })
            .catch((err) => {
                $log.error(err);
            });
    }

    function setRefreshDataTimer() {
        $scope.refreshDataTimer = $interval(refreshData, refreshInterval * 1000);
    }

    function refreshData() {
        $scope.remainingTimeToRefresh = refreshInterval;
        if ($scope.timeDecreaser) {
            $interval.cancel($scope.timeDecreaser);
        }
        ServerStatusService.getActiveServerList()
            .then(loadData)
            .catch((err) => {
                $log.error(err);
            });
        $scope.time = new Date().toLocaleString();
        $scope.timeDecreaser = $interval(() => {$scope.remainingTimeToRefresh -= 1;}, 1000);
        setGridExportingFileNames($scope.gridOptions, CURRENT_STATUS_FILENAME_PREFIX + '-' + $scope.time);
    }

    function loadData(activeServerList) {
        ProjectService.getAllCurrentStatusList(activeServerList)
            .then((rows) => {
                $scope.gridOptions.data = rows;
                $scope.resolvedRatioTotal = getResolvedRatioTotal(rows);
                $scope.allServerCount = rows.length;
                $scope.activeServerCount = activeServerList.length;
            })
            .catch((err) => {
                $log.error(err);
            });
    }

    function getResolvedRatioTotal(rows) {
        const defectCountTotalSum = _.sum(_.map(rows, 'defectCountTotal'));

        if (defectCountTotalSum <= 0) {
            return '';
        }

        const defectCountFixedSum = _.sum(_.map(rows, 'defectCountFixed'));
        const defectCountDismissedSum = _.sum(_.map(rows, 'defectCountDismissed'));

        return `${((defectCountFixedSum + defectCountDismissedSum) / defectCountTotalSum * 100).toFixed(1)}%`;
    }

    $scope.getResolvedRatio = function(entity) {
        if (!entity.defectCountTotal)
            return '';
        return `${((entity.defectCountFixed + entity.defectCountDismissed) / entity.defectCountTotal * 100).toFixed(1)}%`;
    };

    $scope.$on("$destroy", () => {
        if ($scope.refreshDataTimer) {
            $interval.cancel($scope.refreshDataTimer);
        }
        if ($scope.timeDecreaser) {
            $interval.cancel($scope.timeDecreaser);
        }
    });

    $scope.reloadPage = function() {
        $window.location.reload();
    }
});