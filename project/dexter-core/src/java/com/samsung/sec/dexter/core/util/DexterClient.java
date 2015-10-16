/**
 * Copyright (c) 2014 Samsung Electronics, Inc.,
 * All rights reserved.
 * 
 * Redistribution and use in source and binary forms, with or without
 * modification, are permitted provided that the following conditions are met:
 * 
 * * Redistributions of source code must retain the above copyright notice, this
 *   list of conditions and the following disclaimer.
 * 
 * * Redistributions in binary form must reproduce the above copyright notice,
 *   this list of conditions and the following disclaimer in the documentation
 *   and/or other materials provided with the distribution.
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

import java.io.IOException;
import java.security.cert.CertificateException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.StringTokenizer;

import org.apache.log4j.Logger;

import com.google.common.base.Strings;
import com.google.common.reflect.TypeToken;
import com.google.gson.Gson;
import com.samsung.sec.dexter.core.analyzer.ResultFileConstant;
import com.samsung.sec.dexter.core.config.DefectGroup;
import com.samsung.sec.dexter.core.checker.CheckerConfig;
import com.samsung.sec.dexter.core.config.DexterCode;
import com.samsung.sec.dexter.core.config.DexterConfig;
import com.samsung.sec.dexter.core.config.IDexterStandaloneListener;
import com.samsung.sec.dexter.core.defect.Defect;
import com.samsung.sec.dexter.core.plugin.IDexterPlugin;
import com.samsung.sec.dexter.core.exception.DexterException;
import com.samsung.sec.dexter.core.exception.DexterRuntimeException;
import com.samsung.sec.dexter.core.filter.DefectFilter;
import com.samsung.sec.dexter.core.filter.FalseAlarmConfigurationTree;
import com.samsung.sec.dexter.core.filter.IFalseAlarmConfiguration;
import com.sun.jersey.api.client.Client;
import com.sun.jersey.api.client.UniformInterfaceException;
import com.sun.jersey.api.client.WebResource;
import com.sun.jersey.api.client.config.ClientConfig;
import com.sun.jersey.api.client.config.DefaultClientConfig;
import com.sun.jersey.core.impl.provider.entity.StringProvider;

public class DexterClient implements IDexterClient, IDexterStandaloneListener {
	private final static Logger LOG = Logger.getLogger(DexterClient.class);

	private IDexterWebResource webResource;
	
    private static final String AUTHORIZATION = "Authorization";
	private static final String APPLICATION_TYPE_JSON = "application/json";
	private static final String HTTP_PREFIX = "http://";
	private static final int TIME_OUT = 15000;

	/** Dexter Server Info. */
	private String serverHost = "";
	private int serverPort = -1;
	private boolean isLogin = false;
	private String currentUserId = "";
	private String currentUserPwd = "";
	private int currentUserNo;
	private boolean isCurrentUserAdmin = false;
	//private String text;
	
	private final  List<IDexterLoginInfoListener> loginInfoListenerList = new ArrayList<IDexterLoginInfoListener>();
	
	private static class DexterClientHolder {
		private final static DexterClient INSTANCE = new DexterClient();
	}

	/**
	 * Before using methods for the class, you have to setup once as below.
	 * 
	 * the followings are preconditions:
	 *   1) Set Dexter Server Connection Info. : setServerHost(host) and setServerPort(port)
	 *   2) Log in to Dexter Server : login(userId, userPwd)
	 * 
	 * @return
	 */
	public static DexterClient getInstance() {
		return DexterClientHolder.INSTANCE;
	}
	
	/**
	 * singleton
	 */
	private DexterClient() {
		this.webResource = new JerseyDexterWebResource();
		LOG.debug("DexterClient");
	}
	
	/**
	 * If you want to your own WebResource object, use this method.
	 * Unless you use this method, DexterClient will use DefaultWebResource object which is built-in
	 * 
	 * @param resource
	 */
	@Override
    public void setWebResource(IDexterWebResource resource){
		this.webResource = resource;
	}
	
	/**
	 * @return the isLogin
	 */
	@Override
    public boolean isLogin() {
		return isLogin;
	}

	/**
	 * @return the currentUserId
	 */
	@Override
    public String getCurrentUserId() {
		return currentUserId;
	}
	
	/**
	 * @param trim
	 */
	@Override
    public void setDexterServer(final String serverAddress) {
		final StringTokenizer st = new StringTokenizer(serverAddress, ":");

		if (st.hasMoreTokens()) {
			this.serverHost = st.nextToken();
		}

		if (st.hasMoreTokens()) {
			final String portStr = st.nextToken();
			if (portStr.matches("[0-9]+")) {
				this.serverPort = Integer.parseInt(portStr);
			}
		}
	}
	
	/**
	 * set Dexter Server Info.
	 * 
	 * @precondition serverHost should not be null and serverPort should be over 0
	 * @param string
	 * @param i
	 *            void
	 */
	@Override
    public void setDexterServer(final String serverHost, final int serverPort) {
		assert !Strings.isNullOrEmpty(serverHost);
		assert serverPort > 0;

		this.serverHost = serverHost;
		this.serverPort = serverPort;
	}
	
	private boolean isResultOk(final String resultText){
		final Map<String, String> result = getResultMap(resultText);
		
		if ("ok".equals(result.get("result")) || "ok".equals(result.get("status"))) {
			return true;
		} else {
			LOG.debug("DexterClient Error: " + result.get("errorMessage") + "\n" + resultText);
			return false;
		}
	}
	
	private void checkResultOk(final String resultText){
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
    public void login(final String id, final String pwd){
		assert Strings.isNullOrEmpty(id) == false;
		assert Strings.isNullOrEmpty(pwd) == false;
		
		String text = webResource.getText(getServiceUrl(DexterConfig.CHECK_ACCOUNT), id, pwd);

		final Gson gson = new Gson();
		@SuppressWarnings("unchecked")
		final Map<String, Object> result = gson.fromJson(text, Map.class);

		if ("ok".equals(result.get("result"))) {
			this.isLogin = true;
			this.currentUserNo = ((Double) result.get("userNo")).intValue();
			this.currentUserId = result.get("userId").toString();
			this.currentUserPwd = result.get("userPwd").toString();
			this.isCurrentUserAdmin = "true".equals(result.get("isAdmin").toString());
		} else {
			this.isLogin = false;
			throw new DexterRuntimeException("ID or Password is invalid.");
		}
	}
	
	private String getServiceUrl(String serviceUrl){
		return HTTP_PREFIX + this.serverHost + ":" + this.serverPort + serviceUrl;
	}
	
	/**
	 * @return boolean
	 */
	@Override
    public void login() {
		assert Strings.isNullOrEmpty(this.currentUserId) == false;
		assert Strings.isNullOrEmpty(this.currentUserPwd) == false;

		try {
	        login(this.currentUserId, this.currentUserPwd);
	        LOG.debug("Log-in successfully : " + this.currentUserId);
        } catch (DexterRuntimeException e) {
        	LOG.debug(e.getMessage(), e);
        }
	}

	/**
	 * @param resultJson
	 * @throws DexterException 
	 */
	@Override
    public void sendAnalsysisResult(final String resultJson){
		if (Strings.isNullOrEmpty(resultJson)) {
			throw new DexterRuntimeException("The result file has no content to send");
		}

		if (isLogin == false) {
			throw new DexterRuntimeException("You are not log-in or your account("
			        + DexterClient.getInstance().currentUserId + ") doesn't exist in Dexter Server.");
		}

		Map<String, Object> body = new HashMap<String, Object>();
		body.put("result", resultJson);

		String resultText = webResource.postWithBody(getServiceUrl(DexterConfig.PUT_ANALYSIS_RESULT), 
				this.currentUserId, this.currentUserPwd, body);

		checkResultOk(resultText);
	}
	
	/**
	 * @param resultJson
	 *            void
	 * @throws CertificateException
	 */
	@Override
    public void insertSourceCode(final long snapshotId, final long defectGroupId, 
    		final String modulePath, final String fileName, final String sourceCode){
		assert Strings.isNullOrEmpty(fileName) == false;
		
		if (Strings.isNullOrEmpty(sourceCode)) {
			throw new DexterRuntimeException("Invalide Parameter of sourceCode: null or empty.  fileName:" + fileName);
		}
		
		final Map<String, Object> body = new HashMap<String, Object>();
		body.put(ResultFileConstant.SNAPSHOT_ID, snapshotId);
		body.put(ResultFileConstant.GROUP_ID, defectGroupId);
		body.put(ResultFileConstant.MODULE_PATH, modulePath);
		body.put(ResultFileConstant.FILE_NAME, fileName);
		body.put(ResultFileConstant.SOURCE_CODE, DexterUtil.getBase64String(sourceCode));
		
		String resultText = webResource.postWithBody(getServiceUrl(DexterConfig.POST_SNAPSHOT_SOURCECODE), 
				this.currentUserId, this.currentUserPwd, body);

		checkResultOk(resultText);
	}


	/**
	 * @precondition 
	 * 		1. installation nodejs  2. installation of Dexter Server  3. installation of Database 
	 * 
	 * @param dexterServerFullPath : DexterServer location in the same PC 
	 * @param optionMap
	 * 		database.host : 
	 * 		database.port :
	 * 		database.name :
	 * 		database.user :
	 * 		database.password :
	 * 
	 * @return port to run Dexter Server
	 */
	public static int startDexterServer(final String dexterServerFullPath) {
	//TODO: public int startDexterServer(final String dexterServerFullPath, final Map<String, String> optionMap) {
		final Runtime rt = Runtime.getRuntime();
		final int MaxPortNumber = 65535;
		
		int port = 4982;
		
		while(DexterUtil.isAvailablePort("localhost", port++) && port < MaxPortNumber);
		
		if(port > MaxPortNumber){
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
	 * Only for Testing
	 * 
	 * @param serverHost
	 * @param serverPort
	 * @param userId
	 * @param userPwd
	 * @return
	 * @return boolean
	 */
	public static boolean stopDexterServer(final String serverHost, final int serverPort, final String userId, final String userPwd) {
		assert Strings.isNullOrEmpty(serverHost) == false;
		assert serverPort > 0;
		assert Strings.isNullOrEmpty(userId) == false;
		assert Strings.isNullOrEmpty(userPwd) == false;
		
		Client client = null;
		try{
			final ClientConfig config = new DefaultClientConfig();
			config.getClasses().add(StringProvider.class);
			config.getProperties().put(ClientConfig.PROPERTY_READ_TIMEOUT, TIME_OUT);
			config.getProperties().put(ClientConfig.PROPERTY_CONNECT_TIMEOUT, TIME_OUT);
			
			client = Client.create(config);
			client.setConnectTimeout(TIME_OUT);
			client.setReadTimeout(TIME_OUT);
			
			final WebResource resource = client.resource(DexterClient.getInstance().getServiceUrl(DexterConfig.STOP_SERVER));
			final String text = resource.accept(APPLICATION_TYPE_JSON)
					.header(AUTHORIZATION, "Basic " + DexterUtil.getBase64String(userId + ":" + userPwd))
					.delete(String.class);

			return "ok".equals(text);
		} catch (Exception e){
			e.printStackTrace();
			return false;
		} finally {
			if(client != null){
				client.destroy();
			}
		}

	}
	
	public static boolean deleteDexterDatabase(final String serverHost, final int serverPort, final String userId, final String userPwd) {
		assert Strings.isNullOrEmpty(serverHost) == false;
		assert serverPort > 0;
		assert Strings.isNullOrEmpty(userId) == false;
		assert Strings.isNullOrEmpty(userPwd) == false;

		Client client = null;
		try{
			final ClientConfig config = new DefaultClientConfig();
			config.getClasses().add(StringProvider.class);
			config.getProperties().put(ClientConfig.PROPERTY_READ_TIMEOUT, TIME_OUT);
			config.getProperties().put(ClientConfig.PROPERTY_CONNECT_TIMEOUT, TIME_OUT);
			
			client = Client.create(config);
			client.setConnectTimeout(TIME_OUT);
			client.setReadTimeout(TIME_OUT);
			
			final WebResource resource = client.resource(
					DexterClient.getInstance().getServiceUrl("/api/dexter-db"));
			final String text = resource.accept(APPLICATION_TYPE_JSON)
					.header(AUTHORIZATION, "Basic " + DexterUtil.getBase64String(userId + ":" + userPwd))
					.delete(String.class);

			return "ok".equals(text);
		} catch (Exception e){
			e.printStackTrace();
			return false;
		} finally {
			if(client != null){
				client.destroy();
			}
		}

	}

	/**
	 * @param text
	 * @return boolean
	 */
	@Override
    public boolean isServerAddressOk(final String serverAddress) {
		assert Strings.isNullOrEmpty(serverAddress) == false;
		
		try {
			final String text = webResource.getText(HTTP_PREFIX + serverAddress + DexterConfig.CHECK_SERVER_ADDRESS, 
					this.currentUserId, this.currentUserPwd);
			return "ok".equals(text);
		} catch (Exception e) {
			LOG.debug(e.getMessage(), e);
			return false;
		}
	}


	/**
	 * @precondition serverHost field should not be null and serverPort field should be over 0
	 * 
	 * @param text
	 * @return boolean
	 */
	@Override
    public boolean isServerAlive() {
		if (Strings.isNullOrEmpty(serverHost) || serverPort <= 0) {
			LOG.debug("Invalid Dexter Server or Port, maybe you need to login on Dexter Server");
			return false;
		}

		try {
			final String text = webResource.getText(getServiceUrl(DexterConfig.CHECK_SERVER_ADDRESS), this.currentUserId, this.currentUserPwd);
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
	
	public boolean hasSupportedHelpHtmlFile(final StringBuilder url){
		try {
			final String text = webResource.getConnectionResult(url.toString(), this.currentUserId, this.currentUserPwd);
			if("false".equals(text)) {
				return false;
			}
			else {
				return  true;
			}
		} catch(IllegalStateException e){
			LOG.debug(e.getMessage(), e);
			return false;
		} catch(UniformInterfaceException e){
			LOG.debug(e.getMessage(), e);
			return false;
		} catch (Exception e){
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
		if(Strings.isNullOrEmpty(id)){
			LOG.error("Invalid Parameter : id is null or empty");
			return false;
		}

        try {
	        final String text = webResource.getText(getServiceUrl(DexterConfig.CHECK_HAS_ACCOUNT + "/" + id), 
	        		this.currentUserId, this.currentUserPwd);
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

		final StringBuilder url = new StringBuilder();
		url.append(getServiceUrl(DexterConfig.ADD_ACCOUNT)).append("?userId=").append(id).append("&userId2=").append(pwd);

		if (isAdmin) {
			url.append("&isAdmin=Y");
		} else {
			url.append("&isAdmin=N");
		}

		final String text = webResource.postText(url.toString(), this.currentUserId, this.currentUserPwd);

		checkResultOk(text);
	}

	/**
	 * @param defect
	 */
	@Override
    public void changeDefectStatus(final Defect defect, final String status){
		assert defect != null;
		assert !Strings.isNullOrEmpty(status);

		final Map<String, Object> body = new HashMap<String, Object>();
		body.put("defect", defect.toJson());
		body.put("defectStatus", status);
		body.put("a", status);

		String text = webResource.postWithBody(getServiceUrl(DexterConfig.DISMISS_DEFECT), 
				this.currentUserId, this.currentUserPwd, body);
		checkResultOk(text);
	}

	/**
	 * @param defect
	 */
	@Override
    public void insertDefectFilter(final Defect defect){
		assert defect != null;
		
		final Map<String, Object> body = new HashMap<String, Object>();
		body.put("defect", defect.toJson());

		final String text = webResource.postWithBody(getServiceUrl(DexterConfig.FILTER_FALSE_ALARM), 
				this.currentUserId, this.currentUserPwd, body);
		checkResultOk(text);
	}

	/**
	 * @precondition Defect object should not be null
	 * @param defect
	 *            void
	 * @throws DexterException 
	 */
	@Override
    public void removeDefectFilter(final Defect defect){
		assert defect != null;

		final Map<String, Object> body = new HashMap<String, Object>();
		body.put("defect", defect.toJson());

		String text = webResource.postWithBody(getServiceUrl(DexterConfig.FILTER_DELETE_FALSE_ALARM), this.currentUserId, this.currentUserPwd, body);
		checkResultOk(text);
	}

	/**
	 * @return
	 */
	@Override
    public int getLastFalseAlarmVersion() {
        try {
            Map<String, Object> result = webResource.getMap(getServiceUrl(DexterConfig.GET_FALSE_ALARM_VERSION),
	        			currentUserId, currentUserPwd);
        	
	        if (result == null || result.get("version") == null) {
	        	LOG.debug("There is no version for False Alarm Data");
	        	return -2;
	        } 

	        Object obj = result.get("version");
	        if(obj instanceof Integer){
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
		
		final String text = webResource.getText(getServiceUrl(DexterConfig.FILTER_FALSE_ALARM), this.currentUserId, this.currentUserPwd);
		@SuppressWarnings("unchecked")
        final List<Map<String, String>> result = new Gson().fromJson(text, List.class);
		
		for(Map<String, String> map : result){
			final DefectFilter filter = new DefectFilter();
			//filter.setFid(map.get(""));
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
        	final StringBuilder url = new StringBuilder();
    		url.append(getServiceUrl(DexterConfig.GET_DEXTER_PLUGIN_UPDATE_URL)).append("/").append(DexterUtil.getBit());
    		
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
	        final String text = webResource.postWithBody(getServiceUrl(DexterConfig.POST_GLOBAL_DID), 
	        		this.currentUserId, this.currentUserPwd, body);
	        
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
    public void deleteDefects(final String modulePath, final String fileName){
		assert Strings.isNullOrEmpty(fileName) == false;
		
		final Map<String, Object> body = new HashMap<String, Object>();
		body.put(ResultFileConstant.MODULE_PATH, modulePath);
		body.put(ResultFileConstant.FILE_NAME, fileName); 

		final String text = webResource.deleteWithBody(getServiceUrl(DexterConfig.DEFECT_DELETE), 
				this.currentUserId, this.currentUserPwd, body);
		checkResultOk(text);
	}

	/**
	 * @return String
	 */
	@Override
    public List<DefectGroup> getDefectGroupByGroupName(final String groupName) {
		assert !Strings.isNullOrEmpty(groupName); 

        final Map<String, Object> result = webResource.getMap(getServiceUrl(DexterConfig.DEFECT_GROUP + "/" + groupName), 
				this.currentUserId, this.currentUserPwd);

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
    public void insertDefectGroup(final DefectGroup defectGroup){
		assert defectGroup != null;
		
		final Map<String, Object> body = new HashMap<String, Object>();
		body.put("groupName", defectGroup.getGroupName());
		body.put("groupType", defectGroup.getGroupType());
		body.put("description", defectGroup.getDescription());
		body.put("createdDateTime", (Long) defectGroup.getCreatedDateTime());
		body.put("creatorNo", defectGroup.getCreatorNo());
		body.put("parentId", defectGroup.getParentId());

		final String text = webResource.postWithBody(getServiceUrl(DexterConfig.DEFECT_GROUP), this.currentUserId, this.currentUserPwd, body);
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
	        final String text = webResource.putWithBody(getServiceUrl(DexterConfig.DEFECT_GROUP), this.currentUserId, this.currentUserPwd, body);
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
	        String text = webResource.deleteWithBody(getServiceUrl(DexterConfig.DEFECT_GROUP + "/" + defectGroupId), 
	        		this.currentUserId, this.currentUserPwd, new HashMap<String,Object>(0));
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
	        text = webResource.getText(getServiceUrl(DexterConfig.DEFECT_GROUP), this.currentUserId, this.currentUserPwd);
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
			final String text = webResource.getText(getServiceUrl(DexterConfig.CODE + "/" + codeKey), this.currentUserId, this.currentUserPwd);
			
			final Gson gson = new Gson();
			final Map<String, String> result = getResultMap(text);
			
			if ("ok".equals(result.get("status"))) {
				final List<DexterCode> resultList = gson.fromJson(result.get("result"), new TypeToken<List<DexterCode>>() {
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

	/**
	 * @return char[]
	 */
	@Override
    public String getSourceCode(final String modulePath, final String fileName) {
		final String url = DexterConfig.SOURCE_CODE + "?fileName=" + fileName + "&modulePath=" + modulePath + "&changeToBase64=false";

		try {
			return webResource.getText(getServiceUrl(url), this.currentUserId, this.currentUserPwd);
		} catch (DexterRuntimeException e) {
			LOG.error(e.getMessage(), e);
			return "";
		}
	}

	// ////////////////////////////////////////////////////////////////////////////////
	/**
	 * @return String
	 */
	@Override
    public String getCurrentUserPwd() {
		return this.currentUserPwd;
	}

	/**
	 * @param tempIsLogin
	 *            void
	 */
	@Override
	public void setLogin(final boolean isLogin) {
		this.isLogin = isLogin;
	}

	/**
	 * @param tempUserId
	 *            void
	 */
	@Override
    public void setUserId(final String userId) {
		this.currentUserId = userId;
	}

	/**
	 * @param tempUserPwd
	 *            void
	 */
	@Override
    public void setUserPwd(final String userPwd) {
		this.currentUserPwd = userPwd;
	}

	/**
	 * @return the serverHost
	 */
	@Override
    public String getServerHost() {
		if (Strings.isNullOrEmpty(serverHost)){
			throw new DexterRuntimeException("ServerHost is not set");
		}
		
		return serverHost;
	}
	
	/**
	 * @return the serverPort
	 */
	@Override
    public int getServerPort() {
		return serverPort;
	}

	/**
	 * @param serverHost
	 *            the serverHost to set
	 */
	@Override
    public void setServerHost(final String serverHost) {
		this.serverHost = serverHost;
	}

	/**
	 * @param serverPort
	 *            the serverPort to set
	 */
	@Override
    public void setServerPort(final int serverPort) {
		this.serverPort = serverPort;
	}

	/**
	 * @param currentUserId
	 *            the currentUserId to set
	 */
	@Override
    public void setCurrentUserId(final String currentUserId) {
		this.currentUserId = currentUserId;
	}

	/**
	 * @param currentUserPwd
	 *            the currentUserPwd to set
	 */
	@Override
    public void setCurrentUserPwd(final String currentUserPwd) {
		this.currentUserPwd = currentUserPwd;
	}

	/**
	 * @param userNo
	 *            void
	 */
	@Override
    public void setCurrentUserNo(final int userNo) {
		this.currentUserNo = userNo;
	}

	/**
	 * @return the currentUserNo
	 */
	@Override
    public int getCurrentUserNo() {
		return currentUserNo;
	}

	/**
	 * @return the isCurrentUserAdmin
	 */
	@Override
    public boolean isCurrentUserAdmin() {
		return isCurrentUserAdmin;
	}

	/**
	 * @param isCurrentUserAdmin
	 *            the isCurrentUserAdmin to set
	 */
	@Override
	public void setCurrentUserAdmin(final boolean isCurrentUserAdmin) {
		this.isCurrentUserAdmin = isCurrentUserAdmin;
	}

	/**
	 * @return String
	 */
	@Override
    public String getDexterWebUrl() {
		if (getServerHost().startsWith("http")) {
			return this.serverHost + ":" + this.serverPort + "/defect";
		} else {
			return HTTP_PREFIX + this.serverHost + ":" + this.serverPort + "/defect";
		}
	}
	
	/**
	 * @return String
	 */
	@Override
    public String getDexterDashboardUrl() {
		if (getServerHost().startsWith("http")) {
			return this.serverHost + ":" + this.serverPort + "/dashboard";
		} else {
			return HTTP_PREFIX + this.serverHost + ":" + this.serverPort + "/dashboard";
		}
	}
	
	@Override
	public synchronized void addLoginInfoListener(final IDexterLoginInfoListener listener){
		if(!loginInfoListenerList.contains(listener)){
			loginInfoListenerList.add(listener);
		}
	}
	
	@Override
	public synchronized void removeLoginInfoListener(final IDexterLoginInfoListener listener){
		loginInfoListenerList.remove(listener);
	}
	
	@Override
	public synchronized void runLoginInfoHandler(String oldServerHost, int oldServerPort, String oldUserId) {
		if (oldServerHost.equals(this.serverHost) && 
				oldServerPort == this.serverPort && 
				oldUserId.equals(this.currentUserId)) {
			return;
		}
			
		for (int i=0; i<loginInfoListenerList.size();i++){
			final IDexterLoginInfoListener listener = loginInfoListenerList.get(i);
			
			if(listener != null){
				listener.handleDexterLoginInfoChanged();
			} else {
				loginInfoListenerList.remove(i--);
			}
		}
	}

	@Override
	public void handleDexterStandaloneChanged() {
		if (DexterConfig.getInstance().isStandalone()) {
    		setWebResource(new DummyDexterWebResource());
    	} else {
    		setWebResource(new JerseyDexterWebResource());
    	}
	}
	
	public void getDexterPluginCheckerJsonFile(IDexterPlugin plugin, String pluginName) {
		try {
			String text = webResource.postText(getServiceUrl(DexterConfig.GET_DEXTER_PLUGIN_CHECKER_JSON_FILE + "/" + pluginName), this.currentUserId, this.currentUserPwd);
			if(!(text.equals(DexterConfig.NO_UPDATE_CHECKER_CONFIG)))
			{
				Gson gson = new Gson();
				CheckerConfig cc = gson.fromJson(text, CheckerConfig.class);
				plugin.setCheckerConfig(cc);
			}
		} catch (DexterRuntimeException e) {
			LOG.error(e.getMessage(), e);
			throw new DexterRuntimeException(e.getMessage(), e);
		}
	}
	
}
