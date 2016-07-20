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

const mysql = require("mysql");
const moment = require('moment');
const Promise = require('bluebird');
const rp = require('request-promise');
const _ = require("lodash");
const project = require('./project');
const server = require('./server');
const database = require("../util/database");
const route = require('./route');
const log = require('../util/logging');

exports.getAll = function(req, res) {
    const sql =
        "SELECT year, week, groupName, projectName, language, allDefectCount,   "+
        "       allNew, allFix, allDis, criNew, criFix, criDis,                 "+
        "       majNew, majFix, majDis, minNew, minFix, minDis,                 "+
        "       crcNew, crcFix, crcDis, etcNew, etcFix, etcDis                  "+
        "FROM WeeklyStatus                                                      "+
        "LEFT JOIN ProjectInfo                                                  "+
        "ON WeeklyStatus.pid = ProjectInfo.pid                                  "+
        "ORDER BY year DESC, week DESC,                                         "+
        "         groupName ASC, projectName ASC                                ";

    route.executeSqlAndSendResponseRows(sql, res);
};

exports.getByGroup = function(req, res) {
    const year = mysql.escape(req.params.year);
    const week = mysql.escape(req.params.week);

    let sql =
        "SELECT year, week, groupName,                                  " +
        "       SUM(userCount) AS userCount,                            " +
        "       COUNT(projectName) AS projectCount,                     " +
        "       SUM(allDefectCount) AS allDefectCount,                  " +
        "       SUM(allFix) AS allFix,                                  " +
        "       SUM(allDis) AS allDis                                   " +
        "FROM WeeklyStatus                                              " +
        "LEFT JOIN ProjectInfo                                          " +
        "ON WeeklyStatus.pid = ProjectInfo.pid                          ";

    if (year == 0 && week == 0) {
        sql += "WHERE year = YEAR(CURDATE()) AND week = WEEK(CURDATE()) ";
    } else {
        sql += "WHERE year = " + year + " AND week = " + week + "       ";
    }

    sql += "GROUP BY groupName ORDER BY allDefectCount DESC, groupName ASC";

    route.executeSqlAndSendResponseRows(sql, res);
};

exports.getMinYear = function(req, res) {
    const sql = "SELECT year FROM WeeklyStatus ORDER BY year ASC LIMIT 1";
    database.exec(sql)
        .then((rows) => {
            res.send({status:'ok', value: rows[0].year});
        })
        .catch((err) => {
            log.error(err);
            res.send({status:"fail", errorMessage: err.message});
        });
};

exports.getMaxYear = function(req, res) {
    const sql = "SELECT year FROM WeeklyStatus ORDER BY year DESC LIMIT 1";
    database.exec(sql)
        .then((rows) => {
            res.send({status:'ok', value: rows[0].year});
        })
        .catch((err) => {
            log.error(err);
            res.send({status:"fail", errorMessage: err.message});
        });
};

exports.getMaxWeek = function(req, res) {
    const year = mysql.escape(req.params.year);
    const sql = "SELECT week FROM WeeklyStatus WHERE year = " + year + " ORDER BY week DESC LIMIT 1";
    database.exec(sql)
        .then((rows) => {
            res.send({status:'ok', value: rows[0].week});
        })
        .catch((err) => {
            log.error(err);
            res.send({status:"fail", errorMessage: err.message});
        });
};

exports.getDefectCountByProjectName = function(req, res) {
    const projectServer = _.find(server.getServerListInternal(), {'projectName': req.params.projectName});
    const defectCountUrl = `http://${projectServer.hostIP}:${projectServer.portNumber}/api/v2/defect-count`;
    rp(defectCountUrl)
        .then((data) => {
            const parsedData = JSON.parse('' + data);
            res.send({status:'ok', values: parsedData.values});
        })
        .catch((err) => {
            log.error(`Failed to get defect count for pid ${projectServer.pid} : ${err}`);
            res.send({status:"fail", errorMessage: err.message});
        });
};

exports.saveSnapshot = function() {
    const activeServerList = _.filter(server.getServerListInternal(), {'active': true});

    Promise.map(activeServerList, (server) => {
        const defectCountUrl = `http://${server.hostIP}:${server.portNumber}/api/v2/detailed-defect-count`;
        const userCountUrl = `http://${server.hostIP}:${server.portNumber}/api/v2/user-count`;
        let promises = [];
        let defectValues = {};
        let userCount = 0;

        promises.push(new Promise((resolve, reject) => {
            rp(defectCountUrl)
                .then((data) => {
                    defectValues = JSON.parse('' + data).values;
                    resolve();
                })
                .catch((err) => {
                    log.error(`Failed to get detailed defect count for pid ${server.pid} : ${err}`);
                    reject();
                });
        }));
        promises.push(new Promise((resolve, reject) => {
            rp(userCountUrl)
                .then((data) => {
                    userCount = JSON.parse('' + data).value;
                    resolve();
                })
                .catch((err) => {
                    log.error(`Failed to get user count for pid ${server.pid} : ${err}`);
                    reject();
                });
        }));

        Promise.all(promises)
            .then(() => {
                insertSnapshotToDatabase(server.pid, defectValues, userCount);
            })
            .catch((err) => {
                log.error(err);
            });
    }).catch((err) => {
        log.error(err);
    });
};

function insertSnapshotToDatabase(pid, defectValues, userCount) {
    const year = moment().get('year');
    const weekOfYear = moment().isoWeek();
    const dayOfWeek = moment().isoWeekday();
    const {allDefectCount, allNew, allFix, allDis, criNew, criFix, criDis,
        majNew, majFix, majDis, minNew, minFix, minDis,
        crcNew, crcFix, crcDis, etcNew, etcFix, etcDis,
        invalidStatusCode, invalidSeverityCode} = defectValues;

    const sql =
        `INSERT INTO WeeklyStatus(pid, year, week, day, userCount, allDefectCount,
                                    allNew, allFix, allDis, criNew, criFix, criDis,
                                    majNew, majFix, majDis, minNew, minFix, minDis,
                                    crcNew, crcFix, crcDis, etcNew, etcFix, etcDis)
                     VALUES (${pid}, ${year}, ${weekOfYear}, ${dayOfWeek}, ${userCount}, ${allDefectCount},
                            ${allNew}, ${allFix}, ${allDis}, ${criNew}, ${criFix}, ${criDis},
                            ${majNew}, ${majFix}, ${majDis}, ${minNew}, ${minFix}, ${minDis},
                            ${crcNew}, ${crcFix}, ${crcDis}, ${etcNew}, ${etcFix}, ${etcDis})`;

    database.exec(sql)
        .then(() => {
            log.info(`Inserted snapshot for pid ${pid} : # of invalidStatusCode: ${invalidStatusCode}, # of invalidSeverityCode: ${invalidSeverityCode}`);

        })
        .catch((err) => {
            log.error(`Failed to insert snapshot for pid ${pid} : ${err}`);
        });
}