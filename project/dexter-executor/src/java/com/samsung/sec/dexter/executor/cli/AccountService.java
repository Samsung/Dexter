package com.samsung.sec.dexter.executor.cli;

import com.samsung.sec.dexter.core.util.DexterClient;
import com.samsung.sec.dexter.core.util.DexterServerConfig;
import com.samsung.sec.dexter.core.util.EmptyDexterClient;
import com.samsung.sec.dexter.core.util.IDexterClient;
import com.samsung.sec.dexter.core.util.IDexterWebResource;
import com.samsung.sec.dexter.core.util.JerseyDexterWebResource;

public class AccountService {
	private ICLILog cliLog;
	
    public AccountService(ICLILog cliLog) {
		this.cliLog = cliLog;
	}

	public ICLILog getCliLog() {
		return cliLog;
	}

	public void createAccount(final IDexterCLIOption cliOption) {
        final IDexterClient client = createDexterClient(cliOption);
        final IAccountHandler accountHandler = createAccountHandler(client, cliOption);
        accountHandler.createAccount(cliOption.getUserId(), cliOption.getUserPassword());
    }

    public IAccountHandler createAccountHandler(final IDexterClient client, final IDexterCLIOption cliOption) {
        if (cliOption.isStandAloneMode()) {
            return new EmptyAccountHandler();
        } else {
            return new AccountHandler(client, cliLog);
        }
    }

    public IDexterClient createDexterClient(final IDexterCLIOption cliOption) {
        if (cliOption.isStandAloneMode()) {
            return new EmptyDexterClient();
        } else {
        	IDexterWebResource webResource = new JerseyDexterWebResource(
        			new DexterServerConfig(
        					cliOption.getUserId(), 
	    					cliOption.getUserPassword(),
	    					cliOption.getServerHostIp(), 
	    					cliOption.getServerPort()));
        	
            return new DexterClient(webResource);
        }
    }
}
