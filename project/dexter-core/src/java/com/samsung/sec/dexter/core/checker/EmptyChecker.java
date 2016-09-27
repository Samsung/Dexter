package com.samsung.sec.dexter.core.checker;

import com.samsung.sec.dexter.core.plugin.PluginVersion;

import java.util.HashMap;
import java.util.Map;

public class EmptyChecker implements IChecker {
    @Override
    public String getType() {
        return "";
    }

    @Override
    public void setType(String type) {}

    @Override
    public void setCategoryName(String categoryName) {}

    @Override
    public String getCategoryName() {
        return "";
    }

    @Override
    public void setCwe(int cwe) {}

    @Override
    public void setDescription(StringBuilder description) {}

    @Override
    public int getCwe() {
        return 0;
    }

    @Override
    public void setCWE(int cwe) {}

    @Override
    public void addDescriptionWithNewLine(String string) {}

    @Override
    public void setSeverityCode(String severityCode) {}

    @Override
    public void setCode(String code) {}

    @Override
    public Map<String, String> getProperties() {
        return new HashMap<String, String>(0);
    }

    @Override
    public String getSeverityCode() {
        return "";
    }

    @Override
    public String getCode() {
        return "";
    }

    @Override
    public void setActive(boolean isActive) {}

    @Override
    public boolean isActive() {
        return false;
    }

    @Override
    public void addProperty(String key, String value) {}

    @Override
    public void setDescription(String description) {}

    @Override
    public void setVersion(PluginVersion version) {}

    @Override
    public void setName(String name) {}

    @Override
    public String getDescription() {
        return "";
    }

    @Override
    public PluginVersion getVersion() {
        return new PluginVersion("0.0.0");
    }

    @Override
    public String getName() {
        return "";
    }

    @Override
    public String getProperty(String key) {
        return "";
    }
}
