package com.samsung.sec.dexter.eclipse.ui;

import org.eclipse.core.commands.AbstractHandler;
import org.eclipse.core.commands.ExecutionEvent;
import org.eclipse.core.commands.ExecutionException;
import org.eclipse.core.commands.IHandler;
import org.eclipse.ui.IViewPart;

import com.samsung.sec.dexter.core.config.DexterConfig;

import com.samsung.sec.dexter.core.exception.DexterRuntimeException;
import com.samsung.sec.dexter.core.util.DexterClient;
import com.samsung.sec.dexter.eclipse.ui.util.EclipseUtil;
import com.samsung.sec.dexter.eclipse.ui.view.PlatzView;

public class OpenPlatzViewHandler extends AbstractHandler implements IHandler {
	@Override
	public Object execute(ExecutionEvent event) throws ExecutionException {
		try {
			IViewPart platzPart = EclipseUtil.findView(PlatzView.ID);
			final PlatzView platzview = (PlatzView) platzPart;
			StringBuilder url = new StringBuilder();
			url.append(DexterConfig.PLATZ_API_URL).append("?dexterId=").append(DexterClient.getInstance().getCurrentUserId());
			
			platzview.setUrl(url.toString());
			EclipseUtil.showView(PlatzView.ID);
		} catch (DexterRuntimeException e) {
			EclipseUtil.errorMessageBox("Open Error", "Cannot open the PLATZ View");
		}

		return null;
	}

	
	

}
