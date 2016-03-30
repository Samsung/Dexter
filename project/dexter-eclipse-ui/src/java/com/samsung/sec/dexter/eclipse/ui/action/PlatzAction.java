package com.samsung.sec.dexter.eclipse.ui.action;

import org.eclipse.jface.action.IAction;
import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.jface.viewers.ISelection;
import org.eclipse.ui.IObjectActionDelegate;
import org.eclipse.ui.IViewPart;
import org.eclipse.ui.IWorkbenchPart;

import com.samsung.sec.dexter.core.config.DexterConfig;
import com.samsung.sec.dexter.core.defect.Defect;
import com.samsung.sec.dexter.core.exception.DexterRuntimeException;
import com.samsung.sec.dexter.core.util.DexterClient;
import com.samsung.sec.dexter.eclipse.ui.util.EclipseUtil;
import com.samsung.sec.dexter.eclipse.ui.view.PlatzView;


public class PlatzAction implements IObjectActionDelegate {
	private Defect defect;
	private IWorkbenchPart part;

	public PlatzAction() {
	}

	@Override
	public void run(IAction action) {
		assert defect != null;
		
		try{
			IViewPart platzPart = EclipseUtil.findView(PlatzView.ID);
			final PlatzView platzView = (PlatzView) platzPart;
			StringBuilder url = new StringBuilder();
			url.append(DexterConfig.PLATZ_API_URL).append("?dexterId=").append(DexterClient.getInstance().getCurrentUserId());
			
			platzView.setUrl(url.toString());
			EclipseUtil.showView(PlatzView.ID);
		} catch (DexterRuntimeException e){
			MessageDialog.openError(part.getSite().getShell(), "PLATZ Error", 
					"Cannot open the PLATZ View");
		}
	}
	@Override
	public void selectionChanged(IAction action, ISelection selection) {
	}
	@Override
	public void setActivePart(IAction action, IWorkbenchPart targetPart) {
		part = targetPart;
	}

}
