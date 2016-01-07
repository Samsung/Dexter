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

var winston = require('winston');
var fs = require('fs');
var dUtil = require('./dexter-util');
var logFilePath = "./log/dexter-server.log";

var logger;
var _prefix;

exports.init = function (){
    _prefix = global.runOptions.serverName + '@' + global.runOptions.port + " : ";

    if(!fs.existsSync("./log")){
        fs.mkdirSync("./log");
    }

    logger = new (winston.Logger)({
        transports: [
            new (winston.transports.File)({
                filename: logFilePath,
                handleExceptions: true,
                exitOnError: false,
                colorize : true
            }),
            new (winston.transports.Console)({
                level: 'error',
                handleExceptions: true,
                exitOnError: false,
                colorize : true
            })
        ]
    });


    if(process.env.NODE_ENV === 'production'){
        winston.remove(winston.transports.Console);
        logger.remove(winston.transports.Console);
    }

    logger.setLevels(winston.config.syslog.levels);
};

log = function(message, type) {
    if(!logger){
        console.log(">> logger is not ready !");
        console.log(message);
        return;
    }

    console.log(dUtil.getCurrentTimeString() + " " + _prefix + message);

    if(type === 'error'){
        logger.log('error', dUtil.getCurrentTimeString() + " " + _prefix + message);
    } else if(type === 'warn'){
        logger.log('warn', dUtil.getCurrentTimeString() + " " + _prefix + message);
    } else if(type === 'debug'){
        //if('development' === process.env.NODE_ENV){
            console.log(dUtil.getCurrentTimeString() + " " + _prefix + message);
        //}
        logger.log('debug', dUtil.getCurrentTimeString() + " " + _prefix + message);
    } else {
        logger.log('info', dUtil.getCurrentTimeString() + " " + _prefix + message);
    }
};

exports.info = function(message) {
    log(message, 'info');
};

exports.warn = function(message) {
    log(message, 'warn');
};

exports.error = function(message) {
    log(message, 'error');
};

exports.debug = function(message) {
    log(message, 'debug');
};
