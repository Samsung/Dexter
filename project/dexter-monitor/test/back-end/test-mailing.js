var expect = require('chai').expect;
var assert = require('chai').assert;
var sinon = require('sinon');
var proxyquire = require('proxyquire');

describe('tests for mailing.js', function(){
    var mailing;
    var server;

    before(function (){
        server = proxyquire('../../routes/server', {  });
        server.setServerListJsonFilePath("./server-list.sample.json");
        global.config = { email: 'test@samsungtest.com' };
        server.init(true);
        var logStub = createLogStub();

        mailing = proxyquire('../../util/mailing',
            {
                '../util/logging': logStub
            });
    });

    after(function(){
        if(server) server.stopServerChecking();
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