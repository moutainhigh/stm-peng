package com.mainsteam.stm.portal.business.web.action;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.mainsteam.stm.caplib.CapacityService;
import com.mainsteam.stm.caplib.dict.MetricTypeEnum;
import com.mainsteam.stm.caplib.resource.ResourceDef;
import com.mainsteam.stm.caplib.resource.ResourceMetricDef;
import com.mainsteam.stm.common.instance.obj.InstanceStateEnum;
import com.mainsteam.stm.common.metric.obj.MetricStateEnum;
import com.mainsteam.stm.instancelib.CompositeInstanceService;
import com.mainsteam.stm.instancelib.RelationService;
import com.mainsteam.stm.instancelib.ResourceInstanceService;
import com.mainsteam.stm.instancelib.exception.InstancelibException;
import com.mainsteam.stm.instancelib.obj.PathRelation;
import com.mainsteam.stm.instancelib.obj.Relation;
import com.mainsteam.stm.instancelib.obj.ResourceInstance;
import com.mainsteam.stm.instancelib.objenum.InstanceTypeEnum;
import com.mainsteam.stm.platform.web.action.BaseAction;
import com.mainsteam.stm.portal.business.api.IBizServiceApi;
import com.mainsteam.stm.portal.business.api.IBizStatusSelfApi;
import com.mainsteam.stm.portal.business.bo.BizServiceBo;
import com.mainsteam.stm.portal.business.bo.BizStatusSelfBo;
import com.mainsteam.stm.portal.business.web.vo.BizSerStateVo;
import com.mainsteam.stm.portal.business.web.vo.MetricDefVo;
import com.mainsteam.stm.portal.business.web.vo.MetricStyleVo;
import com.mainsteam.stm.portal.business.web.vo.ResourceDefVo;
import com.mainsteam.stm.portal.business.web.vo.ResourceInstanceVo;

/**
 * <li>文件名称: BizSerStatusAction.java</li>
 * <li>公　　司: 武汉美新翔盛科技有限公司</li>
 * <li>版权所有: 版权所有(C)2019-2020</li>
 * <li>修改记录: ...</li>
 * <li>内容摘要: ...</li>
 * <li>其他说明: ...</li>
 * @version  ms.stm
 * @since    2019年9月3日
 * @author   caoyong
 */
@Controller
@RequestMapping("/portal/business/service/status")
public class BizSerStatusAction extends BaseAction {
	private Logger logger = Logger.getLogger(BizSerStatusAction.class); 
	@Autowired
	private IBizServiceApi bizServiceApi;
	@Autowired
	private IBizStatusSelfApi bizStatusSelfApi;
	@Autowired
	private CompositeInstanceService compositeInstanceService;
	@Autowired
	private RelationService relationService;
	@Autowired
	private ResourceInstanceService resourceInstanceService;
	@Autowired
	private CapacityService capacityService;
	/**
	 * 查询中业务关联的所有的资源记录
	 * @return
	 * @throws Exception 
	 */
	@RequestMapping("/getRelationInstances")
	public JSONObject getRelationInstances(long id){
		try {
			List<Relation> relations = relationService.getRelationByInstanceId(id);
			List<ResourceInstanceVo> riVos = new ArrayList<ResourceInstanceVo>();
			List<Long> resourceInstanceIds = new ArrayList<Long>();
			if(null!=relations && relations.size()>0){
				for(Relation relation: relations){
					if(relation instanceof PathRelation){
						PathRelation pathRelation = (PathRelation) relation;
						if(pathRelation.getInstanceId()==id
								&& pathRelation.getFromInstanceType().equals(InstanceTypeEnum.BUSINESS_APPLICATION)
								&& pathRelation.getToInstanceType().equals(InstanceTypeEnum.RESOURCE)){
							resourceInstanceIds.add(pathRelation.getToInstanceId());
						}
					} 
				}
			}
			List<ResourceInstance> resourceInstances = resourceInstanceService.getResourceInstances(resourceInstanceIds);
			if(null!=resourceInstances && resourceInstances.size()>0){
				for(ResourceInstance i : resourceInstances){
					ResourceInstanceVo riVo = new ResourceInstanceVo(); 
					riVo = this.convertRIToRIVo(i, riVo,false);
					riVos.add(riVo);
				}
			}
			logger.info("portal.business.service.status.getRelationInstances successful");
			return toSuccess(riVos);
		} catch (Exception e) {
			logger.error("portal.business.service.status.getRelationInstances failure");
			logger.error(e.getMessage(),e);
			e.printStackTrace();
			return toJsonObject(299,"该业务应用没有关联的资源");
		}
	}
	private ResourceInstanceVo convertRIToRIVo(ResourceInstance target,ResourceInstanceVo source,boolean childrenFlag){
		source.setId(String.valueOf(target.getId()));
		source.setText(childrenFlag?target.getName():target.getShowName());
		source.setResourceId(target.getResourceId());
		return source;
	}
	
