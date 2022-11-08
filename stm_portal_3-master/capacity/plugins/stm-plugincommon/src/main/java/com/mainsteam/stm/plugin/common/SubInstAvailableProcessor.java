package com.mainsteam.stm.plugin.common;

import com.mainsteam.stm.caplib.state.Availability;
import com.mainsteam.stm.caplib.state.Collectibility;
import com.mainsteam.stm.pluginprocessor.*;
import com.mainsteam.stm.pluginsession.PluginResultSet;
import com.mainsteam.stm.pluginsession.PluginSessionContext;
import com.mainsteam.stm.pluginsession.exception.PluginSessionRunException;

/**
 * @author lich
 */
public class SubInstAvailableProcessor implements PluginResultSetProcessor {

    private static final String COLLECT_FAIL_VALUE = "CollectFailValue";
    private static final String UNKNOWN = "Unknown";
    private static final String INDEX_COLUMN = "IndexColumn";
    private static final String VALUE_COLUMN = "ValueColumn";
    private static final String INDEX_KEY = "IndexKey";

    @Override
    public void process(ResultSet resultSet, ProcessParameter parameter, PluginSessionContext context) throws PluginSessionRunException {
        String indexKey = parameter.getParameterValueByKey(INDEX_KEY).getValue();
        if (indexKey != null) {
            String indexColumnName = parameter.getParameterValueByKey(INDEX_COLUMN).getValue();
            String valueColumnName = parameter.getParameterValueByKey(VALUE_COLUMN).getValue();
            ResultSetMetaInfo metaInfo = resultSet.getResultMetaInfo();
            int indexColumn = metaInfo.indexOfColumnName(indexColumnName);
            int valueColumn = metaInfo.indexOfColumnName(valueColumnName);
            String[] indexColumnValues = resultSet.getColumn(indexColumn);
            String[] valueColumnValues = resultSet.getColumn(valueColumn);
            int row;
            for (row = 0; row < indexColumnValues.length; ++row) {
                if (indexKey.equals(indexColumnValues[row])) {
                    break;
                }
            }
            String valueKey = null;
            if (row < valueColumnValues.length) {
                valueKey = valueColumnValues[row];
            }
            if (valueKey == null) {
                resultSet.putValue(row, indexColumn, indexKey);
                ParameterValue collectFailValue = parameter.getParameterValueByKey(COLLECT_FAIL_VALUE);
                if (collectFailValue != null && UNKNOWN.equals(collectFailValue.getValue())) {
                    if (resultSet.getExtraValue(PluginResultSet.ExtraValueConstants.COLLECT_FAIL_EXCEPTION) != null) {
                        resultSet.putValue(row, valueColumn, Availability.UNKNOWN_CODE | Collectibility.UNCOLLECTIBLE_CODE);
                    } else {
                        resultSet.putValue(row, valueColumn, Availability.UNAVAILABLE_CODE | Collectibility.COLLECTIBLE_CODE);
                    }
                } else {
                    if (resultSet.getExtraValue(PluginResultSet.ExtraValueConstants.COLLECT_FAIL_EXCEPTION) != null) {
                        resultSet.putValue(row, valueColumn, Availability.UNAVAILABLE_CODE | Collectibility.UNCOLLECTIBLE_CODE);
                    } else {
                        resultSet.putValue(row, valueColumn, Availability.UNAVAILABLE_CODE | Collectibility.COLLECTIBLE_CODE);
                    }
                }
            }
        }
    }

}
