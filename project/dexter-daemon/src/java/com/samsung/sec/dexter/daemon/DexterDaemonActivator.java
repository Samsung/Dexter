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

import java.io.InputStream;

import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Status;
import org.eclipse.core.runtime.jobs.Job;
import org.eclipse.ui.plugin.AbstractUIPlugin;
import org.osgi.framework.BundleContext;

import com.google.common.base.Strings;
import com.samsung.sec.dexter.core.config.DexterConfig;
import com.samsung.sec.dexter.core.config.IDexterHomeListener;
import com.samsung.sec.dexter.core.exception.DexterRuntimeException;
import com.samsung.sec.dexter.core.util.DexterClient;
import com.samsung.sec.dexter.core.util.DexterUtil;
import com.samsung.sec.dexter.core.util.IDexterClient;
import com.samsung.sec.dexter.core.util.IDexterLoginInfoListener;
import com.samsung.sec.dexter.core.util.PersistenceProperty;
import com.samsung.sec.dexter.daemon.job.MonitorForDexterConfigFile;
import com.samsung.sec.dexter.eclipse.ui.util.EclipseLog;

/**
 * The activator class controls the plug-in life cycle
 */
public class DexterDaemonActivator extends AbstractUIPlugin implements IDexterHomeListener, IDexterLoginInfoListener {

	// The plug-in ID
	public static final String PLUGIN_ID = "dexter-daemon"; //$NON-NLS-1$
	public static final String APP_NAME = "Dexter Daemon";
	public final static EclipseLog LOG = new EclipseLog(PLUGIN_ID);
	
	private static final String SOURCE_INSIGHT_EXE_KEY = "sourceInsightExe";
	private String sourceInsiteExe;
	
	MonitorForDexterConfigFile monitorJob;

	private static DexterDaemonActivator plugin;

	static {
		DexterConfig.getInstance().setRunMode(DexterConfig.RunMode.DAEMON);
	}

	public DexterDaemonActivator() {
	}
	

	/*
	 * @see
	 * org.eclipse.ui.plugin.AbstractUIPlugin#start(org.osgi.framework.BundleContext
	 * )
	 */
	public void start(BundleContext context) throws Exception {
		
		super.start(context);
		plugin = this;
		LOG.setPlugin(this);
		checkOS();
		
		initializeAfterSettingDexterHome();
		DexterConfig.getInstance().addDexterHomeListener(this);
		DexterClient.getInstance().addLoginInfoListener(this);
	}
	
	private void checkOS() {
	    if (DexterUtil.getOS() != DexterUtil.OS.WINDOWS) {
			throw new DexterRuntimeException("I am sorry we do not support LINUX or MAC OSX yet.");
		}
    }
	
	/*
	 * @see com.samsung.sec.dexter.core.config.IDexterHomeListener#
	 * handleDexterHomeChanged()
	 */
	@Override
	public void handleDexterHomeChanged() {
		initializeAfterSettingDexterHome();
	}
	
	@Override
	public void handleDexterLoginInfoChanged() {
		setWindowTitleWithLoginInformation();
	}
	
	private void initializeAfterSettingDexterHome() {
		if(Strings.isNullOrEmpty(DexterConfig.getInstance().getDexterHome())){
			return;
		}
		
		unzipDexterCliZipFile();
		setWindowTitleWithLoginInformation();
		initializeSourceInsightEnvironment();
		startMonitorForDexterConfigFile();
		
		setSourceInsightStatusRegistryAsRunning();
	}
	
	private void unzipDexterCliZipFile() {
		final String dexterHome = DexterConfig.getInstance().getDexterHome();
		final String pluginVersion = DexterDaemonActivator.getDefault().getBundle().getVersion().toString();
		final String targetTempZipPath = dexterHome + "/temp/dexter-cli_"  + pluginVersion + ".zip";

		final InputStream is = getInputStreamForDexterCliZipFile("/dexter-cli.zip");
		DexterUtil.unzipInClassPath(is, targetTempZipPath, dexterHome);
	}
	
	private InputStream getInputStreamForDexterCliZipFile(String sourceZipFileInClasspath) {
		final InputStream is = getClass().getResourceAsStream(sourceZipFileInClasspath);
		
		if (is == null) {
			throw new DexterRuntimeException("can't find dexter-cli.zip file: " + sourceZipFileInClasspath);
		}
		
	    return is;
    }

