package com.mainsteam.stm.plugin.jmx;
import com.mainsteam.stm.plugin.jmx.JMXPluginSession;
import com.mainsteam.stm.pluginprocessor.ParameterValue;
import com.mainsteam.stm.pluginsession.PluginResultSet;
import com.mainsteam.stm.pluginsession.PluginSessionContext;
import com.mainsteam.stm.pluginsession.exception.PluginSessionRunException;
import com.mainsteam.stm.pluginsession.parameter.Parameter;
import com.mainsteam.stm.pluginsession.parameter.PluginArrayExecutorParameter;
import com.mainsteam.stm.pluginsession.parameter.PluginInitParameter;


public class JMXPluginSessionTest {

	public static void main(String[] args) {
		
		JMXPluginSession session = new JMXPluginSession();
		PluginArrayExecutorParameter executorParameter = new PluginArrayExecutorParameter();
		ParameterValue[] pvs = new ParameterValue[3];
		for (int i = 0; i < pvs.length; i++) {
			pvs[i] = new ParameterValue();
		}
		pvs[0].setKey("objectName");
		pvs[0].setValue("Catalina:type=Manager,*");
		pvs[1].setKey("attributes");
		pvs[1].setValue("");
		pvs[2].setKey("multi");
		pvs[2].setValue("true");
		executorParameter.setParameters(pvs);

		PluginSessionContext context = null;
		PluginInitParameter initParameters = new PluginInitParameter() {
			public Parameter[] getParameters() {
				ParameterValue[] pvs = new ParameterValue[6];
				for (int i = 0; i < pvs.length; i++) {
					pvs[i] = new ParameterValue();
				}

				pvs[0].setKey("IP");
				pvs[0].setValue("172.16.8.62");
				pvs[1].setKey("jmxPort");
				pvs[1].setValue("8849");
				pvs[2].setKey("jmxUsername");
				pvs[2].setValue(null);
				pvs[3].setKey("jmxPassword");
				pvs[3].setValue(null);
				return pvs;
			}

			@Override
			public String getParameterValueByKey(String key) {
				return null;
			}
		};
		
		try {
			session.init(initParameters);
		} catch (PluginSessionRunException e) {
			e.printStackTrace();
		}
		
		try {
			PluginResultSet resultSet = session.execute(executorParameter, context);
			System.out.println(resultSet);
		} catch (PluginSessionRunException e) {
			e.printStackTrace();
		}

	}

}
