/**
 * 
 */
package com.mainsteam.stm.node.service.heartbeat;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import com.mainsteam.stm.node.dao.NodeHeartbeatCheckDAO;
import com.mainsteam.stm.node.dao.NodeHeartbeatDAO;
import com.mainsteam.stm.node.dao.pojo.NodeHeartbeatCheckDO;
import com.mainsteam.stm.node.dao.pojo.NodeHeartbeatDO;
import com.mainsteam.stm.node.heartbeat.NodeHeartbeat;
import com.mainsteam.stm.node.heartbeat.NodeHeartbeatCount;
import com.mainsteam.stm.node.heartbeat.NodeHeartbeatService;
import com.mainsteam.stm.platform.sequence.service.ISequence;

/**
 * @author ziw
 * 
 */
public class NodeHeartbeatServiceImpl implements NodeHeartbeatService {

	private static final Log logger = LogFactory
			.getLog(NodeHeartbeatService.class);

	private NodeHeartbeatDAO heartbeatDAO;

	private NodeHeartbeatCheckDAO heartbeatCheckDAO;

	private ISequence sequence;

	/**
	 * @param heartbeatDAO
	 *            the heartbeatDAO to set
	 */
	public final void setHeartbeatDAO(NodeHeartbeatDAO heartbeatDAO) {
		this.heartbeatDAO = heartbeatDAO;
	}

