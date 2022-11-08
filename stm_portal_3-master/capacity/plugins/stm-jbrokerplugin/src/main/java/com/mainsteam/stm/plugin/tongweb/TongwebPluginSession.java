package com.mainsteam.stm.plugin.tongweb;

import java.io.IOException;
import java.net.MalformedURLException;
import java.util.HashMap;
import java.util.Map;

import javax.management.MBeanServerConnection;
import javax.management.remote.JMXConnector;
import javax.management.remote.JMXConnectorFactory;
import javax.management.remote.JMXServiceURL;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import com.mainsteam.stm.errorcode.CapcityErrorCodeConstant;
import com.mainsteam.stm.plugin.session.BaseSession;
import com.mainsteam.stm.pluginsession.exception.PluginSessionRunException;
import com.mainsteam.stm.pluginsession.parameter.Parameter;
import com.mainsteam.stm.pluginsession.parameter.PluginInitParameter;

public class TongwebPluginSession extends BaseSession {
	private Log log=LogFactory.getLog(TongwebPluginSession.class);
	private static final String TONGWEB_PLUGIN_IP = "tongwebIP";
	private static final String TONGWEB_PLUGIN_PORT = "tongwebPort";
	private static final String TONGWEB_PLUGIN_USERNAME = "tongwebUsername";
	private static final String TONGWEB_PLUGIN_PWD = "tongwebPassword";
	private JMXConnector jmxConnector = null;
	private MBeanServerConnection mBeanServerConnection = null;

	@Override
	public void init(PluginInitParameter initParameters)
			throws PluginSessionRunException {
		Parameter[] init = initParameters.getParameters();
		for (int i = 0; i < init.length; i++) {
			switch (init[i].getKey()) {
			case TONGWEB_PLUGIN_IP:
				super.getParameter().setIp(init[i].getValue());
				break;
			case TONGWEB_PLUGIN_PORT:
				super.getParameter().setPort(
						Integer.parseInt(init[i].getValue()));
				break;
			case TONGWEB_PLUGIN_USERNAME:
				super.getParameter().setUsername(init[i].getValue());
				break;
			case TONGWEB_PLUGIN_PWD:
				super.getParameter().setPassword(init[i].getValue());
				break;
			default:
				break;
			}
		}
		String ip = super.getParameter().getIp();
		int port = super.getParameter().getPort();
		String userName = super.getParameter().getUsername();
		String password = super.getParameter().getPassword();
		createSession(ip, port, userName, password);
		if (isAlive()) {
			super.getParameter()
					.setmBeanServerConnection(mBeanServerConnection);
		}
	}

	private MBeanServerConnection createSession(String ip, int port,
			String userName, String password) throws PluginSessionRunException {
		String jmxUrl = "service:jmx:rmi:///jndi/rmi://" + ip + ":" + port
				+ "/server";
		String credentials[] = new String[] { userName, password };
		Map<String, String[]> env = new HashMap<String, String[]>();
		env.put(JMXConnector.CREDENTIALS, credentials);
		try {
			JMXServiceURL jmxServiceURL = new JMXServiceURL(jmxUrl);

			jmxConnector = JMXConnectorFactory.connect(jmxServiceURL, env);

			mBeanServerConnection = jmxConnector.getMBeanServerConnection();

		} catch (MalformedURLException e) {
			throw new PluginSessionRunException(
					CapcityErrorCodeConstant.ERR_CAPCITY_URL_ERR, e);
		} catch (IOException e) {
			e.printStackTrace();
			throw new PluginSessionRunException(
					CapcityErrorCodeConstant.ERR_CAPCITY_JMX_CONN, e);
		}
		return mBeanServerConnection;
	}
	@Override
	public void destory() {
		if (jmxConnector != null && isAlive()) {
			try {
				jmxConnector.close();
			} catch (IOException e) {
				log.error("close JMX connect  failed" + e.getMessage(), e);
			}
		}
	}
	@Override
	public boolean isAlive() {
		if (jmxConnector != null) {
			try {
				jmxConnector.getConnectionId();
				return true;
			} catch (IOException e) {
				return false;
			}
		} else {
			return false;
		}
	}
}
