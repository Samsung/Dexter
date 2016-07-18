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
package com.samsung.sec.dexter.daemon;

import com.google.common.base.Strings;
import com.samsung.sec.dexter.core.config.DexterConfig;
import com.samsung.sec.dexter.core.exception.DexterRuntimeException;
import com.samsung.sec.dexter.core.job.DexterJobFacade;
import com.samsung.sec.dexter.core.util.DexterUtil;
import com.samsung.sec.dexter.core.util.IDexterClient;
import com.samsung.sec.dexter.daemon.p2.P2Util;
import com.samsung.sec.dexter.eclipse.ui.DexterUIActivator;
import com.samsung.sec.dexter.eclipse.ui.util.EclipseUtil;

import java.io.File;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

import org.apache.log4j.Logger;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Status;
import org.eclipse.core.runtime.jobs.Job;
import org.eclipse.equinox.p2.core.IProvisioningAgent;
import org.eclipse.equinox.p2.operations.ProvisioningJob;
import org.eclipse.equinox.p2.operations.ProvisioningSession;
import org.eclipse.equinox.p2.operations.UpdateOperation;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.MenuDetectEvent;
import org.eclipse.swt.events.MenuDetectListener;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.graphics.Point;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Menu;
import org.eclipse.swt.widgets.MenuItem;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.swt.widgets.Tray;
import org.eclipse.swt.widgets.TrayItem;
import org.eclipse.ui.IWorkbenchWindow;
import org.eclipse.ui.PlatformUI;
import org.eclipse.ui.application.ActionBarAdvisor;
import org.eclipse.ui.application.IActionBarConfigurer;
import org.eclipse.ui.application.IWorkbenchWindowConfigurer;
import org.eclipse.ui.application.WorkbenchWindowAdvisor;
import org.osgi.framework.BundleContext;
import org.osgi.framework.ServiceReference;

public class ApplicationWorkbenchWindowAdvisor extends WorkbenchWindowAdvisor {
	private final static Logger LOG = Logger.getLogger(ApplicationWorkbenchWindowAdvisor.class);
	private IWorkbenchWindow window;
	private TrayItem trayItem;
	private Image trayImage;
	private static String TITLE;
	private static IWorkbenchWindowConfigurer WIN_CONFIGURE;

	private Job updateJob;

	public ApplicationWorkbenchWindowAdvisor(IWorkbenchWindowConfigurer configurer) {
		super(configurer);
	}

	public ActionBarAdvisor createActionBarAdvisor(IActionBarConfigurer configurer) {
		return new ApplicationActionBarAdvisor(configurer);
	}

	public void preWindowOpen() {
		// after updating, previous dexter plug-ins should be deleted
		// but it dosen't work, so we have to delete manually despite of
		// performance issue of starting
		deletePreviousDexterPlugins();
		deletePreviousDexterFeatures();

		setWinConfigure(getWindowConfigurer());

		WIN_CONFIGURE.setInitialSize(new Point(800, 600));
		WIN_CONFIGURE.setShowCoolBar(true);
		WIN_CONFIGURE.setShowStatusLine(true);
		WIN_CONFIGURE.setShowProgressIndicator(true);

		if (Strings.isNullOrEmpty(TITLE) == false) {
			WIN_CONFIGURE.setTitle(TITLE);
		}
	}

	private static void setWinConfigure(IWorkbenchWindowConfigurer conf) {
		WIN_CONFIGURE = conf;
	}

	public static void setWindowTitle(String title) {
		TITLE = title;
		if (WIN_CONFIGURE != null) {
			WIN_CONFIGURE.setTitle(title);
		}
	}

	@Override
	public void postWindowClose() {
		super.postWindowClose();
	}

	private void deletePreviousDexterPlugins() {
		final File pluginDir = new File("plugins");

		if (!pluginDir.exists()) {
			throw new DexterRuntimeException("There is no plugins folder. The Dexter archive file might be invalid.");
		}

		final Map<String, File> tempList = new HashMap<String, File>();

		File[] files = DexterUtil.getSubFiles(pluginDir);
		for (File pluginFile : files) {
			if (pluginFile.isDirectory())
				continue;

			final String fileName = pluginFile.getName();

			if (fileName.startsWith("dexter")) {
				deleteDexterPluginIfHasOldOne(tempList, pluginFile, fileName);
			}
		}
	}

