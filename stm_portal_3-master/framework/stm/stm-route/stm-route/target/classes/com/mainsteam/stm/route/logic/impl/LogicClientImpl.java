/**
 * 
 */
package com.mainsteam.stm.route.logic.impl;

import java.io.IOException;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import com.mainsteam.stm.route.RouteEntry;
import com.mainsteam.stm.route.RouteEntrySupporter;
import com.mainsteam.stm.route.connection.ConnectionProtocol;
import com.mainsteam.stm.route.logic.LogicAppEnum;
import com.mainsteam.stm.route.logic.LogicClient;
import com.mainsteam.stm.route.logic.LogicConnection;
import com.mainsteam.stm.route.physical.PhsicalConnectionManager;
import com.mainsteam.stm.route.physical.connection.PhysicalConnection;

/**
 * @author ziw
 * 
 */
public class LogicClientImpl implements LogicClient {

	private static final Log logger = LogFactory.getLog(LogicClient.class);

	private PhsicalConnectionManager connectionManager;

	private RouteEntrySupporter supporter;

	/**
	 * 
	 */
	public LogicClientImpl() {
	}

	/**
	 * @param supporter
	 *            the supporter to set
	 */
	public final void setSupporter(RouteEntrySupporter supporter) {
		this.supporter = supporter;
	}

	/**
	 * @param connectionManager
	 *            the connectionManager to set
	 */
	public final void setConnectionManager(
			PhsicalConnectionManager connectionManager) {
		this.connectionManager = connectionManager;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * com.mainsteam.stm.route.logic.LogicClient#createConection(java.lang.
	 * String, int, com.mainsteam.stm.route.logic.LogicAppEnum)
	 */
	@Override
	public LogicConnection createConection(String ip, int port,
			LogicAppEnum appEnum) throws IOException {
		if (logger.isTraceEnabled()) {
			logger.trace("createConection start");
		}
		ConnectionProtocol protocol = null;
		if (appEnum == LogicAppEnum.TRANSFER_UDP) {
			protocol = ConnectionProtocol.UDP;
		} else {
			protocol = ConnectionProtocol.TCP;
		}
		/**
		 * 根据路径，找到其直接节点的ip和port
		 */
		String nextIp = null;
		int nextPort = -1;
		if (supporter != null) {
			RouteEntry entry = supporter.getNextIp(ip, port, appEnum);
			if (entry == null) {
				nextIp = ip;
				nextPort = port;
			} else {
				nextIp = entry.getIp();
				nextPort = entry.getPort();
			}
		} else {
			if (logger.isWarnEnabled()) {
				logger.warn("createConection RouteEntrySupporter is null.");
			}
			nextIp = ip;
			nextPort = port;
		}
		PhysicalConnection physicalConnection = connectionManager
				.getConnection(nextIp, nextPort, protocol, appEnum);
		LogicConnection clientConection = physicalConnection
				.createLogicConnection(appEnum, ip, port);
		if (logger.isTraceEnabled()) {
			logger.trace("createConection end");
		}
		return clientConection;
	}
}
