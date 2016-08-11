package com.samsung.sec.dexter.executor.cli;

import com.samsung.sec.dexter.core.analyzer.AnalysisResult;
import com.samsung.sec.dexter.core.analyzer.IAnalysisResultHandler;
import com.samsung.sec.dexter.core.util.IDexterClient;

import java.util.List;

public class EmptyAnalysisResultHandler implements IAnalysisResultHandler {

    @Override
    public void handleAnalysisResult(List<AnalysisResult> analysisResult, IDexterClient client) {}

    @Override
    public void printLogAfterAnalyze() {}
}
