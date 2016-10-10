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

var JSONbig = require("json-bigint");
var database = require("../util/database");
var logging = require('../util/logging');
var dexterUtil = require('../util/dexter-util');
var _ = require('lodash');

var account = require("../routes/account");
var base64 = require("../routes/base64");

var _sharedDataVersion = {
    falseAlarm:0
};

function setFalseAlarmVersionFromDB() {
    sql = "SELECT version FROM SharedDataVersion WHERE name = 'FalseAlarm' ";

    database.exec(sql, function (err, result){
        if(err) {
            logging.error(err.message);
        } else if(result && result.length == 1) {
            _sharedDataVersion.falseAlarm = result[0].version;
        } else {
            logging.error('cannot set the shared data version of False Alarm, because result value is ' + result);
        }
    });
};

exports.getFalseAlarmVersion = function(req, res){
    res.send({status:'ok', version: _sharedDataVersion.falseAlarm});
};


function sleep(seconds, callback){
    "use strict";

    setTimeout(function() {
        if(callback !== undefined){
            callback();
        }
    }, seconds * 1000);
};

function sleepEx(ms){
    "use strict";

    var now = new Date().getTime();
    while(new Date().getTime() < now + ms){
    }
};

exports.getProjectDefectStatus = function (req, res){
    var statusCode = req.query.statusCode;

    var sql = "select "
        + "'all-module' as modulePath, count(did) as defectCount, "
        + "sum(if(statusCode = 'NEW', 1, 0)) as newCount, "
        + "sum(if(statusCode = 'FIX', 1, 0)) as fixCount, "
        + "sum(if(statusCode = 'EXC', 1, 0)) as excCount "
        + "from Defect ";

    if(statusCode != undefined && statusCode != ""){
        sql += "where statusCode = " + statusCode;
    }

    database.exec(sql, function (err, result){
        if(err){
            logging.error(err.message);
            res.send(401, {status:"fail", errorMessage: err.message});
            return ;
        }
        if(result){
            res.send(200, result);
        }
    });
};


exports.getProjectDefectStatusV2 = function (req, res){
    var statusCode = req.query.statusCode;

    var sql = "select "
        + "'all-module' as modulePath, count(did) as defectCount, "
        + "sum(if(statusCode = 'NEW', 1, 0)) as newCount, "
        + "sum(if(statusCode = 'FIX', 1, 0)) as fixCount, "
        + "sum(if(statusCode = 'EXC', 1, 0)) as excCount "
        + "from Defect ";

    if(statusCode != undefined && statusCode != ""){
        sql += "where statusCode = " + statusCode;
    }

    return database.execV2(sql)
        .then(function(rows) {
            res.send({status:'ok', rows: rows} );
        })
        .catch(function(err) {
            logging.error(err);
            res.send({status:"fail", errorMessage: err.message});
        });
};

// todo : Consider View for performance
exports.getModuleDefectStatus = function (req, res){
    var statusCode = req.query.statusCode;

    var sql = "select "
        + "ifnull(modulePath, '') as modulePath, count(did) as defectCount, "
        + "sum(if(statusCode = 'NEW', 1, 0)) as newCount, "
        + "sum(if(statusCode = 'FIX', 1, 0)) as fixCount, "
        + "sum(if(statusCode = 'EXC', 1, 0)) as excCount "
        + "from Defect ";

    if(statusCode != undefined && statusCode != ""){
        sql += "where statusCode = " + database.toSqlValue(statusCode);
    }

    sql += " group by modulePath order by modulePath ";

    database.exec(sql, function (err, result){
        if(err){
            logging.error(err.message);
            res.send(401, {status:"fail", errorMessage: err.message});
        }
        if(result){
            res.send(200, result);
        }
    });
};

// todo : Consider View for performance
exports.getModuleDefectStatusV2 = function (req, res){
    var statusCode = req.query.statusCode;

    var sql = "select "
        + "ifnull(modulePath, '') as modulePath, count(did) as defectCount, "
        + "sum(if(statusCode = 'NEW', 1, 0)) as newCount, "
        + "sum(if(statusCode = 'FIX', 1, 0)) as fixCount, "
        + "sum(if(statusCode = 'EXC', 1, 0)) as excCount "
        + "from Defect ";

    if(statusCode != undefined && statusCode != ""){
        sql += "where statusCode = " + database.toSqlValue(statusCode);
    }

    sql += " group by modulePath order by modulePath ";

    database.execV2(sql)
        .then(function(rows) {
            res.send({status:'ok', rows: rows} );
        })
        .catch(function(err) {
            logging.error(err);
            res.send({status:"fail", errorMessage: err.message});
        });
};

// todo : Consider View for performance
exports.getFileDefectStatus = function (req, res){

    var statusCode = req.query.statusCode;

    var sql = "select "
        + "ifnull(modulePath, '') as modulePath, fileName, count(did) as defectCount, "
        + "sum(if(statusCode = 'NEW', 1, 0)) as newCount, "
        + "sum(if(statusCode = 'FIX', 1, 0)) as fixCount, "
        + "sum(if(statusCode = 'EXC', 1, 0)) as excCount "
        + "from Defect ";

    if(statusCode != undefined && statusCode != ""){
        sql += "where statusCode = " + database.toSqlValue(statusCode);
    }

    sql += " group by modulePath, fileName order by modulePath, fileName";

    database.exec(sql, function (err, result){
        if(err){
            logging.error(err.message);
            res.send(401, {status:"fail", errorMessage: err.message});
        }
        if(result){
            res.send(200, result);
        }
    });
};


exports.getFileDefectStatusV2 = function (req, res){

    var modulePath = req.query.modulePath;

    var sql = "select "
        + "ifnull(modulePath, '') as modulePath, fileName, count(did) as defectCount, "
        + "sum(if(statusCode = 'NEW', 1, 0)) as newCount, "
        + "sum(if(statusCode = 'FIX', 1, 0)) as fixCount, "
        + "sum(if(statusCode = 'EXC', 1, 0)) as excCount "
        + "from Defect ";

    if(modulePath != undefined && modulePath != ""){
        sql += " where modulePath = " + database.toSqlValue(modulePath);
    } else {
        sql += " where modulePath is null ";
    }

    sql += " group by modulePath, fileName order by modulePath, fileName";

    database.execV2(sql)
        .then(function(rows) {
            res.send({status:'ok', rows: rows} );
        })
        .catch(function(err) {
            logging.error(err);
            res.send({status:"fail", errorMessage: err.message});
        });

};


exports.getModuleAndFileName = function (req, res) {
    var sql = "select ifnull(modulePath,'') as modulePath, fileName from Defect GROUP BY modulePath, fileName; ";
    database.exec(sql, function (err, result){
        if(err){
            logging.error(err.message);
            res.send(401, {status:"fail", errorMessage: err.message});
        }
        if(result){
            res.send(200, result);
        }
    });
};

exports.getDefectForSecurity = function(req, res){
    var sql = "select "
        + " did, toolName, language, checkerCode, ifnull(modulePath,'') as modulePath, fileName, ifnull(className,'') as className,"
        + " ifnull(methodName,'') as methodName, severityCode, ifnull(categoryName,'') as categoryName, statusCode, message, "
        + " createdDateTime, modifiedDateTime, creatorNo, modifierNo, "
        + " (select count(B.oid) from Occurence B where B.did = A.did) as occurenceCount, "
        + " ifnull((select group_concat(if(B.startLine = -1,'1', B.startLine) ORDER BY B.startLine SEPARATOR '|') from Occurence B where B.did = A.did),'') as occurenceLine,"
        + " (select userId from Account where userNo = A.creatorNo) as creatorId, "
        + " (select userId from Account where userNo = A.modifierNo) as modifierId "
        + " from "
        + " Defect A WHERE categoryName='SECURITY' order by checkerCode, fileName, className, methodName ";

    return database.execV2(sql)
        .then(function(rows) {
            res.send({status:'ok', rows: rows} );
        })
        .catch(function(err) {
            logging.error(err);
            res.send({status:"fail", errorMessage: err.message});
        });

};

exports.getDefectForCSV = function(req, res){
    var sql = "select "
        + "did, toolName, language, checkerCode, ifnull(modulePath,'') as modulePath, fileName, ifnull(className,'') as className,"
        + "ifnull(methodName,'') as methodName, severityCode, ifnull(categoryName,'') as categoryName, statusCode, message, "
        + "createdDateTime, modifiedDateTime, creatorNo, modifierNo, "
        + "(select count(B.oid) from Occurence B where B.did = A.did) as occurenceCount, "
        + "ifnull((select group_concat(if(B.startLine = -1,'1', B.startLine) ORDER BY B.startLine SEPARATOR '|') from Occurence B where B.did = A.did),'') as occurenceLine,"
        + "(select userId from Account where userNo = A.creatorNo) as creatorId, "
        + "(select userId from Account where userNo = A.modifierNo) as modifierId "
        + "from "
        + "Defect A ";

    sql += " order by checkerCode, fileName, className, methodName ";
    return database.execV2(sql)
        .then(function(rows) {
            res.send({status:'ok', rows: rows} );
        })
        .catch(function(err) {
            logging.error(err);
            res.send({status:"fail", errorMessage: err.message});
        });
};

exports.getDidDefectForCSV = function(req, res){
    var sql = "select "
        + "did, toolName, language, checkerCode, ifnull(modulePath,'') as modulePath, fileName, ifnull(className,'') as className,"
        + "ifnull(methodName,'') as methodName, severityCode, ifnull(categoryName,'') as categoryName, statusCode, message, "
        + "createdDateTime, modifiedDateTime, creatorNo, modifierNo, "
        + "(select count(B.oid) from Occurence B where B.did = A.did) as occurenceCount, "
        + "ifnull((select group_concat(if(B.startLine = -1,'1', B.startLine) ORDER BY B.startLine SEPARATOR '|') from Occurence B where B.did = A.did),'') as occurenceLine,"
        + "(select userId from Account where userNo = A.creatorNo) as creatorId, "
        + "(select userId from Account where userNo = A.modifierNo) as modifierId "
        + "from "
        + "Defect A ";

    sql += " order by checkerCode, fileName, className, methodName ";
    return database.execV2(sql)
        .then(function(rows) {
            res.send({status:'ok', rows: rows} );
        })
        .catch(function(err) {
            logging.error(err);
            res.send({status:"fail", errorMessage: err.message});
        });
};

exports.getSnapshotDefectForCSV = function(req, res){
    if(req == undefined || req.query== undefined || req.query.snapshotId == undefined){
        res.send({status:"fail", errorMessage: "Input(parameter) error"});
        return;
    }

    var snapshotId = req.query.snapshotId;

    var sql = "SELECT "
        + "snapshotId, did, toolName, language, checkerCode, fileName, ifnull(modulePath,'') as modulePath , ifnull(className,'') as className ,"
        + "ifnull(methodName,'') as methodName, severityCode, ifnull(categoryName,'') as categoryName, statusCode, message, createdDateTime, modifiedDateTime, creatorNo, modifierNo,"
        + "(select count(B.startLine) from SnapshotOccurenceMap B where B.did = A.did and snapshotId = "+ database.toSqlValue(snapshotId)+ ") as occurenceCount,"
        + " ifnull((select group_concat(if(B.startLine = -1,'1', B.startLine) ORDER BY B.startLine SEPARATOR '|') "
        + "from SnapshotOccurenceMap B where B.did = A.did and snapshotId = "+ database.toSqlValue(snapshotId)+ "),'') as occurenceLine,"
        + "(select statusCode from Defect B where B.did = A.did) as currentStatusCode,"
        + "(select userId from Account where userNo = A.creatorNo) as creatorId,"
        + "(select userId from Account where userNo = A.modifierNo) as modifierId, "
        + "ifnull(chargerNo,'') as chargerNo, ifnull(reviewerNo,'') as reviewerNo, ifnull(approvalNo,'') as approvalNo "
        + "From SnapshotDefectMap AS A WHERE snapshotId =" + database.toSqlValue(snapshotId) ;

   /* database.exec(sql, function (err, result){
        if(err){
            logging.error(err.message);
            res.send(401, {status:"fail", errorMessage: err.message});
        }
        if(result){
            res.send(200, result);
        }
    });*/

    return database.execV2(sql)
        .then(function(rows) {
            res.send({status:'ok', rows: rows} );
        })
        .catch(function(err) {
            logging.error(err);
            res.send({status:"fail", errorMessage: err.message});
        });

};



exports.getSnapshotDefectForSecurity = function(req, res){
    if(req == undefined || req.query== undefined || req.query.snapshotId == undefined){
        res.send({status:"fail", errorMessage: "Input(parameter) error"});
        return;
    }

    var snapshotId = req.query.snapshotId;

    var sql = "SELECT "
        + "snapshotId, did, toolName, language, checkerCode, fileName, ifnull(modulePath,'') as modulePath , ifnull(className,'') as className ,"
        + "ifnull(methodName,'') as methodName, severityCode, ifnull(categoryName,'') as categoryName, statusCode, message, createdDateTime, modifiedDateTime, creatorNo, modifierNo,"
        + "(select count(B.startLine) from SnapshotOccurenceMap B where B.did = A.did and snapshotId = "+ database.toSqlValue(snapshotId)+ ") as occurenceCount,"
        + " ifnull((select group_concat(if(B.startLine = -1,'1', B.startLine) ORDER BY B.startLine SEPARATOR '|') "
        + "from SnapshotOccurenceMap B where B.did = A.did and snapshotId = "+ database.toSqlValue(snapshotId)+ "),'') as occurenceLine,"
        + "(select statusCode from Defect B where B.did = A.did) as currentStatusCode,"
        + "(select userId from Account where userNo = A.creatorNo) as creatorId,"
        + "(select userId from Account where userNo = A.modifierNo) as modifierId, "
        + "ifnull(chargerNo,'') as chargerNo, ifnull(reviewerNo,'') as reviewerNo, ifnull(approvalNo,'') as approvalNo "
        + "From SnapshotDefectMap AS A WHERE snapshotId =" + database.toSqlValue(snapshotId) + " and categoryName = 'SECURITY'" ;

    return database.execV2(sql)
        .then(function(rows) {
            res.send({status:'ok', rows: rows} );
        })
        .catch(function(err) {
            logging.error(err);
            res.send({status:"fail", errorMessage: err.message});
        });
};


exports.getDefectsByModuleAndFileForDid = getDefectsByModuleAndFileForDid;

