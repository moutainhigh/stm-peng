package com.mainsteam.stm.portal.business.web.action;


import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.annotation.Resource;

import org.apache.log4j.Logger;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;

import com.alibaba.fastjson.JSONObject;
import com.mainsteam.stm.common.instance.obj.InstanceStateEnum;
import com.mainsteam.stm.common.metric.InstanceStateService;
import com.mainsteam.stm.instancelib.ResourceInstanceService;
import com.mainsteam.stm.instancelib.exception.InstancelibException;
import com.mainsteam.stm.instancelib.obj.ResourceInstance;
import com.mainsteam.stm.instancelib.objenum.InstanceLifeStateEnum;
import com.mainsteam.stm.platform.file.bean.FileConstantEnum;
import com.mainsteam.stm.platform.web.action.BaseAction;
import com.mainsteam.stm.portal.business.api.BizCanvasApi;
import com.mainsteam.stm.portal.business.api.BizHealthHisApi;
import com.mainsteam.stm.portal.business.api.BizMainApi;
import com.mainsteam.stm.portal.business.bo.BizCanvasDataBo;
import com.mainsteam.stm.portal.business.bo.BizCanvasLinkBo;
import com.mainsteam.stm.portal.business.bo.BizCanvasNodeBo;
import com.mainsteam.stm.portal.business.bo.BizInstanceNodeBo;
import com.mainsteam.stm.portal.business.bo.BizNodeMetricRelBo;
import com.mainsteam.stm.portal.business.service.util.BizNodeTypeDefine;
import com.mainsteam.stm.portal.business.service.util.BizStatusDefine;
import com.mainsteam.stm.portal.resource.api.IResourceApplyApi;
import com.mainsteam.stm.state.obj.InstanceStateData;
/**
 * <li>文件名称: BizSelfAction.java</li>
 * <li>公　　司: 武汉美新翔盛科技有限公司</li>
 * <li>版权所有: 版权所有(C)2019-2020</li>
 * <li>修改记录: ...</li>
 * <li>内容摘要: ...</li>
 * <li>其他说明: ...</li>
 * @version  ms.stm
 * @since    2016年8月25日
 * @author   pengl
 */
@Controller
@RequestMapping("/portal/business/canvas")
public class BizCanvasAction extends BaseAction{
	
	@Resource
	private BizCanvasApi bizCanvasApi;
	
	@Resource
	private BizMainApi bizMainApi;
	
	@Resource
	private BizHealthHisApi bizHealthHisApi;
	
	@Resource
	private InstanceStateService instanceStateService;
	
	@Resource
	private ResourceInstanceService resourceInstanceService;
	
	@Resource
	private IResourceApplyApi resourceApplyApi;
	
	private Logger logger = Logger.getLogger(BizCanvasAction.class);
	
	/**
	 * 获取业务绘图数据
	 * @param bizId
	 * @return
	 */
	@RequestMapping("/getCanvasData")
	public JSONObject getCanvasData(long bizId){
		return toSuccess(bizCanvasApi.getCanvas(bizId));
	}
	
	/**
	 * 修改业务绘图数据
	 * @param bizId
	 * @return
	 */
	@RequestMapping("/updateCanvas")
	public JSONObject updateCanvas(String canvasData){
		BizCanvasDataBo data = JSONObject.parseObject(canvasData,BizCanvasDataBo.class);
		return toSuccess(bizCanvasApi.updateCanvas(data));
	}
	
	/**
	 * 批量添加节点
	 * @param bizId
	 * @return
	 */
	@RequestMapping("/insertCanvasNode")
	public JSONObject insertCanvasNode(String canvasData){
		
		BizCanvasDataBo data = JSONObject.parseObject(canvasData,BizCanvasDataBo.class);
		List<BizCanvasNodeBo> nodes = data.getNodes();
		
		boolean isCalculateHealth = false;
		boolean isAddChildBiz = false;
		
		for(BizCanvasNodeBo node : nodes){
			
			if(node.getNodeType() == BizNodeTypeDefine.BUSINESS_TYPE){
				isCalculateHealth = true;
				isAddChildBiz = true;
				node.setNodeStatus(bizHealthHisApi.getBizStatus(node.getInstanceId()));
				node.setStatusTime(new Date());
			}
			
			if(node.getNodeType() == BizNodeTypeDefine.INSTANCE_TYPE){
				isCalculateHealth = true;
				InstanceStateData instanceStateData = instanceStateService.getStateAdapter(node.getInstanceId());
				if(instanceStateData != null){
					node.setNodeStatus(getInstanceState(instanceStateData.getState()));
				}else{
					node.setNodeStatus(BizStatusDefine.NORMAL_STATUS);
				}
				node.setStatusTime(new Date());
			}
			
			long newNodeId = bizCanvasApi.insertCanvasNode(node);
			
			if(newNodeId <= 0){
				return toSuccess(false);
			}
			
			node.setId(newNodeId);
			
		}
		
		if(isCalculateHealth){
			//触发健康度计算
			bizMainApi.calculateBizHealth(nodes.get(0).getBizId(),1,isAddChildBiz,null,-1);
		}
		
		return toSuccess(nodes);
	}
	
