package com.mainsteam.stm.plugin.TX9200;
import com.mainsteam.stm.pluginprocessor.ParameterValue;
import com.mainsteam.stm.pluginsession.PluginResultSet;
import com.mainsteam.stm.pluginsession.PluginSessionContext;
import com.mainsteam.stm.pluginsession.parameter.PluginArrayExecutorParameter;


public class Tx9200SessionTest {
	public static void main(String[] args){
		try {
			String ipaddress = "10.27.169.2";
			String username = "helpdesk";
			String password = "Ruisui123";
			Tx9200PluginSession Tx9200PluginSession = new Tx9200PluginSession(ipaddress, username, password);
			PluginSessionContext context = null;
			/*PluginArrayExecutorParameter executorParameter = new PluginArrayExecutorParameter();
			ParameterValue[] pvs = new ParameterValue[1];
			for (int i = 0; i < pvs.length; i++) {
				pvs[i] = new ParameterValue();
			}
			pvs[0].setKey("COMMAND");
			pvs[0].setValue("com.Tx9200.execute");
			executorParameter.setParameters(pvs);*/
			PluginResultSet pluginResultSet = Tx9200PluginSession.execute(null, context);
			System.out.println(pluginResultSet.toString());
		}
		catch(Exception e){
			e.printStackTrace();
			
		}
	}
}
