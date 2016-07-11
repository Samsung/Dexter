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

monitorApp.controller("UserStatusCtrl", function($scope, $http, $log, UserService, uiGridConstants) {

    const columnDefs = [
        {field:'groupName',                 displayName:'Group',                    width: 170,
            cellClass: 'grid-align',    headerTooltip: 'Group name'},
        {field:'allDeveloperCount',         displayName:'Developers',               width: 110,
            cellClass: 'grid-align',    headerTooltip: 'Number of developers',
            aggregationType: uiGridConstants.aggregationTypes.sum},
        {field:'targetDeveloperCount',      displayName:'Target developers',        width: 155,
            cellClass: 'grid-align',    headerTooltip: 'Number of developers who should install Dexter',
            aggregationType: uiGridConstants.aggregationTypes.sum},
        {field:'installedDeveloperCount',   displayName:'Installed developers',     width: 170,
            cellClass: 'grid-align',    headerTooltip: 'Number of developers who installed Dexter',
            aggregationType: uiGridConstants.aggregationTypes.sum},
        {field:'installationRate',          displayName:'Installation rate (%)',    width: 160,
            cellClass: 'grid-align',    headerTooltip: 'Installation rate',
            aggregationType: () => $scope.rate, footerCellClass: 'grid-align'},
        {field:'nonTargetDeveloperCount',   displayName:'Non-target developers',    width: 210,
            cellClass: 'grid-align',    headerTooltip: 'Number of developers not applicable to install Dexter',
            aggregationType: uiGridConstants.aggregationTypes.sum}
    ];

    initialize();

    function initialize() {
        $scope.gridOptions = createGrid(columnDefs);
        $scope.gridOptions.showColumnFooter = true;
        $scope.gridOptions.exporterOlderExcelCompatibility = true;
        $scope.time = new Date().toLocaleString();
        loadDate();
        setGridExportingFileNames($scope.gridOptions, USER_STATUS_FILENAME_PREFIX + '-' + $scope.time);
    }

    function loadDate() {
        UserService.getUserStatus()
            .then((rows) => {
                $scope.gridOptions.data = rows;
                const targetDeveloperCountTotal = _.sum(_.map($scope.gridOptions.data, 'targetDeveloperCount'));
                const installedDeveloperCountTotal = _.sum(_.map($scope.gridOptions.data, 'installedDeveloperCount'));
                $scope.rate = ((installedDeveloperCountTotal / targetDeveloperCountTotal) * 100).toFixed(1);
            })
            .catch((err) => {
                $log.error(err);
            });
    }
});