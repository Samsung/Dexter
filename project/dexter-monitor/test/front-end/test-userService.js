/**
 * Copyright (c) 2016 Samsung Electronics, Inc.,
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
describe('UserService Test', function() {

    beforeEach(module('dexterMonitorApp'));

    var $rootScope, $httpBackend, DefectService;

    beforeEach(inject(function( _$rootScope_, _$httpBackend_, _UserService_) {
        $rootScope = _$rootScope_;
        $httpBackend = _$httpBackend_;
        UserService = _UserService_;
    }));

    describe('getExtraInfoByUserIdList()', function() {

        it('should reject if userIdList is null', function() {
            UserService.getExtraInfoByUserIdList(null)
                .then(function(rows) {
                    assert.ok(false);
                })
                .catch(function(err) {
                    assert.ok(true);
                });
            $rootScope.$apply();
        });

        it('should reject if userIdList is empty', function() {
            UserService.getExtraInfoByUserIdList([])
                .then(function(rows) {
                    assert.ok(false);
                })
                .catch(function(err) {
                    assert.ok(true);
                });
            $rootScope.$apply();
        });

        it('should return the rows containing extra user information sent from server', function(done) {
            $httpBackend
                .whenGET('/api/v2/user/extra-info/' + 'Samsung1,Samsung2,Samsung3,Samsung4')
                .respond({status:'ok',
                    rows:[{userId:'Samsung1', department:'VD1', title: 'engineer1', employeeNumber: 1234},
                          {userId:'Samsung2', department:'VD2', title: 'engineer2', employeeNumber: 5678},
                          {userId:'Samsung3', department:'VD3', title: 'engineer3', employeeNumber: 9101},
                          {userId:'Samsung4', department:'VD4', title: 'engineer4', employeeNumber: 1121}]
                });

            UserService.getExtraInfoByUserIdList(['Samsung1', 'Samsung2', 'Samsung3', 'Samsung4'])
                .then(function(rows) {
                    assert.equal(rows[0].userId, 'Samsung1');
                    assert.equal(rows[1].department, 'VD2');
                    assert.equal(rows[2].title, 'engineer3');
                    assert.equal(rows[3].employeeNumber, 1121);
                    done();
                });
            $httpBackend.flush();
        });
    });
});