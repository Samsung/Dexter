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
package com.samsung.sec.dexter.metrics;

import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;
import java.util.StringTokenizer;

import org.apache.log4j.Logger;

import com.google.common.base.Charsets;
import com.google.common.base.Strings;
import com.puppycrawl.tools.checkstyle.Checker;
import com.puppycrawl.tools.checkstyle.ConfigurationLoader;
import com.puppycrawl.tools.checkstyle.PropertiesExpander;
import com.puppycrawl.tools.checkstyle.api.AuditEvent;
import com.puppycrawl.tools.checkstyle.api.AuditListener;
import com.puppycrawl.tools.checkstyle.api.Check;
import com.puppycrawl.tools.checkstyle.api.CheckstyleException;
import com.puppycrawl.tools.checkstyle.api.Configuration;
import com.samsung.sec.dexter.core.config.DexterConfig;
import com.samsung.sec.dexter.core.metrics.CodeMetrics;
import com.samsung.sec.dexter.core.metrics.FunctionMetrics;
import com.samsung.sec.dexter.metrics.util.CdtUtil;
import com.samsung.sec.dexter.util.CppUtil;


public class CodeMetricsGenerator {
	static Logger logger = Logger.getLogger(CodeMetricsGenerator.class);
	
	public static void main(String[] args) throws IOException, CheckstyleException {
		final String filePath = "C:\\DEV\\workspace\\runtime-EclipseApplication\\DefectTest\\src\\defect\\example\\LockInversion.java";
		List<String> functionList = new ArrayList<String>();
		functionList.add("CSQLiteCursorCache");
		functionList.add("~CDbRow");
		getCodeMetrics(DexterConfig.LANGUAGE.JAVA, filePath, new CodeMetrics(), new FunctionMetrics(), functionList);
    }
	
	
	public static void getCodeMetrics(final DexterConfig.LANGUAGE language, final String filePath,
			final CodeMetrics codeMetrics, final FunctionMetrics functionMetrics, final List<String> functionList){
		if(Strings.isNullOrEmpty(filePath)){
			logger.error("there is no file to analyze for code metrics");
			return;
		}
		
		final File file = new File(filePath);
		
		if(file.exists() == false || file.isDirectory()){
			logger.error("there is no file : " + filePath);
			return;
		}
		
		if(language == DexterConfig.LANGUAGE.JAVA){
			useCheckStyle(filePath, new QualityAuditListener(codeMetrics));
			if(file.length() == 0){
				createDefaultMetrics(codeMetrics);
				createDefaultFunctionMetrics(functionMetrics);
			}
		} else if(language == DexterConfig.LANGUAGE.C || language == DexterConfig.LANGUAGE.CPP){
			codeMetrics.setMetrics(CppUtil.generatorCodeMetrics(filePath));
			if (functionList.size() > 0) {
				functionMetrics.setFunctionMetrics(CdtUtil.generatorCodeMetrics(filePath, functionList));
			}
			if(file.length() ==0 || codeMetrics.getMetrics() == null || codeMetrics.getMetrics().size() == 0){
				createDefaultMetrics(codeMetrics);
			}
			if(file.length()==0|| functionMetrics.getMetrics() == null || functionMetrics.getMetrics().size() == 0){
				createDefaultFunctionMetrics(functionMetrics);
			}
		} else {	// default values
			createDefaultMetrics(codeMetrics);
			createDefaultFunctionMetrics(functionMetrics);
		}
	}


	private static void createDefaultMetrics(final CodeMetrics codeMetrics) {
	    codeMetrics.addMetric("loc", 0);
	    codeMetrics.addMetric("sloc", 0);
	    codeMetrics.addMetric("maxComplexity", 0);
	    codeMetrics.addMetric("classCnt", 0);
	    codeMetrics.addMetric("methodCnt", 0);
	    codeMetrics.addMetric("minComplexity", 0);
	    codeMetrics.addMetric("averageCompexity", 0);
	    codeMetrics.addMetric("commentRatio", 0.00f);
    }
	
	private static void createDefaultFunctionMetrics(final FunctionMetrics functionMetrics){
		functionMetrics.addMetric("cc", 0);
		functionMetrics.addMetric("sloc", 0);
		functionMetrics.addMetric("callDepth", 0);
	}
	
	@SuppressWarnings("deprecation")
    private static void useCheckStyle(final String javaFilePath, final AuditListener listener) {
		final StringBuilder configString = new StringBuilder();
		configString.append("<!DOCTYPE module PUBLIC ")
			.append("\"-//Puppy Crawl//DTD Check Configuration 1.2//EN\" ")
		.append("\"http://www.puppycrawl.com/dtds/configuration_1_2.dtd\"> ")
		.append("<module name=\"Checker\">")
		.append("	<module name=\"TreeWalker\">")
		.append("		<module name=\"com.samsung.sec.dexter.metrics.CommentLineCheck\" />")
		.append("		<module name=\"com.samsung.sec.dexter.metrics.CyclometicComplexityCheck\"/>")
		.append("		<module name=\"com.samsung.sec.dexter.metrics.ClassAndMethodCountCheck\"/>")
		.append("	</module>")
		.append("</module>");
		
		final InputStream stream = new ByteArrayInputStream(configString.toString().getBytes(Charsets.UTF_8));
		Configuration configForDefect;
		Checker c = null;
        
		try {
			configForDefect = ConfigurationLoader.loadConfiguration(stream, new PropertiesExpander(System.getProperties()), true);
	        
			final List<File> files = new ArrayList<File>();
	        final File src = new File(javaFilePath);
	        
			files.add(src);
			
			c = new Checker();
			final ClassLoader moduleClassLoader = Check.class.getClassLoader();
			c.setModuleClassLoader(moduleClassLoader);
			c.configure(configForDefect);
			c.addListener(listener);
			c.process(files);
        } catch (CheckstyleException e) {
        	logger.error(e.getMessage(), e);
        } finally {
        	if(c != null){
        		c.destroy();
        	}
        	
       		try {
                stream.close();
            } catch (IOException e) {
            }
        }
	}
}

class QualityAuditListener implements AuditListener {
	private CodeMetrics codeMetrics = new CodeMetrics();
	
	public QualityAuditListener(final CodeMetrics codeMetrics){
		this.codeMetrics = codeMetrics;
	}
	
	public void addError(final AuditEvent ae) {
		final StringTokenizer st = new StringTokenizer(ae.getMessage(), ";");
		while (st.hasMoreTokens()) {
			final String v = st.nextToken();
			final int index = v.indexOf(':');
			if(Strings.isNullOrEmpty(v) == false && v.length() > 0 && index > 0){
				codeMetrics.addMetric(v.substring(0, index), v.substring(index + 1));
			}
		}
	}

	public void addException(AuditEvent arg0, Throwable arg1) {
	}

	public void auditFinished(final AuditEvent arg0) {
//		if (DexterConfig.getInstance().getRunMode() == DexterConfig.RunMode.CLI) {
//			final Gson gson = new Gson();
//			logger.info(":)   " + codeMetrics.getFileName() + " - Code Metrics Info:");
//			logger.info("\t\t" + gson.toJson(codeMetrics));
//		}
	}

	public void auditStarted(AuditEvent arg0) {
	}

	public void fileFinished(AuditEvent arg0) {
	}

	public void fileStarted(AuditEvent arg0) {
	}
};
