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
package com.samsung.sec.dexter.eclipse.ui.view;

import java.io.File;
import java.io.IOException;
import java.util.Date;
import java.util.List;

import org.eclipse.core.filesystem.EFS;
import org.eclipse.core.filesystem.IFileStore;
import org.eclipse.core.resources.IFile;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.preferences.InstanceScope;
import org.eclipse.jface.action.Action;
import org.eclipse.jface.action.IMenuManager;
import org.eclipse.jface.action.IStatusLineManager;
import org.eclipse.jface.action.MenuManager;
import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.jface.text.BadLocationException;
import org.eclipse.jface.text.IDocument;
import org.eclipse.jface.viewers.AbstractTreeViewer;
import org.eclipse.jface.viewers.DoubleClickEvent;
import org.eclipse.jface.viewers.IDoubleClickListener;
import org.eclipse.jface.viewers.ISelectionChangedListener;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.jface.viewers.SelectionChangedEvent;
import org.eclipse.jface.viewers.TreeViewer;
import org.eclipse.jface.viewers.Viewer;
import org.eclipse.jface.viewers.ViewerSorter;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.FileDialog;
import org.eclipse.swt.widgets.Text;
import org.eclipse.swt.widgets.Tree;
import org.eclipse.swt.widgets.TreeColumn;
import org.eclipse.swt.widgets.TreeItem;
import org.eclipse.ui.IEditorPart;
import org.eclipse.ui.IMemento;
import org.eclipse.ui.IViewSite;
import org.eclipse.ui.IWorkbenchPage;
import org.eclipse.ui.PartInitException;
import org.eclipse.ui.PlatformUI;
import org.eclipse.ui.XMLMemento;
import org.eclipse.ui.ide.IDE;
import org.eclipse.ui.part.ViewPart;
import org.eclipse.ui.texteditor.ITextEditor;
import org.osgi.service.prefs.BackingStoreException;
import org.osgi.service.prefs.Preferences;

import com.google.common.base.Strings;
import com.samsung.sec.dexter.core.analyzer.AnalysisConfig;
import com.samsung.sec.dexter.core.analyzer.AnalysisResult;
import com.samsung.sec.dexter.core.config.DexterConfig;
import com.samsung.sec.dexter.core.config.IDexterHomeListener;
import com.samsung.sec.dexter.core.defect.Defect;
import com.samsung.sec.dexter.core.util.DexterClient;
import com.samsung.sec.dexter.core.util.DexterUtil;
import com.samsung.sec.dexter.core.util.PersistenceProperty;
import com.samsung.sec.dexter.eclipse.ui.DexterUIActivator;
import com.samsung.sec.dexter.eclipse.ui.util.EclipseUtil;
import com.samsung.sec.dexter.executor.DexterAnalyzer;
import com.samsung.sec.dexter.executor.DexterAnalyzerAdapter;

public class AnalysisLogTreeView extends ViewPart implements IDexterHomeListener{
	public static final String ID = "com.samsung.sec.dexter.eclipse.ui.view.AnalysisLogTreeView";
	public static final String P_SHOW_TEXT_AREA = "show_text_area"; 
	public static final String P_COLUMN_1 = "column1"; 
	public static final String P_COLUMN_2 = "column2"; 
	public static final String P_COLUMN_3 = "column3"; 
	public static final String P_COLUMN_4 = "column4"; 
	public static final String P_COLUMN_5 = "column5"; 
	public static final String P_COLUMN_6 = "column6"; 
	public static final String P_COLUMN_7 = "column7"; 
	public static final String P_ORDER_VALUE = "orderValue";
	
	private TreeViewer logTreeView;
	private Tree tree;
	private RootAnalysisLog rootLog;
	private AnalyzerHandler analyzerHandler = new AnalyzerHandler(); 
	private Display display;
	private Text messageText;
	private IMemento fMemento;
	
	private TreeColumn fColumn1;
	private TreeColumn fColumn2;
	private TreeColumn fColumn3;
	private TreeColumn fColumn4;
	private TreeColumn fColumn5;
	private TreeColumn fColumn6;
	private TreeColumn fColumn7;
	
	private int ITEM_ORDER;
	public static final int ASCENDING = 1;
	public static final int DESCENDING = -1;
	
	public AnalysisLogTreeView() {
		rootLog = new RootAnalysisLog();
		rootLog.loadFromLogFiles();
	}
	
