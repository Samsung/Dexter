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

import com.samsung.sec.dexter.core.exception.DexterException;

public interface IDexterWebResource {
	/**
	 * RESTful API 
	 * METHOD: GET
	 * 
	 * @param uri
	 * @param id
	 * @param pwd
	 * @return
	 * @throws DexterException
	 * @return String
	 */
	public String getText(final String uri, final String id, final String pwd);

	/**
	 * RESTful API
	 * METHOD: GET
	 * 
	 * @param uri
	 * @param id
	 * @param pwd
	 * @return
	 * @throws DexterException
	 * @return Map<String,Object>
	 */
	public Map<String, Object> getMap(final String uri, final String id, final String pwd);

	/**
	 * RESTful API
	 * METHOD: POST
	 * 
	 * @param uri
	 * @param id
	 * @param pwd
	 * @return
	 * @throws DexterException
	 * @return String
	 */
	public abstract String postText(final String uri, final String id, final String pwd);
	
	/**
	 * RESTful API
	 * METHOD: POST
	 * 
	 * @param uri
	 * @param id
	 * @param pwd
	 * @param body	request.body
	 * @return
	 * @throws DexterException
	 * @return String
	 */
	public abstract String postWithBody(final String uri, final String id, final String pwd, final Map<String, Object> body);
	
	public abstract String postWithBodyforCLI(final String uri, final String id, final String pwd, final String bodyJson);


	/**
	 * RESTful API
	 * METHOD: DELETE
	 * 
	 * @param uri
	 * @param id
	 * @param pwd
	 * @param body	reqeust.body
	 * @return
	 * @throws DexterException
	 * @return String
	 */
	public abstract String deleteWithBody(final String uri, final String id, final String pwd, final Map<String, Object> body);

	/**
	 * RESTful API
	 * METHOD: PUT
	 * 
	 * @param uri
	 * @param id
	 * @param pwd
	 * @param body
	 * @return
	 * @throws DexterException
	 * @return String
	 */
	public String putWithBody(String uri, String id, String pwd, Map<String, Object> body);
	
	/**
	 * RESTful API
	 * METHOD: GET
	 * 
	 * @param uri
	 * @param id
	 * @param pwd
	 * @param body
	 * @return
	 * @throws DexterException
	 * @return String
	 */
	
	public String getConnectionResult(final String uri, final String id, final String pwd);		
}
