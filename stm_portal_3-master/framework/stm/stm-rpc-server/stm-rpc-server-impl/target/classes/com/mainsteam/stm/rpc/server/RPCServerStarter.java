/**
 * 
 */
package com.mainsteam.stm.rpc.server;

import java.io.IOException;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

/**
 * @author ziw
 * 
 */
public class RPCServerStarter {

	private static final Log logger = LogFactory.getLog(RPCServerStarter.class);

	private OCRPCServer server;

	/**
	 * @param server
	 *            the server to set
	 */
	public final void setServer(OCRPCServer server) {
		this.server = server;
	}

	/**
	 * 
	 */
	public RPCServerStarter() {
	}

	public void start() throws IOException {
		String ignoreJmx = System.getProperty("IgnoreJmx");
		System.out.println("ignoreJmx=" + ignoreJmx);
		if ("true".equals(ignoreJmx)) {
			if (logger.isInfoEnabled()) {
				logger.info("start jmx server  is ignored.");
			}
			return;
		}
		server.startServer();
	}

	public static void main(String[] args) {
		RPCServerStarter r = new RPCServerStarter();
		try {
			r.start();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
}
