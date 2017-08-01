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

import com.google.common.io.Files;
import com.samsung.sec.dexter.core.defect.Defect;
import com.samsung.sec.dexter.core.defect.Occurence;
import com.samsung.sec.dexter.core.util.DexterUtil;

import java.io.File;
import java.io.IOException;
import java.util.List;

import org.apache.commons.io.Charsets;

public class CLIResultFile implements ICLIResultFile {
    @Override
    public void writeJsonResultFilePrefix(final File file) throws IOException {
        Files.append("[\n", file, Charsets.UTF_8);
    }

    @Override
    public void writeXmlResultFilePrefix(final File file) throws IOException {
        Files.append("<dexter-result created=\"" + DexterUtil.currentDateTime() + "\">\n",
                file, Charsets.UTF_8);
    }

    @Override
    public void writeXml2ResultFilePrefix(final File file) throws IOException {
        Files.append("<dexter-result created=\"" + DexterUtil.currentDateTime() + "\">\n",
                file, Charsets.UTF_8);
    }

    @Override
    public void writeJsonResultFileBody(final File file, final List<Defect> allDefectList) throws IOException {
        for (Defect defect : allDefectList) {
            Files.append(defect.toJson(), file, Charsets.UTF_8);
            Files.append(",\n", file, Charsets.UTF_8);
        }
    }

    @Override
    public void writeXmlResultFileBody(final File file, final List<Defect> allDefectList,
            final String sourceFileFullPath) throws IOException {
        int size = allDefectList.size() * 1024;
        StringBuilder m;
        if (size < Integer.MAX_VALUE)
            m = new StringBuilder(size);
        else
            m = new StringBuilder(Integer.MAX_VALUE);

        m.append("\t<error filename=\"").append(sourceFileFullPath).append("\">\n");

        for (Defect defect : allDefectList) {
            m.append("\t\t<defect checker=\"").append(defect.getCheckerCode()).append("\">\n");
            for (Occurence o : defect.getOccurences()) {
                m.append("\t\t\t<occurence startLine=\"").append(o.getStartLine()).append("\" ")
                        .append("endLine=\"").append(o.getEndLine()).append("\" ")
                        .append(" message=\"").append(o.getMessage().replace("\"", "&quot;")).append("\" />\n");
            }
            m.append("\t\t</defect>\n");
        }

        m.append("\t</error>\n");

        m.trimToSize();
        Files.append(m.toString(), file, Charsets.UTF_8);
    }

    @Override
    public void writeXml2ResultFileBody(final File file, final List<Defect> allDefectList,
            final String sourceFileFullPath) throws IOException {
        int size = allDefectList.size() * 1024;
        StringBuilder m;
        if (size < Integer.MAX_VALUE)
            m = new StringBuilder(size);
        else
            m = new StringBuilder(Integer.MAX_VALUE);

        m.append("\t<error filename=\"").append(sourceFileFullPath).append("\">\n");

        for (Defect defect : allDefectList) {
            m.append("\t\t<defect checker=\"").append(defect.getCheckerCode()).append("\">\n");
            for (Occurence o : defect.getOccurences()) {
                m.append("\t\t\t<occurence startLine=\"").append(o.getStartLine()).append("\" ")
                        .append("endLine=\"").append(o.getEndLine()).append("\" ")
                        .append(" message=\"").append(o.getMessage()).append("\" />\n");
            }
            m.append("\t\t</defect>\n");
        }

        m.append("\t</error>\n");

        m.trimToSize();
        Files.append(m.toString(), file, Charsets.UTF_8);
    }

    @Override
    public void writeJsonResultFilePostfix(final File file) throws IOException {
        Files.append("]", file, Charsets.UTF_8);
    }

    @Override
    public void writeXmlResultFilePostfix(final File file) throws IOException {
        Files.append("</dexter-result>", file, Charsets.UTF_8);
    }

    @Override
    public void writeXml2ResultFilePostfix(final File file) throws IOException {
        Files.append("</dexter-result>", file, Charsets.UTF_8);
    }
}
