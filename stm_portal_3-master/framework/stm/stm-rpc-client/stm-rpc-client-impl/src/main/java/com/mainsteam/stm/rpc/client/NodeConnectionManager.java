/**
 * 
 */
package com.mainsteam.stm.rpc.client;

import java.io.IOException;
import java.lang.reflect.InvocationHandler;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.lang.reflect.Proxy;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import javax.management.MBeanServerConnection;
import javax.management.remote.JMXConnector;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import com.mainsteam.stm.jmx.JmxUtil;
import com.mainsteam.stm.node.Node;
import com.mainsteam.stm.route.RouteableJmxConnection;
import com.mainsteam.stm.route.logic.LogicAppEnum;
import com.mainsteam.stm.route.logic.LogicClient;
import com.mainsteam.stm.route.logic.LogicConnection;

/**
 * @author ziw
 * 
 */
public class NodeConnectionManager {

	private static final Log logger = LogFactory.getLog(NodeConnectionManager.class);

	private static final Class<?>[] interfaceMbean = { MBeanServerConnection.class };

	Map<String, JmxConnectorWrapper> jmxConnectionMap = new HashMap<>();

	private LogicClient logicClient;

	private boolean started;

	private NodeConnectionsChecker connectionsChecker;

	/**
	 * 
	 */
	public NodeConnectionManager() {
	}

	/**
	 * @param logicClient
	 *            the logicClient to set
	 */
	public final void setLogicClient(LogicClient logicClient) {
		this.logicClient = logicClient;
	}

	public void start() {
		// 增加对连接的可用性维护，做到自动重连的操作
		started = true;
		connectionsChecker = new NodeConnectionsChecker();
		Thread th = new Thread(connectionsChecker, "NodeConnectionsChecker");
		th.start();
	}

	public boolean ping(Node node) {
		LogicConnection connection;
		try {
			connection = logicClient.createConection(node.getIp(),node.getPort(), LogicAppEnum.PING_NODE);
			connection.close();
			return true;
		} catch (IOException e) {
			if (logger.isErrorEnabled()) {
				logger.error("ping", e);
			}
		}
		return false;
	}

	public synchronized MBeanServerConnection getConnection(Node node,LogicAppEnum appEnum) throws IOException {
		
		StringBuilder b = new StringBuilder();
		b.append(node.getIp()).append(':').append(node.getPort()).append('.').append(appEnum.name());
		String nodeKey = b.toString();
		if (jmxConnectionMap.containsKey(nodeKey)) {
			return jmxConnectionMap.get(nodeKey).conn;
		} else {
			Map<String, Object> env = new HashMap<>();
			RouteableJmxConnection connection = new RouteableJmxConnection(node.getIp(), node.getPort(), logicClient, appEnum);
			env.put("jmx.remote.message.connection", connection);
			JMXConnector connector = JmxUtil.newJMXConnector(node.getIp(),node.getPort(), env);
			JmxConnectorWrapper wrapper = new JmxConnectorWrapper();
			wrapper.connector = connector;
			wrapper.realConnection = connection;
			wrapper.node = node;
			wrapper.env = env;

			MBeanInvokeHandler handler = new MBeanInvokeHandler();
			handler.nodeKey = nodeKey;
			handler.conn = connector.getMBeanServerConnection();
			wrapper.conn = (MBeanServerConnection) Proxy.newProxyInstance(this.getClass().getClassLoader(), interfaceMbean, handler);
			jmxConnectionMap.put(nodeKey, wrapper);
			return wrapper.conn;
		}
	}

	private class MBeanInvokeHandler implements InvocationHandler {
		String nodeKey;
		MBeanServerConnection conn;

