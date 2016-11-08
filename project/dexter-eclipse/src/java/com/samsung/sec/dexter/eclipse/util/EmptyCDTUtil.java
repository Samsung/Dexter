package com.samsung.sec.dexter.eclipse.util;

import org.eclipse.core.resources.IFile;

import com.samsung.sec.dexter.core.analyzer.AnalysisConfig;
import com.samsung.sec.dexter.core.analyzer.IAnalysisEntityFactory;
import com.samsung.sec.dexter.core.exception.DexterRuntimeException;

public class EmptyCDTUtil implements ICDTUtil {

	@Override
	public AnalysisConfig createAnalysisConfigForCpp(IFile file, IAnalysisEntityFactory configFactory) {
		throw new DexterRuntimeException("Fail to load dexter-eclipse-cdt plug-in");
	}

	@Override
	public String getModulePath(IFile file) {
		throw new DexterRuntimeException("Fail to load dexter-eclipse-cdt plug-in");
	}
}
