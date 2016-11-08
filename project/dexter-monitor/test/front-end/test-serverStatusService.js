/**
 * Copyright (c) 2015 Samsung Electronics, Inc.,
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
describe('ServerStatusService Test', function(){
    var ServerStatusService;
    var httpBackend;

    beforeEach(module('dexterMonitorApp')); // should be App Name

    beforeEach(function() {
        inject(function($injector, $httpBackend) {
            $httpBackend
                .when('GET', '/api/v1/server')
                .respond([{'active':true}, {'active':false}, {'name':'abc'}]);

            $httpBackend
                .when('GET', '/api/v1/server/last-modified-time')
                .respond({data: {serverListLastModifiedTime: new Date()}});

            httpBackend = $httpBackend;

            ServerStatusService = $injector.get('ServerStatusService');     // should be Service Name
        });
    });

    it('should return initialzed server list correctly', function(done){
        ServerStatusService.loadServerList(function(error){
            if(error){
                console.log(error);
                assert.fail();
            }

            var servers = ServerStatusService.getInactiveServers();
            assert.equal(2, ServerStatusService.getInactiveServerCount());
            assert.equal(false, servers[0].active);
            assert.equal('abc', servers[1].name);

            servers = ServerStatusService.getActiveServers();
            assert.equal(1, ServerStatusService.getActiveServerCount());
            assert.equal(true, servers[0].active);
            done();
        });

        httpBackend.flush();
    });

    it('should call callback function without an error when called IsServerStatusChanged()', function(done){
        ServerStatusService.IsServerStatusChanged(function(error){
            if(error){
                console.log(error);
                assert.fail();
            }

            done();
        });

        httpBackend.flush();
    });
});