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
describe('OverviewCtrl Test', function() {

    beforeEach(module('dexterMonitorApp'));

    var $controller, $httpBackend, $rootScope, $scope;
    var PROJECT_NAME_1 = 'SamsungProject1';
    var PROJECT_NAME_2 = 'SamsungProject2';
    var PROJECT_NAME_3 = 'SamsungProject3';
    var PROJECT_NAME_4 = 'SamsungProject4';
    var PROJECT_NAME_5 = 'SamsungProject5';

    beforeEach(inject(function(_$controller_, _$httpBackend_, _$rootScope_) {
        $controller = _$controller_;
        $httpBackend = _$httpBackend_;
        $rootScope = _$rootScope_;
        $scope = $rootScope.$new();
        $controller('OverviewCtrl', {$scope: $scope});
        setHttpBackend();
    }));

    describe('initialize()', function() {
        it('should set values properly', function() {
            $httpBackend.flush();

            assert.equal($scope.installationStatusGridOptions.data.length, 2);
            assert.equal($scope.installationStatusGridOptions.data[0].groupName, 'B-group');
            assert.equal($scope.installationStatusGridOptions.data[1].groupName, 'C-group');
            assert.equal($scope.installationStatusGridOptions.data[0].allDeveloperCount, 85);
            assert.equal($scope.installationStatusGridOptions.data[1].allDeveloperCount, 150);
            assert.equal($scope.installationStatusGridOptions.data[0].installedDeveloperCount, 80);
            assert.equal($scope.installationStatusGridOptions.data[1].installedDeveloperCount, 50);
            assert.equal($scope.installationStatusGridOptions.data[0].nonTargetDeveloperCount, 5);
            assert.equal($scope.installationStatusGridOptions.data[1].nonTargetDeveloperCount, 30);
            assert.equal($scope.installationStatusGridOptions.data[0].targetDeveloperCount, 80);
            assert.equal($scope.installationStatusGridOptions.data[1].targetDeveloperCount, 120);
            assert.equal($scope.installationStatusGridOptions.data[0].installationRate, '100.0');
            assert.equal($scope.installationStatusGridOptions.data[1].installationRate, '41.6');

            assert.equal($scope.defectStatusGridOptions.data.length, 2);
            assert.equal($scope.defectStatusGridOptions.data[0].groupName, 'B-group');
            assert.equal($scope.defectStatusGridOptions.data[1].groupName, 'C-group');
            assert.equal($scope.defectStatusGridOptions.data[0].projectCount, 1);
            assert.equal($scope.defectStatusGridOptions.data[1].projectCount, 2);
            assert.equal($scope.defectStatusGridOptions.data[0].userCount, 7);
            assert.equal($scope.defectStatusGridOptions.data[1].userCount, 11);
            assert.equal($scope.defectStatusGridOptions.data[0].defectCountTotal, 15);
            assert.equal($scope.defectStatusGridOptions.data[1].defectCountTotal, 37);
            assert.equal($scope.defectStatusGridOptions.data[0].defectCountFixed, 7);
            assert.equal($scope.defectStatusGridOptions.data[1].defectCountFixed, 14);
            assert.equal($scope.defectStatusGridOptions.data[0].defectCountDismissed, 3);
            assert.equal($scope.defectStatusGridOptions.data[1].defectCountDismissed, 10);
        });
    });

    function setHttpBackend() {
        $httpBackend
            .whenGET('/api/v2/user-status')
            .respond({status:'ok', rows: [
                {
                    groupName: 'B-group',
                    allDeveloperCount: 85,
                    installedDeveloperCount: 80,
                    nonTargetDeveloperCount: 5,
                    targetDeveloperCount: 80,
                    installationRate: '100.0'
                },
                {
                    groupName: 'C-group',
                    allDeveloperCount: 150,
                    installedDeveloperCount: 50,
                    nonTargetDeveloperCount: 30,
                    targetDeveloperCount: 120,
                    installationRate: '41.6'
                }
            ]});

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
                },
                {
                    pid : 5,
                    projectName : PROJECT_NAME_5,
                    hostIP : '2.3.4.5',
                    portNumber : 5432,
                    emailList : ['test9@mail.com','test10@mail.com'],
                    emailingWhenServerDead : 'Y',
                    projectType : 'Preceding',
                    groupName : 'C-group',
                    administrator : 'admin5',
                    active: true
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
                },{
                    projectName : PROJECT_NAME_5,
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
            .whenGET('/api/v2/user-count/' + PROJECT_NAME_5)
            .respond({status:'ok', value:5});

        $httpBackend
            .whenGET('/api/v2/defect-status-count/' + PROJECT_NAME_2)
            .respond({status:'ok', values:{defectCountTotal:15, defectCountFixed:7, defectCountDismissed:3}});
        $httpBackend
            .whenGET('/api/v2/defect-status-count/' + PROJECT_NAME_3)
            .respond({status:'ok', values:{defectCountTotal:20, defectCountFixed:5, defectCountDismissed:9}});
        $httpBackend
            .whenGET('/api/v2/defect-status-count/' + PROJECT_NAME_5)
            .respond({status:'ok', values:{defectCountTotal:17, defectCountFixed:9, defectCountDismissed:1}});
    }
});