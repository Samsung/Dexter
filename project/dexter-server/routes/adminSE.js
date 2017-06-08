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

const CODE_METRICS_DB_NAME = `CodeMetrics`;
const FUNCTION_METRICS_DB_NAME = `FunctionMetrics`;
const DEFECT_DB_NAME = `Defect`;
const OCCURENCE_DB_NAME = `Occurence`;
const SNAPSHOT_DEFECT_MAP_DB_NAME = `SnapshotDefectMap`;
const SNAPSHOT_OCCURENCE_MAP_DB_NAME = `SnapshotOccurenceMap`;
const SOURCE_CODE_MAP_DB_NAME = `SourceCodeMap`;

exports.getDidList = function (req, res) {
    var sql = `SELECT did From ${DEFECT_DB_NAME}`;

    database.execV2(sql)
        .then((rows) => {
            rows = _.orderBy(rows, ['did'], ['asc']);
            res.send({status: 'ok', rows: rows});
        })
        .catch(error => {
            logging.error(error);
            res.send({status: "fail", errorMessage: error.message});
        });
};

exports.getModulePathList = function (req, res) {
    var sql = `SELECT modulePath FROM ${DEFECT_DB_NAME} group by modulePath order by modulePath`;

    database.execV2(sql)
        .then((rows) => {
            res.send({status: 'ok', rows: rows});
        })
        .catch(error => {
            logging.error(error);
            res.send({status: "fail", errorMessage: error.message});
        });
};

function deleteSnapshotOccurence(did) {
    const sql = `DELETE FROM ${SNAPSHOT_OCCURENCE_MAP_DB_NAME} WHERE did=${mysql.escape(did)}`;
    return database.execV2(sql)
        .then(() => {
            return Promise.resolve();
        })
        .catch((error) => {
            return Promise.reject(`[deleteSnapshotOccurence]: ${error}`);
        });
}

function deleteOccurence() {
    const sql = `DELETE FROM ${OCCURENCE_DB_NAME} WHERE did=${mysql.escape(did)}`;
    return database.execV2(sql)
        .then(() => {
            return Promise.resolve()
        })
        .catch((error) => {
            return Promise.reject(`[deleteOccurence]: ${error}`);
        });
}

function makeDidList(result) {
    var didList = [];

    _.forEach(result, rows => {
        didList.push(rows.did);
    });

    return Promise.resolve(_.uniq(didList));
}

function deleteSnapshotOccurenceMapFromDB(modulePathList) {
    const baseSql = `SELECT did FROM ${SNAPSHOT_DEFECT_MAP_DB_NAME} `;
    const sql = makeDeleteSql(baseSql, modulePathList);

    return database.execV2(sql)
        .then((result)=> {
            if (result.length === 0) {
                logging.info("There is no defect information in 'SnapshotDefectMap' table.");
                return Promise.resolve(modulePathList);
            }

            return makeDidList(result)
                .then(deleteSnapshotOccurenceMapForDid)
                .then(() => {
                    return Promise.resolve(modulePathList);
                });
        })
        .catch(error => {
            return Promise.reject(`[deleteSnapshotOccurenceMapFromDB]: ${error}`);
        });
}

function deleteOccurenceMapFromDB(modulePathList) {
    const baseSql = `SELECT did FROM ${DEFECT_DB_NAME} `;
    const sql = makeDeleteSql(baseSql, modulePathList);
    return database.execV2(sql)
        .then((result)=> {
            if (result.length === 0) {
                logging.info("There is no defect information in 'Defect' table.");
                return Promise.resolve(modulePathList);
            }

            return makeDidList(result)
                .then(deleteOccurenceMapForDid)
                .then(() => {
                    return Promise.resolve(modulePathList);
                });
        })
        .catch(error => {
            return Promise.reject(`[deleteOccurenceMapFromDB]: ${error}`);
        });
}

function deleteDefectFromDB(modulePathList) {
    return deleteFromDBForModulePathList(DEFECT_DB_NAME, modulePathList)
        .then(()=> {
            return Promise.resolve(modulePathList);
        })
        .catch(error => {
            return Promise.reject(`[deleteDefectFromDB]: ${error}`);
        });
}

function deleteSnapshotDefectMapFromDB(modulePathList) {
    return deleteFromDBForModulePathList(SNAPSHOT_DEFECT_MAP_DB_NAME, modulePathList)
        .then(()=> {
            return Promise.resolve(modulePathList);
        })
        .catch(error => {
            return Promise.reject(`[deleteSnapshotDefectMapFromDB]: ${error}`);
        });
}

function deleteSourceCodeMapFromDB(modulePathList) {
    return deleteFromDBForModulePathList(SOURCE_CODE_MAP_DB_NAME, modulePathList)
        .then(()=> {
            return Promise.resolve(modulePathList);
        })
        .catch(error => {
            return Promise.reject(`[deleteSourceCodeMapFromDB]: ${error}`);
        });
}

