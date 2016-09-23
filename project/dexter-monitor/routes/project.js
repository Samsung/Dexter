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

const _ = require("lodash");
const moment = require('moment');
const Promise = require('bluebird');
const rp = require('request-promise');
const log = require('../util/logging');
const database = require("../util/database");
const route = require('./route');
const user = require('./user');
const server = require('./server');

exports.getProjectList = function(req, res) {
    const sql = "SELECT projectName, projectType, groupName, language   "+
                "FROM ProjectInfo                                       "+
                "ORDER BY projectName ASC                               ";
    route.executeSqlAndSendResponseRows(sql, res);
};

exports.getSnapshotSummary = function(req, res) {
    const sql =
        `SELECT year, week, installationRatio, resolvedDefectRatio
        FROM WeeklyStatusSummary
        ORDER BY year ASC, week ASC`;
    route.executeSqlAndSendResponseRows(sql, res);
};

exports.saveSnapshotSummary = function() {
    let promises = [];
    const summary = {};

    promises.push(new Promise((resolve) => {
        loadUserStatusSummary(resolve, summary);
    }));

    promises.push(new Promise((resolve) => {
        loadDefectStatusSummary(resolve, summary);
    }));

    Promise.all(promises)
        .then(() => {
            insertSnapshotSummaryToDatabase(summary);
        })
        .catch((err) => {
            log.error(err);
        })
};

function insertSnapshotSummaryToDatabase(summary) {
    const year = moment().get('year');
    const weekOfYear = moment().isoWeek();
    const dayOfWeek = moment().isoWeekday();
    const sql =
        `INSERT INTO WeeklyStatusSummary(year, week, day,
                                    installationRatio, installedDeveloperCount,
                                    resolvedDefectRatio, defectCountTotal)
                     VALUES (${year}, ${weekOfYear}, ${dayOfWeek},
                            ${summary.installationRatio}, '${summary.installedDeveloperCount}',
                            ${summary.resolvedDefectRatio}, ${summary.defectCountTotal})`;

    database.exec(sql)
        .then(() => {
            log.info(`Inserted snapshot summary`);

        })
        .catch((err) => {
            log.error(`Failed to insert snapshot summary : ${err}`);
        });
}

function loadUserStatusSummary(resolve, summary) {
    user.getUserStatusInternal()
        .then((userStatusTable) => {
            const targetDeveloperCountTotal = _.sum(_.map(userStatusTable, 'targetDeveloperCount'));
            const installedDeveloperCountTotal = _.sum(_.map(userStatusTable, 'installedDeveloperCount'));
            if (targetDeveloperCountTotal > 0) {
                summary.installationRatio = ((installedDeveloperCountTotal / targetDeveloperCountTotal) * 100).toFixed(1);
                summary.installedDeveloperCount = `${installedDeveloperCountTotal.toLocaleString()} / ${targetDeveloperCountTotal.toLocaleString()}`;
            } else {
                summary.installationRatio = '';
                summary.installedDeveloperCount = `0 / 0`;
            }
            resolve();
        })
        .catch((err) => {
            log.error(`Failed to get user status summary. null values are inserted. : ${err}`);
            summary.installationRatio = null;
            summary.installedDeveloperCount = null;
            resolve();
        });
}

function loadDefectStatusSummary(resolve, summary) {
    const activeServerList = _.filter(server.getServerListInternal(), {'active': true});

    Promise.map(activeServerList, (server) => {
        const defectCountUrl = `http://${server.hostIP}:${server.portNumber}/api/v2/defect-count`;
        return rp({uri: defectCountUrl, timeout: global.config.serverRequestTimeout * 1000})
            .then((data) => {
                server.defectValues = JSON.parse('' + data).values;
            })
            .catch((err) => {
                log.error(`Failed to get defect count for pid ${server.pid} : ${err}`);
            });
    }).then(() => {
        const defectValues = _.map(activeServerList, 'defectValues');
        const defectCountFixed = _.sum(_.map(defectValues, 'defectCountFixed'));
        const defectCountDismissed =_.sum(_.map(defectValues, 'defectCountDismissed'));
        summary.defectCountTotal = _.sum(_.map(defectValues, 'defectCountTotal'));

        if (summary.defectCountTotal > 0) {
            summary.resolvedDefectRatio = ((defectCountFixed + defectCountDismissed) / summary.defectCountTotal * 100).toFixed(1);
        } else {
            summary.resolvedDefectRatio = '';
        }
        resolve();
    }).catch(() => {
        log.error(`Failed to get defect status summary. null values are inserted. : ${err}`);
        summary.resolvedDefectRatio = null;
        summary.defectCountTotal = null;
        resolve();
    });
}