ccListApp.controller('ccListCtrlForFile',function($scope, $http, $location, $routeParams, $log){
    "use strict";

    var rowHeight = 28;
    var headerHeight = 30;

    $scope.ccListForFile = [];

   $http.get('/api/v1/codeMetrics/ccList', {
            }).then(function(result){
            if(result && result.data){
                $scope.ccListForFile=result.data.result;
            }
        }, function (results){
            $log.info('Error code:' + results.status +';');
        });

    angular.element('#showLoading').hide();

    $scope.ccListGridOptionsForFile = {
        data: 'ccListForFile',
        multiSelect : false,
        enablePaging: true,
        showFooter: true,
        enablePinning: true,
        enableColumnResize: true,
        enableColumnReordering: true,
        enableRowSelection: true,
        enableRowReordering: true,
        totalServerItems: 'allCCListCount',
        headerRowHeight: headerHeight,
        rowHeight: rowHeight,
        columnDefs:[
            {field:"fileName", displayName:'fileName', width: 150, cellClass:'textAlignCenter'},
            {field:"modulePath", displayName:'modulePath', width: 300, cellClass:'textAlignLeft'},
            {field:"metricValue", displayName:'CC Value', width: 100, cellClass:'textAlignCenter'}
        ]
    };
})