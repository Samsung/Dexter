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
describe('AllCurrentStatusCtrl Test', function() {

    beforeEach(module('dexterMonitorApp'));

    var $controller, $httpBackend, $rootScope, $scope;
    var PROJECT_NAME_1 = 'SamsungProject1';
    var PROJECT_NAME_2 = 'SamsungProject2';
    var PROJECT_NAME_3 = 'SamsungProject3';
    var PROJECT_NAME_4 = 'SamsungProject4';

    beforeEach(inject(function(_$controller_, _$httpBackend_, _$rootScope_) {
        $controller = _$controller_;
        $httpBackend = _$httpBackend_;
        $rootScope = _$rootScope_;
        $scope = $rootScope.$new();
        $controller('AllCurrentStatusCtrl', {$scope: $scope});
        setHttpBackend();
    }));

    describe('initialize()', function() {
        it('should set values properly', function() {
            $httpBackend.flush();
            assert.equal($scope.summaryGridOptions.data[0].allGroupCount, 3);
            assert.equal($scope.summaryGridOptions.data[0].allProjectCount, 4);
            assert.equal($scope.summaryGridOptions.data[0].allDefectCount, 10 + 15 + 20 + 17);
            assert.equal($scope.summaryGridOptions.data[0].allAccountCount, 5 + 7 + 6 + 3);
            assert.equal($scope.detailGridOptions.data[0].projectName, PROJECT_NAME_1);
            assert.equal($scope.detailGridOptions.data[1].accountCount, 7);
            assert.equal($scope.detailGridOptions.data[2].defectCountTotal, 20);
            assert.equal($scope.detailGridOptions.data[3].defectCountFixed, 3);
            assert.equal($scope.detailGridOptions.data[3].defectCountExcluded, 7);
            assert.equal($scope.detailGridOptions.exporterCsvFilename, CURRENT_STATUS_FILENAME_PREFIX + '-' + $scope.time + '.csv');
            assert.equal($scope.detailGridOptions.exporterPdfFilename, CURRENT_STATUS_FILENAME_PREFIX + '-' + $scope.time + '.pdf');
        });
    });

    function setHttpBackend() {
        $httpBackend
            .whenGET('/api/v2/project-list')
            .respond({status:'ok', rows:[
                {
                    projectName : PROJECT_NAME_2,
                    projectType : 'Preceding',
                    groupName : 'B-group',
                    language : 'CPP',
                    dbName : 'Project2_Database'
                },{
                    projectName : PROJECT_NAME_1,
                    projectType : 'Maintenance',
                    groupName : 'A-group',
                    language : 'JAVA',
                    dbName : 'Project1_Database'
                },{
                    projectName : PROJECT_NAME_3,
                    projectType : 'Preceding',
                    groupName : 'C-group',
                    language : 'CPP',
                    dbName : 'Project3_Database'
                },{
                    projectName : PROJECT_NAME_4,
                    projectType : 'Preceding',
                    groupName : 'C-group',
                    language : 'JAVA',
                    dbName : 'Project4_Database'
                }
            ]});

        $httpBackend
            .whenGET('/api/v2/user-count/' + PROJECT_NAME_1)
            .respond({status:'ok', value:5});
        $httpBackend
            .whenGET('/api/v2/user-count/' + PROJECT_NAME_2)
            .respond({status:'ok', value:7});
        $httpBackend
            .whenGET('/api/v2/user-count/' + PROJECT_NAME_3)
            .respond({status:'ok', value:6});
        $httpBackend
            .whenGET('/api/v2/user-count/' + PROJECT_NAME_4)
            .respond({status:'ok', value:3});

        $httpBackend
            .whenGET('/api/v2/defect-status-count/' + PROJECT_NAME_1)
            .respond({status:'ok', values:{defectCountTotal:10, defectCountFixed:1, defectCountExcluded:2}});
        $httpBackend
            .whenGET('/api/v2/defect-status-count/' + PROJECT_NAME_2)
            .respond({status:'ok', values:{defectCountTotal:15, defectCountFixed:7, defectCountExcluded:3}});
        $httpBackend
            .whenGET('/api/v2/defect-status-count/' + PROJECT_NAME_3)
            .respond({status:'ok', values:{defectCountTotal:20, defectCountFixed:5, defectCountExcluded:9}});
        $httpBackend
            .whenGET('/api/v2/defect-status-count/' + PROJECT_NAME_4)
            .respond({status:'ok', values:{defectCountTotal:17, defectCountFixed:3, defectCountExcluded:7}});
    }
});