package com.mainsteam.stm.plugin.common;

import org.junit.Test;

import com.mainsteam.stm.pluginprocessor.ParameterValue;
import com.mainsteam.stm.pluginprocessor.ProcessParameter;
import com.mainsteam.stm.pluginprocessor.ResultSet;
import com.mainsteam.stm.pluginsession.PluginResultSet;

public class FormatProcessorTest {

	@Test
	public void testProcess() {

		PluginResultSet pluginResultSet = new PluginResultSet();
		pluginResultSet.putValue(0, 0, "-1.2");

		ResultSet resultset = new ResultSet(pluginResultSet, null);
		FormatProcessor processor = new FormatProcessor();
		ProcessParameter parameter = new ProcessParameter();
		ParameterValue param1 = new ParameterValue();
		param1.setValue("TO_POSITIVE");
		parameter.addParameter(param1);
		processor.process(resultset, parameter, null);
		System.out.println(resultset.getValue(0, 0));

	}

	@Test
	public void testHexToMacAddress() {
		FormatProcessor processor = new FormatProcessor();
		String mac = processor.hexToMacAddress("eeffffff00");
		System.out.println(mac);
	}

	@Test
	public void testHexToIpAddress() {
		FormatProcessor processor = new FormatProcessor();
		String mac = processor.hexToIpAddress("ffffff00");
		System.out.println(mac);
	}
}
