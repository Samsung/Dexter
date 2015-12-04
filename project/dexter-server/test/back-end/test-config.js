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
var sinon = require('sinon');
var database = require("../../util/database");
var logging = require('../../util/logging');
var account = require('../../routes/account');
var config = require('../../routes/config');

describe('Test config.js', function() {

	var result;
	var req = {};
	var res = {send: function(param) {result = param;} };

	before(function(){
		sinon.stub(account, 'getUserNo');
		sinon.stub(database, 'getDateTime', function(time) { return time;});
		sinon.stub(logging, 'debug');
		account.getUserNo.returns(true);
    });

    after(function(){
		account.getUserNo.restore();
		database.getDateTime.restore();
		logging.debug.restore();
    });

    beforeEach(function(){
		result = null;
		sinon.spy(res, 'send');
    });

    afterEach(function(){
		res.send.restore();
		database.exec.restore();
		logging.error.restore();
    });

	describe('For addDefectGroup()', function() {
		it('Should sccess in normal case', function (done) {
			sinon.stub(logging, 'error');
			sinon.stub(database, 'exec', function (sql, callback) {
				callback(null, ['test']);
			});
			
			req = {body:{groupName:'testGroup', createdDateTime:'1234', creatorNo:'1',
						parentId:1}, currentUserId:'testId'};

			config.addDefectGroup(req, res);

			assert.equal(result.status, 'ok');
			res.send.calledOnce.should.be.true;
			logging.error.called.should.be.false;
			done();
		});

		it('Should sccess when createdDateTime/createNo are invalid', function (done) {
			sinon.stub(logging, 'error');
			sinon.stub(database, 'exec', function (sql, callback) {
				callback(null, ['test']);
			});
			
			req = {body:{groupName:'testGroup', parentId:1}, currentUserId:'testId'};

			config.addDefectGroup(req, res);

			assert.equal(result.status, 'ok');
			res.send.calledOnce.should.be.true;
			logging.error.called.should.be.false;
			done();
		});

		it('Should fail when groupName is invalid', function (done) {
			sinon.stub(logging, 'error');
			sinon.stub(database, 'exec', function (sql, callback) {
				callback(null, ['test']);
			});
			
			req = {body:{createdDateTime:'1234', creatorNo:'1',
						parentId:1}, currentUserId:'testId'};

			config.addDefectGroup(req, res);

			assert.equal(result.status, 'fail');
			res.send.calledOnce.should.be.true;
			done();
		});

		it('Should fail when DB result is none', function (done) {
			sinon.stub(logging, 'error');
			sinon.stub(database, 'exec', function (sql, callback) {
				callback(null, []);
			});
			
			req = {body:{groupName:'testGroup', createdDateTime:'1234', creatorNo:'1',
						parentId:1}, currentUserId:'testId'};

			config.addDefectGroup(req, res);

			assert.equal(result.status, 'fail');
			res.send.calledOnce.should.be.true;
			done();
		});
	});

	describe('For updateDefectGroup()', function() {
		it('Should sccess in normal case', function (done) {
			sinon.stub(logging, 'error');
			sinon.stub(database, 'exec', function (sql, callback) {
				callback(null, ['test']);
			});
			
			req = {body:{groupId:1, createdDateTime:'1234', creatorNo:'1',
						parentId:1}, currentUserId:'testId'};

			config.updateDefectGroup(req, res);

			assert.equal(result.status, 'ok');
			res.send.calledOnce.should.be.true;
			logging.error.called.should.be.false;
			done();
		});

		it('Should sccess when createdDateTime/createNo are invalid', function (done) {
			sinon.stub(logging, 'error');
			sinon.stub(database, 'exec', function (sql, callback) {
				callback(null, ['test']);
			});
			
			req = {body:{groupId:1, parentId:1}, currentUserId:'testId'};

			config.updateDefectGroup(req, res);

			assert.equal(result.status, 'ok');
			res.send.calledOnce.should.be.true;
			logging.error.called.should.be.false;
			done();
		});

		it('Should fail when groupName is invalid', function (done) {
			sinon.stub(logging, 'error');
			sinon.stub(database, 'exec', function (sql, callback) {
				callback(null, ['test']);
			});
			
			req = {body:{createdDateTime:'1234', creatorNo:'1',
						parentId:1}, currentUserId:'testId'};

			config.updateDefectGroup(req, res);

			assert.equal(result.status, 'fail');
			res.send.calledOnce.should.be.true;
			done();
		});
	});

	describe('For getDefectGroup()', function() {
		it('Should sccess in normal case', function (done) {
			sinon.stub(logging, 'error');
			var testResult = {id:'testId'};
			sinon.stub(database, 'exec', function (sql, callback) {
				callback(null, [testResult]);
			});
			
			req = {params:{groupName:'testGroup'}};

			config.getDefectGroup(req, res);

			assert.equal(result.status, 'ok');
			assert.equal(result.result, JSON.stringify([testResult]));
			res.send.calledOnce.should.be.true;
			logging.error.called.should.be.false;
			done();
		});

		it('Should success when groupName is invaid', function (done) {
			sinon.stub(logging, 'error');
			var testResult = {id:'testId'};
			sinon.stub(database, 'exec', function (sql, callback) {
				callback(null, [testResult]);
			});

			req = {params:{}};			

			config.getDefectGroup(req, res);

			assert.equal(result.status, 'ok');
			assert.equal(result.result, JSON.stringify([testResult]));
			res.send.calledOnce.should.be.true;
			logging.error.called.should.be.false;
			done();
		});
	});

	describe('For getDefectGroupId()', function() {
		it('Should sccess in normal case', function (done) {
			sinon.stub(logging, 'error');
			var testResult = {id:'testId'};
			sinon.stub(database, 'exec', function (sql, callback) {
				callback(null, [testResult]);
			});
			
			req = {params:{groupName:'testGroup'}};

			config.getDefectGroupId(req, res);

			assert.equal(result.status, 'ok');
			assert.equal(result.result[0], testResult);
			res.send.calledOnce.should.be.true;
			logging.error.called.should.be.false;
			done();
		});
	});

	describe('For deleteDefectGroup()', function() {
		it('Should sccess in normal case', function (done) {
			sinon.stub(logging, 'error');
			var testResult = {id:'testId'};
			sinon.stub(database, 'exec', function (sql, callback) {
				callback(null, [testResult]);
			});
			
			req = {params:{id:'testId'}};

			config.deleteDefectGroup(req, res);

			assert.equal(result.status, 'ok');
			assert.equal(result.result[0], testResult);
			res.send.calledOnce.should.be.true;
			logging.error.called.should.be.false;
			done();
		});
	});

	describe('For getCodes()', function() {
		it('Should sccess in normal case', function (done) {
			sinon.stub(logging, 'error');
			var testResult = {id:'testId'};
			sinon.stub(database, 'exec', function (sql, callback) {
				callback(null, [testResult]);
			});
			
			req = {params:{codeKey:'testKey'}};

			config.getCodes(req, res);

			assert.equal(result.status, 'ok');
			assert.equal(result.result, JSON.stringify([testResult]));
			res.send.calledOnce.should.be.true;
			logging.error.called.should.be.false;
			done();
		});
	});
	
});