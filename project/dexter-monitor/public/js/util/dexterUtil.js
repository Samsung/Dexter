/**
 * Created by min.ho.kim on 2015-04-16.
 */
var d_Util = {
    validateString: function(value, minLength, maxLength){
        return value !== undefined
            && typeof value === 'string'
            && minLength <= value.length && value.length <= maxLength;
    },
    validateNumber: function(value, minValue, maxValue){
        return value !== undefined
            && typeof value === 'number'
            && minValue <= value && value <= maxValue;
    },
    validateBoolean: function(value){
        return value !== undefined
            && typeof value === 'boolean'
    }
};