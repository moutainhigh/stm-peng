package com.mainsteam.stm.portal.business.dao.impl;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.mybatis.spring.SqlSessionTemplate;

import com.mainsteam.stm.platform.dao.BaseDao;
import com.mainsteam.stm.portal.business.bo.BizCanvasBo;
import com.mainsteam.stm.portal.business.bo.BizCanvasLinkBo;
import com.mainsteam.stm.portal.business.bo.BizCanvasNodeBo;
import com.mainsteam.stm.portal.business.bo.BizInstanceNodeBo;
import com.mainsteam.stm.portal.business.bo.BizNodeMetricRelBo;
import com.mainsteam.stm.portal.business.dao.IBizCanvasDao;

public class BizCanvasDaoImpl extends BaseDao<BizCanvasBo> implements IBizCanvasDao {

	public BizCanvasDaoImpl(SqlSessionTemplate session) {
		super(session, IBizCanvasDao.class.getName());
	}

	@Override
	public int insertCanvasInfo(BizCanvasBo canvas) {
		return getSession().insert(getNamespace() + "insertCanvasInfo",canvas);
	}

	@Override
	public int insertCanvasNode(BizCanvasNodeBo canvasNode) {
		String newName = canvasNode.getShowName();
		
		if(newName != null && !newName.equals("")){
			if(newName.length() > 20){
				newName = newName.substring(0, 20);
			}
			newName = newName.replaceAll("\\(", "").replaceAll("\\)", "").replaceAll("\\*", "").replaceAll("\\{", "").replaceAll("\\}", "").replaceAll("\\$", "");
			canvasNode.setShowName(newName);
			if(canvasNode.getNodeType() == 1){
				int repeatCount = getRepeatNodeNameCount(canvasNode);
				if(repeatCount > 0){
					canvasNode.setShowName(canvasNode.getShowName() + "_" + (repeatCount + 1));
				}
			}
		}
		return getSession().insert(getNamespace() + "insertCanvasNode",canvasNode);
	}

	@Override
	public int insertCanvasLink(BizCanvasLinkBo canvasLink) {
		return getSession().insert(getNamespace() + "insertCanvasLink",canvasLink);
	}

	@Override
	public BizCanvasBo getCanvasInfo(long canvasId) {
		return getSession().selectOne(getNamespace() + "getCanvasInfo",canvasId);
	}

	@Override
	public BizCanvasBo getCanvasInfoByBizId(long bizId) {
		// TODO Auto-generated method stub
		return getSession().selectOne(getNamespace() + "getCanvasInfoByBizId",bizId);
	}

	@Override
	public BizCanvasNodeBo getCanvasNode(long canvasNodeId) {
		return getSession().selectOne(getNamespace() + "getCanvasNode",canvasNodeId);
	}
	
	@Override
	public List<Long> getCanvasNodeIds(long bizId) {
		// TODO Auto-generated method stub
		return getSession().selectList(getNamespace() + "getCanvasNodeIds",bizId);
	}

	@Override
	public List<BizCanvasNodeBo> getCanvasNodes(long bizId) {
		// TODO Auto-generated method stub
		return getSession().selectList(getNamespace() + "getCanvasNodes",bizId);
	}
	
	@Override
	public List<BizCanvasNodeBo> getCanvasNodesByFileId(long fileId) {
		// TODO Auto-generated method stub
		return getSession().selectList(getNamespace() + "getCanvasNodesByFileId",fileId);
	}

	@Override
	public BizCanvasLinkBo getCanvasLink(long canvasLinkId) {
		return getSession().selectOne(getNamespace() + "getCanvasLink",canvasLinkId);
	}
	
	@Override
	public List<Long> getCanvasLinkIdByFromNode(List<Long> array) {
		// TODO Auto-generated method stub
		return getSession().selectList(getNamespace() + "getCanvasLinkIdByFromNode", array);
	}

	@Override
	public List<BizCanvasLinkBo> getCanvasLinksByFromNode(List<Long> array) {
		// TODO Auto-generated method stub
		return getSession().selectList(getNamespace() + "getCanvasLinksByFromNode",array);
	}
	
