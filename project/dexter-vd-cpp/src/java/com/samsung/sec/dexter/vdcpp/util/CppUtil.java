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
package com.samsung.sec.dexter.vdcpp.util;

import java.io.PrintStream;
import java.util.Map;
import org.eclipse.cdt.core.dom.ast.ASTVisitor;
import org.eclipse.cdt.core.dom.ast.IASTDeclaration;
import org.eclipse.cdt.core.dom.ast.IASTTranslationUnit;


public class CppUtil {

	static PrintStream out =null;
	static PrintStream getPrintStream()
	{
		return out;
	}
	
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
			if(out !=null)
			{
				out.println(ex.getMessage());
			}
		}
		mapModuleName =DexterUtilHelper.getMapData();

		return mapModuleName;
	}
	
	

}
