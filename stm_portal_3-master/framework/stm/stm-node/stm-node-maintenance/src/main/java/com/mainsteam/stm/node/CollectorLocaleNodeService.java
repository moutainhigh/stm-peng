/**
 * 
 */
package com.mainsteam.stm.node;

import java.io.IOException;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import com.mainsteam.stm.node.exception.NodeException;
import com.mainsteam.stm.rpc.client.OCRPCClient;


/**
 * @author ziw
 *
 */
public class CollectorLocaleNodeService extends AbstractLocaleNodeService {

	private static final Log logger = LogFactory
			.getLog(CollectorLocaleNodeService.class);
	
	private OCRPCClient client;

	/**
	 * @param client the client to set
	 */
	public final void setClient(OCRPCClient client) {
		this.client = client;
	}

	/**
	 * 
	 */
	public CollectorLocaleNodeService() {
	}

	public NodeTable getNodeTableFromDb() throws NodeException {
		NodeTable localTable = getLocalNodeTable();
		NodeGroup currentNodeGroup = getCurrentNodeGroup();
		Node pre = localTable.getNodeInGroup(currentNodeGroup.getPre().getId());
		RemoteNodeManagerMBean nodeService = null;
		try {
			nodeService = client.getRemoteSerivce(pre, RemoteNodeManagerMBean.class);
		} catch (IOException e) {
			if (logger.isErrorEnabled()) {
				logger.error("getNodeTableFromDb", e);
			}
			throw new NodeException(e);
		} 
		return nodeService.getNodeTable();
	}
}
