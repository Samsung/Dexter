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

monitorApp.controller("WeeklyProjectCtrl", function($scope, $http, $log) {

    const columnDefs = [
        {field:'year',              displayName:'Year',         width: 60,    headerTooltip: 'Year'},
        {field:'week',              displayName:'Week',         width: 60,    headerTooltip: 'Week'},
        {field:'groupName',         displayName:'Group',        width: 150,    headerTooltip: 'Group'},
        {field:'projectName',       displayName:'Project',      width: 150,   headerTooltip: 'Project'},
        {field:'language',          displayName:'Lang',         width: 60,    headerTooltip: 'Language'},
        {field:'allDefectCount',    displayName:'All',          width: 71,    headerTooltip: 'All defects'},
        {field:'allNew',            width: 35,      headerCellTemplate:'<div style="text-align: center" uib-tooltip="[New] All types of defects" tooltip-append-to-body="true">All<br>New</div>'},
        {field:'allFix',            width: 35,      headerCellTemplate:'<div style="text-align: center" uib-tooltip="[Fixed] All types of defects" tooltip-append-to-body="true">All<br>Fix</div>'},
        {field:'allDis',            width: 35,      headerCellTemplate:'<div style="text-align: center" uib-tooltip="[Dismissed] All types of defects" tooltip-append-to-body="true">All<br>Dis</div>'},
        {field:'criNew',            width: 35,      headerCellTemplate:'<div style="text-align: center" uib-tooltip="[New] Critical defects" tooltip-append-to-body="true">Cri<br>New</div>'},
        {field:'criFix',            width: 35,      headerCellTemplate:'<div style="text-align: center" uib-tooltip="[Fixed] Critical defects" tooltip-append-to-body="true">Cri<br>Fix</div>'},
        {field:'criDis',            width: 35,      headerCellTemplate:'<div style="text-align: center" uib-tooltip="[Dismissed] Critical defects" tooltip-append-to-body="true">Cri<br>Dis</div>'},
        {field:'majNew',            width: 35,      headerCellTemplate:'<div style="text-align: center" uib-tooltip="[New] Major defects" tooltip-append-to-body="true">Maj<br>New</div>'},
        {field:'majFix',            width: 35,      headerCellTemplate:'<div style="text-align: center" uib-tooltip="[Fixed] Major defects" tooltip-append-to-body="true">Maj<br>Fix</div>'},
        {field:'majDis',            width: 35,      headerCellTemplate:'<div style="text-align: center" uib-tooltip="[Dismissed] Major defects" tooltip-append-to-body="true">Maj<br>Dis</div>'},
        {field:'minNew',            width: 35,      headerCellTemplate:'<div style="text-align: center" uib-tooltip="[New] Minor defects" tooltip-append-to-body="true">Min<br>New</div>'},
        {field:'minFix',            width: 35,      headerCellTemplate:'<div style="text-align: center" uib-tooltip="[Fixed] Minor defects" tooltip-append-to-body="true">Min<br>Fix</div>'},
        {field:'minDis',            width: 35,      headerCellTemplate:'<div style="text-align: center" uib-tooltip="[Dismissed] Minor defects" tooltip-append-to-body="true">Min<br>Dis</div>'},
        {field:'crcNew',            width: 35,      headerCellTemplate:'<div style="text-align: center" uib-tooltip="[New] Coding rule checker defects" tooltip-append-to-body="true">Crc<br>New</div>'},
        {field:'crcFix',            width: 35,      headerCellTemplate:'<div style="text-align: center" uib-tooltip="[Fixed] Coding rule checker defects" tooltip-append-to-body="true">Crc<br>Fix</div>'},
        {field:'crcDis',            width: 35,      headerCellTemplate:'<div style="text-align: center" uib-tooltip="[Dismissed] Coding rule checker defects" tooltip-append-to-body="true">Crc<br>Dis</div>'},
        {field:'etcNew',            width: 35,      headerCellTemplate:'<div style="text-align: center" uib-tooltip="[New] Etc defects" tooltip-append-to-body="true">Etc<br>New</div>'},
        {field:'etcFix',            width: 35,      headerCellTemplate:'<div style="text-align: center" uib-tooltip="[Fixed] Etc defects" tooltip-append-to-body="true">Etc<br>Fix</div>'},
        {field:'etcDis',            width: 35,      headerCellTemplate:'<div style="text-align: center" uib-tooltip="[Dismissed] Etc defects" tooltip-append-to-body="true">Etc<br>Dis</div>'}
    ];

    initialize();

    function initialize() {
        $scope.gridOptions = createGrid(columnDefs);
        loadDefectList();
        setGridExportingFileNames($scope.gridOptions, DEFECT_FILENAME_PREFIX);
    }

    function loadDefectList() {
        $http.get('/api/v2/defect')
            .then((res) => {
                if (!isHttpResultOK(res)) {
                    $log.error('Failed to load defect list');
                    return;
                }

                $scope.gridOptions.data = res.data.rows;
            })
            .catch((err) => {
                $log.error(err);
            });
    }
});