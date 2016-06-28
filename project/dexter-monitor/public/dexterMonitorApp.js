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
        'ui.grid', 'ui.grid.resizeColumns', 'ui.grid.moveColumns', 'ui.grid.exporter', 'ui.grid.autoResize'])
    .factory('_', ['$window', function($window){
        return $window._;
    }]);

monitorApp.config(function($routeProvider){
    $routeProvider
        .when("/", {
            controller: "",
            templateUrl: "view/mainView.html",
            controllerAs: ""
        })
        .when("/serverstatus/", {
            controller: "ServerStatusCtrl",
            templateUrl: "view/serverStatusView.html",
            controllerAs: "main"
        })
        .when("/user/", {
            controller: "",
            templateUrl: "view/userView.html",
            controllerAs: ""
        })
        .when("/user/project/", {
            controller: "",
            templateUrl: "view/userByProjectView.html",
            controllerAs: ""
        })
        .when("/user/group/", {
            controller: "",
            templateUrl: "view/userByGroupView.html",
            controllerAs: ""
        })
        .when("/user/lab/", {
            controller: "",
            templateUrl: "view/userByLabView.html",
            controllerAs: ""
        })
        .when("/defect/", {
            controller: "",
            templateUrl: "view/defectView.html",
            controllerAs: ""
        })
        .when("/defect/project/", {
            controller: "",
            templateUrl: "view/defectByProjectView.html",
            controllerAs: ""
        })
        .when("/defect/group/", {
            controller: "",
            templateUrl: "view/defectByGroupView.html",
            controllerAs: ""
        })
        .when("/defect/lab/", {
            controller: "",
            templateUrl: "view/defectByLabView.html",
            controllerAs: ""
        });

    $routeProvider.otherwise({"redirectTo": "/"});
});
