/**
 * 
 */
package com.mainsteam.stm.node;

/**
 * @author ziw
 *
 */
public class NodeCheck implements NodeCheckMBean {

	/**
	 * 
	 */
	public NodeCheck() {
	}

	/* (non-Javadoc)
	 * @see com.mainsteam.stm.node.NodeCheckMBean#checkNode()
	 */
	@Override
	public NodeState checkNode() {
		NodeState state = new NodeState();
		return state;
	}
}
