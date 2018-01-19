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

import java.util.Map;

import org.codehaus.jackson.jaxrs.JacksonJsonProvider;

import com.samsung.sec.dexter.core.config.DexterConfig;
import com.samsung.sec.dexter.core.exception.DexterRuntimeException;
import com.sun.jersey.api.client.Client;
import com.sun.jersey.api.client.ClientHandlerException;
import com.sun.jersey.api.client.WebResource;
import com.sun.jersey.api.client.config.ClientConfig;
import com.sun.jersey.api.client.config.DefaultClientConfig;
import com.sun.jersey.api.json.JSONConfiguration;
import com.sun.jersey.core.impl.provider.entity.StringProvider;

public class JerseyDexterWebResource implements IDexterWebResource {
	//private final static Logger logger = Logger.getLogger(DefaultDexterWebResource.class);
	private static final String AUTHORIZATION = "Authorization";
	private static final String APPLICATION_TYPE_JSON = "application/json";
	
	private DexterServerConfig serverConfig;
	
	private Client jsonClient;
	private Client stringClient;
	private Client pojoClient;
	
	public JerseyDexterWebResource(final DexterServerConfig serverConfig) {
		this.serverConfig = serverConfig;
	}
	
	@Override
	public void setDexterServerConfig(DexterServerConfig serverConfig) {
		this.serverConfig = serverConfig;
	}
	
	private Client getStringClient() {
		if(stringClient == null){
			int timeout = DexterConfig.getInstance().getServerConnectionTimeOut();
			// Think about Pooling of Clients
			final ClientConfig config = new DefaultClientConfig();
			config.getClasses().add(StringProvider.class);
			config.getProperties().put(ClientConfig.PROPERTY_READ_TIMEOUT, timeout);
			config.getProperties().put(ClientConfig.PROPERTY_CONNECT_TIMEOUT, timeout);
			
			stringClient = Client.create(config);
			stringClient.setConnectTimeout(timeout);
			stringClient.setReadTimeout(timeout);
		}
		
		return stringClient;
	}
	
	private WebResource getStringResource(final String uri){
		getStringClient();
		final WebResource resource = stringClient.resource(uri);
		
		return resource;
	}
	
	private Client getJsonPojoMappingClient() {
		if(jsonClient == null){
			int timeout = DexterConfig.getInstance().getServerConnectionTimeOut();
			
			final ClientConfig config = new DefaultClientConfig();
			config.getClasses().add(JacksonJsonProvider.class);
			config.getFeatures().put(JSONConfiguration.FEATURE_POJO_MAPPING, Boolean.TRUE);
			config.getProperties().put(ClientConfig.PROPERTY_READ_TIMEOUT, timeout);
			config.getProperties().put(ClientConfig.PROPERTY_CONNECT_TIMEOUT, timeout);
			
			this.jsonClient = Client.create(config);
			jsonClient.setConnectTimeout(timeout);
			jsonClient.setReadTimeout(timeout);
		}
		
		return jsonClient;
	}
	
	
	private WebResource getJsonResource(final String uri, final String id, final String pwd){
		getJsonPojoMappingClient();
		final WebResource resource = jsonClient.resource(uri);
		resource.accept(APPLICATION_TYPE_JSON)
		        .type(APPLICATION_TYPE_JSON)
		        .header(AUTHORIZATION, "Basic " + DexterUtil.getBase64String(id + ":" + pwd));
		
		return resource;
	}
	
	private Client getPojoMappingClient() {
		if(pojoClient == null){
			int timeout = DexterConfig.getInstance().getServerConnectionTimeOut();
			
			final ClientConfig config = new DefaultClientConfig();
			config.getClasses().add(StringProvider.class);
			config.getProperties().put(ClientConfig.PROPERTY_READ_TIMEOUT, timeout);
			config.getProperties().put(ClientConfig.PROPERTY_CONNECT_TIMEOUT, timeout);
			config.getFeatures().put(JSONConfiguration.FEATURE_POJO_MAPPING, Boolean.TRUE);

			this.pojoClient = Client.create(config);
			pojoClient.setConnectTimeout(timeout);
			pojoClient.setReadTimeout(timeout);
			
		}

		return pojoClient;
	}
	
