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
package com.samsung.sec.dexter.eclipse;

import com.samsung.sec.dexter.core.analyzer.AnalysisResult;
import com.samsung.sec.dexter.core.analyzer.IAnalysisResultHandler;
import com.samsung.sec.dexter.core.defect.Defect;
import com.samsung.sec.dexter.core.defect.Occurence;
import com.samsung.sec.dexter.core.filter.AnalysisFilterHandler;
import com.samsung.sec.dexter.core.util.IDexterClient;
import com.samsung.sec.dexter.eclipse.builder.DexterMarker;
import com.samsung.sec.dexter.executor.DexterAnalyzer;

import java.util.List;

import org.eclipse.core.resources.IFile;
import org.eclipse.swt.widgets.Display;

class AnalysisResultHandler implements IAnalysisResultHandler {
    private IFile targetFile;

    public AnalysisResultHandler(final IFile file) {
        this.targetFile = file;
    }

    /*
     * (non-Javadoc)
     * 
     * @see com.samsung.sec.dexter.core.analyzer.EndOfAnalysisHandler#
     * handleAnalysisResult(com.samsung.sec.dexter.core.analyzer.AnalysisResult)
     */
    @Override
    public void handleAnalysisResult(final List<AnalysisResult> resultList, final IDexterClient client) {
        if (Display.getCurrent() != null) {
            addDefectMarkers(resultList);
            return;
        }

        Display.getDefault().asyncExec(new Runnable() {
            @Override
            public void run() {
                addDefectMarkers(resultList);
            }
        });
    }

    private void addDefectMarkers(final List<AnalysisResult> resultList) {
        DexterMarker.deleteMarkers(targetFile);

        List<Defect> allDefectList = DexterAnalyzer.getAllDefectList(resultList);

        for (final Defect d : allDefectList) {
            boolean isDefectDissmissed = AnalysisFilterHandler.getInstance().isDefectDismissed(d);

            for (final Occurence o : d.getOccurences()) {
                DexterMarker.addMarker(targetFile, d, o, isDefectDissmissed);
            }
        }
    }

    @Override
    public void printLogAfterAnalyze() {}

    @Override
    public void handleBeginnigOfResultFile() {}

    @Override
    public void handleEndOfResultFile() {}
}