	@Override
	public List<Long> getCanvasLinkIdByToNode(List<Long> array) {
		// TODO Auto-generated method stub
		return getSession().selectList(getNamespace() + "getCanvasLinkIdByToNode",array);
	}

	@Override
	public List<BizCanvasLinkBo> getCanvasLinksByToNode(List<Long> array) {
		// TODO Auto-generated method stub
		return getSession().selectList(getNamespace() + "getCanvasLinksByToNode",array);
	}
	
	@Override
	public int getCanvasLinkCountByPoints(BizCanvasLinkBo canvasLink) {
		// TODO Auto-generated method stub
		return getSession().selectOne(getNamespace() + "getCanvasLinkCountByPoints",canvasLink);
	}

	@Override
	public int updateCanvasInfo(BizCanvasBo canvas) {
		return getSession().update(getNamespace() + "updateCanvasInfo",canvas);
	}
	
	@Override
	public int updateCanvasFileIdInfo(BizCanvasNodeBo canvasNode) {
		return getSession().update(getNamespace() + "updateCanvasFileIdInfo",canvasNode);
	}

	@Override
	public int updateCanvasNodeBaseInfo(BizCanvasNodeBo canvasNode) {
		if(checkNodeNameIsExsit(canvasNode) > 0){
			return -1;
		}
		return getSession().update(getNamespace() + "updateCanvasNodeBaseInfo",canvasNode);
	}
	
	@Override
	public int updateCanvasNodeBaseInfoByBizId(BizCanvasNodeBo canvasNode) {
		return getSession().update(getNamespace() + "updateCanvasNodeBaseInfoByBizId",canvasNode);
	}
	
	@Override
	public int updateCanvasBizMainNodeFileId(BizCanvasNodeBo canvasNode) {
		// TODO Auto-generated method stub
		return getSession().update(getNamespace() + "updateCanvasBizMainNodeFileId",canvasNode);
	}
	
	@Override
	public int updateCanvasNodeStatusInfo(BizCanvasNodeBo canvasNode) {
		return getSession().update(getNamespace() + "updateCanvasNodeStatusInfo",canvasNode);
	}
	
	@Override
	public int updateCanvasNodeStatusInfoByNodeId(BizCanvasNodeBo canvasNode) {
		// TODO Auto-generated method stub
		
		return getSession().update(getNamespace() + "updateCanvasNodeStatusInfoByNodeId",canvasNode);
	}

	@Override
	public int updateCanvasNodeStatusById(BizCanvasNodeBo canvasNode) {
		// TODO Auto-generated method stub
		return getSession().update(getNamespace() + "updateCanvasNodeStatusById",canvasNode);
	}
	
	@Override
	public int updateCanvasNodeTypeInfo(BizCanvasNodeBo canvasNode) {
		return getSession().update(getNamespace() + "updateCanvasNodeTypeInfo",canvasNode);
	}
	
	@Override
	public int updateCanvasNodeAttrInfo(BizCanvasNodeBo canvasNode) {
		return getSession().update(getNamespace() + "updateCanvasNodeAttrInfo",canvasNode);
	}

	@Override
	public int updateCanvasLink(BizCanvasLinkBo canvasLink) {
		return getSession().update(getNamespace() + "updateCanvasLink",canvasLink);
	}

	@Override
	public int deleteCanvasInfo(long canvasId) {
		return getSession().delete(getNamespace() + "deleteCanvasInfo",canvasId);
	}

	@Override
	public int deleteCanvasNode(long canvasNodeId) {
		return getSession().delete(getNamespace() + "deleteCanvasNode",canvasNodeId);
	}

	@Override
	public int deleteCanvasLink(long canvasLinkId) {
		return getSession().delete(getNamespace() + "deleteCanvasLink",canvasLinkId);
	}

	@Override
	public int deleteCanvasInfoByBizId(long bizId) {
		return getSession().delete(getNamespace() + "deleteCanvasInfoByBizId",bizId);
	}

	@Override
	public int deleteCanvasNodeByBizId(long bizId) {
		return getSession().delete(getNamespace() + "deleteCanvasNodeByBizId",bizId);
	}
	
