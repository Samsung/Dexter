package com.samsung.sec.dexter.vdcpp.checkerlogic;
/**
 *  @file   MacroParenthesis.java
 *  @brief  MacroParenthesis class source file
 *  @author adarsh.t
 *
 * Copyright 2015 by Samsung Electronics, Inc.
 * All rights reserved.
 * 
 * Project Description :
 * This software is the confidential and proprietary information
 * of Samsung Electronics, Inc. ("Confidential Information").  You
 * shall not disclose such Confidential Information and shall use
 * it only in accordance with the terms of the license agreement
 * you entered into with Samsung Electronics.
 */


import java.util.Map;
import org.eclipse.cdt.core.dom.ast.IASTFileLocation;
import org.eclipse.cdt.core.dom.ast.IASTName;
import org.eclipse.cdt.core.dom.ast.IASTPreprocessorMacroDefinition;
import org.eclipse.cdt.core.dom.ast.IASTPreprocessorStatement;
import org.eclipse.cdt.core.dom.ast.IASTTranslationUnit;
import org.eclipse.cdt.core.dom.ast.IBinding;
import org.eclipse.cdt.core.dom.ast.IMacroBinding;
import com.samsung.sec.dexter.core.analyzer.AnalysisConfig;
import com.samsung.sec.dexter.core.analyzer.AnalysisResult;
import com.samsung.sec.dexter.core.checker.Checker;
import com.samsung.sec.dexter.core.defect.PreOccurence;
import com.samsung.sec.dexter.vdcpp.plugin.DexterVdCppPlugin;
import com.samsung.sec.dexter.vdcpp.util.CppUtil;


public class MacroParenthesisCheckerLogic implements ICheckerLogic{

	private IASTTranslationUnit translationUnit;
	
	@Override
	public void analyze(final AnalysisConfig config, final AnalysisResult result, 
			final Checker checker, IASTTranslationUnit unit) {
		translationUnit =unit;		
		final IASTPreprocessorStatement[] macroStatementList = unit.getAllPreprocessorStatements();		
		loop:
			for (IASTPreprocessorStatement macro_statement : macroStatementList)
			{
				if (macro_statement instanceof IASTPreprocessorMacroDefinition)
				{
					final IASTPreprocessorMacroDefinition simple_declaration = (IASTPreprocessorMacroDefinition) macro_statement;												

					final IASTName name = simple_declaration.getName().getLastName();		


					if (name.toString().isEmpty())
					{
						continue;
					}


					final IBinding MacroBinding = name.getBinding();
					if (MacroBinding instanceof IMacroBinding)
					{
						final IMacroBinding FuncStyleMacro = (IMacroBinding) MacroBinding;
						if (FuncStyleMacro.isFunctionStyle())
						{
							String macroName = FuncStyleMacro.toString();
							final char[][] funcStyleMacroParamList = FuncStyleMacro.getParameterList();

							if (funcStyleMacroParamList == null)
							{
								continue loop;
							}

							final int ParameterCount = funcStyleMacroParamList.length;
							final String expansion = simple_declaration.getExpansion();

							// For checking if outer parenthesis exist
							if (!expansion.isEmpty() && ((expansion.charAt(0) != '(') || (expansion.charAt(expansion.length() - 1) != ')')))
							{
								
								fillDefectData( config,
										result,  checker,
										macro_statement.getFileLocation(),  checker.getDescription(), macroName);
								continue loop;					
							}

							//For checking if outer parenthesis are enclosing whole expression
							int braceCount = 0;
							for (int k=0; k<expansion.length(); k++)
							{
								if (expansion.charAt(k) == '(')
								{
									braceCount++;
								}
								else if (expansion.charAt(k) == ')')
								{
									braceCount--;
									if ( (braceCount==0) && (k <expansion.length()-1) )
									{
										fillDefectData( config,
												result,  checker,
												macro_statement.getFileLocation(),  checker.getDescription(), macroName);
										continue loop;
										
									}
								}
							}	

							//Checking for inner macro expression
							if (expansion.length() > 2)
							{
								final String strToCheck = expansion.substring(1, expansion.length()-1);

								for (int j=0; j<ParameterCount; j++)
								{
									String toCheck = strToCheck;
									final String param = new String(funcStyleMacroParamList[j]);
									final int len = param.length();
									int index = toCheck.indexOf(param);
									while (index != -1)
									{												
										if ( (index == 0) || (index+len == toCheck.length()) )													
										{
											break;											
										}
										final String before;
										final String after;

										before = toCheck.substring(index - 1, index); 													
										after = toCheck.substring(index + len, index + len + 1); 

										if (!((before.matches("[a-zA-Z0-9_]") || after.matches("[a-zA-Z0-9_]"))))
										{											
												if (!before.equals("(") || !after.equals(")"))
												{

													fillDefectData( config,
															result,  checker,
															macro_statement.getFileLocation(),  checker.getDescription(), macroName);
													continue loop;
												}
											
											toCheck = toCheck.substring(index + len + 1);
											index = toCheck.indexOf(param);
										}
									}
								} 

							} 
						} 
					} 
				} 
			} 
	}


	private void fillDefectData(AnalysisConfig config,
			AnalysisResult result, Checker checker,
			IASTFileLocation fileLocation, String message, String declaratorName) {
		PreOccurence preOcc = createPreOccurence(config, checker, fileLocation, message,declaratorName);
		result.addDefectWithPreOccurence(preOcc);

	}
	private PreOccurence createPreOccurence(AnalysisConfig config,
			Checker checker, IASTFileLocation fileLocation, String msg,String declaratorName) {
		final int startLine = fileLocation.getStartingLineNumber();
		final int endLine = fileLocation.getEndingLineNumber();
		final int startOffset = fileLocation.getNodeOffset();
		final int endOffset = startOffset + fileLocation.getNodeLength();

		Map<String,String> tempmap =CppUtil.extractModuleName(translationUnit, startLine);
		String className =tempmap.get("className");
		String methodName =tempmap.get("methodName");

		PreOccurence preOcc = new PreOccurence();
		preOcc.setCheckerCode(checker.getCode());
		preOcc.setFileName(config.getFileName());
		preOcc.setModulePath(config.getModulePath());
		preOcc.setClassName(className);
		preOcc.setMethodName(methodName);
		preOcc.setLanguage(config.getLanguageEnum().toString());
		preOcc.setSeverityCode(checker.getSeverityCode());
		preOcc.setMessage(checker.getDescription());
		preOcc.setToolName(DexterVdCppPlugin.PLUGIN_NAME);
		preOcc.setStartLine(startLine);
		preOcc.setEndLine(endLine);
		preOcc.setCharStart(startOffset);
		preOcc.setCharEnd(endOffset);
		preOcc.setVariableName(declaratorName);
		preOcc.setStringValue(msg);
		preOcc.setMessage(msg);

		return preOcc;

	}







}
