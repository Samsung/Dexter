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
package com.samsung.sec.dexter.core.util;

import com.google.common.base.Strings;
import com.google.common.reflect.TypeToken;
import com.google.gson.Gson;
import com.samsung.sec.dexter.core.analyzer.ResultFileConstant;
import com.samsung.sec.dexter.core.checker.CheckerConfig;
import com.samsung.sec.dexter.core.config.DefectGroup;
import com.samsung.sec.dexter.core.config.DexterCode;
import com.samsung.sec.dexter.core.config.DexterConfig;
import com.samsung.sec.dexter.core.config.IDexterStandaloneListener;
import com.samsung.sec.dexter.core.defect.Defect;
import com.samsung.sec.dexter.core.exception.DexterException;
import com.samsung.sec.dexter.core.exception.DexterRuntimeException;
import com.samsung.sec.dexter.core.filter.DefectFilter;
import com.samsung.sec.dexter.core.filter.FalseAlarmConfigurationTree;
import com.samsung.sec.dexter.core.filter.IFalseAlarmConfiguration;
import com.samsung.sec.dexter.core.plugin.IDexterPlugin;
import com.sun.jersey.api.client.UniformInterfaceException;

import java.io.IOException;
import java.security.cert.CertificateException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.StringTokenizer;

import org.apache.log4j.Logger;

public class DexterClient implements IDexterClient, IDexterStandaloneListener {
    private final static Logger LOG = Logger.getLogger(DexterClient.class);

	private boolean isLogin;
    private int currentUserNo;
    private boolean isCurrentUserAdmin;
    private IDexterWebResource webResource;

    public DexterClient(final IDexterWebResource webResource) {
        this.webResource = webResource;
    }

    /** @return the isLogin */
    @Override
    public boolean isLogin() {
        return isLogin;
    }

    /** @return the currentUserId */
    @Override
    public String getCurrentUserId() {
        return webResource.getCurrentUserId();
    }

    private boolean isResultOk(final String resultText) {
        final Map<String, String> result = getResultMap(resultText);

        if ("ok".equals(result.get("result")) || "ok".equals(result.get("status"))) {
            return true;
        } else {
            LOG.debug("DexterClient Error: " + result.get("errorMessage") + "\n" + resultText);
            return false;
        }
    }

    private void checkResultOk(final String resultText) {
        final Map<String, String> result = getResultMap(resultText);
        if (!"ok".equals(result.get("result")) && !"ok".equals(result.get("status"))) {
            DexterUtil.dumpAllStackTracesForCurrentThread(LOG);
            throw new DexterRuntimeException(result.get("errorMessage") + "\nRESULT: " + resultText);
        }
    }

    private Map<String, String> getResultMap(final String text) {
        final Gson gson = new Gson();
        @SuppressWarnings("unchecked")
        final Map<String, String> result = gson.fromJson(text, Map.class);
        return result;
    }

    /**
     * login to Dexter Server
     * 
     * @precondition : both serverHost and serverPort fields are set and correct
     * @param id
     * @param pwd
     * @return boolean
     */
    @Override
    public void login(final String id, final String pwd) {
        assert Strings.isNullOrEmpty(id) == false;
        assert Strings.isNullOrEmpty(pwd) == false;

        String text = webResource.getText(DexterConfig.CHECK_ACCOUNT, id, pwd);

        final Gson gson = new Gson();
        @SuppressWarnings("unchecked")
        final Map<String, Object> result = gson.fromJson(text, Map.class);

        if ("ok".equals(result.get("result"))) {
            this.isLogin = true;
            this.currentUserNo = ((Double) result.get("userNo")).intValue();
            this.isCurrentUserAdmin = "true".equals(result.get("isAdmin").toString());
        } else {
            this.isLogin = false;
            throw new DexterRuntimeException("ID or Password is invalid.");
        }
    }

    /** @return boolean */
    @Override
    public void login() {
        assert Strings.isNullOrEmpty(webResource.getCurrentUserId()) == false;
        assert Strings.isNullOrEmpty(webResource.getCurrentUserPassword()) == false;

        try {
            login(webResource.getCurrentUserId(), webResource.getCurrentUserPassword());
            LOG.debug("Log-in successfully : " + webResource.getCurrentUserId());
        } catch (DexterRuntimeException e) {
            LOG.debug(e.getMessage(), e);
        }
    }

