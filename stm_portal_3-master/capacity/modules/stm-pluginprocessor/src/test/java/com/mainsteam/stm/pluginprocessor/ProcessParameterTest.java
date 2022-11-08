package com.mainsteam.stm.pluginprocessor;

import java.util.List;

import org.junit.Test;

public class ProcessParameterTest {

	@Test
	public void testAddParameter() {
		ProcessParameter pp = new ProcessParameter();
		{
			ParameterValue value = new ParameterValue();
			value.setKey("");
			value.setValue("xx");
			pp.addParameter(value);
		}
	}

	@Test
	public void testGetParameterValuesByKey() {

		ProcessParameter pp = new ProcessParameter();
		{
			ParameterValue value = new ParameterValue();
			value.setKey("");
			value.setValue("xx");
			pp.addParameter(value);
		}
		{
			ParameterValue value = new ParameterValue();
			value.setKey("k1");
			value.setValue("1");
			pp.addParameter(value);
		}
		{
			ParameterValue value = new ParameterValue();
			value.setKey("k1");
			value.setValue("2");
			pp.addParameter(value);
		}
		{
			ParameterValue value = new ParameterValue();
			value.setKey("k9");
			value.setValue("9");
			pp.addParameter(value);
		}
		List<ParameterValue> vs1 = pp.getParameterValuesByKey("k1");
		System.out.println("vs1 length:" + vs1.size());
	}

	@Test
	public void testListParameterValues() {
		ProcessParameter pp = new ProcessParameter();
		{
			ParameterValue value = new ParameterValue();
			value.setKey("");
			value.setValue("xx");
			pp.addParameter(value);
		}
		{
			ParameterValue value = new ParameterValue();
			value.setKey("k1");
			value.setValue("1");
			pp.addParameter(value);
		}
		{
			ParameterValue value = new ParameterValue();
			value.setKey("k1");
			value.setValue("2");
			pp.addParameter(value);
		}
		{
			ParameterValue value = new ParameterValue();
			value.setKey("k9");
			value.setValue("9");
			pp.addParameter(value);
		}
		ParameterValue[] vs = pp.listParameterValues();
		System.out.println("vs length:" + vs.length);
	}

}