function getDefectsByModuleAndFileForDid(req, res){
    var did = req.params.did;

    var sql = "select "
        + "did, toolName, language, checkerCode, ifnull(modulePath,'') as modulePath, fileName, ifnull(className,'') as className,"
        + "ifnull(methodName,'') as methodName, severityCode, ifnull(categoryName,'') as categoryName, statusCode, message, "
        + "createdDateTime, modifiedDateTime, creatorNo, modifierNo, "
        + "(select count(B.oid) from Occurence B where B.did = A.did) as occurenceCount, "
        + "ifnull((select group_concat(if(B.startLine = -1,'1', B.startLine) ORDER BY B.startLine SEPARATOR '|') from Occurence B where B.did = A.did),'') as occurenceLine,"
        + "(select userId from Account where userNo = A.creatorNo) as creatorId, "
        + "(select userId from Account where userNo = A.modifierNo) as modifierId "
        + "from "
        + "Defect A ";

    if( did !== undefined || did !== null || did == ""){
        sql += "WHERE did =" + database.toSqlValue(did);
    }

    return database.exec(sql, function (err, result){
        if(err){
            logging.error(err.message);
            res.send(401, {status:"fail", errorMessage: err.message});
        }
        if(result){
            res.send({status:'ok', defect: result});
        }
    });
}


exports.getDefectsByModuleAndFileV2 = getDefectsByModuleAndFileV2;

function getDefectsByModuleAndFileV2(req, res){
    var did = req.query.did;
    var modulePath = base64.decode(req.query.modulePath);
    var fileName = req.query.fileName;
    var statusCode = req.query.statusCode;
    var severityCode = req.query.severityCode;
    var categoryName = req.query.categoryName;
    var checkerCode = req.query.checkerCode;
    var modifierNo = req.query.modifierNo;
    var currentPage = req.query.currentPage;
    var pageSize = req.query.pageSize;
    var message = req.query.message;

    var sql = "select "
        + "did, toolName, language, checkerCode, ifnull(modulePath,'') as modulePath, fileName, ifnull(className,'') as className,"
        + "ifnull(methodName,'') as methodName, severityCode, ifnull(categoryName,'') as categoryName, statusCode, message, "
        + "createdDateTime, modifiedDateTime, creatorNo, modifierNo, "
        + "(select count(B.oid) from Occurence B where B.did = A.did) as occurenceCount, "
        + "ifnull((select group_concat(if(B.startLine = -1,'1', B.startLine) ORDER BY B.startLine SEPARATOR '|') from Occurence B where B.did = A.did),'') as occurenceLine,"
        + "(select userId from Account where userNo = A.creatorNo) as creatorId, "
        + "(select userId from Account where userNo = A.modifierNo) as modifierId "
        + "from "
        + "Defect A ";

    if(modulePath != "" || fileName != "" || statusCode != "" || severityCode != "" || checkerCode != ""
        ||  (modifierNo != "" && modifierNo != undefined) || (did != "" && did != undefined ) ){
        sql += " where 1 = 1 ";
    }
    if(did != "" && did != undefined ){
        sql += "and did = " + did;
    }

    if(modulePath == "##HAS-NO-MODULE##"){
        sql += "and modulePath is null ";
    } else if(modulePath != ""){
        sql += " and modulePath = " + database.toSqlValue(modulePath);
    }

    if(fileName != ""){
        sql += " and fileName = " + database.toSqlValue(fileName);
    }

    if(statusCode != ""){
        sql += " and statusCode = " + database.toSqlValue(statusCode);
    }

    if(severityCode != ""){
        sql += " and severityCode = " + database.toSqlValue(severityCode);
    }

    if(severityCode != ""){
        sql += " and categoryName = " + database.toSqlValue(categoryName);
    }

    if(checkerCode != ""){
        sql += "and checkerCode = " + database.toSqlValue(checkerCode);
    }

    if(modifierNo != ""){
        sql += "and modifierNo = " + modifierNo;
    }

    sql += " order by checkerCode, fileName, className, methodName ";


    if(currentPage != "" && pageSize != ""){
        var beginOffset = (currentPage - 1) * pageSize;
        sql += " LIMIT " + beginOffset + ", " + pageSize;
    }

    database.exec(sql, function (err, result){
        if(err){
            logging.error(err.message);
            res.send(401, {status:"fail", errorMessage: err.message});
        }
        if(result){
            res.send(200, result);
        }
    });
}

// todo : Consider View for performance
function getDefectsByModuleAndFile(req, res){
    var did = req.query.did;
    var modulePath = base64.decode(req.query.modulePath);
    var fileName = req.query.fileName;
    var statusCode = req.query.statusCode;
    var severityCode = req.query.severityCode;
    var checkerCode = req.query.checkerCode;
    var modifierNo = req.query.modifierNo;
    var currentPage = req.query.currentPage;
    var pageSize = req.query.pageSize;
    var message = req.query.message;

    var sql = "select "
        + "did, toolName, language, checkerCode, ifnull(modulePath,'') as modulePath, fileName, ifnull(className,'') as className,"
        + "ifnull(methodName,'') as methodName, severityCode, statusCode, message, "
        + "createdDateTime, modifiedDateTime, creatorNo, modifierNo, "
        + "(select count(B.oid) from Occurence B where B.did = A.did) as occurenceCount, "
        + "ifnull((select group_concat(if(B.startLine = -1,'1', B.startLine) ORDER BY B.startLine SEPARATOR '|') from Occurence B where B.did = A.did),'') as occurenceLine,"
        + "(select userId from Account where userNo = A.creatorNo) as creatorId, "
        + "(select userId from Account where userNo = A.modifierNo) as modifierId "
        + "from "
        + "Defect A ";

    if(modulePath != "" || fileName != "" || statusCode != "" || severityCode != "" || checkerCode != ""
        ||  (modifierNo != "" && modifierNo != undefined) || (did != "" && did != undefined ) ){
        sql += " where 1 = 1 ";
    }
    if(did != "" && did != undefined ){
        sql += "and did = " + did;
    }

    if(modulePath == "##HAS-NO-MODULE##"){
        sql += "and modulePath is null ";
    } else if(modulePath != ""){
        sql += " and modulePath = " + database.toSqlValue(modulePath);
    }

    if(fileName != ""){
        sql += " and fileName = " + database.toSqlValue(fileName);
    }

    if(statusCode != ""){
        sql += " and statusCode = " + database.toSqlValue(statusCode);
    }

    if(severityCode != ""){
        sql += " and severityCode = " + database.toSqlValue(severityCode);
    }

    if(checkerCode != ""){
        sql += "and checkerCode = " + database.toSqlValue(checkerCode);
    }

    if(modifierNo != ""){
        sql += "and modifierNo = " + modifierNo;
    }

    sql += " order by checkerCode, fileName, className, methodName ";

    if(currentPage != "" && pageSize != ""){
        var beginOffset = (currentPage - 1) * pageSize;
        sql += " LIMIT " + beginOffset + ", " + pageSize;
    }

    database.exec(sql, function (err, result){
        if(err){
            logging.error(err.message);
            res.send(401, {status:"fail", errorMessage: err.message});
        }
        if(result){
            res.send(200, result);
        }
    });
}

exports.getDefectsByModuleAndFile = getDefectsByModuleAndFile;

exports.getDefectCountByModuleAndFile = function (req, res){
    var sql = "select count(did) as defectCount from Defect ";

    database.exec(sql, function (err, result){
        if(err){
            logging.error(err.message);
            res.send(401, {status:"fail", errorMessage: err.message});
        } else if(result && result[0].defectCount){
            res.send({"defectCount": result[0].defectCount});
        } else {
            res.send(401, {status:"fail", errorMessage: "unknown error"});
        }
    });
};

exports.getDefectCount = function (req, res){
    var sql = "select count(did) as defectCount from Defect";

    database.exec(sql, function (err, result){
        if(err){
            logging.error(err.message);
            res.send(401, {status:"fail", errorMessage: err.message});
        } else if(result && result[0].defectCount){
            res.send({"defectCount": result[0].defectCount});
        } else {
            res.send(401, {status:"fail", errorMessage: "unknown error"});
        }
    });
};

exports.changeDefectStatus = function (req, res){
    if(req == undefined || req.body == undefined || req.body.defect == undefined || req.currentUserId == undefined){
        res.send({status:"fail", errorMessage: "No Data or No currentUserId"});
		return;
    }

    var defect;
	try {
		defect = JSONbig.parse(req.body.defect);
	} catch (e) {
		logging.error(e);
		res.send({status:"fail", errorMessage: "defect is not json raw format"});
		return;
	}
	
    var defectStatus = req.body.defectStatus;
    var userNo = account.getUserNo(req.currentUserId);

	res.send({status:"ok", message: "Input Data was taken and being processed"});

    var sql = "UPDATE Defect SET "
        + " statusCode = " + database.toSqlValue(defectStatus)
        + " , modifiedDateTime = now()"
        + " , modifierNo = " + userNo
        + " WHERE "
        + "     toolName = " + database.toSqlValue(defect.toolName)
        + "     and language = " + database.toSqlValue(defect.language)
        + "     and checkerCode = " + database.toSqlValue(defect.checkerCode)
        + "     and fileName = " + database.toSqlValue(defect.fileName)
        + "     and modulePath " + database.compareEqual(defect.modulePath)
        + "     and className " + database.compareEqual(defect.className)
        + "     and methodName " + database.compareEqual(defect.methodName);

    database.exec(sql, function (err, result){
        if(err){
            logging.error(err.message);
        }
    });
};

exports.changeDefectToDismiss = function (req, res){
    if(req == undefined || req.body == undefined || req.body.params == undefined ||
	   req.body.params.didList == undefined || req.currentUserId == undefined){
        res.send({status:"fail", errorMessage: "No DataList or No currentUserId"})
		return;
    }
	
    for(var i=0; i<  req.body.params.didList.length ; i++) {
        var arrDid = req.body.params.didList[i];
        var userNo = account.getUserNo(req.currentUserId);
        var sql = "UPDATE Defect SET "
            + " statusCode = 'EXC'"
            + " , modifiedDateTime = now()"
            + " , modifierNo = " + userNo
            + " WHERE "
            + "     did = " + database.toSqlValue(arrDid);

        database.exec(sql, function (err, result) {
            if (err) {
                logging.error(err.message);
            }
            if (result) {
                res.send(result);
            }
        });
    }
};

exports.changeDefectToNew = function (req, res){
    if(req == undefined || req.body == undefined || req.body.params == undefined ||
	   req.body.params.didList == undefined || req.currentUserId == undefined){
        res.send({status:"fail", errorMessage: "No DataList or No currentUserId"})
		return;
    }
    for(var i=0; i<  req.body.params.didList.length ; i++) {
        var arrDid = req.body.params.didList[i];
        var userNo = account.getUserNo(req.currentUserId);

        var sql = "UPDATE Defect SET "
            + " statusCode = 'NEW'"
            + " , modifiedDateTime = now()"
            + " , modifierNo = " + userNo
            + " WHERE "
            + "     did = " + database.toSqlValue(arrDid);

        database.exec(sql, function (err, result) {
            if (err) {
                logging.error(err.message);
            }
            if (result) {
                res.send(result);
            }
        });
    }
};


exports.changeDefectToFix = function (req, res){
    if(req == undefined || req.body == undefined || req.body.params == undefined ||
	   req.body.params.didList == undefined || req.currentUserId == undefined){
        res.send({status:"fail", errorMessage: "No DataList or No currentUserId"});
		return;
    }

    for(var i=0; i<  req.body.params.didList.length ; i++) {
        var arrDid = req.body.params.didList[i];

        //var defectStatus = req.body.defectStatus;
        var userNo = account.getUserNo(req.currentUserId);

        var sql = "UPDATE Defect SET "
            + " statusCode = 'FIX'"
            + " , modifiedDateTime = now()"
            + " , modifierNo = " + userNo
            + " WHERE "
            + "     did = " + database.toSqlValue(arrDid);

        database.exec(sql, function (err, result) {
            if (err) {
                logging.error(err.message);
            }
            if (result) {
                res.send(result);
            }
        });
    }
};


exports.getAllFalseAlarm = function (req, res){
    var sql = "SELECT "
        + " fid, toolName, language, ifnull(modulePath,'') as modulePath, "
        + " fileName, ifnull(className,'') as className, ifnull(methodName,'') as methodName, checkerCode, "
        + " filterType, createdDateTime, creatorNo "
        + "FROM DefectFilter WHERE filterType = 'F'";

    database.exec(sql, function (err, result){
        if(err){
            logging.error(err.message);
            res.send({status:"fail", errorMessage: err.message});
        }
        if(result){
            res.send(result);
        }
    });
};

exports.getAllFalseAlarmList = function (req, res){
    var sql = "SELECT "
        + " group_concat(distinct language) as languageList, "
        + " group_concat(distinct toolName) as toolNameList, "
        + " group_concat(distinct modulePath) as modulePathList, "
        + " group_concat(distinct fileName) as fileNameList, "
        + " group_concat(distinct className) as classNameList, "
        + " group_concat(distinct methodName) as methodNameList, "
        + " group_concat(distinct checkerCode) as checkerCodeList "
        + " From DefectFilter WHERE filterType = 'F' ";

    database.exec(sql, function (err, result){
        if(err){
            logging.error(err.message);
            res.send({status:"fail", errorMessage: err.message});
        }
        if(result && result.length == 1){
            res.send(result[0]);
        }
    });
};


function increaseSharedDataVersion(dataName, modifierNo){
    var sql = "SELECT version FROM SharedDataVersion WHERE name = " + database.toSqlValue(dataName);

    database.exec(sql, function(err, results){
        if(err){
            logging.debug(err.message);
        }
        if(!results){
            logging.error("unknown error");
        }

        if(results.length == 0){
            sql = "INSERT INTO SharedDataVersion (version, name, modifiedDateTime, modifierNo) "
                + "VALUES ( 1, " + database.toSqlValue(dataName) + ", now(), " + modifierNo + ")";
            database.exec(sql, function (err){
                if(err){
                    logging.debug(err.message);
                }
            })
        } else {
            sql = " UPDATE SharedDataVersion "
                + " SET version = version + 1, modifiedDateTime = now(), modifierNo = " + modifierNo
                + " WHERE name = " + database.toSqlValue(dataName);
            database.exec(sql, function (err){
                if(err){
                    logging.debug(err.message);
                }
                _sharedDataVersion.falseAlarm = results[0].version + 1;
            })
        }
    });
}

