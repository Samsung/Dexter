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
var monitorApp = angular.module("dexterMonitorApp", ['ngRoute', 'ngAnimate', 'ngTouch',
        'ui.grid', 'ui.grid.resizeColumns', 'ui.grid.moveColumns', 'ui.grid.exporter', 'ui.grid.autoResize',
        'ui.bootstrap'])
    .factory('_', ['$window', function($window){
        return $window._;
    }]);

monitorApp.config(function($routeProvider){
    $routeProvider
        .when("/", {
            controller: "AllCurrentStatusCtrl",
            templateUrl: "view/allCurrentStatusView.html"
        })
        .when("/change/", {
            controller: "AllWeeklyStatusCtrl",
            templateUrl: "view/allWeeklyStatusCtrl.html"
        })
        .when("/server-status/", {
            controller: "ServerStatusCtrl",
            templateUrl: "view/serverStatusView.html",
            controllerAs: "main"
        })
        .when("/user-status/", {
            controller: "UserStatusCtrl",
            templateUrl: "view/userStatusView.html",
        })
        .when("/user/", {
            controller: "UserCtrl",
            templateUrl: "view/userView.html",
            controllerAs: "user"
        })
        .when("/user/project/", {
            controller: "UserByProjectCtrl",
            templateUrl: "view/userByProjectView.html",
            controllerAs: "user"
        })
        .when("/user/group/", {
            controller: "UserByGroupCtrl",
            templateUrl: "view/userByGroupView.html",
            controllerAs: "user"
        })
        .when("/user/group/:groupName", {
            controller: "UserByGroupCtrl",
            templateUrl: "view/userByGroupView.html",
            controllerAs: "user"
        })
        .when("/user/lab/", {
            controller: "",
            templateUrl: "view/userByLabView.html",
            controllerAs: ""
        })
        .when("/defect/", {
            controller: "DefectCtrl",
            templateUrl: "view/defectView.html",
            controllerAs: "defect"
        })
        .when("/defect/project/", {
            controller: "DefectByProjectCtrl",
            templateUrl: "view/defectByProjectView.html",
            controllerAs: "defect"
        })
        .when("/defect/group/", {
            controller: "DefectByGroupCtrl",
            templateUrl: "view/defectByGroupView.html",
            controllerAs: "defect"
        })
        .when("/defect/lab/", {
            controller: "",
            templateUrl: "view/defectByLabView.html",
            controllerAs: ""
        });

    $routeProvider.otherwise({"redirectTo": "/"});
});
