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
describe('DefectByProjectCtrl Test', function() {

    beforeEach(module('dexterMonitorApp'));

    var $controller, $httpBackend, defect;

    beforeEach(inject(function(_$controller_, _$httpBackend_) {
        $controller = _$controller_;
        $httpBackend = _$httpBackend_;
        defect = $controller('DefectByProjectCtrl', {$scope: {}});
    }));

    describe('projectChanged()', function() {

        var PROJECT_NAME = '16_DexterMonitorProject';
        var PROJECT_TYPE = 'Maintenance';
        var PROJECT_GROUP = 'Samsung2';
        var PROJECT_LANGUAGE = 'JAVA';

        it('should set current values to that of the selected project', function() {
            $httpBackend.whenGET('/api/v2/defect/project/').respond({status:'ok', rows:[]});

            defect.projects = [{
                'projectType' : 'Preceding',
                'projectName' : 'SamsungProject',
                'groupName' : 'Samsung1',
                'language' : 'CPP'
            },{
                'projectType' : PROJECT_TYPE,
                'projectName' : PROJECT_NAME,
                'groupName' : PROJECT_GROUP,
                'language' : PROJECT_LANGUAGE
            }];

            defect.projectChanged(PROJECT_NAME);

            assert.equal(defect.curProjectName, PROJECT_NAME);
            assert.equal(defect.curProjectType, PROJECT_TYPE);
            assert.equal(defect.curProjectGroup, PROJECT_GROUP);
            assert.equal(defect.curProjectLang, PROJECT_LANGUAGE);
            assert.equal(defect.gridOptions.exporterCsvFilename, DEFECT_FILENAME_PREFIX + '-' + PROJECT_NAME + '.csv');
            assert.equal(defect.gridOptions.exporterPdfFilename, DEFECT_FILENAME_PREFIX + '-' + PROJECT_NAME + '.pdf');
        });
    });
});
