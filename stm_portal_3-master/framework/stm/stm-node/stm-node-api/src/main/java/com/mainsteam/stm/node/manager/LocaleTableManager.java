/**
 * 
 */
package com.mainsteam.stm.node.manager;

import com.mainsteam.stm.node.Node;
import com.mainsteam.stm.node.NodeGroup;
import com.mainsteam.stm.node.NodeTable;
import com.mainsteam.stm.node.exception.NodeException;

/**
 * 本地节点操作
 * 
 * @author ziw
 * 
 */
public interface LocaleTableManager {

	public void saveNodeTable(Node currentNode, NodeTable table)
			throws NodeException;

	public Node getCurrentNode();

	public NodeTable getNodeTable();
	
	public int getCurrentNodeId();

	/**
	 * @return the currentGroup
	 */
	public NodeGroup getCurrentGroup();
}
