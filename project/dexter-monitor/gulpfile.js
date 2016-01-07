"use strict";

var gulp = require('gulp');
var karma = require('karma').server;
var concat = require('gulp-concat');
var stripDebug = require('gulp-strip-debug');
var uglify = require('gulp-uglify');
var mocha = require('gulp-mocha');

gulp.task('default', ['test', 'deploy'], function(){
});

gulp.task('combine-js', function(){
   return gulp.src('public/**/*.js')
       .pipe(stripDebug())
       .pipe(uglify())
       .pipe(gulp.dest('dist'));
});

gulp.task('test-front-end', function(done){
    karma.start({
        configFile: __dirname + '/karma.config.js',
        singleRun: true
    }, function(result){
        console.log(result);
        done();
    });
});

gulp.task('test-back-end', function(){
    return gulp.src(['test/back-end/**/test-*.js'], { read: false })
        .pipe(mocha({reporter: 'spec'}));    // spec | dot | nyan
});

gulp.task('test', ['test-back-end', 'test-front-end'], function(){
});

gulp.task('deploy', function(){
    console.log('deploy...');
});

gulp.task('watch-test', function(){
    gulp.watch(['public/**/*.js', 'util/**/*.js', 'routes/**/*.js', 'test/**'], ['test-back-end']);
});

gulp.task('run', ['combine-js', 'test'], function(){
});