	/**
	 * @param heartbeatCheckDAO
	 *            the heartbeatCheckDAO to set
	 */
	public final void setHeartbeatCheckDAO(
			NodeHeartbeatCheckDAO heartbeatCheckDAO) {
		this.heartbeatCheckDAO = heartbeatCheckDAO;
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
	public NodeHeartbeatServiceImpl() {
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * com.mainsteam.stm.node.heartbeat.NodeHeartbeatService#addNodeHeartbeat
	 * (com.mainsteam.stm.node.heartbeat.NodeHeartbeat)
	 */
	@Override
	public void addNodeHeartbeat(NodeHeartbeat heart) {
		if (logger.isTraceEnabled()) {
			logger.trace("addNodeHeartbeat start nodeId=" + heart.getNodeId());
		}
		long id = this.sequence.next();
		heart.setId(id);
		NodeHeartbeatDO heartbeatDO = new NodeHeartbeatDO();
		heartbeatDO.setId(id);
		heartbeatDO.setNextOccurtime(heart.getNextOccurtime().getTime());
		heartbeatDO.setOccurtime(heart.getOccurtime().getTime());
		heartbeatDO.setExpireOccurtime(heart.getExpireOccurtime().getTime());
		heartbeatDO.setNodeId(heart.getNodeId());
		heartbeatDO.setOccurCount(heart.getOccurCount());
		heartbeatDAO.insert(heartbeatDO);
		if (logger.isTraceEnabled()) {
			logger.trace("addNodeHeartbeat end");
		}
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * com.mainsteam.stm.node.heartbeat.NodeHeartbeatService#getLatestNodeHearbeats
	 * ()
	 */
	@Override
	public List<NodeHeartbeat> getLatestNodeHeartbeats() {
		List<NodeHeartbeatDO> heartbeatDOs = heartbeatDAO.selectLatest();
		List<NodeHeartbeat> heartbeats = new ArrayList<>(heartbeatDOs.size());
		for (NodeHeartbeatDO nodeHeartbeatDO : heartbeatDOs) {
			NodeHeartbeat h = new NodeHeartbeat();
			h.setExpireOccurtime(new Date(nodeHeartbeatDO.getExpireOccurtime()));
			h.setId(nodeHeartbeatDO.getId());
			h.setNextOccurtime(new Date(nodeHeartbeatDO.getNextOccurtime()));
			h.setNodeId(nodeHeartbeatDO.getNodeId());
			h.setOccurCount(nodeHeartbeatDO.getOccurCount());
			h.setOccurtime(new Date(nodeHeartbeatDO.getOccurtime()));
			heartbeats.add(h);
		}
		return heartbeats;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * com.mainsteam.stm.node.heartbeat.NodeHeartbeatService#getLatestNodeHearbeats
	 * (java.util.Date, java.util.Date)
	 */
	@Override
	public List<NodeHeartbeat> getNodeHeartbeats(Date start, Date end) {
		long startTime = 0;
		long endTime = Long.MAX_VALUE;
		if (start != null) {
			startTime = start.getTime();
		}
		if (end != null) {
			endTime = end.getTime();
		}
		List<NodeHeartbeatDO> heartbeatDOs = heartbeatDAO.selectByTime(
				startTime, endTime);
		List<NodeHeartbeat> heartbeats = new ArrayList<>(heartbeatDOs.size());
		for (NodeHeartbeatDO nodeHeartbeatDO : heartbeatDOs) {
			NodeHeartbeat h = new NodeHeartbeat();
			h.setExpireOccurtime(new Date(nodeHeartbeatDO.getExpireOccurtime()));
			h.setId(nodeHeartbeatDO.getId());
			h.setNextOccurtime(new Date(nodeHeartbeatDO.getNextOccurtime()));
			h.setNodeId(nodeHeartbeatDO.getNodeId());
			h.setOccurCount(nodeHeartbeatDO.getOccurCount());
			h.setOccurtime(new Date(nodeHeartbeatDO.getOccurtime()));
			heartbeats.add(h);
		}
		return heartbeats;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * com.mainsteam.stm.node.heartbeat.NodeHeartbeatService#removeNodeHearbeats
	 * (java.util.Date)
	 */
	@Override
	public int removeNodeHeartbeats(Date end) {
		if (logger.isTraceEnabled()) {
			logger.trace("removeNodeHearbeats start");
		}
		int count = heartbeatDAO.delete(end.getTime());
		if (logger.isTraceEnabled()) {
			logger.trace("removeNodeHearbeats end");
		}
		return count;
	}

	@Override
	public int removeNodeHeartbeats(int nodeId, Date end) {
		if (logger.isTraceEnabled()) {
			logger.trace("removeNodeHearbeats start nodeId=" + nodeId);
		}
		int count = heartbeatDAO.delete(nodeId, end.getTime());
		if (logger.isTraceEnabled()) {
			logger.trace("removeNodeHearbeats end.count=" + count);
		}
		return count;
	}

	@Override
	public List<NodeHeartbeatCount> selectNodeHeartbeatCounts() {
		if (logger.isTraceEnabled()) {
			logger.trace("selectNodeHeartbeatCounts start");
		}
		List<NodeHeartbeatCount> heartbeatCounts = null;
		List<NodeHeartbeatCheckDO> checkDOs = heartbeatCheckDAO
				.selectNodeHeartbeatCheckDOs();
		if (checkDOs != null && checkDOs.size() > 0) {
			heartbeatCounts = new ArrayList<>(checkDOs.size());
			for (NodeHeartbeatCheckDO nodeHeartbeatCheckDO : checkDOs) {
				NodeHeartbeatCount c = new NodeHeartbeatCount();
				c.setCheckCount(nodeHeartbeatCheckDO.getCkCount());
				c.setHeartbeatCount(nodeHeartbeatCheckDO.getHbCount());
				c.setNodeId(nodeHeartbeatCheckDO.getNodeId());
				c.setHeartbeatOccurtime(new Date(nodeHeartbeatCheckDO
						.getHbOccurtime()));
				heartbeatCounts.add(c);
			}
		}
		if (logger.isTraceEnabled()) {
			logger.trace("selectNodeHeartbeatCounts end");
		}
		return heartbeatCounts;
	}

	@Override
	public int updateNodeHeartbeatCount(List<NodeHeartbeatCount> counts) {
		if (logger.isTraceEnabled()) {
			logger.trace("updateNodeHeartbeatCount start");
		}
		if (counts == null || counts.size() <= 0) {
			if (logger.isWarnEnabled()) {
				logger.warn("updateNodeHeartbeatCount empty parameter.");
			}
			return -1;
		}
		for (NodeHeartbeatCount nodeHeartbeatCount : counts) {
			NodeHeartbeatCheckDO checkDO = new NodeHeartbeatCheckDO();
			checkDO.setCkCount(nodeHeartbeatCount.getCheckCount());
			checkDO.setHbCount(nodeHeartbeatCount.getHeartbeatCount());
			checkDO.setNodeId(nodeHeartbeatCount.getNodeId());
			checkDO.setHbOccurtime(nodeHeartbeatCount.getHeartbeatOccurtime()!=null?
					nodeHeartbeatCount.getHeartbeatOccurtime().getTime():System.currentTimeMillis());
			int size = heartbeatCheckDAO.updateNodeHeartbeatCheckDO(checkDO);
			if (size <= 0) {
				heartbeatCheckDAO.insertNodeHeartbeatCheckDO(checkDO);
			}
		}
		if (logger.isTraceEnabled()) {
			logger.trace("updateNodeHeartbeatCount end");
		}
		return 0;
	}
}