exports.addFalseAlarm = function (req, res){
    if(req == undefined || req.body == undefined || req.currentUserId == undefined){
        res.send({status:"fail", errorMessage: "No currentUserId"})
		return;
    }

    var userNo = account.getUserNo(req.currentUserId);
    var sql;

    if(req.body.defect != undefined){
        var defect = JSONbig.parse(req.body.defect);

        sql = "SELECT fid From DefectFilter"
            + " WHERE filterType = 'F' "
            + " and toolName = " + database.toSqlValue(defect.toolName)
            + " and language = " + database.toSqlValue(defect.language)
            + " and checkerCode = " + database.toSqlValue(defect.checkerCode)
            + " and fileName = " + database.toSqlValue(defect.fileName)
            + " and modulePath " + database.compareEqual(defect.modulePath)
            + " and className " + database.compareEqual(defect.className)
            + " and methodName " + database.compareEqual(defect.methodName);

        database.exec(sql, function (err, result){
            if(err){
                logging.debug(err.message);
                res.send({status:"fail", errorMessage: "can't select defect filter"});
            }

            if(result && result.length == 0){
                insertDefectFilter(res, defect, userNo, "F");
            } else {
                res.send({status:"ok", message: "already has the same filter"});
            }
        });
    } else if(req.body.defectFilter != undefined){
        var defectFilter = JSONbig.parse(req.body.defectFilter);

        sql = "SELECT fid From DefectFilter"
            + " WHERE filterType = 'F' "
            + " and toolName = " + database.toSqlValue(defectFilter.toolName)
            + " and language = " + database.toSqlValue(defectFilter.language)
            + " and checkerCode = " + database.toSqlValue(defectFilter.checkerCode)
            + " and fileName = " + database.toSqlValue(defectFilter.fileName)
            + " and modulePath " + database.compareEqual(defectFilter.modulePath)
            + " and className " + database.compareEqual(defectFilter.className)
            + " and methodName " + database.compareEqual(defectFilter.methodName);

        database.exec(sql, function (err, result){
            if(err){
                logging.debug(err.message);
                res.send({status:"fail", errorMessage: "can't select defect filter"});
            }

            if(result && result.length == 0){
                insertDefectFilter(res, defectFilter, userNo, "F");
            } else {
                res.send({status:"ok", message: "already has the same filter"});
            }
        });
    }  else if(req.body.params != undefined && req.body.params.did != undefined){
        sql = "SELECT toolName, language, checkerCode, fileName, modulePath, className, methodName "
            + "FROM Defect WHERE did = " + req.body.params.did;

        database.exec(sql, function (err, result){
            "use strict";
            if(err){
                logging.error(err.message);
                res.send({status:"fail", errorMessage: "can't find defect by id : " + req.body.params.did});
                return;
            }

            if(result){
                sql = "SELECT * From DefectFilter"
                    + " WHERE filterType = 'F' "
                    + " and toolName = " + database.toSqlValue(result[0].toolName)
                    + " and language = " + database.toSqlValue(result[0].language)
                    + " and checkerCode = " + database.toSqlValue(result[0].checkerCode)
                    + " and fileName = " + database.toSqlValue(result[0].fileName)
                    + " and modulePath " + database.compareEqual(result[0].modulePath)
                    + " and className " + database.compareEqual(result[0].className)
                    + " and methodName " + database.compareEqual(result[0].methodName);

                defect = result[0];

                database.exec(sql, function (err, result){
                    if(err || !result){
                        logging.debug(err.message);
                        res.send({status:"fail", errorMessage: "can't select defect filter"});
                        return;
                    }

                    if(result && result.length == 0){
                        insertDefectFilter(res, defect, userNo, "F");
                    } else {
                        res.send({status:"ok", message: "already has the same filter"});
                    }
                });
            }
        })
    } else {
        res.send({status:"fail", errorMessage: "No Data"})
    }
};

function insertDefectFilter(res, defect, userNo, filterType){
    var sql = "INSERT INTO DefectFilter "
        + " (toolName, language, modulePath, fileName, className, methodName, checkerCode, filterType, createdDateTime, creatorNo) "
        + " VALUES ( " +  database.toSqlValue(defect.toolName)
        + " , " + database.toSqlValue(defect.language)
        + " , " + database.toSqlValue(defect.modulePath)
        + " , " + database.toSqlValue(defect.fileName)
        + " , " + database.toSqlValue(defect.className)
        + " , " + database.toSqlValue(defect.methodName)
        + " , " + database.toSqlValue(defect.checkerCode)
        + " , " + database.toSqlValue(filterType)
        + " , now() "
        + " , " + userNo + ")";

    database.exec(sql, function (err, rows){
        if(err){
            logging.error(err.message);
            res.send({status:"fail", errorMessage: err.message});
        } else {
            increaseSharedDataVersion("FalseAlarm", userNo);
            res.send({status:"ok", message: "successful"});
        }
    });
}

exports.removeFalseAlarm = function (req, res){
    if(req == undefined || req.body == undefined || req.currentUserId == undefined){
        res.send({status:"fail", errorMessage: "No Data or No currentUserId"})
    }

    var userNo = account.getUserNo(req.currentUserId);
    if(req.body.defect){
        var defect = JSONbig.parse(req.body.defect);
        deleteDefectFilter(res, defect, userNo, "F");
    } else if(req.body.params && req.body.params.did){
        deleteDefectFilterByDid(res, req.body.params.did, userNo, "F");
    } else {
        res.send({status:"fail", errorMessage: "parameter is not valid"});
    }
};




function deleteDefectFilter(res, defect, userNo, filterType){
    var sql = "DELETE FROM DefectFilter "
        + " WHERE filterType = " + database.toSqlValue(filterType)
        + " and toolName = " +  database.toSqlValue(defect.toolName)
        + " and language = " +  database.toSqlValue(defect.language)
        + " and checkerCode = " + database.toSqlValue(defect.checkerCode)
        + " and fileName = " + database.toSqlValue(defect.fileName)
        + " and modulePath " +  database.compareEqual(defect.modulePath)
        + " and className " + database.compareEqual(defect.className)
        + " and methodName " + database.compareEqual(defect.methodName);

    database.exec(sql, function (err, rows){
        if(err){
            logging.error(err.message);
            res.send({status:"fail", errorMessage: err.message});
        }

        if(rows){
            logging.debug("a defect-filter is deleted.");
            increaseSharedDataVersion("FalseAlarm", userNo);
            res.send({status:"ok", message: "successful"});
        }
    });
}

function deleteDefectFilterByDid(res, did, userNo, filterType){
    var sql = "SELECT * FROM Defect WHERE did = " + did;

    database.exec(sql, function(err, result){
        if(err){
            logging.error(err.message);
            res.send({status:"fail", errorMessage: err.message});
        }

        if(result && result[0]){
            deleteDefectFilter(res, result[0], userNo, filterType);
        }
    });
}

exports.removeFileTree = function (req, res){
    if(req == undefined || req.body == undefined || req.body.params == undefined ||
	   req.body.params.fileList == undefined || req.body.params.modulePath == undefined){
        res.send({status:"fail", errorMessage: "No modulePath or No fileName"})
		return;
    }

    var modulePath =  base64.decode(req.body.params.modulePath);

    var sql = "DELETE FROM SourceCodeMap WHERE 0 ";

    for(var i=0; i<  req.body.params.fileList.length ; i++) {
        var fileName = req.body.params.fileList[i];
        if(fileName == ''){
            break;
        }
        else {
            sql = sql + "  or  (     modulePath " + database.compareEqual(modulePath)
                + "         and fileName =  " + database.toSqlValue(fileName) +")";
        }
    }
    database.exec(sql, function (err, result) {
        if (err) {
            logging.error(err.message);
			res.send({status:"fail", errorMessage: err.message});
        }
        if (result) {
            removeSnapshotDefectMap(req, res);
        }
    })
};

function removeDefectFilter(req, res){
    "use strict";
    if(req == undefined || req.body == undefined || req.body.params.fileList == undefined){
        res.send({status:"fail", errorMessage: "No modulePath or No fileName"})
    }

    var modulePath = base64.decode(req.body.params.modulePath);
    var sql = "DELETE FROM DefectFilter WHERE fileName in ( SELECT fileName FROM Defect WHERE 0 "


    for(var i=0; i<  req.body.params.fileList.length ; i++) {
        var fileName = req.body.params.fileList[i];
        if(fileName == ''){
            break;
        }
        else {
            sql = sql + "  or  (     modulePath " + database.compareEqual(modulePath)
                + "         and fileName =  " + database.toSqlValue(fileName) +")";
        }

    }
    sql = sql + ")";

    database.exec(sql, function (err, result) {
        if (err) {
            logging.error(err.message);
        }
        if (result) {
            removeDefect(req, res);
        }
    })
};

function removeCodeMetrics(req, res){
    "use strict";
    if(req == undefined || req.body == undefined || req.body.params.fileList == undefined){
        res.send({status:"fail", errorMessage: "No modulePath or No fileName"})
    }

    var modulePath = base64.decode(req.body.params.modulePath);
    var sql = "DELETE FROM CodeMetrics WHERE fileName in ( SELECT fileName FROM Defect WHERE 0 ";

    for(var i=0; i<  req.body.params.fileList.length ; i++) {
        var fileName = req.body.params.fileList[i];
        if(fileName == ''){
            break;
        }
        else {
            sql = sql + "  or  (     modulePath " + database.compareEqual(modulePath)
                + "         and fileName =  " + database.toSqlValue(fileName) +")";
        }
    }

    sql = sql + ")";

    database.exec(sql, function (err, result) {
        if (err) {
            logging.error(err.message);
        }
        if (result) {
            removeOccurence(req, res);
        }
    })
};

function removeDefect(req, res){
    "use strict";

    if(req == undefined || req.body == undefined || req.body.params.fileList == undefined){
        res.send({status:"fail", errorMessage: "No modulePath or No fileName"})
    }

    var modulePath = base64.decode(req.body.params.modulePath);
    var sql = "DELETE FROM Defect WHERE 0 ";

    for(var i=0; i<  req.body.params.fileList.length ; i++) {
        var fileName = req.body.params.fileList[i];
        if(fileName == ''){
            break;
        }
        else {
            sql = sql + "  or  (     modulePath " + database.compareEqual(modulePath)
                + "         and fileName =  " + database.toSqlValue(fileName) +")";
        }

    }
    database.exec(sql, function (err, result) {
        if (err) {
            logging.error(err.message);
        }
        if (result) {
            logging.debug("a defect-filter is deleted.");
            res.send({status:"ok", message: "successful"});
        }
    })

};

function removeSnapshotOccurencemap(req, res){
    "use strict";
    if(req == undefined || req.body == undefined || req.body.params.fileList == undefined){
        res.send({status:"fail", errorMessage: "No modulePath or No fileName"})
    }

    var modulePath = base64.decode(req.body.params.modulePath);
    var sql = "DELETE FROM SnapshotOccurenceMap WHERE did in ( SELECT did FROM Defect  WHERE 0 ";

    for(var i=0; i<  req.body.params.fileList.length ; i++) {
        var fileName = req.body.params.fileList[i];
        if(fileName == ''){
            break;
        }
        else {
            sql = sql + "  or  (     modulePath " + database.compareEqual(modulePath)
                + "         and fileName =  " + database.toSqlValue(fileName) +")";
        }


    }
    sql = sql + ")";

    database.exec(sql, function (err, result) {
        if (err) {
            logging.error(err.message);
        }
        if (result) {
            removeDefectFilter(req, res);
        }
    })
};

function removeOccurence(req, res){
    "use strict";
    if(req == undefined || req.body == undefined || req.body.params.fileList == undefined){
        res.send({status:"fail", errorMessage: "No modulePath or No fileName"})
    }

    var modulePath = base64.decode(req.body.params.modulePath);
    var sql = "DELETE FROM Occurence WHERE did in ( SELECT did FROM Defect WHERE 0 ";

    for(var i=0; i<  req.body.params.fileList.length ; i++) {
        var fileName = req.body.params.fileList[i];
        if(fileName == ''){
            break;
        }
        else {
            sql = sql + "  or  (     modulePath " + database.compareEqual(modulePath)
                + "         and fileName =  " + database.toSqlValue(fileName) +")";
        }


    }
    sql = sql + ")";

    database.exec(sql, function (err, result) {
        if (err) {
            logging.error(err.message);
        }
    })
}

function removeSnapshotDefectMap(req, res){
    "use strict";
    if(req == undefined || req.body == undefined || req.body.params.fileList == undefined){
        res.send({status:"fail", errorMessage: "No modulePath or No fileName"})
    }

    var modulePath = base64.decode(req.body.params.modulePath);
    var sql = "DELETE FROM SnapshotDefectMap WHERE 0 ";

    for(var i=0; i<  req.body.params.fileList.length ; i++) {
        var fileName = req.body.params.fileList[i];
        if(fileName == ''){
            break;
        }
        else {
            sql = sql + "  or  (     modulePath " + database.compareEqual(modulePath)
                + "         and fileName =  " + database.toSqlValue(fileName) +")";
        }

    }
    database.exec(sql, function (err, result) {
        if (err) {
            logging.error(err.message);
        }
        if (result) {
            removeCodeMetrics(req, res);
        }
    })
};


exports.addSnapshotSourceCode = function(req, res) {
    if(req.body.fileName === undefined || req.body.sourceCode === undefined){
        res.send({status:"fail", errorMessage: "No fileName or no sourceCode"});
		return;
    }

    var snapshotId = req.body.snapshotId;
    var groupId = req.body.groupId;
    var modulePath = req.body.modulePath;
    var fileName = req.body.fileName;
    var sourceCode = req.body.sourceCode;
    var userNo = account.getUserNo(req.currentUserId);

    if(fileName == undefined || fileName == -1 || sourceCode == undefined || sourceCode == -1){
        res.send({status:"fail", errorMessage: "Invalid Data"});
        return;
    }

    if(snapshotId == undefined || snapshotId <= 0 || groupId == undefined || groupId == -1){
        addSourceCodeMap(res, snapshotId, fileName, modulePath, sourceCode, userNo);
    } else {
        var sql = "INSERT INTO Snapshot "
            + " (id, groupId, createdDateTime, creatorNo) "
            + " VALUES ( "
            + snapshotId
            + ", " + groupId
            + ", now()"
            + ", " + userNo + ") ";

        database.exec(sql, function (err, result) {
            if (err) {
                // todo : ingnore when dup
                if(err.code != 'ER_DUP_ENTRY'){
                    logging.error(err.message);
                    res.send({status: "fail", errorMessage: err.message});
                    return;
                }
            }

            addSourceCodeMap(res, snapshotId, fileName, modulePath, sourceCode, userNo);

        });
    }
};

