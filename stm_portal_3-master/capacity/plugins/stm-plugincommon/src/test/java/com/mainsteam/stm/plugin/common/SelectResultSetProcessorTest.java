package com.mainsteam.stm.plugin.common;

import static org.junit.Assert.assertTrue;

import org.junit.BeforeClass;
import org.junit.Test;

import com.mainsteam.stm.pluginprocessor.ParameterValue;
import com.mainsteam.stm.pluginprocessor.ProcessParameter;
import com.mainsteam.stm.pluginprocessor.ResultSet;
import com.mainsteam.stm.pluginprocessor.ResultSetMetaInfo;
import com.mainsteam.stm.pluginsession.PluginResultSet;

public class SelectResultSetProcessorTest {

	@BeforeClass
	public static void setUpBeforeClass() throws Exception {
	}

	@Test
	public void testProcess() {
		
		ResultSetMetaInfo metaInfo = new ResultSetMetaInfo();
		metaInfo.addColumnName("编号");
		metaInfo.addColumnName("OID");
		metaInfo.addColumnName("多余的");
		PluginResultSet pluginResultSet = new PluginResultSet();
		pluginResultSet.addRow(new String[] { "1", "00:01:01:01:01:01","多余的" });
		pluginResultSet.addRow(new String[] { "2", "00:01:01:01:01:02","多余的" });
		ResultSet resultSet = new ResultSet(pluginResultSet, metaInfo);
		
		ProcessParameter parameter = new ProcessParameter();
		ParameterValue paraValue = new ParameterValue();
		paraValue.setKey("SELECT");
		paraValue.setValue("SELECT  编号,OID");
		parameter.addParameter(paraValue);
		
		SelectResultSetProcessor processor = new SelectResultSetProcessor();
		processor.process(resultSet, parameter,null);
		
		String[] columnNames = metaInfo.getColumnNames();
		for (String columnName : columnNames) {
			System.out.println(columnName);
		}
		for (int row = 0; row < resultSet.getRowLength(); row++) {
			for (int column = 0; column < resultSet.getColumnLength(); column++) {
				System.out.println(resultSet.getValue(row, column));
			}
		}
		
		assertTrue(metaInfo.getColumnLength() == 2);
		assertTrue(resultSet.getColumnLength() == 2);
	}

}
