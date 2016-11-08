codeMetricsApp.controller('codeMetricsCtrl', function($scope, $http, $location ,$routeParams, $log) {
    "use strict";

    $scope.fileName='';
    $scope.modulePath = '';
    $scope.codeMetricsQuantity = '';
    $scope.codeMetricsThreshold ='';

    var NOCODEMETRICSMSG="<div class='icon' ><i class='fa fa-exclamation-circle'></i></div> "
        +"<b>There is no analysis information in the selected file to display.</b>"
        +"<br> Use the Dexter Analyze option to start using this code metrics view.";

    var DEFUALTCODEMETRICSMSGForFile="<i class='fa fa-check'></i>&nbsp;&nbsp;<b>";
    var DEFAULTCODEMETRICSMSGForModule="<i class='fa  fa-folder-open-o'></i>&nbsp;&nbsp;";

    var url = $location.absUrl().split('?');

    $scope.isShownType = "fileCodeMetrics";

    getFileInfo(url);

    function getFileInfo(url){
        if(url[1] === undefined){
            $scope.isShownType = "totalCodeMetrics";
            $scope.fileName="Total Code Metrics in this Project";
            $scope.modulePath= "None";
        }else{
            $scope.isShownType = "fileCodeMetrics";
            var fileInfo = url[1].split('&');
            var fileName = fileInfo[0].split('=')[1];
            var modulePath = fileInfo[1].split('=')[1];
            $scope.fileName=fileName;
            $scope.modulePath= modulePath;
        }
    }

    function AddComma(data_value) {
        return Number(data_value).toLocaleString('en').split(".")[0];
    }

    var getCodeMetricsThreshold = function(callback){
        $http.get('/api/v1/codeMetrics/threshold', {
        }).then(function(result){
            if(isHttpResultOK(result)){
                $scope.codeMetricsThreshold = result.data.result;
                callback($scope.codeMetricsThreshold);
            }
        }, function (results){
            $log.error('Error code:' + results.status +';');
        });
    };

   /* var getCodeMetricsQuantity = function(){
        $http.get('/api/v1/codeMetrics/quantity', {
        }).then(function(result){
            if(isHttpResultOK(result)){
                return $scope.codeMetricsQuantity = result;
            }
        }, function(error){
            $log.error('Error code:' + error.status +';');
        });
    };*/

    getCodeMetricsThreshold(function(threshold){
        switch($scope.isShownType){
            case 'totalCodeMetrics':
                var getTotalCodeMetricsUrl = '/api/v1/codeMetrics/total';
                loadTotalCodeMetrics(getTotalCodeMetricsUrl);
                break;
            case 'fileCodeMetrics':
                var getCodeMetricsUrl = '/api/v1/codeMetrics/?fileName='+$scope.fileName+'&modulePath='+$scope.modulePath;
                loadCodeMetrics(threshold, getCodeMetricsUrl);
                break;
            default:
                break;
        }

    });

    var loadTotalCodeMetrics = function(getTotalCodeMetricsUrl){
        $http.get(getTotalCodeMetricsUrl, {
        }).then(function(result){
            if(isHttpResultOK(result)){
                if(result.data.result.length === 0){
                    setTitle(NOCODEMETRICSMSG, 'None');
                    codeMetricsViewHidden();
                }else{
                    var tempResult = result.data.result[0];
                    setTitle(DEFUALTCODEMETRICSMSGForFile + $scope.fileName, DEFAULTCODEMETRICSMSGForModule+$scope.modulePath );
                    var tempThreshold = setTotalThreshold(tempResult, tempResult.fileCount);
                    var totalResult = setTotalResult(tempResult);
                    getCodeMetrics(totalResult, tempThreshold);
                }
            }
        }, function(results){
            $log.error('Error code:' + results.status+';');
        })

    };

    function setTotalResult(result){
        var totalResult = [{
            metricName  : "sloc", metricValue : result.sloc
        },{
            metricName  : "classCount", metricValue : result.classCount
        },{
            metricName  : "avgComplexity", metricValue : result.avgComplexity
        },{
            metricName  : "methodCount", metricValue : result.methodCount
        }];

        return totalResult;
    }


    function setTotalThreshold(result, fileCount){
        var tempCCModerate = parseInt(result.cc) * parseInt(fileCount);
        var tempCCCaution = parseInt(result.cc) * parseInt(fileCount);
        var threshold= {
            "cc":{
                "moderate" : tempCCModerate,
                "caution" : tempCCCaution
            },
            "sloc": result.cc * fileCount,
            "method" :result.cc * fileCount,
            "class" : result.cc * fileCount
        };

        return threshold;

    }

    var loadCodeMetrics = function(threshold, getCodeMetricsUrl){
        $http.get(getCodeMetricsUrl, {
        }).then(function(result){
            if(isHttpResultOK(result)){
                if(result.data.result.length === 0){
                    setTitle(NOCODEMETRICSMSG, 'None');
                    codeMetricsViewHidden();
                }else{
                    var currentFileName= result.data.result[0].fileName;
                    var currentModulePath = result.data.result[0].modulePath;
                    setTitle(DEFUALTCODEMETRICSMSGForFile + currentFileName, DEFAULTCODEMETRICSMSGForModule+currentModulePath );
                    getCodeMetrics(result.data.result,threshold);
                }
            }
        }, function (results){
            $log.error('Error code:' + results.status +';');
        });
    };

    function setTitle(fileName, modulePath){
        $('#titleFileName').html(fileName);
        $('#titleModulePath').html(modulePath);
    }

    function codeMetricsViewHidden(){
        $('#codeMetricsView').html("");
    }


    function setIdHtml(codeMetricName, value, msg){
        $('#'+codeMetricName+'Id').html(value+"<sup style='font-size: 23px'>&nbsp;&nbsp;<b>"+msg+"</b></sup>");
    }

    function getCodeMetrics(result, thresholdList){
        for(var i =0; i< result.length ; i++){
            var value="";
            var type ="";
            var idx = "";
            var msg ="";
            var threshold="";
            var warnMsg= "";

            switch (result[i].metricName) {
                case 'classCount':
                    idx = i;
                    type = 'class';
                    threshold = thresholdList.class;
                    msg = 'CLASS Count';
                    value = result[i].metricValue;
                    warnMsg="Too many CLASS in this file!";
                    break;

                case 'avgComplexity':
                case 'averageComplexity':
                    idx = i;
                    type = 'cc';
                    threshold = thresholdList.cc.caution;
                    msg ='CYCLOMATIC COMPLEXITY(CC)';
                    value = result[i].metricValue;
                    warnMsg = "Too high Cyclomatic Complexity.";
                    break;

                case 'methodCount':
                    idx = i;
                    type = 'method';
                    threshold = thresholdList.method;
                    msg ='METHOD Count';
                    value = result[i].metricValue;
                    warnMsg = "Too many METHOD in this file!";
                    break;

                case 'sloc':
                    idx = i;
                    type = 'sloc';
                    threshold = thresholdList.sloc;
                    msg = 'SLOC';
                    value = result[i].metricValue;
                    warnMsg = "In large LOC makes a code complicated.";
                    break;

                default:
                    break;
            }

            if(type==='cc' || type=='class'){
                thresholdAlarm(type, threshold);
                if(isAttention(value, threshold)){
                    attentionCSS(type);
                }else if(isAlarm(value, threshold)){
                    alarmCSS(type, warnMsg);
                }
            }
            setIdHtml(type, value, msg);


        }
    }

    function isAttention(value, threshold){
        return (parseInt(value) > parseInt(threshold * 0.7 )  && parseInt(value) <= parseInt(threshold));
    }

    function isAlarm(value, threshold){
       return (parseInt(value) > parseInt(threshold));
    }

    function thresholdAlarm(codeMetricName, threshold){
        $('#'+codeMetricName+'Threshold').html(" Threshold is " + AddComma(threshold));
    }

    function alarmCSS(codeMetricName, msg){
        $('#'+codeMetricName+'Id').css("color", 'black');
        $('#'+codeMetricName+'Class').removeClass().addClass('small-box bg-red');
        $('#'+codeMetricName+'FirLine')
            .css("color", 'black')
            .html("<b><i class='fa  fa-warning'></i>  "+msg+"</b>").css("color", 'black');
        $('#'+codeMetricName+'Icon').removeClass().addClass("fa fa-thumbs-o-down");
    }

    function attentionCSS(codeMetricName) {
        $('#' + codeMetricName + 'Id').css("color", 'black');
        $('#' + codeMetricName + 'Class').removeClass().addClass('small-box bg-orange');
        $('#' + codeMetricName + 'FirLine')
            .css("color", 'black')
            .html("<b><i class='fa fa-search'></i>  This require immediate attention</b>: On the threshold!");
        $('#' + codeMetricName + 'Icon').removeClass().addClass("fa fa-hand-o-left");
    }

});