    /**
     * @param resultJson
     * @throws DexterException
     */
    @Override
    public void sendAnalsysisResult(final String resultJson) {
        if (Strings.isNullOrEmpty(resultJson)) {
            throw new DexterRuntimeException("The result file has no content to send");
        }

        if (isLogin == false) {
            throw new DexterRuntimeException(
                    "You are not log-in or your account(" + webResource.getCurrentUserId() + ") doesn't exist in Dexter Server.");
        }

        String resultText = "";
        Map<String, Object> body = new HashMap<String, Object>();
        body.put("result", resultJson);

        if (DexterConfig.getInstance().getRunMode() == DexterConfig.RunMode.CLI) {
            Gson gson = new Gson();
            String jsonBody = gson.toJson(body);
            resultText = webResource.postWithBodyforCLI(DexterConfig.PUT_ANALYSIS_RESULT_V3,
            		webResource.getCurrentUserId(), webResource.getCurrentUserPassword(), jsonBody);
        } else {
            resultText = webResource.postWithBody(DexterConfig.PUT_ANALYSIS_RESULT_V3,
            		webResource.getCurrentUserId(), webResource.getCurrentUserPassword(), body);
        }
        checkResultOk(resultText);
    }

    /**
     * @param resultJson
     * void
     * @throws CertificateException
     */
    @Override
    public void insertSourceCode(final long snapshotId, final long defectGroupId, final String modulePath,
            final String fileName, final String sourceCode) {
        assert Strings.isNullOrEmpty(fileName) == false;

        if (Strings.isNullOrEmpty(sourceCode)) {
            throw new DexterRuntimeException("Invalide Parameter of sourceCode: null or empty.  fileName:" + fileName);
        }

        final Map<String, Object> body = new HashMap<String, Object>();
        body.put(ResultFileConstant.SNAPSHOT_ID, snapshotId);
        body.put(ResultFileConstant.GROUP_ID, defectGroupId);
        body.put(ResultFileConstant.MODULE_PATH, modulePath);
        body.put(ResultFileConstant.FILE_NAME, fileName);

        body.put(ResultFileConstant.SOURCE_CODE, DexterUtil.getBase64CharSequence(sourceCode));

        String resultText = "";

        if (DexterConfig.getInstance().getRunMode().equals(DexterConfig.RunMode.CLI)) {
            Gson gson = new Gson();
            String jsonBody = gson.toJson(body);
            resultText = webResource.postWithBodyforCLI(DexterConfig.POST_SNAPSHOT_SOURCECODE,
            		webResource.getCurrentUserId(), webResource.getCurrentUserPassword(), jsonBody);
        } else {
            resultText = webResource.postWithBody(DexterConfig.POST_SNAPSHOT_SOURCECODE,
            		webResource.getCurrentUserId(), webResource.getCurrentUserPassword(), body);
        }

        checkResultOk(resultText);
    }

    @Override
    public void insertSourceCodeCharSequence(final long snapshotId, final long defectGroupId, final String modulePath,
            final String fileName, final CharSequence sourceCode) {
        assert Strings.isNullOrEmpty(fileName) == false;

        final Map<String, Object> body = new HashMap<String, Object>();
        body.put(ResultFileConstant.SNAPSHOT_ID, snapshotId);
        body.put(ResultFileConstant.GROUP_ID, defectGroupId);
        body.put(ResultFileConstant.MODULE_PATH, modulePath);
        body.put(ResultFileConstant.FILE_NAME, fileName);

        body.put(ResultFileConstant.SOURCE_CODE, DexterUtil.getBase64CharSequence(sourceCode));

        String resultText = "";

        if (DexterConfig.getInstance().getRunMode().equals(DexterConfig.RunMode.CLI)) {
            Gson gson = new Gson();
            String jsonBody = gson.toJson(body);
            resultText = webResource.postWithBodyforCLI(DexterConfig.POST_SNAPSHOT_SOURCECODE,
            		webResource.getCurrentUserId(), webResource.getCurrentUserPassword(), jsonBody);
        } else {
            resultText = webResource.postWithBody(DexterConfig.POST_SNAPSHOT_SOURCECODE,
                    webResource.getCurrentUserId(), webResource.getCurrentUserPassword(), body);
        }

        checkResultOk(resultText);
    }

