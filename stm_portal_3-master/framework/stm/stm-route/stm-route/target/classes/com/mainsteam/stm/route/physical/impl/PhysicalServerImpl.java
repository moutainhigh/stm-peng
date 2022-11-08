/**
 * 
 */
package com.mainsteam.stm.route.physical.impl;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.net.Socket;
import java.net.SocketAddress;
import java.nio.BufferUnderflowException;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.xsocket.MaxReadSizeExceededException;
import org.xsocket.connection.IServer;
import org.xsocket.connection.Server;
import org.xsocket.connection.multiplexed.INonBlockingPipeline;
import org.xsocket.connection.multiplexed.IPipelineConnectHandler;
import org.xsocket.connection.multiplexed.IPipelineDisconnectHandler;
import org.xsocket.connection.multiplexed.MultiplexedProtocolAdapter;

import com.mainsteam.stm.route.RouteEntrySupporter;
import com.mainsteam.stm.route.logic.impl.LogicServerImpl;
import com.mainsteam.stm.route.physical.PhsicalConnectionManager;
import com.mainsteam.stm.route.physical.PhysicalServer;
import com.mainsteam.stm.route.physical.connection.impl.PhsicalServerConnectionAcceptor;

/**
 * @author ziw
 * 
 */
public class PhysicalServerImpl implements PhysicalServer,
		IPipelineConnectHandler, IPipelineDisconnectHandler {

	private static final Log logger = LogFactory
			.getLog(PhysicalServerImpl.class);

	private String listenIp;
	private int listenPort;
	private boolean started;
	private IServer server;

	private RouteEntrySupporter supporter;

	private PhsicalConnectionManager connectionManager;

	private LogicServerImpl logicServer;

	/**
	 * 
	 */
	public PhysicalServerImpl() {
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

	/**
	 * @param logicServer
	 *            the logicServer to set
	 */
	public final void setLogicServer(LogicServerImpl logicServer) {
		this.logicServer = logicServer;
	}

	@Override
	public synchronized boolean setConfig(String listenIp, int listenPort) {
		if (started) {
			return false;
		}
		this.listenIp = listenIp;
		this.listenPort = listenPort;
		return true;
	}

	@Override
	public void stopServer() throws IOException {
		if (logger.isInfoEnabled()) {
			logger.info("stopServer ");
		}
		server.close();
		started = false;
	}

	@Override
	public void startServer() throws IOException {
		if (System.getProperty("testCase") != null) {
			return;
		}

		if (logger.isInfoEnabled()) {
			StringBuilder b = new StringBuilder();
			b.append("startServer... at ").append(listenIp).append(':')
					.append(listenPort);
			logger.info(b.toString());
		}
		/**
		 * 验证端口是否已经被占用
		 */
		boolean used = false;
		try {
			SocketAddress endpoint = new InetSocketAddress(listenIp, listenPort);
			Socket s = new Socket();
			s.connect(endpoint);
			s.close();
			used = true;
		} catch (IOException e) {
		}
		if (used) {
			throw new IOException(listenIp + ":" + listenPort
					+ " has been used.stop to bind it.");
		}
		// InetAddress address = InetAddress.getByName(listenIp);
		MultiplexedProtocolAdapter adapter = new MultiplexedProtocolAdapter(
				this);
		// Map<String, Object> options = new HashMap<String, Object>();
		// options.put(NonBlockingConnection.SO_RCVBUF,
		// SocketConstant.socketBufferSize*100);
		// server = new Server(listenIp, listenPort, adapter);
		server = new Server(listenPort, adapter);
		server.start();
		started = true;
		((LogicServerImpl) (logicServer)).setServerHost(getServerHost());
		StringBuilder b = new StringBuilder();
		b.append("START SERVER REMOTE:").append(listenIp).append(':')
				.append(listenPort);
		String startInfo = b.toString();
		System.out.println(startInfo);
		if (logger.isInfoEnabled()) {
			logger.info(startInfo);
		}
	}

	@Override
	public boolean onConnect(INonBlockingPipeline conn) throws IOException,
			BufferUnderflowException, MaxReadSizeExceededException {
		if (logger.isInfoEnabled()) {
			logger.info("onConnect receive one connect request from="
					+ conn.getRemoteAddress() + ":" + conn.getRemotePort()
					+ " connId=" + conn.getId());
		}
		PhsicalServerConnectionAcceptor acceptor = new PhsicalServerConnectionAcceptor(
				this.supporter, this.connectionManager, this.logicServer,
				this.listenIp, this.listenPort);
		conn.setHandler(acceptor);
		return true;
	}

	@Override
	public boolean onDisconnect(INonBlockingPipeline conn) throws IOException {
		if (logger.isInfoEnabled()) {
			logger.info("onDisconnect physical connection close from "
					+ conn.getRemoteAddress() + ":" + conn.getRemotePort()
					+ " connId=" + conn.getId());
		}
		return false;
	}

	@Override
	public InetSocketAddress getServerHost() {
		InetSocketAddress address = new InetSocketAddress(this.listenIp,
				this.listenPort);
		return address;
	}
}
