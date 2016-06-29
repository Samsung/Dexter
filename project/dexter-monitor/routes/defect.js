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

const route = require('./route');
const log = require('../util/logging');

exports.getAll = function(req, res) {
    const sql =
        "SELECT *                                   "+
        "FROM weeklyData                            "+
        "LEFT JOIN projectInfo                      "+
        "ON weeklyData.pid = projectInfo.pid        "+
        "ORDER BY year DESC, week DESC,             "+
        "         groupName ASC, projectName ASC    ";

    return route.executeSqlAndSendResponse(sql, res);
};

exports.getByProject = function(req, res) {
    const projectName = req.params.projectName;
    const sql =
        "SELECT year, week, accountCount,                   "+
        "       allDefectCount, allFix, allExc              "+
        "FROM weeklyData                                    "+
        "LEFT JOIN projectInfo                              "+
        "ON weeklyData.pid = projectInfo.pid                "+
        "WHERE projectInfo.projectName = '" + projectName + "'"+
        "ORDER BY year DESC, week DESC                      ";

    return route.executeSqlAndSendResponse(sql, res);
};

exports.getByGroup = function(req, res) {
    const year = req.params.year;
    const week = req.params.week;

    let sql =
        "SELECT year, week, groupName,                                  " +
        "       SUM(accountCount) AS accountCount,                      " +
        "       COUNT(projectName) AS projectCount,                     " +
        "       SUM(allDefectCount) AS allDefectCount,                  " +
        "       SUM(allFix) AS allFix,                                  " +
        "       SUM(allExc) AS allExc                                   " +
        "FROM weeklyData                                                " +
        "LEFT JOIN projectInfo                                          " +
        "ON weeklyData.pid = projectInfo.pid                            ";

    if (year == 0 && week == 0) {
        sql += "WHERE year = YEAR(CURDATE()) AND week = WEEK(CURDATE()) ";
    } else {
        sql += "WHERE year = " + year + " AND week = " + week + "       ";
    }

    sql += "GROUP BY groupName ORDER BY allDefectCount DESC, groupName ASC";

    return route.executeSqlAndSendResponse(sql, res);
};

exports.getByLab = function(req, res) {
    const year = req.params.year;
    const week = req.params.week;
};

exports.getMinYear = function(req, res) {
    const sql = "SELECT year FROM weeklyData ORDER BY year ASC LIMIT 1";
    return route.executeSqlAndSendResponse(sql, res);
};

exports.getMaxYear = function(req, res) {
    const sql = "SELECT year FROM weeklyData ORDER BY year DESC LIMIT 1";
    return route.executeSqlAndSendResponse(sql, res);
};

exports.getMinWeek = function(req, res) {
    const year = req.params.year;
    const sql = "SELECT week FROM weeklyData WHERE year = " + year + " ORDER BY week ASC LIMIT 1";
    return route.executeSqlAndSendResponse(sql, res);
};

exports.getMaxWeek = function(req, res) {
    const year = req.params.year;
    const sql = "SELECT week FROM weeklyData WHERE year = " + year + " ORDER BY week DESC LIMIT 1";
    return route.executeSqlAndSendResponse(sql, res);
};

