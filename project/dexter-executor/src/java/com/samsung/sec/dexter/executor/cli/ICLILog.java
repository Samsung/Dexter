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

import com.samsung.sec.dexter.core.plugin.PluginDescription;

import java.io.File;
import java.io.PrintStream;

public interface ICLILog {
    public void printStartingAnalysisMessage();

    public void info(String message);

    public void warn(String message);

    public void error(String message);

    public void infoln(String message);

    public void warnln(String message);

    public void errorln(String message);

    public void errorln(String message, Throwable t);

    void setPrintStream(final PrintStream out);

    void printMessagePreAsyncAnalysis(final String dexterWebUrl);

    void printMessagePreSyncAnalysis();

    void printElapsedTime(final long elapsedSeconds);

    void printResultFileLocation(final File file);

    public void printErrorMessageWhenNoPlugins();

    public void printMessageWhenPluginLoaded(final PluginDescription desc);

    void error(String message, Throwable t);

    void warn(String message, Throwable t);

    void warnln(String message, Throwable t);
}
