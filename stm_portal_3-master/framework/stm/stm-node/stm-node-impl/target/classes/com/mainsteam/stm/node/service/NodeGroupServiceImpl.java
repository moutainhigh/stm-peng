/**
 * 
 */
package com.mainsteam.stm.node.service;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import com.alibaba.fastjson.JSON;
import com.mainsteam.stm.node.NodeFunc;
import com.mainsteam.stm.node.NodeGroup;
import com.mainsteam.stm.node.NodeGroupService;
import com.mainsteam.stm.node.dao.NodeGroupDAO;
import com.mainsteam.stm.node.dao.pojo.NodeGroupDO;
import com.mainsteam.stm.platform.sequence.service.ISequence;

/**
 * @author ziw
 * 
 */
public class NodeGroupServiceImpl implements NodeGroupService {

	private static final Log logger = LogFactory
			.getLog(NodeGroupServiceImpl.class);

	private NodeGroupDAO groupDAO;

	private ISequence sequence;

	/**
	 * @param groupDAO
	 *            the groupDAO to set
	 */
	public final void setGroupDAO(NodeGroupDAO groupDAO) {
		this.groupDAO = groupDAO;
	}

	/**
	 * @param sequence
	 *            the sequence to set
	 */
	public final void setSequence(ISequence sequence) {
		this.sequence = sequence;
	}

	/**
	 * 
	 */
	public NodeGroupServiceImpl() {
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * com.mainsteam.stm.node.NodeGroupService#addGroup(com.mainsteam.stm.
	 * node.NodeGroup)
	 */
	@Override
	public int addGroup(NodeGroup group) {
		if (logger.isTraceEnabled()) {
			logger.trace("addGroup start ");
		}
		group.setId((int) sequence.next());
		if (logger.isInfoEnabled()) {
			logger.info("addGroup group="
					+ JSON.toJSONString(group));
		}
		NodeGroupDO groupDO = toDo(group);
		groupDAO.insert(groupDO);
		if (logger.isTraceEnabled()) {
			logger.trace("addGroup end");
		}
		return group.getId();
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see com.mainsteam.stm.node.NodeGroupService#removeGroup(int)
	 */
	@Override
	public void removeGroup(int groupId) {
		if (logger.isInfoEnabled()) {
			logger.info("removeGroup start groupId=" + groupId);
		}
		groupDAO.removeById(groupId);
		if (logger.isInfoEnabled()) {
			logger.info("removeGroup end");
		}
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * com.mainsteam.stm.node.NodeGroupService#updateNodeGroupRelation(int,
	 * int)
	 */
	@Override
	public void updateNodeGroupRelation(int groupId, int parentGroupId) {
		if (logger.isTraceEnabled()) {
			StringBuilder b = new StringBuilder();
			b.append("updateNodeGroupRelation start");
			b.append(" groupId=").append(groupId);
			b.append(" parentGroupId=").append(parentGroupId);
			logger.trace(b.toString());
		}
		NodeGroupDO g = new NodeGroupDO();
		g.setId(groupId);
		g.setParentId(parentGroupId);
		groupDAO.updateExampleById(g);
		if (logger.isTraceEnabled()) {
			logger.trace("updateNodeGroupRelation end");
		}
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see com.mainsteam.stm.node.NodeGroupService#getNodeGroupById(int)
	 */
	@Override
	public NodeGroup getNodeGroupById(int groupId) {
		if (logger.isTraceEnabled()) {
			logger.trace("getNodeGroupById start");
		}
		NodeGroup group = null;
		NodeGroupDO q = new NodeGroupDO();
		q.setId(groupId);
		NodeGroupDO g = groupDAO.selectById(groupId);
		if (g != null) {
			group = toGroup(g);
			Integer parentId = g.getParentId();
			if (parentId != null) {
				NodeGroupDO p = groupDAO.selectById(parentId.intValue());
				if (p != null) {
					group.setPre(toGroup(p));
				}
			}
			q.setId(null);
			q.setParentId(groupId);
			List<NodeGroupDO> nextGroups = groupDAO.selectByExample(q);
			if (nextGroups != null && nextGroups.size() > 0) {
				List<NodeGroup> nextGroupList = new ArrayList<>(
						nextGroups.size());
				for (NodeGroupDO gd : nextGroups) {
					nextGroupList.add(toGroup(gd));
				}
				group.setNextGroups(nextGroupList);
			}
		}
		if (logger.isTraceEnabled()) {
			logger.trace("getNodeGroupById end");
		}
		return group;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see com.mainsteam.stm.node.NodeGroupService#getNodeGroups()
	 */
	@Override
	public List<NodeGroup> getNodeGroups() {
		List<NodeGroup> groups = null;
		NodeGroupDO q = new NodeGroupDO();
		List<NodeGroupDO> groupDos = groupDAO.selectByExample(q);
		if (groupDos != null && groupDos.size() > 0) {
			Map<Integer, NodeGroup> groupsMap = new HashMap<>();
			for (NodeGroupDO groupDO : groupDos) {
				if (groupsMap.containsKey(groupDO.getId())) {
					NodeGroup parentGroup = groupsMap.get(groupDO.getId());
					parentGroup.setFunc(NodeFunc.valueOf(groupDO.getFunc()));
					parentGroup.setNodeLevel(groupDO.getLevel());
					parentGroup.setName(groupDO.getName());
				} else {
					NodeGroup group = toGroup(groupDO);
					if (groupDO.getParentId() != null
							&& groupDO.getParentId().intValue() != -1) {
						NodeGroup parentGroup = null;
						if (groupsMap.containsKey(groupDO.getParentId())) {
							parentGroup = groupsMap.get(groupDO.getParentId());
						} else {
							parentGroup = new NodeGroup();
							parentGroup.setId(groupDO.getParentId());
							groupsMap.put(groupDO.getParentId(), parentGroup);
						}
						if (parentGroup.getNextGroups() == null) {
							parentGroup
									.setNextGroups(new ArrayList<NodeGroup>());
						}
						parentGroup.getNextGroups().add(group);
						group.setPre(parentGroup);

					}
					groupsMap.put(groupDO.getId(), group);
				}
			}
			/**
			 * 下面将不可用的parent group删除
			 */
			for (Iterator<Integer> iterator = groupsMap.keySet().iterator(); iterator
					.hasNext();) {
				Integer groupId = iterator.next();
				NodeGroup group = groupsMap.get(groupId);
				if (group.getFunc() == null) {
					List<NodeGroup> nextGroups = group.getNextGroups();
					for (NodeGroup g : nextGroups) {
						g.setPre(null);
					}
					iterator.remove();
				}
			}
			groups = new ArrayList<>(groupsMap.values());
		}
		return groups;
	}

	private NodeGroup toGroup(NodeGroupDO groupDO) {
		NodeGroup group = new NodeGroup();
		group.setFunc(NodeFunc.valueOf(groupDO.getFunc()));
		group.setId(groupDO.getId());
		group.setNodeLevel(groupDO.getLevel());
		group.setName(groupDO.getName());
		return group;
	}

	private NodeGroupDO toDo(NodeGroup group) {
		NodeGroupDO groupDO = new NodeGroupDO();
		groupDO.setFunc(group.getFunc().name());
		groupDO.setId(group.getId());
		groupDO.setLevel(group.getNodeLevel());
		groupDO.setName(group.getName());
		groupDO.setParentId(group.getPre() == null ? null : group.getPre()
				.getId());
		groupDO.setUpdateTime(new Date().getTime());
		return groupDO;
	}
}
