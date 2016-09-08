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
const Q = require('q');

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

function deleteCodeMetricsFromDB(modulePathList){
    const deferred = Q.defer();
    var sql = "delete from CodeMetrics";

    _.forEach(modulePathList, function(modulePath, idx){
        if(idx == 0){
            sql += " Where modulePath = " +  database.toSqlValue(modulePath);
        }else {
            sql += " or modulePath = " + database.toSqlValue(modulePath);
        }
    });

    database.execV2(sql)
        .then(function() {
            deferred.resolve(modulePathList);
        })
        .catch(function(err) {
            deferred.reject(err);
        });

    return deferred.promise;

}

function deleteFunctionMetricFromDB(modulePathList){
    const deferred = Q.defer();
    var sql = "delete from FunctionMetrics";
    _.forEach(modulePathList, function(modulePath, idx){
        if(idx === 0){
            sql += " Where modulePath = " +  database.toSqlValue(modulePath);
        }else {
            sql += " or modulePath = " + database.toSqlValue(modulePath);
        }
    });

    database.execV2(sql)
        .then(function() {
            deferred.resolve(modulePathList);
        })
        .catch(function(err) {
            deferred.reject(err);
        });
    return deferred.promise;
}

function deleteSourceCodeMapFromDB(modulePathList){
    const deferred = Q.defer();
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
            deferred.resolve(modulePathList);
        })
        .catch(function(err) {
            deferred.reject(err);
        });

    return deferred.promise;
}

function deleteSnapshotDefectMapFromDB(modulePathList){
    const deferred = Q.defer();
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
            deferred.resolve(modulePathList);
        })
        .catch(function(err) {
            deferred.reject(err);
        });

    return deferred.promise;
}

function deleteDefectFromDB(modulePathList){
    const deferred = Q.defer();
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
            deferred.resolve(modulePathList);
        })
        .catch(function(err) {
            deferred.reject(err);
        });

    return deferred.promise;
}

exports.deleteModulePathList = function(req, res){
    let modulePathList =[];
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

        deleteCodeMetricsFromDB(modulePathList)
            .then(deleteFunctionMetricFromDB)
            .then(deleteSourceCodeMapFromDB)
            .then(deleteDefectFromDB)
            .then(deleteSnapshotDefectMapFromDB)
            .then(function(){
                res.send({status:'ok'});
            })
            .catch(function(err) {
                logging.info(`catch` + err.toString());
            });

    }
};