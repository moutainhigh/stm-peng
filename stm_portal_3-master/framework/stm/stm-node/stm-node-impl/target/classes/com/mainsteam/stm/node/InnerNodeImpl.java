/**
 * 
 */
package com.mainsteam.stm.node;

import java.util.List;

/**
 * @author ziw
 * 
 */
public class InnerNodeImpl extends Node {

	private NodeGroup nodeGroup;

	/**
	 * 
	 */
	private static final long serialVersionUID = -519575466644789025L;

	/**
	 * 
	 */
	public InnerNodeImpl() {
	}

	/**
	 * @param nodeGroup
	 *            the nodeGroup to set
	 */
	public final void setNodeGroup(NodeGroup nodeGroup) {
		this.nodeGroup = nodeGroup;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see com.mainsteam.stm.node.Node#getParentNodeId()
	 */
	@Override
	public int getParentNodeId() {
		List<Node> list = nodeGroup.getPre() == null ? null : nodeGroup
				.getPre().getNodes();
		if (list != null && list.size() > 0) {
			return list.get(0).getId();
		} else {
			return -1;
		}
	}
}
