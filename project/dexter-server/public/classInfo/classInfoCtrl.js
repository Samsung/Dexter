classInfoApp.controller('classInfoCtrl',function($scope, $http, $location){
    "use strict";
    var rowHeight = 28;
    var headerHeight = 30;

    $scope.ccList = [
        {id:'1'},
        {id:'2'},
        {id:'3'}
    ];

    angular.element('#showLoading').hide();

    $scope.classInfoGridOptions = {
        data: 'ccList',
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
            {field:"id", displayName:'ID', width: 350, cellClass:'textAlignCenter'},
            {field:"id", displayName:'ID2', width: 350, cellClass:'textAlignCenter'}
        ]
    };
})