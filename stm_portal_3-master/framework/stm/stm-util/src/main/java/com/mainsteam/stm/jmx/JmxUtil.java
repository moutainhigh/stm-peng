package com.mainsteam.stm.jmx;

import java.io.IOException;
import java.net.MalformedURLException;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.Map;
import java.util.Properties;

import javax.management.MBeanServer;
import javax.management.MalformedObjectNameException;
import javax.management.ObjectName;
import javax.management.remote.JMXConnector;
import javax.management.remote.JMXConnectorFactory;
import javax.management.remote.JMXConnectorServer;
import javax.management.remote.JMXConnectorServerFactory;
import javax.management.remote.JMXServiceURL;

import org.apache.commons.lang.math.NumberUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import com.mainsteam.stm.util.PropertiesFileUtil;

public class JmxUtil {

	private static final Log logger = LogFactory.getLog(JmxUtil.class);

	private static final String JMX_CONFIG = "jmx_config.properties";

	private static final String JMX_CONFIG_KEY_SERVICE_URL = "jmx.serviceurl";

	private static final String JMX_CONFIG_KEY_ENV_PREFIX = "jmx.env.";

	public JmxUtil() {
	}

	public static JMXConnectorServer newConnectorServer(MBeanServer server,
			String ip, int port, Map<String, Object> env) throws IOException {
		Properties p = loadJmxProperties();
		JMXServiceURL serviceURL = newServerURL(ip, port);
		Map<String, Object> configEnv = newJmxEnvironment(p);
		if (env != null && configEnv != null) {
			configEnv.putAll(env);
		}
		JMXConnectorServer connectorServer = JMXConnectorServerFactory
				.newJMXConnectorServer(serviceURL, configEnv, server);
		return connectorServer;
	}

	public static JMXConnector newJMXConnector(String ip, int port,
			Map<String, Object> env) throws IOException {
		Properties p = loadJmxProperties();
		JMXServiceURL serviceURL = newServerURL(ip, port);
		Map<String, Object> configEnv = newJmxEnvironment(p);
		if (env != null && configEnv != null) {
			configEnv.putAll(env);
		}
		JMXConnector jmxc = JMXConnectorFactory.connect(serviceURL, configEnv);
		return jmxc;
	}

	public static Properties loadJmxProperties() {
		Properties p = PropertiesFileUtil.getProperties(JMX_CONFIG);
		return p;
	}

	public static Map<String, Object> newJmxEnvironment(Properties p) {
		HashMap<String, Object> env = new HashMap<String, Object>();
		Enumeration<Object> keys = p.keys();
		while (keys.hasMoreElements()) {
			String key = (String) keys.nextElement();
			if (key.startsWith(JMX_CONFIG_KEY_ENV_PREFIX)) {
				String value = p.getProperty(key);
				key = key.substring(JMX_CONFIG_KEY_ENV_PREFIX.length());
				if (NumberUtils.isNumber(value)) {
					char endChar = value.charAt(value.length() - 1);
					if (endChar == 'l' || endChar == 'L') {
						env.put(key, NumberUtils.toLong(value));
					} else {
						env.put(key, NumberUtils.toInt(value));
					}
				} else {
					env.put(key, value);
				}
			}
		}
		return env;
	}

	public static JMXServiceURL newServerURL(String ip, int port) {
		JMXServiceURL jmxServiceURL = null;
		Properties p = loadJmxProperties();
		String url = p.getProperty(JMX_CONFIG_KEY_SERVICE_URL);
		url = url.replaceAll("\\{host\\}", ip);
		url = url.replaceAll("\\{port\\}", String.valueOf(port));
		if (logger.isInfoEnabled()) {
			logger.info("load jmx ServerURL url=" + url);
		}
		try {
			jmxServiceURL = new JMXServiceURL(url);
		} catch (MalformedURLException e) {
			if (logger.isErrorEnabled()) {
				logger.error("newServerURL", e);
			}
		}
		return jmxServiceURL;
	}

	public static <T> ObjectName createName(Class<T> interfaceClass) {
		ObjectName name = null;
		try {
			name = new ObjectName("stm", "name", interfaceClass.getName());
		} catch (MalformedObjectNameException e) {
			if (logger.isErrorEnabled()) {
				logger.error("createName", e);
			}
		}
		return name;
	}
}
