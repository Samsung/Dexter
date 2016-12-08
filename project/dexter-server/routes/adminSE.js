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
var dutil = require('../util/dexter-util');
const mysql = require("mysql");
const _ = require('lodash');
const Q = require('q');
const Promise = require('bluebird');

exports.getModulePathList = function (req, res) {
    var sql = "SELECT modulePath FROM Defect group by modulePath order by modulePath";

    database.execV2(sql)
        .then((rows) => {
            res.send({status: 'ok', rows: rows});
        })
        .catch(error => {
            logging.error(error);
            res.send({status: "fail", errorMessage: error.message});
        });
};

function deleteCodeMetricsFromDB(modulePathList) {
    var sql = "delete from CodeMetrics";

    _.forEach(modulePathList, (modulePath, idx) => {
        if (idx == 0) {
            sql += " Where modulePath " + database.compareEqualWithEscape(modulePath);
        } else {
            sql += " or modulePath " + database.compareEqualWithEscape(modulePath);
        }
    });

    return database.execV2(sql)
        .then(() => {
            return Promise.resolve(modulePathList);
        })
        .catch(error => {
            return Promise.reject(error);
        });
}

function deleteFunctionMetricFromDB(modulePathList) {
    var sql = "delete from FunctionMetrics";

    _.forEach(modulePathList, (modulePath, idx) => {
        if (idx === 0) {
            sql += " Where modulePath " + database.compareEqualWithEscape(modulePath);
        } else {
            sql += " or modulePath " + database.compareEqualWithEscape(modulePath);
        }
    });

    return database.execV2(sql)
        .then(() => {
            return Promise.resolve(modulePathList);
        })
        .catch(error => {
            return Promise.reject(error);
        });
}

function deleteSourceCodeMapFromDB(modulePathList) {
    var sql = "delete from SourceCodeMap";
    _.forEach(modulePathList, (modulePath, idx) => {
        if (idx === 0) {
            sql += " Where modulePath " + database.compareEqualWithEscape(modulePath);
        } else {
            sql += " or modulePath " + database.compareEqualWithEscape(modulePath);
        }
    });
    return database.execV2(sql)
        .then(() => {
            return Promise.resolve(modulePathList);
        })
        .catch(error => {
            return Promise.reject(error);
        });
}

function deleteSnapshotDefectMapFromDB(modulePathList) {
    var sql = "delete from SnapshotDefectMap";
    _.forEach(modulePathList, (modulePath, idx) => {
        if (idx === 0) {
            sql += " Where modulePath " + database.compareEqualWithEscape(modulePath);
        } else {
            sql += " or modulePath " + database.compareEqualWithEscape(modulePath);
        }
    });

    return database.execV2(sql)
        .then(() => {
            return Promise.resolve(modulePathList);
        })
        .catch(error => {
            return Promise.reject(error);
        });
}

function deleteSnapshotOccurenceMapForDid(didList) {
    var promises = [];

    _.forEach(didList, (did) => {
        promises.push(new Promise((resolve, reject) => {
            const sql = `DELETE from SnapshotOccurenceMap where did=${mysql.escape(did.did)}`;
            database.execV2(sql)
                .then(() => {
                    resolve();
                })
                .catch((error) => {
                    reject(error);
                });
        }))
    });

    return Promise.all(promises)
        .then(() => {
            return "SUCCESS";
        })
        .catch(error => {
            logging.error(error);
            return [];
        });
}

function deleteOccurenceMapForDid(didList) {
    var promises = [];

    _.forEach(didList, (did) => {
        promises.push(new Promise((resolve, reject) => {
            const sql = `DELETE from Occurence where did=${mysql.escape(did.did)}`;
            database.execV2(sql)
                .then(() => {
                    resolve();
                })
                .catch((error) => {
                    reject(error);
                });
        }))
    });

    return Promise.all(promises)
        .then(() => {
            return "SUCCESS";
        })
        .catch(error => {
            logging.error(error);
            return [];
        });
}


function deleteSnapshotOccurenceMapFromDB(modulePathList) {
    var sql = `SELECT did FROM Defect `;

    _.forEach(modulePathList, (modulePath, idx) => {
        if (idx == 0) {
            sql += " Where modulePath " + database.compareEqualWithEscape(modulePath);
        } else {
            sql += " or modulePath " + database.compareEqualWithEscape(modulePath);
        }
    });

    return database.execV2(sql)
        .then((result)=> {
            return deleteSnapshotOccurenceMapForDid(result)
                .then(deleteOccurenceMapForDid(result))
                .then(() => {
                    return Promise.resolve(modulePathList);
                });
        })
        .catch(error => {
            return Promise.reject(error);
        });
}


function deleteDefectFromDB(modulePathList) {
    var sql = "DELETE from Defect";

    _.forEach(modulePathList,  (modulePath, idx) => {
        if (idx == 0) {
            sql += " Where modulePath " + database.compareEqualWithEscape(modulePath);
        } else {
            sql += " or modulePath " + database.compareEqualWithEscape(modulePath);
        }
    });

    return database.execV2(sql)
        .then(() => {
            return Promise.resolve(modulePathList);
        })
        .catch(error => {
            return Promise.reject(error);
        });
}

exports.deleteModulePathList = function (req, res) {
    if (!(_.has(req, "query.modulePathList") && req.query.modulePathList)) {
        res.send({status: "fail", errorMessage: "Please selected module Path"});
        return;
    }

    if (req.query.modulePathListLength < 0) {
        res.send({status: "fail", errorMessage: "Please selected module Path"});
        return;
    }

    if (typeof req.query.modulePathList !== "string") {
        res.send({status: "fail", errorMessage: "'modulePathList' must be String"});
        return;
    }

    var modulePathList = req.query.modulePathList.split(',');

    deleteCodeMetricsFromDB(modulePathList)
        .then(deleteFunctionMetricFromDB)
        .then(deleteSourceCodeMapFromDB)
        .then(deleteSnapshotDefectMapFromDB)
        .then(deleteSnapshotOccurenceMapFromDB)
        .then(deleteDefectFromDB)
        .then(() => {
            res.send({status: 'ok'});
        })
        .catch(error => {
            logging.info(`[ERROR][CATCH]: ${error}`);

        });
};


  