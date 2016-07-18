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

const database = require("../util/database");
const log = require('../util/logging');
const route = require('./route');
const project = require('./project');
const server = require('./server');
const http = require('http');
const fs = require('fs');
const Promise = require('bluebird');
const rp = require('request-promise');
const mysql = require("mysql");
const _ = require("lodash");

function getUserListByProjectDatabaseName(dbName) {
    const sql = "SELECT userId FROM " + mysql.escapeId(dbName) + ".Account ORDER BY userId ASC";
    return database.exec(sql)
        .then((rows) => {
            return rows;
        })
        .catch((err) => {
            log.error(err);
            return null;
        });
}

function sendUserListInDatabaseNameList(dbNameList, res) {
    let allRows = [];
    let promises = [];

    dbNameList.forEach((dbName) => {
        promises.push(new Promise((resolve, reject) => {
            getUserListByProjectDatabaseName(dbName)
                .then((rows) => {
                    allRows = _.union(allRows, rows);
                    resolve();
                })
                .catch((err) => {
                    log.error(err);
                    reject();
                })
        }));
    });

    Promise.all(promises)
        .then(() => {
            allRows = _.filter(allRows, (row) => row.userId !== 'admin' && row.userId !== 'user');
            allRows = _.sortBy(allRows, 'userId');
            res.send({status:'ok', rows: allRows});
        })
        .catch((err) => {
            log.error(err);
            res.send({status:"fail", errorMessage: err.message});
        });
}

function sendUserListInActiveServerList(activeServerList, res) {
    let allRows = [];
    let promises = [];

    activeServerList.forEach((server) => {
        promises.push(new Promise((resolve, reject) => {
            getUserListByProjectDatabaseName(server.dbName)
                .then((rows) => {
                    if (rows) {
                        rows.forEach((row) => {
                            allRows.push({
                                projectName: server.projectName,
                                userId: row.userId
                            });
                        });
                    }
                    resolve();
                })
                .catch((err) => {
                    log.error(err);
                    reject();
                })
        }));
    });

    Promise.all(promises)
        .then(() => {
            allRows = _.filter(allRows, (row) => row.userId !== 'admin' && row.userId !== 'user');
            allRows = _.sortBy(allRows, 'projectName');
            res.send({status:'ok', rows: allRows});
        })
        .catch((err) => {
            log.error(err);
            res.send({status:"fail", errorMessage: err.message});
        });
}

exports.getAll = function(req, res) {
    const activeServerList = _.filter(server.getServerListInternal(), {'active': true});

    Promise.map(activeServerList, (server) => {
        return project.getDatabaseNameByProjectName(mysql.escape(server.projectName))
            .then((dbName) => {
                // Will not be used after creating server API
                server.dbName = dbName;
            });
    }).then(() => {
        sendUserListInActiveServerList(activeServerList, res);
    }).catch((err) => {
        log.error('Failed to get user list: ' + err);
        res.send({status:"fail", errorMessage: err.message});
    });
};

exports.getByProject = function(req, res) {
    const projectName = mysql.escape(req.params.projectName);
    project.getDatabaseNameByProjectName(projectName)
        .then((dbName) => {
            getUserListByProjectDatabaseName(dbName)
                .then((rows) => {
                    res.send({status:'ok', rows: rows});
                })
                .catch((err) => {
                    log.error(err);
                    res.send({status:"fail", errorMessage: err.message});
                });
        })
        .catch((err) => {
            log.error('Failed to get the DB name of the ' + projectName + ' project: ' + err);
            res.send({status:"fail", errorMessage: err.message});
        });
};

exports.getByGroup = function(req, res) {
    const groupName = mysql.escape(req.params.groupName);

    project.getDatabaseNameListByGroupName(groupName)
        .then((rows) => {
            let dbNameList = _.map(rows, 'dbName');
            sendUserListInDatabaseNameList(dbNameList, res);
        })
        .catch((err) => {
            log.error('Failed to get the DB name list of the ' + groupName + ' group: ' + err);
            res.send({status:"fail", errorMessage: err.message});
        });
};

exports.getByLab = function(req, res) {

};

function processReturnedData(data) {
    return data.replace(/((\])|(\[))/g,'').replace(/(^\s*)|(\s*$)/g,'');
}

function loadUserInfo(userId, userInfoUrl, userInfoList) {
    return rp(userInfoUrl + userId)
        .then((data) => {
            data = processReturnedData(data);
            if (!data || !validateUserInfoJson(data, userId)) {
                log.error('Not found user data: ' + userId);
                userInfoList.push({'userId':userId});
            } else {
                const infoJson = JSON.parse('' + data);
                userInfoList.push({
                    'userId':userId,
                    'name':infoJson.cn,
                    'department':infoJson.department,
                    'title':infoJson.title,
                    'employeeNumber':infoJson.employeenumber});
            }
        })
        .catch((err) => {
            log.error(err);
            userInfoList.push({'userId':userId});
        });
}

