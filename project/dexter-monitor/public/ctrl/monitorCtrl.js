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
monitorApp.controller("MonitorCtrl", monitorController);

function monitorController ($scope, $http, $log, $interval, _){
    var _columnDefinitions = [];
    var serverMonitor;
    var serverListLastModifiedTime = new Date();
    var currentLoadingCount = 0;
    var MAX_LOADING_COUNT = 10;

    initialize();

    function initialize(){
        initNgVariables();
        loadServerList();
        createServerListTable();
        startMonitoringServers();
    }

    function initNgVariables(){
        $scope.activeServers = [];
        $scope.inactiveServers = [];
        $scope.serverListModifiedTime = new Date();
        $scope.$log = $log;
    }

    function createServerListTable(){
        createServerGridColumnDefinitions();
        createServerGridOptions();
    }

    function createServerGridColumnDefinitions(){
        var tooltipTemplate = '<span tooltip="{{row.entity.name}}" tooltip-append-to-body="true" tooltip-trigger:"focus">{{row.entity.name}}</span>';
        _columnDefinitions = [
            //{field:'active', displayName:'Active', width: 60, cellTemplate: cellTemplate},
            {field:'type', displayName:'Type'},
            {field:'group', displayName:'Group'},
            {field:'name', displayName:'Name', width:"50%", cellTemplate:tooltipTemplate},
            {field:'emailingWhenServerDead', displayName:'Email', width: 60}
        ];
    }

    function createServerGridOptions(){
        var commonOptions = {
            //multiSelect: true,
            enablePaging: false,
            showFooter: true,
            enablePinning: true,
            enableColumnResize: true,
            enableColumnReordering: true,
            enableRowSelection: false,
            enableRowReordering: true,
            //enableCellEditOnFocus: true,
            filterOptions: $scope.filterOptions,
            showGroupPanel: true,
            showColumnMenu: true,
            showFilter: true,
            //showSelectionCheckbox: true,
            jqueryUIDraggable: false,
            columnDefs: _columnDefinitions,
            sortInfo: {
                fields: ['type', 'group', 'name'],
                directions: ['asc']
            }
        }

        $scope.activeServerGridOptions = _.cloneDeep(commonOptions);
        $scope.activeServerGridOptions.data = 'activeServers';

        $scope.inactiveServerGridOptions = _.cloneDeep(commonOptions);
        $scope.inactiveServerGridOptions.data = 'inactiveServers';
    }

    $scope.startMonitoringServers = startMonitoringServers;

    function startMonitoringServers(){
        serverMonitor = $interval(function(){
            loadServerListWhenServerStatusChanged()
        }, 1000);
    }

    $scope.stopMonitoringServers = stopMonitoringServers;

    function stopMonitoringServers() {
        $interval.cancel(serverMonitor);
    }

    function loadServerListWhenServerStatusChanged(){
        $http.get("/api/v1/server/last-modified-time").then(function(results){
            currentLoadingCount = 0;
            var currentLastModifiedTime = results.data.serverListLastModifiedTime;
            if(currentLastModifiedTime !== serverListLastModifiedTime){
                serverListLastModifiedTime = currentLastModifiedTime;
                loadServerList();
            }
        }, function(results){
            if(currentLoadingCount == 0)
                $log.error("Error: " + results.data + "; " + results.status);

            if(currentLoadingCount++ > MAX_LOADING_COUNT){
                stopMonitoringServers();
                $scope.activeServers = [];
                $scope.inactiveServers = [];
                $scope.errorMessage = "Fail to connect Dexter Monitor Server. After checing the server, refresh this page";
            };
        });
    }

    function loadServerList(){
        $http.get("/api/v1/server").then(function(results){
            $scope.activeServers = _.filter(results.data, function(server){
                return server.active === true;
            });

            $scope.inactiveServers = _.filter(results.data, function(server){
                return server.active === false;
            });
        }, function(results){
            $log.error("Error: " + results.data + "; " + results.status);
        });
    }
}
