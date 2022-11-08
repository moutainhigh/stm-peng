package com.mainsteam.stm.rpc.server;

import java.io.IOException;
import java.lang.management.ManagementFactory;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import javax.management.InstanceAlreadyExistsException;
import javax.management.InstanceNotFoundException;
import javax.management.MBeanRegistrationException;
import javax.management.MBeanServer;
import javax.management.NotCompliantMBeanException;
import javax.management.ObjectName;
import javax.management.remote.JMXConnectorServer;
import javax.management.remote.JMXServiceURL;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import com.mainsteam.stm.jmx.JmxUtil;
import com.mainsteam.stm.node.LocaleNodeService;
import com.mainsteam.stm.node.Node;
import com.mainsteam.stm.node.exception.NodeException;
import com.mainsteam.stm.route.RouteableJmxSocketConnectionServer;
import com.mainsteam.stm.route.logic.LogicServer;

public class OCRPCServerImpl implements OCRPCServer {

	private static final Log logger = LogFactory.getLog(OCRPCServerImpl.class);

	private MBeanServer server;

	private LocaleNodeService nodeService;

	private JMXConnectorServer connectorServer;

	private List<RemoteServiceInfo> serviceInfos;

	private boolean hasStarted;

	private int counter;

	private LogicServer logicServer;

	private String serverIp;

	private int serverPort;

	public OCRPCServerImpl() {
	}

	public void setNodeService(LocaleNodeService nodeService) {
		this.nodeService = nodeService;
	}

	/**
	 * @param logicServer
	 *            the logicServer to set
	 */
	public final void setLogicServer(LogicServer logicServer) {
		this.logicServer = logicServer;
	}

	/**
	 * @return the serverIp
	 */
	public final String getServerIp() {
		return serverIp;
	}

	/**
	 * @param serverIp
	 *            the serverIp to set
	 */
	public final void setServerIp(String serverIp) {
		this.serverIp = serverIp;
	}

	/**
	 * @return the serverPort
	 */
	public final int getServerPort() {
		return serverPort;
	}

	/**
	 * @param serverPort
	 *            the serverPort to set
	 */
	public final void setServerPort(int serverPort) {
		this.serverPort = serverPort;
	}

	public void init() {
		if (logger.isInfoEnabled()) {
			logger.info("OCRPCServer init jmx server.");
		}
		server = ManagementFactory.getPlatformMBeanServer();
		serviceInfos = new ArrayList<>();
		counter = 0;
	}

	public synchronized void start() throws IOException {
		if (System.getProperty("testCase") != null) {
			return;
		}
		if (hasStarted) {
			return;
		}
		if (logger.isInfoEnabled()) {
			logger.info("OCRPCServer starting...");
		}
		Node currentNode = null;
		try {
			currentNode = nodeService.getCurrentNode();
		} catch (NodeException e) {
			logger.error("start", e);
			throw new RuntimeException(e);
		}
		if (currentNode == null) {
			return;
		}
		this.serverIp = currentNode.getIp();
		this.serverPort = currentNode.getPort();
		if (logger.isInfoEnabled()) {
			logger.info("start jmx at currentNode=" + this.serverIp + ":"
					+ this.serverPort);
		}
		JMXServiceURL jmxServiceURL = JmxUtil.newServerURL(this.serverIp,
				this.serverPort);
		Map<String, Object> env = new HashMap<>();
		Map<?, ?> paramMap = null;
		RouteableJmxSocketConnectionServer jmxSocketConnectionServer = new RouteableJmxSocketConnectionServer(
				jmxServiceURL, paramMap, logicServer);
		env.put("jmx.remote.message.connection.server",
				jmxSocketConnectionServer);
		//malachi in
		connectorServer = JmxUtil.newConnectorServer(server, this.serverIp,
				this.serverPort, env);
		connectorServer.start();
		hasStarted = true;
		if (logger.isInfoEnabled()) {
			StringBuilder b = new StringBuilder();
			b.append("started mbean server at ");
			b.append(this.serverIp).append(' ').append(this.serverPort);
			logger.info(b.toString());
		}
	}

