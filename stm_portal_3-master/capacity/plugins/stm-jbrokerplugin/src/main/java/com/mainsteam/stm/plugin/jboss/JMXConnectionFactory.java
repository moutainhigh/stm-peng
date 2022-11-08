package com.mainsteam.stm.plugin.jboss;

import java.io.IOException;
import java.net.MalformedURLException;
import java.util.HashMap;
import java.util.Map;

import javax.management.remote.JMXConnector;
import javax.management.remote.JMXConnectorFactory;
import javax.management.remote.JMXServiceURL;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

/**
 * 实现commons-pool.jar连接池
 */
public class JMXConnectionFactory {

	private static final Log debug = LogFactory
			.getLog(JMXConnectionFactory.class);
	private static Map<String, JMXConnector> jmxMap = new HashMap<String, JMXConnector>();
	private static final int RECONNECT = 1;

	private JMXConnectionFactory() {
	};

	public static boolean check(String ip, int port) {
		JMXConnector connector = makeConnection(ip, port);
		if (connector != null) {
			try {
				connector.close();
			} catch (IOException e) {
				debug.error(e);
			}
			return true;
		} else {
			return false;
		}
	}

	public static void destory(String ip, int port) {
		String key = ip + ":" + port;
		try {
			if (jmxMap.containsKey(key) && jmxMap.get(key) != null) {
				jmxMap.get(key).close();
				jmxMap.remove(key);
			}
		} catch (IOException e) {
			debug.error(e);
		}
	}

	public static Object getConnection(String ip, int port) {
		String key = ip + ":" + port;
		JMXConnector connector = jmxMap.get(key);
		if (connector != null) {
			try {
				connector.getConnectionId(); // 测试连接是否可用
				return connector;
			} catch (IOException e) {
				connector = makeConnection(ip, port);
				if (connector != null) {
					jmxMap.put(key, connector);
					return connector;
				} else {
					debug.error(
							"create Jboss jmx connection failed!"
									+ e.getMessage(), e);
				}
			}
		} else {
			connector = makeConnection(ip, port);
			if (connector != null) {
				jmxMap.put(key, connector);
				return connector;
			} else {
				debug.error("create Jboss jmx connection failed!");
			}
		}
		return null;
	}

	/*
	 * public static boolean init(String ip, int port) throws
	 * PluginSessionRunException { String key = ip + ":" + port; JMXConnector
	 * connector = makeConnection(ip, port); if (connector != null) {
	 * jmxMap.put(key, connector); return true; } else { return false; } }
	 */

	private static JMXConnector makeConnection(String ip, int port) {
		JMXConnector connector = makeConnection(ip, port, RECONNECT);
		return connector;
	}

	private static JMXConnector makeConnection(String ip, int port,
			int reconnectTime) {
		if (reconnectTime < 0) {
			debug.error("reconncet times can not Less than 0");
			return null;
		}
		JMXServiceURL url;
		JMXConnector connector;
		String urlStr = "service:jmx:rmi:///jndi/rmi://" + ip + ":" + port
				+ "/jmxrmi";
		for (int i = 0; i < reconnectTime; i++) {
			try {
				// url = new
				// JMXServiceURL("service:jmx:rmi://"+this.ip+"/jndi/rmi://"+this.ip+":"+this.port+"/jmxconnector");
				url = new JMXServiceURL(urlStr);
				connector = JMXConnectorFactory.connect(url);
				return connector;
			} catch (MalformedURLException e) {
				debug.error("Error JMX URL.", e);
			} catch (IOException e) {
				debug.error("IO Error. ---- "
						+ "service:jmx:rmi:///jndi/rmi://" + ip + ":" + port
						+ "/jmxrmi", e);
				urlStr = "service:jmx:rmi://" + ip + "/jndi/rmi://" + ip + ":"
						+ port + "/jmxconnector";
				try {
					url = new JMXServiceURL(urlStr);
					connector = JMXConnectorFactory.connect(url);
					return connector;
				} catch (MalformedURLException e1) {
					debug.error("Error JMX URL.", e1);
				} catch (IOException e2) {
					debug.error("IO Error. --- " + "service:jmx:rmi://" + ip
							+ "/jndi/rmi://" + ip + ":" + port
							+ "/jmxconnector", e2);
				} catch (Exception e3) {
					debug.error(e3.getMessage(), e);
				}
			} catch (Exception e) {
				debug.error(e.getMessage(), e);
			}

		}
		return null;
	}

}
