package com.mainsteam.stm.plugin.sunjes;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import com.mainsteam.stm.plugin.session.BaseSession;
import com.mainsteam.stm.plugin.sunjes.util.SunJESCollectUtil;
import com.mainsteam.stm.pluginsession.exception.PluginSessionRunException;
import com.mainsteam.stm.pluginsession.parameter.Parameter;
import com.mainsteam.stm.pluginsession.parameter.PluginInitParameter;

public class SunJESPluginSession extends BaseSession {
	
	private static final Log logger = LogFactory.getLog(SunJESPluginSession.class);
	private static final String SUNJES_PLUGIN_IP = "IP";
	private static final String SUNJES_PLUGIN_PORT = "sunjesPort";
	private static final String SUNJES_PLUGIN_USERNAME = "sunjesUsername";
	private static final String SUNJES_PLUGIN_PWD = "sunjesPassword";
	private static final String SUNJES_PLUGIN_INSTANCENAME = "sunjesInstancename";

	@Override
	public void init(PluginInitParameter init)
			throws PluginSessionRunException {
		Parameter[] initParameters=init.getParameters();
		for(int i=0;i<initParameters.length;i++){
			switch (initParameters[i].getKey()) {
			case SUNJES_PLUGIN_IP:
				super.getParameter().setIp(initParameters[i].getValue());
				break;
			case SUNJES_PLUGIN_PORT:
				super.getParameter().setPort(Integer.parseInt(initParameters[i].getValue()));
				break;
			case SUNJES_PLUGIN_USERNAME:
				super.getParameter().setUsername(initParameters[i].getValue());
				break;
			case SUNJES_PLUGIN_PWD:
				super.getParameter().setPassword(initParameters[i].getValue());
				break;
			case SUNJES_PLUGIN_INSTANCENAME:
				SunjesBo sunjes=new SunjesBo();
				sunjes.setInstanceName(initParameters[i].getValue());
				super.getParameter().setSunjesBo(sunjes);
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
	public boolean isAlive() {
		return true;
	}
	@Override
	public boolean check(PluginInitParameter init)
			throws PluginSessionRunException {
		Parameter[] initParameters=init.getParameters();
		for(int i=0;i<initParameters.length;i++){
			switch (initParameters[i].getKey()) {
			case SUNJES_PLUGIN_IP:
				super.getParameter().setIp(initParameters[i].getValue());
				break;
			case SUNJES_PLUGIN_PORT:
				super.getParameter().setPort(Integer.parseInt(initParameters[i].getValue()));
				break;
			case SUNJES_PLUGIN_USERNAME:
				super.getParameter().setUsername(initParameters[i].getValue());
				break;
			case SUNJES_PLUGIN_PWD:
				super.getParameter().setPassword(initParameters[i].getValue());
				break;
			case SUNJES_PLUGIN_INSTANCENAME:
				SunjesBo sunjes=new SunjesBo();
				sunjes.setInstanceName(initParameters[i].getValue());
				super.getParameter().setSunjesBo(sunjes);
				break;
			default:
			if (logger.isWarnEnabled()) {
					logger.warn("warn:unkown initparameter " + initParameters[i].getKey() + "="
							+ initParameters[i].getValue());
				}
				break;
			}
		}
		return SunJESCollectUtil.check(super.getParameter());
	}
}
