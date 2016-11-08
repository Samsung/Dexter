/**
 * Copyright (c) 2014 Samsung Electronics, Inc.,
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
package com.samsung.sec.dexter.core.util;

import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

import com.google.common.base.Strings;
import com.samsung.sec.dexter.core.exception.DexterRuntimeException;

import java.io.File;

import org.junit.After;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;

/**
 * @precondition 1. installing nodejs 2. installing mysql 3. download and unzip
 * dexter-server : should be in same machine
 * 
 * System Environmental Variables:
 * -DDEXTER_TEST_SERVER_PATH=C:\DEV\dexter-server
 * -DDEXTER_TEST_SERVER_PORT=4982 -DDEXTER_TEST_ADMIN_ID=admin
 * -DDEXTER_TEST_ADMIN_PWD=pasword -DDEXTER_TEST_DB_HOST=localhost
 * -DDEXTER_TEST_DB_PORT=3306 -DDEXTER_TEST_DB_NAME=dexter_dev
 * -DDEXTER_TEST_DB_USER_ID=db_id -DDEXTER_TEST_DB_USER_PWD=db_pwd
 */
public class DexterClientIT {

    // Preconditions
    static String dexterServerPath = "";
    static int dexterServerPort = -1;

    static String adminId;
    static String adminPwd;
    static String dbHost;
    static int dbPort = -1;
    static String dbName;
    static String dbUserId;
    static String dbUserPwd;

    static Process process;
    static Thread serverThread;

    static boolean isTestable = true;

    static IDexterClient dc = new EmptyDexterClient();
    

    /**
     * @throws java.lang.Exception
     * void
     */
    @BeforeClass
    public static void setUpBeforeClass() throws Exception {
        loadSystemPropertiesForTest();
        if (isTestable == false)
            return;

        startDexterServerAndCreateDbSchema();
        // waitUntilServerReadyThenLogin();
    }

    private static void loadSystemPropertiesForTest() throws Exception {
        dexterServerPath = getSystemProperty("DEXTER_TEST_SERVER_PATH",
                "DEXTER_TEST_SERVER_PATH=/home/test/dexter-server");
        dexterServerPort = getSystemPropertyInt("DEXTER_TEST_SERVER_PORT", "DEXTER_TEST_SERVER_PORT=4982");
        adminId = getSystemProperty("DEXTER_TEST_ADMIN_ID", "DEXTER_TEST_ADMIN_ID=admin");
        adminPwd = getSystemProperty("DEXTER_TEST_ADMIN_PWD", "DEXTER_TEST_ADMIN_PWD=1234");
        dbHost = getSystemProperty("DEXTER_TEST_DB_HOST", "DEXTER_TEST_DB_HOST=localhost");
        dbPort = getSystemPropertyInt("DEXTER_TEST_DB_PORT", "DEXTER_TEST_DB_PORT=3306");
        dbName = getSystemProperty("DEXTER_TEST_DB_NAME", "DEXTER_TEST_DB_NAME=dexterdb");
        dbUserId = getSystemProperty("DEXTER_TEST_DB_USER_ID", "DEXTER_TEST_DB_USER_ID=user");
        dbUserPwd = getSystemProperty("DEXTER_TEST_DB_USER_PWD", "DEXTER_TEST_DB_USER_PWD=password");

        if (Strings.isNullOrEmpty(dexterServerPath)) {
            isTestable = false;
        }
    }

    private static String getSystemProperty(String key, String example) throws Exception {
        String value = System.getProperty(key);
        if (Strings.isNullOrEmpty(value)) {
            System.out.println("You should set Environmental Variable. eg) set " + example);
            return "";
        }

        return value;
    }

    private static Integer getSystemPropertyInt(String key, String example) throws Exception {
        try {
            int value = Integer.parseInt(System.getProperty(key));
            return value;
        } catch (Exception e) {
            System.out.println("You should set Environmental Variable. eg) set " + example);
            return -1;
        }
    }