function addSourceCodeMap(res, snapshotId, fileName, modulePath, sourceCode, userNo){
    "use strict";

    var sql = " SELECT count(id) as count FROM SourceCodeMap "
        + " WHERE "
        + " fileName = " + database.toSqlValue(fileName)
        + " and modulePath " + database.compareEqual(modulePath);

    if(snapshotId > 0){
        sql += " and snapshotId = " + snapshotId;
    } else {
        sql += " and snapshotId is null ";
    }

    database.exec(sql, function (err, result){
        if(err){
            logging.error(err.message);
            res.send({status: "fail", errorMessage: err.message});
            return;
        }

        if(result && result.length > 0 && result[0].count > 0){
            sql = " UPDATE SourceCodeMap SET "
                + " sourceCode = " + database.toSqlValue(sourceCode)
                + " ,createdDateTime = now() "
                + " ,creatorNo = " + userNo
                + " WHERE "
                + " fileName = " + database.toSqlValue(fileName)
                + " and modulePath " + database.compareEqual(modulePath);
            if(snapshotId > 0){
                sql += " and snapshotId = " + snapshotId;
            } else {
                sql += " and snapshotId is null ";
            }

            database.exec(sql, function (err, result) {
                if(err){
                    logging.error(err.message);
                    res.send({status: "fail", errorMessage: err.message});
                    return;
                }

                res.send({status: "ok"});
            });
        } else {
            if(snapshotId != undefined && snapshotId > 0){
                sql = "INSERT INTO SourceCodeMap "
                    + " ( snapshotId, fileName, modulePath, sourceCode, createdDateTime, creatorNo)"
                    + " VALUES "
                    + " ( " + snapshotId
                    + " , " + database.toSqlValue(fileName)
                    + " , " + database.toSqlValue(modulePath)
                    + " , " + database.toSqlValue(sourceCode)
                    + " , now() "
                    + " , " +  userNo
                    + " ) ";
            } else {
                sql = "INSERT INTO SourceCodeMap "
                    + " ( fileName, modulePath, sourceCode, createdDateTime, creatorNo)"
                    + " VALUES "
                    + " ( " + database.toSqlValue(fileName)
                    + " , " + database.toSqlValue(modulePath)
                    + " , " + database.toSqlValue(sourceCode)
                    + " , now() "
                    + " , " +  userNo
                    + " ) ";
            }

            database.exec(sql, function (err, result) {
                if (err) {
                    if(err.code != 'ER_DUP_ENTRY') {
                        logging.error(err.message);
                        res.send({status: "fail", errorMessage: err.message});
                        return;
                    }
                }

                res.send({status: "ok"});
            });
        }
    });
}

exports.getSnapshotSourceCode = function(req, res) {
    if(req == undefined || req.query == undefined || req.query.fileName == undefined || req.currentUserId == undefined){
        res.send({status:"fail", errorMessage: "No Data or No currentUserId"});
		return;
    }

    var fileName = req.query.fileName;
    var modulePath = base64.decode(req.query.modulePath);
    var snapshotId = req.query.snapshotId;

    var changeToBase64 = req.query.changeToBase64 || true;
    var userNo = account.getUserNo(req.currentUserId);

    if(modulePath == "undefined"){
        modulePath="";
    }

    if(fileName == undefined || fileName == -1){
        res.send({status:"fail", errorMessage: "Invalid Data"});
		return;
    }

    if(snapshotId != "undefined"){
        var sql = "SELECT sourceCode FROM SourceCodeMap "
            + " WHERE "
            + " fileName = " + database.toSqlValue(fileName)
            + " and modulePath " + database.compareEqual(modulePath)
            + " and snapshotId" + database.compareEqual(snapshotId)
            + " ORDER BY createdDateTime desc LIMIT 1";
    }
    else{
        var sql = "SELECT sourceCode FROM SourceCodeMap "
            + " WHERE "
            + " fileName = " + database.toSqlValue(fileName)
            + " and modulePath " + database.compareEqual(modulePath)
            + " ORDER BY createdDateTime desc LIMIT 1";
    }

    database.exec(sql, function (err, result) {
        if (err) {
            logging.error(err.message);
            res.send({status: "fail", errorMessage: err.message});
            return;
        }

        if (result && result[0]) {
            if(changeToBase64){
                res.send(new Buffer(result[0].sourceCode, 'base64').toString('utf8'));
            } else {
                res.send(result[0].sourceCode);
            }
        }
    });
};

exports.getSnapshotSourceCodeV2 = function(req, res) {
    if(req == undefined || req.body == undefined ||
        req.body.params == undefined || req.body.params.fileName == undefined || req.body.params.snapshotId == undefined){
        res.send({status:"fail", errorMessage: "No Data or No currentUserId"});
        return;
    }

    var  fileName = req.body.params.fileName;
    var modulePath = base64.decode(req.body.params.modulePath);
    var snapshotId = req.body.params.snapshotId;

    var changeToBase64 = req.body.params.changeToBase64 || true;

    if(fileName == undefined || fileName == -1){
        res.send({status:"fail", errorMessage: "Invalid Data"});
        return;
    }

    var sql = '';
    if(snapshotId != "undefined"){
        sql = "SELECT sourceCode FROM SourceCodeMap "
            + " WHERE "
            + " fileName = " + database.toSqlValue(fileName)
            + " and modulePath " + database.compareEqual(modulePath)
            + " and snapshotId" + database.compareEqual(snapshotId)
            + " ORDER BY createdDateTime desc LIMIT 1";
    }
    else{
        sql = "SELECT sourceCode FROM SourceCodeMap "
            + " WHERE "
            + " fileName = " + database.toSqlValue(fileName)
            + " and modulePath " + database.compareEqual(modulePath)
            + " ORDER BY createdDateTime desc LIMIT 1";
    }

    database.exec(sql, function (err, result) {
        if (err) {
            logging.error(err.message);
            res.send({status: "fail", errorMessage: err.message});
            return;
        }

        if (result && result[0]) {
            if(changeToBase64){
                res.send(new Buffer(result[0].sourceCode, 'base64').toString('utf8'));
            } else {
                res.send(result[0].sourceCode);
            }
        }
    });
};


exports.checkSnapshotSourceCode = function(req, res) {
    if(req == undefined || req.query == undefined || req.query.fileName == undefined || req.currentUserId == undefined){
        res.send({status:"fail", errorMessage: "No Data or No currentUserId"})
		return;
    }

    var fileName = req.query.fileName;
    var modulePath = base64.decode(req.query.modulePath);
    if(modulePath == "undefined"){
        modulePath="";
    }
    var changeToBase64 = req.query.changeToBase64 || true;
    var userNo = account.getUserNo(req.currentUserId);


    if(fileName == undefined || fileName == -1){
        res.send({status:"fail", errorMessage: "Invalid Data"});
		return;
    }

    var sql = "SELECT count(*) as count FROM SourceCodeMap  "
        + " WHERE "
        + " fileName = " + database.toSqlValue(fileName)
        + " and modulePath " + database.compareEqual(modulePath)
        + " and length(sourceCode) > 0"
        + " ORDER BY createdDateTime desc LIMIT 1";

    database.exec(sql, function (err, result) {
        if (err) {
            logging.error(err.message);
            res.send({status: "fail", errorMessage: err.message});
            return;
        }

        if (result) {
            res.send(result);
        }
    });
};


function makeAnalysisLog(defectList, params){
    var criticalCount = 0;
    var majorCount = 0;
    var minorCount = 0;
    var crcCount = 0;
    var etcCount = 0;

    for(var i =0; i<defectList.length; i++){
        var severityCode = defectList[i].severityCode;
        if(severityCode == "CRI"){
            criticalCount ++;
        } else if(severityCode == "MAJ"){
            majorCount ++;
        } else if(severityCode == "MIN"){
            minorCount ++;
        } else if(severityCode == "CRC"){
            crcCount ++;
        } else if(severityCode == "ETC"){
        }
    }
    addAnalysisLog(params.fileName, params.modulePath, params.userNo, criticalCount, majorCount, minorCount, crcCount, etcCount);

}

function makeAnalysisLogV3(defectList, params){
    var criticalCount = 0;
    var majorCount = 0;
    var minorCount = 0;
    var crcCount = 0;
    var etcCount = 0;

    _.forEach(defectList, function(defect){
        var severityCode = defect.severityCode;
        switch(severityCode) {
            case "CRI" :
                criticalCount++;
                break;
            case "MAJ":
                majorCount ++;
                break;
            case "MIN":
                minorCount ++;
                break;
            case "CRC":
                crcCount ++;
                break;
            case "ETC":
                etcCount ++;
                break;
        }
    });
    addAnalysisLog(params.fileName, params.modulePath, params.userNo, criticalCount, majorCount, minorCount, crcCount, etcCount);
}


exports.addV2 = function(req, res) {
    if(req == undefined || req.body == undefined || req.body.result == undefined || req.currentUserId == undefined){
        res.send({status:"fail", errorMessage: "No Data or No currentUserId"});
        return;
    }

    if(account.getUserNo(req.currentUserId) === undefined){
        res.send({status:"fail", errorMessage: "No UserNo"});
        return ;
    }

    var defectJson = req.body.result;

    // 1. verify result
    var defectObject = JSONbig.parse(defectJson);

    if(defectObject === undefined){
        res.send({status:"fail", errorMessage: "Invalid Data"});
        return ;
    }
    res.send({status:"ok", message: "Input Data was taken and being processed to insert/update/delete SA DB"});

    var defectList = defectObject.defectList;

    var codeMetrics = defectObject.codeMetrics;
    var functionMetrics = defectObject.functionMetrics;

    var params = {};

    params.projectName = defectObject.projectName;
    params.snapshotId = defectObject.snapshotId;
    params.groupId = defectObject.groupId;
    params.modulePath = defectObject.modulePath;

    params.fileName = defectObject.fileName;
    params.userNo = account.getUserNo(req.currentUserId);

    // log
    makeAnalysisLog(defectList, params);

    addCodeMetrics(params.fileName, params.modulePath, params.userNo, params.snapshotId, codeMetrics);

    for(var i=0;i<functionMetrics.length;i++){
        addFunctionMetrics(params.fileName, params.modulePath, params.userNo, params.snapshotId, functionMetrics[i]);
    }

    addSnapshot(params.snapshotId, params.groupId, params.userNo);

    if(defectObject.defectCount == 0){
        newToFixDefectV2(defectList, params);
    } else {
        for(var j=0; j < defectList.length ; j++ ){
            newOrUpdateDefectV2(defectList[j] , params.userNo, params.snapshotId, params.groupId);
        }
        newToFixDefectV2(defectList, params);
    }
};


exports.addV3 = function(req, res) {
    if(req == undefined || req.body == undefined || req.body.result == undefined || req.currentUserId == undefined){
        res.send({status:"fail", errorMessage: "No Data or No currentUserId"});
        return;
    }

    if(account.getUserNo(req.currentUserId) === undefined){
        res.send({status:"fail", errorMessage: "No UserNo"});
        return ;
    }

    var defectJson = req.body.result;

    // 1. verify result
    var defectObject = JSONbig.parse(defectJson);

    if(defectObject === undefined){
        res.send({status:"fail", errorMessage: "Invalid Data"});
        return ;
    }
    res.send({status:"ok", message: "Input Data was taken and being processed to insert/update/delete SA DB"});

    var defectList = defectObject.defectList;

    var codeMetrics = defectObject.codeMetrics;
    var functionMetrics = defectObject.functionMetrics;

    var params = {};

    params.projectName = defectObject.projectName;
    params.snapshotId = defectObject.snapshotId;
    params.groupId = defectObject.groupId;
    params.modulePath = defectObject.modulePath;

    params.fileName = defectObject.fileName;
    params.userNo = account.getUserNo(req.currentUserId);


    // log
    makeAnalysisLogV3(defectList, params);

    addCodeMetrics(params.fileName, params.modulePath, params.userNo, params.snapshotId, codeMetrics);

    for(var i=0;i<functionMetrics.length;i++){
        addFunctionMetrics(params.fileName, params.modulePath, params.userNo, params.snapshotId, functionMetrics[i]);
    }

    addSnapshot(params.snapshotId, params.groupId, params.userNo);

    /*
     in AddV3 - checkAnalysisType [ "SAVE" , "FILE", "FOLDER", "PROJECT", "SNAPSHOT", "UNKNOWN"]
     1. SAVE / FILE / UNKNOWN : update currentUserId,
     2. FOLDER / PROJECT / SNAPSHOT : noUpdate, at the first time to analysis : 'admin : 1'
     */

    params.userNo = getUserNoFromAnalysisType(defectObject.defectCount, defectList, req.currentUserId);

    if(defectObject.defectCount == 0){
        newToFixDefectV2(defectList, params);
    } else {
        for(var j=0; j < defectList.length ; j++ ){
            newOrUpdateDefectV2(defectList[j] , params.userNo, params.snapshotId, params.groupId);
        }
        newToFixDefectV2(defectList, params);
    }
};

function getUserNoFromAnalysisType(defectCount, defectList, currentId){

    if(defectCount == 0){
        return account.getUserNo(currentId);
    }
    var analysisType = defectList[0].analysisType;
    if(analysisType == 'FOLDER' || analysisType == 'PROJECT' || analysisType == 'SNAPSHOT'){
        return account.getUserNo('admin');
    }
    return account.getUserNo(currentId);
}


exports.add = function(req, res) {
    if(req == undefined || req.body == undefined || req.body.result == undefined || req.currentUserId == undefined){
        res.send({status:"fail", errorMessage: "No Data or No currentUserId"});
        return;
    }

    if(account.getUserNo(req.currentUserId) === undefined){
        res.send({status:"fail", errorMessage: "No UserNo"});
        return ;
    }

    var defectJson = req.body.result;

    // 1. verify result
    var defectObject = JSONbig.parse(defectJson);

    if(defectObject === undefined){
        res.send({status:"fail", errorMessage: "Invalid Data"});
        return ;
    }
    res.send({status:"ok", message: "Input Data was taken and being processed to insert/update/delete SA DB"});

    var defectList = defectObject.defectList;
    var codeMetrics = defectObject.codeMetrics;

    var params = {};

    params.projectName = defectObject.projectName;
    params.snapshotId = defectObject.snapshotId;
    params.groupId = defectObject.groupId;
    params.modulePath = defectObject.modulePath;
    params.fileName = defectObject.fileName;
    params.userNo = account.getUserNo(req.currentUserId);

    // log
    makeAnalysisLog(defectList, params);

    addCodeMetrics(params.fileName, params.modulePath, params.userNo, params.snapshotId, codeMetrics);

    addSnapshot(params.snapshotId, params.groupId, params.userNo);

    if(defectObject.defectCount == 0){
        newToFixDefect(defectList, params);
    } else {
        for(var j=0; j < defectList.length ; j++ ){
            newOrUpdateDefect(defectList[j] , params.userNo, params.snapshotId, params.groupId);
        }
        newToFixDefect(defectList, params);
    }
};

