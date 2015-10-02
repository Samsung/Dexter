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
package com.samsung.sec.dexter.eclipse.builder;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.nio.charset.Charset;

import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.IMarker;
import org.eclipse.core.resources.IResource;
import org.eclipse.core.runtime.CoreException;

import com.google.common.base.Strings;
import com.samsung.sec.dexter.core.analyzer.ResultFileConstant;
import com.samsung.sec.dexter.core.defect.Defect;
import com.samsung.sec.dexter.core.defect.Occurence;
import com.samsung.sec.dexter.core.exception.DexterRuntimeException;
import com.samsung.sec.dexter.core.util.DexterUtil;
import com.samsung.sec.dexter.eclipse.DexterEclipseActivator;


public class DexterMarker {
	public static final String DEFECT_MARKER_TYPE = "dexter-eclipse.defectProblem";
	public static final String DEFECT_DISMISSED_MARKER_TYPE = "dexter-eclipse.defectDismissedProblem";
	
	public static void addMarker(IFile file, Defect defect, Occurence occurence, boolean isDismissed) {
		
		final int severity = toEclipseMarkerSeverity(defect.getSeverityCode());
		final String message = occurence.getMessage();
		
		try {
			IMarker marker = null; 
			final StringBuilder msg = new StringBuilder(100);
			
			
			if(isDismissed){
				marker = file.createMarker(DEFECT_DISMISSED_MARKER_TYPE);
				
				msg.append(message).append(" ").append(DexterUtil.LINE_SEPARATOR)
					.append("DID: To-Be-Defined").append(DexterUtil.LINE_SEPARATOR)
					.append("Status: Dismissed").append(" ").append(DexterUtil.LINE_SEPARATOR);
				
				marker.setAttribute(IMarker.PRIORITY, IMarker.PRIORITY_LOW);
			} else {
				marker = file.createMarker(DEFECT_MARKER_TYPE);
				
				msg.append(message).append(" ").append(DexterUtil.LINE_SEPARATOR)
					.append("DID: To-Be-Defined").append(" ").append(DexterUtil.LINE_SEPARATOR)
					.append("Status: New").append(" ").append(DexterUtil.LINE_SEPARATOR);
				
				marker.setAttribute(IMarker.SEVERITY, severity);
				marker.setAttribute(IMarker.PRIORITY, IMarker.PRIORITY_HIGH);
			}
			
			msg.append("Checker: ").append(defect.getCheckerCode()).append(" ").append(DexterUtil.LINE_SEPARATOR);
			
			marker.setAttribute(IMarker.MESSAGE, msg.toString());
			marker.setAttribute(IMarker.TRANSIENT, false); // persistence
			
			if(occurence.getStartLine() <= 0) {
				marker.setAttribute(IMarker.LINE_NUMBER, 1);
			} else {
				marker.setAttribute(IMarker.LINE_NUMBER, occurence.getStartLine());
			}
			
			if(occurence.getEndLine() <= 0) {
				marker.setAttribute(ResultFileConstant.END_LINE, 1);
			} else {
				marker.setAttribute(ResultFileConstant.END_LINE, occurence.getEndLine());
			}
			
			// Defect Info
			marker.setAttribute(ResultFileConstant.TOOL_NAME, defect.getToolName());
			marker.setAttribute(ResultFileConstant.LANGUAGE, defect.getLanguage());
			marker.setAttribute(ResultFileConstant.CHECKER_CODE, defect.getCheckerCode());
			marker.setAttribute(ResultFileConstant.METHOD_NAME, defect.getMethodName());
			marker.setAttribute(ResultFileConstant.CLASS_NAME, defect.getClassName());
			marker.setAttribute(ResultFileConstant.FILE_NAME, defect.getFileName());
			marker.setAttribute(ResultFileConstant.MODULE_PATH, defect.getModulePath());
			marker.setAttribute(ResultFileConstant.SEVERITY_CODE, defect.getSeverityCode());
			marker.setAttribute(ResultFileConstant.LOCAL_DID, defect.getLocalDid());
			
			marker.setAttribute(ResultFileConstant.CHAR_START, Integer.toString(occurence.getCharStart()));
			marker.setAttribute(ResultFileConstant.CHAR_END, Integer.toString(occurence.getCharEnd()));
			marker.setAttribute(ResultFileConstant.DEFECT_MESSAGE, defect.getMessage());
			marker.setAttribute(ResultFileConstant.OCCURENCE_CODE, occurence.getCode());
			
			int startLine = occurence.getStartLine();
			if (startLine <= 0) {
				startLine = 1;
			}
			
			if(occurence.getCharStart() >= 0 && occurence.getCharEnd() >= 0){
				marker.setAttribute(IMarker.CHAR_START, occurence.getCharStart());
				marker.setAttribute(IMarker.CHAR_END, occurence.getCharEnd());
			} else if(severity != IMarker.SEVERITY_INFO && isDismissed == false){
				setLineAttribute(file, marker, startLine);
			}
		} catch (CoreException e) {
			DexterEclipseActivator.LOG.error(e.getMessage(), e);
        } catch (Exception e) {
        	DexterEclipseActivator.LOG.error(e.getMessage(), e);
        } finally {
        	
        }
	}
	
