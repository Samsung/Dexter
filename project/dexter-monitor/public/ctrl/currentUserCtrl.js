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

monitorApp.controller("CurrentUserCtrl", function($scope, $http, $log, UserService) {

    const columnDefs = [
        {field:'projectName',       displayName:'Project',  width: '20%',   cellClass: 'grid-align',    headerTooltip: 'Project name'},
        {field:'userId',            displayName:'ID',       width: '15%',   headerTooltip: 'User ID',
            cellTemplate: '<div class="ui-grid-cell-contents grid-align" uib-tooltip="Click if you want to see extra information of \'{{COL_FIELD}}\'"' +
            ' tooltip-placement="top" tooltip-append-to-body="true"><a href ng-click="grid.appScope.getExtraInfo(row.entity)">{{COL_FIELD}}</a></div>'},
        {field:'name',              displayName:'Name',     width: '15%',   cellClass: 'grid-align',    headerTooltip: 'User name'},
        {field:'department',        displayName:'Group',    width: '20%',   cellClass: 'grid-align',    headerTooltip: 'Group name'},
        {field:'title',             displayName:'Title',    width: '15%',   cellClass: 'grid-align',    headerTooltip: 'User title'},
        {field:'employeeNumber',    displayName:'Number',   width: '15%',   cellClass: 'grid-align',    headerTooltip: 'User number'}
    ];

    initialize();

    function initialize() {
        $scope.gridOptions = createGrid(columnDefs);
        loadUserList();
        $scope.time = new Date().toLocaleString();
        setGridExportingFileNames($scope.gridOptions, USER_FILENAME_PREFIX + '-' + $scope.time);
    }

    function loadUserList() {
        UserService.getUserList()
            .then((rows) => {
                $scope.gridOptions.data = rows;
            })
            .catch((err) => {
                $log.error(err);
            });
    }

    $scope.getExtraInfo = function(entity) {
        if (entity.name && entity.department && entity.title && entity.employeeNumber)
            return;

        UserService.getExtraInfoByUserId(entity.userId)
            .then((row) => {
                if(row) {
                    entity.name = row.name;
                    entity.department = row.department;
                    entity.title = row.title;
                    entity.employeeNumber = row.employeeNumber;
                }
            })
            .catch((err) => {
                $log.error(err);
            });
    };
});