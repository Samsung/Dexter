package com.samsung.sec.dexter.eclipse.action;

import com.samsung.sec.dexter.core.config.DexterConfig;
import com.samsung.sec.dexter.core.exception.DexterRuntimeException;
import com.samsung.sec.dexter.core.util.IDexterClient;
import com.samsung.sec.dexter.eclipse.DexterEclipseActivator;
import com.samsung.sec.dexter.eclipse.ui.DexterUIActivator;
import com.samsung.sec.dexter.eclipse.ui.util.EclipseUtil;
import com.samsung.sec.dexter.eclipse.ui.view.CodeMetricsView;

import java.util.Iterator;

import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.IResource;
import org.eclipse.jface.action.IAction;
import org.eclipse.jface.viewers.ISelection;
import org.eclipse.jface.viewers.StructuredSelection;
import org.eclipse.ui.IObjectActionDelegate;
import org.eclipse.ui.IViewPart;
import org.eclipse.ui.IWorkbenchPart;

public class OpenCodeMetricsActionDelegate implements IObjectActionDelegate {

    ISelection selection;

    @Override
    public void run(IAction action) {
        if (selection instanceof StructuredSelection) {

            final StructuredSelection sel = (StructuredSelection) this.selection;

            @SuppressWarnings("unchecked")
            Iterator<Object> iter = sel.iterator();
            while (iter.hasNext()) {
                final Object object = iter.next();
                if (object instanceof IResource) {
                    final IResource resource = (IResource) object;
                    showCodeMetricsView(resource);
                }
            }
        }
    }

    private void showCodeMetricsView(final IResource resource) {
        if (resource instanceof IFile) {
            final IFile targetFile = (IFile) resource;
            String modulePath = "";
            if (EclipseUtil.isValidJavaResource(resource)) {
                modulePath = DexterEclipseActivator.getJDTUtil().getModulePath(targetFile);
            } else if (EclipseUtil.isValidCAndCppResource(resource)) {
                modulePath = DexterEclipseActivator.getCDTUtil().getModulePath(targetFile);
            }

            StringBuilder createCodeMetricsUrl = new StringBuilder(1024);
            try {
                IViewPart view = EclipseUtil.findView(CodeMetricsView.ID);
                final CodeMetricsView codeMetricsView = (CodeMetricsView) view;
                final IDexterClient client = DexterUIActivator.getDefault().getDexterClient();

                createCodeMetricsUrl.append("http://").append(client.getServerHost()).append(":") //$NON-NLS-1$ //$NON-NLS-2$
                        .append(client.getServerPort()).append(DexterConfig.CODE_METRICS_BASE)// $NON-NLS-1$
                        .append("?").append(DexterConfig.CODE_METRICS_FILE_NAME).append("=") //$NON-NLS-1$
                        .append(targetFile.getName()).append("&").append(DexterConfig.CODE_METRICS_MODULE_PATH) //$NON-NLS-1$
                        .append("=").append(modulePath);

                codeMetricsView.setUrl(createCodeMetricsUrl.toString());
                EclipseUtil.showView(CodeMetricsView.ID);
            } catch (DexterRuntimeException e) {
                DexterEclipseActivator.LOG.error("Cannot open the Code Metrics Description View");
                DexterEclipseActivator.LOG.error(e.getMessage(), e);
            }

        }
    }

    @Override
    public void selectionChanged(IAction action, ISelection selection) {
        this.selection = selection;
    }

    @Override
    public void setActivePart(IAction action, IWorkbenchPart targetPart) {}

}