	private void deleteDexterPluginIfHasOldOne(final Map<String, File> tempList, File pluginFile,
			final String fileName) {
		String pluginPrefixName = getPluginPrefixName(fileName);
		File tempPluginFile = tempList.get(pluginPrefixName);

		if (tempPluginFile == null) {
			tempList.put(pluginPrefixName, pluginFile);
		} else {
			if (pluginFile.lastModified() >= tempPluginFile.lastModified()) {
				tempList.remove(pluginPrefixName);
				tempPluginFile.delete();
			} else {
				pluginFile.delete();
			}
		}
	}

	private void deletePreviousDexterFeatures() {
		final File featureDir = new File("features");
		final Map<String, File> tempFeatureList = new HashMap<String, File>();

		if (!featureDir.exists()) {
			throw new DexterRuntimeException("There is no features folder. The Dexter archive file might be invalid.");
		}

		File[] files = DexterUtil.getSubFiles(featureDir);
		for (File featureFile : files) {
			final String featureFileName = featureFile.getName();
			if (featureFileName.startsWith("dexter")) {
				deleteDexterFeatureIfHasOldOne(tempFeatureList, featureFile, featureFileName);
			}
		}
	}

	private void deleteDexterFeatureIfHasOldOne(final Map<String, File> tempFeatureList, File featureFile,
			final String featureFileName) {
		String pluginPrefixName = getPluginPrefixName(featureFileName);
		if (pluginPrefixName.equals(DexterConfig.NOT_FOUND_FOLDER_NAME)) {
			return;
		}

		File tempPluginFile = tempFeatureList.get(pluginPrefixName);
		if (tempPluginFile == null) {
			tempFeatureList.put(pluginPrefixName, featureFile);
		} else {
			if (featureFile.lastModified() >= tempPluginFile.lastModified()) {
				tempFeatureList.remove(pluginPrefixName);
				try {
					DexterUtil.deleteDirectory(tempPluginFile);
				} catch (IOException e) {
					LOG.error(e.getMessage(), e);
				}
			} else {
				try {
					DexterUtil.deleteDirectory(featureFile);
				} catch (IOException e) {
					LOG.error(e.getMessage(), e);
				}

			}
		}
	}

	private String getPluginPrefixName(final String fileName) {
		int index = fileName.indexOf('_');
		if (index == -1) {
			return DexterConfig.NOT_FOUND_FOLDER_NAME;
		} else {
			String fileNamePrefix = fileName.substring(0, index);
			return fileNamePrefix;
		}
	}

	/*
	 * @see org.eclipse.ui.application.WorkbenchWindowAdvisor#postWindowOpen()
	 */
	@Override
	public void postWindowOpen() {
		super.postWindowOpen();

		window = getWindowConfigurer().getWindow();
		// window.getShell().setMinimized(true); // if you want start this as a
		// minimized size at starting
		trayItem = initTaskItem(window);

		minimizeBehavior();
		hookPopupMenu();

		startMonitorForLogin();
	}

	@Override
	public boolean preWindowShellClose() {
		return super.preWindowShellClose();
	}

	private void minimizeBehavior() {
		if (trayItem != null) {
			trayItem.addSelectionListener(new SelectionAdapter() {
				@Override
				public void widgetSelected(SelectionEvent e) {
					Shell shell = window.getShell();
					if (!shell.isVisible()) {
						window.getShell().setMinimized(false);
						shell.setVisible(true);
					}
				}
			});
		}
	}

	private void hookPopupMenu() {
		if (trayItem != null) {
			trayItem.addMenuDetectListener(new MenuDetectListener() {
				@Override
				public void menuDetected(MenuDetectEvent e) {
					Menu menu = new Menu(window.getShell(), SWT.POP_UP);

					MenuItem exit = new MenuItem(menu, SWT.NONE);
					exit.setText("Exit Dexter Daemon");
					exit.addSelectionListener(new SelectionAdapter() {
						@Override
						public void widgetSelected(SelectionEvent e) {
							window.getWorkbench().close();
						}
					});

					menu.setVisible(true);
				}
			});
		}
	}

	private TrayItem initTaskItem(IWorkbenchWindow window) {
		final Tray tray = window.getShell().getDisplay().getSystemTray();
		TrayItem trayItem = new TrayItem(tray, SWT.NONE);
		trayImage = EclipseUtil.getImage(DexterDaemonActivator.PLUGIN_ID, "/icons/dexterLogo.gif");
		trayItem.setImage(trayImage);
		trayItem.setToolTipText("Dexter Daemon");

		return trayItem;
	}

	/*
	 * @see org.eclipse.ui.application.WorkbenchWindowAdvisor#dispose()
	 */
	@Override
	public void dispose() {
		if (trayImage != null) {
			trayImage.dispose();
		}

		if (trayItem != null) {
			trayItem.dispose();
		}
	}

