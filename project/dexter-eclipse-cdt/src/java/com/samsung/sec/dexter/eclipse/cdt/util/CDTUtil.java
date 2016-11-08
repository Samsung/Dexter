package com.samsung.sec.dexter.eclipse.cdt.util;

import org.eclipse.core.resources.IFile;

import com.samsung.sec.dexter.core.analyzer.AnalysisConfig;
import com.samsung.sec.dexter.core.analyzer.IAnalysisEntityFactory;
import com.samsung.sec.dexter.eclipse.util.ICDTUtil;

public class CDTUtil implements ICDTUtil {

	public CDTUtil() {
	}

	@Override
	public AnalysisConfig createAnalysisConfigForCpp(IFile file, IAnalysisEntityFactory configFactory) {
		return EclipseCppUtil.createAnalysisConfigForCpp(file, configFactory);
	}

	@Override
	public String getModulePath(IFile file) {
		return EclipseCppUtil.getModulePath(file);
	}
}
