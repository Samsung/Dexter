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

monitorApp.controller("DefectByProjectCtrl", function($scope, $http, $log, ProjectService) {
    let defect = this;
    defect.projectNames = [];
    defect.projects = [];
    defect.curProjectName = 'Select a project';

    const columnDefs = [
        {field:'year',              displayName:'Year',         width: 105,     headerTooltip: 'Year'},
        {field:'week',              displayName:'Week',         width: 105,     headerTooltip: 'Week'},
        {field:'userCount',         displayName:'User',         width: 170,     headerTooltip: 'Number of users'},
        {field:'allDefectCount',    displayName:'Total',        width: 170,     headerTooltip: 'Number of defects'},
        {field:'allFix',            displayName:'Fixed',        width: 170,     headerTooltip: 'Number of fixed defects'},
        {field:'allDis',            displayName:'Dismissed',    width: 170,     headerTooltip: 'Number of dismissed defects'}
    ];

    initialize();

    function initialize() {
        defect.gridOptions = createGrid(columnDefs);
        loadProjectList();
    }
    
    function loadProjectList() {
        ProjectService.getProjectList()
            .then((rows) => {
                defect.projects = rows;
                defect.projectNames = _.map(defect.projects, 'projectName');
            })
            .catch((err) => {
                $log.error(err);
            });
    }

    defect.projectChanged = function(projectName) {
        loadDefectListByProject(projectName);
        defect.curProjectName = projectName;

        let project = _.find(defect.projects, (project) => {
            return project.projectName === projectName;
        });
        defect.curProjectType = project.projectType;
        defect.curProjectGroup = project.groupName;
        defect.curProjectLang = project.language;

        setGridExportingFileNames(defect.gridOptions, DEFECT_FILENAME_PREFIX + '-' + defect.curProjectName);
    };

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