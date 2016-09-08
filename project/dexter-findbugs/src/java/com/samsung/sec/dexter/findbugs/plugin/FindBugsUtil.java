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
package com.samsung.sec.dexter.findbugs.plugin;

import com.google.common.base.Charsets;
import com.google.common.io.Files;
import com.samsung.sec.dexter.core.util.DexterUtil;

import java.io.File;
import java.io.IOException;
import java.util.Iterator;

import org.apache.log4j.Logger;

import edu.umd.cs.findbugs.BugPattern;
import edu.umd.cs.findbugs.DetectorFactory;
import edu.umd.cs.findbugs.DetectorFactoryCollection;

public class FindBugsUtil {
    private final static Logger LOG = Logger.getLogger(FindBugsUtil.class);

    protected void createHelpHtml(DetectorFactoryCollection DETECTOR_FACTORY_COLLECTION) {
        final Iterator<DetectorFactory> iter = DETECTOR_FACTORY_COLLECTION.factoryIterator();

        while (iter.hasNext()) {
            final DetectorFactory f = iter.next();

            final String fileName = f.getShortName() + ".html";

            final File htmlFile = new File(
                    "C:/DEV/temp/findbugs_html_" + DexterUtil.currentDateTime() + "/" + fileName);

            try {
                final StringBuilder sb = new StringBuilder(1024);
                sb.append(sb);

                for (final BugPattern b : f.getReportedBugPatterns()) {
                    sb.append(b.getDetailHTML());
                    if (b.getCWEid() != 0) {
                        sb.append("CWE ID: ").append(b.getCWEid());
                    }
                }

                Files.write(sb.toString(), htmlFile, Charsets.UTF_8);
            } catch (IOException e) {
                LOG.error(e.getMessage(), e);
            }
        }
    }

    protected void createHelpHtmlEx2(DetectorFactoryCollection DETECTOR_FACTORY_COLLECTION) {
        final Iterator<DetectorFactory> iter = DETECTOR_FACTORY_COLLECTION.factoryIterator();

        while (iter.hasNext()) {
            final DetectorFactory f = iter.next();
            String htmlFolderStr = "C:/DEV/temp/findbugs_html";
            final File htmlFolder = new File(htmlFolderStr);
            if (htmlFolder.exists() == false) {
                DexterUtil.createFolderWithParents(htmlFolderStr);
            }

            try {
                for (final BugPattern b : f.getReportedBugPatterns()) {
                    final StringBuilder sb = new StringBuilder(1024);
                    final File htmlFile = new File("C:/DEV/temp/findbugs_html/" + b.getAbbrev() + ".html");

                    sb.append("Factory Name: ").append(f.getShortName()).append("<br/>\n");
                    sb.append("Bug Pattern Name: ").append(b.getAbbrev()).append("<br/>\n");
                    sb.append("Type: ").append(b.getType()).append("<br/>\n");
                    sb.append("CWE ID: ").append(b.getCWEid()).append("<br/>\n");
                    sb.append("Detail HTML: ").append(b.getDetailHTML()).append("<br/>\n");
                    sb.append("Short Desc: ").append(b.getShortDescription()).append("<br/>\n");
                    sb.append("<br/><br/>\n");

                    if (htmlFile.exists()) {
                        Files.append(sb.toString(), htmlFile, Charsets.UTF_8);
                    } else {
                        Files.write(sb.toString(), htmlFile, Charsets.UTF_8);
                    }
                }

            } catch (IOException e) {
                LOG.error(e.getMessage(), e);
            }
        }
    }

    protected void createHelpHtmlEx(DetectorFactoryCollection DETECTOR_FACTORY_COLLECTION) {
        final Iterator<DetectorFactory> iter = DETECTOR_FACTORY_COLLECTION.factoryIterator();

        while (iter.hasNext()) {
            final DetectorFactory f = iter.next();

            String htmlFolderStr = "C:/DEV/temp/findbugs_html";
            final File htmlFolder = new File(htmlFolderStr);
            if (htmlFolder.exists() == false) {
                DexterUtil.createFolderWithParents(htmlFolderStr);
            }

            try {
                for (final BugPattern b : f.getReportedBugPatterns()) {
                    final StringBuilder sb = new StringBuilder(1024);
                    final File htmlFile = new File("C:/DEV/temp/findbugs_html/" + b.getType() + ".html");

                    sb.append("Factory Name: ").append(f.getShortName()).append("<br/>\n");
                    sb.append("Bug Pattern Name: ").append(b.getAbbrev()).append("<br/>\n");
                    sb.append("Type: ").append(b.getType()).append("<br/>\n");
                    sb.append("CWE ID: ").append(b.getCWEid()).append("<br/>\n");
                    sb.append("Detail Text: ").append(b.getDetailText()).append("<br/>\n");
                    sb.append("Short Desc: ").append(b.getShortDescription()).append("<br/>\n");
                    sb.append("<br/><br/>\n");

                    if (htmlFile.exists()) {
                        Files.append(sb.toString(), htmlFile, Charsets.UTF_8);
                    } else {
                        Files.write(sb.toString(), htmlFile, Charsets.UTF_8);
                    }
                }

            } catch (IOException e) {
                LOG.error(e.getMessage(), e);
            }
        }
    }

    protected void createHelpHtmlEx3(DetectorFactoryCollection DETECTOR_FACTORY_COLLECTION) {
        final Iterator<DetectorFactory> iter = DETECTOR_FACTORY_COLLECTION.factoryIterator();

        while (iter.hasNext()) {
            final DetectorFactory f = iter.next();

            for (final BugPattern b : f.getReportedBugPatterns()) {
                final StringBuilder sb = new StringBuilder(1024);

                sb.append(b.getType()).append("\t");
                sb.append(f.getShortName()).append("\t");
                sb.append(b.getAbbrev()).append("\t");
                sb.append(b.getCWEid()).append("\t");
                sb.append(b.getShortDescription());
            }
        }
    }
}
