/**
 * 
 */
package com.mainsteam.stm.node.job;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.quartz.Job;
import org.quartz.JobExecutionContext;
import org.quartz.JobExecutionException;

import com.mainsteam.stm.job.IJob;
import com.mainsteam.stm.job.ScheduleManager;
import com.mainsteam.stm.node.Node;
import com.mainsteam.stm.node.NodeGroup;
import com.mainsteam.stm.node.NodeService;
import com.mainsteam.stm.node.NodeTable;
import com.mainsteam.stm.node.exception.NodeException;
import com.mainsteam.stm.node.heartbeat.NodeHeartbeat;
import com.mainsteam.stm.node.heartbeat.NodeHeartbeatCount;
import com.mainsteam.stm.node.heartbeat.NodeHeartbeatService;
import com.mainsteam.stm.util.OSUtil;
import com.mainsteam.stm.util.SpringBeanUtil;

/**
 * @author ziw
 * 
 */
@org.quartz.DisallowConcurrentExecution
public class NodeStateComputeJob implements Job,Serializable {

	/**
	 * 防止每次build时，升级环境时，造成Job不能使用
	 */
	private static final long serialVersionUID = -2343806849629516569L;

	private static final Log logger = LogFactory.getLog(NodeStateComputeJob.class);

//	private static final int LEAVE_TIME_OFFSET = 7 * 24*60*60*1000;// 7天

	/**心跳只保留1天的数据*/
	private static final int LEAVE_TIME_OFFSET = 1 * 24*60*60*1000;// 7天

	private static long HEARTBEAT_INVALID_COUNT = 10;

	private ScheduleManager scheduleManager;

	public void setScheduleManager(ScheduleManager scheduleManager) {
		this.scheduleManager = scheduleManager;
	}

	/**
	 * 
	 */
	public NodeStateComputeJob() {
	}

