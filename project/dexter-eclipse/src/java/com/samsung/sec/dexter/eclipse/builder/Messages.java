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
package com.samsung.sec.dexter.eclipse.builder;

import org.eclipse.osgi.util.NLS;

public class Messages extends NLS {
	private static final String BUNDLE_NAME = "com.samsung.sec.dexter.eclipse.builder.messages"; //$NON-NLS-1$
	public static String DefectHelpResolution_DEFECT_HELP_DESC;
	public static String DefectHelpResolution_DEFECT_HELP_ERROR;
	public static String DefectHelpResolution_DEFECT_HELP_ERROR_DESC;
	public static String DefectHelpResolution_SHOW_DEFECT_DESC;
	public static String DismissDefectResolution_DISMISS_DESC;
	public static String DismissDefectResolution_DISMISS_ERROR_MSG;
	public static String DismissDefectResolution_DISMISS_LABEL;
	public static String GetGlobalIdResolution_GET_GLOBAL_ID_DESC;
	public static String GetGlobalIdResolution_GET_GLOBAL_ID_LABEL;
	public static String UndismissDefectResolution_UNDISMISS_DESC;
	public static String UndismissDefectResolution_UNDISMISS_ERROR;
	public static String UndismissDefectResolution_UNDISMISS_LABEL;
	static {
		// initialize resource bundle
		NLS.initializeMessages(BUNDLE_NAME, Messages.class);
	}

	private Messages() {
	}
}
