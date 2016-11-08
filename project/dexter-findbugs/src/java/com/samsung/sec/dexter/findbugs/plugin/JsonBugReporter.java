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
package com.samsung.sec.dexter.findbugs.plugin;

import javax.annotation.CheckForNull;

import org.apache.log4j.Logger;

import com.samsung.sec.dexter.core.analyzer.AnalysisResult;
import com.samsung.sec.dexter.core.checker.CheckerConfig;
import com.samsung.sec.dexter.core.config.DexterConfig;
import com.samsung.sec.dexter.core.config.DexterConfig.RunMode;
import com.samsung.sec.dexter.core.defect.PreOccurence;

import edu.umd.cs.findbugs.AbstractBugReporter;
import edu.umd.cs.findbugs.AnalysisError;
import edu.umd.cs.findbugs.BugAnnotation;
import edu.umd.cs.findbugs.BugCollection;
import edu.umd.cs.findbugs.BugInstance;
import edu.umd.cs.findbugs.ClassAnnotation;
import edu.umd.cs.findbugs.DetectorFactory;
import edu.umd.cs.findbugs.FieldAnnotation;
import edu.umd.cs.findbugs.IntAnnotation;
import edu.umd.cs.findbugs.LocalVariableAnnotation;
import edu.umd.cs.findbugs.MethodAnnotation;
import edu.umd.cs.findbugs.Project;
import edu.umd.cs.findbugs.SortedBugCollection;
import edu.umd.cs.findbugs.SourceLineAnnotation;
import edu.umd.cs.findbugs.StringAnnotation;
import edu.umd.cs.findbugs.classfile.ClassDescriptor;

public class JsonBugReporter extends AbstractBugReporter {
	private final static Logger logger = Logger.getLogger(JsonBugReporter.class);
	private final SortedBugCollection bugCollection;
	private AnalysisResult result;
	private CheckerConfig checkerConfig;

	/**
	 * @param project
	 */
	public JsonBugReporter(Project project, CheckerConfig config, AnalysisResult result) {
		this.checkerConfig = config;
		this.result = result;
		
		this.bugCollection = new SortedBugCollection(getProjectStats(), project);
		bugCollection.setTimestamp(System.currentTimeMillis());
	}

	@Override
	public void finish() {
		final BugCollection bugCollection = getBugCollection();
		if(bugCollection == null){
			logger.debug("SortedBugCollection is null, there is no defect data for " + result.getSourceFileFullPath());
			return;
		}
		
		/*
		if(bugCollection.getCollection().size() > 0){
			logger.info(";(   " + result.getFileName() + " has " +  bugCollection.getCollection().size() + " defect(s) :");
		} else {
			logger.info(":)   " + result.getFileName() + " has no defect by analyzing with " + FindbugsDexterPlugin.PLUGIN_NAME);
		}*/
		
		for (final BugInstance bugInstance : bugCollection.getCollection()) {
			final PreOccurence preOccr = new PreOccurence();
			
			final DetectorFactory factory = bugInstance.getDetectorFactory();
			if(factory == null){
				logger.error("Cannot add defect information because DetectorFactory is null (BugInstance: " + bugInstance.getAbbrev() + ")");
				continue;
			}

			final String checkerCode = bugInstance.getType();
			if(!checkerConfig.isActiveChecker(checkerCode)){
				continue;
			}
			
			preOccr.setCheckerCode(checkerCode);
			preOccr.setSeverityCode(checkerConfig.getCheckerSeverity(checkerCode));
			preOccr.setToolName(FindbugsDexterPlugin.PLUGIN_NAME);
			preOccr.setLanguage(DexterConfig.LANGUAGE.JAVA.toString());
			preOccr.setMessage(bugInstance.getMessage());
			preOccr.setFileName(result.getFileName());
			preOccr.setOccurenceCode(bugInstance.getBugPattern().getAbbrev());
			
			handleBugAnnotation(bugInstance, preOccr);

			result.addDefectWithPreOccurence(preOccr);
			
			if(DexterConfig.getInstance().getRunMode() == RunMode.CLI){
				logger.info("\t\t" + preOccr.toJson());
			}
		}
	}

