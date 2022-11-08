package com.mainsteam.stm.portal.business.state;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import javax.annotation.Resource;

import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.util.StringUtils;

import com.mainsteam.stm.alarm.AlarmService;
import com.mainsteam.stm.alarm.obj.AlarmProviderEnum;
import com.mainsteam.stm.alarm.obj.AlarmSenderParamter;
import com.mainsteam.stm.alarm.obj.SysModuleEnum;
import com.mainsteam.stm.caplib.CapacityService;
import com.mainsteam.stm.caplib.dict.MetricTypeEnum;
import com.mainsteam.stm.caplib.resource.ResourceMetricDef;
import com.mainsteam.stm.common.instance.obj.InstanceStateEnum;
import com.mainsteam.stm.common.metric.InstanceStateService;
import com.mainsteam.stm.common.metric.MetricStateService;
import com.mainsteam.stm.instancelib.CompositeInstanceService;
import com.mainsteam.stm.instancelib.RelationService;
import com.mainsteam.stm.instancelib.ResourceInstanceService;
import com.mainsteam.stm.instancelib.obj.CompositeInstance;
import com.mainsteam.stm.instancelib.obj.PathRelation;
import com.mainsteam.stm.instancelib.obj.Relation;
import com.mainsteam.stm.instancelib.obj.ResourceInstance;
import com.mainsteam.stm.instancelib.objenum.InstanceTypeEnum;
import com.mainsteam.stm.lock.LockCallback;
import com.mainsteam.stm.lock.LockService;
import com.mainsteam.stm.portal.business.bo.BizServiceBo;
import com.mainsteam.stm.portal.business.bo.BizStatusSelfBo;
import com.mainsteam.stm.portal.business.dao.IBizServiceDao;
import com.mainsteam.stm.portal.business.dao.IBizStatusSelfDao;
import com.mainsteam.stm.profilelib.alarm.obj.AlarmRuleProfileEnum;
import com.mainsteam.stm.state.engine.StateHandle;
import com.mainsteam.stm.state.obj.InstanceStateChangeData;
import com.mainsteam.stm.state.obj.InstanceStateData;
import com.mainsteam.stm.state.obj.MetricStateChangeData;

/**
 * <li>文件名称: BizSerState.java</li>
 * <li>公　　司: 武汉美新翔盛科技有限公司</li>
 * <li>版权所有: 版权所有(C)2019-2020</li>
 * <li>修改记录: ...</li>
 * <li>内容摘要: 根据业务服务规则计算业务服务状态</li>
 * <li>其他说明: ...</li>
 * @version  ms.stm
 * @since    2019年9月18日
 * @author   caoyong
 */
