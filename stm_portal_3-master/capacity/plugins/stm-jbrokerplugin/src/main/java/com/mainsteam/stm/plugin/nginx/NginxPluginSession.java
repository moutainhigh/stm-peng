package com.mainsteam.stm.plugin.nginx;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import com.mainsteam.stm.plugin.apache.ApachePluginSession;
import com.mainsteam.stm.plugin.session.BaseSession;
import com.mainsteam.stm.pluginsession.exception.PluginSessionRunException;
import com.mainsteam.stm.pluginsession.parameter.Parameter;
import com.mainsteam.stm.pluginsession.parameter.PluginInitParameter;

public class NginxPluginSession extends BaseSession{
	
	private static final String NIGINX_PLUGIN_IP = "IP";
	private static final String NIGINX_PLUGIN_PORT = "nginxPort";
	private static final String NIGINX_PLUGIN_PAGENAME = "PAGENAME";
	private static final Log logger = LogFactory.getLog(ApachePluginSession.class);
	private static final String NIGINX_PLUGIN_TIMEOUT = "nginxTimeout";
	private static final int timeout = 30000;
	
	public void init(PluginInitParameter init)
			throws PluginSessionRunException {
		Parameter[] initParameters = init.getParameters();
		NginxBo niginxBo=new NginxBo();
		super.getParameter().setTimeout(timeout);
		for (int i = 0; i < initParameters.length; i++) {
			
			switch (initParameters[i].getKey()) {
			case NIGINX_PLUGIN_IP:
				super.getParameter().setIp(initParameters[i].getValue());
				break;
			case NIGINX_PLUGIN_PORT:
				super.getParameter().setPort(Integer.parseInt(initParameters[i].getValue()));  
				break;
			case NIGINX_PLUGIN_PAGENAME:
				niginxBo.setPageName(initParameters[i].getValue());
				super.getParameter().setNiginxBo(niginxBo);
				break;
			default:
				if (logger.isWarnEnabled()) {
						logger.warn("warn:unkown initparameter " + initParameters[i].getKey() + "="
								+ initParameters[i].getValue());
					}
					break;
			}
		}
	}

	@Override
	public void destory() {
	}

	@Override
	public void reload() {

	}
	
	public boolean check(PluginInitParameter init)
			throws PluginSessionRunException {
		Parameter[] initParameters = init.getParameters();
		NginxBo niginxBo=new NginxBo();
		for (int i = 0; i < initParameters.length; i++) {

			switch (initParameters[i].getKey()) {
			case NIGINX_PLUGIN_IP:
				super.getParameter().setIp(initParameters[i].getValue());
				break;
			case NIGINX_PLUGIN_PORT:
				super.getParameter().setPort(Integer.parseInt(initParameters[i].getValue()));  
				break;
			case NIGINX_PLUGIN_PAGENAME:
				niginxBo.setPageName(initParameters[i].getValue());
				super.getParameter().setNiginxBo(niginxBo);
				break;
			case NIGINX_PLUGIN_TIMEOUT:
				super.getParameter().setTimeout(Integer.parseInt(initParameters[i].getValue()));  
				break;
			default:
				if (logger.isWarnEnabled()) {
						logger.warn("warn:unkown initparameter " + initParameters[i].getKey() + "="
								+ initParameters[i].getValue());
					}
					break;
			}}
		return NginxCollectorUtil.isConnect(getParameter());
	}
	@Override
	public boolean isAlive() {
		return true;
	}
}
