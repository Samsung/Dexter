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
package com.samsung.sec.dexter.core.checker;

import java.io.File;
import java.io.FileInputStream;
import java.util.ArrayList;
import java.util.List;

import org.apache.poi.xssf.usermodel.XSSFRow;
import org.apache.poi.xssf.usermodel.XSSFSheet;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;

import com.samsung.sec.dexter.core.config.DexterConfig;
import com.samsung.sec.dexter.core.exception.DexterRuntimeException;
import com.samsung.sec.dexter.core.util.DexterUtil;
import com.samsung.sec.dexter.core.util.PoiUtil;

public class CheckerConfigExcelHandler {
	private XSSFWorkbook workbook;
	private String version;
	private String toolName;
	private String language;
	private String dataSheetName;
	private int beginRow;
	private int endRow;
	private int checkerCodeIndex;
	private int checkerNameIndex;
	private int checkerCategoryIndex;
	private int useTypeIndex;
	private int severityIndex;
	private int cweIndex;
	private int shortDescIndex;
	private int propertyRowIndex;
	private int propertyBeginColumn;
	private int propertyEndColumn;
	private List<String> propertyKeyList;
	
	public CheckerConfigExcelHandler(String excelFilePath){
		XSSFSheet configSheet = getConfigSheet(excelFilePath);
		initFields(configSheet);
	}
	
	private XSSFSheet getConfigSheet(String excelFilePath){
		File excelFile = DexterUtil.toFile(excelFilePath);
		FileInputStream fis = null;
		
		try{
			fis = new FileInputStream(excelFile);
			workbook = new XSSFWorkbook(fis);
			return workbook.getSheet(DexterConfig.SHEET_NAME_OF_CHECKER_CONFIG);
		} catch (Exception e) {
			throw new DexterRuntimeException(e.getMessage() + " (Check if the document is encripted)", e);
		} finally {
			DexterUtil.handleClosingFileInputStream(fis);
		}
	}
	
	private void initFields(XSSFSheet configSheet){
		int rowIndex = 1;
		this.version = PoiUtil.getValueInSheet(configSheet, rowIndex++, 1);
		this.toolName = PoiUtil.getValueInSheet(configSheet, rowIndex++, 1);
		this.language = PoiUtil.getValueInSheet(configSheet, rowIndex++, 1);
		this.dataSheetName = PoiUtil.getValueInSheet(configSheet, rowIndex++, 1);
		this.beginRow = PoiUtil.getIntValueInSheet(configSheet, rowIndex++, 1);
		this.endRow = PoiUtil.getIntValueInSheet(configSheet, rowIndex++, 1);
		this.checkerCodeIndex = PoiUtil.getAlpahbetToIntValueInSheet(configSheet, rowIndex++, 1);
		this.checkerNameIndex = PoiUtil.getAlpahbetToIntValueInSheet(configSheet, rowIndex++, 1);
		this.checkerCategoryIndex = PoiUtil.getAlpahbetToIntValueInSheet(configSheet, rowIndex++, 1);
		this.useTypeIndex = PoiUtil.getAlpahbetToIntValueInSheet(configSheet, rowIndex++, 1);
		this.severityIndex = PoiUtil.getAlpahbetToIntValueInSheet(configSheet, rowIndex++, 1);
		this.cweIndex = PoiUtil.getAlpahbetToIntValueInSheet(configSheet, rowIndex++, 1);
		this.shortDescIndex = PoiUtil.getAlpahbetToIntValueInSheet(configSheet, rowIndex++, 1);
		this.propertyRowIndex = PoiUtil.getIntValueInSheet(configSheet, rowIndex++, 1);
		this.propertyBeginColumn = PoiUtil.getAlpahbetToIntValueInSheet(configSheet, rowIndex++, 1);
		this.propertyEndColumn = PoiUtil.getAlpahbetToIntValueInSheet(configSheet, rowIndex++, 1);
	}
	
	private void createPropertyKeyList(XSSFSheet dataSheet){
		if(this.propertyRowIndex == -1){
			return;
		}
		
		propertyKeyList = new ArrayList<String>(this.propertyEndColumn - this.propertyBeginColumn);
		
		for(int c = this.propertyBeginColumn; c <= this.propertyEndColumn; c++){
			propertyKeyList.add(PoiUtil.getValueInSheet(dataSheet, this.propertyRowIndex - 1, c));
		}
	}
	
	protected CheckerConfig getCheckerConfig(){
		CheckerConfig checkerConfig = new CheckerConfig(toolName, DexterConfig.LANGUAGE.valueOf(language));
		XSSFSheet dataSheet = workbook.getSheet(dataSheetName);
		createPropertyKeyList(dataSheet);
		
		for(int r= beginRow-1; r < endRow; r++){
			XSSFRow row = dataSheet.getRow(r);
			Checker checker = createChecker(row);
			checkerConfig.addChecker(checker);
		}
		
		return checkerConfig;
	}

	private Checker createChecker(XSSFRow row) {
		Checker checker = new Checker(PoiUtil.getValueInRow(row, checkerCodeIndex),
				PoiUtil.getValueInRow(row, checkerNameIndex),	
				version,
				PoiUtil.getValueInRow(row, shortDescIndex),
				isActive(row));
		
		checker.setType(PoiUtil.getValueInRow(row, useTypeIndex));
		checker.setCategoryName(PoiUtil.getValueInRow(row, checkerCategoryIndex));
		checker.setSeverityCode(PoiUtil.getValueInRow(row, severityIndex));
		checker.setCwe(PoiUtil.getIntValueInRow(row, cweIndex));
		
		addCheckerProperty(checker, row);
		
		return checker;
    }
	
	private void addCheckerProperty(IChecker checker, XSSFRow row) {
		if(this.propertyRowIndex == -1){
			return;
		}
		
		for(int c = this.propertyBeginColumn, keyIndex = 0; c <= this.propertyEndColumn; c++, keyIndex++){
			String key = propertyKeyList.get(keyIndex);
			String value = PoiUtil.getValueInRow(row, c);
			checker.addProperty(key, value);
		}
    }

	private boolean isActive(XSSFRow row){
		final String type = row.getCell(useTypeIndex).getStringCellValue();
		if(DexterConfig.getInstance().isReviewMode()){
			return "NONE".equals(type) ? false : true;
		} else {
			return "BOTH".equals(type) || "DEV".equals(type);
		}
	}
}
