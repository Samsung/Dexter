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

monitorApp.controller("AllWeeklyStatusCtrl", function($scope, $http, $log) {

    const columnDefs = [
        {field:'year',                  displayName:'Year',         width: 100,     cellClass: 'grid-align',    headerTooltip: 'Year'},
        {field:'week',                  displayName:'Week',         width: 100,     cellClass: 'grid-align',    headerTooltip: 'Week'},
        {field:'defectCountTotal',      displayName:'Defect(All)',  width: 170,     cellClass: 'grid-align',    headerTooltip: 'Number of all defects'},
        {field:'defectCountFixed',      displayName:'Defect(Fix)',  width: 170,     cellClass: 'grid-align',    headerTooltip: 'Number of fixed defects'},
        {field:'defectCountDismissed',  displayName:'Defect(Dis)',  width: 170,     cellClass: 'grid-align',    headerTooltip: 'Number of dismissed defects'},
        {field:'userCount',             displayName:'User',         width: 170,     cellClass: 'grid-align',    headerTooltip: 'Number of users'}
    ];

    initialize();

    function initialize() {
        $scope.gridOptions = createGrid(columnDefs);
        loadDate();
        setGridExportingFileNames($scope.gridOptions, WEEKLY_STATUS_FILENAME_PREFIX);
    }

    function loadDate() {
        $http.get('/api/v2/defect-weekly-change')
            .then((res) => {
                if (!isHttpResultOK(res)) {
                    $log.error('Failed to load weekly change status list');
                    return;
                }

                $scope.gridOptions.data = res.data.rows;
            })
            .catch((err) => {
                $log.error(err);
            });
    }
});