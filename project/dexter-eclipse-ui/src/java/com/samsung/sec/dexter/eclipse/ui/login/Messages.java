/**
 * Copyright (c) 2014 Samsung Electronics, Inc.,
 * All rights reserved.
 * 
 * Redistribution and use in source and binary forms, with or without
 * modification, are permitted provided that the following conditions are met:
 * 
 * * Redistributions of source code must retain the above copyright notice, this
 * list of conditions and the following disclaimer.
 * 
 * * Redistributions in binary form must reproduce the above copyright notice,
 * this list of conditions and the following disclaimer in the documentation
 * and/or other materials provided with the distribution.
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
package com.samsung.sec.dexter.eclipse.ui.login;

import org.eclipse.osgi.util.NLS;

public class Messages extends NLS {
    private static final String BUNDLE_NAME = "com.samsung.sec.dexter.eclipse.ui.login.messages"; //$NON-NLS-1$
    public static String LoginDialog_ACCOUNT_ASK_POSTFIX_MSG;
    public static String LoginDialog_ACCOUNT_ASK_PREFIX_MSG;
    public static String LoginDialog_ACCOUNT_CREATED_MSG;
    public static String LoginDialog_ACCOUNT_ERROR_MSG;
    public static String LoginDialog_CHECK_SERVER_MSG;
    public static String LoginDialog_CREATE_ACCOUNT_DIALOG_TITLE;
    public static String LoginDialog_DEXTER_HOME_DESC;
    public static String LoginDialog_DEXTER_HOME_DIALOG_DESC;
    public static String LoginDialog_DEXTER_HOME_DIALOG_TITLE;
    public static String LoginDialog_DEXTER_HOME_ERROR_MSG;
    public static String LoginDialog_DEXTER_HOME_SPACE_ERROR_MSG;
    public static String LoginDialog_DEXTER_LOGIN_DIALOG_DESC;
    public static String LoginDialog_DEXTER_LOGIN_DIALOG_TITLE;
    public static String LoginDialog_DEXTER_SERVER_DESC;
    public static String LoginDialog_FAIL_TO_CREATE_ACCOUNT_MSG;
    public static String LoginDialog_FIND;
    public static String LoginDialog_HANDLE_LOGIN_MSG;
    public static String LoginDialog_ID_VAL_MSG;
    public static String LoginDialog_INIT_ENV_MSG;
    public static String LoginDialog_LOGIN_ERROR_MSG;
    public static String LoginDialog_LOGIN_GUIDE_MSG;
    public static String LoginDialog_LOGIN_STATUS_MSG;
    public static String LoginDialog_NETWORK_ERROR_MSG;
    public static String LoginDialog_NETWORK_OK_MSG;
    public static String LoginDialog_NETWORK_TEST;
    public static String LoginDialog_NETWORK_TEST_MSG;
    public static String LoginDialog_NETWORK_TESTING_MSG;
    public static String LoginDialog_NO_ACCOUNT_ERROR_MSG;
    public static String LoginDialog_ID_DESC;
    public static String LoginDialog_PASSWORD_DESC;
    public static String LoginDialog_PASSWORD_ERROR_MSG;
    public static String LoginDialog_PASSWORD_LENGTH_VAL_MSG;
    public static String LoginDialog_PASSWORD_VAL_MSG;
    public static String LoginDialog_SERVER_ERROR_MSG;
    public static String LoginDialog_SHELL_TITLE;

    static {
        // initialize resource bundle
        NLS.initializeMessages(BUNDLE_NAME, Messages.class);
    }

    private Messages() {}
}
