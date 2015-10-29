dashboardApp.controller("DashboardCtrl", function($scope, $http, $log){
    "use strict";

    $http.get('/api/v1/projectName', {
    }).then(function (result) {
        if( result && result.data){
            var html = 'Dashboard : ' +result.data.result;
            $scope.projectName = result.data.result;
            $('#indexDashboardTitle').html(html);
        }
    }, function (results) {
        $log.error(results);
    });

    var _developerRowChart = dc.rowChart("#developer-div");
    var _moduleRowChart = dc.rowChart("#module-row-div");
    var _moduleBubbleChart = dc.bubbleChart('#module-defect-div');
    var _fileBubbleChart = dc.bubbleChart('#file-defect-div');
    var _checkerRowChart= dc.rowChart("#checker-status-div");
    var _fileDetailTableChart = dc.dataTable("#file-detail-table");
    var _defectStatusPieChart = dc.pieChart("#defect-status-div");

    var _moduleDimension;
    var _fileDimension;
    var _fileBubbleDimension;
    var _statusDimension;
    var _developerDimension;
    var _checkerDimension;
    var _checkerModuleDimension;
    var _checkerFileDimension;
    var _checkerStatusDimension;
    var _metricStatusDimension;

    var _titleGroup;
    var _moduleDimensionGroup;
    var _defectiveModuleDimensionGroup;
    var _fileDimGroup;
    var _checkerDimensionGroup;
    var _developerDimensionGroup;
    var _statusDimensionGroup;

    var _titleDuration = 500;
    var _chartDuration = 800;

    var _totalDefectCount = 0;
    var _newDefectCount = 0;
    var _fixDefectCount = 0;
    var _excDefectCount = 0; // exc: exception

    var _heightPadding = 8;  // px
    var _widthPadding = 100;
    var _fontHeight = 25;
    var _titleHeight = 100;
    var _baseHeight = 300;

    var _checkerVsModule;
    var _developerVsChecker = {
        developerIds:[],
        data: [],
        add: function (id, checkerCode){
            var checkerCodes;
            var index = this.developerIds.indexOf(id);

            if(index < 0){
                this.developerIds.splice(0,0,id);
                checkerCodes = [];
                checkerCodes.splice(0,0,checkerCode);
                this.data.splice(0,0,checkerCodes);
            } else {
                checkerCodes = this.data[index];
                if(checkerCodes.indexOf(checkerCode) < 0){
                    checkerCodes.splice(0,0,checkerCode);
                    this.data[index] = checkerCodes;
                }
            }
        },
        getCheckerCodes: function(developerId){
            var index = this.developerIds.indexOf(developerId);
            return this.data[index];
        }
    };

    var _fileBubbleChartSearchOptions = {
        'limitSize': 100,
        'modulePath' : '',
        'defectStatus': ''
    };

    initialize();

    function initialize(){
        var developerVsModule;
        initFileListTable();

        d3.json("/api/v1/metrics-and-defect", handleMetricsAndDefect);

        function handleMetricsAndDefect (data) {
            var defectFilter = crossfilter(data);
            $scope.fileList = data;

            calculateDefectCount(data);
            createModuleDimensionAndGroup(defectFilter);
            createFileDim(defectFilter);
            createMetricsStatusDim(defectFilter);

            d3.json("/api/v1/developer-and-file", handleDeveloperAndFile);
        }

        function handleDeveloperAndFile (developerData){
            developerVsModule = developerData;
            var developerFilter = crossfilter(developerData);
            createDeveloperDimAndGroup(developerFilter);

            d3.json("/api/v1/checker-and-defect", handleCheckerAndDefect);
        }

        function handleCheckerAndDefect (checkerData){
            _checkerVsModule = checkerData;
            var checkerFilter = crossfilter(checkerData);

            createDeveloperVsChecker(developerVsModule, checkerData);
            createCheckerDimAndGroups(checkerFilter);

            initCharts();
            drawCharts();

            $(window).resize(drawCharts);
        }
    }

    function initFileListTable(){
        createFileListTable();
        $scope.isHideFileList = true;
        $scope.toggleShowFileListTitle = 'Show All File Information';

        $scope.toggleShowFileList = function(){
            if($scope.isHideFileList === true){
                $scope.isHideFileList = false;
                $scope.toggleShowFileListTitle = 'Hide All File Information';
                $scope.gridOptions.ngGrid.buildColumns();

            } else {
                $scope.isHideFileList = true;
                $scope.toggleShowFileListTitle = 'Show All File Information';
                $scope.gridOptions.ngGrid.buildColumns();
            }
        };
    }

    function calculateDefectCount(data){
        _totalDefectCount = 0;
        _newDefectCount = 0;
        _fixDefectCount = 0;
        _excDefectCount = 0;

        data.forEach(function(item){
            _totalDefectCount += item.totalCnt;
            _newDefectCount += item.newCnt;
            _fixDefectCount += item.fixCnt;
            _excDefectCount += item.excCnt;
        });
    }

    function createModuleDimensionAndGroup(defectFilter){
        createTitleGroup(defectFilter);
        createModuleDimension(defectFilter);
        createModuleDimensionGroup();
        createDefectiveModuleDimension();
    }

    function createTitleGroup(defectFilter){
        _titleGroup = defectFilter.groupAll().reduce(reduceAdd, reduceRemove, reduceInit);

        function reduceAdd (field, item) {
            field.totalCnt += item.totalCnt;
            field.criticalCnt += item.criticalCnt;
            field.cnc += item.cnc;
            field.cfc += item.cfc;
            field.cec += item.cec;
            field.majorCnt += item.majorCnt;
            field.mnc += item.mnc;
            field.mfc += item.mfc;
            field.mec += item.mec;
            field.minorCnt += item.minorCnt;
            field.nnc += item.nnc;
            field.nfc += item.nfc;
            field.nec += item.nec;
            field.crcCnt += item.crcCnt;
            field.rnc += item.rnc;
            field.rfc += item.rfc;
            field.rec += item.rec;
            field.etcCnt += item.etcCnt;
            field.enc += item.enc;
            field.efc += item.efc;
            field.eec += item.eec;
            field.loc += item.loc;
            field.sloc += item.sloc;
            field.classCnt += item.classCnt;
            field.methodCnt += item.methodCnt;
            field.fileCnt += 1;
            field.newCnt += item.newCnt;
            field.fixCnt += item.fixCnt;
            field.excCnt += item.excCnt;
            return field;
        }

        function reduceRemove (field, item) {
            field.totalCnt -= item.totalCnt;
            field.criticalCnt -= item.criticalCnt;
            field.cnc -= item.cnc;
            field.cfc -= item.cfc;
            field.cec -= item.cec;
            field.majorCnt -= item.majorCnt;
            field.mnc -= item.mnc;
            field.mfc -= item.mfc;
            field.mec -= item.mec;
            field.minorCnt -= item.minorCnt;
            field.nnc -= item.nnc;
            field.nfc -= item.nfc;
            field.nec -= item.nec;
            field.crcCnt -= item.crcCnt;
            field.rnc -= item.rnc;
            field.rfc -= item.rfc;
            field.rec -= item.rec;
            field.etcCnt -= item.etcCnt;
            field.enc -= item.enc;
            field.efc -= item.efc;
            field.eec -= item.eec;
            field.loc -= item.loc;
            field.sloc -= item.sloc;
            field.classCnt -= item.classCnt;
            field.methodCnt -= item.methodCnt;
            field.fileCnt -= 1;
            field.newCnt -= item.newCnt;
            field.fixCnt -= item.fixCnt;
            field.excCnt -= item.excCnt;
            return field;
        }

        function reduceInit () {
            return {
                totalCnt:0,
                criticalCnt:0,
                cnc:0,
                cfc:0,
                cec:0,
                majorCnt:0,
                mnc:0,
                mfc:0,
                mec:0,
                minorCnt:0,
                nnc:0,
                nfc:0,
                nec:0,
                crcCnt:0,
                rnc:0,
                rfc:0,
                rec:0,
                etcCnt:0,
                enc:0,
                efc:0,
                eec:0,
                loc:0,
                sloc:0,
                classCnt:0,
                methodCnt:0,
                fileCnt:0,
                newCnt:0,
                fixCnt:0,
                excCnt:0
            };
        }
    }

    function createModuleDimension(defectFilter){
        _moduleDimension = defectFilter.dimension(function (dimension) {
            return dimension.modulePath;
        });
    }

    function createModuleDimensionGroup(){
        _moduleDimensionGroup = _moduleDimension.group().reduce(
            function (field, item) { // add
                field.totalCntSum += item.totalCnt;
                field.classCntSum += item.classCnt;
                field.methodCntSum += item.methodCnt;
                field.newCntSum += item.newCnt;
                field.fixCntSum += item.fixCnt;
                field.excCntSum += item.excCnt;
                field.locSum += item.sloc;
                return field;
            },
            function(field, item) { // remove
                field.totalCntSum -= item.totalCnt;
                field.classCntSum -= item.classCnt;
                field.methodCntSum -= item.methodCnt;
                field.newCntSum -= item.newCnt;
                field.fixCntSum -= item.fixCnt;
                field.excCntSum -= item.excCnt;
                field.locSum -= item.sloc;
                return field;
            },
            function() { // field init
                return {totalCntSum:0, locSum:0, classCntSum:0, methodCntSum:0, fixCntSum:0, excCntSum:0, newCntSum:0}
            }
        );
    }

    function createDefectiveModuleDimension(){
        _defectiveModuleDimensionGroup = {
            all: function(){
                return _moduleDimensionGroup.all().filter(function(data){
                    return data.value.totalCntSum > 0;
                })
            }
        }
    }

    function createFileDim(defectFilter){
        _fileDimension = defectFilter.dimension(function (dimension) {
            return dimension.fileName;
        });
    }

    function createMetricsStatusDim(defectFilter){
        _metricStatusDimension = defectFilter.dimension(function (d) {
            var hasNew = d.newCnt > 0;
            var hasFix = d.fixCnt > 0;
            var hasExc = d.excCnt > 0;

            return { hasNew:hasNew, hasFix:hasFix, hasExc:hasExc};
        });
    }

    function createDeveloperDimAndGroup(developerFilter){
        createDeveloperDim();
        createDeveloperDimGroup();

        function createDeveloperDim(){
            _developerDimension = developerFilter.dimension(function (dimension) {
                return dimension.developerId;
            });
        }

        function createDeveloperDimGroup(){
            _developerDimensionGroup = _developerDimension.group().reduce(
                function (field, item) {
                    field.fileNameCount ++;
                    field.accessTotalCount += item.count;
                    return field;
                },
                function(field, item) {
                    field.fileNameCount --;
                    field.accessTotalCount -= item.count;
                    return field;
                },
                function() {
                    return {fileNameCount:0, accessTotalCount:0};
                }
            );


        }
    }

    function createDeveloperVsChecker(developerVsModule, checkerVsModule){
        for(var i=0; i<developerVsModule.length; i++){
            var item = developerVsModule[i];

            var devId = item.developerId;
            var fileName = item.fileName;
            var modulePath = item.modulePath;

            for(var j=0; j<checkerVsModule.length; j++){
                var checkerItem = checkerVsModule[j];

                if(fileName === checkerItem.fileName && modulePath === checkerItem.modulePath){
                    _developerVsChecker.add(devId, checkerItem.checkerCode);
                }
            }
        }
    }

    function createCheckerDimAndGroups(checkerFilter){
        _checkerDimension = checkerFilter.dimension(function (d) {
            return d.checkerCode;
        });

        _checkerModuleDimension = checkerFilter.dimension(function (d) {
            return d.modulePath;
        });

        _checkerFileDimension = checkerFilter.dimension(function (d) {
            return d.fileName;
        });

        _statusDimension = checkerFilter.dimension(function (d) {
            return d.statusCode;
        });

        _checkerStatusDimension = checkerFilter.dimension(function (d) {
            return d.statusCode;
        });

        createCheckerDimGroup();
        createStatusDimGroup();
    }

    function createCheckerDimGroup(){
        _checkerDimensionGroup = _checkerDimension.group().reduce(
            function (field, item) {
                field.sum += item.count;

                if(item.severityCode == "CRI")  field.criticalCntSum += item.count;
                else if(item.severityCode == "MAJ") field.majorCntSum += item.count;
                else if(item.severityCode == "MIN") field.minorCntSum += item.count;
                else if(item.severityCode == "ETC") field.etcCntSum += item.count;
                if(item.statusCode == "NEW")    field.newCntSum += item.count;
                else if(item.statusCode == "FIX")   field.fixCntSum += item.count;
                else if(item.statusCode == "EXC")   field.excCntSum += item.count;

                return field;
            },
            function(field, item) {
                field.sum -= item.count;

                if(item.severityCode == "CRI")  field.criticalCntSum -= item.count;
                else if(item.severityCode == "MAJ") field.majorCntSum -= item.count;
                else if(item.severityCode == "MIN") field.minorCntSum -= item.count;
                else if(item.severityCode == "ETC") field.etcCntSum -= item.count;
                if(item.statusCode == "NEW")    field.newCntSum -= item.count;
                else if(item.statusCode == "FIX")   field.fixCntSum -= item.count;
                else if(item.statusCode == "EXC")   field.excCntSum -= item.count;

                return field;
            },
            function() {
                return {
                    sum:0, criticalCntSum:0, majorCntSum:0, minorCntSum:0, crcCntSum:0, etcCntSum:0,
                    newCntSum:0, fixCntSum:0, excCntSum:0
                };
            }
        );
    }

    function createStatusDimGroup(){
        _statusDimensionGroup = _statusDimension.group().reduce(
            function (field, item) {
                field.totalCntSum += item.count;

                if(item.statusCode == "NEW")    field.newCntSum += item.count;
                else if(item.statusCode == "FIX")   field.fixCntSum += item.count;
                else if(item.statusCode == "EXC")   field.excCntSum += item.count;

                return field;
            },
            function(field, item) {
                field.totalCntSum -= item.count;

                if(item.statusCode == "NEW")    field.newCntSum -= item.count;
                else if(item.statusCode == "FIX")   field.fixCntSum -= item.count;
                else if(item.statusCode == "EXC")   field.excCntSum -= item.count;

                return field;
            },
            function() {
                return {
                    totalCntSum:0, newCntSum:0, fixCntSum:0, excCntSum:0
                }
            }
        );
    }

    function initCharts(){
        createDeveloperRowChart();
        createModuleRowChart();
        createModuleBubbleChart();
        createDetailTable();
        createCheckerRowChart();
        createDefectStatusChart();
    }


    function drawCharts(){
        renderDeveloperRowChart();
        setOverviewDivArea();
        renderDefectStatusPieChart();
        setOverviewTableArea();
        renderOverviewData();
        renderCheckerRowChart();
        renderModuleRowChart();
        renderModuleBubbleChart();
        renderFileBubbleChart();
        renderFileTable();

        loadDataAndRenderFileBubbleChart();
    }

    function renderDeveloperRowChart(){
        var developerCount = _developerDimensionGroup.all().length;

        var height = (developerCount * _fontHeight) + _titleHeight;
        $(".chartFirstRow").height(height > _baseHeight ? height : _baseHeight);

        var element = $("#developer-div");
        setWidth(element);

        _developerRowChart.height(element.height() - _heightPadding);
        _developerRowChart.width(element.width());

        _developerRowChart.render();
    }

    function setOverviewDivArea(){
        setWidth($('#overview-div'));
    }

    function renderDefectStatusPieChart(){
        var divElement = $("#overview-div");
        var minWidthOfOverviewTable = 290;
        var maxHeight = divElement.height() - 30;

        var divWidth = divElement.width();
        var clientWidth = divWidth < minWidthOfOverviewTable ?  0 : (divWidth - minWidthOfOverviewTable);

        if(clientWidth > maxHeight) clientWidth = maxHeight;

        _defectStatusPieChart.width(clientWidth);
        _defectStatusPieChart.height(clientWidth);

        if(clientWidth > 0){
            _defectStatusPieChart.radius(clientWidth - (clientWidth/2));
            _defectStatusPieChart.innerRadius(clientWidth/10);
            _defectStatusPieChart.render();
        }

    }

    function setOverviewTableArea(){
        var element = $("#overview-div");
        var defectStatusElement = $('defect-status-div');
        var tableElement = $('#overview-table');

        if(_defectStatusPieChart.width() > 0){
            var width = element.width() - _defectStatusPieChart.width() - 20;
        } else {
            width = element.width() - 10;
        }

        tableElement.width(width);
        tableElement.height(element.height() - _heightPadding - 50);
    }

    function renderOverviewData(){
        renderTotalCnt();
        renderTncTitle();
        renderCriticalCntTitle();
        renderCncTitle();
        renderCfcTitle();
        renderCecTitle();
        renderCriticalPpmTitle();
        renderMajorCntTitle();
        renderMncTitle();
        renderMfcTitle();
        renderMecTitle();
        renderMajorPpmTitle();
        renderMinorCntTitle();
        renderNncTitle();
        renderNfcTitle();
        renderNecTitle();
        renderCrcCntTitle();
        renderRncTitle();
        renderRfcTitle();
        renderRecTitle();
        renderEtcCntTitle();
        renderEncTitle();
        renderEfcTitle();
        renderEecTitle();
        renderLocTitle();
        renderClassCntTitle();
        renderMethodCntTitle();
        renderFileCntTitle();
    }

    function renderTotalCnt(){
        createNumberDisplay("#totalCntTitle", _titleDuration, function(d){
            return d.totalCnt;
        });
    }

    function renderTncTitle(){
        createNumberDisplay("#tncTitle", _titleDuration, function(d) {
            var totalNewCnt = d.cnc + d.mnc + d.nnc + d.rnc + d.enc;
            var totalFixCnt = d.cfc + d.mfc + d.nfc + d.rfc + d.efc;
            var totalExcCnt = d.cec + d.mec + d.nec + d.rec + d.eec;

            $scope.totalCntTooltip = "New:" + totalNewCnt + " Fixed:" + totalFixCnt + " Dismissed:" + totalExcCnt;

            return totalNewCnt;
        });
    }

    function renderCriticalCntTitle(){
        createNumberDisplay("#criticalCntTitle", 0, function(d){
            return d.criticalCnt;
        });
    }

    function renderCncTitle(){
        createNumberDisplay("#cncTitle", 0, function(d) {
            $scope.cncTooltip = "New:" + d.cnc + " Fixed:" + d.cfc + " Dismissed:" + d.cec;
            return d.cnc;
        });
    }

    function renderCfcTitle(){
        createNumberDisplay("#cfcTitle", 0, function(d) {
            return d.cfc;
        });
    }

    function renderCecTitle(){
        createNumberDisplay("#cecTitle", 0, function(d) {
            return d.cec;
        });
    }

    function renderCriticalPpmTitle(){
        createNumberDisplay("#criticalPpmTitle", _titleDuration, function(d) {
            var value = 0;

            if(d == undefined || d == 0){
                value = 0;
            } else {
                value = Math.round((d.cnc / d.sloc) * 1000000);
            }

            return value;
        });
    }

    function renderMajorCntTitle(){
        createNumberDisplay("#majorCntTitle", 0, function (d){
            return Math.floor(d.majorCnt);
        });
    }

    function renderMncTitle(){
        createNumberDisplay("#mncTitle", 0, function(d) {
            $scope.mncTooltip = "New:" + d.mnc + " Fixed:" + d.mfc + " Dismissed:" + d.mec;
            return d.mnc;
        });
    }

    function renderMfcTitle(){
        createNumberDisplay("#mfcTitle", 0, function(d) {
            return d.mfc;
        });
    }

    function renderMecTitle(){
        createNumberDisplay("#mecTitle", 0, function(d) {
            return d.mec;
        });
    }

    function renderMajorPpmTitle(){
        createNumberDisplay("#majorPpmTitle", _titleDuration, function(d) {
            var value = 0;

            if(d !== undefined || d !== 0){
                value = Math.round((d.mnc / d.sloc) * 1000000);
            }

            return value;
        });
    }

    function renderMinorCntTitle(){
        createNumberDisplay("#minorCntTitle", 0, function (d){
            return Math.floor(d.minorCnt);
        });
    }

    function renderNncTitle(){
        createNumberDisplay("#nncTitle", 0, function(d) {
            $scope.nncTooltip = "New:" + d.nnc + " Fixed:" + d.nfc + " Dismissed:" + d.nec;
            return d.nnc;
        });
    }

    function renderNfcTitle(){
        createNumberDisplay("#nfcTitle", 0, function(d) {
            return d.nfc;
        });
    }

    function renderNecTitle(){
        createNumberDisplay("#necTitle", 0, function(d) {
            return d.nec;
        });
    }

    function renderCrcCntTitle(){
        createNumberDisplay("#crcCntTitle", 0, function (d){
            return Math.floor(d.crcCnt);
        });
    }

    function renderRncTitle(){
        createNumberDisplay("#rncTitle", 0, function(d) {
            $scope.rncTooltip = "New:" + d.rnc + " Fixed:" + d.rfc + " Dismissed:" + d.rec;
            return d.rnc;
        });
    }

    function renderRfcTitle(){
        createNumberDisplay("#rfcTitle", 0, function(d) {
            return d.rfc;
        });
    }

    function renderRecTitle(){
        createNumberDisplay("#recTitle", 0, function(d) {
            return d.rec;
        });
    }

    function renderEtcCntTitle(){
        createNumberDisplay("#etcCntTitle", 0, function (d){
            return Math.floor(d.etcCnt);
        });
    }

    function renderEncTitle(){
        createNumberDisplay("#encTitle", 0, function(d) {
            $scope.encTooltip = "New:" + d.enc + " Fixed:" + d.efc + " Dismissed:" + d.eec;
            return d.enc;
        });
    }

    function renderEfcTitle(){
        createNumberDisplay("#efcTitle", 0, function(d) {
            return d.efc;
        });
    }

    function renderEecTitle(){
        createNumberDisplay("#eecTitle", 0, function(d) {
            return d.eec;
        });
    }

    function renderLocTitle(){
        createNumberDisplay("#locTitle", _titleDuration, function (d){
            return d.sloc;
        });
    }

    function renderClassCntTitle(){
        createNumberDisplay("#classCntTitle", _titleDuration, function(d){
            return d.classCnt;
        });
    }

    function renderMethodCntTitle(){
        createNumberDisplay("#methodCntTitle", _titleDuration, function(d){
            return d.methodCnt;
        });
    }

    function renderFileCntTitle(){
        createNumberDisplay("#fileCntTitle", _titleDuration, function(d){
            return d.fileCnt;
        });
    }

    function createNumberDisplay(id, duration, valueAccessorHandler){
        var numberDisplay = dc.numberDisplay(id);

        numberDisplay
            .valueAccessor(valueAccessorHandler)
            .group(_titleGroup)
            .transitionDuration(duration)
            .formatNumber(d3.format(",f"));

        numberDisplay.render();
    }

    function renderCheckerRowChart(){
        var checkerCount = _checkerDimensionGroup.all().length;
        var element = $("#checker-status-div");

        setHeightWithItemSize(element, checkerCount);
        setWidth(element);

        _checkerRowChart.height(element.height() - _heightPadding);
        _checkerRowChart.width(element.width());

        _checkerRowChart.render();
    }

    function renderModuleRowChart(){
        var moduleCount = _defectiveModuleDimensionGroup.all().length;
        var moduleRowElement = $("#module-row-div");
        var chartStatusElement = $("#checker-status-div");

        setHeightWithItemSize(moduleRowElement, moduleCount);
        setWidth(moduleRowElement);

        if(chartStatusElement.height() > (moduleRowElement.height() - _heightPadding)
            && moduleRowElement.width() < (window.innerWidth/2)){
            moduleRowElement.height(chartStatusElement.height());
            _moduleRowChart.height(chartStatusElement.height() - _heightPadding);
        } else {
            _moduleRowChart.height(moduleRowElement.height() - _heightPadding);
        }

        _moduleRowChart.width(moduleRowElement.width());

        _moduleRowChart.render();
    }

    function renderModuleBubbleChart(){
        var element = $("#module-defect-div");
        setWidth(element);

        _moduleBubbleChart.width(element.width());
        _moduleBubbleChart.height(element.height() - _heightPadding);

        _moduleBubbleChart.render();
    }

    function renderFileBubbleChart(){
        var element = $("#file-defect-div");
        setWidth(element);

        _fileBubbleChart.width(element.width());
        _fileBubbleChart.height(element.height() - _heightPadding);

        if(_fileBubbleChart.dimension()){
            _fileBubbleChart.render();
        }
    }

    function renderFileTable(){
        $("#file-detail-div").width(window.innerWidth - _widthPadding);

        _fileDetailTableChart.render();
    }

    function setHeightWithItemSize(element, itemSize){
        var rowH = (itemSize * _fontHeight) + _titleHeight;
        element.height(rowH > _baseHeight ? rowH : _baseHeight);
    }

    function setWidth(element){
        var width;
        var marginWidth = 70;
        var scrollWidth = 10;
        var minWidth = 300;
        var realInnerWidth = window.innerWidth - (marginWidth*2) - scrollWidth;

        if( realInnerWidth > (minWidth * 2)){
            width = (window.innerWidth/2) - marginWidth - scrollWidth/2;
        } else {
            width = window.innerWidth - marginWidth - scrollWidth;
        }

        element.width(width);
    }

    function createDeveloperRowChart(){
        _developerRowChart
            .dimension(_developerDimension)
            .group(_developerDimensionGroup)
            .transitionDuration(_chartDuration)
            .elasticX(true)
            .label(function(d){
                return d.key + "(" + d.value.fileNameCount + " files)";
            })
            .title(function(d){
                return d.key;
            })
            .keyAccessor(function (p) {
                return p.key;
            })
            .valueAccessor(function (p) {
                return p.value.fileNameCount;
            })
            .on("filtered", handleFiltering);

        function handleFiltering (chart){
            dc.events.trigger(function(){
                _checkerRowChart.filterAll();

                if(chart.filter()){
                    var checkerCodes = _developerVsChecker.getCheckerCodes(chart.filter());

                    for(var i=0; i<checkerCodes.length; i++){
                        var checkerCode = checkerCodes[i];
                        _checkerRowChart.filter(checkerCode);
                    }
                }
            });
        }
    }

    function createDefectStatusChart(){
        _defectStatusPieChart
            .transitionDuration(_chartDuration)
            .dimension(_statusDimension)
            .group(_statusDimensionGroup)
            .radius(120) // px
            .innerRadius(20) // px
            //.slicesCap(4)
            .colors(d3.scale.category10())
            .minAngleForLabel(0.1);

        _defectStatusPieChart.label(function(d){
            var value = '';
            if(d.key == "NEW"){
                value = d.key + " (" + d.value.newCntSum + ", " + getPercentage(d.value.newCntSum, _totalDefectCount) + ")";
            } else if(d.key == "FIX"){
                value =  d.key + " (" + d.value.fixCntSum + ", " + getPercentage(d.value.fixCntSum, _totalDefectCount) + ")";
            } else if(d.key == "EXC"){
                value =  d.key + " (" + d.value.excCntSum + ", " + getPercentage(d.value.excCntSum, _totalDefectCount) + ")";
            }

            return value;
        });

        _defectStatusPieChart.title(function(d){
            var value = '';
            if(d.key == "NEW"){
                value = d.key + " (" + d.value.newCntSum + " defects, " + getPercentage(d.value.newCntSum, _totalDefectCount) + ")";
            } else if(d.key == "FIXED"){
                value = d.key + " (" + d.value.fixCntSum + " defects, " + getPercentage(d.value.fixCntSum, _totalDefectCount) + ")";
            } else if(d.key == "DISMISSED"){
                value = d.key + " (" + d.value.excCntSum + " defects, " + getPercentage(d.value.excCntSum, _totalDefectCount) + ")";
            }

            return value;
        });
            //.legend(dc.legend().x(70).y(100).itemHeight(13).gap(5))
        _defectStatusPieChart.keyAccessor(function (p) {
                return p.key;
        });

        _defectStatusPieChart.valueAccessor(function (p) {
            var value = '';
            if(p.key === "NEW"){
                value = p.value.newCntSum;
            } else if(p.key === "FIX"){
                value = p.value.fixCntSum;
            } else if(p.key === "EXC"){
                value = p.value.excCntSum;
            }

            return value;
        });

        _defectStatusPieChart.on("filtered", function (chart) {
            dc.events.trigger(function() {
                if (chart.filter()) {
                    _checkerStatusDimension.filter(chart.filter());

                    if(chart.filter() === 'NEW'){
                        _metricStatusDimension.filter(function (d){
                            return d.hasNew == true;
                        });
                    } else if(chart.filter() == 'FIX'){
                        _metricStatusDimension.filter(function (d){
                            return d.hasFix == true;
                        });
                    } else if(chart.filter() == 'EXC'){
                        _metricStatusDimension.filter(function (d){
                            return d.hasExc == true;
                        });
                    }

                    _fileBubbleChartSearchOptions.defectStatus = chart.filter();
                } else {
                    _checkerStatusDimension.filterAll();
                    _metricStatusDimension.filterAll();

                    _fileBubbleChartSearchOptions.defectStatus = '';
                }

                loadDataAndRenderFileBubbleChart();
            })
        });
    }

    function createCheckerRowChart(){
        _checkerRowChart
            .dimension(_checkerDimension, _checkerModuleDimension, _checkerFileDimension)
            .group(_checkerDimensionGroup)
            .transitionDuration(_chartDuration)
            .elasticX(true)
            .renderLabel(true)
            .colors(d3.scale.category10())
            .keyAccessor(function (p) {     // y
                //keysInList.push(p.key);
                return p.key;
            })
            .valueAccessor(function (p) {   // x
                return p.value.sum;
            })
            .label(function (p) {
                return p.key + " (" + p.value.fixCntSum + "/" + p.value.sum + ")";
            })
            .title(function (p) {
                return p.key + " (total:" + p.value.sum + " new:" + p.value.newCntSum + " fix:" + p.value.fixCntSum
                    + " exc(dismissed):" + p.value.excCntSum + ")";
            });

        _checkerRowChart.on("filtered", function (chart){
            dc.events.trigger(function(){
                if(chart.filter()){
                    _checkerVsModule.forEach(function (item){
                        chart.filters().forEach(function (checkerCodeName){
                            if(item.checkerCode == checkerCodeName){
                                if(_fileBubbleChart.hasFilter(item.fileName) == false){
                                    _fileBubbleChart.filter(item.fileName);
                                }
                            }
                        })
                    });
                } else {
                    _fileBubbleChart.filterAll();
                }
            });
        });
    }

    /*
     * load data whenever it needs because of performance
     */
    function loadDataAndRenderFileBubbleChart(){
        var filter;

        $http.get("/api/v1/metrics-and-defect-limit", { params: _fileBubbleChartSearchOptions }
        ).then(function(results){
            filter = crossfilter(results.data);
            _fileBubbleDimension = filter.dimension(function (d) { return d.fileName; });

            _fileDimGroup = _fileBubbleDimension.group().reduce(
                function (field, item) {
                    field.totalCntSum = item.totalCnt;
                    field.classCntSum = item.classCnt;
                    field.methodCntSum = item.methodCnt;
                    field.newCntSum = item.newCnt;
                    field.fixCntSum = item.fixCnt;
                    field.excCntSum = item.excCnt;
                    field.locSum = item.sloc;
                    field.maxComplexity = item.maxComplexity;
                    return field;
                },
                function(field) {
                    field.totalCntSum = 0;
                    field.classCntSum = 0;
                    field.methodCntSum = 0;
                    field.newCntSum = 0;
                    field.fixCntSum = 0;
                    field.excCntSum = 0;
                    field.locSum = 0;
                    field.maxComplexity = 0;
                    return field;
                },
                function() {
                    return {
                        totalCntSum:0, locSum:0, classCntSum:0, methodCntSum:0,
                        fixCntSum:0, excCntSum:0, newCntSum:0, maxComplexity:0
                    }
                }
            );

            initFileBubbleChart();

        }, function(results){
            $log.info("Error: " + results.data + "; " + results.status);
        });
    }

    function initFileBubbleChart(){
        _fileBubbleChart
            .dimension(_fileDimension, _moduleDimension, _metricStatusDimension)
            .group(_fileDimGroup)
            .transitionDuration(_chartDuration)
            .colors(d3.scale.category10())
            .minRadiusWithLabel(2)
            .maxBubbleRelativeSize(0.1)
            .elasticY(true)
            .elasticX(true)
            .elasticRadius(true)
            .yAxisPadding(10)
            .xAxisPadding(10)
            .x(d3.scale.linear().domain([0, 100]))
            .y(d3.scale.linear().domain([0, 100]))
            .r(d3.scale.linear().domain([0, 100]))
            .keyAccessor(function (p) {     // y
                return p.value.maxComplexity;
            })
            .valueAccessor(function (p) {   // x
                return p.value.locSum;
            })
            .radiusValueAccessor(function (p) { // r
                return p.value.totalCntSum;
            })
            .brushOn(true)
            .renderLabel(true)
            .renderHorizontalGridLines(true)
            .renderVerticalGridLines(true);

        _fileBubbleChart.label(function (p) {
            var value = '';
            if(p.key == undefined || p.key == ""){
                value = "No Module (" + p.value.totalCntSum + ")";
            } else {
                value = p.key + " (" + p.value.totalCntSum + ")";
            }

            return value;
        });

        _fileBubbleChart.title(function (p) {
            var value = '';
            if(p.key == undefined || p.key == ""){
                value = "No Module (LOC:" + p.value.locSum + ", Max Complexity:" + p.value.maxComplexity
                    + ", Defect:" + p.value.totalCntSum + ", Class:" + p.value.classCntSum + ")";
            } else {
                value = p.key + " (LOC:" + p.value.locSum + ", Max Complexity:" + p.value.maxComplexity
                    + ", Defect:" + p.value.totalCntSum + ", Class:" + p.value.classCntSum + ")";
            }

            return value;
        });

        _fileBubbleChart.margins().left = 50;

        _fileBubbleChart.render();
    }

    function createModuleBubbleChart(){
        _moduleBubbleChart
            .dimension(_moduleDimension, _fileDimension, _metricStatusDimension)
            .group(_moduleDimensionGroup)
            .transitionDuration(_chartDuration)
            .colors(d3.scale.category10())
            .minRadiusWithLabel(2)
            .maxBubbleRelativeSize(0.1)
            .brushOn(true)
            .renderLabel(true)
            .renderHorizontalGridLines(true)
            .renderVerticalGridLines(true)
            .elasticY(true)
            .elasticX(true)
            .elasticRadius(true)
            .yAxisPadding(40)
            .xAxisPadding(5)
            .x(d3.scale.linear().domain([0, 1000]))
            .y(d3.scale.linear().domain([0, 1000]))
            .r(d3.scale.linear().domain([0, 1000]))
            .keyAccessor(function (p) {     // y
                return p.value.classCntSum;
            })
            .valueAccessor(function (p) {   // x
                return p.value.locSum;
            })
            .radiusValueAccessor(function (p) { // r
                return p.value.totalCntSum;
            });

        _moduleBubbleChart.label(function (p) {
            var value = '';
            if(p.key == undefined || p.key == ""){
                value = "No Module (" + p.value.totalCntSum + ")";
            } else {
                value = p.key + " (" + p.value.totalCntSum + ")";
            }

            return value;
        });

        _moduleBubbleChart.title(function (p) {
            var value = '';
            if(p.key == undefined || p.key == ""){
                value = "No Module (LOC:" + p.value.locSum + ", Class:" + p.value.classCntSum + ", Defect:"
                    + p.value.totalCntSum + ")";
            } else {
                value = p.key + " (LOC:" + p.value.locSum + ", Class:" + p.value.classCntSum + ", Defect:"
                    + p.value.totalCntSum + ")";
            }

            return value;
        });

        _moduleBubbleChart.on("filtered", function (chart) {
            dc.events.trigger(function() {
                if(chart.filter()) {
                    _fileBubbleChartSearchOptions.modulePath = chart.filter();
                } else {
                    _fileBubbleChartSearchOptions.modulePath = '';
                }

                loadDataAndRenderFileBubbleChart();
                filterFileListTable();
            })
        });

        _moduleBubbleChart.margins().left = 50;  // for Y axis labeling
    }

    function createModuleRowChart(){
        _moduleRowChart
            .transitionDuration(_chartDuration)
            .dimension(_moduleDimension, _fileDimension, _metricStatusDimension)
            .group(_defectiveModuleDimensionGroup)
            .elasticX(true)
            .renderLabel(true)
            .colors(d3.scale.category10());

        _moduleRowChart.label(function(d){
            var value = '';
            if(d.key == undefined || d.key == ""){
                value = "no module (" + d.value.totalCntSum + ")";
            } else {
                value = d.key + " (" + d.value.totalCntSum + ")";
            }

            return value;
        });

        _moduleRowChart.title(function(d){
            var value = '';
            if(d.key == undefined || d.key == ""){
                value = "no module - total:" + d.value.totalCntSum + ", new:" + d.value.newCntSum + ", fixed:" + d.value.fixCntSum + ", dismissed:" + d.value.excCntSum;
            } else {
                value = d.key + " - total:" + d.value.totalCntSum + ", new:" + d.value.newCntSum + ", fixed:" + d.value.fixCntSum + ", dismissed:" + d.value.excCntSum;
            }

            return value;
        });

        _moduleRowChart.keyAccessor(function (p) {
            return p.key;
        });

        _moduleRowChart.valueAccessor(function (p) {
            return p.value.totalCntSum;
        });

        _moduleRowChart.on("filtered", function (chart){
            dc.events.trigger(function(){
                if(chart.filter()){
                    _moduleBubbleChart.filter(chart.filter());
                } else {
                    _moduleBubbleChart.filterAll();
                }
            });
        });
    }

    function createDetailTable(){
        _fileDetailTableChart.width(1400)
            .dimension(_moduleDimension, _fileDimension)
            .group(function(d) { return "[Module] " + d.modulePath})
            .size(100)
            .transitionDuration(_chartDuration)
            .columns([
                //       function(d) { return d.modulePath; },
                function(d) { return d.fileName; },
                function(d) { return d.classCnt; },
                function(d) { return d.newCnt + "/" + d.totalCnt; },
                function(d) { return d.excCnt; },
                function(d) { return d.cnc + " | " + d.mnc; },
                function(d) { return d.maxComplexity; },
                function(d) { return d.sloc; },
                function(d) { return Math.round(d.commentRatio * 100) + "%"; },
                function(d) { return d.modifierId; }
            ])
            .sortBy(function(d) { return d.newCnt;})
            .order(d3.descending);
    }

    function createFileListTable(){
        $scope.gridOptions = {
            data: 'fileList',
            multiSelect: false,
            showFooter: true,
            enablePinning: true,
            enableColumnResize: true,
            enableColumnReordering: true,
            enableRowReordering: true,
            headerRowHeight: 26,
            rowHeight: 26,
            showGroupPanel: true,
            showColumnMenu: true,
            showFilter: true,
            showSelectionCheckbox: false,
            jqueryUIDraggable: false,
            columnDefs: [
                {field:'modulePath', displayName:'Module'},
                {field:'fileName', displayName:'File', width:200},
                {field:'classCnt', displayName:'Class', cellClass:'textAlignCenter'},
                {field:'methodCnt', displayName:'Method', cellClass:'textAlignCenter'},
                {field:'totalCnt', displayName:'Total', cellClass:'textAlignCenter'},
                {field:'newCnt', displayName:'New', cellClass:'textAlignCenter'},
                {field:'fixCnt', displayName:'Fix', cellClass:'textAlignCenter'},
                {field:'excCnt', displayName:'Dismissed', cellClass:'textAlignCenter'},
                {field:'criticalCnt', displayName:'Critical', cellClass:'textAlignCenter'},
                {field:'majorCnt', displayName:'Major', cellClass:'textAlignCenter'},
                {field:'minorCnt', displayName:'Minor', cellClass:'textAlignCenter'},
                {field:'etcCnt', displayName:'Etc', cellClass:'textAlignCenter'},
                {field:'sloc', displayName:'LOC', cellClass:'textAlignCenter'},
                {field:'maxComplexity', displayName:'Max Complexity', cellClass:'textAlignCenter'},
                {field:'averageComplexity', displayName:'Average Complexity', cellClass:'textAlignCenter'},
                {field:'modifierId', displayName:'Last Modifier', cellClass:'textAlignCenter'},
            ]
        };
    }


    $scope.currentFileList = [];
    function getPercentage(a, b){
        var value = '';
        if(b == 0){ value = '0%';}
        else value = ((a/b)*100).toFixed(1) + '%';
        return value;
    }

    function comma(str) {
        str = String(str);
        return str.replace(/(\d)(?=(?:\d{3})+(?!\d))/g, '$1,');
    }

    $(".exportFileList").on('click', function (event) {
        // Data URI
        $scope.csvContent = "data:text/csv;charset=utf-8,";
        setExportFileFormat($scope.fileList);
        exportExcelFile.apply(this, [$scope.csvContent, 'export.csv']);
    });

    var setExportFileFormat = function(results){
        for(var i =0 ; i < results.length ; i++){

            if(i == 0){
                $scope.csvContent = $scope.csvContent + 'Module,File,Class,Method,Total,New,Fix,Dismissed,'
                    +'Critical,Major,Minor,Etc,LOC,LastModifier,MaxComplexity, AverageComplexity' + '\n' ;
            }
            if(results[i].modifierId == null ){
                results[i].modifierId = '';
            }
            $scope.currentFileList[i] = results[i].modulePath + ',' +  results[i].fileName + ',' + results[i].classCnt +
                ','+ results[i].methodCnt + ',' + results[i].totalCnt + ',' + results[i].newCnt + ',' +
                results[i].fixCnt + ',' + results[i].excCnt + ',' + results[i].criticalCnt + ','
                + results[i].majorCnt + ','+ results[i].minorCnt + ','+ results[i].etcCnt +','
                + results[i].sloc + ','+ results[i].modifierId + ','+ results[i].maxComplexity +','+ results[i].averageComplexity ;
            $scope.csvContent = $scope.csvContent + $scope.currentFileList[i] + '\n' ;
        }
    };

    var exportExcelFile = function (){
        var encodedUri = encodeURI($scope.csvContent);
        $(this)
            .attr({
                'download': $scope.projectName + '_fileList.csv',
                'href': encodedUri,
                'target': '_blank'
            });

    };



});
