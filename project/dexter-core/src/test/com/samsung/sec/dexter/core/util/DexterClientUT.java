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
package com.samsung.sec.dexter.core.util;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

import com.google.gson.Gson;
import com.samsung.sec.dexter.core.exception.DexterRuntimeException;

import java.util.HashMap;
import java.util.Map;

import org.junit.After;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;

public class DexterClientUT {
	IDexterClient dc = new DexterClient("", "", "");

	@BeforeClass
	public static void setUpBeforeClass() throws Exception {
	}

	@AfterClass
	public static void tearDownAfterClass() throws Exception {
	}

	@Before
	public void setUp() throws Exception {
		dc.setServerHost("localhost");
		dc.setServerPort(4982);
	}

	@After
	public void tearDown() throws Exception {
		initDexterClient();
	}

	private void initDexterClient() {
		dc.setCurrentUserAdmin(false);
		dc.setCurrentUserId("");
		dc.setCurrentUserNo(-1);
		dc.setCurrentUserPwd("");
		dc.setLogin(false);
	}

	//////////////////////////////////////////////////////////////////////////////////
	// TEST SECTION
	//////////////////////////////////////////////////////////////////////////////////
	@Test
	public void login_success() {
		// precondition

		// stub - mock
		dc.setWebResource(new DexterWebResourceAdapter() {
			@Override
			public String getText(String uri, String id, String pwd) {
				Map<String, Object> result = new HashMap<String, Object>();
				result.put("result", "ok");
				result.put("userNo", 3);
				result.put("userId", "admin");
				result.put("userPwd", "dex#0001");
				result.put("isAdmin", true);

				return new Gson().toJson(result);
			}
		});

		// test-data
		final String id = "admin";
		final String pwd = "dex#0001";

		// test
		try {
			dc.login(id, pwd);

			assertTrue(id.equals(dc.getCurrentUserId()));
			assertTrue(pwd.equals(dc.getCurrentUserPwd()));
			assertTrue(dc.isCurrentUserAdmin());
			assertTrue(dc.isLogin());
			assertEquals(3, dc.getCurrentUserNo());
		} catch (DexterRuntimeException e) {
			e.printStackTrace();
			fail();
		}
	}

	@Test
	public void login_failure() {
		// precondition

		// stub - mock
		dc.setWebResource(new DexterWebResourceAdapter() {
			@Override
			public String getText(String uri, String id, String pwd) {
				StringBuilder msg = new StringBuilder();
				// msg.append("{ \"result\": \"ok\", \"userNo\": 3, \"userId\":
				// \"min.ho.kim\", \"userPwd\": \"1234\", \"isAdmin\": false
				// }");
				msg.append("{  \"result\": \"faile\" }");

				return msg.toString();
			}
		});

		// test-data
		final String id = "admin";
		final String pwd = "dex#0001";

		// test
		try {
			dc.login(id, pwd);
			fail();
		} catch (DexterRuntimeException e) {
		}

		assertFalse(id.equals(dc.getCurrentUserId()));
		assertFalse(pwd.equals(dc.getCurrentUserPwd()));
		assertFalse(dc.isCurrentUserAdmin());
		assertFalse(dc.isLogin());
		assertTrue(3 != dc.getCurrentUserNo());
	}

	// @Test
	// public void DefectGroup_CRUD_테스트(){
	// String groupName = "ROOT";
	// String groupType = "PRJ";
	// String description = "group desc...";
	// long createdDateTime = System.currentTimeMillis();
	//
	// DefectGroup group = new DefectGroup();
	// group.setGroupName(groupName);
	// group.setGroupType(groupType);
	// group.setDescription(description);
	// group.setCreatorNo(dc.getCurrentUserNo());
	// group.setCreatedDateTime(createdDateTime);
	//
	//
	// // 1. create
	// assertTrue(dc.createDefectGroup(group));
	//
	//
	// // 2. retrieve
	// List<DefectGroup> defectGroupList =
	// dc.getDefectGroupByGroupName(groupName);
	//
	// assertNotNull(defectGroupList);
	//
	// assertEquals(1, defectGroupList.size());
	//
	// DefectGroup retGroup = defectGroupList.get(0);
	//
	// assertEquals(groupName, retGroup.getGroupName());
	// assertEquals(groupType, retGroup.getGroupType());
	// assertEquals(description, retGroup.getDescription());
	// assertEquals(-1, retGroup.getParentId());
	// assertEquals(createdDateTime/1000, retGroup.getCreatedDateTime());
	// assertEquals(dc.getCurrentUserNo(), retGroup.getCreatorNo());
	//
	//
	// // 3. update
	// long parentId = retGroup.getId();
	// String groupName2 = "ROOT2";
	// String groupType2 = "COM";
	// String description2 = "group desc...2";
	//
	// group.setId(parentId);
	// group.setGroupName(groupName2);
	// group.setGroupType(groupType2);
	// group.setDescription(description2);
	//
	// dc.setDefectGroup(group);
	//
	// defectGroupList = dc.getDefectGroupByGroupName(groupName2);
	//
	// assertNotNull(defectGroupList);
	//
	// assertTrue(defectGroupList.size() == 1);
	//
	// retGroup = defectGroupList.get(0);
	//
	// assertEquals(parentId, retGroup.getId());
	// assertEquals(groupName2, retGroup.getGroupName());
	// assertEquals(groupType2, retGroup.getGroupType());
	// assertEquals(description2, retGroup.getDescription());
	//
	// // 4. parent - child
	// groupName2 = "CHILD";
	// groupType2 = "COM";
	// description2 = "group desc...3";
	// DefectGroup child = new DefectGroup();
	// child.setGroupName(groupName2);
	// child.setGroupType(groupType2);
	// child.setDescription(description2);
	// child.setParentId(parentId);
	//
	// assertTrue(dc.createDefectGroup(child));
	//
	// defectGroupList = dc.getDefectGroupByGroupName(groupName2);
	//
	// assertNotNull(defectGroupList);
	//
	// assertEquals(1, defectGroupList.size());
	//
	// DefectGroup retChild = defectGroupList.get(0);
	//
	// assertEquals(groupName2, retChild.getGroupName());
	// assertEquals(groupType2, retChild.getGroupType());
	// assertEquals(description2, retChild.getDescription());
	// assertEquals(parentId, retChild.getParentId());
	// assertEquals(dc.getCurrentUserNo(), retGroup.getCreatorNo());
	//
	//
	// // 5. all list
	// List<DefectGroup> allList = dc.getDefectGroupList();
	// assertEquals(2, allList.size());
	//
	// // 6. delete
	// dc.deleteDefectGroup(retChild.getId());
	// dc.deleteDefectGroup(retGroup.getId());
	//
	//
	// defectGroupList = dc.getDefectGroupByGroupName(groupName);
	// assertTrue(defectGroupList.size() == 0);
	// }

	// @Test
	// public void DexterCodes_조회_테스트(){
	// List<DexterCode> results = dc.getCodes("group-type");
	//
	// assertNotNull(results);
	//
	// assertEquals(5, results.size());
	//
	// boolean hasKey = false;
	// for(DexterCode code : results){
	// if("PRJ".equals(code.getCodeValue())){
	// assertEquals("group-type", code.getCodeKey());
	// assertEquals("Project", code.getCodeName());
	// hasKey = true;
	// }
	// }
	//
	// assertTrue(hasKey);
	// }
}
