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
describe('WeeklyGroupCtrl Test', function() {

    beforeEach(module('dexterMonitorApp'));

    var $controller, $httpBackend, $rootScope, $scope, $compile;

    beforeEach(inject(function(_$controller_, _$httpBackend_, _$rootScope_, _$compile_) {
        $controller = _$controller_;
        $httpBackend = _$httpBackend_;
        $rootScope = _$rootScope_;
        $scope = $rootScope.$new();
        $controller('WeeklyGroupCtrl', {$scope: $scope});
        $compile = _$compile_;
    }));

    beforeEach(function() {
        $httpBackend.whenGET('/api/v2/defect/max-year').respond({status:'ok', value:2016});
        $httpBackend.whenGET('/api/v2/defect/min-year').respond({status:'ok', value:2014});
        $httpBackend.whenGET('/api/v2/defect/max-week/' + '2016').respond({status:'ok', value:25});
        $httpBackend.whenGET('/api/v2/defect/group/' + '2016/' + '25')
            .respond({status:'ok', rows:[
                {year:2016, week:25, groupName:'SamsungG_1', userCount:5, projectCount:3, allDefectCount:24, allFix:10, allDis:5},
                {year:2016, week:25, groupName:'SamsungG_2', userCount:7, projectCount:2, allDefectCount:16, allFix:2, allDis:4}
            ]});

        const elements = $compile(
            "<canvas id='weekly-group-user'></canvas>" +
            "<canvas id='weekly-group-project'></canvas>" +
            "<canvas id='weekly-group-defect'></canvas>")($scope);
        $scope.$digest();
        document.body.appendChild(elements[0]);
        document.body.appendChild(elements[1]);
        document.body.appendChild(elements[2]);
    });

    describe('setCurrentYearAndReloadData()', function() {
        it('should set values properly', function() {
            $scope.setCurrentYearAndReloadData(2016);
            $rootScope.$apply();
            $httpBackend.flush();

            assert.equal($scope.years[0], 2014);
            assert.equal($scope.years[1], 2015);
            assert.equal($scope.years[2], 2016);
            assert.equal($scope.curYear, 2016);
            assert.equal($scope.maxWeekOfCurYear, 25);
            assert.equal($scope.curWeek, 25);

            assertGridOptions($scope);
        });
    });

    describe('setCurrentWeekAndReloadData()', function() {
        it('should set values properly', function() {
            $scope.curYear = 2016;
            $scope.curWeek = 25;

            $scope.setCurrentWeekAndReloadData();
            $rootScope.$apply();
            $httpBackend.flush();

            assertGridOptions($scope);
        });
    });

    function assertGridOptions($scope) {
        assert.equal($scope.gridOptions.data[0].year, 2016);
        assert.equal($scope.gridOptions.data[0].week, 25);
        assert.equal($scope.gridOptions.data[0].groupName, 'SamsungG_1');
        assert.equal($scope.gridOptions.data[0].userCount, 5);
        assert.equal($scope.gridOptions.data[0].projectCount, 3);
        assert.equal($scope.gridOptions.data[0].allDefectCount, 24);
        assert.equal($scope.gridOptions.data[0].allFix, 10);
        assert.equal($scope.gridOptions.data[0].allDis, 5);
        assert.equal($scope.gridOptions.data[1].groupName, 'SamsungG_2');
        assert.equal($scope.gridOptions.data[1].userCount, 7);
        assert.equal($scope.gridOptions.data[1].projectCount, 2);
        assert.equal($scope.gridOptions.data[1].allDefectCount, 16);
        assert.equal($scope.gridOptions.data[1].allFix, 2);
        assert.equal($scope.gridOptions.data[1].allDis, 4);
        assert.equal($scope.gridOptions.exporterCsvFilename, DEFECT_FILENAME_PREFIX + '-' + 2016 + '-' + 25 + '.csv');
        assert.equal($scope.gridOptions.exporterPdfFilename, DEFECT_FILENAME_PREFIX + '-' + 2016 + '-' + 25 + '.pdf');
    }
});