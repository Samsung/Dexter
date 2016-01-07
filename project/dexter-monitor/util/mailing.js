/**
 * Copyright (c) 2015 Samsung Electronics, Inc.,
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

var log = require('../util/logging');
var smtpTransport;
var email;

exports.init = function() {
    email = global.config.email;

    if(email.type === "smtp") initializeSMTP();
};

/**
 * @param emailParameters - from(option), toList, ccList, bccList, title, [contents | htmlContents]
 *  eg)
 *  {
 *      from: "sender@samsung.com"
 *      toList:["receiver@samsung.com"],
 *      ccList:["someone1@samsung.com", "someone2@samsung.com"],
 *      bccList:["someone3@samsung.com"],
 *      title:"dexter-monitor test email by API service",
 *      contents:"test message body",
 *      htmlContents: "<h1> hello </h1>"
 *  }
 *      - 'from' can be ignored if API has default sender
 *      - 'contents' property is prior to 'htmlContents'. ie. if there is contents, htmlContents must be ignored.
 * @param callback(error_message, success_message)
 */
exports.sendEmail = function(emailParameters, callback){
    switch(email.type){
        case "smtp":
            sendEmailBySMTP(emailParameters, callback);
            break;
        case "api":
            sendEmailByAPI(emailParameters, callback);
            break;
        default:
            var errorMsg = "email.type configuration is not valid";
            log.error(errorMsg);
            callback(errorMsg);
    }
};

exports.isSmtpValid = function(){
    return smtpTransport !== undefined;
};

function sendEmailByAPI(emailParameters, callback){
    if(invalidPrecondition(emailParameters, callback)) return;

    var mailOptions = {
        to: emailParameters.toList,
        cc: emailParameters.ccList,
        bcc: emailParameters.bccList,
        title: emailParameters.title,
        body: emailParameters.contents || emailParameters.htmlContents
    };

    var request = require('request');
    request.post(
        email.apiUrl,
        { form: mailOptions },
        function (error, response /*, body*/){
            handleEmailingErrorWithCallback(error, callback);

            if(response.statusCode === 200){
                handleEmailingSuccessWithCallback(emailParameters.title, callback);
            } else {
                log.error(response)
                if(callback) callback(response);
            }
        }
    );
}

function invalidPrecondition(emailParameters, callback){
    if(!email || !email.apiUrl || email.apiUrl.length <= 0){
        var errorMsg = "email.apiUrl configuration is not valid";
        log.error(errorMsg);
        if(callback) callback(errorMsg);
        return true;
    }

    if(!isEmailParametersValid(emailParameters)){
        log.error('emailParameters are not valid');
        if(callback) callback('emailParameters are not valid');
        return true;
    }

    return false;
}

function handleEmailingErrorWithCallback(error, callback){
    if(error){
        log.error(error);
        if(callback) callback(error);
    }
}

function handleEmailingSuccessWithCallback(title, callback){
    var msg = 'email sent: ' + title;
    log.info(msg);
    if(callback) callback(null, msg);
}

function isEmailParametersValid(emailParameters){
    if(!emailParameters || !emailParameters.toList || emailParameters.toList.length <= 0) return false;
    if(!emailParameters.title || emailParameters.title.length <= 0) return false;
    if(!emailParameters.contents && !emailParameters.htmlContents) return false;

    return true;
}

function initializeSMTP(){
    var nodemailer = require('nodemailer');

    smtpTransport = nodemailer.createTransport("SMTP", {
        service: 'EmailService',
        port: email.port,
        auth: {
            user: email.id,
            pass: email.password
        }
    });
}

function sendEmailBySMTP(emailParameters, callback){
    if(invalidPreconditionForSMTP(emailParameters, callback)) return;

    var mailOptions = {
        from: emailParameters.from || email.defaultSenderName + ' <' + email.defaultSenderEmail + '>',
        to: emailParameters.toList.join(),
        cc: emailParameters.ccList.join(),
        bcc: emailParameters.bccList.join(),
        subject: emailParameters.title,
        text: emailParameters.contents || '',
        html: emailParameters.htmlContents || ''
    };

    smtpTransport.sendMail(mailOptions, function(error, response){
        handleEmailingErrorWithCallback(error, callback);
        if(response)
            handleEmailingSuccessWithCallback(emailParameters.title, callback);
    });
}

function invalidPreconditionForSMTP(emailParameters, callback){
    if(!smtpTransport) {
        log.error('smtpTransport is not initialized');
        if(callback) callback('smtpTransport is not initialized');
        return true;
    }

    if(!isEmailParametersValid(emailParameters)){
        log.error('emailParameters are not valid');
        log.error(emailParameters);
        if(callback) callback('emailParameters are not valid');
        return true;
    }

    return false;
}