	private WebResource getPojoResource(final String uri, final String id, final String pwd){
		getPojoMappingClient();
		final WebResource resource = pojoClient.resource(uri);
		
		resource.accept(APPLICATION_TYPE_JSON)
		        .type(APPLICATION_TYPE_JSON)
		        .header(AUTHORIZATION, getAuthorizationValue(id, pwd));
		
		return resource;
	}
	
	private String getAuthorizationValue(final String id, final String pwd){
		return "Basic " + DexterUtil.getBase64String(id + ":" + pwd);
	}
	
	/* (non-Javadoc)
	 * @see com.samsung.sec.dexter.core.util.IDexterWebResource#getText(java.lang.String, java.lang.String, java.lang.String)
	 */
	@Override
	public String getText(final String restApiPath, final String id, final String pwd) {
		final WebResource resource = getStringResource(getServiceUrl(restApiPath));

		try {
			return resource.accept(APPLICATION_TYPE_JSON)
	        		.header(AUTHORIZATION, "Basic " + DexterUtil.getBase64String(id + ":" + pwd))
	        		.get(String.class);
		} catch (ClientHandlerException e) {
			throw new DexterRuntimeException(e.getMessage() + " : Connection refused connect. check your server is on > " + restApiPath, e);
		} catch (Exception e){
			throw new DexterRuntimeException(e.getMessage(), e);
		}
	}
	

	@SuppressWarnings("unchecked")
    @Override
	public Map<String, Object> getMap(final String restApiPath, final String id, final String pwd)  {
		final WebResource jsonResource = getJsonResource(getServiceUrl(restApiPath), id, pwd);
		
		try {
			return (Map<String, Object>) jsonResource.get(Map.class);
		} catch (ClientHandlerException e) {
			throw new DexterRuntimeException(e.getMessage() + " : Connection refused connect. check your server is on > " + getServiceUrl(restApiPath), e);
		} catch (Exception e){
			throw new DexterRuntimeException(e.getMessage(), e);
		}
	}
	
	@Override
	public String postWithBody(final String restApiPath, final String id, final String pwd, final Map<String, Object> body){
		final WebResource resource = getPojoResource(getServiceUrl(restApiPath), id, pwd);
		
		try{
			return resource.accept(APPLICATION_TYPE_JSON)
	        .type(APPLICATION_TYPE_JSON)
	        .header(AUTHORIZATION, getAuthorizationValue(id, pwd))
	        .post(String.class, body);
		} catch (ClientHandlerException e) {
			throw new DexterRuntimeException(e.getMessage() + " : Connection refused connect. check your server is on > " + getServiceUrl(restApiPath), e);
		} catch (Exception e){
			throw new DexterRuntimeException(e.getMessage(), e);
		}
	}
	
	@Override
	public String putWithBody(final String restApiPath, final String id, final String pwd, final Map<String, Object> body){
		final WebResource resource = getPojoResource(getServiceUrl(restApiPath), id, pwd);
		
		try{
			return resource.accept(APPLICATION_TYPE_JSON)
			        .type(APPLICATION_TYPE_JSON)
			        .header(AUTHORIZATION, getAuthorizationValue(id, pwd))
			        .post(String.class, body);
		} catch (ClientHandlerException e) {
			throw new DexterRuntimeException(e.getMessage() + " : Connection refused connect. check your server is on > " + getServiceUrl(restApiPath), e);
		} catch (Exception e){
			throw new DexterRuntimeException(e.getMessage(), e);
		}
	}
	
