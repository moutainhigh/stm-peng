package com.mainsteam.stm.plugin.common;

import com.mainsteam.stm.pluginprocessor.PluginResultSetProcessor;
import com.mainsteam.stm.pluginprocessor.ProcessParameter;
import com.mainsteam.stm.pluginprocessor.ResultSet;
import com.mainsteam.stm.pluginsession.PluginSessionContext;
import com.mainsteam.stm.pluginsession.exception.PluginSessionRunException;

/**
 * Created by Lich on 2016/12/20.
 */
public class ResultSetClearProcessor implements PluginResultSetProcessor {

    public final static String CLEAN_META_INFO = "CleanMetaInfo";

    @Override
    public void process(ResultSet resultSet, ProcessParameter parameter, PluginSessionContext context) throws PluginSessionRunException {
        resultSet.clear();
        boolean clean = true;
        if (parameter.getParameterValueByKey(CLEAN_META_INFO) != null) {
            if (!Boolean.valueOf(parameter.getParameterValueByKey(CLEAN_META_INFO).getValue())) {
                clean = false;
            }
        }
        if (clean)
            resultSet.getResultMetaInfo().removeAllColumnNames();
    }
}