    /**
     * @precondition 1. installation nodejs 2. installation of Dexter Server 3.
     * installation of Database
     * 
     * @param dexterServerFullPath
     * : DexterServer location in the same PC
     * @param optionMap
     * database.host : database.port : database.name : database.user
     * : database.password :
     * 
     * @return port to run Dexter Server
     */
    public static int startDexterServer(final String dexterServerFullPath) {
        final Runtime rt = Runtime.getRuntime();
        final int MaxPortNumber = 65535;

        int port = 4982;

        while (DexterUtil.isAvailablePort("localhost", port++) && port < MaxPortNumber)
            ;

        if (port > MaxPortNumber) {
            return -1;
        }

        try {
            rt.exec("node " + dexterServerFullPath + "\\server.js");
            return port;
        } catch (IOException e) {
            LOG.error(e);
            return -1;
        }
    }

    /**
     * @param text
     * @return boolean
     */
    @Override
    public boolean isServerAlive(final String serverAddress) {
        assert Strings.isNullOrEmpty(serverAddress) == false;

        try {
            final String text = webResource.getText(DexterConfig.CHECK_SERVER_ADDRESS,"", "");
            return "ok".equals(text);
        } catch (Exception e) {
            LOG.debug(e.getMessage(), e);
            return false;
        }
    }

    /**
     * @precondition serverHost field should not be null and serverPort field
     * should be over 0
     * 
     * @param text
     * @return boolean
     */
    @Override
    public boolean isServerAlive() {
        if (Strings.isNullOrEmpty(webResource.getServerHostname()) || webResource.getServerPort() <= 0) {
            LOG.debug("Invalid Dexter Server or Port, maybe you need to login on Dexter Server");
            return false;
        }

        try {
            final String text = webResource.getText(DexterConfig.CHECK_SERVER_ADDRESS,
                    webResource.getCurrentUserId(), webResource.getCurrentUserPassword());
            return "ok".equals(text);
        } catch (Exception e) {
            if ("java.net.ConnectException: Connection refused: connect".equals(e.getMessage())) {
                LOG.debug("Dexter Server is not running or network problem");
            } else {
                LOG.debug(e.getMessage(), e);
            }
            return false;
        }
    }

    @Override
    public boolean hasSupportedHelpHtmlFile(final StringBuilder url) {
        try {
            if (DexterConfig.getInstance().isStandalone()) {
                throw new Exception();
            } else {
                final String text = webResource.getConnectionResult(url.toString(), webResource.getCurrentUserId(),
                        webResource.getCurrentUserPassword());
                if ("false".equals(text)) {
                    return false;
                } else {
                    return true;
                }
            }
        } catch (IllegalStateException | UniformInterfaceException e) {
            LOG.debug(e.getMessage(), e);
            return false;
        } catch (Exception e) {
            throw new DexterRuntimeException(e.getMessage(), e);
        }
    }

    /**
     * @param id
     * @param pwd
     * @return Object
     */
    @Override
    public boolean hasAccount(final String id) {
        if (Strings.isNullOrEmpty(id)) {
            LOG.error("Invalid Parameter : id is null or empty");
            return false;
        }

        try {
            final String text = webResource.getText(DexterConfig.CHECK_HAS_ACCOUNT + "/" + id,
                    webResource.getCurrentUserId(), webResource.getCurrentUserPassword());
            return isResultOk(text);
        } catch (DexterRuntimeException e) {
            LOG.debug(e.getMessage(), e);
            return false;
        }
    }

