"use strict";
adminSEApp.controller('adminSECtrl', function($scope, $http, $location ,$routeParams, $log) {
    const NONE_MODULE_PATH = 'undefined';

    $('select').on('select2:select', function (event) {
        addModulePathList(event);
        changeSelectedModuleCount();
    });

    $('select').on('select2:unselect', function (event) {
        removeModulePathList(event);
        changeSelectedModuleCount();
    });

    function changeSelectedModuleCount(){
        var count = $scope.selectedModulePathList.length;
        $("#removeModuleCount").html(parseInt(count));
        if(count > 0) {
            $("#isModuleSelected").removeClass("fa fa-folder").addClass("fa fa-folder-open");
        }else{
            $("#isModuleSelected").removeClass("fa fa-folder-open").addClass("fa fa-folder");
        }
    }

    function addModulePathList(event){
        if (!event) { return; }
        JSON.stringify(event.params, function (key, value) {
            $scope.selectedModulePathList.push(value.data.id);
        });
    }

    function removeModulePathList(event){
        if (!event) { return; }

        JSON.stringify(event.params, function (key, value) {
            $scope.selectedModulePathList.pop(value.data.id);
        });
    }

    var init = function(){
        isShownLoadingImage();
        $scope.modulePathList = [];
        $scope.selectedModulePathList = [];
        $(".select2").select2({});
        $('select').trigger('change.select2'); // Notify only Select2 of changes
        getModulePathList();
    };

    init();

    function isShownLoadingImage(){
        angular.element('#showLoading').show();
    }

    function isHiddenLoadingImage(){
        angular.element('#showLoading').hide();
    }

    function getModulePathList(){
        const  getModulePathListUrl ='/api/v2/module-path-list';

        $http.get(getModulePathListUrl, {
        }).then(function(result){
            if(isHttpResultOK(result)){
                isHiddenLoadingImage();
                $scope.modulePathList = [];
                angular.forEach(result.data.rows, function(index){
                    $scope.modulePathList.push(index.modulePath);
                });
                $scope.modulePathList.push(NONE_MODULE_PATH);

                $(".select2").select2({
                    data : $scope.modulePathList
                });
            }
        }, function(results){
            $log.error('Error code:' + results.status+';');
        });
    }

    $scope.deleteModulePathList = function(){
        alert("Defect can not restore if defect is once deleted in modulePath that you selected.");
        isShownLoadingImage();
        var deleteModulePathListUrl = '/api/v2/module-path-list';
        $http.delete(deleteModulePathListUrl, {
            params : {
                "modulePathList": $scope.selectedModulePathList.toString(),
                "modulePathListLength" : $scope.selectedModulePathList.length
            }
        }).then(function(result){
            if(isHttpResultOK(result)){
                isHiddenLoadingImage();
                alert("Your removal request is completed.");
                location.reload(true);
            }
        }, function(result){
            alert("The Delete request failed, please contact to SE admin.");
            $log.error('Error code:' + result.status+';');
        });
    }
});