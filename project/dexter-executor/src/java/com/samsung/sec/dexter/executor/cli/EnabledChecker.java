package com.samsung.sec.dexter.executor.cli;

import com.google.common.base.Strings;
import com.samsung.sec.dexter.core.util.DexterUtil;

public class EnabledChecker {
    private String toolName;
    private String language;
    private String code;

    public EnabledChecker(final String toolName, final String language, final String code) {
        this.toolName = DexterUtil.getStringOrEmptyString(toolName);
        this.language = DexterUtil.getStringOrEmptyString(language);
        this.code = DexterUtil.getStringOrEmptyString(code);
    }

    public String getCode() {
        return code;
    }

    public void setCode(String enabledCheckerCode) {
        this.code = enabledCheckerCode.trim();
    }

    public String getLanguage() {
        return language;
    }

    public void setLanguage(String enabledCheckerLanguage) {
        this.language = enabledCheckerLanguage.trim();
    }

    public String getToolName() {
        return toolName;
    }

    public void setToolName(String enabledCheckerToolName) {
        this.toolName = enabledCheckerToolName.trim();
    }

    public boolean isSameChecker(final String toolName, final String language, String code) {
        if (this.code.equalsIgnoreCase(code.trim()) == false)
            return false;

        if (Strings.isNullOrEmpty(this.toolName) == false && this.toolName.equals(toolName.trim()) == false) {
            return false;
        }

        if (Strings.isNullOrEmpty(this.language) == false && this.language.equals(language.trim()) == false) {
            return false;
        }

        return true;
    }
}