	/**
	 * @param attribute 
	 */
    public static void toggleMarkerDismissed(final IFile file, final String markerType, final String localDid) {
    	try {
	        for(final IMarker marker : file.findMarkers(markerType, true, IResource.DEPTH_INFINITE)){
	        	if(marker.getAttribute(ResultFileConstant.LOCAL_DID, "").equals(localDid)){
	        		toggleMarkerDismissed(marker);
	        	}
	        }
        } catch (CoreException e) {
        	DexterEclipseActivator.LOG.error(e.getMessage(), e);
        }
    }
    
	private static void toggleMarkerDismissed(final IMarker oldMarker) {
		try {
			IMarker newMarker = null; 
			final StringBuilder msg = new StringBuilder(100);
			final IFile file = (IFile) oldMarker.getResource();
			
			if(DEFECT_DISMISSED_MARKER_TYPE.equals(oldMarker.getType())){
				newMarker = file.createMarker(DEFECT_MARKER_TYPE);
				newMarker.setAttribute(IMarker.MESSAGE, msg.toString().replace("Status: Dismissed", "Status: New"));
				newMarker.setAttribute(IMarker.PRIORITY, IMarker.PRIORITY_HIGH);
				newMarker.setAttribute(IMarker.SEVERITY, toEclipseMarkerSeverity(oldMarker.getAttribute(ResultFileConstant.SOURCE_CODE, "")));
				newMarker.setAttribute(IMarker.SEVERITY, oldMarker.getAttribute(IMarker.SEVERITY, 0));
			} else if(DEFECT_MARKER_TYPE.equals(oldMarker.getType())){
				newMarker = file.createMarker(DEFECT_DISMISSED_MARKER_TYPE);
				newMarker.setAttribute(IMarker.MESSAGE, msg.toString().replace("Status: New", "Status: Dismissed"));
				newMarker.setAttribute(IMarker.SEVERITY, toEclipseMarkerSeverity(oldMarker.getAttribute(ResultFileConstant.SOURCE_CODE, "")));
				newMarker.setAttribute(IMarker.PRIORITY, IMarker.PRIORITY_LOW);
			} else {
				DexterEclipseActivator.LOG.error("Cannot change marker type for " + oldMarker.getType());
				return;
			}
			
			newMarker.setAttribute(IMarker.TRANSIENT, false); // persistence
			newMarker.setAttribute(IMarker.LINE_NUMBER, oldMarker.getAttribute(IMarker.LINE_NUMBER, 1));
			newMarker.setAttribute(ResultFileConstant.END_LINE, oldMarker.getAttribute(ResultFileConstant.END_LINE, 1));
			
			// Defect Info
			newMarker.setAttribute(ResultFileConstant.TOOL_NAME, oldMarker.getAttribute(ResultFileConstant.TOOL_NAME, ""));
			newMarker.setAttribute(ResultFileConstant.LANGUAGE, oldMarker.getAttribute(ResultFileConstant.LANGUAGE, ""));
			newMarker.setAttribute(ResultFileConstant.CHECKER_CODE, oldMarker.getAttribute(ResultFileConstant.CHECKER_CODE, ""));
			newMarker.setAttribute(ResultFileConstant.METHOD_NAME, oldMarker.getAttribute(ResultFileConstant.METHOD_NAME, ""));
			newMarker.setAttribute(ResultFileConstant.CLASS_NAME, oldMarker.getAttribute(ResultFileConstant.CLASS_NAME, ""));
			newMarker.setAttribute(ResultFileConstant.FILE_NAME, oldMarker.getAttribute(ResultFileConstant.FILE_NAME, ""));
			newMarker.setAttribute(ResultFileConstant.MODULE_PATH, oldMarker.getAttribute(ResultFileConstant.MODULE_PATH, ""));
			newMarker.setAttribute(ResultFileConstant.CHAR_START, oldMarker.getAttribute(ResultFileConstant.CHAR_START, ""));
			newMarker.setAttribute(ResultFileConstant.CHAR_END, oldMarker.getAttribute(ResultFileConstant.CHAR_END, ""));
			newMarker.setAttribute(ResultFileConstant.DEFECT_MESSAGE, oldMarker.getAttribute(ResultFileConstant.DEFECT_MESSAGE, ""));
			newMarker.setAttribute(ResultFileConstant.OCCURENCE_CODE, oldMarker.getAttribute(ResultFileConstant.OCCURENCE_CODE, ""));
			newMarker.setAttribute(IMarker.CHAR_START, oldMarker.getAttribute(IMarker.CHAR_START, ""));
			newMarker.setAttribute(IMarker.CHAR_END, oldMarker.getAttribute(IMarker.CHAR_END, ""));
			
			oldMarker.delete();
		} catch (CoreException e) {
			DexterEclipseActivator.LOG.error(e.getMessage(), e);
        } catch (Exception e) {
        	DexterEclipseActivator.LOG.error(e.getMessage(), e);
        }
	}

