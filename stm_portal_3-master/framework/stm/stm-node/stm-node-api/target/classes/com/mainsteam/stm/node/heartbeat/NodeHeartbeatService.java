/**
 * 
 */
package com.mainsteam.stm.node.heartbeat;

import java.util.Date;
import java.util.List;

/**
 * 
 * 为节点心跳维护提供接口
 * 
 * @author ziw
 * 
 */
public interface NodeHeartbeatService {

	/**
	 * 新增一条心跳记录
	 * 
	 * @param heart
	 */
	public void addNodeHeartbeat(NodeHeartbeat heart);

	/**
	 * 查询最近的心跳记录
	 * 
	 * @return List<NodeHeartbeat>
	 */
	public List<NodeHeartbeat> getLatestNodeHeartbeats();

	/**
	 * 查询指定时间段内产生的心跳记录
	 * 
	 * @param start
	 * @param end
	 * @return List<NodeHeartbeat>
	 */
	public List<NodeHeartbeat> getNodeHeartbeats(Date start, Date end);

	/**
	 * 删除指定时间前的心跳记录
	 * 
	 * @param end
	 *            指定的时间点
	 * @return int
	 */
	public int removeNodeHeartbeats(Date end);

	/**
	 * 删除指定时间前的心跳记录
	 * 
	 * @param nodeId
	 * @param end
	 * @return
	 */
	public int removeNodeHeartbeats(int nodeId, Date end);

	/**
	 * 更新心跳的检查记录
	 * 
	 * @param heartbeatCount
	 *            心跳的检查记录
	 * @return
	 */
	public int updateNodeHeartbeatCount(List<NodeHeartbeatCount> heartbeatCount);

	/**
	 * 获取所有的心跳检查记录
	 * 
	 * @return List<NodeHeartbeatCount>
	 */
	public List<NodeHeartbeatCount> selectNodeHeartbeatCounts();
}
