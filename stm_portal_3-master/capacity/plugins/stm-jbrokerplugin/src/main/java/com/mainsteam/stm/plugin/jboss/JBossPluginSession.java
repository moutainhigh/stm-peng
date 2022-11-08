package com.mainsteam.stm.plugin.jboss;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import com.mainsteam.stm.errorcode.CapcityErrorCodeConstant;
import com.mainsteam.stm.plugin.session.BaseSession;
import com.mainsteam.stm.pluginsession.exception.PluginSessionRunException;
import com.mainsteam.stm.pluginsession.parameter.Parameter;
import com.mainsteam.stm.pluginsession.parameter.PluginInitParameter;

public class JBossPluginSession extends BaseSession {

	private static final Log logger = LogFactory
			.getLog(JBossPluginSession.class);

	public static final String JBOSS_PLUGIN_IP = "IP";
	public static final String JBOSS_PLUGIN_PORT = "jbossPort";

	@Override
	public void init(PluginInitParameter init) throws PluginSessionRunException {
		Parameter[] initParameters = init.getParameters();
		for (int i = 0; i < initParameters.length; i++) {
			switch (initParameters[i].getKey()) {
			case JBOSS_PLUGIN_IP:
				super.getParameter().setIp(initParameters[i].getValue());
				break;
			case JBOSS_PLUGIN_PORT:
				super.getParameter().setPort(
						Integer.parseInt(initParameters[i].getValue()));
				break;
			default:
				if (logger.isWarnEnabled()) {
					logger.warn("warn:unkown initparameter "
							+ initParameters[i].getKey() + "="
							+ initParameters[i].getValue());
				}
				break;
			}
		}
	}

	@Override
	public void destory() {
		JMXConnectionFactory.destory(super.getParameter().getIp(), super
				.getParameter().getPort());
	}

	@Override
	public void reload() {
	}

	@Override
	public boolean isAlive() {
		return true;
	}

	@Override
	public boolean check(PluginInitParameter initParameters)
			throws PluginSessionRunException {
		String ip = null;
		int port = -1;
		Parameter[] init = initParameters.getParameters();
		for (int i = 0; i < init.length; i++) {
			switch (init[i].getKey()) {
			case JBOSS_PLUGIN_IP:
				ip = init[i].getValue();
				break;
			case JBOSS_PLUGIN_PORT:
				try {
					port = Integer.parseInt(init[i].getValue());
					break;
				} catch (Exception e) {
					logger.error(
							"Abnormal Port!the port is" + init[i].getValue(), e);
					throw new PluginSessionRunException(
							CapcityErrorCodeConstant.ERR_CAPCITY_DISCOVERY_PARAMS,
							"Abnormal Port");
				}

			default:
				break;
			}
		}
		return JMXConnectionFactory.check(ip, port);
	}
}
