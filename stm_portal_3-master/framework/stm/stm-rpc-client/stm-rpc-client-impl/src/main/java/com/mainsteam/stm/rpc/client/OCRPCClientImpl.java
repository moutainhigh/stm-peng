/**
 * 
 */
package com.mainsteam.stm.rpc.client;

import java.io.IOException;

import javax.management.JMX;
import javax.management.MBeanServerConnection;
import javax.management.ObjectName;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import com.mainsteam.stm.errorcode.ServerErrorCodeConstant;
import com.mainsteam.stm.jmx.JmxUtil;
import com.mainsteam.stm.node.LocaleNodeService;
import com.mainsteam.stm.node.Node;
import com.mainsteam.stm.node.NodeFunc;
import com.mainsteam.stm.node.NodeGroup;
import com.mainsteam.stm.node.NodeTable;
import com.mainsteam.stm.node.exception.NodeException;
import com.mainsteam.stm.route.logic.LogicAppEnum;
import com.mainsteam.stm.util.OSUtil;

/**
 * @author ziw
 * 
 */
public class OCRPCClientImpl implements OCRPCClient {

	private static final Log logger = LogFactory.getLog(OCRPCClientImpl.class);

	private static final String CHECK_PARENT_NODE = "CHECK_PARENT_NODE";

	private NodeConnectionManager connectionManager;

	private LocaleNodeService nodeService;

	/**
	 * 
	 */
	public OCRPCClientImpl() {
	}

	public void setConnectionManager(NodeConnectionManager connectionManager) {
		this.connectionManager = connectionManager;
	}

	/**
	 * @param nodeService
	 *            the nodeService to set
	 */
	public final void setNodeService(LocaleNodeService nodeService) {
		this.nodeService = nodeService;
	}

	public void start() throws NodeException {
		/**
		 * 探测父节点是否启动
		 */
		String checkParentNode = OSUtil.getEnv(CHECK_PARENT_NODE,
				Boolean.FALSE.toString());
		System.out.println("check parent node.checkParentNode="
				+ checkParentNode);
		if (Boolean.TRUE.toString().equalsIgnoreCase(checkParentNode)) {
			NodeGroup currentNodeGroup;
			try {
				currentNodeGroup = nodeService.getCurrentNodeGroup();
			} catch (NodeException e) {
				if (logger.isErrorEnabled()) {
					logger.error("start", e);
				}
				throw e;
			}
			if(currentNodeGroup == null){
				if (logger.isErrorEnabled()) {
					String msg = "currentNodeGroup is emtpy.Please check the config/node/nodeTables.xml.";
					logger.error(msg);
					throw new NodeException(ServerErrorCodeConstant.ERR_NODE_CURRENT_NOT_EXIST_NODE_TABLES_XML,msg);
				}
			}
			NodeGroup preGroup = currentNodeGroup.getPre();
			if (preGroup != null) {
				Node preNode = preGroup.selectNode();
				boolean pingResult = false;
				String msg = null;
				do {
					pingResult = connectionManager.ping(preNode);
					if (pingResult) {
						break;
					} else if (msg == null) {
						StringBuilder b = new StringBuilder(
								"Cannot connect to preNode,waiting 10 seconds. ");
						b.append(" preNode.ip=").append(preNode.getIp());
						b.append(" preNode.port=").append(preNode.getPort());
						msg = b.toString();
					}
					if (logger.isErrorEnabled()) {
						logger.error(msg);
					}
					System.out.println(msg);
					synchronized (this) {
						try {
							this.wait(10000);// wait 10 second.
						} catch (InterruptedException e) {
						}
					}
				} while (true);
			} else {
				System.out.println("No parent node.continue next step.");
				if (logger.isInfoEnabled()) {
					logger.info("No parent node.continue next step.");
				}
			}
		}
	}

	@Override
	public <T> T getRemoteSerivce(Node node, Class<T> remoteInterfaceClass)
			throws IOException {
		if (logger.isTraceEnabled()) {
			logger.trace("getRemoteSerivce start");
		}
		MBeanServerConnection connection = connectionManager.getConnection(
				node, LogicAppEnum.RPC_JMX_NODE_GROUP);
		if (logger.isDebugEnabled()) {
			logger.debug("getRemoteSerivce getconnection ok from "
					+ node.toString());
		}
		ObjectName objectName = JmxUtil.createName(remoteInterfaceClass);
		T proxyInstance = JMX.newMBeanProxy(connection, objectName,
				remoteInterfaceClass, false);
		if (logger.isTraceEnabled()) {
			logger.trace("getRemoteSerivce end");
		}
		return proxyInstance;
	}

	@Override
	public <T> T getParentRemoteSerivce(NodeFunc func,
			Class<T> remoteInterfaceClass) throws IOException {
		NodeGroup funcGroup = null;
		Node parentNode = null;
		try {
			NodeGroup group = nodeService.getCurrentNodeGroup();
			funcGroup = group.getPre();
			while (funcGroup != null && funcGroup != group) {
				if (funcGroup.getFunc() == func) {
					parentNode = funcGroup.selectNode();
					break;
				}
			}
		} catch (NodeException e) {
			if (logger.isErrorEnabled()) {
				logger.error("getParentRemoteSerivce", e);
			}
			throw new IOException(e);
		}
		if (parentNode == null) {
			throw new IOException("Cannt found parent node.");
		}
		return getRemoteSerivce(parentNode, remoteInterfaceClass);
	}

	@Override
	public <T> T getDirectRemoteSerivce(Node node, Class<T> remoteInterfaceClass)
			throws IOException {
		if (logger.isTraceEnabled()) {
			logger.trace("getDirectRemoteSerivce start");
		}
		MBeanServerConnection connection = connectionManager.getConnection(
				node, LogicAppEnum.RPC_JMX_NODE);
		if (logger.isDebugEnabled()) {
			logger.debug("getDirectRemoteSerivce getconnection ok from "
					+ node.toString());
		}
		ObjectName objectName = JmxUtil.createName(remoteInterfaceClass);
		T proxyInstance = JMX.newMBeanProxy(connection, objectName,
				remoteInterfaceClass, false);
		if (logger.isTraceEnabled()) {
			logger.trace("getDirectRemoteSerivce end");
		}
		return proxyInstance;
	}

	@Override
	public <T> T getRemoteSerivce(int nodeGroupId, Class<T> remoteInterfaceClass)
			throws IOException {
		T serviceObj = null;
		try {
			NodeTable table = nodeService.getLocalNodeTable();
			Node node = table.getNodeInGroup(nodeGroupId);
			if(node==null){
				throw new IOException("cann't find Node for group["+nodeGroupId+"]");
			}
			serviceObj = getRemoteSerivce(node, remoteInterfaceClass);
		} catch (NodeException e) {
			if (logger.isErrorEnabled()) {
				logger.error("getRemoteSerivce", e);
			}
			throw new IOException(e);
		}
		return serviceObj;
	}
}
