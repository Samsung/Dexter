package com.samsung.sec.dexter.core.plugin;

import java.util.List;

import com.google.common.base.Strings;
import com.samsung.sec.dexter.core.analyzer.AnalysisConfig;
import com.samsung.sec.dexter.core.analyzer.AnalysisResult;
import com.samsung.sec.dexter.core.checker.CheckerConfig;
import com.samsung.sec.dexter.core.exception.DexterRuntimeException;

public interface IDexterPluginManager {

	List<AnalysisResult> analyze(final AnalysisConfig config);

	void initDexterPlugins() throws DexterRuntimeException;

	CheckerConfig getCheckerConfig(final String pluginName);

	void setCheckerConfig(final String pluginName, final CheckerConfig config);

}
