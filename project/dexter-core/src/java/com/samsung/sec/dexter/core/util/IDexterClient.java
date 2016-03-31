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

import java.util.List;

import com.samsung.sec.dexter.core.checker.CheckerConfig;
import com.samsung.sec.dexter.core.config.DefectGroup;
import com.samsung.sec.dexter.core.config.DexterCode;
import com.samsung.sec.dexter.core.defect.Defect;
import com.samsung.sec.dexter.core.plugin.IDexterPlugin;
import com.samsung.sec.dexter.core.filter.IFalseAlarmConfiguration;

public interface IDexterClient {
	public String getDexterDashboardUrl();

	public String getDexterWebUrl();

	public boolean isCurrentUserAdmin();

	public int getCurrentUserNo();

	public void setCurrentUserNo(final int userNo);

	public void setCurrentUserPwd(final String currentUserPwd);

	public void setCurrentUserId(final String currentUserId);

	public void setServerPort(final int serverPort);

	public void setServerHost(final String serverHost);

	public int getServerPort();

	public String getServerHost();

	public void setUserPwd(final String userPwd);

	public void setUserId(final String userId);

	public String getCurrentUserPwd();

	public String getSourceCode(final String modulePath, final String fileName);

	public List<DexterCode> getCodes(final String codeKey);

	public List<DefectGroup> getDefectGroupList();

	public boolean deleteDefectGroup(final long defectGroupId);

	public boolean updateDefectGroup(final DefectGroup defectGroup);

	/**
	 * create new DefectGroup
	 * 
	 * @param defectGroup
	 * @return void
	 */
	public void insertDefectGroup(final DefectGroup defectGroup);

	/**
	 * get defect group
	 * defect group is a kind of key to analyze defects status as a whole project
	 * 
	 * @param groupName
	 * @return
	 * @return List<DefectGroup>
	 */
	public List<DefectGroup> getDefectGroupByGroupName(final String groupName);

	/**
	 * delete all defects in a given file
	 * 
	 * @param modulePath
	 * @param fileName
	 * @return void
	 */
	public void deleteDefects(final String modulePath, final String fileName);

	public long getGlobalDid(final Defect defect);

	/**
	 * return false alarm object from Dexter Server
	 * 
	 * @return
	 * @return IFalseAlarmConfiguration
	 */
	public IFalseAlarmConfiguration getFalseAlarmTree();

	/**
	 * get last version of false alarm information that indicates the change of false alarm list to be merged to local
	 * 
	 * @return
	 * @return int  > 0 : Ok
	 * 			-2 : Error while executing Client module. it can be caused by the dead of Dexter Server.
	 * 			-3 : Return-value is not number
	 * 			-4 : Unknown error 
	 */
	public int getLastFalseAlarmVersion();

	public void insertDefectFilter(final Defect defect);
	
	/**
	 * remove defect filter(false positive) on Dexter Server
	 * 
	 * @param defect
	 * @return void
	 */
	public void removeDefectFilter(final Defect defect);

	public void changeDefectStatus(final Defect defect, final String status);

	/**
	 * create new account for Dexter Server
	 * 
	 * @param id
	 * @param pwd
	 * @param isAdmin
	 * @return void
	 */
	public void createAccount(final String id, final String pwd, final boolean isAdmin);

	public boolean hasAccount(final String id);

	public void setDexterServer(final String serverAddress);

	public boolean isServerAlive();

	public boolean isServerAddressOk(final String serverAddress);

	public String getCurrentUserId();

	public boolean isLogin();

	public void setDexterServer(final String serverHost, final int serverPort);

	/**
	 * add source codes into Dexter Server
	 * 
	 * @param snapshotId		option. -1: not use snapshot, timestamp: use snapshot
	 * @param defectGroupId		option. -1: not use defect group. timestamp: use defect group
	 * @param modulePath		mandatory. "": no modulePath, "a/b/": has module path
	 * @param fileName			mandatory. not including path. only filename + extension
	 * @param sourceCode		mandatory. the contents of file (text. UTF-8)
	 * @return
	 */
	public void insertSourceCode(final long snapshotId, final long defectGroupId, 
			final String modulePath, final String fileName, final String sourceCode);

	/**
	 * [Mandatory] Method���듭떖 湲곕뒫 諛��ъ슜���뚭퀬由ъ쬁���꾩쟾��臾몄옣�쇰줈 湲곗닠�쒕떎.
	 * 
	 * @param resultJson
	 * @return void
	 */
	public void sendAnalsysisResult(final String resultJson);

	/**
	 * log in to Dexter Server with current user ID and Password in a object
	 */
	public void login();

	/**
	 * log in to Dexter Server
	 * 
	 * @param id
	 * @param pwd
	 * @return void
	 */
	public void login(final String id, final String pwd);

	/**
	 * If you want to your own WebResource object, use this method.
	 * Unless you use this method, DexterClient will use DefaultWebResource object which is built-in
	 * 
	 * @param resource
	 */
	public void setWebResource(IDexterWebResource resource);

	/**
	 * set the current user as an administrator or not.
	 * If admin, the user can do administrator things such as create a snapshot
	 * 
	 * @param isAdmin
	 */
	void setCurrentUserAdmin(boolean isAdmin);

	void setLogin(boolean b);

	public String getDexterPluginUpdateUrl();
	public CheckerConfig getDexterPluginChecker(IDexterPlugin plugin, String pluginName);
	
	public void addLoginInfoListener(final IDexterLoginInfoListener listener);
	public void removeLoginInfoListener(final IDexterLoginInfoListener listener);
	public void runLoginInfoHandler(String oldServerHost, int oldServerPort, String oldUserId);

}
