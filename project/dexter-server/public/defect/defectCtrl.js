defectApp.controller('DefectCtrl', function($scope, $http, $sce, $location, $anchorScroll, $routeParams, $log ){
    "use strict";

    var init = function() {
        $scope.isHideFileTree = true;
        $scope.isHideLoginBtn = true;
        $scope.isHidedDetailTab = false;
        $scope.isHidedToButton = true;
        $scope.isAdminUser = false;
        $scope.isCurrentLoginId = false;

        $scope.isHideFileTreeBtnTitle = 'Show File Tree';

        $('#animatiroonTab').css('display', 'none');
        $('#animationTab').css('left', document.body.clientWidth);

        $scope.search = {
            modulePath: '',
            fileName: '',
            statusCode: '',
            severityCode: '',
            checkerCode: '',
            modifierNo: ''
        };
        $scope.treeItem = {
            'modulePath': '',
            'name': '',
            'label': '',
            'type': '',
            'defectCount': '',
            'newCount': '',
            'fixCount': '',
            'excCount': '',
            'children': []
        };

        $scope.didList = [];
        $scope.fileTree = [];
        $scope.defectSelections = [];
        $scope.defectList = [];
        $scope.selectedDidListInGrid = [];
        $scope.currentDefectList = [];
        $scope.currentFileList = [];

        $scope.totalServerItems = 0;
        $scope.projectName = 'dexter-project';

        initLocalStorage();
        getProjectNameForDefect();
        checkLogin();
    };

    init();

    function checkLogin(){
        $http.get("/api/v1/accounts/checkLogin", {
        }).then (function (results) {
            if(results.data.userId){
                $scope.isHideLoginBtnTitle = results.data.userId + "/ logout";
            }
            else{
                $scope.isHideLoginBtnTitle = 'Login';
            }
       }, function(results){
            $log.error(results);
        });
    };

    function initLocalStorage(){
        window.localStorage['pageSize'] = (window.localStorage['pageSize']) || 500;
        window.localStorage['currentPage'] = parseInt(window.localStorage['currentPage']) || 1;
    };

    function getProjectNameForDefect() {
        $http.get('/api/v1/projectName', {
        }).then(function (result) {
            if (result && result.data) {
                var html = 'Defect : ' + result.data.result;
                $scope.projectName = result.data.result;
                $('#indexTitle').html(html);
            }
        }, function (results) {
            $log.error(results);
        });
    }

    function moveTopOfPage(){
            $location.hash('bookmark');
            $anchorScroll();
    }

    function removeLocalStorageOfResources(){
        window.localStorage.removeItem('modulePath');
        window.localStorage.removeItem('fileName');
        window.localStorage.removeItem('selectTree');
    }

    var resetAdminUserFlag = function(){
        $scope.isAdminUser = false;
        $scope.isCurrentLoginId = false;
    };

    moveTopOfPage();
    resetAdminUserFlag();

    function loadTreeItemFromDB(state, type){
        $http.get('/api/v1/defect/status/'+ state, {
            params: {
                statusCode: $scope.search.statusCode
            }
        }).then(function (results) {// success
            if (results && results.data) {
                for (var i = 0; i < results.data.length; i++) {
                    var data = results.data[i];
                    $scope.treeItem = {
                        'name': data.modulePath,
                        'label': data.modulePath + ' (' + data.newCount + '/' + data.defectCount + ')',
                        'type': type,
                        'defectCount': data.defectCount,
                        'newCount': data.newCount,
                        'fixCount': data.fixCount,
                        'excCount': data.excCount,
                        'children': []
                    };
                    if(type ==='project'){
                        $scope.fileTree.splice(i, 0, $scope.treeItem);
                    }
                    else if(type ==='module'){
                        $scope.fileTree[0].children.splice(i,0, $scope.treeItem);
                    }
                    else if (type ==='file'){
                        $scope.treeItem.modulePath = data.modulePath;
                        $scope.treeItem.name = data.fileName;
                        $scope.treeItem.label = data.fileName + ' (' + data.newCount + "/" + data.defectCount + ')';
                        for (var j = 0; j < $scope.fileTree[0].children.length; j++) {
                            if ($scope.fileTree[0].children[j].name == data.modulePath) {
                                $scope.fileTree[0].children[j].children.splice($scope.fileTree[0].children.length, 0, $scope.treeItem);
                            }
                        }
                    }
                }
            }
        }, function (results) {
            $log.error('Error: ' + results.data + '; ' + results.status);
        });
    };

    function loadTreeItem() {
        loadTreeItemFromDB('project','project');
        loadTreeItemFromDB('modulePath','module');
        loadTreeItemFromDB('fileName','file');
    };

    $scope.isHideFileTree = true;
    $scope.isHideLoginBtn = true;
    $('#animatiroonTab').css('display','none');
    $('#animationTab').css('left', document.body.clientWidth);

    $scope.isHideFileTreeBtnTitle = 'Show File Tree';
    $scope.isHideLoginBtnTitle = 'Login';


    $scope.deselectSelectionList = function(){
        $scope.defectSelections.length = 0;
        window.localStorage.removeItem('defectSelections');
        deselectSelectionDefectList();
    };

    /* all of the alert MSG */
    var hideDefectAlertMSG = function(){
        angular.element('#showDefectAlert').hide();
    };

    var showDefectAlertMSG = function (str, status){
        var alertStatus = status;
        var status = status;
        (status == 'success') ? status = 'glyphicon-ok': status ='glyphicon-exclamation-sign' ;
        (alertStatus == 'success') ? alertStatus = 'alert-success': alertStatus ='alert-warning' ;
        var showAlertDefectMSG = "<a class='close' data-dismiss='alert' aria-hidden='true'><div class='defect-alert "+ alertStatus+" fade in'>" +
            "x </a><span class='glyphicon "+ status +"'>&nbsp;</span><strong></strong>"+ str+ " </strong></div>";
        angular.element('#showDefectAlert').show().html(showAlertDefectMSG);
    };

    function successLogin(results){
        $scope.isAdminUser = results.isAdmin;
        if(! $scope.isAdminUser){
            $('#adminBtn').css({display : 'none'});
        }
        $scope.isCurrentLoginId = true;
        $scope.isHideLoginBtn = true;
        $scope.currentLoginId = results.userId;
        $scope.isHideLoginBtnTitle = $scope.currentLoginId + "/ logout";

        var str = "You have successfully logged in of Dexter Web. Welcome : " +$scope.currentLoginId +". ";
        angular.element('#showDefectAlert').html(showDefectAlertMSG(str,'success'));
        setTimeout(hideDefectAlertMSG, 5000);
    }
    function successAdminLogin(results){
        $scope.currentLoginId = results.userId;
        $scope.isHideLoginBtnTitle = $scope.currentLoginId + "/ logout";
        openAdminPage();
    }

    $scope.checkLogin = function() {
        if($scope.currentLoginId === undefined){
            $http.get("/api/v1/accounts/checkWebLogin", {
            }).then (function (results) {
                if(results.data.userId){
                    successLogin(results.data);
                }
                else{
                    var str = "Please check Your ID or PW and use the Dexter account.";
                    angular.element('#showDefectAlert').html(showDefectAlertMSG(str,'error'));
                    setTimeout(hideDefectAlertMSG, 5000);
                }
            }, function(results){
                $log.error(results);
            });
        }
        else {
            $scope.isHideLoginBtn = true;
            alert("Do you really want to logout on Dexter Web?");
            $scope.isHideLoginBtnTitle = 'Login';
            logout();
        }
    };

    var openAdminPage = function() {
        window.open('../admin','Dexter Admin Configuration','width=1204 height=580 left=50% top=50%');
    };

    $scope.checkAdmin = function() {
        $http.get("/api/v1/accounts/checkAdmin", {
        }).then (function (results) {
            if(results.data.isAdmin){
               successAdminLogin(results.data);
            }
            else{
                var str = "Please use Dexter Admin Account.";
                angular.element('#showDefectAlert').html(showDefectAlertMSG(str,'error'));
                setTimeout(hideDefectAlertMSG, 5000);
            }
        }, function(results){
            $log.error(results);
        });

    };

    $scope.$watch( 'currentUserId', function(newVal, oldVal){
        if(oldVal == 'undefined'){
            $scope.isHideLoginBtn = false;
        }
        if(newVal !== oldVal) {
            $scope.isCurrentLoginId = true;
            $scope.currentUserId = newVal;
            $scope.isHideLoginBtnTitle = $scope.currentUserId + "/ logout";
        }
    }, true);



    function logout() {
        var xmlHttp;
        if (window.XMLHttpRequest) {
            xmlHttp = new XMLHttpRequest();
        }
        if (window.ActiveXObject) {
            document.execCommand("ClearAuthenticationCache");
            window.location.href='#';
        } else {
            xmlHttp.open('GET', '/api/v1/accounts/logout', true, 'logout', 'logout');
            xmlHttp.send('');
            xmlHttp.onreadystatechange = function() {
                if (xmlHttp.readyState == 4) {
                    window.location.href='#';
                    var str = "You have been logged out of Dexter Web. ";
                    angular.element('#showDefectAlert').html(showDefectAlertMSG(str,'error'));
                    setTimeout(hideDefectAlertMSG, 5000);
                }
            }
        }
        return false;
    }

    $scope.currentFileList = [];

    function toggleFileTreeAnimation(isShowState){
        if(isShowState === true) {
            $('#animationTab').hide();
        }
        else {
            var calculateLeft = document.body.clientWidth - 598;
            $('#animationTab').css('display', 'block').animate({left: calculateLeft})
                .css('left', document.body.clientWidth);
        }
    }

    function toggleShowFileTreeBtn(isShowState){
        $scope.isHideFileTree = isShowState;
        $scope.isHideFileTreeBtnTitle = ((isShowState === true) ? 'Show File Tree' : 'Hide File Tree');
    }

    $scope.loadCurrentFileTree = function(btnID) {
        if (btnID !== 'showFileTreeBtn'){
            return ;
        }
        if ($scope.isHideFileTree === true) {
            loadTreeItem();
        }
        toggleFileTreeAnimation(!($scope.isHideFileTree));
        toggleShowFileTreeBtn(!($scope.isHideFileTree));
    };

    $scope.isHidedDetailTab = false;
    $scope.isHidedToButton = true;
    $scope.didList = [];

    //only administrator
    var fixSelectedItem = function() {
        $scope.didList = [];
        for (var i = 0; i < $scope.defectSelections.length; i++) {
            if ($scope.defectSelections[i].statusCode != 'FIX') {
                $scope.didList[i] = $scope.defectSelections[i].did;
                dismissSelectedDefect($scope.defectSelections[i].did, true);
            }
        }
        $http.post('/api/v1/defect/changeFix', {
            params: {
                didList: $scope.didList
            }
        }).then(function (results) {
            // success
            if (results && results.data) {
                for (var i = 0; i < results.data.length; i++) {
                    var data = results.data[i];
                    $scope.fileTree.splice(i, 0, moduleItem);
                }
            }
            //$scope.defectSelections.length = 0;
            var str = 'Success to Apply for changed.';
            angular.element('#showDefectAlert').html(showDefectAlertMSG(str, 'success'));
            setTimeout(hideDefectAlertMSG, 5000);
            showDefects();
        }, function (results) {
            // error
            $log.error('Error: ' + results.data + ';'  + results.status);
            var str = 'An unexpected error has occurred, It is not changed status of defects. ';
            angular.element('#showDefectAlert').html(showDefectAlertMSG(str,'error'));
            setTimeout(hideDefectAlertMSG, 5000);
        });
    };

    $scope.changeDefectStatus = function (defectStatus) {
        $http.post("/api/v1/defect/"+defectStatus , {
            params: {
                didList: $scope.didList
            }
        }).then(function (results) {
            if (results && results.data) {
                for (var i = 0; i < results.data.length; i++) {
                    var data = results.data[i];
                    $scope.fileTree.splice(i, 0, moduleItem);
                }
            }
            var str = "Success to Apply for changed. ";
            angular.element('#showDefectAlert').html(showDefectAlertMSG(str,'success'));
            setTimeout(hideDefectAlertMSG, 5000);
            showDefects();
        }, function (results) {
            // error
            $log.error("Error: " + results.data + "; " + results.status);
            var str = "An unexpected error has occurred, It isn't changed status of defects. ";
            angular.element('#showDefectAlert').html(showDefectAlertMSG(str,'error'));
            setTimeout(hideDefectAlertMSG, 5000);
        });
    };

    $scope.markDefectStatusByjQuery = function(state){
        $scope.didList = [];
        $scope.didList[0] = $scope.currentDetailDid ;
        $scope.changeDefectStatus(state);

    };

    $scope.markDefectStatusByAngular = function(state) {
        if($scope.defectSelections.length == 0){
            $log.info("need to select items");
            return ;
        }
        $scope.didList = [];
        var selectedItem = $scope.defectSelections;

        for (var i = 0, len = selectedItem.length; i < len; i++) {

            if (selectedItem[i].statusCode != "EXC" || selectedItem[i].statusCode != "NEW" ) {
                $scope.didList[i] = selectedItem[i].did;
                dismissSelectedDefect(selectedItem[i].did, true);
            }
        }
        $scope.changeDefectStatus(state);
    };

    $scope.removeDefectFromDB = function(){
        $scope.fileList = [];
        if(window.localStorage['fileName'] != '') {
            if (confirm("Did you remove [" + window.localStorage['modulePath'] + "/" + window.localStorage['fileName'] + " ] file in workspace?")){
                $scope.fileList = window.localStorage['fileName'].split(' ');
                $http.post("/api/v1/filter/delete-file-tree", {
                    params: {
                        modulePath : base64.encode(window.localStorage['modulePath']),
                        fileList : $scope.fileList
                    }
                }).then(function(results){
                    $scope.loadCurrentFileTree('removeFileTreeBtn');
                    removeLocalStorageOfResources();

                }, function(results){
                });
            }
        }
        else{
            var str = "Please select any module or file in File Tree. ";
            angular.element('#showDefectAlert').html(showDefectAlertMSG(str,'error'));
            setTimeout(hideDefectAlertMSG, 5000);
        }
    };

    $scope.fileTree = [];

    function goPageInNgGrid(page){
        $scope.pagingOptions.currentPage = page;
    }

    function showDefects() {
        //loadTotalDefectCount();
        loadTotalDefectInformation();
    }

    function setModulePathAndFileNameInSearch(modulePath, fileName){
        $scope.search.modulePath = modulePath;
        $scope.search.fileName = fileName;
    }

    $scope.$watch('fileTreeId.currentNode', function(){
        if($scope.fileTreeId.currentNode === undefined ){
            return;
        }
        var currentNode = $scope.fileTreeId.currentNode;
        var modulePath ='';
        var fileName ='';
        switch (currentNode.type) {
            case 'module':
                modulePath = currentNode.name == '' ? '##HAS-NO-MODULE##' : currentNode.name;
                break;
            case 'file':
                modulePath = currentNode.modulePath;
                fileName = currentNode.name;
                break;
        }
        setModulePathAndFileNameInSearch(modulePath, fileName);
        goPageInNgGrid(1);
        showDefects();
    }, false);

    var dismissSelectedDefect = function(did, isActive) {
        if(isActive == true){
            $http.post('/api/v1/filter/false-alarm', {
                params: { did: did }
            }).then(function(results){
                    $log.info('dismissSelectDefect');
                    $log.debug(results.data);
                }, function(results){
                    $log.error(results.data + results.status);
            });
        } else {
            $http.post('/api/v1/filter/delete-false-alarm', {
                params: { did: did }
            }).then(function(results){
                $log.info('deleteDismissSelectDefect');
                $log.debug(results.data);
            }, function(results){
                $log.error(results.data + results.status);
            });
        }
    };

    $scope.getOnlyClassName = function(className){
        if(className==null){
            return "";
        }
        else{
            var start = className.lastIndexOf(".") + 1;
            return className.substring(start);
        }
    };


    $scope.isStatusNotNew = function(value){
        return (value == 'EXC') || (value == 'FIX');
    };

    $scope.isStatusNew = function(value){
        return value == 'NEW';
    };

    $scope.isMajor = function(value){
        return value == 'CRI';
    };

    $scope.isTooLate = function(value){
        var valueDate = new Date(value);
        var today = new Date();
        var diff = today.getTime() - valueDate.getTime();
        return (diff / (60*60*24*1000)) > 5;
    };

    $scope.filterOptions = {
        filterText: '',
        useExternalFilter: false
    };

    $scope.totalServerItems = 0;

    window.localStorage['pageSize'] = (window.localStorage['pageSize']) || 1000;
    window.localStorage['currentPage'] = parseInt(window.localStorage['currentPage']) || 1;

    $scope.pagingOptions = {
        pageSizes: [250, 500, 1000, 2500],
        pageSize: window.localStorage['pageSize'],
        currentPage: parseInt(window.localStorage['currentPage'])
    };

    $scope.$watch('pagingOptions', function(newVal, oldVal){
        if(newVal !== oldVal) {
            showDefects();
            $scope.newCurrentPage = parseInt( $scope.totalServerItems / $scope.pagingOptions.pageSize ) +1;
            if($scope.pagingOptions.currentPage > $scope.newCurrentPage){
                $scope.pagingOptions.currentPage = $scope.newCurrentPage;
            }
            window.localStorage['pageSize'] = $scope.pagingOptions.pageSize;
            window.localStorage['currentPage'] = $scope.pagingOptions.currentPage;
        }
    }, true);

    var rowHeight = 28;
    var headerHeight = 30;


    function loadTotalDefectInformation() {
        if ($routeParams.snapshotId !== undefined) {
            setSnapshotColumnField();
            var snapshotId = $routeParams.snapshotId;
            $http.get('/api/v1/snapshot/showSnapshotDefectPage', {
                params: {
                    'snapshotId': snapshotId
    }
            }).then(function (results) {
                if (results && results.data) {
                    $scope.defectList = results.data.defectInSnapshot;
                    $scope.totalServerItems = results.data.length;

                } else {
                    $log.debug(results);
                }
            }, function (results) {
                $log.error(results.status);
            });
        }
        else {
			showDefectPage();
        }
    }

	function showDefectPage() {
		var defectParams = {
                'did': $scope.search.did,
                'modulePath': base64.encode($scope.search.modulePath),
                'fileName': $scope.search.fileName,
                'statusCode': $scope.search.statusCode,
                'severityCode': $scope.search.severityCode,
                'checkerCode': $scope.search.checkerCode,
                'modifierNo': $scope.search.modifierNo,
                'currentPage': $scope.pagingOptions.currentPage,
                'pageSize': $scope.pagingOptions.pageSize
            };
		
		$http.get('/api/v1/defect/count', {
            params: defectParams
        }).then(function (results) { // success
            if (results && results.data) {
                $scope.totalServerItems = results.data.defectCount;
            }
        }, function (results) { // error
            $log.error(results.status);
        });

		$http.get('/api/v1/defect', {
			params: defectParams
		}).then(function (results) { // success
			if (results && results.data) {
				$scope.defectList = results.data;
                angular.element('#showLoading').hide();
			}
		}, function (results) { // error
            $log.error(results.status);
		});
	}

    var setSnapshotColumnField = function (){
        angular.forEach($scope.gridOptions.columnDefs, function(obj){
            if(obj.field == 'currentStatusCode'){
                obj.visible = true;
                obj.cellTemplate= '<div ng-class="{greenBG: isStatusNotNew(row.getProperty(col.field))}"><div class="ngCellText">{{row.getProperty(col.field)}}</div></div>';
            }
            if(obj.field == 'statusCode'){
                obj.displayName = 'snap.Status';
                obj.cellTemplate= '<div class="ngCellText">{{row.getProperty(col.field)}}</div>';
            }
        });
    };


    $scope.gridOptions = {
        data: 'defectList',
        selectedItems: $scope.defectSelections,
        multiSelect: true,
        enablePaging: true,
        showFooter: true,
        enablePinning: true,
        enableColumnResize: true,
        enableColumnReordering: true,
        enableRowSelection: true,
        enableRowReordering: true,
        totalServerItems: 'totalServerItems',
        pagingOptions: $scope.pagingOptions,
        filterOptions: $scope.filterOptions,
        headerRowHeight: headerHeight,
        rowHeight: rowHeight,
        showGroupPanel: true,
        showColumnMenu: true,
        showFilter: true,
        showSelectionCheckbox: true,
        canSelectRows: true,
        afterSelectionChange: function (item)
        {
            $scope.selectedDidInNgGrid = item.entity;
        },
        jqueryUIDraggable: false,
        columnDefs: [
            {field:'did', displayName:'ID', width: 80, cellClass:'textAlignCenter'},
            {field:'checkerCode', displayName:'Checker'},
            {field:'occurenceCount', displayName:'Count', width:70, resizable: true, cellClass:'textAlignCenter', cellTemplate: '<div ng-class="{redBG: row.getProperty(col.field) >= 10}"><div class="ngCellText">{{row.getProperty(col.field)}}</div></div>' },
            {field:'occurenceLine', displayName:'Line No.', width:70, resizable: true },
            {field:'severityCode', displayName:'Severity', width:70,resizable: true, cellClass:'textAlignCenter', cellTemplate: '<div ng-class="{redFG: isMajor(row.getProperty(col.field))}"><div class="ngCellText">{{row.getProperty(col.field)}}</div></div>' },
            {field:'statusCode', displayName:'Status', width:82, resizable: true, cellClass:'textAlignCenter', cellTemplate: '<div ng-class="{redBG: isStatusNew(row.getProperty(col.field))}"><div class="ngCellText">{{row.getProperty(col.field)}}</div></div>' },
            {field:'currentStatusCode', visible:false, displayName:'cur.Status', width:82, resizable: true, cellClass:'textAlignCenter', cellTemplate: '<div class="ngCellText">{{row.getProperty(col.field)}}</div>' },
            {field:'modulePath', displayName:'Module',width:250, resizable: true, cellTemplate: '<div class="ngCellText">{{row.getProperty(col.field)}}</div>'},
            {field:'fileName', displayName:'File', resizable: true, cellTemplate: '<div class="ngCellText">{{row.getProperty(col.field)}}</div>'},
            {field:'className', displayName:'Class', resizable: true, cellTemplate: '<div class="ngCellText">{{getOnlyClassName(row.getProperty(col.field))}}</div>'},
            {field:'methodName', displayName:'Method/Function', resizable: true},
            {field:'language', displayName:'Language', cellClass:'textAlignCenter', width:85, resizable: true, cellclass:'textAlignCenter' },
            {field:'toolName',displayName:'Tool',visible:false, cellClass:'textAlignCenter', width:85, resizable: true, cellclass:'textAlignCenter' },
            {field:'modifierId', displayName:'Author', resizable: true, visible:false,  cellClass:'textAlignCenter'},
            {field:'modifiedDateTime', displayName:'Date', resizable: true, cellClass:'textAlignCenter', cellTemplate: '<div><div class="ngCellText">{{row.getProperty(col.field) | date:"yyyy-MM-dd HH:mm:ss"}}</div></div>' },
            {field:'message', displayName:"Description", visible:false, resizable: true}
        ]
    };

    showDefects();


    $scope.selectDefectRow = function(){
        if(window.localStorage['defectSelections'] === undefined ) {
            return ;
        }
        var localDid= [];
        var defectSelections = window.localStorage['defectSelections'];
        localDid = defectSelections.split(',');
        $scope.gridOptions.selectAll(false);
        angular.forEach(localDid, function(_did){
            angular.forEach($scope.defectList, function (data, index) {
                if (data.did == _did) {
                    $scope.gridOptions.selectRow(index, true);
                }
            })
        });
    };

    $scope.$on('ngGridEventData', function() {
        if($scope.totalServerItems === 0){
            return ;
        }
        //setTimeout(hideAlertLoading, 20);
        $scope.selectDefectRow();
    });

    var deselectSelectionDefectList = function(){
        $scope.gridOptions.selectAll(false);
    };

    document.onkeydown = fkey;
    document.onkeypress = fkey;
    document.onkeyup = fkey;

    var wasPressed = false;

    function fkey(e){
        e = e || window.event;
        if( wasPressed ){
            return;
        }
        if (e.keyCode == 116) {
            wasPressed = true;
        }
    }

    $scope.selectedDidListInGrid = [];

    $scope.$watch('defectSelections.length', function(newVal, oldVal){
        if($scope.defectSelections.length === 0){
            window.localStorage['defectSelections'] = '';
            $scope.selectedDidListInGrid = [];
            $scope.isHidedDetailTab = true;
            initCurrentState();
            return ;
        }

        if(newVal > oldVal){
            $scope.selectedDidListInGrid.push($scope.selectedDidInNgGrid.did);
            window.localStorage['defectSelections'] = $scope.selectedDidListInGrid;
            var len = $scope.defectSelections.length - 1;
            $scope.setCurrentDetail($scope.defectSelections[len].checkerCode, $scope.defectSelections[len].did);
        }
        else{
            angular.forEach($scope.selectedDidListInGrid, function(obj, index) {
                if(obj == $scope.selectedDidInNgGrid.did){
                    $scope.selectedDidListInGrid.splice(index, 1);
                }
            });
            window.localStorage['defectSelections'] = $scope.selectedDidListInGrid;
        }
    });

    function initCurrentState(){
        $scope.tempDidList = [];
        $scope.defectOccurenceInformations = [];
        $scope.cntK = [];
        $scope.selectedDefect = '';
        $scope.defectSourceCodes = [];
        $scope.defectOccurrences = [];
        $scope.defectDescriptions = [];
    }

    $scope.storeCurrentDefectInfo = function(did){
        // select did from defectList
        for(var q=0; q<$scope.defectList.length; q++) {
            if ($scope.defectList[q].did == did) {
                var currentDefect = $scope.defectList[q];
                $scope.currentDetailDid = currentDefect.did;
                $scope.currentDetailModulePath = currentDefect.modulePath || 'undefined';
                $scope.currentDetailFileName = currentDefect.fileName;
                $scope.currentDetailCheckerCode = currentDefect.checkerCode;
                $scope.currentDetailSeverityCode = currentDefect.severityCode;
                $scope.currentDetailOccurenceCount = currentDefect.occurenceCount;
                $scope.currentDetailStatus = currentDefect.statusCode;
                $scope.currentDetailClassName = currentDefect.className;
                $scope.currentDetailMethodName = currentDefect.methodName || 'N/A';
                $scope.currentDetailModifierId = currentDefect.modifierId;
                $scope.currentDetailModifiedDateTime = currentDefect.modifiedDateTime;
                $scope.currentDetailmessage = currentDefect.message;
            }
        }
    };

    $scope.hideDetailTab =  function(){
        ($scope.isHidedDetailTab == true) && ($scope.isHidedDetailTab = false);
        ($scope.isHidedToButton == true) && ($scope.isHidedToButton = false) ;
    };


    $scope.createHelpHtmlURL = function(selectedDefect){
        if (selectedDefect.hasOwnProperty('checkerCode')) {
            $scope.checkerCodeSet[selectedDefect.checkerCode] = '/tool/'
                + selectedDefect.toolName + "/"
                + selectedDefect.language + "/help/"
                + selectedDefect.checkerCode + ".html";
        }
    };



    $scope.getHelpDescription = function(selectedDefect, checkerCode){
        if ($scope.selectedDefect.checkerCode == checkerCode) {
            $http.get($scope.checkerCodeSet[checkerCode], {"checkerCode": checkerCode})
                .then(function (results) {
                    if (results) {
                        getCheckerDescription(results.data, results.config.checkerCode);
                    }
                },function(){
                    $http.get('/tool/NotFoundCheckerDescription/empty_checker_description.html')
                        .success(function (results){
                            if (results) {
                                getCheckerDescription(results, checkerCode);
                            }
                        });
                })
        }
    };

    var getCheckerDescription = function(results, checkerCode){
        $scope.defectDescriptions = [];
        var defectDescription = {"checkerCode": checkerCode, "description": $sce.trustAsHtml(results) };
        $scope.currentChecker = checkerCode;
        $scope.defectDescriptions.splice($scope.defectDescriptions.length, 0, defectDescription);
        $scope.currentDetailDescription = $scope.defectDescriptions[0].description;
    };

    $scope.getDefectOccurrenceInFile= function(selectedDefect, did) {
        //check did in selectedList
        if (selectedDefect.did == did) {
            $scope.selectedDefectModulePath = (selectedDefect.modulePath) || "undefined";

            var snapshotId = 'undefined';
            if ($routeParams.snapshotId !== undefined) {
                snapshotId = $routeParams.snapshotId;
                $http.get("/api/v1/snapshot/occurenceInFile", {
                    params: {
                        'modulePath': base64.encode($scope.selectedDefectModulePath),
                        'fileName': selectedDefect.fileName,
                        'snapshotId' : snapshotId
                    }
                }).then(function (results) {
                    $scope.defectOccurrences[results.config.params.fileName] = (results.data) || 'undefined';
                });
            }
            else {
                $http.get("/api/v1/occurenceInFile", {
                    params: {
                        'modulePath': base64.encode($scope.selectedDefectModulePath),
                        'fileName': selectedDefect.fileName
                    }
                }).then(function (results) {
                    $scope.defectOccurrences[results.config.params.fileName] = (results.data) || 'undefined';
                });
            }

            $http.get("/api/v1/analysis/snapshot/checkSourceCode", {
                params: {
                    'modulePath': base64.encode($scope.selectedDefectModulePath),
                    'fileName': selectedDefect.fileName
                }
            }).then(function (results) {
                if (results) {
                    var getCodeMsg = " : Please wait until loading source code";
                    var msg = "<pre class='prettyprint linenums'>" + getCodeMsg + "</pre>";
                    angular.element('#SourceCodeTab').html(msg);

                    if (results.data[0].count > 0) {
                        $http.get("/api/v1/analysis/snapshot/source", {
                                params: {
                                    'modulePath': base64.encode($scope.selectedDefectModulePath),
                                    'fileName': selectedDefect.fileName,
                                    'snapshotId': snapshotId
                                }
                            }
                        ).then(function (results) {
                                if (results) {
                                    $scope.defectSourceCodes.splice($scope.defectSourceCodes.length, 0, {
                                        'source': results.data,
                                        'fileName': results.config.params.fileName,
                                        'modulePath': base64.decode(results.config.params.modulePath)
                                    });
                                    $scope.getSourceCode();
                                }
                            }, function () {
                            });
                    }
                    else if (results.data[0].count == 0) {
                        var noCodeMsg = "There is no content for source codes. When you use snapshot or CLI, you can see the source codes.";
                        var ht = "<pre class='prettyprint linenums'>" + noCodeMsg + "</pre>";
                        angular.element('#SourceCodeTab').html(ht);
                    }
                }
            });
        }
        //}
    };



    $scope.setCurrentDetail = function(checkerCode,did) {
        $("#bookmark").remove();
        // for defect Description
        $scope.checkerCodeSet = {};
        $scope.hideDetailTab();
        $scope.storeCurrentDefectInfo(did);
        //for (var index in $scope.defectSelections) {
        var len = $scope.defectSelections.length;
        $scope.selectedDefect = $scope.defectSelections[len-1];
        $scope.createHelpHtmlURL($scope.selectedDefect);
        $scope.getHelpDescription($scope.selectedDefect, checkerCode);
        $scope.getDefectOccurrenceInFile($scope.selectedDefect, did);
        //}
    };

    $(document).on('click','#markFalseDefectSelectedItem', function(){
        $scope.markDefectStatusByjQuery('markFalseDefect');
    });
    $(document).on('click','#markDefectSelectedItem', function(){
        $scope.markDefectStatusByjQuery('markDefect');
    });
    $(document).on('click','#SourceTab', function(){
        moveTopOfPage();
    });

    var checkOccurrenceLine = function(currentStartLine, k){
        $scope.commentIndex1[k] = (currentStartLine) / 10;
        $scope.commentIndex2[k] = ((currentStartLine) % 10) -1;

        ($scope.commentIndex1[k] == 0 && $scope.commentIndex2[k] == -1) && ($scope.commentIndex2[k] = 0);
        if($scope.commentIndex1[k] != 0 && $scope.commentIndex2[k] == -1) {
            $scope.commentIndex1[k] = $scope.commentIndex1[k]-1;
            $scope.commentIndex2[k] = 9;
        }
    };

    $scope.addDefectComment = function(){
        var comment = {};
        $scope.commentIndex1 = [];
        $scope.commentIndex2 = [];
        $scope.cntK = [];

        for(var k=0 ; k<$scope.defectOccurrences[$scope.currentDetailFileName].length; k++) {
            var curDefectOccurrenceInFile=$scope.defectOccurrences[$scope.currentDetailFileName][k];
            $scope.cntK[k] = k + 1;
            var currentStartLine= curDefectOccurrenceInFile.startLine;
            var currentSourceMSG = curDefectOccurrenceInFile.message;

            $scope.defectOccurenceInformations[k] = curDefectOccurrenceInFile;
            if ($scope.defectOccurenceInformations[k].modifierId == null) {
                $scope.defectOccurenceInformations[k].modifierId = $scope.currentDetailModifierId;
            }

            if(curDefectOccurrenceInFile.did == $scope.currentDetailDid){
                curDefectOccurrenceInFile.modifiedId = (curDefectOccurrenceInFile.modifiedId) || $scope.currentDetailModifierId;
                $scope.currentDidStartLine= curDefectOccurrenceInFile.startLine;
                $scope.currentDidEndLine = curDefectOccurrenceInFile.endLine;
                $scope.currentDidSourceMSG = curDefectOccurrenceInFile.message;

                var moveTopOfPageBtn = $('.topOfPageDummy').clone(true);
                moveTopOfPageBtn.removeClass('topOfPageDummy').attr('id','moveTopOfPage');

                var defectBtn = $('.realDefectDummy').clone(true);
                defectBtn.removeClass('realDefectDummy').attr('id','markDefectSelectedItem');

                var falseBtn = $('.falseDefectDummy').clone(true);
                falseBtn.removeClass('falseDefectDummy').attr('id','markFalseDefectSelectedItem');

                comment[k] = "<div id='bookmark' class='anchor'>" + moveTopOfPageBtn[0].outerHTML + ' / ' + defectBtn[0].outerHTML + ' / ' + falseBtn[0].outerHTML ;
                comment[k] = comment[k] +  "<span class='nocode annotCheck' style='display:inline-block' width:100%><span class='glyphicon glyphicon-star'></span>" +
                    "&nbsp;DID: <strong>" + curDefectOccurrenceInFile.did+"</strong>&nbsp;(" +
                    $scope.cntK[k] + " of " + $scope.defectOccurrences[$scope.currentDetailFileName].length +
                    ")&nbsp;" + "<strong>[" + curDefectOccurrenceInFile.severityCode +" /</strong> Checker: <strong>" +
                    curDefectOccurrenceInFile.checkerCode + "]</strong><br>: &nbsp;" + currentSourceMSG + "&nbsp;</span></div>";
            }
            else{
                comment[k] = "<div><span class='nocode annot' style='display:inline-block' width:100%>"
                    + "<span class='glyphicon glyphicon-star-empty'></span>&nbsp;DID: <strong>" + curDefectOccurrenceInFile.did+"</strong>&nbsp;("
                    + $scope.cntK[k] + " of " + $scope.defectOccurrences[$scope.currentDetailFileName].length
                    + ")&nbsp;" + "<strong>[" + curDefectOccurrenceInFile.severityCode +" /</strong> Checker: <strong>"
                    + curDefectOccurrenceInFile.checkerCode + "]</strong><br>: &nbsp;" + currentSourceMSG + "&nbsp;</span></div>";
            }
            checkOccurrenceLine(currentStartLine, k);
            $("#SourceCodeTab .L" + $scope.commentIndex2[k]).each(function (index) {
                if (index == parseInt($scope.commentIndex1[k])) {
                    $(this).before(comment[k]);
                }
            });
            moveTopOfPage();
        }
    };

    $scope.getSourceCode = function(){
        var ht = {};

        for(var j = 0; j < $scope.defectSourceCodes.length; j++) {
            var sourceCode = $scope.defectSourceCodes[j];
            if (sourceCode.modulePath == $scope.currentDetailModulePath && sourceCode.fileName == $scope.currentDetailFileName)  {
                $scope.src = sourceCode.source.replace(/</g, "&lt;").replace(/>/g, "&gt;");
                ht = "<pre class='prettyprint linenums'>" + $scope.src + "</pre>";
                angular.element('#SourceCodeTab').html(ht);
                prettyPrint();
            }
        }
        $scope.addDefectComment();
    };

    $scope.currentDefectList = [];

    function uncomma(str) {
        str = String(str);
        return "" + str.replace(/,/g, '. ');
    }

    var setExportFileFormat = function(format, results){
        switch(format){
            case 'defect':
                for(var i =0 ; i < results.length ; i++){

                    if(i == 0){
                        $scope.csvContent = $scope.csvContent + 'Did,Checker,Count,Line No,Severity,Status, Module,File,Class,Method/Function,Language,Tool,Author,Date' + '\n' ;
                    }
                    results[i].className = uncomma(results[i].className);
                    $scope.currentDefectList[i] = results[i].did + ',' +  results[i].checkerCode + ',' + results[i].occurenceCount + ','+ results[i].occurenceLine + ','
                        + results[i].severityCode + ',' + results[i].statusCode + ',' + results[i].modulePath + ',' + results[i].fileName + ',' + results[i].className + ','
                        + results[i].methodName + ','+ results[i].language + ','+ results[i].toolName +','+results[i].creatorId + ','+ results[i].modifiedDateTime ;
                    $scope.csvContent = $scope.csvContent + $scope.currentDefectList[i] + '\n' ;
                }
                break;
            case 'snapshot':
                for(var i =0 ; i < results.length ; i++){
                    if(i == 0){
                        $scope.csvContent = $scope.csvContent + 'Did,Checker,Count,Line No,Severity,Snapshot Status,Current Status,Module,File,Class,Method/Function,Language,Tool,Author,Date' + '\n' ;
                    }
                    results[i].className = uncomma(results[i].className);
                    $scope.currentDefectList[i] = results[i].did + ',' +  results[i].checkerCode + ',' + results[i].occurenceCount + ','+ results[i].occurenceLine + ','
                        + results[i].severityCode + ',' + results[i].statusCode + ',' + results[i].currentStatusCode + ','+ results[i].modulePath + ',' + results[i].fileName + ',' + results[i].className + ','
                        + results[i].methodName + ','+ results[i].language + ','+ results[i].toolName +','+ results[i].creatorId + ','+ results[i].createdDateTime ;
                    $scope.csvContent = $scope.csvContent + $scope.currentDefectList[i] + '\n' ;
                }
                break;
        }

    };

    // This must be a hyperlink
    $(".exportDefect").on('click', function (event) {
        // Data URI
        $scope.csvContent = "data:text/csv;charset=utf-8,";
        var format = 'defect';
        if ($routeParams.snapshotId !== undefined) {
            var format = 'snapshot';

        }
        setExportFileFormat(format, $scope.defectList);
        exportExcelFile.apply(this, [$scope.csvContent, 'export.csv']);
    });

    $scope.exportFileTreeToCSV = function(){

        // Data URI
        $scope.csvContent = "data:text/csv;charset=utf-8,";
        var format = 'defect';
        if ($routeParams.snapshotId !== undefined) {
            var format = 'snapshot';

        }
        setExportFileFormat(format, $scope.defectList);
        exportExcelFile();
    };

    var exportExcelFile = function (){
        var encodedUri = encodeURI($scope.csvContent);
        $(this)
            .attr({
                'download': $scope.projectName +'_defectList.csv',
                'href': encodedUri,
                'target': '_blank'
            });

    };

});

