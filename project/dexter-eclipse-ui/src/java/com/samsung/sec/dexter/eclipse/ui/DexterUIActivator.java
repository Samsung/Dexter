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
package com.samsung.sec.dexter.eclipse.ui;

import com.google.common.base.Strings;
import com.samsung.sec.dexter.core.config.DexterConfig;
import com.samsung.sec.dexter.core.config.IDexterStandaloneListener;
import com.samsung.sec.dexter.core.exception.DexterRuntimeException;
import com.samsung.sec.dexter.core.job.DexterJobFacade;
import com.samsung.sec.dexter.core.plugin.BaseDexterPluginManager;
import com.samsung.sec.dexter.core.plugin.IDexterPlugin;
import com.samsung.sec.dexter.core.plugin.IDexterPluginInitializer;
import com.samsung.sec.dexter.core.plugin.IDexterPluginManager;
import com.samsung.sec.dexter.core.util.DexterClient;
import com.samsung.sec.dexter.core.util.EmptyDexterClient;
import com.samsung.sec.dexter.core.util.IDexterClient;
import com.samsung.sec.dexter.eclipse.ui.login.LoginDialog;
import com.samsung.sec.dexter.eclipse.ui.login.Messages;
import com.samsung.sec.dexter.eclipse.ui.util.EclipseLog;
import com.samsung.sec.dexter.eclipse.ui.util.EclipseUtil;
import com.samsung.sec.dexter.executor.DexterAnalyzer;
import com.samsung.sec.dexter.executor.DexterExecutorActivator;

import java.util.List;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.ScheduledFuture;
import java.util.concurrent.TimeUnit;

import org.eclipse.core.runtime.IConfigurationElement;
import org.eclipse.core.runtime.IExtension;
import org.eclipse.core.runtime.Platform;
import org.eclipse.jface.dialogs.InputDialog;
import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.ui.plugin.AbstractUIPlugin;
import org.osgi.framework.BundleContext;

/**
 * The activator class controls the plug-in life cycle
 */
public class DexterUIActivator extends AbstractUIPlugin implements IDexterPluginInitializer, IDexterStandaloneListener {
	public static final String PLUGIN_ID = "dexter-eclipse-ui"; //$NON-NLS-1$
	public final static EclipseLog LOG = new EclipseLog(PLUGIN_ID);

	private static DexterUIActivator plugin;
	private IDexterClient client;

	private final ScheduledExecutorService scheduler = Executors.newScheduledThreadPool(5);
	private DexterJobFacade jobFacade;
	private ScheduledFuture<?> loginFuture = null;

	private IDexterPluginManager pluginManager;

	/**
	 * The constructor
	 */
	public DexterUIActivator() {
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.eclipse.ui.plugin.AbstractUIPlugin#start(org.osgi.framework.
	 * BundleContext)
	 */
	public void start(BundleContext context) throws Exception {
		super.start(context);
		plugin = this;
		LOG.setPlugin(this);
		initDexterClient();

		pluginManager = new BaseDexterPluginManager(this, client);
		DexterAnalyzer.getInstance();

		// TODO standalone 모드에서 서버 관련 처리 모드 Disable하기
		// TODO 아래 실행 순서 다시 확인하기
		final boolean isStandalone = getPreferenceStore().getBoolean("isStandalone");
		DexterConfig.getInstance().setStandalone(isStandalone);
		DexterConfig.getInstance().addDexterStandaloneListener(client);
		DexterConfig.getInstance().addDexterStandaloneListener(this);

		// TODO Job 실행 시점 정리
		if (!DexterConfig.getInstance().isStandalone())
			startLoginScheduler();

		startMonitorForLogin();
	}

	public void initDexterJobFacade() {
		if (jobFacade != null) {
			DexterConfig.getInstance().removeDexterStandaloneListener(jobFacade);
			jobFacade.shutdownScheduleService();
		}

		jobFacade = new DexterJobFacade(client);
		jobFacade.startDexterServerJobs();
		jobFacade.startGeneralJobs();
		DexterConfig.getInstance().addDexterStandaloneListener(jobFacade);
	}

	private void startMonitorForLogin() {
		Runnable checkLoginJob = new Runnable() {
			@Override
			public void run() {
				loginJob(EclipseUtil.getActiveWorkbenchWindowShell());
			}
		};

		final ScheduledExecutorService scheduler = Executors.newScheduledThreadPool(1);

		scheduler.scheduleAtFixedRate(checkLoginJob, 1, DexterJobFacade.SLEEP_FOR_LOGIN, TimeUnit.SECONDS);
	}

