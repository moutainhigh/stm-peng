/**
 * 
 */
package com.mainsteam.stm.route.physical.connection.impl;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import com.mainsteam.stm.route.connection.ConnectionProtocol;
import com.mainsteam.stm.route.logic.LogicAppEnum;
import com.mainsteam.stm.route.physical.PhsicalConnectionManager;
import com.mainsteam.stm.route.physical.PhysicalClient;
import com.mainsteam.stm.route.physical.connection.PhysicalConnection;

/**
 * @author ziw
 * 
 */
public class PhsicalConnectionManagerImpl implements PhsicalConnectionManager {

	private static final Log logger = LogFactory
			.getLog(PhsicalConnectionManager.class);

	private Map<String, PhysicalConnection> connectionsMap;

	private PhysicalClient client;

	/**
	 * 
	 */
	public PhsicalConnectionManagerImpl() {
		connectionsMap = new HashMap<String, PhysicalConnection>();
	}

	/**
	 * @param client
	 *            the client to set
	 */
	public final void setClient(PhysicalClient client) {
		this.client = client;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * com.mainsteam.stm.route.physical.PhsicalConnectionManager#getConnection
	 * (java.lang.String, int,
	 * com.mainsteam.stm.route.connection.ConnectionProtocol)
	 */
	@Override
	public synchronized PhysicalConnection getConnection(String ip, int port,
			ConnectionProtocol protocol, LogicAppEnum appEnum)
			throws IOException {
		StringBuilder b = new StringBuilder();
		b.append(ip).append(':').append(port).append(':').append(protocol);
		if (appEnum == LogicAppEnum.RPC_JMX_NODE
				|| appEnum == LogicAppEnum.RPC_JMX_NODE_GROUP) {
			b.append(':').append("RPC");
		} else {
			b.append(':').append("TRANSFER");
		}
		PhysicalConnection conn = null;
		String key = b.toString();
		if (logger.isInfoEnabled()) {
			logger.info("getConnection start " + key);
		}
		if (connectionsMap.containsKey(key)) {
			conn = connectionsMap.get(key);
			if (!conn.isValid()) {
				if (logger.isInfoEnabled()) {
					logger.info("getConnection conn is invalid.close it and create a new one.key="+key);
				}
				conn.close();
				connectionsMap.remove(key);
				conn = client.createPhysicalConnection(ip, port, protocol);
				connectionsMap.put(key, conn);
			}
		} else {
			conn = client.createPhysicalConnection(ip, port, protocol);
			connectionsMap.put(key, conn);
		}
		if (logger.isTraceEnabled()) {
			logger.trace("getConnection end");
		}
		return conn;
	}
}
