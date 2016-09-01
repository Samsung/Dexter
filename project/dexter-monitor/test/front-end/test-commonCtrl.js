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
describe('CommonCtrl Test', function() {

    beforeEach(module('dexterMonitorApp'));

    var $controller, $rootScope, $scope, $location;

    beforeEach(inject(function(_$controller_, _$rootScope_, _$location_) {
        $controller = _$controller_;
        $rootScope = _$rootScope_;
        $scope = $rootScope.$new();
        $controller('CommonCtrl', {$scope: $scope});
        $location = _$location_;
    }));

    describe('isActiveView()', function() {
        it('should return true if the passing parameter is equal to the current location' , function() {
            $location.path('/user/project/');
            assert.equal($scope.isActiveView('/user/project/'), true);
        });
    });

    describe('isActiveView()', function() {
        it('should return false if the passing parameter is NOT equal to the current location', function() {
            $location.path('/user/group/');
            assert.equal($scope.isActiveView('/user/project/'), false);
        });
    });

    describe('isActiveViewWithParam()', function() {
        it('should return true if the current location starts with the passing parameter', function() {
            $location.path('/user/group/SE');
            assert.equal($scope.isActiveViewWithParam('/user/group/'), true);
        });
    });

    describe('isActiveViewWithParam()', function() {
        it('should return false if the current location does NOT starts with the passing parameter', function() {
            $location.path('/user/group/SE');
            assert.equal($scope.isActiveViewWithParam('/user/project/'), false);
        });
    });
});