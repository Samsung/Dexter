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
var database = require("../util/database");
var logging = require('../util/logging');
var _ = require('lodash');
var dutil = require('../util/dexter-util');

exports.getModulePathList = function(req, res){
    var sql = "SELECT modulePath FROM Defect group by modulePath order by modulePath";

    database.execV2(sql)
        .then(function(rows) {
            res.send({status:'ok', rows: rows} );
        })
        .catch(function(err) {
            logging.error(err);
            res.send({status:"fail", errorMessage: err.message});
        });
};

function deleteCodeMetricsFromDB(modulePathList, modulePathListLength, callback){
    var sql = "delete from CodeMetrics";
    _.forEach(modulePathList, function(modulePath, idx){
        if(idx ==0){
            sql += " Where modulePath = " +  database.toSqlValue(modulePath);
        }else {
            sql += " or modulePath = " + database.toSqlValue(modulePath);
        }
    });

    database.execV2(sql)
        .then(function() {
            callback(modulePathList,modulePathListLength, deleteSourceCodeMapFromDB);
        })
        .catch(function(err) {
            logging.error(err);
        });


}

function deleteFunctionMetricFromDB(modulePathList,modulePathListLength, callback){
    var sql = "select * from FunctionMetrics";
    _.forEach(modulePathList, function(modulePath, idx){
        if(idx === 0){
            sql += " Where modulePath = " +  database.toSqlValue(modulePath);
        }else {
            sql += " or modulePath = " + database.toSqlValue(modulePath);
        }
    });

    database.execV2(sql)
        .then(function() {
            callback(modulePathList,modulePathListLength, deleteSnapshotDefectMapFromDB);
        })
        .catch(function(err) {
            logging.error(err);
        });

}

function deleteSourceCodeMapFromDB(modulePathList, modulePathListLength, callback){
    var sql = "delete from SourceCodeMap";
    _.forEach(modulePathList, function(modulePath, idx){
        if(idx === 0){
            sql += " Where modulePath = " +  database.toSqlValue(modulePath);
        }else {
            sql += " or modulePath = " + database.toSqlValue(modulePath);
        }
    });
    database.execV2(sql)
        .then(function() {
            callback(modulePathList,modulePathListLength, deleteDefectFromDB);
        })
        .catch(function(err) {
            logging.error(err);
        });
}

function deleteSnapshotDefectMapFromDB(modulePathList,modulePathListLength, callback){
    var sql = "delete from SnapshotDefectMap";
    _.forEach(modulePathList, function(modulePath, idx){
        if(idx ==0){
            sql += " Where modulePath = " +  database.toSqlValue(modulePath);
        }else {
            sql += " or modulePath = " + database.toSqlValue(modulePath);
        }
    });

    database.execV2(sql)
        .then(function() {
            return ;
        })
        .catch(function(err) {
            logging.error(err);
        });

    callback(modulePathList,modulePathListLength, deleteDefectFromDB);
}

function deleteDefectFromDB(modulePathList){
    var sql = "delete from Defect";
    _.forEach(modulePathList, function(modulePath, idx){
        if(idx == 0){
            sql += " Where modulePath = " +  database.toSqlValue(modulePath);
        }else {
            sql += " or modulePath = " + database.toSqlValue(modulePath);
        }
    });
    database.execV2(sql)
        .then(function() {
        })
        .catch(function(err) {
            logging.error(err);
            res.send({status:"fail", errorMessage: err.message});
        });
}

exports.deleteModulePathList = function(req, res){
    var modulePathList =[];
    if(_.has(req,"query.modulePathList") && req.query.modulePathList) {
        if(req.query.modulePathListLength < 0 ){
            res.send({status:"fail", errorMessage: "Please selected module Path"});
        }
        var temp = req.query.modulePathList;
        if( typeof temp === "string" ){
            modulePathList = temp.split(',');
        }else{
            res.send({status:"fail", errorMessage: "'modulePathList' must be String"});
        }

        var modulePathListLength = req.query.modulePathListLength;
        deleteCodeMetricsFromDB(modulePathList, modulePathListLength, deleteFunctionMetricFromDB);
        res.send({status:'ok'});
    }
};