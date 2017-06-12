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
        return "UNKNOWN";
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
