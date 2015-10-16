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
package com.samsung.sec.dexter.opensource.plugin;

import java.io.InputStreamReader;
import java.io.Reader;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.regex.PatternSyntaxException;

import net.xeoh.plugins.base.annotations.PluginImplementation;

import org.apache.log4j.Logger;

import com.google.gson.Gson;
import com.samsung.sec.dexter.core.analyzer.AnalysisConfig;
import com.samsung.sec.dexter.core.analyzer.AnalysisEntityFactory;
import com.samsung.sec.dexter.core.analyzer.AnalysisResult;
import com.samsung.sec.dexter.core.analyzer.IAnalysisEntityFactory;
import com.samsung.sec.dexter.core.checker.Checker;
import com.samsung.sec.dexter.core.checker.CheckerConfig;
import com.samsung.sec.dexter.core.config.DexterConfig;
import com.samsung.sec.dexter.core.config.DexterConfig.LANGUAGE;
import com.samsung.sec.dexter.core.defect.Defect;
import com.samsung.sec.dexter.core.defect.Occurence;
import com.samsung.sec.dexter.core.exception.DexterRuntimeException;
import com.samsung.sec.dexter.core.plugin.IDexterPlugin;
import com.samsung.sec.dexter.core.plugin.PluginDescription;
import com.samsung.sec.dexter.core.plugin.PluginVersion;

@PluginImplementation
public class DexterOpensourcePlugin implements IDexterPlugin {
	private CheckerConfig checkerConfig;
	public final static String PLUGIN_NAME = "dexter-opensource";
	
	private final static Logger LOG = Logger.getLogger(DexterOpensourcePlugin.class);

	public DexterOpensourcePlugin() {
	}

	@Override
	public void init()  {
		initCheckerConfig();
	}
	
	protected synchronized void initCheckerConfig() {
		try{
			Reader reader = new InputStreamReader(this.getClass().getClassLoader().getResourceAsStream("checker-config.json"));
			
			Gson gson = new Gson();
			this.checkerConfig = gson.fromJson(reader, CheckerConfig.class);
		} catch (Exception e){
			throw new DexterRuntimeException(e.getMessage(), e);
		}
	}

	@Override
	public void destroy()  {
	}

	@Override
	public void handleDexterHomeChanged(String oldPath, String newPath)  {
	}

	@Override
	public PluginDescription getDexterPluginDescription() {
		return new PluginDescription("Samsung Electroincs", PLUGIN_NAME, 
				new  PluginVersion(0, 9, 1), 
				DexterConfig.LANGUAGE.ALL, 
				"");
	}

	@Override
	public void setCheckerConfig(CheckerConfig cc) {
		this.checkerConfig = cc;
	}

	@Override
	public CheckerConfig getCheckerConfig() {
		return this.checkerConfig;
	}

	@Override
	public AnalysisResult analyze(AnalysisConfig config)  {
		final String sourcecode = config.getSourcecodeThatReadIfNotExist();
		IAnalysisEntityFactory factory = new AnalysisEntityFactory();
		AnalysisResult result = factory.createAnalysisResult(config);
		
		for(Checker checker : this.checkerConfig.getCheckerList()){
			if (checker.isActive() == false) {
				continue;
			}
			
			final String regExp = checker.getProperty("RegExp");
			
			try {
				Pattern pattern = Pattern.compile(regExp);
				Matcher matcher = pattern.matcher(sourcecode);
				
				if(matcher.find()){
					Defect defect = new Defect();
					defect.setCheckerCode(checker.getCode());
					defect.setFileName(config.getFileName());
					defect.setModulePath(config.getModulePath());
					defect.setClassName("");
					defect.setMethodName("");
					defect.setLanguage(config.getLanguageEnum().toString());
					defect.setSeverityCode(checker.getSeverityCode());
					defect.setMessage(checker.getProperty("EnglishDescription"));
					defect.setToolName(PLUGIN_NAME);
					
					Occurence occ = new Occurence();
					occ.setStartLine(1);
					occ.setEndLine(1);
					occ.setStringValue(matcher.group());
					occ.setMessage(checker.getProperty("ShortDescription"));
					
					defect.addOccurence(occ);
					
					result.addDefect(defect);
				}
			} catch (PatternSyntaxException e){
				LOG.error("incorrect regexp: " + regExp);
				LOG.error(e.getMessage(), e);
			}
		}
		
		return result;
	}

	@Override
	public boolean supportLanguage(LANGUAGE language) {
		// support all languages
		return true;
	}

	@Override
    public String[] getSupportingFileExtensions() {
		return new String[] {"java", "c", "cpp", "h", "hpp", "cs", "js", "css", "html"};
    }
}
