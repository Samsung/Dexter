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

    var $rootScope, $httpBackend, UserService;

    beforeEach(inject(function( _$rootScope_, _$httpBackend_, _UserService_) {
        $rootScope = _$rootScope_;
        $httpBackend = _$httpBackend_;
        UserService = _UserService_;
    }));

    describe('getExtraInfoByUserId()', function() {

        it('should reject if userId is null', function() {
            UserService.getExtraInfoByUserId(null)
                .then(function(rows) {
                    assert.ok(false);
                })
                .catch(function(err) {
                    assert.ok(true);
                });
            $rootScope.$apply();
        });

        it('should reject if the length of userId is 0', function() {
            UserService.getExtraInfoByUserId('')
                .then(function(rows) {
                    assert.ok(false);
                })
                .catch(function(err) {
                    assert.ok(true);
                });
            $rootScope.$apply();
        });
    });
});