    private static void startDexterServerAndCreateDbSchema() throws Exception {
        final StringBuilder cmd = new StringBuilder();

        if (DexterUtil.getOS() == DexterUtil.OS.WINDOWS) {
            cmd.append("cmd /C C:\\DEV\\tool\\nodejs\\node.exe ");
        } else if (DexterUtil.getOS() == DexterUtil.OS.LINUX) {
            cmd.append("node ");
        } else {
            throw new Exception("Unknown OS to run Dexter Server for Unit Test");
        }

        cmd.append(dexterServerPath).append(DexterUtil.FILE_SEPARATOR).append("server.js ");
        cmd.append(" -p=").append(dexterServerPort);
        cmd.append(" -database.host=").append(dbHost);
        cmd.append(" -database.port=").append(dbPort);
        cmd.append(" -database.name=").append(dbName);
        cmd.append(" -database.user=").append(dbUserId);
        cmd.append(" -database.password=").append(dbUserPwd);

        serverThread = new Thread() {
            public void run() {
                System.out.println(cmd.toString());
                try {
                    System.out.println("Dexter Server is starting...");
                    process = Runtime.getRuntime().exec(cmd.toString(), new String[0], new File(dexterServerPath));
                    process.waitFor();
                    System.out.println("Dexter Server is terminated.");
                } catch (Exception e) {
                    e.printStackTrace();
                }
            };
        };
        serverThread.start();
    }

    // private static void waitUntilServerReadyThenLogin() throws Exception {
    // int count = 0;
    //
    // // waiting less 10 seconds
    // while(count < 10){
    // if(dc.isServerAlive()){
    // Thread.sleep(5000);
    // dc.login(adminId, adminPwd);
    // return;
    // }
    //
    // Thread.sleep(1000);
    // count++;
    // }
    //
    // throw new Exception("Can't start Dexter Server : " + dc.getServerHost() +
    // ":" + dc.getServerPort());
    // }

    /**
     * @throws java.lang.Exception
     * void
     */
    @AfterClass
    public static void tearDownAfterClass() throws Exception {
        if (isTestable == false)
            return;

        // 1. discard Dexter Server info.
        // 2. discard Dexter Database info.

        // DexterClient.deleteDexterDatabase("localhost", dexterServerPort,
        // adminId, adminPwd);
        // DexterClient.stopDexterServer("localhost", dexterServerPort, adminId,
        // adminPwd);
        // DexterClient.stopDexterServer("localhost", dexterServerPort, "admin",
        // "dex#0001");

        if (process != null) {
            process.destroy();
        }
    }

    /**
     * @throws java.lang.Exception
     * void
     */
    @Before
    public void setUp() throws Exception {}

    /**
     * @throws java.lang.Exception
     * void
     */
    @After
    public void tearDown() throws Exception {}

    @Test
    public void login_test() {
        if (isTestable == false)
            return;

        final String id = "admin";
        final String pwd = "dex#0001";

        try {
            dc.login(id, pwd);
        } catch (DexterRuntimeException e) {
            e.printStackTrace();
            fail();
        }

        assertTrue(id.equals(dc.getCurrentUserId()));
        assertTrue(pwd.equals(dc.getCurrentUserPwd()));
    }