function newToFixDefectV2(defectList, params){
    var sql = "select " +
        "did, toolName, language, checkerCode, fileName, modulePath, className, methodName," +
        "severityCode, ifnull(categoryName,'') as categoryName, statusCode, message, " +
        "createdDateTime, modifiedDateTime, creatorNo, modifierNo, chargerNo, reviewerNo, approvalNo " +
        "from Defect " +
        "where " +
        "   fileName = " + database.toSqlValue(params.fileName) +
        "   and modulePath " + database.compareEqual(params.modulePath) +
        "   and statusCode = 'NEW'";

    database.exec(sql, function (err, dbResult) {
        if(err){
            logging.error(err.message);
            return;
        }

        if(dbResult.length >0){
            var inDefect;
            var isExist;
            var dbDefect;

            for(var x=0; x<dbResult.length; x++){
                isExist = false;
                dbDefect = dbResult[x];

                for(var y=0; y<defectList.length; y++){
                    inDefect = defectList[y];
                    if(isSameDefect(dbDefect, inDefect)){
                        isExist = true;
                        break;
                    }
                }

                if(isExist == false){
                    fixDefect(dbDefect, params.userNo, params.snapshotId);
                }
            }
        }

    });
}



function newToFixDefect(defectList, params){
    var sql = "select " +
        "did, toolName, language, checkerCode, fileName, modulePath, className, methodName," +
        "severityCode, statusCode, message, " +
        "createdDateTime, modifiedDateTime, creatorNo, modifierNo, chargerNo, reviewerNo, approvalNo " +
        "from Defect " +
        "where " +
        "   fileName = " + database.toSqlValue(params.fileName) +
        "   and modulePath " + database.compareEqual(params.modulePath) +
        "   and statusCode = 'NEW'";

    database.exec(sql, function (err, dbResult) {
        if(err){
            logging.error(err.message);
            return;
        }

        if(dbResult.length >0){
            var inDefect;
            var isExist;
            var dbDefect;

            for(var x=0; x<dbResult.length; x++){
                isExist = false;
                dbDefect = dbResult[x];

                for(var y=0; y<defectList.length; y++){
                    inDefect = defectList[y];
                    if(isSameDefect(dbDefect, inDefect)){
                        isExist = true;
                        break;
                    }
                }

                if(isExist == false){
                    fixDefect(dbDefect, params.userNo, params.snapshotId);
                }
            }
        }

    });
}

function addAnalysisLog(fileName, modulePath, userNo, defectCriticalCount, defectMajorCount, defectMinorCount, defectCrcCount, defectEtcCount) {
    "use strict";
    var sql = "INSERT INTO AnalysisLog "
        + " (fileName, modulePath, analystNo, defectCriticalCount, defectMajorCount, defectMinorCount, defectCrcCount, defectEtcCount, createdDateTime) "
        + " VALUES ("
        + database.toSqlValue(fileName)
        + ", " + database.toSqlValue(modulePath)
        + ", " + userNo
        + ", " + defectCriticalCount
        + ", " + defectMajorCount
        + ", " + defectMinorCount
        + ", " + defectCrcCount
        + ", " + defectEtcCount
        + ", now()) ";

    database.exec(sql, function(err, results){
        if(err){
            logging.error(err);
        }
    });
}

function updateAllCodeMetricsNoLast(){
    "use strict";

    var sql = "UPDATE CodeMetrics SET "
        + " lastYn = 'N' "
        + " WHERE "
        + " lastYn = 'Y'";

    database.exec(sql);
}

function addFunctionMetrics(fileName, modulePath, userNo, snapshotId, functionMetric){

    // �ϴ� ������Ʈ
    var sql = "UPDATE FunctionMetrics SET"
        + " lastYn = 'N' "
        + " WHERE "
        + " lastYn = 'Y'"
        + " and fileName = " + database.toSqlValue(fileName)
        + " and modulePath " + database.compareEqual(modulePath)
        + " and functionName =" +database.toSqlValue(functionMetric.functionName);

    database.exec(sql, function(err){
        if (err) {
            logging.error(err.message);
            return;
        }

        sql = "INSERT INTO FunctionMetrics(snapshotId, fileName, modulePath, functionName, cc, sloc, callDepth,  createdDateTime, creatorNo, lastYn) "
            + "VALUES ("
            + snapshotId
            + ", " + database.toSqlValue(fileName)
            + ", " + database.toSqlValue(modulePath)
            + ", " + database.toSqlValue(functionMetric.functionName)
            + ", " + database.toSqlValue(functionMetric.cc)
            + ", " + database.toSqlValue(functionMetric.sloc)
            + ", " + database.toSqlValue(functionMetric.callDepth)
            + ", now()"
            + ", " + userNo + ", 'Y')";

        database.exec(sql, function (err){
            if (err) {
                logging.error(err.message);
            }
        });
    });

}

function addCodeMetrics(fileName, modulePath, userNo, snapshotId, codeMetrics) {
    var hasData = false;
    var tempCodeMetricsKeyList = [];
    var codeMetricsKeyList = [];

    for(var key in codeMetrics){
        hasData = true;
        tempCodeMetricsKeyList.push(key);
    }

    if(hasData == false){
        return;
    }

    codeMetricsKeyList = tempCodeMetricsKeyList.join("','");

    if(snapshotId == undefined || snapshotId <= 0){
        snapshotId = "null";
    }

    var sql = "UPDATE CodeMetrics SET "
        + " lastYn = 'N' "
        + " WHERE "
        + " lastYn = 'Y'"
        + " and fileName = " + database.toSqlValue(fileName)
        + " and modulePath " + database.compareEqual(modulePath)
        + " and metricName in ('"+codeMetricsKeyList +"')";

    database.exec(sql, function(err, results){
        if (err) {
            logging.error(err.message);
            return;
        }

        for(var key in codeMetrics){
            var value = codeMetrics[key];

            sql = "INSERT INTO CodeMetrics "
                + " (snapshotId, fileName, modulePath, metricName, metricValue, createdDateTime, creatorNo, lastYn) "
                + " VALUES ("
                + snapshotId
                + ", " + database.toSqlValue(fileName)
                + ", " + database.toSqlValue(modulePath)
                + ", " + database.toSqlValue(key)
                + ", " + database.toSqlValue(value)
                + ", now()"
                + ", " + userNo + ", 'Y')";

            database.exec(sql, function (err, result){
                if (err) {
                    logging.error(err.message);
                }
            });
        }
    });
}

exports.getCodeMetrics = function(req, res) {
    var modulePath = req.query.modulePath;
    var fileName = req.query.fileName;
    var snapshotId = req.query.snapshotId;

    var sql = "select id, snapshotId, fileName, ifnull(modulePath, 'NO_MODULE_PATH') as modulePath, metricName, "
        + " metricValue, createdDateTime, creatorNo "
        + " from CodeMetrics "
        + " WHERE ";


    if(snapshotId != undefined && snapshotId != ""){
        sql += " snapshotId " + database.compareEqual(snapshotId);
    } else {
        sql += " lastYn = 'Y'";
    }

    if(modulePath != undefined && modulePath != ""){
        if(modulePath == 'NO_MODULE_PATH'){
            sql += " and modulePath is null ";
        } else {
            sql += " and modulePath " + database.compareEqual(modulePath);
        }
    }

    if(fileName != undefined && fileName != ""){
        sql += " and fileName = " + database.toSqlValue(fileName);
    }

    database.exec(sql, function (err, result) {
        if (err) {
            logging.error(err.message);
            res.send({status: "fail", errorMessage: err.message});
            return;
        }

        if (result){
            res.send(result);
        }
    });
};

exports.getCodeMetricsAndDefects = function(req, res) {
    var sql = "select "
        + " ifnull(A.modulePath, 'NO_MODULE_PATH') as modulePath, A.fileName, ifnull(A.snapshotId, '') as snapshotId, "
        + "    ifnull(A.loc, 0) as loc, "
        + "    ifnull(A.sloc, 0) as sloc, "
        + "    ifnull(A.minComplexity, 0) as minComplexity, "
        + "    ifnull(A.maxComplexity, 0) as maxComplexity, "
        + "    ifnull(A. averageComplexity, 0) as averageComplexity, "
        + "    ifnull(A.classCnt, 0) as classCnt, "
        + "    ifnull(A.methodCnt, 0) as methodCnt, "
        + "    round(ifnull(A.commentRatio, 0), 2) as commentRatio,"
        + "    ifnull(B.totalCnt, 0) as totalCnt, "
        + "    ifnull(B.criticalCnt, 0) as criticalCnt, "
        + "    ifnull(B.majorCnt, 0) as majorCnt, "
        + "    ifnull(B.minorCnt, 0) as minorCnt, "
        + "    ifnull(B.crcCnt, 0) as crcCnt, "
        + "    ifnull(B.etcCnt, 0) as etcCnt, "
        + "    ifnull(B.newCnt, 0) as newCnt, "
        + "    ifnull(B.fixCnt, 0) as fixCnt, "
        + "    ifnull(B.excCnt, 0) as excCnt, "
        + "    ifnull(B.criticalNewCnt, 0) as cnc, "
        + "    ifnull(B.criticalFixCnt, 0) as cfc, "
        + "    ifnull(B.criticalExcCnt, 0) as cec, "
        + "    ifnull(B.majorNewCnt, 0) as mnc, "
        + "    ifnull(B.majorFixCnt, 0) as mfc, "
        + "    ifnull(B.majorExcCnt, 0) as mec, "
        + "    ifnull(B.minorNewCnt, 0) as nnc, "
        + "    ifnull(B.minorFixCnt, 0) as nfc, "
        + "    ifnull(B.minorExcCnt, 0) as nec, "
        + "    ifnull(B.crcNewCnt, 0) as rnc, "
        + "    ifnull(B.crcFixCnt, 0) as rfc, "
        + "    ifnull(B.crcExcCnt, 0) as rec, "
        + "    ifnull(B.etcNewCnt, 0) as enc, "
        + "    ifnull(B.etcFixCnt, 0) as efc, "
        + "    ifnull(B.etcExcCnt, 0) as eec, "
        + "    B.modifierId "
        + " from "
        + " (select "
        + " fileName, ifnull(modulePath, 'NO_MODULE_PATH') as modulePath, snapshotId, "
        + "    sum(if(metricName = 'loc', metricValue, 0)) as loc, "
        + "    sum(if(metricName = 'sloc', metricValue, 0)) as sloc, "
        + "    sum(if(metricName = 'minComplexity', metricValue, 0)) as minComplexity, "
        + "    sum(if(metricName = 'maxComplexity', metricValue, 0)) as maxComplexity, "
        + "    sum(if(metricName = 'averageComplexity', metricValue, 0)) as averageComplexity, "
        + "    sum(if(metricName = 'classCount', metricValue, 0)) as classCnt, "
        + "    sum(if(metricName = 'methodCount', metricValue, 0)) as methodCnt, "
        + "    sum(if(metricName = 'commentRatio', metricValue, 0.0)) as commentRatio "
        + " from CodeMetrics "
        + " WHERE "
        + " lastYn = 'Y' "
        + " group by modulePath, fileName) as A left join "
        + " (select "
        + " ifnull(modulePath, 'NO_MODULE_PATH') as modulePath, fileName, count(did) as totalCnt, "
        + "    sum(if(severityCode = 'CRI', 1, 0)) As criticalCnt, "
        + "    sum(if(severityCode = 'MAJ', 1, 0)) As majorCnt, "
        + "    sum(if(severityCode = 'MIN', 1, 0)) As minorCnt, "
        + "    sum(if(severityCode = 'CRC', 1, 0)) As crcCnt, "
        + "    sum(if(severityCode = 'ETC', 1, 0)) As etcCnt, "
        + "    sum(if(statusCode = 'NEW', 1, 0)) As newCnt, "
        + "    sum(if(statusCode = 'FIX', 1, 0)) As fixCnt, "
        + "    sum(if(statusCode = 'EXC', 1, 0)) As excCnt, "
        + "    sum(if(severityCode = 'CRI', if(statusCode = 'NEW', 1, 0), 0)) As criticalNewCnt, "
        + "    sum(if(severityCode = 'CRI', if(statusCode = 'FIX', 1, 0), 0)) As criticalFixCnt, "
        + "    sum(if(severityCode = 'CRI', if(statusCode = 'EXC', 1, 0), 0)) As criticalExcCnt, "
        + "    sum(if(severityCode = 'MAJ', if(statusCode = 'NEW', 1, 0), 0)) As majorNewCnt, "
        + "    sum(if(severityCode = 'MAJ', if(statusCode = 'FIX', 1, 0), 0)) As majorFixCnt, "
        + "    sum(if(severityCode = 'MAJ', if(statusCode = 'EXC', 1, 0), 0)) As majorExcCnt, "
        + "    sum(if(severityCode = 'MIN', if(statusCode = 'NEW', 1, 0), 0)) As minorNewCnt, "
        + "    sum(if(severityCode = 'MIN', if(statusCode = 'FIX', 1, 0), 0)) As minorFixCnt, "
        + "    sum(if(severityCode = 'MIN', if(statusCode = 'EXC', 1, 0), 0)) As minorExcCnt, "
        + "    sum(if(severityCode = 'CRC', if(statusCode = 'NEW', 1, 0), 0)) As crcNewCnt, "
        + "    sum(if(severityCode = 'CRC', if(statusCode = 'FIX', 1, 0), 0)) As crcFixCnt, "
        + "    sum(if(severityCode = 'CRC', if(statusCode = 'EXC', 1, 0), 0)) As crcExcCnt, "
        + "    sum(if(severityCode = 'ETC', if(statusCode = 'NEW', 1, 0), 0)) As etcNewCnt, "
        + "    sum(if(severityCode = 'ETC', if(statusCode = 'FIX', 1, 0), 0)) As etcFixCnt, "
        + "    sum(if(severityCode = 'ETC', if(statusCode = 'EXC', 1, 0), 0)) As etcExcCnt, "
        + "    (select userId from Account where userNo = modifierNo) as modifierId "
        + " from Defect "
        + " group by modulePath, fileName) as B "
        + " on "
        + " A.modulePath = B.modulePath and A.fileName = B.fileName "
        + " order by newCnt desc ";

    database.exec(sql, function (err, result) {
        if (err) {
            logging.error(err.message);
            res.send({status: "fail", errorMessage: err.message});
            return;
        }

        if (result){
            res.send(result);
        }
    });
};

