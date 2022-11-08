package com.mainsteam.stm.portal.business.service.impl;
 

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.util.StringUtils;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.mainsteam.stm.alarm.AlarmService;
import com.mainsteam.stm.alarm.event.AlarmEventService;
import com.mainsteam.stm.alarm.obj.AlarmEvent;
import com.mainsteam.stm.alarm.obj.AlarmProviderEnum;
import com.mainsteam.stm.alarm.obj.AlarmSenderParamter;
import com.mainsteam.stm.alarm.obj.SysModuleEnum;
import com.mainsteam.stm.alarm.query.AlarmEventQuery;
import com.mainsteam.stm.caplib.CapacityService;
import com.mainsteam.stm.caplib.dict.MetricTypeEnum;
import com.mainsteam.stm.caplib.resource.ResourceMetricDef;
import com.mainsteam.stm.common.instance.obj.InstanceStateEnum;
import com.mainsteam.stm.common.metric.InstanceStateService;
import com.mainsteam.stm.common.metric.MetricStateService;
import com.mainsteam.stm.common.metric.obj.MetricStateEnum;
import com.mainsteam.stm.home.screen.api.ITopoApi;
import com.mainsteam.stm.home.screen.bo.Biz;
import com.mainsteam.stm.instancelib.CompositeInstanceService;
import com.mainsteam.stm.instancelib.InstanceRelationService;
import com.mainsteam.stm.instancelib.RelationService;
import com.mainsteam.stm.instancelib.ResourceInstanceService;
import com.mainsteam.stm.instancelib.exception.InstancelibException;
import com.mainsteam.stm.instancelib.obj.CompositeInstance;
import com.mainsteam.stm.instancelib.obj.CompositeProp;
import com.mainsteam.stm.instancelib.obj.Instance;
import com.mainsteam.stm.instancelib.obj.InstanceRelation;
import com.mainsteam.stm.instancelib.obj.PathRelation;
import com.mainsteam.stm.instancelib.obj.Relation;
import com.mainsteam.stm.instancelib.obj.ResourceInstance;
import com.mainsteam.stm.instancelib.objenum.InstanceTypeEnum;
import com.mainsteam.stm.platform.mybatis.plugin.pagination.Page;
import com.mainsteam.stm.platform.web.vo.ILoginUser;
import com.mainsteam.stm.portal.business.api.IBizServiceApi;
import com.mainsteam.stm.portal.business.api.IBizStatusSelfApi;
import com.mainsteam.stm.portal.business.bo.BizServiceBo;
import com.mainsteam.stm.portal.business.bo.BizStatusSelfBo;
import com.mainsteam.stm.portal.business.bo.BizWarnViewBo;
import com.mainsteam.stm.portal.business.dao.IBizServiceDao;
import com.mainsteam.stm.portal.business.report.api.BizSerReportListenerEngine;
import com.mainsteam.stm.portal.business.report.obj.BizSerReport;
import com.mainsteam.stm.portal.business.report.obj.BizSerReportEvent;
import com.mainsteam.stm.profilelib.alarm.obj.AlarmRuleProfileEnum;
import com.mainsteam.stm.simple.search.api.ISearchApi;
import com.mainsteam.stm.simple.search.bo.ResourceBizRel;
import com.mainsteam.stm.state.obj.InstanceStateData;
import com.mainsteam.stm.system.um.user.api.IUserApi;
import com.mainsteam.stm.system.um.user.bo.User;

public class BizServiceImpl implements IBizServiceApi,ITopoApi {
	/**contextPath的占位符*/
	private static final String replaceHolder = "%s";
	private IBizServiceDao bizServiceDao;
	@Autowired
	private IBizStatusSelfApi bizStatusSelfApi;
	@Autowired
	private ISearchApi searchApi;
	@Autowired
	private CompositeInstanceService compositeInstanceService;
	@Autowired
	private RelationService relationService;
	@Autowired
	private InstanceStateService instanceStateService ;
	@Autowired
	private MetricStateService metricStateService;
	@Autowired
	private InstanceRelationService instanceRelationService;
	@Resource
	private AlarmService alarmService;
	@Resource
	private AlarmEventService alarmEventService;
	@Resource
	private IUserApi stm_system_userApi;
	@Resource
	private BizSerReportListenerEngine bizSerReportListenerEngine;
	@Autowired
	private ResourceInstanceService resourceInstanceService;
	@Autowired
	private CapacityService capacityService;
	/**
	 * 新增业务服务,新增时状态默认为默认规则，新增时间为当前时间
	 * @throws Exception 
	 */
	@Override
	public Long insert(BizServiceBo bizServiceBo) throws Exception {
		int nameIsExsit = bizServiceDao.checkGroupNameIsExsit(bizServiceBo.getName(),null);
		if(nameIsExsit > 0){
			return -1l;
		}
		bizServiceBo.setEntry_datetime(new Date());
		bizServiceBo.setStatus(InstanceStateEnum.NORMAL.name());
		bizServiceBo.setStatus_type("0");
		CompositeInstance compositeInstance = new CompositeInstance();
		compositeInstance.setInstanceType(InstanceTypeEnum.BUSINESS_APPLICATION);
		compositeInstance.setName(bizServiceBo.getName());
		
		List<CompositeProp> props = new ArrayList<CompositeProp>();
//		this.setCompositeInstanceProps(bizServiceBo,props);
		compositeInstance.setProps(props);
		
		Long id = compositeInstanceService.addCompositeInstance(compositeInstance);
		bizServiceBo.setId(id);
		bizServiceDao.insert(bizServiceBo);
		return id;
	}
	
