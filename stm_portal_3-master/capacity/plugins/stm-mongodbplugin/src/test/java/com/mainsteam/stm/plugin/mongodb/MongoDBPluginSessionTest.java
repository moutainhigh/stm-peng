package com.mainsteam.stm.plugin.mongodb;

import org.junit.Test;

import com.mainsteam.stm.pluginprocessor.ParameterValue;
import com.mainsteam.stm.pluginsession.PluginResultSet;
import com.mainsteam.stm.pluginsession.PluginSessionContext;
import com.mainsteam.stm.pluginsession.exception.PluginSessionRunException;
import com.mainsteam.stm.pluginsession.parameter.Parameter;
import com.mainsteam.stm.pluginsession.parameter.PluginArrayExecutorParameter;
import com.mainsteam.stm.pluginsession.parameter.PluginInitParameter;

public class MongoDBPluginSessionTest {

	@Test
	public void mongoDBTest() throws PluginSessionRunException{
		PluginArrayExecutorParameter executorParameter = new PluginArrayExecutorParameter();
		ParameterValue[] pvs = new ParameterValue[2];
		for (int i = 0; i < pvs.length; i++) {
			pvs[i] = new ParameterValue();
		}
		pvs[0].setKey("SHELL");
		pvs[0].setValue("serverStatus");
		pvs[1].setKey("PARAMETER");
		pvs[1].setValue("asserts");
		executorParameter.setParameters(pvs);
		
		PluginSessionContext context = null;
		PluginInitParameter initParameters = new PluginInitParameter() {
			public Parameter[] getParameters() {
				ParameterValue[] pvs = new ParameterValue[6];
				for (int i = 0; i < pvs.length; i++) {
					pvs[i] = new ParameterValue();
				}

				pvs[0].setKey(MongoDBPluginSession.JDBCPLUGIN_IP);
				pvs[0].setValue("192.168.10.236");
				pvs[1].setKey(MongoDBPluginSession.JDBCPLUGIN_JDBC_PORT);
				pvs[1].setValue("27017");
				pvs[2].setKey(MongoDBPluginSession.JDBCPLUGIN_DB_NAME);
				pvs[2].setValue("test");
//				pvs[3].setKey(JDBCPluginSession.JDBCPLUGIN_DB_USERNAME);
//				pvs[3].setValue("sa");
//				pvs[4].setKey(JDBCPluginSession.JDBCPLUGIN_DB_PASSWORD);
//				pvs[4].setValue("password123!");
//				pvs[5].setKey(JDBCPluginSession.JDBCPLUGIN_DB_NAME);
//				pvs[5].setValue("win20itsjcq89y");
				return pvs;
			}

			public String getParameterValueByKey(String key) {
				return null;
			}
		};
		
		MongoDBPluginSession session = new MongoDBPluginSession();
		session.init(initParameters);
		PluginResultSet pluginResultSet = session.execute(executorParameter,
				context);
		System.out.println(pluginResultSet);
	}
}
