/**
 * Copyright (c) 2014 Samsung Electronics, Inc.,
 * All rights reserved.
 * 
 * Redistribution and use in source and binary forms, with or without
 * modification, are permitted provided that the following conditions are met:
 * 
 * * Redistributions of source code must retain the above copyright notice, this
 * list of conditions and the following disclaimer.
 * 
 * * Redistributions in binary form must reproduce the above copyright notice,
 * this list of conditions and the following disclaimer in the documentation
 * and/or other materials provided with the distribution.
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
package com.samsung.sec.dexter.peerreview.plugin;

import org.apache.log4j.Logger;

import com.samsung.sec.dexter.core.analyzer.AnalysisConfig;
import com.samsung.sec.dexter.core.analyzer.AnalysisEntityFactory;
import com.samsung.sec.dexter.core.analyzer.AnalysisResult;
import com.samsung.sec.dexter.core.analyzer.IAnalysisEntityFactory;
import com.samsung.sec.dexter.core.checker.CheckerConfig;
import com.samsung.sec.dexter.core.config.DexterConfig;
import com.samsung.sec.dexter.core.config.DexterConfig.LANGUAGE;
import com.samsung.sec.dexter.core.plugin.IDexterPlugin;
import com.samsung.sec.dexter.core.plugin.PluginDescription;
import com.samsung.sec.dexter.core.plugin.PluginVersion;

import net.xeoh.plugins.base.annotations.PluginImplementation;

@PluginImplementation
public class DexterPeerReviewPlugIn implements IDexterPlugin {
    private final static Logger logger = Logger.getLogger(DexterPeerReviewPlugIn.class);
    
    public final static String PLUGIN_NAME = "peer-review";
    public final static PluginVersion PLUGIN_VERSION = new PluginVersion("0.0.1");
    private static PluginDescription PLUGIN_DESCRIPTION;
    
    private IAnalysisEntityFactory analysisEntityFactory = new AnalysisEntityFactory();

	public DexterPeerReviewPlugIn() {
		// TODO Auto-generated constructor stub
	}

	@Override
	public void init() {
		// TODO Auto-generated method stub

	}

	@Override
	public void destroy() {
		// TODO Auto-generated method stub

	}

	@Override
	public void handleDexterHomeChanged(String oldPath, String newPath) {
		// TODO Auto-generated method stub

	}

	@Override
	public PluginDescription getDexterPluginDescription() {
        if (PLUGIN_DESCRIPTION == null) {
            PLUGIN_DESCRIPTION = new PluginDescription("Samsung Electroincs", PLUGIN_NAME,
                    PLUGIN_VERSION,
                    DexterConfig.LANGUAGE.CPP,
                    "");
        }

        return PLUGIN_DESCRIPTION;
	}

	@Override
	public void setCheckerConfig(CheckerConfig cc) {
		// TODO Auto-generated method stub

	}

	@Override
	public CheckerConfig getCheckerConfig() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public AnalysisResult analyze(AnalysisConfig config) {
		logger.info("analyze called");
		
		AnalysisResult result = analysisEntityFactory.createAnalysisResult(config);
		return result;
	}

	@Override
	public boolean supportLanguage(LANGUAGE language) {
		if (language == LANGUAGE.C || language == LANGUAGE.CPP || language == LANGUAGE.JAVA) {
            return true;
        }
		
        return false;
	}

	@Override
	public String[] getSupportingFileExtensions() {
		return new String[] { "c", "cpp", "h", "hpp", "java" };
	}

}
