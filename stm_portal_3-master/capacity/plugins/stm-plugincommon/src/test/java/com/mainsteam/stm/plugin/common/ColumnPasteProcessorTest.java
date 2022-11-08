/**
 * 
 */
package com.mainsteam.stm.plugin.common;

import org.junit.After;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;

import com.mainsteam.stm.pluginprocessor.ParameterValue;
import com.mainsteam.stm.pluginprocessor.ProcessParameter;
import com.mainsteam.stm.pluginprocessor.ResultSet;
import com.mainsteam.stm.pluginsession.PluginResultSet;

/**
 * @author Administrator
 *
 */
public class ColumnPasteProcessorTest {

	/**
	 * @throws java.lang.Exception
	 */
	@BeforeClass
	public static void setUpBeforeClass() throws Exception {
	}

	/**
	 * @throws java.lang.Exception
	 */
	@AfterClass
	public static void tearDownAfterClass() throws Exception {
	}

	/**
	 * @throws java.lang.Exception
	 */
	@Before
	public void setUp() throws Exception {
	}

	/**
	 * @throws java.lang.Exception
	 */
	@After
	public void tearDown() throws Exception {
	}

	/**
	 * Test method for
	 * {@link com.mainsteam.stm.plugin.common.ColumnPasteProcessor#process(com.mainsteam.stm.pluginprocessor.ResultSet, com.mainsteam.stm.pluginprocessor.ProcessParameter)}
	 * .
	 */
	@Test
	public void testProcess() {
		ColumnPasteProcessor processor = new ColumnPasteProcessor();
		PluginResultSet pluginResultSet = new PluginResultSet();
		pluginResultSet.addRow(new String[] { "1", "00:01:01:01:01:01" });
		pluginResultSet.addRow(new String[] { "2", "00:01:01:01:01:01" });
		pluginResultSet.addRow(new String[] { "3", "00:01:01:01:01:02" });
		pluginResultSet.addRow(new String[] { "4", "00:00:00:00:00:00" });
		// ResultSetMetaInfo metaInfo = new ResultSetMetaInfo();
		ResultSet resultSet = new ResultSet(pluginResultSet, null);
		ProcessParameter parameter = new ProcessParameter();
		ParameterValue paraValue = new ParameterValue();
		paraValue.setKey("filter");
		paraValue.setValue("00:00:00:00:00:00");
		parameter.addParameter(paraValue );
		processor.process(resultSet, parameter,null);
		String val = resultSet.getValue(0, 0);
		System.out.println(val);
	}

}
