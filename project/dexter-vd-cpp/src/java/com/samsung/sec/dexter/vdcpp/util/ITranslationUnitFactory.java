package com.samsung.sec.dexter.vdcpp.util;

import org.eclipse.cdt.core.dom.ast.IASTTranslationUnit;

import com.samsung.sec.dexter.core.analyzer.AnalysisConfig;

public interface ITranslationUnitFactory {
	public IASTTranslationUnit getASTTranslationUnit(AnalysisConfig config);
}
