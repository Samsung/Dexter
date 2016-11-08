/**
 * Copyright (c) 2016 Samsung Electronics, Inc.,
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
package com.samsung.sec.dexter.core.util;

import java.util.ArrayList;
import java.util.List;

import org.apache.log4j.Logger;

import com.google.common.base.Strings;
import com.samsung.sec.dexter.core.checker.CheckerConfig;
import com.samsung.sec.dexter.core.config.DefectGroup;
import com.samsung.sec.dexter.core.config.DexterCode;
import com.samsung.sec.dexter.core.config.DexterConfig;
import com.samsung.sec.dexter.core.config.DexterConfig.LANGUAGE;
import com.samsung.sec.dexter.core.defect.Defect;
import com.samsung.sec.dexter.core.filter.EmptyFalseAlarmConfiguration;
import com.samsung.sec.dexter.core.filter.IFalseAlarmConfiguration;
import com.samsung.sec.dexter.core.plugin.IDexterPlugin;

public class EmptyDexterClient implements IDexterClient {
    private final static Logger LOG = Logger.getLogger(EmptyDexterClient.class);
    private final static String HTTP_PREFIX = "http://";

    @Override
    public String getDexterDashboardUrl() {
        return "";
    }

    @Override
    public String getDexterWebUrl() {
        return "";
    }

    @Override
    public boolean isCurrentUserAdmin() {
        return false;
    }

    @Override
    public int getCurrentUserNo() {
        return 0;
    }

    @Override
    public void setCurrentUserNo(int userNo) {}

    @Override
    public int getServerPort() {
        return 0;
    }

    @Override
    public String getServerHost() {
        return "";
    }

    @Override
    public String getCurrentUserPwd() {
        return "";
    }

    @Override
    public String getSourceCode(String modulePath, String fileName) {
        return "";
    }

    @Override
    public List<DexterCode> getCodes(String codeKey) {
        return new ArrayList<DexterCode>(0);
    }

    @Override
    public List<DefectGroup> getDefectGroupList() {
        return new ArrayList<DefectGroup>(0);
    }

    @Override
    public boolean deleteDefectGroup(long defectGroupId) {
        return false;
    }

    @Override
    public boolean updateDefectGroup(DefectGroup defectGroup) {
        return false;
    }

    @Override
    public void insertDefectGroup(DefectGroup defectGroup) {}

    @Override
    public List<DefectGroup> getDefectGroupByGroupName(String groupName) {
        return new ArrayList<DefectGroup>(0);
    }

    @Override
    public void deleteDefects(String modulePath, String fileName) {}

    @Override
    public long getGlobalDid(Defect defect) {
        return 0;
    }

    @Override
    public IFalseAlarmConfiguration getFalseAlarmTree() {
        return new EmptyFalseAlarmConfiguration();
    }

    @Override
    public int getLastFalseAlarmVersion() {
        return 0;
    }

    @Override
    public void insertDefectFilter(Defect defect) {}

    @Override
    public void removeDefectFilter(Defect defect) {}

    @Override
    public void changeDefectStatus(Defect defect, String status) {}

    @Override
    public void createAccount(String id, String pwd, boolean isAdmin) {}

    @Override
    public boolean hasAccount(String id) {
        return false;
    }

    @Override
    public boolean isServerAlive() {
        return false;
    }

    @Override
    public String getCurrentUserId() {
        return "";
    }

    @Override
    public boolean isLogin() {
        return false;
    }

    @Override
    public void insertSourceCode(long snapshotId, long defectGroupId, String modulePath, String fileName,
            String sourceCode) {}

    @Override
    public void sendAnalsysisResult(String resultJson) {}

    @Override
    public void login() {}

    @Override
    public void login(String id, String pwd) {}

    @Override
    public void setWebResource(IDexterWebResource resource) {}

    @Override
    public void setCurrentUserAdmin(boolean isAdmin) {}

    @Override
    public void setLogin(boolean b) {}

    @Override
    public String getDexterPluginUpdateUrl() {
        return "";
    }

    @Override
    public CheckerConfig getDexterPluginChecker(IDexterPlugin plugin, String pluginName) {
        return new CheckerConfig("empty checker config", LANGUAGE.UNKNOWN);
    }

    @Override
    public void handleWhenNotStandaloneMode(DexterServerConfig serverConfig) {}

    @Override
    public void handleWhenStandaloneMode() {}

    @Override
    public String getDexterCodeMetricsUrl() {
        return "";
    }

    @Override
    public String getDexterFunctionMetricsUrl() {
        return "";
    }

    @Override
    public boolean hasSupportedHelpHtmlFile(StringBuilder url) {
        return false;
    }

    void setWebResource(DummyDexterWebResource webResource) {}

    @Override
    public boolean isServerAlive(String serverAddress) {
        assert Strings.isNullOrEmpty(serverAddress) == false;

        try {
            final String text = JerseyDexterWebResource.getTextWithoutLogin(HTTP_PREFIX + serverAddress + DexterConfig.CHECK_SERVER_ADDRESS);
            return "ok".equals(text);
        } catch (Exception e) {
            LOG.debug(e.getMessage(), e);
            return false;
        }
    }

    @Override
    public void insertSourceCodeCharSequence(long snapshotId, long defectGroupId, String modulePath, String fileName,
            CharSequence sourceCode) {
        // TODO Auto-generated method stub

    }
}
