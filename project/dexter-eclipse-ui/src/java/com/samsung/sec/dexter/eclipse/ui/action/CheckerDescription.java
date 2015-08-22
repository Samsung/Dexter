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
package com.samsung.sec.dexter.eclipse.ui.action;

import java.util.Iterator;

import org.eclipse.jface.action.IAction;
import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.jface.viewers.ISelection;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.ui.IObjectActionDelegate;
import org.eclipse.ui.IViewPart;
import org.eclipse.ui.IWorkbenchPart;

import com.samsung.sec.dexter.core.config.DexterConfig;
import com.samsung.sec.dexter.core.defect.Defect;
import com.samsung.sec.dexter.core.exception.DexterRuntimeException;
import com.samsung.sec.dexter.core.util.DexterClient;
import com.samsung.sec.dexter.eclipse.ui.util.EclipseUtil;
import com.samsung.sec.dexter.eclipse.ui.view.DefectHelpView;
import com.samsung.sec.dexter.eclipse.ui.view.DefectLog;

public class CheckerDescription implements IObjectActionDelegate {
	private Defect defect;
	private IWorkbenchPart part;

	public CheckerDescription() {
	}

	@Override
	public void run(IAction action) {
		assert defect != null;
		
		try{
			IViewPart view = EclipseUtil.findView(DefectHelpView.ID);
			final DefectHelpView helpView = (DefectHelpView) view;
			
				StringBuilder url = new StringBuilder();
				url.append("http://").append(DexterClient.getInstance().getServerHost()).append(":") //$NON-NLS-1$ //$NON-NLS-2$
				.append(DexterClient.getInstance().getServerPort()).append(DexterConfig.DEFECT_HELP_BASE).append("/") //$NON-NLS-1$
				.append(defect.getToolName()).append("/").append(defect.getLanguage()).append(DexterConfig.DEFECT_HELP) //$NON-NLS-1$
				.append("/").append(defect.getCheckerCode()).append(".html"); //$NON-NLS-1$ //$NON-NLS-2$ 
				
				if(defect.getOccurences() != null && defect.getOccurences().size() == 1){
				 	url.append("#").append(defect.getFirstOccurence().getCode());
				}
				
				if(DexterClient.getInstance().hasSupportedHelpHtmlFile(url) == false){
					System.out.println(DexterClient.getInstance().hasSupportedHelpHtmlFile(url) );
					url.setLength(0);
					url.append("http://").append(DexterClient.getInstance().getServerHost()).append(":") //$NON-NLS-1$ //$NON-NLS-2$
					.append(DexterClient.getInstance().getServerPort()).append(DexterConfig.DEFECT_HELP_BASE).append("/") //$NON-NLS-1$
					.append(DexterConfig.NOT_FOUND_CHECKER_DESCRIPTION).append("/").append(DexterConfig.EMPTY_HTML_FILE_NAME).append(".html"); //$NON-NLS-1$ //$NON-NLS-2$
				}
				
				helpView.setUrl(url.toString());
			
	            EclipseUtil.showView(DefectHelpView.ID);
		} catch (DexterRuntimeException e){
			MessageDialog.openError(part.getSite().getShell(), "Checker Description Error", 
					"Cannot open the Checker Description View");
		}
	}

	@Override
	public void selectionChanged(IAction action, ISelection selection) {
		if(!(selection instanceof IStructuredSelection)){
			defect = null;
			return;
		}
		
		final IStructuredSelection sel = (IStructuredSelection) selection;
		@SuppressWarnings("unchecked")
		final Iterator<Object> iter = sel.iterator();
		
		while(iter.hasNext()){
			final Object obj = iter.next();
			
			if(obj instanceof DefectLog){
				defect = ((DefectLog) obj).getDefect();
			}
		}
	}

	@Override
	public void setActivePart(IAction action, IWorkbenchPart targetPart) {
		part = targetPart;
	}

}
