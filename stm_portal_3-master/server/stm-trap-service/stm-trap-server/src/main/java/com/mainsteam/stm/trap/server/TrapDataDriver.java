/**
 * 
 */
package com.mainsteam.stm.trap.server;

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetSocketAddress;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

/** 
 * @author 作者：ziw
 * @date 创建时间：2017年11月20日 上午10:53:35
 * @version 1.0
 */
/** 
 */
public class TrapDataDriver {
	private static final Log logger = LogFactory.getLog(TrapDataDriver.class);

	private int listenPort;
	private TrapDataHandler[] trapDataHandlers;
	private DatagramSocket endpoint;

	public TrapDataDriver(int listenPort, TrapDataHandler[] trapDataHandlers) {
		super();
		this.listenPort = listenPort;
		this.trapDataHandlers = trapDataHandlers;
	}

	public int getListenPort() {
		return listenPort;
	}

	public TrapDataHandler[] getTrapDataHandlers() {
		return trapDataHandlers;
	}

	private void close() {
		endpoint.close();
	}

	public void start() {
		final byte[] buffer = new byte[65536];
		try {
			endpoint = new DatagramSocket(listenPort);
			endpoint.setReceiveBufferSize(buffer.length * 100);
			if (logger.isInfoEnabled()) {
				logger.info("trap listen at " + listenPort);
			}
			new Thread(new Runnable() {
				@Override
				public void run() {
					while (true) {
						DatagramPacket packet = new DatagramPacket(buffer, buffer.length);
						try {
							endpoint.receive(packet);
						} catch (IOException e) {
							if (logger.isErrorEnabled()) {
								logger.error("start", e);
							}
						}
						byte[] msg = packet.getData();
						int length = packet.getLength();
						if (length > 0) {
							byte[] content = new byte[length];
							System.arraycopy(msg, 0, content, 0, length);
							for (final TrapDataHandler handler : trapDataHandlers) {
								handler.handleData(
										((InetSocketAddress) packet.getSocketAddress()).getAddress().getHostAddress(),
										content);
							}
						}
					}
				}
			}, "trap-listen-" + listenPort).start();
		} catch (IOException e) {
			if (logger.isErrorEnabled()) {
				logger.error("start trap endpoint " + listenPort + " failed.", e);
			}
		}
	}

	public void stop() {
		close();
	}
}
