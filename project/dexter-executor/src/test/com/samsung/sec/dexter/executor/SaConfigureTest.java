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
package com.samsung.sec.dexter.executor;

import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

import org.junit.After;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;

import com.samsung.sec.dexter.core.exception.DexterRuntimeException;
import com.samsung.sec.dexter.core.util.DexterClient;

public class SaConfigureTest {
	public final static String ID = "tester";
	public final static String PWD = "testdexter";
	
	static {
		String serverPath = System.getenv("DEXTER_SERVER_HOME");
		System.out.println("DEXTER_SERVER_HOME >>>>> " +  serverPath);
		DexterClient.getInstance().setDexterServer("http://localhost", 4982);
		DexterClient.startDexterServer(serverPath);
	}
	
	
	@BeforeClass
	public static void setUpBeforeClass() throws Exception {
	}

	@AfterClass
	public static void tearDownAfterClass() throws Exception {
		System.out.println(">>>>>> DEXTER SERVER WILL BE SHUTDOWN <<<<<<<");
		
		//DexterClient.stopDexterServer("http://localhost", 4982, "tester", "testdexter");
	}

	@Before
	public void setUp() throws Exception {
	}

	@After
	public void tearDown() throws Exception {
	}

	/**
	 * login with user id and password
	 *  - id should be single id.
	 *  - password : 4~20 lenghts, allowed english, number, and special characters(~,!,@,#,$,%,^,&,*)
	 */
	@Test
	public void loginWithTestIdAndPwd() {
		try {
	        DexterClient.getInstance().login(ID + "1", PWD);
	        fail();
        } catch (DexterRuntimeException e) {
        }
		
		try {
	        DexterClient.getInstance().login(ID, PWD);
        } catch (DexterRuntimeException e) {
	        fail();
        }
		
	}
	
	/**
	 * Initialize SaConfigure for DexterAnalyzer
	 * 
	 * 1. load SA plug-ins description (FindBugs, VDPX, Prevent, etc.)
	 * 2. load SA plug-ins executor
	 * 3. load SA plug-ins checkers description
	 */
	@Test
	public void initializeBeforeExecuting() {
		DexterClient.getInstance().setDexterServer("http://localhost", 4982);
		
		String id = "tester";
		String pwd = "testdexter";
		
		
		try {
	        DexterClient.getInstance().login(id, pwd);
        } catch (DexterRuntimeException e) {
        	e.printStackTrace();
        	fail();
        }
		
		assertTrue(DexterClient.getInstance().isLogin());
	}
}
