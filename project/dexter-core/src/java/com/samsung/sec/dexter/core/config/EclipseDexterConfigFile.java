package com.samsung.sec.dexter.core.config;

import com.samsung.sec.dexter.core.analyzer.ResultFileConstant;
import com.samsung.sec.dexter.core.util.IDexterClient;

import java.util.Map;

public class EclipseDexterConfigFile extends DexterConfigFile {
    private IDexterClient client;

    public EclipseDexterConfigFile(final IDexterClient client) {
        this.client = client;
    }

    @Override
    public void setFields(final Map<String, Object> params) {
        checkDexterConfigMap(params);

        /*
         * setDexterHome((String) params.get("dexterHome"));
         * setDexterServerIp((String) params.get("dexterServerIp"));
         * setDexterServerPort(DexterUtil.getIntFromMap(params, "dexterServerPort"));
         */

        setDexterHome(DexterConfig.getInstance().getDexterHome());
        setDexterServerIp(client.getServerHost());
        setDexterServerPort(client.getServerPort());

        setProjectName((String) params.get(ResultFileConstant.PROJECT_NAME));
        setProjectFullPath((String) params.get("projectFullPath") + "/");
        setSourceDirList(getStringListFromMap(params, "sourceDir"));
        setHeaderDirList(getStringListFromMap(params, "headerDir"));
        setSourceEncoding((String) params.get("sourceEncoding"));
        setLibDirList(getStringListFromMap(params, "libDir"));
        setBinDir((String) params.get("binDir"));
        setType((String) params.get("type"));
        setModulePath((String) params.get(ResultFileConstant.MODULE_PATH));
        setFileNameList(getStringListFromMap(params, ResultFileConstant.FILE_NAME));
        setResultFileFullPath((String) params.get("resultFileFullPath"));
        setFunctionList(getStringListFromMap(params, "functionList"));
        setSnapshotId((String) params.get(ResultFileConstant.SNAPSHOT_ID));
        setDexterServerIp((String) params.get("dexterServerIp"));
    }
}
