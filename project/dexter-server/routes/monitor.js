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

exports.getUserCount = function(req, res) {
    var sql =
        "SELECT COUNT(userId) AS userCount          " +
        "FROM Account                               " +
        "WHERE userId!='admin' AND userId!='user'   ";

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
        "WHERE userId!='admin' and userId!='user'   " +
        "ORDER BY userId ASC                        ";

    database.execute(sql)
        .then(function(rows) {
            res.send({status:'ok', rows: rows});
        })
        .catch(function(err) {
            logging.error(err);
            res.send({status:"fail", errorMessage: err.message});
        });
};