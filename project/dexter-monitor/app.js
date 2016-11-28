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

const express = require('express');
const http = require('http');
const path = require('path');
const schedule = require('node-schedule');
const log = require('./util/logging');
const database = require("./util/database");
const server = require('./routes/server');
const util = require('./util/dexter-util');
const user = require('./routes/user');
const defect = require('./routes/defect');
const project = require('./routes/project');

const app = express();

const runOptions = {
    databaseHost:'localhost',
    databasePort:3306,
    databaseUser:'',
    databasePassword:'',
    databaseAdminUser:'',
    databaseAdminPassword:'',
    databaseName:'',
    getDbUrl : function(){
        return this.databaseName + "@" + this.databaseHost + ':' + this.databasePort;
    }
};

initialize();

function initialize(){
    initExcutionMode();
    initConfigFromFile();
    setDatabaseOptionsByConfig();
    loadCliOptions();
    configureApp();
    initModules();
    startServer();
    setScheduler();
}

function initConfigFromFile(){
    const fs = require('fs');
    const text = fs.readFileSync('./config.json', 'utf8');
    if(!text) {
        console.log("there is no %dexter-monitor-home%/config.json file.");
        process.exit(-1);
    }

    global.config = JSON.parse(text);
    global.config.ip = util.getLocalIPAddress();
}

function setDatabaseOptionsByConfig(){
    let dbConfig = global.config.database;
    runOptions.databaseHost = dbConfig.host;
    runOptions.databasePort = dbConfig.port;
    runOptions.databaseUser = dbConfig.user;
    runOptions.databasePassword = dbConfig.password;
    runOptions.databaseName = dbConfig.name;
}

function initExcutionMode(){
    if(!process.env.NODE_ENV){
        process.env.NODE_ENV = 'production';
    } else {
        process.env.NODE_ENV = 'development';
    }
}

function loadCliOptions(){
    const cliOptions = util.getCliOptions();
    global.config.port = cliOptions.getValue("p", 4981);
}

function configureApp(){
    app.configure(function(){
        app.use(allowCORS);
    });

    app.set('views', path.join(__dirname, 'views'));
    app.set('view engine', 'jade');
    app.use(express.favicon());
    app.use(express.json());
    app.use(express.urlencoded());
    app.use(express.methodOverride());
    app.use(app.router);
    app.use(express.static(path.join(__dirname, 'public')));

    if ('development' === app.get('env')) {
        app.use(express.errorHandler());
    }

    initErrorLog();
    setWebApis()
}

function allowCORS(req, res, next){
    res.header("Access-Control-Allow-Origin", "*");
    res.header("Access-Control-Allow-Headers", "X-Requested-With");
    res.header('Access-Control-Allow-Methods', 'GET,PUT,POST,DELETE');

    if ('OPTIONS' === req.method) {
        res.send(200);
    }

    next();
}

function initErrorLog(){
    app.on('error', function(err){
        log.error(err);
    });

    process.on('uncaughtException', function (e){
        log.error(e);
    });
}

function setWebApis(){
    app.get('/api/v1/server', server.getServerList);
    app.get('/api/v1/server/last-modified-time', server.getServerListLastModifiedTime);
    app.get('/api/v1/server-detailed-status', getServerDetailedStatus);
    app.post('/api/v2/server/list-update', server.updateServerList);
    app.get('/api/v1/server/config', server.getConfig);

    app.get('/api/v2/user', user.getAll);
    app.get('/api/v2/user/extra-info/:userIdList', user.getMoreInfoByUserIdList);
    app.get('/api/v2/user-count/:projectName', user.getUserCountByProjectName);
    app.get('/api/v2/user-status', user.getUserStatus);

    app.get('/api/v2/defect/min-year', defect.getMinYear);
    app.get('/api/v2/defect/max-year', defect.getMaxYear);
    app.get('/api/v2/defect/max-week/:year', defect.getMaxWeek);
    app.get('/api/v2/defect', defect.getAll);
    app.get('/api/v2/defect/group/:year/:week', defect.getByGroup);
    app.get('/api/v2/defect-status-count/:projectName', defect.getDefectCountByProjectName);

    app.get('/api/v2/project-list', project.getProjectList);
    app.get('/api/v2/snapshot-summary', project.getSnapshotSummary);
	app.post('/api/v2/create-project',project.createProject);
}

function initModules(){
    database.init(runOptions);
    server.init();
}

function startServer(){
    if(!global.config.port || global.config.port < 1024 || global.config.port >= 65535){
        log.error("you should set the port for monitor server");
        process.exit(-2);
    }
    if(!global.config.serverRequestTimeout || global.config.serverRequestTimeout < 1) {
        log.error("serverRequestTimeout value should be greater than or equal to 1 (5 is set instead)");
        global.config.serverRequestTimeout = 5;
    }
    if(!global.config.projectStatusRefreshInterval || global.config.projectStatusRefreshInterval < 5) {
        log.error("projectStatusRefreshInterval value should be greater than or equal to 5 (60 is set instead)");
        global.config.projectStatusRefreshInterval = 60;
    }

    http.createServer(app).listen(global.config.port, function(){
        log.info('Dexter Monitor is listening on port ' + global.config.port);
    });
}

function setScheduler() {
    const time = `0 0 * * ${global.config.snapshotDayOfWeek}`;
    schedule.scheduleJob(time, () => {
        log.info('Start saving snapshot');
        defect.saveSnapshot();
        project.saveSnapshotSummary();
    });
}

function getServerDetailedStatus (req, res){
    res.jsonp({
        "isAlive":"ok",
        "pid": process.pid,
        "memory": process.memoryUsage(),
        "uptime": process.uptime(),
        "ip": global.config.ip,
        "port": global.config.port
    });
}