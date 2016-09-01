/**
 * Copyright (c) 2016 Samsung Electronics, Inc.,
 * All rights reserved.
 * 
 * Redistribution and use in source and binary forms, with or without
 * modification, are permitted provided that the following conditions are met:
 * 
 * * Redistributions of source code must retain the above copyright notice, this
 * list of conditions and the following disclaimer.
 * 
 * * Redistributions in binary form must reproduce the above copyright notice,
 * this list of conditions and the following disclaimer in the documentation
 * and/or other materials provided with the distribution.
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
package com.samsung.sec.dexter.executor.cli;

import com.samsung.sec.dexter.core.checker.Checker;
import com.samsung.sec.dexter.core.exception.DexterRuntimeException;
import com.samsung.sec.dexter.core.plugin.BaseDexterPluginManager;
import com.samsung.sec.dexter.core.plugin.IDexterPlugin;
import com.samsung.sec.dexter.core.plugin.IDexterPluginInitializer;
import com.samsung.sec.dexter.core.plugin.PluginDescription;
import com.samsung.sec.dexter.core.util.IDexterClient;

import java.util.ArrayList;
import java.util.List;

public class CLIDexterPluginManager extends BaseDexterPluginManager {
    private ICLILog cliLog;
    private IDexterCLIOption cliOption;

    public CLIDexterPluginManager(final IDexterPluginInitializer pluginInitializer, final IDexterClient client,
            final ICLILog cliLog, final IDexterCLIOption cliOption) {
        super(pluginInitializer, client);

        assert cliLog != null;
        assert cliOption != null;

        this.cliLog = cliLog;
        this.cliOption = cliOption;
    }

    @Override
    public void initDexterPlugins() throws DexterRuntimeException {
        assert initializer != null;

        pluginList = new ArrayList<IDexterPlugin>(0);
        initializer.init(pluginList);

        if (getPluginList().size() == 0) {
            cliLog.printErrorMessageWhenNoPlugins();
            //System.exit(1);
        }

        initSupportingFileExetensions();

        if (!cliOption.isStandAloneMode() && client.isServerAlive()) {
            updateCheckerConfig();
        }

        for (IDexterPlugin plugin : getPluginList()) {
            PluginDescription desc = plugin.getDexterPluginDescription();

            resetCheckerEnable(desc.getPluginName(), desc.getLanguage().toString(),
                    plugin.getCheckerConfig().getCheckerList());
        }
    }

    private void resetCheckerEnable(final String toolName, final String language, final List<Checker> checkers) {
        if (cliOption.isSpecifiedCheckerEnabledMode() == false)
            return;

        for (Checker checker : checkers) {
            boolean isEnable = cliOption.checkCheckerEnablenessByCliOption(toolName, language, checker);
            checker.setActive(isEnable);
        }
    }
}
