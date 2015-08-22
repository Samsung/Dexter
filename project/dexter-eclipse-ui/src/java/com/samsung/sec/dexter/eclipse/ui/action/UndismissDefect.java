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

import java.util.HashSet;
import java.util.Iterator;
import java.util.Set;

import org.eclipse.jface.action.IAction;
import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.jface.viewers.ISelection;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.swt.widgets.Tree;
import org.eclipse.ui.IObjectActionDelegate;
import org.eclipse.ui.IWorkbenchPart;

import com.samsung.sec.dexter.core.defect.Defect;
import com.samsung.sec.dexter.core.filter.AnalysisFilterHandler;
import com.samsung.sec.dexter.eclipse.ui.DexterUIActivator;
import com.samsung.sec.dexter.eclipse.ui.view.AnalysisLogTreeView;
import com.samsung.sec.dexter.eclipse.ui.view.DefectLog;

public class UndismissDefect implements IObjectActionDelegate {
	private Set<DefectLog> defectLogList = new HashSet<DefectLog>();
	private IWorkbenchPart part;
	
	
	public UndismissDefect() {
	}

	@Override
	public void run(IAction action) {
		if(defectLogList.size() == 0){
			DexterUIActivator.LOG.error("the selection is not valid");
			return;
		}
		
		if(!(part.getSite().getPart() instanceof AnalysisLogTreeView)){
			MessageDialog.openError(part.getSite().getShell(), "error", "invalid part");
			return;
		}
		
		final AnalysisLogTreeView view = (AnalysisLogTreeView) part.getSite().getPart();
		final Tree tree = view.getLogTreeView().getTree();
		
		if(tree.getItems() == null && tree.getItems().length == 0){
			return;
		}
		
		for(final DefectLog log : defectLogList){
			if(log.isDismissed() == false){
				continue;
			}
			
			final Defect defect = log.getDefect();
			AnalysisFilterHandler.getInstance().removeDefectFilter(defect);
			log.setDismissed(false);
			
			// 트리 화면 바꾸기
			view.getLogTreeView().refresh(log);
		}
	}

	@Override
	public void selectionChanged(IAction action, ISelection selection) {
		if(!(selection instanceof IStructuredSelection)){
			defectLogList = new HashSet<DefectLog>();
			return;
		}
		
		final IStructuredSelection sel = (IStructuredSelection) selection;
		@SuppressWarnings("rawtypes")
		final Iterator iter = sel.iterator();
		
		defectLogList.clear();
		while(iter.hasNext()){
			final Object obj = iter.next();
			
			if(obj instanceof DefectLog){
				defectLogList.add((DefectLog) obj);
			}
		}
	}

	@Override
	public void setActivePart(IAction action, IWorkbenchPart targetPart) {
		this.part = targetPart;
	}
}
