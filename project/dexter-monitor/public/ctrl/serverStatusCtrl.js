/**
 * Copyright (c) 2015 Samsung Electronics, Inc.,
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

monitorApp.controller("ServerStatusCtrl", function ($interval, ServerStatusService, _) {
    var main = this;

    var serverMonitor;
    var rowHeight = 30;
    var headerHeight = 110;

    main.startMonitoringServers = startMonitoringServers;
    main.stopMonitoringServers = stopMonitoringServers;

    initialize();

    function initialize(){
        createServerListTable();
        loadServerList();
        startMonitoringServers();
    }

    function createServerListTable(){
        main.activeServerGridOptions = getCommonOptions();
        main.activeServerGridOptions.data = [];

        main.inactiveServerGridOptions = getCommonOptions();
        main.inactiveServerGridOptions.data = [];
    }

    function getCommonOptions(){
        return {
            enableSorting: true,
            enableFiltering: true,
            showGridFooter: true,
            enableGridMenu: true,
            enableSelectAll: true,
            exporterCsvFilename: 'server-list.csv',
            exporterPdfFilename: 'server-list.pdf',
            exporterPdfDefaultStyle: {fontSize: 8},
            exporterTableStyle: {margin: [5,5,5,5]},
            exporterPdfTableHeaderStyle: {fontSize: 10, bold: true, italics: true, color: 'red'},
            exporterPdfOrientation: 'landscape',
            exporterPdfPageSize: 'A4',
            columnDefs: getServerGridColumnDefinitions()
        };
    }

    function getServerGridColumnDefinitions(){
        return [
            {field:'type', displayName:'Type', cellTooltip: function(row, col){ return row.entity.type;}},
            {field:'group', displayName:'Group', cellTooltip: function(row, col){ return row.entity.group;}},
            {field:'name', displayName:'Name', width:"50%", cellTooltip: function(row, col){ return row.entity.name;}},
            {field:'emailingWhenServerDead', displayName:'Email', width: 60, cellTooltip: function(row, col){
                return row.entity.emailingWhenServerDead;}}
        ];
    }

    function startMonitoringServers(){
        serverMonitor = $interval(function(){
            ServerStatusService.IsServerStatusChanged(function(error){
                if(error){
                    stopMonitoringServers();
                    ServerStatusService.initServerList();
                    main.errorMessage = "Fail to connect Dexter Monitor Server. After checking the server, refresh this page";
                } else {
                    loadServerList();
                }
            })
        }, 1000);
    }

    function stopMonitoringServers() {
        $interval.cancel(serverMonitor);
    }

    function loadServerList(){
        ServerStatusService.loadServerList(function(){
            main.activeServerGridOptions.data = ServerStatusService.getActiveServers();
            main.inactiveServerGridOptions.data = ServerStatusService.getInactiveServers();
            resizeHeightOfServerTables();
        });
    }

    function resizeHeightOfServerTables(){
        angular.element(document.getElementById('activeTable'))
            .css('height', (ServerStatusService.getActiveServerCount() * rowHeight + headerHeight) + 'px');

        angular.element(document.getElementById('inactiveTable'))
            .css('height', (ServerStatusService.getInactiveServerCount() * rowHeight + headerHeight) + 'px');
    }
});