	@Override
	public void createPartControl(final Composite parent) {
		display = parent.getDisplay();
		
		final GridLayout layout = new GridLayout();
		layout.numColumns = 1;
		parent.setLayout(layout);
		
		messageText = new Text(parent, SWT.MULTI | SWT.READ_ONLY | SWT.BORDER | SWT.V_SCROLL | SWT.WRAP);
		messageText.setBackground(Display.getDefault().getSystemColor(SWT.COLOR_WHITE));
		
		GridData gridData = new GridData();
		gridData.horizontalAlignment = GridData.FILL;
		gridData.heightHint = 100;
		gridData.minimumHeight = 100;
		messageText.setLayoutData(gridData);
		
		tree = new Tree(parent, SWT.BORDER | SWT.H_SCROLL | SWT.V_SCROLL | SWT.MULTI | SWT.FULL_SELECTION);
		tree.setHeaderVisible(true);
		tree.setLinesVisible(true);
		
		fColumn1 = new TreeColumn(tree, SWT.CENTER);
		fColumn1.setText("Items");
		fColumn1.setAlignment(SWT.LEFT);
		fColumn1.setWidth(fMemento.getInteger(P_COLUMN_1));
		fColumn1.addSelectionListener(new SelectionAdapter() {
			public void widgetSelected(SelectionEvent e) {
				ITEM_ORDER *= -1;
				logTreeView.setSorter(new LogTreeViewerSorter());
				fMemento.putInteger(P_ORDER_VALUE, ITEM_ORDER);
				tree.setSortColumn(fColumn1);
				tree.setSortDirection(ITEM_ORDER == ASCENDING ? SWT.UP : SWT.DOWN);
			}
		});
		tree.setSortColumn(fColumn1);
		tree.setSortDirection(ITEM_ORDER == ASCENDING ? SWT.UP : SWT.DOWN);
		
		fColumn2 = new TreeColumn(tree, SWT.CENTER);
		fColumn2.setText("Status");
		fColumn2.setAlignment(SWT.CENTER);
		fColumn2.setWidth(fMemento.getInteger(P_COLUMN_2));
		
		fColumn3 = new TreeColumn(tree, SWT.CENTER);
		fColumn3.setText("Severity");
		fColumn3.setAlignment(SWT.CENTER);
		fColumn3.setWidth(fMemento.getInteger(P_COLUMN_3));
		
		fColumn4 = new TreeColumn(tree, SWT.CENTER);
		fColumn4.setText("Module");
		fColumn4.setAlignment(SWT.LEFT);
		fColumn4.setWidth(fMemento.getInteger(P_COLUMN_4));
		
		fColumn5 = new TreeColumn(tree, SWT.CENTER);
		fColumn5.setText("Class");
		fColumn5.setAlignment(SWT.LEFT);
		fColumn5.setWidth(fMemento.getInteger(P_COLUMN_5));
		
		fColumn6 = new TreeColumn(tree, SWT.CENTER);
		fColumn6.setText("Method");
		fColumn6.setAlignment(SWT.LEFT);
		fColumn6.setWidth(fMemento.getInteger(P_COLUMN_6));
		
		fColumn7 = new TreeColumn(tree, SWT.CENTER);
		fColumn7.setText("Message");
		fColumn7.setAlignment(SWT.LEFT);
		fColumn7.setWidth(fMemento.getInteger(P_COLUMN_7));
		
		gridData = new GridData(GridData.FILL_BOTH);
		gridData.horizontalAlignment = GridData.FILL;
		gridData.verticalAlignment = GridData.FILL;
		tree.setLayoutData(gridData);
		
		
		logTreeView = new TreeViewer(tree);
		logTreeView.setLabelProvider(new AnalysisLogLabelProvider());
		logTreeView.setContentProvider(new AnalysisLogContentProvider());
		logTreeView.setSorter(new LogTreeViewerSorter());
		logTreeView.setInput(rootLog);
		logTreeView.getSorter().sort(logTreeView, tree.getItems());
		
		logTreeView.addDoubleClickListener(new IDoubleClickListener() {
			@Override
			public void doubleClick(DoubleClickEvent event) {
				final String sourceInsightExe = PersistenceProperty.getInstance().read(DexterConfig.SOURCE_INSIGHT_EXE_PATH_KEY);
				if(Strings.isNullOrEmpty(sourceInsightExe)){
					final FileDialog dialog = new FileDialog(parent.getShell(), SWT.OPEN);
					dialog.setText("Select your Source Insight executable file : Insight3.exe");
					final String[] filterExt = { "*.exe" };
					dialog.setFilterExtensions(filterExt);
					
					final String exePath = dialog.open();
					
					if(Strings.isNullOrEmpty(exePath)){
						MessageDialog.openError(parent.getShell(), "Error for Insight3.exe" , "You have to select Insight3.exe file first");
						DexterUIActivator.LOG.error("You have to select Insight3.exe file first");
						return;
					}

					DexterUIActivator.LOG.info("set Insight3.exe file path :" + exePath);
					PersistenceProperty.getInstance().write(DexterConfig.SOURCE_INSIGHT_EXE_PATH_KEY, exePath);
				}
				
				if(event.getSelection().isEmpty() || !(event.getSelection() instanceof IStructuredSelection)){
					DexterUIActivator.LOG.error("Invalid Selection : it is not IStructuredSelection");
					return;
				}
				
				final Object target = ((IStructuredSelection) event.getSelection()).getFirstElement();
				
				if(target instanceof DefectLog){
					// XXX Needs to implement
				} else if(target instanceof OccurenceLog){
					display.asyncExec(new Runnable() {
                        public void run() {
	                    	final OccurenceLog log = (OccurenceLog) target;
	                    	final DexterConfig.LANGUAGE lang = log.getParent().getDefect().getLanguageEnum();
	    					
	                    	final String sourceInsightExe = PersistenceProperty.getInstance().read(DexterConfig.SOURCE_INSIGHT_EXE_PATH_KEY);
	    					if((DexterConfig.LANGUAGE.C == lang || DexterConfig.LANGUAGE.CPP == lang) 
	    							&& !Strings.isNullOrEmpty(sourceInsightExe)){
	    						final String fileFullPath = log.getParent().getParent().getFileFullPath();
	    						final int line = log.getOccurence().getStartLine();
	    						if(line == -1){
	    							DexterUIActivator.LOG.error("cannot open the SourceInsight because line is -1");
	    							return;
	    						}
	    						
	    						
	    						final StringBuilder cmd = new StringBuilder();
	    						cmd.append("\"").append(sourceInsightExe).append("/Insight3.exe").append("\"").append(" -i ")
	    						.append("+").append(line).append(" ").append(fileFullPath);
	    						try {
	    							Runtime.getRuntime().exec(cmd.toString());
	    						} catch (IOException e) {
	    							DexterUIActivator.LOG.error(e.getMessage(), e);
	    						} catch (Exception e){
	    							DexterUIActivator.LOG.error(e.getMessage(), e);
	    						}
	    					} else if(DexterConfig.RunMode.ECLIPSE == DexterConfig.getInstance().getRunMode()){
	    						final File f = new File(log.getParent().getParent().getFileFullPath());
	    						final IFile file = EclipseUtil.getIFileFromFile(f);
	    						
	    						if(file == null){
	    							DexterUIActivator.LOG.error("Cannot open the file because of null pointer of the file");
	    							return;
	    						}
	    						
	    						final IWorkbenchPage page = PlatformUI.getWorkbench().getActiveWorkbenchWindow().getActivePage();
	    						int line = log.getOccurence().getStartLine();
	                            try {
	    	                        IEditorPart openEditor = IDE.openEditor(page, file);
	    	                        
	    	                        if(openEditor instanceof ITextEditor) {
	    	                        	ITextEditor textEditor = (ITextEditor) openEditor;
	    	                        	IDocument document = textEditor.getDocumentProvider().getDocument(textEditor.getEditorInput());
	    	                        	textEditor.selectAndReveal(document.getLineOffset(line -1), document.getLineLength(line-1)-1);
	    	                        }
	                            } catch (CoreException e) {
	                            	DexterUIActivator.LOG.error(e.getMessage(), e);
	                            } catch (BadLocationException e) {
	                            	DexterUIActivator.LOG.error(e.getMessage(), e);
                                }
	    					} else {
	    						final File file = new File(log.getParent().getParent().getFileFullPath());
	    						if(file.exists() == false){
	    							DexterUIActivator.LOG.error("Cannot open the file because of no exist the file");
	    							return;
	    						}
	    						
	    						final IFileStore fileStore = EFS.getLocalFileSystem().getStore(file.toURI());
	    						final IWorkbenchPage page = PlatformUI.getWorkbench().getActiveWorkbenchWindow().getActivePage();
	    						
	    						try{
	    							IDE.openEditorOnFileStore(page, fileStore);
	    						} catch (PartInitException e){
	    							DexterUIActivator.LOG.error(e.getMessage(), e);
	    						}
	    					}
	                    }
                    });
				} 
				
			}
		});
		
		
		logTreeView.addSelectionChangedListener(new ISelectionChangedListener() {
			@Override
			public void selectionChanged(SelectionChangedEvent event) {
				final Object obj = event.getSelection();
				if(!(obj instanceof IStructuredSelection)){
					return;
				}
				
				final IStructuredSelection selection = (IStructuredSelection) obj;
				Object ele = selection.getFirstElement();
				if(ele instanceof AnalysisLog){
					final AnalysisLog log = (AnalysisLog) ele;
					messageText.setText(log.getFileFullPath());
				} else if(ele instanceof DefectLog){
					final DefectLog log = (DefectLog) ele;
					messageText.setText(log.getDefect().getMessage().replace("[#", "\r\n[#"));
				} else if(ele instanceof OccurenceLog){
					final OccurenceLog log = (OccurenceLog) ele;
					messageText.setText(log.getOccurence().getMessage() + "\r\n\r\n" 
							+ log.getOccurence().getStringValue());
				}
				
			}
		});
		
		initMenu();
		
		DexterConfig.getInstance().addDexterHomeListener(this);
		DexterAnalyzer.getInstance().addListener(analyzerHandler);
		
		DexterClient.getInstance().login();
		DexterConfig.getInstance().createInitialFolderAndFiles();
	}