    /**
     * @param id
     * @param pwd
     * @return boolean
     */
    @Override
    public void createAccount(final String id, final String pwd, final boolean isAdmin) {
        assert Strings.isNullOrEmpty(id) == false;
        assert Strings.isNullOrEmpty(pwd) == false;

        if (pwd.length() < 4 || pwd.length() > 20) {
            throw new DexterRuntimeException("Password length must be 4 to 20.");
        }

        final StringBuilder url = new StringBuilder(1024);
        url.append(DexterConfig.ADD_ACCOUNT).append("?userId=").append(id).append("&userId2=")
                .append(pwd);

        if (isAdmin) {
            url.append("&isAdmin=Y");
        } else {
            url.append("&isAdmin=N");
        }

        url.trimToSize();
        final String text = webResource.postText(url.toString(), webResource.getCurrentUserId(), webResource.getCurrentUserPassword());

        checkResultOk(text);
    }

    /**
     * @param defect
     */
    @Override
    public void changeDefectStatus(final Defect defect, final String status) {
        assert defect != null;
        assert !Strings.isNullOrEmpty(status);

        final Map<String, Object> body = new HashMap<String, Object>();
        body.put("defect", defect.toJson());
        body.put("defectStatus", status);
        body.put("a", status);

        String text = webResource.postWithBody(DexterConfig.DISMISS_DEFECT, webResource.getCurrentUserId(),
                webResource.getCurrentUserPassword(), body);
        checkResultOk(text);
    }

    /**
     * @param defect
     */
    @Override
    public void insertDefectFilter(final Defect defect) {
        assert defect != null;

        final Map<String, Object> body = new HashMap<String, Object>();
        body.put("defect", defect.toJson());

        final String text = webResource.postWithBody(DexterConfig.FILTER_FALSE_ALARM, webResource.getCurrentUserId(),
                webResource.getCurrentUserPassword(), body);
        checkResultOk(text);
    }

    /**
     * @precondition Defect object should not be null
     * @param defect
     * void
     * @throws DexterException
     */
    @Override
    public void removeDefectFilter(final Defect defect) {
        assert defect != null;

        final Map<String, Object> body = new HashMap<String, Object>();
        body.put("defect", defect.toJson());

        String text = webResource.postWithBody(DexterConfig.FILTER_DELETE_FALSE_ALARM,
                webResource.getCurrentUserId(), webResource.getCurrentUserPassword(), body);
        checkResultOk(text);
    }

    /** @return */
    @Override
    public int getLastFalseAlarmVersion() {
        try {
            Map<String, Object> result = webResource.getMap(DexterConfig.GET_FALSE_ALARM_VERSION,
                    webResource.getCurrentUserId(), webResource.getCurrentUserPassword());

            if (result == null || result.get("version") == null) {
                LOG.debug("There is no version for False Alarm Data");
                return -2;
            }

            Object obj = result.get("version");
            if (obj instanceof Integer) {
                return (Integer) obj;
            } else {
                return -3;
            }
        } catch (DexterRuntimeException e) {
            LOG.error(e.getMessage(), e);
            return -4;
        }
    }

    /**
     * @return
     * @throws DexterException
     */
    @Override
    public IFalseAlarmConfiguration getFalseAlarmTree() {
        final IFalseAlarmConfiguration tree = new FalseAlarmConfigurationTree();

        final String text = webResource.getText(DexterConfig.FILTER_FALSE_ALARM, webResource.getCurrentUserId(),
                webResource.getCurrentUserPassword());
        @SuppressWarnings("unchecked")
        final List<Map<String, String>> result = new Gson().fromJson(text, List.class);

        for (Map<String, String> map : result) {
            final DefectFilter filter = new DefectFilter();
            // filter.setFid(map.get(""));
            filter.setToolName(map.get(ResultFileConstant.TOOL_NAME));
            filter.setLanguage(map.get(ResultFileConstant.LANGUAGE));
            filter.setFileName(map.get(ResultFileConstant.FILE_NAME));
            filter.setModulePath(map.get(ResultFileConstant.MODULE_PATH));
            filter.setClassName(map.get(ResultFileConstant.CLASS_NAME));
            filter.setMethodName(map.get(ResultFileConstant.METHOD_NAME));
            filter.setCheckerCode(map.get(ResultFileConstant.CHECKER_CODE));
            filter.setActive(true);

            tree.addFalseAlarm(filter);
        }

        return tree;
    }

