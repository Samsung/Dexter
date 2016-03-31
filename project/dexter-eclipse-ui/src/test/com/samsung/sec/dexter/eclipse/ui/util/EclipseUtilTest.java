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
package com.samsung.sec.dexter.eclipse.ui.util;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import org.eclipse.core.resources.IResource;
import org.junit.Test;

public class EclipseUtilTest {
	@Test
	public void isValidJavaResource_should_return_true() {
		IResource resource = mock(IResource.class);
		
		when(resource.getType()).thenReturn(IResource.FILE);
		
		when(resource.getName()).thenReturn("test.java");
		assertTrue(EclipseUtil.isValidJavaResource(resource));
		
		when(resource.getName()).thenReturn("1.java");
		assertTrue(EclipseUtil.isValidJavaResource(resource));
		
		when(resource.getName()).thenReturn("1234567890123456789012345678901234567890123456789012345678901234567890.java");
		assertTrue(EclipseUtil.isValidJavaResource(resource));
	}
	
	@Test
	public void isValidJavaResource_should_return_false_when_invalid_type() {
		IResource resource = mock(IResource.class);
		
		when(resource.getName()).thenReturn("test.java");

		when(resource.getType()).thenReturn(IResource.FOLDER);
		assertFalse(EclipseUtil.isValidJavaResource(resource));
		
		when(resource.getType()).thenReturn(IResource.PROJECT);
		assertFalse(EclipseUtil.isValidJavaResource(resource));
	}
	
	@Test
	public void isValidJavaResource_should_return_false_when_invalid_name() {
		IResource resource = mock(IResource.class);
		
		when(resource.getType()).thenReturn(IResource.FILE);
		
		when(resource.getName()).thenReturn("test.java1");
		assertFalse(EclipseUtil.isValidJavaResource(resource));
		
		when(resource.getName()).thenReturn("test.1java");
		assertFalse(EclipseUtil.isValidJavaResource(resource));
		
		when(resource.getName()).thenReturn("java");
		assertFalse(EclipseUtil.isValidJavaResource(resource));
		
		when(resource.getName()).thenReturn("test1.java.test2");
		assertFalse(EclipseUtil.isValidJavaResource(resource));
		
		when(resource.getName()).thenReturn(".java.test2");
		assertFalse(EclipseUtil.isValidJavaResource(resource));
		
		when(resource.getName()).thenReturn(".java");
		assertFalse(EclipseUtil.isValidJavaResource(resource));
		
		when(resource.getName()).thenReturn("");
		assertFalse(EclipseUtil.isValidJavaResource(resource));
	}
}