	private static void setLineAttribute(final IFile file, final IMarker marker, final int startLine) {
	    int newLineSize = 0;
	    
	    //nal String workspacePath = ResourcesPlugin.getWorkspace().getRoot().getLocation().toOSString();
	    
	    final String fileFullPath = DexterUtil.refinePath(file.getLocation().toFile().getAbsolutePath());
	    //final File f = new File(workspacePath + file.getFullPath().toOSString());
	    final File f = new File(fileFullPath);
	    
	    String charset;
        try {
	        charset = file.getCharset().toUpperCase();
	        
	        if(f.exists() == false || !Charset.isSupported(charset)){
	        	DexterEclipseActivator.LOG.error("File can't read to mark defects : " + fileFullPath + ", encode:" + charset);
	        	return;
	        }
        } catch (CoreException e) {
        	DexterEclipseActivator.LOG.error(e.getMessage(), e);
        	
        	return;
        }
	    
	    // log : DexterEclipseActivator.error("for Marking: " + workspacePath + file.getFullPath().toOSString()	+ ", encode:" + charset);
	    
	    FileReader reader = null;
	    try {
	    	reader = new FileReader(f);
	    	final char[] chars = new char[2048];
	    	reader.read(chars);
	    	
	    	final String content = new String(chars);
	    	if(Strings.isNullOrEmpty(content) == false && content.indexOf("\r\n") >= 0){
	    		newLineSize = 2;
	    	} else {
	    		newLineSize = 1;
	    	}
	    	
	    } catch (IOException e){
	    	DexterEclipseActivator.LOG.error(e.getMessage(), e);
	    	newLineSize = 2;
	    } finally {
	    	try {
	    		if(reader != null)
	    			reader.close();
            } catch (IOException e) {
            	// Intentionally
            }
	    }
	    
	    
	    String line;
	    InputStream fis = null;
	    BufferedReader br = null;
	    
	    try {
	        fis = new FileInputStream(fileFullPath);
	        br = new BufferedReader(new InputStreamReader(fis, Charset.forName(charset)));
	        
	        int curLine = 1;
	        int offset = 0;
	        int sOffset = -1;
	        int eOffset = -1;
	        
	        while((line = br.readLine()) != null) {
	        	if(curLine == startLine){
	        		final String trimStr = line.trim();
	        		final int start = line.indexOf(trimStr);
	        		
	        		sOffset = offset + start;
	        		eOffset = offset + line.length();
	        		br.close();
	        		fis.close();
	        		break;
	        	}
	        	
	        	offset += line.length() + newLineSize;
	        	curLine ++;
	        }
	        
	        marker.setAttribute(IMarker.CHAR_START, sOffset);
	        marker.setAttribute(IMarker.CHAR_END, eOffset);
        } catch (FileNotFoundException e) {
        	DexterEclipseActivator.LOG.error(e.getMessage(), e);
        } catch (CoreException e) {
        	DexterEclipseActivator.LOG.error(e.getMessage(), e);
        } catch (IOException e) {
        	DexterEclipseActivator.LOG.error(e.getMessage(), e);
        } finally {
        	try {
        		if(br != null){
        			br.close();
        		}
        		if(fis != null){
        			fis.close();
        		}
        	} catch (IOException e) {
        		// Intentionally
        	}
        }
    }

