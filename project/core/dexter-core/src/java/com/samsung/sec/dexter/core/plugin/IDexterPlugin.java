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
package com.samsung.sec.dexter.core.plugin;

import net.xeoh.plugins.base.Plugin;

import com.samsung.sec.dexter.core.analyzer.AnalysisConfig;
import com.samsung.sec.dexter.core.analyzer.AnalysisResult;
import com.samsung.sec.dexter.core.checker.CheckerConfig;
import com.samsung.sec.dexter.core.config.DexterConfig.LANGUAGE;
import com.samsung.sec.dexter.core.exception.DexterException;

public interface IDexterPlugin extends Plugin{
	/**
	 * Initialize Plug-in : configuration, loading, etc.
	 * called only once by SaPluginManager
	 * 
	 * @throws DexterException
	 * @return boolean
	 */
	public void init();
	
	/**
	 * When Dexter Main program is terminated or no need to use this plug-in,
	 * this method will be called.
	 * 
	 * @throws DexterException
	 * @return boolean
	 */
	public void destroy();
	
	/**
	 * When Dexter Home Path is changed, this method will be called.
	 * for example, if you have a dependency of Dexter Home,
	 * you have to handle this event  
	 * 
	 * @throws DexterException
	 * @return void
	 */
	public void handleDexterHomeChanged(final String oldPath, final String newPath);  
	
	/**
	 * return description of the plug-in
	 * @return PluginDescription
	 */
	public PluginDescription getDexterPluginDescription();
	
	/**
	 * set the default checkers' configuration.
	 * @param cc 
	 */
	public void setCheckerConfig(CheckerConfig cc);
	
	/**
	 * @return return current CheckerConfig object
	 * @throws DexterException 
	 */
	public CheckerConfig getCheckerConfig();
	
	/**
	 * run the static analysis with the plug-in
	 * if errors, result.getErrorCode() has under 0 values(-1, -2, ... : AnalysisResult.ERROR_#)
	 * if you need, use 'synchronized' keywords.
	 * 
	 * @param config if null, the plug-in will use default AnalsysiConfig which set by setAnalsysiConfig() method
	 * @param result after executing, the result object will be set
	 */
	public AnalysisResult analyze(final AnalysisConfig config);
	
    /**
     * check the plug-in whether support a specific language such as Java, C, C++
     * @param language DexterConfig.Language
     * @return if support, return true
     */
    public boolean supportLanguage(final LANGUAGE language);
    
    /**
     * return file extension array that the plug-in can analyze
     * 
     * @return String[]
     */
    public String[] getSupportingFileExtensions();
}