    @Override
    public String getDexterPluginUpdateUrl() {
        try {
            final StringBuilder url = new StringBuilder(1024);
            url.append(DexterConfig.GET_DEXTER_PLUGIN_UPDATE_URL).append("/")
                    .append(DexterUtil.getBit());

            url.trimToSize();
            final String text = webResource.getText(url.toString(), "", "");

            final Map<String, String> result = getResultMap(text);

            if ("ok".equals(result.get("status"))) {
                return result.get("url");
            }
        } catch (DexterRuntimeException e) {
            LOG.error(e.getMessage(), e);
        }
        return "";
    }

    /**
     * @param localDid
     * @return long
     */
    @Override
    public long getGlobalDid(final Defect defect) {
        assert defect != null;

        final Map<String, Object> body = new HashMap<String, Object>();
        body.put("defect", defect.toJson());

        try {
            final String text = webResource.postWithBody(DexterConfig.POST_GLOBAL_DID,
                    webResource.getCurrentUserId(), webResource.getCurrentUserPassword(), body);

            final Map<String, String> result = getResultMap(text);

            if ("ok".equals(result.get("result"))) {
                return Long.parseLong(result.get("globalDid"));
            } else {
                LOG.error(result.get("errorMessage"));
                return -1l;
            }
        } catch (DexterRuntimeException e) {
            LOG.error(e.getMessage(), e);
            return -2l;
        }
    }

    /**
     * @param replace
     * @param name
     */
    @Override
    public void deleteDefects(final String modulePath, final String fileName) {
        assert Strings.isNullOrEmpty(fileName) == false;

        final Map<String, Object> body = new HashMap<String, Object>();
        body.put(ResultFileConstant.MODULE_PATH, modulePath);
        body.put(ResultFileConstant.FILE_NAME, fileName);

        final String text = webResource.deleteWithBody(DexterConfig.DEFECT_DELETE, webResource.getCurrentUserId(),
                webResource.getCurrentUserPassword(), body);
        checkResultOk(text);
    }

    /** @return String */
    @Override
    public List<DefectGroup> getDefectGroupByGroupName(final String groupName) {
        assert !Strings.isNullOrEmpty(groupName);

        final Map<String, Object> result = webResource.getMap(
                DexterConfig.DEFECT_GROUP + "/" + groupName, webResource.getCurrentUserId(), webResource.getCurrentUserPassword());

        if ("ok".equals(result.get("status"))) {
            final String resultJson = (String) result.get("result");
            final List<DefectGroup> resultList = new Gson().fromJson(resultJson, new TypeToken<List<DefectGroup>>() {
                private static final long serialVersionUID = 7128024360990021164L;
            }.getType());
            return resultList;
        } else {
            throw new DexterRuntimeException("" + result.get("errorMessage"));
        }
    }

    /**
     * you can't specify id for DefectGroup
     * 
     * @param defectGroup
     * @return boolean
     */
    @Override
    public void insertDefectGroup(final DefectGroup defectGroup) {
        assert defectGroup != null;

        final Map<String, Object> body = new HashMap<String, Object>();
        body.put("groupName", defectGroup.getGroupName());
        body.put("groupType", defectGroup.getGroupType());
        body.put("description", defectGroup.getDescription());
        body.put("createdDateTime", (Long) defectGroup.getCreatedDateTime());
        body.put("creatorNo", defectGroup.getCreatorNo());
        body.put("parentId", defectGroup.getParentId());

        final String text = webResource.postWithBody(DexterConfig.DEFECT_GROUP, webResource.getCurrentUserId(),
                webResource.getCurrentUserPassword(), body);
        checkResultOk(text);
    }

    /**
     * do not specify id for DefectGroup programmatically
     * 
     * @param defectGroup
     * @return boolean
     */
    @Override
    public boolean updateDefectGroup(final DefectGroup defectGroup) {
        assert defectGroup != null;

        final Map<String, Object> body = new HashMap<String, Object>();
        body.put(ResultFileConstant.GROUP_ID, defectGroup.getId());
        body.put("groupName", defectGroup.getGroupName());
        body.put("groupType", defectGroup.getGroupType());
        body.put("description", defectGroup.getDescription());
        body.put("createdDateTime", (Long) defectGroup.getCreatedDateTime());
        body.put("creatorNo", defectGroup.getCreatorNo());
        body.put("parentId", defectGroup.getParentId());

        try {
            final String text = webResource.putWithBody(DexterConfig.DEFECT_GROUP, webResource.getCurrentUserId(),
                    webResource.getCurrentUserPassword(), body);
            return isResultOk(text);
        } catch (DexterRuntimeException e) {
            LOG.error(e.getMessage(), e);
            return false;
        }

    }

