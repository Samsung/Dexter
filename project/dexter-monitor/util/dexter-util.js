/**
 * Copyright (c) 2015 Samsung Electronics, Inc.,
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

var moment = require('moment');
var log = require('../util/logging');
var _ = require('lodash');

exports.getCurrentTimeString = function () {
    var now = moment().format();
    return now.replace(/\..+/, '').replace(/\-/g, '').replace(/\:/g, '').replace(/T/, '-').substr(0,15);
};

/* refer to : http://stackoverflow.com/questions/3653065/get-local-ip-address-in-node-js */
exports.getLocalIPAddress = function(){
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
            option[0] = _.trim(option[0])
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

function isValidCliOption(option){
    if(_.isArray(option) === false || option.length !== 2) return false;

    var key = _.trim(option[0]);
    var value = _.trim(option[1]);

    if(key.length < 2) return false;
    if(value.length < 1) return false;

    return key.startsWith('-');
}
