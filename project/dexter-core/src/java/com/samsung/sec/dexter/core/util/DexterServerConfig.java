package com.samsung.sec.dexter.core.util;

import java.util.StringTokenizer;

import com.google.gson.annotations.SerializedName;

public class DexterServerConfig {
	private final static String HTTP_PREFIX = "http://";
	
	@SerializedName("ip") 
	public String hostname;
	public int port;
	@SerializedName("id") 
	public String userId;
	@SerializedName("pw") 
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
	
	@Override
	public boolean equals(Object obj) {
		if (obj == null)
			return false; 
		
		if (this.getClass() != obj.getClass()) 
			return false;
		
		if (this == obj) 
			return true;

		DexterServerConfig that = (DexterServerConfig)obj;
		
		return isSameString(this.hostname, that.hostname) &&
				isSameString(this.userId, that.userId) && 
				isSameString(this.userPwd, that.userPwd) &&
				this.port == that.port;
	}
	
	private boolean isSameString(String src, String dest) {
		return src == null ? dest == null : src.equals(dest);
		
	}
}