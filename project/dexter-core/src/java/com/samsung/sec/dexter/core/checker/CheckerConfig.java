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
package com.samsung.sec.dexter.core.checker;

import com.samsung.sec.dexter.core.config.DexterConfig.LANGUAGE;
import com.samsung.sec.dexter.core.exception.DexterException;
import com.samsung.sec.dexter.core.exception.DexterRuntimeException;

import java.util.ArrayList;
import java.util.List;

import org.apache.log4j.Logger;

public class CheckerConfig {
    private List<Checker> checkerList = new ArrayList<Checker>(100);
    private String toolName;
    private LANGUAGE language;

    final static Logger logger = Logger.getLogger(CheckerConfig.class);

    public CheckerConfig(final String toolName, final LANGUAGE language) {
        this.toolName = toolName;
        this.language = language;
    }

    /**
     * @return the checkerSetList
     */

    /**
     * @return the checkerPropertyTypeList
     */

    /**
     * @param checkerSetList
     * the checkerSetList to set
     */

    /**
     * @param string
     * @param string2
     * void
     */

    /**
     * @param string
     * @param string2
     * void
     */

    /**
     * @return the toolName
     */
    public String getToolName() {
        return toolName;
    }

    /**
     * @return the language
     */
    public LANGUAGE getLanguage() {
        return language;
    }

    /**
     * @param toolName
     * the toolName to set
     */
    public void setToolName(final String toolName) {
        this.toolName = toolName;
    }

    /**
     * @param language
     * the language to set
     */
    public void setLanguage(final LANGUAGE language) {
        this.language = language;
    }

    /**
     * @param value
     * @return boolean
     */
    public boolean hasChecker(final String checkerCode) {
        for (IChecker checker : checkerList) {
            if (checker.getCode().equals(checkerCode)) {
                return true;
            }
        }

        return false;
    }

    public void addChecker(Checker checker) {
        if (!checkerList.contains(checker)) {
            checkerList.add(checker);
        }
    }

    /**
     * 
     * @param checkerCode
     * @return return false, if checker in inactive or not exist
     */
    public boolean isActiveChecker(final String checkerCode) {
        IChecker checker = null;
        try {
            checker = getChecker(checkerCode);
            return checker.isActive();
        } catch (DexterRuntimeException e) {
            logger.error(e.getMessage(), e);
            return false;
        }
    }

    /**
     * @param checkerCode
     * @return
     * @throws DexterException
     * if there is no Checker Object
     */
    public IChecker getChecker(String checkerCode) {
        for (IChecker checker : checkerList) {
            if (checker.getCode().equals(checkerCode)) {
                return checker;
            }
        }

        return new EmptyChecker();
    }

    public List<Checker> getCheckerList() {
        return this.checkerList;
    }

    /**
     * @param checkerCode
     * @return if checker does not exist, return "UNKNOWN"
     */
    public String getCheckerSeverity(String checkerCode) {
        IChecker checker = null;
        try {
            checker = getChecker(checkerCode);
            return checker.getSeverityCode();
        } catch (DexterRuntimeException e) {
            logger.error(e.getMessage(), e);
            return "UNKNOWN";
        }
    }
}
