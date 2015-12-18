package com.samsung.sec.dexter.core.analyzer;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import java.io.File;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.junit.Test;

import com.google.common.io.Files;
import com.samsung.sec.dexter.core.config.DexterConfig;
import com.samsung.sec.dexter.core.defect.Defect;
import com.samsung.sec.dexter.core.defect.Occurence;
import com.samsung.sec.dexter.core.metrics.CodeMetrics;
import com.samsung.sec.dexter.core.util.DexterUtil;

public class AnalysisResultFileManagerTest {
	@Test
	public void shouldWriteResultJsonFileWhenHasValidResultList() {
		File tempDexterHomePath = Files.createTempDir();
		DexterConfig.getInstance().setDexterHome(tempDexterHomePath.getAbsolutePath());
		
		List<AnalysisResult> resultList = new ArrayList<>();
		
		AnalysisResult result = new AnalysisResult();
		// set general info
		result.setDefectGroupId(100);
		result.setFileName("TestSource.java");
		result.setModulePath("com.samsung");
		result.setProjectFullPath("/home/dev/project");
		result.setProjectName("test-project");
		result.setResultFileFullPath(tempDexterHomePath.getAbsolutePath());
		result.setSnapshotId(200);
		result.setSourceFileFullPath("/home/dev/project/test-project/src/com/samsung/TestSource.java");
		
		// set codeMetrics
		CodeMetrics metrics = new CodeMetrics();
		metrics.addMetric("minComplexity", 1);
		metrics.addMetric("maxComplexity", 17);
		metrics.addMetric("cloc", 1);
		metrics.addMetric("classCount", 1);
		metrics.addMetric("avgComplexity", 4);
		metrics.addMetric("loc", 98);
		metrics.addMetric("methodCount", 5);
		metrics.addMetric("commentRatio", 0.01F);
		metrics.addMetric("sloc", 81);
		
		result.setCodeMetrics(metrics);

		Occurence o = new Occurence();
		o.setStartLine(79);
		o.setEndLine(79);
		o.setMessage("Memory leak: sgtree");
		
		Defect defect = new Defect();
		defect.setCreatedDateTime(1439973272636L);
		defect.setMessage("[#1@79] Memory leak: sgtree ");
		defect.setSeverityCode("CRI");
		defect.setModifiedDateTime(1439973272636L);
		defect.setCheckerCode("memleak");
		defect.setClassName("TestSource");
		defect.setMethodName("testMethod");
		defect.setToolName("my-tool");
		defect.setLanguage(DexterConfig.LANGUAGE.JAVA.toString());
		defect.setFileName("TestSource.java");
		defect.setModulePath("com/samsung");
		
		defect.addOccurence(o);
		
		result.addDefect(defect);
		
		resultList.add(result);
		
		AnalysisResultFileManager.getInstance().writeJson(resultList);
		
		boolean hasResultFile = false;
		String resultFilePath = "";
		
		File resultFile = new File(tempDexterHomePath.getAbsolutePath() + "/result");
		
		for(File file : DexterUtil.getSubFiles(resultFile)){
			final String fileExtension = Files.getFileExtension(file.getAbsolutePath());
			if(file.isFile() && "json".equals(fileExtension)){
				hasResultFile = true;
				resultFilePath = file.getAbsolutePath();
				break;
			}
		}
		
		assertTrue(hasResultFile);
		
		StringBuilder contents = DexterUtil.readFile(resultFilePath);
		Map<String, Object> resultMap = DexterUtil.getMapFromJsonString(contents);
		
		// check general info
		assertEquals("200", resultMap.get(ResultFileConstant.SNAPSHOT_ID));
		assertEquals("com/samsung", resultMap.get(ResultFileConstant.MODULE_PATH));		
		assertEquals("TestSource.java", resultMap.get(ResultFileConstant.FILE_NAME));
		assertEquals("/home/dev/project/test-project/src/com/samsung/TestSource.java", resultMap.get(ResultFileConstant.FULL_FILE_PATH));
		assertEquals("test-project", resultMap.get(ResultFileConstant.PROJECT_NAME));
		assertEquals("1", resultMap.get(ResultFileConstant.GROUP_ID));	// currently Group ID field is not using
		assertEquals("1", resultMap.get(ResultFileConstant.DEFECT_COUNT));
		assertFalse(resultMap.containsKey(ResultFileConstant.TOOL_NAME));
		assertFalse(resultMap.containsKey(ResultFileConstant.LANGUAGE));
		
		// check code metrics
		final String expectedMetricString = "{maxComplexity=17.0, minComplexity=1.0, cloc=1.0, loc=98.0, avgComplexity=4.0, classCount=1.0, commentRatio=0.01, methodCount=5.0, sloc=81.0}";
		assertEquals(expectedMetricString, resultMap.get(ResultFileConstant.CODE_METRICS).toString());
		
		// check defect list
		final String expectedDefectListString = "[{gdid=-1.0, occurences=[{code=, startLine=79.0, endLine=79.0, charStart=-1.0, charEnd=-1.0, variableName=, stringValue=, fieldName=, message=Memory leak: sgtree}], createdDateTime=1.439973272636E12, message=[#1@79] Memory leak: sgtree [#1@79] Memory leak: sgtree , severityCode=CRI, modifiedDateTime=1.439973272636E12, checkerCode=memleak, className=TestSource, methodName=testMethod, toolName=my-tool, language=JAVA, fileName=TestSource.java, modulePath=com/samsung}]";
		assertEquals(expectedDefectListString, resultMap.get(ResultFileConstant.DEFECT_LIST).toString());
	}
}