	private void handleBugAnnotation(final BugInstance bugInstance,
			final PreOccurence preOccr) {
		for (final BugAnnotation bugAnnotation : bugInstance.getAnnotations()) {
			if (bugAnnotation instanceof SourceLineAnnotation) {
				if (!result.getFileName().equals(((SourceLineAnnotation) bugAnnotation).getSourceFile())) {
					continue;
				}
				final SourceLineAnnotation annt = (SourceLineAnnotation) bugAnnotation;
				preOccr.setStartLine(annt.getStartLine());
				preOccr.setEndLine(annt.getEndLine());
			} else if (bugAnnotation instanceof ClassAnnotation) {
				if (!result.getFileName().equals(((ClassAnnotation) bugAnnotation).getSourceFileName())) {
					continue;
				}
				final ClassAnnotation annt = (ClassAnnotation) bugAnnotation;
				preOccr.setClassName(annt.getClassName());
				preOccr.setModulePath(annt.getPackageName());
			} else if (bugAnnotation instanceof IntAnnotation) {
				final IntAnnotation annt = (IntAnnotation) bugAnnotation;
				logger.debug(annt.isSignificant());
			} else if (bugAnnotation instanceof LocalVariableAnnotation) {
				final LocalVariableAnnotation annt = (LocalVariableAnnotation) bugAnnotation;
				preOccr.setVariableName(annt.getName());
			} else if (bugAnnotation instanceof StringAnnotation) {
				final StringAnnotation annt = (StringAnnotation) bugAnnotation;
				preOccr.setStringValue(annt.getValue());
			} else if (bugAnnotation instanceof MethodAnnotation) {
				if (!result.getFileName().equals(((MethodAnnotation) bugAnnotation).getSourceFileName())) {
					continue;
				}
				final MethodAnnotation annt = (MethodAnnotation) bugAnnotation;
				if("<init>".equals(annt.getMethodName())){
					preOccr.setMethodName("constructor");
					continue;
				}
				preOccr.setMethodName(annt.getMethodName());
			} else if (bugAnnotation instanceof FieldAnnotation) {
				if (!result.getFileName().equals(((FieldAnnotation) bugAnnotation).getSourceFileName())) {
					continue;
				}
				
				final FieldAnnotation annt = (FieldAnnotation) bugAnnotation;
				preOccr.setFieldName(annt.getFieldName());
			} else {
				// TypeAnnotation, IntAnnotation
				// logger.debug("No Processing Annotation: " +
				// ba.getDescription());
			}
		}
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see edu.umd.cs.findbugs.BugReporter#getBugCollection()
	 */
	@Override
	public @CheckForNull BugCollection getBugCollection() {
		return this.bugCollection;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * edu.umd.cs.findbugs.classfile.IClassObserver#observeClass(edu.umd.cs.
	 * findbugs.classfile.ClassDescriptor)
	 */
	@Override
	public void observeClass(ClassDescriptor classDescriptor) {
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * edu.umd.cs.findbugs.AbstractBugReporter#doReportBug(edu.umd.cs.findbugs
	 * .BugInstance)
	 */
	@Override
	protected void doReportBug(BugInstance bugInstance) {
		if (bugCollection.add(bugInstance))
			notifyObservers(bugInstance);
	}

	private boolean analysisErrors;

	private boolean missingClasses;

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * edu.umd.cs.findbugs.AbstractBugReporter#reportAnalysisError(edu.umd.cs
	 * .findbugs.AnalysisError)
	 */
	@Override
	public void reportAnalysisError(AnalysisError error) {
		if (!analysisErrors) {
			emitLine("The following errors occurred during analysis :");
			analysisErrors = true;
		}
		emitLine("\t" + error.getMessage());
		if (error.getExceptionMessage() != null) {
			emitLine("\t\t" + error.getExceptionMessage());
			final String[] stackTrace = error.getStackTrace();
			if (stackTrace != null) {
				for (final String aStackTrace : stackTrace) {
					emitLine("\t\t\tAt " + aStackTrace);
				}
			}
		}
	}

	/**
	 * Emit one line of the error message report. By default, error messages are
	 * printed to System.err. Subclasses may override.
	 * 
	 * @param line
	 *            one line of the error report
	 */
	protected void emitLine(String line) {
		line = line.replaceAll("\t", "  ");
		logger.error(line);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * edu.umd.cs.findbugs.AbstractBugReporter#reportMissingClass(java.lang.
	 * String)
	 */
	@Override
	public void reportMissingClass(String message) {
		if (!missingClasses) {
			emitLine("The following classes needed for analysis were missing:");
			missingClasses = true;
		}
		emitLine("\t" + message);
	}
}
