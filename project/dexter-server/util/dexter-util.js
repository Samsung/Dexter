/**
 * Copyright (c) 2014 Samsung Electronics, Inc.,
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
    
var moment = require('moment');
var log = require('../util/logging');
var _ = require('lodash');

// 20150414-153520
exports.getCurrentTimeString = function () {
    var now = moment().format();
    return now.replace(/\..+/, '').replace(/\-/g, '').replace(/\:/g, '').replace(/T/, '-').substr(0,15);
};

exports.getUserId = function (req) {
    var str = req.header('Authorization');

    if(str && str.length > 0){
        str = str.substring("Basic ".length, str.length);
        var decoded = new Buffer(str, 'base64').toString('utf8');
        var auths = decoded.split(":");

        if(auths && auths.length == 2){
            return auths[0];
        }
    }
    return "";
};

exports.getUserPwd = function (req) {
    var str = req.header('Authorization');

    if(str && str.length > 0){
        str = str.substring("Basic ".length, str.length);
        var decoded = new Buffer(str, 'base64').toString('utf8');
        var auths = decoded.split(":");

        if(auths && auths.length == 2){
            return auths[1];
        }
    }
};

exports.getUserIdAndPwd = function (req) {
    if(req == undefined || req.header == undefined){
        return;
    }
    var str = req.header('Authorization');

    if(str && str.length > 0){
        str = str.substring("Basic ".length, str.length);
        var decoded = new Buffer(str, 'base64').toString('utf8');
        var auths = decoded.split(":");

        if(auths && auths.length == 2){
            return auths;
        }
    }
};

exports.isSameString = function (str1, str2){
    if( (str1 == undefined || str1 == null || str1 == "")
        && (str2 == undefined || str2 == null || str2 == "")){
        return true;
    }

    return str1 == str2;
};

exports.wait = function (ms){
    if(!ms){
        ms = 1000;
    }
    var unixtime_ms = new Date().getTime();
    while(new Date().getTime() < unixtime_ms + ms) {}
};

exports.getLocalhostIp = function(callback){
    callback(getLocalIPAddress());
};

/* refer to : http://stackoverflow.com/questions/3653065/get-local-ip-address-in-node-js */
exports.getLocalIPAddress = getLocalIPAddress;

function getLocalIPAddress(){
    var interfaces = require('os').networkInterfaces();
    for (var devName in interfaces) {
        var iface = interfaces[devName];

        for (var i = 0; i < iface.length; i++) {
            var alias = iface[i];
            if (alias.family === 'IPv4' && alias.address !== '127.0.0.1' && !alias.internal)
                return alias.address;
        }
    }

    return '0.0.0.0';
};

exports.getCliOptions = function() {
    var options = {};

    _(process.argv).forEach(function(item){
        var option = item.split('=');

        if(isValidCliOption(option)){
            option[0] = _.trim(option[0]);
            var key = option[0].substr(1, (option[0].length-1));
            options[key] = _.trim(option[1]);
        }
    }).value();

    return {
        options: options,
        getValue: function (key, defaultValue){
            if(!key || _.trim(key).length === 0){
                return undefined;
            }

            var _key = _.trim(key);

            if(this.options[_key]){
                return this.options[_key];
            } else if(defaultValue){
                this.options[_key] = defaultValue;
                return defaultValue;
            } else {
                return undefined;
            }
        }
    };
};


function startsWith(str, checker){
    if(str!=null && checker!=null && str.length > checker.length){
        if(str.toUpperCase().substr(0,checker.toUpperCase().length) == checker.toUpperCase()){
            return true;
        }else{
            return false;
        }
    }else{
        return false;
    }
}

function isValidCliOption(option){
    if(_.isArray(option) === false || option.length !== 2) return false;

    var key = _.trim(option[0]);
    var value = _.trim(option[1]);

    if(key.length < 2) return false;
    if(value.length < 1) return false;

    return key.startsWith('-');

}



/*
exports.getCliOptions = function() {
    var options = {};

    for(var i = 2; i < process.argv.length; i++){
        var option = process.argv[i].split('=');
        if(typeof option === 'object' && option.length === 2){
            var key = option[0].substr(1, (option[0].length-1));
            options[key] = option[1];
        }
    }

    return {
        options: options,
        getCliValue: function (key, defaultValue){
            return this.options[key] === undefined ? defaultValue : this.options[key];
        }
    };
};*/