exports.getCodeMetricsAndDefectsLimit = function(req, res) {
    var defectStatus = req.query.defectStatus;
    var modulePath = req.query.modulePath ? req.query.modulePath : '';
    var limitSize = req.query.limitSize ? req.query.limitSize : 100;

    var sql = "select "
        + " A.fileName, A.modulePath, B.totalCnt, A.classCnt, A.methodCnt, "
        + " B.newCnt, B.fixCnt, B.excCnt, A.sloc, A.maxComplexity "
        + " from "
        + " (select "
        + " fileName, "
        + " ifnull(modulePath, 'NO_MODULE_PATH') as modulePath, "
        + " sum(if(metricName = 'sloc', metricValue, 0)) as sloc, "
        + " sum(if(metricName = 'maxComplexity', metricValue, 0)) as maxComplexity, "
        + " sum(if(metricName = 'classCount', metricValue, 0)) as classCnt, "
        + " sum(if(metricName = 'methodCount', metricValue, 0)) as methodCnt "
        + " from CodeMetrics "
        + " WHERE lastYn = 'Y' "
        + " group by modulePath, fileName) as A "
        + " left join "
        + " (select "
        + " ifnull(modulePath, 'NO_MODULE_PATH') as modulePath, fileName, count(did) as totalCnt, "
        + " sum(if(statusCode = 'NEW', 1, 0)) As newCnt, "
        + " sum(if(statusCode = 'FIX', 1, 0)) As fixCnt, "
        + " sum(if(statusCode = 'EXC', 1, 0)) As excCnt "
        + " from Defect "
        + " group by modulePath, fileName) as B "
        + " on A.modulePath = B.modulePath and A.fileName = B.fileName "
        + " where B.fileName is not null ";

    if(modulePath !== ''){
        sql += " and A.modulePath = " + database.toSqlValue(modulePath);
    }

    if(defectStatus === 'NEW'){
        sql += " and B.newCnt > 0 ";
    } else if(defectStatus === 'FIX'){
        sql += " and B.fixCnt > 0 ";
    } else if(defectStatus === 'EXC'){
        sql += " and B.excCnt > 0 ";
    }

    sql += " order by B.newCnt desc, A.fileName "
        + " LIMIT " + limitSize;

    database.exec(sql, function (err, result) {
        if (err) {
            logging.error(err.message);
            res.send({status: "fail", errorMessage: err.message});
            return;
        }

        if (result){
            res.send(result);
        }
    });
};

exports.getCheckerAndDefects = function(req, res) {
    //var sql = "select checkerCode, severityCode, toolName, language, count(did) as count "
    //    + " from Defect group by checkerCode order by count(did) desc ";

    //var sql = "select modulePath, fileName, checkerCode, severityCode, toolName, language from Defect";

    var sql = "select ifnull(modulePath, 'NO_MODULE_PATH') as modulePath, fileName, checkerCode, severityCode, statusCode, toolName, language, count(did) as count from Defect "
        + " group by toolName, language, checkerCode, severityCode, statusCode, modulePath, fileName";

    database.exec(sql, function (err, result) {
        if (err) {
            logging.error(err.message);
            res.send({status: "fail", errorMessage: err.message});
            return;
        }

        if (result){
            res.send(result);
        }
    });
};

exports.getDevelopers = function(req, res) {
    /*
     var sql = "select "
     + "     A.modulePath, A.fileName, group_concat(A.analystNo) as analystNos, group_concat(A.analystId) as analystIds "
     + "    from "
     + "         (select "
     + "             modulePath, fileName, analystNo, "
     + "             (select userId from Account where userNo = analystNo) as analystId "
     + "         from AnalysisLog group by modulePath, fileName, analystNo) as A "
     + "    group by modulePath, fileName";
     */
    var sql = "select analystNo as developerNo, "
        + "       (select ifnull(userId, 'invalid-user') from Account where userNo = analystNo) as developerId, "
        + "        ifnull(modulePath, 'NO_MODULE_PATH') as modulePath, fileName, count(id) as count "
        + "    from AnalysisLog "
        + "    group by analystNo, modulePath, fileName";

    database.exec(sql, function (err, result) {
        if (err) {
            logging.error(err.message);
            res.send({status: "fail", errorMessage: err.message});
            return;
        }

        if (result){
            res.send(result);
        }
    });
};
/*
 function addSnapshotByFile(params){
 if(params.snapshotId == undefined || params.snapshotId <= 0 || params.groupId == undefined || params.groupId == -1){
 //logging.debug("Invalid parameter at analysis.js addSnapshotByFile() : " + JSON.stringify(params));
 return;
 }
 // has Snapshot?
 var sql = "SELECT id FROM Snapshot WHERE id = " + params.snapshotId;

 database.exec(sql, function (err, result){
 if(err){
 logging.error(err.message);
 return;
 }

 if(result){
 if(result[0] === undefined) {
 sql = "INSERT INTO Snapshot "
 + " (id, groupId, createdDateTime, creatorNo) "
 + " VALUES ( "
 + params.snapshotId
 + ", " + params.groupId
 + ", " + database.getDateTimeEx(params.snapshotId)
 + ", " + params.userNo + ") ";
 }else {
 sql = "INSERT INTO Snapshot "
 + " (id, groupId, createdDateTime, creatorNo) "
 + " VALUES ( "
 + params.snapshotId
 + ", " + params.groupId
 + ", now()"
 + ", " + params.userNo + ") ";
 }
 database.exec(sql, function (err, result){
 "use strict";
 if(err){
 logging.error(err.message);
 return;
 }

 if(result){
 addSnapshotDefectMapByFile(params);
 }
 })
 } else {
 addSnapshotDefectMapByFile(params);
 }
 });
 }

 function addSnapshotDefectMapByFile(params){
 if(params.snapshotId == undefined || params.snapshotId <= 0 || params.groupId == undefined || params.groupId == -1){
 //logging.debug("Invalid parameter at analysis.js addSnapshotAndDefectMapByFile() : " + JSON.stringify(params));
 return;
 }

 var sql = "INSERT INTO SnapshotDefectMap "
 + "(snapshotId, did, toolName, language, checkerCode, fileName, modulePath, className, methodName, "
 + " severityCode, statusCode, message, createdDateTime, modifiedDateTime, creatorNo, modifierNo) "
 + "SELECT "
 + params.snapshotId
 + " , did, toolName, language, checkerCode, fileName, modulePath, className, methodName, "
 + " severityCode, statusCode, message, createdDateTime, modifiedDateTime, creatorNo, modifierNo "
 + " FROM Defect "
 + " WHERE "
 //+ "         toolName = " + database.toSqlValue(params.toolName)
 //+ "         and language = " + database.toSqlValue(params.language)
 + "         and fileName = " + database.toSqlValue(params.fileName)
 + "         and modulePath " + database.compareEqual(params.modulePath);

 database.exec(sql, function (err, result){
 if(err){
 logging.error(err.message);
 }
 });
 }
 */



function addSnapshotDefectMapV2(snapshotId, defect){
    if(snapshotId == undefined || snapshotId <= 0){
        //logging.debug("Invalid parameter at analysis.js addSnapshotAndDefectMap() : " + snapshotId);
        return;
    }

    var sql = "INSERT INTO SnapshotDefectMap "
        + "(snapshotId, did, toolName, language, checkerCode, fileName, modulePath, className, methodName, "
        + " severityCode, categoryName, statusCode, message, createdDateTime, modifiedDateTime, creatorNo, modifierNo) "
        + "SELECT "
        + snapshotId
        + ", did, toolName, language, checkerCode, fileName, modulePath, className, methodName, "
        + " severityCode, categoryName, statusCode, message, createdDateTime, modifiedDateTime, creatorNo, modifierNo "
        + " FROM Defect "
        + " WHERE "
        + "     did in (select did from Defect where "
        + "         toolName = " + database.toSqlValue(defect.toolName)
        + "         and language = " + database.toSqlValue(defect.language)
        + "         and checkerCode = " + database.toSqlValue(defect.checkerCode)
        + "         and fileName = " + database.toSqlValue(defect.fileName)
        + "         and modulePath " + database.compareEqual(defect.modulePath)
        + "         and className " + database.compareEqual(defect.className)
        + "         and methodName " + database.compareEqual(defect.methodName)
        + "     )";

    database.exec(sql, function (err, result){
        if(err){
            logging.error(err.message);
        }
    });
}

function addSnapshotDefectMap(snapshotId, defect){
    if(snapshotId == undefined || snapshotId <= 0){
        //logging.debug("Invalid parameter at analysis.js addSnapshotAndDefectMap() : " + snapshotId);
        return;
    }

    var sql = "INSERT INTO SnapshotDefectMap "
        + "(snapshotId, did, toolName, language, checkerCode, fileName, modulePath, className, methodName, "
        + " severityCode, statusCode, message, createdDateTime, modifiedDateTime, creatorNo, modifierNo) "
        + "SELECT "
        + snapshotId
        + ", did, toolName, language, checkerCode, fileName, modulePath, className, methodName, "
        + " severityCode, statusCode, message, createdDateTime, modifiedDateTime, creatorNo, modifierNo "
        + " FROM Defect "
        + " WHERE "
        + "     did in (select did from Defect where "
        + "         toolName = " + database.toSqlValue(defect.toolName)
        + "         and language = " + database.toSqlValue(defect.language)
        + "         and checkerCode = " + database.toSqlValue(defect.checkerCode)
        + "         and fileName = " + database.toSqlValue(defect.fileName)
        + "         and modulePath " + database.compareEqual(defect.modulePath)
        + "         and className " + database.compareEqual(defect.className)
        + "         and methodName " + database.compareEqual(defect.methodName)
        + "     )";

    database.exec(sql, function (err, result){
        if(err){
            logging.error(err.message);
        }
    });
}

function addSnapshotDefectMapV2(snapshotId, defect){
    if(snapshotId == undefined || snapshotId <= 0){
        //logging.debug("Invalid parameter at analysis.js addSnapshotAndDefectMap() : " + snapshotId);
        return;
    }

    var sql = "INSERT INTO SnapshotDefectMap "
        + "(snapshotId, did, toolName, language, checkerCode, fileName, modulePath, className, methodName, "
        + " severityCode, categoryName, statusCode, message, createdDateTime, modifiedDateTime, creatorNo, modifierNo) "
        + "SELECT "
        + snapshotId
        + ", did, toolName, language, checkerCode, fileName, modulePath, className, methodName, "
        + " severityCode, ifnull(categoryName,'') as categoryName, statusCode, message, createdDateTime, modifiedDateTime, creatorNo, modifierNo "
        + " FROM Defect "
        + " WHERE "
        + "     did in (select did from Defect where "
        + "         toolName = " + database.toSqlValue(defect.toolName)
        + "         and language = " + database.toSqlValue(defect.language)
        + "         and checkerCode = " + database.toSqlValue(defect.checkerCode)
        + "         and fileName = " + database.toSqlValue(defect.fileName)
        + "         and modulePath " + database.compareEqual(defect.modulePath)
        + "         and className " + database.compareEqual(defect.className)
        + "         and methodName " + database.compareEqual(defect.methodName)
        + "     )";

    database.exec(sql, function (err, result){
        if(err){
            logging.error(err.message);
        }
    });
}

function addSnapshotOccurenceMap(defect, index, createdDateTime, userNo, snapshotId){
    "use strict";

    if(snapshotId == undefined || snapshotId <= 0){
        return;
    }

    var occurence = defect.occurences[index];

    var sql = "INSERT INTO SnapshotOccurenceMap "
        + "(snapshotId, did, startLine, endLine, charStart, charEnd, statusCode, variableName, stringValue, "
        + " fieldName, message, createdDateTime, modifiedDateTime, creatorNo) "
        + "VALUES "
        + "(" + snapshotId
        + ",    (select did from Defect where "
        + "         toolName = " + database.toSqlValue(defect.toolName)
        + "         and language = " + database.toSqlValue(defect.language)
        + "         and checkerCode = " + database.toSqlValue(defect.checkerCode)
        + "         and fileName = " + database.toSqlValue(defect.fileName)
        + "         and modulePath " + database.compareEqual(defect.modulePath)
        + "         and className " + database.compareEqual(defect.className)
        + "         and methodName " + database.compareEqual(defect.methodName)
        + "     )"
        + ", " + occurence.startLine
        + ", " + occurence.endLine
        + ", " + occurence.charStart
        + ", " + occurence.charEnd
        + ", 'NEW'"
        + ", " + database.toSqlValue(occurence.variableName)
        + ", " + database.toSqlValue(occurence.stringValue)
        + ", " + database.toSqlValue(occurence.fieldName)
        + ", " + database.toSqlValue(occurence.message)
        + ", now()"
        + ", now()"
        + ", " + userNo
        + ")";

    database.exec(sql, function (err, result) {
        if (err) {
            logging.error(err.message);
        }
    });
}

function newOrUpdateDefectV2(defect, userNo, snapshotId, groupId){
    var sql = "select " +
        "did, toolName, language, checkerCode, fileName, modulePath, className, methodName," +
        "severityCode, ifnull(categoryName,'') as categoryName, statusCode, message, " +
        "createdDateTime, modifiedDateTime, creatorNo, modifierNo, chargerNo, reviewerNo, approvalNo " +
        "from Defect " +
        "where " +
        " toolName = " +  database.toSqlValue(defect.toolName)
        + " and language = " + database.toSqlValue(defect.language)
        + " and checkerCode = " + database.toSqlValue(defect.checkerCode)
        + " and categoryName " + database.compareEqual(defect.categoryName)
        + " and fileName = " + database.toSqlValue(defect.fileName)
        + " and modulePath " + database.compareEqual(defect.modulePath)
        + " and className " + database.compareEqual(defect.className)
        + " and methodName " + database.compareEqual(defect.methodName) ;

    database.exec(sql, function (err, dbResult) {
        if(err){
            logging.error(err.message);
            return;
        }

        if(dbResult.length < 1){
            insertDefectV2(defect, userNo, snapshotId);
        } else {
            updateDefectV2(defect, userNo, snapshotId, dbResult[0].did);
        }

    });
}

