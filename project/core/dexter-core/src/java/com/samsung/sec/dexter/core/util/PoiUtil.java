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
package com.samsung.sec.dexter.core.util;

import org.apache.poi.xssf.usermodel.XSSFCell;
import org.apache.poi.xssf.usermodel.XSSFRow;
import org.apache.poi.xssf.usermodel.XSSFSheet;

public class PoiUtil {
	public static String getValueInSheet(XSSFSheet sheet, int row, int column) {
		if (sheet == null || sheet.getRow(row) == null || sheet.getRow(row).getCell(column) == null) {
			return "";
		}

		return sheet.getRow(row).getCell(column).getStringCellValue();
	}

	public static int getIntValueInSheet(XSSFSheet sheet, int row, int column) {
		try {
			XSSFCell cell = sheet.getRow(row).getCell(column);
			
			if (cell.getCellType() == XSSFCell.CELL_TYPE_NUMERIC) {
				return (int) cell.getNumericCellValue();
			} else if (cell.getCellType() == XSSFCell.CELL_TYPE_STRING) {
				return Integer.parseInt(getValueInSheet(sheet, row, column));
			}  else {
				return -1;
			}
		} catch (Exception e) {
			return -1;
		}
	}

	public static int getAlpahbetToIntValueInSheet(XSSFSheet sheet, int row, int column) {
		return DexterUtil.alphabetToInt(getValueInSheet(sheet, row, column));
	}

	public static String getValueInRow(XSSFRow row, int index) {
		if (index == -1 || row == null || row.getCell(index) == null) {
			return "";
		}

		try {
			XSSFCell cell = row.getCell(index); 
			if (cell.getCellType() == XSSFCell.CELL_TYPE_NUMERIC) {
				return "" + cell.getNumericCellValue();
			} else if (cell.getCellType() == XSSFCell.CELL_TYPE_STRING) {
				return cell.getStringCellValue();
			} else {
				return cell.getRawValue();
			}
		} catch (Exception e) {
			return "";
		}
	}

	public static int getIntValueInRow(XSSFRow row, int index) {
		try {
			XSSFCell cell = row.getCell(index); 
			if (cell.getCellType() == XSSFCell.CELL_TYPE_NUMERIC) {
				return (int) cell.getNumericCellValue();
			} else if (cell.getCellType() == XSSFCell.CELL_TYPE_STRING) {
				return Integer.parseInt(getValueInRow(row, index));
			} else {
				return -1;
			}
		} catch (Exception e) {
			return -1;
		}
	}
}
