functionMetricsApp.controller('functionMetricsCtrl', function($scope, $http,  $location,$routeParams, $log) {
    "use strict";

    $scope.functionMetrics=[];
    $scope.fileName = '';
    $scope.modulePath = '';


    var url = $location.absUrl().split('?');

    getFileInfo(url);
    function loadMetricForAllFiles(){
        var loadMetricsForAllFilesURl = "/api/v2/functionMetrics/All";
        $http.get(loadMetricsForAllFilesURl,{
        }).then(function(results){
            if(isHttpResultOK(results)){
                var functionMetrics = results.data.result;
                makeArrayData(functionMetrics);

                $("#FunctionMetricsTable").DataTable({
                    "lengthMenu": [[5, 10, 25, -1], [5, 10, 25, "All"]],
                    "destroy" : true,
                    "data": $scope.functionMetricsArray,
                    "columns":[
                        {"title":"FunctionName"},
                        {"title":"CC(Cyclomatic Complexity)"},
                        {"title":"SLOC"}
                    ]
                });
            }
        });


    }

    function getFileInfo(url){
        if(url[1] == null || url[1] == undefined){
            loadMetricForAllFiles();
            return;
        }
        var fileInfo = url[1].split('&');
        var fileName = fileInfo[0].split('=')[1];
        var modulePath = fileInfo[1].split('=')[1];
        $scope.fileName=fileName;
        setTitle(fileName);
        $scope.modulePath= modulePath;

        if(fileInfo[2] == null || fileInfo[2]==undefined){
            $scope.functionList = [];
            return ;
        }
        var tmpFunctionList = fileInfo[2].split('=')[1];
        var functionList = tmpFunctionList.split(',');


        $scope.functionList= functionList;
        loadMetricsForEachFile();
    }

    function setTitle(fileName){
        $("#fileNameForFunctionView").html("FileName : " + fileName);
    }

    function loadMetricsForEachFile() {
        var functionMetricUrl = '/api/v2/functionMetrics';
        $http.post(functionMetricUrl, {
            params: {
                fileName: $scope.fileName,
                modulePath: $scope.modulePath,
                functionList: $scope.functionList
            }
        }).then(function (results) {
            if (isHttpResultOK(results)) {
                var functionMetrics = results.data.result;
                makeArrayData(functionMetrics);

                $("#FunctionMetricsTable").DataTable({
                    "lengthMenu": [[5, 10, 25, -1], [5, 10, 25, "All"]],
                    "destroy": true,
                    "data": $scope.functionMetricsArray,
                    "columns": [
                        {"title": "FunctionName"},
                        {"title": "CC(Cyclomatic Complexity)"},
                        {"title": "SLOC"}
                    ]
                });
            }
        });

    }
    /*
    var getfunctionMetricsUrl = '/api/v2/functionMetrics/?fileName='+$scope.fileName+'&modulePath='+$scope.modulePath+'&functionLIst='+$scope.functionList;

    $http.get(getfunctionMetricsUrl,{
    }).then(function(results){
        if(isHttpResultOK(results)){
            var functionMetrics = results.data.result;
            makeArrayData(functionMetrics);

            $("#FunctionMetricsTable").DataTable({
                "lengthMenu": [[5, 10, 25, -1], [5, 10, 25, "All"]],
                "data": $scope.functionMetricsArray,
                "columns":[
                    {"title":"FunctionName"},
                    {"title":"CC(Cyclomatic Complexity)"},
                    {"title":"SLOC"}
                ]
            });
        }
   });*/

    function makeArrayData(functionMetrics){
        $scope.functionMetricsArray = [];
        var tempArr = new Array();
        for(var i=0; i< functionMetrics.length; i++){
            var tempNewArr = new Array(functionMetrics[i].functionName, functionMetrics[i].cc,functionMetrics[i].sloc );
            tempArr.push (tempNewArr);
        }
        $scope.functionMetricsArray = tempArr;
    }

    $scope.refresh = function(){
    };


});