function newOrUpdateDefect(defect, userNo, snapshotId, groupId){
    var sql = "select " +
        "did, toolName, language, checkerCode, fileName, modulePath, className, methodName," +
        "severityCode, statusCode, message, " +
        "createdDateTime, modifiedDateTime, creatorNo, modifierNo, chargerNo, reviewerNo, approvalNo " +
        "from Defect " +
        "where " +
        " toolName = " +  database.toSqlValue(defect.toolName)
        + " and language = " + database.toSqlValue(defect.language)
        + " and checkerCode = " + database.toSqlValue(defect.checkerCode)
        + " and fileName = " + database.toSqlValue(defect.fileName)
        + "  and modulePath " + database.compareEqual(defect.modulePath)
        + " and className " + database.compareEqual(defect.className)
        + " and methodName " + database.compareEqual(defect.methodName) ;

    database.exec(sql, function (err, dbResult) {
        if(err){
            logging.error(err.message);
            return;
        }

        if(dbResult.length < 1){
            insertDefect(defect, userNo, snapshotId);
        } else {
            updateDefect(defect, userNo, snapshotId, dbResult[0].did);
        }

    });
}

function addSnapshot(snapshotId, groupId, userNo){
    if(snapshotId === undefined || snapshotId <= 0 || groupId === undefined || groupId === -1 ){
        return;
    }
    var sql = "SELECT id FROM Snapshot WHERE id = " + snapshotId;

    database.exec(sql, function (err, result) {
        if (err) {
            return;
        }
        if (result) {
            var sql = "INSERT INTO Snapshot "
                    + " (id, groupId, createdDateTime, creatorNo) "
                    + " VALUES ( "
                    + snapshotId
                    + ", " + groupId
                    + ", now()"
                    + ", " + userNo + ") ";

            database.exec(sql, function (err, result) {
                if (err) {
                    logging.error(err.message);
                }
            });
        }
    });
}

function insertDefectV2(defect, userNo, snapshotId){
    var sql = "INSERT INTO Defect "
        + "(toolName, language, checkerCode, fileName, modulePath, className, methodName, "
        + " severityCode, categoryName, statusCode, message, createdDateTime, modifiedDateTime, creatorNo, modifierNo) "
        + "VALUES (  "
        + database.toSqlValue(defect.toolName)
        + ", " + database.toSqlValue(defect.language)
        + ", " + database.toSqlValue(defect.checkerCode)
        + ", " + database.toSqlValue(defect.fileName)
        + ", " + database.toSqlValue(defect.modulePath)
        + ", " + database.toSqlValue(defect.className)
        + ", " + database.toSqlValue(defect.methodName)
        + ", " + database.toSqlValue(defect.severityCode)
        + ", " + database.toSqlValue(defect.categoryName)
        + ", " + database.toSqlValue('NEW')
        + ", " + database.toSqlValue(defect.message)
        + ", now()"
        + ", now()"
        + ", " + userNo
        + ", " + userNo
        + ")";

    database.exec(sql, function (err, result){
        if(err){
            logging.error(err.message);
            return;
        }

        if(result){
            for(var i=0; i<defect.occurences.length; i++){
                insertOccurence(defect, i, defect.createdDateTime, userNo);
                addSnapshotOccurenceMap(defect, i, defect.createdDateTime, userNo, snapshotId)
            }
            //addSnapshotDefectMap(snapshotId, defect);
            addSnapshotDefectMapV2(snapshotId, defect);
        }
    });
}

function insertDefect(defect, userNo, snapshotId){
    var sql = "INSERT INTO Defect "
        + "(toolName, language, checkerCode, fileName, modulePath, className, methodName, "
        + " severityCode, statusCode, message, createdDateTime, modifiedDateTime, creatorNo, modifierNo) "
        + "VALUES (  "
        + database.toSqlValue(defect.toolName)
        + ", " + database.toSqlValue(defect.language)
        + ", " + database.toSqlValue(defect.checkerCode)
        + ", " + database.toSqlValue(defect.fileName)
        + ", " + database.toSqlValue(defect.modulePath)
        + ", " + database.toSqlValue(defect.className)
        + ", " + database.toSqlValue(defect.methodName)
        + ", " + database.toSqlValue(defect.severityCode)
        + ", " + database.toSqlValue('NEW')
        + ", " + database.toSqlValue(defect.message)
        + ", now()"
        + ", now()"
        + ", " + userNo
        + ", " + userNo
        + ")";

    database.exec(sql, function (err, result){
        if(err){
            logging.error(err.message);
            return;
        }

        if(result){
            for(var i=0; i<defect.occurences.length; i++){
                insertOccurence(defect, i, defect.createdDateTime, userNo);
                addSnapshotOccurenceMap(defect, i, defect.createdDateTime, userNo, snapshotId)
            }
        addSnapshotDefectMap(snapshotId, defect);
        }
    });
}

function insertOccurence(defect, index, createdDateTime, userNo){
    var occurence = defect.occurences[index];

    var sql = "INSERT INTO Occurence "
        + "(did, startLine, endLine, charStart, charEnd, statusCode, variableName, stringValue,"
        + " fieldName, message, createdDateTime, modifiedDateTime, creatorNo) "
        + "VALUES "
        + "("
        + "     (select did from Defect where "
        + "         toolName = " + database.toSqlValue(defect.toolName)
        + "         and language = " + database.toSqlValue(defect.language)
        + "         and checkerCode = " + database.toSqlValue(defect.checkerCode)
        + "         and fileName = " + database.toSqlValue(defect.fileName)
        + "         and modulePath " + database.compareEqual(defect.modulePath)
        + "         and className " + database.compareEqual(defect.className)
        + "         and methodName " + database.compareEqual(defect.methodName)
        + "     )"
        + ", " + occurence.startLine
        + ", " + occurence.endLine
        + ", " + occurence.charStart
        + ", " + occurence.charEnd
        + ", 'NEW'"
        + ", " + database.toSqlValue(occurence.variableName)
        + ", " + database.toSqlValue(occurence.stringValue)
        + ", " + database.toSqlValue(occurence.fieldName)
        + ", " + database.toSqlValue(occurence.message)
        + ", now()"
        + ", now()"
        + ", " + userNo
        + ")";

    database.exec(sql, function (err, result){
        if(err){
            logging.error(err.message);
        }
    });
}

function isSameDefect(d1, d2){
    if(d1 === undefined || d2 === undefined){
        return false;
    }

    if(!dexterUtil.isSameString(d1.checkerCode, d2.checkerCode)){
        return false;
    }
    if(!dexterUtil.isSameString(d1.methodName, d2.methodName)){
        return false;
    }
    if(!dexterUtil.isSameString(d1.className, d2.className)){
        return false;
    }
    if(!dexterUtil.isSameString(d1.fileName, d2.fileName)){
        return false;
    }
    if(!dexterUtil.isSameString(d1.toolName, d2.toolName)){
        return false;
    }
    if(!dexterUtil.isSameString(d1.language, d2.language)){
        return false;
    }
    if(!dexterUtil.isSameString(d1.modulePath, d2.modulePath)){
        return false;
    }

    return true;
}

// defect: update, occur: delete/insert
function updateDefect(defect, userNo, snapshotId, did) {
    var sql = "UPDATE Defect SET "
        + " severityCode = " + database.toSqlValue(defect.severityCode)
        + " , statusCode = CASE WHEN statusCode='FIX' THEN 'NEW' ELSE statusCode END"
        + " , message = " + database.toSqlValue(defect.message)
        + " , modifiedDateTime = now()"
        + " , modifierNo = " + userNo
        + " WHERE "
        + "     did = " + did;

    database.exec(sql, function (err, result){
        if(err){
            logging.error(err.message);
        }

        if(result){
            updateOccurence(defect, userNo, snapshotId);
            addSnapshotDefectMap(snapshotId, defect);
        }
    });
}

// defect: update, occur: delete/insert
function updateDefectV2(defect, userNo, snapshotId, did) {
    var sql = "UPDATE Defect SET "
        + " severityCode = " + database.toSqlValue(defect.severityCode)
        + " , statusCode = CASE WHEN statusCode = 'EXC' THEN statusCode ELSE 'NEW' END"
        + " , message = " + database.toSqlValue(defect.message)
        + " , modifiedDateTime = now()"
        + " , modifierNo = " + userNo
        + " WHERE "
        + "     did = " + did;

    database.exec(sql, function (err, result){
        if(err){
            logging.error(err.message);
        }

        if(result){
            updateOccurence(defect, userNo, snapshotId);
            addSnapshotDefectMapV2(snapshotId, defect);
        }
    });
};


function updateOccurence(newDf, userNo, snapshotId) {
    var sql = "DELETE FROM Occurence "
        + "WHERE "
        + "     did in "
        + "     (select did from Defect where "
        + "         toolName = " + database.toSqlValue(newDf.toolName)
        + "         and language = " + database.toSqlValue(newDf.language)
        + "         and checkerCode = " + database.toSqlValue(newDf.checkerCode)
        + "         and fileName = " + database.toSqlValue(newDf.fileName)
        + "         and modulePath " + database.compareEqual(newDf.modulePath)
        + "         and className " + database.compareEqual(newDf.className)
        + "         and methodName " + database.compareEqual(newDf.methodName)
        + "     )";

    database.exec(sql, function (err, result){
        if(err){
            logging.error(err.message);
        }
        if(result && newDf != undefined && newDf.occurences != undefined && newDf.occurences.length != undefined){
            for(var i =0; i<newDf.occurences.length; i++){
                insertOccurence(newDf, i, newDf.createdDateTime, userNo);
                addSnapshotOccurenceMap(newDf, i, newDf.createdDateTime, userNo, snapshotId);
            }
        }
    });
}

function fixDefect(defect, userNo, snapshotId){

    if(defect == undefined || userNo == undefined){
        logging.error("Invalid Parameter at fixDefect() in analysis.js");
        return;
    }

    var sql = "UPDATE Defect SET"
        + " statusCode = 'FIX'"
        + " , modifiedDateTime = now()"
        + " , modifierNo = " + userNo
        + " WHERE "
        + "         toolName = " + database.toSqlValue(defect.toolName)
        + "         and language = " + database.toSqlValue(defect.language)
        + "         and checkerCode = " + database.toSqlValue(defect.checkerCode)
        + "         and fileName = " + database.toSqlValue(defect.fileName)
        + "         and modulePath " + database.compareEqual(defect.modulePath)
        + "         and className " + database.compareEqual(defect.className)
        + "         and methodName " + database.compareEqual(defect.methodName);

    database.exec(sql, function (err, result){
        if(err){
            logging.error(err.message);
            logging.error(sql);
        }

        if(result) {
            updateOccurence(defect, userNo, snapshotId);
        }
    });
}

exports.getGlobalDid = function(req, res) {
    if(req == undefined || req.body == undefined || req.body.defect == undefined || req.currentUserId == undefined){
        res.send({status:"fail", errorMessage: "No Data or No currentUserId"})
		return;
    }

    var defect;
	try {
		defect = JSONbig.parse(req.body.defect);
	} catch (e) {
		logging.error(e);
		res.send({result:'fail', errorMessage: 'defect is not json raw format'});
		return;
	}
	
    var sql = "select did from Defect "
        + " WHERE "
        + "     toolName = " + database.toSqlValue(defect.toolName)
        + "     and language = " + database.toSqlValue(defect.language)
        + "     and checkerCode = " + database.toSqlValue(defect.checkerCode)
        + "     and fileName = " + database.toSqlValue(defect.fileName)
        + "     and modulePath " + database.compareEqual(defect.modulePath)
        + "     and className " + database.compareEqual(defect.className)
        + "     and methodName " + database.compareEqual(defect.methodName);

    database.exec(sql, function (err, result) {
        if(err){
            res.send({result:'fail', errorMessage: err.message, errorCode: -1});
            logging.error(err.message);
            return;
        }

        if(result.length != 1){
            var msg = 'there is no result for ' + defect.did;
            res.send({result:'fail', errorMessage: msg , errorCode: -2});
            logging.error(msg);
            return;
        }

        res.send({
            result: 'ok',
            globalDid: "" + result[0].did
        });
    });
};

exports.deleteDefect = function(req, res) {
    if(req == undefined || req.body == undefined || req.body.modulePath == undefined
        || req.body.fileName == undefined || req.currentUserId == undefined){
        res.send({status:"fail", errorMessage: "No Data or No currentUserId"});
		return;
    }

    var modulePath = req.body.modulePath;
    var fileName = req.body.fileName;
    var userNo = account.getUserNo(req.currentUserId);

    var sql = "UPDATE Defect SET "
        + " statusCode = 'EFD'"
        + " , modifiedDateTime = now()"
        + " , modifierNo = " + userNo
        + " WHERE "
        + "     fileName = " + database.toSqlValue(fileName)
        + "     and modulePath " + database.compareEqual(modulePath);

    database.exec(sql, function (err, result){
        if(err){
            logging.error(err.message);
            res.send({status:"fail", errorMessage: "db processing failed"})
        }

        if(result){
            res.send({status:"ok"})
        }
    });
};

exports.getOccurencesByDid = function(req, res) {
    if(req == undefined || req.params == undefined || req.params.did == undefined){
        res.send({status:"fail", errorMessage: "Input(parameter) error"})
		return;
    }

    var did = req.params.did;
    var sql = "SELECT "
        + "     oid, did, startLine, endLine, charStart, charEnd, variableName, stringValue, fieldName, message, "
        + "     createdDateTime, modifiedDateTime, creatorNo, modifierNo, "
        + "     (select userId from Account where userNo = creatorNo) as creatorId, "
        + "     (select userId from Account where userNo = modifierNo) as modifierId "
        + " FROM Occurence "
        + " WHERE "
        + "     did = " + did ;

    sql += " order by startLine";
    database.exec(sql, function (err, result) {
        if(err){
            res.send({result:'fail', errorMessage: err.message, errorCode: -1});
            logging.error(err.message);
            return;
        }

        if(result){
            res.send(result);
            return;
        }

        res.send({result: 'fail', errorMessage: "unknown error"});
    });
};

