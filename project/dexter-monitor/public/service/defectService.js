/**
 * Copyright (c) 2015 Samsung Electronics, Inc.,
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
"use strict";

monitorApp.service('DefectService', function($http, $log, $q) {
    let minYear = 0;
    let maxYear = 0;

    this.getMinYear = function() {
        if (minYear != 0) {
            return $q.resolve(minYear);
        }

        return $http.get('/api/v2/defect/min-year')
            .then(function (res) {
                if (!isHttpResultOK(res)) {
                    $log.error('Failed to load min year');
                    return 2014;
                }

                minYear = res.data.rows[0].year;
                return minYear;
            })
            .catch(function (err) {
                $log.error(err);
                return 2014;
            });
    };

    this.getMaxYear = function() {
        if (maxYear != 0) {
            return $q.resolve(maxYear);
        }

        return $http.get('/api/v2/defect/max-year')
            .then(function (res) {
                if (!isHttpResultOK(res)) {
                    $log.error('Failed to load max year');
                    return Date().getFullYear();
                }

                maxYear = res.data.rows[0].year;
                return maxYear;
            })
            .catch(function (err) {
                $log.error(err);
                return Date().getFullYear();
            });
    };

    this.getMinWeek = function(year) {
        return $http.get('/api/v2/defect/min-week/' + year)
            .then(function (res) {
                if (!isHttpResultOK(res)) {
                    $log.error('Failed to load min week');
                    return 1;
                }

                return res.data.rows[0].week;
            })
            .catch(function (err) {
                $log.error(err);
                return 1;
            });
    };

    this.getMaxWeek = function(year) {
        return $http.get('/api/v2/defect/max-week/' + year)
            .then(function (res) {
                if (!isHttpResultOK(res)) {
                    $log.error('Failed to load max week');
                    return 53;
                }

                return res.data.rows[0].week;
            })
            .catch(function (err) {
                $log.error(err);
                return 53;
            });
    }
});
