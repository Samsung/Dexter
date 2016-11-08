package com.samsung.sec.dexter.core.checker;

import com.google.common.base.Strings;
import com.samsung.sec.dexter.core.plugin.PluginVersion;
import com.samsung.sec.dexter.core.util.DexterUtil;

import java.util.Map;

public interface IChecker {

    String getType();

    void setType(String type);

    void setCategoryName(String categoryName);

    String getCategoryName();

    void setCwe(final int cwe);

    void setDescription(final StringBuilder description);

    int getCwe();

    void setCWE(final int cwe);

    void addDescriptionWithNewLine(final String string);

    void setSeverityCode(final String severityCode);

    void setCode(final String code);

    Map<String, String> getProperties();

    String getSeverityCode();

    String getCode();

    void setActive(final boolean isActive);

    boolean isActive();

    void addProperty(String key, String value);

    void setDescription(final String description);

    void setVersion(final PluginVersion version);

    void setName(final String name);

    String getDescription();

    PluginVersion getVersion();

    String getName();

    String getProperty(final String key);

}
