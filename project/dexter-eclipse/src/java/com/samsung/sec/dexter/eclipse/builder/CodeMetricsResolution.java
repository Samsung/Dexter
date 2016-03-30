package com.samsung.sec.dexter.eclipse.builder;

import org.eclipse.core.resources.IMarker;
import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.swt.graphics.Image;
import org.eclipse.ui.IMarkerResolution2;
import org.eclipse.ui.IViewPart;
import org.eclipse.ui.PlatformUI;

import com.samsung.sec.dexter.core.config.DexterConfig;
import com.samsung.sec.dexter.core.exception.DexterRuntimeException;
import com.samsung.sec.dexter.core.util.DexterClient;
import com.samsung.sec.dexter.eclipse.DexterEclipseActivator;
import com.samsung.sec.dexter.eclipse.ui.util.EclipseUtil;
import com.samsung.sec.dexter.eclipse.ui.view.CodeMetricsView;

public class CodeMetricsResolution implements IMarkerResolution2 {

	@Override
	public String getLabel() {
		return Messages.CodeMetricsResolution_SHOW_CODE_METRICS_DESC;
	}

	@Override
	public void run(IMarker marker) {
		try {
			final IViewPart view = EclipseUtil.findView(CodeMetricsView.ID);
			final CodeMetricsView codeMetricsView = (CodeMetricsView) view;
			final StringBuilder url = new StringBuilder();
			
			url.append("http://").append(DexterClient.getInstance().getServerHost()).append(":") //$NON-NLS-1$ //$NON-NLS-2$
			.append(DexterClient.getInstance().getServerPort()).append(DexterConfig.CODE_METRICS_BASE);//$NON-NLS-1$
			//.append("?").append(DexterConfig.CODE_METRICS_FILE_NAME).append("=").append(fileName)//$NON-NLS-1$
			//.append("&").append(DexterConfig.CODE_METRICS_MODULE_PATH).append("=").append(modulePath);//$NON-NLS-1$
			
			codeMetricsView.setUrl(url.toString());
			EclipseUtil.showView(CodeMetricsView.ID);
		} catch (DexterRuntimeException e) {
			DexterEclipseActivator.LOG.error(e.getMessage(), e);
			MessageDialog.openConfirm(PlatformUI.getWorkbench().getActiveWorkbenchWindow().getShell(),
			        Messages.CodeMetricsResolution_CODE_METRICS_ERROR,
			        Messages.CodeMetricsResolution_CODE_METRICS_ERROR_DESC);
		}

	}

	@Override
	public String getDescription() {
		return Messages.CodeMetricsResolution_CODE_METRICS_DESC;
		
	}

	@Override
	public Image getImage() {
		return EclipseUtil.getImage(DexterEclipseActivator.PLUGIN_ID, "/icons/codeMetricsView7.gif"); //$NON-NLS-1$
	}

}
