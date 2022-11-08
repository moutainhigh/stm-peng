/**
 * 
 */
package com.mainsteam.stm.route.physical.connection.impl;

import java.io.IOException;
import java.net.Inet4Address;
import java.net.InetAddress;
import java.net.InetSocketAddress;
import java.net.SocketTimeoutException;
import java.util.HashMap;
import java.util.Map;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.xsocket.connection.IConnectExceptionHandler;
import org.xsocket.connection.IDisconnectHandler;
import org.xsocket.connection.INonBlockingConnection;
import org.xsocket.connection.NonBlockingConnection;
import org.xsocket.connection.multiplexed.IBlockingPipeline;
import org.xsocket.connection.multiplexed.IMultiplexedConnection;
import org.xsocket.connection.multiplexed.INonBlockingPipeline;
import org.xsocket.connection.multiplexed.MultiplexedConnection;

import com.mainsteam.stm.route.logic.LogicAppEnum;
import com.mainsteam.stm.route.logic.LogicConnection;
import com.mainsteam.stm.route.logic.connection.impl.ClientConnectionImpl;
import com.mainsteam.stm.route.physical.connection.PhysicalConnection;

/**
 * @author ziw
 * 
 */
public class PhysicalConnectionImpl implements PhysicalConnection, IDisconnectHandler, IConnectExceptionHandler {

	private static final Log logger = LogFactory.getLog(PhysicalConnectionImpl.class);

	private int connTimeout = 30000;

	private int readTimeout = 3600000;

	private INonBlockingConnection nativeCon;

	private InetAddress address;

	private int port;

	private IMultiplexedConnection multiplexedCon;

	private String id;

	private boolean isClosed = false;

	/**
	 * 
	 */
	public PhysicalConnectionImpl(InetAddress address, int port) {
		this.port = port;
		this.address = address;
	}

	public PhysicalConnectionImpl(IMultiplexedConnection multiplexedCon, INonBlockingConnection nativeCon) {
		this.multiplexedCon = multiplexedCon;
	}

	public void connect() throws IOException {
		if (nativeCon == null) {
			try {
				Map<String, Object> options = new HashMap<String, Object>();
				options.put(NonBlockingConnection.SO_TIMEOUT, Integer.valueOf(readTimeout));
				// options.put(NonBlockingConnection.SO_RCVBUF,
				// SocketConstant.socketBufferSize);
				// options.put(NonBlockingConnection.SO_SNDBUF,
				// SocketConstant.socketBufferSize);
				nativeCon = new NonBlockingConnection(new InetSocketAddress(address, port), null, this, true,
						connTimeout, options, null, false);
				logger.info("connect to address=" + address + " port=" + port + " connId=" + nativeCon.getId());
				multiplexedCon = new MultiplexedConnection(nativeCon);
				this.id = multiplexedCon.getId();
			} catch (IOException e) {
				if (logger.isErrorEnabled()) {
					logger.error("connect", e);
				}
				throw e;
			}
		}
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * com.mainsteam.stm.route.physical.connection.PhysicalConnection#isValid()
	 */
	@Override
	public boolean isValid() {
		return isClosed == false && this.nativeCon.isOpen();
	}

	@Override
	public String getId() {
		return this.id;
	}

	public IBlockingPipeline getBlockingPipeline(String logicConnectionId) {
		try {
			return multiplexedCon.getBlockingPipeline(logicConnectionId);
		} catch (IOException e) {
			if (logger.isErrorEnabled()) {
				logger.error("getBlockingPipeline", e);
			}
		}
		return null;
	}

	public INonBlockingPipeline getNonBlockingPipeline(String logicConnectionId) {
		try {
			return multiplexedCon.getNonBlockingPipeline(logicConnectionId);
		} catch (IOException e) {
			if (logger.isErrorEnabled()) {
				logger.error("getBlockingPipeline", e);
			}
		}
		return null;
	}

	public INonBlockingPipeline createNonBlockingPipeline() throws IOException {
		String connetionId = multiplexedCon.createPipeline();
		return multiplexedCon.getNonBlockingPipeline(connetionId);
	}

	@Override
	public synchronized LogicConnection createLogicConnection(LogicAppEnum appEnum, String distIp, int distPort)
			throws IOException {
		if (logger.isInfoEnabled()) {
			StringBuilder b = new StringBuilder("createLogicConnection start distIp=");
			b.append(distIp).append(" distPort=").append(distPort).append(" appEnum=").append(appEnum);
			logger.info(b.toString());
		}
		if (!this.nativeCon.isOpen()) {
			this.nativeCon.close();
			throw new IOException("Physical Connection has not valid.");
		}
		INonBlockingPipeline blockingPipeline = createNonBlockingPipeline();
		if (logger.isInfoEnabled()) {
			StringBuilder b = new StringBuilder("createLogicConnection connId=");
			b.append(blockingPipeline.getId());
			logger.info(b.toString());
		}
		ClientConnectionImpl clientConnection = new ClientConnectionImpl(blockingPipeline.getId(), blockingPipeline,
				appEnum);
		clientConnection.setDistIp(distIp);
		clientConnection.setDistPort(distPort);
		clientConnection.setSrcIp(InetAddress.getLocalHost().getHostAddress());
		clientConnection.setSrcPort(this.nativeCon.getLocalPort());
		try {
			clientConnection.connect();
		} catch (Exception e) {
			if (logger.isErrorEnabled()) {
				logger.error("createLogicConnection", e);
			}
			blockingPipeline.close();
			throw new RuntimeException("can't connect to server[" + distIp + ":" + distPort + "]", e);
		}
		if (logger.isTraceEnabled()) {
			logger.trace("createLogicConnection end");
		}
		return clientConnection;
	}

	@Override
	public boolean onDisconnect(INonBlockingConnection conn) throws IOException {
		if (logger.isWarnEnabled()) {
			StringBuilder b = new StringBuilder("onDisconnect physical connection is closed from ");
			b.append(conn.getRemoteAddress()).append(':').append(conn.getRemotePort());
			b.append(" on this server at ").append(conn.getLocalAddress().getHostAddress()).append(':')
					.append(conn.getLocalPort());
			logger.warn(b.toString());
		}
		isClosed = true;
		this.nativeCon.close();
		return false;
	}

	@Override
	public synchronized void close() throws IOException {
		if (this.nativeCon != null) {
			this.nativeCon.close();
			this.multiplexedCon.close();
			this.nativeCon = null;
			this.multiplexedCon = null;
			isClosed = true;
		}
	}

	@Override
	public boolean onConnectException(INonBlockingConnection connection, IOException ioe) throws IOException {
		if (logger.isInfoEnabled()) {
			logger.info("onConnectException "+this.address+":"+this.port+" "+ioe);
		}
		connection.close();
		return false;
	}
	
	public static void main(String[] args) throws InterruptedException, IOException {
		new PhysicalConnectionImpl(Inet4Address.getByName("127.0.0.1"), 5000).connect();
		Thread.sleep(3000000);
	}
}
