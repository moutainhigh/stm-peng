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
public class SubInstAvailableProcessorTest {

    private final SubInstAvailableProcessor processor = new SubInstAvailableProcessor();

    @Test
    public void processEmptyResultSet() throws Exception {
        PluginResultSet pluginResultSet = new PluginResultSet();
        ResultSetMetaInfo metaInfo = new ResultSetMetaInfo();
        metaInfo.addColumnName("id");
        metaInfo.addColumnName("name");
        metaInfo.addColumnName("status");
        ResultSet resultSet = new ResultSet(pluginResultSet, metaInfo);

        ProcessParameter processParameter = new ProcessParameter();
        ParameterValue parameterValue = new ParameterValue();
        parameterValue.setKey("IndexColumn");
        parameterValue.setValue("id");
        processParameter.addParameter(parameterValue);
        parameterValue = new ParameterValue();
        parameterValue.setKey("ValueColumn");
        parameterValue.setValue("status");
        processParameter.addParameter(parameterValue);
        parameterValue = new ParameterValue();
        parameterValue.setKey("IndexKey");
        parameterValue.setValue("2");
        processParameter.addParameter(parameterValue);

        processor.process(resultSet, processParameter, null);

        assertEquals(1, resultSet.getRowLength());
        assertEquals(3, resultSet.getColumnLength());
        assertTrue(resultSet.getValue(0, 0).equals("2"));
        int value = Integer.valueOf(resultSet.getValue(0, 2));
        assertTrue(Availability.valueOf(value) == Availability.UNAVAILABLE && Collectibility.valueOf(value) == Collectibility.COLLECTIBLE);
    }

    @Test
    public void processEmptyResultSetWithFail() throws Exception {
        PluginResultSet pluginResultSet = new PluginResultSet();
        ResultSetMetaInfo metaInfo = new ResultSetMetaInfo();
        metaInfo.addColumnName("id");
        metaInfo.addColumnName("name");
        metaInfo.addColumnName("status");
        ResultSet resultSet = new ResultSet(pluginResultSet, metaInfo);
        resultSet.setExtraValue(PluginResultSet.ExtraValueConstants.COLLECT_FAIL_EXCEPTION, new RuntimeException());

        ProcessParameter processParameter = new ProcessParameter();
        ParameterValue parameterValue = new ParameterValue();
        parameterValue.setKey("IndexColumn");
        parameterValue.setValue("id");
        processParameter.addParameter(parameterValue);
        parameterValue = new ParameterValue();
        parameterValue.setKey("ValueColumn");
        parameterValue.setValue("status");
        processParameter.addParameter(parameterValue);
        parameterValue = new ParameterValue();
        parameterValue.setKey("IndexKey");
        parameterValue.setValue("2");
        processParameter.addParameter(parameterValue);

        processor.process(resultSet, processParameter, null);

        assertEquals(1, resultSet.getRowLength());
        assertEquals(3, resultSet.getColumnLength());
        assertTrue(resultSet.getValue(0, 0).equals("2"));
        int value = Integer.valueOf(resultSet.getValue(0, 2));
        assertTrue(Availability.valueOf(value) == Availability.UNAVAILABLE && Collectibility.valueOf(value) == Collectibility.UNCOLLECTIBLE);
    }

    @Test
    public void processEmptyResultSetWhenFailValueUnknown() throws Exception {
        PluginResultSet pluginResultSet = new PluginResultSet();
        ResultSetMetaInfo metaInfo = new ResultSetMetaInfo();
        metaInfo.addColumnName("id");
        metaInfo.addColumnName("name");
        metaInfo.addColumnName("status");
        ResultSet resultSet = new ResultSet(pluginResultSet, metaInfo);

        ProcessParameter processParameter = new ProcessParameter();
        ParameterValue parameterValue = new ParameterValue();
        parameterValue.setKey("IndexColumn");
        parameterValue.setValue("id");
        processParameter.addParameter(parameterValue);
        parameterValue = new ParameterValue();
        parameterValue.setKey("ValueColumn");
        parameterValue.setValue("status");
        processParameter.addParameter(parameterValue);
        parameterValue = new ParameterValue();
        parameterValue.setKey("IndexKey");
        parameterValue.setValue("2");
        processParameter.addParameter(parameterValue);
        parameterValue = new ParameterValue();
        parameterValue.setKey("CollectFailValue");
        parameterValue.setValue("Unknown");
        processParameter.addParameter(parameterValue);

        processor.process(resultSet, processParameter, null);

        assertEquals(1, resultSet.getRowLength());
        assertEquals(3, resultSet.getColumnLength());
        assertTrue(resultSet.getValue(0, 0).equals("2"));
        int value = Integer.valueOf(resultSet.getValue(0, 2));
        assertTrue(Availability.valueOf(value) == Availability.UNAVAILABLE && Collectibility.valueOf(value) == Collectibility.COLLECTIBLE);
    }