	/**
	 * 删除业务服务
	 * @throws InstancelibException 
	 */
	@Override
	public int del(long id) throws Exception {
		//删除复合资源实例
	//	compositeInstanceService.removeCompositeInstance(id);
		//删除极简模式-业务管理查询数据
		List<Instance> instances = instanceRelationService.getInstaceCollectPOsByInstanceId(id);
		ResourceBizRel model = this.convert2ResourceBizRel(id, instances);
		if(model.getResourceIds()!=null && model.getResourceIds().size()>0){
			searchApi.delSearchBiz(this.convert2ResourceBizRel(id, instances));
			compositeInstanceService.removeCompositeInstance(id);
		}
		//删除自定义状态规则数据
		bizStatusSelfApi.delByBizSerId(id);
		//删除业务应用数据
		int count = bizServiceDao.del(id);
		try {
		//启动删除监听
			BizSerReport source = new BizSerReport();
			source.setId(id);
			BizSerReportEvent event = new BizSerReportEvent(source);
			bizSerReportListenerEngine.handleListen(event);
		} catch (Exception e) {
			e.printStackTrace();
		}
		return count;
	}

	/**
	 * 修改业务服务
	 */
	@SuppressWarnings({ "rawtypes", "unchecked" })
	@Override
	public int update(BizServiceBo bizServiceBo) throws Exception{
		bizServiceBo.setEntry_datetime(new Date());
		int nameIsExsit = bizServiceDao.checkGroupNameIsExsit(bizServiceBo.getName(),bizServiceBo.getOldName());
		if(nameIsExsit > 0){
			return -1;
		}
		CompositeInstance compositeInstance = new CompositeInstance();
		compositeInstance.setInstanceType(InstanceTypeEnum.BUSINESS_APPLICATION);
		if(null!=bizServiceBo.getName()) compositeInstance.setName(bizServiceBo.getName());
		compositeInstance.setId(bizServiceBo.getId());
		
		if(null!=bizServiceBo.getFileId() || null!=bizServiceBo.getRemark()){
			List<CompositeProp> props = new ArrayList<CompositeProp>();
//			this.setCompositeInstanceProps(bizServiceBo, props);
			compositeInstance.setProps(props);
		}
		
		if(null != bizServiceBo.getTopology() && !"".equals(bizServiceBo.getTopology())){
			//保存拓扑图复合资源关系
			String topology = bizServiceBo.getTopology();
			topology = topology.replace("\\\"", "\\\'");
			List<List> list = setRelationsAndClindInstances(topology);
			List<Relation> relations = relationService.getRelationByInstanceId(bizServiceBo.getId());
			if(relations!=null && relations.size()>0){
				List<Long> instanceIds = new ArrayList<Long>();
				for(Relation relation: relations){
					if(relation instanceof PathRelation){
						PathRelation pathRelation = (PathRelation) relation;
						if(pathRelation.getInstanceId()==bizServiceBo.getId()
								&& pathRelation.getFromInstanceType().equals(InstanceTypeEnum.BUSINESS_APPLICATION)
								&& pathRelation.getToInstanceType().equals(InstanceTypeEnum.RESOURCE)){
							boolean flag = true;
							if(null != list && list.size()>0 && null!=list.get(0) && list.get(0).size()>0){
								for(Relation newRelation : (List<Relation>)list.get(0)){
									PathRelation newPathRelation = (PathRelation) newRelation;
									newPathRelation.setInstanceId(compositeInstance.getId());
									if(newPathRelation.getInstanceId()==pathRelation.getInstanceId()
											&&newPathRelation.getFromInstanceId()==pathRelation.getFromInstanceId()
											&&newPathRelation.getFromInstanceType()==pathRelation.getFromInstanceType()
											&&newPathRelation.getToInstanceId()==pathRelation.getToInstanceId()
											&&newPathRelation.getToInstanceType()==pathRelation.getToInstanceType()){
										flag = false;
										break;
									}
								}
							}
							if(flag){
								instanceIds.add(pathRelation.getToInstanceId());
							}
						}
					} 
				}
				if(null!=instanceIds && instanceIds.size()>0){
					List<Long> childInstanceIds = new ArrayList<Long>();
					for(Long instanceId:instanceIds){
						List<ResourceInstance> cInstances = resourceInstanceService.getChildInstanceByParentId(instanceId);
						if(null!=cInstances&&cInstances.size()>0){
							for(ResourceInstance childInstance:cInstances){
								childInstanceIds.add(childInstance.getId());
							}
						}
					}
					instanceIds.addAll(childInstanceIds);
				}
				bizStatusSelfApi.delByBizSerIdAndInstanceIds(bizServiceBo.getId(),instanceIds.toArray(new Long[]{}));
			}
			if(null != list && list.size()>0){
				if(null!=list.get(0) && list.get(0).size()>0){
					for(Relation relation : (List<Relation>)list.get(0)){
						PathRelation pathRelation = (PathRelation) relation;
						pathRelation.setInstanceId(compositeInstance.getId());
						relation = pathRelation;
					}
					InstanceRelation instanceReatiom = new InstanceRelation();
					instanceReatiom.setRelations(list.get(0));
					compositeInstance.setInstanceReatiom(instanceReatiom);
					this.setBizServiceStatus(bizServiceBo, (List<Relation>)list.get(0));
				}else{
					this.setBizServiceStatus(bizServiceBo, new ArrayList<Relation>());
				} 
				if(null!=list.get(1) && list.get(1).size()>0){
					compositeInstance.setElements(list.get(1));
					ResourceBizRel model = this.convert2ResourceBizRel(bizServiceBo.getId(), (List<Instance>)list.get(1));
					if(model.getResourceIds().size()>0){
						searchApi.delSearchBiz(this.convert2ResourceBizRel(bizServiceBo.getId(), (List<Instance>)list.get(1)));
						searchApi.saveSearchBiz(this.convert2ResourceBizRel(bizServiceBo.getId(), (List<Instance>)list.get(1)));
					}
				}
			}
			bizServiceBo.setTopology(topology);
		}
		
		compositeInstanceService.updateCompositeInstance(compositeInstance);
		int count = bizServiceDao.update(bizServiceBo);
		return count;
	}
	
