package com.mainsteam.stm.route;

import java.io.IOException;
import java.net.InetAddress;
import java.net.MalformedURLException;
import java.util.HashMap;
import java.util.Map;

import javax.management.remote.JMXServiceURL;
import javax.management.remote.generic.MessageConnection;
import javax.management.remote.generic.MessageConnectionServer;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.util.StringUtils;

import com.mainsteam.stm.route.logic.LogicAppEnum;
import com.mainsteam.stm.route.logic.LogicServer;

/**
 * @author heshengchao
 * 
 */
public class RouteableJmxSocketConnectionServer implements
		MessageConnectionServer {

	private static final Log logger = LogFactory
			.getLog(RouteableJmxSocketConnectionServer.class);

	private JMXServiceURL addr;
	@SuppressWarnings("rawtypes")
	private Map env;
	private LogicServer logicServer;

	/**
	 * @param paramJMXServiceURL
	 * @param paramMap
	 * @throws IOException
	 */
	public RouteableJmxSocketConnectionServer(JMXServiceURL paramJMXServiceURL,
			Map<?, ?> paramMap, LogicServer logicServer) throws IOException {
		if (logger.isDebugEnabled()) {
			logger.debug("constructor Constructs a SocketConnectionServer on "
					+ paramJMXServiceURL);
		}

		if (paramJMXServiceURL == null) {
			throw new NullPointerException("Null address.");
		}

		if (!"jmxmp".equalsIgnoreCase(paramJMXServiceURL.getProtocol())) {
			throw new MalformedURLException("Unknown protocol: "
					+ paramJMXServiceURL.getProtocol());
		}

//		String str = null;
//		if (paramMap != null) {
//			str = (String) paramMap.get("jmx.remote.server.address.wildcard");
//		}

//		this.wildcard = (str == null ? true : str.equalsIgnoreCase("true"));

		this.addr = paramJMXServiceURL;

		this.logicServer = logicServer;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * javax.management.remote.generic.MessageConnectionServer#start(java.util
	 * .Map)
	 */
	@SuppressWarnings({ "rawtypes", "unchecked" })
	@Override
	public void start(Map paramMap) throws IOException {
		if (logger.isDebugEnabled()) {
			logger.debug("start Starts the server now.");
		}

		HashMap<?, ?> localHashMap = new HashMap();
		if (this.env != null) {
			localHashMap.putAll(this.env);
		}
		if (paramMap != null) {
			localHashMap.putAll(paramMap);
		}
		int port = this.addr.getPort();
		String str = this.addr.getHost();
		if (StringUtils.isEmpty(str)) {
			str = InetAddress.getLocalHost().getHostName();
		}

		this.addr = new JMXServiceURL("jmxmp", str, port);

		this.env = localHashMap;

		this.logicServer.startServer(LogicAppEnum.RPC_JMX_NODE_GROUP);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see javax.management.remote.generic.MessageConnectionServer#accept()
	 */
	@Override
	public MessageConnection accept() throws IOException {
		if (logger.isDebugEnabled()) {
			logger.debug("RouteableJmxSocketConnectionServer accept Waiting a new connection...");
		}
		RouteableJmxConnection localSocketConnection = new RouteableJmxConnection(
				logicServer.accept(LogicAppEnum.RPC_JMX_NODE_GROUP));
		return localSocketConnection;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see javax.management.remote.generic.MessageConnectionServer#stop()
	 */
	@Override
	public void stop() throws IOException {
		if (logger.isDebugEnabled()) {
			logger.debug("stop Stops the server now.");
		}
		logicServer = null;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see javax.management.remote.generic.MessageConnectionServer#getAddress()
	 */
	@Override
	public JMXServiceURL getAddress() {
		return this.addr;
	}
}
