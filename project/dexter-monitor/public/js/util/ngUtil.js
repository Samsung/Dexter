/**
 * Created by min.ho.kim on 2015-04-16.
 */
var d_NgUtil = {
    getRedGreenCellTemplateByBooleanValue : function(){
        var activeColumnCss = "{red: row.getProperty(col.field) === false, green: row.getProperty(col.field) === true}";
        return '<div class="text-center" ng-class="' + activeColumnCss + '">' +
            '<div class="ngCellText">{{row.getProperty(col.field)}}</div>' +
            '</div>';
    }
};
