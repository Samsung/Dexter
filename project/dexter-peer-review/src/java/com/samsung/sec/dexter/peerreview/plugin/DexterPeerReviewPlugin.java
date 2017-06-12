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
package com.samsung.sec.dexter.peerreview.plugin;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.apache.log4j.Logger;

import com.google.common.collect.Lists;
import com.samsung.sec.dexter.core.analyzer.AnalysisConfig;
import com.samsung.sec.dexter.core.analyzer.AnalysisEntityFactory;
import com.samsung.sec.dexter.core.analyzer.AnalysisResult;
import com.samsung.sec.dexter.core.analyzer.IAnalysisEntityFactory;
import com.samsung.sec.dexter.core.checker.CheckerConfig;
import com.samsung.sec.dexter.core.checker.EmptyCheckerConfig;
import com.samsung.sec.dexter.core.checker.ICheckerConfig;
import com.samsung.sec.dexter.core.config.DexterConfig;
import com.samsung.sec.dexter.core.config.DexterConfig.LANGUAGE;
import com.samsung.sec.dexter.core.defect.Defect;
import com.samsung.sec.dexter.core.defect.Occurence;
import com.samsung.sec.dexter.core.exception.DexterRuntimeException;
import com.samsung.sec.dexter.core.plugin.IDexterPlugin;
import com.samsung.sec.dexter.core.plugin.PluginDescription;
import com.samsung.sec.dexter.core.plugin.PluginVersion;

import net.xeoh.plugins.base.annotations.PluginImplementation;

@PluginImplementation
public class DexterPeerReviewPlugin implements IDexterPlugin {
	protected ICheckerConfig checkerConfig = new EmptyCheckerConfig(DexterPeerReviewPlugin.PLUGIN_NAME,
			DexterConfig.LANGUAGE.ALL);
	private final static Logger logger = Logger.getLogger(DexterPeerReviewPlugin.class);

	public final static String PLUGIN_NAME = "dexter-peerreview";
	public final static PluginVersion PLUGIN_VERSION = new PluginVersion("0.0.1");
	private static PluginDescription PLUGIN_DESCRIPTION = new PluginDescription("Samsung Electroincs", PLUGIN_NAME,
			PLUGIN_VERSION, DexterConfig.LANGUAGE.ALL, "Dexter Peer Review Plug-in");

	private IAnalysisEntityFactory analysisEntityFactory = new AnalysisEntityFactory();

	public Pattern DPRPattern, defaultPattern, totalSeverityPattern, severityPattern;
	public Map<String, String> DPRHashMap = new HashMap<String, String>();

	public DexterPeerReviewPlugin() {
		init();
	}

	@Override
	public void init() {
		initPattern();
		initSeverityHashMap();
	}

	protected void initSeverityHashMap() {
		DPRHashMap.put("cri", "CRI");
		DPRHashMap.put("critical", "CRI");
		DPRHashMap.put("maj", "MAJ");
		DPRHashMap.put("major", "MAJ");
		DPRHashMap.put("crc", "CRC");
	}

	protected void initPattern() {
		DPRPattern = Pattern.compile(DexterConfig.DPR_REG_EXP);
		totalSeverityPattern = Pattern.compile(DexterConfig.TOTAL_SEVERITY_REG_EXP);
		severityPattern = Pattern.compile(DexterConfig.SEVERITY_REG_EXP);
		defaultPattern = Pattern.compile(DexterConfig.SIMPLE_DPR_COMMENT_REG_EXP);
	}

	@Override
	public void destroy() {

	}

	@Override
	public void handleDexterHomeChanged(String oldPath, String newPath) {
		init();
	}

	@Override
	public PluginDescription getDexterPluginDescription() {
		return PLUGIN_DESCRIPTION;
	}

	@Override
	public void setCheckerConfig(ICheckerConfig cc) {
		this.checkerConfig = cc;
	}

	@Override
	public ICheckerConfig getCheckerConfig() {
		return this.checkerConfig;
	}

	public int[] makeOffsetArray(CharSequence sourcecode) {
		int[] offsets = new int[sourcecode.length()];

		int line = 1;
		Pattern pattern = Pattern.compile("\n");
		Matcher matcher = pattern.matcher(sourcecode);

		try {
			matcher.region(0, sourcecode.length());
			while (matcher.find()) {
				offsets[++line] = matcher.end();
			}
			return Arrays.copyOf(offsets, line + 1);
		} catch (IllegalStateException e) {
			throw new DexterRuntimeException(e.getMessage(), e);
		}
	}

	@Override
	public AnalysisResult analyze(AnalysisConfig config) {
		final CharSequence sourcecode = config.getSourcecodeThatReadIfNotExist();
		AnalysisResult result = analysisEntityFactory.createAnalysisResult(config);

		try {
			int[] offsets = makeOffsetArray(sourcecode);
			List<DPRComment> comments = getAllDPRCommentFromSourcecode(offsets, sourcecode);
			List<Defect> defectList = makeDefectList(config, comments);
			result.setDefectList(defectList);
		} catch (Exception e) {
			logger.error("incorrect regExp:");
			logger.error(e.getMessage(), e);
		}
		return result;
	}

