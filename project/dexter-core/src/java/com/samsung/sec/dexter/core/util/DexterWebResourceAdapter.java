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

import java.util.HashMap;
import java.util.Map;

public class DexterWebResourceAdapter implements IDexterWebResource {

	@Override
	public String getText(String uri, String id, String pwd) {
		return "";
	}

	@Override
	public Map<String, Object> getMap(String uri, String id, String pwd) {
		return new HashMap<String, Object>(0);
	}

	@Override
	public String postText(String uri, String id, String pwd) {
		return "";
	}

	@Override
	public String postWithBody(String uri, String id, String pwd, Map<String, Object> body) {
		return "";
	}

	@Override
	public String deleteWithBody(String uri, String id, String pwd, Map<String, Object> body) {
		return "";
	}

	@Override
	public String putWithBody(String uri, String id, String pwd, Map<String, Object> body) {
		return "";
	}

	@Override
	public String getConnectionResult(String uri, String id, String pwd) {
		return "";
	}

	@Override
	public String postWithBodyforCLI(String uri, String id, String pwd, String bodyJson) {
		return "";
	}

	@Override
	public void setDexterServerConfig(DexterServerConfig serverConfig) {
		
	}

	@Override
	public String getServiceUrl(String serviceUrl) {
		return null;
	}

	@Override
	public String getCurrentUserId() {
		return null;
	}

	@Override
	public String getCurrentUserPassword() {
		return null;
	}

	@Override
	public String getServerHostname() {
		return null;
	}

	@Override
	public int getServerPort() {
		return 0;
	}
}