	/**
	 * 设置复合资源实例的props属性
	 * @param bizServiceBo
	 * @param props
	 */
	private void setCompositeInstanceProps(BizServiceBo bizServiceBo,List<CompositeProp> props){
		CompositeProp prop = new CompositeProp();
		String[] propValues=new String[1];
		
		if(null!=bizServiceBo.getFileId()){
			prop = new CompositeProp();
			propValues=new String[1];
			propValues[0]=String.valueOf(bizServiceBo.getFileId());
			
			prop.setKey("fileId");
			prop.setValues(propValues);
			props.add(prop);
		}
		if(null!=bizServiceBo.getRemark()){
			prop = new CompositeProp();
			propValues=new String[1];
			propValues[0]=bizServiceBo.getRemark();
			
			prop.setKey("remark");
			prop.setValues(propValues);
			props.add(prop);
		}
	}
	
	/**
	 * 根据当前业务服务中关联的资源的状态设置业务自身的状态
	 * @param bizServiceBo
	 * @param list
	 */
	@SuppressWarnings({ "rawtypes", "unchecked" })
	private void setBizServiceStatus(BizServiceBo bizServiceBo,List list){
		List<Long> resourceInstanceIds = new ArrayList<Long>();
		if(null!=list && list.size()>0){
			for(Relation relation: (List<Relation>)list){
				if(relation instanceof PathRelation){
					PathRelation pathRelation = (PathRelation) relation;
					if(pathRelation.getInstanceId()==bizServiceBo.getId()
							&& pathRelation.getFromInstanceType().equals(InstanceTypeEnum.BUSINESS_APPLICATION)
							&& pathRelation.getToInstanceType().equals(InstanceTypeEnum.RESOURCE)){
						resourceInstanceIds.add(pathRelation.getToInstanceId());
					}
				} 
			}
		}
		if(resourceInstanceIds.size()==0){
			String oldState = bizServiceBo.getStatus();
			bizServiceBo.setStatus(InstanceStateEnum.NORMAL.name());
			String newState = bizServiceBo.getStatus();
			this.addAlarm(oldState, newState, bizServiceBo);
		}else if(bizServiceBo.getStatus_type().equals("1")){//自定义规则
			String oldState = bizServiceBo.getStatus();
			this.setStatusByDefinedRules(bizServiceBo, resourceInstanceIds);
			String newState = bizServiceBo.getStatus();
			this.addAlarm(oldState, newState, bizServiceBo);
		}else{//默认
			String oldState = bizServiceBo.getStatus();
			this.setStatusBydefaultRules(bizServiceBo, resourceInstanceIds);
			String newState = bizServiceBo.getStatus();
			this.addAlarm(oldState, newState, bizServiceBo);
		}
	}
	
