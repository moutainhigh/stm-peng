/**
 * 
 */
package com.mainsteam.stm.route.physical.impl;

import java.io.IOException;
import java.net.InetAddress;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import com.mainsteam.stm.route.connection.ConnectionProtocol;
import com.mainsteam.stm.route.physical.PhysicalClient;
import com.mainsteam.stm.route.physical.connection.PhysicalConnection;
import com.mainsteam.stm.route.physical.connection.impl.PhysicalConnectionImpl;

/**
 * @author ziw
 * 
 */
public class PhysicalClientImpl implements PhysicalClient {

	private static final Log logger = LogFactory
			.getLog(PhysicalClientImpl.class);

	/**
	 * 
	 */
	public PhysicalClientImpl() {
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * com.mainsteam.stm.route.physical.PhysicalClient#createPhysicalConnection
	 * (java.lang.String, int,
	 * com.mainsteam.stm.route.connection.ConnectionProtocol)
	 */
	@Override
	public PhysicalConnection createPhysicalConnection(String ip, int port,
			ConnectionProtocol protocol) throws IOException {
		if (logger.isInfoEnabled()) {
			logger.info("new PhysicalConnection protocol=" + protocol);
		}
		if (ConnectionProtocol.TCP == protocol) {
			PhysicalConnectionImpl connectionImpl = new PhysicalConnectionImpl(
					InetAddress.getByName(ip), port);
			if (logger.isInfoEnabled()) {
				logger.info("new PhysicalConnection end.connect start.");
			}
			connectionImpl.connect();
			return connectionImpl;
		} else {
			throw new IOException("Not support this protocol.protocol="
					+ protocol);
		}
	}
}
