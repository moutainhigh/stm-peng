package com.mainsteam.stm.plugin.common;

import com.mainsteam.stm.pluginprocessor.PluginResultSetProcessor;
import com.mainsteam.stm.pluginprocessor.ProcessParameter;
import com.mainsteam.stm.pluginprocessor.ResultSet;
import com.mainsteam.stm.pluginsession.PluginSessionContext;
import com.mainsteam.stm.pluginsession.exception.PluginSessionRunException;

/**
 * Created by Lich on 2016/12/20.
 */
public class ColumnConvergeProcessor implements PluginResultSetProcessor {

    public static final String CONNECTOR = "Connector";

    @Override
    public void process(ResultSet resultSet, ProcessParameter parameter, PluginSessionContext context) throws PluginSessionRunException {
        String connector = ",";
        if (parameter.getParameterValueByKey(CONNECTOR) != null)
            connector = parameter.getParameterValueByKey(CONNECTOR).getValue();
        String[] result = new String[resultSet.getColumnLength()];
        for (int i = 0; i < result.length; ++i) {
            String[] values = resultSet.getColumn(i);
            StringBuilder sb = new StringBuilder();
            for (String value : values) {
                if (value != null) {
                    sb.append(value).append(connector);
                }
            }
            result[i] = sb.substring(0, sb.length() - connector.length());
        }
        resultSet.clear();
        resultSet.addRow(result);
    }
}
