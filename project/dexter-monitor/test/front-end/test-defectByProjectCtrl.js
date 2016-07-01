
describe('DefectByProjectCtrl Test', function() {

    beforeEach(module('dexterMonitorApp'));

    var $controller, $httpBackend, defect;

    beforeEach(inject(function(_$controller_, _$httpBackend_) {
        $controller = _$controller_;
        $httpBackend = _$httpBackend_;
        defect = $controller('DefectByProjectCtrl', {$scope: {}});
    }));

    describe('projectChanged()', function() {

        var PROJECT_NAME = '16_DexterMonitorProject';
        var PROJECT_TYPE = 'Maintenance';
        var PROJECT_GROUP = 'Samsung2';
        var PROJECT_LANGUAGE = 'JAVA';

        it('should set current values to that of the selected project', function() {
            $httpBackend.whenGET('/api/v2/defect/project/').respond({status:'ok', rows:[]});

            defect.projects = [{
                'projectType' : 'Preceding',
                'projectName' : 'SamsungProject',
                'groupName' : 'Samsung1',
                'language' : 'CPP'
            },{
                'projectType' : PROJECT_TYPE,
                'projectName' : PROJECT_NAME,
                'groupName' : PROJECT_GROUP,
                'language' : PROJECT_LANGUAGE
            }];

            defect.projectChanged(PROJECT_NAME);

            assert.equal(defect.curProjectName, PROJECT_NAME);
            assert.equal(defect.curProjectType, PROJECT_TYPE);
            assert.equal(defect.curProjectGroup, PROJECT_GROUP);
            assert.equal(defect.curProjectLang, PROJECT_LANGUAGE);
            assert.equal(defect.gridOptions.exporterCsvFilename, DEFECT_FILENAME_PREFIX + '-' + PROJECT_NAME + '.csv');
            assert.equal(defect.gridOptions.exporterPdfFilename, DEFECT_FILENAME_PREFIX + '-' + PROJECT_NAME + '.pdf');
        });
    });
});
