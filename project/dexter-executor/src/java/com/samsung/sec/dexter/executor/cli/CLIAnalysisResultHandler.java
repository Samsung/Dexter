package com.samsung.sec.dexter.executor.cli;

import java.io.IOException;
import java.util.List;

import com.samsung.sec.dexter.core.analyzer.AnalysisResult;
import com.samsung.sec.dexter.core.analyzer.EndOfAnalysisHandler;
import com.samsung.sec.dexter.core.defect.Defect;
import com.samsung.sec.dexter.core.defect.Occurence;
import com.samsung.sec.dexter.core.util.DexterClient;
import com.samsung.sec.dexter.executor.DexterAnalyzer;

public class CLIAnalysisResultHandler implements EndOfAnalysisHandler {
	private ICLILog cliLog;
	private IDexterCLIOption cliOption;
	private ICLIResultFile cliResultFile;
	
	private int totalCnt = 0;
	private int totalOccurenceCnt = 0;
	private int criticalCnt = 0;
	private int majorCnt = 0;
	private int minorCnt = 0;
	private int etcCnt = 0;
	private int crcCnt = 0;
	
	public CLIAnalysisResultHandler(final ICLILog cliLog, final IDexterCLIOption cliOption,
			final ICLIResultFile cliResultFile) {
		this.cliLog = cliLog;
		this.cliOption = cliOption;
		this.cliResultFile = cliResultFile;
	}
	
	@Override
	public void handleAnalysisResult(List<AnalysisResult> resultList) {
		assert resultList.size() > 0;
		
		List<Defect> allDefectList = DexterAnalyzer.getAllDefectList(resultList);
		final String sourceFileFullPath = DexterAnalyzer.getSourceFileFullPath(resultList);
		
		try {
            writeResultFile(allDefectList, sourceFileFullPath);
        } catch (IOException e) {
        	cliLog.errorln(e.getMessage(), e);
        }
		
		
		cliLog.infoln(" - " + sourceFileFullPath);
		
		if(allDefectList.size() == 0){
			cliLog.infoln("    > No Defect");
			return;
		} else {
			cliLog.infoln("    > Total Defects: " + allDefectList.size());
		}

		printDefect(allDefectList);
	}

	private void writeResultFile(List<Defect> allDefectList, String sourceFileFullPath) throws IOException {
		if(cliOption.isJsonFile()){
			cliResultFile.writeJsonResultFileBody(cliOption.getJsonResultFile(), allDefectList);
		}
		
		if(cliOption.isXmlFile()){
			cliResultFile.writeXmlResultFileBody(cliOption.getXmlResultFile(), allDefectList, sourceFileFullPath);
		}
		
		if(cliOption.isXml2File()){
			cliResultFile.writeXml2ResultFileBody(cliOption.getXml2ResultFile(), allDefectList, sourceFileFullPath);
		}
    }

	private void printDefect(List<Defect> allDefectList) {
		for (final Defect defect : allDefectList) {
			switch(defect.getSeverityCode()){
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
			
			cliLog.infoln("    > " + defect.getCheckerCode() + " / " + defect.getSeverityCode() + " / " + defect.getOccurences().size());
			printOccurences(defect.getOccurences().toArray(new Occurence[defect.getOccurences().size()]));
		}
		
		printLogAfterAnalyze();
    }
	
	private void printOccurences(Occurence[] occs){
		for(int i=0; i<occs.length; i++){
			final Occurence o = occs[i];
			
			if((i+1) == occs.length){
				cliLog.infoln("       └ " + o.getStartLine() + " " + o.getMessage());
			} else {
				cliLog.infoln("       ├ " + o.getStartLine() + " " + o.getMessage());
			}
		}
	}
	
	private void printLogAfterAnalyze() {
	    if(cliOption.isAsynchronousMode()) return;
	    
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
		
		if(cliOption.isStandAloneMode() == false){
			cliLog.infoln("※ See Dexter WEB site for more information : " + DexterClient.getInstance().getDexterWebUrl());
			cliLog.infoln("");
		}
    }
}
