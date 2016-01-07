var expect = require('chai').expect;
var assert = require('chai').assert;
var sinon = require('sinon');
var proxyquire = require('proxyquire');

describe('tests for server.js', function(){
    var server;

    before(function (){
        server = proxyquire('../../routes/server', {  });
        server.setServerListJsonFilePath("./server-list.sample.json");

        server.init();
    });

    it('should have proper configuration information', function(){
        expect(config.appName).to.equal('Dexter Monitor');
        expect(config.description).to.equal('This program will monitor and control servers');
    });

    it('should have proper server list', function(done){
        var res = sinon.stub();
        res.send = function(serverList){
            assert.equal(1, serverList.length);
            assert.equal("Dexter Server", serverList[0].type);
            assert.equal("SE", serverList[0].group);
            assert.equal("Test Dexter Server for VD", serverList[0].name);
            assert.equal("http://localhost:4982/api/v1/isServerAlive", serverList[0].heartbeat);
            assert.equal("ok", serverList[0].expectedResponse);
            assert.equal(true, serverList[0].usingRunCommandWhenServerDead);
            assert.equal(true, serverList[0].emailingWhenServerDead);
            assert.deepEqual(['min.ho.kim@samsung.com'], serverList[0].emailList);
            assert.equal('MinHo Kim, min.ho.kim@samsung.com', serverList[0].serverAdministrator);

            //assert.isTrue(serverList[0].rerunLastTryTime > 0);
            //assert.equal(0, serverList[0].rerunTimes);

            done();
        };

        server.getServerList(null, res);
    });
});