    @Test
    public void processEmptyResultSetWithFailWhenFailValueUnknown() throws Exception {
        PluginResultSet pluginResultSet = new PluginResultSet();
        ResultSetMetaInfo metaInfo = new ResultSetMetaInfo();
        metaInfo.addColumnName("id");
        metaInfo.addColumnName("name");
        metaInfo.addColumnName("status");
        ResultSet resultSet = new ResultSet(pluginResultSet, metaInfo);
        resultSet.setExtraValue(PluginResultSet.ExtraValueConstants.COLLECT_FAIL_EXCEPTION, new RuntimeException());

        ProcessParameter processParameter = new ProcessParameter();
        ParameterValue parameterValue = new ParameterValue();
        parameterValue.setKey("IndexColumn");
        parameterValue.setValue("id");
        processParameter.addParameter(parameterValue);
        parameterValue = new ParameterValue();
        parameterValue.setKey("ValueColumn");
        parameterValue.setValue("status");
        processParameter.addParameter(parameterValue);
        parameterValue = new ParameterValue();
        parameterValue.setKey("IndexKey");
        parameterValue.setValue("2");
        processParameter.addParameter(parameterValue);
        parameterValue = new ParameterValue();
        parameterValue.setKey("CollectFailValue");
        parameterValue.setValue("Unknown");
        processParameter.addParameter(parameterValue);

        processor.process(resultSet, processParameter, null);

        assertEquals(1, resultSet.getRowLength());
        assertEquals(3, resultSet.getColumnLength());
        assertTrue(resultSet.getValue(0, 0).equals("2"));
        int value = Integer.valueOf(resultSet.getValue(0, 2));
        assertTrue(Availability.valueOf(value) == Availability.UNKNOWN && Collectibility.valueOf(value) == Collectibility.UNCOLLECTIBLE);
    }

    @Test
    public void processResultSetWhenFailValueUnknown() throws Exception {
        PluginResultSet pluginResultSet = new PluginResultSet();
        ResultSetMetaInfo metaInfo = new ResultSetMetaInfo();
        metaInfo.addColumnName("id");
        metaInfo.addColumnName("name");
        metaInfo.addColumnName("status");
        ResultSet resultSet = new ResultSet(pluginResultSet, metaInfo);
        resultSet.addRow(new String[]{"1", "a", "on"});
        resultSet.addRow(new String[]{"3", "c", "on"});

        ProcessParameter processParameter = new ProcessParameter();
        ParameterValue parameterValue = new ParameterValue();
        parameterValue.setKey("IndexColumn");
        parameterValue.setValue("id");
        processParameter.addParameter(parameterValue);
        parameterValue = new ParameterValue();
        parameterValue.setKey("ValueColumn");
        parameterValue.setValue("status");
        processParameter.addParameter(parameterValue);
        parameterValue = new ParameterValue();
        parameterValue.setKey("IndexKey");
        parameterValue.setValue("2");
        processParameter.addParameter(parameterValue);
        parameterValue = new ParameterValue();
        parameterValue.setKey("CollectFailValue");
        parameterValue.setValue("Unknown");
        processParameter.addParameter(parameterValue);

        processor.process(resultSet, processParameter, null);

        assertEquals(3, resultSet.getRowLength());
        assertEquals(3, resultSet.getColumnLength());
        assertTrue(resultSet.getValue(2, 0).equals("2"));
        int value = Integer.valueOf(resultSet.getValue(2, 2));
        assertTrue(Availability.valueOf(value) == Availability.UNAVAILABLE && Collectibility.valueOf(value) == Collectibility.COLLECTIBLE);
    }

    @Test
    public void processResultSetWithFailWhenFailValueUnknown() throws Exception {
        PluginResultSet pluginResultSet = new PluginResultSet();
        ResultSetMetaInfo metaInfo = new ResultSetMetaInfo();
        metaInfo.addColumnName("id");
        metaInfo.addColumnName("name");
        metaInfo.addColumnName("status");
        ResultSet resultSet = new ResultSet(pluginResultSet, metaInfo);
        resultSet.addRow(new String[]{"1", "a", "on"});
        resultSet.addRow(new String[]{"2", "b", null});
        resultSet.addRow(new String[]{"3", "c", "on"});
        resultSet.setExtraValue(PluginResultSet.ExtraValueConstants.COLLECT_FAIL_EXCEPTION, new RuntimeException());

        ProcessParameter processParameter = new ProcessParameter();
        ParameterValue parameterValue = new ParameterValue();
        parameterValue.setKey("IndexColumn");
        parameterValue.setValue("id");
        processParameter.addParameter(parameterValue);
        parameterValue = new ParameterValue();
        parameterValue.setKey("ValueColumn");
        parameterValue.setValue("status");
        processParameter.addParameter(parameterValue);
        parameterValue = new ParameterValue();
        parameterValue.setKey("IndexKey");
        parameterValue.setValue("2");
        processParameter.addParameter(parameterValue);
        parameterValue = new ParameterValue();
        parameterValue.setKey("CollectFailValue");
        parameterValue.setValue("Unknown");
        processParameter.addParameter(parameterValue);

        processor.process(resultSet, processParameter, null);

        assertEquals(3, resultSet.getRowLength());
        assertEquals(3, resultSet.getColumnLength());
        assertTrue(resultSet.getValue(1, 0).equals("2"));
        int value = Integer.valueOf(resultSet.getValue(1, 2));
        assertTrue(Availability.valueOf(value) == Availability.UNKNOWN && Collectibility.valueOf(value) == Collectibility.UNCOLLECTIBLE);
    }

}
