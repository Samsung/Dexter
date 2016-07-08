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
describe('DefectCtrl Test', function() {

    beforeEach(module('dexterMonitorApp'));

    var $controller, $httpBackend, defect;

    beforeEach(inject(function(_$controller_, _$httpBackend_) {
        $controller = _$controller_;
        $httpBackend = _$httpBackend_;
        defect = $controller('DefectCtrl', {$scope: {}});
    }));

    describe('initialize()', function() {
        it('should set values properly', function() {
            $httpBackend.whenGET('/api/v2/defect')
                .respond({status:'ok', rows:[
                    {year:2016, week:25, groupName:'SamsungG_1', projectName:'SSP_1', language:'CPP', allDefectCount : 40,
                        allNew : 15, allFix : 33, allDis : 0, criNew : 2, criFix : 16, criDis : 20,
                        majNew : 8, majFix : 4, majDis : 5, minNew : 5, minFix : 3, minDis : 1,
                        crcNew : 0, crcFix : 3, crcDis : 14, etcNew : 0, etcFix : 0, etcDis : 7},
                    {year:2015, week:3, groupName:'SamsungG_2', projectName:'SSP_2', language:'JAVA', allDefectCount : 60,
                        allNew : 20, allFix : 30, allDis : 10, criNew : 12, criFix : 13, criDis : 30,
                        majNew : 3, majFix : 4, majDis : 5, minNew : 5, minFix : 3, minDis : 1,
                        crcNew : 0, crcFix : 3, crcDis : 14, etcNew : 0, etcFix : 0, etcDis : 7}
                ]});

            $httpBackend.flush();

            assert.equal(defect.gridOptions.data[0].year, 2016);
            assert.equal(defect.gridOptions.data[0].week, 25);
            assert.equal(defect.gridOptions.data[0].groupName, 'SamsungG_1');
            assert.equal(defect.gridOptions.data[0].projectName, 'SSP_1');
            assert.equal(defect.gridOptions.data[0].language, 'CPP');
            assert.equal(defect.gridOptions.data[0].allDefectCount, 40);
            assert.equal(defect.gridOptions.data[0].allNew, 15);
            assert.equal(defect.gridOptions.data[0].allFix, 33);
            assert.equal(defect.gridOptions.data[0].allDis, 0);
            assert.equal(defect.gridOptions.data[0].criFix, 16);
            assert.equal(defect.gridOptions.data[0].majDis, 5);
            assert.equal(defect.gridOptions.data[1].year, 2015);
            assert.equal(defect.gridOptions.data[1].week, 3);
            assert.equal(defect.gridOptions.data[1].groupName, 'SamsungG_2');
            assert.equal(defect.gridOptions.data[1].projectName, 'SSP_2');
            assert.equal(defect.gridOptions.data[1].language, 'JAVA');
            assert.equal(defect.gridOptions.data[1].criNew, 12);
            assert.equal(defect.gridOptions.data[1].majFix, 4);
            assert.equal(defect.gridOptions.data[1].minDis, 1);
            assert.equal(defect.gridOptions.data[1].crcNew, 0);
            assert.equal(defect.gridOptions.data[1].etcFix, 0);
        });
    });
});