	public void loginJob(final Shell shell) {
		final Shell localShell;

		if (shell == null || shell.isDisposed()) {
			if (Display.getDefault() != null && Display.getDefault().getActiveShell() != null) {
				localShell = Display.getDefault().getActiveShell();
			} else {
				return;
			}
		} else {
			localShell = shell;
		}

		localShell.getDisplay().syncExec(new Runnable() {
			@Override
			public void run() {
				if (Strings.isNullOrEmpty(DexterConfig.getInstance().getDexterHome()) == false
						&& Strings.isNullOrEmpty(client.getCurrentUserId()) == false) {
					return;
				}

				final LoginDialog dialog = new LoginDialog(localShell);
				final int ret = dialog.open();

				if (ret == InputDialog.CANCEL) {
					MessageDialog.openError(localShell, "Dexter Login Error", //$NON-NLS-1$
							Messages.LoginDialog_LOGIN_GUIDE_MSG);
				}
			}
		});
	}

	private void startLoginScheduler() {
		if (loginFuture == null || loginFuture.isDone()) {
			Runnable checkLoginJob = new Runnable() {
				@Override
				public void run() {
					Display.getDefault().asyncExec(new Runnable() {
						@Override
						public void run() {
							if (!DexterConfig.getInstance().getRunMode().equals(DexterConfig.RunMode.DAEMON))
								loginJob(null);
						}
					});
				}
			};

			loginFuture = getDefault().scheduler.scheduleAtFixedRate(checkLoginJob, 5, DexterJobFacade.SLEEP_FOR_LOGIN,
					TimeUnit.SECONDS);
		}
	}

	private void stopLoginScheduler() {
		if (loginFuture != null)
			loginFuture.cancel(false);
	}

	void initDexterClient() {
		final DexterConfig config = DexterConfig.getInstance();
		final String dexterHome = getPreferenceStore().getString(DexterConfig.DEXTER_HOME_KEY);
		config.setDexterHome(dexterHome);

		if (config.isStandalone()) {
			client = new EmptyDexterClient();
			return;
		}

		final String id = getPreferenceStore().getString("userId");
		final String pwd = getPreferenceStore().getString("userPwd");
		final String serverAddress = getPreferenceStore().getString("serverAddress");

		if (Strings.isNullOrEmpty(id) || Strings.isNullOrEmpty(pwd) || Strings.isNullOrEmpty(serverAddress)) {
			LOG.info(
					"Initialize failure for Connection of Dexter Server because no id, pwd, serverAddress. If you are using standalong mode Dexter, ignore this message");
			return;
		}

		try {
			client = new DexterClient(serverAddress, id, pwd);
		} catch (DexterRuntimeException e) {
			LOG.error(e.getMessage(), e);
		}
	}

	public IDexterClient getDexterClient() {
		return this.client;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.eclipse.ui.plugin.AbstractUIPlugin#stop(org.osgi.framework.
	 * BundleContext)
	 */
	public void stop(BundleContext context) throws Exception {
		plugin = null;

		DexterConfig.getInstance().removeDexterStandaloneListener(client);
		DexterConfig.getInstance().removeDexterStandaloneListener(this);
		DexterConfig.getInstance().removeDexterStandaloneListener(jobFacade);
		jobFacade.shutdownScheduleService();
		super.stop(context);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * com.samsung.sec.dexter.executor.DexterPluginInitializer#init(java.util.
	 * List)
	 */
	@Override
	public void init(List<IDexterPlugin> pluginList) {
		IExtension[] extensions = Platform.getExtensionRegistry()
				.getExtensionPoint(DexterExecutorActivator.PLUGIN_ID, "DexterPlugin").getExtensions();

		if (extensions.length == 0) {
			throw new DexterRuntimeException("There is no Extensions for Static Analysis Eclipse Plug-ins");
		}

		for (int i = 0; i < extensions.length; i++) {
			final IConfigurationElement[] configs = extensions[i].getConfigurationElements();

			if (configs.length == 0) {
				DexterUIActivator.LOG.warn("cannot load Dexter Plugin : " + extensions[i].getLabel());
				continue;
			}

			initDexterPlugin(pluginList, configs);
		}

		if (pluginList.size() == 0) {
			throw new DexterRuntimeException("There are no dexter plug-ins to add");
		}
	}

	private void initDexterPlugin(List<IDexterPlugin> pluginHandlerList, final IConfigurationElement[] configs) {
		for (final IConfigurationElement config : configs) {
			IDexterPlugin plugin = null;
			try {
				Object o = config.createExecutableExtension("class");

				if (o instanceof IDexterPlugin) {
					plugin = (IDexterPlugin) o;
				} else {
					continue;
				}

				LOG.info(config.getAttribute("class") + " has been loaded");

				if (!pluginHandlerList.contains(plugin)) {
					plugin.init();
					pluginHandlerList.add(plugin);
				}
			} catch (Exception e) {
				// even one of plug-in has problem, Dexter should be able to
				// run.
				LOG.error(e.getMessage(), e);
			}
		}
	}

	/**
	 * Returns the shared instance
	 *
	 * @return the shared instance
	 */
	public static DexterUIActivator getDefault() {
		return plugin;
	}

	public IDexterPluginManager getPluginManager() {
		return this.pluginManager;
	}

	@Override
	public void handleDexterStandaloneChanged() {
		if (DexterConfig.getInstance().isStandalone()) {
			stopLoginScheduler();
		} else {
			startLoginScheduler();
		}
	}
}
