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
var chai = require('Chai');
var assert = require("assert");
var should = chai.should;

var request = require('request');
var http = require('http');
var sinon = require('sinon');

var proxyquire = require('proxyquire');
var server;

describe('RESTful API Test Suite', function() {
    before(function(){
        createAndRunDexterServer();
    });

    function createAndRunDexterServer(){
        process.env.PORT = 4982;

        var databaseStub = createDatabaseStub();
        var accountStub = createAccountStub();
        var configStub = createConfigStub();
        var logStub = createLogStub();
        var authUtilStub = createAuthUtilStub();
        var analysisStub = createAnalysisStub();
        var dexterUtilStub = createDexterUtilStub();

        server = proxyquire('../../server', {
            './util/database': databaseStub,
            './routes/account': accountStub,
            './routes/analysis': analysisStub,
            './routes/config': configStub,
            './util/logging': logStub,
            './util/auth-util': authUtilStub,
            './util/dexter-util': dexterUtilStub
        });
    }

    function createDatabaseStub(){
        var databaseStub = sinon.stub();
        databaseStub.init = function() { };
        databaseStub.getConnection = function() { };

        return databaseStub;
    }

    function createAccountStub(){
        var accountStub = sinon.stub();
        accountStub.init = function() { };
        accountStub.getAccountCount = function(req, res){ res.send(200); };
        accountStub.userId = function(req, res){ res.send(200); };
        accountStub.checkWebLogin = function(req, res){ res.send(200); };
        accountStub.logout = function(req, res){ res.send(200); };
        accountStub.findAll = function(req, res){ res.send(200); };
        accountStub.findById = function(req, res){ res.send(200); };
        accountStub.hasAccount = function(req, res){ res.send(200); };
        accountStub.checkLogin = function(req, res){ res.send(200); };
        accountStub.checkAdmin = function(req, res){ res.send(200); };

        accountStub.add = function(req, res){ res.send(200); };
        accountStub.webAdd = function(req, res){ res.send(200); };
        accountStub.update = function(req, res){ res.send(200); };
        accountStub.webUpdate = function(req, res){ res.send(200); };

        accountStub.remove = function(req, res){ res.send(200); };
        accountStub.removeAll = function(req, res){ res.send(200); };

        return accountStub;
    }

    function createConfigStub(){
        var configStub = sinon.stub();
        configStub.addAccessLog = function(req, res) { };
        configStub.getDefectGroup = function(req, res) {res.send(200); };
        configStub.getDefectGroupId = function(req, res) {res.send(200); };
        configStub.getCodes = function(req, res) {res.send(200); };
        configStub.getCheckerConfigJsonFile = function(req, res) {res.send(200); };
        configStub.deleteDefectGroup = function(req, res) { res.send(200);};

        return configStub;
    }

    function createLogStub(){
        var logStub = sinon.stub();
        logStub.info = function(message){};
        logStub.warn = function(message){};
        logStub.error = function(message){};
        logStub.debug = function(message){};

        return logStub;
    }

    function createAuthUtilStub(){
        var authUtilStub = sinon.stub();
        var express = require('express');
        authUtilStub.getBasicAuth = express.basicAuth(function(user, pass){
            return true;
        });

        return authUtilStub;
    }

    function createAnalysisStub(){
        var analysisStub = sinon.stub();
        analysisStub.getProjectDefectStatus = function(req, res){ res.send(200); };
        analysisStub.getFileDefectStatus = function(req, res){ res.send(200); };
        analysisStub.getAllFalseAlarm = function(req, res){ res.send(200); };
        analysisStub.getAllFalseAlarmList = function(req, res){ res.send(200); };
        analysisStub.getModuleAndFileName = function(req, res){ res.send(200); };
        analysisStub.getModuleDefectStatus = function(req, res){ res.send(200); };
        analysisStub.getDefectsByModuleAndFile = function(req, res){ res.send(200); };
        analysisStub.getDefectCountByModuleAndFile = function(req, res){ res.send(200); };
        analysisStub.getDefectCount = function(req, res){ res.send(200); };
        analysisStub.getOccurencesByDid = function(req, res){ res.send(200); };
        analysisStub.getAllSnapshot = function(req, res){ res.send(200); };
        analysisStub.getCodeMetrics = function(req, res){ res.send(200); };
        analysisStub.getCodeMetricsAndDefects = function(req, res){ res.send(200); };
        analysisStub.getCodeMetricsAndDefectsLimit = function(req, res){ res.send(200); };
        analysisStub.getCheckerAndDefects = function(req, res){ res.send(200); };
        analysisStub.getDevelopers = function(req, res){ res.send(200); };

        return analysisStub;
    }

    function createDexterUtilStub(){
        var stub = sinon.stub();
        stub.getCliOptions = function (){
            return {
                options: {},
                getCliValue : function(key, defaultValue){
                    if(defaultValue) return defaultValue;
                    else return "";
                }
            };
        };

        /*
        stub.getCliValue = function(key, defaultValue){
            if(defaultValue) return defaultValue;
            else return "";
        };*/

        return stub;
    }

    after(function(){
        server.forceStopServer();
    });

    function checkGetMethodReturnValue(apiUrl, statusCode, methodType, done){
        var url = 'http://userid:password@localhost:4982/' + apiUrl;

        var options = {
            url: url,
            method: methodType
        };

        request(options, function (err, res) {
            assert.ok(!err);
            assert.ok(res);
            assert.equal(res.statusCode, statusCode);
            done();
        });
    }

    function itWithTestData(testData){
        testData.forEach(function(data){
            if(!data.methodType){
                data.methodType = 'GET';
            }

            it('API Exisitng Test for [' + data.apiUrl + '] on ' + data.methodType + ' method', function(done){
                checkGetMethodReturnValue(data.apiUrl, data.statusCode, data.methodType, done);
            })
        });
    }

    describe('For Accounts API', function() {
        var testData = [
            // get
            {apiUrl:'api/defect/status/project', statusCode:200},
            {apiUrl:'api/defect/status/fileName', statusCode:200},

            {apiUrl:'api/v1/accounts/userId', statusCode:200},
            {apiUrl:'api/v1/accounts/checkWebLogin', statusCode:200},
            {apiUrl:'api/v1/accounts/logout', statusCode:200},
            {apiUrl:'api/v1/accounts/accountCount', statusCode:200},
            {apiUrl:'api/v1/accounts/findAll', statusCode:200},
            {apiUrl:'api/v1/accounts/findById/' + 'myUserId', statusCode:200},
            {apiUrl:'api/v1/accounts/hasAccount/' + 'myUserId', statusCode:200},
            {apiUrl:'api/v1/accounts/checkLogin', statusCode:200},
            {apiUrl:'api/v1/accounts/checkAdmin', statusCode:200},

            // post
            {apiUrl:'api/v1/accounts/add', statusCode:200, methodType:'POST'},
            {apiUrl:'api/v1/accounts/webAdd', statusCode:200, methodType:'POST'},
            {apiUrl:'api/v1/accounts/update/' + 'myUserId', statusCode:200, methodType:'POST'},
            {apiUrl:'api/v1/accounts/webUpdate/' + 'myUserId', statusCode:200, methodType:'POST'},

            // delete
            {apiUrl:'api/v1/accounts/remove/' + 'myUserId', statusCode:200, methodType: 'DELETE'},
            {apiUrl:'api/v1/accounts/removeAll', statusCode:200, methodType: 'DELETE'}
        ];

        itWithTestData(testData);
    });

    describe('For Server Managing API', function() {
        var testData = [
            // get
            {apiUrl:'api/v1/isServerAlive', statusCode:200},
            {apiUrl:'api/v1/isServerAlive2', statusCode:200}
        ];

        itWithTestData(testData);
    });

    describe('For Analysis API', function() {
        var testData = [
            // get
            {apiUrl:'api/v1/analysis/snapshot/source', statusCode:200},
            {apiUrl:'api/v1/analysis/snapshot/checkSourceCode', statusCode:200},

            // post
            {apiUrl:'api/v1/analysis/result', statusCode:200, methodType: 'POST'},
            {apiUrl:'api/v1/analysis/snapshot/source', statusCode:200, methodType: 'POST'}
        ];

        itWithTestData(testData);
    });

    describe('For Defect Filter API', function() {
        var testData = [
            // get
            {apiUrl:'api/v1/filter/false-alarm', statusCode:200},
            {apiUrl:'api/v1/filter/false-alarm-list', statusCode:200},

            // post
            {apiUrl:'api/v1/filter/false-alarm', statusCode:200, methodType: 'POST'},
            {apiUrl:'api/v1/filter/delete-false-alarm', statusCode:200, methodType: 'POST'},
            {apiUrl:'api/v1/filter/delete-file-tree', statusCode:200, methodType: 'POST'}
        ];

        itWithTestData(testData);
    });

    describe('For Defect API', function() {
        var testData = [
            // get
            {apiUrl:'api/v1/defect/moduleAndFile', statusCode:200},
            {apiUrl:'api/v1/defect/status/project', statusCode:200},
            {apiUrl:'api/v1/defect/status/modulePath', statusCode:200},
            {apiUrl:'api/v1/defect/status/fileName', statusCode:200},
            {apiUrl:'api/v1/defect', statusCode:200},
            {apiUrl:'api/v1/defect/count', statusCode:200},
            {apiUrl:'api/v1/webDefectCount', statusCode:200},
            {apiUrl:'api/v1/occurence/' + '123', statusCode:200},
            {apiUrl:'api/v1/occurenceInFile', statusCode:200},

            // post
            {apiUrl:'api/v1/defect/gid', statusCode:200, methodType: 'POST'},
            {apiUrl:'api/v1/defect/dismiss', statusCode:200, methodType: 'POST'},
            {apiUrl:'api/v1/defect/markFalseDefect', statusCode:200, methodType: 'POST'},
            {apiUrl:'api/v1/defect/markDefect', statusCode:200, methodType: 'POST'},
            {apiUrl:'api/v1/defect/changeFix', statusCode:200, methodType: 'POST'},

            // delete
            {apiUrl:'api/v1/defect/deleteAll', statusCode:200, methodType: 'DELETE'}
        ];

        itWithTestData(testData);
    });

    describe('For Snapshot API', function() {
        var testData = [
            // get
            {apiUrl:'api/v1/snapshot/snapshotList', statusCode:200},
            {apiUrl:'api/v1/snapshot/showSnapshotDefectPage', statusCode:200},
            {apiUrl:'api/v1/snapshot/occurenceInFile', statusCode:200}
        ];

        itWithTestData(testData);
    });

    describe('For Code Metrics API', function() {
        var testData = [
            // get
            {apiUrl:'api/v1/metrics', statusCode:200},
            {apiUrl:'api/v1/metrics-and-defect', statusCode:200},
            {apiUrl:'api/v1/metrics-and-defect-limit', statusCode:200},
            {apiUrl:'api/v1/checker-and-defect', statusCode:200},
            {apiUrl:'api/v1/developer-and-file', statusCode:200}
        ];

        itWithTestData(testData);
    });

    describe('For Configuration API', function() {
        var testData = [
            // get
            {apiUrl:'api/v1/config/defect-group/' + 'mygroupName', statusCode:200},
            {apiUrl:'api/v1/config/defect-group', statusCode:200},
            {apiUrl:'api/v1/config/defect-group-id/' + 'myGroupName', statusCode:200},
            {apiUrl:'api/v1/config/code/:codeKey', statusCode:200},
            {apiUrl:'api/v1/version/false-alarm', statusCode:200},

            // post
            {apiUrl:'api/v1/config/defect-group', statusCode:200, methodType: 'POST'},
            {apiUrl:'api/v1/config/' + 'dexter-cppcheck', statusCode:200, methodType: 'POST'},

            // put
            {apiUrl:'api/v1/config/defect-group', statusCode:200, methodType: 'PUT'},

            // delete
            {apiUrl:'api/v1/config/defect-group/' + 'myGroupId', statusCode:200, methodType: 'DELETE'}
        ];

        itWithTestData(testData);
    });
});