	@Override
	public int deleteCanvasBizNodeByBizId(long bizId) {
		// TODO Auto-generated method stub
		return getSession().delete(getNamespace() + "deleteCanvasBizNodeByBizId",bizId);
	}

	@Override
	public int deleteCanvasLinkByToNode(long toNode) {
		return getSession().delete(getNamespace() + "deleteCanvasLinkByToNode",toNode);
	}

	@Override
	public int deleteCanvasLinkByFromNode(long fromNode) {
		return getSession().delete(getNamespace() + "deleteCanvasLinkByFromNode",fromNode);
	}

	/**
	 * 检查指定节点是否在指定业务内
	 */
	@Override
	public int checkNodeByBizId(long bizId,List<Long> nodeIds) {
		Map<String, Object> map = new HashMap<String, Object>();
		map.put("bizId", bizId);
		map.put("list", nodeIds);
		return getSession().selectOne(getNamespace() + "checkNodeByBizId",map);
	}

	@Override
	public BizInstanceNodeBo getInstanceNode(long canvasNodeId) {
		BizInstanceNodeBo instanceNode = getSession().selectOne(getNamespace() + "getInstanceNode",canvasNodeId);
		List<BizNodeMetricRelBo> relList = getInstanceBindRelation(canvasNodeId);
		if(relList == null || relList.size() <= 0){
			instanceNode.setType(1);
		}else{
			String metricId = relList.get(0).getMetricId();
			if(metricId != null && !metricId.equals("")){
				instanceNode.setType(3);
			}else{
				instanceNode.setType(2);
			}
		}
		return instanceNode;
	}
	
	@Override
	public BizInstanceNodeBo getInstanceNodeAndRelation(long canvasNodeId) {
		BizInstanceNodeBo instanceNode = getSession().selectOne(getNamespace() + "getInstanceNode",canvasNodeId);
		List<BizNodeMetricRelBo> relList = getInstanceBindRelation(canvasNodeId);
		if(relList == null || relList.size() <= 0){
			instanceNode.setType(1);
		}else{
			String metricId = relList.get(0).getMetricId();
			if(metricId != null && !metricId.equals("")){
				instanceNode.setType(3);
			}else{
				instanceNode.setType(2);
			}
		}
		instanceNode.setBind(relList);
		return instanceNode;
	}

	@Override
	public List<BizNodeMetricRelBo> getInstanceBindRelation(long canvasNodeId) {
		// TODO Auto-generated method stub
		return getSession().selectList(getNamespace() + "getInstanceBindRelation",canvasNodeId);
	}
	
	@Override
	public List<BizNodeMetricRelBo> getChildInstanceBindRelation(
			long canvasNodeId) {
		// TODO Auto-generated method stub
		return getSession().selectList(getNamespace() + "getChildInstanceBindRelation",canvasNodeId);
	}

	@Override
	public List<BizNodeMetricRelBo> getChildInstanceMetricBindRelation(
			long canvasNodeId) {
		// TODO Auto-generated method stub
		return getSession().selectList(getNamespace() + "getChildInstanceMetricBindRelation",canvasNodeId);
	}

	@Override
	public int insertCanvasNodeBindRelation(BizNodeMetricRelBo nodeRel) {
		// TODO Auto-generated method stub
		return getSession().insert(getNamespace() + "insertCanvasNodeBindRelation",nodeRel);
	}

	@Override
	public int deleteCanvasNodeBindRelation(long nodeId) {
		return getSession().delete(getNamespace() + "deleteCanvasNodeBindRelation",nodeId);
	}

	@Override
	public List<BizInstanceNodeBo> getAllInstanceNode() {
		
		List<BizInstanceNodeBo> nodes = getSession().selectList(getNamespace() + "getAllInstanceNode");
		
		if(nodes == null){
			return new ArrayList<BizInstanceNodeBo>();
		}
		
		for(BizInstanceNodeBo node : nodes){
			
			List<BizNodeMetricRelBo> relList = getInstanceBindRelation(node.getId());
			if(relList == null || relList.size() <= 0){
				node.setType(1);
			}else{
				String metricId = relList.get(0).getMetricId();
				if(metricId != null && !metricId.equals("")){
					node.setType(3);
				}else{
					node.setType(2);
				}
			}
		}
		
		return nodes;
	}

