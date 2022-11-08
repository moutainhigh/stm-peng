package com.mainsteam.stm.plugin.apache;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import com.mainsteam.stm.plugin.session.BaseSession;
import com.mainsteam.stm.pluginsession.exception.PluginSessionRunException;
import com.mainsteam.stm.pluginsession.parameter.Parameter;
import com.mainsteam.stm.pluginsession.parameter.PluginInitParameter;

public class ApachePluginSession extends BaseSession {
	
	private static final Log logger = LogFactory.getLog(ApachePluginSession.class);
	
	private static final String APACHE_PLUGIN_IP = "IP";
	private static final String APACHE_PLUGIN_PORT = "apachePort";
	private static final String APACHE_PLUGIN_URLPARAM = "apacheUrlparam";
	private static final String APACHE_PLUGIN_VERSION = "apacheVersion";
	private static final String APACHE_PLUGIN_TIMEOUT = "apacheTimeout";
	private static final String APACHE_PLUGIN_ISSSL="isSSL";
	private static final int timeout = 30000;
	
	@Override
	public void init(PluginInitParameter init)
			throws PluginSessionRunException {
		Parameter[] initParameters = init.getParameters();
		ApacheBo apacheBo=new ApacheBo();
		super.getParameter().setTimeout(timeout);
		for (int i = 0; i < initParameters.length; i++) {
			
			switch (initParameters[i].getKey()) {
			case APACHE_PLUGIN_IP:
				super.getParameter().setIp(initParameters[i].getValue());
				break;
			case APACHE_PLUGIN_PORT:
				super.getParameter().setPort(Integer.parseInt(initParameters[i].getValue()));  
				break;
			
			case APACHE_PLUGIN_URLPARAM:
				apacheBo.setUrlParam(initParameters[i].getValue()); 
				super.getParameter().setApacheBo(apacheBo);
				break;
			case APACHE_PLUGIN_ISSSL:
				apacheBo.setSSL(initParameters[i].getValue().equals("0")?false:true);
				super.getParameter().setApacheBo(apacheBo);
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
	@Override
	public boolean check(PluginInitParameter init)
			throws PluginSessionRunException {
		Parameter[] initParameters = init.getParameters();
		ApacheBo apacheBo=new ApacheBo();
		for (int i = 0; i < initParameters.length; i++) {
			
			switch (initParameters[i].getKey()) {
			case APACHE_PLUGIN_IP:
				super.getParameter().setIp(initParameters[i].getValue());
				break;
			case APACHE_PLUGIN_PORT:
				super.getParameter().setPort(Integer.parseInt(initParameters[i].getValue()));  
				break;
			case APACHE_PLUGIN_VERSION:
				apacheBo.setApacheVersion(initParameters[i].getValue());
				super.getParameter().setApacheBo(apacheBo);
				break;
			case APACHE_PLUGIN_TIMEOUT:
				super.getParameter().setTimeout(Integer.parseInt(initParameters[i].getValue()));  
				break;
			case APACHE_PLUGIN_URLPARAM:
				apacheBo.setUrlParam(initParameters[i].getValue()); 
				super.getParameter().setApacheBo(apacheBo);
				break;
			case APACHE_PLUGIN_ISSSL:
				apacheBo.setSSL(initParameters[i].getValue().equals("0")?false:true);
				super.getParameter().setApacheBo(apacheBo);
				break;
			default:
				if (logger.isWarnEnabled()) {
						logger.warn("warn:unkown initparameter " + initParameters[i].getKey() + "="
								+ initParameters[i].getValue());
					}
					break;
			}}
		return ApacheCollectorUtil.isConnect(getParameter());
	}
	@Override
	public boolean isAlive() {
		return true;
	}

}
