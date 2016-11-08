var codeMetricsApp = angular.module('codeMetricsApp', ['ngRoute', 'ngGrid', 'ngAnimate', 'ui.bootstrap', 'ngSanitize']);

codeMetricsApp.config(function($routeProvider){
    $routeProvider
        .when('/', {
            controller: 'codeMetricsCtrl',
            templateUrl: 'codeMetricsView.html'
        })
        .when('/:fileName/', {
            controller: 'codeMetricsCtrl',
            templateUrl: 'codeMetricsView.html'
        });
    //$routeProvider.otherwise({"redirectTo":"/"})
});