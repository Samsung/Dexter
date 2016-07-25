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
describe('CurrentProjectCtrl Test', function() {

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
        $controller('CurrentProjectCtrl', {$scope: $scope});
        setHttpBackend();
    }));

    describe('initialize()', function() {
        it('should set values properly', function() {
            $httpBackend.flush();
            assert.equal($scope.gridOptions.data.length, 4);
            assert.equal($scope.gridOptions.data[0].projectName, PROJECT_NAME_1);
            assert.equal($scope.gridOptions.data[1].projectName, PROJECT_NAME_2);
            assert.equal($scope.gridOptions.data[2].projectName, PROJECT_NAME_3);
            assert.equal($scope.gridOptions.data[3].projectName, PROJECT_NAME_4);
            assert.equal($scope.gridOptions.data[0].groupName, 'A-group');
            assert.equal($scope.gridOptions.data[1].groupName, 'B-group');
            assert.equal($scope.gridOptions.data[2].groupName, 'C-group');
            assert.equal($scope.gridOptions.data[3].groupName, 'C-group');
            assert.equal($scope.gridOptions.data[0].userCount, undefined);
            assert.equal($scope.gridOptions.data[1].userCount, 7);
            assert.equal($scope.gridOptions.data[2].userCount, 6);
            assert.equal($scope.gridOptions.data[3].userCount, undefined);
            assert.equal($scope.gridOptions.data[0].defectCountTotal, undefined);
            assert.equal($scope.gridOptions.data[1].defectCountTotal, 15);
            assert.equal($scope.gridOptions.data[2].defectCountTotal, 20);
            assert.equal($scope.gridOptions.data[3].defectCountTotal, undefined);
            assert.equal($scope.gridOptions.data[0].defectCountFixed, undefined);
            assert.equal($scope.gridOptions.data[1].defectCountFixed, 7);
            assert.equal($scope.gridOptions.data[2].defectCountFixed, 5);
            assert.equal($scope.gridOptions.data[3].defectCountFixed, undefined);
            assert.equal($scope.gridOptions.data[0].defectCountDismissed, undefined);
            assert.equal($scope.gridOptions.data[1].defectCountDismissed, 3);
            assert.equal($scope.gridOptions.data[2].defectCountDismissed, 9);
            assert.equal($scope.gridOptions.data[3].defectCountDismissed, undefined);
            assert.equal($scope.getResolvedRate($scope.gridOptions.data[0]), '');
            assert.equal($scope.getResolvedRate($scope.gridOptions.data[1]), ((7+3)/15*100).toFixed(1) + '%');
            assert.equal($scope.getResolvedRate($scope.gridOptions.data[2]), ((5+9)/20*100).toFixed(1) + '%');
            assert.equal($scope.getResolvedRate($scope.gridOptions.data[3]), '');
            assert.equal($scope.gridOptions.data[0].serverStatus, 'Inactive');
            assert.equal($scope.gridOptions.data[1].serverStatus, 'Active');
            assert.equal($scope.gridOptions.data[2].serverStatus, 'Active');
            assert.equal($scope.gridOptions.data[3].serverStatus, 'Inactive');
            assert.equal($scope.gridOptions.exporterCsvFilename, CURRENT_STATUS_FILENAME_PREFIX + '-' + $scope.time + '.csv');
            assert.equal($scope.gridOptions.exporterPdfFilename, CURRENT_STATUS_FILENAME_PREFIX + '-' + $scope.time + '.pdf');
        });
    });

    function setHttpBackend() {
        $httpBackend
            .whenGET('/api/v1/server')
            .respond(
                [{
                    pid : 1,
                    projectName : PROJECT_NAME_1,
                    hostIP : '3.4.5.6',
                    portNumber : 7890,
                    emailList : ['test4@mail.com','test5@mail.com'],
                    emailingWhenServerDead : 'Y',
                    projectType : 'Maintenance',
                    groupName : 'A-group',
                    administrator : 'admin3',
                    active: false
                },
                {
                    pid : 2,
                    projectName : PROJECT_NAME_2,
                    hostIP : '1.2.3.4',
                    portNumber : 5678,
                    emailList : ['test1@mail.com','test2@mail.com'],
                    emailingWhenServerDead : 'Y',
                    projectType : 'Preceding',
                    groupName : 'B-group',
                    administrator : 'admin1',
                    active: true
                },
                {
                    pid : 3,
                    projectName : PROJECT_NAME_3,
                    hostIP : '5.6.7.8',
                    portNumber : 1234,
                    emailList : ['test3@mail.com','test4@mail.com'],
                    emailingWhenServerDead : 'N',
                    projectType : 'Preceding',
                    groupName : 'C-group',
                    administrator : 'admin2',
                    active: true
                },
                {
                    pid : 4,
                    projectName : PROJECT_NAME_4,
                    hostIP : '8.7.6.5',
                    portNumber : 4321,
                    emailList : ['test7@mail.com','test8@mail.com'],
                    emailingWhenServerDead : 'N',
                    projectType : 'Preceding',
                    groupName : 'C-group',
                    administrator : 'admin4',
                    active: false
                }]
            );

        $httpBackend
            .whenGET('/api/v2/project-list')
            .respond({status:'ok', rows:[
                {
                    projectName : PROJECT_NAME_2,
                    projectType : 'Preceding',
                    groupName : 'B-group',
                    language : 'CPP'
                },{
                    projectName : PROJECT_NAME_1,
                    projectType : 'Maintenance',
                    groupName : 'A-group',
                    language : 'JAVA'
                },{
                    projectName : PROJECT_NAME_3,
                    projectType : 'Preceding',
                    groupName : 'C-group',
                    language : 'CPP'
                },{
                    projectName : PROJECT_NAME_4,
                    projectType : 'Preceding',
                    groupName : 'C-group',
                    language : 'JAVA'
                }
            ]});

        $httpBackend
            .whenGET('/api/v2/user-count/' + PROJECT_NAME_2)
            .respond({status:'ok', value:7});
        $httpBackend
            .whenGET('/api/v2/user-count/' + PROJECT_NAME_3)
            .respond({status:'ok', value:6});

        $httpBackend
            .whenGET('/api/v2/defect-status-count/' + PROJECT_NAME_2)
            .respond({status:'ok', values:{defectCountTotal:15, defectCountFixed:7, defectCountDismissed:3}});
        $httpBackend
            .whenGET('/api/v2/defect-status-count/' + PROJECT_NAME_3)
            .respond({status:'ok', values:{defectCountTotal:20, defectCountFixed:5, defectCountDismissed:9}});
    }
});