	private void initMenu() {
	    final MenuManager menuManager = new MenuManager("#PopupMenu");
		
		menuManager.setRemoveAllWhenShown(true);
		
		if(logTreeView != null){
			logTreeView.getTree().setMenu(menuManager.createContextMenu(logTreeView.getTree()));
			getSite().registerContextMenu(menuManager, logTreeView);
		}
		
		IMenuManager barmenuManager = getViewSite().getActionBars().getMenuManager();
		barmenuManager.add(createShowTextAction());
    }
	
	private Action createShowTextAction() {
		Action action = new Action("Show text area") {
			public void run() {
				showTextArea(isChecked());
			}
		};
		
		boolean visible = fMemento.getBoolean(P_SHOW_TEXT_AREA).booleanValue();
		action.setChecked(visible);
		showTextArea(visible);
		return action;
	}
	
	private void showTextArea(boolean visible) {
		fMemento.putBoolean(P_SHOW_TEXT_AREA, visible);
		Composite parentComposite = messageText.getParent(); 
		GridData gd = (GridData) messageText.getLayoutData();
		gd.exclude = !visible;
		messageText.setVisible(visible);
		parentComposite.layout(false);
	}
	
	@Override
	public void init(IViewSite site, IMemento memento) throws PartInitException {
		super.init(site, memento);
		if (memento == null)
			this.fMemento = XMLMemento.createWriteRoot("DEXTERVIEW"); 
		else
			this.fMemento = memento;
		
		readSettings();
		
		ITEM_ORDER = this.fMemento.getInteger(P_ORDER_VALUE);
	}
	
