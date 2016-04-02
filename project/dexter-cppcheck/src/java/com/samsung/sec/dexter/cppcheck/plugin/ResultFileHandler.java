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

package com.samsung.sec.dexter.cppcheck.plugin;

import java.util.Map;

import org.apache.log4j.Logger;
import org.xml.sax.Attributes;
import org.xml.sax.SAXException;
import org.xml.sax.helpers.DefaultHandler;

import com.google.common.base.Strings;
import com.samsung.sec.dexter.core.analyzer.AnalysisConfig;
import com.samsung.sec.dexter.core.analyzer.AnalysisResult;
import com.samsung.sec.dexter.core.analyzer.ResultFileConstant;
import com.samsung.sec.dexter.core.checker.Checker;
import com.samsung.sec.dexter.core.checker.CheckerConfig;
import com.samsung.sec.dexter.core.config.DexterConfig;
import com.samsung.sec.dexter.core.config.DexterConfig.LANGUAGE;
import com.samsung.sec.dexter.core.defect.PreOccurence;
import com.samsung.sec.dexter.core.exception.DexterRuntimeException;
import com.samsung.sec.dexter.core.plugin.PluginVersion;
import com.samsung.sec.dexter.core.util.DexterUtil;
import com.samsung.sec.dexter.util.CppUtil;

public class ResultFileHandler extends DefaultHandler {
	private PreOccurence currentOccurence;
	private AnalysisResult result;
	private AnalysisConfig config;
	private CheckerConfig checkerConfig;
	
	private final static Logger logger = Logger.getLogger(ResultFileHandler.class);
	
	public ResultFileHandler(final AnalysisResult result, final AnalysisConfig config, final CheckerConfig checkerConfig){
		this.config = config;
		this.checkerConfig = checkerConfig;
		this.result = result;
	}
	
	/* (non-Javadoc)
	 * @see org.xml.sax.helpers.DefaultHandler#startDocument()
	 */
	@Override
	public void startDocument() throws SAXException {
	    super.startDocument();
	}
	
	/* (non-Javadoc)
	 * @see org.xml.sax.helpers.DefaultHandler#endDocument()
	 */
	@Override
	public void endDocument() throws SAXException {
	    super.endDocument();
	}
	
	/* (non-Javadoc)
	 * @see org.xml.sax.helpers.DefaultHandler#startElement(java.lang.String, java.lang.String, java.lang.String, org.xml.sax.Attributes)
	 */
	@Override
	public void startElement(final String uri, final String localName, final String qName, final Attributes attributes) throws SAXException {
	    super.startElement(uri, localName, qName, attributes);
	    if("error".equals(qName)){
	    	final String checkerCode = attributes.getValue("id").toLowerCase();
	    	
	    	currentOccurence = new PreOccurence();
	    	currentOccurence.setLanguage(LANGUAGE.CPP.toString());
	    	if(checkerCode.startsWith(DexterConfig.SECURITY_CHECK_PREFIX)){
	    		currentOccurence.setMessage(attributes.getValue("msg").replace("&apos;", "'"));
	    	}	
	    	else{
	    		currentOccurence.setMessage(attributes.getValue("verbose").replace("&apos;", "'"));
	    	}
	    	currentOccurence.setToolName(CppcheckDexterPlugin.PLUGIN_NAME);
	    	currentOccurence.setFileName(config.getFileName());
	    	currentOccurence.setModulePath(config.getModulePath());

	    	currentOccurence.setCheckerCode(checkerCode);
	    	
	    	try{
	    		Checker checker = checkerConfig.getChecker(checkerCode);
	    		currentOccurence.setSeverityCode(checker.getSeverityCode());
	    		currentOccurence.setCategoryName(checker.getCategoryName());
	    		
	    	} catch (DexterRuntimeException e){
	    		logger.info(e.getMessage());
	    		Checker checker = new Checker(checkerCode, checkerCode, 
	    				PluginVersion.fromImplementationVersion(CppcheckDexterPlugin.class).getVersion(), true);
	    		
	    		if("true".equals(attributes.getValue("inconclusive"))){
	    			checker.setSeverityCode("ETC");
	    			checker.setActive(false);
	    		} else {
	    			setSeverityForNewChecker(attributes, checker);
	    		}
	    		checkerConfig.addChecker(checker);
	    		logger.info("Found new checker(" + checkerCode + ") in " + config.getSourceFileFullPath());
	    	}
	    } else if("location".equals(qName)){
	    	final String fileName = attributes.getValue("file");
	    	if(!result.getSourceFileFullPath().equals(DexterUtil.refinePath(fileName))){
	    		logger.debug("target file and defect detected file are not same");
	    		logger.debug("target file: " + result.getSourceFileFullPath());
	    		logger.debug("detected file: " + fileName);
	    		return;
	    	}
	    	
	    	currentOccurence.setStartLine(Integer.parseInt(attributes.getValue("line")));
	    	currentOccurence.setEndLine(Integer.parseInt(attributes.getValue("line")));
	    	currentOccurence.setCharStart(-1);
	    	currentOccurence.setCharEnd(-1);
	    	
	    	String locationMsg = attributes.getValue("msg");
	    	if(Strings.isNullOrEmpty(locationMsg) == false){
	    		currentOccurence.setMessage(currentOccurence.getMessage() + " " + localName);
	    	}
	    	
            Map<String, String> nameMap = CppUtil.extractModuleName(config.getSourceFileFullPath(), currentOccurence.getStartLine());
            if(Strings.isNullOrEmpty(nameMap.get(ResultFileConstant.CLASS_NAME)) == false){
            	currentOccurence.setClassName(nameMap.get(ResultFileConstant.CLASS_NAME));
            }
            if(Strings.isNullOrEmpty(nameMap.get(ResultFileConstant.METHOD_NAME)) == false){
            	currentOccurence.setMethodName(nameMap.get(ResultFileConstant.METHOD_NAME));
            }
	    }
	}

	private void setSeverityForNewChecker(final Attributes attributes, Checker checker) {
		// do not use new checker before reviewing in SE
		checker.setSeverityCode("ETC");
		/*
	    switch(attributes.getValue("severity")){
	    case "error" :
	    	checker.setSeverityCode("CRI");
	    	break;
	    case "warning" :
	    	checker.setSeverityCode("MAJ");
	    	break;
	    case "style":
	    case "performance":
	    case "portability":
	    	checker.setSeverityCode("MIN");
	    	break;
	    default :
	    	checker.setSeverityCode("ETC");
	    }
	    */
	}
	
	/* (non-Javadoc)
	 * @see org.xml.sax.helpers.DefaultHandler#endElement(java.lang.String, java.lang.String, java.lang.String)
	 */
	@Override
	public void endElement(final String uri, final String localName, final String qName) throws SAXException {
	    super.endElement(uri, localName, qName);
	    
	    if("error".equals(qName)){
	    	if(checkerConfig.isActiveChecker(currentOccurence.getCheckerCode()) == false){
	    		return;
	    	}
	    	
	    	System.out.println(currentOccurence.toJson());
	    	
	    	if (currentOccurence.getStartLine() != -1) {
	    		result.addDefectWithPreOccurence(currentOccurence);
			} else {
				logger.warn("Not added defect(start line is -1) : " + currentOccurence.toJson());
			}
	    } 
	}
}
