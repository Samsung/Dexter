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
package com.samsung.sec.dexter.core.filter;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import org.junit.Test;

import com.samsung.sec.dexter.core.config.DexterConfig;
import com.samsung.sec.dexter.core.defect.Defect;
import com.sun.org.apache.xml.internal.resolver.helpers.PublicId;

public class FalseAlarmConfigurationTreeTest {

	@Test
	public void isFalseAlarmTest_ReturnsTrue_GivenAlarmMatchingDefectFilter() {
		FalseAlarmConfigurationTree tester = new FalseAlarmConfigurationTree();
		Defect defect1 = new Defect();
		Defect defect2 = new Defect();
		Defect defect3 = new Defect();
		Defect defect4 = new Defect();
		Defect defect5 = new Defect();
		Defect defect6 = new Defect();
		DefectFilter defectFilter1 = new DefectFilter();
		DefectFilter defectFilter2 = new DefectFilter();
		DefectFilter defectFilter3 = new DefectFilter();
		DefectFilter defectFilter4 = new DefectFilter();
		DefectFilter defectFilter5 = new DefectFilter();
		DefectFilter defectFilter6 = new DefectFilter();

		defectFilter1.setToolName("Tool Name");
		defectFilter2.setLanguage("Language");
		defectFilter3.setModulePath("Module Path");
		defectFilter4.setFileName("File Name");
		defectFilter5.setClassName("Class Name");
		defectFilter6.setMethodName("Method Name");

		tester.addFalseAlarm(defectFilter1);
		tester.addFalseAlarm(defectFilter2);
		tester.addFalseAlarm(defectFilter3);
		tester.addFalseAlarm(defectFilter4);
		tester.addFalseAlarm(defectFilter5);
		tester.addFalseAlarm(defectFilter6);

		defect1.setToolName("Tool Name");
		assertTrue(tester.isFalseAlarm(defect1));

		defect2.setLanguage("Language");
		assertTrue(tester.isFalseAlarm(defect2));

		defect3.setModulePath("Module Path");
		assertTrue(tester.isFalseAlarm(defect3));

		defect4.setFileName("File Name");
		assertTrue(tester.isFalseAlarm(defect4));

		defect5.setClassName("Class Name");
		assertTrue(tester.isFalseAlarm(defect5));

		defect6.setMethodName("Method Name");
		assertTrue(tester.isFalseAlarm(defect6));
	}

	@Test
	public void isFalseAlarmTest_ReturnsFalse_GivenNOFilters() {
		FalseAlarmConfigurationTree tester = new FalseAlarmConfigurationTree();
		Defect defect1 = new Defect();
		Defect defect2 = new Defect();
		Defect defect3 = new Defect();

		// no filter for this defect
		defect1.setToolName("Another Tool Name that does not match any filter");
		assertFalse(tester.isFalseAlarm(defect1));

		// no filter for this defect
		defect2.setClassName("");
		assertFalse(tester.isFalseAlarm(defect2));

		// no filter for this completely unset defect
		assertFalse(tester.isFalseAlarm(defect3));
	}

}