	/**
	 * 业务状态变化产生告警
	 * @param oldState
	 * @param newState
	 * @param bizServiceBo
	 * @throws Exception
	 */
	private void addAlarm(String oldState,String newState,BizServiceBo bizServiceBo){
//		状态变化产生告警
		if((!StringUtils.isEmpty(oldState) && !newState.equals(oldState))
				|| StringUtils.isEmpty(oldState)){
			AlarmSenderParamter alarmSenderParamter = new AlarmSenderParamter();
			alarmSenderParamter.setDefaultMsg(bizServiceBo.getName()+"状态由"+getStateCNName(oldState)+"变为"+getStateCNName(newState));
			alarmSenderParamter.setDefaultMsgTitle(bizServiceBo.getName()+"状态由"+getStateCNName(oldState)+"变为"+getStateCNName(newState));
			alarmSenderParamter.setGenerateTime(new Date());
			alarmSenderParamter.setLevel(getInstanceState(newState));//
			alarmSenderParamter.setProvider(AlarmProviderEnum.OC4);
			alarmSenderParamter.setSysID(SysModuleEnum.BUSSINESS);
			alarmSenderParamter.setExt2(SysModuleEnum.BUSSINESS.name());
			alarmSenderParamter.setProfileID(bizServiceBo.getId());
			alarmSenderParamter.setSourceID(String.valueOf(bizServiceBo.getId()));
			alarmSenderParamter.setSourceName(bizServiceBo.getName());
			alarmSenderParamter.setRuleType(AlarmRuleProfileEnum.biz_profile);
			alarmSenderParamter.setRecoverKeyValue(new String[]{String.valueOf(bizServiceBo.getId())});
			alarmService.notify(alarmSenderParamter);
		}
	}
	private InstanceStateEnum getInstanceState(String state){
		if(InstanceStateEnum.CRITICAL.name().equals(state)){
			return InstanceStateEnum.CRITICAL;
		}else if(InstanceStateEnum.SERIOUS.name().equals(state)){
			return InstanceStateEnum.SERIOUS;
		}else if(InstanceStateEnum.WARN.name().equals(state)){
			return InstanceStateEnum.WARN;
		}else if(InstanceStateEnum.NORMAL.name().equals(state)){
			return InstanceStateEnum.NORMAL;
		}
		return InstanceStateEnum.NORMAL;
	}
	private String getStateCNName(String state){
		if(InstanceStateEnum.CRITICAL.name().equals(state)){
		return	InstanceStateEnum.getValue(InstanceStateEnum.CRITICAL);
		}else if(InstanceStateEnum.SERIOUS.name().equals(state)){
			return	InstanceStateEnum.getValue(InstanceStateEnum.SERIOUS);
		}else if(InstanceStateEnum.WARN.name().equals(state)){
			return	InstanceStateEnum.getValue(InstanceStateEnum.WARN);
		}else if(InstanceStateEnum.NORMAL.name().equals(state)){
			return	InstanceStateEnum.getValue(InstanceStateEnum.NORMAL);
		}
		return	InstanceStateEnum.getValue(InstanceStateEnum.NORMAL);
	}
	/**
	 * 根据自定义条件设置业务服务状态
	 * @param bizServiceBo
	 * @param list
	 */
	private void setStatusByDefinedRules(BizServiceBo bizServiceBo,List<Long> list){
		List<BizStatusSelfBo> bizStatusSelfBos = bizStatusSelfApi.getByBizSerId(bizServiceBo.getId());
		List<BizStatusSelfBo> criticalSelfs = new ArrayList<BizStatusSelfBo>();
		List<BizStatusSelfBo> seriousSelfs = new ArrayList<BizStatusSelfBo>();
		List<BizStatusSelfBo> warnSelfs = new ArrayList<BizStatusSelfBo>();
		int	criticalOperation = Integer.parseInt(bizServiceBo.getDeath_relation());
		int	seriousOperation = Integer.parseInt(bizServiceBo.getSerious_relation());
		int	warnOperation = Integer.parseInt(bizServiceBo.getWarn_relation());
		
		for(BizStatusSelfBo self:bizStatusSelfBos){
			if(self.getType() == 0) criticalSelfs.add(self);
			else if(self.getType() == 1) seriousSelfs.add(self);
			else if(self.getType() == 2) warnSelfs.add(self);
		}
		boolean criticalFlag = this.setStatusFlag(criticalSelfs, criticalOperation);
		boolean seriousFlag = this.setStatusFlag(seriousSelfs, seriousOperation);
		boolean warnFlag = this.setStatusFlag(warnSelfs, warnOperation);
		
		if(criticalFlag) bizServiceBo.setStatus(InstanceStateEnum.CRITICAL.name());
		else if(seriousFlag) bizServiceBo.setStatus(InstanceStateEnum.SERIOUS.name());	
		else if(warnFlag) bizServiceBo.setStatus(InstanceStateEnum.WARN.name());
		else{
//			String status = "";
//			for(Long i: list){
//				if(null == instanceStateService.getStateAdapter(i)) continue;
//				status = instanceStateService.getStateAdapter(i).getState().name();
//				if(status.equals(InstanceStateEnum.UNKOWN.name())){
//					unknownFlag = true;
//					break;
//				}else if(status.equals(InstanceStateEnum.NORMAL.name())){
//					normalFlag = true;
//				}
//			}
//			if(unknownFlag) bizServiceBo.setStatus(InstanceStateEnum.UNKOWN.name());
//			else if(normalFlag) bizServiceBo.setStatus(InstanceStateEnum.NORMAL.name());
			bizServiceBo.setStatus(InstanceStateEnum.NORMAL.name());
		}
	}
	/**
	 * 自定义计算条件(并/或)计算结果
	 * @param list
	 * @param operation
	 * @return
	 */
	@SuppressWarnings("deprecation")
	private boolean setStatusFlag(List<BizStatusSelfBo> list,int operation){
		if(null==list || list.size()==0) return false;
		if(operation == 0){//并且
			boolean flag = true;
			for(BizStatusSelfBo self:list){
				if(self.getMetric_id()==null){
					if(null==instanceStateService.getStateAdapter(self.getInstance_id()) ||
							!self.getState().equals(instanceStateService.getStateAdapter(self.getInstance_id()).getState().name())){
						flag = false;
						break;
					}
				}else{
					try {
						ResourceInstance instance = resourceInstanceService.getResourceInstance(self.getInstance_id());
						if(null!=instance){
							ResourceMetricDef rmd = capacityService.getResourceMetricDef(instance.getResourceId(), self.getMetric_id());
							if(rmd.getMetricType().name().equals(MetricTypeEnum.PerformanceMetric.name())){
								if(null==metricStateService.getMetricState(self.getInstance_id(), self.getMetric_id()) ||
										!self.getState().equals(metricStateService.getMetricState(self.getInstance_id(), self.getMetric_id()).getState().name())){
									flag = false;
									break;
								}
							}else if(rmd.getMetricType().name().equals(MetricTypeEnum.AvailabilityMetric.name())){
								if(null==metricStateService.getMetricState(self.getInstance_id(), self.getMetric_id()) ||
										!self.getState().equals(metricStateService.getMetricState(self.getInstance_id(), self.getMetric_id()).getState().name())){
									flag = false;
									break;
								}
							}
						}
					} catch (InstancelibException e) {
						e.printStackTrace();
					}
				}
			}
			return flag;
		}else if(operation == 1){//或者
			boolean flag = false;
			for(BizStatusSelfBo self:list){
				if(self.getMetric_id()==null){
					if(null==instanceStateService.getStateAdapter(self.getInstance_id())){
						continue;
					}else if(self.getState().equals(instanceStateService.getStateAdapter(self.getInstance_id()).getState().name())){
						flag = true;
						break;
					}
				}else{
					//区分性能指标和可用性指标
					try {
						ResourceInstance instance = resourceInstanceService.getResourceInstance(self.getInstance_id());
						if(null!=instance){
							ResourceMetricDef rmd = capacityService.getResourceMetricDef(instance.getResourceId(), self.getMetric_id());
							if(rmd.getMetricType().name().equals(MetricTypeEnum.PerformanceMetric.name())){
								if(null==metricStateService.getMetricState(self.getInstance_id(), self.getMetric_id())){
									continue;
								}else if(self.getState().equals(metricStateService.getMetricState(self.getInstance_id(), self.getMetric_id()).getState().name())){
									flag = true;
									continue;
								}
							}else if(rmd.getMetricType().name().equals(MetricTypeEnum.AvailabilityMetric.name())){
								if(null==metricStateService.getMetricState(self.getInstance_id(), self.getMetric_id())){
									continue;
								}else if(self.getState().equals(metricStateService.getMetricState(self.getInstance_id(), self.getMetric_id()).getState().name())){
									flag = true;
									break;
								}
							}
						}
					} catch (Exception e) {
					}
				}
			}
			return flag;
		}
		return false;
	} 
	/**
	 * 根据默认条件计算业务服务状态
	 * @param bizServiceBo
	 * @param list
	 */
	private void setStatusBydefaultRules(BizServiceBo bizServiceBo,List<Long> list){
		boolean criticalFlag = false,seriousFlag = false,warnFlag = false;
		boolean normalFlag = false;
		for(Long i: list){
			if(i!=0l){
				InstanceStateData instanceStateData =  instanceStateService.getStateAdapter(i);
				if(null!=instanceStateData){
					String status = "";
					status = instanceStateService.getStateAdapter(i).getState().name();
					if(status.equals(InstanceStateEnum.CRITICAL.name())){
						criticalFlag = true;
					}else if(status.equals(InstanceStateEnum.SERIOUS.name())){
						seriousFlag = true;
					}else if(status.equals(InstanceStateEnum.WARN.name())){
						warnFlag = true;
					}else if(status.equals(InstanceStateEnum.NORMAL.name())){
						normalFlag = true;
					}
				}
			}
		}
		if(criticalFlag) bizServiceBo.setStatus(InstanceStateEnum.CRITICAL.name());
		else if(seriousFlag) bizServiceBo.setStatus(InstanceStateEnum.SERIOUS.name());	
		else if(warnFlag) bizServiceBo.setStatus(InstanceStateEnum.WARN.name());	
		else if(normalFlag) bizServiceBo.setStatus(InstanceStateEnum.NORMAL.name());
	}
	/**
	 * 根据topology json数据返回业务拓扑图的关系List
	 * @param topology
	 * @return
	 */
	@SuppressWarnings({ "unused", "rawtypes" })
	private List<List> setRelationsAndClindInstances(String topology){
		if(!"".equals(topology) && null !=topology){
			List<List> result = new ArrayList<List>();
			List<Relation> relations = new ArrayList<Relation>();
			Map map = (Map) JSON.parse(topology);
			List<Map> lines = JSONArray.parseArray(map.get("lines").toString(),Map.class);
			List<Map> containerList = JSONArray.parseArray(map.get("containers").toString(),Map.class);
			List<Map> containers = new ArrayList<Map>();
			this.getAllcontains(containerList, containers);
			for(Map lineMap : lines){
				String from = (String) lineMap.get("from");
				String to = (String) lineMap.get("to");
				String fromType = "", toType="";long fromId = 0l, toId = 0l;
				PathRelation relation = new PathRelation();
				for(Map containerMap : containers){
					Map dataMap =  (Map) containerMap.get("data");
					if(from.equals(containerMap.get("ID").toString())
							|| to.equals(containerMap.get("ID").toString())){
						if("resource".equals(dataMap.get("type").toString())//资源
								|| "bizMain".equals(dataMap.get("type").toString())//业务应用
								|| "bizDep".equals(dataMap.get("type").toString())//业务单位
								|| "bizSer".equals(dataMap.get("type").toString())){//业务服务
							if(from.equals(containerMap.get("ID").toString())){
								fromType = (String) dataMap.get("type");
								if("resource".equals(fromType)){
									relation.setFromInstanceType(InstanceTypeEnum.RESOURCE);
								}else if("bizMain".equals(fromType)){
									relation.setFromInstanceType(InstanceTypeEnum.BUSINESS_APPLICATION);
								}else if("bizDep".equals(fromType)){
									relation.setFromInstanceType(InstanceTypeEnum.BUSINESS_UNIT);
								}else if("bizSer".equals(fromType)){
									relation.setFromInstanceType(InstanceTypeEnum.BUSINESS_SERVICE);
								}
								relation.setFromInstanceId(Long.parseLong(dataMap.get("id").toString()));
							}else if(to.equals(containerMap.get("ID").toString())){
								toType = (String) dataMap.get("type");
								if("resource".equals(toType)){
									relation.setToInstanceType(InstanceTypeEnum.RESOURCE);
								}else if("bizMain".equals(toType)){
									relation.setToInstanceType(InstanceTypeEnum.BUSINESS_APPLICATION);
								}else if("bizDep".equals(toType)){
									relation.setToInstanceType(InstanceTypeEnum.BUSINESS_UNIT);
								}else if("bizSer".equals(toType)){
									relation.setToInstanceType(InstanceTypeEnum.BUSINESS_SERVICE);
								}
								relation.setToInstanceId(Long.parseLong(dataMap.get("id").toString()));
							}
						} 
					}
				}
				if(relation.getToInstanceId() != 0l 
						&& relation.getFromInstanceId() != 0l
						&& relation.getFromInstanceType() != null 
						&& relation.getToInstanceType() != null
						&& (
								(relation.getFromInstanceType()==InstanceTypeEnum.BUSINESS_UNIT && relation.getToInstanceType()==InstanceTypeEnum.BUSINESS_SERVICE)
								||(relation.getFromInstanceType()==InstanceTypeEnum.BUSINESS_SERVICE && relation.getToInstanceType()==InstanceTypeEnum.BUSINESS_APPLICATION)
								||(relation.getFromInstanceType()==InstanceTypeEnum.BUSINESS_APPLICATION && relation.getToInstanceType()==InstanceTypeEnum.RESOURCE)
								
								||(relation.getFromInstanceType()==InstanceTypeEnum.RESOURCE && relation.getToInstanceType()==InstanceTypeEnum.BUSINESS_APPLICATION)
								||(relation.getFromInstanceType()==InstanceTypeEnum.BUSINESS_APPLICATION && relation.getToInstanceType()==InstanceTypeEnum.BUSINESS_SERVICE)
								||(relation.getFromInstanceType()==InstanceTypeEnum.BUSINESS_SERVICE && relation.getToInstanceType()==InstanceTypeEnum.BUSINESS_UNIT)
							)
				){
					if((relation.getFromInstanceType().equals(InstanceTypeEnum.RESOURCE) && relation.getToInstanceType().equals(InstanceTypeEnum.BUSINESS_APPLICATION))
								||(relation.getFromInstanceType().equals(InstanceTypeEnum.BUSINESS_APPLICATION) && relation.getToInstanceType().equals(InstanceTypeEnum.BUSINESS_SERVICE))
								||(relation.getFromInstanceType().equals(InstanceTypeEnum.BUSINESS_SERVICE) && relation.getToInstanceType().equals(InstanceTypeEnum.BUSINESS_UNIT))){
						Long fId = relation.getFromInstanceId(),tId = relation.getToInstanceId();
						InstanceTypeEnum ftype = relation.getFromInstanceType(),tType = relation.getToInstanceType();
						relation.setFromInstanceId(tId);
						relation.setFromInstanceType(tType);
						relation.setToInstanceId(fId);
						relation.setToInstanceType(ftype);
					}
					relations.add(relation);
				}
			}
			List<Instance> instances = new ArrayList<Instance>();
			for(Map conMap:containers){
				if(StringUtils.isEmpty(conMap.get("data"))) continue;
				Map dataMap =  (Map) conMap.get("data");
				if("resource".equals((String) dataMap.get("type"))){
					ResourceInstance rInstance = new ResourceInstance();
					rInstance.setId(Long.parseLong(dataMap.get("id").toString()));
					instances.add(rInstance);
				}else if("bizMain".equals((String) dataMap.get("type"))){
					CompositeInstance cInstance = new CompositeInstance();
					cInstance.setId(Long.parseLong(dataMap.get("id").toString()));
					instances.add(cInstance);
				}
			}
			result.add(relations);
			result.add(instances);
			return result;
		}else return null;
	}
	
