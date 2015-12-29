/**
 *  @file   CppUtil.java
 *  @brief  CppUtil class source file
 *  @author adarsh.t
 *
* Copyright 2014 by Samsung Electronics, Inc.
* All rights reserved.
* 
* Project Description :
* This software is the confidential and proprietary information
* of Samsung Electronics, Inc. ("Confidential Information").  You
* shall not disclose such Confidential Information and shall use
* it only in accordance with the terms of the license agreement
* you entered into with Samsung Electronics.
*/
package com.samsung.sec.dexter.vdcpp.util;

import java.util.Map;

import org.eclipse.cdt.core.dom.ast.ASTVisitor;
import org.eclipse.cdt.core.dom.ast.IASTDeclaration;
import org.eclipse.cdt.core.dom.ast.IASTTranslationUnit;
import org.mortbay.util.IO;

import com.samsung.sec.dexter.core.exception.DexterRuntimeException;


public class CppUtil {

	
	private CppUtil()
	{
	}
	
	/**
	 * ExtractModuleName(String sourceFilePath, final int lineNumber) method is responsible
	 * for getting ModuleInformation
	 * 
	 * @param     	[in] String sourceFilePath, final int lineNumber
	 * @return		[out] Map<String, String>
	 * @warning		[None]
	 * @exception	IO exception
	 */	
	public static synchronized Map<String, String>  extractModuleName(IASTTranslationUnit translationUnit, final int lineNumber)
	{
		
		Map<String, String> mapModuleName = null;		
		try
		{
			ASTVisitor visitor = new ASTVisitor() {
				public int visit(IASTDeclaration declaration ) {

					boolean visitStatus =DexterUtilHelper.visitFunction(declaration,lineNumber,".cpp");

					if(visitStatus)
					{						
						return ASTVisitor.PROCESS_ABORT;
					}

					return ASTVisitor.PROCESS_CONTINUE;

				}
			};
			visitor.shouldVisitDeclarations = true;	
			translationUnit.accept(visitor);
		}
		catch(Exception ex)
		{			
			throw new DexterRuntimeException(ex.getMessage());			
		}
		mapModuleName =DexterUtilHelper.getMapData();

		return mapModuleName;
	}
	
	

}
