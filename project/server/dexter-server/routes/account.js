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

var database = require("../util/database");
var logging = require('../util/logging');
var dutil = require('../util/dexter-util');

// TODO: Make a Object
var accountIndex = 0;
var accountList = [];

function Account (userId, userPwd) {
    this.userNo = 0;
    this.userId = userId;
    this.userPwd = userPwd;

    this.setUserNo = function(userNo) {
        this.userNo = Math.floor(userNo);
    };

    this.setAdmin = function (adminYn) {
        this.isAdmin = adminYn;
    };
}

function init() {
    "use strict";

    var sql = "select userNo, userId, userPwd, adminYn, createdDateTime, modifiedDateTime from Account";

    database.exec(sql, function (err, result) {
        if(err){
            logging.error(err.message);
            return;
        }

        if(result == undefined || result.length < 1){
            logging.error("there is no result for account list");
            return;
        }

        accountList.length = 0;
        accountIndex = 0;

        for(var i=0; i < result.length; i++) {
            var item = result[i];

            var account = new Account(item.userId, item.userPwd);
            account.setUserNo(item.userNo);
            if(item.adminYn && item.adminYn == 'Y'){
                account.setAdmin(true);
            } else {
                account.setAdmin(false);
            }
            accountList[accountIndex++] = account;
        }
    });
}

function init(callback) {
    "use strict";

    var sql = "select userNo, userId, userPwd, adminYn, createdDateTime, modifiedDateTime from Account";

    database.exec(sql, function (err, result) {
        if(err){
            logging.error(err.message);
            return;
        }

        if(result == undefined || result.length < 1){
            logging.error("there is no result for account list");
            return;
        }

        accountList.length = 0;
        accountIndex = 0;

        for(var i=0; i < result.length; i++) {
            var item = result[i];

            var account = new Account(item.userId, item.userPwd);
            account.setUserNo(item.userNo);
            if(item.adminYn && item.adminYn == 'Y'){
                account.setAdmin(true);
            } else {
                account.setAdmin(false);
            }
            accountList[accountIndex++] = account;
        }

        if(callback != undefined){
            callback();
        }
    });
}

exports.init = init;

exports.getUserNo = function(userId) {
    for(var i=0; i<accountList.length; i++){
        if (accountList[i].userId === userId) {
            return accountList[i].userNo;
        }
    }
    return undefined;
};


exports.userId = function(req, res) {
    var auths = dutil.getUserIdAndPwd(req);
    var userId = auths[0];
    var userPwd = auths[1];

    if(accountList){
        for(var i=0; i<accountList.length; i++){
            var account = accountList[i];
            if(account && account.userId == userId){
                if(account.userPwd == userPwd){
                    res.send({
                        result: "ok",
                        userNo: Math.floor(account.userNo),
                        userId: account.userId,
                        userPwd: account.userPwd,
                        isAdmin: account.isAdmin || false
                    });
                    return;
                }
            }
        }

        res.send({result: "fail"});
    } else {
        res.send({result: "fail"});
    }
};

exports.logout = function(req, res) {
    res.send(200, {status:"success"});
};


exports.getAccountCount = function (req, res){
    var sql = "select count(userId) as accountCount from Account";

    database.exec(sql, function (err, result){
        if(err){
            logging.error(err.message);
            res.send(401, {status:"fail", errorMessage: err.message});
        } else if(result && result[0].accountCount){
            res.send({"accountCount": result[0].accountCount});
        } else {
            res.send(401, {status:"fail", errorMessage: "unknown error"});
        }
    });
};


exports.findAll = function(req, res) {
    var sql = "select userNo, userId, userPwd, adminYn, createdDateTime, modifiedDateTime from Account";

    database.exec(sql, function (err, result) {
        if(err){
            logging.error(err.message);
            res.send({result:'fail', errorMessage: err.message, errorCode: -1});
            return;
        }

        if(result.length < 1){
            var msg = "there is no result for account list";
            res.send({result:'fail', errorMessage: msg, errorCode: -1});
            logging.error(msg);
            return;
        }

        res.send({
            result: 'ok',
            accounts: result
        });
    });
};

