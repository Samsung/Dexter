/**
 * Copyright (c) 2016 Samsung Electronics, Inc.,
 * All rights reserved.
 *
 * Redistribution and use in source and binary forms, with or without
 * modification, are permitted provided that the following conditions are met:
 *
 * * Redistributions of source code must retain the above copyright notice, this
 *   list of conditions and the following disclaimer.
 *
 * * Redistributions in binary form must reproduce the above copyright notice,
 *   this list of conditions and the following disclaimer in the documentation
 *   and/or other materials provided with the distribution.
 *
 * THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS "AS IS"
 * AND ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE
 * IMPLIED WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE ARE
 * DISCLAIMED. IN NO EVENT SHALL THE COPYRIGHT HOLDER OR CONTRIBUTORS BE LIABLE
 * FOR ANY DIRECT, INDIRECT, INCIDENTAL, SPECIAL, EXEMPLARY, OR CONSEQUENTIAL
 * DAMAGES (INCLUDING, BUT NOT LIMITED TO, PROCUREMENT OF SUBSTITUTE GOODS OR
 * SERVICES; LOSS OF USE, DATA, OR PROFITS; OR BUSINESS INTERRUPTION) HOWEVER
 * CAUSED AND ON ANY THEORY OF LIABILITY, WHETHER IN CONTRACT, STRICT LIABILITY,
 * OR TORT (INCLUDING NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY OUT OF THE USE
 * OF THIS SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY OF SUCH DAMAGE.
 */
"use strict";

monitorApp.controller("DefectByProjectCtrl", function($scope, $http, $log) {
    let defect = this;
    let minYear;
    let maxYear;
    let maxWeekOfCurYear;
    defect.projectNames = [];
    defect.projects = [];
    defect.curProjectName = 'Select a project';

    initialize();

    function initialize() {
        createDefectListTable();
        loadProjectList();
    }
    
    function loadProjectList() {
        $http.get('/api/v2/project-list')
            .then((res) => {
                if (!isHttpResultOK(res)) {
                    $log.error('Failed to load project list');
                    return;
                }

                defect.projects = res.data.rows;
                defect.projectNames = _.map(defect.projects, 'projectName');
            })
            .catch((err) => {
                $log.error(err);
            });
    }

    defect.projectChanged = function(projectName) {
        loadDefectListByProject(projectName);
        defect.curProjectName = projectName;
        defect.projects.forEach((project) => {
            if (project.projectName === projectName) {
                defect.curProjectType = project.projectType;
                defect.curProjectGroup = project.groupName;
                defect.curProjectLang = project.language;
            }
        });
        changeExportingFileNames();
    };

    function createDefectListTable() {
        defect.gridOptions = getCommonOptions();
        defect.gridOptions.data = [];
    }

    function getCommonOptions() {
        return {
            enableSorting: true,
            enableFiltering: true,
            showGridFooter: true,
            enableGridMenu: true,
            enableSelectAll: true,
            exporterCsvFilename: 'defect-list.csv',
            exporterPdfFilename: 'defect-list.pdf',
            exporterPdfDefaultStyle: {fontSize: 8},
            exporterTableStyle: {margin: [5,5,5,5]},
            exporterPdfTableHeaderStyle: {fontSize: 10, bold: true, italics: true, color: 'red'},
            exporterPdfOrientation: 'landscape',
            exporterPdfPageSize: 'A4',
            columnDefs: getDefectGridColumnDefinitions()
        };
    }

    function getDefectGridColumnDefinitions() {
        return [
            {field:'year',              displayName:'Year',         width: 105,     headerTooltip: 'Year'},
            {field:'week',              displayName:'Week',         width: 105,     headerTooltip: 'Week'},
            {field:'accountCount',      displayName:'Accout',       width: 170,     headerTooltip: 'Number of accounts'},
            {field:'allDefectCount',    displayName:'Total',        width: 170,     headerTooltip: 'Number of defects'},
            {field:'allFix',            displayName:'Fixed',        width: 170,     headerTooltip: 'Number of fixed defects'},
            {field:'allExc',            displayName:'Excluded',     width: 170,     headerTooltip: 'Number of excluded defects'}
        ];
    }

    function changeExportingFileNames() {
        defect.gridOptions.exporterCsvFilename = defect.curProjectName + '-defect-list.csv';
        defect.gridOptions.exporterPdfFilename = defect.curProjectName + '-defect-list.pdf';
    }

    function loadDefectListByProject(projectName) {
        $http.get('/api/v2/defect/project/' + projectName)
            .then((res) => {
                if (!isHttpResultOK(res)) {
                    $log.error('Failed to load defect list');
                    return;
                }

                defect.gridOptions.data = res.data.rows;
            })
            .catch((err) => {
                $log.error(err);
            });
    }
});