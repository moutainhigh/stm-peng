/**
 * 
 */
package com.mainsteam.stm.node;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

/**
 * @author ziw
 * 
 */
public class NodeTable implements Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = 5979599204374827033L;

	private Date updateTime;

	private List<NodeGroup> groups;

	/**
	 * 
	 */
	public NodeTable() {
	}

	public List<NodeGroup> getGroups() {
		return groups;
	}

	public void setGroups(List<NodeGroup> entries) {
		this.groups = entries;
	}

	/**
	 * 从节点组中，拿到一个可用的节点。
	 * 
	 * @param nodeGroupId
	 * @return Node
	 */
	public Node getNodeInGroup(int nodeGroupId) {
		Node r = null;
		if (groups != null && groups.size() > 0) {
			for (NodeGroup group : groups) {
				if (nodeGroupId == group.getId()) {
					r = group.selectNode();
					break;
				}
			}
		}
		return r;
	}

	public Node selectNode(String ip, int port) {
		Node r = null;
		if (groups != null && groups.size() > 0) {
			for (NodeGroup group : groups) {
				Node n = group.selectNode(ip, port);
				if (n != null) {
					r = n;
					break;
				}
			}
		}
		return r;
	}

	public NodeGroup selectNextNodeGroup(NodeGroup currentNodeGroup, String ip,
			int port) {
		if (groups != null && groups.size() > 0) {
			for (NodeGroup group : groups) {
				if (group.selectNode(ip, port) != null
						&& group != currentNodeGroup) {
					int direction = currentNodeGroup.getNodeLevel()
							- group.getNodeLevel();
					if (direction < 0) {
						NodeGroup parent = group.getPre();
						while (parent != null) {
							if (parent == currentNodeGroup) {
								break;
							} else {
								group = parent;
								parent = group.getPre();
							}
						}
					} else if (direction == 0) {
						if (currentNodeGroup.getPre() != null) {
							group = currentNodeGroup.getPre();
						}
					} else {
						group = currentNodeGroup.getPre();
					}
					return group;
				}
			}
		}
		return null;
	}

	public List<Node> selectNodesByType(NodeFunc func) {
		List<Node> nodeList = new ArrayList<>();
		if (groups != null && !groups.isEmpty()) {
			for (NodeGroup group : groups) {
				if (group.getFunc() != func) {
					continue;
				}
				List<Node> groupNodes = group.getNodes();
				if (groupNodes != null && !groupNodes.isEmpty()) {
					for(Node node :groupNodes){
						if(":".equals(String.valueOf(node.getInstallPath().charAt(1)))){
						 node.setInstallPath(new StringBuffer(node.getInstallPath()).insert(2, "\\").toString());
						}
					}
					nodeList.addAll(groupNodes);
				}
			}
		}
		return nodeList;
	}

	/**
	 * @return the updateTime
	 */
	public final Date getUpdateTime() {
		return updateTime;
	}

	/**
	 * @param updateTime
	 *            the updateTime to set
	 */
	public final void setUpdateTime(Date updateTime) {
		this.updateTime = updateTime;
	}
}
