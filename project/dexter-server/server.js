/**
 * Copyright (c) 2014 Samsung Electronics, Inc.,
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

var express = require('express');
var path = require('path');
var http = require('http');

var database = require("./util/database");
var util = require('./util/dexter-util');
var log = require('./util/logging');
var authUtil = require('./util/auth-util');

var account = require("./routes/account");
var analysis = require("./routes/analysis");
var config = require("./routes/config");
var codeMetrics = require("./routes/codeMetrics");
var functionMetrics = require("./routes/functionMetrics");

var app = express();

global.runOptions = {
    port:4982,
    databaseHost:'localhost',
    databasePort:3306,
    databaseUser:'',
    databasePassword:'',
    databaseAdminUser:'',
    databaseAdminPassword:'',
    databaseName:'',
    serverName:'dexter-server-default',
    getDbUrl : function(){
        return this.databaseName + "@" + this.databaseHost + ':' + this.databasePort;
    }
};

var auth = authUtil.getBasicAuth;

var noNeedAccessLogUriList = [
    '/api/isServerAlive',
    '/api/isServerAlive2',
    '/api/analysis/snapshot/source',
    '/api/version/false-alarm',
    '/api/accounts/checkLogin',
    '/api/v1/isServerAlive',
    '/api/v1/isServerAlive2',
    '/api/v2/server-status',
    '/api/v2/server-detailed-status',
    '/api/v1/analysis/snapshot/source',
    '/api/v1/version/false-alarm',
    '/api/v1/accounts/checkLogin'
];

exports.startServer = startServer;
exports.stopServer = stopServer;

initialize();

function initialize(){
    setRunOptionsByCliOptions();
    setExecutionMode();
    setAppConfigure();
    initModules();
    initRestAPI();
    startServer();
}

function setRunOptionsByCliOptions(){
    var cliOptions = util.getCliOptions();

    global.runOptions.port = cliOptions.getValue('p', 4982);
    global.runOptions.databaseHost = cliOptions.getValue('database.host', 'localhost');
    global.runOptions.databasePort = cliOptions.getValue('database.port', 3306);
    global.runOptions.databaseUser = cliOptions.getValue('database.user', '');
    global.runOptions.databasePassword = cliOptions.getValue('database.password', '');
    global.runOptions.databaseAdminUser = cliOptions.getValue('database.admin.user', '');
    global.runOptions.databaseAdminPassword = cliOptions.getValue('database.admin.password', '');
    global.runOptions.databaseName = cliOptions.getValue('database.name', '');
    global.runOptions.serverName = cliOptions.getValue('server.name', 'dexter-server-default');
    global.runOptions.serverIP = util.getLocalIPAddress();
}

function setExecutionMode(){
    if(process.env.NODE_ENV === undefined){
        process.env.NODE_ENV = 'production';
    }
}

function setAppConfigure(){
    app.configure(function () {
        app.set("jsonp callback", true);
        app.set('views', path.join(__dirname, 'views'));
        app.set('view engine', 'jade');
        app.use(express.static(path.join(__dirname, 'public')));
        app.use(express.json({limit:'50mb'}));
        app.use(express.urlencoded());
        app.use(express.methodOverride());
    });

    app.configure('development', function(){
        app.use(express.errorHandler({ dumpExceptions: true, showStack: true}));
    });


    app.configure('production', function(){
        app.use(express.errorHandler({"dumpExceptions": false, "showStack": false}));
    });


    app.all('*', function(req, res, next){
        setCurrentUserIdAndNoOnRequest(req);
        addAccessLog(req);
        setResponseHeaderSupporingCORS(res);

        next();
    });
}

function initModules(){
    log.init();
    database.init();
    account.init();
}

function setCurrentUserIdAndNoOnRequest(req, res){
    req.currentUserId = util.getUserId(req);
    req.currentUserNo = account.getUserNo(req.currentUserId);
}

function addAccessLog(req){
    if(isNoNeedToAddAccessLog(req.url)){
        return;
    }

    var parameter = {
        remoteAddress: req.remoteAddress,
        currentUserNo: req.currentUserNo,
        uri: req.url,
        method: req.method,
        query: req.query
    };

    config.addAccessLog(parameter);
}

function isNoNeedToAddAccessLog(url){
    return noNeedAccessLogUriList.indexOf(url) >= 0;
}

// CORS : Cross-Origin Resource Sharing
function setResponseHeaderSupporingCORS(res){
    res.setHeader('Access-Control-Allow-Origin', '*');
    res.setHeader('Access-Control-Allow-Methods', 'GET');
    res.setHeader('Access-Control-Allow-Headers', 'X-Requested-With,content-type');

    res.header("Access-Control-Allow-Origin", "*");
    res.header("Access-Control-Allow-Headers", "X-Requested-With");
}

function initRestAPI(){
    /***** URL to Handler Mapping *****/
    app.get('/api/v1/projectName', database.getProjectName);
    /* Managing Accounts */
    app.get('/api/accounts/userId', auth, account.userId);
    app.get('/api/accounts/checkWebLogin', auth, account.checkWebLogin);
    app.get('/api/accounts/logout',auth, account.logout);
    app.get('/api/accounts/accountCount', account.getAccountCount);
    app.get('/api/accounts/findAll', auth, account.findAll);
    app.get('/api/accounts/findById/:userId', auth, account.findById);
    app.get('/api/accounts/hasAccount/:userId', account.hasAccount);
    app.get('/api/accounts/checkLogin', account.checkLogin);
    app.get('/api/accounts/checkAdmin', auth, account.checkAdminAccount);
    app.post('/api/accounts/add', account.add);
    app.post('/api/accounts/webAdd', account.webAdd);
    app.post('/api/accounts/update/:userId', account.update);
    app.post('/api/accounts/webUpdate/:userId', account.webUpdate);
    app.delete('/api/accounts/remove/:userId', auth, account.remove);
    app.delete('/api/accounts/removeAll', auth, account.removeAll);

    /* Server Managing */
    app.get('/api/isServerAlive', checkServer);
    app.get('/api/isServerAlive2', checkServer2);
    app.delete('/api/server', stopServer);
    app.delete('/api/dexter-db', auth, deleteDexterDatabase);

    /* Analysis Result */
    app.post('/api/analysis/result', auth, analysis.add);
    app.post('/api/analysis/snapshot/source', auth, analysis.addSnapshotSourceCode);
    app.get('/api/analysis/snapshot/source', auth, analysis.getSnapshotSourceCode);
    app.get('/api/analysis/snapshot/checkSourceCode', auth, analysis.checkSnapshotSourceCode);

    /* Defect Filter */
    //app.get('/api/filter/defect/maxid', auth, analysis.getMaxIdOfDexterFilter);
    app.get('/api/filter/false-alarm', auth, analysis.getAllFalseAlarm);
    app.get('/api/filter/false-alarm-list', auth, analysis.getAllFalseAlarmList);
    app.post('/api/filter/false-alarm', auth, analysis.addFalseAlarm);
    app.post('/api/filter/delete-false-alarm', auth, analysis.removeFalseAlarm);
    app.post('/api/filter/delete-file-tree', auth, analysis.removeFileTree);

    /* Defect */
    app.post('/api/defect/gid', auth, analysis.getGlobalDid);
    app.delete('/api/defect/deleteAll', auth, analysis.deleteDefect);
    app.post('/api/defect/dismiss', auth, analysis.changeDefectStatus);
    app.get('/api/defect/moduleAndFile', auth, analysis.getModuleAndFileName);
    app.post('/api/defect/markFalseDefect', auth, analysis.changeDefectToDismiss);
    app.post('/api/defect/markDefect', auth, analysis.changeDefectToNew);
    app.post('/api/defect/changeFix', auth, analysis.changeDefectToFix);
    app.get('/api/defect/status/project', analysis.getProjectDefectStatus);
    app.get('/api/defect/status/modulePath', analysis.getModuleDefectStatus);
    app.get('/api/defect/status/fileName', analysis.getFileDefectStatus);
    app.get('/api/defect', analysis.getDefectsByModuleAndFile);
    app.get('/api/defect/count', analysis.getDefectCountByModuleAndFile);
    app.get('/api/webDefectCount', analysis.getDefectCount);
    app.get('/api/occurence/:did', analysis.getOccurencesByDid);
    app.get('/api/occurenceInFile', analysis.getOccurencesByFileName);

    /* SnapshotDefectMap / SnapshotSourcecodeMap*/
    app.get('/api/snapshot/snapshotList', analysis.getAllSnapshot);
    app.get('/api/snapshot/showSnapshotDefectPage',analysis.getDefectListInSnapshot);
    app.get('/api/snapshot/occurenceInFile', analysis.getOccurencesByFileNameInSnapshot);

    /* code metrics */
    app.get('/api/metrics', analysis.getCodeMetrics);
    app.get("/api/metrics-and-defect", analysis.getCodeMetricsAndDefects);
    app.get("/api/metrics-and-defect-limit", analysis.getCodeMetricsAndDefectsLimit);
    app.get("/api/checker-and-defect", analysis.getCheckerAndDefects);
    app.get("/api/developer-and-file", analysis.getDevelopers);

    /* Config & SharedDataVersion */
    app.post('/api/config/defect-group', auth, config.addDefectGroup);
    app.put('/api/config/defect-group', auth, config.updateDefectGroup);
    app.get('/api/config/defect-group/:groupName', auth, config.getDefectGroup);
    app.get('/api/config/defect-group', auth, config.getDefectGroup);
    app.get('/api/config/defect-group-id/:groupName', auth, config.getDefectGroupId);
    app.delete('/api/config/defect-group/:id', auth, config.deleteDefectGroup);
    app.get('/api/config/code/:codeKey', config.getCodes);
    app.get('/api/version/false-alarm', analysis.getFalseAlarmVersion);
    app.get('/api/config/update-url/_32', function(req, res){
        util.getLocalhostIp(function(localhostIp) {
            var url = "http://"+localhostIp + ":" + global.runOptions.port + "/plugin/32";
            res.send(200, {status: "ok", "url": url });
        });
    });
    app.get('/api/config/update-url/_64', function(req, res){
        util.getLocalhostIp(function(localhostIp) {
            res.send(200, {status: "ok", "url": "http://"+ localhostIp + ":" +  global.runOptions.port + "/plugin/64" });
        });
    });

    //////////////////////////// API v1
    /* Managing Accounts v1 */
    app.get('/api/v1/accounts/userId', auth,account.userId);
    app.get('/api/v1/accounts/checkWebLogin', auth, account.checkWebLogin);
    app.get('/api/v1/accounts/logout',auth, account.logout);
    app.get('/api/v1/accounts/accountCount', account.getAccountCount);
    app.get('/api/v1/accounts/findAll', auth, account.findAll);
    app.get('/api/v1/accounts/findById/:userId', auth, account.findById);
    app.get('/api/v1/accounts/hasAccount/:userId', account.hasAccount);
    app.get('/api/v1/accounts/checkLogin', account.checkLogin);
    app.get('/api/v1/accounts/checkAdmin', auth, account.checkAdminAccount);
    app.post('/api/v1/accounts/add', account.add);
    app.post('/api/v1/accounts/webAdd', account.webAdd);
    app.post('/api/v1/accounts/update/:userId', account.update);
    app.post('/api/v1/accounts/webUpdate/:userId', account.webUpdate);
    app.delete('/api/v1/accounts/remove/:userId', auth, account.remove);
    app.delete('/api/v1/accounts/removeAll', auth, account.removeAll);

    /* Server Managing v1 */
    app.get('/api/v1/isServerAlive', checkServer);
    app.get('/api/v1/isServerAlive2', checkServer2);
    app.get('/api/v2/server-status', checkServer2);
    app.get('/api/v2/server-detailed-status', getServerDetailedStatus);
    app.delete('/api/v1/server', stopServer);
    app.delete('/api/v1/dexter-db', auth, deleteDexterDatabase);

    /* Analysis Result */
    app.post('/api/v1/analysis/result', auth, analysis.add);
    app.post('/api/v1/analysis/snapshot/source', auth, analysis.addSnapshotSourceCode);
    app.get('/api/v1/analysis/snapshot/source', auth, analysis.getSnapshotSourceCode);
    app.get('/api/v1/analysis/snapshot/checkSourceCode', auth, analysis.checkSnapshotSourceCode);

    /* Defect Filter */
    //app.get('/api/filter/defect/maxid', auth, analysis.getMaxIdOfDexterFilter);
    app.get('/api/v1/filter/false-alarm', auth, analysis.getAllFalseAlarm);
    app.get('/api/v1/filter/false-alarm-list', auth, analysis.getAllFalseAlarmList);
    app.post('/api/v1/filter/false-alarm', auth, analysis.addFalseAlarm);
    app.post('/api/v1/filter/delete-false-alarm', auth, analysis.removeFalseAlarm);
    app.post('/api/v1/filter/delete-file-tree', auth, analysis.removeFileTree);

    /* Defect */
    app.post('/api/v1/defect/gid', auth, analysis.getGlobalDid);
    app.post('/api/v1/defect/deleteAll', auth, analysis.deleteDefect);
    app.delete('/api/v1/defect/deleteAll', auth, analysis.deleteDefect);
    app.post('/api/v1/defect/dismiss', auth, analysis.changeDefectStatus);
    app.get('/api/v1/defect/moduleAndFile', auth, analysis.getModuleAndFileName);
    app.post('/api/v1/defect/markFalseDefect', auth, analysis.changeDefectToDismiss);
    app.post('/api/v1/defect/markDefect', auth, analysis.changeDefectToNew);
    app.post('/api/v1/defect/changeFix', auth, analysis.changeDefectToFix);
    app.get('/api/v1/defect/status/project', analysis.getProjectDefectStatus);
    app.get('/api/v1/defect/status/modulePath', analysis.getModuleDefectStatus);
    app.get('/api/v1/defect/status/fileName', analysis.getFileDefectStatus);
    app.get('/api/v1/defect', analysis.getDefectsByModuleAndFile);
    app.get('/api/v1/defect/count', analysis.getDefectCountByModuleAndFile);
    app.get('/api/v1/webDefectCount', analysis.getDefectCount);
    app.get('/api/v1/occurence/:did', analysis.getOccurencesByDid);
    app.get('/api/v1/occurenceInFile', analysis.getOccurencesByFileName);

    /* SnapshotDefectMap / SnapshotSourcecodeMap*/
    app.get('/api/v1/snapshot/snapshotList', analysis.getAllSnapshot);
    app.get('/api/v1/snapshot/showSnapshotDefectPage',analysis.getDefectListInSnapshot);
    app.get('/api/v1/snapshot/occurenceInFile', analysis.getOccurencesByFileNameInSnapshot);

    /* code metrics */
    app.get('/api/v1/metrics', analysis.getCodeMetrics);
    app.get("/api/v1/metrics-and-defect", analysis.getCodeMetricsAndDefects);
    app.get("/api/v1/metrics-and-defect-limit", analysis.getCodeMetricsAndDefectsLimit);
    app.get("/api/v1/checker-and-defect", analysis.getCheckerAndDefects);
    app.get("/api/v1/developer-and-file", analysis.getDevelopers);

    /* Config & SharedDataVersion */
    app.post('/api/v1/config/defect-group', auth, config.addDefectGroup);
    app.put('/api/v1/config/defect-group', auth, config.updateDefectGroup);
    app.get('/api/v1/config/defect-group/:groupName', auth, config.getDefectGroup);
    app.get('/api/v1/config/defect-group', auth, config.getDefectGroup);
    app.get('/api/v1/config/defect-group-id/:groupName', auth, config.getDefectGroupId);
    app.delete('/api/v1/config/defect-group/:id', auth, config.deleteDefectGroup);
    app.get('/api/v1/config/code/:codeKey', config.getCodes);
    app.get('/api/v1/version/false-alarm', analysis.getFalseAlarmVersion);
    app.get('/api/v1/config/update-url/_32', function(req, res){
        util.getLocalhostIp(function(localhostIp) {
            res.send(200, {status: "ok", "url": "http://"+localhostIp + ":" + global.runOptions.port + "/plugin/32" });
        });
    });
    app.get('/api/v1/config/update-url/_64', function(req, res){
        util.getLocalhostIp(function(localhostIp) {
            res.send(200, {status: "ok", "url": "http://"+ localhostIp + ":" +  global.runOptions.port + "/plugin/64" });
        });
    });

    /* Get Latest Checekr Config Json File */
    app.post('/api/v1/config/:pluginName', config.getCheckerConfigJsonFile);

    /* Code Metrics For Dexter Client*/
    app.get('/api/v1/codeMetrics/total', codeMetrics.getTotalCodeMetrics);
    app.get('/api/v1/codeMetrics/', codeMetrics.getCodeMetricsFromClient);
    app.get('/api/v1/codeMetrics/ccList', codeMetrics.getCodeMetricsForCC);
    app.get('/api/v1/codeMetrics/sloc', codeMetrics.getFunctionMetricsForSloc);

    app.post('/api/v1/codeMetrics/topCodeMetrics', codeMetrics.getTopCodeMetrics);

    app.get('/api/v1/codeMetrics/topCC', codeMetrics.getTopCCForCodeMetrics);
    app.get('/api/v1/codeMetrics/topMethod', codeMetrics.getTopMethodForCodeMetrics);
    app.get('/api/v1/codeMetrics/topClass', codeMetrics.getTopClassForCodeMetrics);
    app.get('/api/v1/codeMetrics/topSloc', codeMetrics.getTopSLOCForCodeMetrics);

    app.get('/api/v1/codeMetrics/threshold', codeMetrics.getCodeMetricsThreshold);
    app.get('/api/v1/codeMetrics/quantity', codeMetrics.getCodeMetricsQuantity);


    /** API VERSION 2 **/
    app.post('/api/v2/analysis/result', auth, analysis.addV2);
    app.get('/api/v2/defect', analysis.getDefectsByModuleAndFileV2);
    app.get('/api/v2/snapshot/showSnapshotDefectPage',analysis.getDefectListInSnapshotV2);

    app.get('/api/v2/defect/security', analysis.getDefectForSecurity);
    app.post('/api/v2/defect/deleteAll', auth, analysis.deleteDefect);

    app.get('/api/v2/functionMetrics/', functionMetrics.getTotalFunctionMetrics);
    app.get('/api/v2/functionMetrics/All', functionMetrics.getAllFunctionMetrics);
    app.post('/api/v2/functionMetrics', functionMetrics.getFunctionMetrics);

    app.get('/api/2/codeMetrics/slocList', codeMetrics.getCodeMetricsForSloc);
    /* SnapshotDefectMap / SnapshotSourcecodeMap*/
    app.get('/api/v2/snapshot/snapshotList', analysis.getAllSnapshotV2);
    app.get('/api/v2/snapshot/security', analysis.getSnapshotDefectForSecurity);

    /* For CSV file */
    app.get('/api/v2/defect/All', analysis.getDefectForCSV);
    app.get('/api/v2/snapshot/All', analysis.getSnapshotDefectForCSV);

}

