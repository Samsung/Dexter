package com.samsung.sec.dexter.eclipse.ui.action;

import com.samsung.sec.dexter.core.config.DexterConfig;
import com.samsung.sec.dexter.core.exception.DexterRuntimeException;
import com.samsung.sec.dexter.core.util.IDexterClient;
import com.samsung.sec.dexter.eclipse.ui.DexterUIActivator;
// import com.samsung.sec.dexter.eclipse.ui.notifier.NotificationType;
// import com.samsung.sec.dexter.eclipse.ui.notifier.NotifierDialog;
import com.samsung.sec.dexter.eclipse.ui.util.EclipseUtil;
import com.samsung.sec.dexter.eclipse.ui.view.AnalysisLog;
import com.samsung.sec.dexter.eclipse.ui.view.FunctionMetricsView;

import java.util.Iterator;
import java.util.List;

import org.apache.log4j.Logger;
import org.eclipse.jface.action.IAction;
import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.jface.viewers.ISelection;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.ui.IObjectActionDelegate;
import org.eclipse.ui.IViewPart;
import org.eclipse.ui.IWorkbenchPart;

public class FunctionMetricsDescription implements IObjectActionDelegate {

    static Logger logger = Logger.getLogger(FunctionMetricsDescription.class);

    private AnalysisLog analysisLog;
    private String fileName;
    private String modulePath;

    private List<String> functionList;

    private IWorkbenchPart part;

    public FunctionMetricsDescription() {}

    @Override
    public void run(IAction action) {
        assert analysisLog != null;

        try {
            IViewPart functionMetricsPart = EclipseUtil.findView(FunctionMetricsView.ID);
            final FunctionMetricsView functionMetricsView = (FunctionMetricsView) functionMetricsPart;
            IDexterClient client = DexterUIActivator.getDefault().getDexterClient();

            StringBuilder makeFunctionMetricsUrl = new StringBuilder(1024);
            makeFunctionMetricsUrl.append("http://").append(client.getServerHost()).append(":") //$NON-NLS-1$ //$NON-NLS-2$
                    .append(client.getServerPort()).append(DexterConfig.FUNCTION_METRICS_BASE)// $NON-NLS-1$
                    .append("?").append(DexterConfig.CODE_METRICS_FILE_NAME).append("=").append(fileName)//$NON-NLS-1$
                    .append("&").append(DexterConfig.CODE_METRICS_MODULE_PATH).append("=").append(modulePath);//$NON-NLS-1$

            if (functionList.size() > 0) {
                makeFunctionMetricsUrl.append("&").append(DexterConfig.FUNCTION_METRICS_FUNCTION_LIST).append("=")
                        .append(functionList.get(0));
                for (int i = 0; i < functionList.size(); i++) {
                    makeFunctionMetricsUrl.append(",").append(functionList.get(i));
                }
            }

            makeFunctionMetricsUrl.trimToSize();
            functionMetricsView.setUrl(makeFunctionMetricsUrl.toString());
            EclipseUtil.showView(FunctionMetricsView.ID);

        } catch (DexterRuntimeException e) {
            MessageDialog.openError(part.getSite().getShell(), "Code Metrics (Fuction) Description Error",
                    "Cannot open the Code Metrics (Fuction) Description View");
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
                functionList = ((AnalysisLog) obj).getFunctionList();
            }
        }

    }

    @Override
    public void setActivePart(IAction action, IWorkbenchPart targetPart) {}

}