public class BizSerState{
	/**
	 * 日志
	 */
	private Logger logger = Logger.getLogger(BizSerState.class);
	@Resource
	private IBizServiceDao protalBizServiceDao;
	@Resource
	private IBizStatusSelfDao protalBizStatusSelfDao;
	@Autowired
	private InstanceStateService instanceStateService ;
	@Autowired
	private MetricStateService metricStateService;
	@Autowired
	private CompositeInstanceService compositeInstanceService;
	@Autowired
	private ResourceInstanceService resourceInstanceService;
	@Autowired
	private RelationService relationService;
	@Autowired
	private LockService lockService;
	@Resource
	private AlarmService alarmService;
	@Autowired
	private CapacityService capacityService;
	public int getOrder() {
		return 10;
	}
	public void onInstanceStateChange(final InstanceStateChangeData arg0) {
		lockService.sync("bizserstate",new LockCallback<Object>() {
			@Override
			public Object doAction() {
				//当前状态改变的资源实例
				Long instanceId = arg0.getNewState().getInstanceID();
				String instanceNewState = arg0.getNewState().getState().name();
				logger.info("portal.business.state.onInstanceStateChange current instanceId:"
						+instanceId+";instanceState:"+instanceNewState+" influence begain........");
				try {
					//所有的业务应用
					List<CompositeInstance> compositeInstances = 
							compositeInstanceService.getCompositeInstanceByInstanceType(InstanceTypeEnum.BUSINESS_APPLICATION);
					for(CompositeInstance c:compositeInstances){
						//复合资源对应的业务应用记录
						BizServiceBo bso = protalBizServiceDao.get(c.getId());
						if(bso==null){ continue; }
						//所有关联的资源
						List<Relation> relations = relationService.getRelationByInstanceId(c.getId());
						List<Long> resourceInstanceIds = new ArrayList<Long>();
						if(null!=relations && relations.size()>0){
							for(Relation relation: relations){
								if(relation instanceof PathRelation){
									PathRelation pathRelation = (PathRelation) relation;
									if(pathRelation.getInstanceId()==c.getId()
											&& pathRelation.getFromInstanceType().equals(InstanceTypeEnum.BUSINESS_APPLICATION)
											&& pathRelation.getToInstanceType().equals(InstanceTypeEnum.RESOURCE)){
										resourceInstanceIds.add(pathRelation.getToInstanceId());
									}
								} 
							}
						}
						if(null==resourceInstanceIds || resourceInstanceIds.size()==0){
							String oldStatus = bso.getStatus();
							bso.setStatus(InstanceStateEnum.NORMAL.name());
							addAlarm(oldStatus, bso.getStatus(), bso);
							protalBizServiceDao.update(bso);
							continue;
						} 
						boolean changeFlag = false;
						for(Long i:resourceInstanceIds){
							if(instanceId.longValue() == i.longValue()){
								changeFlag = true;
								break;
							}
						}
						if(bso.getStatus_type().equals("1")){//自定义
							List<BizStatusSelfBo> bizStatusSelfBos = protalBizStatusSelfDao.getByBizSerId(c.getId());
							for(BizStatusSelfBo self:bizStatusSelfBos){
								if(self.getInstance_id() == instanceId && instanceNewState.equals(self.getState())){
									changeFlag = true;
									break;
								}
							}
						}
						if(changeFlag){
							String oldStatus = bso.getStatus();
							if(bso.getStatus_type().equals("1")){//自定义规则
								setStatusByDefinedRules(bso, resourceInstanceIds);
							}else{//默认
								setStatusBydefaultRules(bso, resourceInstanceIds);
							}
							if(!oldStatus.equals(bso.getStatus())){
								addAlarm(oldStatus, bso.getStatus(), bso);
								protalBizServiceDao.update(bso);
							}
						}
					}
					logger.info("portal.business.state.onInstanceStateChange current instanceId:"
							+instanceId+";instanceState:"+instanceNewState+" influence end........");
				} catch (Exception e) {
					e.printStackTrace();
					logger.error(e.getMessage(),e);
				}
				return null;
			}
		},20);
		
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
			logger.info("portal.business.state.addAlarm(business service id="+bizServiceBo.getId()+") successful");
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
	private void setStatusByDefinedRules(BizServiceBo bizServiceBo,List<Long> list) throws Exception{
		List<BizStatusSelfBo> bizStatusSelfBos = protalBizStatusSelfDao.getByBizSerId(bizServiceBo.getId());
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
//				if(i!=0l && instanceStateService.getState(i)!=null){
//					status = instanceStateService.getState(i).getState().name();
//				}
//				if(status.equals(InstanceStateEnum.UNKOWN.name())){
//					unknownFlag = true;
//				}else if(status.equals(InstanceStateEnum.NORMAL.name())){
//					normalFlag = true;
//				}
//			}
//			if(unknownFlag) bizServiceBo.setStatus(InstanceStateEnum.UNKOWN.name());
//			else if(normalFlag) bizServiceBo.setStatus(InstanceStateEnum.NORMAL.name());
			bizServiceBo.setStatus(InstanceStateEnum.NORMAL.name());
		}
	}
	private boolean setStatusFlag(List<BizStatusSelfBo> list,int operation) throws Exception{
		if(null==list || list.size()==0) return false;
		if(operation == 0){//并且
			boolean flag = true;
			for(BizStatusSelfBo self:list){
				if(self.getMetric_id()==null){
					if(null==instanceStateService.getState(self.getInstance_id()) ||
							!self.getState().equals(instanceStateService.getState(self.getInstance_id()).getState().name())){
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
					} catch (Exception e) {
					}
				}
			}
			return flag;
		}else if(operation == 1){//或者
			boolean flag = false;
			for(BizStatusSelfBo self:list){
				if(self.getMetric_id()==null){
					if(null==instanceStateService.getState(self.getInstance_id())){
						continue;
					}else if(self.getState().equals(instanceStateService.getState(self.getInstance_id()).getState().name())){
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
									break;
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
					}catch(Exception e){
					}
				}
			}
			return flag;
		}
		return false;
	} 
	
