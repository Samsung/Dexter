package com.samsung.sec.dexter.executor;

import java.util.List;

import com.samsung.sec.dexter.core.analyzer.AnalysisConfig;
import com.samsung.sec.dexter.core.analyzer.AnalysisResult;
import com.samsung.sec.dexter.core.checker.CheckerConfig;
import com.samsung.sec.dexter.core.config.DexterConfig.LANGUAGE;
import com.samsung.sec.dexter.core.plugin.IDexterPlugin;
import com.samsung.sec.dexter.core.plugin.IDexterPluginInitializer;
import com.samsung.sec.dexter.core.plugin.PluginDescription;
import com.samsung.sec.dexter.core.plugin.PluginVersion;

public class DexterPluginInitializerMock implements IDexterPluginInitializer {

	@Override
	public void init(List<IDexterPlugin> pluginHandlerList) {
		IDexterPlugin mockPlugin = new IDexterPlugin(){
			private CheckerConfig checkerConfig = new CheckerConfig("dexter-findbugs", LANGUAGE.JAVA);
			
			@Override
			public void init() {
			}

			@Override
			public void destroy() {
			}

			@Override
			public void handleDexterHomeChanged(String oldPath, String newPath) {
			}

			@Override
			public PluginDescription getDexterPluginDescription() {
				return new PluginDescription("findbugs", "dexter-findbugs", new PluginVersion("1.0.0"), 
						LANGUAGE.JAVA, "plugin description");
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
			public AnalysisResult analyze(AnalysisConfig config) {
				return null;
			}

			@Override
			public boolean supportLanguage(LANGUAGE language) {
				if(language == LANGUAGE.JAVA)
					return true;
				return false;
			}

			@Override
			public String[] getSupportingFileExtensions() {
				return new String[]{".java"};
			}
		};
		
		pluginHandlerList.add(mockPlugin);
	}

}