	public synchronized void close() throws IOException {
		if (!hasStarted) {
			return;
		}
		try {
			connectorServer.stop();
		} catch (IOException e) {
			if (logger.isErrorEnabled()) {
				logger.error("close", e);
			}
			throw e;
		}
		for (RemoteServiceInfo info : serviceInfos) {
			try {
				server.unregisterMBean(info.getObjectName());
			} catch (MBeanRegistrationException | InstanceNotFoundException e) {
				if (logger.isErrorEnabled()) {
					logger.error("close", e);
				}
			}
		}
		hasStarted = false;
	}

	public boolean registerObjectSerivce(Object remoteService,
			Class<?> interfaceClass) {
		if (logger.isTraceEnabled()) {
			logger.trace("registerSerivce start interfaceClass="
					+ interfaceClass);
		}
		ObjectName name = JmxUtil.createName(interfaceClass);
		try {
			server.registerMBean(remoteService, name);
			RemoteServiceInfo info = new RemoteServiceInfo();
			info.setClassName(interfaceClass.getName());
			info.setRegisterDate(new Date());
			info.setRemoteServiceDesc(remoteService.toString());
			info.setInstance(remoteService);
			info.setObjectName(name);
			info.setRemoteServiceId(counter++);
			serviceInfos.add(info);
		} catch (InstanceAlreadyExistsException | MBeanRegistrationException
				| NotCompliantMBeanException e) {
			if (logger.isErrorEnabled()) {
				logger.error("registerService", e);
			}
			return false;
		}
		if (logger.isTraceEnabled()) {
			logger.trace("registerSerivce end");
		}
		return true;
	}

	@Override
	public <T> boolean registerService(T remoteService, Class<T> interfaceClass) {
		if (server == null) {
			return false;
		}
		ObjectName name = JmxUtil.createName(interfaceClass);
		try {
			server.registerMBean(remoteService, name);
			RemoteServiceInfo info = new RemoteServiceInfo();
			info.setClassName(interfaceClass.getName());
			info.setRegisterDate(new Date());
			info.setRemoteServiceDesc(remoteService.toString());
			info.setInstance(remoteService);
			info.setObjectName(name);
			info.setRemoteServiceId(counter++);
			serviceInfos.add(info);
		} catch (InstanceAlreadyExistsException | MBeanRegistrationException
				| NotCompliantMBeanException e) {
			if (logger.isErrorEnabled()) {
				logger.error("registerService", e);
			}
			return false;
		}
		return true;
	}

	@Override
	public boolean unregisterService(Object remoteService) {
		if (server == null) {
			return false;
		}
		for (Iterator<RemoteServiceInfo> iterator = serviceInfos.iterator(); iterator
				.hasNext();) {
			RemoteServiceInfo si = iterator.next();
			if (si.getInstance() == remoteService) {
				try {
					server.unregisterMBean(si.getObjectName());
					iterator.remove();
					return true;
				} catch (MBeanRegistrationException | InstanceNotFoundException e) {
					if (logger.isErrorEnabled()) {
						logger.error("unregisterService", e);
					}
				}
				break;
			}
		}
		return false;
	}

	@Override
	public boolean unregisterService(long remoteServiceId) {
		if (server == null) {
			return false;
		}
		for (Iterator<RemoteServiceInfo> iterator = serviceInfos.iterator(); iterator
				.hasNext();) {
			RemoteServiceInfo si = iterator.next();
			if (si.getRemoteServiceId() == remoteServiceId) {
				try {
					server.unregisterMBean(si.getObjectName());
					iterator.remove();
					return true;
				} catch (MBeanRegistrationException | InstanceNotFoundException e) {
					if (logger.isErrorEnabled()) {
						logger.error("unregisterService", e);
					}
				}
				break;
			}
		}
		return false;
	}

	@Override
	public List<RemoteServiceInfo> listService() {
		if (server == null) {
			return null;
		}
		return new ArrayList<>(serviceInfos);
	}

	@Override
	public void startServer() throws IOException {
		start();
	}

	@Override
	public void stopServer() throws IOException {
		close();
	}

	@Override
	public boolean isStarted() {
		return this.hasStarted;
	}
}
