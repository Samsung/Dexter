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
package com.samsung.sec.dexter.eclipse.ui.util;

import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Plugin;
import org.eclipse.core.runtime.Status;

public class EclipseLog {
	private String pluginId;
	private Plugin activator;
	
	public EclipseLog(String pluginId){
		this.pluginId = pluginId;
	}
	
	public void setPlugin(Plugin activator){
		this.activator = activator;
	}
	
	public void log(final IStatus status){
		assert activator != null;
		
		activator.getLog().log(status);
	}
	
	public void info(final String message){
		log(IStatus.INFO, IStatus.OK, message, null);
	}
	
	public void warn(final String message){
		warn(message, null);
	}
	
	public void warn(final String message, final Throwable exception){
		log(IStatus.WARNING, IStatus.WARNING, message, exception);
	}
	
	public void error(final String message, final Throwable exception){
		log(IStatus.ERROR, IStatus.ERROR, message, exception);
	}
	
	public void error(final String message){
		error(message, null);
	}
	
	public void log(final int severity, final int code, final String message, final Throwable exception) {
		log(createStatus(severity, code, message, exception));
	}
	
	private IStatus createStatus(final int severity, final int code, final String message, final Throwable exception) {
		return new Status(severity, pluginId, code, message, exception);
	}
}
