/**
 * 
 */
package com.mainsteam.stm.route.physical.connection.impl;

import java.io.IOException;
import java.nio.BufferUnderflowException;
import java.nio.channels.ClosedChannelException;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.xsocket.MaxReadSizeExceededException;
import org.xsocket.connection.IDataHandler;
import org.xsocket.connection.INonBlockingConnection;
import org.xsocket.connection.multiplexed.INonBlockingPipeline;
import org.xsocket.connection.multiplexed.IPipelineDisconnectHandler;

import com.mainsteam.stm.route.connection.ConnectionProtocol;
import com.mainsteam.stm.route.logic.LogicAppEnum;
import com.mainsteam.stm.route.physical.PhsicalConnectionManager;
import com.mainsteam.stm.route.physical.connection.ProxyConnection;

/**
 * @author ziw
 * 
 */
public class ProxyConnectionImpl implements ProxyConnection, IDataHandler,
		IPipelineDisconnectHandler {

	private static final Log logger = LogFactory
			.getLog(ProxyConnectionImpl.class);

	private String distIp;
	private int distPort;
	private String srcIp;
	private int srcPort;
	private ConnectionProtocol protocol;
	private INonBlockingPipeline fromPipeline;
	private INonBlockingPipeline toPipeline;

	/**
	 * 
	 */
	public ProxyConnectionImpl(INonBlockingPipeline fromPipeline,
			ConnectionProtocol protocol) {
		this.fromPipeline = fromPipeline;
		this.protocol = protocol;
	}

	/**
	 * @return the distIp
	 */
	public final String getDistIp() {
		return distIp;
	}

	/**
	 * @param distIp
	 *            the distIp to set
	 */
	public final void setDistIp(String distIp) {
		this.distIp = distIp;
	}

	/**
	 * @return the distPort
	 */
	public final int getDistPort() {
		return distPort;
	}

	/**
	 * @param distPort
	 *            the distPort to set
	 */
	public final void setDistPort(int distPort) {
		this.distPort = distPort;
	}

	/**
	 * @return the srcIp
	 */
	public final String getSrcIp() {
		return srcIp;
	}

	/**
	 * @param srcIp
	 *            the srcIp to set
	 */
	public final void setSrcIp(String srcIp) {
		this.srcIp = srcIp;
	}

	/**
	 * @return the srcPort
	 */
	public final int getSrcPort() {
		return srcPort;
	}

	/**
	 * @param srcPort
	 *            the srcPort to set
	 */
	public final void setSrcPort(int srcPort) {
		this.srcPort = srcPort;
	}

	public void connect(PhsicalConnectionManager manager, LogicAppEnum appEnum)
			throws IOException {
		if (toPipeline == null) {
			if (logger.isInfoEnabled()) {
				logger.info("connect start.appEnum" + appEnum);
			}
			try {
				PhysicalConnectionImpl connectionImpl = (PhysicalConnectionImpl) manager
						.getConnection(distIp, distPort, protocol, appEnum);
				if (logger.isInfoEnabled()) {
					logger.info("connect get connection ok.");
				}
				this.toPipeline = connectionImpl.createNonBlockingPipeline();
				this.toPipeline.setHandler(this);
				if (logger.isInfoEnabled()) {
					StringBuilder b = new StringBuilder(
							"connect createNonBlockingPipeline ok.");
					b.append(" fromConnId=").append(fromPipeline.getId());
					b.append(" toConnId=").append(toPipeline.getId());
					logger.info(b.toString());
				}
			} catch (IOException e) {
				if (logger.isErrorEnabled()) {
					logger.error("connect to create proxy connection fail.", e);
				}
				throw e;
			}
			if (logger.isDebugEnabled()) {
				logger.debug("connect setHandler ok.");
			}
			/**
			 * 将当前信息转发给目的地
			 */
			// onData(this.fromPipeline);
		}
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.xsocket.connection.IDataHandler#onData(org.xsocket.connection.
	 * INonBlockingConnection)
	 */
	@Override
	public boolean onData(INonBlockingConnection conn) throws IOException,
			BufferUnderflowException, ClosedChannelException,
			MaxReadSizeExceededException {
		if (conn == this.fromPipeline) {
			this.toPipeline.transferFrom(this.fromPipeline);
			this.toPipeline.flush();
		} else if (conn == this.toPipeline) {
			this.fromPipeline.transferFrom(toPipeline);
			this.fromPipeline.flush();
		}
		return true;
	}

	@Override
	public synchronized boolean onDisconnect(INonBlockingPipeline pipeline)
			throws IOException {
		if (this.fromPipeline == null || this.toPipeline == null) {
			if (logger.isErrorEnabled()) {
				logger.error("onDisconnect this proxy connection has been droped already.");
			}
			return false;
		}
		if (logger.isInfoEnabled()) {
			StringBuilder b = new StringBuilder(
					"onDisconnect pipeline is disconnect,close the proxy connection.");
			b.append(pipeline.getRemoteAddress()).append(':')
					.append(pipeline.getRemotePort());
			b.append(" cause connId=").append(pipeline.getId());
			b.append(" fromConnId=").append(this.fromPipeline.getId());
			b.append(" toConnId=").append(this.toPipeline.getId());
			logger.info(b.toString());
		}
		if (pipeline == this.fromPipeline) {
			if (this.toPipeline.isOpen()) {
				this.toPipeline.close();
			}
		} else if (pipeline == this.toPipeline) {
			if (this.fromPipeline.isOpen()) {
				this.fromPipeline.close();
			}
		}
		this.toPipeline = null;
		this.fromPipeline = null;
		return false;
	}
}