	@Override
	public void saveState(IMemento memento) {
		if (this.fMemento == null || memento == null)
			return;
		
		this.fMemento.putInteger(P_COLUMN_1, getColumnWidth(fColumn1, 300));
		this.fMemento.putInteger(P_COLUMN_2, getColumnWidth(fColumn2, 80));
		this.fMemento.putInteger(P_COLUMN_3, getColumnWidth(fColumn3, 60));
		this.fMemento.putInteger(P_COLUMN_4, getColumnWidth(fColumn4, 150));
		this.fMemento.putInteger(P_COLUMN_5, getColumnWidth(fColumn5, 150));
		this.fMemento.putInteger(P_COLUMN_6, getColumnWidth(fColumn6, 150));
		this.fMemento.putInteger(P_COLUMN_7, getColumnWidth(fColumn7, 300));
		memento.putMemento(this.fMemento);
		writeSettings();
	}
	
	private Preferences getLogPreferences() {
		return InstanceScope.INSTANCE.getNode(ID);
	}
	
	private void readSettings() {
		Preferences instancePrefs = getLogPreferences();
		fMemento.putBoolean(P_SHOW_TEXT_AREA, instancePrefs.getBoolean(P_SHOW_TEXT_AREA, true));
		fMemento.putInteger(P_ORDER_VALUE, instancePrefs.getInt(P_ORDER_VALUE, ASCENDING));
		fMemento.putInteger(P_COLUMN_1, getColumnWidthPreference(instancePrefs, P_COLUMN_1, 300));
		fMemento.putInteger(P_COLUMN_2, getColumnWidthPreference(instancePrefs, P_COLUMN_2, 80));
		fMemento.putInteger(P_COLUMN_3, getColumnWidthPreference(instancePrefs, P_COLUMN_3, 60));
		fMemento.putInteger(P_COLUMN_4, getColumnWidthPreference(instancePrefs, P_COLUMN_4, 150));
		fMemento.putInteger(P_COLUMN_5, getColumnWidthPreference(instancePrefs, P_COLUMN_5, 150));
		fMemento.putInteger(P_COLUMN_6, getColumnWidthPreference(instancePrefs, P_COLUMN_6, 150));
		fMemento.putInteger(P_COLUMN_7, getColumnWidthPreference(instancePrefs, P_COLUMN_7, 500));
	}
	
