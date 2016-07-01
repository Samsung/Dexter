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

const log = require('../util/logging');
const route = require('./route');
const project = require('./project');
const http = require('http');
const fs = require('fs');
const Promise = require('bluebird');
const rp = require('request-promise');
const mysql = require("mysql");

exports.getAll = function(req, res) {

};

exports.getByProject = function(req, res) {
    let projectName = mysql.escape(req.params.projectName);
    return project.getDatabaseNameByProjectName(projectName)
        .then((dbName) => {
            const sql = "SELECT userId FROM " + mysql.escapeId(dbName) + ".Account ORDER BY userId ASC";
            return route.executeSqlAndSendResponseRows(sql, res);
        })
        .catch((err) => {
            log.error('Failed to get the DB name of the ' + projectName + ' project: ' + err);
            return null;
        });
};

exports.getByGroup = function(req, res) {

};

exports.getByLab = function(req, res) {

};

function processReturnedData(data) {
    return data.replace(/((\])|(\[))/g,'').replace(/(^\s*)|(\s*$)/g,'');
}

function loadUserInfo(userId, userInfoUrl, userInfoList) {
    return rp(userInfoUrl + userId)
        .then(function(data) {
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
        .catch(function(err) {
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
    userIdList.forEach(function(userId) {
        promises.push(loadUserInfo(userId, userInfoUrl, userInfoList));
    });

    return Promise.all(promises)
        .then(function() {
            res.send({status:'ok', rows: userInfoList});
        })
        .catch(function(err) {
            console.error(err);
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