		@Override
		public Object invoke(Object proxy, Method method, Object[] args)
				throws Throwable {
			try {
				return method.invoke(conn, args);
			} catch (InvocationTargetException e) {
				if (logger.isErrorEnabled()) {
					logger.error(e.getTargetException().getMessage(), e);
				}
				throw e.getTargetException();
			} catch (Throwable e) {
				if (e instanceof IOException
						&& "close".equals(method.getName()) == false) {
					removeConnection(nodeKey);
					nodeKey = null;
					conn = null;
				}
				if (logger.isErrorEnabled()) {
					logger.error("invoke", e);
				}
				throw e;
			}
		}
	}

	public synchronized void destory() {
		if (logger.isInfoEnabled()) {
			logger.info("destory all jmx connections.");
		}
		List<String> wrapps = new ArrayList<>(jmxConnectionMap.keySet());
		for (Iterator<String> iterator = wrapps.iterator(); iterator.hasNext();) {
			String nodeKey = (String) iterator.next();
			JmxConnectorWrapper w = jmxConnectionMap.get(nodeKey);
			try {
				w.connector.close();
				w.conn = null;
				w.connector = null;
				iterator.remove();
				if (logger.isInfoEnabled()) {
					logger.info("destoryed connection to " + nodeKey);
				}
			} catch (IOException e) {
				if (logger.isErrorEnabled()) {
					logger.error("destory", e);
				}
			}
		}
	}

	private class JmxConnectorWrapper {
		JMXConnector connector;
		RouteableJmxConnection realConnection;
		MBeanServerConnection conn;
		@SuppressWarnings("unused")
		Node node;
		@SuppressWarnings("unused")
		Map<String, Object> env;
	}

	private synchronized void removeConnection(
			NodeConnectionManager.JmxConnectorWrapper jmxConnectorWrapper,
			String nodeKey) {
		if (jmxConnectorWrapper.connector != null) {
			if (logger.isInfoEnabled()) {
				logger.info("close old connector ");
			}
			try {
				jmxConnectorWrapper.connector.close();
				jmxConnectorWrapper.connector = null;
				jmxConnectorWrapper.conn = null;
			} catch (Exception e) {
				if (logger.isErrorEnabled()) {
					logger.error("check client jmx conneciton", e);
				}
			}
		}
		jmxConnectionMap.remove(nodeKey);
	}

	private synchronized void removeConnection(String nodeKey) {
		NodeConnectionManager.JmxConnectorWrapper jmxConnectorWrapper = jmxConnectionMap
				.get(nodeKey);
		if (jmxConnectorWrapper != null
				&& jmxConnectorWrapper.connector != null) {
			if (logger.isInfoEnabled()) {
				logger.info("close old connector ");
			}
			try {
				jmxConnectorWrapper.connector.close();
				jmxConnectorWrapper.connector = null;
				jmxConnectorWrapper.conn = null;
			} catch (Exception e) {
				if (logger.isErrorEnabled()) {
					logger.error("check client jmx conneciton", e);
				}
			}
		}
		jmxConnectionMap.remove(nodeKey);
	}

	private class NodeConnectionsChecker implements Runnable {
		@Override
		public void run() {
			while (started) {
				List<String> wrapps = new ArrayList<>(jmxConnectionMap.keySet());
				for (int i = 0; i < wrapps.size(); i++) {
					String nodeKey = wrapps.get(i);
					NodeConnectionManager.JmxConnectorWrapper jmxConnectorWrapper = jmxConnectionMap
							.get(nodeKey);
					if (jmxConnectorWrapper == null) {
						continue;
					}
					// if (jmxConnectorWrapper.realConnection.isAlive() ==
					// false) {
					// removeConnection(jmxConnectorWrapper, nodeKey);
					// } else {
					try {
						jmxConnectorWrapper.realConnection.checkState();
					} catch (IllegalStateException e) {
						if (logger.isErrorEnabled()) {
							logger.error("check client jmx conneciton ", e);
						}
						removeConnection(jmxConnectorWrapper, nodeKey);
					}
					// }
				}
				synchronized (this) {
					try {
						this.wait(1000);
					} catch (InterruptedException e) {
					}
				}
			}
		}
	}
}