	/**
	 * 批量添加连线
	 * @param bizId
	 * @return
	 */
	@RequestMapping("/insertCanvasLink")
	public JSONObject insertCanvasLink(String link){
		BizCanvasLinkBo data = JSONObject.parseObject(link,BizCanvasLinkBo.class);
		return toSuccess(bizCanvasApi.insertCanvasLink(data));
	}
	
	/**
	 * 批量删除连线
	 * @param bizId
	 * @return
	 */
	@RequestMapping("/deleteCanvasLink")
	public JSONObject deleteCanvasLink(String ids){
		
		if(ids == null || ids.equals("")){
			return toSuccess(false);
		}
		String[] idArray = ids.split(",");

		return toSuccess(bizCanvasApi.deleteCanvasLink(idArray));
	}
	
	private int getInstanceState(InstanceStateEnum state){
		if(state == InstanceStateEnum.CRITICAL){
			return BizStatusDefine.DEATH_STATUS;
		}else if(state == InstanceStateEnum.SERIOUS){
			return BizStatusDefine.SERIOUS_STATUS;
		}else if(state == InstanceStateEnum.WARN){
			return BizStatusDefine.WARN_STATUS;
		}else if(state == InstanceStateEnum.NORMAL){
			return BizStatusDefine.NORMAL_STATUS;
		}
		return BizStatusDefine.NORMAL_STATUS;
	}
	
	/**
	 * 批量删除节点
	 * @param bizId
	 * @return
	 */
	@RequestMapping("/deleteCanvasNode")
	public JSONObject deleteCanvasNode(String ids){
		
		if(ids == null || ids.equals("")){
			return toSuccess(false);
		}
		String[] idArray = ids.split(",");
		long[] idList = new long[idArray.length];
		for(int i = 0 ; i < idArray.length ; i ++){
			idList[i] = Long.parseLong(idArray[i]);
		}
		
		return toSuccess(bizCanvasApi.deleteCanvasNode(idList));
	}
	
	/**
	 * 获取资源节点的基本信息
	 * @param bizId
	 * @return
	 */
	@RequestMapping("/getInstanceNode")
	public JSONObject getInstanceNode(long canvasNodeId){
		return toSuccess(bizCanvasApi.getInstanceNode(canvasNodeId));
	}
	
	/**
	 * 获取已经选中的子资源实例
	 */
	@RequestMapping("/getSelectChildInstanceTree")
	public JSONObject getSelectChildInstanceTree(long canvasNodeId){
		return toSuccess(bizCanvasApi.getSelectChildInstanceTree(canvasNodeId));
	}
	
	/**
	 * 获取已经选中的指标实例
	 */
	@RequestMapping("/getSelectMetricTree")
	public JSONObject getSelectMetricTree(long canvasNodeId){
		return toSuccess(bizCanvasApi.getSelectMetricTree(canvasNodeId));
	}
	
	/**
	 * 获取未选中的子资源实例
	 */
	@RequestMapping("/getUnSelectChildInstanceTree")
	public JSONObject getUnSelectChildInstanceTree(long canvasNodeId,String childResource,String searchContent){
		return toSuccess(bizCanvasApi.getUnSelectChildInstanceTree(canvasNodeId,childResource,searchContent));
	}
	
	/**
	 * 获取未选中的指标实例
	 */
	@RequestMapping("/getUnSelectMetricTree")
	public JSONObject getUnSelectMetricTree(long canvasNodeId,String childResource,String searchContent){
		return toSuccess(bizCanvasApi.getUnSelectMetricTree(canvasNodeId,childResource,searchContent));
	}
	
	/**
	 * 修改资源节点信息
	 * @return
	 */
	@RequestMapping("/updateInstanceNode")
	public JSONObject updateInstanceNode(String instanceNode){
		
		BizInstanceNodeBo data = JSONObject.parseObject(instanceNode,BizInstanceNodeBo.class);
		
		return toSuccess(bizCanvasApi.updateInstanceNode(data));
	}
	
	/**
	 * 获取资源的子资源类型
	 * @return
	 */
	@RequestMapping("/getChildResourceList")
	public JSONObject getChildResourceList(long instanceId){
		
		return toSuccess(bizCanvasApi.getChildResourceList(instanceId));
	}
	
