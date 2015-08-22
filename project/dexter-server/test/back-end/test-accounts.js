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
var dutil = require('../../util/dexter-util');
var database = require("../../util/database");
var account = require('../../routes/account');
var logging = require('../../util/logging');

describe('Test account.js', function() {

	var result;
	var req = {};
	var res = {send: function(param) {result = param;} };
	var resSpy; 

	var dutilStub = sinon.stub(dutil, 'getUserIdAndPwd');
	var databaseStub = sinon.stub(database, 'exec', function (sql, callback) {
		callback(null, accountsDB);
	});

    before(function(){
		sinon.stub(logging, 'error');
		sinon.stub(logging, 'info');
	});

    after(function(){
		databaseStub.restore();
		logging.error.restore();
		logging.info.restore();
    });

    beforeEach(function(){
		result = null;
		resSpy = sinon.spy(res, 'send');
    });

    afterEach(function(){
		resSpy.restore();
    });

	var nullDB = [];
	var oneAccountDB = [{'userId':'testId', 'userPwd':'testPass', 'userNo':0, 'adminYn':'N', 'createdDateTime':'1234', 'modifiedDateTime':'1234'}];
	var accountsDB = [
			{'userId':'testId1', 'userPwd':'testPass1', 'userNo':1, 'adminYn':'N', 'createdDateTime':'1234', 'modifiedDateTime':'1234'},
			{'userId':'testId2', 'userPwd':'testPass2', 'userNo':2, 'adminYn':'N', 'createdDateTime':'1234', 'modifiedDateTime':'1234'},
			{'userId':'testId3', 'userPwd':'testPass3', 'userNo':3, 'adminYn':'N', 'createdDateTime':'1234', 'modifiedDateTime':'1234'},
			{'userId':'testId', 'userPwd':'testPass', 'userNo':0, 'adminYn':'N', 'createdDateTime':'1234', 'modifiedDateTime':'1234'}];

	account.init();
	
    describe('For checking acount', function() {
	    it('userid() should sucess when userId/pass exists in accountList', function (done) {
			var loginInfo = ['testId', 'testPass'];
			dutilStub.returns(loginInfo);

			account.userId(req, res);

			assert.equal(result.result, 'ok');
			assert.equal(result.userId, loginInfo[0]);
			assert.equal(result.userPwd, loginInfo[1]);
			assert(resSpy.calledOnce);
			done();
        });

		it('userid() Should fail when userId/pass does not exists in accountList', function (done) {
			dutilStub.returns(['testId4', 'testPass4']);
			
			account.userId(req, res);

			assert.equal(result.result, 'fail');
			assert(resSpy.calledOnce);
			done();
        });

		it('checkWebLogin() should sucess when userId/pass exists in accountList', function (done) {
			var loginInfo = ['testId', 'testPass'];
			dutilStub.returns(loginInfo);
			
			account.checkWebLogin(req, res);

			assert.equal(result.result, 'ok');
			assert.equal(result.userId, loginInfo[0]);
			assert.equal(result.userPwd, loginInfo[1]);
			assert(resSpy.calledOnce);
			done();
        });

		it('checkWebLogin() Should fail when userId/pass does not exists in accountList', function (done) {
			dutilStub.returns(['testId4', 'testPass4']);
			
			account.checkWebLogin(req, res);

			assert.equal(result.result, 'fail');
			assert(resSpy.calledOnce);
			done();
        });

		it('checkLogin() should sucess when userId/pass exists in accountList', function (done) {
			var loginInfo = ['testId', 'testPass'];
			dutilStub.returns(loginInfo);
			
			account.checkLogin(req, res);

			assert.equal(result.result, 'ok');
			assert.equal(result.userId, loginInfo[0]);
			assert.equal(result.userPwd, loginInfo[1]);
			assert(resSpy.calledOnce);
			done();
        });

		it('checkLogin() Should fail when userId/pass does not exists in accountList', function (done) {
			dutilStub.returns(['testId4', 'testPass4']);
			
			account.checkLogin(req, res);

			assert.equal(result.result, 'fail');
			assert(resSpy.calledOnce);
			done();
        });

		it('checkAdminAccount() should sucess when userId/pass exists in accountList', function (done) {
			var loginInfo = ['testId', 'testPass'];
			dutilStub.returns(loginInfo);
			
			account.checkAdminAccount(req, res);

			assert.equal(result.result, 'ok');
			assert.equal(result.userId, loginInfo[0]);
			assert.equal(result.userPwd, loginInfo[1]);
			assert(resSpy.calledOnce);
			done();
        });

		it('checkAdminAccount() Should fail when userId/pass does not exists in accountList', function (done) {
			dutilStub.returns(['testId4', 'testPass4']);
			
			account.checkAdminAccount(req, res);

			assert.equal(result.result, 'fail');
			assert(resSpy.calledOnce);
			done();
        });

    });

	describe('For findAll()', function() {
		it('Should success when DB result exists', function (done) {
			account.findAll(req, res);

			assert.equal(result.result, 'ok');
			assert.equal(result.accounts, accountsDB);
			assert(resSpy.calledOnce);
			done();
		});

		it('Should fail when DB result doesn\'t exist', function (done) {
			databaseStub.restore();
			databaseStub = sinon.stub(database, 'exec', function (sql, callback) {
				callback(null, nullDB);
			});
			
			account.findAll(req, res);

			assert.equal(result.result, 'fail');
			assert(resSpy.calledOnce);
			done();
		});
	});

	describe('For findById()', function() {
		it('Should success when DB result exists', function (done) {
			databaseStub.restore();
			databaseStub = sinon.stub(database, 'exec', function (sql, callback) {
				callback(null, oneAccountDB);
			});

			req.params = {'userId':'testId'};
			account.findById(req, res);

			assert.equal(result.result, 'ok');
			assert.equal(result.userId, req.params.userId);
			assert(resSpy.calledOnce);
			done();
		});

		it('Should fail when DB result dosen\'t exists', function (done) {
			databaseStub.restore();
			databaseStub = sinon.stub(database, 'exec', function (sql, callback) {
				callback(null, nullDB);
			});

			req.params = {'userId':'testId'};
			account.findById(req, res);

			assert.equal(result.result, 'fail');
			assert(resSpy.calledOnce);
			done();
		});
	});

	describe('For hasAccount()', function() {
		it('Should success when DB result exists', function (done) {
			databaseStub.restore();
			databaseStub = sinon.stub(database, 'exec', function (sql, callback) {
				callback(null, oneAccountDB);
			});

			req.params = {'userId':'testId'};
			account.hasAccount(req, res);

			assert.equal(result.result, 'ok');
			assert.equal(result.userId, req.params.userId);
			assert(resSpy.calledOnce);
			done();
		});

		it('Should fail when DB result dosen\'t exists', function (done) {
			databaseStub.restore();
			databaseStub = sinon.stub(database, 'exec', function (sql, callback) {
				callback(null, nullDB);
			});

			req.params = {'userId':'testId'};
			account.hasAccount(req, res);

			assert.equal(result.result, 'fail');
			assert(resSpy.calledOnce);
			done();
		});
	});

	describe('For add()', function() {
		it('Should fail when userId.length > 100', function (done) {
			databaseStub.restore();
			databaseStub = sinon.stub(database, 'exec', function (sql, callback) {
				callback(null, oneAccountDB);
			});

			var longId = '01234567890123456789012345678901234567890123456789'
				+ '01234567890123456789012345678901234567890123456789012';
			req.query = {'userId':longId, 'userId2': '1234','isAdmin':'Y'};

			account.add(req, res);

			assert.equal(result.result, 'fail');
			assert(resSpy.calledOnce);
			done();
		});

		it('Should fail when passwd.length is not within 4 ~ 20', function (done) {
			databaseStub.restore();
			databaseStub = sinon.stub(database, 'exec', function (sql, callback) {
				callback(null, oneAccountDB);
			});

			var invalidPasswd = '12';
			req.query = {'userId':'testId', 'userId2':invalidPasswd, 'isAdmin':'Y'};

			account.add(req, res);
			
			assert.equal(result.result, 'fail');
			assert(resSpy.calledOnce);
			done();
		});

		it('Should success when userId/passwd are valid', function (done) {
			databaseStub.restore();
			databaseStub = sinon.stub(database, 'exec', function (sql, callback) {
				callback(null, oneAccountDB);
			});

			req.query = {'userId':'testId', 'userId2':'1234', 'isAdmin':'Y'};

			account.add(req, res);
			
			assert.equal(result.result, 'ok');
			assert(resSpy.calledOnce);
			done();
		});

		it('Should fail when DB error occurs', function (done) {
			databaseStub.restore();
			databaseStub = sinon.stub(database, 'exec', function (sql, callback) {
				callback({'code':'test'}, null);
			});

			req.query = {'userId':'testId', 'userId2':'1234', 'isAdmin':'Y'};

			account.add(req, res);
			
			assert.equal(result.result, 'fail');
			assert(resSpy.calledOnce);
			done();
		});
	});

	describe('For webAdd()', function() {
		it('Should fail when userId.length > 100', function (done) {
			databaseStub.restore();
			databaseStub = sinon.stub(database, 'exec', function (sql, callback) {
				callback(null, oneAccountDB);
			});

			var longId = '01234567890123456789012345678901234567890123456789'
				+ '01234567890123456789012345678901234567890123456789012';
			req.body = {'params':{'userId':longId, 'userId2': '1234','isAdmin':'Y'}};

			account.webAdd(req, res);

			assert.equal(result.result, 'fail');
			assert(resSpy.calledOnce);
			done();
		});

		it('Should fail when passwd.length is not within 4 ~ 20', function (done) {
			databaseStub.restore();
			databaseStub = sinon.stub(database, 'exec', function (sql, callback) {
				callback(null, oneAccountDB);
			});

			var invalidPasswd = '12';
			req.body = {'params':{'userId':'testId', 'userId2':invalidPasswd, 'isAdmin':'Y'}};

			account.webAdd(req, res);
			
			assert.equal(result.result, 'fail');
			assert(resSpy.calledOnce);
			done();
		});

		it('Should success when userId/passwd are valid', function (done) {
			databaseStub.restore();
			databaseStub = sinon.stub(database, 'exec', function (sql, callback) {
				callback(null, oneAccountDB);
			});

			req.body = {'params':{'userId':'testId', 'userId2':'1234', 'isAdmin':'Y'}};

			account.webAdd(req, res);
			
			assert.equal(result.result, 'ok');
			assert(resSpy.calledOnce);
			done();
		});

		it('Should fail when DB error occurs', function (done) {
			databaseStub.restore();
			databaseStub = sinon.stub(database, 'exec', function (sql, callback) {
				callback({'code':'test'}, null);
			});

			req.body = {'params':{'userId':'testId', 'userId2':'1234', 'isAdmin':'Y'}};

			account.webAdd(req, res);
			
			assert.equal(result.result, 'fail');
			assert(resSpy.calledOnce);
			done();
		});
	});

	describe('For update()', function() {
		it('Should fail when oldUserId.length > 100', function (done) {
			databaseStub.restore();
			databaseStub = sinon.stub(database, 'exec', function (sql, callback) {
				callback(null, oneAccountDB);
			});

			var longId = '01234567890123456789012345678901234567890123456789'
				+ '01234567890123456789012345678901234567890123456789012';
			req.params = {'userId': longId};
			req.body = {'userId':'testId', 'userId2': '1234','isAdmin':'Y'};

			account.update(req, res);

			assert.equal(result.result, 'fail');
			assert.equal(result.errorCode, -1);
			assert(resSpy.calledOnce);
			done();
		});
		
		it('Should fail when userId.length > 100', function (done) {
			databaseStub.restore();
			databaseStub = sinon.stub(database, 'exec', function (sql, callback) {
				callback(null, oneAccountDB);
			});

			var longId = '01234567890123456789012345678901234567890123456789'
				+ '01234567890123456789012345678901234567890123456789012';
			req.params = {'userId':'testId'};
			req.body = {'userId':longId, 'userId2': '1234','isAdmin':'Y'};

			account.update(req, res);

			assert.equal(result.result, 'fail');
			assert.equal(result.errorCode, -2);
			assert(resSpy.calledOnce);
			done();
		});

		it('Should fail when passwd.length is not within 4 ~ 20', function (done) {
			databaseStub.restore();
			databaseStub = sinon.stub(database, 'exec', function (sql, callback) {
				callback(null, oneAccountDB);
			});

			req.params = {'userId':'testOldId'};
			var invalidPasswd = '1234567890123456789012';			
			req.body = {'userId':'testId', 'userId2': invalidPasswd,'isAdmin':'Y'};

			account.update(req, res);
			
			assert.equal(result.result, 'fail');
			assert.equal(result.errorCode, -3);
			assert(resSpy.calledOnce);
			done();
		});

		it('Should success when userId/passwd are valid', function (done) {
			databaseStub.restore();
			databaseStub = sinon.stub(database, 'exec', function (sql, callback) {
				callback(null, oneAccountDB);
			});

			req.params = {'userId':'testOldId'};
			req.body = {'userId':'testId', 'userId2': '1234','isAdmin':'Y'};

			account.update(req, res);
			
			assert.equal(result.result, 'ok');
			assert(resSpy.calledOnce);
			done();
		});

		it('Should fail when DB error occurs', function (done) {
			databaseStub.restore();
			databaseStub = sinon.stub(database, 'exec', function (sql, callback) {
				callback({'code':'test'}, null);
			});

			req.params = {'userId':'testOldId'};
			req.body = {'userId':'testId', 'userId2': '1234','isAdmin':'Y'};

			account.update(req, res);
			
			assert.equal(result.result, 'fail');
			assert(resSpy.calledOnce);
			done();
		});
	});

	describe('For WebUpdate()', function() {
		it('Should fail when oldUserId.length > 100', function (done) {
			databaseStub.restore();
			databaseStub = sinon.stub(database, 'exec', function (sql, callback) {
				callback(null, oneAccountDB);
			});

			var longId = '01234567890123456789012345678901234567890123456789'
				+ '01234567890123456789012345678901234567890123456789012';
			req.params = {'userId': longId};
			req.body = {'params':{'userId':'testId', 'userId2': '1234','isAdmin':'Y'}};

			account.webUpdate(req, res);

			assert.equal(result.result, 'fail');
			assert.equal(result.errorCode, -1);
			assert(resSpy.calledOnce);
			done();
		});
		
		it('Should fail when userId.length > 100', function (done) {
			databaseStub.restore();
			databaseStub = sinon.stub(database, 'exec', function (sql, callback) {
				callback(null, oneAccountDB);
			});

			var longId = '01234567890123456789012345678901234567890123456789'
				+ '01234567890123456789012345678901234567890123456789012';
			req.params = {'userId':'testId'};
			req.body = {'params':{'userId':longId, 'userId2': '1234','isAdmin':'Y'}};

			account.webUpdate(req, res);

			assert.equal(result.result, 'fail');
			assert.equal(result.errorCode, -2);
			assert(resSpy.calledOnce);
			done();
		});

		it('Should fail when passwd.length is not within 4 ~ 20', function (done) {
			databaseStub.restore();
			databaseStub = sinon.stub(database, 'exec', function (sql, callback) {
				callback(null, oneAccountDB);
			});

			req.params = {'userId':'testOldId'};
			var invalidPasswd = '1234567890123456789012';			
			req.body = {'params':{'userId':'testId', 'userId2': invalidPasswd,'isAdmin':'Y'}};

			account.webUpdate(req, res);
			
			assert.equal(result.result, 'fail');
			assert.equal(result.errorCode, -3);
			done();
		});

		it('Should success when userId/passwd are valid', function (done) {
			databaseStub.restore();
			databaseStub = sinon.stub(database, 'exec', function (sql, callback) {
				callback(null, oneAccountDB);
			});

			req.params = {'userId':'testOldId'};
			req.body = {'params':{'userId':'testId', 'userId2': '1234','isAdmin':'Y'}};

			account.webUpdate(req, res);
			
			assert.equal(result.result, 'ok');
			assert(resSpy.calledOnce);
			done();
		});

		it('Should fail when DB error occurs', function (done) {
			databaseStub.restore();
			databaseStub = sinon.stub(database, 'exec', function (sql, callback) {
				callback({'code':'test'}, null);
			});

			req.params = {'userId':'testOldId'};
			req.body = {'params':{'userId':'testId', 'userId2': '1234','isAdmin':'Y'}};

			account.webUpdate(req, res);
			
			assert.equal(result.result, 'fail');
			assert(resSpy.calledOnce);
			done();
		});
	});

	describe('For remove()', function() {
		it('Should success in normal case ', function (done) {
			databaseStub.restore();
			databaseStub = sinon.stub(database, 'exec', function (sql, callback) {
				callback(null, oneAccountDB);
			});

			req.params = {userId:'testOldId'};
			
			account.remove(req, res);

			assert.equal(result.result, 'ok');
			assert(resSpy.calledOnce);
			done();
		});

		it('Should fail when DB error occurs', function (done) {
			databaseStub.restore();
			databaseStub = sinon.stub(database, 'exec', function (sql, callback) {
				callback({message:'test'}, null);
			});

			req.params = {userId:'testOldId'};
			req.params = {deletedSelectedItems:['userId', 'userId2', 'userId3']};

			account.remove(req, res);
			
			assert.equal(result.result, 'fail');
			assert(resSpy.calledOnce);
			done();
		});
	});

	describe('For removeAll()', function() {
		it('Should success in normal case ', function (done) {
			databaseStub.restore();
			databaseStub = sinon.stub(database, 'exec', function (sql, callback) {
				callback(null, oneAccountDB);
			});

			req.query = {deleteSelectedItems:['userId', 'userId2', 'userId3']};
			
			account.removeAll(req, res);

			assert.equal(result.result, 'ok');
			assert(resSpy.calledOnce);
			done();
		});
	});
});