    /*
     * @Test public void DefectGroup_CRUD_test(){ String groupName = "ROOT";
     * String groupType = "PRJ"; String description = "group desc..."; long
     * createdDateTime = System.currentTimeMillis();
     * 
     * DefectGroup group = new DefectGroup(); group.setGroupName(groupName);
     * group.setGroupType(groupType); group.setDescription(description);
     * group.setCreatorNo(dc.getCurrentUserNo());
     * group.setCreatedDateTime(createdDateTime);
     * 
     * 
     * // 1. create try{ dc.insertDefectGroup(group); } catch (DexterException
     * e){ fail(); }
     * 
     * 
     * // 2. retrieve List<DefectGroup> defectGroupList = null; try {
     * defectGroupList = dc.getDefectGroupByGroupName(groupName); } catch
     * (DexterException e2) { fail(); }
     * 
     * assertNotNull(defectGroupList);
     * 
     * assertEquals(1, defectGroupList.size());
     * 
     * DefectGroup retGroup = defectGroupList.get(0);
     * 
     * assertEquals(groupName, retGroup.getGroupName()); assertEquals(groupType,
     * retGroup.getGroupType()); assertEquals(description,
     * retGroup.getDescription()); assertEquals(-1, retGroup.getParentId());
     * assertEquals(createdDateTime/1000, retGroup.getCreatedDateTime());
     * assertEquals(dc.getCurrentUserNo(), retGroup.getCreatorNo());
     * 
     * 
     * // 3. update long parentId = retGroup.getId(); String groupName2 =
     * "ROOT2"; String groupType2 = "COM"; String description2 =
     * "group desc...2";
     * 
     * group.setId(parentId); group.setGroupName(groupName2);
     * group.setGroupType(groupType2); group.setDescription(description2);
     * 
     * dc.updateDefectGroup(group);
     * 
     * try { defectGroupList = dc.getDefectGroupByGroupName(groupName2); } catch
     * (DexterException e1) { fail(); }
     * 
     * assertNotNull(defectGroupList);
     * 
     * assertTrue(defectGroupList.size() == 1);
     * 
     * retGroup = defectGroupList.get(0);
     * 
     * assertEquals(parentId, retGroup.getId()); assertEquals(groupName2,
     * retGroup.getGroupName()); assertEquals(groupType2,
     * retGroup.getGroupType()); assertEquals(description2,
     * retGroup.getDescription());
     * 
     * // 4. parent - child groupName2 = "CHILD"; groupType2 = "COM";
     * description2 = "group desc...3"; DefectGroup child = new DefectGroup();
     * child.setGroupName(groupName2); child.setGroupType(groupType2);
     * child.setDescription(description2); child.setParentId(parentId);
     * 
     * try { dc.insertDefectGroup(child); } catch (DexterException e) { fail();
     * }
     * 
     * try { defectGroupList = dc.getDefectGroupByGroupName(groupName2); } catch
     * (DexterException e) { fail(); }
     * 
     * assertNotNull(defectGroupList);
     * 
     * assertEquals(1, defectGroupList.size());
     * 
     * DefectGroup retChild = defectGroupList.get(0);
     * 
     * assertEquals(groupName2, retChild.getGroupName());
     * assertEquals(groupType2, retChild.getGroupType());
     * assertEquals(description2, retChild.getDescription());
     * assertEquals(parentId, retChild.getParentId());
     * assertEquals(dc.getCurrentUserNo(), retGroup.getCreatorNo());
     * 
     * 
     * // 5. all list List<DefectGroup> allList = dc.getDefectGroupList();
     * assertEquals(2, allList.size());
     * 
     * // 6. delete dc.deleteDefectGroup(retChild.getId());
     * dc.deleteDefectGroup(retGroup.getId());
     * 
     * 
     * try { defectGroupList = dc.getDefectGroupByGroupName(groupName); } catch
     * (DexterException e) { fail(); } assertTrue(defectGroupList.size() == 0);
     * }
     * 
     * @Test public void DexterCodes_조회_테스트(){ List<DexterCode> results =
     * dc.getCodes("group-type");
     * 
     * assertNotNull(results);
     * 
     * assertEquals(5, results.size());
     * 
     * boolean hasKey = false; for(DexterCode code : results){
     * if("PRJ".equals(code.getCodeValue())){ assertEquals("group-type",
     * code.getCodeKey()); assertEquals("Project", code.getCodeName()); hasKey =
     * true; } }
     * 
     * assertTrue(hasKey); }
     */
}