	private List<Defect> makeDefectList(AnalysisConfig config, List<DPRComment> comments) {
		ArrayList<DPRComment> criDefects = Lists.newArrayList();
		ArrayList<DPRComment> majDefects = Lists.newArrayList();
		ArrayList<DPRComment> crcDefects = Lists.newArrayList();

		for (DPRComment comment : comments) {
			if ("CRI".equals(comment.getSeverity())) {
				criDefects.add(comment);
			} else if ("MAJ".equals(comment.getSeverity())) {
				majDefects.add(comment);
			} else {
				crcDefects.add(comment);
			}
		}

		List<Defect> defectList = new ArrayList<Defect>();
		if (!criDefects.isEmpty()) {
			defectList.add(makeDefect(config, "CRI", criDefects));
		}
		if (!majDefects.isEmpty()) {
			defectList.add(makeDefect(config, "MAJ", majDefects));
		}
		if (!crcDefects.isEmpty()) {
			defectList.add(makeDefect(config, "CRC", crcDefects));
		}

		return defectList;
	}

	private Defect makeDefect(AnalysisConfig config, String severity, ArrayList<DPRComment> defectInfos) {
		Defect defect = new Defect();

		defect.setCheckerCode("DPR_" + severity);
		defect.setFileName(config.getFileName());
		defect.setModulePath(config.getModulePath());
		defect.setClassName("");
		defect.setMethodName("");
		defect.setLanguage(config.getLanguageEnum().toString());
		defect.setSeverityCode(severity);
		defect.setCategoryName("PeerReview");
		defect.setMessage("");
		defect.setToolName(PLUGIN_NAME);
		defect.setAnalysisType(config.getAnalysisType().name());

		for (DPRComment defectInfo : defectInfos) {
			Occurence occ = new Occurence();
			occ.setStartLine(defectInfo.getStartLine());
			occ.setEndLine(defectInfo.getEndLine());
			occ.setStringValue("DPR");
			occ.setMessage(defectInfo.getReviewComment());

			defect.addOccurence(occ);
		}

		return defect;
	}

	static String getFullCommentWithoutLineSeparator(String comment) {
		return comment.replaceAll("(\r\n|\n)", " ").replaceAll("\\s+", " ").trim();
	}

	protected Map<String, String> getDPRCommentMetadata(String comment) {
		Matcher severityMatcher = severityPattern.matcher(comment);
		Matcher defaultMatcher = defaultPattern.matcher(comment);
		Map<String, String> infoHashMap = new HashMap<String, String>();
		try {
			if (severityMatcher.find()) {
				infoHashMap = getCommentMeta(comment);
			} else if (defaultMatcher.find()) {
				infoHashMap.put("severity", "CRC");
				infoHashMap.put("comment", comment.substring(defaultMatcher.end()).trim());
			} else {
				// nothing to do: It may be not DPR Comment.
			}
			return infoHashMap;
		} catch (IllegalStateException e) {
			throw new DexterRuntimeException(e.getMessage(), e);
		}
	}

	protected String getSeverityFromMap(String severity) {
		if (DPRHashMap.containsKey(severity)) {
			return DPRHashMap.get(severity);
		}
		return "CRC";
	}

	protected Map<String, String> getCommentMeta(String comment) {
		Map<String, String> infoHashMap = new HashMap<String, String>();
		Matcher matcher = severityPattern.matcher(comment);
		try {
			if (matcher.find()) {
				infoHashMap.put("severity", getSeverityFromMap(matcher.group(1).toLowerCase()));
				infoHashMap.put("comment", comment.substring(matcher.end()).trim());
			}
			return infoHashMap;
		} catch (IllegalStateException | IndexOutOfBoundsException e) {
			throw new DexterRuntimeException(e.getMessage(), e);
		}
	}

	@Override
	public boolean supportLanguage(LANGUAGE language) {
		// support all languages
		return true;
	}

	@Override
	public String[] getSupportingFileExtensions() {
		return new String[] { "c", "cpp", "h", "hpp", "cs", "java", "js", "css", "html" };
	}

	public ArrayList<DPRComment> getAllDPRCommentFromSourcecode(int[] offsets, CharSequence sourcecode) {
		ArrayList<DPRComment> matchList = new ArrayList<>();
		Matcher matcher = DPRPattern.matcher(sourcecode);

		try {
			while (matcher.find()) {
				DPRComment comment = new DPRComment();
				comment.setStartLine(getLineFromOffset(offsets, matcher.start()));
				comment.setEndLine(getLineFromOffset(offsets, matcher.end()));
				comment.setFullComment(getFullCommentWithoutLineSeparator(matcher.group()));

				Map<String, String> infoHashMap = getDPRCommentMetadata(comment.getFullComment());
				comment.setSeverity(infoHashMap.get("severity"));
				comment.setReviewComment(infoHashMap.get("comment"));

				matchList.add(comment);
			}
			return matchList;
		} catch (IllegalStateException | IndexOutOfBoundsException e) {
			throw new DexterRuntimeException(e.getMessage(), e);
		}
	}

	protected int getLineFromOffset(int[] offsets, int offset) {
		boolean flag = false;
		int index = -1;

		int low = 0;
		int mid = 0;
		int high = offsets.length - 1;

		while ((low <= high)) {
			mid = ((low + high) >>> 1);
			if (offsets[mid] == offset) {
				flag = true;
				index = mid;
				break;
			}

			if (offsets[mid] < offset) {
				low = mid + 1;
			} else {
				high = mid - 1;
			}

			if ((low == mid || mid == high) && flag == false) {
				index = high;
				break;
			}
		}
		return (index);
	}
}
