var assert = require('chai').assert;
var sinon = require('sinon');
var database = require("../../util/database");
var Q = require('q');
var rewire = require('rewire');

describe('tests for server.js', function(){
    var server;

    before(function (){
        server = rewire('../../routes/server');
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
        },{
            pid: 2,
            projectName: 'Test_Project2',
            hostIP: '2.3.4.5',
            portNumber: 5002,
            emailList: ['admin1@test2.com', 'admin2@test2.com'],
            emailingWhenServerDead: 'N',
            projectType: 'Preceding',
            groupName: 'TestGroup2',
            administrator: 'Admin - admin3@test2.com'
        },{
            pid: 3,
            projectName: 'Test_Project3',
            hostIP: '3.4.5.6',
            portNumber: 5003,
            emailList: ['admin1@test3.com', 'admin2@test3.com'],
            emailingWhenServerDead: 'Y',
            projectType: 'Maintenance',
            groupName: 'TestGroup3',
            administrator: 'Admin - admin3@test3.com'
        }];
        sinon.stub(database, 'exec', function (sql) {
            return Q(queryResult);
        });
        server.init(true);
    });

    after(function(){
        if(server) server.stopServerChecking();
        database.exec.restore();
    });

    it('should have proper server list', function(){
        var serverList = server.__get__('serverList');
        assert.equal(3, serverList.length);
        assert.equal("Preceding", serverList[0].projectType);
        assert.equal("TestGroup1", serverList[0].groupName);
        assert.equal("Test_Project1", serverList[0].projectName);
        assert.equal("http://2.3.4.5:5002/api/v1/isServerAlive", serverList[1].heartbeat);
        assert.equal('N', serverList[1].emailingWhenServerDead);
        assert.deepEqual(['admin1@test3.com', 'admin2@test3.com'], serverList[2].emailList);
        assert.equal('Admin - admin3@test3.com', serverList[2].administrator);
    });
});