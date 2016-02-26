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
"use strict";

var sinon = require('sinon');
var chai = require('chai');
var assert = chai.assert;
var rewire = require("rewire");

var logging = require('../../util/logging');

describe('test for util/database.js', function(){
    var database;

    before(function(){
        createDatabaseMock();
    });

    function createDatabaseMock(){
        database = rewire("../../util/database");

        database.__set__({
            global : {
                runOptions :  {
                    "databaseHost":"", "databasePort":"", "databaseUser":"", "databasePassword":"", "databaseName":"",
                    getDbUrl : function() {
                        return {};
                    }
                }
            },
            logging : {
                info: function(){},
                error: function(msg){
                    console.log(msg);
                }
            },
            mysql : {
                createPool: function(){
                    return {
                        query : function(sql, callback){
                            callback();
                        }
                    };
                }
            }
        });
    }

    describe('for init function', function(){
        it('should create database pool object', function(done){
            var mysql = database.__get__("mysql");
            var createPoolSpy = sinon.spy(mysql, 'createPool');

            database.init()
                .then(function(){
                    assert.equal(1, createPoolSpy.callCount);
                    var databasePool = database.__get__("databasePool");
                    assert.isNotNull(databasePool);
                    done();
                })
                .catch(function(err){
                    done(err);
                });
        });
    });

    describe('for getProjectName function', function(){
        it('should return proper project name when condition is fine', function(){
            database.__set__({
                global : {
                    runOptions :  {
                        "databaseHost":"", "databasePort":"", "databaseUser":"", "databasePassword":"", "databaseName":"test-db-name",
                        getDbUrl : function() { return {}; }
                    }
                }
            });

            var res = sinon.stub();
            var returnCode, returnMessage;
            res.send = function(code, arg){
                returnCode= code;
                returnMessage=arg;
            };
            database.getProjectName({}, res);

            assert.equal(200, returnCode);
            assert.equal("ok", returnMessage.status);
            assert.equal("test-db-name", returnMessage.projectName);
        });

        it('should return error when condition is fine', function(){
            database.__set__({
                global : {
                    runOptions :  {
                        "databaseHost":"", "databasePort":"", "databaseUser":"", "databasePassword":"", "databaseName":"",
                        getDbUrl : function() { return {}; }
                    }
                },
                logging : {
                    info: function(){},
                    error: function(msg){ console.log(msg); }}
            });

            var res = sinon.stub();
            var returnCode, returnMessage;
            res.send = function(code, arg){returnCode= code; returnMessage=arg;};
            database.getProjectName({}, res);

            assert.equal(500, returnCode);
            assert.equal("fail", returnMessage.status);
            assert.equal("unknown", returnMessage.projectName);
        });
    });

    // TODO Add new test for real connection to Dexter DB when it exists
});