/**
 * 
 */
package com.mainsteam.stm.node;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import com.mainsteam.stm.node.exception.NodeException;
import com.mainsteam.stm.route.RouteConnectionException;
import com.mainsteam.stm.route.RouteEntry;
import com.mainsteam.stm.route.RouteEntrySupporter;
import com.mainsteam.stm.route.logic.LogicAppEnum;
import com.mainsteam.stm.route.logic.LogicClient;
import com.mainsteam.stm.route.physical.PhysicalServer;

/**
 * @author ziw
 * 
 */
public class RouteEntrySupporterNodeImpl implements RouteEntrySupporter {

	private static final Log logger = LogFactory
			.getLog(RouteEntrySupporterNodeImpl.class);

	private LocaleNodeService nodeService;
//
//	private LogicClient logicClient;
//
//	private PhysicalServer physicalServer;

	/**
	 * @param nodeService
	 *            the nodeService to set
	 */
	public final void setNodeService(LocaleNodeService nodeService) {
		this.nodeService = nodeService;
	}

	/**
	 * @param logicClient
	 *            the logicClient to set
	 */
	public final void setLogicClient(LogicClient logicClient) {
//		this.logicClient = logicClient;
		logicClient.setSupporter(this);
	}

	/**
	 * @param physicalServer
	 *            the physicalServer to set
	 */
	public final void setPhysicalServer(PhysicalServer physicalServer) {
//		this.physicalServer = physicalServer;
		physicalServer.setSupporter(this);
	}

	/**
	 * 
	 */
	public RouteEntrySupporterNodeImpl() {
	}

	public void start() {
//		this.logicClient.setSupporter(this);
//		this.physicalServer.setSupporter(this);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * com.mainsteam.stm.route.RouteEntrySupporter#getNextIp(java.lang.String,
	 * int)
	 */
	@Override
	public RouteEntry getNextIp(String distIp, int distPort,
			LogicAppEnum appEnum) throws RouteConnectionException {
		if (logger.isDebugEnabled()) {
			StringBuilder b = new StringBuilder();
			b.append("getNextIp start distIp=").append(distIp);
			b.append(" distPort=").append(distPort);
			logger.debug(b.toString());
		}
		RouteEntry entry = null;
		try {
			Node currentNode = nodeService.getCurrentNode();
			/**
			 * 如果当前节点还没有注册，则返回参数的地址。
			 */
			if (currentNode == null) {
				entry = new RouteEntry();
				entry.setIp(distIp);
				entry.setPort(distPort);
				if (logger.isDebugEnabled()) {
					logger.debug("getNextIp return dist node directly.");
				}
				return entry;
			}
			if (logger.isDebugEnabled()) {
				logger.debug("getNextIp currentNode.ip=" + currentNode.getIp()
						+ "  currentNode.port=" + currentNode.getPort());
			}
			/**
			 * 如果是当前节点，则返回null。
			 */
			if (currentNode.getIp().equals(distIp)
					&& currentNode.getPort() == distPort) {
				return null;
			}
			Node nextNode = null;
			NodeGroup currentNodeGroup = nodeService.getCurrentNodeGroup();
			Node selectNode = currentNodeGroup.selectNode(distIp, distPort);
			if (selectNode != null) {
				if (appEnum != LogicAppEnum.RPC_JMX_NODE) {
					/* 如果在同一分组，则认为其功能是相同的，是集群中的一个节点，也可以由当前节点来处理。 */
					if (logger.isDebugEnabled()) {
						logger.debug("getNextIp currentNodeGroup.selectNode is not null.");
					}
					return null;
				} else {
					entry = new RouteEntry();
					entry.setIp(selectNode.getIp());
					entry.setPort(selectNode.getPort());
				}
			} else {
				/**
				 * 查找下一跳需要的分组，并从分组的节点集群中，找到一个可用的节点。
				 */
				NodeTable table = nodeService.getLocalNodeTable();
				selectNode = table.selectNode(distIp, distPort);
				NodeGroup nextGroup = table.selectNextNodeGroup(
						currentNodeGroup, distIp, distPort);
				/**
				 * 如果下一跳分组和目标节点在同一个分组，则直接使用目标组内节点，否则从下一跳节点中选择一个。
				 */
				if (selectNode != null && nextGroup != null
						&& selectNode.getGroupId() == nextGroup.getId()) {
					/**
					 * 如果是直接节点调用，则返回直接节点，否则从节点组中查找一个可用节点
					 */
					if (appEnum.isGroupable()) {
						nextNode = nextGroup.selectNode();
					} else {
						nextNode = selectNode;
					}
				} else if (nextGroup != null) {
					if (logger.isDebugEnabled()) {
						logger.debug("getNextIp nextGroup.id="
								+ nextGroup.getId());
					}
					nextNode = nextGroup.selectNode();
				}
				if (nextNode != null) {
					entry = new RouteEntry();
					entry.setIp(nextNode.getIp());
					entry.setPort(nextNode.getPort());
				} else {
					throw new RouteConnectionException(1,
							"next node not found to distIp=" + distIp
									+ " distPort=" + distPort);
				}
			}
		} catch (NodeException e) {
			if (logger.isErrorEnabled()) {
				logger.error("getNextIp", e);
			}
			throw new RouteConnectionException(e);
		}
		if (logger.isDebugEnabled()) {
			logger.debug("getNextIp next.entry.ip=" + entry.getIp() + " port="
					+ entry.getPort());
		}
		if (logger.isTraceEnabled()) {
			logger.trace("getNextIp end");
		}
		return entry;
	}

	@Override
	public RouteEntry getCurrentIp() {
		RouteEntry entry = null;
		try {
			Node currentNode = nodeService.getCurrentNode();
			entry = new RouteEntry();
			entry.setIp(currentNode.getIp());
			entry.setPort(currentNode.getPort());
		} catch (NodeException e) {
			if (logger.isErrorEnabled()) {
				logger.error("getNextIp", e);
			}
		}
		return entry;
	}
}
