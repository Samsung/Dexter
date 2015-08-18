var adminApp = angular.module('adminApp', ['ngRoute', 'ngGrid', 'ngAnimate', 'ui.bootstrap', 'ngSanitize']);

adminApp.config(function($routeProvider){
    $routeProvider.when('/', {
        controller: 'AdminCtrl',
        templateUrl: 'adminView.html'
    });
});