exports.getMoreInfoByUserIdList = function(req, res) {
    const configText = fs.readFileSync("./config.json", 'utf8');
    const configJson = JSON.parse(configText);
    const userInfoUrl = configJson.userInfoUrl;
    const userIdList = req.params.userIdList.split(",");

    let userInfoList = [];
    let promises = [];
    userIdList.forEach((userId) => {
        promises.push(loadUserInfo(userId, userInfoUrl, userInfoList));
    });

    Promise.all(promises)
        .then(() => {
            res.send({status:'ok', rows: userInfoList});
        })
        .catch((err) => {
            log.error(err);
            res.send({status:"fail", errorMessage: err.message});
        });
};

function validateUserInfoJson(data, userid) {
    if(data.indexOf("\"userid\":\"" + userid + "\"") < 0) {
        log.error('Incorrect result from user info server');
        return false;
    }
    return true;
}

exports.getUserCountByProjectName = function(req, res) {
    const projectName = mysql.escape(req.params.projectName);
    project.getDatabaseNameByProjectName(projectName)
        .then((dbName) => {
            const sql =
                "SELECT COUNT(userId) AS userCount              " +
                "FROM " + mysql.escapeId(dbName) + ".Account    " +
                "WHERE userId!='admin' AND userId!='user'       ";
            database.exec(sql)
                .then((rows) => {
                    res.send({status:'ok', value: rows[0].userCount});
                })
                .catch((err) => {
                    log.error(err);
                    res.send({status:"fail", errorMessage: err.message});
                });
        })
        .catch((err) => {
            log.error(err);
            res.send({status:"fail", errorMessage: err.message});
        });
};

function loadUserStatusList() {
    const sql = "SELECT userId, dexterYn FROM DexterUserList";
    return database.exec(sql)
        .then((rows) => {
            return rows;
        })
        .catch((err) => {
            log.error(err);
            return [];
        });
}

function loadUserList() {
    return project.getDatabaseNameList()
        .then((rows) => {
            const dbNameList = _.map(rows, 'dbName');
            let allRows = [];
            let promises = [];

            dbNameList.forEach((dbName) => {
                promises.push(new Promise((resolve, reject) => {
                    getUserListByProjectDatabaseName(dbName)
                        .then((rows) => {
                            allRows = _.union(allRows, rows);
                            resolve();
                        })
                        .catch((err) => {
                            log.error(err);
                            reject();
                        })
                }));
            });

            return Promise.all(promises)
                .then(() => {
                    allRows = _.uniq(allRows, (row) => {
                        return row.userId;
                    });
                    return allRows;
                })
                .catch((err) => {
                    log.error(err);
                    return [];
                });
        })
        .catch((err) => {
            log.error('Failed to get the DB name list: ' + err);
            return [];
        });
}

function updateUserStatusList() {
    let promises = [];
    let userStatusList = [];
    let userList = [];

    promises.push(new Promise((resolve, reject) => {
        loadUserStatusList()
            .then((rows) => {
                userStatusList = rows;
                resolve();
            })
            .catch((err) => {
                log.error(err);
                reject();
            })
    }));
    promises.push(new Promise((resolve, reject) => {
        loadUserList()
            .then((rows) => {
                userList = rows;
                resolve();
            })
            .catch((err) => {
                log.error(err);
                reject();
            })
    }));

    return Promise.all(promises)
        .then(() => {
            return Promise.map(userStatusList, (row) => {
                if (_.findIndex(userList, (user) => user.userId == row.userId) >= 0) {
                    row.dexterYn = 'Y';
                } else if (row.dexterYn == 'Y') {
                    row.dexterYn = 'N';
                } else {
                    return;
                }

                const sql =
                    `UPDATE DexterUserList
                     SET dexterYn='${row.dexterYn}', reason='', ide='', language=''
                     WHERE userId='${row.userId}'`;
                return database.exec(sql)
                    .catch((err) => {
                        log.error('Failed to set dexterYn : ' + err);
                    });
            });
        });
}

function loadAndCreateUserStatusTable() {
    const sql = `SELECT DexterUserList.userLab AS groupName,
                        COUNT(DexterUserList.userId) AS allDeveloperCount,
                        installedDeveloperCount,
                        nonTargetDeveloperCount
                 FROM DexterUserList
                 LEFT JOIN (SELECT userLab, COUNT(DUL1.userId) AS installedDeveloperCount
                            FROM DexterUserList AS DUL1
                            WHERE DUL1.dexterYn='y'
                            GROUP BY DUL1.userLab) AS d1
                 ON DexterUserList.userLab = d1.userLab
                 LEFT JOIN (SELECT userLab, COUNT(DUL2.userId) AS nonTargetDeveloperCount
                            FROM DexterUserList AS DUL2
                            WHERE DUL2.dexterYn='u'
                            GROUP BY DUL2.userLab) AS d2
                 ON DexterUserList.userLab = d2.userLab
                 GROUP BY DexterUserList.userLab`;
    return database.exec(sql)
        .then((rows) => {
            rows.forEach((row) => {
                row.targetDeveloperCount = row.allDeveloperCount - row.nonTargetDeveloperCount;
                row.installationRate = (row.installedDeveloperCount / row.targetDeveloperCount * 100).toFixed(1);
            });
            return rows;
        });
}

exports.getUserStatus = function(req, res) {
    updateUserStatusList()
        .then(loadAndCreateUserStatusTable)
        .then((userStatusTable) => {
            res.send({status:'ok', rows: userStatusTable});
        })
        .catch((err) => {
            res.send({status:"fail", errorMessage: err.message});
        });
};