/**
 * 
 */
package com.mainsteam.stm.transfer;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import com.mainsteam.stm.route.logic.LogicAppEnum;
import com.mainsteam.stm.route.logic.LogicServer;
import com.mainsteam.stm.route.logic.connection.ServerConnection;

/**
 * @author ziw
 * 
 */
public class MetricDataTransferServer implements Runnable {

	private static final Log logger = LogFactory
			.getLog(MetricDataTransferServer.class);

	private LogicServer logicServer;

	private TransferDataDispatcher dataDispatcher;

	private boolean started = false;


	/**
	 * @param logicServer the logicServer to set
	 */
	public final void setLogicServer(LogicServer logicServer) {
		this.logicServer = logicServer;
	}

	/**
	 * @param dataDispatcher
	 *            the dataDispatcher to set
	 */
	public final void setDataDispatcher(TransferDataDispatcher dataDispatcher) {
		this.dataDispatcher = dataDispatcher;
	}

	/**
	 * @return the started
	 */
	public final boolean isStarted() {
		return started;
	}

	/**
	 * 
	 */
	public MetricDataTransferServer() {
	}

	public synchronized void start() {
		if (logger.isInfoEnabled()) {
			logger.info("Metric DataTransfer Server start");
		}
		if (started) {
			return;
		}
		logicServer.startServer(LogicAppEnum.TRANSFER_TCP);
		started = true;
		new Thread(this, "MetricDataTransferServer").start();
		if (logger.isInfoEnabled()) {
			logger.info("Metric DataTransfer Server end");
		}
	}

	@Override
	public void run() {
		while (started) {
			ServerConnection connection = logicServer.accept(LogicAppEnum.TRANSFER_TCP);
			String srcIp = connection.getSrcIp();
			int srcPort = connection.getSrcPort();
			LogicAppEnum appEnum = connection.getConnectionApp();
			
			StringBuilder b = new StringBuilder();
			b.append("TransferDataHandler-").append(srcIp).append(':').append(srcPort).append('.').append(appEnum);
			
			TransferDataReceiver dataHandler = new TransferDataReceiver(connection, this, dataDispatcher);
			new Thread(dataHandler, b.toString()).start();
			if (logger.isInfoEnabled()) {
				logger.info("accept on transfer connection from "+ b.toString());
			}
		}
	}
}
