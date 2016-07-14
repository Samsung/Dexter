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

monitorApp.service('ServerStatusService', function($http, $log) {
    var service = this;
    var activeServers = [];
    var inactiveServers = [];

    service.initServerList = function(){
        $log.debug('active and inactive server list is initiated');
        activeServers = [];
        inactiveServers = [];
    };

    service.loadServerList = function(callback){
        $http.get("/api/v1/server").then(function(results){
            activeServers = _.filter(results.data, {'active': true});
            inactiveServers = _.filter(results.data, function(item) { return !item.active;});

            if(callback) callback();
        }, function(results){
            $log.error("Error: " + results.data + "; " + results.status);
            if(callback) callback(results);
        });
    };

    service.getActiveServers = function (){
        return activeServers;
    };

    service.getInactiveServers = function (){
        return inactiveServers;
    };

    service.getActiveServerCount = function (){
        return activeServers.length;
    };

    service.getInactiveServerCount = function (){
        return inactiveServers.length;
    };

    service.getActiveServerList = function() {
        return $http.get("/api/v1/server")
            .then((results) => {
                return _.filter(results.data, {'active': true});
            })
            .catch((err) => {
                $log.error(err);
                return [];
            });
    };

    var localLastModifiedTimeOfServers = new Date();
    var currentLoadingCount = 0;
    var MAX_LOADING_COUNT = 10;

    service.IsServerStatusChanged = function(callback){
        $http.get("/api/v1/server/last-modified-time").then(function(results){
            currentLoadingCount = 0;
            var remoteLastModifiedTimeOfServers = results.data.serverListLastModifiedTime;

            if(remoteLastModifiedTimeOfServers !== localLastModifiedTimeOfServers){
                localLastModifiedTimeOfServers = remoteLastModifiedTimeOfServers;
                callback();
            }
        }, function(results){
            if(currentLoadingCount === 0)
                $log.error("Error: " + results.data + "; " + results.status);

            if(++currentLoadingCount > MAX_LOADING_COUNT)
                callback(results);
        });
    }
});
