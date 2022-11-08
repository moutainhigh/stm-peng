package com.mainsteam.stm.plugin.common;

import com.mainsteam.stm.pluginprocessor.*;
import com.mainsteam.stm.pluginsession.PluginSessionContext;
import com.mainsteam.stm.pluginsession.exception.PluginSessionRunException;

import java.util.HashSet;
import java.util.List;

public class ColumnSelectProcessor implements PluginResultSetProcessor {

    private static final String SELECT = "SELECT";

    @Override
    public void process(ResultSet resultSet, ProcessParameter parameter, PluginSessionContext context) throws PluginSessionRunException {
        List<ParameterValue> parameterValueList = parameter.getParameterValuesByKey(SELECT);
        HashSet<String> selectedColumnSet = new HashSet<>();
        for (ParameterValue parameterValue : parameterValueList) {
            selectedColumnSet.add(parameterValue.getValue());
        }
        ResultSetMetaInfo metaInfo = resultSet.getResultMetaInfo();
        for (int index = 0; index < metaInfo.getColumnLength(); ++index) {
            if (!selectedColumnSet.contains(metaInfo.getColumnName(index))) {
                resultSet.removeColumn(index);
                --index;
            }
        }
    }

}
