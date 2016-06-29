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

/**
 * Created by min.ho.kim on 2014-03-26.
 */
"use strict";
var mysql = require("mysql");
var logging = require('./logging');
var Q = require('q');
var _databasePool;


var _runOptions;
exports.init = function(runOptions){
    _runOptions = runOptions;

    setRunOptionsImmutable();
    return initDatabase();
};

function setRunOptionsImmutable(){
    Object.freeze(_runOptions);
}

exports.getProjectName = function(req, res){
    res.send({
        result: _runOptions.databaseName
    });
};

exports.getDBInfo = function() {
	return {
		host : _runOptions.databaseHost,
		name : _runOptions.databaseName
	};
};

function initDatabase(){
    return initDbPool();

   /* _databasePool.query('SELECT 1 + 1 As solution', function(err) {
        if(err){
            if(err.code === "ER_BAD_DB_ERROR"){
                logging.error(err);
                installDexterDatabase();
            } else if(err.code === "ECONNREFUSED"){
                logging.error(err);
                logging.error("There is no Mysql Instance. Please check your MySQL Connection : ");
                throw err;
            } else {
                logging.error(err);
                throw err;
            }
        }
    });*/
}

function initDbPool(){
	var deferred = Q.defer();
	
    _databasePool = mysql.createPool({
        /*    debug: true, */
        host : _runOptions.databaseHost,
        port : _runOptions.databasePort,
        user : _runOptions.databaseUser,
        password: _runOptions.databasePassword,
        database : _runOptions.databaseName,
		connectionLimit: 20,
		waitForConnections: true
        /*connectTimeout: 10000 */
    });

    _databasePool.query('SELECT 1 + 1 AS solution', function(err) {
        if (err){
            logging.error('Database Connection Failed : ' + _runOptions.getDbUrl());
			deferred.reject(new Error(err));
        } else {
            logging.info('Database Connected : ' + _runOptions.getDbUrl());
			deferred.resolve('Database Connected');
        }
    });

	return deferred.promise;
}

function runMysqlScript(scripts, index){
    if(scripts.length <= index){
        return;
    }

    var cmd = "mysql -h " + _runOptions.databaseHost
        + " -u " + _runOptions.databaseAdminUser
        + " -p" + _runOptions.databaseAdminPassword + " -e \"" + scripts[index] + "\"";

    var exec = require('child_process').exec;
    exec(cmd, function(error, stdout, stderr){
        if(error){
            if(error.code != 1){
                logging.error(error);
                logging.error("Execute Failed: " + cmd);
                return;
            }

            logging.error(error);
            process.exit(2);
        }

        logging.info("Executed: " + cmd);

        if(++index >= scripts.length){
            execMysqlScript(process.cwd() + "/config/ddl_lines.sql");
        } else {
            runMysqlScript(scripts, index);
        }
    });
}


function execMysqlScript(scriptFilePath){
    var cmd = "mysql -h " + _runOptions.databaseHost
        + " -u " + _runOptions.databaseUser
        + " -p" + _runOptions.databasePassword
        + " " + _runOptions.databaseName + " < " + scriptFilePath;

    var exec = require('child_process').exec;
    exec(cmd, function(error, stdout, stderr){
        if(error){
            /*
            if(error.code != 1){
                logging.error(error);
                logging.error("Execute Failed: " + cmd);
                return;
            }
            */

            logging.error(error);
            process.exit(3);
        }

        logging.info("Executed: " + cmd);
        initDbPool();
    });
}

/*
function execMysqlCmd(script){
    var cmd = "mysql -h " + _runOptions.databaseHost
        + " -u " + _runOptions.databaseAdminUser
        + " -p" + _runOptions.databaseAdminPassword
        + " -e \"" + script + "\"";

    var exec = require('child_process').exec;
    exec(cmd, function(error, stdout, stderr){
        if(error){
            if(error.code != 1){
                logging.error(error);
                logging.error("Execute Failed: " + cmd);
                return;
            }
        }

        logging.info("Executed: " + cmd);
    });
}
*/


exports.getDatabaseName = function(){
    return _runOptions.databaseName;
};

exports.exec = function (sql, args){
	var deferred = Q.defer();
    _databasePool.getConnection(function(err, connection){
        if (err) {
            logging.error(err.message);
			deferred.reject(new Error(err));
        } else {
			if(connection.isClosed){
				var con_err = {message : "Invalid DB Connection : closed"}; 
				logging.error(con_err.message);
				deferred.reject(new Error(con_err));
			} else {
				logging.debug(sql);
				var query = connection.query(sql, args, function(err, rows) {
					connection.release();
					if (err) {
						logging.error(err.message);
						deferred.reject(new Error(err));
					} else {
						deferred.resolve(rows);
					}
				});
			}
		}
    });
	return deferred.promise;
};

exports.escape = function(text) {
	return _databasePool.escape(text);
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