exports.findById = function(req, res) {
    var userId = req.params.userId;

    if(!userId){
        logging.error("userId value is null");
        return;
    }

    var sql = "select userNo, userId, userPwd, adminYn, createdDateTime, modifiedDateTime from Account "
        + "where userId = " + database.toSqlValue(userId);

    database.exec(sql, function (err, result) {
        if(err){
            res.send({result:'fail', errorMessage: err.message, errorCode: -1});
            logging.error(err.message);
            return;
        }

        if(result.length != 1){
            var msg = 'there is no result for ' + userId;
            res.send({result:'fail', errorMessage: msg , errorCode: -2});
            logging.error(msg);
            return;
        }

        for(var i=0; i<accountList.length; i++){
            var account = accountList[i];
            if(account.userId == userId){
                account.setUserNo(result[0].userNo);
                account.userPwd = result[0].userPwd;
                account.isAdmin = !!(result[0].adminYn && result[0].adminYn == 'Y');
            }
        }

        res.send({
            result: 'ok',
            userNo: "" + result[0].userNo,
            userId: result[0].userId,
            adminYn: result[0].adminYn,
            createdDateTime: result[0].createdDateTime,
            modifiedDateTime: result[0].modifiedDateTime
        });
    });
};

exports.hasAccount = function(req, res) {
    var userId = req.params.userId;

    if(!userId){
        logging.error("userId value is null");
        return;
    }

    var sql = "select userNo, userId, userPwd, adminYn, createdDateTime, modifiedDateTime from Account "
        + "where userId = " + database.toSqlValue(userId);

    database.exec(sql, function (err, result) {
        if(err){
            res.send({result:'fail', errorMessage: err.message, errorCode: -1});
            logging.error(err.message);
            return;
        }

        if(result.length != 1){
            var msg = 'there is no result for ' + userId;
            res.send({result:'fail', errorMessage: msg , errorCode: -2});
            logging.error(msg);
            return;
        }

        res.send({
            result: 'ok',
            userNo: Math.floor(result[0].userNo),
            userId: result[0].userId,
            createdDateTime: result[0].createdDateTime
        });
    });
};

exports.checkLogin = function(req, res) {
    var auths = dutil.getUserIdAndPwd(req);
    if(auths === undefined){
        res.send({result: "fail"});
        return ;
    }
    var userId = auths[0];
    var userPwd = auths[1];

    if(accountList){
        for(var i=0; i<accountList.length; i++){
            var account = accountList[i];
            if(account && account.userId == userId){
                if(account.userPwd == userPwd){
                    res.send({
                        result: "ok",
                        userNo: Math.floor(account.userNo),
                        userId: account.userId,
                        userPwd: account.userPwd,
                        isAdmin: account.isAdmin || false
                    });
                    return;
                }
            }
        }
        res.send({result: "fail"});
    } else {
        res.send({result: "fail"});
    }
};

exports.checkWebLogin = function(req, res) {
    var auths = dutil.getUserIdAndPwd(req);
    var userId = auths[0];
    var userPwd = auths[1];

    if(accountList){
        for(var i=0; i<accountList.length; i++){
            var account = accountList[i];
            if(account && account.userId == userId){
                if(account.userPwd == userPwd){
                    res.send({
                        result: "ok",
                        userNo: Math.floor(account.userNo),
                        userId: account.userId,
                        userPwd: account.userPwd,
                        isAdmin: account.isAdmin || false
                    });
                    return;
                }
            }
        }
        res.send({result: "fail"});
    } else {
        res.send({result: "fail"});
    }
};

exports.checkWebAdminAccount = function(req, res) {
    var auths = dutil.getUserIdAndPwd(req);
    var userId = auths[0];
    var userPwd = auths[1];

    if(accountList){
        for(var i=0; i<accountList.length; i++){
            var account = accountList[i];
            if(account && account.userId == userId){
                if(account.userPwd == userPwd){
                    res.send({
                        result: "ok",
                        userNo: Math.floor(account.userNo),
                        userId: account.userId,
                        userPwd: account.userPwd,
                        isAdmin: account.isAdmin || false
                    });
                    return;
                }
            }
        }
        res.send({result: "fail"});
    } else {
        res.send({result: "fail"});
    }
};

exports.checkAdminAccount = function(req, res) {
    var auths = dutil.getUserIdAndPwd(req);
    var userId = auths[0];
    var userPwd = auths[1];

    if(accountList){
        for(var i=0; i<accountList.length; i++){
            var account = accountList[i];
            if(account && account.userId == userId){
                if(account.userPwd == userPwd){
                    res.send({
                        result: "ok",
                        userNo: Math.floor(account.userNo),
                        userId: account.userId,
                        userPwd: account.userPwd,
                        isAdmin: account.isAdmin || false
                    });
                    return;
                }
            }
        }
        res.send({result: "fail"});
    } else {
        res.send({result: "fail"});
    }
};

