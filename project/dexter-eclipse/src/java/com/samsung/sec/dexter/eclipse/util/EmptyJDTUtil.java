package com.samsung.sec.dexter.eclipse.util;

import org.eclipse.core.resources.IFile;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IConfigurationElement;
import org.eclipse.core.runtime.IExtension;
import org.eclipse.core.runtime.Platform;

import com.samsung.sec.dexter.core.analyzer.AnalysisConfig;
import com.samsung.sec.dexter.core.analyzer.IAnalysisEntityFactory;
import com.samsung.sec.dexter.core.exception.DexterRuntimeException;
import com.samsung.sec.dexter.eclipse.DexterEclipseActivator;

public class EmptyJDTUtil implements IJDTUtil {

	@Override
	public AnalysisConfig createAnalysisConfigForJava(IFile file, IAnalysisEntityFactory configFactory) {
		throw new DexterRuntimeException("Fail to load dexter-eclipse-jdt plug-in");
	}

	@Override
	public String getModulePath(IFile file) {
		throw new DexterRuntimeException("Fail to load dexter-eclipse-jdt plug-in");
	}
}
