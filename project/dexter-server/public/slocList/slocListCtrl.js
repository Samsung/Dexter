slocListApp.controller('slocListCtrl',function($scope, $http, $location){
    "use strict";
    var rowHeight = 28;
    var headerHeight = 30;

    $scope.slocList = [];

    $scope.isTooHigh = function(value){
        return value > 500;
    };

    $http.get('/api/v1/codeMetrics/sloc', {
    }).then(function(result){
        if(result && result.data){
            $scope.slocList=result.data.result;
        }
    }, function (results){
        $log.info('Error code:' + results.status +';');
    });


    angular.element('#showLoading').hide();

    $scope.slocListGridOptions = {
        data: 'slocList',
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
            {field:"fileName", displayName:'File', width: 200, cellClass:'textAlignCenter'},
            {field:"modulePath", displayName:'Module', cellClass:'textAlignLeft'},
            {field:"metricValue", displayName:'SLOC', cellClass:'textAlignCenter', cellTemplate: '<div ng-class="{redBG: isTooHigh(row.getProperty(col.field))}"><div class="ngCellText">{{row.getProperty(col.field)}}</div></div>'}

        ]
    };
})