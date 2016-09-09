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
package com.samsung.sec.dexter.core.analyzer;

import com.samsung.sec.dexter.core.defect.Defect;
import com.samsung.sec.dexter.core.exception.DexterRuntimeException;
import com.samsung.sec.dexter.core.util.IDexterClient;

import java.util.ArrayList;
import java.util.List;

import org.apache.log4j.Logger;

public class DefaultAnalysisResultChangeHandler implements IAnalysisResultHandler {
    static Logger logger = Logger.getLogger(DefaultAnalysisResultChangeHandler.class);

    private int totalCnt = 0;
    private int criticalCnt = 0;
    private int majorCnt = 0;
    private int minorCnt = 0;
    private int crcCnt = 0;
    private int etcCnt = 0;

    @Override
    public void handleAnalysisResult(final List<AnalysisResult> resultList, final IDexterClient client) {
        try {
            if (client.isLogin() == false) {
                return;
            }

            List<Defect> allDefectList = new ArrayList<Defect>();

            for (AnalysisResult result : resultList) {
                client.sendAnalsysisResult(AnalysisResultFileManager.getInstance().getJson(result));
                allDefectList.addAll(result.getDefectList());
            }

            for (Defect defect : allDefectList) {
                totalCnt++;
                if ("CRI".equals(defect.getSeverityCode())) {
                    criticalCnt++;
                } else if ("MAJ".equals(defect.getSeverityCode())) {
                    majorCnt++;
                } else if ("MIN".equals(defect.getSeverityCode())) {
                    minorCnt++;
                } else if ("CRC".equals(defect.getSeverityCode())) {
                    crcCnt++;
                } else if ("ETC".equals(defect.getSeverityCode())) {
                    etcCnt++;
                }
            }
        } catch (DexterRuntimeException e) {
            logger.error(e.getMessage(), e);
            return;
        }
    }

    /**
     * @return the totalCnt
     */
    public int getTotalCnt() {
        return totalCnt;
    }

    /**
     * @return the criticalCnt
     */
    public int getCriticalCnt() {
        return criticalCnt;
    }

    /**
     * @return the majorCnt
     */
    public int getMajorCnt() {
        return majorCnt;
    }

    /**
     * @return the minorCnt
     */
    public int getMinorCnt() {
        return minorCnt;
    }

    /**
     * @return the crcCnt
     */
    public int getCrcCnt() {
        return crcCnt;
    }

    /**
     * @return the etcCnt
     */
    public int getEtcCnt() {
        return etcCnt;
    }

    @Override
    public void printLogAfterAnalyze() {}

    @Override
    public void handleBeginnigOfResultFile() {}

    @Override
    public void handleEndOfResultFile() {}
}
