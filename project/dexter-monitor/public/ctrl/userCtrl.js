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

monitorApp.controller("UserCtrl", function($scope, $http, $log, UserService) {
    let user = this;

    const columnDefs = [
        {field:'userId',            displayName:'ID',       width: 194,     cellClass: 'grid-align',    headerTooltip: 'ID'},
        {field:'name',              displayName:'Name',     width: 204,     cellClass: 'grid-align',    headerTooltip: 'Name'},
        {field:'department',        displayName:'Group',    width: 284,     cellClass: 'grid-align',    headerTooltip: 'Group'},
        {field:'title',             displayName:'Title',    width: 194,     cellClass: 'grid-align',    headerTooltip: 'Title'},
        {field:'employeeNumber',    displayName:'Number',   width: 184,     cellClass: 'grid-align',    headerTooltip: 'Number'}
    ];

    initialize();

    function initialize() {
        user.gridOptions = createGrid(columnDefs);
        loadUserList();
        setGridExportingFileNames(user.gridOptions, USER_FILENAME_PREFIX);
    }

    function loadUserList() {
        $http.get('/api/v2/user')
            .then((res) => {
                if (!isHttpResultOK(res)) {
                    $log.error('Failed to load user list');
                    return;
                }

                user.gridOptions.data = res.data.rows;
            })
            .catch((err) => {
                $log.error(err);
            });
    }

    user.getExtraInfo = function() {
        const userIdList = _.map(user.gridOptions.data, 'userId');
        UserService.getExtraInfoByUserIdList(userIdList)
            .then((rows) => {
                if(rows) {
                    user.gridOptions.data = _.sortBy(rows, 'userId');
                }
            })
            .catch((err) => {
                $log.error(err);
            });
    };
});