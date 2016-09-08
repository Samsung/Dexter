package com.samsung.sec.dexter.eclipse.ui;

import com.samsung.sec.dexter.core.config.DexterConfig;
import com.samsung.sec.dexter.core.exception.DexterRuntimeException;
import com.samsung.sec.dexter.eclipse.ui.util.EclipseUtil;
import com.samsung.sec.dexter.eclipse.ui.view.PlatzView;

import org.eclipse.core.commands.AbstractHandler;
import org.eclipse.core.commands.ExecutionEvent;
import org.eclipse.core.commands.ExecutionException;
import org.eclipse.core.commands.IHandler;
import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.swt.widgets.Display;
import org.eclipse.ui.IViewPart;

public class OpenPlatzViewHandler extends AbstractHandler implements IHandler {

    @Override
    public Object execute(ExecutionEvent event) throws ExecutionException {
        try {
            IViewPart platzPart = EclipseUtil.findView(PlatzView.ID);
            final PlatzView platzview = (PlatzView) platzPart;
            StringBuilder url = new StringBuilder(1024);
            url.append(DexterConfig.PLATZ_API_URL);
            platzview.setUrl(url.toString());
            EclipseUtil.showView(PlatzView.ID);
        } catch (DexterRuntimeException e) {
            MessageDialog.openError(Display.getDefault().getActiveShell(), "PLATZ Error", "Cannot open the PLATZ View");
            DexterUIActivator.LOG.error(e.getMessage(), e);
        }
        return null;
    }

}
