package com.mainsteam.stm.plugin.common;

import com.mainsteam.stm.caplib.state.Availability;
import com.mainsteam.stm.caplib.state.Collectibility;
import com.mainsteam.stm.pluginprocessor.ParameterValue;
import com.mainsteam.stm.pluginprocessor.ProcessParameter;
import com.mainsteam.stm.pluginprocessor.ResultSet;
import com.mainsteam.stm.pluginprocessor.ResultSetMetaInfo;
import com.mainsteam.stm.pluginsession.PluginResultSet;
import org.junit.Test;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

/**
 * @author lich
 */
public class AvailableConverterTest {

    private final AvailableConverter converter = new AvailableConverter();

    @Test
    public void processNonEmptyResultSet() throws Exception {
        PluginResultSet pluginResultSet = new PluginResultSet();
        ResultSetMetaInfo metaInfo = new ResultSetMetaInfo();
        ResultSet resultSet = new ResultSet(pluginResultSet, metaInfo);
        resultSet.putValue(0, 0, "notEmpty");
        converter.process(resultSet, new ProcessParameter(), null);
        assertEquals(resultSet.getRowLength(), 1);
        assertEquals(resultSet.getColumnLength(), 1);
        assertTrue(resultSet.getValue(0, 0).equals("notEmpty"));
    }

    @Test
    public void processEmptyResultSet() throws Exception {
        PluginResultSet pluginResultSet = new PluginResultSet();
        ResultSetMetaInfo metaInfo = new ResultSetMetaInfo();
        ResultSet resultSet = new ResultSet(pluginResultSet, metaInfo);
        converter.process(resultSet, new ProcessParameter(), null);
        assertEquals(resultSet.getRowLength(), 1);
        assertEquals(resultSet.getColumnLength(), 1);
        int value = Integer.valueOf(resultSet.getValue(0, 0));
        assertTrue(Availability.valueOf(value) == Availability.UNAVAILABLE && Collectibility.valueOf(value) == Collectibility.COLLECTIBLE);
    }


    @Test
    public void processMultiRowEmptyResultSet() throws Exception {
        PluginResultSet pluginResultSet = new PluginResultSet();
        ResultSetMetaInfo metaInfo = new ResultSetMetaInfo();
        ResultSet resultSet = new ResultSet(pluginResultSet, metaInfo);
        resultSet.addRow(new String[]{});
        resultSet.addRow(new String[]{});
        converter.process(resultSet, new ProcessParameter(), null);
        assertEquals(resultSet.getRowLength(), 1);
        assertEquals(resultSet.getColumnLength(), 1);
        int value = Integer.valueOf(resultSet.getValue(0, 0));
        assertTrue(Availability.valueOf(value) == Availability.UNAVAILABLE && Collectibility.valueOf(value) == Collectibility.COLLECTIBLE);
    }

    @Test
    public void processEmptyResultSetWithFail() throws Exception {
        PluginResultSet pluginResultSet = new PluginResultSet();
        ResultSetMetaInfo metaInfo = new ResultSetMetaInfo();
        ResultSet resultSet = new ResultSet(pluginResultSet, metaInfo);
        resultSet.setExtraValue(PluginResultSet.ExtraValueConstants.COLLECT_FAIL_EXCEPTION, new RuntimeException());
        converter.process(resultSet, new ProcessParameter(), null);
        assertEquals(resultSet.getRowLength(), 1);
        assertEquals(resultSet.getColumnLength(), 1);
        int value = Integer.valueOf(resultSet.getValue(0, 0));
        assertTrue(Availability.valueOf(value) == Availability.UNAVAILABLE && Collectibility.valueOf(value) == Collectibility.UNCOLLECTIBLE);
    }

    @Test
    public void processEmptyResultSetWhenFailValueUnknown() throws Exception {
        PluginResultSet pluginResultSet = new PluginResultSet();
        ResultSetMetaInfo metaInfo = new ResultSetMetaInfo();
        ResultSet resultSet = new ResultSet(pluginResultSet, metaInfo);

        ProcessParameter processParameter = new ProcessParameter();
        ParameterValue parameterValue = new ParameterValue();
        parameterValue.setKey("CollectFailValue");
        parameterValue.setValue("Unknown");
        processParameter.addParameter(parameterValue);

        converter.process(resultSet, processParameter, null);
        assertEquals(resultSet.getRowLength(), 1);
        assertEquals(resultSet.getColumnLength(), 1);
        int value = Integer.valueOf(resultSet.getValue(0, 0));
        assertTrue(Availability.valueOf(value) == Availability.UNAVAILABLE && Collectibility.valueOf(value) == Collectibility.COLLECTIBLE);
    }

    @Test
    public void processEmptyResultSetWithFailWhenFailValueUnknown() throws Exception {
        PluginResultSet pluginResultSet = new PluginResultSet();
        ResultSetMetaInfo metaInfo = new ResultSetMetaInfo();
        ResultSet resultSet = new ResultSet(pluginResultSet, metaInfo);
        resultSet.setExtraValue(PluginResultSet.ExtraValueConstants.COLLECT_FAIL_EXCEPTION, new RuntimeException());

        ProcessParameter processParameter = new ProcessParameter();
        ParameterValue parameterValue = new ParameterValue();
        parameterValue.setKey("CollectFailValue");
        parameterValue.setValue("Unknown");
        processParameter.addParameter(parameterValue);

        converter.process(resultSet, processParameter, null);

        assertEquals(resultSet.getRowLength(), 1);
        assertEquals(resultSet.getColumnLength(), 1);
        int value = Integer.valueOf(resultSet.getValue(0, 0));
        assertTrue(Availability.valueOf(value) == Availability.UNKNOWN && Collectibility.valueOf(value) == Collectibility.UNCOLLECTIBLE);
    }
}
