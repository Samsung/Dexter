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
*/package com.samsung.sec.dexter.executor.cli;

import java.io.InputStream;
import java.io.PrintStream;
import java.util.Scanner;

import com.google.common.base.Strings;
import com.samsung.sec.dexter.core.exception.DexterRuntimeException;
import com.samsung.sec.dexter.core.util.DexterClient;
import com.samsung.sec.dexter.core.util.IDexterClient;

public class Account implements IAccount{
	private IDexterClient client = DexterClient.getInstance();
	private InputStream in = System.in;
	private PrintStream out = System.out;
	private static int MAX_TRY_COUNT = 3;
	
	
	@Override
    public void createAccount(String userId, String password) {
		try{
			if(isValidUserId(userId) == false){
				userId = readUserId();
			}
			
			if(isValidUserId(password) == false){
				password = readPassword();
			}
			
	    	client.createAccount(userId, password, false);
	    	client.login(userId, password);
	    } catch (DexterRuntimeException e1){
	    	throw new RuntimeException("Can't make new account. try it again. " + e1.getMessage(), e1);
	    }
    }
	
	private String readUserId() {
		Scanner input = new Scanner(in);
		try{
			String userId = "";
			int tryCount = 0;
			
			do {
				out.print("Enter your ID (4 - 20 length, 'CTRL + C' to exit): ");
				userId = input.nextLine().trim();
				
				if(++tryCount >= MAX_TRY_COUNT){
					throw new DexterRuntimeException("Invalid User ID");
				}
			} while (isValidUserId(userId) == false);
			
			return userId;
		} catch (Exception e){
			throw new DexterRuntimeException(e.getMessage(), e);
		} finally {
			input.close();
		}
		
    }
	
	private boolean isValidUserId(final String userId){
		if(Strings.isNullOrEmpty(userId)) return false;
		
		if(userId.length() < 4 || userId.length() > 20) return false;
		
		if(client.hasAccount(userId)){
			out.println("Your account is already exist: " + userId);
			return false;
		}
		
		return true;
	}
	
	private String readPassword() {
		Scanner input = new Scanner(in);
		try{
			String password = "";
			int tryCount = 0;
			
			do {
				out.print("Enter your password('CTRL + C' to exit): ");
				password = input.nextLine().trim();
			
				String password2 = "";
				out.print("Enter your password again('CTRL + C' to exit): ");
				password2 = input.nextLine().trim();
				
				if(password.equals(password2) == false){
					out.println("Passwords are not same.");
					password = "";
				}
				
				if(++tryCount >= MAX_TRY_COUNT){
					throw new DexterRuntimeException("Invalid Passowrd");
				}
			} while (isValidPassword(password) == false);
			
			return password;
		} catch (Exception e){
			throw new DexterRuntimeException(e.getMessage(), e);
		} finally {
			input.close();
		}
    }
	
	private boolean isValidPassword(final String password){
		if(Strings.isNullOrEmpty(password)) return false;
		
		return true;
	}

	public void setDexterClient(IDexterClient client) {
	    this.client = client;
    }
	
	public void setInputStream(InputStream in){
		this.in = in;
	}
	
	public void setPrintStream(PrintStream out){
		this.out = out;
	}
}
