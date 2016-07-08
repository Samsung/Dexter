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
describe('AllWeeklyStatusCtrl Test', function() {

    beforeEach(module('dexterMonitorApp'));

    var $controller, $httpBackend, $rootScope, $scope;

    beforeEach(inject(function(_$controller_, _$httpBackend_, _$rootScope_) {
        $controller = _$controller_;
        $httpBackend = _$httpBackend_;
        $rootScope = _$rootScope_;
        $scope = $rootScope.$new();
        $controller('AllWeeklyStatusCtrl', {$scope: $scope});
        setHttpBackend();
    }));

    describe('initialize()', function() {
        it('should set values properly', function() {
            $httpBackend.flush();
            assert.equal($scope.gridOptions.data[0].year, 2016);
            assert.equal($scope.gridOptions.data[0].week, 26);
            assert.equal($scope.gridOptions.data[0].defectCountTotal, 50);
            assert.equal($scope.gridOptions.data[0].defectCountFixed, 17);
            assert.equal($scope.gridOptions.data[0].defectCountDismissed, 20);
            assert.equal($scope.gridOptions.data[0].userCount, 5);
            assert.equal($scope.gridOptions.data[1].year, 2016);
            assert.equal($scope.gridOptions.data[1].week, 25);
            assert.equal($scope.gridOptions.data[1].defectCountTotal, 40);
            assert.equal($scope.gridOptions.data[1].defectCountFixed, 13);
            assert.equal($scope.gridOptions.data[1].defectCountDismissed, 19);
            assert.equal($scope.gridOptions.data[1].userCount, 2);
            assert.equal($scope.gridOptions.exporterCsvFilename, WEEKLY_STATUS_FILENAME_PREFIX + '.csv');
            assert.equal($scope.gridOptions.exporterPdfFilename, WEEKLY_STATUS_FILENAME_PREFIX + '.pdf');
        });
    });

    function setHttpBackend() {
        $httpBackend
            .whenGET('/api/v2/defect-weekly-change')
            .respond({status:'ok', rows:[
                {
                    year : 2016,
                    week : 26,
                    defectCountTotal : 50,
                    defectCountFixed : 17,
                    defectCountDismissed : 20,
                    userCount : 5
                },{
                    year : 2016,
                    week : 25,
                    defectCountTotal : 40,
                    defectCountFixed : 13,
                    defectCountDismissed : 19,
                    userCount : 2
                }
            ]});
    }
});