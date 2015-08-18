var dashboardApp = angular.module("dashboardApp", ['ngRoute', 'ngGrid', 'ngAnimate', 'ui.bootstrap', 'ngSanitize']);

dashboardApp.config(function($routeProvider){
    $routeProvider.when("/", {
        controller: "DashboardCtrl",
        templateUrl: "dashboardView.html"
    });
});