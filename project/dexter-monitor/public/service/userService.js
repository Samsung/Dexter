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
"use strict";

monitorApp.service('UserService', function($http, $log, $q) {

    this.getExtraInfoByUserId = function(userId) {
        if (!userId || userId.length == 0)
            return $q.reject('User ID is invalid');

        return $http.get('/api/v2/user/extra-info/' + userId)
            .then((res) => {
                if (!isHttpResultOK(res)) {
                    $log.error('Failed to load extra user info');
                    return null;
                }

                return res.data.rows[0];
            })
            .catch((err) => {
                $log.error(err);
                return null;
            });
    };

    this.getUserList = function() {
        return $http.get('/api/v2/user')
            .then((res) => {
                console.log(res);
                if (!isHttpResultOK(res)) {
                    $log.error('Failed to get user list');
                    return [];
                }

                return res.data.rows;
            })
            .catch((err) => {
                $log.error(err);
                return [];
            });
    };

    this.getUserStatus = function() {
        return $http.get('/api/v2/user-status')
            .then((res) => {
                if (!isHttpResultOK(res)) {
                    $log.error('Failed to get user status list');
                    return [];
                }

                return res.data.rows;
            })
            .catch((err) => {
                $log.error(err);
                return [];
            });
    };
});