	@SuppressWarnings({ "rawtypes" })
	private void getAllcontains(List<Map> containers,List<Map> list){
		if(null!=containers && containers.size()>0){
			for(int i=0;i<containers.size();i++){
				list.add(containers.get(i));
				List<Map> child = JSONArray.parseArray(containers.get(i).get("containers").toString(),Map.class);
				this.getAllcontains(child,list);
			}
		}
	}
	/**
	 * 通过ID查询业务信息
	 */
	@Override
	public BizServiceBo get(long id){
		BizServiceBo bizServiceBo = bizServiceDao.get(id);
		return bizServiceBo;
	}

	@Override
	public List<BizServiceBo> getList() throws Exception{
		List<BizServiceBo> bizServiceBoList = bizServiceDao.getList();
		return bizServiceBoList;
	}
	
	@Override
	public void getListPage(Page<BizServiceBo, Object> page) throws Exception{
		bizServiceDao.getListPage(page);
	}

	public IBizServiceDao getBizServiceDao() {
		return bizServiceDao;
	}

	public void setBizServiceDao(IBizServiceDao bizServiceDao) {
		this.bizServiceDao = bizServiceDao;
	}

	/**
	 * 添加/删除网络设备资源时转ResourceBizRel
	 * @param bo
	 * @return
	 */
	private ResourceBizRel convert2ResourceBizRel(Long id,List<Instance> instances){
		List<Long> ids = new ArrayList<Long>();
		if(instances!=null && instances.size()>0){
			for(Instance instance : instances){
				if(instance instanceof ResourceInstance){
					boolean flag = false;
					for(Long current:ids){
						if(instance.getId()==current){
							flag = true;
							break;
						}
					}
					if(flag) continue;
					ids.add(instance.getId());
				}
			}
		}
		ResourceBizRel model = new ResourceBizRel();
		model.setBizId(id);
		model.setResourceIds(ids);
		BizServiceBo bo = bizServiceDao.get(id);
		model.setNav("业务管理-"+bo.getName()+"-"+id);
		return model;
	}

