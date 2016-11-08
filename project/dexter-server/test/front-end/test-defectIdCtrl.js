/**
 * Copyright (c) 2014 Samsung Electronics, Inc.,
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

describe('Test defectIdCtrl', function(){

    beforeEach(module("defectApp"));

    var $controller, $scope, $rootScope;

    beforeEach(inject(function(_$rootScope_, _$controller_ ){
        $rootScope = _$rootScope_;
        $scope = $rootScope.$new();
        $controller = _$controller_;
    }));

    describe('For checkSnapshotView()', function(){
        it('Should fail when Default Page', function(done){
            $controller('DefectIdCtrl', {
                $scope: $scope,
                $routeParams : {
                }
            });
            $scope.checkSnapshotView();
            assert.equal($scope.isSnapshotView, false);
            done();
        });

        it('Should success when Snapshot Page', function(done){
            $controller('DefectIdCtrl', {
                $scope: $scope,
                $routeParams : {
                    snapshotId: 150000
                }
            });
            $scope.checkSnapshotView();
            assert.equal($scope.isSnapshotView, true);
            done();
        });
    });

    describe('For checkDefectIdView()', function(){
        it('Should fail when Default Page', function(done){
            $controller('DefectIdCtrl', {
                $scope: $scope,
                $routeParams : {
                }
            });
            $scope.checkDefectIdView();
            assert.equal($scope.isDefectIdView, false);
            done();
        });

        it('Should success when Snapshot Page', function(done){
            $controller('DefectIdCtrl', {
                $scope: $scope,
                $routeParams : {
                    defectId: 144
                }
            });
            $scope.checkDefectIdView();
            assert.equal($scope.isDefectIdView, true);
            done();
        });
    })

});