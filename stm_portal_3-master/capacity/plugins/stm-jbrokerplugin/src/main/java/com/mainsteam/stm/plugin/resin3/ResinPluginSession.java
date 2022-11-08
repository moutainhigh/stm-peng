package com.mainsteam.stm.plugin.resin3;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import com.mainsteam.stm.plugin.session.BaseSession;
import com.mainsteam.stm.pluginsession.exception.PluginSessionRunException;
import com.mainsteam.stm.pluginsession.parameter.Parameter;
import com.mainsteam.stm.pluginsession.parameter.PluginInitParameter;

public class ResinPluginSession extends BaseSession {
	
	private static final Log logger = LogFactory.getLog(ResinPluginSession.class);
	private static final String RESIN_PLUGIN_IP = "IP";
	private static final String RESIN_PLUGIN_PORT = "resinPort";
	private static final String RESIN_PLUGIN_USERNAME = "resinUsername";
	private static final String RESIN_PLUGIN_PWD = "resinPassword";
	
	@Override
	public void init(PluginInitParameter init)
			throws PluginSessionRunException {
		Parameter[] initParameters=init.getParameters();
		for(int i=0;i<initParameters.length;i++){
			switch (initParameters[i].getKey()) {
			case RESIN_PLUGIN_IP:
				super.getParameter().setIp(initParameters[i].getValue());
				break;
			case RESIN_PLUGIN_PORT:
				super.getParameter().setPort(Integer.parseInt(initParameters[i].getValue()));			
				break;
			case RESIN_PLUGIN_USERNAME:
				super.getParameter().setUsername((initParameters[i].getValue()));
				break;
			case RESIN_PLUGIN_PWD:
				super.getParameter().setPassword((initParameters[i].getValue()));
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
		ResinConnect.close(super.getParameter());
	}

	@Override
	public void reload() {
		// TODO Auto-generated method stub

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
			case RESIN_PLUGIN_IP:
				super.getParameter().setIp(initParameters[i].getValue());
				break;
			case RESIN_PLUGIN_PORT:
				super.getParameter().setPort(Integer.parseInt(initParameters[i].getValue()));			
				break;
			case RESIN_PLUGIN_USERNAME:
				super.getParameter().setUsername((initParameters[i].getValue()));
				break;
			case RESIN_PLUGIN_PWD:
				super.getParameter().setPassword((initParameters[i].getValue()));
				break;
			default:
				if (logger.isWarnEnabled()) {
						logger.warn("warn:unkown initparameter " + initParameters[i].getKey() + "="
								+ initParameters[i].getValue());
					}
					break;
			}
		}
		return ResinConnect.check(super.getParameter());
	}
}