function startServer(){
    http.globalAgent.maxSockets = Infinity;  // 5, 10, ...

    dexterServer = http.createServer(app).listen(global.runOptions.port, function(){
        log.info('Dexter server listening on port ' + global.runOptions.port);
        log.info('Dexter server location : ' + __dirname);
        log.info("Execution Mode : " + app.get('env'));
    });
};

//use UT Code
function stopServer (req, res){
    var userId = req.currentUserId;

    if(account.checkAdmin(userId) === false) {
        log.info('only administrator can stop the server : ' + userId);
        if(res != undefined) res.send("fail");
        return;
    }

    log.info('Dexter server is closing on port ' + global.runOptions.port);

    if(res != undefined) res.send("ok");

    dexterServer.close();
    process.exit(1);
};

exports.forceStopServer = function(){
    dexterServer.close();
};


function deleteDexterDatabase (req, res){
    var userId = req.currentUserId;

    if(account.checkAdmin(userId) === false) {
        log.info('only administrator can delete Dexter Database : ' + userId);
        if(res != undefined) res.send("fail");
        return;
    }

    log.info('Dexter database will be removed by ' + userId);
    if(res != undefined) res.send("ok");
    database.deleteDexterDatabase();
};

function checkServer (req, res){
    res.status(200);
    res.send("ok");
    //res.send({"isAlive":"ok"});
    //res.writeHead(200, { 'Content-Type': 'application/json' });
    //res.jsonp({"isAlive":"ok"});
};

function checkServer2 (req, res){
    //res.writeHead(200, { 'Content-Type': 'application/json' });
    res.jsonp({"isAlive":"ok"});
}

function getServerDetailedStatus (req, res){
    res.jsonp({
        "isAlive":"ok",
        "pid": process.pid,
        "memory": process.memoryUsage(),
        "uptime": process.uptime(),
        "ip": global.runOptions.serverIP,
        "port": global.runOptions.port
    });
}
