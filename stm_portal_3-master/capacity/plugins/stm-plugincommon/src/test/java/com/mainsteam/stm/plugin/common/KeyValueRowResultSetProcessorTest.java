package com.mainsteam.stm.plugin.common;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

import org.junit.After;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;

import com.mainsteam.stm.pluginprocessor.ParameterValue;
import com.mainsteam.stm.pluginprocessor.ProcessParameter;
import com.mainsteam.stm.pluginprocessor.ResultSet;
import com.mainsteam.stm.pluginsession.PluginResultSet;
import com.mainsteam.stm.pluginsession.exception.PluginSessionRunException;

/**
 * @author 作者：ziw
 * @date 创建时间：2017年12月5日 下午3:58:07
 * @version 1.0
 */
public class KeyValueRowResultSetProcessorTest {

	@BeforeClass
	public static void setUpBeforeClass() throws Exception {
	}

	@AfterClass
	public static void tearDownAfterClass() throws Exception {
	}

	@Before
	public void setUp() throws Exception {
	}

	@After
	public void tearDown() throws Exception {
	}

	@Test
	public void testProcess() {
		KeyValueRowResultSetProcessor processor = new KeyValueRowResultSetProcessor();
		ProcessParameter parameter = new ProcessParameter();
		ParameterValue value = new ParameterValue();
		value.setKey("searchKey");
		value.setValue("key2");
		parameter.addParameter(value);
		PluginResultSet pluginResultSet = new PluginResultSet();
		pluginResultSet.addRow(new String[] { "key1", "value11", "key2", "value21", "key3", "value31" });
		pluginResultSet.addRow(new String[] { "key1", "value21", "key2", "value22", "key3", "value32" });
		pluginResultSet.addRow(new String[] { "key1", "value31", "key2", "value32", "key3", "value33" });
		ResultSet resultSet = new ResultSet(pluginResultSet, null);
		try {
			processor.process(resultSet, parameter, null);
		} catch (PluginSessionRunException e) {
			e.printStackTrace();
			fail();
		}
		assertEquals(3, resultSet.getRowLength());
		assertEquals(2, resultSet.getColumnLength());
		for (int i = 0; i < resultSet.getRowLength(); i++) {
			assertEquals(value.getValue(), resultSet.getValue(i, 0));
		}

		parameter = new ProcessParameter();
		value = new ParameterValue();
		value.setKey("searchKey");
		value.setValue("key2");
		parameter.addParameter(value);
		ParameterValue value1 = new ParameterValue();
		value1.setKey("removeKey");
		value1.setValue("true");
		parameter.addParameter(value1);

		pluginResultSet = new PluginResultSet();
		pluginResultSet.addRow(new String[] { "key1", "value11", "key2", "value21", "key3", "value31" });
		pluginResultSet.addRow(new String[] { "key1", "value12", "key2", "value22", "key3", "value32" });
		pluginResultSet.addRow(new String[] { "key1", "value13", "key2", "value23", "key3", "value33" });
		resultSet = new ResultSet(pluginResultSet, null);
		try {
			processor.process(resultSet, parameter, null);
		} catch (PluginSessionRunException e) {
			e.printStackTrace();
			fail();
		}
		assertEquals(3, resultSet.getRowLength());
		assertEquals(1, resultSet.getColumnLength());
		for (int i = 0; i < resultSet.getRowLength(); i++) {
			assertTrue(resultSet.getValue(i, 0).startsWith("value2"));
		}
	}
}
