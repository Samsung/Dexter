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
package com.samsung.sec.dexter.executor;

import java.util.List;

import com.samsung.sec.dexter.core.analyzer.AnalysisConfig;
import com.samsung.sec.dexter.core.analyzer.AnalysisResult;

public class DexterAnalyzerAdapter implements IDexterAnalyzerListener {

	/* (non-Javadoc)
	 * @see com.samsung.sec.dexter.executor.IDexterAnalyzerListener#handlePreSendSourceCode(com.samsung.sec.dexter.core.analyzer.AnalysisConfig, com.samsung.sec.dexter.core.analyzer.AnalysisResult)
	 */
    @Override
    public void handlePreSendSourceCode(final AnalysisConfig config) {
    }

	/* (non-Javadoc)
	 * @see com.samsung.sec.dexter.executor.IDexterAnalyzerListener#handlePostSendSourceCode(com.samsung.sec.dexter.core.analyzer.AnalysisConfig, com.samsung.sec.dexter.core.analyzer.AnalysisResult)
	 */
    @Override
    public void handlePostSendSourceCode(final AnalysisConfig config) {
    }

	/* (non-Javadoc)
	 * @see com.samsung.sec.dexter.executor.IDexterAnalyzerListener#handlePreRunCodeMetrics(com.samsung.sec.dexter.core.analyzer.AnalysisConfig, com.samsung.sec.dexter.core.analyzer.AnalysisResult)
	 */
    @Override
    public void handlePreRunCodeMetrics(final AnalysisConfig config) {
    }

	/* (non-Javadoc)
	 * @see com.samsung.sec.dexter.executor.IDexterAnalyzerListener#handlePostRunCodeMetrics(com.samsung.sec.dexter.core.analyzer.AnalysisConfig, com.samsung.sec.dexter.core.analyzer.AnalysisResult)
	 */
    @Override
    public void handlePostRunCodeMetrics(final AnalysisConfig config) {
    }

	/* (non-Javadoc)
	 * @see com.samsung.sec.dexter.executor.IDexterAnalyzerListener#handlePreRunStaticAnalysis(com.samsung.sec.dexter.core.analyzer.AnalysisConfig, com.samsung.sec.dexter.core.analyzer.AnalysisResult)
	 */
    @Override
    public void handlePreRunStaticAnalysis(final AnalysisConfig config) {
    }

	/* (non-Javadoc)
	 * @see com.samsung.sec.dexter.executor.IDexterAnalyzerListener#handlePostRunStaticAnalysis(com.samsung.sec.dexter.core.analyzer.AnalysisConfig, com.samsung.sec.dexter.core.analyzer.AnalysisResult)
	 */
    @Override
    public void handlePostRunStaticAnalysis(final AnalysisConfig config, final List<AnalysisResult> resultList) {
    }
}
