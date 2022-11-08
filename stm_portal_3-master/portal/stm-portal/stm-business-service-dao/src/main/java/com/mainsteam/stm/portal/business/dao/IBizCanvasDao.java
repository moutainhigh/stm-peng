package com.mainsteam.stm.portal.business.dao;

import java.util.List;

import com.mainsteam.stm.portal.business.bo.BizCanvasBo;
import com.mainsteam.stm.portal.business.bo.BizCanvasLinkBo;
import com.mainsteam.stm.portal.business.bo.BizCanvasNodeBo;
import com.mainsteam.stm.portal.business.bo.BizInstanceNodeBo;
import com.mainsteam.stm.portal.business.bo.BizNodeMetricRelBo;

public interface IBizCanvasDao {

	public int insertCanvasInfo(BizCanvasBo canvas);
	
	public int insertCanvasNode(BizCanvasNodeBo canvasNode);
	
	public int insertCanvasNodeBindRelation(BizNodeMetricRelBo nodeRel);
	
	public int insertCanvasLink(BizCanvasLinkBo canvasLink);
	
	public BizCanvasBo getCanvasInfo(long canvasId);
	
	public BizCanvasBo getCanvasInfoByBizId(long bizId);
	
	public BizCanvasNodeBo getCanvasNode(long canvasNodeId);
	
	public BizInstanceNodeBo getInstanceNode(long canvasNodeId);
	
	public BizInstanceNodeBo getInstanceNodeAndRelation(long canvasNodeId);
	
	public List<BizNodeMetricRelBo> getInstanceBindRelation(long canvasNodeId);
	
	public List<BizNodeMetricRelBo> getChildInstanceBindRelation(long canvasNodeId);
	
	public List<BizNodeMetricRelBo> getChildInstanceMetricBindRelation(long canvasNodeId);
	
	public List<Long> getCanvasNodeIds(long bizId);
	
	public List<BizCanvasNodeBo> getCanvasNodes(long bizId);
	
	public List<BizCanvasNodeBo> getCanvasNodesByFileId(long fileId);
	
	public BizCanvasLinkBo getCanvasLink(long canvasLinkId);
	
	public List<Long> getCanvasLinkIdByFromNode(List<Long> array);
	
	public List<BizCanvasLinkBo> getCanvasLinksByFromNode(List<Long> array);
	
	public List<Long> getCanvasLinkIdByToNode(List<Long> array);
	
	public List<BizCanvasLinkBo> getCanvasLinksByToNode(List<Long> array);
	
	public int getCanvasLinkCountByPoints(BizCanvasLinkBo canvasLink);
	
	public int updateCanvasInfo(BizCanvasBo canvas);
	
	public int updateCanvasFileIdInfo(BizCanvasNodeBo canvasNode);
	
	public int updateCanvasNodeBaseInfo(BizCanvasNodeBo canvasNode);
	
	public int updateCanvasNodeBaseInfoByBizId(BizCanvasNodeBo canvasNode);
	
	public int updateCanvasBizMainNodeFileId(BizCanvasNodeBo canvasNode);
	
	public int updateCanvasNodeStatusInfo(BizCanvasNodeBo canvasNode);
	
	public int updateCanvasNodeStatusInfoByNodeId(BizCanvasNodeBo canvasNode);
	
	public int updateCanvasNodeStatusById(BizCanvasNodeBo canvasNode);
	
	public int updateCanvasNodeTypeInfo(BizCanvasNodeBo canvasNode);
	
	public int updateCanvasNodeAttrInfo(BizCanvasNodeBo canvasNode);
	
	public int updateCanvasLink(BizCanvasLinkBo canvasLink);
	
	public int deleteCanvasInfo(long canvasId);
	
	public int deleteCanvasInfoByBizId(long bizId);
	
	public int deleteCanvasNode(long canvasNodeId);
	
	public int deleteCanvasNodeBindRelation(long nodeId);
	
	public int deleteCanvasNodeByBizId(long bizId);
	
	public int deleteCanvasBizNodeByBizId(long bizId);
	
	public int deleteBindRelationByChildInstance(List<Long> instanceIds);
	
	public int deleteCanvasLink(long canvasLinkId);
	
	public int deleteCanvasLinkByToNode(long toNode);
	
	public int deleteCanvasLinkByFromNode(long fromNode);
	
	public int checkNodeByBizId(long bizId,List<Long> nodeIds);
	
	public List<BizInstanceNodeBo> getAllInstanceNode();
	
	public List<BizInstanceNodeBo> getInstanceNodesByBiz(long bizId);
	
	public List<BizCanvasNodeBo> getBusinessNodesByBiz(long bizId);
	
	public List<Long> getNodeIdFromBindRelation(long childInstanceId);
	
	public List<Long> getAllNodeIdFromChildInstanceBindRelation(long childInstanceId);
	
	public List<Long> getBindRelationByChildInstance(List<Long> childInstanceIds);
	
	public List<Long> getNodeIdFromBindChildMetricRelation(long childInstanceId,String metricId);
	
	public List<Long> getNodeIdFromBindMainMetricRelation(long mainInstanceId,String metricId);
	
	public int checkNodeNameIsExsit(BizCanvasNodeBo canvasNode);
	
	public int getRepeatNodeNameCount(BizCanvasNodeBo canvasNode);
	
	public List<Long> getBizIdsByInstanceId(long instanceId);
	
	
}
