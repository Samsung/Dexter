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

import java.security.cert.CertificateException;
import java.util.List;

import com.samsung.sec.dexter.core.plugin.IDexterPlugin;
import com.samsung.sec.dexter.core.checker.CheckerConfig;
import com.samsung.sec.dexter.core.config.DefectGroup;
import com.samsung.sec.dexter.core.config.DexterCode;
import com.samsung.sec.dexter.core.defect.Defect;
import com.samsung.sec.dexter.core.filter.IFalseAlarmConfiguration;

/**
 * Mock for Handling Dexter Server(Database, etc)
 */
public class DexterClientMock implements IDexterClient {
	public String getDexterDashboardUrl() {
	    return null;
    }

	public String getDexterWebUrl() {
	    return null;
    }

	public boolean isCurrentUserAdmin() {
	    return false;
    }

	public int getCurrentUserNo() {
	    return 0;
    }

	public void setCurrentUserNo(final int userNo) {
    }

	public void setCurrentUserPwd(final String currentUserPwd) {
    }

	public void setCurrentUserId(final String currentUserId) {
    }

	public void setServerPort(final int serverPort) {
    }

	public void setServerHost(final String serverHost) {
    }

	public int getServerPort() {
	    return 0;
    }

	public String getServerHost() {
	    return null;
    }
	
	public String getServerHostForUI() {
	    return null;
    }

	public void setUserPwd(final String userPwd) {
    }

	public void setUserId(final String userId) {
    }

	public String getCurrentUserPwd() {
	    return null;
    }

	public String getSourceCode(final String modulePath, final String fileName) {
	    return null;
    }

	public List<DexterCode> getCodes(final String codeKey) {
	    return null;
    }

	public List<DefectGroup> getDefectGroupList() {
	    return null;
    }

	public boolean deleteDefectGroup(final long defectGroupId) {
	    return false;
    }


	public List<DefectGroup> getDefectGroupByGroupName(final String groupName) {
	    return null;
    }


	public IFalseAlarmConfiguration getFalseAlarmTree() {
	    return null;
    }

	public int getLastFalseAlarmVersion() {
	    return 0;
    }


	public boolean sendNewDefectFilter(final String content) throws CertificateException {
	    return false;
    }

	public int getUserNo(final String userId, final String userPwd) {
	    return 0;
    }

	public boolean hasAccount(final String id) {
	    return false;
    }

	public void setDexterServer(final String serverAddress) {
    }

	public boolean isServerAlive() {
	    return false;
    }

	public boolean isServerAddressOk(final String serverAddress) {
	    return false;
    }

	public String getCurrentUserId() {
	    return null;
    }

	public boolean isLogin() {
	    return false;
    }

	public void setDexterServer(final String serverHost, final int serverPort) {
    }


	public void login() {
    }

	public void setWebResource(IDexterWebResource resource) {
    }

	@Override
    public boolean updateDefectGroup(DefectGroup defectGroup) {
	    // TODO Auto-generated method stub
	    return false;
    }

	@Override
    public void insertDefectGroup(DefectGroup defectGroup) {
	    // TODO Auto-generated method stub
	    
    }

	@Override
    public long getGlobalDid(Defect defect) {
	    // TODO Auto-generated method stub
	    return 0;
    }

	@Override
    public void insertDefectFilter(Defect defect) {
	    // TODO Auto-generated method stub
	    
    }

	@Override
    public void removeDefectFilter(Defect defect) {
	    // TODO Auto-generated method stub
	    
    }

	@Override
    public void changeDefectStatus(Defect defect, String status) {
	    // TODO Auto-generated method stub
	    
    }

	@Override
    public void deleteDefects(String modulePath, String fileName) {
	    // TODO Auto-generated method stub
	    
    }

	@Override
    public void createAccount(String id, String pwd, boolean isAdmin) {
	    // TODO Auto-generated method stub
	    
    }

	@Override
    public void insertSourceCode(long snapshotId, long defectGroupId, String modulePath, String fileName,
            String sourceCode) {
	    // TODO Auto-generated method stub
	    
    }

	@Override
    public void sendAnalsysisResult(String resultJson) {
	    // TODO Auto-generated method stub
	    
    }

	@Override
    public void login(String id, String pwd) {
	    // TODO Auto-generated method stub
	    
    }

	@Override
    public void setCurrentUserAdmin(boolean isAdmin) {
	    // TODO Auto-generated method stub
	    
    }

	@Override
    public void setLogin(boolean b) {
	    // TODO Auto-generated method stub
	    
    }

	@Override
    public String getDexterPluginUpdateUrl() {
	    // TODO Auto-generated method stub
	    return null;
    }
	
	@Override
	public void addLoginInfoListener(final IDexterLoginInfoListener listener) {
	}
	
	@Override
	public void removeLoginInfoListener(final IDexterLoginInfoListener listener) {
	}
	
	@Override
	public void runLoginInfoHandler(String oldServerHost, int oldServerPort, String oldUserId) {
	}
	
	@Override
	public CheckerConfig getDexterPluginChecker(IDexterPlugin plugin, String pluginName) {
		return null ;
	}

	@Override
	public void getCodeMetricsFromFile(String fileName, String modulePath) {
	}	
}
