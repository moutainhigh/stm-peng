/**
 * 
 */
package com.mainsteam.stm.node;

import java.io.Serializable;
import java.util.List;

/**
 * 节点表节点
 * 
 * @author ziw
 * 
 */
public class NodeGroup implements Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = 5914026595233312693L;

	/**
	 * 组id
	 */
	private int id;

	/**
	 * 组名称
	 */
	private String name;

	/**
	 * 上一层节点
	 */
	private NodeGroup pre;

	/**
	 * 该层级别
	 */
	private int nodeLevel;

	/**
	 * 该层的节点列表
	 */
	private List<Node> nodes;

	/**
	 * 节点类型
	 */
	private NodeFunc func;

	/**
	 * 下一层节点
	 */
	private List<NodeGroup> nextGroups;

	/**
	 * 判断节点是否隔离墙节点
	 * true 隔离节点  false 非隔离节点
	 */
	private boolean isolated = false;
	
	/**
	 * 
	 */
	public NodeGroup() {
	}

	public NodeGroup getPre() {
		return pre;
	}

	public void setPre(NodeGroup pre) {
		this.pre = pre;
	}

	public int getNodeLevel() {
		return nodeLevel;
	}

	public void setNodeLevel(int nodeLevel) {
		this.nodeLevel = nodeLevel;
	}

	public List<Node> getNodes() {
		return nodes;
	}

	public void setNodes(List<Node> nodes) {
		this.nodes = nodes;
	}

	public NodeFunc getFunc() {
		return func;
	}

	public void setFunc(NodeFunc func) {
		this.func = func;
	}

	/**
	 * @return the nextGroups
	 */
	public final List<NodeGroup> getNextGroups() {
		return nextGroups;
	}

	/**
	 * @param nextGroups
	 *            the nextGroups to set
	 */
	public final void setNextGroups(List<NodeGroup> nextGroups) {
		this.nextGroups = nextGroups;
	}

	public int getId() {
		return id;
	}

	public void setId(int id) {
		this.id = id;
	}

	/**
	 * @return the name
	 */
	public final String getName() {
		return name;
	}

	/**
	 * @param name
	 *            the name to set
	 */
	public final void setName(String name) {
		this.name = name;
	}

	/**
	 * 选择一个可用性高的节点
	 * 
	 * @return Node
	 */
	public Node selectNode() {
		// TODO:
		return this.nodes != null && this.nodes.size() > 0 ? nodes.get(0)
				: null;
	}

	public Node selectNode(String ip, int port) {
		if (nodes != null && nodes.size() > 0) {
			for (Node n : nodes) {
				if (n.getIp().equals(ip) && n.getPort() == port) {
					return n;
				}
			}
		}
		return null;
	}
	
	public boolean isIsolated() {
		return isolated;
	}

	public void setIsolated(boolean isolated) {
		this.isolated = isolated;
	}
}