	@Override
	public void selectWarnViewPage(Page<BizWarnViewBo, BizWarnViewBo> page,
			BizServiceBo bizServiceBo, String status) throws Exception {
		AlarmEventQuery alarmEventQuery = new AlarmEventQuery();
		SysModuleEnum[] enums={SysModuleEnum.BUSSINESS};
		alarmEventQuery.setSysIDes(enums);
		List<String> sourceIDes = new ArrayList<String>();
		sourceIDes.add(String.valueOf(bizServiceBo.getId()));
		if(sourceIDes.size()>0) alarmEventQuery.setSourceIDes(sourceIDes);
		if(!StringUtils.isEmpty(status)){
			List<MetricStateEnum> list = new ArrayList<MetricStateEnum>();
			list.add(getMetricStateEnum(status));
			alarmEventQuery.setStates(list);
		}
		Page<AlarmEvent, AlarmEventQuery> pageResult = alarmEventService.findAlarmEvent(alarmEventQuery, 
				(int)page.getStartRow(), (int)page.getRowCount());
		List<BizWarnViewBo> datas = new ArrayList<BizWarnViewBo>();
		List<AlarmEvent> events = pageResult.getDatas();
		for(AlarmEvent event: events){
			BizWarnViewBo bo = new BizWarnViewBo();
			bo.setId(event.getEventID());
			bo.setSourceId(Long.parseLong(event.getSourceID()));
			bo.setContent(event.getContent());
			bo.setName(event.getSourceName());
			bo.setWarnTime(event.getCollectionTime());
			bo.setLevel(event.getLevel().name());
			datas.add(bo);
		}
		page.setDatas(datas);
		page.setTotalRecord(pageResult.getTotalRecord());
	}
	private static MetricStateEnum getMetricStateEnum(String metricStateEnumString) {
		MetricStateEnum ise = null;
		if (null == metricStateEnumString) {
			return ise;
		} else {
			switch (metricStateEnumString) {
			case "all":
				break;
			case "down":
				ise = MetricStateEnum.CRITICAL;
				break;
			case "metric_error":
				ise = MetricStateEnum.SERIOUS;
				break;
			case "metric_warn":
				ise = MetricStateEnum.WARN;
				break;
			case "metric_recover":
				ise = MetricStateEnum.NORMAL;
				break;
			}
		}
		return ise;
	}

