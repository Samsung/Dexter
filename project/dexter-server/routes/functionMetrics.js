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

var database = require("../util/database");
var logging = require('../util/logging');
var dutil = require('../util/dexter-util');

var account = require("../routes/account");
var base64 = require("../routes/base64");

var sql;

var _ = require("lodash");

/*exports.getTotalFunctionMetrics = function(req, res){
    var query = req.query;

    sql = "SELECT"
        + " snapshotId, functionName, fileName , ifnull(modulePath,'') as modulePath, "
        + " functionName, cc, sloc, callDepth, createdDateTime"
        + " FROM FunctionMetrics "
        + " WHERE modulePath " +database.compareEqual(query.modulePath)
        + " and fileName " +database.compareEqual(query.fileName)
        + " and lastYn='Y';";

    console.log(sql);
    database.exec(sql, function (err, result){
        if(err) {
            logging.debug(err.message);
        }
        res.send({status:"ok", result: result});

    });
};*/

exports.getAllFunctionMetrics = function(req, res){
    sql = "SELECT "
        +" snapshotId, functionName, fileName , ifnull(modulePath,'') as modulePath, "
        +" functionName, cc, sloc, callDepth, max(createdDateTime) as createdDateTime "
        +" FROM FunctionMetrics "
        +"WHERE lastYn='Y' group by functionName; ";

    database.exec(sql, function (err, result){
        if(err) {
            logging.debug(err.message);
        }
        res.send({status:"ok", result: result});

    });

};
exports.getTotalFunctionMetrics = function(req, res){
    var query = req.query;
    var functionList = query.functionList;

    sql = "SELECT"
        + " snapshotId, functionName, fileName , ifnull(modulePath,'') as modulePath, "
        + " functionName, cc, sloc, callDepth, createdDateTime"
        + " FROM FunctionMetrics "
        + " WHERE modulePath " +database.compareEqual(query.modulePath)
        + " and fileName " +database.compareEqual(query.fileName)
        + " and lastYn='Y' ";


    for(var i=0;i<functionList.length; i++){
        if(i==0){
            sql += " ( functionName = " +database.toSqlValue(functionName[i])
        }else{
            sql += " OR functionName = " + database.toSqlValue(functionName[i])
        }
        sql = ")";
    }

    database.exec(sql, function (err, result){
        if(err) {
            logging.debug(err.message);
        }
        res.send({status:"ok", result: result});

    });
};

exports.getFunctionMetrics = function(req, res){
    var fileName = req.body.params.fileName;
    var modulePath = req.body.params.modulePath;

    var functionList = req.body.params.functionList;

    sql = "SELECT"
        + " snapshotId, functionName, fileName , ifnull(modulePath,'') as modulePath, "
        + " functionName, cc, sloc, callDepth, MAX(createdDateTime) as createdDateTime"
        + " FROM FunctionMetrics "
        + " WHERE modulePath " +database.compareEqual(modulePath)
        + " and fileName " +database.compareEqual(fileName)
        + " and lastYn='Y' ";

    for(var i = 0;i<functionList.length; i++){
        if(i == 0){
            sql += " and ( functionName = " +database.toSqlValue(functionList[i])
        }else{
            sql += " OR functionName = " + database.toSqlValue(functionList[i])
        }
        if(i == functionList.length -1){
            sql += ")";
        }
    }
    sql +=  " group by functionName;";

    database.exec(sql, function (err, result){
        if(err) {
            logging.debug(err.message);
        }
        res.send({status:"ok", result: result});

    });
};