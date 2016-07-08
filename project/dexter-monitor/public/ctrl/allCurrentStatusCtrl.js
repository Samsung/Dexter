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

monitorApp.controller("AllCurrentStatusCtrl", function($scope, $http, $log, ProjectService, uiGridConstants) {

    const summaryColumnDefs = [
        {field:'allGroupCount',     displayName:'Group',    width: 220,     cellClass: 'grid-align',    headerTooltip: 'Number of groups'},
        {field:'allProjectCount',   displayName:'Project',  width: 220,     cellClass: 'grid-align',    headerTooltip: 'Number of projects'},
        {field:'allDefectCount',    displayName:'Defect',   width: 220,     cellClass: 'grid-align',    headerTooltip: 'Number of defects'},
        {field:'allUserCount',      displayName:'User',     width: 220,     cellClass: 'grid-align',    headerTooltip: 'Number of users'}
    ];

    const detailColumnDefs = [
        {field:'groupName',             displayName:'Group',            width: 180,     cellClass: 'grid-align',
            headerTooltip: 'Group name'},
        {field:'projectName',           displayName:'Project',          width: 180,     cellClass: 'grid-align',
            headerTooltip: 'Project name', aggregationType: uiGridConstants.aggregationTypes.count},
        {field:'defectCountTotal',      displayName:'Defect(All)',      width: 140,     cellClass: 'grid-align',
            headerTooltip: 'Number of all defects', aggregationType: uiGridConstants.aggregationTypes.sum},
        {field:'defectCountFixed',      displayName:'Defect(Fix)',      width: 140,     cellClass: 'grid-align',
            headerTooltip: 'Number of fixed defects', aggregationType: uiGridConstants.aggregationTypes.sum},
        {field:'defectCountDismissed',  displayName:'Defect(Dis)',      width: 140,     cellClass: 'grid-align',
            headerTooltip: 'Number of dismissed defects', aggregationType: uiGridConstants.aggregationTypes.sum},
        {field:'userCount',             displayName:'User',             width: 115,     cellClass: 'grid-align',
            headerTooltip: 'Number of users', aggregationType: uiGridConstants.aggregationTypes.sum}
    ];

    initialize();

    function initialize() {
        $scope.summaryGridOptions = createGrid(summaryColumnDefs);
        removeUselessGridOptions($scope.summaryGridOptions);
        $scope.detailGridOptions = createGrid(detailColumnDefs);
        $scope.detailGridOptions.showColumnFooter = true;
        loadDate();
        $scope.time = new Date().toLocaleString();
        setGridExportingFileNames($scope.detailGridOptions, CURRENT_STATUS_FILENAME_PREFIX + '-' + $scope.time);
    }

    function removeUselessGridOptions(gridOptions) {
        gridOptions.enableSorting = false;
        gridOptions.enableFiltering = false;
        gridOptions.showGridFooter = false;
        gridOptions.enableGridMenu = false;
    }

    function loadDate() {
        ProjectService.getAllCurrentStatusList()
            .then((rows) => {
                $scope.detailGridOptions.data = rows;
                $scope.summaryGridOptions.data = [{
                    allGroupCount: _.uniq(_.map(rows, 'groupName')).length,
                    allProjectCount: rows.length,
                    allDefectCount: _.sum(_.map(rows, 'defectCountTotal')),
                    allUserCount: _.sum(_.map(rows, 'userCount'))
                }];
            })
            .catch((err) => {
                $log.error(err);
            });
    }
});