/* Used by Server Auth */
exports.checkAccount = function(userId, userPwd) {
    if(accountList){
        for(var i=0; i<accountList.length; i++){
            var account = accountList[i];
            if(account && account.userId == userId){
                if(account.userPwd == userPwd){
                    return true;
                }
            }
        }
        return false;
    } else {
        return false;
    }
    //return true;
};

exports.checkAdmin = function(userId) {
    for(var i=0; i<accountList.length; i++){
        var account = accountList[i];
        if(account.userId == userId){
            return account.adminYn;
        }
    }
    return false;
};

exports.add = function(req, res) {
    var userId = req.query.userId;
    var passwd = req.query.userId2;
    var isAdmin = req.query.isAdmin;

    // TODO: Verify Input Value
    if(userId == undefined || userId.length > 100){
        var msg = "Invalid user ID for " + userId + " (under 100 lengths, english and number allowed)";
        res.send({result:'fail', errorMessage: msg, errorCode: -1});
        logging.error(msg);
        return;
    }

    if(passwd == undefined || passwd.length < 4 || passwd.length > 20){
        msg = "Invalid password for " + passwd + " (4 ~ 20 lengths, english and number allowed)";
        res.send({result:'fail', errorMessage: msg, errorCode: -2});
        logging.error(msg);
        return;
    }

    var sql = "insert into Account "
        + "(userId, userPwd, adminYn, createdDateTime) "
        + "values "
        + "(" + database.toSqlValue(userId)
        + ", " + database.toSqlValue(passwd)
        + ", " + database.toSqlValue(isAdmin)
        + ", now())";

    database.exec(sql, function callbackForInsert(err, result){
        if(err) {
            if(err.code = 'ER_DUP_ENTRY'){
                var msg = "there is duplicated user ID for " + userId;
                res.send({result:'fail', errorMessage: msg, errorCode: -3});
                logging.error(msg);
                return;
            } else {
                res.send({result:'fail', errorMessage: err.message , errorCode: -4});
                logging.error(err);
                return;
            }
        }

        init(function (){
            "use strict";
            logging.info("the account has been added with id for " + userId);
            res.send({result: 'ok'});
        });
    });
};

exports.webAdd = function(req, res) {
    if(req == undefined ){
        res.send({status:"fail", errorMessage: "No currentUserId or PW"})
    }
    var userId = req.body.params.userId;
    var passwd = req.body.params.userId2;
    var isAdmin = req.body.params.isAdmin;

    // TODO: Verify Input Value
    if(userId == undefined || userId.length > 100){
        var msg = "Invalid user ID for " + userId + " (under 100 lengths, english and number allowed)";
        res.send({result:'fail', errorMessage: msg, errorCode: -1});
        logging.error(msg);
        return;
    }

    if(passwd == undefined || passwd.length < 4 || passwd.length > 20){
        msg = "Invalid password for " + passwd + " (4 ~ 20 lengths, english and number allowed)";
        res.send({result:'fail', errorMessage: msg, errorCode: -2});
        logging.error(msg);
        return;
    }

    var sql = "insert into Account "
        + "(userId, userPwd, adminYn, createdDateTime) "
        + "values "
        + "(" + database.toSqlValue(userId)
        + ", " + database.toSqlValue(passwd)
        + ", " + database.toSqlValue(isAdmin)
        + ", now())";

    database.exec(sql, function callbackForInsert(err, result){
        if(err) {
            if(err.code = 'ER_DUP_ENTRY'){
                var msg = "there is duplicated user ID for " + userId;
                res.send({result:'fail', errorMessage: msg, errorCode: -3});
                logging.error(msg);
                return;
            } else {
                res.send({result:'fail', errorMessage: err.message , errorCode: -4});
                logging.error(err);
                return;
            }
        }

        init(function (){
            "use strict";
            res.send({result: 'ok'});
            logging.info("the account has been added with id for " + userId);
        });
    });
};


