package com.mainsteam.stm.plugin.weblogic;

import java.io.IOException;
import java.net.MalformedURLException;
import java.util.Hashtable;

import javax.management.remote.JMXConnector;
import javax.management.remote.JMXConnectorFactory;
import javax.management.remote.JMXServiceURL;
import javax.naming.Context;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import com.mainsteam.stm.plugin.parameter.JBrokerParameter;
import com.mainsteam.stm.plugin.session.BaseSession;
import com.mainsteam.stm.pluginsession.exception.PluginSessionRunException;
import com.mainsteam.stm.pluginsession.parameter.Parameter;
import com.mainsteam.stm.pluginsession.parameter.PluginInitParameter;

/**
 * Weblogic采集插件
 */
public class WeblogicPluginSession extends BaseSession {

	private static final Log logger = LogFactory
			.getLog(WeblogicPluginSession.class);
	private static final String WEBLOGIC_PLUGIN_IP = "IP";
	private static final String WEBLOGIC_PLUGIN_PORT = "weblogicPort";
	private static final String WEBLOGIC_PLUGIN_USERNAME = "weblogicUsername";
	private static final String WEBLOGIC_PLUGIN_PWD = "weblogicPassword";
	private static final String WEBLOGIC_PLUGIN_INSTANCENAME = "weblogicInstancename";
	JMXConnector connector = null;
	private boolean isAlive = false;

	@Override
	public void init(PluginInitParameter init) throws PluginSessionRunException {
		Parameter[] initParameters = init.getParameters();
		WeblogicBo weblogicBo = new WeblogicBo();
		for (int i = 0; i < initParameters.length; i++) {
			switch (initParameters[i].getKey()) {
			case WEBLOGIC_PLUGIN_IP:
				super.getParameter().setIp(initParameters[i].getValue());
				;
				break;
			case WEBLOGIC_PLUGIN_PORT:
				super.getParameter().setPort(
						Integer.parseInt(initParameters[i].getValue()));
				break;
			case WEBLOGIC_PLUGIN_USERNAME:
				super.getParameter().setUsername(initParameters[i].getValue());
				break;
			case WEBLOGIC_PLUGIN_PWD:
				super.getParameter().setPassword(initParameters[i].getValue());
				break;
			case WEBLOGIC_PLUGIN_INSTANCENAME:
				weblogicBo.setInstancename(initParameters[i].getValue());
				super.getParameter().setWeblogicBo(weblogicBo);
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
		connector = createJMXConnector(super.getParameter());
		if (isAlive) {
			weblogicBo.setJmxConnector(connector);
			super.getParameter().setWeblogicBo(weblogicBo);
		}
	}

	public JMXConnector createJMXConnector(JBrokerParameter obj) {
		String ip = obj.getIp();
		int port = obj.getPort();
		String username = obj.getUsername();
		String password = obj.getPassword();
		JMXConnector conn = null;
		String protocol = "t3";
		String jndiroot = "/jndi/";
		String mserver = "weblogic.management.mbeanservers.domainruntime";
		JMXServiceURL serviceURL;
		try {
			serviceURL = new JMXServiceURL(protocol, ip, port, jndiroot
					+ mserver);
		} catch (MalformedURLException e) {
			logger.error(e.getMessage(), e);
			return null;
		}
		Hashtable<String, String> h = new Hashtable<String, String>();
		h.put(Context.SECURITY_PRINCIPAL, username);
		h.put(Context.SECURITY_CREDENTIALS, password);
		h.put(JMXConnectorFactory.PROTOCOL_PROVIDER_PACKAGES,
				"weblogic.management.remote");
		try {
			String isSun = System
					.getProperty("sun.lang.ClassLoader.allowArraySyntax");
			logger.debug("sun.lang.ClassLoader.allowArraySyntax---------->"
					+ isSun);
			conn = JMXConnectorFactory.connect(serviceURL, h);
			isAlive = true;
			logger.debug("weblogic-->establish the jmx connection is successful ");
			return conn;
		} catch (IOException e) {
			logger.error("connection message-->" + "protocol" + protocol
					+ "--ip:" + ip + "--port:" + port + "--jndiroot:"
					+ jndiroot + "--msserver:" + mserver + "--username:"
					+ username + "--password:" + password + "<--");
			logger.error("getJMXConnector error!");
			logger.error(
					"----------getJMXConnector error! stackTrace:"
							+ e.getMessage(), e);
			return null;
		}

	}

	@Override
	public void destory() {
		if (connector != null && isAlive()) {
			try {
				connector.close();
				isAlive = false;
			} catch (IOException e) {
				logger.error("close JMX connect  failed" + e.getMessage(), e);
			}
		}
	}

	@Override
	public void reload() {
		// TODO Auto-generated method stub

	}

	@Override
	public boolean isAlive() {
		try {
			if (connector.getConnectionId() != null) {
				isAlive = true;
			} else {
				isAlive = false;
			}
		} catch (IOException e) {
			isAlive = false;
		}
		return isAlive;
	}

	@Override
	public boolean check(PluginInitParameter init)
			throws PluginSessionRunException {
		JMXConnector checkConnector = null;
		Parameter[] initParameters = init.getParameters();
		WeblogicBo weblogicBo = new WeblogicBo();
		for (int i = 0; i < initParameters.length; i++) {
			switch (initParameters[i].getKey()) {
			case WEBLOGIC_PLUGIN_IP:
				super.getParameter().setIp(initParameters[i].getValue());
				;
				break;
			case WEBLOGIC_PLUGIN_PORT:
				super.getParameter().setPort(
						Integer.parseInt(initParameters[i].getValue()));
				break;
			case WEBLOGIC_PLUGIN_USERNAME:
				super.getParameter().setUsername(initParameters[i].getValue());
				break;
			case WEBLOGIC_PLUGIN_PWD:
				super.getParameter().setPassword(initParameters[i].getValue());
				break;
			case WEBLOGIC_PLUGIN_INSTANCENAME:
				weblogicBo.setInstancename(initParameters[i].getValue());
				super.getParameter().setWeblogicBo(weblogicBo);
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
		checkConnector = createJMXConnector(super.getParameter());
		if (checkConnector != null) {
			try {
				checkConnector.close();
			} catch (IOException e) {
				logger.error(e);
			}
			return true;
		} else {
			return false;
		}
	}
}
