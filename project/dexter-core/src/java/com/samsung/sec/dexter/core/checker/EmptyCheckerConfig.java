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

import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.log4j.Logger;

import com.samsung.sec.dexter.core.config.DexterConfig.LANGUAGE;

public class EmptyCheckerConfig implements ICheckerConfig{
	 private transient Map<String, Checker> checkerMap = new HashMap<>();
	    private String toolName;
	    private LANGUAGE language;
	    private List<Checker> checkerList;

	    final static Logger logger = Logger.getLogger(EmptyCheckerConfig.class);

	    public EmptyCheckerConfig(final String toolName, final LANGUAGE language) {
	        this.toolName = toolName;
	        this.language = language;
	    }

	    public String getToolName() {
	        return toolName;
	    }

	    public LANGUAGE getLanguage() {
	        return language;
	    }

	    public void setToolName(final String toolName) {}

	    public void setLanguage(final LANGUAGE language) {}

	    public boolean hasChecker(final String checkerCode) {
	        return checkerMap.containsKey(checkerCode);
	    }

	    public void addChecker(Checker checker) {}

	    /**
	     * 
	     * @param checkerCode
	     * @return return false, if checker in inactive or not exist
	     */
	    public boolean isActiveChecker(final String checkerCode) {
	        return new EmptyChecker().isActive();
	    }

	    public IChecker getChecker(String checkerCode) {
	        return new EmptyChecker();
	    }

	    /**
	     * @param checkerCode
	     * @return if checker does not exist, return "UNKNOWN"
	     */
	    public String getCheckerSeverity(String checkerCode) {
	        return new EmptyChecker().getSeverityCode();
	    }

	    public void disableAllCheckers() {}

	    public void setCheckerActive(String checkerCode, boolean active) {}

	    public Collection<Checker> getCheckerList() {
	        return checkerMap.values();
	    }

	    public void checkerListToMap() {
	        if (checkerList == null)
	            return;

	        checkerMap = new HashMap<>(checkerList.size());
	        for (Checker checker : checkerList) {
	            checkerMap.put(checker.getCode(), checker);
	        }       

	        checkerList = null;
	    }
}
