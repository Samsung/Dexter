var expect = require('chai').expect;
var assert = require('chai').assert;
var sinon = require('sinon');
var proxyquire = require('proxyquire');

describe('tests for server.js', function(){
    var server;

    before(function (){
        server = proxyquire('../../routes/server', {  });
        server.setServerListJsonFilePath("./test/back-end/server-list.sample.json");

        server.init(true);
    });

    after(function(){
        if(server) server.stopServerChecking();
    });

    it('should have proper server list', function(done){
        var res = sinon.stub();

        res.send = function(serverList){
            assert.equal(2, serverList.length);
            assert.equal("Dexter Server", serverList[0].type);
            assert.equal("SE", serverList[0].group);
            assert.equal("Test Dexter Server for VD", serverList[0].name);
            assert.equal("http://localhost:4983/api/v2/server-status", serverList[1].heartbeat);
            assert.equal(false, serverList[1].emailingWhenServerDead);
            assert.deepEqual(['min.ho.kim@samsung.com'], serverList[1].emailList);
            assert.equal('MinHo Kim, min.ho.kim@samsung.com', serverList[1].serverAdministrator);

            done();
        };

        server.getServerList(null, res);
    });
});