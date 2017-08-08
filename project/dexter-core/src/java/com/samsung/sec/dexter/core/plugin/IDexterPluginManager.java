package com.samsung.sec.dexter.core.plugin;

import com.samsung.sec.dexter.core.analyzer.AnalysisConfig;
import com.samsung.sec.dexter.core.analyzer.AnalysisResult;
import com.samsung.sec.dexter.core.checker.CheckerConfig;
import com.samsung.sec.dexter.core.checker.ICheckerConfig;
import com.samsung.sec.dexter.core.util.IDexterClient;

import java.util.List;

public interface IDexterPluginManager {

	List<AnalysisResult> analyze(final AnalysisConfig config);

	void initDexterPlugins();

	ICheckerConfig getCheckerConfig(final String pluginName);

	void setCheckerConfig(final String pluginName, final ICheckerConfig config);
	
	void setDexterClient(IDexterClient dexterClient);

}
