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

monitorApp.controller("OverviewCtrl", function($scope, $http, $log, UserService, ProjectService, uiGridConstants) {

    const summaryColumnDefs = [
        {field:'installationRate',          width: '25%',       cellClass: 'grid-align',
            headerCellTemplate:'<div class="grid-align ui-grid-cell-contents"' +
            'uib-tooltip="Installed / Target * 100" tooltip-append-to-body="true">Installation rate</div>'},
        {field:'installedDeveloperCount',   width: '25%',       cellClass: 'grid-align',
            headerCellTemplate:'<div class="grid-align ui-grid-cell-contents"' +
            'uib-tooltip="Installed / Target" tooltip-append-to-body="true">Installed developers</div>'},
        {field:'resolvedDefectRate',        width: '25%',       cellClass: 'grid-align',
            headerCellTemplate:'<div class="grid-align ui-grid-cell-contents"' +
            'uib-tooltip="(Fixed + Dismissed) / Total * 100" tooltip-append-to-body="true">Resolved defect rate</div>'},
        {field:'defectCountTotal',          width: '25%',       cellClass: 'grid-align',
            headerCellTemplate:'<div class="grid-align ui-grid-cell-contents"' +
            'uib-tooltip="Total defects" tooltip-append-to-body="true">Total defects</div>'}
    ];

    const installationStatusColumnDefs = [
        {field:'groupName',                 displayName:'Group',                    width: '17%',
            cellClass: 'grid-align',    headerTooltip: 'Group name'},
        {field:'allDeveloperCount',         displayName:'Developers',               width: '16%',
            cellClass: 'grid-align',    headerTooltip: 'Number of developers',
            aggregationType: uiGridConstants.aggregationTypes.sum},
        {field:'targetDeveloperCount',      displayName:'Target developers',        width: '16%',
            cellClass: 'grid-align',    headerTooltip: 'Number of developers who should install Dexter',
            aggregationType: uiGridConstants.aggregationTypes.sum},
        {field:'installedDeveloperCount',   displayName:'Installed developers',     width: '16%',
            cellClass: 'grid-align',    headerTooltip: 'Number of developers who installed Dexter',
            aggregationType: uiGridConstants.aggregationTypes.sum},
        {field:'installationRate',          displayName:'Installation rate (%)',    width: '16%',
            cellClass: 'grid-align',    headerTooltip: 'Installation rate',
            aggregationType: () => $scope.rate, footerCellClass: 'grid-align'},
        {field:'nonTargetDeveloperCount',   displayName:'Non-target developers',    width: '19%',
            cellClass: 'grid-align',    headerTooltip: 'Number of developers not applicable to install Dexter',
            aggregationType: uiGridConstants.aggregationTypes.sum}
    ];

    const defectStatusColumnDefs = [
        {field:'groupName',             displayName:'Group',            width: '20%',   cellClass: 'grid-align',
            headerTooltip: 'Group name', cellTooltip: true},
        {field:'projectCount',          displayName:'Project',          width: '16%',   cellClass: 'grid-align',
            headerTooltip: 'Number of projects',            aggregationType: uiGridConstants.aggregationTypes.sum},
        {field:'userCount',             displayName:'User',             width: '16%',   cellClass: 'grid-align',
            headerTooltip: 'Number of users',               aggregationType: uiGridConstants.aggregationTypes.sum},
        {field:'defectCountTotal',      displayName:'Total',            width: '16%',   cellClass: 'grid-align',
            headerTooltip: 'Number of all defects',         aggregationType: uiGridConstants.aggregationTypes.sum},
        {field:'defectCountFixed',      displayName:'Fixed',            width: '16%',   cellClass: 'grid-align',
            headerTooltip: 'Number of fixed defects',       aggregationType: uiGridConstants.aggregationTypes.sum},
        {field:'defectCountDismissed',  displayName:'Dismissed',        width: '16%',   cellClass: 'grid-align',
            headerTooltip: 'Number of dismissed defects',   aggregationType: uiGridConstants.aggregationTypes.sum}
    ];

    initialize();

    function initialize() {
        $scope.summaryGridOptions = createGrid(summaryColumnDefs);
        removeUselessGridOptions($scope.summaryGridOptions);
        removeScrollbarFromGrid($scope.summaryGridOptions);
        $scope.installationStatusGridOptions = createGrid(installationStatusColumnDefs);
        $scope.installationStatusGridOptions.showColumnFooter = true;
        removeScrollbarFromGrid($scope.installationStatusGridOptions);
        $scope.defectStatusGridOptions = createGrid(defectStatusColumnDefs);
        $scope.defectStatusGridOptions.showColumnFooter = true;
        removeScrollbarFromGrid($scope.defectStatusGridOptions);

        $scope.time = new Date().toLocaleString();
        $scope.summaryGridOptions.data.push({});
        loadDataForInstallationStatusGrid();
        loadDataForDefectStatusGrid();
        setGridExportingFileNames($scope.installationStatusGridOptions, INSTALLATION_STATUS_FILENAME_PREFIX + '-' + $scope.time);
        setGridExportingFileNames($scope.defectStatusGridOptions, DEFECT_STATUS_FILENAME_PREFIX + '-' + $scope.time);
    }

    function removeScrollbarFromGrid(gridOptions) {
        gridOptions.enableHorizontalScrollbar = uiGridConstants.scrollbars.NEVER;
        gridOptions.enableVerticalScrollbar =  uiGridConstants.scrollbars.NEVER;
    }

    function removeUselessGridOptions(gridOptions) {
        gridOptions.enableSorting = false;
        gridOptions.enableFiltering = false;
        gridOptions.showGridFooter = false;
        gridOptions.enableGridMenu = false;
    }

    function loadDataForInstallationStatusGrid() {
        UserService.getUserStatus()
            .then((rows) => {
                $scope.installationStatusGridOptions.data = rows;
                resizeHeightOfGrid('overviewInstallationStatusGrid', rows.length);
                const targetDeveloperCountTotal = _.sum(_.map($scope.installationStatusGridOptions.data, 'targetDeveloperCount'));
                const installedDeveloperCountTotal = _.sum(_.map($scope.installationStatusGridOptions.data, 'installedDeveloperCount'));
                $scope.rate = ((installedDeveloperCountTotal / targetDeveloperCountTotal) * 100).toFixed(1);
                $scope.summaryGridOptions.data[0].installationRate = $scope.rate + '%';
                $scope.summaryGridOptions.data[0].installedDeveloperCount
                    = `${installedDeveloperCountTotal.toLocaleString()} / ${targetDeveloperCountTotal.toLocaleString()}`;
            })
            .catch((err) => {
                $log.error(err);
            });
    }

    function loadDataForDefectStatusGrid() {
        ProjectService.getCurrentStatusByGroup()
            .then((rows) => {
                $scope.defectStatusGridOptions.data = rows;
                resizeHeightOfGrid('overviewDefectStatusGrid', rows.length);
                const defectCountTotal = _.sum(_.pull(_.map(rows, 'defectCountTotal'), ""));
                const defectCountFixed = _.sum(_.pull(_.map(rows, 'defectCountFixed'), ""));
                const defectCountDismissed =_.sum(_.pull(_.map(rows, 'defectCountDismissed'), ""));
                $scope.summaryGridOptions.data[0].resolvedDefectRate
                    = ((defectCountFixed + defectCountDismissed) / defectCountTotal * 100).toFixed(1) + '%';
                $scope.summaryGridOptions.data[0].defectCountTotal = defectCountTotal.toLocaleString();
            })
            .catch((err) => {
                $log.error(err);
            });
    }

    function resizeHeightOfGrid(gridId, rowCount) {
        angular.element(document.getElementById(gridId))
            .css('height', (rowCount * ROW_HEIGHT + HEADER_HEIGHT) + 'px');
    }
});