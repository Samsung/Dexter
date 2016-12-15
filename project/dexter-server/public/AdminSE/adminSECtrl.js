"use strict";
adminSEApp.controller('adminSECtrl', function ($scope, $http, $location, $routeParams, $log) {
    const NONE_MODULE_PATH = 'undefined';

    $('.did-select').on('select2:select', function (event) {
        addDidList(event);
        changeSelectedDidCount();
    });

    $('.module-select').on('select2:select', function (event) {
        addModulePathList(event);
        changeSelectedModuleCount();
    });

    $('.did-select').on('select2:unselect', function (event) {
        removeDidList(event);
        changeSelectedDidCount();
    });

    $('.module-select').on('select2:unselect', function (event) {
        removeModulePathList(event);
        changeSelectedModuleCount();
    });

    function changeSelectedModuleCount() {
        const count = parseInt($scope.selectedModulePathList.length);
        $("#removeModuleCount").html(count);
        if (count > 0) {
            $("#isModuleSelected").removeClass("fa fa-folder").addClass("fa fa-folder-open");
        } else {
            $("#isModuleSelected").removeClass("fa fa-folder-open").addClass("fa fa-folder");
        }
    }

    function changeSelectedDidCount() {
        const count = parseInt($scope.selectedDidList.length);
        $("#removeDidCount").html(count);
        if (count > 0) {
            $("#isDidSelected").removeClass("fa fa-minus-square").addClass("fa fa-bug");
        } else {
            $("#isDidSelected").removeClass("fa fa-bug").addClass("fa fa-minus-square");
        }
    }

    function addModulePathList(event) {
        if (!event) {
            return;
        }
        JSON.stringify(event.params, function (key, value) {
            $scope.selectedModulePathList.push(value.data.id);
        });
    }

    function removeModulePathList(event) {
        if (!event) {
            return;
        }

        JSON.stringify(event.params, function (key, value) {
            $scope.selectedModulePathList.pop(value.data.id);
        });
    }

    function addDidList(event) {
        if (!event) {
            return;
        }

        JSON.stringify(event.params, function (key, value) {
            $scope.selectedDidList.push(value.data.id);
        });
    }

    function removeDidList(event) {
        if (!event) {
            return;
        }

        JSON.stringify(event.params, function (key, value) {
            $scope.selectedDidList.pop(value.data.id);
        });
    }


    var init = function () {
        showLoadingImage();
        $scope.modulePathList = [];
        $scope.selectedModulePathList = [];

        $scope.didList = [];
        $scope.selectedDidList = [];

        $(".did-select").select2({});
        $('select').trigger('change.did-select'); // Notify only Select2 of changes

        $(".module-select").select2({});
        $('select').trigger('change.module-select'); // Notify only Select2 of changes
        hideLoadingImage();
    };

    init();

    function showLoadingImage() {
        angular.element('#showLoading').show();
    }

    function hideLoadingImage() {
        angular.element('#showLoading').hide();
    }

    $scope.getDidList = function () {
        showLoadingImage();
        const getModulePathListUrl = '/api/v3/did-list';

        $http.get(getModulePathListUrl, {}).then((result) => {
            if (isHttpResultOK(result)) {
                $scope.didList = [];
                angular.forEach(result.data.rows, (index) => {
                    $scope.didList.push(index.did);
                });

                $(".did-select").select2({
                    data: $scope.didList
                });
                hideLoadingImage();
            }
        }, function (results) {
            $log.error('Error code:' + results.status + ';');
        });
    };

    $scope.getModulePathList = function () {
        showLoadingImage();
        const getModulePathListUrl = '/api/v2/module-path-list';

        $http.get(getModulePathListUrl, {}).then(function (result) {
            if (isHttpResultOK(result)) {
                hideLoadingImage();
                $scope.modulePathList = [];
                angular.forEach(result.data.rows, function (index) {
                    $scope.modulePathList.push(index.modulePath);
                });
                $scope.modulePathList.push(NONE_MODULE_PATH);

                $(".module-select").select2({
                    data: $scope.modulePathList
                });
                hideLoadingImage();
            }
        }, function (results) {
            $log.error('Error code:' + results.status + ';');
        });
    };

    $scope.deleteModulePathList = function () {
        alert("Defect can not restore if defect is once deleted in modulePath that you selected.");
        showLoadingImage();
        var deleteModulePathListUrl = '/api/v2/module-path-list';
        $http.delete(deleteModulePathListUrl, {
            params: {
                "modulePathList": $scope.selectedModulePathList.toString(),
                "modulePathListLength": $scope.selectedModulePathList.length
            }
        }).then((result) => {
            if (isHttpResultOK(result)) {
                hideLoadingImage();
                alert("Your removal request is completed.");
                location.reload(true);
            }
        }, function (result) {
            hideLoadingImage();
            alert("The Delete request failed, please contact to SE admin.");
            $log.error(`Error code: ${result.status}`);
        });
    };

    $scope.deleteDidList = function () {
        alert("Defect can not restore if defect is once deleted that you selected.");
        showLoadingImage();
        var deleteDidListUrl = '/api/v3/did-list';
        $http.delete(deleteDidListUrl, {
            params: {
                "didList": $scope.selectedDidList.toString(),
                "didListLength": $scope.selectedDidList.length
            }
        }).then((result)=> {
            hideLoadingImage();
            if (isHttpResultOK(result)) {
                alert("Your removal request is completed.");
                location.reload(true);
                return;
            }
            alert("The Delete request failed, please contact to SE admin.");
        }, (result)=> {
            hideLoadingImage();
            alert("The Delete request failed, please contact to SE admin.");
            $log.error(`Error code: ${result.status}`);
        })
    }
});