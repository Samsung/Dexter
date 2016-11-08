package com.samsung.sec.dexter.vdcpp.checkerlogic;

import org.eclipse.cdt.core.dom.ast.IASTTranslationUnit;

import com.samsung.sec.dexter.core.analyzer.AnalysisConfig;
import com.samsung.sec.dexter.core.analyzer.AnalysisResult;
import com.samsung.sec.dexter.core.checker.IChecker;

public interface ICheckerLogic {
	public void analyze(final AnalysisConfig config, final AnalysisResult result, 
			final IChecker checker, final IASTTranslationUnit unit);
}
