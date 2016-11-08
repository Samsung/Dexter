module.exports = function(config) {
  config.set({

    // base path that will be used to resolve all patterns (eg. files, exclude)
    basePath: '',


    // frameworks to use
    // available frameworks: https://npmjs.org/browse/keyword/karma-adapter
    frameworks: ['mocha', 'chai'],

    plugins: [
        'karma-mocha',
        'karma-chrome-launcher',
        'karma-chai',
        'karma-babel-preprocessor',
        'karma-spec-reporter',
        'karma-coverage'
    ],

    files: [
        {pattern: 'public/js/**/*.css'},
        'public/js/angular/angular.js',
        'public/js/angular-route/angular-route.min.js',
        'public/js/angular-touch/angular-touch.min.js',
        'public/js/angular-animate/angular-animate.min.js',
        'public/js/angular-ui-grid/ui-grid.min.js',
        'public/js/angular-bootstrap/ui-bootstrap-tpls.min.js',
        'public/js/pdfmake/build/pdfmake.min.js',
        'public/js/pdfmake/build/vfs_fonts.js',
        'public/js/jquery/dist/jquery.min.js',
        'public/js/bootstrap/dist/js/bootstrap.min.js',
        'public/js/lodash/dist/lodash.min.js',
        'public/dexterMonitorApp.js',
        'public/common.js',
        'public/util/*.js',
        'public/ctrl/*.js',
        'public/service/*.js',
        'public/js/angular-mocks/angular-mocks.js',
        'public/js/DateJS/build/production/date.min.js',
        'public/js/chart.js/dist/Chart.min.js',
        {pattern: 'test/front-end/**/*.js'}
    ],


    // list of files to exclude
    exclude: [
    ],

    // preprocess matching files before serving them to the browser
    // available preprocessors: https://npmjs.org/browse/keyword/karma-preprocessor
    preprocessors: {
        'public/common.js'      :   ['babel','coverage'],
        'public/util/*.js'      :   ['babel','coverage'],
        'public/ctrl/*.js'      :   ['babel','coverage'],
        'public/service/*.js'   :   ['babel','coverage']
    },
    babelPreprocessor: {
        options: {
            presets: ['es2015']
        }
    },
    coverageReporter: {
        dir: 'test/front-end/coverage'
    },

    // test results reporter to use
    // possible values: 'dots', 'progress'
    // available reporters: https://npmjs.org/browse/keyword/karma-reporter
    reporters: ['spec', 'coverage'],

    // web server port
    port: 9876,

    // enable / disable colors in the output (reporters and logs)
    colors: true,

    // level of logging
    // possible values: config.LOG_DISABLE || config.LOG_ERROR || config.LOG_WARN || config.LOG_INFO || config.LOG_DEBUG
    logLevel: config.LOG_INFO,

    // enable / disable watching file and executing tests whenever any file changes
    autoWatch: true,

    // start these browsers
    // available browser launchers: https://npmjs.org/browse/keyword/karma-launcher
    browsers: ['Chrome'],

    // Continuous Integration mode
    // if true, Karma captures browsers, runs the tests and exits
    singleRun: false
  });
};
