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
