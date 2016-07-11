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
describe('UserByGroupCtrl Test', function() {

    beforeEach(module('dexterMonitorApp'));

    var $controller, $httpBackend, user;

    beforeEach(inject(function(_$controller_, _$httpBackend_) {
        $controller = _$controller_;
        $httpBackend = _$httpBackend_;
        user = $controller('UserByGroupCtrl', {$scope: {}});
    }));

    describe('groupChanged()', function() {

        var GROUP_NAME = '16_DexterMonitorProject';
        var USER_ID_1 = 'SamsungId1';
        var USER_ID_2 = 'SamsungId2';

        beforeEach(function() {
            $httpBackend.whenGET('/api/v2/group-list').respond({status:'ok', rows:[
                {'groupName':GROUP_NAME}
            ]});
        });

        it('should set current values to that of the selected group', function() {
            $httpBackend.whenGET('/api/v2/user/group/' + GROUP_NAME).respond({status:'ok', rows:[
                {'userId':USER_ID_1},{'userId':USER_ID_2}
            ]});

            user.groupChanged(GROUP_NAME);
            $httpBackend.flush();

            assert.equal(user.curGroupName, GROUP_NAME);
            assert.equal(user.gridOptions.exporterCsvFilename, USER_FILENAME_PREFIX + '-' + GROUP_NAME + '.csv');
            assert.equal(user.gridOptions.exporterPdfFilename, USER_FILENAME_PREFIX + '-' + GROUP_NAME + '.pdf');
            assert.equal(user.gridOptions.data[0].userId, USER_ID_1);
            assert.equal(user.gridOptions.data[1].userId, USER_ID_2);
        });
    });
});