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
var account = require("../routes/account");
var sql;


function addDefectGroupMap(res, id, parentId, userNo) {
    sql = "INSERT INTO DefectGroupMap "
        + "(id, parentId, createdDateTime, creatorNo) "
        + "VALUES ("
        + id
        + ", " + parentId
        + ", now(), " + userNo
        + ") ";

    database.exec(sql, function (err, result){
        if(err) {
            logging.error(err.message);
            res.send({status:"fail", errMessage: err.message});
        } else if(result){
            res.send({status:"ok"});
        } else {
            res.send({status:"fail", errMessage: "Unknown Error"});
        }
    });
}

function setDefectGroupMap(res, id, parentId, userNo) {
    sql = "UPDATE DefectGroupMap SET "
        + " parentId = " + parentId
        + ", createdDateTime = now() "
        + ", creatorNo = " + userNo
        + " WHERE id = " + id;

    database.exec(sql, function (err, result){
        if(err) {
            logging.error(err.message);
            res.send({status:"fail", errMessage: err.message});
        } else if(result){
            res.send({status:"ok"});
        } else {
            res.send({status:"fail", errMessage: "Unknown Error"});
        }
    });
}


exports.checkSupportedChecker = function(req, res){

};

exports.addDefectGroup = function(req, res){
    if(req === undefined || req.body === undefined || req.body.groupName === undefined || req.currentUserId === undefined){
        res.send({status:"fail", errMessage: "No Data or No currentUserId"});
		return;
    }
    var userNo = account.getUserNo(req.currentUserId);

    sql = "SELECT (if(max(id) is null, 1, max(id) + 1)) as maxId FROM DefectGroup";

    database.exec(sql, function (err, result){
        if(err) {
           logging.error(err.message);
           res.send({status:"fail", errMessage: err.message});
        } else if(result && result.length > 0){
            var maxId = result[0].maxId;

            sql = "INSERT INTO DefectGroup "
                + "(id, groupName, groupType, description, createdDateTime, creatorNo) "
                + " VALUES ( "
                + maxId
                + ", " + database.toSqlValue(req.body.groupName)
                + ", " + database.toSqlValue(req.body.groupType)
                + ", " + database.toSqlValue(req.body.description);

            if(req.body.createdDateTime !== undefined && (req.body.createdDateTime !== "") && (0 !== req.body.createdDateTime)){
                sql += ", " + database.getDateTimeEx(req.body.createdDateTime);
            } else {
                sql += ", now() ";
            }

            if(req.body.creatorNo !== undefined && req.body.creatorNo !== ""){
                sql += ", " + req.body.creatorNo + ")";
            } else {
                sql += ", " + userNo + ")";
            }

            database.exec(sql, function (err, result) {
                if(err){
                    logging.error(err.message);
                    res.send({status:"fail", errMessage: err.message});
                } else if(result){
                    if(req.body.parentId !== -1){
                        addDefectGroupMap(res, maxId, req.body.parentId, userNo );
                    } else {
                        res.send({status:"ok"});
                    }
                } else {
                    res.send({status:"fail", errMessage: "Unknown Error"});
                }
            });
        } else {
            logging.error("no max id for DefectGroup");
            res.send({status:"fail", errMessage: "no max id for DefectGroup"});
        }
    });
};

exports.updateDefectGroup = function(req, res){
    if(req === undefined || req.body === undefined || req.body.groupId === undefined || req.currentUserId === undefined){
        res.send({status:"fail", errMessage: "No Data or No currentUserId"});
		return;
    }
    var userNo = account.getUserNo(req.currentUserId);

    sql = "UPDATE DefectGroup SET "
        + "groupName = " + database.toSqlValue(req.body.groupName)
        + ", groupType = " + database.toSqlValue(req.body.groupType)
        + ", description = " + database.toSqlValue(req.body.description);

    if(req.body.createdDateTime !== undefined && req.body.createdDateTime !== ""){
        sql += ", createdDateTime = " + database.getDateTimeEx(req.body.createdDateTime);
    }

    if(req.body.creatorNo !== undefined && req.body.creatorNo !== ""){
        sql += ", creatorNo = " + req.body.creatorNo;
    }

    sql += " WHERE id = " + req.body.groupId;

    database.exec(sql, function (err, result) {
        if(err){
            logging.error(err.message);
            res.send({status:"fail", errMessage: err.message});
        } else if(result){
            if(req.body.parentId !== -1){
                setDefectGroupMap(res, req.body.groupId, req.body.parentId, userNo);
            } else {
                res.send({status:"ok"});
            }
        } else {
            res.send({status:"fail", errMessage: "Unknown Error"});
        }
    });
};

