adminSEApp.controller('adminSECtrl', function($scope, $http, $location ,$routeParams, $log) {
    "use strict";

    $('select').on('select2:select', function (e) {
        addModulePathList(e);
        changeSelectedModuleCount();
    });

    $('select').on('select2:unselect', function (e) {
        removeModulePathList(e);
        changeSelectedModuleCount();
    });

    function changeSelectedModuleCount(){
        var count = $scope.selectedModulePathList.length;
        $("#removeModuleCount").html(parseInt(count));
        if(count > 0) {
            $("#isModuleSelected").removeClass("fa fa-folder").addClass("fa fa-folder-open");
        }
        if(count==0){
            $("#isModuleSelected").removeClass("fa fa-folder-open").addClass("fa fa-folder");
        }
    }

    function addModulePathList(evt){
        if (!evt) { return; }
        JSON.stringify(evt.params, function (key, value) {
            $scope.selectedModulePathList.push(value.data.id);
        });
    }

    function removeModulePathList(evt){
        if (!evt) { return; }

        JSON.stringify(evt.params, function (key, value) {
            $scope.selectedModulePathList.pop(value.data.id);
        });
    }

    var init = function(){
        $scope.modulePathList = [];
        $scope.selectedModulePathList = [];
        $(".select2").select2({});
        $('select').trigger('change.select2'); // Notify only Select2 of changes
    };

    init();

    var getModulePathListUrl ='/api/v2/adminSE/modulePath';
    $http.get(getModulePathListUrl, {
    }).then(function(result){
        if(isHttpResultOK(result)){
            $scope.modulePathList = [];
            angular.forEach(result.data.rows, function(idx){
                $scope.modulePathList.push(idx.modulePath);
            });

            $(".select2").select2({
                data : $scope.modulePathList
            });
        }
    }, function(results){
        $log.error('Error code:' + results.status+';');
    });



    $scope.deleteModulePathList = function(){
        alert("It can not restore if it is once deleted.");
        var deleteModulePathListUrl = '/api/v2/adminSE/deleteModulePath';
        $http.delete(deleteModulePathListUrl, {
                params : {
                    "modulePathList": $scope.selectedModulePathList.toString(),
                    "modulePathListLength" : $scope.selectedModulePathList.length
                }
            }
        ).then(function(result){
                if(isHttpResultOK(result)){
                    location.reload(true);
                }
            })
    }, function(results){
        $log.error('Error code:' + results.status+';');
    };
});