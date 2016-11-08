var slocListApp = angular.module('slocListApp', ['ngRoute', 'ngGrid', 'ngAnimate', 'ui.bootstrap', 'ngSanitize']);

slocListApp.config(function($routeProvider){
    $routeProvider.when('/', {
        controller: 'slocListCtrl',
        templateUrl: 'slocListView.html'
    });
});