/**
 * 
 */
package com.mainsteam.stm.route.logic.impl;

import java.net.InetSocketAddress;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import com.mainsteam.stm.route.logic.LogicAppEnum;
import com.mainsteam.stm.route.logic.LogicServer;
import com.mainsteam.stm.route.logic.connection.ServerConnection;
import com.mainsteam.stm.route.logic.connection.impl.ServerConnectionImpl;

/**
 * @author ziw
 * 
 */
public class LogicServerImpl implements LogicServer {

	private static final Log logger = LogFactory.getLog(LogicServer.class);

	private Map<LogicAppEnum, List<ServerConnection>> connectionsMap;

	private InetSocketAddress serverSocketAddress;
	
	/**
	 * 
	 */
	public LogicServerImpl() {
	}

	public synchronized void start() {
		connectionsMap = new HashMap<LogicAppEnum, List<ServerConnection>>(
				LogicAppEnum.values().length);
	}

	private LogicAppEnum getKey(LogicAppEnum appEnum) {
		if (LogicAppEnum.RPC_JMX_NODE == appEnum
				|| LogicAppEnum.RPC_JMX_NODE_GROUP == appEnum) {
			return LogicAppEnum.RPC_JMX_NODE_GROUP;
		} else if (LogicAppEnum.TRANSFER_TCP == appEnum
				|| LogicAppEnum.TRANSFER_UDP == appEnum) {
			return LogicAppEnum.TRANSFER_TCP;
		} else if (LogicAppEnum.FILE_TRANSFER_TCP == appEnum) {
			return LogicAppEnum.FILE_TRANSFER_TCP;
		} else {
			return LogicAppEnum.PING_NODE;
		}
	}

	public synchronized void addLoginConnection(ServerConnection connection) {
		LogicAppEnum appEnum = connection.getConnectionApp();
		LogicAppEnum key = getKey(appEnum);
		synchronized (key) {
			if (connectionsMap.containsKey(key)) {
				List<ServerConnection> connections = connectionsMap.get(key);
				connections.add(connection);
				key.notifyAll();
			} else if (appEnum == LogicAppEnum.PING_NODE) {
				if (logger.isInfoEnabled()) {
					logger.info("addLoginConnection recieve one ping connection.");
				}
				return;
			} else {
				if (logger.isErrorEnabled()) {
					logger.error("addLoginConnection logic server has not start on appEnum="
							+ appEnum);
				}
				connection.close();
			}
		}
	}

	@Override
	public synchronized void startServer(LogicAppEnum appEnum) {
		LogicAppEnum key = getKey(appEnum);
		if (connectionsMap.containsKey(key)) {
			if (logger.isWarnEnabled()) {
				logger.warn("startServer server on " + appEnum
						+ " has started.");
			}
			return;
		}
		connectionsMap.put(key, new ArrayList<ServerConnection>());
		if (logger.isInfoEnabled()) {
			logger.info("startServer on " + appEnum);
		}
	}

	@Override
	public ServerConnection accept(LogicAppEnum appEnum) {
		LogicAppEnum key = getKey(appEnum);
		synchronized (key) {
			if (connectionsMap.containsKey(key)) {
				ServerConnection conn = null;
				List<ServerConnection> connections = connectionsMap.get(key);
				do {
					if (connections.size() <= 0) {
						try {
							key.wait(1000);
						} catch (InterruptedException e) {
							e.printStackTrace();
						}
					} else {
						conn = connections.remove(0);
						if (conn != null) {
							((ServerConnectionImpl) conn).setServer(null);
						}
					}
				} while (conn == null);
				if (logger.isInfoEnabled()) {
					logger.info("accept one connection for " + appEnum);
				}
				return conn;
			}
		}
		throw new RuntimeException("Server has not been started for " + appEnum);
	}

	@Override
	public synchronized void stopServer(LogicAppEnum appEnum) {
		LogicAppEnum key = getKey(appEnum);
		synchronized (appEnum) {
			if (connectionsMap.containsKey(key)) {
				connectionsMap.remove(key);
				appEnum.notifyAll();
			}
		}
	}

	@Override
	public synchronized void removeServerConnection(ServerConnection conn) {
		for (Iterator<List<ServerConnection>> iterator = connectionsMap
				.values().iterator(); iterator.hasNext();) {
			List<ServerConnection> connectionList = iterator.next();
			connectionList.remove(conn);
		}
	}

	@Override
	public InetSocketAddress getServerHost() {
		return serverSocketAddress;
	}
	
	public void setServerHost(InetSocketAddress serverSocketAddress){
		this.serverSocketAddress = serverSocketAddress;
	}
}
