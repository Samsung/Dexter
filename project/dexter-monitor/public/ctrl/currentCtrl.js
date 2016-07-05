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

monitorApp.controller("CurrentCtrl", function($scope, $http, $log, ProjectService, uiGridConstants) {
    let current = this;

    const summaryColumnDefs = [
        {field:'groupCountAll',     displayName:'Group',    width: 220,     cellClass: 'grid-align',    headerTooltip: 'Number of groups'},
        {field:'projectCountAll',   displayName:'Project',  width: 220,     cellClass: 'grid-align',    headerTooltip: 'Number of projects'},
        {field:'defectCountAll',    displayName:'Defect',   width: 220,     cellClass: 'grid-align',    headerTooltip: 'Number of defects'},
        {field:'accountCountAll',   displayName:'Account',  width: 220,     cellClass: 'grid-align',    headerTooltip: 'Number of accounts'}
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
        {field:'defectCountExcluded',   displayName:'Defect(Exc)',      width: 140,     cellClass: 'grid-align',
            headerTooltip: 'Number of excluded defects', aggregationType: uiGridConstants.aggregationTypes.sum},
        {field:'accountCount',          displayName:'Account',          width: 115,     cellClass: 'grid-align',
            headerTooltip: 'Number of accounts', aggregationType: uiGridConstants.aggregationTypes.sum}
    ];

    initialize();

    function initialize() {
        current.summaryGridOptions = createGrid(summaryColumnDefs);
        removeUselessGridOptions(current.summaryGridOptions);
        current.detailGridOptions = createGrid(detailColumnDefs);
        current.detailGridOptions.showColumnFooter = true;
        loadDate();
        current.time = new Date().toLocaleString();
        setGridExportingFileNames(current.detailGridOptions, CURRENT_DETAIL_FILENAME_PREFIX + '-' + current.time);
    }

    function removeUselessGridOptions(gridOptions) {
        gridOptions.enableSorting = false;
        gridOptions.enableFiltering = false;
        gridOptions.showGridFooter = false;
        gridOptions.enableGridMenu = false;
    }

    function loadDate() {
        ProjectService.getCurrentDetailList()
            .then((rows) => {
                current.detailGridOptions.data = rows;
                current.summaryGridOptions.data = [{
                    groupCountAll: _.uniq(_.map(rows, 'groupName')).length,
                    projectCountAll: rows.length,
                    defectCountAll: _.sum(_.map(rows, 'defectCountTotal')),
                    accountCountAll: _.sum(_.map(rows, 'accountCount'))
                }];
            })
            .catch((err) => {
                $log.error(err);
            });
    }
});