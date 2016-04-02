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

var codeMetricsFileName = "codeMetric.config.json";
var thresholdObj = require("../"+ codeMetricsFileName);

var sql;

var _ = require("lodash");


exports.getTotalCodeMetrics = function(req, res){
    sql = "SELECT"
        + " count(distinct fileName) as fileCount,"
        + " SUM(CASE WHEN metricName='sloc' THEN metricValue END) as sloc ,"
        + " SUM(CASE WHEN metricName='avgComplexity'  THEN metricValue END) as avgComplexity ,"
        + " SUM(CASE WHEN metricName='classCount'  THEN metricValue END) as classCount ,"
        + " SUM(CASE WHEN metricName='methodCount'  THEN metricValue END) as methodCount"
        + " FROM CodeMetrics WHERE lastYn ='Y';";

    database.exec(sql, function (err, result){
        if(err) {
            logging.debug(err.message);
        }
        res.send({status:"ok", result: result});

    });
};


exports.getCodeMetricsFromClient = function(req, res){
    var query = req.query;


    sql = "SELECT fileName, modulePath, metricName, metricValue "
        + "FROM CodeMetrics "
        + "WHERE fileName = " +database.toSqlValue(query.fileName)
    + " and modulePath " + database.compareEqual(query.modulePath)
    + " and lastYn = 'Y';";

    database.exec(sql, function (err, result){
        if(err) {
            logging.debug(err.message);
        }
        res.send({status:"ok", result: result});

    });
};

exports.getFunctionMetricsForSloc = function(req, res){
    sql ="select fileName, modulePath, metricValue From CodeMetrics where metricName='sloc'"
        +" group by fileName order by metricValue *1 DESC;";

    database.exec(sql, function (err, result){
        if(err) {
            logging.debug(err.message);
        }
        res.send({status:"ok", result: result});

    });
};

exports.getCodeMetricsForCC = function(req, res){
    sql ="select fileName, modulePath, metricValue From CodeMetrics where metricName='avgComplexity'"
        +" group by fileName order by metricValue *1 DESC;";

    database.exec(sql, function (err, result){
        if(err) {
            logging.debug(err.message);
        }
        res.send({status:"ok", result: result});

    });
};


exports.getCodeMetricsForSloc = function(req, res){
    sql = "SELECT snapshotId, functionName, fileName , ifnull(modulePath,'') as modulePath, functionName, sloc,"
        + " MAX(createdDateTime) as createdDateTime FROM FunctionMetrics"
        + " WHERE lastYn='Y' group by functionName" ;

    database.exec(sql, function (err, result){
        if(err) {
            logging.debug(err.message);
        }
        res.send({status:"ok", result: result});

    });
};

exports.getTopCodeMetrics = function(req, res){
    var codeMetricName = req.body.params.codeMetricName;
    sql = "select fileName, modulePath, metricName, metricValue From CodeMetrics "
        +"WHERE metricName ="+ database.toSqlValue(codeMetricName) +" and lastYn = 'Y' order by metricValue *1 DESC LIMIT 3;"

    database.exec(sql, function (err, result){
        if(err) {
            logging.debug(err.message);
        }
        res.send({status:"ok", result: result});
    })
};


exports.getTopCCForCodeMetrics = function(req, res){
    var data = getCodeMetricsQuantity();
    var quantity = data.result;
    var thresholdData = getCodeMetricsThreshold();
    var threshold = thresholdData.result.cc;

    sql = "select fileName, modulePath, metricName, metricValue From CodeMetrics "
        +"WHERE metricName ="+ database.toSqlValue('avgComplexity') +" and lastYn = 'Y' order by metricValue *1 DESC LIMIT "+quantity+";"

    var cationCCList = [];
    database.exec(sql, function (err, result){
        if(err) {
            logging.debug(err.message);
        }
        _(result).forEach(function(idx){
            if(parseInt(idx.metricValue) >= parseInt(threshold.caution)){
                cationCCList.push(idx);
            }else{
                idx.fileName='None';
                idx.metricValue='-';
                cationCCList.push(idx);
            }
        }).value();

        res.send({status:"ok", result: cationCCList});
    })
};

exports.getTopMethodForCodeMetrics = function(req, res){
    var data = getCodeMetricsQuantity();
    var quantity = data.result;
    var thresholdData = getCodeMetricsThreshold();
    var threshold = thresholdData.result.method;

    sql = "select fileName, modulePath, metricName, metricValue From CodeMetrics "
        +"WHERE metricName ="+ database.toSqlValue('methodCount') +" and lastYn = 'Y' order by metricValue *1 DESC LIMIT "+quantity+";"

    var cationMethodList = [];
    database.exec(sql, function (err, result){
        if(err) {
            logging.debug(err.message);
        }
        _(result).forEach(function(idx){
            if(parseInt(idx.metricValue) >= parseInt(threshold)){
                cationMethodList.push(idx);
            }else{
                idx.fileName='None';
                idx.metricValue='-';
                cationMethodList.push(idx);
            }
        }).value();
        res.send({status:"ok", result: cationMethodList});
    })
};

exports.getTopClassForCodeMetrics = function(req, res){
    var data = getCodeMetricsQuantity();
    var quantity = data.result;

    var thresholdData = getCodeMetricsThreshold();
    var threshold = thresholdData.result.class;

    sql = "select fileName, modulePath, metricName, metricValue From CodeMetrics "
        +"WHERE metricName ="+ database.toSqlValue('classCount') +" and lastYn = 'Y' order by metricValue *1 DESC LIMIT "+quantity+";"

    var cationClassList = [];
    database.exec(sql, function (err, result){
        if(err) {
            logging.debug(err.message);
        }
        _(result).forEach(function(idx){
            if(parseInt(idx.metricValue) >= parseInt(threshold)){
                cationClassList.push(idx);
            }else{
                idx.fileName='None';
                idx.metricValue='-';
                cationClassList.push(idx);
            }
        }).value();

        res.send({status:"ok", result: cationClassList});
    })
};

exports.getTopSLOCForCodeMetrics = function(req, res){
    var data = getCodeMetricsQuantity();
    var quantity = data.result;

    var thresholdData = getCodeMetricsThreshold();
    var threshold = thresholdData.result.sloc;

    sql = "select fileName, modulePath, metricName, metricValue From CodeMetrics "
        +"WHERE metricName ="+ database.toSqlValue('sloc') +" and lastYn = 'Y' order by metricValue *1 DESC LIMIT "+quantity+";"

    var cationSLOCList=[];
    database.exec(sql, function (err, result){
        if(err) {
            logging.debug(err.message);
        }

        _(result).forEach(function(idx){
            if(parseInt(idx.metricValue) >= parseInt(threshold)){
                cationSLOCList.push(idx);
            }else{
                idx.fileName='None';
                idx.metricValue='-';
                cationSLOCList.push(idx);
            }
        }).value();
        res.send({status:"ok", result: cationSLOCList});
    })
};

exports.getCodeMetricsThreshold = function(req, res) {
    var threshold = thresholdObj[0].codeMetricThreshold ;
    res.send(200,  {status:"ok", result:threshold});
};
exports.getCodeMetricsQuantity = function(req, res){
    var quantity = thresholdObj[0].codeMetricQuantity;
    res.send(200,  {status:"ok", result:quantity});
};

function getCodeMetricsThreshold(){
    var threshold = thresholdObj[0].codeMetricThreshold ;
    return {status:"ok", result:threshold};
}


function getCodeMetricsQuantity(){
    var quantity = thresholdObj[0].codeMetricQuantity;
    return {status:"ok", result:quantity};
}

