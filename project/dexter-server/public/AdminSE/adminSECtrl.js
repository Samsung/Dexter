adminSEApp.controller('adminSECtrl', function($scope, $http, $location ,$routeParams, $log) {
    "use strict";

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
        $scope.modulePathList = [];
        $scope.selectedModulePathList = [];
        $(".select2").select2({});
        $('select').trigger('change.select2'); // Notify only Select2 of changes
    };

    init();

    var getModulePathListUrl ='/api/v2/module-path-list';
    $http.get(getModulePathListUrl, {
    }).then(function(result){
        if(isHttpResultOK(result)){
            console.log(result.data.rows.length);
            $scope.modulePathList = [];
            angular.forEach(result.data.rows, function(index){
                $scope.modulePathList.push(index.modulePath);
            });

            $(".select2").select2({
                data : $scope.modulePathList
            });
        }
    }, function(results){
        $log.error('Error code:' + results.status+';');
    });



    $scope.deleteModulePathList = function(){
        alert("Defect can not restore if defect is once deleted in modulePath that you selected.");
        var deleteModulePathListUrl = '/api/v2/module-path-list';
        $http.delete(deleteModulePathListUrl, {
            params : {
                "modulePathList": $scope.selectedModulePathList.toString(),
                "modulePathListLength" : $scope.selectedModulePathList.length
            }
        }).then(function(result){
            if(isHttpResultOK(result)){
                alert("Your selected module path data has been wiped.");
                location.reload(true);
            }
        }, function(result){
            alert("The Delete request failed, please contact to SE admin.");
            $log.error('Error code:' + result.status+';');
        });
    }
});