    @Override
    public boolean deleteDefectGroup(final long defectGroupId) {
        assert defectGroupId > 0;

        try {
            String text = webResource.deleteWithBody(DexterConfig.DEFECT_GROUP + "/" + defectGroupId,
                    webResource.getCurrentUserId(), webResource.getCurrentUserPassword(), new HashMap<String, Object>(0));
            return isResultOk(text);
        } catch (DexterRuntimeException e) {
            LOG.error(e.getMessage(), e);
            return false;
        }
    }

    @Override
    public List<DefectGroup> getDefectGroupList() {
        String text;
        try {
            text = webResource.getText(DexterConfig.DEFECT_GROUP, webResource.getCurrentUserId(),
                    webResource.getCurrentUserPassword());
        } catch (DexterRuntimeException e) {
            LOG.error(e.getMessage(), e);
            return new ArrayList<DefectGroup>(0);
        }

        final Gson gson = new Gson();
        @SuppressWarnings("unchecked")
        final Map<String, Object> result = gson.fromJson(text, Map.class);

        if ("ok".equals(result.get("status"))) {
            final String resultJson = (String) result.get("result");
            final List<DefectGroup> resultList = gson.fromJson(resultJson, new TypeToken<List<DefectGroup>>() {
                private static final long serialVersionUID = 721969096430380905L;
            }.getType());
            return resultList;
        } else {
            LOG.error(result.get("errorMessage"));
            return new ArrayList<DefectGroup>(0);
        }
    }

    @Override
    public List<DexterCode> getCodes(final String codeKey) {
        assert !Strings.isNullOrEmpty(codeKey);

        try {
            final String text = webResource.getText(DexterConfig.CODE + "/" + codeKey,
                    webResource.getCurrentUserId(), webResource.getCurrentUserPassword());

            final Gson gson = new Gson();
            final Map<String, String> result = getResultMap(text);

            if ("ok".equals(result.get("status"))) {
                final List<DexterCode> resultList = gson.fromJson(result.get("result"),
                        new TypeToken<List<DexterCode>>() {
                            private static final long serialVersionUID = -685254649838058063L;
                        }.getType());
                return resultList;
            } else {
                LOG.error(result.get("errorMessage"));
                return new ArrayList<DexterCode>(0);
            }
        } catch (DexterRuntimeException e) {
            LOG.error(e.getMessage(), e);
            return new ArrayList<DexterCode>(0);
        }
    }

    /** @return char[] */
    @Override
    public String getSourceCode(final String modulePath, final String fileName) {
        final String url = DexterConfig.SOURCE_CODE + "?fileName=" + fileName + "&modulePath=" + modulePath
                + "&changeToBase64=false";

        try {
            return webResource.getText(url, webResource.getCurrentUserId(), webResource.getCurrentUserPassword());
        } catch (DexterRuntimeException e) {
            LOG.error(e.getMessage(), e);
            return "";
        }
    }

    // ////////////////////////////////////////////////////////////////////////////////
    /** @return String */
    @Override
    public String getCurrentUserPwd() {
        return webResource.getCurrentUserPassword();
    }

    /**
     * @param tempIsLogin
     * void
     */
    @Override
    public void setLogin(final boolean isLogin) {
        this.isLogin = isLogin;
    }

    /** @return the serverHost */
    @Override
    public String getServerHost() {
    	return webResource.getServerHostname();
    }

    /** @return the serverPort */
    @Override
    public int getServerPort() {
    	return webResource.getServerPort();
    }

    /**
     * @param userNo
     * void
     */
    @Override
    public void setCurrentUserNo(final int userNo) {
        this.currentUserNo = userNo;
    }

    /** @return the currentUserNo */
    @Override
    public int getCurrentUserNo() {
        return currentUserNo;
    }