exports.update = function(req, res) {
    var oldUserId = req.params.userId;
    var userId = req.body.userId;
    var passwd = req.body.userId2;
    var isAdmin = req.body.isAdmin;

    // TODO: Verify Input Value
    if(!oldUserId || oldUserId.length > 100){
        var msg = "Invalid old user ID for " + oldUserId + " (under 100 lengths, english and number allowed)";
        res.send({result:'fail', errorMessage: msg, errorCode: -1});
        logging.error(msg);
		return;
    }

    if(!userId || userId.length > 100){
        var msg = "Invalid user ID for " + userId + " (under 100 lengths, english and number allowed)";
        res.send({result:'fail', errorMessage: msg, errorCode: -2});
        logging.error(msg);
		return;
    }

    if(!passwd || !(passwd.length >= 4 && passwd.length <=20)){
        var msg = "Invalid password for " + passwd + " (4 ~ 20 lengths, english and number allowed)";
        res.send({result:'fail', errorMessage: msg, errorCode: -3});
        logging.error(msg);
		return;
    }

    var sql = "Update Account set "
        + "userId = " +  database.toSqlValue(userId)
        + ", userPwd = " + database.toSqlValue(passwd)
        + ", adminYn = " + database.toSqlValue(isAdmin)
        + ", modifiedDateTime = now()"
        + " where "
        + "userId = " + database.toSqlValue(oldUserId);

    database.exec(sql, function callbackForInsert(err, result){
        if(err) {
            res.send({result:'fail', errorMessage: err.message , errorCode: -1});
            logging.error(err);
            return;
        }

        init(function(){
            "use strict";

            res.send({result: 'ok'});
            logging.info("the account has been updated on id for " + userId);
        });
    });
};

exports.webUpdate = function(req, res) {
    var oldUserId = req.params.userId;
    var userId = req.body.params.userId;
    var passwd = req.body.params.userId2;
    var isAdmin = req.body.params.isAdmin;

    // TODO: Verify Input Value
    if(!oldUserId || oldUserId.length > 100){
        var msg = "Invalid old user ID for " + oldUserId + " (under 100 lengths, english and number allowed)";
        res.send({result:'fail', errorMessage: msg, errorCode: -1});
        logging.error(msg);
		return;
    }

    if(!userId || userId.length > 100){
        var msg = "Invalid user ID for " + userId + " (under 100 lengths, english and number allowed)";
        res.send({result:'fail', errorMessage: msg, errorCode: -2});
        logging.error(msg);
		return;
    }

    if(!passwd || !(passwd.length >= 4 && passwd.length <=20)){
        var msg = "Invalid password for " + passwd + " (4 ~ 20 lengths, english and number allowed)";
        res.send({result:'fail', errorMessage: msg, errorCode: -3});
        logging.error(msg);
		return;
    }

    var sql = "Update Account set "
        + "userId = " +  database.toSqlValue(userId)
        + ", userPwd = " + database.toSqlValue(passwd)
        + ", adminYn = " + database.toSqlValue(isAdmin)
        + ", modifiedDateTime = now()"
        + " where "
        + "userId = " + database.toSqlValue(oldUserId);

    database.exec(sql, function callbackForInsert(err, result){
        if(err) {
            res.send({result:'fail', errorMessage: err.message , errorCode: -1});
            logging.error(err);
            return;
        }

        init(function(){
            "use strict";

            res.send({result: 'ok'});
            logging.info("the account has been updated on id for " + userId);
        });
    });
};


exports.remove = function(req, res) {
    var userId = req.params.userId;
    var sql = "delete from Account "
        + "where userId = " + database.toSqlValue(userId);

    database.exec(sql, function (err, result) {
        if(err){
            res.send({result:'fail', errorMessage: err.message, errorCode: -1});
            logging.error(err);
            return;
        }

		res.send({ result: 'ok' });
        logging.info("the account has been deleted for " + userId);

		for(var i=0; i<accountList.length; i++){
            var account = accountList[i];
            if(account.userId == userId){
                accountList[i] = null;
            }
        }
		
        init();
    });
};

exports.removeAll = function(req, res) {
    var userId;
    for(var i=0; i<  req.query.deleteSelectedItems.length && req.query.deleteSelectedItems[i] != null; i++) {
        userId = req.query.deleteSelectedItems[i];

        if(!userId){
            logging.error("Invalid userId value");
            continue;
        }

        var sql = "delete from Account "
            + "where userId = " + database.toSqlValue(userId);

        database.exec(sql, function (err, result) {
            if (err) {
                logging.error(err);
                return;
            }

            logging.info("the account has been deleted for " + userId);

            for (var i = 0; i < accountList.length && !accountList[i]; i++) {
                var account = accountList[i];
                if (account.userId == userId) {
                    accountList[i] = null;
                }
            }
        })
    }

	init();
	res.send({ result: 'ok' });            
};
