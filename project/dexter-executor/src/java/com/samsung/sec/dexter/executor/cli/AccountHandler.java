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

import com.google.common.base.Strings;
import com.samsung.sec.dexter.core.exception.DexterRuntimeException;
import com.samsung.sec.dexter.core.util.IDexterClient;

import java.io.InputStream;
import java.io.PrintStream;
import java.util.Scanner;

public class AccountHandler implements IAccountHandler {
	private IDexterClient client;
	private InputStream in = System.in;
	private ICLILog cliLog = new CLILog(System.out);

	private static int MAX_TRY_COUNT = 5;

	public AccountHandler(final IDexterClient client, final ICLILog cliLog) {
		assert client != null;
		assert cliLog != null;

		this.client = client;
		this.cliLog = cliLog;
	}

	@Override
	public boolean loginOrCreateAccount() {
		try {
			client.login(client.getCurrentUserId(), client.getCurrentUserPwd());
			return true;
		} catch (DexterRuntimeException e) {
			cliLog.errorln("Invalid userId ID(" + client.getCurrentUserId() + ") or password("
					+ client.getCurrentUserPwd() + ")");
			cliLog.infoln("You can create your account by the following command:");
			cliLog.infoln("$ chmod 755 create-user.sh");
			cliLog.infoln("$ create-user.sh -u your_id -p your_password -h dexter_server_ip -o dexter_server_port");
			cliLog.infoln("OR");
			cliLog.infoln(
					"$ java -jar dexter-executor.jar -c -u your_id -p your_password -h dexter_server_ip -o dexter_server_port");
			cliLog.infoln("If you want reset your password:");
			cliLog.infoln("$ java -jar dexter-executor.jar -r -u your_id -h dexter_server_ip -o dexter_server_port");
			return false;
		}
	}

	@Override
	public void createAccount(String userId, String password) {
		try {
			if (isValidUserId(userId) == false) {
				userId = readUserId();
			}

			if (isValidUserId(password) == false) {
				password = readPassword();
			}

			client.createAccount(userId, password, false);
			cliLog.infoln("Your account is created: " + userId);
			client.login(userId, password);
		} catch (DexterRuntimeException e1) {
			throw new RuntimeException("Can't make new account. try it again. " + e1.getMessage(), e1);
		}
	}

	private String readUserId() {
		Scanner input = new Scanner(in);
		try {
			String userId = "";
			int tryCount = 0;

			do {
				cliLog.info("Enter your ID (4 - 20 length, 'CTRL + C' to exit): ");
				userId = input.nextLine().trim();

				if (++tryCount >= MAX_TRY_COUNT) {
					throw new DexterRuntimeException("Invalid User ID");
				}
			} while (isValidUserId(userId) == false);

			return userId;
		} catch (Exception e) {
			throw new DexterRuntimeException(e.getMessage(), e);
		} finally {
			input.close();
		}

	}

	private boolean isValidUserId(final String userId) {
		if (Strings.isNullOrEmpty(userId))
			return false;

		if (userId.length() < 4 || userId.length() > 20)
			return false;

		if (client.hasAccount(userId)) {
			cliLog.infoln("Your account is already exist: " + userId);
			return false;
		}

		return true;
	}

	private String readPassword() {
		Scanner input = new Scanner(in);
		try {
			String password = "";
			int tryCount = 0;

			do {
				cliLog.info("Enter your password('CTRL + C' to exit): ");
				password = input.nextLine().trim();

				String password2 = "";
				cliLog.info("Enter your password again('CTRL + C' to exit): ");
				password2 = input.nextLine().trim();

				if (password.equals(password2) == false) {
					cliLog.infoln("Passwords are not same.");
					password = "";
				}

				if (++tryCount >= MAX_TRY_COUNT) {
					throw new DexterRuntimeException("Invalid Passowrd");
				}
			} while (isValidPassword(password) == false);

			return password;
		} catch (Exception e) {
			throw new DexterRuntimeException(e.getMessage(), e);
		} finally {
			input.close();
		}
	}

	private boolean isValidPassword(final String password) {
		if (Strings.isNullOrEmpty(password))
			return false;

		return true;
	}

	@Override
	public void setDexterClient(IDexterClient client) {
		this.client = client;
	}

	@Override
	public void setInputStream(InputStream in) {
		this.in = in;
	}

	@Override
	public void setPrintStream(PrintStream out) {
		this.cliLog.setPrintStream(out);
	}
}
