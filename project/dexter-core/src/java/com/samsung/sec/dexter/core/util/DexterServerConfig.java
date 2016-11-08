package com.samsung.sec.dexter.core.util;

import java.util.StringTokenizer;

public class DexterServerConfig {
	private final static String HTTP_PREFIX = "http://";
	
	public String hostname;
	public int port;
	public String userId;
	public String userPwd;

	public DexterServerConfig(final String userId, final String userPwd, final String serverAddress) {
		this.userId = userId;
		this.userPwd = userPwd;
		setHostnameAndPortByAddress(serverAddress);
	}
	
	public DexterServerConfig(final String userId, final String userPwd, final String hostname, final int port) {
		this.userId = userId;
		this.userPwd = userPwd;
		this.hostname = hostname;
		this.port = port;
	}
	
	private void setHostnameAndPortByAddress(final String serverAddress) {
        final StringTokenizer st = new StringTokenizer(serverAddress, ":");

        if (st.hasMoreTokens()) {
            this.hostname = st.nextToken();
        }

        if (st.hasMoreTokens()) {
            final String portStr = st.nextToken();
            if (portStr.matches("[0-9]+")) {
                this.port = Integer.parseInt(portStr);
            }
        }
    }

	public String getHostname() {
		return hostname;
	}

	public int getPort() {
		return port;
	}

	public String getUserId() {
		return userId;
	}

	public String getUserPwd() {
		return userPwd;
	}
	
	public String getServiceUrl(String serviceUrl) {
        return HTTP_PREFIX + this.hostname + ":" + this.port + serviceUrl;
    }
}