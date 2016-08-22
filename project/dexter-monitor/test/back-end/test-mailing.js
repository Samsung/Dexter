var assert = require('chai').assert;
var sinon = require('sinon');
var proxyquire = require('proxyquire');
var database = require("../../util/database");
var Q = require('q');

describe('tests for mailing.js', function(){
    var mailing;
    var server;

    before(function (){
        server = proxyquire('../../routes/server', {  });
        global.config = { email: 'test@samsungtest.com' };
        var queryResult = [{
            pid: 1,
            projectName: 'Test_Project1',
            hostIP: '1.2.3.4',
            portNumber: 5001,
            emailList: ['admin1@test1.com', 'admin2@test1.com'],
            emailingWhenServerDead: 'Y',
            projectType: 'Preceding',
            groupName: 'TestGroup1',
            administrator: 'Admin - admin3@test1.com'
        }];
        sinon.stub(database, 'exec', function (sql) {
            return Q(queryResult);
        });

        server.init(true);
        var logStub = createLogStub();

        mailing = proxyquire('../../util/mailing',
            {
                '../util/logging': logStub
            });
    });

    after(function(){
        if(server) server.stopServerChecking();
        database.exec.restore();
    });

    function createLogStub(){
        var logStub = sinon.stub();
        logStub.info = function(message){};
        logStub.warn = function(message){console.log(message);};
        logStub.error = function(message){console.log(message);};
        logStub.debug = function(message){};

        return logStub;
    }

    it('should be initiated properly', function(){
        global.config.email = {
             "type":"smtp",
             "id":"",
             "password":"",
             "port":25,
             "defaultSenderEmail":"min.ho.kim@samsung.com",
             "defaultSenderName":"MinHo Kim"
        };

        mailing.init();
        assert.isTrue(mailing.isSmtpValid());
    });

    it('should email by SMTP successfully', function(done){
        global.config.email = {
            "type":"smtp",
            "id":"",
            "password":"",
            "port":25,
            "defaultSenderEmail":"min.ho.kim@samsung.com",
            "defaultSenderName":"MinHo Kim"
        };

        mailing.init();

        var params = {
            from:"min.ho.kim@samsung.com",
            toList:["min.ho.kim@samsung.com"],
            ccList:["min.ho.kim@samsung.com"],
            bccList:["min.ho.kim@samsung.com"],
            title:"dexter-monitor test email by SMTP",
            contents:"test message body"
        };

        mailing.sendEmail(params, function(error, results){
            // TODO: mocking or can test it in home due to the firewall
            //if(error) assert.fail();

            done();
        });
    });

    it('should email by API Service successfully', function(done){
        global.config.email = {
            "type":"api",
            "apiUrl":"http://email-server:5003/api/v1/email",
            "defaultSenderEmail":"min.ho.kim@samsung.com",
            "defaultSenderName":"MinHo Kim"
        };

        mailing.init();

        var params = {
            toList:["min.ho.kim@samsung.com"],
            ccList:["min.ho.kim@samsung.com"],
            bccList:["min.ho.kim@samsung.com"],
            title:"dexter-monitor test email by API service",
            contents:"test message body"
        };

        mailing.sendEmail(params, function(error, results){
            if(error) assert.fail();
            done();
        });
    });
});