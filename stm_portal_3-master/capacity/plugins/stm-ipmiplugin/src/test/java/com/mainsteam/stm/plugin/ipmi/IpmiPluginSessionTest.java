package com.mainsteam.stm.plugin.ipmi;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

import com.mainsteam.stm.pluginsession.PluginResultSet;
import com.mainsteam.stm.pluginsession.PluginSession;
import com.mainsteam.stm.pluginsession.exception.PluginSessionRunException;
import com.mainsteam.stm.pluginsession.parameter.Parameter;
import com.mainsteam.stm.pluginsession.parameter.PluginArrayExecutorParameter;
import com.mainsteam.stm.pluginsession.parameter.PluginInitParameter;

public class IpmiPluginSessionTest {
	PluginSession testSession;

	@Before
	public void Before() throws PluginSessionRunException {
		testSession = new IpmiPluginSession();
		PluginInitParameter initParameters = new PluginInitParameter() {

			@Override
			public Parameter[] getParameters() {
				ParameterValue[] parameterValues = new ParameterValue[6];
				for (int i = 0; i < parameterValues.length; ++i)
					parameterValues[i] = new ParameterValue();
				parameterValues[0].setKey("IP");
				parameterValues[0].setValue("192.168.1.44");
				parameterValues[1].setKey("port");
				parameterValues[1].setValue("623");
				parameterValues[2].setKey("level");
				parameterValues[2].setValue("ADMINISTRATOR");
				parameterValues[3].setKey("authtype");
				parameterValues[3].setValue("MD5");
				parameterValues[4].setKey("username");
				parameterValues[4].setValue("root");
				parameterValues[5].setKey("password");
				parameterValues[5].setValue("12345678");
				return parameterValues;
			}

			@Override
			public String getParameterValueByKey(String key) {
				return null;
			}
		};
		testSession.init(initParameters);
		System.setProperty("caplibs.path", "F:\\OC4New\\Capacity\\cap_libs");
	}

	@Test
	public void testExecute() throws PluginSessionRunException {
		ParameterValue parameterValue = new ParameterValue();
		parameterValue.setKey("command");
		parameterValue.setValue("fru");
		PluginArrayExecutorParameter executorParameter = new PluginArrayExecutorParameter();
		executorParameter.setParameters(new ParameterValue[] { parameterValue });
		try {
			PluginResultSet resultSet = testSession.execute(executorParameter, null);
			Assert.assertTrue(resultSet.getValue(0, 0).startsWith("FRU"));
		} catch (Exception e) {
			Assert.fail();
		}
	}

}

class ParameterValue implements Parameter {

	private String key;
	private String value;
	private String type;

	public ParameterValue() {
	}

	public String getType() {
		return type;
	}

	public String getKey() {
		return key;
	}

	public void setKey(String key) {
		this.key = key;
	}

	public String getValue() {
		return value;
	}

	public void setType(String type) {
		this.type = type;
	}

	public void setValue(String value) {
		this.value = value;
	}
}
