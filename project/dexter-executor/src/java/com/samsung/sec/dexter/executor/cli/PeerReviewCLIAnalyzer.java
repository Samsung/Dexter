package com.samsung.sec.dexter.executor.cli;

import java.util.List;

import com.samsung.sec.dexter.core.analyzer.AnalysisConfig;
import com.samsung.sec.dexter.core.analyzer.AnalysisEntityFactory;
import com.samsung.sec.dexter.core.analyzer.IAnalysisResultHandler;
import com.samsung.sec.dexter.core.config.PeerReviewHome;
import com.samsung.sec.dexter.core.plugin.IDexterPluginInitializer;
import com.samsung.sec.dexter.core.plugin.IDexterPluginManager;
import com.samsung.sec.dexter.core.util.DexterClient;
import com.samsung.sec.dexter.core.util.IDexterClient;
import com.samsung.sec.dexter.core.util.JerseyDexterWebResource;
import com.samsung.sec.dexter.executor.CLIPluginInitializer;
import com.samsung.sec.dexter.executor.DexterAnalyzer;

public class PeerReviewCLIAnalyzer {
	private final ICLILog cliLog;
	private final IDexterCLIOption cliOption;
	private final DexterAnalyzer dexterAnalyzer;
	
	public PeerReviewCLIAnalyzer(IDexterCLIOption cliOption, ICLILog cliLog, DexterAnalyzer dexterAnalyzer) {
		this.cliOption = cliOption;
		this.cliLog = cliLog;
		this.dexterAnalyzer = dexterAnalyzer;
	}

	public void analyze(List<String> changedFileList, PeerReviewHome home) {
		IDexterClient dexterClient = createDexterClient(home);
		AnalysisConfig baseAnalysisConfig = home.toAnalysisConfig();
		IAnalysisResultHandler analysisResultHandler = new CLIAnalysisResultHandler(dexterClient.getDexterWebUrl(), cliOption, cliLog);
		IDexterPluginManager dexterPluginManager = loadDexterPlugins(dexterClient, cliOption);
		
		loginOrCreateAccount(dexterClient, cliOption);
		
		for (String changedFile : changedFileList) {
			dexterAnalyzer.runAsync(
            		createAnalysisConfig(changedFile, analysisResultHandler, baseAnalysisConfig), 
            		dexterPluginManager, 
            		dexterClient);
		}
	}
	
	private void loginOrCreateAccount(final IDexterClient client, final IDexterCLIOption cliOption) {
        if (cliOption.isStandAloneMode())
            return;

        final IAccountHandler accountHandler = createAccountHandler(client, cliOption);
        if (accountHandler.loginOrCreateAccount() == false) {
            cliLog.error("Failed to login or create new account");
            System.exit(1);
        }
    }
	
	private IAccountHandler createAccountHandler(final IDexterClient client, final IDexterCLIOption cliOption) {
        if (cliOption.isStandAloneMode()) {
            return new EmptyAccountHandler();
        } else {
            return new AccountHandler(client, cliLog);
        }
    }
	
	private AnalysisConfig createAnalysisConfig(final String fileFullPath,
            final IAnalysisResultHandler cliAnalysisResultHandler, final AnalysisConfig baseAnalysisConfig) {
        final AnalysisConfig config = new AnalysisEntityFactory()
                .copyAnalysisConfigWithoutSourcecode(baseAnalysisConfig);

        config.setResultHandler(cliAnalysisResultHandler);
        config.setSourceFileFullPath(fileFullPath);
        config.generateFileNameWithSourceFileFullPath();
        config.generateModulePath();

        return config;
    }
	
	private IDexterPluginManager loadDexterPlugins(final IDexterClient client, final IDexterCLIOption cliOption) {
        IDexterPluginInitializer initializer = new CLIPluginInitializer(cliLog);
        IDexterPluginManager pluginManager = new CLIDexterPluginManager(initializer, client, cliLog, cliOption);
        pluginManager.initDexterPlugins();

        return pluginManager;
    }
	
	private IDexterClient createDexterClient(PeerReviewHome home) {
		return new DexterClient(
				new JerseyDexterWebResource(home.getDexterServerConfig()));
	}
}
