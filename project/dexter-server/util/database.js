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

"use strict";
var mysql = require('mysql');
var _ = require('lodash');
var Promise = require('bluebird');

var logging = require('./logging');
var util = require('./dexter-util');
var databasePool;

/**
 * You should call this function before using this module.
 * this function rates and tests MySQL Database Pool
 * with global.runOptions's properties which are set by setRunOptionsByCliOptions9() in server.js
 *
 * @returns {*} BlueBird Promise Object
 */
exports.init = function(){
   return initDatabase();
};

/**
 * @param req no need to set any value
 * @param res if success, return the database name as a project name, Otherwise return error code 500
 */
exports.getProjectName = function(req, res){
    var projectName = getDatabaseName();

    if(projectName === "unknown"){
        res.send(500, {
            status : "fail",
            errorMessage : "cannot get project name because goloba.runOptions.databaseName is not valid",
            projectName : projectName
        });
    } else {
        res.send(200, {
            status : "ok",
            projectName :  projectName
        });
    }
};

/**
 * @return if success, return database name, otherwise, return "unknonw" string
 * @type {getDatabaseName}
 */
exports.getDatabaseName = getDatabaseName;

function getDatabaseName(){
    if(_.has(global, ["runOptions", "databaseName"]) && global.runOptions.databaseName.length > 0){
        return global.runOptions.databaseName;
    } else {
        return "unknown";
    }
};

function initDatabase(){
    checkGlobalRunOptions();

    return createPool()
        .then(checkDatabasePool);
}

function checkGlobalRunOptions(){
    if(!global.runOptions
        || !global.runOptions.hasOwnProperty("databaseHost")
        || !global.runOptions.hasOwnProperty("databasePort")
        || !global.runOptions.hasOwnProperty("databaseUser")
        || !global.runOptions.hasOwnProperty("databasePassword")
        || !global.runOptions.hasOwnProperty("databaseName"))
        throw new Error("global.runOptions is not defined properly");
}

function createPool(){
    return new Promise(function(resolve, reject){
        databasePool = mysql.createPool({
            host : global.runOptions.databaseHost,
            port : global.runOptions.databasePort,
            user : global.runOptions.databaseUser,
            password: global.runOptions.databasePassword,
            database : global.runOptions.databaseName,
            connectionLimit : 10
        });

        if(databasePool)    resolve();
        else reject(new Error('database pool is not valid'));
    });
}

function checkDatabasePool() {
    return new Promise(function(resolve, reject){
        databasePool.query('SELECT 1 + 1 AS solution', function(err) {
            if (err){
                logging.error('Dexter Database Connection Failed : ' + global.runOptions.getDbUrl())
                reject(err);
            } else {
                logging.info('Dexter Database Connected : ' + global.runOptions.getDbUrl());
                resolve();
            }
        });
    });
}

/**
 * sql will be executed
 *
 * @param sql       sql statement without ';' mark in the end of the statement
 * @param callback   If sql execution is success, callback function will be called. Otherwise never called
 *                      It can be replaced by Promise(bluebird) in the future
 */
exports.exec = function (sql, callback){
    databasePool.getConnection(function(err, connection){
        if(err){
            logging.error(err.message);
            throw err;
        } else if(connection){
            if(connection.isClosed){
                var msg = "Invalid DB Connection : closed";
                logging.error(msg);
                throw msg;
            }

            connection.query(sql, callback);
            logging.debug(sql);
            connection.release();
        } else {
            logging.debug(sql);
            throw "unknown error when executing sql";
        }
    });
};

exports.execTx = function (connection, sql, callback){
    if(connection){
        if(connection.isClosed){
            logging.error("Invalid DB Connection : closed");
        }

        var query = connection.query(sql, callback);
        logging.debug(sql);
    }
};

exports.toSqlValue = function(value){
    if(value == undefined || value == null || value === 'null' || value === ''){
        return "null";
    } else {
        var str = "" + value;
        return "'" + str.replace(/\'/g, "/") + "'";
    }
};

exports.compareEqual = function(value){
    if(value == undefined || value == 'null' || value == ''){
        return " is null ";
    } else {
        return " = '" + value + "'";
    }
};

exports.getDateTime = function(value){
    var retValue;

    if(typeof value.getTime === 'function'){
        retValue = value.getTime();
    } else if (typeof value === 'number'){
        retValue = value;
    } else if(typeof value === 'string') {
        if(value.indexOf("T") !== -1){
            var date = parseDate(value);
            retValue = date.getTime();
        } else {
            retValue = value;
        }
    } else {
        logging.error("Invalid value at database.js getDateTime : " + value + " , typeof:" + typeof value);
        retValue = 0;
    }

    return retValue;
};

exports.getDateTimeEx = function(value) {
    var retValue;
    if(typeof value.getTime === 'function'){
        retValue = "FROM_UNIXTIME(" + Math.floor(value.getTime()/1000) + ")";
    } else if(typeof value === 'number') {
        retValue = "FROM_UNIXTIME(" + Math.floor(value/1000) + ")";
    } else if(typeof value === 'string') {
        if(value.indexOf("T") !== -1){
            var date = parseDate(value);
            retValue = "FROM_UNIXTIME(" + Math.floor(date.getTime()/1000) + ")";
        } else {
            retValue = "FROM_UNIXTIME(" + Math.floor(value/1000) + ")";
        }
    } else {
        logging.error("Invalid value at database.js getDateTimeEx : " + value + " , typeof:" + typeof value)
        retValue = "0";
    }

    return retValue;
};