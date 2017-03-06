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
var Q = require('q');
var logging = require('../../util/logging');
var account = require('../../routes/account');
var fs = require('fs');
var path = require('path');
var analysis = require('../../routes/analysis');


describe('Test analysis.js', function() {

	var result;
	var req = {};
	var res = {send: function(param) {result = param;} };
	var defectTwoData = fs.readFileSync(
		path.resolve(__dirname, "./result_defect_count2.json"), "utf8");
	var defectOneData = fs.readFileSync(
		path.resolve(__dirname, "./result_defect_count1.json"), "utf8");

	var defectZeroData = fs.readFileSync(
		path.resolve(__dirname, "./result_defect_count0.json"), "utf8");


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
		//database.exec.restore();
		logging.error.restore();
    });

	describe('For add()', function() {
		afterEach(function() {
			database.exec.restore();
		});

		it('Should sccess in normal case', function (done) {
			sinon.stub(logging, 'error');
			sinon.stub(database, 'exec', function (sql, callback) {
				callback(null, []);
			});
			
			req = {body:{result: defectTwoData}, currentUserId:'testId'};

			analysis.add(req, res);

			assert.equal(result.status, 'ok');
			res.send.calledOnce.should.be.true;
			logging.error.called.should.be.false;
			done();
		});

		it('Should insert defects into DB when its are new', function (done) {
			sinon.stub(logging, 'error');
			sinon.stub(database, 'exec', function (sql, callback) {
				callback(null, []);
			});
			
			var spyDBInsert = database.exec.withArgs(
				sinon.match('INSERT INTO Defect').and(sinon.match('\'NEW\'')));
			req = {body:{result: defectTwoData}, currentUserId:'testId'};

			analysis.add(req, res);

			assert.equal(spyDBInsert.callCount, 2);
			logging.error.called.should.be.false;
			done();
		});

		it('Should update defects into DB when its are existing', function (done) {
			sinon.stub(logging, 'error');
			sinon.stub(database, 'exec', function (sql, callback) {
				var resultQuery = [];
				if (isSelectDefectQuery(sql))
					resultQuery = [{did:'1'}];
						
				callback(null, resultQuery);
			});
			
			var spyDBInsert = database.exec.withArgs(sinon.match('INSERT INTO Defect'));
			var spyDBUpdate = database.exec.withArgs(sinon.match('UPDATE Defect SET'));
			
			req = {body:{result: defectTwoData}, currentUserId:'testId'};

			analysis.add(req, res);

			assert(spyDBUpdate.callCount > 0);
			assert.equal(spyDBInsert.callCount, 0);
			logging.error.called.should.be.false;
			done();
		});

		it('Should change \'NEW\' to \'FIX\' status when defectCount is zero', function (done) {
			sinon.stub(logging, 'error');
			sinon.stub(database, 'exec', function (sql, callback) {
				var resultQuery = [];
				if (isSelectDefectQuery(sql))
					resultQuery = [{did:'1'},{did:'2'}];
						
				callback(null, resultQuery);
			});
			
			var spyDBUpdate = database.exec.withArgs(
				sinon.match('UPDATE Defect SET statusCode = \'FIX\''));
			
			req = {body:{result: defectZeroData}, currentUserId:'testId'};

			analysis.add(req, res);

			assert.equal(spyDBUpdate.callCount, 2);
			logging.error.called.should.be.false;
			done();
		});

		it('Should change \'NEW\' to \'FIX\' status when defectCount decreased', function (done) {
			sinon.stub(logging, 'error');
			sinon.stub(database, 'exec', function (sql, callback) {
				var resultQuery = [];
				if (isSelectDefectQuery(sql))
					resultQuery = [{did:'1'}];
						
				callback(null, resultQuery);
			});
			
			var spyDBUpdate = database.exec.withArgs(
				sinon.match('UPDATE Defect SET statusCode = \'FIX\''));
			
			req = {body:{result: defectOneData}, currentUserId:'testId'};

			analysis.add(req, res);

			assert.equal(spyDBUpdate.callCount, 1);
			logging.error.called.should.be.false;
			done();
		});


		it('Should remain \'ETC\' status although \'ETC\' defects are fixed', function (done) {
			sinon.stub(logging, 'error');
			sinon.stub(database, 'exec', function (sql, callback) {
				callback(null, []);
			});
			
			var spyDBUpdate = database.exec.withArgs(
				sinon.match('UPDATE Defect SET statusCode = \'FIX\''));
			
			req = {body:{result: defectZeroData}, currentUserId:'testId'};

			analysis.add(req, res);

			assert.equal(spyDBUpdate.callCount, 0);
			logging.error.called.should.be.false;
			done();
		});
		
		function isSelectDefectQuery(sql) {
			sql = sql.toLowerCase();
			if (sql.indexOf('select') == 0 && sql.indexOf('from defect') > 0) 
				return true;
			else
				return false;
		}
	});

	describe('For addSnapshotSourceCode()', function() {
		afterEach(function() {
			database.exec.restore();
		});
			
		it('Should sccess when snapshotId is valid', function (done) {
			sinon.stub(logging, 'error');
			sinon.stub(database, 'exec', function (sql, callback) {
				callback(null, []);
			});
			
			req = {body:{snapshotId:1, groupId:1, modulePath:'testMod',
						 fileName:'testFile', sourceCode:'testCode'}};

			analysis.addSnapshotSourceCode(req, res);

			assert.equal(result.status, 'ok');
			res.send.calledOnce.should.be.true;
			logging.error.called.should.be.false;
			done();
		});

		it('Should sccess when snapshotId is invalid', function (done) {
			sinon.stub(logging, 'error');
			sinon.stub(database, 'exec', function (sql, callback) {
				callback(null, []);
			});
			
			req = {body:{groupId:1, modulePath:'testMod',
						 fileName:'testFile', sourceCode:'testCode'}};

			analysis.addSnapshotSourceCode(req, res);

			assert.equal(result.status, 'ok');
			res.send.calledOnce.should.be.true;
			logging.error.called.should.be.false;
			done();
		});

		it('Should fail when request params are invalid', function (done) {
			sinon.stub(logging, 'error');
			sinon.stub(database, 'exec', function (sql, callback) {
				callback(null, []);
			});
			
			req = {body:{groupId:1, modulePath:'testMod',
						 fileName:'testFile'}};

			analysis.addSnapshotSourceCode(req, res);

			assert.equal(result.status, 'fail');
			res.send.calledOnce.should.be.true;
			done();
		});
	});

	describe('For getSnapshotSourceCode()', function() {
		afterEach(function() {
			database.exec.restore();
		});
		it('Should return a source code in normal case', function (done) {
			var resultCode = {sourceCode:'testResultCode'};
			sinon.stub(logging, 'error');
			sinon.stub(database, 'exec', function (sql, callback) {
				callback(null, [resultCode]);
			});
			
			req = {query:{fileName:'testFile', modulePath:'testMod',
						 changeToBase64:true}, currentUserId:'testId'};

			analysis.getSnapshotSourceCode(req, res);

			assert.equal(result, new Buffer(resultCode.sourceCode, 'base64').toString('utf8'));
			res.send.calledOnce.should.be.true;
			logging.error.called.should.be.false;
			done();
		});

		it('Should fail when request params are invalid', function (done) {
			sinon.stub(logging, 'error');
			sinon.stub(database, 'exec', function (sql, callback) {
				callback(null, []);
			});
			
			req = {query:{modulePath:'testMod',
						 changeToBase64:true}, currentUserId:'testId'};

			analysis.getSnapshotSourceCode(req, res);

			assert.equal(result.status, 'fail');
			res.send.calledOnce.should.be.true;
			done();
		});
	});

	describe('For checkSnapshotSourceCode()', function() {
		afterEach(function() {
			database.exec.restore();
		});
		it('Should check the source code in normal case', function (done) {
			sinon.stub(logging, 'error');
			sinon.stub(database, 'exec', function (sql, callback) {
				callback(null, 1);
			});
			
			req = {query:{fileName:'testFile', modulePath:'testMod',
						 changeToBase64:true}, currentUserId:'testId'};

			analysis.checkSnapshotSourceCode(req, res);

			assert.equal(result, 1);
			res.send.calledOnce.should.be.true;
			logging.error.called.should.be.false;
			done();
		});

		it('Should fail when request params are invalid', function (done) {
			sinon.stub(logging, 'error');
			sinon.stub(database, 'exec', function (sql, callback) {
				callback(null, []);
			});
			
			req = {query:{modulePath:'testMod',
						 changeToBase64:true}, currentUserId:'testId'};

			analysis.checkSnapshotSourceCode(req, res);

			assert.equal(result.status, 'fail');
			res.send.calledOnce.should.be.true;
			done();
		});
	});

	describe('For getAllFalseAlarm()', function() {
		afterEach(function() {
			database.exec.restore();
		});
		it('Should return all false alarms in normal case', function (done) {
			var falseAlarms = [{fid:1, toolName:'test'}, {fid:2, toolName:'test2'}];
			sinon.stub(logging, 'error');
			sinon.stub(database, 'exec', function (sql, callback) {
				callback(null, falseAlarms);
			});
			
			req = {};

			analysis.getAllFalseAlarm(req, res);

			assert.equal(result, falseAlarms);
			res.send.calledOnce.should.be.true;
			logging.error.called.should.be.false;
			done();
		});
	});

	describe('For getAllFalseAlarmList()', function() {
		afterEach(function() {
			database.exec.restore();
		});
		it('Should return all false alarm list in normal case', function (done) {
			var falseAlarmList = {languageList:['testLang', 'testLang2'],
								  toolNameList:['testTool', 'testTool2']};
			sinon.stub(logging, 'error');
			sinon.stub(database, 'exec', function (sql, callback) {
				callback(null, [falseAlarmList]);
			});
			
			req = {};

			analysis.getAllFalseAlarmList(req, res);

			assert.equal(result, falseAlarmList);
			res.send.calledOnce.should.be.true;
			logging.error.called.should.be.false;
			done();
		});
	});

	describe('For addFalseAlarm()', function() {
		afterEach(function() {
			database.exec.restore();
		});
		it('Should success when defect is valid', function (done) {
			sinon.stub(logging, 'error');
			sinon.stub(database, 'exec', function (sql, callback) {
				callback(null, ['test']);
			});
			
			req = {body:{defect:'{"toolName":"test", "fileName":"test"}'},
				  currentUserId:'testId'};

			analysis.addFalseAlarm(req, res);

			assert.equal(result.status, 'ok');
			res.send.calledOnce.should.be.true;
			logging.error.called.should.be.false;
			done();
		});

		it('Should success when defectFilter is valid', function (done) {
			sinon.stub(logging, 'error');
			sinon.stub(database, 'exec', function (sql, callback) {
				callback(null, ['test']);
			});
			
			req = {body:{defectFilter:'{"toolName":"test", "fileName":"test"}'},
				  currentUserId:'testId'};

			analysis.addFalseAlarm(req, res);

			assert.equal(result.status, 'ok');
			res.send.calledOnce.should.be.true;
			logging.error.called.should.be.false;
			done();
		});

		it('Should success when did is valid', function (done) {
			sinon.stub(logging, 'error');
			sinon.stub(database, 'exec', function (sql, callback) {
				callback(null, ['test']);
			});
			
			req = {body:{params:{did:1}},currentUserId:'testId'};

			analysis.addFalseAlarm(req, res);

			assert.equal(result.status, 'ok');
			res.send.calledOnce.should.be.true;
			logging.error.called.should.be.false;
			done();
		});

		it('Should fail when request params are invalid', function (done) {
			sinon.stub(logging, 'error');
			sinon.stub(database, 'exec', function (sql, callback) {
				callback(null, ['test']);
			});
			
			req = {body:'test',currentUserId:'testId'};

			analysis.addFalseAlarm(req, res);

			assert.equal(result.status, 'fail');
			res.send.calledOnce.should.be.true;
			done();
		});

	});

	describe('For removeFalseAlarm()', function() {
		afterEach(function() {
			database.exec.restore();
		});
		it('Should success when defect is valid', function (done) {
			sinon.stub(logging, 'error');
			sinon.stub(database, 'exec', function (sql, callback) {
				callback(null, ['test']);
			});
			
			req = {body:{defect:'{"toolName":"test", "fileName":"test"}'},
				  currentUserId:'testId'};

			analysis.removeFalseAlarm(req, res);

			assert.equal(result.status, 'ok');
			res.send.calledOnce.should.be.true;
			logging.error.called.should.be.false;
			done();
		});

		it('Should success when did is valid', function (done) {
			sinon.stub(logging, 'error');
			sinon.stub(database, 'exec', function (sql, callback) {
				callback(null, ['test']);
			});
			
			req = {body:{params:{did:1}},currentUserId:'testId'};

			analysis.removeFalseAlarm(req, res);

			assert.equal(result.status, 'ok');
			res.send.calledOnce.should.be.true;
			logging.error.called.should.be.false;
			done();
		});

		it('Should fail when request params are invalid', function (done) {
			sinon.stub(logging, 'error');
			sinon.stub(database, 'exec', function (sql, callback) {
				callback(null, ['test']);
			});
			
			req = {body:'test',currentUserId:'testId'};

			analysis.removeFalseAlarm(req, res);

			assert.equal(result.status, 'fail');
			res.send.calledOnce.should.be.true;
			done();
		});
	});

	describe('For getGlobalDid()', function() {
		afterEach(function() {
			database.exec.restore();
		});
		it('Should success in normal case', function (done) {
			sinon.stub(logging, 'error');
			sinon.stub(database, 'exec', function (sql, callback) {
				callback(null, [{did:1}]);
			});
			
			req = {body:{defect:"{\"toolName\":\"testTool\", \"language\":\"testLan\",\"checkerCode\":\"testCode\", \"fileName\":\"file\",\"className\":\"class\", \"methodName\":\"method\"}"},
				  currentUserId:'testId'};

			analysis.getGlobalDid(req, res);

			assert.equal(result.result, 'ok');
			assert.equal(result.globalDid, 1);
			res.send.calledOnce.should.be.true;
			logging.error.called.should.be.false;
			done();
		});

		it('Should fail when defect is invalid', function (done) {
						sinon.stub(logging, 'error');
			sinon.stub(database, 'exec', function (sql, callback) {
				callback(null, [{did:1}]);
			});
			
			req = {body:{}, currentUserId:'testId'};

			analysis.getGlobalDid(req, res);

			assert.equal(result.status, 'fail');
			res.send.calledOnce.should.be.true;
			done();
		});

		it('Should fail when DB result is none', function (done) {
						sinon.stub(logging, 'error');
			sinon.stub(database, 'exec', function (sql, callback) {
				callback(null, []);
			});

			req = {body:{defect:"{\"toolName\":\"testTool\", \"language\":\"testLan\",\"checkerCode\":\"testCode\", \"fileName\":\"file\",\"className\":\"class\", \"methodName\":\"method\"}"},
				  currentUserId:'testId'};

			analysis.getGlobalDid(req, res);

			assert.equal(result.result, 'fail');
			res.send.calledOnce.should.be.true;
			done();
		});

		it('Should fail when defect is not json raw format', function (done) {
			sinon.stub(logging, 'error');
			sinon.stub(database, 'exec', function (sql, callback) {
				callback(null, []);
			});

			req = {body:{defect:{toolName:'testTool'}},
				  currentUserId:'testId'};

			analysis.getGlobalDid(req, res);

			assert.equal(result.result, 'fail');
			res.send.calledOnce.should.be.true;
			done();
		});
	});

	describe('For deleteDefect()', function() {
		afterEach(function() {
			database.exec.restore();
		});

		// TODO check below test
		//it('Should success in normal case', function (done) {
		//	sinon.stub(logging, 'error');
		//	sinon.stub(database, 'exec', function (sql, callback) {
		//		callback(null, ['test']);
		//	});
		//
		//	req = {body:{modulePath:'testPath', fileName:'testFile'},
		//		  currentUserId:'testId'};
        //
		//	analysis.deleteDefect(req, res);
        //
		//	assert.equal(result.status, 'ok');
		//	res.send.calledOnce.should.be.true;
		//	logging.error.called.should.be.false;
		//	done();
		//});

		it('Should fail when request parameters are invalid', function (done) {
			sinon.stub(logging, 'error');
			sinon.stub(database, 'exec', function (sql, callback) {
				callback(null, ['test']);
			});
			
			req = {body:{},
				  currentUserId:'testId'};

			analysis.deleteDefect(req, res);

			assert.equal(result.status, 'fail');
			res.send.calledOnce.should.be.true;
			done();	
		});
	});

	describe('For changeDefectStatus()', function() {
		afterEach(function() {
			database.exec.restore();
		});
		it('Should success in normal case', function (done) {
			sinon.stub(logging, 'error');
			sinon.stub(database, 'exec', function (sql, callback) {
				callback(null, ['test']);
			});
			
			req = {body:{defect:"{\"toolName\":\"testTool\", \"language\":\"testLan\",\"checkerCode\":\"testCode\", \"fileName\":\"file\",\"className\":\"class\", \"methodName\":\"method\"}",
						defectStatus:'NEW'}, currentUserId:'testId'};

			analysis.changeDefectStatus(req, res);

			assert.equal(result.status, 'ok');
			res.send.calledOnce.should.be.true;
			logging.error.called.should.be.false;
			done();
		});

		it('Should fail when request parameters are invalid ', function (done) {
			sinon.stub(logging, 'error');
			sinon.stub(database, 'exec', function (sql, callback) {
				callback(null, ['test']);
			});
			
			req = {body:{}, currentUserId:'testId'};

			analysis.changeDefectStatus(req, res);

			assert.equal(result.status, 'fail');
			res.send.calledOnce.should.be.true;
			done();
		});

		it('Should fail when defect is not json raw foramt', function (done) {
			sinon.stub(logging, 'error');
			sinon.stub(database, 'exec', function (sql, callback) {
				callback(null, ['test']);
			});
			
			req = {body:{defect: {toolName:'testTool'}, 
						defectStatus:'NEW'}, currentUserId:'testId'};

			analysis.changeDefectStatus(req, res);

			assert.equal(result.status, 'fail');
			res.send.calledOnce.should.be.true;
			done();
		});
	});

	describe('For getModuleAndFileName()', function() {
		afterEach(function() {
			database.exec.restore();
		});
		it('Should success in normal case', function (done) {
			sinon.stub(logging, 'error');
			sinon.stub(database, 'exec', function (sql, callback) {
				callback(null, ['test']);
			});
			
			req = {};

			analysis.getModuleAndFileName(req, res);

			assert.equal(result, 200);
			res.send.calledOnce.should.be.true;
			logging.error.called.should.be.false;
			done();
		});
	});

	describe('For changeDefectToDismiss()', function() {
		afterEach(function() {
			database.exec.restore();
		});
		// TODO check below test
		//it('Should success in normal case', function (done) {
		//	sinon.stub(logging, 'error');
		//	var testResult = ['test'];
		//	sinon.stub(database, 'exec', function (sql, callback) {
		//		callback(null, testResult);
		//	});
		//
		//	req = {body:{params:{didList:[1,2,3]}}, currentUserId:'testId'};
        //
		//	analysis.changeDefectToDismiss(req, res);
        //
		//	assert.equal(result, testResult);
		//	res.send.calledOnce.should.be.true;
		//	logging.error.called.should.be.false;
		//	done();
		//});

		it('Should fail when request parameters are invalid', function (done) {
			sinon.stub(logging, 'error');
			var testResult = ['test'];
			sinon.stub(database, 'exec', function (sql, callback) {
				callback(null, testResult);
			});
			
			req = {body:{}, currentUserId:'testId'};

			analysis.changeDefectToDismiss(req, res);

			assert.equal(result.status, 'fail');
			res.send.calledOnce.should.be.true;
			done();
		});
	});