exports.getAllSnapshot = function (req, res){
    //sql = 'SELECT A.id, A.groupId, A.createdDateTime, B.UserId FROM Snapshot AS A LEFT OUTER JOIN Account AS B ON A.creatorNo = B.UserNo';
    var sql = 'SELECT A.id, A.groupId, A.createdDateTime,'
        +"(select count(did) as defectCount from SnapshotDefectMap where snapshotId = A.id) as defectCount,"
        +"(select count(did) as defectCount from SnapshotDefectMap where snapshotId = A.id and severityCode='CRI' ) as criCount,"
        +"(select count(did) as defectCount from SnapshotDefectMap where snapshotId = A.id and severityCode='MAJ') as majCount,"
        +"(select userId from Account where userNo=A.creatorNo) as userId FROM Snapshot AS A ";

    database.exec(sql, function (err, result) {
        if(err) {
            logging.error(err.message);
            res.send({status:'fail', errMessage: err.message});
        } else if(result) {
            res.send({status:'ok', snapshotInfo : result});
        } else {
            res.send({status:'fail', errMessage: 'Unknown Error'});
        }
    });
};

exports.getAllSnapshotV2 = function (req, res){
    //sql = 'SELECT A.id, A.groupId, A.createdDateTime, B.UserId FROM Snapshot AS A LEFT OUTER JOIN Account AS B ON A.creatorNo = B.UserNo';
    var sql = 'SELECT A.id, A.groupId, A.createdDateTime,'
        +"(select count(did) as defectCount from SnapshotDefectMap where snapshotId = A.id) as defectCount,"
        +"(select count(did) as defectCount from SnapshotDefectMap where snapshotId = A.id and severityCode='CRI' ) as criCount,"
        +"(select count(did) as defectCount from SnapshotDefectMap where snapshotId = A.id and severityCode='MAJ') as majCount,"
        +"(select count(did) as defectCount from SnapshotDefectMap where snapshotId = A.id and categoryName='SECURITY') as secCount,"
        +"(select userId from Account where userNo=A.creatorNo) as userId FROM Snapshot AS A ";

    database.exec(sql, function (err, result) {
        if(err) {
            logging.error(err.message);
            res.send({status:'fail', errMessage: err.message});
        } else if(result) {
            res.send({status:'ok', snapshotInfo : result});
        } else {
            res.send({status:'fail', errMessage: 'Unknown Error'});
        }
    });
};

exports.getOccurencesByFileNameInSnapshot = function(req, res){
    if(req == undefined || req.query == undefined || req.query.fileName == undefined || req.query.snapshotId){
        res.send({status:"fail", errorMessage: "Input(parameter) error"});
        return;
    }
    var modulePath;
    if (req.query.modulePath)
        modulePath = base64.decode(req.query.modulePath);

    var fileName = req.query.fileName;
    var snapshotId = req.query.snapshotId;

    var sql = "SELECT "
        + " did, if(startLine = -1, 1, startLine) as startLine, endLine, if(charStart = -1,'N/A', charStart) as charStart, "
        + " if(charEnd = -1,'N/A', charEnd) as charEnd, ifnull(variableName,'N/A') as variableName, ifnull(stringValue,'N/A') as stringValue,"
        + " ifnull(fieldName,'N/A') as fieldName, message, createdDateTime, modifiedDateTime, creatorNo, ifnull(modifierNo,'N/A') as modifierNo, "
        + "     (select userId from Account where userNo = creatorNo) as creatorId, "
        + "     (select userId from Account where userNo = modifierNo) as modifierId, "
        + "     (select checkerCode from Defect B where B.did = A.did) as checkerCode, "
        + "     (select severityCode from Defect B where B.did = A.did) as severityCode "
        + " FROM SnapshotOccurenceMap A"
        + " WHERE did in(select did from Defect WHERE" ;

    if(modulePath == "undefined"){
        modulePath = "";
        sql +=  "     modulePath is " + database.toSqlValue(modulePath);
    }
    else{
        sql += "     modulePath = " + database.toSqlValue(modulePath);
    }
    sql += "     and fileName = " + database.toSqlValue(fileName) + ") and snapshotId = "+ database.toSqlValue(snapshotId) + "group by did order by startLine" ;

    database.exec(sql, function (err, result) {
        if(err){
            res.send({result:'fail', errorMessage: err.message, errorCode: -1});
            logging.error(err.message);
            return;
        }

        if(result){
            res.send(result);
            return;
        }

        res.send({result: 'fail', errorMessage: "unknown error"});
    });
};


exports.getOccurencesByFileNameInSnapshotV2 = function(req, res){
    if(req == undefined || req.body.params == undefined || req.body.params.fileName == undefined || req.body.params.snapshotId == undefined){
        res.send({status:"fail", errorMessage: "Input(parameter) error"});
        return;
    }
    var modulePath = '';
    if (req.body.params.modulePath)
        modulePath = base64.decode(req.body.params.modulePath);

    var fileName = req.body.params.fileName;
    var snapshotId = req.body.params.snapshotId;

    var sql = "SELECT "
        + " did, if(startLine = -1, 1, startLine) as startLine, endLine, if(charStart = -1,'N/A', charStart) as charStart, "
        + " if(charEnd = -1,'N/A', charEnd) as charEnd, ifnull(variableName,'N/A') as variableName, ifnull(stringValue,'N/A') as stringValue,"
        + " ifnull(fieldName,'N/A') as fieldName, message, createdDateTime, modifiedDateTime, creatorNo, ifnull(modifierNo,'N/A') as modifierNo, "
        + "     (select userId from Account where userNo = creatorNo) as creatorId, "
        + "     (select userId from Account where userNo = modifierNo) as modifierId, "
        + "     (select checkerCode from Defect B where B.did = A.did) as checkerCode, "
        + "     (select severityCode from Defect B where B.did = A.did) as severityCode "
        + " FROM SnapshotOccurenceMap A"
        + " WHERE did in(select did from Defect WHERE" ;

    if(modulePath == "undefined"){
        modulePath = "";
        sql +=  "     modulePath is " + database.toSqlValue(modulePath);
    }
    else{
        sql += "     modulePath = " + database.toSqlValue(modulePath);
    }
    sql += "     and fileName = " + database.toSqlValue(fileName) + ") and snapshotId = "+ database.toSqlValue(snapshotId) + " order by startLine" ;

    database.exec(sql, function (err, result) {
        if(err){
            res.send({result:'fail', errorMessage: err.message, errorCode: -1});
            logging.error(err.message);
            return;
        }

        if(result){
            res.send(result);
            return;
        }

        res.send({result: 'fail', errorMessage: "unknown error"});
    });
};


exports.getDefectListInSnapshot = function (req, res){
    if(req == undefined || req.query== undefined || req.query.snapshotId == undefined){
        res.send({status:"fail", errorMessage: "Input(parameter) error"});
		return;
    }

    sql = "SELECT "
        + "snapshotId, did, toolName, language, checkerCode, fileName, ifnull(modulePath,'') as modulePath , ifnull(className,'') as className ,"
        + "ifnull(methodName,'') as methodName, severityCode, statusCode, message, createdDateTime, modifiedDateTime, creatorNo, modifierNo,"
        + "(select count(B.startLine) from SnapshotOccurenceMap B where B.did = A.did and snapshotId = "+ database.toSqlValue(req.query.snapshotId)+ ") as occurenceCount,"
        + " ifnull((select group_concat(if(B.startLine = -1,'1', B.startLine) ORDER BY B.startLine SEPARATOR '|') from SnapshotOccurenceMap B where B.did = A.did and snapshotId = "+ database.toSqlValue(req.query.snapshotId)+ "),'') as occurenceLine,"
        + "(select statusCode from Defect B where B.did = A.did) as currentStatusCode,"
        + "(select userId from Account where userNo = A.creatorNo) as creatorId,"
        + "(select userId from Account where userNo = A.modifierNo) as modifierId, "
        + "ifnull(chargerNo,'') as chargerNo, ifnull(reviewerNo,'') as reviewerNo, ifnull(approvalNo,'') as approvalNo "
        + "From SnapshotDefectMap AS A WHERE snapshotId =" + database.toSqlValue(req.query.snapshotId);

    database.exec(sql, function (err, result) {
        if(err) {
            logging.error(err.message);
            res.send({status:'fail', errMessage: err.message});
        } else if(result) {
            res.send({status:'ok', defectInSnapshot : result});
        } else {
            res.send({status:'fail', errMessage: 'Unknown Error'});
        }
    });
};

exports.getDefectListInSnapshotV2 = function (req, res){
    if(req == undefined || req.params== undefined || req.params.snapshotId == undefined){
        res.send({status:"fail", errorMessage: "Input(parameter) error"});
        return;
    }

    var snapshotId = req.params.snapshotId;

    var sql = "SELECT "
        + "snapshotId, did, toolName, language, checkerCode, fileName, ifnull(modulePath,'') as modulePath , ifnull(className,'') as className ,"
        + "ifnull(methodName,'') as methodName, severityCode, ifnull(categoryName,'') as categoryName, statusCode, message, createdDateTime, modifiedDateTime, creatorNo, modifierNo,"
        + "(select count(B.startLine) from SnapshotOccurenceMap B where B.did = A.did and snapshotId = "+ database.toSqlValue(snapshotId)+ ") as occurenceCount,"
        + " ifnull((select group_concat(if(B.startLine = -1,'1', B.startLine) ORDER BY B.startLine SEPARATOR '|') "
        + " FROM SnapshotOccurenceMap B where B.did = A.did and snapshotId = "+ database.toSqlValue(snapshotId)+ "),'') as occurenceLine,"
        + "(select statusCode from Defect B where B.did = A.did) as currentStatusCode,"
        + "(select userId from Account where userNo = A.creatorNo) as creatorId,"
        + "(select userId from Account where userNo = A.modifierNo) as modifierId, "
        + "ifnull(chargerNo,'') as chargerNo, ifnull(reviewerNo,'') as reviewerNo, ifnull(approvalNo,'') as approvalNo "
        + "From SnapshotDefectMap AS A WHERE snapshotId =" + database.toSqlValue(snapshotId);

    database.exec(sql, function (err, result) {
        if(err) {
            logging.error(err.message);
            res.send({status:'fail', errMessage: err.message});
        } else if(result) {
            res.send({status:'ok', defectInSnapshot : result});
        } else {
            res.send({status:'fail', errMessage: 'Unknown Error'});
        }
    });
};

exports.getDefectListInSnapshotForDid = function (req, res){
    if(req === undefined || req.params === undefined || req.params.snapshotId === undefined || req.params.did === undefined){
        res.send({status:"fail", errorMessage: "Input(parameter) error"});
        return;
    }

    var snapshotId = req.params.snapshotId;
    var did = req.params.did;


    var sql = "SELECT "
        + "snapshotId, did, toolName, language, checkerCode, fileName, ifnull(modulePath,'') as modulePath , ifnull(className,'') as className ,"
        + "ifnull(methodName,'') as methodName, severityCode, ifnull(categoryName,'') as categoryName, statusCode, message, createdDateTime, modifiedDateTime, creatorNo, modifierNo,"
        + "(select count(B.startLine) from SnapshotOccurenceMap B where B.did = A.did and snapshotId = "+ database.toSqlValue(snapshotId)+ ") as occurenceCount,"
        + " ifnull((select group_concat(if(B.startLine = -1,'1', B.startLine) ORDER BY B.startLine SEPARATOR '|') "
        + " FROM SnapshotOccurenceMap B where B.did = A.did and snapshotId = "+ database.toSqlValue(snapshotId)+ "),'') as occurenceLine,"
        + "(select statusCode from Defect B where B.did = A.did) as currentStatusCode,"
        + "(select userId from Account where userNo = A.creatorNo) as creatorId,"
        + "(select userId from Account where userNo = A.modifierNo) as modifierId, "
        + "ifnull(chargerNo,'') as chargerNo, ifnull(reviewerNo,'') as reviewerNo, ifnull(approvalNo,'') as approvalNo "
        + "From SnapshotDefectMap AS A WHERE snapshotId =" + database.toSqlValue(snapshotId);

    if(did !== undefined || did !== "" || did === null){
        sql += " and did = " + database.toSqlValue(did);
    }

    database.exec(sql, function (err, result) {
        if(err) {
            logging.error(err.message);
            res.send({status:'fail', errMessage: err.message});
        } else if(result) {
            res.send({status:'ok', defectInSnapshot : result});
        } else {
            res.send({status:'fail', errMessage: 'Unknown Error'});
        }
    });
};



exports.getOccurencesByFileName = function(req, res) {
    if(req == undefined || req.query == undefined || req.query.fileName == undefined){
        res.send({status:"fail", errorMessage: "Input(parameter) error"});
		return;
    }
    var modulePath;
	if (req.query.modulePath)
		modulePath = base64.decode(req.query.modulePath);
	
    var fileName = req.query.fileName;
    var changeToBase64 = req.query.changeToBase64 || true;

    var sql = "SELECT "
        + " oid, did, if(startLine = -1, 1, startLine) as startLine, endLine, if(charStart = -1,'N/A', charStart) as charStart, "
        + " if(charEnd = -1,'N/A', charEnd) as charEnd, ifnull(variableName,'N/A') as variableName, ifnull(stringValue,'N/A') as stringValue,"
        + " ifnull(fieldName,'N/A') as fieldName, message, createdDateTime, modifiedDateTime, creatorNo, ifnull(modifierNo,'N/A') as modifierNo, "
        + "     (select userId from Account where userNo = creatorNo) as creatorId, "
        + "     (select userId from Account where userNo = modifierNo) as modifierId, "
        + "     (select checkerCode from Defect B where B.did = A.did) as checkerCode, "
        + "     (select severityCode from Defect B where B.did = A.did) as severityCode "
        + " FROM Occurence A"
        + " WHERE did in(select did from Defect WHERE" ;

    if(modulePath == "undefined"){
        modulePath = "";
        sql +=  "     modulePath is " + database.toSqlValue(modulePath);
    }
    else{
        sql += "     modulePath = " + database.toSqlValue(modulePath);
    }
    sql += "     and fileName = " + database.toSqlValue(fileName) + ") order by startLine" ;
    database.exec(sql, function (err, result) {
        if(err){
            res.send({result:'fail', errorMessage: err.message, errorCode: -1});
            logging.error(err.message);
            return;
        }

        if(result){
            //res.send(result);
            res.send(result);
            return;
        }

        res.send({result: 'fail', errorMessage: "unknown error"});
    });
};

