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
package com.samsung.sec.dexter.eclipse.ui;

import org.eclipse.core.commands.AbstractHandler;
import org.eclipse.core.commands.ExecutionEvent;
import org.eclipse.core.commands.ExecutionException;
import org.eclipse.core.commands.IHandler;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Status;
import org.eclipse.core.runtime.jobs.Job;
import org.eclipse.jface.dialogs.InputDialog;
import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.swt.widgets.Display;
import org.eclipse.ui.IWorkbenchWindow;
import org.eclipse.ui.handlers.HandlerUtil;

import com.samsung.sec.dexter.core.config.ProjectAnalysisConfiguration;
import com.samsung.sec.dexter.eclipse.ui.analysis.ProjectOrFolderAnalyzer;
import com.samsung.sec.dexter.eclipse.ui.analysis.RunDexterAnalysisDialog;

public class RunDexterAnalysisHandler extends AbstractHandler implements IHandler {
	private IWorkbenchWindow window;
	
	@Override
	public Object execute(ExecutionEvent event) throws ExecutionException {
		window = HandlerUtil.getActiveWorkbenchWindowChecked(event);
		final RunDexterAnalysisDialog dialog = new RunDexterAnalysisDialog(window.getShell());
		
		if(dialog.open() == InputDialog.OK){
			final ProjectAnalysisConfiguration projectCfg = dialog.getProjectAnalysisConfiguration();
			analyze(projectCfg);
		}
		
		return null;
	}


	/*
	 * TODO: UIJob으로 실행하는 경우 리스트에 결과가 수행완료 후에 한번에 Item이 추가됨
	 * Job으로 실행하는 경우 MessageDialog 등의 UI 개체 사용을 할 수 없음
	 */
	private void analyze(final ProjectAnalysisConfiguration projectCfg) {
	    Job job = new Job("Static Analysis..."){
	    	@Override
	    	public IStatus run(IProgressMonitor monitor) {
	    		try{
	    			new ProjectOrFolderAnalyzer(projectCfg, monitor).run();
	    			
	    			return Status.OK_STATUS;
	    		} catch (final IllegalStateException e){
	    			showErrorMessage(e);
	    			return Status.CANCEL_STATUS;
	    		}
	    	}

	    	private void showErrorMessage(final IllegalStateException e) {
	            Display.getDefault().syncExec(new Runnable() {
	            	@Override
	            	public void run() {
	            		MessageDialog.openError(Display.getDefault().getActiveShell(), "Static Analysis Error", e.getMessage());
	            	}
	            });
	        }
	    };
	    
	    job.setUser(true);
	    job.schedule();
    }
}
