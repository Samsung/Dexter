package com.samsung.sec.dexter.vdcpp.plugin;

import com.google.gson.Gson;
import com.samsung.sec.dexter.core.analyzer.AnalysisConfig;
import com.samsung.sec.dexter.core.analyzer.AnalysisEntityFactory;
import com.samsung.sec.dexter.core.analyzer.AnalysisResult;
import com.samsung.sec.dexter.core.analyzer.IAnalysisEntityFactory;
import com.samsung.sec.dexter.core.checker.CheckerConfig;
import com.samsung.sec.dexter.core.checker.IChecker;
import com.samsung.sec.dexter.core.config.DexterConfig;
import com.samsung.sec.dexter.core.config.DexterConfig.LANGUAGE;
import com.samsung.sec.dexter.core.defect.Defect;
import com.samsung.sec.dexter.core.exception.DexterRuntimeException;
import com.samsung.sec.dexter.core.plugin.IDexterPlugin;
import com.samsung.sec.dexter.core.plugin.PluginDescription;
import com.samsung.sec.dexter.core.plugin.PluginVersion;
import com.samsung.sec.dexter.core.util.DexterUtil;
import com.samsung.sec.dexter.vdcpp.checkerlogic.ICheckerLogic;
import com.samsung.sec.dexter.vdcpp.util.ITranslationUnitFactory;
import com.samsung.sec.dexter.vdcpp.util.TranslationUnitFactory;

import java.io.IOException;
import java.io.InputStreamReader;
import java.io.Reader;
import java.util.HashMap;
import java.util.Map;
import java.util.Properties;

import org.apache.log4j.Logger;
import org.eclipse.cdt.core.dom.ast.IASTTranslationUnit;

import net.xeoh.plugins.base.annotations.PluginImplementation;

@PluginImplementation
public class DexterVdCppPlugin implements IDexterPlugin {
    public final static String PLUGIN_NAME = "dexter-vd-cpp";
    private final static PluginVersion PLUGIN_VERSION = new PluginVersion("0.10.5");
    private static PluginDescription PLUGIN_DESCRIPTION;

    private final static Logger LOG = Logger.getLogger(DexterVdCppPlugin.class);

    private CheckerConfig checkerConfig;
    private Map<String, ICheckerLogic> checkerLogicMap;

    private ITranslationUnitFactory unitFactory = new TranslationUnitFactory();
    private IAnalysisEntityFactory analysisEntityFactory = new AnalysisEntityFactory();

    public DexterVdCppPlugin() {}

    @Override
    public void init() {
        initCheckerConfig();
        final Properties checkerLogicProperties = getChekcerLogicProperties();
        initCheckerLogicMap(checkerLogicProperties);
    }

    protected synchronized void initCheckerConfig() {
        try {
            Reader reader = new InputStreamReader(DexterUtil.getResourceAsStreamInClassPath(this.getClass(),
                    "checker-config.json"));

            Gson gson = new Gson();
            this.checkerConfig = gson.fromJson(reader, CheckerConfig.class);
            this.checkerConfig.checkerListToMap();
        } catch (Exception e) {
            throw new DexterRuntimeException(e.getMessage(), e);
        }
    }

    private Properties getChekcerLogicProperties() {
        Properties checkerLogicProperties = new Properties();
        try {
            checkerLogicProperties.load(DexterUtil.getResourceAsStreamInClassPath(this.getClass(),
                    "CheckerLogic.properties"));

            return checkerLogicProperties;
        } catch (IOException e) {
            throw new DexterRuntimeException(e.getMessage(), e);
        }
    }

    // removed synchronized statement due to thread locking for too much time (dexter bug). 
    protected void initCheckerLogicMap(final Properties checkerLogicProperties) {
        assert this.checkerConfig != null;
        checkerLogicMap = new HashMap<String, ICheckerLogic>();

        for (IChecker checker : this.checkerConfig.getCheckerList()) {
            putCheckerLogic(checker.getCode(), checkerLogicProperties);
        }
    }

    private void putCheckerLogic(final String checkerCode, final Properties checkerLogicProperties) {
        try {
            final Class<ICheckerLogic> clazz = getCheckerLogicClass(checkerLogicProperties, checkerCode);
            checkerLogicMap.put(checkerCode, clazz.newInstance());
        } catch (DexterRuntimeException e) {
            LOG.error("CheckerLogic does not exist. checker code:" + checkerCode);
            LOG.error(e.getMessage(), e);
        } catch (InstantiationException e) {
            LOG.error(e.getMessage(), e);
        } catch (IllegalAccessException e) {
            LOG.error(e.getMessage(), e);
        }
    }

    @Override
    public void destroy() {
        // do nothing;
    }

    @Override
    public void handleDexterHomeChanged(String oldPath, String newPath) {
        // do nothing;
    }

    @Override
    public synchronized PluginDescription getDexterPluginDescription() {
        if (PLUGIN_DESCRIPTION == null) {
            PLUGIN_DESCRIPTION = new PluginDescription("Samsung Electroincs", PLUGIN_NAME,
                    // TODO: It dosen't work in Eclipse Plugin - PluginVersion.fromImplementationVersion(DexterVdCppPlugin.class),
                    PLUGIN_VERSION,
                    DexterConfig.LANGUAGE.CPP,
                    "");
        }

        return PLUGIN_DESCRIPTION;
    }

    @Override
    public void setCheckerConfig(CheckerConfig cc) {
        this.checkerConfig = cc;
    }

    @Override
    public CheckerConfig getCheckerConfig() {
        return this.checkerConfig;
    }

    @Override
    public AnalysisResult analyze(final AnalysisConfig config) {
        AnalysisResult result = analysisEntityFactory.createAnalysisResult(config);
        IASTTranslationUnit unit = unitFactory.getASTTranslationUnit(config);

        for (IChecker checker : this.checkerConfig.getCheckerList()) {
            if (checker.isActive() == false)
                continue;

            final ICheckerLogic logic = getCheckerLogic(checker.getCode());
            logic.analyze(config, result, checker, unit);

            for (Defect defect : result.getDefectList()) {
                defect.setAnalysisType(config.getAnalysisType().name());
            }
        }

        return result;
    }

    @Override
    public boolean supportLanguage(LANGUAGE language) {
        if (language == LANGUAGE.C || language == LANGUAGE.CPP) {
            return true;
        } else {
            return false;
        }
    }

    @Override
    public String[] getSupportingFileExtensions() {
        return new String[] { "c", "cpp", "h", "hpp" };
    }

    @SuppressWarnings("unchecked")
    private Class<ICheckerLogic> getCheckerLogicClass(final Properties checkerLogicProperties,
            final String checkerCode) {
        String className = checkerLogicProperties.getProperty(checkerCode);

        try {
            return (Class<ICheckerLogic>) this.getClass().getClassLoader().loadClass(className);
        } catch (ClassNotFoundException e) {
            throw new DexterRuntimeException(e.getMessage(), e);
        }
    }

    protected ICheckerLogic getCheckerLogic(final String checkerCode) {
        return checkerLogicMap.get(checkerCode);
    }
}
