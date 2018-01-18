/**
 * Copyright (c) 2017 Samsung Electronics, Inc.,
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
package com.samsung.sec.dexter.core;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import org.junit.Test;

import com.sun.org.apache.bcel.internal.generic.NEW;

public class BaseDefectTest {
	Object TestedObject;
	BaseDefect TestedBaseDefect;

	/**
	 * Each test is for checking consecutive "if's" in overridden "equals" method in
	 * BaseDefect class
	 */
	@Test
	public void equalsTest() {
		BaseDefect tester = new BaseDefect();
		TestedObject = new BaseDefect();

		assertTrue(tester.equals(TestedObject));
	}

	@Test
	public void equalsTest_ReturnsFalse_GivenNullObject() {
		BaseDefect tester = new BaseDefect();
		TestedObject = null;

		assertFalse(tester.equals(TestedObject));
	}

	@Test
	public void equalsTest_ReturnsFalse_GivenObjectNotTestedDefect() {
		BaseDefect tester = new BaseDefect();
		// TestedObject could be any object that is NOT a BaseDefect
		TestedObject = new Object();

		assertFalse(tester.equals(TestedObject));
	}

	@Test
	public void equalsTest_ReturnsTrue_GivenBaseDefectWithTheSameCheckerCode() {
		BaseDefect tester = new BaseDefect();
		TestedBaseDefect = new BaseDefect();

		tester.setCheckerCode("Code1");
		TestedBaseDefect.setCheckerCode("Code1");

		assertTrue(tester.equals(TestedBaseDefect));
	}

	@Test
	public void equalsTest_ReturnsFalse_GivenBaseDefectWithDifferentCheckerCode() {
		BaseDefect tester = new BaseDefect();
		TestedBaseDefect = new BaseDefect();

		tester.setCheckerCode("Code1");
		TestedBaseDefect.setCheckerCode("Code2");

		assertFalse(tester.equals(TestedBaseDefect));
	}

	@Test
	public void equalsTest_ReturnsTrue_GivenBaseDefectWithTheSameMethodName() {
		BaseDefect tester = new BaseDefect();
		TestedBaseDefect = new BaseDefect();

		tester.setMethodName("Method1");
		TestedBaseDefect.setMethodName("Method1");

		assertTrue(tester.equals(TestedBaseDefect));
	}

	@Test
	public void equalsTest_ReturnsFalse_GivenBaseDefectWithDifferentMethodName() {
		BaseDefect tester = new BaseDefect();
		TestedBaseDefect = new BaseDefect();

		tester.setMethodName("Method1");
		TestedBaseDefect.setMethodName("Method2");

		assertFalse(tester.equals(TestedBaseDefect));
	}

	@Test
	public void equalsTest_ReturnsTrue_GivenBaseDefectWithTheSameClassName() {
		BaseDefect tester = new BaseDefect();
		TestedBaseDefect = new BaseDefect();

		tester.setClassName("Class1");
		TestedBaseDefect.setClassName("Class1");

		assertTrue(tester.equals(TestedBaseDefect));
	}

	@Test
	public void equalsTest_ReturnsFalse_GivenBaseDefectWithDifferentClassName() {
		BaseDefect tester = new BaseDefect();
		TestedBaseDefect = new BaseDefect();

		tester.setClassName("Class1");
		TestedBaseDefect.setClassName("Class2");

		assertFalse(tester.equals(TestedBaseDefect));
	}

	@Test
	public void equalsTest_ReturnsTrue_GivenBaseDefectWithTheSameFileName() {
		BaseDefect tester = new BaseDefect();
		TestedBaseDefect = new BaseDefect();

		tester.setFileName("FileName1");
		TestedBaseDefect.setFileName("FileName1");

		assertTrue(tester.equals(TestedBaseDefect));
	}

	@Test
	public void equalsTest_ReturnsFalse_GivenBaseDefectWithDifferentFileName() {
		BaseDefect tester = new BaseDefect();
		TestedBaseDefect = new BaseDefect();

		tester.setFileName("FileName1");
		TestedBaseDefect.setFileName("FileName2");

		assertFalse(tester.equals(TestedBaseDefect));
	}

	@Test
	public void equalsTest_ReturnsTrue_GivenBaseDefectWithTheSameModulePath() {
		BaseDefect tester = new BaseDefect();
		TestedBaseDefect = new BaseDefect();

		tester.setModulePath("ModulePath1");
		TestedBaseDefect.setModulePath("ModulePath1");

		assertTrue(tester.equals(TestedBaseDefect));
	}

	@Test
	public void equalsTest_ReturnsFalse_GivenBaseDefectWithDifferentModulePath() {
		BaseDefect tester = new BaseDefect();
		TestedBaseDefect = new BaseDefect();

		tester.setModulePath("ModulePath1");
		TestedBaseDefect.setModulePath("ModulePath2");

		assertFalse(tester.equals(TestedBaseDefect));
	}

	@Test
	public void equalsTest_ReturnsTrue_GivenBaseDefectWithTheSameToolName() {
		BaseDefect tester = new BaseDefect();
		TestedBaseDefect = new BaseDefect();

		tester.setToolName("Tool1");
		TestedBaseDefect.setToolName("Tool1");

		assertTrue(tester.equals(TestedBaseDefect));
	}

	@Test
	public void equalsTest_ReturnsFalse_GivenBaseDefectWithDifferentToolName() {
		BaseDefect tester = new BaseDefect();
		TestedBaseDefect = new BaseDefect();

		tester.setToolName("Tool1");
		TestedBaseDefect.setToolName("Tool2");

		assertFalse(tester.equals(TestedBaseDefect));
	}

	@Test
	public void equalsTest_ReturnsTrue_GivenBaseDefectWithTheSameLanguage() {
		BaseDefect tester = new BaseDefect();
		TestedBaseDefect = new BaseDefect();

		tester.setLanguage("Language1");
		TestedBaseDefect.setLanguage("Language1");

		assertTrue(tester.equals(TestedBaseDefect));
	}

	@Test
	public void equalsTest_ReturnsFalse_GivenBaseDefectWithDifferentLanguage() {
		BaseDefect tester = new BaseDefect();
		TestedBaseDefect = new BaseDefect();

		tester.setLanguage("Language1");
		TestedBaseDefect.setLanguage("Language2");

		assertFalse(tester.equals(TestedBaseDefect));
	}

	@Test
	public void setClassNameTest_SetsClassNameToEmpty_GivenNullArgument() {
		BaseDefect tester = new BaseDefect();

		tester.setClassName(null);

		assertEquals(tester.getClassName(), "");
	}

	@Test
	public void setMethodNameTest_SetsMethodNameToEmpty_GivenNullArgument() {
		BaseDefect tester = new BaseDefect();

		tester.setMethodName(null);

		assertEquals(tester.getMethodName(), "");
	}

	@Test
	public void setCheckerCodeTest_SetsCheckerCodeToEmpty_GivenNullArgument() {
		BaseDefect tester = new BaseDefect();

		tester.setCheckerCode(null);

		assertEquals(tester.getCheckerCode(), "");
	}
}