	private static int toEclipseMarkerSeverity(final String severityCode) {
	    if("CRI".equals(severityCode)){
			return IMarker.SEVERITY_ERROR;
		} else if("MAJ".equals(severityCode)){
			return IMarker.SEVERITY_WARNING;
		} else if("MIN".equals(severityCode)){
			return IMarker.SEVERITY_WARNING;
		} else if("CRC".equals(severityCode)){
			return IMarker.SEVERITY_WARNING;
		} else {
			return IMarker.SEVERITY_INFO;
		}
    }
	
	public static void deleteMarkers(final IFile file) {
		try {
			file.deleteMarkers(DEFECT_MARKER_TYPE, false, IResource.DEPTH_ZERO);
			file.deleteMarkers(DEFECT_DISMISSED_MARKER_TYPE, false, IResource.DEPTH_ZERO);
		} catch (CoreException ce) {
			DexterEclipseActivator.LOG.error(ce.getMessage(), ce);
		}
	}
	
	/**
	 * return defect which created by Eclipse Marker, so it has incomplete information
	 * such as occurence list(only one), etc.
	 * 
	 * @param marker
	 * @return Defect
	 */
	public static Defect markerToIncompleteDefect(final IMarker marker){
		if(marker == null || marker.exists() == false){
			throw new DexterRuntimeException("Invalid Parameter : marker is null or not exisit");
		}
		
		Defect defect = null;
		
        try {
	        defect = new Defect();
	        defect.setToolName(marker.getAttribute(ResultFileConstant.TOOL_NAME) == null ? "" : (String) marker.getAttribute(ResultFileConstant.TOOL_NAME));
	        defect.setLanguage(marker.getAttribute(ResultFileConstant.LANGUAGE) == null ? "" : (String) marker.getAttribute(ResultFileConstant.LANGUAGE));
	        defect.setCheckerCode(marker.getAttribute(ResultFileConstant.CHECKER_CODE) == null ? "" : (String) marker.getAttribute(ResultFileConstant.CHECKER_CODE));
	        defect.setMethodName(marker.getAttribute(ResultFileConstant.METHOD_NAME) == null ? "" : (String) marker.getAttribute(ResultFileConstant.METHOD_NAME));
	        defect.setClassName(marker.getAttribute(ResultFileConstant.CLASS_NAME) == null ? "" : (String) marker.getAttribute(ResultFileConstant.CLASS_NAME));
	        defect.setFileName(marker.getAttribute(ResultFileConstant.FILE_NAME) == null ? "" : (String) marker.getAttribute(ResultFileConstant.FILE_NAME));
	        defect.setModulePath(marker.getAttribute(ResultFileConstant.MODULE_PATH) == null ? "" : (String) marker.getAttribute(ResultFileConstant.MODULE_PATH));
	        defect.setMessage(marker.getAttribute(ResultFileConstant.DEFECT_MESSAGE) == null ? "" : (String) marker.getAttribute(ResultFileConstant.DEFECT_MESSAGE));
	        
	        final Occurence o = new Occurence();
	        o.setCode(marker.getAttribute(ResultFileConstant.OCCURENCE_CODE) == null ? "" : (String) marker.getAttribute(ResultFileConstant.OCCURENCE_CODE));
	        o.setStartLine(marker.getAttribute(IMarker.LINE_NUMBER) == null ? 1 : (Integer) marker.getAttribute(IMarker.LINE_NUMBER));
	        o.setEndLine(marker.getAttribute(ResultFileConstant.END_LINE) == null ? 1 : (Integer) marker.getAttribute(ResultFileConstant.END_LINE));
	        
	        defect.addOccurence(o);
        } catch (NumberFormatException e) {
        	throw new DexterRuntimeException(e.getMessage(), e);
        } catch (CoreException e) {
        	throw new DexterRuntimeException(e.getMessage(), e);
        } catch (Exception e){
        	throw new DexterRuntimeException(e.getMessage(), e);
        }
		
		return defect;
	}
}
