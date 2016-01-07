/**
 * Created by min.ho.kim on 2015-04-28.
 */
describe('monitorApp ', function(){

    beforeEach(module("dexterMonitorApp"));

    var monitorController, scope;

    beforeEach(inject(function($rootScope, $controller){
        scope = $rootScope.$new();
        monitorController = $controller('MonitorCtrl', {
            $scope: scope
        });
    }));
});