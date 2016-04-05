var defectApp = angular.module("defectApp", ['ngRoute', 'angularTreeview', 'ngGrid', 'ngAnimate', 'ui.bootstrap', 'ngSanitize','ngCsv']);

defectApp.config(function($routeProvider){
    $routeProvider
        .when("/", {
            controller: "DefectCtrl",
            templateUrl: "defectTreeView.html"
        })
        .when("/snapshot/", {
            controller: "snapshotCtrl",
            templateUrl: "snapshotView.html"
        })
        .when("/snapshot/:snapshotId/", {
            controller: "DefectCtrl",
            templateUrl: "defectTreeView.html"
        }

    );
    $routeProvider.otherwise({"redirectTo": "/"});
});

defectApp.factory(
    "_",
    function( $window ){
        var _ = $window._;
        delete ( $window._ );
        // YOU CAN Added CUSTOM LODASH METHODS ----


        // Return the [formerly global] reference so that it can be ingected
        // into other aspects of the AngularJS application.
        return ( _ );

    }

)
angular.element(document).ready(function() {
});
