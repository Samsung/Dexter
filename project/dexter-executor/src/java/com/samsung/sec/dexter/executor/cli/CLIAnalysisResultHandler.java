/**
 * Copyright (c) 2016 Samsung Electronics, Inc.,
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
package com.samsung.sec.dexter.executor.cli;

import com.samsung.sec.dexter.core.analyzer.AnalysisResult;
import com.samsung.sec.dexter.core.analyzer.AnalysisResultFileManager;
import com.samsung.sec.dexter.core.analyzer.EndOfAnalysisHandler;
import com.samsung.sec.dexter.core.defect.Defect;
import com.samsung.sec.dexter.core.defect.Occurence;
import com.samsung.sec.dexter.core.job.SendResultJob;
import com.samsung.sec.dexter.core.util.IDexterClient;
import com.samsung.sec.dexter.executor.DexterAnalyzer;

import java.io.IOException;
import java.util.List;

public class CLIAnalysisResultHandler implements EndOfAnalysisHandler {
	private ICLILog cliLog;
	private IDexterCLIOption cliOption;
	private ICLIResultFile cliResultFile;
	private String dexterWebUrl = "";

	private int totalCnt = 0;
	private int totalOccurenceCnt = 0;
	private int criticalCnt = 0;
	private int majorCnt = 0;
	private int minorCnt = 0;
	private int etcCnt = 0;
	private int crcCnt = 0;

	public CLIAnalysisResultHandler(final String dexterWebUrl, final ICLIResultFile cliResultFile,
			final IDexterCLIOption cliOption, final ICLILog cliLog) {
		this.dexterWebUrl = dexterWebUrl;
		this.cliResultFile = cliResultFile;
		this.cliLog = cliLog;
		this.cliOption = cliOption;
	}

	@Override
	public void handleAnalysisResult(List<AnalysisResult> resultList, final IDexterClient client) {
		assert resultList.size() > 0;

		handleBeginnigOfResultFile();

		List<Defect> allDefectList = DexterAnalyzer.getAllDefectList(resultList);
		final AnalysisResult firstAnalysisResult = resultList.get(0);
		final String sourceFileFullPath = firstAnalysisResult.getSourceFileFullPath();

		try {
			writeResultFile(allDefectList, sourceFileFullPath);

			if (cliOption.isStandAloneMode()) {
				return;
			}

			final String resultFilePrefix = AnalysisResultFileManager.getInstance()
					.getResultFilePrefixName(firstAnalysisResult.getModulePath(), firstAnalysisResult.getFileName());
			SendResultJob.sendResultFileThenDelete(client, resultFilePrefix);
		} catch (IOException e) {
			cliLog.errorln(e.getMessage(), e);
		}

		cliLog.infoln(" - " + sourceFileFullPath);

		if (allDefectList.size() == 0) {
			cliLog.infoln("    > No Defect");
			return;
		} else {
			cliLog.infoln("    > Total Defects: " + allDefectList.size());
		}

		printDefect(allDefectList);

		handleEndOfResultFile();
	}

	private void handleBeginnigOfResultFile() {
		try {
			if (cliOption.isJsonFile()) {
				cliResultFile.writeJsonResultFilePrefix(cliOption.getJsonResultFile());
			}

			if (cliOption.isXmlFile()) {
				cliResultFile.writeXmlResultFilePrefix(cliOption.getXmlResultFile());
			}

			if (cliOption.isXml2File()) {
				cliResultFile.writeXml2ResultFilePrefix(cliOption.getXmlResultFile());
			}
		} catch (IOException e) {
			cliLog.errorln(e.getMessage(), e);
		}
	}

	private void handleEndOfResultFile() {
		try {
			if (cliOption.isJsonFile()) {
				cliResultFile.writeJsonResultFilePostfix(cliOption.getJsonResultFile());
				cliLog.printResultFileLocation(cliOption.getJsonResultFile());
			}

			if (cliOption.isXmlFile()) {
				cliResultFile.writeXmlResultFilePostfix(cliOption.getXmlResultFile());
				cliLog.printResultFileLocation(cliOption.getXmlResultFile());
			}

			if (cliOption.isXml2File()) {
				cliResultFile.writeXml2ResultFilePostfix(cliOption.getXmlResultFile());
				cliLog.printResultFileLocation(cliOption.getXml2ResultFile());
			}
		} catch (IOException e) {
			cliLog.errorln(e.getMessage(), e);
		}
	}

	private void writeResultFile(List<Defect> allDefectList, String sourceFileFullPath) throws IOException {
		if (cliOption.isJsonFile()) {
			cliResultFile.writeJsonResultFileBody(cliOption.getJsonResultFile(), allDefectList);
		}

		if (cliOption.isXmlFile()) {
			cliResultFile.writeXmlResultFileBody(cliOption.getXmlResultFile(), allDefectList, sourceFileFullPath);
		}

		if (cliOption.isXml2File()) {
			cliResultFile.writeXml2ResultFileBody(cliOption.getXml2ResultFile(), allDefectList, sourceFileFullPath);
		}
	}

	private void printDefect(List<Defect> allDefectList) {
		for (final Defect defect : allDefectList) {
			switch (defect.getSeverityCode()) {
			case "CRI":
				criticalCnt++;
				break;
			case "MAJ":
				majorCnt++;
				break;
			case "MIN":
				minorCnt++;
				break;
			case "CRC":
				crcCnt++;
				break;
			case "ETC":
				etcCnt++;
				break;
			default:
				defect.setSeverityCode("ETC");
				etcCnt++;
				break;
			}

			totalCnt++;
			totalOccurenceCnt += defect.getOccurences().size();

			cliLog.infoln("    > " + defect.getCheckerCode() + " / " + defect.getSeverityCode() + " / "
					+ defect.getOccurences().size());
			printOccurences(defect.getOccurences().toArray(new Occurence[defect.getOccurences().size()]));
		}

		printLogAfterAnalyze();
	}

	private void printOccurences(Occurence[] occs) {
		for (int i = 0; i < occs.length; i++) {
			final Occurence o = occs[i];

			if ((i + 1) == occs.length) {
				cliLog.infoln("       └ " + o.getStartLine() + " " + o.getMessage());
			} else {
				cliLog.infoln("       ├ " + o.getStartLine() + " " + o.getMessage());
			}
		}
	}

	private void printLogAfterAnalyze() {
		if (cliOption.isAsynchronousMode())
			return;

		cliLog.infoln("");
		cliLog.infoln("====================================================");
		cliLog.infoln("- Total Defects: " + totalCnt);
		cliLog.infoln("- Critical Defects: " + criticalCnt);
		cliLog.infoln("- Major Defects: " + majorCnt);
		cliLog.infoln("- Minor Defects: " + minorCnt);
		cliLog.infoln("- CRC Defects: " + crcCnt);
		cliLog.infoln("- Etc. Defects: " + etcCnt);
		cliLog.infoln("- Total Occurences: " + totalOccurenceCnt);
		cliLog.infoln("====================================================");
		cliLog.infoln("");

		if (cliOption.isStandAloneMode() == false) {
			cliLog.infoln("※ See Dexter WEB site for more information : " + dexterWebUrl);
			cliLog.infoln("");
		}
	}
}
