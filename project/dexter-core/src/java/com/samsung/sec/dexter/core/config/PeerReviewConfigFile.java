package com.samsung.sec.dexter.core.config;

import java.util.Map;

import com.samsung.sec.dexter.core.exception.DexterRuntimeException;
import com.samsung.sec.dexter.core.util.DexterUtil;
import com.samsung.sec.dexter.core.util.FileUtil;

public class PeerReviewConfigFile extends DexterConfigFile {
	private FileUtil fileService;
	
	public PeerReviewConfigFile(FileUtil fileService) {
		this.fileService = fileService;
	}
	
	protected void checkDexterConfigMap(final Map<String, Object> map) {
		DexterUtil.checkNullOrEmptyOfMap(map);
    }
    
    @Override
    public void setFields(final Map<String, Object> params) {
        checkDexterConfigMap(params);

        setDexterHome((String) params.get("dexterHome"));
    }
    
    @Override
    public void setDexterHome(String dexterHome) {
        if (fileService.exists(dexterHome) == false)
            throw new DexterRuntimeException("there is no dexter home folder : " + dexterHome);

        this.dexterHome = dexterHome;
    }
}
