defectApp.controller('snapshotCtrl', function ($scope, $http, $location){
    "use strict";
    var rowHeight = 28;
    var headerHeight = 30;

    window.localStorage['snapshotPageSize'] = (window.localStorage['snapshotPageSize']) || 100 ;
    window.localStorage['snapshotCurrentPage'] = parseInt(window.localStorage['snapshotCurrentPage']) || 1;

    $scope.snapshotPagingOptions = {
        pageSizes: [20 , 50, 100, 500],
        pageSize: window.localStorage['snapshotPageSize'],
        currentPage: parseInt(window.localStorage['snapshotCurrentPage'])
    };

    $scope.snapshotFilterOptions = {
        filterText: '',
        useExternalFilter: false
    };

    $scope.$watch('snapshotPagingOptions', function(newVal, oldVal){
        if(newVal !== oldVal) {
            window.localStorage['snapshotPageSize'] = $scope.snapshotPagingOptions.pageSize;
            window.localStorage['snapshotCurrentPage'] = $scope.snapshotPagingOptions.currentPage;
        }
    }, true);

    $scope.$on('ngGridEventData', function(row,event) {
        if (event == $scope.snapshotGridOptions.gridId) {
            var defectRow = parseInt($scope.allSnapshotCount % window.localStorage['snapshotPageSize'])+1;
        }
    });

    $scope.snapshotSelection=[];

    $scope.snapshotGridOptions = {
        data: 'snapshotList',
        multiSelect: false,
        enablePaging: true,
        showFooter: true,
        enablePinning: true,
        enableColumnResize: true,
        enableColumnReordering: true,
        enableRowSelection: true,
        enableRowReordering: true,
        totalServerItems: 'allSnapshotCount',
        pagingOptions: $scope.snapshotPagingOptions,
        filterOptions: $scope.snapshotFilterOptions,
        headerRowHeight: headerHeight,
        rowHeight: rowHeight,
        showGroupPanel: true,
        showColumnMenu: true,
        showFilter: true,
        showSelectionCheckbox: true,
        selectWithCheckboxOnly : true,
        jqueryUIDraggable: false,
        plugins:[new ngGridSingleSelectionPlugin()],
        columnDefs: [
            {field:"id", displayName:'Snapshot ID', width: 350, cellClass:'textAlignCenter'},
            {field:"defectCount", displayName:'Total Count', width :130, cellClass:'textAlignCenter'},
            {field:"criCount", displayName:'CRI Defect Count', width :130, cellClass:'textAlignCenter'},
            {field:"majCount", displayName:'MAJ Defect Count', width :130, cellClass:'textAlignCenter'},
            {field:"userId", displayName:'Creator', cellClass:'textAlignCenter'},
            {field:"createdDateTime", displayName:'Date', cellClass:'textAlignCenter', cellTemplate: '<div><div class="ngCellText">{{row.getProperty(col.field) | date:"yyyy-MM-dd HH:mm:ss"}}</div></div>' },
            {field:"groupId", displayName:'Group ID',visible:false}
       ]
    };


    function ngGridSingleSelectionPlugin() {
        var self = this;
        self.lastSelectedRow = null;
        self.selectedRowItems = [];
        self.allRowItems = [];
        self.isAllRowSelected = false;
        self.grid = null;
        self.scope=null;
        self.init = function (scope, grid, services) {
            self.services = services;
            self.grid = grid;
            self.scope=scope;
            self.initNeddedProprties();
            // mousedown event on row selection
            grid.$viewport.on('mousedown', self.onRowMouseDown);
            // mousedown event on checkbox header selection
            grid.$headerContainer.on('mousedown', self.onHeaderMouseDown);
        };
        //init properties
        self.initNeddedProprties = function () {
            self.grid.config.multiSelect = true;
            self.grid.config.showSelectionCheckbox = true;
            self.grid.config.selectWithCheckboxOnly = true;
        };
        self.onRowMouseDown = function (event) {
            // Get the closest row element from where we clicked.
            var targetRow = $(event.target).closest('.ngRow');
            // Get the scope from the row element
            var rowScope = angular.element(targetRow).scope();
            if (rowScope) {
                var row = rowScope.row;
                if (event.target.type !== 'checkbox') {
                    // if  select all rows checkbox was pressed
                    if (self.isAllRowSelected) {
                        self.selectedRowItems = self.grid.rowCache;
                    }
                    //set to false selected rows with checkbox
                    angular.forEach(self.selectedRowItems,function (rowItem) {
                        rowItem.selectionProvider.setSelection(rowItem, false);
                    });
                    self.selectedRowItems = [];
                    //set to false last selected row
                    if (self.lastSelectedRow) {
                        self.lastSelectedRow.selectionProvider.setSelection(self.lastSelectedRow, false);
                    }
                    if (!row.selected) {
                        row.selectionProvider.setSelection(row, true);
                        self.lastSelectedRow = row;
                        self.scope.$emit('ngGridEventRowSeleted',row);
                    }
                }
                else {
                    if (!row.selected) {
                        self.selectedRowItems.push(row);
                        self.scope.$emit('ngGridEventRowSeleted',row);

                    }
                }
            }
        };
        // mousedown event for checkbox header selection
        self.onHeaderMouseDown = function(event) {
            if (event.target.type === 'checkbox') {
                if (!event.target.checked) {
                    self.isAllRowSelected = true;
                } else {
                    self.isAllRowSelected = false;
                }
            }
        }

    }

    $scope.loadSnapshotInformation = _loadSnapshotInformation();

    function _loadSnapshotInformation() {
        $http.get('/api/v1/snapshot/snapshotList').then(function(results){
            if(results && results.data) {
                angular.element('#showLoading').hide();
                $scope.allSnapshotCount = results.data.snapshotInfo.length;
                $scope.snapshotList = results.data.snapshotInfo;
            }else {
                $log.error("error: " + results);
            }
        }, function(results){ // error
            $log.info('Error Code: ' + results.status + ';');
        });
    }


    $scope.$on('ngGridEventRowSeleted',function(event,row){
        $scope.selectedRow = row;
        var setUrl = '/snapshot/' + $scope.selectedRow.entity.id;
        var setPageName = 'Dexter Snapshot Id : ' + $scope.selectedRow.snapshotId;
        $location.path(setUrl);
    });

});