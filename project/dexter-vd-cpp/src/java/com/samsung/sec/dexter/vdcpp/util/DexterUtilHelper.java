/**
 *  @file   DexterUtilHelper.java
 *  @brief  DexterUtilHelper class source file
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

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

import org.eclipse.cdt.core.dom.ast.IASTCaseStatement;
import org.eclipse.cdt.core.dom.ast.IASTCompoundStatement;
import org.eclipse.cdt.core.dom.ast.IASTDeclSpecifier;
import org.eclipse.cdt.core.dom.ast.IASTDeclaration;
import org.eclipse.cdt.core.dom.ast.IASTDeclarator;
import org.eclipse.cdt.core.dom.ast.IASTDoStatement;
import org.eclipse.cdt.core.dom.ast.IASTForStatement;
import org.eclipse.cdt.core.dom.ast.IASTFunctionDeclarator;
import org.eclipse.cdt.core.dom.ast.IASTFunctionDefinition;
import org.eclipse.cdt.core.dom.ast.IASTIfStatement;
import org.eclipse.cdt.core.dom.ast.IASTName;
import org.eclipse.cdt.core.dom.ast.IASTNode;
import org.eclipse.cdt.core.dom.ast.IASTSimpleDeclaration;
import org.eclipse.cdt.core.dom.ast.IASTStatement;
import org.eclipse.cdt.core.dom.ast.IASTSwitchStatement;
import org.eclipse.cdt.core.dom.ast.IASTWhileStatement;
import org.eclipse.cdt.internal.core.dom.parser.cpp.CPPASTCatchHandler;
import org.eclipse.cdt.internal.core.dom.parser.cpp.CPPASTCompositeTypeSpecifier;
import org.eclipse.cdt.internal.core.dom.parser.cpp.CPPASTTemplateId;
import org.eclipse.cdt.internal.core.dom.parser.cpp.CPPASTTryBlockStatement;

import com.samsung.sec.dexter.core.exception.DexterRuntimeException;


class DexterUtilHelper 
{
	
	static int count =0;
	static int methodCount=0;
	static int classCount=0;
	static Map<String, Integer> mapSourceMatrices =new HashMap<String, Integer>();	
	private static Map<String, String> mapModuleName =new HashMap<String, String>();
	static Map<String, String> getMapData()
	{
		if(mapModuleName.isEmpty())
		{
			mapModuleName.put("className", "");
			mapModuleName.put("methodName", "");
		}
		return mapModuleName;
	}
	

	private DexterUtilHelper()
	{
	
	}

	/**
	 * visitFunction(IASTDeclaration declaration , int lineNo, String fileExtension) method is responsible
	 * for visit functional statement
	 * 
	 * @param     	[in] IASTDeclaration declaration , int lineNo, String fileExtension
	 * @return		[out] boolean
	 * @warning		[None]
	 * @exception	IO exception
	 */
	static boolean visitFunction(IASTDeclaration declaration , int lineNo, String fileExtension)
	{
		boolean visitStatus =false;

		if ((declaration instanceof IASTSimpleDeclaration) )				
		{
			final IASTSimpleDeclaration simple_declaration = (IASTSimpleDeclaration) declaration;			
			String className ="";
			String MethodName ="";

			final IASTDeclarator[] declaratorList = simple_declaration.getDeclarators();
			for (IASTDeclarator declarator : declaratorList)
			{
				if (declarator instanceof IASTFunctionDeclarator)
				{	

					if (simple_declaration.getDeclSpecifier().getStorageClass() == IASTDeclSpecifier.sc_typedef)
					{
						return visitStatus;
					}					

					if(simple_declaration.getFileLocation().getFileName().contains(".h"))
					{
						if(fileExtension.equals(".cpp") ||fileExtension.equals(".c"))
						{
							return visitStatus;
						}
					}

					final int FunctionStartingLineNumber = declarator.getFileLocation().getStartingLineNumber();
					final int FunctionEndLineNumber = declaration.getFileLocation().getEndingLineNumber();

					if(FunctionStartingLineNumber >lineNo)
					{
						visitStatus =true;
						putDataInToMap(className, "");	
					}
					IASTNode node1 =declarator.getParent().getParent();

					if(node1 instanceof CPPASTCompositeTypeSpecifier)
					{
						IASTName name =((CPPASTCompositeTypeSpecifier)node1).getName();
						if(name instanceof CPPASTTemplateId)
						{
							name = ((CPPASTTemplateId) name).getTemplateName();
						}
						
						className =name.toString();
					}

					MethodName =declarator.getName().toString();

					if(fileExtension.equals(".c"))
					{
						className ="";
					}
					if(FunctionStartingLineNumber <=lineNo  && lineNo <=FunctionEndLineNumber)
					{	
						visitStatus =true;
						putDataInToMap( className, MethodName);
					}
				}
			}
		}
		else if (declaration instanceof IASTFunctionDefinition  )
		{
			final IASTFunctionDefinition Func_Definition = (IASTFunctionDefinition) declaration;	
			final IASTFunctionDeclarator Func_Declarator = Func_Definition.getDeclarator();

			final int FunctionStartingLineNumber =declaration.getFileLocation().getStartingLineNumber();
			final int FunctionEndLineNumber = declaration.getFileLocation().getEndingLineNumber();
			String className ="";
			String MethodName ="";

			IASTNode node = Func_Definition.getParent();
			if(node instanceof CPPASTCompositeTypeSpecifier)
			{
				IASTName name =((CPPASTCompositeTypeSpecifier)node).getName();
				if(name instanceof CPPASTTemplateId)
				{
					name = ((CPPASTTemplateId) name).getTemplateName();
				}
				
				className =name.toString();
			}

			if (Func_Definition.getDeclSpecifier().getStorageClass() == IASTDeclSpecifier.sc_typedef)
			{
				return visitStatus;
			}

			if(Func_Definition.getFileLocation().getFileName().contains(".h"))
			{
				if(fileExtension.equals(".cpp") ||fileExtension.equals(".c"))
				{
					return visitStatus;
				}
			}

			MethodName =Func_Declarator.getName().toString();				 

			if(FunctionStartingLineNumber >lineNo)
			{
				visitStatus =true;
				putDataInToMap(className, "");	
			}

			if(MethodName.contains("::"))
			{
				int indexofColon =MethodName.indexOf("::");
				className =MethodName.substring(0,indexofColon);
				MethodName =MethodName.substring(indexofColon+2);
			}		


			if(FunctionStartingLineNumber <=lineNo  && lineNo <=FunctionEndLineNumber)
			{				
				visitStatus =true;
				putDataInToMap(className, MethodName);			

			}

		}

		return visitStatus;

	}

	private static void putDataInToMap(	String className, String MethodName) 
	{		
		mapModuleName.clear();		
		mapModuleName.put("className", className);
		mapModuleName.put("methodName", MethodName);
	}

	
	/**
	 * visitSourceCodeforCalMethodAndClassCount(IASTDeclaration declaration ) method is responsible
	 * for visit functional statement
	 * 
	 * @param     	[in] IASTDeclaration declaration 
	 * @return		[out] boolean
	 * @warning		[None]
	 * @exception	IO exception
	 */
	static boolean visitSourceCodeforCalMethodAndClassCount(IASTDeclaration declaration )
	{	
		//String declarationString ="";
		if ((declaration instanceof IASTSimpleDeclaration) )				
		{
			final IASTSimpleDeclaration simple_declaration = (IASTSimpleDeclaration) declaration;
			IASTDeclSpecifier  decSpecifier = simple_declaration.getDeclSpecifier();
			if ((decSpecifier instanceof CPPASTCompositeTypeSpecifier ) )
			{	
				classCount++;
				CPPASTCompositeTypeSpecifier cppComptypeSpecifier= ((CPPASTCompositeTypeSpecifier) decSpecifier);
				IASTDeclaration[]  astDeclarationList = cppComptypeSpecifier.getMembers();
			    				
				for (IASTDeclaration declaration1 : astDeclarationList)
				{
					if (declaration1 instanceof IASTSimpleDeclaration)
					{
						final IASTDeclarator[] declaratorList = ((IASTSimpleDeclaration) declaration1).getDeclarators();
						for (IASTDeclarator declarator : declaratorList)
						{
							if (declarator instanceof IASTFunctionDeclarator)
							{
								methodCount++;			
								
							}
						}
					}
				}				

			}			

		}
		else if ((declaration instanceof IASTFunctionDefinition ) )
		{
			
			methodCount++;	
		}		
		return false;
	}

	
	/**
	 * visitSourceCodeforCalFileComplexity(IASTDeclaration declaration) method is responsible
	 * for visit functional statement
	 * 
	 * @param     	[in] IASTDeclaration declaration 
	 * @return		[out] boolean
	 * @warning		[None]
	 * @exception	IO exception
	 */
	static boolean visitSourceCodeforCalFileComplexity(IASTDeclaration declaration) {
		String MethodName="";
		count=0;
		if (declaration instanceof IASTFunctionDefinition )
			{
			final IASTFunctionDefinition Func_Definition = (IASTFunctionDefinition) declaration;
			final IASTFunctionDeclarator Func_Declarator = Func_Definition.getDeclarator();
			  IASTStatement  iastStatement =((IASTFunctionDefinition) declaration).getBody();
			  MethodName =Func_Declarator.getName().toString();
			  visitCompoundStatement(iastStatement);
			  int complexity =count+1;			  
			  mapSourceMatrices.put(MethodName, complexity);			
				  
			}
			
					
		return false;
	}

	/**
	 * visitCompoundStatement(IASTStatement iastStatement) method is responsible
	 * for visit functional statement
	 * 
	 * @param     	[in] IASTStatement iastStatement 
	 * @return		[out] void
	 * @warning		[None]
	 * @exception	IO exception
	 */
	private static void visitCompoundStatement(IASTStatement iastStatement) {
		if(iastStatement instanceof IASTCompoundStatement )
		  {
			  IASTStatement[]  arrayStatement = ((IASTCompoundStatement) iastStatement).getStatements();
			  for (IASTStatement statement : arrayStatement)
			  {
				  
				  if((statement instanceof IASTIfStatement)||(statement instanceof IASTForStatement)||(statement instanceof IASTSwitchStatement)||
						  (statement instanceof IASTWhileStatement)|| (statement instanceof IASTCaseStatement)|| (statement instanceof IASTDoStatement)|| (statement instanceof IASTCompoundStatement))
				  {
					 
					  count++;
					  if(statement instanceof IASTIfStatement)
					  {							  
						  final IASTIfStatement if_statement = (IASTIfStatement) statement; 						 						  
						  visitElseIfStatement(if_statement);
					  }					  
					  else if (statement instanceof IASTForStatement)
						{
							final IASTForStatement for_statement = (IASTForStatement) statement;
							final IASTStatement for_body = for_statement.getBody();
							if (for_body instanceof IASTCompoundStatement)
							{
								visitCompoundStatement(for_body);
							}
							
							
						}
						else if (statement instanceof IASTWhileStatement)
						{
							final IASTWhileStatement while_statement = (IASTWhileStatement) statement;
							final IASTStatement while_body = while_statement.getBody();
							if (while_body instanceof IASTCompoundStatement)
							{
								visitCompoundStatement(while_body);
							}				
							
						}
						else if (statement instanceof IASTDoStatement)
						{
							final IASTDoStatement do_statement = (IASTDoStatement) statement;
							final IASTStatement do_body = do_statement.getBody();
							if (do_body instanceof IASTCompoundStatement)
							{
								visitCompoundStatement(do_body);
							}
						}
						else if (statement instanceof IASTSwitchStatement)
						{
							final IASTSwitchStatement switch_statement = (IASTSwitchStatement) statement;							
							
							final IASTStatement switch_body = switch_statement.getBody();							
														
							if ((switch_body instanceof IASTCompoundStatement ))
							{																
								 visitCompoundStatement(switch_body);
							}
						}
						
				  }	
				    
			  }  
			 
		  }
		  else if(iastStatement instanceof CPPASTTryBlockStatement)
		  {
			  count++;
			  final CPPASTTryBlockStatement try_statement = (CPPASTTryBlockStatement) iastStatement;
			 IASTStatement astStatement =try_statement.getTryBody();
			  if(astStatement instanceof IASTCompoundStatement)
			  {
				  visitCompoundStatement(astStatement);
			  }
			 
		  }
		  else if(iastStatement instanceof CPPASTCatchHandler)
		  {
			  count++;
			  final CPPASTCatchHandler try_statement = (CPPASTCatchHandler) iastStatement;
				 IASTStatement astStatement =try_statement.getCatchBody();
				  if(astStatement instanceof IASTCompoundStatement)
				  {
					  visitCompoundStatement(astStatement);
				  }
			 
		  }
	}

	/**
	 * @param if_statement
	 */
	private static void visitElseIfStatement(final IASTIfStatement if_statement) {
		final IASTStatement else_clause = if_statement.getElseClause();
		final IASTStatement than_clause = if_statement.getThenClause();
		 if(than_clause !=null)
		  {				 
			  //count++;
			  if(than_clause instanceof IASTCompoundStatement)
			  {
				  	
				  visitCompoundStatement(than_clause);
			  }
			  else if(than_clause instanceof IASTIfStatement)
			  {
				  count++;
				  visitElseIfStatement((IASTIfStatement)than_clause);
			  }
		  }
		
		
		  if(else_clause !=null)
		  {				 
			  //count++;
			  if(else_clause instanceof IASTCompoundStatement)
			  {
				  	
				  visitCompoundStatement(else_clause);
			  }
			  else if(else_clause instanceof IASTIfStatement)
			  {
				  count++;
				  visitElseIfStatement((IASTIfStatement)else_clause);
			  }
		  }
	}

	
	/**
	 * createSourceFileFromFileContent(String fileContents) method is responsible
	 * Create source file from file content
	 * 
	 * @param     	[in] String fileContents
	 * @return		[out] String
	 * @throws IOException 
	 * @warning		[None]
	 * @exception	IO exception
	 */
	 static String createSourceFileFromFileContent(String fileContents) throws IOException {
		
		 String current = System.getProperty("user.dir");
		 String FilePath= "C:\\DexterTempFile.cpp";	
		 if(!current.isEmpty())
		 {
			 FilePath =current+File.separator+"DexterTempFile.cpp";
		 }		 
		 FileOutputStream fout=null;	
		try
		{		   
			fout = new FileOutputStream (new File(FilePath));			
			fileContents.getBytes();			
			byte by[] = fileContents.getBytes(); 
			fout.write(by);					
		} 
		catch (IOException ex) 
		{			
			throw new DexterRuntimeException(ex.getMessage());			
		} 
		finally
		{
			if(fout !=null)
			{
				fout.close(); 		
				fout =null;
			}
		}
		return FilePath;
	}

	 
	 /**
		 * DeleteTempFile(String sourceFilePath) method is responsible
		 * delete content file
		 * 
		 * @param     	[in] String fileContents
		 * @return		[out] String
		 * @warning		[None]
		 * @exception	IO exception
		 */
	 static void deleteTempFile(String sourceFilePath) {
			File tempfile =new File(sourceFilePath);
			if(tempfile.exists())
			{
				tempfile.delete();
			}
		}
	
	
}
