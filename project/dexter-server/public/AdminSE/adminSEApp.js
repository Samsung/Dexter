var adminSEApp = angular.module('adminSEApp', ['ngRoute', 'ngGrid', 'ngAnimate', 'ui.bootstrap', 'ngSanitize']);

adminSEApp.config(function($routeProvider){
    $routeProvider
        .when('/', {
            controller: 'adminSECtrl',
            templateUrl: 'adminSEView.html'
        });
});