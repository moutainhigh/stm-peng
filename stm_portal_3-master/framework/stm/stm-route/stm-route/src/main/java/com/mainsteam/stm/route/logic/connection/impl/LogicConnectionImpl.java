/**
 * 
 */
package com.mainsteam.stm.route.logic.connection.impl;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.xsocket.connection.multiplexed.INonBlockingPipeline;

import com.mainsteam.stm.route.logic.LogicAppEnum;
import com.mainsteam.stm.route.logic.LogicConnection;
import com.mainsteam.stm.route.logic.connection.ClientConection;

/**
 * @author ziw
 * 
 */
public class LogicConnectionImpl implements ClientConection {

	private static final Log logger = LogFactory.getLog(LogicConnection.class);

	private String id;

	private LogicAppEnum appEnum;

	private PipelineInputStream in;

	private INonBlockingPipeline nonBlockingPipeline;

	private OutputStream out;

	private String distIp;

	private int distPort;

	private String srcIp;

	private int srcPort;

	/**
	 * @throws IOException
	 * 
	 */
	public LogicConnectionImpl(String id,
			INonBlockingPipeline nonBlockingPipeline, LogicAppEnum appEnum)
			throws IOException {
		this.in = new PipelineInputStream();
		this.in.setConnection(this);
		nonBlockingPipeline.setHandler(this.in);
		this.nonBlockingPipeline = nonBlockingPipeline;
		this.out = new PipelineOutputStream(nonBlockingPipeline);
		this.appEnum = appEnum;
		this.id = id;
		if (logger.isInfoEnabled()) {
			logger.info("LogicConnection is connected.connId="
					+ nonBlockingPipeline.getId());
		}
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

	/**
	 * @return the distIp
	 */
	public final String getDistIp() {
		return distIp;
	}

	/**
	 * @return the id
	 */
	public final String getId() {
		return id;
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

	/*
	 * (non-Javadoc)
	 * 
	 * @see com.mainsteam.stm.route.logic.LogicConnection#getConnectionApp()
	 */
	@Override
	public LogicAppEnum getConnectionApp() {
		return appEnum;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see com.mainsteam.stm.route.logic.LogicConnection#isValid()
	 */
	@Override
	public boolean isValid() {
		return this.in.isClosed() == false;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see com.mainsteam.stm.route.logic.LogicConnection#close()
	 */
	@Override
	public synchronized void close() {
		if (this.nonBlockingPipeline != null && logger.isInfoEnabled()) {
			logger.info("close connId=" + this.nonBlockingPipeline.getId());
		}
		if (this.out != null) {
			try {
				this.out.flush();
			} catch (IOException e) {
			}
			try {
				this.out.close();
			} catch (IOException e) {
			}
		}
		try {
			this.in.close();
		} catch (IOException e) {
		}
		try {
			this.nonBlockingPipeline.close();
		} catch (IOException e) {
		}
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see com.mainsteam.stm.route.logic.LogicConnection#getInputStream()
	 */
	@Override
	public InputStream getInputStream() {
		return this.in;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see com.mainsteam.stm.route.logic.LogicConnection#getOutputStream()
	 */
	@Override
	public OutputStream getOutputStream() {
		return this.out;
	}
}
