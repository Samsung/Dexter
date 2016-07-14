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

monitorApp.controller("CurrentProjectCtrl", function($scope, $http, $log, ProjectService, ServerStatusService, uiGridConstants) {

    const columnDefs = [
        {field:'groupName',             displayName:'Group',            width: '17%',   cellClass: 'grid-align',
            headerTooltip: 'Group name', cellTooltip: true},
        {field:'projectName',           displayName:'Project',          width: '18%',   cellClass: 'grid-align',
            headerTooltip: 'Project name', cellTooltip: true, aggregationType: uiGridConstants.aggregationTypes.count},
        {field:'userCount',             displayName:'User',             width: '12%',   cellClass: 'grid-align',
            headerTooltip: 'Number of users', aggregationType: uiGridConstants.aggregationTypes.sum},
        {field:'defectCountTotal',      displayName:'Total',            width: '12%',   cellClass: 'grid-align',
            headerTooltip: 'Number of all defects', aggregationType: uiGridConstants.aggregationTypes.sum},
        {field:'defectCountFixed',      displayName:'Fixed',            width: '12%',   cellClass: 'grid-align',
            headerTooltip: 'Number of fixed defects', aggregationType: uiGridConstants.aggregationTypes.sum},
        {field:'defectCountDismissed',  displayName:'Dismissed',        width: '12%',   cellClass: 'grid-align',
            headerTooltip: 'Number of dismissed defects', aggregationType: uiGridConstants.aggregationTypes.sum},
        {field:'serverStatus',          displayName:'Server Status',    width: '17%',   cellClass: 'grid-align',
            headerTooltip: 'Server status', footerCellClass: 'grid-align',
            aggregationType: () => `Active: ${$scope.activeServerCount} / Inactive: ${$scope.allServerCount-$scope.activeServerCount}`,
            cellTemplate:'<div class="ui-grid-cell-contents"' +
                        ' ng-class="{\'server-status-active\':COL_FIELD == \'Active\',' +
                        '            \'server-status-inactive\':COL_FIELD == \'Inactive\'}">{{COL_FIELD}}</div>'}
    ];

    initialize();

    function initialize() {
        $scope.allServerCount = 0;
        $scope.activeServerCount = 0;
        $scope.gridOptions = createGrid(columnDefs);
        $scope.gridOptions.showColumnFooter = true;
        ServerStatusService.getActiveServerList()
            .then(loadData)
            .catch((err) => {
                $log.error(err);
            });
        $scope.time = new Date().toLocaleString();
        setGridExportingFileNames($scope.gridOptions, CURRENT_STATUS_FILENAME_PREFIX + '-' + $scope.time);
    }

    function loadData(activeServerList) {
        ProjectService.getAllCurrentStatusList(activeServerList)
            .then((rows) => {
                $scope.gridOptions.data = rows;
                $scope.allServerCount = rows.length;
                $scope.activeServerCount = activeServerList.length;
            })
            .catch((err) => {
                $log.error(err);
            });
    }
});