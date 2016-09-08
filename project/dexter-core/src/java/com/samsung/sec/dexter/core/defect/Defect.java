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
package com.samsung.sec.dexter.core.defect;

import com.google.common.base.Objects;
import com.google.gson.Gson;
import com.samsung.sec.dexter.core.BaseDefect;
import com.samsung.sec.dexter.core.analyzer.ResultFileConstant;
import com.samsung.sec.dexter.core.exception.DexterException;
import com.samsung.sec.dexter.core.filter.DefectFilter;
import com.samsung.sec.dexter.core.util.DexterUtil;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

public class Defect extends BaseDefect {
    /** set by server module not by local */
    private long gdid = -1;
    private transient String localDid = "" + System.currentTimeMillis();
    private List<Occurence> occurences = new ArrayList<Occurence>();
    private long createdDateTime = -1;
    private String message = "";
    private String severityCode = "";
    private String categoryName = "";
    private long modifiedDateTime = -1;
    private String analysisType = "";

    public Defect() {
        createdDateTime = System.currentTimeMillis();
        modifiedDateTime = createdDateTime;
    }

    /*
     * (non-Javadoc)
     * 
     * @see java.lang.Object#toString()
     */
    @Override
    public String toString() {
        return Objects.toStringHelper(this).add("did", this.gdid)
                .add(checkerCode, checkerCode)
                .add(ResultFileConstant.TOOL_NAME, toolName)
                .add(ResultFileConstant.LANGUAGE, language)
                .add(ResultFileConstant.CLASS_NAME, className)
                .add(ResultFileConstant.FILE_NAME, getFileName())
                .add(ResultFileConstant.MODULE_PATH, getModulePath())
                .add("occ size", occurences.size())
                .add("AnalysisType", getAnalysisType())
                .add(ResultFileConstant.OCCURENCES, occurences).toString();
    }

    /*
     * (non-Javadoc)
     * 
     * @see java.lang.Object#equals(java.lang.Object)
     */
    @Override
    public boolean equals(final Object obj) {
        return super.equals(obj);
    }

    public boolean equalsWithPreOccurence(final Object obj) {
        if (obj == null) {
            return false;
        }

        return super.equals(obj);
    }

    /*
     * (non-Javadoc)
     * 
     * @see java.lang.Object#hashCode()
     */
    @Override
    public int hashCode() {
        return super.hashCode();
    }

    public String toJson() {
        Gson gson = new Gson();
        return gson.toJson(this);
    }

    public void addOccurence(final Occurence occurence) {
        assert this.occurences.contains(occurence) == false;

        String key = "[#" + (occurences.size() + 1) + "@" + occurence.getStartLine() + "] ";
        //		if(this.message.indexOf(key) < 0){
        //		}
        this.message += key + occurence.getMessage() + " ";
        occurences.add(occurence);
    }

    /**
     * @return Object
     */
    public Occurence getFirstOccurence() {
        final Iterator<Occurence> iter = this.occurences.iterator();
        if (iter.hasNext()) {
            return iter.next();
        }

        return null;
    }

    /**
     * @return the occurences
     */
    public List<Occurence> getOccurences() {
        return occurences;
    }

    /**
     * @param occurences
     * the occurences to set
     */
    public void setOccurences(final List<Occurence> occurences) {
        this.occurences = occurences;
    }

    /**
     * @return the localDid
     */
    public String getLocalDid() {
        return localDid;
    }

    /**
     * @return the createdDateTime
     */
    public long getCreatedDateTime() {
        return createdDateTime;
    }

    /**
     * @return the message
     */
    public String getMessage() {
        return message;
    }

    /**
     * @return the lastModifiedTime
     */
    public long getLastModifiedTime() {
        return getModifiedDateTime();
    }

    /**
     * @return the modifiedDateTime
     */
    public long getModifiedDateTime() {
        return modifiedDateTime;
    }

    /**
     * @param createdDateTime
     * the createdDateTime to set
     */
    public void setCreatedDateTime(final long createdTime) {
        this.createdDateTime = createdTime;
    }

    /**
     * please use this carefully, because this field can be set automatically, when adding a occurrence.
     * 
     * @param message
     * the message to set
     */
    public void setMessage(final String message) {
        this.message = message;
    }

    /**
     * @param lastModifiedTime
     * the lastModifiedTime to set
     */
    public void setLastModifiedTime(final long lastModifiedTime) {
        setModifiedDateTime(lastModifiedTime);
    }

    /**
     * @param modifiedDateTime
     * the modifiedDateTime to set
     */
    public void setModifiedDateTime(final long lastModifiedTime) {
        this.modifiedDateTime = lastModifiedTime;
    }

