package com.mainsteam.stm.portal.business.api;

import java.util.List;
import java.util.Map;
import java.util.Set;

import com.mainsteam.stm.portal.business.bo.BizCanvasDataBo;
import com.mainsteam.stm.portal.business.bo.BizCanvasLinkBo;
import com.mainsteam.stm.portal.business.bo.BizCanvasNodeBo;
import com.mainsteam.stm.portal.business.bo.BizInstanceNodeTree;
import com.mainsteam.stm.portal.business.bo.BizInstanceNodeBo;
import com.mainsteam.stm.portal.business.bo.BizNodeMetricRelBo;

public interface BizCanvasApi {

	public boolean deleteCanvas(long bizId);
	
	public long insertCanvasNode(BizCanvasNodeBo canvasNode);
	
	public long insertCanvasLink(BizCanvasLinkBo canvasLink);
	
	public boolean updateCanvas(BizCanvasDataBo canvasData);
	
	public BizCanvasDataBo getCanvas(long bizId);
	
	public List<BizCanvasNodeBo> getCanvasNode(long bizId);
	
	public BizCanvasNodeBo getCanvasNodeById(long id);
	
	public boolean deleteCanvasNode(long[] ids);
	
	public boolean deleteCanvasLink(String[] ids);
	
	public BizInstanceNodeBo getInstanceNode(long canvasNodeId);
	
	public List<BizInstanceNodeTree> getSelectChildInstanceTree(long canvasNodeId);
	
	public List<BizInstanceNodeTree> getUnSelectChildInstanceTree(long canvasNodeId,String childResource,String searchContent);
	
	public List<BizInstanceNodeTree> getSelectMetricTree(long canvasNodeId);
	
	public List<BizInstanceNodeTree> getUnSelectMetricTree(long canvasNodeId,String childResource,String searchContent);
	
	public boolean updateInstanceNode(BizInstanceNodeBo instanceNode);
	
	/**
	 * 获取业务响应时间的url资源
	 * @param bizId
	 * @return
	 */
	public Set<Long> getResponeTimeMetricInstanceList(long bizId);
	
	/**
	 * 获取业务计算容量的主机(包括虚拟机)资源
	 * @param bizId
	 * @return
	 */
	public Set<Long> getCalculateMetricInstanceList(long bizId);
	
	/**
	 * 获取业务存储主机、虚拟机和存储设备的硬盘子资源
	 * @param bizId
	 * @return
	 */
	public Set<Long> getStoreMetricInstanceList(long bizId);
	
	/**
	 * 获取业务数据库表空间子资源
	 * @param bizId
	 * @return
	 */
	public Set<Long> getDatabaseMetricInstanceList(long bizId);
	
	/**
	 * 获取业务网络设备接口子资源
	 * @param bizId
	 * @return
	 */
	public Set<Long> getBandwidthMetricInstanceList(long bizId);
	
	public List<Map<String, Object>> getChildResourceList(long instanceId);
	
	public List<BizCanvasNodeBo> getNodeListByBiz(long bizId);
	
	public BizCanvasNodeBo getSingleInstanceNodeType(long nodeId);
	
	/**
	 * 根据节点ID查找所有绑定的子资源
	 * @param nodeId
	 * @return
	 */
	public List<BizNodeMetricRelBo> getChildInstanceByNodeId(long nodeId);
	
	/**
	 * 根据节点ID查找所有绑定的指标
	 * @param nodeId
	 * @return
	 */
	public List<BizNodeMetricRelBo> getMetricByNodeId(long nodeId);
	
}