	private void writeSettings() {
		Preferences instancePrefs = getLogPreferences();
		instancePrefs.putBoolean(P_SHOW_TEXT_AREA, fMemento.getBoolean(P_SHOW_TEXT_AREA).booleanValue());
		instancePrefs.putInt(P_ORDER_VALUE, fMemento.getInteger(P_ORDER_VALUE));
		instancePrefs.putInt(P_COLUMN_1, fMemento.getInteger(P_COLUMN_1));
		instancePrefs.putInt(P_COLUMN_2, fMemento.getInteger(P_COLUMN_2));
		instancePrefs.putInt(P_COLUMN_3, fMemento.getInteger(P_COLUMN_3));
		instancePrefs.putInt(P_COLUMN_4, fMemento.getInteger(P_COLUMN_4));
		instancePrefs.putInt(P_COLUMN_5, fMemento.getInteger(P_COLUMN_5));
		instancePrefs.putInt(P_COLUMN_6, fMemento.getInteger(P_COLUMN_6));
		instancePrefs.putInt(P_COLUMN_7, fMemento.getInteger(P_COLUMN_7));
		

		try {
			instancePrefs.flush();
		} catch (BackingStoreException e) {
			DexterUIActivator.LOG.error(e.getMessage(), e);
		}
	}
	
	int getColumnWidthPreference(Preferences instancePrefs, String key, int defaultwidth) {
		int width = instancePrefs.getInt(key, defaultwidth);
		return width < 1 ? defaultwidth : width;
	}
	
	int getColumnWidth(TreeColumn column, int defaultwidth) {
		int width = column.getWidth();
		return width < 1 ? defaultwidth : width;
	}
	
	@Override
	public void setFocus() {
		if(logTreeView != null){
			logTreeView.getControl().setFocus();
		}
	}
	
	class LogTreeViewerSorter extends ViewerSorter {
		/* (non-Javadoc)
		 * @see org.eclipse.jface.viewers.ViewerComparator#compare(org.eclipse.jface.viewers.Viewer, java.lang.Object, java.lang.Object)
		 */
		@Override
		public int compare(Viewer viewer, Object e1, Object e2) {
			if(e1 instanceof TreeItem && e2 instanceof TreeItem){
				if( !(((TreeItem) e1).getData() instanceof AnalysisLog)
					|| ! (((TreeItem) e2).getData() instanceof AnalysisLog)){
						return super.compare(viewer, e1, e2);
				}
				
				final AnalysisLog a1 = (AnalysisLog) ((TreeItem) e1).getData();
				final AnalysisLog a2 = (AnalysisLog) ((TreeItem) e2).getData();

				return compareAnalysisLog(a1, a2) * ITEM_ORDER;
				
			} else if(e1 instanceof AnalysisLog && e2 instanceof AnalysisLog){
				final AnalysisLog a1 = (AnalysisLog) e1;
				final AnalysisLog a2 = (AnalysisLog) e2;
				
				return compareAnalysisLog(a1, a2) * ITEM_ORDER;
			} else if(e1 instanceof DefectLog && e2 instanceof DefectLog){
				final DefectLog d1 = (DefectLog) e1;
				final DefectLog d2 = (DefectLog) e2;
				
				return d1.getDefect().getCheckerCode().compareTo(d2.getDefect().getCheckerCode()) * ITEM_ORDER;
			} else if(e1 instanceof OccurenceLog && e2 instanceof OccurenceLog){
				final OccurenceLog o1 = (OccurenceLog) e1;
				final OccurenceLog o2 = (OccurenceLog) e2;
				
				if(o1.getOccurence().getStartLine() > o2.getOccurence().getStartLine()){
					return 1;
				} else if(o1.getOccurence().getStartLine() < o2.getOccurence().getStartLine()){
					return -1;
				} else {
					return 0;
				}
			}
			
		    return super.compare(viewer, e1, e2);
		}
		