// TODO check below test
	//describe('For changeDefectToNew()', function() {
	//	it('Should success in normal case', function (done) {
	//		sinon.stub(logging, 'error');
	//		var testResult = ['test'];
	//		sinon.stub(database, 'exec', function (sql, callback) {
	//			callback(null, testResult);
	//		});
	//
	//		req = {body:{params:{didList:[1,2,3]}}, currentUserId:'testId'};
    //
	//		analysis.changeDefectToNew(req, res);
    //
	//		assert.equal(result, testResult);
	//		res.send.calledOnce.should.be.true;
	//		logging.error.called.should.be.false;
	//		done();
	//	});
    //
	//	it('Should fail when request parameters are invalid', function (done) {
	//		sinon.stub(logging, 'error');
	//		var testResult = ['test'];
	//		sinon.stub(database, 'exec', function (sql, callback) {
	//			callback(null, testResult);
	//		});
	//
	//		req = {body:{}, currentUserId:'testId'};
    //
	//		analysis.changeDefectToNew(req, res);
    //
	//		assert.equal(result.status, 'fail');
	//		res.send.calledOnce.should.be.true;
	//		done();
	//	});
    //
	//});
	describe('For changeDefectToFix()', function() {
		afterEach(function() {
			database.exec.restore();
		});
		//it('Should success in normal case', function (done) {
		//	sinon.stub(logging, 'error');
		//	var testResult = ['test'];
		//	sinon.stub(database, 'exec', function (sql, callback) {
		//		callback(null, testResult);
		//	});
		//
		//	req = {body:{params:{didList:[1,2,3]}}, currentUserId:'testId'};
        //
		//	analysis.changeDefectToFix(req, res);
        //
		//	assert.equal(result, testResult);
		//	res.send.calledOnce.should.be.true;
		//	logging.error.called.should.be.false;
		//	done();
		//});
		it('Should fail when request parameters are invalid', function (done) {
			sinon.stub(logging, 'error');
			var testResult = ['test'];
			sinon.stub(database, 'exec', function (sql, callback) {
				callback(null, testResult);
			});
			
			req = {body:{}, currentUserId:'testId'};

			analysis.changeDefectToFix(req, res);

			assert.equal(result.status, 'fail');
			res.send.calledOnce.should.be.true;
			done();
		});
	});
	
	describe('For getProjectDefectStatus()', function() {
		afterEach(function() {
			database.exec.restore();
		});
		it('Should success in normal case', function (done) {
			sinon.stub(logging, 'error');
			sinon.stub(database, 'exec', function (sql, callback) {
				callback(null, ['test']);
			});
			
			req = {query:{statusCode:'NEW'}};

			analysis.getProjectDefectStatus(req, res);

			assert.equal(result, 200);
			res.send.calledOnce.should.be.true;
			logging.error.called.should.be.false;
			done();
		});
	});

	describe('For getModuleDefectStatus()', function() {
		afterEach(function() {
			database.exec.restore();
		});
		it('Should success in normal case', function (done) {
			sinon.stub(logging, 'error');
			sinon.stub(database, 'exec', function (sql, callback) {
				callback(null, ['test']);
			});
			
			req = {query:{statusCode:'NEW'}};

			analysis.getModuleDefectStatus(req, res);

			assert.equal(result, 200);
			res.send.calledOnce.should.be.true;
			logging.error.called.should.be.false;
			done();
		});
	});

	describe('For getFileDefectStatus()', function() {
		afterEach(function() {
			database.exec.restore();
		});
		it('Should success in normal case', function (done) {
			sinon.stub(logging, 'error');
			sinon.stub(database, 'exec', function (sql, callback) {
				callback(null, ['test']);
			});
			
			req = {query:{statusCode:'NEW'}};

			analysis.getFileDefectStatus(req, res);

			assert.equal(result, 200);
			res.send.calledOnce.should.be.true;
			logging.error.called.should.be.false;
			done();
		});
	});

	describe('For getDefectsByModuleAndFile()', function() {
		afterEach(function() {
			database.exec.restore();
		});
		it('Should success in normal case', function (done) {
			sinon.stub(logging, 'error');
			sinon.stub(database, 'exec', function (sql, callback) {
				callback(null, ['test']);
			});
			
			req = {query:{did:1, modulePath:'testModule', fileName:'testFile', statusCode:'NEW',
						 serverityCode:'CRI', checkerCode:'test', modifierNo:1, currentPage:1,
						 pageSize:1, message:'test'}};

			analysis.getDefectsByModuleAndFile(req, res);

			assert.equal(result, 200);
			res.send.calledOnce.should.be.true;
			logging.error.called.should.be.false;
			done();
		});
	});

	describe('For getDefectCount()', function() {
		afterEach(function() {
			database.exec.restore();
		});
		it('Should success in normal case', function (done) {
			sinon.stub(logging, 'error');
			var testResult = {defectCount:1};
			sinon.stub(database, 'exec', function (sql, callback) {
				callback(null, [testResult]);
			});
			
			req = {};

			analysis.getDefectCount(req, res);

			assert.equal(result.defectCount, testResult.defectCount);
			res.send.calledOnce.should.be.true;
			logging.error.called.should.be.false;
			done();
		});
	});

	describe('For getOccurencesByDid()', function() {
		afterEach(function() {
			database.exec.restore();
		});
		it('Should success in normal case', function (done) {
			sinon.stub(logging, 'error');
			var testResult = {oid:2};
			sinon.stub(database, 'exec', function (sql, callback) {
				callback(null, [testResult]);
			});
			
			req = {params:{did:1}};

			analysis.getOccurencesByDid(req, res);

			assert.equal(result[0], testResult);
			res.send.calledOnce.should.be.true;
			logging.error.called.should.be.false;
			done();
		});
		
		it('Should fail when did is invalid', function (done) {
			sinon.stub(logging, 'error');
			var testResult = {oid:2};
			sinon.stub(database, 'exec', function (sql, callback) {
				callback(null, [testResult]);
			});
			
			req = {params:{}};

			analysis.getOccurencesByDid(req, res);

			assert.equal(result.status, 'fail');
			res.send.calledOnce.should.be.true;
			done();
		});
	});

	describe('For getOccurencesByFileName()', function() {
		afterEach(function() {
			database.exec.restore();
		});
		it('Should success in normal case', function (done) {
			sinon.stub(logging, 'error');
			var testResult = {oid:2};
			sinon.stub(database, 'exec', function (sql, callback) {
				callback(null, [testResult]);
			});

			req = {query:{fileName:'testFile', modulePath:'testModule', changeToBase64:true}};			
			analysis.getOccurencesByFileName(req, res);

			assert.equal(result[0], testResult);
			res.send.calledOnce.should.be.true;
			logging.error.called.should.be.false;
			done();
		});

		it('Should success when only modulePath is invalid', function (done) {
			sinon.stub(logging, 'error');
			var testResult = {oid:2};
			sinon.stub(database, 'exec', function (sql, callback) {
				callback(null, [testResult]);
			});

			req = {query:{fileName:'testFile', changeToBase64:true}};			
			analysis.getOccurencesByFileName(req, res);

			assert.equal(result[0], testResult);
			res.send.calledOnce.should.be.true;
			logging.error.called.should.be.false;
			done();
		});

		it('Should fail when request queries are invalid', function (done) {
			sinon.stub(logging, 'error');
			var testResult = {oid:2};
			sinon.stub(database, 'exec', function (sql, callback) {
				callback(null, [testResult]);
			});
			
			req = {query:{}};

			analysis.getOccurencesByFileName(req, res);

			assert.equal(result.status, 'fail');
			res.send.calledOnce.should.be.true;
			done();
		});
	});

	describe('For getAllSnapshot()', function() {
		afterEach(function() {
			database.exec.restore();
		});
		it('Should success in normal case', function (done) {
			sinon.stub(logging, 'error');
			var testResult = {id:'testId'};
			sinon.stub(database, 'exec', function (sql, callback) {
				callback(null, [testResult]);
			});
			
			req = {};

			analysis.getAllSnapshot(req, res);

			assert.equal(result.status, 'ok');
			assert.equal(result.snapshotInfo[0], testResult);
			res.send.calledOnce.should.be.true;
			logging.error.called.should.be.false;
			done();
		});
	});

	describe('For getDefectListInSnapshot()', function() {
		afterEach(function() {
			database.exec.restore();
		});
		it('Should success in normal case', function (done) {
			sinon.stub(logging, 'error');
			var testResult = {did:'testDid'};
			sinon.stub(database, 'exec', function (sql, callback) {
				callback(null, [testResult]);
			});
			
			req = {query:{snapshotId:1}};

			analysis.getDefectListInSnapshot(req, res);

			assert.equal(result.status, 'ok');
			assert.equal(result.defectInSnapshot[0], testResult);
			res.send.calledOnce.should.be.true;
			logging.error.called.should.be.false;
			done();
		});

		it('Should fail when snapshotId is invalid', function (done) {
			sinon.stub(logging, 'error');
			var testResult = {did:'testDid'};
			sinon.stub(database, 'exec', function (sql, callback) {
				callback(null, [testResult]);
			});
			
			req = {query:{}};

			analysis.getDefectListInSnapshot(req, res);

			assert.equal(result.status, 'fail');
			res.send.calledOnce.should.be.true;
			done();
		});
	});

	describe('For getCodeMetrics()', function() {
		afterEach(function() {
			database.exec.restore();
		});
		it('Should success in normal case', function (done) {
			sinon.stub(logging, 'error');
			var testResult = {id:'testId'};
			sinon.stub(database, 'exec', function (sql, callback) {
				callback(null, [testResult]);
			});
			
			req = {query:{modulePath:'testModule', fileName:'testFile', snapshotId:1}};

			analysis.getCodeMetrics(req, res);

			assert.equal(result[0], testResult);
			res.send.calledOnce.should.be.true;
			logging.error.called.should.be.false;
			done();
		});

		it('Should success when query parameters are invalid', function (done) {
			sinon.stub(logging, 'error');
			var testResult = {did:'testDid'};
			sinon.stub(database, 'exec', function (sql, callback) {
				callback(null, [testResult]);
			});
			
			req = {query:{}};			

			analysis.getCodeMetrics(req, res);

			assert.equal(result[0], testResult);
			res.send.calledOnce.should.be.true;
			logging.error.called.should.be.false;
			done();
		});
	});

	describe('For getCodeMetricsAndDefects()', function() {
		afterEach(function() {
			database.exec.restore();
		});
		it('Should succuss in normal case', function (done) {
			sinon.stub(logging, 'error');
			var testResult = {loc:123};
			sinon.stub(database, 'exec', function (sql, callback) {
				callback(null, [testResult]);
			});
			
			req = {};

			analysis.getCodeMetricsAndDefects(req, res);

			assert.equal(result[0], testResult);
			res.send.calledOnce.should.be.true;
			logging.error.called.should.be.false;
			done();
		});
	});

	describe('For getCodeMetricsAndDefectsLimit()', function() {
		afterEach(function() {
			database.exec.restore();
		});
		it('Should success in normal case', function (done) {
			sinon.stub(logging, 'error');
			var testResult = {sloc:123};
			sinon.stub(database, 'exec', function (sql, callback) {
				callback(null, [testResult]);
			});
			
			req = {query:{defectStatus:'NEW', modulePath:'testModule', limitSize:10}};

			analysis.getCodeMetricsAndDefectsLimit(req, res);

			assert.equal(result[0], testResult);
			res.send.calledOnce.should.be.true;
			logging.error.called.should.be.false;
			done();
		});

		it('Should success when query parameters are invalid', function (done) {
			sinon.stub(logging, 'error');
			var testResult = {sloc:123};
			sinon.stub(database, 'exec', function (sql, callback) {
				callback(null, [testResult]);
			});
			
			req = {query:{}};

			analysis.getCodeMetricsAndDefectsLimit(req, res);

			assert.equal(result[0], testResult);
			res.send.calledOnce.should.be.true;
			logging.error.called.should.be.false;
			done();
		});
	});

	describe('For getCheckerAndDefects()', function() {
		afterEach(function() {
			database.exec.restore();
		});
		it('Should succuss in normal case', function (done) {
			sinon.stub(logging, 'error');
			var testResult = {modulePath:'testModule'};
			sinon.stub(database, 'exec', function (sql, callback) {
				callback(null, [testResult]);
			});
			
			req = {};

			analysis.getCheckerAndDefects(req, res);

			assert.equal(result[0], testResult);
			res.send.calledOnce.should.be.true;
			logging.error.called.should.be.false;
			done();
		});
	});

	describe('For getDevelopers()', function() {
		afterEach(function() {
			database.exec.restore();
		});
		it('Should succuss in normal case', function (done) {
			sinon.stub(logging, 'error');
			var testResult = {modulePath:'testModule'};
			sinon.stub(database, 'exec', function (sql, callback) {
				callback(null, [testResult]);
			});
			
			req = {};

			analysis.getDevelopers(req, res);

			assert.equal(result[0], testResult);
			res.send.calledOnce.should.be.true;
			logging.error.called.should.be.false;
			done();
		});
	});

	describe('For getFalseAlarmVersion()', function() {
		afterEach(function() {
			database.exec.restore();
		});
		it('Should succuss always', function (done) {
			sinon.stub(logging, 'error');
			sinon.stub(database, 'exec', function (sql, callback) {
				callback(null, []);
			});
			
			req = {};

			analysis.getFalseAlarmVersion(req, res);

			assert.equal(result.status, 'ok');
			result.version.should.be.Number;
			res.send.calledOnce.should.be.true;
			logging.error.called.should.be.false;
			done();
		});
	});

	describe('For getDefectsByModuleAndFileV2()', function() {
		afterEach(function() {
			database.exec.restore();
		});
		it('Should success in normal case', function (done) {
			sinon.stub(logging, 'error');
			sinon.stub(database, 'exec', function (sql, callback) {
				callback(null, ['test']);
			});

			req = {query:{did:1, modulePath:'testModule', fileName:'testFile', statusCode:'NEW',
				serverityCode:'CRI', checkerCode:'test', modifierNo:1, currentPage:1,
				pageSize:1, message:'test'}};

			analysis.getDefectsByModuleAndFileV2(req, res);

			assert.equal(result, 200);
			res.send.calledOnce.should.be.true;
			logging.error.called.should.be.false;
			done();
		});
	});

	describe('For getProjectDefectStatusV2()', function() {
		afterEach(function() {
			database.execV2.restore();
		});

		it('Should success in normal case', function(done) {
			sinon.stub(logging, 'error');

			var queryResult = ['test'];
			sinon.stub(database, 'execV2', function(sql) {
				return Q.resolve(queryResult);
			});

			req = {query:{
				statusCode:'NEW'}
			};

			analysis.getProjectDefectStatusV2(req, res)
				.done(function(){
					assert.equal(result.status, 'ok');
					assert.equal(res.send.calledOnce, true);
					assert.equal(logging.error.called,false);
					done();
				});
		});
	});

	describe('For getDefectsByModuleAndFileForDid()', function (){
		afterEach(function(){
			database.exec.restore();
		});

		it('Should success in just one selected defect', function(done){
			sinon.stub(logging,'error');
			sinon.stub(database, 'exec', function (sql, callback) {
				callback(null, []);
			});

			req = {
				params : {
					did : 1444
				}
			};

			analysis.getDefectsByModuleAndFileForDid(req, res);

			const query = database.exec.args[0][0];
			const paramDid = /1444/;
			assert.equal(result.status, 'ok');
			assert.equal(paramDid.test(query), true);

			done();
		});
	});

	describe('For getDefectListInSnapshotForDid()', function (){
		afterEach(function(){
			database.exec.restore();
		});

		it('Should success in just one selected defect in Snapshot', function(done){
			sinon.stub(logging,'error');
			sinon.stub(database, 'exec', function (sql, callback) {
				callback(null, []);
			});

			var snapshotId = 15000000;
			var did = 1444;
			req = {
				params : {
					snapshotId : snapshotId,
					did : did
				}
			};

			analysis.getDefectListInSnapshotForDid(req, res);

			const query = database.exec.args[0][0];
			const paramDid = /1444/;
			const paramSnapshotId = /15000000/;
			assert.equal(result.status, 'ok');
			assert.equal((paramDid.test(query) && paramSnapshotId.test(query)), true);

			done();
		});
	});

    describe('For getDefectCountByModuleAndFileV3()', function () {
        afterEach(function () {
            database.execV2.restore();
        });

        it('Should success in normal case', function (done) {
            sinon.stub(logging, 'error');

            var queryResult = ['test'];
            sinon.stub(database, 'execV2', function (sql) {
                return Q.resolve(queryResult);
            });

            req = {};

            analysis.getDefectCountByModuleAndFileV3(req, res)
                .done(() => {
                    assert.equal(result.status, 'ok');
                    done();
                });
        });

    });

});