    /**
     * @return the severity
     */
    public String getSeverityCode() {
        return this.severityCode;
    }

    /**
     * @param severity
     * the severity to set
     */
    public void setSeverityCode(final String severityCode) {
        this.severityCode = severityCode;
    }

    /**
     * @return the category
     */
    public String getCategoryName() {
        return this.categoryName;
    }

    /**
     * @param category
     * the severity to set
     */

    public void setCategoryName(final String categoryName) {
        this.categoryName = categoryName;
    }

    /**
     * @param currentUserNo
     * @return DefectFilter
     * @throws DexterException
     */
    public DefectFilter toDefectFilter() {
        final DefectFilter filter = new DefectFilter();
        filter.setActive(true);
        filter.setCheckerCode(checkerCode);
        filter.setClassName(className);
        filter.setCreatedDateTime(System.currentTimeMillis());

        filter.setToolName(toolName);
        filter.setLanguage(language);
        filter.setFileName(getFileName());
        filter.setMethodName(methodName);
        filter.setModulePath(getModulePath());

        return filter;
    }

    /**
     * @return the gdid
     */
    public long getGdid() {
        return gdid;
    }

    /**
     * @param gdid
     * the gdid to set
     */
    public void setGdid(final long gdid) {
        this.gdid = gdid;
    }

    /**
     * @param string
     * @return
     */
    public static Defect fromMap(final Map<String, Object> map) {
        final Defect defect = new Defect();
        defect.setCreatedDateTime(Double.doubleToLongBits((Double) map.get("createdDateTime")) / 1000);
        //defect.setMessage((String) map.get("message"));
        defect.setSeverityCode((String) map.get(ResultFileConstant.SEVERITY_CODE));
        defect.setCategoryName((String) map.get(ResultFileConstant.CATEGORY_NAME));

        defect.setModifiedDateTime(Double.doubleToLongBits((Double) map.get("modifiedDateTime")) / 1000);
        defect.setCheckerCode((String) map.get(ResultFileConstant.CHECKER_CODE));
        defect.setMethodName((String) map.get(ResultFileConstant.METHOD_NAME));
        defect.setToolName((String) map.get(ResultFileConstant.TOOL_NAME));
        defect.setLanguage((String) map.get(ResultFileConstant.LANGUAGE));
        defect.setFileName((String) map.get(ResultFileConstant.FILE_NAME));
        defect.setModulePath((String) map.get(ResultFileConstant.MODULE_PATH));
        defect.setClassName((String) map.get(ResultFileConstant.CLASS_NAME));

        @SuppressWarnings("unchecked")
        final List<Map<String, Object>> occMap = (List<Map<String, Object>>) map.get(ResultFileConstant.OCCURENCES);
        for (final Map<String, Object> om : occMap) {
            Occurence o = new Occurence();

            o.setCode((String) om.get("code"));
            o.setStartLine(DexterUtil.toInt((Double) om.get(ResultFileConstant.START_LINE)));
            o.setEndLine(DexterUtil.toInt((Double) om.get(ResultFileConstant.END_LINE)));
            o.setCharStart(DexterUtil.toInt((Double) om.get(ResultFileConstant.CHAR_START)));
            o.setCharEnd(DexterUtil.toInt((Double) om.get(ResultFileConstant.CHAR_END)));
            o.setVariableName((String) om.get(ResultFileConstant.VARIABLE_NAME));
            o.setStringValue((String) om.get(ResultFileConstant.STRING_VALUE));
            o.setFieldName((String) om.get(ResultFileConstant.FIELD_NAME));
            o.setMessage((String) om.get(ResultFileConstant.MESSAGE));

            defect.addOccurence(o);
        }

        return defect;
    }

    public String getOccurenceLines(String separator) {
        int size = this.occurences.size() * 500;
        StringBuilder lines;

        if (size < Integer.MAX_VALUE)
            lines = new StringBuilder(this.occurences.size() * 500);
        else
            lines = new StringBuilder(Integer.MAX_VALUE);

        for (Occurence o : this.occurences) {
            lines.append(o.getStartLine()).append(separator);
        }

        return lines.toString();
    }

    public String getShortDescription() {
        StringBuilder msg = new StringBuilder(1024);
        msg.append(this.checkerCode)
                .append(" / ").append(this.severityCode)
                .append(" (count:").append(this.occurences.size()).append(", ")
                .append(" lines: ").append(getOccurenceLines(" ")).append(")").append(this.message);
        return msg.toString();
    }

    public String getAnalysisType() {
        return analysisType;
    }

    public void setAnalysisType(String analysisType) {
        this.analysisType = analysisType;
    }
}