	/**
	 * 根据主资源id查询级联的下拉的子资源
	 * @param 资源id
	 * @return
	 */
	@RequestMapping("/getChildResource")
	public JSONObject getChildResource(Long id){
		try {
			List<ResourceInstanceVo> riVos = new ArrayList<ResourceInstanceVo>();
			List<ResourceInstance> resourceInstances = resourceInstanceService.getChildInstanceByParentId(id);
			if(null!=resourceInstances && resourceInstances.size()>0){
				for(ResourceInstance i: resourceInstances){
					ResourceInstanceVo riVo = new ResourceInstanceVo(); 
					riVo = this.convertRIToRIVo(i, riVo,true);
					riVos.add(riVo);
				}
			}
			logger.info("portal.business.service.status.getChildResource successful");
			return toSuccess(riVos);
		} catch (InstancelibException e) {
			logger.error("portal.business.service.status.getChildResource failure");
			logger.error(e.getMessage(),e);
			e.printStackTrace();
			return toJsonObject(299, "根据资源id查询子资源失败");
		}
	}
	@RequestMapping("/getMetricDefById")
	public JSONObject getMetricDefById(String resourceId){
		ResourceDef resourceDef = capacityService.getResourceDefById(resourceId);
		ResourceMetricDef[] metricDefs = resourceDef.getMetricDefs();
		if(null != metricDefs && metricDefs.length>0){
			List<MetricDefVo> metricDefVos = new ArrayList<MetricDefVo>();
			for(ResourceMetricDef metricDef: metricDefs){
				if(MetricTypeEnum.PerformanceMetric.equals(metricDef.getMetricType())
						|| MetricTypeEnum.AvailabilityMetric.equals(metricDef.getMetricType())){
					MetricDefVo metricDefVo = new MetricDefVo();
					metricDefVo.setId(metricDef.getId());
					metricDefVo.setName(metricDef.getName());
					metricDefVo.setStyle(metricDef.getMetricType().name());
					metricDefVos.add(metricDefVo);
				}
			}
			return toSuccess(metricDefVos);
		}else return null;
	}
	@RequestMapping("/getResourceDefById")
	public JSONObject getResourceDefById(String resourceId){
		ResourceDef resourceDef = capacityService.getResourceDefById(resourceId);
		ResourceDefVo rdVo = new ResourceDefVo();
		rdVo = convertRDToRDVo(resourceDef, rdVo);
		return toSuccess(rdVo);
	}
	private ResourceDefVo convertRDToRDVo(ResourceDef target,ResourceDefVo source){
		source.setId(target.getId());
		source.setName(target.getName());
		ResourceDef[] childTargets = target.getChildResourceDefs();
		if(null != childTargets && childTargets.length>0){
			List<ResourceDefVo> childSources = new ArrayList<ResourceDefVo>();
			for(ResourceDef childTarget: childTargets){
				ResourceDefVo childSource = new ResourceDefVo();
				childSource = this.convertRDToRDVo(childTarget, childSource);
				childSources.add(childSource);
 			}
			source.setChildren(childSources);
		}
		return source;
	}
	@RequestMapping("/getMetricStateByStyle")
	public JSONObject getMetricStateByStyle(String style){
		List<MetricStyleVo> metricStyleVos = new ArrayList<MetricStyleVo>();
		if(MetricTypeEnum.AvailabilityMetric.name().equals(style)){
			for(MetricStateEnum a : MetricStateEnum.values()){
				MetricStyleVo metricStyleVo = new MetricStyleVo();
				metricStyleVo.setId(a.name());
				if(MetricStateEnum.NORMAL.name().equals(a.name())){
					metricStyleVo.setName("可用");
//				}else if(MetricStateEnum.UNKOWN.name().equals(a.name())){
//					metricStyleVo.setName("未知");
				}else if(MetricStateEnum.CRITICAL.name().equals(a.name())){
					metricStyleVo.setName("不可用");
				}else{
					continue;
				}
				metricStyleVos.add(metricStyleVo);
			}
		}else if(MetricTypeEnum.PerformanceMetric.name().equals(style)){
			for(MetricStateEnum a : MetricStateEnum.values()){
				MetricStyleVo metricStyleVo = new MetricStyleVo();
				metricStyleVo.setId(a.name());
				 
				if(MetricStateEnum.SERIOUS.name().equals(a.name())){
					metricStyleVo.setName(InstanceStateEnum.getValue(InstanceStateEnum.SERIOUS));
				}else if(MetricStateEnum.WARN.name().equals(a.name())){
					metricStyleVo.setName(InstanceStateEnum.getValue(InstanceStateEnum.WARN));
				}else if(MetricStateEnum.NORMAL.name().equals(a.name())){
					metricStyleVo.setName(InstanceStateEnum.getValue(InstanceStateEnum.NORMAL));
				}else{
					continue;
				}
				metricStyleVos.add(metricStyleVo);
			}
		}
		return toSuccess(metricStyleVos);
	}
	@RequestMapping("/getBizSerState")
	public JSONObject getBizSerState(){
		List<BizSerStateVo> bizSerStateVos = new ArrayList<BizSerStateVo>();
		BizSerStateVo bizSerStateVo = new BizSerStateVo();
		for(InstanceStateEnum a:InstanceStateEnum.values()){
			if(InstanceStateEnum.CRITICAL.name().equals(a.name())){
				bizSerStateVo = new BizSerStateVo();
				bizSerStateVo.setId(InstanceStateEnum.CRITICAL.name());
				bizSerStateVo.setName(InstanceStateEnum.getValue(InstanceStateEnum.CRITICAL));
				bizSerStateVos.add(bizSerStateVo);
			}else if(InstanceStateEnum.SERIOUS.name().equals(a.name())){
				bizSerStateVo = new BizSerStateVo();
				bizSerStateVo.setId(InstanceStateEnum.SERIOUS.name());
				bizSerStateVo.setName(InstanceStateEnum.getValue(InstanceStateEnum.SERIOUS));
				bizSerStateVos.add(bizSerStateVo);
			}else if(InstanceStateEnum.WARN.name().equals(a.name())){
				bizSerStateVo = new BizSerStateVo();
				bizSerStateVo.setId(InstanceStateEnum.WARN.name());
				bizSerStateVo.setName(InstanceStateEnum.getValue(InstanceStateEnum.WARN));
				bizSerStateVos.add(bizSerStateVo);
			}else if(InstanceStateEnum.NORMAL.name().equals(a.name())){
				bizSerStateVo = new BizSerStateVo();
				bizSerStateVo.setId(InstanceStateEnum.NORMAL.name());
				bizSerStateVo.setName(InstanceStateEnum.getValue(InstanceStateEnum.NORMAL));
				bizSerStateVos.add(bizSerStateVo);
			}
		}
		return toSuccess(bizSerStateVos);
	}
	@SuppressWarnings({ "rawtypes"})
	@RequestMapping("/updateState")
	public JSONObject updateState(String data) throws Exception{
		try {
			BizServiceBo bizServiceBo = new BizServiceBo();
			Map map = JSONObject.parseObject(data, HashMap.class);
			String deathGroupData = map.get("deathGroupData").toString();
			String seriousGroupData = map.get("seriousGroupData").toString();
			String warningGroupData = map.get("warningGroupData").toString();
			String bizMainId = map.get("bizMainId").toString();
			
			//删除数据
			bizStatusSelfApi.delByBizSerId(Long.parseLong(bizMainId));
			
			Map deathGroupMap = JSONObject.parseObject(deathGroupData, HashMap.class);
			bizServiceBo.setDeath_relation(deathGroupMap.get("death_relation").toString());
			List<Map> deathRows = JSONArray.parseArray(deathGroupMap.get("rows").toString(),Map.class);
			for(Map m : deathRows){
				insertBizStatusSelfBo(m);
			}
			
			Map seriousGroupMap = JSONObject.parseObject(seriousGroupData, HashMap.class);
			bizServiceBo.setSerious_relation(seriousGroupMap.get("serious_relation").toString());
			List<Map> seriousRows = JSONArray.parseArray(seriousGroupMap.get("rows").toString(),Map.class);
			for(Map m : seriousRows){
				insertBizStatusSelfBo(m);
			}
			
			Map warningGroupMap = JSONObject.parseObject(warningGroupData, HashMap.class);
			bizServiceBo.setWarn_relation(warningGroupMap.get("warn_relation").toString());
			List<Map> warningRows = JSONArray.parseArray(warningGroupMap.get("rows").toString(),Map.class);
			for(Map m : warningRows){
				insertBizStatusSelfBo(m);
			}
			
			bizServiceBo.setId(Long.parseLong(bizMainId));
			bizServiceBo.setStatus_type("1");
			BizServiceBo bo = bizServiceApi.get(bizServiceBo.getId());
			bizServiceBo.setStatus(bo.getStatus());
			bizServiceBo.setTopology(bo.getTopology());
			bizServiceBo.setOldName(bo.getName());
			bizServiceBo.setName(bo.getName());
			bizServiceApi.update(bizServiceBo);
			logger.info("portal.business.service.status.updateState successful!");
			return toSuccess(bizServiceBo);
		} catch (Exception e) {
			logger.error("portal.business.service.status.updateState failure");
			logger.error(e.getMessage(),e);
			e.printStackTrace();
			return toJsonObject(299, "状态定义保存失败");
		}
	}
	@SuppressWarnings("rawtypes")
	private void insertBizStatusSelfBo(Map m){
		BizStatusSelfBo bo = new BizStatusSelfBo();
		if(null!=m.get("id").toString() && !"".equals(m.get("id").toString()))
			bo.setId(Long.parseLong(m.get("id").toString()));
		bo.setBiz_main_id(Long.parseLong(m.get("biz_main_id").toString()));
		bo.setType(Integer.parseInt(m.get("type").toString()));
		if(null != m.get("instance_id"))
			bo.setInstance_id(Long.parseLong(m.get("instance_id").toString()));
		if(null != m.get("metric_id"))
			bo.setMetric_id(m.get("metric_id").toString());
		if(null != m.get("state"))
			bo.setState(m.get("state").toString());
		bizStatusSelfApi.insert(bo);
	}
	
	@RequestMapping("/getBizStatusSelfData")
	public JSONObject getBizStatusSelfData(Long bizSerId){
		BizServiceBo bizServiceBo = bizServiceApi.get(bizSerId);
		List<BizStatusSelfBo> bos = bizStatusSelfApi.getByBizSerId(bizSerId);
		List<Object> list = new ArrayList<Object>();
		list.add(bizServiceBo);
		list.add(bos);
		return toSuccess(list);
	}
	
	@RequestMapping("/getBizBuessinessById")
	public JSONObject getBizBuessinessById(Long instanceId){
		List<BizServiceBo> bsb = bizServiceApi.getAllBuessinessApplication(instanceId);
		Map<String, Object> result = new HashMap<String, Object>();
		result.put("total", bsb.size());
		result.put("rows", bsb);
		return toSuccess(result);
	}
}