	@Override
    public String deleteWithBody(final String restApiPath, final String id, final String pwd, final Map<String, Object> body)  {
		final WebResource resource = getPojoResource(getServiceUrl(restApiPath), id, pwd);
		
		try{
			return resource.accept(APPLICATION_TYPE_JSON)
			        .type(APPLICATION_TYPE_JSON)
			        .header(AUTHORIZATION, getAuthorizationValue(id, pwd))
			        .post(String.class, body);
		} catch (ClientHandlerException e) {
			throw new DexterRuntimeException(e.getMessage() + " : Connection refused connect. check your server is on > " + getServiceUrl(restApiPath), e);
		} catch (Exception e){
			throw new DexterRuntimeException(e.getMessage(), e);
		}
	}
	
	@Override
    public String postText(final String restApiPath, final String id, final String pwd){
		final WebResource resource = getPojoResource(getServiceUrl(restApiPath), id, pwd);
		
		try{
			return resource.accept(APPLICATION_TYPE_JSON)
			        .type(APPLICATION_TYPE_JSON)
			        .header(AUTHORIZATION, getAuthorizationValue(id, pwd))
			        .post(String.class);
		} catch (ClientHandlerException e) {
			throw new DexterRuntimeException(e.getMessage() + " : Connection refused connect. check your server is on > " + getServiceUrl(restApiPath), e);
		} catch (Exception e){
			throw new DexterRuntimeException(e.getMessage(), e);
		}
	}

	public String getConnectionResult(final String uri, final String id, final String pwd) {
		final WebResource resource = getStringResource(uri);
		
		try{
			return resource.accept(APPLICATION_TYPE_JSON)
					 .header(AUTHORIZATION, getAuthorizationValue(id, pwd))
	        		.get(String.class);
		} catch (ClientHandlerException e) {
			throw new DexterRuntimeException(e.getMessage());
		} 
		
	}

	@Override
	public String postWithBodyforCLI(String restApiPath, String id, String pwd, String bodyJson) {
		final WebResource resource = getPojoResource(getServiceUrl(restApiPath), id, pwd);

		try{
			return resource.accept(APPLICATION_TYPE_JSON)
	        .type(APPLICATION_TYPE_JSON)
	        .header(AUTHORIZATION, getAuthorizationValue(id, pwd))
	        .post(String.class, bodyJson);
		} catch (ClientHandlerException e) {
			throw new DexterRuntimeException(e.getMessage() + " : Connection refused connect. check your server is on > " + getServiceUrl(restApiPath), e);
		} catch (Exception e){
			throw new DexterRuntimeException(e.getMessage(), e);
		}
	}

	@Override
	public String getServiceUrl(String serviceUrl) {
		assert serverConfig != null;
		
		return serverConfig.getServiceUrl(serviceUrl);
	}

	@Override
	public String getCurrentUserId() {
		assert serverConfig != null;
		
		return serverConfig.getUserId();
	}
	
	@Override
	public String getCurrentUserPassword() {
		assert serverConfig != null;
		
		return serverConfig.getUserPwd();
	}

	@Override
	public String getServerHostname() {
		assert serverConfig != null;
		return serverConfig.getHostname();
	}

	@Override
	public int getServerPort() {
		assert serverConfig != null;
		return serverConfig.getPort();
	}
	
	public static String getTextWithoutLogin(final String uri) {
		Client client;
		
		int timeout = DexterConfig.getInstance().getServerConnectionTimeOut();
		// Think about Pooling of Clients
		final ClientConfig config = new DefaultClientConfig();
		config.getClasses().add(StringProvider.class);
		config.getProperties().put(ClientConfig.PROPERTY_READ_TIMEOUT, timeout);
		config.getProperties().put(ClientConfig.PROPERTY_CONNECT_TIMEOUT, timeout);
		
		client = Client.create(config);
		client.setConnectTimeout(timeout);
		client.setReadTimeout(timeout);
		
		final WebResource resource = client.resource(uri);

		try {
			return resource.accept(APPLICATION_TYPE_JSON)
	        		.get(String.class);
		} catch (ClientHandlerException e) {
			throw new DexterRuntimeException(e.getMessage() + " : Connection refused connect. check your server is on > " + uri, e);
		} catch (Exception e){
			throw new DexterRuntimeException(e.getMessage(), e);
		}
	}
}
