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
describe('ProjectService Test', function() {

    beforeEach(module('dexterMonitorApp'));

    var $rootScope, $httpBackend, ProjectService;

    beforeEach(inject(function( _$rootScope_, _$httpBackend_, _ProjectService_) {
        $rootScope = _$rootScope_;
        $httpBackend = _$httpBackend_;
        ProjectService = _ProjectService_;
    }));

    describe('getCurrentDetailList()', function() {

        it('should return the rows containing the current defect and account status sent from server', function(done) {
            $httpBackend
                .whenGET('/api/v2/project-list')
                .respond({status:'ok', rows:[
                    {
                        projectName : 'SamsungProject2',
                        projectType : 'Preceding',
                        groupName : 'B-group',
                        language : 'CPP',
                        dbName : 'Project2_Database'
                    },{
                        projectName : 'SamsungProject1',
                        projectType : 'Maintenance',
                        groupName : 'A-group',
                        language : 'JAVA',
                        dbName : 'Project1_Database'
                    },{
                        projectName : 'SamsungProject3',
                        projectType : 'Preceding',
                        groupName : 'C-group',
                        language : 'CPP',
                        dbName : 'Project3_Database'
                    }
                ]});

            $httpBackend
                .whenGET('/api/v2/user-count/' + 'Project1_Database')
                .respond({status:'ok', value:5});
            $httpBackend
                .whenGET('/api/v2/user-count/' + 'Project2_Database')
                .respond({status:'ok', value:7});
            $httpBackend
                .whenGET('/api/v2/user-count/' + 'Project3_Database')
                .respond({status:'ok', value:6});
            $httpBackend
                .whenGET('/api/v2/defect-status-count/' + 'Project1_Database')
                .respond({status:'ok', values:{defectCountTotal:10, defectCountFixed:1, defectCountExcluded:2}});
            $httpBackend
                .whenGET('/api/v2/defect-status-count/' + 'Project2_Database')
                .respond({status:'ok', values:{defectCountTotal:15, defectCountFixed:7, defectCountExcluded:3}});
            $httpBackend
                .whenGET('/api/v2/defect-status-count/' + 'Project3_Database')
                .respond({status:'ok', values:{defectCountTotal:20, defectCountFixed:5, defectCountExcluded:9}});

            ProjectService.getCurrentDetailList()
                .then(function(rows) {
                    assert.equal(rows[0].projectName, 'SamsungProject1');
                    assert.equal(rows[0].accountCount, 5);
                    assert.equal(rows[0].defectCountTotal, 10);
                    assert.equal(rows[0].defectCountFixed, 1);
                    assert.equal(rows[0].defectCountExcluded, 2);
                    assert.equal(rows[1].projectName, 'SamsungProject2');
                    assert.equal(rows[1].accountCount, 7);
                    assert.equal(rows[1].defectCountTotal, 15);
                    assert.equal(rows[1].defectCountFixed, 7);
                    assert.equal(rows[1].defectCountExcluded, 3);
                    assert.equal(rows[2].projectName, 'SamsungProject3');
                    assert.equal(rows[2].accountCount, 6);
                    assert.equal(rows[2].defectCountTotal, 20);
                    assert.equal(rows[2].defectCountFixed, 5);
                    assert.equal(rows[2].defectCountExcluded, 9);
                    done();
                });
            $httpBackend.flush();
        });
    });
});