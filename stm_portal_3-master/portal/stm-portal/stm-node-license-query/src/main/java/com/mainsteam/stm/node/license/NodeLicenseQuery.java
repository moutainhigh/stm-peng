/**
 * 
 */
package com.mainsteam.stm.node.license;

import java.util.List;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import com.mainsteam.stm.node.Node;
import com.mainsteam.stm.node.NodeFunc;
import com.mainsteam.stm.node.NodeService;
import com.mainsteam.stm.node.NodeTable;
import com.mainsteam.stm.node.exception.NodeException;
import com.mainsteam.stm.system.license.api.ILicenseRemainCountApi;

/**
 * @author ziw
 * 
 */
public class NodeLicenseQuery extends ILicenseRemainCountApi {

	private static final Log logger = LogFactory.getLog(NodeLicenseQuery.class);

	private NodeService nodeService;

	/**
	 * 
	 */
	public NodeLicenseQuery() {
		super();
	}
	

	/**
	 * @param nodeService the nodeService to set
	 */
	public void setNodeService(NodeService nodeService) {
		this.nodeService = nodeService;
	}



	@Override
	public String getType() {
		return "stm-num-dcs";
	}

	@Override
	public int getUsed() {
		NodeTable table;
		try {
			table = nodeService.getNodeTable();
		} catch (NodeException e) {
			if (logger.isErrorEnabled()) {
				logger.error("getUsed", e);
			}
			throw new RuntimeException(e);
		}
		if (table != null) {
			List<Node> nodes = table.selectNodesByType(NodeFunc.collector);
			if (nodes != null) {
				return nodes.size();
			}
		}
		return 0;
	}
}
