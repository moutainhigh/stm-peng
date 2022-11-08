package com.mainsteam.stm.plugin.common;

import static org.junit.Assert.*;

import org.junit.BeforeClass;
import org.junit.Test;

import com.mainsteam.stm.pluginprocessor.ConverterResult;
import com.mainsteam.stm.pluginprocessor.ParameterValue;
import com.mainsteam.stm.pluginprocessor.ProcessParameter;
import com.mainsteam.stm.pluginprocessor.ResultSet;
import com.mainsteam.stm.pluginprocessor.ResultSetMetaInfo;
import com.mainsteam.stm.pluginsession.PluginResultSet;
import com.mainsteam.stm.pluginsession.exception.PluginSessionRunException;

public class SubInstResultSetConverterTest {

	@BeforeClass
	public static void setUpBeforeClass() throws Exception {
	}

	@Test
	public void testConvert() throws PluginSessionRunException {
		PluginResultSet pluginResultSet = new PluginResultSet();
		pluginResultSet.addRow(new String[] { "1", "00:01:01:01:01:01" });
		pluginResultSet.addRow(new String[] { "2", "00:01:01:01:01:02" });
		pluginResultSet.addRow(new String[] { "3", "00:00:00:00:00:00" });
		// ResultSetMetaInfo metaInfo = new ResultSetMetaInfo();
		ResultSetMetaInfo metaInfo = new ResultSetMetaInfo();
		metaInfo.addColumnName("编号");
		metaInfo.addColumnName("OID");
		ResultSet resultSet = new ResultSet(pluginResultSet, metaInfo);

		SubInstResultSetConverter subConverter = new SubInstResultSetConverter();
		ConverterResult[] subColumns = subConverter.convert(null, null);
		assertNull(subColumns);

		subColumns = subConverter.convert(resultSet, null);
		assertTrue(subColumns.length > 0);
		for (int i = 0; i < subColumns.length; i++) {
			assertTrue(subColumns[i].getResultData()[0].equals(resultSet.getValue(i, 0)));
		}

		ProcessParameter parameter = new ProcessParameter();
		subColumns = subConverter.convert(resultSet, parameter);
		assertTrue(subColumns.length > 0);
		for (int i = 0; i < subColumns.length; i++) {
			assertTrue(subColumns[i].getResultData()[0].equals(resultSet.getValue(i,
					resultSet.getColumnLength() - 1)));
		}

		{
			ParameterValue paraValue = new ParameterValue();
			paraValue.setKey("ValueColumnTitle");
			paraValue.setValue("OID");
			parameter.addParameter(paraValue);
			subColumns = subConverter.convert(resultSet, parameter);
			assertTrue(subColumns.length > 0);

			String[] columnValues = resultSet.getColumn("OID");
			for (int i = 0; i < subColumns.length; i++) {
				assertTrue(subColumns[i].getResultData()[0]
						.equals(columnValues[i]));
			}
		}

		{
			ParameterValue paraValue = new ParameterValue();
			paraValue.setKey("InstPropertyKey");
			paraValue.setValue("3");
			parameter.addParameter(paraValue);
			subColumns = subConverter.convert(resultSet, parameter);
			assertTrue(subColumns.length > 0);
			String[] columnValues = resultSet.getColumn("OID");
			for (int i = 0; i < subColumns.length; i++) {
				assertTrue(subColumns[i].getResultData()[0]
						.equals(columnValues[i]));
			}
		}

		{
			ParameterValue paraValue = new ParameterValue();
			paraValue.setKey("IndexColumnTitle");
			paraValue.setValue("编号");
			parameter.addParameter(paraValue);
			subColumns = subConverter.convert(resultSet, parameter);
			assertTrue(subColumns.length == 1);
			String[] columnValues = resultSet.getColumn("OID");
			assertTrue(subColumns[0].getResultData()[0].equals(columnValues[2]));
		}
	}
}