	private void startMonitorForLogin() {
		Runnable checkLoginJob = new Runnable() {
			@Override
			public void run() {
				runUpdateJob();
			}
		};

		final ScheduledExecutorService scheduler = Executors.newScheduledThreadPool(1);

		scheduler.scheduleAtFixedRate(checkLoginJob, 1, DexterJobFacade.SLEEP_FOR_LOGIN, TimeUnit.SECONDS);
	}

	private void runUpdateJob() {
		final IDexterClient client = DexterUIActivator.getDefault().getDexterClient();
		if (client == null) {
			throw new DexterRuntimeException("cann't check Dexter Update because DexterClient is null");
		}

		updateJob = new Job("Waiting to connect Dexter Server...") {
			@Override
			protected IStatus run(final IProgressMonitor monitor) {
				monitor.beginTask("Checking Dexter Server Update", 10);

				if (client.isServerAlive()) {
					Display.getDefault().asyncExec(new Runnable() {
						@Override
						public void run() {
							final String updateUrl = client.getDexterPluginUpdateUrl();
							checkerAndUpdateDexterPlugin(updateUrl, monitor);
						}
					});
				}

				return Status.OK_STATUS;
			}
		};

		updateJob.setUser(true);
		updateJob.schedule();
	}

	private void checkerAndUpdateDexterPlugin(final String updateUrl, final IProgressMonitor monitor) {
		IProvisioningAgent agent = getProvisioningAgent();

		if (agent == null) {
			DexterDaemonActivator.LOG.error("No agent loaded for Dexter Daemon Update");
			monitor.done();
			return;
		}

		if (!P2Util.addRepository(agent, updateUrl)) {
			DexterDaemonActivator.LOG.error("could not add Dexter Daemon Update repostory!");
			monitor.done();
			return;
		}

		checkAndUpdatePluginAndRestart(agent, monitor);
		monitor.done();
		updateJob.done(Status.OK_STATUS);
	}

	private void checkAndUpdatePluginAndRestart(final IProvisioningAgent agent, IProgressMonitor monitor) {
		ProvisioningSession session = new ProvisioningSession(agent);
		UpdateOperation operation = new UpdateOperation(session);
		// SubMonitor sub = SubMonitor.convert(monitor, "Checking for Dexter
		// Daemon updates...", 200);

		if (checkUpdateExistence(operation, monitor)) {
			EclipseUtil.infoMessageBox("Dexter Daemon Update",
					"There is a new version of Dexter Daemon.\nIt will be updated now");
			updateDexterPluginAndRestart(operation, monitor);
			monitor.done();
		}
	}

	private boolean checkUpdateExistence(UpdateOperation operation, IProgressMonitor monitor) {
		// IStatus status = operation.resolveModal(sub.newChild(100));
		IStatus status = operation.resolveModal(monitor);

		if (status.getCode() == UpdateOperation.STATUS_NOTHING_TO_UPDATE) {
			DexterDaemonActivator.LOG.info("There is no update for dexter plugin");
			// sub.done();
			monitor.done();
			return false;
		}

		if (status.getSeverity() == IStatus.CANCEL || status.getSeverity() == IStatus.ERROR) {
			DexterDaemonActivator.LOG.info("Updating dexter plugins is stopped due to user or error");
			// sub.done();
			monitor.done();
			return false;
		}

		monitor.done();
		return true;
	}

	private void updateDexterPluginAndRestart(UpdateOperation operation, IProgressMonitor monitor) {
		ProvisioningJob job = operation.getProvisioningJob(monitor);
		// IStatus status = job.runModal(sub.newChild(100));
		IStatus status = job.runModal(monitor);

		if (status.getSeverity() == IStatus.CANCEL) {
			DexterDaemonActivator.LOG.info("Updating dexter plugins is stopped due to user");
			// sub.done();
			monitor.done();
			return;
		}

		EclipseUtil.infoMessageBox("Dexter Daemon Update",
				"Update is successfully finished!\nDexter Daemon will be terminated and executed again.");
		// sub.done();
		monitor.done();
		PlatformUI.getWorkbench().restart();
	}

	private IProvisioningAgent getProvisioningAgent() {
		BundleContext bundleContext = DexterDaemonActivator.getDefault().getBundle().getBundleContext();
		ServiceReference<IProvisioningAgent> serviceReference = bundleContext
				.getServiceReference(IProvisioningAgent.class);

		IProvisioningAgent agent = bundleContext.getService(serviceReference);

		return agent;
	}
}
