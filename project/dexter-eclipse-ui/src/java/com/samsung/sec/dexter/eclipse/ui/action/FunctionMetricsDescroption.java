package com.samsung.sec.dexter.eclipse.ui.action;

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

import com.samsung.sec.dexter.core.config.DexterConfig;
import com.samsung.sec.dexter.core.exception.DexterRuntimeException;
import com.samsung.sec.dexter.core.util.DexterClient;
//import com.samsung.sec.dexter.eclipse.ui.notifier.NotificationType;
//import com.samsung.sec.dexter.eclipse.ui.notifier.NotifierDialog;
import com.samsung.sec.dexter.eclipse.ui.util.EclipseUtil;
import com.samsung.sec.dexter.eclipse.ui.view.AnalysisLog;
import com.samsung.sec.dexter.eclipse.ui.view.FunctionMetricsView;

public class FunctionMetricsDescroption implements IObjectActionDelegate {
	
	static Logger logger = Logger.getLogger(FunctionMetricsDescroption.class);

	private AnalysisLog analysisLog;
	private String fileName;
	private String modulePath;
	
	private List<String> functionList ;

	private IWorkbenchPart part;
	
	public FunctionMetricsDescroption() {
	}

	@Override
	public void run(IAction action) {
		assert analysisLog != null;
		
		try{
		IViewPart view = EclipseUtil.findView(FunctionMetricsView.ID);
		final FunctionMetricsView functionMetricsView = (FunctionMetricsView) view;
		
		StringBuilder url = new StringBuilder();
		url.append("http://").append(DexterClient.getInstance().getServerHost()).append(":") //$NON-NLS-1$ //$NON-NLS-2$
		.append(DexterClient.getInstance().getServerPort()).append(DexterConfig.FUNCTION_METRICS_BASE)//$NON-NLS-1$
		.append("?").append(DexterConfig.CODE_METRICS_FILE_NAME).append("=").append(fileName)//$NON-NLS-1$
		.append("&").append(DexterConfig.CODE_METRICS_MODULE_PATH).append("=").append(modulePath);//$NON-NLS-1$
		
		for(int i=0;i<functionList.size(); i++){
			if(i==0){
				url.append("&").append(DexterConfig.FUNCTION_METRICS_FUNCTION_LIST).append("=").append(functionList.get(i));
			}else{
				url.append(",").append(functionList.get(i));
			}
		}
		
		functionMetricsView.setUrl(url.toString());
		EclipseUtil.showView(FunctionMetricsView.ID);	
		
		//NotifierDialog.notify("[Dexter] Need to confirm Code Metrics (Function)",  "This require immediate attention" + fileName, NotificationType.values()[1]);
		
		}
		catch (DexterRuntimeException e){
			MessageDialog.openError(part.getSite().getShell(), "Code Metrics (Fuction) Description Error", 
					"Cannot open the Code Metrics (Fuction) Description View");
		}
	}

	@Override
	public void selectionChanged(IAction action, ISelection selection) {
		if(!(selection instanceof IStructuredSelection)){
			analysisLog = null;
			return;
		}
		
		final IStructuredSelection sel = (IStructuredSelection) selection;
		@SuppressWarnings("unchecked")
		final Iterator<Object> iter = sel.iterator();
		
		while(iter.hasNext()){
			final Object obj = iter.next();
			
			if(obj instanceof AnalysisLog){
				fileName = ((AnalysisLog) obj).getFileName();
				modulePath = ((AnalysisLog) obj).getModulePath();
				functionList = ((AnalysisLog) obj).getFunctionList();
			} 
		}


	}

	@Override
	public void setActivePart(IAction action, IWorkbenchPart targetPart) {
		// TODO Auto-generated method stub

	}

}
