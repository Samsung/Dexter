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
describe('DefectByGroupCtrl Test', function() {

    beforeEach(module('dexterMonitorApp'));

    var $controller, $httpBackend, $rootScope, defect;

    beforeEach(inject(function(_$controller_, _$httpBackend_, _$rootScope_) {
        $controller = _$controller_;
        $httpBackend = _$httpBackend_;
        $rootScope = _$rootScope_;
        defect = $controller('DefectByGroupCtrl', {$scope: {}});
    }));

    beforeEach(function() {
        $httpBackend.whenGET('/api/v2/defect/max-year').respond({status:'ok', value:2016});
        $httpBackend.whenGET('/api/v2/defect/min-year').respond({status:'ok', value:2014});
        $httpBackend.whenGET('/api/v2/defect/max-week/' + '2016').respond({status:'ok', value:25});
        $httpBackend.whenGET('/api/v2/defect/group/' + '2016/' + '25')
            .respond({status:'ok', rows:[
                {year:2016, week:25, groupName:'SamsungG_1', accountCount:5, projectCount:3, allDefectCount:24, allFix:10, allExc:5},
                {year:2016, week:25, groupName:'SamsungG_2', accountCount:7, projectCount:2, allDefectCount:16, allFix:2, allExc:4}
            ]});
    });

    describe('yearChanged()', function() {
        it('should set values properly', function() {
            defect.yearChanged(2016);
            $rootScope.$apply();
            $httpBackend.flush();

            assert.equal(defect.years[0], 2014);
            assert.equal(defect.years[1], 2015);
            assert.equal(defect.years[2], 2016);
            assert.equal(defect.curYear, 2016);
            assert.equal(defect.maxWeekOfCurYear, 25);
            assert.equal(defect.curWeek, 25);

            assertGridOptions(defect);
        });
    });

    describe('weekChanged()', function() {
        it('should set values properly', function() {
            defect.curYear = 2016;
            defect.curWeek = 25;

            defect.weekChanged();
            $rootScope.$apply();
            $httpBackend.flush();

            assertGridOptions(defect);
        });
    });

    function assertGridOptions(defect) {
        assert.equal(defect.gridOptions.data[0].year, 2016);
        assert.equal(defect.gridOptions.data[0].week, 25);
        assert.equal(defect.gridOptions.data[0].groupName, 'SamsungG_1');
        assert.equal(defect.gridOptions.data[0].accountCount, 5);
        assert.equal(defect.gridOptions.data[0].projectCount, 3);
        assert.equal(defect.gridOptions.data[0].allDefectCount, 24);
        assert.equal(defect.gridOptions.data[0].allFix, 10);
        assert.equal(defect.gridOptions.data[0].allExc, 5);
        assert.equal(defect.gridOptions.data[1].groupName, 'SamsungG_2');
        assert.equal(defect.gridOptions.data[1].accountCount, 7);
        assert.equal(defect.gridOptions.data[1].projectCount, 2);
        assert.equal(defect.gridOptions.data[1].allDefectCount, 16);
        assert.equal(defect.gridOptions.data[1].allFix, 2);
        assert.equal(defect.gridOptions.data[1].allExc, 4);
        assert.equal(defect.gridOptions.exporterCsvFilename, DEFECT_FILENAME_PREFIX + '-' + 2016 + '-' + 25 + '.csv');
        assert.equal(defect.gridOptions.exporterPdfFilename, DEFECT_FILENAME_PREFIX + '-' + 2016 + '-' + 25 + '.pdf');
    }
});