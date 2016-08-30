package com.samsung.sec.dexter.eclipse.ui.action;

import com.samsung.sec.dexter.core.config.DexterConfig;
import com.samsung.sec.dexter.core.exception.DexterRuntimeException;
import com.samsung.sec.dexter.core.util.IDexterClient;
import com.samsung.sec.dexter.eclipse.ui.DexterUIActivator;
import com.samsung.sec.dexter.eclipse.ui.util.EclipseUtil;
import com.samsung.sec.dexter.eclipse.ui.view.AnalysisLog;
import com.samsung.sec.dexter.eclipse.ui.view.CodeMetricsView;

import java.util.Iterator;

import org.eclipse.jface.action.IAction;
import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.jface.viewers.ISelection;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.ui.IObjectActionDelegate;
import org.eclipse.ui.IViewPart;
import org.eclipse.ui.IWorkbenchPart;

public class CodeMetricsDescription implements IObjectActionDelegate {
	private AnalysisLog analysisLog;
	private String fileName;
	private String modulePath;

	private IWorkbenchPart part;

	public CodeMetricsDescription() {

	}

	@Override
	public void run(IAction action) {
		assert analysisLog != null;

		try {
			IViewPart codeMetricsPart = EclipseUtil.findView(CodeMetricsView.ID);
			final CodeMetricsView codeMetricsView = (CodeMetricsView) codeMetricsPart;
			final IDexterClient client = DexterUIActivator.getDefault().getDexterClient();

			StringBuilder makeCodeMetricsUrl = new StringBuilder();
			makeCodeMetricsUrl.append("http://").append(client.getServerHost()).append(":") //$NON-NLS-1$ //$NON-NLS-2$
					.append(client.getServerPort()).append(DexterConfig.CODE_METRICS_BASE)// $NON-NLS-1$
					.append("?").append(DexterConfig.CODE_METRICS_FILE_NAME).append("=").append(fileName)//$NON-NLS-1$
					.append("&").append(DexterConfig.CODE_METRICS_MODULE_PATH).append("=").append(modulePath);//$NON-NLS-1$

			codeMetricsView.setUrl(makeCodeMetricsUrl.toString());

			EclipseUtil.showView(CodeMetricsView.ID);

		} catch (DexterRuntimeException e) {
			MessageDialog.openError(part.getSite().getShell(), "Code Metrics Description Error",
					"Cannot open the Code Metrics Description View");
		}
	}

	@Override
	public void selectionChanged(IAction action, ISelection selection) {
		if (!(selection instanceof IStructuredSelection)) {
			analysisLog = null;
			return;
		}

		final IStructuredSelection sel = (IStructuredSelection) selection;
		@SuppressWarnings("unchecked")
		final Iterator<Object> iter = sel.iterator();

		while (iter.hasNext()) {
			final Object obj = iter.next();

			if (obj instanceof AnalysisLog) {
				fileName = ((AnalysisLog) obj).getFileName();
				modulePath = ((AnalysisLog) obj).getModulePath();
			}
		}

	}

	@Override
	public void setActivePart(IAction action, IWorkbenchPart targetPart) {
		part = targetPart;
	}

}
