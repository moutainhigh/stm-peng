/**
 * 
 */
package com.mainsteam.stm.node.task;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import com.mainsteam.stm.node.Node;
import com.mainsteam.stm.node.NodeGroup;
import com.mainsteam.stm.node.NodeTable;

/**
 * @author ziw
 * 
 */
public class LocalNodeTableChecker {

	private static final Log logger = LogFactory
			.getLog(LocalNodeTableChecker.class);
	private static SimpleDateFormat format = new SimpleDateFormat(
			"yyyy-MM-dd HH:mm:ss.SSS");

	/**
	 * 
	 */
	public LocalNodeTableChecker() {
	}

	private static String formatDate(Date date) {
		return date == null ? "null" : format.format(date);
	}

	public static boolean checkUpdate(NodeTable table, Date updateTime,
			int nodeGroupSize, int nodeSize, Node currentNode,
			NodeGroup currentNodeGroup, NodeTable dbTable) {
		boolean isUpdate = false;
		/**
		 * 根据时间来判断，是否需要更新本地节点表
		 */
		/*
		 * if (table.getUpdateTime() != null &&
		 * table.getUpdateTime().compareTo(updateTime) != 0) { isUpdate = true;
		 * if (logger.isInfoEnabled()) { StringBuilder b = new StringBuilder();
		 * b.append("check result.table updateTime changed. ");
		 * b.append("from ").append(formatDate(table.getUpdateTime()))
		 * .append(" to ").append(formatDate(updateTime));
		 * logger.info(b.toString()); } } else if (table.getUpdateTime() == null
		 * && table.getUpdateTime() != updateTime) { if (logger.isInfoEnabled())
		 * { StringBuilder b = new StringBuilder();
		 * b.append("check result.table updateTime changed. ");
		 * b.append("from ").append(formatDate(table.getUpdateTime()))
		 * .append(" to ").append(formatDate(updateTime));
		 * logger.info(b.toString()); } isUpdate = true; }
		 */
		if (!isUpdate) {
			/**
			 * 根据节点组个数是否发生变化，判断是否更新本地节点表
			 */
			int currentNodeGroupSize = table.getGroups() == null ? 0 : table
					.getGroups().size();
			if (nodeGroupSize != currentNodeGroupSize) {
				if (logger.isInfoEnabled()) {
					StringBuilder b = new StringBuilder();
					b.append("check result.table nodeGroupSize changed. ");
					b.append(" from ").append(currentNodeGroupSize)
							.append(" to ").append(nodeGroupSize);
					logger.info(b.toString());
				}
				isUpdate = true;
			}
		}

		if (!isUpdate) {
			/**
			 * 根据节点个数是否发生变化，判断是否更新本地节点表
			 */
			int currentSize = 0;
			List<NodeGroup> groups = table.getGroups();
			if (groups != null && groups.size() > 0) {
				for (NodeGroup nodeGroup : groups) {
					List<Node> nodeList = nodeGroup.getNodes();
					currentSize += (nodeList == null ? 0 : nodeList.size());
				}
			}
			if (nodeSize != currentSize) {
				if (logger.isInfoEnabled()) {
					StringBuilder b = new StringBuilder();
					b.append("check result.table nodeSize changed. ");
					b.append(" from ").append(nodeSize).append(" to ")
							.append(currentSize);
					logger.info(b.toString());
				}
				isUpdate = true;
			}
		}

		if (isUpdate && currentNodeGroup != null && currentNode != null) {
			/**
			 * 检查节点表数据是否正确
			 */
			boolean isHasGroup = false;
			boolean isHasNode = false;
			List<NodeGroup> groups = dbTable.getGroups();
			if (groups != null && groups.size() > 0) {
				for (NodeGroup nodeGroup : groups) {
					if (nodeGroup.getId() == currentNodeGroup.getId()) {
						isHasGroup = true;
						List<Node> nodeList = nodeGroup.getNodes();
						if (nodeList != null && nodeList.size() > 0) {
							for (Node node : nodeList) {
								if (node.getId() == currentNode.getId()) {
									isHasNode = true;
									break;
								}
							}
						}
						break;
					}
				}
			}
			if (isHasGroup == false) {
				if (logger.isErrorEnabled()) {
					logger.error("checkUpdate nodeTables has no currentNodeGroup.stop to update it.");
				}
				isUpdate = false;
			} else if (isHasNode == false) {
				if (logger.isErrorEnabled()) {
					logger.error("checkUpdate nodeTables has no currentNode.stop to update it.");
				}
				isUpdate = false;
			}
		}
		return isUpdate;
	}

}
