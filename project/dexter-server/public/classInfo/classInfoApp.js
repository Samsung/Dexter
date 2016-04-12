var classInfoApp = angular.module('classInfoApp', ['ngRoute', 'ngGrid', 'ngAnimate', 'ui.bootstrap', 'ngSanitize']);

classInfoApp.config(function($routeProvider){
    $routeProvider.when('/', {
        controller: 'classInfoCtrl',
        templateUrl: 'classInfoView.html'
    });
});