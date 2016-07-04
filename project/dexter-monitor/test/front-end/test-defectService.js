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


describe('DefectService Test', function() {

    beforeEach(module('dexterMonitorApp'));

    var $httpBackend, DefectService;

    beforeEach(inject(function(_$httpBackend_, _DefectService_) {
        $httpBackend = _$httpBackend_;
        DefectService = _DefectService_;
    }));

    describe('getMinYear()', function() {
        var MIN_YEAR_SUCCESS_CASE = 2012;
        var MIN_YEAR_FAILURE_CASE = 2014;

        it('should return the min year sent from server', function() {
            $httpBackend
                .whenGET('/api/v2/defect/min-year')
                .respond({status:'ok', value:MIN_YEAR_SUCCESS_CASE});

            DefectService.getMinYear()
                .then(function(year) {
                    assert.equal(year, MIN_YEAR_SUCCESS_CASE);
                });
            $httpBackend.flush();
        });

        it('should return 2014 if server responds with \'fail\' status', function() {
            $httpBackend
                .whenGET('/api/v2/defect/min-year')
                .respond({status:'fail', errorMessage:''});

            DefectService.getMinYear()
                .then(function(year) {
                    assert.equal(year, MIN_YEAR_FAILURE_CASE);
                });
            $httpBackend.flush();
        });
    });

    describe('getMaxYear()', function() {
        var MAX_YEAR_SUCCESS_CASE = 2015;
        var MAX_YEAR_FAILURE_CASE = new Date().getFullYear();

        it('should return the max year sent from server', function() {
            $httpBackend
                .whenGET('/api/v2/defect/max-year')
                .respond({status:'ok', value:MAX_YEAR_SUCCESS_CASE});

            DefectService.getMaxYear()
                .then(function(year) {
                    assert.equal(year, MAX_YEAR_SUCCESS_CASE);
                });
            $httpBackend.flush();
        });

        it('should return the current year if server responds with \'fail\' status', function() {
            $httpBackend
                .whenGET('/api/v2/defect/max-year')
                .respond({status:'fail', errorMessage:''});

            DefectService.getMaxYear()
                .then(function(year) {
                    assert.equal(year, MAX_YEAR_FAILURE_CASE);
                });
            $httpBackend.flush();
        });
    });

    describe('getMinWeek()', function() {
        var MIN_WEEK_SUCCESS_CASE = 35;
        var MIN_WEEK_FAILURE_CASE = 1;

        it('should return the min week sent from server', function() {
            $httpBackend
                .whenGET('/api/v2/defect/min-week/2016')
                .respond({status:'ok', value:MIN_WEEK_SUCCESS_CASE});

            DefectService.getMinWeek(2016)
                .then(function(week) {
                    assert.equal(week, MIN_WEEK_SUCCESS_CASE);
                });
            $httpBackend.flush();
        });

        it('should return 1 if server responds with \'fail\' status', function() {
            $httpBackend
                .whenGET('/api/v2/defect/min-week/2016')
                .respond({status:'fail', errorMessage:''});

            DefectService.getMinWeek(2016)
                .then(function(week) {
                    assert.equal(week, MIN_WEEK_FAILURE_CASE);
                });
            $httpBackend.flush();
        });
    });

    describe('getMaxWeek()', function() {
        var MAX_WEEK_SUCCESS_CASE = 40;

        it('should return the max week sent from server', function() {
            $httpBackend
                .whenGET('/api/v2/defect/max-week/2016')
                .respond({status:'ok', value:MAX_WEEK_SUCCESS_CASE});

            DefectService.getMaxWeek(2016)
                .then(function(week) {
                    assert.equal(week, MAX_WEEK_SUCCESS_CASE);
                });
            $httpBackend.flush();
        });

        it('should return 53 if server responds with \'fail\' status', function() {
            $httpBackend
                .whenGET('/api/v2/defect/max-week/2009')
                .respond({status:'fail', errorMessage:''});

            DefectService.getMaxWeek(2009)
                .then(function(week) {
                    assert.equal(week, 53);
                });
            $httpBackend.flush();
        });

        it('should return 52 if server responds with \'fail\' status', function() {
            $httpBackend
                .whenGET('/api/v2/defect/max-week/2016')
                .respond({status:'fail', errorMessage:''});

            DefectService.getMaxWeek(2016)
                .then(function(week) {
                    assert.equal(week, 52);
                });
            $httpBackend.flush();
        });
    });
});
