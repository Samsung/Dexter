var ccListApp = angular.module('ccListApp', ['ngRoute', 'ngGrid', 'ngAnimate', 'ui.bootstrap', 'ngSanitize']);

ccListApp.config(function($routeProvider){
    $routeProvider
        .when('/', {
            controller: 'ccListCtrl',
            templateUrl: 'ccListView.html'
        })
        .when("/:fileName/",{
            controller: 'ccListCtrlForFile',
            templateUrl: 'ccListViewForFile.html'
        })
    ;
    $routeProvider.otherwise({"redirectTo":"/"})

});