	@Override
	public List<BizInstanceNodeBo> getInstanceNodesByBiz(long bizId) {
		
		List<BizInstanceNodeBo> nodes = getSession().selectList(getNamespace() + "getInstanceNodesByBiz",bizId);
		
		if(nodes == null){
			return new ArrayList<BizInstanceNodeBo>();
		}
		
		for(BizInstanceNodeBo node : nodes){
			
			List<BizNodeMetricRelBo> relList = getInstanceBindRelation(node.getId());
			if(relList == null || relList.size() <= 0){
				node.setType(1);
			}else{
				String metricId = relList.get(0).getMetricId();
				if(metricId != null && !metricId.equals("")){
					node.setType(3);
				}else{
					node.setType(2);
				}
			}
			node.setBind(relList);
		}
		
		return nodes;
	}
	

	@Override
	public List<BizCanvasNodeBo> getBusinessNodesByBiz(long bizId) {
		// TODO Auto-generated method stub
		return getSession().selectList(getNamespace() + "getBusinessNodesByBiz",bizId);
	}
	
	/**
	 * 获取所有绑定指定子资源的节点
	 */
	@Override
	public List<Long> getNodeIdFromBindRelation(long childInstanceId) {
		// TODO Auto-generated method stub
		return getSession().selectList(getNamespace() + "getNodeIdFromBindRelation",childInstanceId);
	}
	
	/**
	 * 获取所有和指定子资源有关的节点
	 */
	@Override
	public List<Long> getAllNodeIdFromChildInstanceBindRelation(
			long childInstanceId) {
		return getSession().selectList(getNamespace() + "getAllNodeIdFromChildInstanceBindRelation",childInstanceId);
	}
	
	/**
	 * 获取所有绑定指定子资源和指定指标的节点
	 */
	@Override
	public List<Long> getNodeIdFromBindChildMetricRelation(long childInstanceId,String metricId) {
		// TODO Auto-generated method stub
		BizNodeMetricRelBo rel = new BizNodeMetricRelBo();
		rel.setChildInstanceId(childInstanceId);
		rel.setMetricId(metricId);
		return getSession().selectList(getNamespace() + "getNodeIdFromBindChildMetricRelation",rel);
	}
	
	/**
	 * 获取所有绑定指定主资源和指定指标的节点
	 */
	@Override
	public List<Long> getNodeIdFromBindMainMetricRelation(long mainInstanceId,String metricId) {
		// TODO Auto-generated method stub
		BizNodeMetricRelBo rel = new BizNodeMetricRelBo();
		rel.setChildInstanceId(mainInstanceId);
		rel.setMetricId(metricId);
		return getSession().selectList(getNamespace() + "getNodeIdFromBindMainMetricRelation",rel);
	}

	@Override
	public int checkNodeNameIsExsit(BizCanvasNodeBo canvasNode) {
		return getSession().selectOne(getNamespace() + "checkNodeNameIsExsit",canvasNode);
	}

	@Override
	public int getRepeatNodeNameCount(BizCanvasNodeBo canvasNode) {
		return getSession().selectOne(getNamespace() + "getRepeatNodeNameCount",canvasNode);
	}

	@Override
	public List<Long> getBindRelationByChildInstance(List<Long> list) {
		// TODO Auto-generated method stub
		return getSession().selectList(getNamespace() + "getBindRelationByChildInstance",list);
	}

	@Override
	public int deleteBindRelationByChildInstance(List<Long> list) {
		// TODO Auto-generated method stub
		return getSession().delete(getNamespace() + "deleteBindRelationByChildInstance",list);
	}

	@Override
	public List<Long> getBizIdsByInstanceId(long instanceId) {
		// TODO Auto-generated method stub
		return getSession().selectList(getNamespace() + "getBizIdsByInstanceId",instanceId);
	}

}