exports.getDefectGroupId = function(req, res) {
    var groupName = req.params.groupName;

    sql = "SELECT id DefectGroup "
        + " WHERE groupName = " + groupName;

    database.exec(sql, function (err, result){
        if(err) {
            logging.error(err.message);
            res.send({status:"fail", errMessage: err.message});
        } else if(result){
            res.send({status:"ok", result: result});
        } else {
            res.send({status:"fail", errMessage: "Unknown Error"});
        }
    });
};

exports.deleteDefectGroup = function(req, res) {
    var id = req.params.id;

    sql = "DELETE FROM DefectGroup "
        + " WHERE id = " + id;

    database.exec(sql, function (err, result){
        if(err) {
            logging.error(err.message);
            res.send({status:"fail", errMessage: err.message});
        } else if(result){
            res.send({status:"ok", result: result});
        } else {
            res.send({status:"fail", errMessage: "Unknown Error"});
        }
    });
};

exports.getDefectGroup = function(req, res) {
    var groupName = req.params.groupName;

    sql = "SELECT A.id, A.groupName, A.groupType, A.description, UNIX_TIMESTAMP(A.createdDateTime) as createdDateTime, "
        + " A.creatorNo, if(B.parentId is null, -1, B.parentId) as parentId  "
        + "from DefectGroup A left join DefectGroupMap B on A.id = B.id ";

    if(groupName !== undefined && groupName !== ""){
        sql += " where A.groupName = " + database.toSqlValue(groupName);
    }

    database.exec(sql, function (err, result){
        if(err) {
            logging.error(err.message);
            res.send({status:"fail", errMessage: err.message});
        } else if(result){
            res.send({status:"ok", result: JSON.stringify(result)});
        } else {
            logging.error("Unknown Error at config.js / getDefectGroup");
            res.send({status:"fail", errMessage: "Unknown Error"});
        }
    });
};

exports.getCodes = function(req, res) {
    var codeKey = req.params.codeKey;

    sql = "SELECT codeKey, codeValue, codeName, description "
        + " FROM Configure "
        + " WHERE codeKey = " + database.toSqlValue(codeKey);

    database.exec(sql, function (err, result){
        if(err) {
            logging.error(err.message);
            res.send({status:"fail", errMessage: err.message});
        } else if(result) {
            res.send({status:"ok", result : JSON.stringify(result)});
        } else {
            res.send({status:"fail", errMessage: "Unknown Error"});
        }
    });
};


exports.addAccessLog = function(param) {
    // TODO: test
    var sql = "INSERT INTO AccessLog "
        + " (remote, api, method, query, body, creatorNo, createdDateTime) "
        + " VALUES ( "
        + database.toSqlValue(param.remoteAddress)
        + ", " + database.toSqlValue(param.uri)
        + ", " + database.toSqlValue(param.method)
        + ", " + database.toSqlValue(JSON.stringify(param.query))
        + ", " + database.toSqlValue(JSON.stringify(param.body))
        + ", " + database.toSqlValue(param.currentUserNo)
        + ", now()) ";

    database.exec(sql, function (err, result){
        if(err) {
            logging.debug(err.message);
        }
    });
};


exports.getCheckerConfigJsonFile = function(req, res){
    var pluginName = req.params.pluginName;
    console.log(pluginName);
    var fileName = "../config/plugin/checker-config_"+ pluginName +".json";

    try{
        var configJson = require(fileName);
        logging.info( "Update " + pluginName + " checker config");
        res.send(configJson);
    }catch(e){
        res.send("noUpdate");
    }

};