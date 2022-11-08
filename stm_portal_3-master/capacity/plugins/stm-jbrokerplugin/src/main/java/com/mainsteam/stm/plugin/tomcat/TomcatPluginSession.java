package com.mainsteam.stm.plugin.tomcat;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import com.mainsteam.stm.plugin.session.BaseSession;
import com.mainsteam.stm.pluginsession.exception.PluginSessionRunException;
import com.mainsteam.stm.pluginsession.parameter.Parameter;
import com.mainsteam.stm.pluginsession.parameter.PluginInitParameter;

/**
 * Tomcat采集插件
 * @author xiaop_000
 *
 */
public class TomcatPluginSession extends BaseSession {
	
	private static final Log logger = LogFactory.getLog(TomcatPluginSession.class);
	public static final String TOMCAT_PLUGIN_PASSWORD = "tomcatpassword";

	public static final String TOMCAT_PLUGIN_USERNAME = "tomcatusername";

	public static final String TOMCAT_PLUGIN_PORT = "tomcatport";

	public static final String TOMCAT_PLUGIN_IP = "IP";
	
	public static final String TOMCAT_PLUGIN_VERSION="tomcatVersion";
	@Override
	public void init(PluginInitParameter init)
			throws PluginSessionRunException {
		Parameter[] initParameters = init.getParameters();
		for (int i = 0; i < initParameters.length; i++) {
			
			switch (initParameters[i].getKey()) {
			case TOMCAT_PLUGIN_IP:
				super.getParameter().setIp(initParameters[i].getValue());
				break;
			case TOMCAT_PLUGIN_PORT:
				super.getParameter().setPort(Integer.parseInt(initParameters[i].getValue()));  
				break;
			case TOMCAT_PLUGIN_USERNAME:
				super.getParameter().setUsername(initParameters[i].getValue()); 
				break;
			
			case TOMCAT_PLUGIN_PASSWORD:
				super.getParameter().setPassword(initParameters[i].getValue()); 
				break;
				
			case TOMCAT_PLUGIN_VERSION:
				super.getParameter().setTomcatVersion(initParameters[i].getValue()); 
				break;
			default:
				if (logger.isWarnEnabled()) {
						logger.warn("warn:unkown initparameter " + initParameters[i].getKey() + "="
								+ initParameters[i].getValue());
					}
					break;
				
			
			}
		}
//			String ip = tomcatBo.getIp();
//			int port = tomcatBo.getPort();
//			String url = "http://"+ip+":"+port+"/manager/status";
			//TomcatCollector.getInstance(url, tomcatBo.getUsername(), tomcatBo.getPassword(), tomcatVersion);
		
	}
	
	@Override
	public void destory() {
	}

	@Override
	public void reload() {
	}

	@Override
	public boolean isAlive() {
		return true;
	}
	
}
