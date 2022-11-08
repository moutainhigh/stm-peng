/**
 * 
 */
package com.mainsteam.stm.route.physical.connection.impl;

import java.io.IOException;
import java.nio.BufferUnderflowException;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.xsocket.MaxReadSizeExceededException;
import org.xsocket.connection.multiplexed.INonBlockingPipeline;
import org.xsocket.connection.multiplexed.IPipelineConnectHandler;
import org.xsocket.connection.multiplexed.IPipelineDataHandler;
import org.xsocket.connection.multiplexed.IPipelineDisconnectHandler;

import com.mainsteam.stm.route.RouteEntry;
import com.mainsteam.stm.route.RouteEntrySupporter;
import com.mainsteam.stm.route.connection.ConnectHeader;
import com.mainsteam.stm.route.connection.ConnectionProtocol;
import com.mainsteam.stm.route.logic.LogicAppEnum;
import com.mainsteam.stm.route.logic.connection.impl.ServerConnectionImpl;
import com.mainsteam.stm.route.logic.impl.LogicServerImpl;
import com.mainsteam.stm.route.physical.PhsicalConnectionManager;
import com.mainsteam.stm.route.util.IpUtil;

/**
 * @author ziw
 * 
 */
public class PhsicalServerConnectionAcceptor
		implements IPipelineDataHandler, IPipelineConnectHandler, IPipelineDisconnectHandler {

	private static final Log logger = LogFactory.getLog(PhsicalServerConnectionAcceptor.class);

	private RouteEntrySupporter supporter;

	private PhsicalConnectionManager connectionManager;

	private LogicServerImpl logicServer;

	private String serverIp;

	private int serverPort;

	private int leftDataLength = -1;

	private ConnectHeader header;

	/**
	 * 
	 */
	public PhsicalServerConnectionAcceptor(RouteEntrySupporter supporter, PhsicalConnectionManager connectionManager,
			LogicServerImpl logicServer, String serverIp, int serverPort) {
		this.supporter = supporter;
		this.connectionManager = connectionManager;
		this.logicServer = logicServer;
		this.serverIp = serverIp;
		this.serverPort = serverPort;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.xsocket.connection.multiplexed.IPipelineDataHandler#onData(org.
	 * xsocket .connection.multiplexed.INonBlockingPipeline)
	 */
	@Override
	public boolean onData(INonBlockingPipeline pipeline)
			throws IOException, BufferUnderflowException, MaxReadSizeExceededException {
		if (logger.isInfoEnabled()) {
			logger.info("Accept logic connection start.connId=" + pipeline.getId());
		}
		boolean isReady = false;
		/**
		 * 处理连接创建逻辑
		 */
		try {
			int availSize = pipeline.available();
			if (leftDataLength < 0) {
				int headSize = ConnectHeader.getHeadSize();
				if (availSize >= headSize) {
					byte[] headContent = pipeline.readBytesByLength(headSize);
					header = ConnectHeader.readHeader(headContent);
					if (availSize >= (headSize + header.getDataLength())) {
						isReady = true;
					} else {
						leftDataLength = header.getDataLength() - (availSize - headSize);
					}
				}
			} else {
				leftDataLength = header.getDataLength() - availSize;
				if (leftDataLength <= 0) {
					isReady = true;
				}
			}
			if (isReady) {
				if (header.getOpertion() == ConnectHeader.OPERATION_CREATE_CONNECTION) {
					byte app = header.getApp();
					LogicAppEnum appEnum = LogicAppEnum.valueOf(app);
					/* 创建连接 */
					byte[] content = pipeline.readBytesByLength(header.getDataLength());
					Object[] ipAddress = IpUtil.getAddress(content);
					String srcIp = (String) ipAddress[0];
					int srcPort = (Integer) ipAddress[1];

					String distIp = (String) ipAddress[2];
					int distPort = (Integer) ipAddress[3];

					/**
					 * 查找下一跳
					 */
					RouteEntry entry = null;
					if (supporter != null) {
						entry = supporter.getNextIp(distIp, distPort, appEnum);
					} else {
						if (logger.isWarnEnabled()) {
							logger.warn("onData RouteEntrySupporter is null.");
						}
					}
					if (entry != null) {
						if (logger.isInfoEnabled()) {
							logger.info("createConnection route to " + entry.getIp() + ":" + entry.getPort());
						}
						pipeline.resetToReadMark();
						ProxyConnectionImpl proxyConnectionImpl = new ProxyConnectionImpl(pipeline,
								ConnectionProtocol.TCP);
						proxyConnectionImpl.setDistIp(entry.getIp());
						proxyConnectionImpl.setDistPort(entry.getPort());
						proxyConnectionImpl.setSrcIp(srcIp);
						proxyConnectionImpl.setSrcPort(srcPort);
						if (logger.isInfoEnabled()) {
							logger.info("create proxy connection to connection.");
						}
						proxyConnectionImpl.connect(this.connectionManager, appEnum);
						pipeline.setHandler(proxyConnectionImpl);
						proxyConnectionImpl.onData(pipeline);
						if (logger.isInfoEnabled()) {
							logger.info("create proxy connection ok.");
						}
					} else {
						if (logger.isInfoEnabled()) {
							logger.info("createConnection to me. from " + srcIp + ":" + srcPort);
						}
						pipeline.removeReadMark();
						String connectionId = pipeline.getId();
						/**
						 * 如果是当前服务，需要将当前连接注入到Logicserver
						 * 
						 */
						ServerConnectionImpl connection = new ServerConnectionImpl(connectionId, pipeline, appEnum);
						connection.setSrcIp(srcIp);
						connection.setSrcPort(srcPort);

						// modify by ziw at 2017年7月27日 上午10:13:12 -- 适应隔离墙机制
						// /**
						// * 回写创建连接成功
						// */
						// header.setOpertion(ConnectHeader.OPERATION_CREATE_CONNECTION_RESPONSE);
						// byte[] headerBytes = header.toBytes();
						// byte[] ipBytes = IpUtil.makeBytes(srcIp, srcPort,
						// this.serverIp, this.serverPort);
						// byte[] address = new byte[headerBytes.length
						// + ipBytes.length];
						// System.arraycopy(headerBytes, 0, address, 0,
						// headerBytes.length);
						// System.arraycopy(ipBytes, 0, address,
						// headerBytes.length, ipBytes.length);
						// pipeline.write(address);
						// pipeline.flush();

						this.logicServer.addLoginConnection(connection);
					}
					destory();
					if (logger.isInfoEnabled()) {
						logger.info("Accept connection ok.");
					}
				}
				return true;
			}
		} catch (Exception e) {
			if (logger.isErrorEnabled()) {
				logger.error("onData", e);
			}
			pipeline.close();
			throw new IOException(e);
		}
		if (logger.isTraceEnabled()) {
			logger.trace("Accept connection end");
		}
		return false;
	}

	private void destory() {
		/**
		 * 该对象生命周期结束，清空属性
		 */
		this.connectionManager = null;
		this.supporter = null;
		this.logicServer = null;
	}

	@Override
	public boolean onConnect(INonBlockingPipeline pipeline)
			throws IOException, BufferUnderflowException, MaxReadSizeExceededException {
		if (logger.isInfoEnabled()) {
			logger.info("create connection start. connId=" + pipeline.getId());
		}
		pipeline.markReadPosition();
		return true;
	}

	@Override
	public boolean onDisconnect(INonBlockingPipeline pipeline) throws IOException {
		if (logger.isInfoEnabled()) {
			logger.info("onDisconnect connection is disconnect from=" + pipeline.getRemoteAddress() + ":"
					+ pipeline.getRemotePort() + " connId=" + pipeline.getId());
		}
		return false;
	}
}
