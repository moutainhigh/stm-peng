package com.mainsteam.stm.plugin.common;

import com.mainsteam.stm.caplib.state.Availability;
import com.mainsteam.stm.caplib.state.Collectibility;
import com.mainsteam.stm.pluginprocessor.ParameterValue;
import com.mainsteam.stm.pluginprocessor.PluginResultSetProcessor;
import com.mainsteam.stm.pluginprocessor.ProcessParameter;
import com.mainsteam.stm.pluginprocessor.ResultSet;
import com.mainsteam.stm.pluginsession.PluginResultSet;
import com.mainsteam.stm.pluginsession.PluginSessionContext;

public class AvailableConverter implements PluginResultSetProcessor {

    private static final String COLLECT_FAIL_VALUE = "CollectFailValue";
    private static final String UNKNOWN = "Unknown";

    @Override
    public void process(ResultSet resultSet, ProcessParameter parameter, PluginSessionContext context) {
        if (resultSet.isEmpty()) {
            resultSet.clear();
            ParameterValue collectFailValue = parameter.getParameterValueByKey(COLLECT_FAIL_VALUE);
            if (collectFailValue != null && UNKNOWN.equals(collectFailValue.getValue())) {
                if (resultSet.getExtraValue(PluginResultSet.ExtraValueConstants.COLLECT_FAIL_EXCEPTION) != null) {
                    resultSet.putValue(0, 0, Availability.UNKNOWN_CODE | Collectibility.UNCOLLECTIBLE_CODE);
                } else {
                    resultSet.putValue(0, 0, Availability.UNAVAILABLE_CODE | Collectibility.COLLECTIBLE_CODE);
                }
            } else {
                if (resultSet.getExtraValue(PluginResultSet.ExtraValueConstants.COLLECT_FAIL_EXCEPTION) != null) {
                    resultSet.putValue(0, 0, Availability.UNAVAILABLE_CODE | Collectibility.UNCOLLECTIBLE_CODE);
                } else {
                    resultSet.putValue(0, 0, Availability.UNAVAILABLE_CODE | Collectibility.COLLECTIBLE_CODE);
                }
            }
        }
    }

}
