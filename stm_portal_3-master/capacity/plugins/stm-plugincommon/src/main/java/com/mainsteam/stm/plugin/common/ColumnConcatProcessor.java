package com.mainsteam.stm.plugin.common;

import com.mainsteam.stm.pluginprocessor.*;
import com.mainsteam.stm.pluginsession.PluginSessionContext;
import com.mainsteam.stm.pluginsession.exception.PluginSessionRunException;
import com.mainsteam.stm.license.LicenseCheckException;//zwameter.Parameter;

import java.util.ArrayList;

/**
 * Created by Lich on 2016/12/20.
 */
public class ColumnConcatProcessor implements PluginResultSetProcessor {

    public static final String SOURCE = "Source";
    public static final String DEST = "Dest";
    public static final String CONNECTOR = "Connector";


    @Override
    public void process(ResultSet resultSet, ProcessParameter parameter, PluginSessionContext context) throws PluginSessionRunException {
        String connector = "";
        if (parameter.getParameterValueByKey(CONNECTOR) != null)
            connector = parameter.getParameterValueByKey(CONNECTOR).getValue();
        ArrayList<Integer> sourceList = new ArrayList<>();
        ResultSetMetaInfo metaInfo = resultSet.getResultMetaInfo();
        metaInfo.addColumnName(parameter.getParameterValueByKey(DEST).getValue());
        for (ParameterValue parameterValue : parameter.getParameterValuesByKey(SOURCE)) {
            sourceList.add(metaInfo.indexOfColumnName(parameterValue.getValue()));
        }
        int column = metaInfo.getColumnLength() - 1;
        for (int row = 0; row < resultSet.getRowLength(); ++row) {
            StringBuilder sb = new StringBuilder();
            for (Integer source : sourceList) {
                if (resultSet.getValue(row, source) != null)
                    sb.append(resultSet.getValue(row, source)).append(connector);
            }
            if (sb.length() > 0) {
                resultSet.putValue(row, column, sb.substring(0, sb.length() - connector.length()));
            }
        }
    }
}