	public void start() {
		if (OSUtil.getEnv("testCase") != null) {
			return;
		}
		if (logger.isInfoEnabled()) {
			logger.info("start NodeStateComputeJob.");
		}
		try {
			
			// 每隔10秒启动一个Job，扫描计算节点状态
			scheduleManager.scheduleJob(new IJob(this, "*/10 * * * * ?"));
		} catch (Exception e) {
			if (logger.isErrorEnabled()) {
				logger.error("NodeStateComputeJob error!", e);
			}
		}
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.quartz.Job#execute(org.quartz.JobExecutionContext)
	 */
	@Override
	public void execute(JobExecutionContext context)
			throws JobExecutionException {
		Object bean = SpringBeanUtil.getObject("nodeHeartbeatService");
		if (bean == null) {
			if (logger.isWarnEnabled()) {
				logger.warn("execute nodeHeartbeatService is null.");
			}
			return;
		}else {
			if (logger.isInfoEnabled()) {
				logger.info("NodeStateComputeJob execute start.");
			}
		}
		NodeHeartbeatService nodeHeartbeatService = (NodeHeartbeatService) bean;

		List<NodeHeartbeat> heartbeats = nodeHeartbeatService
				.getLatestNodeHeartbeats();
		List<NodeHeartbeatCount> heartbeatCounts = nodeHeartbeatService
				.selectNodeHeartbeatCounts();
		NodeService nodeService = (NodeService) SpringBeanUtil
				.getObject("nodeService");
		NodeTable table;
		try {
			table = nodeService.getNodeTable();
		} catch (NodeException e) {
			if (logger.isErrorEnabled()) {
				logger.error("execute", e);
			}
			return;
		}
		List<NodeGroup> groups = table.getGroups();
		if (groups == null || groups.size() <= 0) {
			if (logger.isErrorEnabled()) {
				logger.error("execute groups is empty.");
			}
			return;
		}
		Map<Integer, NodeHeartbeatCount> nodeHeartbeatCountMap = new HashMap<Integer, NodeHeartbeatCount>();
		if (heartbeatCounts != null && heartbeatCounts.size() > 0) {
			for (NodeHeartbeatCount count : heartbeatCounts) {
				nodeHeartbeatCountMap.put(count.getNodeId(), count);
			}
		}

		Map<Integer, NodeHeartbeat> nodeHeartbeatMap = new HashMap<Integer, NodeHeartbeat>();
		if (heartbeats != null && heartbeats.size() > 0) {
			for (NodeHeartbeat nodeHeartbeat : heartbeats) {
				nodeHeartbeatMap.put(nodeHeartbeat.getNodeId(), nodeHeartbeat);
			}
		}

		List<Node> changedStateNodes = new ArrayList<Node>();
		List<NodeHeartbeatCount> updateCounts = new ArrayList<NodeHeartbeatCount>();

		for (NodeGroup nodeGroup : groups) {
			List<Node> nodes = nodeGroup.getNodes();
			if (nodes != null && nodes.size() > 0) {
				for (Node node : nodes) {
					Integer nodeId = node.getId();
					boolean oldState = node.isAlive();
					NodeHeartbeat nodeHeartbeat = nodeHeartbeatMap.get(nodeId);
					long thisHeartbeatCount = 0;
					Date thisOccurTime = null;
					if (nodeHeartbeat != null) {
						thisHeartbeatCount = nodeHeartbeat.getOccurCount();
						thisOccurTime = nodeHeartbeat.getOccurtime();
					}
					NodeHeartbeatCount counter = nodeHeartbeatCountMap
							.get(nodeId);
					if (counter == null) {
						counter = new NodeHeartbeatCount();
						counter.setCheckCount(oldState ? 0 : HEARTBEAT_INVALID_COUNT);
						counter.setNodeId(nodeId);
						counter.setHeartbeatCount(thisHeartbeatCount);
						counter.setHeartbeatOccurtime(thisOccurTime == null ? new Date()
								: thisOccurTime);
						updateCounts.add(counter);
						continue;
					} else {
						updateCounts.add(counter);
					}

					if (counter.getHeartbeatCount() == thisHeartbeatCount
							&& ((thisOccurTime == null) || (counter
									.getHeartbeatOccurtime().getTime() == thisOccurTime
									.getTime()))) {
						counter.setCheckCount(counter.getCheckCount() + 1);
						if (counter.getCheckCount() >= HEARTBEAT_INVALID_COUNT) {
							// unactive
							if (oldState == false) {
								if (logger.isInfoEnabled()) {
									StringBuilder b = new StringBuilder(
											"execute node is not startup.stop continuly.nodeId=");
									b.append(nodeId);
									b.append(" checkCount=").append(
											counter.getCheckCount());
									logger.info(b.toString());
								}
								continue;
							}
							Node n = new Node();
							n.setId(nodeId.intValue());
							n.setAlive(false);
							changedStateNodes.add(n);
							if (logger.isInfoEnabled()) {
								StringBuilder b = new StringBuilder(
										"node is change to unAlive.nodeId=");
								b.append(nodeId);
								b.append(" checkCount=").append(
										counter.getCheckCount());
								logger.info(b.toString());
							}
						}
					} else {
						counter.setHeartbeatOccurtime(thisOccurTime);
						counter.setHeartbeatCount(thisHeartbeatCount);
						counter.setCheckCount(0);
						if (oldState == false) {
							// active
							Node n = new Node();
							n.setId(nodeId.intValue());
							n.setAlive(true);
							if (logger.isInfoEnabled()) {
								logger.info("node is change to alive.nodeId="
										+ nodeId);
							}
							changedStateNodes.add(n);
						}
					}
				}
			}
		}
		if (updateCounts.size() > 0) {
			nodeHeartbeatService.updateNodeHeartbeatCount(updateCounts);
		}
		if (changedStateNodes.size() > 0) {
			nodeService.updateNodesState(changedStateNodes);
		}

		/**
		 * 系统刚刚启动或者刚过凌晨0点的时候，执行一次
		 */
		boolean isRunDelete = context != null
				&& (context.getRefireCount() <= 1 || isFirstFireAfterDaily(
						context.getPreviousFireTime(), context.getFireTime()));
		Date end = null;
		if (isRunDelete) {
			/**
			 * 删除7前天的数据。
			 */
			end = new Date(new Date().getTime() - LEAVE_TIME_OFFSET);
			try {
				nodeHeartbeatService.removeNodeHeartbeats(end);
			} catch (Exception e) {
				if (logger.isErrorEnabled()) {
					logger.error("removeNodeHearbeats", e);
				}
			}
		}
		if (logger.isInfoEnabled()) {
			logger.info("NodeStateComputeJob execute end.");
		}
	}

	private boolean isFirstFireAfterDaily(Date pre, Date current) {
		Calendar c = Calendar.getInstance();
		c.setTimeInMillis(current.getTime());
		int currentHour = c.get(Calendar.HOUR_OF_DAY);
		if (currentHour > 0) {
			return false;
		}
		int minute = c.get(Calendar.MINUTE);
		if (minute > 0) {
			return false;
		}
		c.setTimeInMillis(pre.getTime());
		int lastHoure = c.get(Calendar.HOUR_OF_DAY);
		return lastHoure > 0;
	}
}
