package com.mainsteam.stm.plugin.vmware;

import org.junit.Test;

import com.mainsteam.stm.plugin.vmware.ESXiPluginSession;
import com.mainsteam.stm.pluginprocessor.ParameterValue;
import com.mainsteam.stm.pluginsession.PluginResultSet;
import com.mainsteam.stm.pluginsession.PluginSession;
import com.mainsteam.stm.pluginsession.PluginSessionContext;
import com.mainsteam.stm.pluginsession.exception.PluginSessionRunException;
import com.mainsteam.stm.pluginsession.parameter.Parameter;
import com.mainsteam.stm.pluginsession.parameter.PluginArrayExecutorParameter;
import com.mainsteam.stm.pluginsession.parameter.PluginInitParameter;


public class TestVm {

	@Test
	public void test() {
		PluginArrayExecutorParameter executorParameter = new PluginArrayExecutorParameter();
		ParameterValue pvs = new ParameterValue();
		pvs.setKey("COMMAND");
		pvs.setValue("com.mainsteam.stm.plugin.vmware.collector.VMWareESXICollector.getNetInterfaceList");
		ParameterValue pvs1 = new ParameterValue();
		pvs1.setKey("uuid");
		pvs1.setValue("4c4c4544-0037-3410-8042-b7c04f473032");
		
		executorParameter.setParameters(new ParameterValue[]{pvs,pvs1});
		
		PluginSessionContext context = null;
		PluginInitParameter initParameters = new PluginInitParameter() {
			public Parameter[] getParameters() {
				ParameterValue[] pvs = new ParameterValue[3];
				for (int i = 0; i < pvs.length; i++) {
					pvs[i] = new ParameterValue();
				}
				pvs[0].setKey("IP");
				pvs[0].setValue("172.16.7.121");
				pvs[1].setKey("username");
				pvs[1].setValue("root");
				pvs[2].setKey("password");
				pvs[2].setValue("passw0rd");
				return pvs;
			}
			public String getParameterValueByKey(String key) {
				return null;
			}
		};
		
		PluginSession session = new ESXiPluginSession();
		try {
			session.init(initParameters);
			PluginResultSet pluginResultSet = session.execute(executorParameter, context);
			System.out.println(pluginResultSet.toString());
		} catch (PluginSessionRunException e) {
			e.printStackTrace();
		}
	}

}
