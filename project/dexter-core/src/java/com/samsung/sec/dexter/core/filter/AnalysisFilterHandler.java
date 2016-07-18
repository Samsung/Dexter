/**
 * Copyright (c) 2014 Samsung Electronics, Inc.,
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
package com.samsung.sec.dexter.core.filter;

import com.google.common.base.Charsets;
import com.google.common.base.Strings;
import com.google.common.io.Files;
import com.google.gson.Gson;
import com.samsung.sec.dexter.core.config.DexterConfig;
import com.samsung.sec.dexter.core.config.IDexterHomeListener;
import com.samsung.sec.dexter.core.defect.Defect;
import com.samsung.sec.dexter.core.exception.DexterRuntimeException;
import com.samsung.sec.dexter.core.util.DexterUtil;
import com.samsung.sec.dexter.core.util.IDexterClient;

import java.io.File;
import java.io.IOException;
import java.util.concurrent.atomic.AtomicInteger;

import org.apache.log4j.Logger;

public class AnalysisFilterHandler implements IDexterHomeListener {
	final private static Logger LOG = Logger.getLogger(AnalysisFilterHandler.class);
	private IFalseAlarmConfiguration falseAlarmCfg;
	private boolean hasFilterToUpload;
	// private static int LAST_VERSION = -1;
	private static AtomicInteger LAST_VERSION = new AtomicInteger(-1);

	private AnalysisFilterHandler() {
		DexterConfig.getInstance().addDexterHomeListener(this);
		LOG.debug("AnalysisFilterHandler");
	}

	private static class AnalysisFilterHolder {
		private final static AnalysisFilterHandler INSTANCE = new AnalysisFilterHandler();
	}

	public static AnalysisFilterHandler getInstance() {
		return AnalysisFilterHolder.INSTANCE;
	}

	public boolean isDefectDismissed(final Defect defect) {
		// TODO : Excluding Scope에 해당하는 파일인지 검사 - method, class 등
		if (falseAlarmCfg == null)
			return false;
		else
			return falseAlarmCfg.isFalseAlarm(defect);
	}

	/**
	 * 덱스터 서버에 연결이 되어 있으면 바로 필터를 추가하고, 그렇지 않으면 파일에 기록한다.
	 * 
	 * @precondition parameter Defect object should not be null
	 * @param defect
	 *            void
	 */
	public void addDefectFilter(final Defect defect, final IDexterClient client) {
		assert defect != null;

		try {
			falseAlarmCfg.addFalseAlarm(defect.toDefectFilter());

			client.changeDefectStatus(defect, DexterConfig.DEFECT_STATUS_DISMISSED);
			client.insertDefectFilter(defect);
			setFilterToUpload(false);
		} catch (DexterRuntimeException e) {
			appendFilterToTempFile(defect, true);
			setFilterToUpload(true);
		}
	}

	/**
	 * @param defect
	 *            void
	 */
	public void removeDefectFilter(final Defect defect, final IDexterClient client) {
		assert defect != null;

		try {
			falseAlarmCfg.removeFalseAlarm(defect.toDefectFilter());

			client.changeDefectStatus(defect, DexterConfig.DEFECT_STATUS_NEW);
			client.removeDefectFilter(defect);
			setFilterToUpload(false);
		} catch (DexterRuntimeException e) {
			appendFilterToTempFile(defect, false);
			setFilterToUpload(true);
		}
	}

	private synchronized void appendFilterToTempFile(final Defect defect, final boolean isActive) {
		final File defectFilterFile = new File(DexterConfig.getInstance().getFilterFilePath());

		try {
			if (defectFilterFile.exists() == false && defectFilterFile.createNewFile() == false) {
				LOG.error("Cannot create filter file:" + defectFilterFile.toPath().toString());
				return;
			}

			final DefectFilter filter = defect.toDefectFilter();
			filter.setActive(isActive);
			Files.append(filter.toJson().replace("\\", "/").replace(DexterUtil.LINE_SEPARATOR, "/").replace("//", "/"),
					defectFilterFile, Charsets.UTF_8);
			Files.append(DexterUtil.LINE_SEPARATOR, defectFilterFile, Charsets.UTF_8);
		} catch (IOException e) {
			throw new DexterRuntimeException(e.getMessage() + " : Failed to add filter info to filter json file", e);
		}
	}

	private void setFilterToUpload(boolean has) {
		this.hasFilterToUpload = has;
	}

	public boolean hasFilterToUpload() {
		return this.hasFilterToUpload;
	}

	/**
	 * 현재 필터 데이터 버전과 데이터베이스의 버전을 비교
	 * 
	 * @return
	 */
	public boolean hasFilterToDownload(final IDexterClient client) {
		final int lastVersion = client.getLastFalseAlarmVersion();

		return LAST_VERSION.get() < lastVersion;
	}

	/**
	 * @precondition DexterClient.isLogin == true
	 */
	public void downloadFalseAlarmFilter(final IDexterClient client) {
		final DexterConfig config = DexterConfig.getInstance();
		final String path = config.getFalseAlarmListFilePath();
		final Gson gson = new Gson();
		final File falseAlarmListFile = new File(path);

		try {
			final IFalseAlarmConfiguration cfg = client.getFalseAlarmTree();
			if (cfg != null) {
				this.falseAlarmCfg = cfg;
				Files.write(gson.toJson(cfg), falseAlarmListFile, Charsets.UTF_8);
				LAST_VERSION.set(client.getLastFalseAlarmVersion());
			}
		} catch (Exception e) {
			LAST_VERSION.set(-1);
			LOG.error(e.getMessage(), e);
		}
	}

	/**
	 * @param filterFile
	 */
	public void uploadFalseAlarmFilter(final IDexterClient client) {
		final File filterFile = new File(DexterConfig.getInstance().getFilterFilePath());
		final String filterOldPath = DexterConfig.getInstance().getOldFilterFolderPath();

		try {
			final Gson gson = new Gson();
			for (String content : Files.readLines(filterFile, Charsets.UTF_8)) {
				if (Strings.isNullOrEmpty(content)) {
					continue;
				}

				final DefectFilter filter = gson.fromJson(content, DefectFilter.class);
				final Defect defectFromFilter = filter.toDefect();

				if (filter.isActive()) {
					client.insertDefectFilter(defectFromFilter);
					client.changeDefectStatus(defectFromFilter, DexterConfig.DEFECT_STATUS_DISMISSED);
				} else {
					client.removeDefectFilter(defectFromFilter);
					client.changeDefectStatus(defectFromFilter, DexterConfig.DEFECT_STATUS_NEW);
				}
			}

			Files.copy(filterFile,
					new File(filterOldPath + "/" + DexterUtil.currentDateTime() + "_" + filterFile.getName()));
			Files.write("", filterFile, Charsets.UTF_8);

			setFilterToUpload(false);
		} catch (IOException e) {
			LOG.error(e.getMessage(), e);
		} catch (DexterRuntimeException e) {
			LOG.error(e.getMessage(), e);
		}
	}

	@Override
	public void handleDexterHomeChanged(final String oldPath, final String newPath) {
		loadFilterFromFile();
	}

	private void loadFilterFromFile() {
		final String path = DexterConfig.getInstance().getFalseAlarmListFilePath();
		final File falseAlarmListFile = new File(path);

		if (falseAlarmListFile.exists() && falseAlarmListFile.length() > 0) {
			final String contents = DexterUtil.getContentsFromFile(path, Charsets.UTF_8);
			this.falseAlarmCfg = new Gson().fromJson(contents, FalseAlarmConfigurationTree.class);
		} else {
			falseAlarmCfg = new FalseAlarmConfigurationTree();
		}

		final String filterOldPath = DexterConfig.getInstance().getOldFilterFolderPath();
		DexterUtil.createFolderWithParents(filterOldPath);
	}
}
