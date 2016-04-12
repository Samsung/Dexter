var functionMetricsApp = angular.module('functionMetricsApp', ['ngRoute', 'ngGrid', 'ngAnimate', 'ui.bootstrap','ngSanitize']);
functionMetricsApp.config(function($routeProvider){
    $routeProvider
        .when('/', {
            controller: 'functionMetricsCtrl',
            templateUrl: 'functionMetricsView.html'
        });
    $routeProvider.otherwise({"redirectTo":"/"})

});