	@Override
	public List<User> getSystemUsers() {
		return stm_system_userApi.queryAllUserNoPage();
	}

	@SuppressWarnings({ "unchecked", "rawtypes" })
	@Override
	public List getNewlyState(Long id) {
		List list = new ArrayList();
		try {
			CompositeInstance c = compositeInstanceService.getCompositeInstance(id);
			List<Instance> instances = c.getElements();
			List<com.mainsteam.stm.portal.business.bo.ResourceInstance> rits = 
					new ArrayList<com.mainsteam.stm.portal.business.bo.ResourceInstance>();
			for(Instance i: instances){
				if(i instanceof CompositeInstance){
					BizServiceBo bs = bizServiceDao.get(i.getId());
					list.add(bs);
				}else{
					com.mainsteam.stm.portal.business.bo.ResourceInstance r = 
							new com.mainsteam.stm.portal.business.bo.ResourceInstance();
					r.setId(String.valueOf(i.getId()));
					HashMap<String, String> attributes = new HashMap<String, String>();
					if(null == instanceStateService.getStateAdapter(i.getId())){
						attributes.put("status", InstanceStateEnum.NORMAL.name());
					}else{
						attributes.put("status", instanceStateService.getStateAdapter(i.getId()).getState().name());
					}
					r.setAttributes(attributes);
					rits.add(r);
				}
			}
			list.add(rits);
		} catch (InstancelibException e) {
			e.printStackTrace();
		}
		return list;
	}

