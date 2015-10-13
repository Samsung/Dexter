adminApp.controller('AdminCtrl', function($scope, $http, $log){

    var init = function(){
        $scope.isHidedDetailTab = true;
        $scope.totalAdminServerItems = 0;

        window.localStorage['adminPageSize'] = (window.localStorage['adminPageSize']) || 10;
        window.localStorage['adminCurrentPage'] = parseInt(window.localStorage['adminCurrentPage']) || 1;
        $scope.projectName = 'dexter-project';
	
	 $http.get('/api/v1/projectName', {
        }).then(function (result) {
            if( result && result.data){
                var html = 'Configuration on Dexter Web: ' +result.data.result;
                $scope.projectName = result.data.result;
                $('#indexAdminTitle').html(html);
            }
        }, function (results) {
            $log.error(results);
        });
    };

    init();


    $scope.checkIDReg = function (userId){
        var checkId = userId;
        var idReg = /^[A-Za-z0-9_.-]{4,101}$/;
        if( idReg.test(checkId) ){
            return true;
        }else{
            alert('It is not valid for This ID. It is too short or length 5~100 with alphabet and _,.,-.');
            return false;
        }
    };

    $scope.createAccount = function() {
        $scope.accountObj= document.getElementsByName('inputUser');
        $scope.createAdminYn = document.getElementsByClassName('btn createAdminBtn active')[0];

        $scope.addUserId = $scope.accountObj[0].value;
        var RegID = $scope.checkIDReg($scope.addUserId);



        var addPW = $scope.accountObj[1].value;
        var addPWConfirm = $scope.accountObj[2].value;

        if(!(RegID)){
            alert('Please check the Id : ' + RegId);
            return ;
        }

        if( addPW !== addPWConfirm){
            var errorMessage = 'Password is different from PW Confirm';
            alert(errorMessage);
            return ;
        }

        var addAdminYn = ($scope.createAdminYn.textContent === 'Admin') ? 'Y' : 'N';
        $http.post("/api/v1/accounts/webAdd", {
            params: {
                userId: $scope.addUserId,
                userId2: addPW,
                isAdmin: addAdminYn
            }
        }).then(function (results) {
            if(results.data.result === 'ok'){
                $scope.showAccount();
                var str = 'Success to Apply for Add Account';
                angular.element('#showAdminAlert').html(showAlertSuccessMSG(str));
                setTimeout(hideAlertMSG, 5000);
            }else{
                alert(results.data.errorMessage);
                $log.error(results.data);
            }
        }, function (results) {
            $log.error(results);
        });
    };

    $scope.modifySelectedItem = function() {
        $scope.accountObj = document.getElementsByName('modifyUser');
        $scope.changeAdminBtn = document.getElementsByClassName('btn changeAdminBtn active')[0];
        $scope.changeUserId = ($scope.accountObj[0].value == "") ? $scope.currentAccountId : $scope.accountObj[0].value;
        $scope.changePW = ($scope.accountObj[1].value == "") ? $scope.currentAccountPw : $scope.accountObj[1].value;


        var RegID = $scope.checkIDReg($scope.changeUserId);

        if(!(RegID)){
            alert('Please check the Id : '+RegID);
            return ;
        }

        var changeAdminYn = ($scope.changeAdminBtn.textContent === 'Admin') ? 'Y' : 'N';
        $http.post('/api/v1/accounts/webUpdate/' + $scope.currentAccountId , {
            params: {
                userId: $scope.changeUserId,
                userId2: $scope.changePW,
                isAdmin: changeAdminYn
            }
        }).then(function (results, err) {
            if (results) {
                $scope.showAccount();
                var str='Success to Apply for changed';
                angular.element('#showAdminAlert').html(showAlertSuccessMSG(str));
                setTimeout(hideAlertMSG, 5000);
            }
            else {
                str="Error: " + results  ;
                angular.element('#showAdminAlert').html(showAlertErrorMSG(str));
                setTimeout(hideAlertMSG, 5000);
            }
        })
    };

    var removeAccount = function(Id){
        $http.delete('/api/v1/accounts/remove/' + $scope.deleteSelectedItems[0])
            .then(function(results) {
                if (results) {
                    var str = 'Success to Apply for Delete';
                    angular.element('#showAdminAlert').html(showAlertSuccessMSG(str));
                    setTimeout(hideAlertMSG, 5000);
                }
                else {
                    str = 'Error: ' + results.data;
                    angular.element('#showAdminAlert').html(showAlertErrorMSG(str));
                    setTimeout(hideAlertMSG, 5000);
                }
            });
    };

    var removeAccountList = function(){
        $http.delete('/api/v1/accounts/removeAll', {
            params: {
                deleteSelectedItems: $scope.deleteSelectedItems
            }
        }).then(function (results) {
            if (results) {
                var str = 'Success to Apply for Delete.';
                angular.element('#showAdminAlert').html(showAlertSuccessMSG(str));
                setTimeout(hideAlertMSG, 5000);
            } else {
                str = 'Error: ' + results.data;
                angular.element('#showAdminAlert').html(showAlertErrorMSG(str));
                setTimeout(hideAlertMSG, 5000);
            }
        })
    };

    $scope.deleteSelectedItem = function() {
        $scope.deleteSelectedItems= [];
        for (var i = 0; i < $scope.accountSelections.length; i++) {
            $scope.deleteSelectedItems[i] = $scope.accountSelections[i].userId;
        }

        if( $scope.deleteSelectedItems.length == 1){
            removeAccount($scope.deleteSelectedItems[0]);
        }
        else {
            removeAccountList();
        }
    };

    function funcShowAccount() {
        $http.get('/api/v1/accounts/findAll', {
        }).then(function (results) {
            if (results && results.data) {
                $scope.adminAccountList = results.data.accounts;
                $scope.totalAdminServerItems = results.data.accounts.length;
            }
            else {
                $log.error('fail');
            }
        }, function (results) {
            $log.error('Load Error');
        });
    };

    $scope.showAccount = funcShowAccount;

    var hideAlertMSG = function(){
        angular.element('#showAdminAlert').hide();
    };

    var showAlertSuccessMSG = function(str){
        var showAlertMSG= "<a data-dismiss='alert' aria-hidden='true'><div class='admin-alert alert-success fade in'>" +
            "</a><span class='glyphicon glyphicon-ok'>&nbsp;</span><strong>"+ str+"</strong>(This message will disappear after 5 seconds.)</div>";
        angular.element('#showAdminAlert').show().html(showAlertMSG);
    };

    var showAlertErrorMSG = function(str){
        var showAlertMSG= "<a data-dismiss='alert' aria-hidden='true'><div class='admin-alert alert-danger fade in'>" +
            "</a><span class='glyphicon glyphicon-exclamation-sign'>&nbsp;</span><strong>"+ str+"</strong>(This message will disappear after 5 seconds.)</div>";
        angular.element('#showAdminAlert').show().html(showAlertMSG);
    };

    $scope.adminFilterOptions = {
        filterText: "",
        useExternalFilter: false
    };


    $scope.adminPagingOptions = {
        pageSizes: [10, 25, 100, 250],
        pageSize: window.localStorage['adminPageSize'],
        currentPage: parseInt(window.localStorage['adminCurrentPage'])
    };

    $scope.$watch('adminPagingOptions', function(newVal, oldVal){
        if(newVal !== oldVal) {
            $scope.newAdminCurrentPage = parseInt( $scope.totalAdminServerItems / parseInt($scope.adminPagingOptions.pageSize) ) +1;
            if($scope.adminPagingOptions.currentPage > $scope.newAdminCurrentPage){
                $scope.adminPagingOptions.currentPage = $scope.newAdminCurrentPage;
            }
            $scope.showAccount();
            window.localStorage['adminPageSize'] = parseInt($scope.adminPagingOptions.pageSize);
            window.localStorage['adminCurrentPage'] = $scope.adminPagingOptions.currentPage;
        }
    }, true);

    $scope.adminAccountList = [];
    $scope.accountSelections = [];

    $scope.adminGridOptions = {
        data: 'adminAccountList',
        selectedItems: $scope.accountSelections,
        multiSelect: true,
        enablePaging: true,
        showFooter: true,
        enablePinning: true,
        enableColumnResize: true,
        enableColumnReordering: true,
        enableRowReordering: true,
        totalAdminServerItems: 'totalAdminServerItems',
        pagingOptions: $scope.adminPagingOptions,
        filterOptions: $scope.adminFilterOptions,
        headerRowHeight: 28,
        rowHeight: 30,
        showGroupPanel: true,
        showColumnMenu: true,
        showFilter: true,
        showSelectionCheckbox: true,
        jqueryUIDraggable: false,
        columnDefs: [
            {field:'userNo', displayName:'No.', width: 80, cellClass:'textAlignCenter' },
            {field:'userId', displayName:'ID', width: 200, cellClass:'textAlignCenter'},
            {field:'adminYn', displayName:'Admin', width: 100, cellClass:'textAlignCenter', cellTemplate: '<div ng-class="{redBG: row.getProperty(col.field) >= 10}"><div class="ngCellText">{{row.getProperty(col.field)}}</div></div>' },
            {field:'createdDateTime', displayName:"CreatedDate", cellClass:'textAlignCenter', cellTemplate: '<div ng-class="{redBG: isTooLate(row.getProperty(col.field))}"><div class="ngCellText">{{row.getProperty(col.field) | date:"yyyy-MM-dd"}}</div></div>'},
            {field:'modifiedDateTime', displayName:'ModifiedDate', cellClass:'textAlignCenter', cellTemplate: '<div ng-class="{redBG: isTooLate(row.getProperty(col.field))}"><div class="ngCellText">{{row.getProperty(col.field) | date:"yyyy-MM-dd"}}</div></div>' ,visible:false }
        ]
    };

    $scope.$watch('adminAccountList', function() {
        $scope.accountSelections.splice(0, $scope.accountSelections.length);
    });

    $scope.$watch('accountSelections.length', function() {
        $scope.setAdminCurrentDetail();

    });

    $scope.setAdminCurrentDetail = function(){
        var currentIndex = $scope.accountSelections.length-1;
        if($scope.accountSelections.length != 0 ) {
            $scope.currentAccountId = $scope.accountSelections[currentIndex].userId;
            $scope.currentAccountPw = $scope.accountSelections[currentIndex].userPwd;
            $scope.currentAccountAdminYn = $scope.accountSelections[currentIndex].adminYn;
            var changeAdminY= document.getElementById('changeAdminYBtn');
            var changeAdminN= document.getElementById('changeAdminNBtn');
            if ($scope.currentAccountAdminYn === 'Y') {
                changeAdminY.className = 'btn changeAdminBtn active' ;
                changeAdminN.className = 'btn changeAdminBtn' ;
            }
            else {
                changeAdminY.className = 'btn changeAdminBtn' ;
                changeAdminN.className = 'btn changeAdminBtn active' ;
            }
            $scope.currentAccountCreatedDate = $scope.accountSelections[currentIndex].createdDateTime;
            $scope.currentAccountModifiedDate = $scope.accountSelections[currentIndex].modifiedDateTime;
        }
    };
    funcShowAccount();

    $scope.currentAccountList= [];
    // This must be a hyperlink
    $(".exportAccountList").on('click', function (event) {
        // Data URI
        $scope.csvContent = "data:text/csv;charset=utf-8,";
        setExportFileFormat($scope.adminAccountList);
        exportExcelFile.apply(this, [$scope.csvContent, 'export.csv']);
    });

    var setExportFileFormat = function(results){
        for(var i =0 ; i < results.length ; i++){
            if(i == 0){
                $scope.csvContent = $scope.csvContent + 'No,Id,Admin,CreateDate' + '\n' ;
            }
            $scope.currentAccountList[i] = results[i].userNo + ',' +results[i].userId +','+results[i].adminYn+','+results[i].createdDateTime;
            $scope.csvContent = $scope.csvContent + $scope.currentAccountList[i] + '\n' ;
        }
    };

    var exportExcelFile = function (){
        var encodedUri = encodeURI($scope.csvContent);
        $(this)
            .attr({
                'download': $scope.projectName +'_account.csv',
                'href': encodedUri,
                'target': '_blank'
            });
    };
});