	private void setWindowTitleWithLoginInformation() {
		final IDexterClient client = DexterClient.getInstance();
		String serverString, userId, loginString;
		
		try {
			serverString = getPreferenceStore().getString("serverAddress");
			userId = getPreferenceStore().getString("userId");
			
			serverString = client.getServerHost() + ":" + client.getServerPort(); 
			userId = client.getCurrentUserId();
			loginString = "(" + serverString + " - " + userId + ")";
			
			getPreferenceStore().putValue("serverAddress", serverString);
			getPreferenceStore().putValue("userId", userId);
		} catch (DexterRuntimeException e) {
			loginString = "(offline)";
		}
		

		ApplicationWorkbenchWindowAdvisor.setWindowTitle(DexterDaemonActivator.APP_NAME 
				+ " v" + DexterDaemonActivator.getDefault().getBundle().getVersion().toString()
				+ " " + loginString);
    }
	
	private void initializeSourceInsightEnvironment(){
		if(DexterUtil.getOS() == DexterUtil.OS.WINDOWS){
			setDexterHomeInWindowsRegistry();
			setSourceInsightExeFilePath();
		}
	}
	
	private void setDexterHomeInWindowsRegistry() {
        final String rootKey = "\"HKEY_CURRENT_USER\\Software\\Source Dynamics\\Source Insight\\3.0\"";
		final String dexterHomeKey = "dexterHomeRegKey";
		
		DexterUtil.setRegistry(rootKey, dexterHomeKey, DexterConfig.getInstance().getDexterHome(), DexterUtil.REG_TYPE.REG_SZ);
    }
	
	private void setSourceInsightExeFilePath(){
		final PersistenceProperty property = PersistenceProperty.getInstance();
		sourceInsiteExe = property.read(SOURCE_INSIGHT_EXE_KEY);
		
		if(Strings.isNullOrEmpty(sourceInsiteExe)){
			final String homeKey = "HKEY_LOCAL_MACHINE\\SOFTWARE\\Microsoft\\Windows\\CurrentVersion\\App Paths\\insight3.exe";
			final String key = "Path";
			final String sourceInsightPath = DexterUtil.readRegistry(homeKey, key);
			
			if(Strings.isNullOrEmpty(sourceInsightPath)){
				LOG.error("cannot find source insight exe file path in windows registry");
				return;
			}
			
			this.sourceInsiteExe = "\"" + sourceInsightPath  + "/insight3.exe\"";
			try {
				property.write(SOURCE_INSIGHT_EXE_KEY, sourceInsightPath);
			} catch (NullPointerException e) {
				LOG.error(e.getMessage());
			}
		}
	}

	private void startMonitorForDexterConfigFile() {
		monitorJob = new MonitorForDexterConfigFile();
		monitorJob.setUser(false);
		monitorJob.setPriority(Job.LONG);
		monitorJob.setSystem(true);
		monitorJob.schedule();
	}
	
	private void setSourceInsightStatusRegistryAsRunning() {
		Job job = new Job("set registry for sourceinsight"){
			@Override
            protected IStatus run(IProgressMonitor monitor) {
				final String rootKey = "\"HKEY_CURRENT_USER\\Software\\Source Dynamics\\Source Insight\\3.0\"";
				final String dexterDaemonHomeKey = "g_dexterDaemon";
				DexterUtil.setRegistry(rootKey, dexterDaemonHomeKey, "1", DexterUtil.REG_TYPE.REG_SZ);
	            return Status.OK_STATUS;
            }
			
		};
		
		job.schedule();
	}
	
	private void setSourceInsightStatusRegistryAsStopped() {
		Job job = new Job("set registry for sourceinsight"){
			@Override
            protected IStatus run(IProgressMonitor monitor) {
				final String rootKey = "\"HKEY_CURRENT_USER\\Software\\Source Dynamics\\Source Insight\\3.0\"";
				final String dexterDaemonHomeKey = "g_dexterDaemon";
				DexterUtil.setRegistry(rootKey, dexterDaemonHomeKey, "0", DexterUtil.REG_TYPE.REG_SZ);
	            return Status.OK_STATUS;
            }
			
		};
		
		job.schedule();
	}
	
	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * org.eclipse.ui.plugin.AbstractUIPlugin#stop(org.osgi.framework.BundleContext
	 * )
	 */
	public void stop(BundleContext context) throws Exception {
		setSourceInsightStatusRegistryAsStopped();
		if(monitorJob != null)	monitorJob.cancel();
		DexterConfig.getInstance().removeDexterHomeListener(this);
		DexterClient.getInstance().removeLoginInfoListener(this);
		super.stop(context);
	}
	

	/**
	 * Returns the shared instance
	 * 
	 * @return the shared instance
	 */
	public static DexterDaemonActivator getDefault() {
		return plugin;
	}
}