		private int compareAnalysisLog(final AnalysisLog a1, final AnalysisLog a2){
			if(a1.getCreatedTimeStr() == null){
				return -1;
			} else if(a2.getCreatedTimeStr() == null){
				return 1;
			} else if (a1.getCreatedTimeStr() == null && a2.getCreatedTimeStr() == null){
				return 0;
			} else {
				return a1.getCreatedTimeStr().compareTo(a2.getCreatedTimeStr()) * -1;
			}
		}
		
		/* (non-Javadoc)
		 * @see org.eclipse.jface.viewers.ViewerComparator#category(java.lang.Object)
		 */
		@Override
		public int category(Object element) {
			if(element instanceof AnalysisLog){
				return 1;
			} else if(element instanceof DefectLog){
				return 2;
			} else if(element instanceof OccurenceLog){
				return 3;
			} else {
				return 4;
			}
		}
	}
	
	class AnalyzerHandler extends DexterAnalyzerAdapter {
		/* (non-Javadoc)
		 * @see com.samsung.sec.dexter.executor.DexterAnalyzerAdapter#handlePostRunStaticAnalysis(com.samsung.sec.dexter.core.analyzer.AnalysisConfig, com.samsung.sec.dexter.core.analyzer.AnalysisResult)
		 */
		@Override
		public void handlePostRunStaticAnalysis(final AnalysisConfig config, final List<AnalysisResult> resultList) {
			final List<Defect> allDefectList = DexterAnalyzer.getAllDefectList(resultList);
			final String fileName = DexterAnalyzer.getFileName(resultList);
			final String sourceFileFullPath = DexterAnalyzer.getSourceFileFullPath(resultList);
			
			final AnalysisLog log = new AnalysisLog();
			final Date date = new Date();
			log.setCreatedTime(date);
			log.setCreatedTimeStr(DexterUtil.currentDateTime());
			log.setDefectCount(allDefectList.size());
			log.setFileName(fileName);
			log.setFileFullPath(sourceFileFullPath);
			
			for(final Defect defect : allDefectList){
				log.addDefectLog(defect);
			}
			
			display.syncExec(new Runnable() {
				@Override
				public void run() {
					logTreeView.collapseAll();
					logTreeView.add(rootLog, log);
					logTreeView.getSorter().sort(logTreeView, tree.getItems());
					logTreeView.refresh(log, false);
					logTreeView.expandToLevel(log, AbstractTreeViewer.ALL_LEVELS);
					updateStatusBar("added new log (defect: " + log.getDefectLogList().size() + ") - " + log.getFileFullPath());
					rootLog.addChild(log);
				}
			});
			
		}
	}
	
	public void openFirstElement(){
		if(tree != null && logTreeView != null && tree.getItems() != null && tree.getItems().length > 0){
			logTreeView.expandToLevel(tree.getItems()[0].getData(), AbstractTreeViewer.ALL_LEVELS);
		}
	}
	
	/* (non-Javadoc)
	 * @see org.eclipse.ui.part.WorkbenchPart#dispose()
	 */
	@Override
	public void dispose() {
		writeSettings();
		DexterConfig.getInstance().removeDexterHomeListener(this);
		DexterAnalyzer.getInstance().removeListener(analyzerHandler);
	    super.dispose();
	}

	/**
	 * @return the logTreeView
	 */
	public TreeViewer getLogTreeView() {
		return logTreeView;
	}

	/* (non-Javadoc)
	 * @see com.samsung.sec.dexter.core.config.IDexterHomeListener#handleDexterHomeChanged()
	 */
    @Override
    public void handleDexterHomeChanged() {
		rootLog = new RootAnalysisLog();
		rootLog.loadFromLogFiles();
		messageText.setText("");
		logTreeView.setInput(rootLog);
		logTreeView.refresh();
    }
    
    public void updateStatusBar(final String message){
    	final IStatusLineManager statusLine = this.getViewSite().getActionBars().getStatusLineManager();
    	statusLine.setMessage(message);
    }
}
