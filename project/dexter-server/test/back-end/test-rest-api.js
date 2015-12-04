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

        server = proxyquire('../../server', {
            './util/database': databaseStub,
            './routes/account': accountStub,
            './routes/analysis': analysisStub,
            './routes/config': configStub,
            './util/logging': logStub,
            './util/auth-util': authUtilStub
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

        return analysisStub;
    }

    after(function(){
        server.forceStopServer();
    });

    function checkGetMethodReturnValue(apiUrl, statusCode, done){
        var url = 'http://userid:password@localhost:4982/' + apiUrl;

        var options = {
            url: url,
            method: 'GET'
        };

        request(options, function (err, res) {
            assert.ok(!err);
            assert.ok(res);
            assert.equal(res.statusCode, statusCode);
            done();
        });
    }

    describe('For Accounts Service', function() {
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
            {apiUrl:'api/v1/accounts/checkAdmin', statusCode:200}

            /*
            // post
            {apiUrl:'api/v1/accounts/add', statusCode:200},
            {apiUrl:'api/v1/accounts/webAdd', statusCode:200},
            {apiUrl:'api/v1/accounts/update/:userId', statusCode:200},
            {apiUrl:'api/v1/accounts/webUpdate/:userId', statusCode:200},

            // delete
            {apiUrl:'api/v1/accounts/remove/:userId', statusCode:200},
            {apiUrl:'api/v1/accounts/removeAll', statusCode:200}
            */
        ];

        testData.forEach(function(data, index){
            it('API Test for ' + data.apiUrl, function(done){
                checkGetMethodReturnValue(data.apiUrl, data.statusCode, done);
            })
        });

        // TODO: test for analysis, filter, defect, snapshot, code metrics, config, etc.
    });
});