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
package com.samsung.sec.dexter.executor.cli;

import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;
import static org.mockito.Matchers.anyBoolean;
import static org.mockito.Matchers.anyString;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import com.samsung.sec.dexter.core.util.DexterUtil;
import com.samsung.sec.dexter.core.util.EmptyDexterClient;
import com.samsung.sec.dexter.core.util.IDexterClient;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.PrintStream;

import org.junit.Before;
import org.junit.Test;

public class AccountTest {
	private IAccountHandler accountHandler = null;

	@Before
	public void createAccountObject() {
		accountHandler = new AccountHandler(new EmptyDexterClient(), new CLILog(System.out));
	}

	@Test
	public void should_create_account_when_id_is_valid() {
		final String[] testShortIds = { "abcd", "abcde", "1234", "12345678901234567890" };

		for (String id : testShortIds) {
			IDexterClient client = mock(IDexterClient.class);

			when(client.hasAccount(anyString())).thenReturn(false);

			accountHandler.setDexterClient(client);
			accountHandler.createAccount(id, "user-password");

			verify(client).login(anyString(), anyString());
			verify(client).createAccount(anyString(), anyString(), anyBoolean());
		}
	}

	@Test
	public void should_ask_account_input_when_id_is_too_short() throws IOException {
		final String[] testShortIds = { "", "a", "b", "abc" };

		for (String id : testShortIds) {
			IDexterClient client = mock(IDexterClient.class);

			// when(client.hasAccount(anyString())).thenReturn(true);
			when(client.hasAccount("12345")).thenReturn(false);

			ByteArrayOutputStream byteOut = new ByteArrayOutputStream();
			PrintStream out = new PrintStream(byteOut);
			out.flush();

			ByteArrayInputStream byteIn = createTestInputStream();

			accountHandler.setDexterClient(client);
			accountHandler.setInputStream(byteIn);
			accountHandler.setPrintStream(out);

			accountHandler.createAccount(id, "user-password");

			String testResult = "Enter your ID (4 - 20 length, 'CTRL + C' to exit):";
			assertTrue(byteOut.toString().startsWith(testResult));

			verify(client).login(anyString(), anyString());
			verify(client).createAccount(anyString(), anyString(), anyBoolean());

			byteOut.close();
			out.close();
			byteIn.close();
		}
	}

	@Test
	public void should_ask_account_input_when_id_is_too_long() throws IOException {
		final String[] testShortIds = { "123456789012345678901", "123456789012345678902" };

		for (String id : testShortIds) {
			IDexterClient client = mock(IDexterClient.class);

			when(client.hasAccount("12345")).thenReturn(false);

			ByteArrayOutputStream byteOut = new ByteArrayOutputStream();
			PrintStream out = new PrintStream(byteOut);
			out.flush();
			ByteArrayInputStream byteIn = createTestInputStream();

			accountHandler.setDexterClient(client);
			accountHandler.setInputStream(byteIn);
			accountHandler.setPrintStream(out);

			accountHandler.createAccount(id, "user-password");

			String testResult = "Enter your ID (4 - 20 length, 'CTRL + C' to exit):";
			assertTrue(byteOut.toString().startsWith(testResult));

			verify(client).login(anyString(), anyString());
			verify(client).createAccount(anyString(), anyString(), anyBoolean());

			byteOut.close();
			out.close();
			byteIn.close();
		}
	}

	@Test
	public void should_ask_account_when_id_exists() throws IOException {
		IDexterClient client = mock(IDexterClient.class);

		when(client.hasAccount(anyString())).thenReturn(true);
		accountHandler.setDexterClient(client);

		ByteArrayInputStream byteIn = createTestInputStream();
		accountHandler.setInputStream(byteIn);
		
		try {
			accountHandler.createAccount("1234", "user-password");
			fail();
		} catch (RuntimeException e) {
		} finally {
			byteIn.close();
		}
	}

	private ByteArrayInputStream createTestInputStream() {
		String sampleInput = "12345" + DexterUtil.LINE_SEPARATOR;
		ByteArrayInputStream byteIn = new ByteArrayInputStream(sampleInput.getBytes());
		return byteIn;
	}
}
