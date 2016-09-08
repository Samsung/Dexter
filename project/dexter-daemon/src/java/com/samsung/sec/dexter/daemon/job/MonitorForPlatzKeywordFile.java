package com.samsung.sec.dexter.daemon.job;

import com.samsung.sec.dexter.core.config.DexterConfig;
import com.samsung.sec.dexter.core.config.IDexterHomeListener;
import com.samsung.sec.dexter.core.config.PlatzKeywordFile;
import com.samsung.sec.dexter.core.exception.DexterRuntimeException;
import com.samsung.sec.dexter.core.util.DexterUtil;
import com.samsung.sec.dexter.daemon.DexterDaemonActivator;
import com.samsung.sec.dexter.eclipse.ui.DexterUIActivator;
import com.samsung.sec.dexter.eclipse.ui.util.EclipseUtil;
import com.samsung.sec.dexter.eclipse.ui.view.PlatzView;

import java.io.File;

import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.NullProgressMonitor;
import org.eclipse.core.runtime.Status;
import org.eclipse.core.runtime.jobs.Job;
import org.eclipse.ui.progress.UIJob;

public class MonitorForPlatzKeywordFile extends Job implements IDexterHomeListener {
    public static long LAST_CONF_CHANAGED_TIME = -1;
    private final static int MONITOR_DELAY = 500;
    private File keywordFile;

    public MonitorForPlatzKeywordFile() {
        super("Monitoring target keyword in sourceinsight");
        initPlatzKeywordFile();
        DexterConfig.getInstance().addDexterHomeListener(this);
    }

    private void initPlatzKeywordFile() {
        final String dexterHome = DexterConfig.getInstance().getDexterHome();
        String keywordFilePath = dexterHome + "/bin/" + DexterConfig.DAEMON_FOLDER_NAME + "/"
                + DexterConfig.PLATZ_KEYWORD_FILENAME;
        DexterUtil.createEmptyFileIfNotExist(keywordFilePath);
        keywordFile = new File(keywordFilePath);
        LAST_CONF_CHANAGED_TIME = keywordFile.lastModified();
    }

    @Override
    public void handleDexterHomeChanged(final String oldPath, final String newPath) {
        initPlatzKeywordFile();
    }

    @Override
    protected IStatus run(IProgressMonitor monitor) {
        do {
            try {
                checkPlatzKeywordFileAndAnalyze();
                delayMonitor();
            } catch (DexterRuntimeException e) {
                DexterDaemonActivator.LOG.error(e.getMessage(), e);
            }
        } while (monitor.isCanceled() == false);
        DexterConfig.getInstance().removeDexterHomeListener(this);
        return Status.CANCEL_STATUS;
    }

    private void checkPlatzKeywordFileAndAnalyze() {
        final long lastModified = keywordFile.lastModified();
        if (LAST_CONF_CHANAGED_TIME == lastModified) {
            return;
        }

        PlatzKeywordFile platzKeywordFile = new PlatzKeywordFile();
        platzKeywordFile.loadFromFile(keywordFile);
        handleKeywordFileChanged(platzKeywordFile.getKeyword());

        LAST_CONF_CHANAGED_TIME = lastModified;
    }

    private void handleKeywordFileChanged(final String keyword) {
        new UIJob("Switching perspectives") {
            @Override
            public IStatus runInUIThread(IProgressMonitor monitor) {

                try {
                    final PlatzView platzView = (PlatzView) EclipseUtil.findView(PlatzView.ID);
                    StringBuilder url = new StringBuilder(1024);
                    url.append(DexterConfig.PLATZ_SEARCH_API_URL).append(keyword).append("?dexterId=")
                            .append(DexterUIActivator.getDefault().getDexterClient().getCurrentUserId());
                    platzView.setUrl(url.toString());
                    return Status.OK_STATUS;
                } catch (DexterRuntimeException e) {
                    EclipseUtil.errorMessageBox("Open Error",
                            "Fail to open PLATZ View. Please report this to Dexter Developer Team.");
                    return Status.CANCEL_STATUS;
                }
            }
        }.run(new NullProgressMonitor());
    }

    private void delayMonitor() {
        try {
            Thread.sleep(MONITOR_DELAY);
        } catch (InterruptedException e) {}
    }

    @Override
    protected void canceling() {
        DexterConfig.getInstance().removeDexterHomeListener(this);
        super.canceling();
    }

}
