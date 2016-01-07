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

var expect = require('chai').expect;
var assert = require('chai').assert;

var dexterUtil = require('../../util/dexter-util');

describe('tests for dexter-util.js', function(){
    it('should return current time as a string type "yyyymmdd-hhMMss"', function(){
        var currentTime = dexterUtil.getCurrentTimeString();
        expect(currentTime).to.match(/[0-9][0-9][0-9][0-9](01|02|03|04|05|06|07|08|09|10|11|12)[0-3][0-9]\-[0-2][0-9][0-5][0-9][0-5][0-9]/);
    });

    it('should return correct localhost IP', function(){
        var ip = dexterUtil.getLocalIPAddress();
        expect(ip).to.match(/[0-9]{1,3}\.[0-9]{1,3}\.[0-9]{1,3}\.[0-9]{1,3}/);
    });

    it("should return correct cli options", function(){
        var tempArgv = process.argv.slice();
        process.argv.push("-p1=1234");
        process.argv.push("-p2=1234");
        process.argv.push("-port=1234");
        process.argv.push("-name=HongGilDong");

        var options = dexterUtil.getCliOptions();

        assert.equal('1234', options.getValue('p1'));
        assert.equal('1234', options.getValue('p2', "5678"));
        assert.equal('5678', options.getValue('p3', "5678"));
        assert.equal('1234', options.getValue('port'));
        assert.equal('HongGilDong', options.getValue('name'));

        process.argv = tempArgv;
    });

    it("should return cli options with wrong options", function(){
        var tempArgv = process.argv.slice();

        process.argv.push("p1-1234");
        process.argv.push("p2:1234");
        process.argv.push("p3 1234");
        process.argv.push("-p4:1234");
        process.argv.push("-p5 1234");
        process.argv.push("-p6=");
        process.argv.push("-=1234");
        process.argv.push("- =1234");

        var options = dexterUtil.getCliOptions();

        assert.equal('defaultValue', options.getValue('p1', 'defaultValue'));
        assert.equal('defaultValue', options.getValue('p2', 'defaultValue'));
        assert.equal('defaultValue', options.getValue('p3', 'defaultValue'));
        assert.equal('defaultValue', options.getValue('p4', 'defaultValue'));
        assert.equal('defaultValue', options.getValue('p5', 'defaultValue'));
        assert.equal('defaultValue', options.getValue('p6', 'defaultValue'));
        assert.equal(undefined, options.getValue('p7'));
        assert.equal(undefined, options.getValue('', 'defaultValue'));
        assert.equal(undefined, options.getValue(' ', 'defaultValue'));

        process.argv = tempArgv;
    })
});