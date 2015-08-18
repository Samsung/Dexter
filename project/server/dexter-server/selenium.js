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

var test = require('selenium-webdriver/testing');
var webdriver = require('selenium-webdriver');
var assert = require('assert');
var chrome = require('selenium-webdriver/chrome');
//var firefox = require('selenuum-webdriver/firefox');
var path = require('chromedriver').path;

test.describe('Dexter Web UI Test', function() {
    var driver = new webdriver.Builder().
        withCapabilities(webdriver.Capabilities.chrome())
        .build();

     /*var driver = new webdriver.Builder().
     withCapabilities(webdriver.Capabilities.firefox())
     .build();*/

    driver.get('http://localhost:4982/defect', function (done) {
    });

    test.it('should show home page 1', function () {
        driver.getTitle().then(function (title) {
            // Understanding the API
            assert.equal(title,'Defect');
        });
    });

    test.it('should show home page 2', function () {
        driver.getTitle().then(function (title) {
            if (title !== 'Dexter') {
                throw new Error('Expected "Dexter", but was "' + title + '"');
            }
        });
    });

    test.it('should show dashboard page',function() {
        driver.findElement(webdriver.By.id('chartBtn')).click();
    });

    test.it('should show the first defect', function(){
        var searchBox = driver.findElement(webdriver.By.className('defectList'));
        searchBox.getAttribute('className').then(function(value) {
            assert.equal(value, 'div.ngTopPanel.ng-scope');
        });
    });

    test.it('should show Admin page',function() {
        driver.findElement(webdriver.By.id('adminBtn')).click();
    });

    //Promises
    test.it('should show home page is always (Dexter)',function() {
        var promise = driver.getTitle();

        promise.then(function(title) {
            console.log("title is: " + title);
        });
    });

    //Error Handling
    test.it('should show home page is always (Dexter)',function() {
        driver.switchTo().alert().dismiss().then(null, function(e){
            if (e.code !== webdriver.ErrorCode.SUCCESS) {
                throw e;
            }
        });
        driver.findElement(webdriver.By.id('adminBtn')).click();
    });

    test.it('test find Elements List', function(){
        List<WebElement> elementList;
        var elementList = driver.findElement(By.xpath("public/defect[contains(@class,'btn_)]"));
        console.log(elementList);
        for(elementList in ele){
            log.i(""+ele.getText());
        }
        console.log(elementList);
    });
});

