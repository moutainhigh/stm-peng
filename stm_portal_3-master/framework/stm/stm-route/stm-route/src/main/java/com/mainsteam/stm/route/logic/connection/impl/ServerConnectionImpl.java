/**
 * 
 */
package com.mainsteam.stm.route.logic.connection.impl;

import java.io.IOException;

import org.xsocket.connection.multiplexed.INonBlockingPipeline;

import com.mainsteam.stm.route.logic.LogicAppEnum;
import com.mainsteam.stm.route.logic.LogicServer;
import com.mainsteam.stm.route.logic.connection.ServerConnection;

/**
 * @author ziw
 * 
 */
public class ServerConnectionImpl extends LogicConnectionImpl implements
		ServerConnection {

	private String fromIp;

	private int fromPort;

	private LogicServer server;

	/**
	 * @param id
	 * @param nonBlockingPipeline
	 * @param blockingPipeline
	 * @param appEnum
	 * @throws IOException
	 */
	public ServerConnectionImpl(String id,
			INonBlockingPipeline nonBlockingPipeline, LogicAppEnum appEnum)
			throws IOException {
		super(id, nonBlockingPipeline, appEnum);
	}

	/**
	 * @return the server
	 */
	public final LogicServer getServer() {
		return server;
	}

	/**
	 * @param server
	 *            the server to set
	 */
	public final void setServer(LogicServer server) {
		this.server = server;
	}

	/**
	 * @return the fromIp
	 */
	public final String getFromIp() {
		return fromIp;
	}

	/**
	 * @param fromIp
	 *            the fromIp to set
	 */
	public final void setFromIp(String fromIp) {
		this.fromIp = fromIp;
	}

	/**
	 * @return the fromPort
	 */
	public final int getFromPort() {
		return fromPort;
	}

	/**
	 * @param fromPort
	 *            the fromPort to set
	 */
	public final void setFromPort(int fromPort) {
		this.fromPort = fromPort;
	}

	public void close() {
		super.close();
		if (this.server != null) {
			this.server.removeServerConnection(this);
		}
	}
}
