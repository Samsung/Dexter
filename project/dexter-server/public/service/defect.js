"use strict";
defectApp.service('defectService', function ($http, $q, $location, $log) {
    const url = $location.absUrl().split('?');
    var projectName = '';

    this.getCSVHeaderForDefectView = function () {
        return ["Did", "Checker", "Count", "Line No", "Severity", "Category", "Status",
            "Module", "File", "Class", "Method/Function", "Language", "Tool", "Author", "Date", "", "", "URL", url];
    };

    this.getCSVHeaderForSnapshotView = function () {
        return ["Did", "Checker", "Count", "Line No", "Severity", "Category", "Snapshot Status", "Current Status",
            "Module", "File", "Class", "Method/Function", "Language", "Tool", "Author", "Date", "", "", "URL", url];
    };

    this.getDefaultDefectInfo = function () {
        return ({
            'Did': 0,
            'Checker': 'None',
            'Count': 0,
            'Line No': 0,
            'Severity': 0,
            'Category': 'None',
            'Status': 'None',
            'Module': 'None',
            'File': 'None',
            'Class': 'None',
            'Method/Function': 'None',
            'Language': 'None',
            'Tool': 'None',
            'Author': 'None',
            'Date': 'There is no defect in this project'
        });
    };

    this.getDefaultSnapshotDefectInfo = function () {
        return ({
            'Did': 'None',
            'Checker': 'None',
            'Count': 0,
            'Line No': 0,
            'Severity': 'None',
            'Category': 'None',
            'Snapshot Status': 'None',
            'Current Status': 'None',
            'Module': 'None',
            'File': 'None',
            'Class': 'None',
            'Method/Function': 'None',
            'Language': 'None',
            'Tool': 'None',
            'Author': 'None',
            'Date': 'None'
        });
    };

    this.loadProjectName = function () {
        if (projectName !== '') {
            return Promise.resolve(projectName);
        }

        return $http.get('/api/v3/projectName')
            .then(function (results) {
                if (isHttpResultOK(results)) {
                    projectName = results.data.projectName;
                    return projectName;
                }
            }).catch(error => {
                $log.error(error);
                return 'undefined';
            });
    }

});

defectApp.filter('nullFilter', function () {
    return function (input) {
        return input == 'null' ? 'N/A' : input;
    };
});
