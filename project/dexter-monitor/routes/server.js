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

const http = require('http');
const database = require("../util/database");
const log = require('../util/logging');
const mailing = require('../util/mailing');
const _ = require('lodash');

let serverList = [];
let serverListLastModifiedTime = new Date();
let checkingServerStatus;

exports.init = function(doNotStartChecking){

    mailing.init();

    loadServerList()
        .then(initServerStatusValues)
        .then(() => {
            if (!doNotStartChecking)
                startCheckingServerStatus();
        });
};

function loadServerList() {
    const sql =
        `SELECT pid, projectName, hostIP, portNumber,
                emailList, emailingWhenServerDead,
                projectType, groupName, administrator
        FROM ProjectInfo`;
    return database.exec(sql)
        .then((rows) => {
            serverList = rows;
        })
        .catch((err) => {
            log.error(err);
        });
}

function initServerStatusValues() {
    const date = new Date().getTime();
    serverList.forEach((server) => {
        const lastDigitOfHostIP = server.hostIP.split('.')[3];
        server.name = `${server.projectName}(${lastDigitOfHostIP}:${server.portNumber})`;
        server.rerunLastTryTime = date;
        server.rerunTimes = 0;
        server.heartbeat = `http://${server.hostIP}:${server.portNumber}/api/v1/isServerAlive`;
        server.heartbeatInDetail = `http://${server.hostIP}:${server.portNumber}/api/v2/server-detailed-status`;  // Not used yet
    });
}

function startCheckingServerStatus(){
    checkingServerStatus = setInterval(() => {
        checkServerStatusAndRunWhenItDown();
    }, global.config.serverStatusCheckInterval * 1000);
}

exports.startCheckingServerStatus = startCheckingServerStatus;
exports.stopServerChecking = stopCheckingServerStatus;  // TODO check

// TODO: detailedServerStatus

function checkServerStatusAndRunWhenItDown(){
    _.forEach(serverList, (server) => {
        http.get(server.heartbeat, (res) => {
            if(res.statusCode === 200) {
                setServerActiveStatus(server, true);
                return;
            }

            handleServerWhenDown(server, res);
        }).on('error', (error) => {
            handleServerWhenDown(server, error);
        });
    });
}

function setServerActiveStatus(server, serverStatus){
    if(server.active !== serverStatus){
        serverListLastModifiedTime = new Date();

        if(serverStatus === true){
            server.notifiedWhenItDown = false;
            log.info('server (' + server.name + ') is active now');
        } else {
            log.info('server (' + server.name + ') is not active now');
        }
    }

    server.active = serverStatus;
}

function handleServerWhenDown(server, error){
    if(error) log.error(error);

    setServerActiveStatus(server, false);
    emailWhenServerDown(server);
    //TODO runServerWhenTerminated(server);
}

function emailWhenServerDown(server) {
    if(server.emailingWhenServerDead === 'N' || server.notifiedWhenItDown) return;

    const params = {
        toList:server.emailList,
        ccList:["min.ho.kim@samsung.com"],
        title:"[Dexter Monitor] Server Failure: " + server.name,
        contents: createEmailContentsWhenServerDown(server)
    };

    mailing.sendEmail(params, (error) => {
        if(error)
            log.error("fail to email: " + JSON.stringify(params));
        else
            log.info('emailed because ' + server.name + ' is down');
    });

    server.notifiedWhenItDown = true;
}

function createEmailContentsWhenServerDown(server){
    return "Please check your server status.<br><br>"
        + '<b> - Server Name</b>: <b style="color:red">' + server.name + "</b><br>"
        + "<b> - Server Type/Group</b>: " + server.type + " / " + server.group + "<br>"
        + "<b> - Server Administrator</b>: " + server.serverAdministrator + "<br>"
        + "<b> - Status</b>: the heartbeat is not working. (the server may not be working)<br>"
        + "<b> - heartbeat</b>: " + server.heartbeat + "<br>"
        + "<b> - Check Time</b>: " + new Date();
}

function stopCheckingServerStatus(){
    clearInterval(checkingServerStatus);
}

exports.getConfig = function(req, res) {
    res.send(config);
};

exports.getServerListLastModifiedTime = function(req, res) {
    res.send(200, {"serverListLastModifiedTime": serverListLastModifiedTime});
};

exports.getServerList = function(req, res) {
    res.send(serverList);
};