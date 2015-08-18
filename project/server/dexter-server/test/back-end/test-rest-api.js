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

var assert = require("assert");
var should = require('should');
var request = require('request');
var http = require('http');
var sinon = require('sinon');
var server;

describe('RESTful API Test Suite', function() {

    before(function(){
    });

    after(function(){
    });

    beforeEach(function(){
        process.env.PORT = 4989;
        server = require('../../server');
    });

    afterEach(function(){
        var req = sinon.stub();
        req.currentUserId = 'admin';
        server.forceStopServer();
    });


    describe('For Accounts', function() {
        it('GET / getProjectDefectStatus', function (done) {
            request('http://localhost:4989/api/defect/status/project', function (err, res, body) {
                var req = { query: { statusCode: 'FIX'} };
                analysis = require('../../routes/analysis');
                analysis.getProjectDefectStatus(req);
                assert.equal(res.statusCode, 200);
            });
            done();
        });

        it('GET / getFileDefectStatus', function (done) {
            request('http://localhost:4989/api/defect/status/fileName', function (err, res, body) {
                var req = { query: { statusCode: 'FIX'} };
                analysis = require('../../routes/analysis');
                analysis.getFileDefectStatus(req);
                assert.equal(res.statusCode, 200);
            });
            done();
        });
    });

    describe('For Defect Count', function() {

        it('GET / getDefectCountByModuleAndFile', function (done) {
            request('http://localhost:4989/api/defect/count', function (err, res, body) {
                var req = { body: { didList: '13' } };
                analysis = require('../../routes/analysis');
                analysis.getDefectCountByModuleAndFile(req);
                assert.equal(res, 200);
            });
            done();
        });
    });

    describe('For Auth Account', function() {
        it('GET / Auth for findById', function (done) {
            var options = {
                url: 'http://localhost:4989/api/accounts/findById/:userId',
                headers: {
                    'Authorization': 'Basic bWluaG86MTIzNA=='
                }
            };
            request(options, function (err, res, body) {
                var req = { body: { userId: 'minjung.baek' } };
                analysis = require('../../routes/analysis');
                analysis.findById(req);
                assert.equal(res.statusCode, 200);
            });
            done();
        });
    });


    describe('For Change Defect Status as Auth Account', function() {
        it('POST / changeDefectToDismiss', function (done) {
            var options = {
                url: 'http://localhost:4989/api/defect/changeDismiss',
                headers: {
                    'Authorization': 'Basic bWluaG86MTIzNA=='
                }
            };
            request(options, function (err, res, body) {
                var req = { body: { didList: '13' } };
                analysis = require('../../routes/analysis');
                analysis.findById(req);
                assert.equal(res.statusCode, 200);
            });
            done();
        });
    });

    /*it('GET / All Accounts List with wrong address', function(done) {
        request('http://localhost:4989/api/account', function(err, res, body){
            assert.equal(res.statusCode, 404);
        });
        done();
    });

    it('GET / All Accounts List without Authorization', function(done) {
        request('http://localhost:4989/api/accounts', function(err, res, body){
            assert.equal(res.statusCode, 401);
        });
        done();
    });

    it('GET / All Accounts List with Authorization', function(done) {
        var options = {
            url: 'http://localhost:4989/api/accounts',
            headers: {
                'Authorization': 'Basic bWluaG86MTIzNA=='
            }
        };
        request(options, function(err, res, body){
            assert.equal(res.statusCode, 200);
        });
        done();
    });
    */

});
