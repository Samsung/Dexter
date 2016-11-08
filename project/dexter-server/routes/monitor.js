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
"use strict";

var database = require("../util/database");
var logging = require('../util/logging');

exports.getDefectCount = function(req, res) {
    var sql =
        "SELECT (SELECT COUNT(did) FROM Defect) AS defectCountTotal,                    " +
        "(SELECT COUNT(did) FROM Defect WHERE statusCode='FIX') AS defectCountFixed,    " +
        "COUNT(did) AS defectCountDismissed FROM Defect WHERE statusCode='EXC'          ";

    database.execute(sql)
        .then(function(rows) {
            res.send({status:'ok', values: rows[0]});
        })
        .catch(function(err) {
            logging.error(err);
            res.send({status:"fail", errorMessage: err.message});
        });
};

exports.getDetailedDefectCount = function(req, res) {
    var sql =
        "SELECT COUNT(*) AS allDefectCount,                                                                     " +
        "(SELECT SUM(if(statusCode = 'NEW', 1, 0)) FROM Defect) AS allNew,                                      " +
        "(SELECT SUM(if(statusCode = 'FIX', 1, 0)) FROM Defect) AS allFix,                                      " +
        "(SELECT SUM(if(statusCode = 'EXC', 1, 0)) FROM Defect) AS allDis,                                      " +
        "(SELECT SUM(if(statusCode = 'NEW', 1, 0) && if(severityCode = 'CRI', 1, 0)) FROM Defect) AS criNew,    " +
        "(SELECT SUM(if(statusCode = 'FIX', 1, 0) && if(severityCode = 'CRI', 1, 0)) FROM Defect) AS criFix,    " +
        "(SELECT SUM(if(statusCode = 'EXC', 1, 0) && if(severityCode = 'CRI', 1, 0)) FROM Defect) AS criDis,    " +
        "(SELECT SUM(if(statusCode = 'NEW', 1, 0) && if(severityCode = 'MAJ', 1, 0)) FROM Defect) AS majNew,    " +
        "(SELECT SUM(if(statusCode = 'FIX', 1, 0) && if(severityCode = 'MAJ', 1, 0)) FROM Defect) AS majFix,    " +
        "(SELECT SUM(if(statusCode = 'EXC', 1, 0) && if(severityCode = 'MAJ', 1, 0)) FROM Defect) AS majDis,    " +
        "(SELECT SUM(if(statusCode = 'NEW', 1, 0) && if(severityCode = 'MIN', 1, 0)) FROM Defect) AS minNew,    " +
        "(SELECT SUM(if(statusCode = 'FIX', 1, 0) && if(severityCode = 'MIN', 1, 0)) FROM Defect) AS minFix,    " +
        "(SELECT SUM(if(statusCode = 'EXC', 1, 0) && if(severityCode = 'MIN', 1, 0)) FROM Defect) AS minDis,    " +
        "(SELECT SUM(if(statusCode = 'NEW', 1, 0) && if(severityCode = 'CRC', 1, 0)) FROM Defect) AS crcNew,    " +
        "(SELECT SUM(if(statusCode = 'FIX', 1, 0) && if(severityCode = 'CRC', 1, 0)) FROM Defect) AS crcFix,    " +
        "(SELECT SUM(if(statusCode = 'EXC', 1, 0) && if(severityCode = 'CRC', 1, 0)) FROM Defect) AS crcDis,    " +
        "(SELECT SUM(if(statusCode = 'NEW', 1, 0) && if(severityCode = 'ETC', 1, 0)) FROM Defect) AS etcNew,    " +
        "(SELECT SUM(if(statusCode = 'FIX', 1, 0) && if(severityCode = 'ETC', 1, 0)) FROM Defect) AS etcFix,    " +
        "(SELECT SUM(if(statusCode = 'EXC', 1, 0) && if(severityCode = 'ETC', 1, 0)) FROM Defect) AS etcDis,    " +
        "(SELECT SUM(if(statusCode is null OR statusCode NOT IN ('NEW','FIX','EXC'), 1, 0)) FROM Defect) AS invalidStatusCode,  " +
        "(SELECT SUM(if(severityCode is null OR severityCode NOT IN ('CRI','MAJ','MIN','CRC','ETC'), 1, 0)) FROM Defect) AS invalidSeverityCode " +
        "FROM Defect                                                                                            ";

    database.execute(sql)
        .then(function(rows) {
            res.send({status:'ok', values: rows[0]});
        })
        .catch(function(err) {
            logging.error(err);
            res.send({status:"fail", errorMessage: err.message});
        });
};

exports.getUserCount = function(req, res) {
    var sql =
        "SELECT COUNT(userId) AS userCount          " +
        "FROM Account                               " +
        "WHERE userNo!='1' and userNo!='2'          " ;

    database.execute(sql)
        .then(function(rows) {
            res.send({status:'ok', value: rows[0].userCount});
        })
        .catch(function(err) {
            logging.error(err);
            res.send({status:"fail", errorMessage: err.message});
        });
};

exports.getUserList = function(req, res) {
    var sql =
        "SELECT userId                              " +
        "FROM Account                               " +
        "WHERE userNo!='1' and userNo!='2'          " ;

    database.execute(sql)
        .then(function(rows) {
            res.send({status:'ok', rows: rows});
        })
        .catch(function(err) {
            logging.error(err);
            res.send({status:"fail", errorMessage: err.message});
        });
};