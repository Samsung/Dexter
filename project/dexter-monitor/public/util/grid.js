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

const DEFECT_FILENAME_PREFIX = 'defect-list';
const USER_FILENAME_PREFIX = 'user-list';
const INSTALLATION_STATUS_FILENAME_PREFIX = 'installation-status';
const DEFECT_STATUS_FILENAME_PREFIX = 'defect-status';
const CURRENT_STATUS_FILENAME_PREFIX = 'current-status-list';
const WEEKLY_STATUS_FILENAME_PREFIX = 'weekly-status-list';

const ROW_HEIGHT = 32;
const HEADER_HEIGHT = 110;

function createGrid(columnDefs) {
    return {
        enableSorting: true,
        enableFiltering: true,
        showGridFooter: true,
        enableGridMenu: true,
        enableSelectAll: true,
        exporterOlderExcelCompatibility: true,
        exporterCsvFilename: 'list.csv',
        exporterPdfFilename: 'list.pdf',
        exporterPdfDefaultStyle: {fontSize: 8},
        exporterTableStyle: {margin: [5,5,5,5]},
        exporterPdfTableHeaderStyle: {fontSize: 10, bold: true, italics: true, color: 'red'},
        exporterPdfOrientation: 'landscape',
        exporterPdfPageSize: 'A4',
        columnDefs: columnDefs,
        data: []
    };
}

function setGridExportingFileNames(gridOptions, fileName) {
    gridOptions.exporterCsvFilename = fileName + '.csv';
    gridOptions.exporterPdfFilename = fileName + '.pdf';
}