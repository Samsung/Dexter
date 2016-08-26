/**
 * Copyright (c) 2014 Samsung Electronics, Inc.,
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

import java.io.File;
import java.util.List;

public interface IDexterCLIOption {
    public enum CommandMode {
        NONE, CREATE_ACCOUNT, STATIC_ANALYSIS
    };

    /**
     * @param args
     * it should be parameters from main method.
     */
    void createCliOptionFromArguments(final String[] args);

    /**
     * @return full file path of dexter_cfg.json file, which has configurations
     * to run Dexter CLI such as project name, source folder path, etc.
     */
    String getConfigFilePath();

    boolean isStandAloneMode();

    boolean isAsynchronousMode();

    boolean isSpecifiedCheckerEnabledMode();

    boolean isTargetFilesOptionEnabled();

    List<String> getTargetFileFullPathList();

    String getUserId();

    String getUserPassword();

    boolean checkCheckerEnablenessByCliOption(final String toolName, final String language, Checker checker);

    boolean isXml2File();

    boolean isXmlFile();

    boolean isJsonFile();

    File getXml2ResultFile();

    File getXmlResultFile();

    File getJsonResultFile();

    CommandMode getCommandMode();

    int getServerPort();

    String getServerHostIp();

}