	private void setStatusBydefaultRules(BizServiceBo bizServiceBo,List<Long> list) throws Exception{
		boolean criticalFlag = false,seriousFlag = false,warnFlag = false;
		boolean normalFlag = false;
		for(Long i: list){
			if(i!=0l){
				InstanceStateData instanceStateData =  instanceStateService.getState(i);
				if(null!=instanceStateData){
					String status = "";
					status = instanceStateService.getState(i).getState().name();
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
	public void onMetricStateChange(final MetricStateChangeData arg0) {
		lockService.sync("bizserstate", new LockCallback<Object>() {
			@Override
			public Object doAction() {
				Long instanceId = arg0.getNewState().getInstanceID();
				String metricID = arg0.getNewState().getMetricID();
				String metricNewState = arg0.getNewState().getState().name();
				logger.info("portal.business.state.onMetricStateChange current instanceId:"
						+instanceId+";metricID:"+metricID+";metricState:"+metricNewState+" influence begain........");
				try {
					//所有的业务应用
					List<CompositeInstance> compositeInstances = 
							compositeInstanceService.getCompositeInstanceByInstanceType(InstanceTypeEnum.BUSINESS_APPLICATION);
					for(CompositeInstance c:compositeInstances){
						//复合资源对应的业务服务记录
						BizServiceBo bso = protalBizServiceDao.get(c.getId());
						if(bso==null || bso.getStatus_type().equals("0")){//默认规则
							continue;
						}else if(bso.getStatus_type().equals("1")){//自定义规则
							//找到自定义的规则
							List<BizStatusSelfBo> bizStatusSelfBos = protalBizStatusSelfDao.getByBizSerId(c.getId());
							//所有关联的资源
							List<Relation> relations = relationService.getRelationByInstanceId(c.getId());
							List<Long> resourceInstanceIds = new ArrayList<Long>();
							if(null!=relations && relations.size()>0){
								for(Relation relation: relations){
									if(relation instanceof PathRelation){
										PathRelation pathRelation = (PathRelation) relation;
										if(pathRelation.getInstanceId()==c.getId()
												&& pathRelation.getFromInstanceType().equals(InstanceTypeEnum.BUSINESS_APPLICATION)
												&& pathRelation.getToInstanceType().equals(InstanceTypeEnum.RESOURCE)){
											resourceInstanceIds.add(pathRelation.getToInstanceId());
										}
									} 
								}
							}
							if(resourceInstanceIds == null || resourceInstanceIds.size()==0){
								String oldStatus = bso.getStatus();
								bso.setStatus(InstanceStateEnum.NORMAL.name());
								addAlarm(oldStatus, bso.getStatus(), bso);
								continue;
							} 
							boolean changeFlag = false;
							for(BizStatusSelfBo self:bizStatusSelfBos){
								if(self.getInstance_id() == instanceId && metricID.equals(self.getMetric_id())
										&& metricNewState.equals(self.getState())){
									changeFlag = true;
									break;
								}
							}
							if(changeFlag){
								String oldStatus = bso.getStatus();
								setStatusByDefinedRules(bso, resourceInstanceIds);
								if(!oldStatus.equals(bso.getStatus())){
									addAlarm(oldStatus, bso.getStatus(), bso);
									protalBizServiceDao.update(bso);
								}
							}
						}
					}
					logger.info("portal.business.state.onMetricStateChange current instanceId:"
							+instanceId+";metricID:"+metricID+";metricState:"+metricNewState+" influence begain........");
				} catch (Exception e) {
					logger.error(e.getMessage(),e);
				}
				return null;
			}
		}, 20);
	}
	
}