	@SuppressWarnings({ "rawtypes", "unchecked" })
	@Override
	public List getNewlyStateByIds(Long[] ids, Long[] resourceIds) {
		List list = new ArrayList();
		List<BizServiceBo> bos = bizServiceDao.getListByIds(Arrays.asList(ids));
		list.add(bos);
		List<com.mainsteam.stm.portal.business.bo.ResourceInstance> rits = 
				new ArrayList<com.mainsteam.stm.portal.business.bo.ResourceInstance>();
		for(Long l: resourceIds){
			com.mainsteam.stm.portal.business.bo.ResourceInstance r = 
					new com.mainsteam.stm.portal.business.bo.ResourceInstance();
			r.setId(String.valueOf(l));
			HashMap<String, String> attributes = new HashMap<String, String>();
			if(null == instanceStateService.getStateAdapter(l)){
				attributes.put("status", InstanceStateEnum.NORMAL.name());
			}else{
				attributes.put("status", instanceStateService.getStateAdapter(l).getState().name());
			}
			r.setAttributes(attributes);
			rits.add(r);
		}
		list.add(rits);
		return list;
	}

	@Override
	public List<Biz> getBizs(ILoginUser user,HttpServletRequest request) {
		List<Biz> bizs = new ArrayList<Biz>();
		try {
			List<BizServiceBo> bos = bizServiceDao.getList();
			for(BizServiceBo bo : bos){
				if(user.isSystemUser() || user.isDomainUser() || user.isManagerUser()){
					Biz b = new Biz();
					b.setBizId(bo.getId());
					b.setBizType(Biz.BIZ_TYPE_BIZ);
					b.setTitle(bo.getName());
					if(!StringUtils.isEmpty(bo.getTopology())){
						b.setContent(bo.getTopology().replace(replaceHolder, request.getContextPath()));
						b.setThumbnail(b.getContent());
					}
					bizs.add(b);
				}
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		return bizs;
	}

	@Override
	public List<String> getSourceIds(ILoginUser user) {
		List<String> sourceIds = new ArrayList<String>();
		List<String> results = bizServiceDao.getSourceIds();
		if(null!=results && results.size()>0){
			for(String s : results){
				if(user.isSystemUser() || user.isDomainUser() || user.isManagerUser()){
					sourceIds.add(s);
				}
			}
		}
		return sourceIds;
	}

	@Override
	public InstanceStateEnum getStateById(Long id) {
		String status = bizServiceDao.getStateById(id);
		InstanceStateEnum state = null;
		switch (status) {
		case "CRITICAL":
			state = InstanceStateEnum.CRITICAL;
			break;
		case "SERIOUS":
			state = InstanceStateEnum.SERIOUS;
			break;
		case "WARN":
			state = InstanceStateEnum.WARN;
			break;
		case "NORMAL":
			state = InstanceStateEnum.NORMAL;
			break;
		default:
			state = InstanceStateEnum.NORMAL;
			break;
		}
		return state;
	}

	@Override
	public List<BizServiceBo> getAllBuessinessApplication(long containInstanceId) {
		List<BizServiceBo> bos= new ArrayList<BizServiceBo>();
		List<CompositeInstance> compositeInstances=	compositeInstanceService.getCompositeInstancesByContainInstanceId(containInstanceId);
		if(compositeInstances!=null){
			for (CompositeInstance compositeInstance : compositeInstances) {
				if(InstanceTypeEnum.BUSINESS_APPLICATION.equals(compositeInstance.getInstanceType())){
					BizServiceBo bo=	bizServiceDao.getBizBuessinessById(compositeInstance.getId());
					bos.add(bo);
				}
			}
		}
		return bos;
	}
	
	/**
	 * 按名字模糊查询
	 * 
	 * @param List<BizServiceBo>
	 * @return
	 */
	public List<BizServiceBo> getByName(String name){
		return bizServiceDao.getByName(name);
	}

}