	/**
	 * 获取业务的子业务和资源节点
	 * @return
	 */
	@RequestMapping("/getNodeListByBiz")
	public JSONObject getNodeListByBiz(long bizId){
		
		return toSuccess(bizCanvasApi.getNodeListByBiz(bizId));
	}
	
	
	/**
	 * 获取单个资源节点的类型
	 * @return
	 */
	@RequestMapping("/getSingleInstanceNodeType")
	public JSONObject getSingleInstanceNodeType(long nodeId){
		
		return toSuccess(bizCanvasApi.getSingleInstanceNodeType(nodeId));
		
	}
	
	/**
	 * 根据节点ID查找所有绑定的子资源
	 * @param nodeId
	 * @return
	 */
	@RequestMapping("/getChildInstanceByNodeId")
	public JSONObject getChildInstanceByNodeId(long nodeId){
		
		return toSuccess(bizCanvasApi.getChildInstanceByNodeId(nodeId));
	}
	
	/**
	 * 根据节点ID,主资源id,查找子资源分类信息
	 * @param nodeId
	 * @return
	 */
	@RequestMapping("/getChildInsTypeInfoByNodeId")
	public JSONObject getChildInsTypeInfoByNodeId(long nodeId,long parentInstanceId){
		try {
			ResourceInstance parent = resourceInstanceService.getResourceInstance(parentInstanceId);
			List<ResourceInstance> riList = parent.getChildren();
			
			List<BizNodeMetricRelBo> bnmList = bizCanvasApi.getChildInstanceByNodeId(nodeId);
			Map<String,List<Map<String,Object>>> resultMap = new HashMap<String,List<Map<String,Object>>>();
			for(BizNodeMetricRelBo bnm:bnmList){
				for(ResourceInstance ri:riList){
					if(ri.getId()==bnm.getChildInstanceId()){
						Map<String,Object> map = new HashMap<String,Object>();
						map.put("nodeId", bnm.getNodeId());
						map.put("childInstanceId", bnm.getChildInstanceId());
						map.put("childInstanceType", ri.getChildType());
						InstanceLifeStateEnum ils = ri.getLifeState();
						if(null!=ils){
							map.put("isMonitor", ils==InstanceLifeStateEnum.MONITORED);
						}else{
							map.put("isMonitor", false);
						}
						
						if(resultMap.containsKey(ri.getChildType())){
							resultMap.get(ri.getChildType()).add(map);
						}else{
							List<Map<String,Object>> mapList = new ArrayList<Map<String,Object>>();
							mapList.add(map);
							resultMap.put(ri.getChildType(), mapList);
						}
						break;
					}
				}
			}
			
			return toSuccess(resultMap);
		} catch (InstancelibException e) {
			return null;
		}
		
	}
	
	/**
	 * 根据节点ID查找所有绑定的指标
	 * @param nodeId
	 * @return
	 */
	@RequestMapping("/getMetricByNodeId")
	public JSONObject getMetricByNodeId(long nodeId){
		
		return toSuccess(bizCanvasApi.getMetricByNodeId(nodeId));
	}
	
	/**
	 * 根据节点ID获取绑定指标信息
	 * @param nodeId
	 * @return
	 */
	@RequestMapping("/getMetricInfoByNodeId")
	public JSONObject getMetricInfoByNodeId(long nodeId,long parentInstanceId){
		List<BizNodeMetricRelBo> BnmList = bizCanvasApi.getMetricByNodeId(nodeId);
		Map<Long ,List<String>> queryMap = new HashMap<Long ,List<String>>();
		if(null==BnmList||BnmList.size()==0){
			return toSuccess(null);
		}
		for(BizNodeMetricRelBo bnm:BnmList){
			Long ins = parentInstanceId;
			if(bnm.getChildInstanceId()>0){
				ins = bnm.getChildInstanceId();
			}else{
				ins = parentInstanceId;
			}
			if(queryMap.containsKey(ins)){
				queryMap.get(ins).add(bnm.getMetricId());
			}else{
				List<String> strList = new ArrayList<String>();
				strList.add(bnm.getMetricId());
				queryMap.put(ins, strList);
			}
		}
		try {
			ResourceInstance parent = resourceInstanceService.getResourceInstance(parentInstanceId);
			if(parent.getLifeState()==InstanceLifeStateEnum.MONITORED){
				return toSuccess(resourceApplyApi.getBizMetricInfo(queryMap, parent));
			}else{
				return toSuccess("1");//关联资源未监控!
			}
			
		} catch (InstancelibException e) {
			logger.error("biz---getBizMetricInfoByNodeId--InstancelibException"+e.getMessage());
			return toSuccess("2");//无相关数据!
		}
	}
}