    /** @return the isCurrentUserAdmin */
    @Override
    public boolean isCurrentUserAdmin() {
        return isCurrentUserAdmin;
    }

    /**
     * @param isCurrentUserAdmin
     * the isCurrentUserAdmin to set
     */
    @Override
    public void setCurrentUserAdmin(final boolean isCurrentUserAdmin) {
        this.isCurrentUserAdmin = isCurrentUserAdmin;
    }

    /** @return String */
    @Override
    public String getDexterWebUrl() {
        if (getServerHost().startsWith("http")) {
            return webResource.getServerHostname() + ":" + webResource.getServerPort() + "/defect";
        } else {
           // return HTTP_PREFIX + webResource.getServerHostname() + ":" + webResource.getServerPort() + "/defect";
            return webResource.getServiceUrl("/defect");
        }
    }

    /** @return String */
    @Override
    public String getDexterDashboardUrl() {
        if (getServerHost().startsWith("http")) {
            return webResource.getServerHostname() + ":" + webResource.getServerPort() + "/dashboard";
        } else {
            //return HTTP_PREFIX + webResource.getServerHostname() + ":" + webResource.getServerPort() + "/dashboard";
            return webResource.getServiceUrl("/dashboard");
        }
    }

    /** @return String */
    @Override
    public String getDexterCodeMetricsUrl() {
        if (getServerHost().startsWith("http")) {
            return webResource.getServerHostname() + ":" + webResource.getServerPort() + "/codeMetrics";
        } else {
            //return HTTP_PREFIX + webResource.getServerHostname() + ":" + webResource.getServerPort() + "/codeMetrics";
            return webResource.getServiceUrl("/codeMetrics");
        }
    }

    /** @return String */
    @Override
    public String getDexterFunctionMetricsUrl() {
        if (getServerHost().startsWith("http")) {
            return webResource.getServerHostname() + ":" + webResource.getServerPort() + "/functionMetrics";
        } else {
            //return HTTP_PREFIX + webResource.getServerHostname() + ":" + webResource.getServerPort() + "/functionMetrics";
        	return webResource.getServiceUrl("/functionMetrics");
        }
    }

    @Override
    public void handleWhenStandaloneMode() {
        setWebResource(new DummyDexterWebResource());
    }

    @Override
    public void handleWhenNotStandaloneMode(DexterServerConfig serverConfig) {
        setWebResource(new JerseyDexterWebResource(serverConfig));
    }

    @Override
    public void setWebResource(IDexterWebResource webResource) {
        this.webResource = webResource;
    }

    public CheckerConfig getDexterPluginChecker(IDexterPlugin plugin, String pluginName) {
        try {
            String text = webResource.postText(
                    DexterConfig.GET_DEXTER_PLUGIN_CHECKER_JSON_FILE + "/" + pluginName,
                    webResource.getCurrentUserId(), webResource.getCurrentUserPassword());
            if (!(text.equals(DexterConfig.NO_UPDATE_CHECKER_CONFIG))) {
                Gson gson = new Gson();
                CheckerConfig cc = gson.fromJson(text, CheckerConfig.class);
                return cc;
            } else {
                CheckerConfig cc = plugin.getCheckerConfig();
                return cc;
            }
        } catch (DexterRuntimeException e) {
            LOG.error(e.getMessage(), e);
            throw new DexterRuntimeException(e.getMessage(), e);
        }
    }

    public void getFunctionMetrics(String fileName, String modulePath, List<String> functionList) {
        try {
            final Map<String, Object> body = new HashMap<String, Object>();
            body.put(ResultFileConstant.FILE_NAME, fileName);
            body.put(ResultFileConstant.MODULE_PATH, modulePath);
            body.put(ResultFileConstant.FUNCTION_LIST, functionList);

            final String text = webResource.postWithBody(DexterConfig.POST_FUNCTION_METRICS,
                    webResource.getCurrentUserId(), webResource.getCurrentUserPassword(), body);

            checkResultOk(text);
        } catch (DexterRuntimeException e) {
            LOG.error(e.getMessage(), e);
            throw new DexterRuntimeException(e.getMessage(), e);
        }
    }
}