function deleteFunctionMetricFromDB(modulePathList) {
    return deleteFromDBForModulePathList(FUNCTION_METRICS_DB_NAME, modulePathList)
        .then(()=> {
            return Promise.resolve(modulePathList);
        })
        .catch(error => {
            return Promise.reject(`[deleteFunctionMetricFromDB]: ${error}`);
        });
}

function deleteCodeMetricsFromDB(modulePathList) {
    return deleteFromDBForModulePathList(CODE_METRICS_DB_NAME, modulePathList)
        .then(()=> {
            return Promise.resolve(modulePathList);
        })
        .catch(error => {
            return Promise.reject(`[deleteCodeMetricsFromDB]: ${error}`);
        });
}

function deleteFromDBForModulePathList(databaseName, modulePathList) {
    const baseSql = `DELETE FROM ${databaseName} `;
    const sql = makeDeleteSql(baseSql, modulePathList);
    return database.execV2(sql)
        .then(() => {
            return Promise.resolve(modulePathList);
        })
        .catch(error => {
            return Promise.reject(`[deleteFromDBForModulePathList]: ${error}`);
        });
}

function makeDeleteSql(baseSql, modulePathList) {
    var sql = baseSql;

    _.forEach(modulePathList, (modulePath, idx) => {
        if (idx == 0) {
            sql += " WHERE modulePath " + database.compareEqualWithEscape(modulePath);
        } else {
            sql += " OR modulePath " + database.compareEqualWithEscape(modulePath);
        }
    });

    return sql;
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
        .then(deleteSnapshotOccurenceMapFromDB)
        .then(deleteSnapshotDefectMapFromDB)
        .then(deleteOccurenceMapFromDB)
        .then(deleteDefectFromDB)
        .then(() => {
            res.send({status: 'ok'});
        })
        .catch(error => {
            logging.info(`[ERROR][CATCH]: ${error}`);
            res.send({status: "fail", errorMessage: "The Delete request failed, please contact to SE admin."});
        });
};

exports.deleteDidList = function (req, res) {
    if (!(_.has(req, "query.didList") && req.query.didList)) {
        res.send({status: "fail", errorMessage: "Please selected did list"});
        return;
    }

    if (req.query.didListLength < 0) {
        res.send({status: "fail", errorMessage: "Please selected did"});
        return;
    }


    if (typeof req.query.didList !== "string") {
        res.send({status: "fail", errorMessage: "'didList' must be String"});
        return;
    }

    var didList = req.query.didList.split(',');

    deleteSnapshotOccurenceMapForDid(didList)
        .then(deleteSnapshotDefectMapFromDBForDid)
        .then(deleteOccurenceMapForDid)
        .then(deleteDefectFromDBForDid)
        .then(()=> {
            res.send({status: 'ok'});
        })
        .catch(error => {
            logging.info(`[ERROR][CATCH]: ${error}`);
            res.send({status: "fail", errorMessage: "The Delete request failed, please contact to SE admin."});
        });
};

function deleteSnapshotOccurenceMapForDid(didList) {
    return deleteFromDBForDidList(SNAPSHOT_OCCURENCE_MAP_DB_NAME, didList)
        .then(()=> {
            return Promise.resolve(didList);
        })
        .catch(error => {
            return Promise.reject(error);
        });
}

function deleteSnapshotDefectMapFromDBForDid(didList) {
    return deleteFromDBForDidList(SNAPSHOT_DEFECT_MAP_DB_NAME, didList)
        .then(()=> {
            return Promise.resolve(didList);
        })
        .catch(error => {
            return Promise.reject(error);
        });
}

function deleteOccurenceMapForDid(didList) {
    return deleteFromDBForDidList(OCCURENCE_DB_NAME, didList)
        .then(()=> {
            return Promise.resolve(didList);
        })
        .catch(error => {
            return Promise.reject(error);
        });
}

function deleteDefectFromDBForDid(didList) {
    return deleteFromDBForDidList(DEFECT_DB_NAME, didList)
        .then(()=> {
            return Promise.resolve(didList);
        })
        .catch(error => {
            return Promise.reject(error);
        });
}

function deleteFromDBForDidList(databaseName, didList) {
    var sql = `DELETE FROM ${databaseName} `;

    _.forEach(didList, (did, idx) => {
        if (idx === 0) {
            sql += " WHERE did " + database.compareEqualWithEscape(did);
        } else {
            sql += " OR did " + database.compareEqualWithEscape(did);
        }
    });

    return database.execV2(sql)
        .then(() => {
            return Promise.resolve(didList);
        })
        .catch(error => {
            return Promise.reject(error);
        });
}
