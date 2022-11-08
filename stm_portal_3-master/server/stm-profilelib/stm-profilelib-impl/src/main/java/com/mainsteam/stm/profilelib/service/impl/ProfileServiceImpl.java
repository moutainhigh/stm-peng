package com.mainsteam.stm.profilelib.service.impl;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang.StringUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import com.mainsteam.stm.caplib.CapacityService;
import com.mainsteam.stm.caplib.dict.CapacityConst;
import com.mainsteam.stm.caplib.dict.FrequentEnum;
import com.mainsteam.stm.caplib.dict.LinkResourceConsts;
import com.mainsteam.stm.caplib.dict.PerfMetricStateEnum;
import com.mainsteam.stm.caplib.dict.ResourceTypeConsts;
import com.mainsteam.stm.caplib.resource.ResourceDef;
import com.mainsteam.stm.caplib.resource.ResourceMetricDef;
import com.mainsteam.stm.caplib.resource.ThresholdDef;
import com.mainsteam.stm.errorcode.ServerErrorCodeConstant;
import com.mainsteam.stm.instancelib.ResourceInstanceService;
import com.mainsteam.stm.instancelib.exception.InstancelibException;
import com.mainsteam.stm.instancelib.obj.ResourceInstance;
import com.mainsteam.stm.instancelib.objenum.InstanceLifeStateEnum;
import com.mainsteam.stm.instancelib.service.ResourceInstanceExtendService;
import com.mainsteam.stm.platform.sequence.service.ISequence;
import com.mainsteam.stm.profilelib.ProfileService;
import com.mainsteam.stm.profilelib.TimelineService;
import com.mainsteam.stm.profilelib.bean.ProfileSwitchConvert;
import com.mainsteam.stm.profilelib.bean.ProfileSwitchRelation;
import com.mainsteam.stm.profilelib.bean.TempResult;
import com.mainsteam.stm.profilelib.dao.LastProfileDAO;
import com.mainsteam.stm.profilelib.dao.ProfileChangeDAO;
import com.mainsteam.stm.profilelib.dao.ProfileDAO;
import com.mainsteam.stm.profilelib.dao.ProfileInstanceRelationDAO;
import com.mainsteam.stm.profilelib.dao.ProfileMetricDAO;
import com.mainsteam.stm.profilelib.dao.ProfileThresholdDAO;
import com.mainsteam.stm.profilelib.exception.ProfilelibException;
import com.mainsteam.stm.profilelib.interceptor.ProfileChangeManager;
import com.mainsteam.stm.profilelib.obj.Instance;
import com.mainsteam.stm.profilelib.obj.MetricSetting;
import com.mainsteam.stm.profilelib.obj.Profile;
import com.mainsteam.stm.profilelib.obj.ProfileChangeData;
import com.mainsteam.stm.profilelib.obj.ProfileInfo;
import com.mainsteam.stm.profilelib.obj.ProfileInstanceRelation;
import com.mainsteam.stm.profilelib.obj.ProfileMetric;
import com.mainsteam.stm.profilelib.obj.ProfileSwitchData;
import com.mainsteam.stm.profilelib.obj.ProfileThreshold;
import com.mainsteam.stm.profilelib.obj.Threshold;
import com.mainsteam.stm.profilelib.obj.Timeline;
import com.mainsteam.stm.profilelib.obj.TimelineInfo;
import com.mainsteam.stm.profilelib.objenum.ProfileChangeEnum;
import com.mainsteam.stm.profilelib.objenum.ProfileTypeEnum;
import com.mainsteam.stm.profilelib.po.ProfileChangePO;
import com.mainsteam.stm.profilelib.po.ProfileInfoPO;
import com.mainsteam.stm.profilelib.po.ProfileInstRelationPO;
import com.mainsteam.stm.profilelib.po.ProfileMetricPO;
import com.mainsteam.stm.profilelib.po.ProfileThresholdPO;
import com.mainsteam.stm.profilelib.service.ProfileExtendService;
import com.mainsteam.stm.profilelib.service.impl.querybean.QueryProfileMetric;
import com.mainsteam.stm.profilelib.util.ProfileCache;
import com.mainsteam.stm.util.ResourceOrMetricConst;


public class ProfileServiceImpl implements ProfileService, ProfileExtendService {

	private static final Log logger = LogFactory.getLog(ProfileServiceImpl.class);
	// 表示接口、服务启动状态
	private static String SUCCESS_STATE = "1";
	private ProfileDAO profileDAO;
	private ProfileInstanceRelationDAO profileInstRelDAO;
	private ProfileMetricDAO profileMetricDAO;
	private ProfileThresholdDAO profileThresholdDAO;
	private ProfileChangeDAO profileChangeDAO;
	private LastProfileDAO lastProfileDAO;
	private ResourceInstanceService resourceInstanceService;
	private ResourceInstanceExtendService resourceInstanceExtendService;
	private CapacityService capacityService;
	private TimelineService timelineService;
	private ISequence ocProfilelibMainSequence;
	private ISequence ocProfilelibMetricSequence;
	private ISequence ocProfilelibThresholdSequence;
	private ISequence ocProfileChangeSequence;
	private ProfileCache profileCache;
	private ProfileChangeManager profileMetricAlarmManager;
	private ProfileChangeManager profileMetricMonitorManager;
	private ProfileChangeManager profileResourceCancelMonitorManager;
	private ProfileChangeManager profileSwitchManager;
	
	private Calendar calendarInstance = Calendar.getInstance();

	private QueryProfileMetric convertToQyeryProfileMetric(ProfileMetric metric){
		return new QueryProfileMetric(metric, this);
	}
	
	@Override
	public long addMonitorUseDefault(long instanceId) throws ProfilelibException {
		if (logger.isInfoEnabled()) {
			logger.info("addMonitorUseDefault start mainInstId=" + instanceId);
		}
		long profileId = checkMonitorDefaultProfileIdByInstId(instanceId);
		if (profileId > 0) {
			if (logger.isInfoEnabled()) {
				logger.info("addMonitorUseDefault end mainInstId=" + instanceId);
			}
			String msg = "ResourceInstance:" + instanceId + "has monitor!";
			throw new ProfilelibException(ServerErrorCodeConstant.ERR_SERVER_PROFILE_INTANCE_REPEAT, msg);
		}

		ResourceInstance mainInst = null;
		List<ResourceInstance> subInsts = null;
		try {
			mainInst = resourceInstanceService.getResourceInstance(instanceId);
			subInsts = mainInst.getChildren();
		} catch (Exception e) {
			if (logger.isErrorEnabled()) {
				logger.error("addMonitorUseDefault: get resource intance error", e);
			}
			throw new ProfilelibException(ServerErrorCodeConstant.ERR_SERVER_OPERATE_DATABASE_ERROR, " get resource intance error");
		}
		//是否是第一次加入监控，判断当前是否有策略绑定，如果没有，就过滤down，否则不过滤
		ProfileInfo info = getBasicInfoByResourceInstanceId(instanceId);
		List<ResourceInstance> children = null;
		if(info == null){
			children = filterDown(mainInst, subInsts);
		}else{
			children = subInsts;
		}
		List<ProfileInstRelationPO> syncTOcollector = new ArrayList<>();
		HashMap<Long, Long> syncLastTOCollector = new HashMap<>();

		// 添加策略，策略与实例，指标，阈值到数据库，返回主策略ID
		TempResult r = monitorInstanceDefault(mainInst, children, syncTOcollector, syncLastTOCollector);
		long newMainProfileId = r.getProfileId();

		updateInstanceStates(syncTOcollector, syncLastTOCollector, InstanceLifeStateEnum.MONITORED);
		// 放入同步到数据到数据库，job 查询记录然后
		addMonitorDataToDB(syncTOcollector, syncLastTOCollector);
		if(r.getProfileSwitchConvert() != null){
			switchProfileNotice(r.getProfileSwitchConvert(), ProfileTypeEnum.DEFAULT);
		}
		if (logger.isInfoEnabled()) {
			logger.info("addMonitorUseDefault end mainInstId=" + instanceId);
		}
		return newMainProfileId;
	}
	
	@Override
	public long addMonitorUseDefaultForTopo(long instanceId) throws ProfilelibException {
		if (logger.isTraceEnabled()) {
			logger.trace("addMonitorUseDefault start mainInstId=" + instanceId);
		}
		long profileId = checkMonitorDefaultProfileIdByInstId(instanceId);
		if (profileId > 0) {
			if (logger.isTraceEnabled()) {
				logger.trace("addMonitorUseDefault end mainInstId=" + instanceId);
			}
			String msg = "ResourceInstance:" + instanceId + "has monitor!";
			throw new ProfilelibException(ServerErrorCodeConstant.ERR_SERVER_PROFILE_INTANCE_REPEAT, msg);
		}

		ResourceInstance mainInst = null;
		List<ResourceInstance> subInsts = null;
		try {
			mainInst = resourceInstanceService.getResourceInstance(instanceId);
			subInsts = mainInst.getChildren();
		} catch (Exception e) {
			if (logger.isErrorEnabled()) {
				logger.error("addMonitorUseDefault: get resource intance error", e);
			}
			throw new ProfilelibException(ServerErrorCodeConstant.ERR_SERVER_OPERATE_DATABASE_ERROR, " get resource intance error");
		}
		List<ResourceInstance> children = filterDown(mainInst, subInsts);
		List<ProfileInstRelationPO> syncTOcollector = new ArrayList<>();
		HashMap<Long, Long> syncLastTOCollector = new HashMap<>();
		// 添加策略，策略与实例，指标，阈值到数据库，返回主策略ID
		TempResult r = monitorInstanceDefault(mainInst, children, syncTOcollector, syncLastTOCollector);
		long newMainProfileId = r.getProfileId();
		updateInstanceStates(syncTOcollector, syncLastTOCollector, InstanceLifeStateEnum.MONITORED);
		// 放入同步到数据到数据库，job 查询记录然后
		addMonitorDataToDB(syncTOcollector, syncLastTOCollector);
		if(r.getProfileSwitchConvert() != null){
			switchProfileNotice(r.getProfileSwitchConvert(), ProfileTypeEnum.DEFAULT);
		}
		return newMainProfileId;
	}


	/**
	 * 批量监控，不存在策略切换。页面策略切换只针对单个资源操作
	 */
	@Override
	public Map<Long, Long> addMonitorUseDefault(List<Long> instanceIds) throws ProfilelibException {
		if (logger.isInfoEnabled()) {
			logger.info("batch addMonitor use default start,instanceIds:" + instanceIds);
		}
		if (null == instanceIds || instanceIds.isEmpty()) {
			if (logger.isErrorEnabled()) {
				logger.error("batch addMonitor use default end:instanceIds is null!");
			}
			return null;
		}
		Map<Long, Long> instanceProfileResult = new HashMap<Long, Long>();
		/**
		 * 检查资源实例是否已监控，已监控的资源不做任何操作
		 * */
		Map<Long, Long> checkResult = checkMonitorDefaultProfileIdByInstId(instanceIds);
		// 已监控资源ID列表
		List<Long> isMonitorInstanceIds = new ArrayList<Long>();
		// 未监控资源ID列表
		List<Long> isNotMonitorInstanceIds = new ArrayList<Long>();
		for (Map.Entry<Long, Long> instanceProfile : checkResult.entrySet()) {
			if (instanceProfile.getValue() > 0) {
				isMonitorInstanceIds.add(instanceProfile.getKey());
				instanceProfileResult.put(instanceProfile.getKey(), instanceProfile.getValue());
			} else {
				isNotMonitorInstanceIds.add(instanceProfile.getKey());
			}
		}
		if (!isMonitorInstanceIds.isEmpty()) {
			logger.info("batch addMonitor use default instanceIds:" + isMonitorInstanceIds + " has monitor!");
		}
		// 如果未监控资源ID列表为空，表示所有资源均已经加入监控
		if (isNotMonitorInstanceIds.isEmpty()) {
			if (logger.isInfoEnabled()) {
				logger.info("not monitor is empty,batch addMonitor use default end,instanceIds:" + instanceIds);
			}
			return instanceProfileResult;
		}

		Map<ResourceInstance, List<ResourceInstance>> mainResourceInstanceSubRel = new HashMap<ResourceInstance, List<ResourceInstance>>();
		for (Long mainInstanceId : isNotMonitorInstanceIds) {
			try {
				ResourceInstance mainInst = resourceInstanceService.getResourceInstance(mainInstanceId);
				mainResourceInstanceSubRel.put(mainInst, mainInst.getChildren());
			} catch (InstancelibException e) {
				if (logger.isErrorEnabled()) {
					logger.error("batch addMonitor use default, get instance [" + mainInstanceId + "] error:" + e);
				}
			}
		}

		//List<ProfileInstRelationPO> syncTOcollector = new ArrayList<>();
		//插入绑定关系
		List<ProfileInstRelationPO> profileInstRelationPOs = new ArrayList<ProfileInstRelationPO>();
		Map<Long,InstanceLifeStateEnum> updatNotMonitored = new HashMap<Long, InstanceLifeStateEnum>();
		/*
		 * 需要判断策略是否创建。创建成功后，再根据模型对应的策略加入监控。
		 */
		//key:resourceId  values:profileId
		HashMap<String,Long> default_resourceId_profileId = new HashMap<String, Long>();
		//获取所有的默认策略-- 只查询一次数据库
		List<ProfileInfo> defaultProfiles = getAllProfileBasicInfo(ProfileTypeEnum.DEFAULT);
		if(CollectionUtils.isNotEmpty(defaultProfiles)){
			for (ProfileInfo profileInfo : defaultProfiles) {
				if(!default_resourceId_profileId.containsKey(profileInfo.getResourceId())){
					default_resourceId_profileId.put(profileInfo.getResourceId(), profileInfo.getProfileId());
				}
			}
		}
		for (Map.Entry<ResourceInstance, List<ResourceInstance>> instanceSubRel : mainResourceInstanceSubRel.entrySet()) {
			ResourceInstance mainInstance = instanceSubRel.getKey();
			long monotorProfileId = 0;
			try {
				Long value_ProfileId = default_resourceId_profileId.get(mainInstance.getResourceId());
				if(value_ProfileId == null){
					//创建默认策略
					ResourceDef mainResource = capacityService.getResourceDefById(mainInstance.getResourceId());
					monotorProfileId = ocProfilelibMainSequence.next();
					// 通过模型创建默认策略
					HashMap<String,Long> result = createDefaultSubProfileInfo(monotorProfileId, mainResource);
					default_resourceId_profileId.putAll(result);
				} else {
					monotorProfileId = value_ProfileId.longValue();
				}
			} catch (Exception e1) {
			}
			if(monotorProfileId == 0){
				logger.info("instanceId:" + mainInstance.getId() + " create default profile failed.");
				continue;
			}
			List<ResourceInstance> children = filterDown(mainInstance, instanceSubRel.getValue());
			// 构造绑定关系
			ProfileInstRelationPO parentProfileInstRelationPO = new ProfileInstRelationPO();
			parentProfileInstRelationPO.setInstanceId(mainInstance.getId());
			parentProfileInstRelationPO.setProfileId(monotorProfileId);
			profileInstRelationPOs.add(parentProfileInstRelationPO);
			if(CollectionUtils.isNotEmpty(children)){
				for (ResourceInstance chidResourceInstance : children) {
					Long chidProfileId = default_resourceId_profileId.get(chidResourceInstance.getResourceId());
					if(chidProfileId == null){ 
						//不添加监控，状态是Monitored,需要再次赋值not_monitor..
						if(chidResourceInstance.getLifeState() == InstanceLifeStateEnum.MONITORED){
							updatNotMonitored.put(chidResourceInstance.getId(), InstanceLifeStateEnum.NOT_MONITORED);	
						}
					} else {
						ProfileInstRelationPO childProfileInstRelationPO = new ProfileInstRelationPO();
						childProfileInstRelationPO.setInstanceId(chidResourceInstance.getId());
						childProfileInstRelationPO.setProfileId(chidProfileId.longValue());
						childProfileInstRelationPO.setParentInstanceId(mainInstance.getId());
						profileInstRelationPOs.add(childProfileInstRelationPO);
					}
				}
			}
			instanceProfileResult.put(mainInstance.getId(), monotorProfileId);
		}
		try {
			//先移除现有数据库存在的绑定（未监控但是有绑定关系这种），防止数据出错误
			profileInstRelDAO.removeInstRelByparentInstIds(isNotMonitorInstanceIds);
			profileInstRelDAO.insertInstRels(profileInstRelationPOs);
			if(!updatNotMonitored.isEmpty()){
				resourceInstanceService.updateResourceInstanceState(updatNotMonitored);
			}
			//放入同步到数据到数据库用于同步到DCS
			updateInstanceStates(profileInstRelationPOs, null, InstanceLifeStateEnum.MONITORED);
			//放入同步到数据到数据库用于同步到DCS
			addMonitorDataToDB(profileInstRelationPOs, null);
		} catch (Exception e) {
			if(logger.isErrorEnabled()){
				logger.error("", e);
			}
		}
		if (logger.isInfoEnabled()) {
			logger.info("batch addMonitor use default end,instanceIds:" + instanceIds);
		}
		return instanceProfileResult;
	}
	
	
	@Override
	public Map<Long, Long> addMonitorUseDefaultForTopo(List<Long> instanceIds) throws ProfilelibException {
//		if (null == instanceIds || instanceIds.isEmpty()) {
//			if (logger.isErrorEnabled()) {
//				logger.error("batch addMonitor use default error:instanceIds is null!");
//			}
//			return null;
//		}
//		Map<Long, Long> instanceProfileResult = new HashMap<Long, Long>();
//		/**
//		 * 检查资源实例是否已监控，已监控的资源不做任何操作
//		 * */
//		Map<Long, Long> checkResult = checkMonitorDefaultProfileIdByInstId(instanceIds);
//		// 未监控资源ID列表
//		List<Long> isNotMonitorInstanceIds = new ArrayList<Long>();
//		for (Map.Entry<Long, Long> instanceProfile : checkResult.entrySet()) {
//			isNotMonitorInstanceIds.add(instanceProfile.getKey());
//		}
//		// 如果未监控资源ID列表为空，表示所有资源均已经加入监控
//		if (isNotMonitorInstanceIds.isEmpty()) {
//			return instanceProfileResult;
//		}
//
//		Map<ResourceInstance, List<ResourceInstance>> mainResourceInstanceSubRel = new HashMap<ResourceInstance, List<ResourceInstance>>();
//		for (Long mainInstanceId : isNotMonitorInstanceIds) {
//			try {
//				ResourceInstance mainInst = resourceInstanceService.getResourceInstance(mainInstanceId);
//				mainResourceInstanceSubRel.put(mainInst, mainInst.getChildren());
//			} catch (InstancelibException e) {
//				if (logger.isErrorEnabled()) {
//					logger.error("batch addMonitor use default, get instance [" + mainInstanceId + "] error:" + e);
//				}
//			}
//		}
//
//		List<ProfileInstRelationPO> syncTOcollector = new ArrayList<>();
//		HashMap<Long, Long> syncLastTOCollector = new HashMap<>();
//		for (Map.Entry<ResourceInstance, List<ResourceInstance>> instanceSubRel : mainResourceInstanceSubRel.entrySet()) {
//			ResourceInstance mainInstance = instanceSubRel.getKey();
//			List<ResourceInstance> children = filterDown(mainInstance, instanceSubRel.getValue());
//			// 添加策略，策略与实例，指标，阈值到数据库，返回主策略ID
//			TempResult r = monitorInstanceDefault(mainInstance, children, syncTOcollector, syncLastTOCollector);
//			long newMainProfileId = r.getProfileId();
//			updateInstanceStates(syncTOcollector, syncLastTOCollector, InstanceLifeStateEnum.MONITORED);
//			// 放入同步到数据到数据库，job 查询记录然后
//			addMonitorDataToDB(syncTOcollector, syncLastTOCollector);
//			instanceProfileResult.put(mainInstance.getId(), newMainProfileId);
//			if(r.getProfileSwitchConvert() != null){
//				switchProfileNotice(r.getProfileSwitchConvert(), ProfileTypeEnum.DEFAULT);
//			}
//		}
		return addMonitorUseDefaultForTopo(instanceIds);
	}
	

	@Override
	public void addMonitorUseDefault(long mainInstanceId, List<Long> chirdrenInstanceId) throws ProfilelibException {
		if (logger.isInfoEnabled()) {
			StringBuilder b = new StringBuilder(100);
			b.append("addMonitorUseDefault start mainInstId=");
			b.append(mainInstanceId);
			b.append(" chirdren:" + chirdrenInstanceId);
			logger.info(b.toString());
		}
		ResourceInstance mainInst = null;
		List<ResourceInstance> subInsts = new ArrayList<ResourceInstance>();
		try {
			mainInst = resourceInstanceService.getResourceInstance(mainInstanceId);
			List<ResourceInstance> allSubInsts = mainInst.getChildren();
			if(null!=allSubInsts && !allSubInsts.isEmpty()){
				for (ResourceInstance resourceInstance : allSubInsts) {
					if (chirdrenInstanceId.contains(resourceInstance.getId())) {
						subInsts.add(resourceInstance);
					}
				}
			}
		} catch (Exception e) {
			if (logger.isErrorEnabled()) {
				logger.error("addMonitorUseDefault: get resource intance error", e);
			}
			throw new ProfilelibException(ServerErrorCodeConstant.ERR_SERVER_OPERATE_DATABASE_ERROR, " get resource intance error");
		}
		List<ProfileInstRelationPO> syncTOcollector = new ArrayList<>();
		HashMap<Long, Long> syncLastTOCollector = new HashMap<>();

		// 添加策略，策略与实例，指标，阈值到数据库，返回主策略ID
		monitorInstanceDefault(mainInst, subInsts, syncTOcollector, syncLastTOCollector);

		updateInstanceStates(syncTOcollector, syncLastTOCollector, InstanceLifeStateEnum.MONITORED);
		// 放入同步到数据到数据库，job 查询记录然后
		addMonitorDataToDB(syncTOcollector, syncLastTOCollector);
		if (logger.isInfoEnabled()) {
			StringBuilder b = new StringBuilder(100);
			b.append("addMonitorUseDefault end mainInstId=");
			b.append(mainInstanceId);
			b.append(" chirdren:" + chirdrenInstanceId);
			logger.info(b.toString());
		}
	}

	@Override
	public void addMonitorUsePersonalize(long parentProfileId, long parentInstanceId) throws ProfilelibException {
		if (logger.isInfoEnabled()) {
			StringBuilder b = new StringBuilder();
			b.append("addMonitorUsePersonalize start parentInstanceId=").append(parentInstanceId);
			logger.info(b);
		}
		boolean switchMonitorNotice = false;
		ProfileSwitchRelation relations = switchRemoveProfileInstanceRelByResource(parentInstanceId);
		HashMap<Long, Long> deleted  = relations.getBeforeDeletingAllRelation();
		List<ProfileInstRelationPO> profileInstRelationPOs = new ArrayList<ProfileInstRelationPO>();
		Long lastParentProfileId = deleted.get(parentInstanceId);
		//用于临时存放子资源模型绑定的资源，方便通知使用
		HashMap<String,List<Long>> childMonitor = new HashMap<String, List<Long>>();
		//本次策略跟上次策略不一致需要通知-策略切换通知
		if(lastParentProfileId != null){
			if( lastParentProfileId.longValue() != parentProfileId){
				switchMonitorNotice = true;
			}
		} else {
			lastParentProfileId = 0L;
		}
		// ResourceId与子资源实例集合 key:resourceId value: 资源实例id
		HashMap<String, HashSet<Long>> childResourceIdAndInstanceIds = new HashMap<>(20);
		List<ProfileInfoPO> profileInfoPOs = null;
		try {
			List<ResourceInstance> children = resourceInstanceService.getChildInstanceByParentId(parentInstanceId);
			if (CollectionUtils.isNotEmpty(children)) {
				for (ResourceInstance resourceInstance : children) {
					if(resourceInstance.getLifeState() == InstanceLifeStateEnum.MONITORED){
						String resourceId = resourceInstance.getResourceId();
						if (childResourceIdAndInstanceIds.containsKey(resourceId)) {
							childResourceIdAndInstanceIds.get(resourceId).add(resourceInstance.getId());
						} else {
							HashSet<Long> child = new HashSet<Long>();
							child.add(resourceInstance.getId());
							childResourceIdAndInstanceIds.put(resourceInstance.getResourceId(), child);
						}
						if(switchMonitorNotice){
							List<Long> childInstanceIds = childMonitor.get(resourceId);
							if(childInstanceIds == null){
								childInstanceIds = new ArrayList<Long>();
							}
							childInstanceIds.add(resourceInstance.getId());
							childMonitor.put(resourceId, childInstanceIds);
						}
					}
				}
			}
			profileInfoPOs = profileDAO.getPersonalizeProfileBasicInfoByParentProfileId(parentProfileId);

			if (CollectionUtils.isNotEmpty(profileInfoPOs)) {
				for (ProfileInfoPO profileInfoPO : profileInfoPOs) {
					String resournceId = profileInfoPO.getResourceId();
					long instanceId = profileInfoPO.getResourceInstanceId();
					ProfileInstRelationPO profileInstRelationPO = null;
					if(profileInfoPO.getProfileId() != parentProfileId ){
						// 个性化策略子
						HashSet<Long> instanceIds = childResourceIdAndInstanceIds.get(resournceId);
						if (CollectionUtils.isNotEmpty(instanceIds)) {
							for (long id : instanceIds) {
								profileInstRelationPO = new ProfileInstRelationPO();
								profileInstRelationPO.setInstanceId(id);
								profileInstRelationPO.setProfileId(profileInfoPO.getProfileId());
								profileInstRelationPO.setParentInstanceId(parentInstanceId);
								profileInstRelationPOs.add(profileInstRelationPO);
							}
							//只执行一次绑定，个性化模型有可能会有多个子策略
							childResourceIdAndInstanceIds.remove(resournceId);
						}
					} else {
						// 个性化策略父
						profileInstRelationPO = new ProfileInstRelationPO();
						profileInstRelationPO.setInstanceId(instanceId);
						profileInstRelationPO.setProfileId(profileInfoPO.getProfileId());
						profileInstRelationPOs.add(profileInstRelationPO);
					}
				}
			}
		} catch (Exception e) {
			if (logger.isErrorEnabled()) {
				logger.error("", e);
			}
			throw new ProfilelibException(ServerErrorCodeConstant.ERR_SERVER_OPERATE_DATABASE_ERROR, "addMonitorUsePersonalize error!");
		}

		try {
			// 插入策略实例信息
			profileInstRelDAO.insertInstRels(profileInstRelationPOs);
			
			updateInstanceStates(profileInstRelationPOs, deleted, InstanceLifeStateEnum.MONITORED);
			
			/*
			 * 同步到采集器数据入库
			 */
			addMonitorDataToDB(profileInstRelationPOs, deleted);
			if(switchMonitorNotice){
				Profile p = getProfilesById(lastParentProfileId);
				if(p == null){
					StringBuilder b = new StringBuilder(100);
					b.append("addMonitorUsePersonalize last profile profileId=");
					b.append(lastParentProfileId);
					b.append(" query is null");
					logger.error(b.toString());
				}else{
					ProfileSwitchConvert convert = new ProfileSwitchConvert();
					convert.setChildMonitor(childMonitor);
					convert.setCurrentProfileId(parentProfileId);
					convert.setLastProfileId(lastParentProfileId);
					convert.setParentInstanceId(parentInstanceId);
					switchProfileNotice(convert,ProfileTypeEnum.PERSONALIZE);
				}
			}
		} catch (Exception e) {
			if (logger.isErrorEnabled()) {
				logger.error("addMonitorUsePersonalize error!", e);
			}
			throw new ProfilelibException(ServerErrorCodeConstant.ERR_SERVER_OPERATE_DATABASE_ERROR, "insertInstRels error");
		}
		if (logger.isInfoEnabled()) {
			StringBuilder b = new StringBuilder();
			b.append("addMonitorUsePersonalize end parentInstanceId=").append(parentInstanceId);
			logger.info(b);
		}
	}

	@Override
	public void addMonitorUseSpecial(long profileId, long parentInstanceId) throws ProfilelibException {
		if (logger.isInfoEnabled()) {
			StringBuilder b = new StringBuilder();
			b.append("addMonitorUseSpecial start profileId=").append(profileId);
			b.append(" parentInstanceId=").append(parentInstanceId);
			logger.info(b);
		}
		// 删除已绑定的资源与策略关系
		boolean switchMonitorNotice = false;
		ProfileSwitchRelation relations = switchRemoveProfileInstanceRelByResource(parentInstanceId);
		HashMap<Long, Long> deleted  = relations.getBeforeDeletingAllRelation();
		
		Long lastParentProfileId = deleted.get(parentInstanceId);
		//用于临时存放子资源模型绑定的资源，方便通知使用
		HashMap<String,List<Long>> childMonitor = new HashMap<>();
		//本次策略跟上次策略不一致需要通知--取消监控通知
		if(lastParentProfileId != null){
			if( lastParentProfileId.longValue() != profileId){
				switchMonitorNotice  = true;
			}
		}else{
			lastParentProfileId = 0L;
		}
		
		List<ProfileInstRelationPO> profileInstRelationPOs = new ArrayList<>();
		ProfileInstRelationPO profileInstRelationPO = new ProfileInstRelationPO();
		profileInstRelationPO.setInstanceId(parentInstanceId);
		profileInstRelationPO.setProfileId(profileId);
		profileInstRelationPOs.add(profileInstRelationPO);

		try {
			List<ResourceInstance> children = resourceInstanceService.getChildInstanceByParentId(parentInstanceId);
			if (null != children && !children.isEmpty()) {
				HashSet<String> resourceIds = new HashSet<String>();
				for (ResourceInstance resourceInstance : children) {
					if(resourceInstance.getLifeState() == InstanceLifeStateEnum.MONITORED){
						String resourceId = resourceInstance.getResourceId();
						resourceIds.add(resourceId);
						if(switchMonitorNotice){
							List<Long> childInstanceIds = childMonitor.get(resourceId);
							if(childInstanceIds == null){
								childInstanceIds = new ArrayList<Long>();
							}
							childInstanceIds.add(resourceInstance.getId());
							childMonitor.put(resourceId, childInstanceIds);
						}
					}
				}
				List<ProfileInfoPO> existProfileInfoPOs = null;
				// 需要判断自定义策略子策略是否存在，不存在，改子实例不加入到监控
				if(resourceIds.isEmpty()){
					existProfileInfoPOs = new ArrayList<ProfileInfoPO>();
				}else{
					ProfileInfoPO poQuery = new ProfileInfoPO();
					poQuery.setParentProfileId(profileId);
					poQuery.setProfileType(ProfileTypeEnum.SPECIAL.toString());
					existProfileInfoPOs = profileDAO.getProfileInfoPO(poQuery);
				}
				HashMap<String, Long> resourceAndProfile = new HashMap<>();
				for (ProfileInfoPO profileInfoPO : existProfileInfoPOs) {
					resourceAndProfile.put(profileInfoPO.getResourceId(), profileInfoPO.getProfileId());
				}
				Map<Long,InstanceLifeStateEnum> updatNotMonitored = new HashMap<Long, InstanceLifeStateEnum>();
				for (ResourceInstance resourceInstance : children) {
					if(resourceInstance.getLifeState() == InstanceLifeStateEnum.MONITORED){
						// 只有子策略存在的时候。子实例才加入到监控
						if (resourceAndProfile.containsKey(resourceInstance.getResourceId())) {
							long tempProfileId = resourceAndProfile.get(resourceInstance.getResourceId());
							ProfileInstRelationPO childProfileInstRelationPO = new ProfileInstRelationPO();
							childProfileInstRelationPO.setInstanceId(resourceInstance.getId());
							childProfileInstRelationPO.setProfileId(tempProfileId);
							childProfileInstRelationPO.setParentInstanceId(parentInstanceId);
							profileInstRelationPOs.add(childProfileInstRelationPO);
						}else{
							updatNotMonitored.put(resourceInstance.getId(), InstanceLifeStateEnum.NOT_MONITORED);
						}
					}
				}
				if(!updatNotMonitored.isEmpty()){
					try {
						resourceInstanceService.updateResourceInstanceState(updatNotMonitored);
					} catch (Exception e) {
						if (logger.isErrorEnabled()) {
							logger.error("update resource instance state error:", e);
						}
						throw new ProfilelibException(ServerErrorCodeConstant.ERR_SERVER_OPERATE_DATABASE_ERROR, "updateResourceInstanceState error");
					}
				}
			}
			profileInstRelDAO.insertInstRels(profileInstRelationPOs);
			updateInstanceStates(profileInstRelationPOs, deleted, InstanceLifeStateEnum.MONITORED);
			addMonitorDataToDB(profileInstRelationPOs, deleted);
			if(switchMonitorNotice){
				Profile p = getProfilesById(lastParentProfileId);
				if(p == null){
					StringBuilder b = new StringBuilder(100);
					b.append("addMonitorUseSpecial last profile profileId=");
					b.append(lastParentProfileId);
					b.append(" query is null");
					logger.error(b.toString());
				}else{
					ProfileSwitchConvert convert = new ProfileSwitchConvert();
					convert.setChildMonitor(childMonitor);
					convert.setCurrentProfileId(profileId);
					convert.setLastProfileId(lastParentProfileId);
					convert.setParentInstanceId(parentInstanceId);
					switchProfileNotice(convert,ProfileTypeEnum.SPECIAL);
				}
			}
		} catch (Exception e) {
			if (logger.isErrorEnabled()) {
				logger.error("addMonitorUseSpecial error!", e);
			}
			throw new ProfilelibException(4345, "");
		}
		if (logger.isInfoEnabled()) {
			StringBuilder b = new StringBuilder();
			b.append("addMonitorUseSpecial end profileId=").append(profileId);
			b.append(" parentInstanceId=").append(parentInstanceId);
			logger.info(b);
		}
	}

	@Override
	public void addProfileInstance(long profileId, List<Long> instances) throws ProfilelibException {
		if(CollectionUtils.isEmpty(instances)){
			if(logger.isInfoEnabled()){
				logger.info("addProfileInstance end, instanceIds is empty!");
			}
			return;
		}
		if (logger.isInfoEnabled()) {
			StringBuilder b = new StringBuilder();
			b.append("addProfileInstance start profileId="+ profileId +" and instanceIds=").append(instances);
			logger.info(b);
		}
		try {
			ProfileInfoPO profileInfoPO = profileDAO.getProfilePoById(profileId);
			HashMap<Long, Long> deleted = new HashMap<>();
			if (profileInfoPO != null) {
				// 如果是个性化策略，需要修改个性化策略状态
//				if (ProfileTypeEnum.PERSONALIZE.toString().equals(profileInfoPO.getProfileType())) {
//					profileInfoPO.setIsUse("1");
//					profileDAO.updateProfile(profileInfoPO);
//				}
				if (profileInfoPO.getParentProfileId() == null || profileInfoPO.getParentProfileId() == 0) {
					// 父
					//deleted = removeProfileInstanceRelByResource(instances);
					ProfileSwitchRelation r = switchRemoveProfileInstanceRelByResources(instances);
					deleted = r.getBeforeDeletingAllRelation();
				} else {
					// 子
					profileInstRelDAO.removeInstRelByInstIds(instances);
				}
			}else{
				if (logger.isInfoEnabled()) {
					StringBuilder b = new StringBuilder();
					b.append("addProfileInstance start profileId=");
					b.append(profileId);
					b.append(" and instanceIds=").append(instances);
					b.append(" profile not exists!");
					logger.info(b);
				}
				return;
			}
			// 添加
			List<ProfileInstRelationPO> isnertProfileIntances = new ArrayList<>();
			boolean switchMonitorNotice  = false;
			List<ProfileSwitchConvert> coverts = new ArrayList<ProfileSwitchConvert>();
			for (long instanceId : instances) {
				ResourceInstance resourceInstance = resourceInstanceService.getResourceInstance(instanceId);
				if (resourceInstance == null) {
					if (logger.isWarnEnabled()) {
						logger.warn("resourceInstance not found.instanceId=" + instanceId);
					}
					continue;
				}

				// 主资源
				if (resourceInstance.getParentId() <= 0) {
					if (logger.isInfoEnabled()) {
						logger.info("addProfileInstance instanceId:" + instanceId + " is parentInstance!");
					}
					Long lastParentProfileId = deleted.get(resourceInstance.getId());
					//用于临时存放子资源模型绑定的资源，方便通知使用
					HashMap<String,List<Long>> childMonitor  = new HashMap<>();
					//本次策略跟上次策略不一致需要通知--取消监控通知
					if(lastParentProfileId != null){
						if( lastParentProfileId.longValue() != profileId){
							switchMonitorNotice  = true;
						}
					}else{
						lastParentProfileId = 0L;
					}
					
					List<ResourceInstance> childResourceInstances = resourceInstance.getChildren();
					Map<String, List<ResourceInstance>> resourceInstanceRels = new HashMap<String, List<ResourceInstance>>();
					ProfileInstRelationPO mainProfileInstRelation = new ProfileInstRelationPO();
					mainProfileInstRelation.setProfileId(profileId);
					mainProfileInstRelation.setInstanceId(instanceId);
					isnertProfileIntances.add(mainProfileInstRelation);
					//获取有的策略
					ProfileInfoPO pQuery = new ProfileInfoPO();
					if (ProfileTypeEnum.PERSONALIZE.toString().equals(profileInfoPO.getProfileType())) {
						pQuery.setProfileType(ProfileTypeEnum.PERSONALIZE.toString());
					}else if (ProfileTypeEnum.SPECIAL.toString().equals(profileInfoPO.getProfileType())) {
						pQuery.setProfileType(ProfileTypeEnum.SPECIAL.toString());
					}
					pQuery.setParentProfileId(profileId);
					List<ProfileInfoPO> existsProfileIds = profileDAO.getProfileInfoPO(pQuery);
					Map<Long,ProfileInfoPO> existsProfileMaps = new HashMap<Long, ProfileInfoPO>();
					HashMap<String, Long> resourceAndProfile = new HashMap<>();
					if(CollectionUtils.isNotEmpty(existsProfileIds)){
						for (ProfileInfoPO profileInfoPO2 : existsProfileIds) {
							existsProfileMaps.put(profileInfoPO2.getProfileId(), profileInfoPO2);
							resourceAndProfile.put(profileInfoPO2.getResourceId(), profileInfoPO2.getProfileId());
						}
					}
					
					List<ProfileInstRelationPO> lastProfileInstRelationPOs = lastProfileDAO.getLastProfileByParentInstanceId(instanceId);
					Map<Long,InstanceLifeStateEnum> updatNotMonitored = new HashMap<Long, InstanceLifeStateEnum>();
					if (CollectionUtils.isNotEmpty(lastProfileInstRelationPOs)) {
						if (logger.isInfoEnabled()) {
							logger.info("addProfileInstance instanceId:" + instanceId + " existent last profile!");
						}
						for (ProfileInstRelationPO lastProfileInstRelation : lastProfileInstRelationPOs) {
							if (null != lastProfileInstRelation.getParentInstanceId() && !lastProfileInstRelation.getParentInstanceId().equals(0)) {
								for (ResourceInstance childInstance : childResourceInstances) {
									if(childInstance.getLifeState() == InstanceLifeStateEnum.MONITORED){
										//策略切换通知需要使用策略id 绑定的资源
										if (childInstance.getId() == lastProfileInstRelation.getInstanceId()) {
											if(existsProfileMaps.containsKey(lastProfileInstRelation.getProfileId())){
												if (resourceInstanceRels.get(childInstance.getResourceId()) != null) {
													resourceInstanceRels.get(childInstance.getResourceId()).add(childInstance);
												} else {
													List<ResourceInstance> temChildInstances = new ArrayList<>();
													temChildInstances.add(childInstance);
													resourceInstanceRels.put(childInstance.getResourceId(), temChildInstances);
												}
												//构造通知资源的子资源模型跟实例绑定关系
												List<Long> tempChildInstance = childMonitor.get(childInstance.getResourceId());
												if(tempChildInstance == null){
													tempChildInstance = new ArrayList<Long>();
													childMonitor.put(childInstance.getResourceId(), tempChildInstance);
												}
												tempChildInstance.add(childInstance.getId());
											}else{
												//修改资源状态到NOT_MONITORED
												updatNotMonitored.put(childInstance.getId(), InstanceLifeStateEnum.NOT_MONITORED);
											}
										}
									}
								}
							}
						}
					} else {
						if (logger.isInfoEnabled()) {
							logger.info("addProfileInstance instanceId:" + instanceId + " not existent last profile!");
						}
						List<ResourceInstance> childen = filterDown(resourceInstance, childResourceInstances);
						if (CollectionUtils.isNotEmpty(childen)) {
							for (ResourceInstance childInstance : childen) {
								if (resourceAndProfile.containsKey(childInstance.getResourceId())) {
									if (resourceInstanceRels.get(childInstance.getResourceId()) != null) {
										resourceInstanceRels.get(childInstance.getResourceId()).add(childInstance);
									} else {
										List<ResourceInstance> temChildInstances = new ArrayList<>();
										temChildInstances.add(childInstance);
										resourceInstanceRels.put(childInstance.getResourceId(), temChildInstances);
									}
									//构造通知资源的子资源模型跟实例绑定关系
									List<Long> tempChildInstance = childMonitor.get(childInstance.getResourceId());
									if(tempChildInstance == null){
										tempChildInstance = new ArrayList<Long>();
										childMonitor.put(childInstance.getResourceId(), tempChildInstance);
									}
									tempChildInstance.add(childInstance.getId());
								}else {
									//修改资源状态到NOT_MONITORED
									updatNotMonitored.put(childInstance.getId(), InstanceLifeStateEnum.NOT_MONITORED);
								}
							}
						}
					}
					//策略切换通知
					ProfileSwitchConvert convert = new ProfileSwitchConvert();
					convert.setCurrentProfileId(profileId);
					convert.setLastProfileId(lastParentProfileId);
					convert.setParentInstanceId(resourceInstance.getId());
					convert.setChildMonitor(childMonitor);
					coverts.add(convert);
					
					if(!updatNotMonitored.isEmpty()){
						try {
							resourceInstanceService.updateResourceInstanceState(updatNotMonitored);
						} catch (Exception e) {
							if (logger.isErrorEnabled()) {
								logger.error("update resource instance state error:", e);
							}
							throw new ProfilelibException(ServerErrorCodeConstant.ERR_SERVER_OPERATE_DATABASE_ERROR, "updateResourceInstanceState error");
						}
					}
					
					if (!resourceInstanceRels.isEmpty()) {
						for (Map.Entry<String, List<ResourceInstance>> riRel : resourceInstanceRels.entrySet()) {
							ProfileInfoPO queryProfileInfo = new ProfileInfoPO();
							queryProfileInfo.setParentProfileId(profileId);
							queryProfileInfo.setResourceId(riRel.getKey());
							List<ResourceInstance> childInstances = riRel.getValue();
							if (null != childInstances && !childInstances.isEmpty()) {
								List<ProfileInfoPO> childProfileInfos = profileDAO.getProfileInfoPO(queryProfileInfo);
								if (CollectionUtils.isNotEmpty(childProfileInfos)) {
									for (ResourceInstance childInstance : childInstances) {
										ProfileInstRelationPO childProfileInstRelation = new ProfileInstRelationPO();
										childProfileInstRelation.setProfileId(childProfileInfos.get(0).getProfileId());
										childProfileInstRelation.setInstanceId(childInstance.getId());
										childProfileInstRelation.setParentInstanceId(childInstance.getParentId());
										isnertProfileIntances.add(childProfileInstRelation);
									}
								}
							}
						}
					}
				} else {
					if (logger.isInfoEnabled()) {
						logger.info("addProfileInstance instanceId:" + instanceId + " is childenInstance!");
					}
					// 子资源
					ProfileInstRelationPO profileInstRelationPO = new ProfileInstRelationPO();
					profileInstRelationPO.setProfileId(profileId);
					profileInstRelationPO.setInstanceId(instanceId);
					if (profileInfoPO.getParentProfileId() != null && profileInfoPO.getParentProfileId() != 0) {
						ResourceInstance childResourceInstance = resourceInstanceService.getResourceInstance(instanceId);
						if (childResourceInstance == null) {
							if (logger.isWarnEnabled()) {
								logger.warn("resourceInstance not found.instanceId=" + instanceId);
							}
							continue;
						}
						profileInstRelationPO.setParentInstanceId(childResourceInstance.getParentId());
					}
					isnertProfileIntances.add(profileInstRelationPO);
				}
			}
			profileInstRelDAO.insertInstRels(isnertProfileIntances);
			// 更新资源实例状态--已监控
			updateInstanceStates(isnertProfileIntances, deleted, InstanceLifeStateEnum.MONITORED);
			// 同步到采集器数据入库
			addMonitorDataToDB(isnertProfileIntances, deleted);
			//通知
			if(switchMonitorNotice && CollectionUtils.isNotEmpty(coverts)){
				switchProfileNotices(coverts, ProfileTypeEnum.valueOf(profileInfoPO.getProfileType()));
			}
		} catch (Exception e) {
			if (logger.isErrorEnabled()) {
				logger.error("updateProfileInstance error", e);
			}
		}
		if (logger.isInfoEnabled()) {
			StringBuilder b = new StringBuilder();
			b.append("addProfileInstance end instanceIds=").append(instances);
			logger.info(b);
		}
	}

	@Override
	public void cancleMonitor(List<Long> mainInsts) throws ProfilelibException {
		if (logger.isInfoEnabled()) {
			logger.info("batch cancleMonitor start parentInsts=" + mainInsts);
		}
		if(CollectionUtils.isEmpty(mainInsts)){
			logger.info("batch cancleMonitor end  mainInstas is empty!");
			return;
		}
		HashSet<Long> removeIds = new HashSet<>(mainInsts);
		List<ProfileInstRelationPO> profileInstRelationPOs = null;
		try {
			profileInstRelationPOs = profileInstRelDAO.getInstRelationByParentInstIds(mainInsts);
			if (CollectionUtils.isNotEmpty(profileInstRelationPOs)) {
				//获取到所有的策略，需要判断策略是否存在
				Map<Long,Long> instanceAndProfile = new HashMap<>();
				HashSet<Long> profileIds = new HashSet<Long>();
				for (ProfileInstRelationPO profileInstRelationPO : profileInstRelationPOs) {
					long profileId = profileInstRelationPO.getProfileId();
					profileIds.add(profileId);
					instanceAndProfile.put(profileInstRelationPO.getInstanceId(), profileInstRelationPO.getProfileId());
				}
				Map<Long,Long> profieIdExists = new HashMap<Long, Long>();
				for (long profileId : profileIds) {
					ProfileInfo info = getProfileBasicInfoByProfileId(profileId);
					if(info != null){
						profieIdExists.put(profileId,profileId);
					}
				}
				
				for (ProfileInstRelationPO profileInstRelationPO : profileInstRelationPOs) {
					removeIds.add(profileInstRelationPO.getInstanceId());
				}
				
				Iterator<ProfileInstRelationPO> iterator = profileInstRelationPOs.iterator();
				while(iterator.hasNext()){
					ProfileInstRelationPO po = iterator.next();
					if(!profieIdExists.containsKey(po.getProfileId())){
						iterator.remove();
					}
				}
				if (!removeIds.isEmpty()) {
					List<Long> removeResult = new ArrayList<>(removeIds);
					// 移除所有父资源，子资源监控
					profileInstRelDAO.removeInstRelByInstIds(removeResult);
					// 称除上一次策略使用记录
					lastProfileDAO.removeLastProfilesByParentIds(mainInsts);
					// 将最新取消监控的记录插入到上一次策略使用记录表中
					lastProfileDAO.insertLastProfiles(profileInstRelationPOs);
					// 更新策略资源状态为未监控
					updateInstanceStates(profileInstRelationPOs, null, InstanceLifeStateEnum.NOT_MONITORED);
					// 同步到采集器
					cancelMonitorToCollector(removeResult);
					Map<Long,List<Long>> noticeData = new HashMap<Long, List<Long>>();
					//取消监控通知
					for (Long tempInstanceId : removeResult) {
						Long profileId = instanceAndProfile.get(tempInstanceId);
						List<Long> intanceIds = noticeData.get(profileId);
						if(intanceIds == null){
							intanceIds = new ArrayList<Long>();
							noticeData.put(profileId,intanceIds);
						}
						intanceIds.add(tempInstanceId);
					}
					cancelMonitorNotice(noticeData);
				}
			}else{
				try {
					Map<Long, InstanceLifeStateEnum> instStateEnumMap = new HashMap<Long, InstanceLifeStateEnum>();
					for (long instanceId : mainInsts) {
						instStateEnumMap.put(instanceId,InstanceLifeStateEnum.NOT_MONITORED);
						//为了处理资源跟策略不同步。直接取消设备监控
						List<ResourceInstance> listChildren = resourceInstanceService.getChildInstanceByParentId(instanceId);
						if(CollectionUtils.isNotEmpty(listChildren)){
							for (ResourceInstance resourceInstance : listChildren) {
								instStateEnumMap.put(resourceInstance.getId(), InstanceLifeStateEnum.NOT_MONITORED);
							}
						}
					}
					resourceInstanceService.updateResourceInstanceState(instStateEnumMap);
				} catch (Exception e) {
					if (logger.isErrorEnabled()) {
						logger.error("update resource instance state error:", e);
					}
				}
				if(logger.isErrorEnabled()){
					logger.error("cancleMonitor(list) parentInstanceId=" + mainInsts + " profile not match!");
				}
			}
		} catch (Exception e) {
			if (logger.isErrorEnabled()) {
				logger.error("cancleMonitor error!", e);
			}
			throw new ProfilelibException(22, "");
		}
		if (logger.isInfoEnabled()) {
			logger.info("batch cancleMonitor end parentInsts=" + mainInsts);
		}
	}

	@Override
	public void cancleMonitor(long parentInstanceId) throws ProfilelibException {
		if (logger.isInfoEnabled()) {
			logger.info("cancleMonitor(long) start parentInstanceId=" + parentInstanceId);
		}
		long parentProfileId = 0;
		List<Long> removeIds = new ArrayList<>(100);
		List<ProfileInstRelationPO> profileInstRelationPOs = null;
		try {
			profileInstRelationPOs = profileInstRelDAO.getInstRelationByParentInstId(parentInstanceId);
			if(CollectionUtils.isNotEmpty(profileInstRelationPOs)){
				HashSet<Long> profileIds = new HashSet<Long>();
				for (ProfileInstRelationPO profileInstRelationPO : profileInstRelationPOs) {
					long profileId = profileInstRelationPO.getProfileId();
					profileIds.add(profileId);
					removeIds.add(profileInstRelationPO.getInstanceId());
					if(parentInstanceId == profileInstRelationPO.getInstanceId()){
						parentProfileId = profileInstRelationPO.getProfileId();
					}
				}
				Map<Long,Long> profieIdExists = new HashMap<Long, Long>();
				for (long profileId : profileIds) {
					ProfileInfo info = getProfileBasicInfoByProfileId(profileId);
					if(info != null){
						profieIdExists.put(profileId,profileId);
					}
				}
				Iterator<ProfileInstRelationPO> iterator = profileInstRelationPOs.iterator();
				while(iterator.hasNext()){
					ProfileInstRelationPO po = iterator.next();
					if(!profieIdExists.containsKey(po.getProfileId())){
						iterator.remove();
					}
				}
				
				if (!removeIds.isEmpty()) {
					// 移除所有父资源，子资源监控
					profileInstRelDAO.removeInstRelByInstIds(removeIds);
					lastProfileDAO.removeLastProfilesByParentIds(Arrays.asList(parentInstanceId));
					lastProfileDAO.insertLastProfiles(profileInstRelationPOs);
					updateInstanceStates(profileInstRelationPOs, null, InstanceLifeStateEnum.NOT_MONITORED);
					// 同步到采集器
					cancelMonitorToCollector(removeIds);
				}
			}else{
				//为了处理资源跟策略不同步。直接取消设备监控
				List<ResourceInstance> listChildren = resourceInstanceService.getChildInstanceByParentId(parentInstanceId);
				Map<Long, InstanceLifeStateEnum> instStateEnumMap = new HashMap<Long, InstanceLifeStateEnum>();
				if(CollectionUtils.isNotEmpty(listChildren)){
					for (ResourceInstance resourceInstance : listChildren) {
						instStateEnumMap.put(resourceInstance.getId(), InstanceLifeStateEnum.NOT_MONITORED);
					}
					try {
						resourceInstanceService.updateResourceInstanceState(instStateEnumMap);
					} catch (Exception e) {
						if (logger.isErrorEnabled()) {
							logger.error("update resource instance state error:", e);
						}
					}
					if(logger.isInfoEnabled()){
						logger.info("cancleMonitor(long) parentInstanceId=" + parentInstanceId + " profile not match!");
					}
				}
			}
			//取消资源监控通知
			Map<Long,List<Long>> noticeData = new HashMap<Long, List<Long>>(1);
			List<Long> canelIds = new ArrayList<>(1);
			canelIds.add(parentInstanceId);
			noticeData.put(parentProfileId,canelIds);
			cancelMonitorNotice(noticeData);
		} catch (Exception e1) {
			if (logger.isErrorEnabled()) {
				logger.error("cancleMonitor error", e1);
			}
			throw new ProfilelibException(ServerErrorCodeConstant.ERR_SERVER_OPERATE_DATABASE_ERROR, "cancleMonitor error");
		}
		if (logger.isInfoEnabled()) {
			logger.info("cancleMonitor(long) end parentInstanceId=" + parentInstanceId);
		}
	}

	@Override
	public Profile copyProfile(long copyProfileId, ProfileInfo profileInfo) throws ProfilelibException {
		if (logger.isTraceEnabled()) {
			logger.trace("copyProfile start copyProfileId=" + copyProfileId);
		}
		Profile result = null;
		ProfileInfoPO mainProfileInfoPO = null;
		try {
			mainProfileInfoPO = profileDAO.getProfilePoById(copyProfileId);
		} catch (Exception e1) {
			if (logger.isErrorEnabled()) {
				logger.error("getProfilePoById error", e1);
			}
		}
		if (mainProfileInfoPO == null) {
			throw new ProfilelibException(0, "profile to copyed not exist.");
		}
		
		long copyMainProfileId = this.ocProfilelibMainSequence.next();
		mainProfileInfoPO.setProfileId(copyMainProfileId);
		mainProfileInfoPO.setProfileName(profileInfo.getProfileName());
		mainProfileInfoPO.setProfileDesc(profileInfo.getProfileDesc());
		mainProfileInfoPO.setDomainId(profileInfo.getDomainId());
		mainProfileInfoPO.setCreateUser(profileInfo.getCreateUser());
	
		//通过父策略ＩＤ查询所有子策略
		ProfileInfoPO infoPO = new ProfileInfoPO();
		infoPO.setParentProfileId(copyProfileId);
		try {
			List<ProfileInfoPO> childrenPos = profileDAO.getProfileInfoPO(infoPO);
			Map<Long, ProfileInfoPO> profileIdInfos = new HashMap<>();
			profileIdInfos.put(copyProfileId, mainProfileInfoPO);
			for (ProfileInfoPO childProfilePO : childrenPos) {
				long copyProfileChildId = childProfilePO.getProfileId();
				childProfilePO.setProfileId(ocProfilelibMainSequence.next());
				childProfilePO.setParentProfileId(copyMainProfileId);
				childProfilePO.setDomainId(profileInfo.getDomainId());
				childProfilePO.setProfileType(ProfileTypeEnum.SPECIAL.toString());
				profileIdInfos.put(copyProfileChildId, childProfilePO);
			}
			copyProfileByPO(profileIdInfos);
		} catch (Exception e) {
			if (logger.isErrorEnabled()) {
				logger.error("copyProfile", e);
			}
			throw new ProfilelibException(e);
		}
		result = getProfilesById(mainProfileInfoPO.getProfileId());
		if (logger.isTraceEnabled()) {
			logger.trace("copyProfile end");
		}
		return result;
	}

	@Override
	public long createChildProfile(long parentProfileId, ProfileInfo childProfileInfo) throws ProfilelibException {
		if (logger.isTraceEnabled()) {
			logger.trace("createChildProfile start");
		}
		if (childProfileInfo.getProfileType().equals(ProfileTypeEnum.DEFAULT)) {
			throw new ProfilelibException(111, "Can't add default child profile!");
		}
		long newProfileId = innerCreateChildProfile(parentProfileId, childProfileInfo);
		if (logger.isTraceEnabled()) {
			logger.trace("createChildProfile end");
		}
		return newProfileId;
	}

	/**
	 * @Title: createPersonalizeProfile
	 * @Description: 创建个性化策略
	 * @param proflie
	 * @return
	 * @throws ProfilelibException
	 *             long
	 * @throws
	 */
	@Override
	public long createPersonalizeProfile(Profile proflie) throws ProfilelibException {
		if (logger.isTraceEnabled()) {
			StringBuilder b = new StringBuilder();
			b.append("createPersonalizeProfile start profileName=").append(proflie.getProfileInfo().getProfileName());
			logger.trace(b);
		}
		ProfileInstanceRelation profileInstanceRelation = proflie.getProfileInstanceRelations();
		if (profileInstanceRelation == null || profileInstanceRelation.getInstances() == null) {
			throw new ProfilelibException(11, "addMonitorUsePersonalize profileinstance bind is null or empty");
		}
		// 删除当前资源及子资源在默认策略中关系
		long parentInstanceId = 0;
		try {
			parentInstanceId = profileInstanceRelation.getInstances().get(0).getInstanceId();
		} catch (Exception e) {
			if (logger.isErrorEnabled()) {
				logger.error("", e);
			}
			throw new ProfilelibException(13, "");
		}
		proflie.getProfileInfo().setUpdateTime(null);
		// 移除上一次策略关系
		HashMap<Long, Long> deleted = removeProfileInstanceRelByResource(parentInstanceId);
		List<ProfileInfoPO> profileInfoPOs = new ArrayList<ProfileInfoPO>();
		List<ProfileMetricPO> profileMetricPOs = new ArrayList<ProfileMetricPO>();
		List<ProfileThresholdPO> profileTholdPOs = new ArrayList<ProfileThresholdPO>();
		List<ProfileInstRelationPO> profileInstRelationPOs = new ArrayList<ProfileInstRelationPO>();
		List<ProfileMetric> addToCacheProfileMetric = new ArrayList<ProfileMetric>(50);
		Map<String, List<ProfileThreshold>> addToCacheProfileThreshold = new HashMap<String, List<ProfileThreshold>>(50);
		long newProfileId = 0;
		try {
			// 转换策略实体
			newProfileId = convertPersonalizeProfileToPO(0, parentInstanceId, proflie, profileInfoPOs, profileMetricPOs, profileTholdPOs, profileInstRelationPOs, addToCacheProfileMetric, addToCacheProfileThreshold);

			// 策略、指标、阈值入库
			insertProfileInfos(profileInfoPOs, profileMetricPOs, profileTholdPOs);

			// 插入策略实例信息
			profileInstRelDAO.insertInstRels(profileInstRelationPOs);
		} catch (Exception e) {
			if (logger.isErrorEnabled()) {
				logger.error("addMonitorUsePersonalize error!", e);
			}
			throw new ProfilelibException(ProfilelibException.CODE_ERROR_VALIDATION + 3, "写入默认策略失败");
		}
		updateInstanceStates(profileInstRelationPOs, deleted, InstanceLifeStateEnum.MONITORED);
		/*
		 * 同步到采集器数据入库
		 */
		addMonitorDataToDB(profileInstRelationPOs, deleted);

		// 同步到缓存
		addToCache(addToCacheProfileMetric, addToCacheProfileThreshold);
		if (logger.isTraceEnabled()) {
			logger.trace("createPersonalizeProfile end");
		}
		return newProfileId;
	}

	/**
	 * @Title: insertProfileInfos
	 * @Description: 同步保存策略、指标、阈值信息
	 * @param profileInfos
	 * @param profileMetrics
	 * @param profileThresholds
	 * @throws Exception
	 *             void
	 * @throws
	 */
	private void insertProfileInfos(List<ProfileInfoPO> profileInfos, List<ProfileMetricPO> profileMetrics, List<ProfileThresholdPO> profileThresholds) throws Exception {
		if (null != profileInfos && !profileInfos.isEmpty()) {
			// 插入策略基本信息
			profileDAO.insertProfiles(profileInfos);
			if (profileMetrics != null && !profileMetrics.isEmpty()) {
				// 插入策略指标信息
				profileMetricDAO.insertMetrics(profileMetrics);
				if (null != profileThresholds && !profileThresholds.isEmpty()) {
					// 插入策略阈值信息
					profileThresholdDAO.insertThresholds(profileThresholds);
				}
			}
		}
	}

	@Override
	public long createSpecialProfile(ProfileInfo parentProfileInfo, List<ProfileInfo> childProfileInfo) throws ProfilelibException {
		if (logger.isTraceEnabled()) {
			StringBuilder b = new StringBuilder(100);
			b.append("createSpecialProfile start name=").append(parentProfileInfo.getProfileName());
			logger.trace(b);
		}
		long profileId = createProfile(parentProfileInfo, childProfileInfo);
		if (logger.isTraceEnabled()) {
			StringBuilder b = new StringBuilder(100);
			b.append("createSpecialProfile end name=").append(parentProfileInfo.getProfileName());
			logger.trace(b);
		}
		return profileId;
	}

	/**
	 * 创建指定的策略信息
	 * 
	 * @param parentInfo
	 * @param childrenInfos
	 * @return Map<String, Long>
	 * @throws ProfilelibException
	 */
	private long createProfile(ProfileInfo parentInfo, List<ProfileInfo> childrenInfos) throws ProfilelibException {
		String mainResourceId = parentInfo.getResourceId();
		long newMainProfileId = ocProfilelibMainSequence.next();
		parentInfo.setProfileId(newMainProfileId);
		ResourceDef mainResource = capacityService.getResourceDefById(mainResourceId);
		/**
		 * 拿到主表设置信息
		 */
		// key: resourceId
		Map<String, ProfileInfoPO> profilePosMap = getInfoPOs(newMainProfileId, parentInfo, childrenInfos);
		/**
		 * 组装策略里的指标设置信息和指标阈值信息
		 */
		List<ProfileMetricPO> profileMetricPOs = new ArrayList<>();
		List<ProfileThresholdPO> profileThresholdPOs = new ArrayList<>();

		Map<String, List<ProfileThreshold>> addToCacheProfileThreshold = new HashMap<String, List<ProfileThreshold>>(50);
		profileMetricPOs.addAll(getResourceMetricPOs(newMainProfileId, mainResource));
		profileThresholdPOs.addAll(getThresholdPOs(newMainProfileId, mainResource, addToCacheProfileThreshold));
		List<ProfileMetric> addToCacheProfileMetric = tranProfileMetricPO2BOs(profileMetricPOs);
		for (ProfileInfo childProfileInfo : childrenInfos) {
			ResourceDef childResource = capacityService.getResourceDefById(childProfileInfo.getResourceId());
			if (childResource == null) {
				if (logger.isWarnEnabled()) {
					logger.warn("ResourceDef is null,resournceId=" + childProfileInfo.getResourceId());
				}
				throw new ProfilelibException(234, "ResourceDef is null,resournceId=" + childProfileInfo.getResourceId());
			}
			long subProfileId = profilePosMap.get(childProfileInfo.getResourceId()).getProfileId();
			List<ProfileMetricPO> childMetricPOS = getResourceMetricPOs(subProfileId, childResource);
			if (!childMetricPOS.isEmpty()) {
				profileMetricPOs.addAll(childMetricPOS);
				profileThresholdPOs.addAll(getThresholdPOs(subProfileId, childResource, addToCacheProfileThreshold));
			}
		}
		try {
			// 插入缺省策略基本信息\指标信息\阈值信息
			insertProfileInfos(new ArrayList<>(profilePosMap.values()), profileMetricPOs, profileThresholdPOs);
		} catch (Exception e) {
			if (logger.isErrorEnabled()) {
				logger.error("", e);
			}
			throw new ProfilelibException(ProfilelibException.CODE_ERROR_VALIDATION + 3, "写入默认策略失败");
		}
		// add to cache
		addToCache(addToCacheProfileMetric, addToCacheProfileThreshold);
		return newMainProfileId;
	}

	/**
	 * 构造主策略和子策略的表信息
	 * 
	 * @param parentInfo
	 * @param childrenInfos
	 * @return
	 */
	private Map<String, ProfileInfoPO> getInfoPOs(long profileId, ProfileInfo parentInfo, List<ProfileInfo> childrenInfos) {
		Map<String, ProfileInfoPO> infoPOs = new HashMap<>(childrenInfos.size() + 1);
		ProfileInfoPO mainInfoPO = getInfoPO(profileId, parentInfo, -1);
		infoPOs.put(mainInfoPO.getResourceId(), mainInfoPO);
		for (ProfileInfo info : childrenInfos) {
			long childProfileId = ocProfilelibMainSequence.next();
			infoPOs.put(info.getResourceId(), getInfoPO(childProfileId, info, parentInfo.getProfileId()));
		}
		return infoPOs;
	}

	/**
	 * @Title: enableMonitor
	 * @Description: 启用资源监控。使用上一次保存策略
	 * @param parentInstanceIds
	 * @throws ProfilelibException
	 *             void
	 * @throws
	 */
	@Override
	public void enableMonitor(List<Long> parentInstanceIds) throws ProfilelibException {
		if (logger.isInfoEnabled()) {
			logger.info("batch enableMonitor list start parentInstanceIds=" + parentInstanceIds);
		}
		if (CollectionUtils.isEmpty(parentInstanceIds)) {
			if (logger.isErrorEnabled()) {
				logger.error("batch enableMonitor start error,parentInstanceIds is null!");
			}
			return;
		}
		HashSet<Long> parentInstanceIdSet = new HashSet<Long>(parentInstanceIds);
		if (logger.isInfoEnabled()) {
			logger.info("Remove duplicate data,parentInstanceIds=" + parentInstanceIdSet);
		}
		/**
		 * 通过资源实例ID获取资源实例，过虑空资源入已监控的资源
		 */
		List<ResourceInstance> allResourceInstances = new ArrayList<ResourceInstance>(parentInstanceIdSet.size());
		try {
			for (Long instanceId : parentInstanceIdSet) {
				ResourceInstance resourceIntance = resourceInstanceService.getResourceInstance(instanceId);
				if (null != resourceIntance) {
					if (resourceIntance.getLifeState() == InstanceLifeStateEnum.MONITORED) {
						if (logger.isInfoEnabled()) {
							logger.info("batch enableMonitor parentInstanceId=" + instanceId + " has monitor.");
						}
					} else {
						allResourceInstances.add(resourceIntance);
					}
				} else {
					if (logger.isWarnEnabled()) {
						logger.warn("enableMonitor resourceinstance not found,instanceId=" + instanceId);
					}
				}
			}
		} catch (InstancelibException e) {
			if (logger.isErrorEnabled()) {
				logger.error("enableMonitor to load ResourceInstance error", e);
			}
			throw new ProfilelibException(11, "enableMonitor to load ResourceInstance error");
		}
		
		if (allResourceInstances.isEmpty()) {
			if (logger.isInfoEnabled()) {
				logger.info("batch enableMonitor end parentInstanceIds=" + parentInstanceIds);
			}
			return;
			//throw new ProfilelibException(11, "enableMonitor to load ResourceInstance error");
		}
		
		//存放资源对应的策略
		Map<ResourceInstance, List<ProfileInfoPO>> instanceDefaultProfileInfos = new HashMap<ResourceInstance, List<ProfileInfoPO>>(allResourceInstances.size());
		List<Long> mainInstanceIds = new ArrayList<Long>(allResourceInstances.size());
		/**
		 * 检查资源是否使用过策略，如果使用过策略将上次使用的策略拿来加入监控
		 * */
		HashMap<Long, Long> deleted = new HashMap<Long, Long>();
		{
			//HashSet<String> useProfileResourceIds = new HashSet<String>();
			List<ProfileInstRelationPO> lastProfileInstancesRel = new ArrayList<ProfileInstRelationPO>();
			List<Long> lastParentInstanceIds = new ArrayList<Long>();
			HashSet<Long> deleteLastProfile = new HashSet<Long>();
			//存循环后的子实例id,用于过滤
			List<Long> childInstanceIds = new ArrayList<Long>();
			for (ResourceInstance mainInstance : allResourceInstances) {
				// 检查默认策略是否被使用，未使用的修改状态为使用中
				List<ProfileInfoPO> infos = getDefaultProfileByResource(mainInstance.getResourceId());
//				if (infos != null && !infos.isEmpty()) {
//					if (infos.get(0).getIsUse().equals("0")) {
//						useProfileResourceIds.add(mainInstance.getResourceId());
//					}
//				}
				// 查找资源实例上一次存放的策略关系
				try {
					//用于过滤,如果通过父实例id获取所有的上一次资源策略绑定关系(如果mainInstance是子资源,须过滤,避免冲突)
					if (!childInstanceIds.contains(mainInstance.getId())) {
						List<ProfileInstRelationPO> lastProfileInstRelationPOs = lastProfileDAO.getLastProfileByParentInstanceId(mainInstance.getId());
						if (CollectionUtils.isNotEmpty(lastProfileInstRelationPOs)) {
							boolean isExistParnetId = false;
							for (ProfileInstRelationPO profileInstRelationPO : lastProfileInstRelationPOs) {
								Long parentInstanceId = profileInstRelationPO.getParentInstanceId();
								if (parentInstanceId != null && parentInstanceId != 0) {
									childInstanceIds.add(profileInstRelationPO.getInstanceId());
								}
								if(!isExistParnetId && profileInstRelationPO.getInstanceId() == mainInstance.getId()){
									isExistParnetId = true;
								}
							}
							if(isExistParnetId){
								lastProfileInstancesRel.addAll(lastProfileInstRelationPOs);
								lastParentInstanceIds.add(mainInstance.getId());
								if (logger.isInfoEnabled()) {
									logger.info("parentInstanceId: " + mainInstance.getId() + " use last Profile!");
								}
							}else{
								//使用默认策略
								mainInstanceIds.add(mainInstance.getId());
								instanceDefaultProfileInfos.put(mainInstance, infos);
								//删除没有主资源上一次策略。子资源全部删除上一次保存的策略。
								deleteLastProfile.add(mainInstance.getId());
							}
						} else {
							mainInstanceIds.add(mainInstance.getId());
							instanceDefaultProfileInfos.put(mainInstance, infos);
						}
					}
				} catch (Exception e) {
					if (logger.isErrorEnabled()) {
						logger.error("load ResourceInstance last profile error:" + e);
					}
				}
			}
			//删除上一次无用的策略绑定关系
			if(CollectionUtils.isNotEmpty(deleteLastProfile)){
				lastProfileDAO.removeLastProfilesByParentIds(new ArrayList<>(deleteLastProfile));
			}
			// 更新被使用的策略状态为使用中
//			if (CollectionUtils.isNotEmpty(useProfileResourceIds)) {
//				updateDefaultProfileStateByResourceId(new ArrayList<String>(useProfileResourceIds), true);
//			}
			if (CollectionUtils.isNotEmpty(lastProfileInstancesRel)) {
				deleted = removeProfileInstanceRelByResource(lastParentInstanceIds);
				// 直接添加到该策略中
				try {
					profileInstRelDAO.insertInstRels(lastProfileInstancesRel);
					updateInstanceStates(lastProfileInstancesRel, deleted, InstanceLifeStateEnum.MONITORED);
					// 同步到采集器
					addMonitorDataToDB(lastProfileInstancesRel, deleted);
				} catch (Exception e) {
					if (logger.isErrorEnabled()) {
						logger.error("enableProfile in insert last profile instance relations error:" + e);
					}
					throw new ProfilelibException(e);
				}
			}
		}

		if (null == mainInstanceIds || mainInstanceIds.isEmpty()) {
			if (logger.isTraceEnabled()) {
				logger.trace("batch enableMonitor end parentInstanceIds=" + parentInstanceIds);
			}
			return;
		}

		deleted = removeProfileInstanceRelByResource(mainInstanceIds);
		// 使用默认策略的主资源实例ID
		List<Long> useDefaultInstanceIds = new ArrayList<Long>();
		// 策略与资源实例关系
		List<ProfileInstRelationPO> profileInstRelationPOs = new ArrayList<>();
		Map<Long, InstanceLifeStateEnum> instStateEnumMap = new HashMap<Long, InstanceLifeStateEnum>();
		for (Map.Entry<ResourceInstance, List<ProfileInfoPO>> instanceProfileInfo : instanceDefaultProfileInfos.entrySet()) {
			ResourceInstance resourceInstance = instanceProfileInfo.getKey();
			List<ProfileInfoPO> infos = instanceProfileInfo.getValue();
			if (CollectionUtils.isEmpty(infos)) {
				//如果资源是子资源实例,没有子策略 ,创建子策略,并监控
				if (resourceInstance.getParentId() != 0) {
					List<ResourceInstance> childrens = new ArrayList<ResourceInstance>();
					childrens.add(resourceInstance);
					try {
						long parentId = resourceInstance.getParentId();
						ResourceInstance parentInstance = resourceInstanceService.getResourceInstance(parentId);
						if (parentInstance != null) {
							List<ProfileInfoPO> profileInfoPOs = getDefaultProfileByResource(parentInstance.getResourceId());
							//如果没有主策略,创建默认主策略及子策略
							if (CollectionUtils.isNotEmpty(profileInfoPOs)) {
								long parentProfileId = profileInfoPOs.get(0).getProfileId();
								List<ProfileInstRelationPO> pirpos = createChildProfile(parentInstance, childrens, instStateEnumMap, parentProfileId);
								profileInstRelationPOs.addAll(pirpos);
							}else {
								useDefaultInstanceIds.add(parentId);
								continue;
							}
						}
					} catch (Exception e) {
						if (logger.isErrorEnabled()) {
							logger.error("create childProfile error",e);
						}
					}
				}else {
					useDefaultInstanceIds.add(resourceInstance.getId());
					if (logger.isInfoEnabled()) {
						logger.info("parentInstanceId: " + resourceInstance.getId() + " use default Profile!");
					}
					continue;
				}
			} else {
				if (logger.isInfoEnabled()) {
					logger.info("parentInstanceId: " + resourceInstance.getId() + " use existent Profile!");
				}
				long profileId = infos.get(0).getProfileId();
				long parentInstanceId = resourceInstance.getId();
				instStateEnumMap.put(parentInstanceId, InstanceLifeStateEnum.MONITORED);
				ProfileInstRelationPO mainProfileInstRelationPO = new ProfileInstRelationPO();
				mainProfileInstRelationPO.setProfileId(profileId);
				mainProfileInstRelationPO.setInstanceId(parentInstanceId);
				profileInstRelationPOs.add(mainProfileInstRelationPO);
				
				List<ResourceInstance> childrens = resourceInstance.getChildren();
				if (CollectionUtils.isNotEmpty(childrens)) {
					List<ProfileInstRelationPO> createChildProfile = createChildProfile(resourceInstance, childrens, instStateEnumMap, profileId);
					profileInstRelationPOs.addAll(createChildProfile);
				}else{
					if(logger.isInfoEnabled()){
						logger.info("enableMonitor parentInstance["+parentInstanceId+"] childrens is empty!");
					}
				}
			}
		}
		try {
			// 添加父资源，子资源监控资源
			profileInstRelDAO.insertInstRels(profileInstRelationPOs);
			updateInstanceStates(profileInstRelationPOs, deleted, InstanceLifeStateEnum.MONITORED);
			addMonitorUseDefault(useDefaultInstanceIds);
			// 同步到采集器
			addMonitorDataToDB(profileInstRelationPOs, deleted);
		} catch (Exception e) {
			if (logger.isErrorEnabled()) {
				logger.error("enableProfile in insert profile instance relations error:" + e);
			}
			throw new ProfilelibException(e);
		}
		if (logger.isInfoEnabled()) {
			logger.info("enableMonitor end parentInstanceIds:" + parentInstanceIds);
		}
	}
	
	private List<ProfileInstRelationPO> createChildProfile(ResourceInstance parentInstance,List<ResourceInstance> childrens,Map<Long, InstanceLifeStateEnum> instStateEnumMap,long parentProfileId){
		List<ProfileInstRelationPO> profileInstRelationPOs = new ArrayList<ProfileInstRelationPO>();
		if (CollectionUtils.isNotEmpty(childrens)) {
			childrens = filterDown(parentInstance, childrens);
			//为了容错处理，添加过滤子资源局重复
			HashSet<Long> tempChildSet = new HashSet<Long>();
			if (CollectionUtils.isNotEmpty(childrens)) {
				for (ResourceInstance subInstance : childrens) {
					long subInstanceId = subInstance.getId();
					if(tempChildSet.contains(subInstanceId)){
						continue;
					}
					tempChildSet.add(subInstanceId);
					try {
						List<ProfileInfoPO> childInfos = getDefaultProfileByResource(subInstance.getResourceId());
						long childProfileId = 0;
						instStateEnumMap.put(subInstanceId, InstanceLifeStateEnum.MONITORED);
						if (null != childInfos && !childInfos.isEmpty()) {
							childProfileId = childInfos.get(0).getProfileId();
						} else {
							ProfileInfo childProfileInfo = new ProfileInfo();
							childProfileInfo.setProfileId(ocProfilelibMainSequence.next());
							childProfileInfo.setResourceId(subInstance.getResourceId());
							childProfileInfo.setUse(true);
							childProfileInfo.setProfileType(ProfileTypeEnum.DEFAULT);
							childProfileInfo.setParentProfileId(parentProfileId);
							childProfileInfo.setProfileName("默认策略" + subInstance.getResourceId());
							childProfileInfo.setUpdateTime(new Date());
							// 需要创建子策略
						    childProfileId = innerCreateChildProfile(parentProfileId, childProfileInfo);
						}
						ProfileInstRelationPO childProfileInstRelationPO = new ProfileInstRelationPO();
						childProfileInstRelationPO.setProfileId(childProfileId);
						childProfileInstRelationPO.setInstanceId(subInstanceId);
						childProfileInstRelationPO.setParentInstanceId(parentInstance.getId());
						profileInstRelationPOs.add(childProfileInstRelationPO);
					} catch (Exception e) {
						if (logger.isErrorEnabled()) {
							logger.error("createChildProfile is error", e);
						}	
					}
				}
			}
		}
		return profileInstRelationPOs;
	}
	
	
	@Override
	public void autoRefreshChildEnableMonitor(long parentId,List<Long> childInstanceIds)
			throws ProfilelibException {
		childMointor(parentId,childInstanceIds,true);
	}
	
	private void childMointor(long parentId,List<Long> childInstanceIds,boolean isFilterDown) throws ProfilelibException {
		if (CollectionUtils.isEmpty(childInstanceIds)) {
			if (logger.isInfoEnabled()) {
				logger.info("batch enableMonitor start error,childInstanceIds is null!");
			}
			return;
		}
		if (logger.isInfoEnabled()) {
			logger.info("batch autoEnableMonitor start childInstanceIds=" + childInstanceIds);
		}
		HashSet<Long> childInstanceIdSet = new HashSet<Long>(childInstanceIds);
		//如果父资源没有监控，自动刷新的子资源不监控
		ProfileInfo info =  getBasicInfoByResourceInstanceId(parentId);
		if(info == null){
			if(logger.isErrorEnabled()){
				StringBuilder b = new StringBuilder(100);
				b.append("batch autoEnableMonitor parentId=").append(b).append(" not monitor!");
				b.append("childIds=").append(childInstanceIdSet).append(" can not monitor!");
				logger.error(b.toString());
			}
			return;
		}
		long parentProfileId = info.getProfileId();
		/*
		 * 过滤down的接口以及服务，不可用不加入到监控
		 */
		ResourceInstance resourceIntance = null;
		ResourceInstance parentInstance = null;
		List<ResourceInstance> subInstaces = new ArrayList<ResourceInstance>(childInstanceIdSet.size());
		try {
			parentInstance = resourceInstanceService.getResourceInstance(parentId);
			if(parentInstance == null){
				if(logger.isErrorEnabled()){
					StringBuilder b = new StringBuilder(100);
					b.append("batch autoEnableMonitor parentId=").append(b).append(" has  deleted!");
					b.append("childIds=").append(childInstanceIdSet).append(" can not monitor!");
					logger.error(b.toString());
				}
				return;
			}
			for (long instanceId : childInstanceIdSet) {
				resourceIntance = resourceInstanceService.getResourceInstance(instanceId);
				if(resourceIntance != null){
					subInstaces.add(resourceIntance);
				}
			}
		} catch (InstancelibException e) {
			if (logger.isErrorEnabled()) {
				logger.error("autoRefreshEnableMonitor to load ResourceInstance error", e);
			}
			return;
		}
		List<ResourceInstance> children = null;
		//过滤子接口
		if(isFilterDown){
			children = filterDown(parentInstance, subInstaces);
		}else{
			children = subInstaces;
		}
		Map<String,ProfileInfoPO> resourceIds = new HashMap<String, ProfileInfoPO>(10);
		//获取当前父资源的所有的子策略。
		try {
			ProfileInfoPO po = new ProfileInfoPO();
			po.setParentProfileId(info.getProfileId());
			List<ProfileInfoPO> allChildrenProfileInfo = profileDAO.getProfileInfoPO(po);
			if(CollectionUtils.isNotEmpty(allChildrenProfileInfo)){
				for (ProfileInfoPO profileInfoPO : allChildrenProfileInfo) {
					resourceIds.put(profileInfoPO.getResourceId(), profileInfoPO);
				}
			}
		} catch (Exception e) {
			if (logger.isErrorEnabled()) {
				logger.error("autoRefreshChildEnableMonitor getProfileInfoPO() error", e);
			}
			return;
		}
		Map<Long, InstanceLifeStateEnum> instStateEnumMap = new HashMap<Long, InstanceLifeStateEnum>();
		// 策略与资源实例关系
		List<ProfileInstRelationPO> profileInstRelationPOs = new ArrayList<>();
		for (ResourceInstance subInstance : children) {
			long subInstanceId = subInstance.getId();
			long subProfileId = 0;
			ProfileInfoPO subInfo = resourceIds.get(subInstance.getResourceId());
			if(subInfo == null){
 				//创建子策略
				ProfileInfo childProfileInfo = new ProfileInfo();
				childProfileInfo.setProfileId(ocProfilelibMainSequence.next());
				childProfileInfo.setResourceId(subInstance.getResourceId());
				childProfileInfo.setUse(true);
				childProfileInfo.setProfileType(info.getProfileType());
				childProfileInfo.setParentProfileId(parentProfileId);
				switch(info.getProfileType()){
					case DEFAULT:
						childProfileInfo.setProfileName("默认策略" + subInstance.getResourceId());
						break;
					case PERSONALIZE:
						childProfileInfo.setProfileName("个性化策略" + subInstance.getResourceId());
						break;
					default:
						childProfileInfo.setProfileName("自定义策略" + subInstance.getResourceId());
						break;
				}
				childProfileInfo.setUpdateTime(new Date());
				// 需要创建子策略
				subProfileId = innerCreateChildProfile(parentProfileId, childProfileInfo);
				subInfo = transProfileInfo(subProfileId, childProfileInfo);
				//只创建一次
				resourceIds.put(subInstance.getResourceId(), subInfo);
			}else{
				subProfileId = subInfo.getProfileId();
			}
			instStateEnumMap.put(subInstanceId, InstanceLifeStateEnum.MONITORED);
			ProfileInstRelationPO childProfileInstRelationPO = new ProfileInstRelationPO();
			childProfileInstRelationPO.setProfileId(subProfileId);
			childProfileInstRelationPO.setInstanceId(subInstanceId);
			childProfileInstRelationPO.setParentInstanceId(parentId);
			profileInstRelationPOs.add(childProfileInstRelationPO);
		}
		try {
			profileInstRelDAO.insertInstRels(profileInstRelationPOs);
			updateInstanceStates(profileInstRelationPOs, null, InstanceLifeStateEnum.MONITORED);
			// 同步到采集器
			addMonitorDataToDB(profileInstRelationPOs, null);
		} catch (Exception e) {
			if (logger.isErrorEnabled()) {
				logger.error("enableProfile in insert last profile instance relations error:" + e);
			}
			throw new ProfilelibException(e);
		}
		if (logger.isInfoEnabled()) {
			logger.info("batch autoEnableMonitor end childInstanceIds=" + childInstanceIdSet);
		}
	}
	
	/**
	 * @Title: enableMonitor
	 * @Description: 拓扑启用监控功能
	 * @param parentInstanceIds
	 * @throws ProfilelibException
	 *             void
	 * @throws
	 */
	@Override
	public void enableMonitorForTopo(List<Long> parentInstanceIds) throws ProfilelibException {
		HashSet<Long> parentCompartInstanceIds = null;
		if (parentInstanceIds != null && !parentInstanceIds.isEmpty()) {
			parentCompartInstanceIds = new HashSet<>(parentInstanceIds.size());
			for (Long parentInstanceId : parentInstanceIds) {
				parentCompartInstanceIds.add(parentInstanceId);
			}
			parentInstanceIds = new ArrayList<>(parentCompartInstanceIds);
		}
		if (parentInstanceIds == null || parentInstanceIds.isEmpty()) {
			if (logger.isErrorEnabled()) {
				logger.error("batch enableMonitor start error,parentInstanceIds is null!");
			}
			return;
		}
		if (logger.isTraceEnabled()) {
			logger.trace("batch enableMonitor start parentInstanceIds=" + parentInstanceIds);
		}

		/**
		 * 通过资源实例ID获取资源实例，过虑空资源入已监控的资源
		 * */
		List<ResourceInstance> mainResourceInstances = new ArrayList<ResourceInstance>(parentInstanceIds.size());
		{
			try {
				for (Long instanceId : parentInstanceIds) {
					ResourceInstance resourceIntance = resourceInstanceService.getResourceInstance(instanceId);
					if (null != resourceIntance) {
						if( resourceIntance.getLifeState() == InstanceLifeStateEnum.NOT_MONITORED ){
							mainResourceInstances.add(resourceIntance);
						}
					} else {
						if (logger.isWarnEnabled()) {
							logger.warn("enableMonitor resourceinstance not found,instanceId=" + instanceId);
						}
					}
				}
			} catch (InstancelibException e) {
				if (logger.isErrorEnabled()) {
					logger.error("enableMonitor to load ResourceInstance error", e);
				}
			}
		}
		if (mainResourceInstances.isEmpty()) {
			if (logger.isTraceEnabled()) {
				logger.trace("batch enableMonitor end parentInstanceIds=" + parentInstanceIds);
			}
			return;
		}
		Map<ResourceInstance, List<ProfileInfoPO>> instanceProfileInfos = new HashMap<ResourceInstance, List<ProfileInfoPO>>(mainResourceInstances.size());
		List<Long> mainInstanceIds = new ArrayList<Long>(mainResourceInstances.size());
		/**
		 * 检查资源是否使用过策略，如果使用过策略将上次使用的策略拿来加入监控
		 * */
		Map<Long,List<ProfileInstRelationPO>> lastProfileBind = new HashMap<Long, List<ProfileInstRelationPO>>();
		HashMap<Long, Long> deleted = new HashMap<Long, Long>();
		{
			List<ProfileInstRelationPO> lastProfileInstancesRel = new ArrayList<ProfileInstRelationPO>();
			List<Long> lastParentInstanceIds = new ArrayList<Long>();
			for (ResourceInstance mainInstance : mainResourceInstances) {
				// 检查默认策略是否被使用，未使用的修改状态为使用中
				List<ProfileInfoPO> infos = getDefaultProfileByResource(mainInstance.getResourceId());

				// 查找资源实例上一次存放的策略关系
				try {
					List<ProfileInstRelationPO> lastProfileInstRelationPOs = lastProfileDAO.getLastProfileByParentInstanceId(mainInstance.getId());
					lastProfileBind.put(mainInstance.getId(), lastProfileInstRelationPOs);
					if (null != lastProfileInstRelationPOs && !lastProfileInstRelationPOs.isEmpty()) {
						lastProfileInstancesRel.addAll(lastProfileInstRelationPOs);
						lastParentInstanceIds.add(mainInstance.getId());
						if (logger.isInfoEnabled()) {
							logger.info("parentInstanceId: " + mainInstance.getId() + " use last Profile!");
						}
					} else {
						mainInstanceIds.add(mainInstance.getId());
						instanceProfileInfos.put(mainInstance, infos);
					}
				} catch (Exception e) {
					if (logger.isErrorEnabled()) {
						logger.error("load ResourceInstance last profile error:" + e);
					}
				}
			}
			if (null != lastProfileInstancesRel && !lastProfileInstancesRel.isEmpty()) {
				deleted = removeProfileInstanceRelByResource(lastParentInstanceIds);
				// 直接添加到该策略中
				try {
					profileInstRelDAO.insertInstRels(lastProfileInstancesRel);
					updateInstanceStates(lastProfileInstancesRel, deleted, InstanceLifeStateEnum.MONITORED);
					// 同步到采集器
					addMonitorDataToDB(lastProfileInstancesRel, deleted);
				} catch (Exception e) {
					if (logger.isErrorEnabled()) {
						logger.error("enableProfile in insert last profile instance relations error:" + e);
					}
					throw new ProfilelibException(e);
				}
			}
		}

		if (null == mainInstanceIds || mainInstanceIds.isEmpty()) {
			if (logger.isTraceEnabled()) {
				logger.trace("batch enableMonitor end parentInstanceIds=" + parentInstanceIds);
			}
			return;
		}

		deleted = removeProfileInstanceRelByResource(mainInstanceIds);
		// 使用默认策略的主资源实例ID
		List<Long> useDefaultInstanceIds = new ArrayList<Long>();
		// 策略与资源实例关系
		List<ProfileInstRelationPO> profileInstRelationPOs = new ArrayList<>();
		Map<Long, InstanceLifeStateEnum> instStateEnumMap = new HashMap<Long, InstanceLifeStateEnum>();
		for (Map.Entry<ResourceInstance, List<ProfileInfoPO>> instanceProfileInfo : instanceProfileInfos.entrySet()) {
			ResourceInstance parentInstance = instanceProfileInfo.getKey();
			List<ProfileInfoPO> infos = instanceProfileInfo.getValue();
			if (null == infos || infos.isEmpty()) {
				useDefaultInstanceIds.add(parentInstance.getId());
				if (logger.isInfoEnabled()) {
					logger.info("parentInstanceId: " + parentInstance.getId() + " use default Profile!");
				}
				continue;
			} else {
				if (logger.isInfoEnabled()) {
					logger.info("parentInstanceId: " + parentInstance.getId() + " use existent Profile!");
				}
				long profileId = infos.get(0).getProfileId(), parentInstanceId = parentInstance.getId();
				instStateEnumMap.put(parentInstanceId, InstanceLifeStateEnum.MONITORED);
				ProfileInstRelationPO mainProfileInstRelationPO = new ProfileInstRelationPO();
				mainProfileInstRelationPO.setProfileId(profileId);
				mainProfileInstRelationPO.setInstanceId(parentInstanceId);
				profileInstRelationPOs.add(mainProfileInstRelationPO);
				List<ResourceInstance> children = parentInstance.getChildren();
				if (null != children && !children.isEmpty()) {
					// 查找资源实例上一次存放的策略关系
					children = filterDown(parentInstance, children);
					for (ResourceInstance subInstance : children) {
						List<ProfileInstRelationPO> last = lastProfileBind.get(parentInstance.getId());
						if(last != null && !last.isEmpty()){
							if(subInstance.getLifeState() == InstanceLifeStateEnum.NOT_MONITORED){
								//没有上一次保存策略.除不可用的接口。全部加入监控
								continue;
							}
						}
						long subInstanceId = subInstance.getId();
						List<ProfileInfoPO> childInfos = getDefaultProfileByResource(subInstance.getResourceId());
						long childProfileId = 0;
						instStateEnumMap.put(subInstanceId, InstanceLifeStateEnum.MONITORED);
						if (null != childInfos && !childInfos.isEmpty()) {
							childProfileId = childInfos.get(0).getProfileId();
						} else {
							ProfileInfo childProfileInfo = new ProfileInfo();
							childProfileInfo.setProfileId(ocProfilelibMainSequence.next());
							childProfileInfo.setResourceId(subInstance.getResourceId());
							childProfileInfo.setUse(true);
							childProfileInfo.setProfileType(ProfileTypeEnum.DEFAULT);
							childProfileInfo.setParentProfileId(profileId);
							childProfileInfo.setProfileName("默认策略" + subInstance.getResourceId());
							childProfileInfo.setUpdateTime(new Date());
							// 需要创建子策略
							childProfileId = innerCreateChildProfile(profileId, childProfileInfo);
						}
						ProfileInstRelationPO childProfileInstRelationPO = new ProfileInstRelationPO();
						childProfileInstRelationPO.setProfileId(childProfileId);
						childProfileInstRelationPO.setInstanceId(subInstanceId);
						childProfileInstRelationPO.setParentInstanceId(parentInstance.getId());
						profileInstRelationPOs.add(childProfileInstRelationPO);
					}
				}else{
					if(logger.isInfoEnabled()){
						logger.info("enableMonitorForTopo parentInstance["+parentInstanceId+"] childrens is empty!");
					}
				}

			}
		}

		try {
			// 添加父资源，子资源监控资源
			profileInstRelDAO.insertInstRels(profileInstRelationPOs);
			updateInstanceStates(profileInstRelationPOs, deleted, InstanceLifeStateEnum.MONITORED);
			addMonitorUseDefaultForTopo(useDefaultInstanceIds);
			// 同步到采集器
			addMonitorDataToDB(profileInstRelationPOs, deleted);
		} catch (Exception e) {
			if (logger.isErrorEnabled()) {
				logger.error("enableProfile in insert profile instance relations error:" + e);
			}
			throw new ProfilelibException(e);
		}

		if (logger.isTraceEnabled()) {
			logger.trace("enableMonitor end,parentInstanceIds:" + parentInstanceIds);
		}
	}

	

	@Override
	public void enableMonitor(long parentInstanceId) throws ProfilelibException {
		if (logger.isInfoEnabled()) {
			logger.info("enableMonitor one start parentInstanceId=" + parentInstanceId);
		}
		ResourceInstance resourceIntance = null;
		try {
			resourceIntance = resourceInstanceService.getResourceInstance(parentInstanceId);
			//子资源调用加入监控方法
			if(resourceIntance.getParentId()>0){
				ResourceInstance parentInstance = resourceIntance.getParentInstance();
				List<ResourceInstance> subInstaces = new ArrayList<ResourceInstance>();
				subInstaces.add(resourceIntance);
				//如果是子资源，过虑掉不可用接口
				subInstaces = filterDown(parentInstance, subInstaces);
				if(!CollectionUtils.isEmpty(subInstaces)){
					resourceIntance = subInstaces.get(0);
				}else{
					resourceIntance = null;
				}
			}
		} catch (InstancelibException e) {
			if (logger.isErrorEnabled()) {
				logger.error("enableMonitor to load ResourceInstance error", e);
			}
		}
		if (resourceIntance == null) {
			if (logger.isWarnEnabled()) {
				logger.warn("enableMonitor resourceinstance not found,instanceId=" + parentInstanceId);
			}
			if (logger.isTraceEnabled()) {
				logger.trace("enableMonitor end parentInstanceId=" + parentInstanceId);
			}
			return;
		}

		if (resourceIntance.getLifeState().equals(InstanceLifeStateEnum.MONITORED)) {
			if (logger.isInfoEnabled()) {
				logger.info("enableMonitor parentInstanceId=" + parentInstanceId + " has monitor.");
			}
			if (logger.isTraceEnabled()) {
				logger.trace("enableMonitor end parentInstanceId=" + parentInstanceId);
			}
			return;
		}

		// 检查默认策略是否被使用，未使用的修改状态为使用中
		List<ProfileInfoPO> infos = getDefaultProfileByResource(resourceIntance.getResourceId());
//		if (infos != null && !infos.isEmpty()) {
//			if (infos.get(0).getIsUse().equals("0")) {
//				List<String> resourceIds = new ArrayList<String>(1);
//				resourceIds.add(resourceIntance.getResourceId());
//				updateDefaultProfileStateByResourceId(resourceIds, true);
//			}
//		}
		List<ProfileInstRelationPO> lastProfileInstRelationPOs = null;
		// 查找资源实例上一次存放的策略关系
		try {
			lastProfileInstRelationPOs = lastProfileDAO.getLastProfileByParentInstanceId(parentInstanceId);
		} catch (Exception e) {
			if (logger.isErrorEnabled()) {
				logger.error("load ResourceInstance last profile error:" + e);
			}
			throw new ProfilelibException(e);
		}
		HashMap<Long, Long> deleted = removeProfileInstanceRelByResource(parentInstanceId);
		if (null != lastProfileInstRelationPOs && !lastProfileInstRelationPOs.isEmpty()) {
			// 直接添加到该策略中
			try {
				profileInstRelDAO.insertInstRels(lastProfileInstRelationPOs);
			} catch (Exception e) {
				if (logger.isErrorEnabled()) {
					logger.error("enableProfile in insert last profile instance relations error:" + e);
				}
				throw new ProfilelibException(e);
			}
			updateInstanceStates(lastProfileInstRelationPOs, deleted, InstanceLifeStateEnum.MONITORED);
			// 同步到采集器
			addMonitorDataToDB(lastProfileInstRelationPOs, deleted);
			if (logger.isTraceEnabled()) {
				logger.trace("enableMonitor end parentInstanceId=" + parentInstanceId);
			}
			return;
		}

		Map<Long, InstanceLifeStateEnum> instStateEnumMap = new HashMap<Long, InstanceLifeStateEnum>();
		instStateEnumMap.put(parentInstanceId, InstanceLifeStateEnum.MONITORED);
		List<ProfileInstRelationPO> profileInstRelationPOs = new ArrayList<>();
		// 存在策略
		if (infos != null && !infos.isEmpty()) {
			long profileId = infos.get(0).getProfileId();
			ProfileInstRelationPO mainProfileInstRelationPO = new ProfileInstRelationPO();
			mainProfileInstRelationPO.setProfileId(profileId);
			mainProfileInstRelationPO.setInstanceId(parentInstanceId);
			profileInstRelationPOs.add(mainProfileInstRelationPO);
			List<ResourceInstance> children = null;
			try {
				children = resourceInstanceService.getChildInstanceByParentId(parentInstanceId);
			} catch (InstancelibException e) {
				if (logger.isErrorEnabled()) {
					logger.error("failed to getChildInstanceByParentId in the enable monitor error:" + e);
				}
				throw new ProfilelibException(e);
			}
			if (null != children && !children.isEmpty()) {
				for (ResourceInstance subInstance : children) {
					long subInstanceId = subInstance.getId();
					List<ProfileInfoPO> childInfos = getDefaultProfileByResource(subInstance.getResourceId());
					long childProfileId = 0;
					instStateEnumMap.put(subInstanceId, InstanceLifeStateEnum.MONITORED);
					if (null != childInfos && !childInfos.isEmpty()) {
						childProfileId = childInfos.get(0).getProfileId();
					} else {
						ProfileInfo childProfileInfo = new ProfileInfo();
						childProfileInfo.setProfileId(ocProfilelibMainSequence.next());
						childProfileInfo.setResourceId(subInstance.getResourceId());
						childProfileInfo.setUse(true);
						childProfileInfo.setProfileType(ProfileTypeEnum.DEFAULT);
						childProfileInfo.setParentProfileId(profileId);
						childProfileInfo.setProfileName("默认策略" + subInstance.getResourceId());
						childProfileInfo.setUpdateTime(new Date());
						// 需要创建子策略
						childProfileId = innerCreateChildProfile(profileId, childProfileInfo);
					}

					ProfileInstRelationPO childProfileInstRelationPO = new ProfileInstRelationPO();
					childProfileInstRelationPO.setProfileId(childProfileId);
					childProfileInstRelationPO.setInstanceId(subInstanceId);
					childProfileInstRelationPO.setParentInstanceId(parentInstanceId);
					profileInstRelationPOs.add(childProfileInstRelationPO);
				}
			}

			try {
				removeProfileInstanceRelByResource(parentInstanceId);
				// 添加父资源，子资源监控资源
				profileInstRelDAO.insertInstRels(profileInstRelationPOs);
				updateInstanceStates(profileInstRelationPOs, deleted, InstanceLifeStateEnum.MONITORED);
			} catch (Exception e) {
				if (logger.isErrorEnabled()) {
					logger.error("enableProfile in insert profile instance relations error:" + e);
				}
				throw new ProfilelibException(e);
			}
			// 同步到采集器
			addMonitorDataToDB(profileInstRelationPOs, deleted);
		} else {
			addMonitorUseDefault(parentInstanceId);
		}
		if (logger.isInfoEnabled()) {
			logger.info("enableMonitor end,parentInstanceId:" + parentInstanceId);
		}
	}
	@Override
	public void enableChildMonitorByParentInstanceId(long parentId, List<Long> childInstanceIds)
			throws ProfilelibException {
		childMointor(parentId,childInstanceIds,false);
	}
	@Override
	public void enableMonitorForLink(List<Long> parentInstanceIds) throws ProfilelibException {
		if (logger.isInfoEnabled()) {
			logger.info("enableMonitorForLink start parentInstanceIds=" + parentInstanceIds);
		}
		List<ProfileInfoPO> infos = null;
		HashSet<Long> result = new HashSet<Long>(parentInstanceIds);
		try {
			String resourceId = LinkResourceConsts.RESOURCE_LAYER2LINK_ID;
			infos = getDefaultProfileByResource(resourceId);
			// 存在策略
			long profileId = 0;
			if (infos == null || infos.isEmpty()) {
				ResourceDef mainResourceDef = capacityService.getResourceDefById(resourceId);
				// 创建连线策略
				ProfileInfoPO addProfileInfoPO = new ProfileInfoPO();
				profileId = ocProfilelibMainSequence.next();
				addProfileInfoPO.setProfileId(profileId);
				addProfileInfoPO.setResourceId(resourceId);
				// 默认使用策略
				addProfileInfoPO.setIsUse("1");
				addProfileInfoPO.setProfileType(ProfileTypeEnum.DEFAULT.toString());
				addProfileInfoPO.setProfileName("默认策略" + mainResourceDef.getName());

				createDefaultSubProfileInfo(profileId, mainResourceDef);
			} else {
				profileId = infos.get(0).getProfileId();
			}

			List<ProfileInstRelationPO> profileInstRelationPOs = new ArrayList<>();
			for (long instanceId : result) {
				ResourceInstance resourceIntance = resourceInstanceService.getResourceInstance(instanceId);
				if (resourceIntance == null) {
					if (logger.isWarnEnabled()) {
						logger.warn("ResourceInstance not found.instanceId=" + instanceId);
					}
				} else {
					if (resourceIntance.getLifeState() == InstanceLifeStateEnum.MONITORED) {
						// 已经加入监控。不需要再次加入
						continue;
					}
				}
				ProfileInstRelationPO mainProfileInstRelationPO = new ProfileInstRelationPO();
				mainProfileInstRelationPO.setProfileId(profileId);
				mainProfileInstRelationPO.setInstanceId(instanceId);
				profileInstRelationPOs.add(mainProfileInstRelationPO);
			}
			// 添加父资源
			profileInstRelDAO.insertInstRels(profileInstRelationPOs);
			updateInstanceStates(profileInstRelationPOs, null, InstanceLifeStateEnum.MONITORED);
		} catch (Exception e) {
			if (logger.isErrorEnabled()) {
				logger.error("cancleMonitor getChildInstanceByParentId error", e);
			}
			throw new ProfilelibException(e);
		}
		if (logger.isInfoEnabled()) {
			logger.info("enableMonitorForLink end parentInstanceIds=" + parentInstanceIds);
		}
	}

	@Override
	public List<ProfileInfo> getAllProfileBasicInfo(ProfileTypeEnum profileType) throws ProfilelibException {
		if (logger.isTraceEnabled()) {
			logger.trace("getAllProfileBasicInfo start");
		}
		List<ProfileInfoPO> profileInfoPOs = null;
		try {
			profileInfoPOs = profileDAO.getAllProfilePos();
		} catch (Exception e) {
			if (logger.isErrorEnabled()) {
				logger.error("get ProfileBasicInfoByResourceIds error", e);
			}
		}
		List<ProfileInfo> profileInfos = null;
		if (null != profileInfoPOs && !profileInfoPOs.isEmpty()) {
			profileInfos = new ArrayList<ProfileInfo>(profileInfoPOs.size());
			for (ProfileInfoPO profileInfoPO : profileInfoPOs) {
				if (!LinkResourceConsts.RESOURCE_LAYER2LINK_ID.equals(profileInfoPO.getResourceId())) {
					if (profileType == null || profileType == ProfileTypeEnum.ALL) {
						profileInfos.add(transProfileInfoPO2BO(profileInfoPO));
					} else {
						if (profileInfoPO.getProfileType().equals(profileType.toString())) {
							profileInfos.add(transProfileInfoPO2BO(profileInfoPO));
						}
					}
				}
			}
		}
		if (logger.isTraceEnabled()) {
			logger.trace("getAllProfileBasicInfo end");
		}
		return profileInfos;
	}

	@Override
	public ProfileInfo getBasicInfoByResourceInstanceId(long resourceInstanceId) throws ProfilelibException {
		if (logger.isTraceEnabled()) {
			logger.trace("getBasicInfoByResourceInstanceId start instanceId=" + resourceInstanceId);
		}
		ProfileInfo profileInfo = null;
		try {
			ProfileInstRelationPO profileInstRelationPO = profileInstRelDAO.getInstRelationByInstId(resourceInstanceId);
			if (profileInstRelationPO != null) {
				// 获取实例的策略信息
				ProfileInfoPO profileInfoPO = profileDAO.getProfilePoById(profileInstRelationPO.getProfileId());
				if (profileInfoPO != null) {
					profileInfo = transProfileInfoPO2BO(profileInfoPO);
				}
			}
		} catch (Exception e) {
			if (logger.isErrorEnabled()) {
				logger.error("", e);
			}
		}
		if (logger.isTraceEnabled()) {
			logger.trace("getBasicInfoByResourceInstanceId end instanceId=" + resourceInstanceId);
		}
		return profileInfo;
	}

	@Override
	public Map<Long, ProfileInfo> getBasicInfoByResourceInstanceIds(List<Long> resourceInstanceIds) throws ProfilelibException {
		if (logger.isTraceEnabled()) {
			logger.trace("getBasicInfoByResourceInstanceIds start");
		}
		Map<Long, ProfileInfo> result = null;
		try {
			List<ProfileInstRelationPO> profileInstRelationPOs = profileInstRelDAO.getInstRelationByInstIds(resourceInstanceIds);

			if (null != profileInstRelationPOs && !profileInstRelationPOs.isEmpty()) {
				// 用于查找策略基本信息
				HashSet<Long> profileIds = new HashSet<Long>();
				Map<Long, List<Long>> profileIntance = new HashMap<>();
				for (ProfileInstRelationPO profileInstRelationPO : profileInstRelationPOs) {
					profileIds.add(profileInstRelationPO.getProfileId());
					List<Long> intanceIds = profileIntance.get(profileInstRelationPO.getProfileId());
					if (intanceIds == null) {
						intanceIds = new ArrayList<Long>();
						profileIntance.put(profileInstRelationPO.getProfileId(), intanceIds);
					}
					intanceIds.add(profileInstRelationPO.getInstanceId());
				}
				if (!profileIds.isEmpty()) {
					// 获取实例的策略信息
					List<ProfileInfoPO> profileInfos = profileDAO.getProfilePoByIds(new ArrayList<Long>(profileIds));
					if (profileInfos != null && !profileInfos.isEmpty()) {
						result = new HashMap<Long, ProfileInfo>();
						for (ProfileInfoPO profileInfoPO : profileInfos) {
							ProfileInfo profileInfo = transProfileInfoPO2BO(profileInfoPO);
							List<Long> intanceIds = profileIntance.get(profileInfo.getProfileId());
							for (Long instanceId : intanceIds) {
								result.put(instanceId, profileInfo);
							}
						}
					}
				}
			}

		} catch (Exception e) {
			if (logger.isErrorEnabled()) {
				logger.error("getBasicInfoByResourceInstanceIds error!", e);
			}
		}
		if (logger.isTraceEnabled()) {
			logger.trace("getBasicInfoByResourceInstanceIds end");
		}
		return result;
	}

	@Override
	public Profile getEmptyPersonalizeProfile(String parentResourceId, long parentInstanceId) throws ProfilelibException {
		if (logger.isTraceEnabled()) {
			StringBuilder b = new StringBuilder();
			b.append("getEmptyPersonalizeProfile start parentResourceId=");
			b.append(parentResourceId);
			b.append(",parentInstanceId=");
			b.append(parentInstanceId);
			logger.trace(b);
		}
		ResourceDef mainDef = capacityService.getResourceDefById(parentResourceId);
		if (mainDef == null) {
			return null;
		}

		/*
		 * 父模型
		 */
		Profile profile = new Profile();
		ProfileInfo profileInfo = new ProfileInfo();
		profileInfo.setResourceId(parentResourceId);
		profileInfo.setProfileType(ProfileTypeEnum.PERSONALIZE);
		profileInfo.setProfileName("个性化策略" + parentInstanceId);
		profileInfo.setUse(true);
		profile.setProfileInfo(profileInfo);
		ProfileInstanceRelation relation = new ProfileInstanceRelation();

		List<Instance> instances = new ArrayList<Instance>();
		Instance instance = new Instance();
		instance.setInstanceId(parentInstanceId);
		instances.add(instance);
		relation.setInstances(instances);
		profile.setProfileInstanceRelations(relation);

		// 指标
		ResourceMetricDef[] metricDefs = mainDef.getMetricDefs();
		if (null != metricDefs && metricDefs.length > 0) {
			List<ProfileMetric> metrics = new ArrayList<ProfileMetric>(metricDefs.length);
			for (ResourceMetricDef metricDef : metricDefs) {
				ProfileMetric metric = new ProfileMetric();
				metric.setMetricId(metricDef.getId());
				metric.setMonitor(metricDef.isMonitor());
				metric.setDictFrequencyId(metricDef.getDefaultMonitorFreq().name());
				metric.setAlarm(metricDef.isAlert());
				metric.setAlarmFlapping(metricDef.getDefaultFlapping());
				// 阈值
				ThresholdDef[] thresholdDef = metricDef.getThresholdDefs();
				List<ProfileThreshold> profileThresholds = null;
				if (thresholdDef != null && thresholdDef.length > 0) {
					profileThresholds = new ArrayList<ProfileThreshold>(3);
					for (ThresholdDef childThresholdDef : thresholdDef) {
						ProfileThreshold profileThreshold = new ProfileThreshold();
						profileThreshold.setMetricId(metric.getMetricId());
						profileThreshold.setPerfMetricStateEnum(childThresholdDef.getState());
						profileThreshold.setThresholdExpression(childThresholdDef.getThresholdExpression());
						if(childThresholdDef.getOperator() != null){
							profileThreshold.setExpressionOperator(childThresholdDef.getOperator().toString());	
						}
						profileThreshold.setThresholdValue(childThresholdDef.getDefaultvalue());
						profileThresholds.add(profileThreshold);
					}
					metric.setMetricThresholds(profileThresholds);
				}
				metrics.add(metric);
			}
			profile.setMetricSetting(new MetricSetting(metrics));
		}

		List<ResourceInstance> children = null;
		try {
			children = resourceInstanceService.getChildInstanceByParentId(parentInstanceId);
		} catch (InstancelibException e) {
			if (logger.isErrorEnabled()) {
				logger.error("", e);
			}
		}
		/*
		 * 子模型
		 */
		ResourceDef[] childResources = mainDef.getChildResourceDefs();
		if (childResources != null && childResources.length > 0) {
			List<Profile> childProfiles = new ArrayList<Profile>(childResources.length);
			for (ResourceDef resourceDef : childResources) {
				Profile childProfile = new Profile();
				ProfileInfo childProfileInfo = new ProfileInfo();
				String subResId = resourceDef.getId();
				childProfileInfo.setResourceId(subResId);
				childProfileInfo.setProfileType(ProfileTypeEnum.PERSONALIZE);
				childProfileInfo.setProfileName("个性化子策略" + resourceDef.getName());
				childProfileInfo.setUse(true);
				childProfile.setProfileInfo(childProfileInfo);

				// 指标
				List<ProfileMetric> childMetrics = null;
				ResourceMetricDef[] childMetricDefs = resourceDef.getMetricDefs();
				if (childMetricDefs != null && childMetricDefs.length > 0) {
					childMetrics = new ArrayList<ProfileMetric>(childMetricDefs.length);
					for (ResourceMetricDef childMetricDef : childMetricDefs) {
						ProfileMetric childMetric = new ProfileMetric();
						childMetric.setMetricId(childMetricDef.getId());
						childMetric.setMonitor(childMetricDef.isMonitor());
						childMetric.setDictFrequencyId(childMetricDef.getDefaultMonitorFreq().name());
						childMetric.setAlarm(childMetricDef.isAlert());
						childMetric.setAlarmFlapping(childMetricDef.getDefaultFlapping());
						// 阈值
						ThresholdDef[] childThresholdDefs = childMetricDef.getThresholdDefs();
						List<ProfileThreshold> childProfileThresholds = null;
						if (childThresholdDefs != null && childThresholdDefs.length > 0) {
							childProfileThresholds = new ArrayList<ProfileThreshold>(3);
							for (ThresholdDef childThresholdDef : childThresholdDefs) {
								ProfileThreshold childProfileThreshold = new ProfileThreshold();
								childProfileThreshold.setMetricId(childMetric.getMetricId());
								childProfileThreshold.setPerfMetricStateEnum(childThresholdDef.getState());
								childProfileThreshold.setThresholdExpression(childThresholdDef.getThresholdExpression());
								if(childThresholdDef.getOperator() != null){
									childProfileThreshold.setExpressionOperator(childThresholdDef.getOperator().toString());
								}
								childProfileThreshold.setThresholdValue(childThresholdDef.getDefaultvalue());
								childProfileThresholds.add(childProfileThreshold);
							}
							childMetric.setMetricThresholds(childProfileThresholds);
						}
						childMetrics.add(childMetric);
					}
					childProfile.setMetricSetting(new MetricSetting(childMetrics));
				}
				if (children != null) {
					Iterator<ResourceInstance> iterator = children.iterator();
					List<Instance> childrenInstances = new ArrayList<Instance>();
					while (iterator.hasNext()) {
						ResourceInstance child = iterator.next();
						if (resourceDef.getId().equals(child.getResourceId())) {
							if(child.getLifeState() == InstanceLifeStateEnum.MONITORED){
								Instance chindInstance = new Instance();
								chindInstance.setInstanceId(child.getId());
								chindInstance.setParentInstanceId(parentInstanceId);
								childrenInstances.add(chindInstance);
							}
							iterator.remove();
						}
					}
					if (!childrenInstances.isEmpty()) {
						ProfileInstanceRelation childRelation = new ProfileInstanceRelation();
						childRelation.setInstances(childrenInstances);
						childProfile.setProfileInstanceRelations(childRelation);
					}
				}
				childProfiles.add(childProfile);
			}
			profile.setChildren(childProfiles);
		}
		if (logger.isTraceEnabled()) {
			StringBuilder b = new StringBuilder();
			b.append("getEmptyPersonalizeProfile end parentResourceId=");
			b.append(parentResourceId);
			b.append(",parentInstanceId=");
			b.append(parentInstanceId);
			logger.trace(b);
		}
		return profile;
	}

	@Override
	public List<ProfileMetric> getMetricByInstanceId(long resourceInstanceId) throws ProfilelibException {

		if (logger.isTraceEnabled()) {
			logger.trace("getMetricByInstanceId start instanceId=" + resourceInstanceId);
		}
		List<ProfileMetric> profileMetrics = null;
		ProfileInfo profileInfo = getBasicInfoByResourceInstanceId(resourceInstanceId);
		if (profileInfo != null) {
			long profileId = profileInfo.getProfileId();
			List<ProfileMetric> metricIds = getMetricByProfileId(profileId);
			if (metricIds != null) {
				profileMetrics = new ArrayList<ProfileMetric>(20);
				for (ProfileMetric profileMetric : metricIds) {
					profileMetrics.add(profileMetric);
				}
			}
		}
		if (logger.isTraceEnabled()) {
			logger.trace("getMetricByInstanceId end instanceId=" + resourceInstanceId);
		}
		return profileMetrics;
	}

	@Override
	public ProfileMetric getMetricByInstanceIdAndMetricId(long resourceInstanceId, String metricId) throws ProfilelibException {
		long profileId = 0;
		ProfileMetric profileMetric = null;
		ProfileInfo profileInfo = getBasicInfoByResourceInstanceId(resourceInstanceId);
		if (profileInfo != null) {
			profileId = profileInfo.getProfileId();
			profileMetric = getMetricByProfileIdAndMetricId(profileId, metricId);
		}
		return profileMetric;
	}

	@Override
	public List<ProfileMetric> getMetricByProfileId(long profileId) throws ProfilelibException {
		if (logger.isTraceEnabled()) {
			logger.trace("getMetricByProfileId start profileId=" + profileId);
		}
		// metrics
		List<ProfileMetric> metrics = null;
		HashSet<String> metricIds = profileCache.getMetricIdByProfileId(profileId);
		if (CollectionUtils.isNotEmpty(metricIds) && metricIds.size() >= 3) {
			metrics = new ArrayList<ProfileMetric>(20);
			for (String metricId : metricIds) {
				ProfileMetric profileMetric = getMetricByProfileIdAndMetricId(profileId, metricId);
				if (profileMetric == null) {
					if (logger.isErrorEnabled()) {
						logger.error("getMetricByProfileIdAndMetricId error profileMetric is null , metricId = " + metricId);
					}
					continue;
				}
				metrics.add(convertToQyeryProfileMetric(profileMetric));
				/*
				 * 注释获取阈值，使用懒加载
				 */
//				if (profileMetric != null) {
//					List<ProfileThreshold> d = getThresholdByProfileIdAndMetricId(profileId, metricId);
//					profileMetric.setMetricThresholds(d);
//					metrics.add(profileMetric);
//				}
			}
			return metrics;
		} else {
			List<ProfileMetricPO> metricsPOs = null;
			try {
				// 查找阈值
				metricsPOs = profileMetricDAO.getMetricsByProfileId(profileId);
				List<ProfileThresholdPO> profileThresholdPOs = profileThresholdDAO.getThresholdByProfileId(profileId);
				Map<String, ProfileMetric> metricsMap = new HashMap<>();
				if (metricsPOs != null) {
					Map<Long, HashSet<String>> profileMetricIds = new HashMap<Long, HashSet<String>>();
					List<ProfileMetric> cacheMetric = new ArrayList<ProfileMetric>(20);
					List<ProfileThreshold> thresholds = transProfileThresholdPO2BOs(profileThresholdPOs);
					for (ProfileMetricPO profileMetricPO : metricsPOs) {
						ProfileMetric profileMetric = tranProfileMetricPO2BO(profileMetricPO);
						metricsMap.put(profileMetric.getMetricId(), profileMetric);
						cacheMetric.add(profileMetric);
						HashSet<String> ids = profileMetricIds.get(profileMetricPO.getProfileId());
						if (ids == null) {
							ids = new HashSet<String>(20);
							profileMetricIds.put(profileMetricPO.getProfileId(), ids);
						}
						ids.add(profileMetricPO.getMetricId());
					}
					for (ProfileThreshold profileThreshold : thresholds) {
						if (metricsMap.containsKey(profileThreshold.getMetricId())) {
							ProfileMetric m = metricsMap.get(profileThreshold.getMetricId());
							if (m.getMetricThresholds() == null) {
								m.setMetricThresholds(new ArrayList<ProfileThreshold>());
							}
							m.getMetricThresholds().add(profileThreshold);
						}
					}
					// add to cache
					for (ProfileMetric profileMetric : cacheMetric) {
						profileCache.addProfileMetric(profileMetric);
						ProfileMetric m = metricsMap.get(profileMetric.getMetricId());
						if (m != null) {
							List<ProfileThreshold> profileThresholds = m.getMetricThresholds();
							if (profileThresholds != null && !profileThresholds.isEmpty()) {
								profileCache.addProfileThreshold(profileId, profileMetric.getMetricId(), profileThresholds);
							}
						}
					}
					// set profile metricId
					if (!profileMetricIds.isEmpty()) {
						for (Entry<Long, HashSet<String>> item : profileMetricIds.entrySet()) {
							profileCache.setProfileMetricKey(item.getKey(), item.getValue());
						}
					}
				}
				metrics = new ArrayList<ProfileMetric>(metricsMap.values());
			} catch (Exception e) {
				if (logger.isErrorEnabled()) {
					logger.error("", e);
				}
			}
		}
		if (logger.isTraceEnabled()) {
			logger.trace("getMetricByProfileId end profileId=" + profileId);
		}
		return metrics;
	}

	@Override
	public ProfileMetric getMetricByProfileIdAndMetricId(long profileId, String metricId) throws ProfilelibException {
		if (logger.isTraceEnabled()) {
			StringBuilder b = new StringBuilder(50);
			b.append("getMetricByprofileId start profileId=");
			b.append(profileId).append(" metricId=").append(metricId);
			logger.trace(b);
		}
		ProfileMetric profileMetric = null;
		try {
			// 取缓存
			profileMetric = profileCache.getProfileMetricBymetricId(profileId, metricId);
			if (profileMetric != null) {
				//使用懒加载阈值
				return convertToQyeryProfileMetric(profileMetric);
			}
			List<ProfileMetricPO> profileMetricPOs = profileMetricDAO.getMetricsByProfileId(profileId);
			if (profileMetricPOs != null) {
				for (ProfileMetricPO profileMetricPO : profileMetricPOs) {
					if (metricId.equals(profileMetricPO.getMetricId())) {
						profileMetric = tranProfileMetricPO2BO(profileMetricPO);
						break;
					}
				}
				// 查找阈值
				if (profileMetric != null) {
					List<ProfileThresholdPO> profileThresholdPOs = profileThresholdDAO.getThresholdByProfileIdAndMetricId(profileId, metricId);
					if (profileThresholdPOs != null) {
						List<ProfileThreshold> profileThresholds = transProfileThresholdPO2BOs(profileThresholdPOs);
						profileMetric.setMetricThresholds(profileThresholds);
					}
				}
			}
			// add to cache
			if (profileMetric != null) {
				profileCache.addProfileMetric(profileMetric);
				List<ProfileThreshold> profileThresholds = profileMetric.getMetricThresholds();
				if (profileThresholds != null && !profileThresholds.isEmpty()) {
					profileCache.addProfileThreshold(profileId, metricId, profileThresholds);
				}
			}
		} catch (Exception e) {
			if (logger.isErrorEnabled()) {
				logger.error("getMetricByprofileIdAndMetricId error", e);
			}
			throw new ProfilelibException(111, "getMetricByprofileIdAndMetricId error");
		}
		if (logger.isTraceEnabled()) {
			StringBuilder b = new StringBuilder(50);
			b.append("getMetricByProfileIdAndMetricId start profileId=");
			b.append(profileId).append(" metricId=").append(metricId);
			logger.trace(b);
		}
		return profileMetric;
	}

	@Override
	public List<ProfileInfo> getParentProfileBasicInfo() throws ProfilelibException {
		if (logger.isTraceEnabled()) {
			logger.trace("getParentProfileBasicInfo start");
		}
		List<ProfileInfoPO> profileInfoPOs = null;
		try {
			profileInfoPOs = profileDAO.getAllProfilePos();
		} catch (Exception e) {
			if (logger.isErrorEnabled()) {
				logger.error("getParentProfileBasicInfo error", e);
			}
		}
		List<ProfileInfo> profileInfos = null;
		if (null != profileInfoPOs && !profileInfoPOs.isEmpty()) {
			profileInfos = new ArrayList<ProfileInfo>(profileInfoPOs.size());
			for (ProfileInfoPO profileInfoPO : profileInfoPOs) {
				if (profileInfoPO.getParentProfileId() == null || profileInfoPO.getParentProfileId() == 0) {
					if ("1".equals(profileInfoPO.getIsUse()) && !LinkResourceConsts.RESOURCE_LAYER2LINK_ID.equals(profileInfoPO.getResourceId())) {
						profileInfos.add(transProfileInfoPO2BO(profileInfoPO));
					}
				}
			}
		}
		if (logger.isTraceEnabled()) {
			logger.trace("getParentProfileBasicInfo end");
		}
		return profileInfos;
	}

	@Override
	public List<ProfileInfo> getParentProfileBasicInfoByType(ProfileTypeEnum profileType) throws ProfilelibException {
		if (logger.isTraceEnabled()) {
			logger.trace("getProfileBasicInfoByType start ProfileType=" + profileType.toString());
		}
		ProfileInfoPO queryProfileInfoPO = new ProfileInfoPO();
		if (profileType != null) {
			if (profileType != ProfileTypeEnum.ALL) {
				queryProfileInfoPO.setProfileType(profileType.toString());
				queryProfileInfoPO.setIsUse("1");
			}
		}
		List<ProfileInfoPO> profileInfoPOs = null;
		try {
			profileInfoPOs = profileDAO.getProfileInfoPO(queryProfileInfoPO);
		} catch (Exception e) {
			if (logger.isErrorEnabled()) {
				logger.error("getProfileBasicInfoByType error", e);
			}
		}
		if (profileInfoPOs == null) {
			return null;
		}
		List<ProfileInfo> profileInfos = new ArrayList<ProfileInfo>();

		for (ProfileInfoPO profileInfoPO : profileInfoPOs) {
			if (profileInfoPO.getParentProfileId() == null || profileInfoPO.getParentProfileId() == 0) {
				profileInfos.add(transProfileInfoPO2BO(profileInfoPO));
			}
		}
		if (logger.isTraceEnabled()) {
			logger.trace("getProfileBasicInfoByType end ProfileType=" + profileType.toString());
		}
		return profileInfos;
	}

	@Override
	public ProfileInfo getPersonalizeProfileBasicInfoByResourceInstanceId(long resourceInstanceId) throws ProfilelibException {
		if (logger.isTraceEnabled()) {
			logger.trace("getPersonalizeProfileBasicInfoByResourceInstanceId start instanceId=" + resourceInstanceId);
		}
		ProfileInfo profileInfo = null;
		try {
			ProfileInfoPO query = new ProfileInfoPO();
			query.setResourceInstanceId(resourceInstanceId);
			List<ProfileInfoPO> profileInfoPOs = profileDAO.getProfileInfoPO(query);
			if (profileInfoPOs != null && !profileInfoPOs.isEmpty()) {
				profileInfo = transProfileInfoPO2BO(profileInfoPOs.get(0));
			}
		} catch (Exception e) {
			if (logger.isErrorEnabled()) {
				logger.error("getPersonalizeProfileBasicInfoByResourceInstanceId error!", e);
			}
		}
		if (logger.isTraceEnabled()) {
			logger.trace("getPersonalizeProfileBasicInfoByResourceInstanceId end instanceId=" + resourceInstanceId);
		}
		return profileInfo;
	}
	
	@Override
	public ProfileMetric getProfileMetricByInstanceIdAndMetricId(long resourceInstanceId,
			String metricId) throws ProfilelibException {
		// TODO 目前只用于前台显示
		long profileId = 0;
		ProfileMetric profileMetric = null;
		List<ProfileMetric> timelineProfileMetrics = null;
		ProfileInfo profileInfo = getBasicInfoByResourceInstanceId(resourceInstanceId);
		if (profileInfo != null) {
			profileId = profileInfo.getProfileId();
			//此处是没有基线的profileMetric
			profileMetric = getMetricByProfileIdAndMetricId(profileId, metricId);
			List<Timeline> timelines = timelineService.getTimelinesByProfileId(profileId);
			if (timelines != null) {
				boolean isCheck = false;
				Date nowDate = new Date();
				for (Timeline timeline : timelines) {
					TimelineInfo timelineInfo = timeline.getTimelineInfo();
					if (timelineInfo != null) {
						Date startTime = timelineInfo.getStartTime();
						Date endTime = timelineInfo.getEndTime();
						if (startTime != null && endTime != null) {
							timelineProfileMetrics = new ArrayList<ProfileMetric>();
							if (nowDate.getTime() >= startTime.getTime() && nowDate.getTime() <= endTime.getTime()) {
								timelineProfileMetrics.addAll(timeline.getMetricSetting().getMetrics());
								if (!isCheck) {
									isCheck = true;
								}
							}
						}
					}
				}
				if (isCheck) {
					for (ProfileMetric timelineProfileMetric : timelineProfileMetrics) {
						if (metricId.equals(timelineProfileMetric.getMetricId())) {
							return timelineProfileMetric;
						}
					}
				}else {
					return profileMetric;
				}
			}else {
				return profileMetric;
			}
		}
		return profileMetric;
	}
	
	@Override
	public List<ProfileMetric> getProfileMetricsByResourceInstanceId(long resourceInstanceId) throws ProfilelibException {
		// TODO 目前只用于前台显示
		ProfileInfo profileInfo = getBasicInfoByResourceInstanceId(resourceInstanceId);
		List<ProfileMetric> profileMetrics = null;
		List<ProfileMetric> timelineProfileMetrics = null;
		if (profileInfo != null) {
			long profileId = profileInfo.getProfileId();
			//此处 获取的profileMetrics不含基线
			profileMetrics = getMetricByProfileId(profileId);
			List<Timeline> timelines = timelineService.getTimelinesByProfileId(profileId);
			if (CollectionUtils.isNotEmpty(timelines)) {
				boolean isCheck = false;
				Date nowDate = new Date();
				for (Timeline timeline : timelines) {
					TimelineInfo timelineInfo = timeline.getTimelineInfo();
					if (timelineInfo != null) {
						Date startTime = timelineInfo.getStartTime();
						Date endTime = timelineInfo.getEndTime();
						if (startTime != null && endTime != null) {
							timelineProfileMetrics = new ArrayList<ProfileMetric>();
							if (nowDate.getTime() >= startTime.getTime() && nowDate.getTime() <= endTime.getTime()) {
								timelineProfileMetrics.addAll(timeline.getMetricSetting().getMetrics());
								if (!isCheck) {
									isCheck = true;
								}
							}
						}
					}
				}
				if (isCheck) {
					return timelineProfileMetrics;
				}else {
					return profileMetrics;
				}
			}else {
				return profileMetrics;
			}
		}
		return profileMetrics;
	}
	
	@Override
	public ProfileInfo getProfileBasicInfoByProfileId(long proflieId) throws ProfilelibException {
		if (logger.isTraceEnabled()) {
			logger.trace("getProfileBasicInfoByProfileId start proflieId" + proflieId);
		}
		ProfileInfo profileInfo = null;
		try {
			ProfileInfoPO profileInfoPO = profileDAO.getProfilePoById(proflieId);
			if (profileInfoPO != null) {
				profileInfo = transProfileInfoPO2BO(profileInfoPO);
			}
		} catch (Exception e) {
			if (logger.isErrorEnabled()) {
				logger.error("getProfilePoById error.", e);
			}
		}
		if (logger.isTraceEnabled()) {
			logger.trace("getProfileBasicInfoByProfileId end proflieId" + proflieId);
		}
		return profileInfo;
	}

	@Override
	public ProfileInfo getProfileBasicInfoByResourceId(String resourceId) throws ProfilelibException {
		if (logger.isTraceEnabled()) {
			logger.trace("getProfileBasicInfoByResourceId start");
		}
		List<ProfileInfoPO> profileInfoPOs = null;
		List<String> resourceIds = new ArrayList<>();
		resourceIds.add(resourceId);
		try {
			profileInfoPOs = profileDAO.getProfileBasicInfoByResourceIds(resourceIds, null);
		} catch (Exception e) {
			if (logger.isErrorEnabled()) {
				logger.error("get ProfileBasicInfoByResourceIds error", e);
			}
		}
		ProfileInfo profileInfo = null;
		if (profileInfoPOs != null) {
			profileInfo = transProfileInfoPO2BO(profileInfoPOs.get(0));
		}
		if (logger.isTraceEnabled()) {
			logger.trace("getProfileBasicInfoByResourceId end");
		}
		return profileInfo;
	}

	@Override
	public List<ProfileInfo> getProfileBasicInfoByResourceId(List<String> resourceIds, ProfileTypeEnum profileType) throws ProfilelibException {
		if (logger.isTraceEnabled()) {
			logger.trace("getProfileBasicInfoByResourceId start");
		}
		List<ProfileInfoPO> profileInfoPOs = null;
		try {
			profileInfoPOs = profileDAO.getProfileBasicInfoByResourceIds(resourceIds, profileType.name());
		} catch (Exception e) {
			if (logger.isErrorEnabled()) {
				logger.error("get ProfileBasicInfoByResourceIds error", e);
			}
		}
		List<ProfileInfo> profileInfos = new ArrayList<ProfileInfo>();
		if (null != profileInfoPOs && !profileInfoPOs.isEmpty()) {
			for (ProfileInfoPO profileInfoPO : profileInfoPOs) {
				if (profileType == null) {
					profileInfos.add(transProfileInfoPO2BO(profileInfoPO));
				} else {
					if (profileInfoPO.getParentProfileId() == null || profileInfoPO.getParentProfileId() == 0) {
						if (profileType.toString().equals(profileInfoPO.getProfileType())) {
							profileInfos.add(transProfileInfoPO2BO(profileInfoPO));
						}
					}
				}
			}
		}
		if (logger.isTraceEnabled()) {
			logger.trace("getProfileBasicInfoByResourceId end");
		}
		return profileInfos;
	}

	@Override
	public List<ProfileInfo> getProfileBasicInfoByResourceIds(List<String> resourceIds) throws ProfilelibException {
		if (logger.isTraceEnabled()) {
			logger.trace("getProfileBasicInfoByResourceId start");
		}
		List<ProfileInfoPO> profileInfoPOs = null;
		try {
			profileInfoPOs = profileDAO.getProfileBasicInfoByResourceIds(resourceIds, null);
		} catch (Exception e) {
			if (logger.isErrorEnabled()) {
				logger.error("get ProfileBasicInfoByResourceIds error", e);
			}
		}
		List<ProfileInfo> profileInfos = new ArrayList<ProfileInfo>();
		if (null != profileInfoPOs && !profileInfoPOs.isEmpty()) {
			for (ProfileInfoPO profileInfoPO : profileInfoPOs) {
				profileInfos.add(transProfileInfoPO2BO(profileInfoPO));
			}
		}
		if (logger.isTraceEnabled()) {
			logger.trace("getProfileBasicInfoByResourceId end");
		}
		return profileInfos;
	}

	@Override
	public Profile getProfilesById(long profileId) throws ProfilelibException {
		if (logger.isDebugEnabled()) {
			logger.debug("getProfilesById start proflieId=" + profileId);
		}
		Profile profile = null;
		try {
			ProfileInfoPO po = profileDAO.getProfilePoById(profileId);
			if(po != null){
				profile = queryProfile(po,-1);
				ProfileInfoPO queryPo = new ProfileInfoPO();
				queryPo.setParentProfileId(profileId);
				queryPo.setIsUse("1");
				List<ProfileInfoPO> children = profileDAO.getProfileInfoPO(queryPo);
				if (children != null && children.size() > 0) {
					List<Profile> childrenProfiles = new ArrayList<>();
					for (ProfileInfoPO child : children) {
						childrenProfiles.add(queryProfile(child,-1));
					}
					profile.setChildren(childrenProfiles);
				}
			}
		} catch (Exception e) {
			if (logger.isErrorEnabled()) {
				logger.error("getProfilesById", e);
			}
		}
		if (logger.isDebugEnabled()) {
			StringBuilder b = new StringBuilder(100);
			b.append("getProfilesById end proflieId=");
			b.append(profileId);
			if(profile == null){
				b.append(" ,Query result is null");
			}else{
				b.append(" ,Query result is ok");
			}
			logger.debug(b);
		}
		return profile;
	}

	@Override
	public Profile getProfilesById(long profileId, int nodeGroupId)
			throws ProfilelibException {
		Profile profile = null;
		try {
			ProfileInfoPO po = profileDAO.getProfilePoById(profileId);
			if(po != null){
				profile = queryProfile(po,nodeGroupId);
				ProfileInfoPO queryPo = new ProfileInfoPO();
				queryPo.setParentProfileId(profileId);
				queryPo.setIsUse("1");
				List<ProfileInfoPO> children = profileDAO.getProfileInfoPO(queryPo);
				if (children != null && children.size() > 0) {
					List<Profile> childrenProfiles = new ArrayList<>();
					for (ProfileInfoPO child : children) {
						childrenProfiles.add(queryProfile(child,nodeGroupId));
					}
					profile.setChildren(childrenProfiles);
				}
			}
		} catch (Exception e) {
			if (logger.isErrorEnabled()) {
				logger.error("getProfilesById", e);
			}
		}
		return profile;
	}
	
	private Profile queryProfile(ProfileInfoPO profileInfoPO,int nodeGroup) throws Exception {
		Profile profile = new Profile();
		long profileId = profileInfoPO.getProfileId();
		ProfileInfo infoBo = transProfileInfoPO2BO(profileInfoPO);
		profile.setProfileInfo(infoBo);
		// relations
		List<ProfileInstRelationPO> relationsPOs = null;
		if(nodeGroup < 0){
			relationsPOs = profileInstRelDAO.getInstRelationsByProfileId(profileId);
		}else{ //查询某个dcs 下资源
			relationsPOs = profileInstRelDAO.getInstRelationsByProfileId(profileId,nodeGroup);
		}
		ProfileInstanceRelation profileInstRelationBOs = transProfileInstRelationPO2BOs(relationsPOs);
		profile.setProfileInstanceRelations(profileInstRelationBOs);
		// metrics
		List<ProfileMetric> metrics = getMetricByProfileId(profileId);
		if (metrics != null) {
			MetricSetting setting = new MetricSetting();
			setting.setMetrics(metrics);
			profile.setMetricSetting(setting);
		}
		// 基线
		List<Timeline> timelines = timelineService.getTimelinesByProfileId(profileId);
		if (timelines != null) {
			profile.setTimeline(timelines);
		}
		return profile;
	}

	

	@Override
	public List<Long> getResourceInstanceByProfileId(long profileId) throws ProfilelibException {
		if (logger.isTraceEnabled()) {
			logger.trace("getResourceInstanceByProfileId start profileId=" + profileId);
		}
		List<Long> intanceIds = null;
		try {
			List<ProfileInstRelationPO> profileInstRelationPOs = profileInstRelDAO.getInstRelationsByProfileId(profileId);
			if (profileInstRelationPOs != null && !profileInstRelationPOs.isEmpty()) {
				intanceIds = new ArrayList<>();
				for (ProfileInstRelationPO profileInstRelationPO : profileInstRelationPOs) {
					intanceIds.add(profileInstRelationPO.getInstanceId());
				}
			}
		} catch (Exception e) {
			if (logger.isErrorEnabled()) {
				logger.error("getResourceInstanceByProfileId error", e);
			}
		}
		if (logger.isTraceEnabled()) {
			logger.trace("getResourceInstanceByProfileId end profileId=" + profileId);
		}
		return intanceIds;
	}

	@Override
	public List<Long> getResourceInstanceByProfileIds(List<Long> profileIds) throws ProfilelibException {
		if (logger.isTraceEnabled()) {
			logger.trace("getResourceInstanceByProfileId start profileIds=" + profileIds);
		}
		List<Long> intanceIds = null;
		try {
			List<ProfileInstRelationPO> profileInstRelationPOs = profileInstRelDAO.getInstRelationsByProfileIds(profileIds);
			if (profileInstRelationPOs != null && !profileInstRelationPOs.isEmpty()) {
				intanceIds = new ArrayList<>();
				for (ProfileInstRelationPO profileInstRelationPO : profileInstRelationPOs) {
					intanceIds.add(profileInstRelationPO.getInstanceId());
				}
			}
		} catch (Exception e) {
			if (logger.isErrorEnabled()) {
				logger.error("getResourceInstanceByProfileId error", e);
			}
		}
		if (logger.isTraceEnabled()) {
			logger.trace("getResourceInstanceByProfileId end profileIds=" + profileIds);
		}
		return intanceIds;
	}

	@Override
	public List<ProfileThreshold> getThresholdByInstanceIdAndMetricId(long instanceId, String metricId) throws ProfilelibException {
		if (logger.isTraceEnabled()) {
			StringBuilder b = new StringBuilder(50);
			b.append("getThresholdByInstanceIdAndMetricId start instanceId=");
			b.append(instanceId).append("metricId=").append(metricId);
			logger.trace(b);
		}
		ProfileInstRelationPO profileInstRelationPO = null;
		try {
			profileInstRelationPO = profileInstRelDAO.getInstRelationByInstId(instanceId);
		} catch (Exception e) {
			if (logger.isErrorEnabled()) {
				logger.error("", e);
			}
		}
		if (profileInstRelationPO != null) {
			return getThresholdByProfileIdAndMetricId(profileInstRelationPO.getProfileId(), metricId);
		}
		if (logger.isTraceEnabled()) {
			StringBuilder b = new StringBuilder(50);
			b.append("getThresholdByInstanceIdAndMetricId start instanceId=");
			b.append(instanceId).append("metricId=").append(metricId);
			logger.trace(b);
		}
		return null;
	}

	@Override
	public List<ProfileThreshold> getThresholdByProfileIdAndMetricId(long profileId, String metricId) throws ProfilelibException {
		if (logger.isTraceEnabled()) {
			StringBuilder b = new StringBuilder(50);
			b.append("getThresholdByProfileIdAndMetricId start profileId=");
			b.append(profileId).append(" metricId=").append(metricId);
			logger.trace(b);
		}
		List<ProfileThresholdPO> profileThresholdPOs = null;
		List<ProfileThreshold> profileThresholds = null;
		try {
			profileThresholds = profileCache.getProfileThresholdBymetricId(profileId, metricId);
			if (profileThresholds != null) {
				if (logger.isTraceEnabled()) {
					StringBuilder b = new StringBuilder(50);
					b.append("cache getThresholdByProfileIdAndMetricId end profileId=");
					b.append(profileId).append(" metricId=").append(metricId);
					logger.trace(b);
				}
				return profileThresholds;
			}
			profileThresholdPOs = profileThresholdDAO.getThresholdByProfileIdAndMetricId(profileId, metricId);
		} catch (Exception e) {
			if (logger.isErrorEnabled()) {
				logger.error("", e);
			}
		}
		if (profileThresholdPOs != null) {
			profileThresholds = transProfileThresholdPO2BOs(profileThresholdPOs);
			// add to cache
			profileCache.addProfileThreshold(profileId, metricId, profileThresholds);
		}else{
			//没有值默认放一个空集合，方便状态计算调用
			profileCache.addProfileThreshold(profileId, metricId, new ArrayList<ProfileThreshold>(1));
		}
		if (logger.isTraceEnabled()) {
			StringBuilder b = new StringBuilder(50);
			b.append("getThresholdByProfileIdAndMetricId end profileId=");
			b.append(profileId).append(" metricId=").append(metricId);
			logger.trace(b);
		}
		return profileThresholds;
	}

	@Override
	public void operateProfileInstance(long profileId, List<Long> addInstances, List<Long> deleteInstances) throws ProfilelibException {
		if (logger.isTraceEnabled()) {
			logger.trace("operateProfileInstance start");
		}
		if (addInstances == null || deleteInstances == null) {
			throw new ProfilelibException(122, "param addInstances or deleteInstances is null");
		}
		if (!addInstances.isEmpty()) {
			addProfileInstance(profileId, addInstances);
		}
		if (!deleteInstances.isEmpty()) {
			removeInstancesFromProfile(profileId, deleteInstances);
		}
		if (logger.isTraceEnabled()) {
			logger.trace("operateProfileInstance end");
		}
	}

	@Override
	public void removeInstancesFromProfile(long profileId, List<Long> instanceIds) throws ProfilelibException {
		if (logger.isInfoEnabled()) {
			StringBuilder b = new StringBuilder(50);
			b.append("removeInstancesFromProfile start profileId=").append(profileId);
			b.append(" instanceId=").append(instanceIds);
			logger.info(b);
		}
		if (instanceIds == null || instanceIds.isEmpty()) {
			if (logger.isWarnEnabled()) {
				logger.warn("param instanceIds is null or empty.");
			}
			if (logger.isTraceEnabled()) {
				logger.trace("removeInstancesFromProfile end");
			}
			return;
		}
		List<Long> allInstanceIds = new ArrayList<Long>(instanceIds.size());
		try {
			Map<Long, InstanceLifeStateEnum> instStateEnumMap = new HashMap<Long, InstanceLifeStateEnum>();
			ProfileInfoPO profileInfoPO = profileDAO.getProfilePoById(profileId);
			if (profileInfoPO.getParentProfileId() == null || profileInfoPO.getParentProfileId() == 0) {
				// 如果是个性化策略，传进来的参数只有一个
//				if (ProfileTypeEnum.PERSONALIZE.toString().equals(profileInfoPO.getProfileType())) {
//					profileInfoPO.setIsUse("0");
//					profileDAO.updateProfile(profileInfoPO);
//				}
				// 父策略，查出所有父实例对应的监控子实例
				List<ProfileInstRelationPO> profileInstRelationPOs = profileInstRelDAO.getInstRelationByParentInstIds(instanceIds);
				if (profileInstRelationPOs != null && !profileInstRelationPOs.isEmpty()) {
					for (ProfileInstRelationPO profileInstRelationPO : profileInstRelationPOs) {
						allInstanceIds.add(profileInstRelationPO.getInstanceId());
						instStateEnumMap.put(profileInstRelationPO.getInstanceId(), InstanceLifeStateEnum.NOT_MONITORED);
					}
				}
			}
			for (long subInstanceId : instanceIds) {
				allInstanceIds.add(subInstanceId);
				instStateEnumMap.put(subInstanceId, InstanceLifeStateEnum.NOT_MONITORED);
			}
			profileInstRelDAO.removeInstRelByInstIds(allInstanceIds);
			try {
				resourceInstanceService.updateResourceInstanceState(instStateEnumMap);
			} catch (Exception e) {
				if (logger.isErrorEnabled()) {
					logger.error("", e);
				}
			}
			// 同步到采集器数据入库
			cancelMonitorToCollector(allInstanceIds);
			//资源取消监控通知计算状态
			Map<Long,List<Long>> noticeData = new HashMap<Long, List<Long>>(1);
			noticeData.put(profileId,instanceIds);
			cancelMonitorNotice(noticeData);
		} catch (Exception e) {
			if (logger.isErrorEnabled()) {
				logger.error("removeInstRelByInstIds error", e);
			}
			throw new ProfilelibException(ServerErrorCodeConstant.ERR_SERVER_OPERATE_DATABASE_ERROR, "removeInstancesFromProfile error");
		}
		if (logger.isInfoEnabled()) {
			StringBuilder b = new StringBuilder(50);
			b.append("removeInstancesFromProfile end profileId=").append(profileId);
			b.append(" instanceId=").append(instanceIds);
			logger.info(b);
		}
	}

	@Override
	public void removeProfileById(long profileId) throws ProfilelibException {
		if (logger.isInfoEnabled()) {
			logger.info("removeProfileById start profileId=" + profileId);
		}
		// 查看profileId 是否是 默认的策略
		ProfileInfoPO profileInfoPO = null;
		try {
			profileInfoPO = profileDAO.getProfilePoById(profileId);
		} catch (Exception e1) {
			if (logger.isErrorEnabled()) {
				logger.error("removeProfileById getProfilePoById error!", e1);
			}
		}
		if (profileInfoPO == null) {
			if (logger.isInfoEnabled()) {
				logger.info("profile is null ,removeProfileById method end");
			}
			return;
		}
		if (ProfileTypeEnum.DEFAULT.toString().equals(profileInfoPO.getProfileType())) {
			if (logger.isErrorEnabled()) {
				logger.error("Can't delete default profile,profileId=" + profileId);
			}
			throw new ProfilelibException(999, "Can't delete default profile");
		}
		Map<Long,List<Long>> noticeData = new HashMap<Long, List<Long>>();
		List<Long> cancelInstanceIds = new ArrayList<Long>();
		try {
			List<Long> profileIds = new ArrayList<>();
			profileIds.add(profileId);
			ProfileInfoPO queryPO = new ProfileInfoPO();
			queryPO.setParentProfileId(profileId);
			List<ProfileInfoPO> children = profileDAO.getProfileInfoPO(queryPO);
			if (CollectionUtils.isNotEmpty(children)) {
				for (ProfileInfoPO profileInfoPO2 : children) {
					profileIds.add(profileInfoPO2.getProfileId());
				}
			}
			List<ProfileInstRelationPO> profileInstRelationPOs = profileInstRelDAO.getInstRelationsByProfileIds(profileIds);
			for (ProfileInstRelationPO profileInstRelationPO : profileInstRelationPOs) {
				if(profileId == profileInstRelationPO.getProfileId()){
					cancelInstanceIds.add(profileInstRelationPO.getInstanceId());
				}
			}
			profileDAO.removeProfileByProfileIds(profileIds);
			lastProfileDAO.removeLastProfilesByProfileIds(profileIds);
			profileMetricDAO.removeMetricByProfileIds(profileIds);
			profileThresholdDAO.removeThresholdByProfileIds(profileIds);
			profileInstRelDAO.removeInstRelByProfileIds(profileIds);
			List<TimelineInfo> timelines = timelineService.getTimelineInfosByProfileId(profileId);
			timelineService.removeTimelineByProfileId(profileId);
			// 同步到采集器数据到数据库,更新资源实例状态
			deleteProfileToCollector(profileInstRelationPOs);
			// 删除缓存
			for (long id : profileIds) {
				removeCacheByProfileId(id, timelines);
			}
			if(!cancelInstanceIds.isEmpty()){
				noticeData.put(profileId, cancelInstanceIds);
				cancelMonitorNotice(noticeData);
			}
		} catch (Exception e) {
			if (logger.isErrorEnabled()) {
				logger.error("delete profile error", e);
			}
		} 
		if (logger.isInfoEnabled()) {
			logger.info("removeProfileById end profileId=" + profileId);
		}
	}

	@Override
	public void removeProfileByIds(List<Long> profileIds) throws ProfilelibException {
		if (logger.isInfoEnabled()) {
			logger.info("removeProfileByIds start profileIds=" + profileIds);
		}
		for (long profileId : profileIds) {
			// 查看profileId 是否是 默认的策略
			ProfileInfoPO profileInfoPO = null;
			try {
				profileInfoPO = profileDAO.getProfilePoById(profileId);
			} catch (Exception e) {
				if (logger.isErrorEnabled()) {
					logger.error("", e);
				}
			}
			if (profileInfoPO == null) {
				continue;
			}
			if (ProfileTypeEnum.DEFAULT.toString().equals(profileInfoPO.getProfileType())) {
				throw new ProfilelibException(999, "Can't delete default profile");
			}
		}
		Map<Long,List<Long>> noticeData = new HashMap<Long, List<Long>>();
		try {
			List<Long> deleteProfileIds = new ArrayList<>();
			// 循环查找子策略，批量删除
			for (long profileId : profileIds) {
				deleteProfileIds.add(profileId);
				ProfileInfoPO queryPO = new ProfileInfoPO();
				queryPO.setParentProfileId(profileId);
				List<ProfileInfoPO> children = profileDAO.getProfileInfoPO(queryPO);
				if (children != null && !children.isEmpty()) {
					for (ProfileInfoPO profileInfoPO2 : children) {
						deleteProfileIds.add(profileInfoPO2.getProfileId());
					}
				}
				noticeData.put(profileId, null);
			}
			List<ProfileInstRelationPO> profileInstRelationPOs = profileInstRelDAO.getInstRelationsByProfileIds(deleteProfileIds);
			for(ProfileInstRelationPO p : profileInstRelationPOs){
				if(noticeData.containsKey(p.getProfileId())){
					List<Long> tempList= noticeData.get(p.getProfileId());
					if(tempList == null){
						tempList = new ArrayList<Long>();
						noticeData.put(p.getProfileId(), tempList);
					}
					tempList.add(p.getInstanceId());
				}
			}
			profileDAO.removeProfileByProfileIds(deleteProfileIds);
			lastProfileDAO.removeLastProfilesByProfileIds(deleteProfileIds);
			profileMetricDAO.removeMetricByProfileIds(deleteProfileIds);
			profileThresholdDAO.removeThresholdByProfileIds(deleteProfileIds);
			profileInstRelDAO.removeInstRelByProfileIds(deleteProfileIds);
			// 需要更新资源实例的状态
			List<TimelineInfo> timelines = new ArrayList<TimelineInfo>();
			for (long profileId : profileIds) {
				List<TimelineInfo> info = timelineService.getTimelineInfosByProfileId(profileId);
				if (info != null) {
					timelines.addAll(info);
				}
				timelineService.removeTimelineByProfileId(profileId);
			}
			// 同步到采集器数据到数据库,更新资源实例状态
			deleteProfileToCollector(profileInstRelationPOs);
			// 删除缓存
			for (long id : profileIds) {
				removeCacheByProfileId(id, timelines);
			}
			//取消实例监控通知
			cancelMonitorNotice(noticeData);
		} catch (Exception e) {
			if (logger.isErrorEnabled()) {
				logger.error("removeSpecialProfileByIds error!", e);
			}
			throw new ProfilelibException(ServerErrorCodeConstant.ERR_SERVER_OPERATE_DATABASE_ERROR, "removeProfileByIds error.");
		}
		if (logger.isInfoEnabled()) {
			logger.info("removeSpecialProfileByIds end");
		}
	}

	@Override
	public void updateDefaultProfileStateByResourceId(List<String> resourceIds, boolean isUse) {
		if (logger.isTraceEnabled()) {
			logger.trace("updateDefaultProfileStateByResourceId start");
		}
		try {
			profileDAO.updateProfileStateByResourceIds(resourceIds, isUse ? "1" : "0");
		} catch (Exception e) {
			if (logger.isErrorEnabled()) {
				logger.error("updateDefaultProfileStateByResourceId error!", e);
			}
		}
		if (logger.isTraceEnabled()) {
			logger.trace("updateDefaultProfileStateByResourceId end");
		}
	}

	@Override
	public void deleteInstances(List<ResourceInstance> instances) throws ProfilelibException {
		List<Long> allInstanceIds = null;
		List<Long> instanceIds = null;
		if(instances!=null && instances.size()>0){
			allInstanceIds = new ArrayList<Long>();
			instanceIds = new ArrayList<Long>(instances.size());
			for (ResourceInstance resourceInstance : instances) {
				allInstanceIds.add(resourceInstance.getId());
				if(!CapacityConst.SNMPOTHERS.equals(resourceInstance.getResourceId())){
					instanceIds.add(resourceInstance.getId());
				}
			}
		}
		if(allInstanceIds==null || allInstanceIds.isEmpty()){
			if(logger.isInfoEnabled()){
				logger.info("deleteInstances instanceid is empty!");
			}
			return;
		}
		if (logger.isTraceEnabled()) {
			logger.trace("deleteInstances start  parentInstanceId=" + allInstanceIds);
		}
		List<Long> removeIds = new ArrayList<>();
		List<ProfileInstRelationPO> profileInstRelationPOs = null;
		try {
			lastProfileDAO.removeLastProfilesByParentIds(instanceIds);
			if(instanceIds!=null && !instanceIds.isEmpty()){
				profileInstRelationPOs = profileInstRelDAO.getInstRelationByParentInstIds(instanceIds);
				if(profileInstRelationPOs!=null){
					for (ProfileInstRelationPO profileInstRelationPO : profileInstRelationPOs) {
						removeIds.add(profileInstRelationPO.getInstanceId());
					}
					lastProfileDAO.insertLastProfiles(profileInstRelationPOs);
				}
			}
			profileInstRelDAO.removeInstRelByparentInstIds(allInstanceIds);
		} catch (Exception e) {
			if (logger.isErrorEnabled()) {
				logger.error("deleteInstances error!", e);
			}
		}
		// 同步到采集器
		cancelMonitorToCollector(removeIds);
		if (logger.isTraceEnabled()) {
			logger.trace("deleteInstances end");
		}
	}

	@Override
	public void deleteProfileByResourceIdAndInstanceId(List<Long> instanceIds, HashSet<String> resourceIds) throws ProfilelibException {
		if (logger.isInfoEnabled()) {
			StringBuilder b = new StringBuilder(100);
			b.append("deleteProfileByResourceIdAndInstanceId start");
			b.append("\n instanceIds=").append(instanceIds);
			b.append("\n resourceIds=").append(resourceIds);
			logger.info(b);
		}
		List<Long> removeIds = new ArrayList<>();
		List<ProfileInstRelationPO> profileInstRelationPOs = null;
		try {
			profileInstRelationPOs = profileInstRelDAO.getInstRelationByParentInstIds(instanceIds);
			if (profileInstRelationPOs != null) {
				for (ProfileInstRelationPO profileInstRelationPO : profileInstRelationPOs) {
					removeIds.add(profileInstRelationPO.getInstanceId());
				}
				if (!removeIds.isEmpty()) {
					// 移除所有父资源，子资源监控
					profileInstRelDAO.removeInstRelByInstIds(removeIds);
					lastProfileDAO.removeLastProfileByInstanceIds(removeIds);
				}
			}
			// 通过模型删除策略相关的信息
			List<ProfileInfoPO> profileList = profileDAO.getProfileBasicInfoByResourceIds(new ArrayList<>(resourceIds));
			if (profileList != null) {
				List<Long> profileIds = new ArrayList<Long>();
				for (ProfileInfoPO profileInfoPO : profileList) {
					profileIds.add(profileInfoPO.getProfileId());
				}
				if (!profileIds.isEmpty()) {
					profileDAO.removeProfileByProfileIds(profileIds);
					profileMetricDAO.removeMetricByProfileIds(profileIds);
					profileThresholdDAO.removeThresholdByProfileIds(profileIds);
				}
			}
		} catch (Exception e) {
			if (logger.isErrorEnabled()) {
				logger.error("deleteInstances error!", e);
			}
		}
		if (logger.isInfoEnabled()) {
			logger.info("deleteProfileByResourceIdAndInstanceId end");
		}
	}

	@Override
	public void updateProfileBaiscInfo(ProfileInfo profileInfo) throws ProfilelibException {
		if (logger.isTraceEnabled()) {
			logger.trace("updateProfileBaiscInfo start");
		}
		profileInfo.setUpdateTime(new Date());
		ProfileInfoPO profileInfoPO = getInfoPO(profileInfo.getProfileId(), profileInfo, 0);
		try {
			profileDAO.updateProfile(profileInfoPO);
		} catch (Exception e) {
			if (logger.isErrorEnabled()) {
				logger.error("updateProfileBaiscInfo error!", e);
			}
		}
		if (logger.isTraceEnabled()) {
			logger.trace("updateProfileBaiscInfo end");
		}
	}

	@Override
	public void updateProfileMetricAlarm(long profileId, Map<String, Boolean> alarms) throws ProfilelibException {
		if (logger.isTraceEnabled()) {
			logger.trace("updateProfileMetricMonitor start");
		}
		List<ProfileMetric> profileMetrics = new ArrayList<ProfileMetric>(alarms.size());
		List<ProfileChangeData> profileMetricChangeDatas = new ArrayList<ProfileChangeData>(alarms.size());
		List<ProfileMetricPO> updateProfileMetricPOs = new ArrayList<ProfileMetricPO>(alarms.size());
		for (Entry<String, Boolean> item : alarms.entrySet()) {
			ProfileMetricPO profileMetricPO = new ProfileMetricPO();
			profileMetricPO.setIsAlarm(item.getValue() == true ? "1" : "0");
			profileMetricPO.setMetricId(item.getKey());
			profileMetricPO.setProfileId(profileId);
			updateProfileMetricPOs.add(profileMetricPO);

			ProfileChangeData profileMetricChangeData = new ProfileChangeData();
			profileMetricChangeData.setIsAlarm(item.getValue() == true ? true : false);
			profileMetricChangeData.setProfileId(profileId);
			profileMetricChangeData.setMetricId(item.getKey());
			profileMetricChangeDatas.add(profileMetricChangeData);

			ProfileMetric metric = profileCache.getProfileMetricBymetricId(profileId, item.getKey());
			if (metric != null) {
				profileMetrics.add(metric);
			}
		}

		try {
			profileMetricDAO.updateProfileMetrics(updateProfileMetricPOs);
		} catch (Exception e) {
			if (logger.isErrorEnabled()) {
				logger.error("updateProfileMetricMonitor error", e);
			}
			throw new ProfilelibException(54, "");
		}
		// 更新缓存
		for (ProfileMetric profileMetric : profileMetrics) {
			boolean isAlarm = alarms.get(profileMetric.getMetricId());
			profileMetric.setAlarm(isAlarm);
			profileCache.updateProfileMetric(profileMetric);
		}
		// 通知
		profileMetricAlarmManager.doMetricChangeInterceptor(profileMetricChangeDatas);
		if (logger.isTraceEnabled()) {
			logger.trace("updateProfileMetricMonitor end");
		}
	}

	@Override
	public void updateProfileMetricFrequency(long profileId, Map<String, String> frequencyValue) throws ProfilelibException {
		if (logger.isTraceEnabled()) {
			logger.trace("updateProfileMetricFrequency end");
		}

		List<ProfileMetricPO> updateProfileMetricPOs = new ArrayList<ProfileMetricPO>();

		List<ProfileChangePO> profileChangePOs = new ArrayList<>();

		List<ProfileMetric> profileMetrics = new ArrayList<ProfileMetric>(frequencyValue.size());

		Date date = new Date();
		try {
			for (Entry<String, String> item : frequencyValue.entrySet()) {
				if(FrequentEnum.valueOf(item.getValue()) != null){
					ProfileMetricPO profileMetricPO = new ProfileMetricPO();
					profileMetricPO.setDictFrequencyId(item.getValue());
					profileMetricPO.setMetricId(item.getKey());
					profileMetricPO.setProfileId(profileId);
					updateProfileMetricPOs.add(profileMetricPO);

					ProfileChangePO profileChangePO = new ProfileChangePO();
					long profileChangeId = ocProfileChangeSequence.next();
					profileChangePO.setProfileChangeId(profileChangeId);
					profileChangePO.setProfileId(profileId);
					profileChangePO.setOperateState(0);
					profileChangePO.setChangeTime(date);
					profileChangePO.setOperateMode(ProfileChangeEnum.UPDATE_METRIC_MONITORFEQ.toString());
					profileChangePO.setSource(item.getKey());
					profileChangePOs.add(profileChangePO);

					ProfileMetric metric = profileCache.getProfileMetricBymetricId(profileId, item.getKey());
					if (metric != null) {
						profileMetrics.add(metric);
					}
				}
			}
		} catch (Exception e) {
			if (logger.isErrorEnabled()) {
				logger.error("updateProfileMetrics error", e);
			}
			throw new ProfilelibException(ServerErrorCodeConstant.ERR_SERVER_OPERATE_DATABASE_ERROR, "updateProfileMetricFrequency DictFrequency error");
		}
		try {
			profileMetricDAO.updateProfileMetrics(updateProfileMetricPOs);
			// 更新到采集器到数据库
			profileChangeDAO.insertProfileChanges(profileChangePOs);
		} catch (Exception e) {
			if (logger.isErrorEnabled()) {
				logger.error("updateProfileMetrics error", e);
			}
			throw new ProfilelibException(ServerErrorCodeConstant.ERR_SERVER_OPERATE_DATABASE_ERROR, "updateProfileMetricFrequency error");
		}
		// 更新缓存
		for (ProfileMetric profileMetric : profileMetrics) {
			String value = frequencyValue.get(profileMetric.getMetricId());
			profileMetric.setDictFrequencyId(value);
			profileCache.updateProfileMetric(profileMetric);
		}
		if (logger.isTraceEnabled()) {
			logger.trace("updateProfileMetricFrequency end");
		}
	}

	@Override
	public void updateProfileMetricMonitor(long profileId, Map<String, Boolean> monitor) throws ProfilelibException {
		if (logger.isTraceEnabled()) {
			logger.trace("updateProfileMetricMonitor start");
		}

		List<ProfileChangeData> profileMetricChangeDatas = new ArrayList<ProfileChangeData>(monitor.size());
		List<ProfileMetricPO> updateProfileMetricPOs = new ArrayList<ProfileMetricPO>();
		List<ProfileChangePO> profileChangePOs = new ArrayList<>();

		List<ProfileMetric> profileMetrics = new ArrayList<ProfileMetric>(monitor.size());

		Date date = new Date();
		for (Entry<String, Boolean> item : monitor.entrySet()) {
			ProfileMetricPO profileMetricPO = new ProfileMetricPO();
			profileMetricPO.setIsUse(item.getValue() == true ? "1" : "0");
			profileMetricPO.setMetricId(item.getKey());
			profileMetricPO.setProfileId(profileId);
			updateProfileMetricPOs.add(profileMetricPO);

			ProfileChangePO profileChangePO = new ProfileChangePO();
			long profileChangeId = ocProfileChangeSequence.next();
			profileChangePO.setProfileChangeId(profileChangeId);
			profileChangePO.setProfileId(profileId);
			profileChangePO.setOperateState(0);
			profileChangePO.setChangeTime(date);
			profileChangePO.setOperateMode(ProfileChangeEnum.UPDATE_METRIC_MONITOR.toString());
			profileChangePO.setSource(item.getKey());
			profileChangePOs.add(profileChangePO);

			ProfileChangeData profileMetricChangeData = new ProfileChangeData();
			profileMetricChangeData.setIsMonitor(item.getValue() == true ? true : false);
			profileMetricChangeData.setProfileId(profileId);
			profileMetricChangeData.setMetricId(item.getKey());
			profileMetricChangeDatas.add(profileMetricChangeData);

			ProfileMetric metric = profileCache.getProfileMetricBymetricId(profileId, item.getKey());
			if (metric != null) {
				profileMetrics.add(metric);
			}
		}

		try {
			profileMetricDAO.updateProfileMetrics(updateProfileMetricPOs);
			profileChangeDAO.insertProfileChanges(profileChangePOs);
		} catch (Exception e) {
			if (logger.isErrorEnabled()) {
				logger.error("updateProfileMetricMonitor error", e);
			}
			throw new ProfilelibException(ServerErrorCodeConstant.ERR_SERVER_OPERATE_DATABASE_ERROR, "updateProfileMetricMonitor error");
		}
		// 更新缓存
		for (ProfileMetric profileMetric : profileMetrics) {
			boolean isMontior = monitor.get(profileMetric.getMetricId());
			profileMetric.setMonitor(isMontior);
			profileCache.updateProfileMetric(profileMetric);
		}
		// 通知
		profileMetricMonitorManager.doMetricChangeInterceptor(profileMetricChangeDatas);
		if (logger.isTraceEnabled()) {
			logger.trace("updateProfileMetricMonitor end");
		}
	}

	@Override
	public void updateProfileMetricThreshold(long profileId, List<Threshold> thresholds) throws ProfilelibException {
		if (logger.isTraceEnabled()) {
			logger.trace("updateProfileMetricThreshold start");
		}
		List<ProfileThresholdPO> updateProfileThresholds = new ArrayList<>();
		Map<Long, Threshold> updateThreholds = new HashMap<>();
		BigDecimal bigdecimal = null;
		for (Threshold threshold : thresholds) {
			ProfileThresholdPO profileThresholdPO = new ProfileThresholdPO();
			//过滤科学计算法.用正常的数字显示
			if(StringUtils.isNotEmpty(threshold.getThresholdValue())){
				bigdecimal = new BigDecimal(threshold.getThresholdValue());
				String thresholdValue = bigdecimal.toPlainString();
				threshold.setThresholdValue(thresholdValue);
				profileThresholdPO.setThresholdValue(thresholdValue);
			}
			profileThresholdPO.setExpressionDesc(threshold.getThresholdExpression());
			if(StringUtils.isNotEmpty(threshold.getExpressionOperator())){
				profileThresholdPO.setExpressionOperator(threshold.getExpressionOperator());
			}
			profileThresholdPO.setAlarmTemplate(StringUtils.trimToEmpty(threshold.getAlarmTemplate()));
			profileThresholdPO.setMkId(threshold.getThreshold_mkId());
			updateProfileThresholds.add(profileThresholdPO);
			updateThreholds.put(threshold.getThreshold_mkId(), threshold);
		}
		try {
			profileThresholdDAO.updateThresholds(updateProfileThresholds);
		} catch (Exception e) {
			if (logger.isErrorEnabled()) {
				logger.error("updateProfileMetricThreshold error", e);
			}
			throw new ProfilelibException(23543, "");
		}
		// 修改缓存
		HashSet<String> metricIds = profileCache.getMetricIdByProfileId(profileId);
		if (metricIds != null) {
			// key: metricId
			for (String metricId : metricIds) {
				List<ProfileThreshold> profileThresholds = profileCache.getProfileThresholdBymetricId(profileId, metricId);
				boolean isUpdate = false;
				if (profileThresholds != null) {
					for (ProfileThreshold profileThreshold : profileThresholds) {
						Threshold threshold = updateThreholds.get(profileThreshold.getThreshold_mkId());
						if (threshold != null) {
							profileThreshold.setThresholdValue(threshold.getThresholdValue());
							profileThreshold.setAlarmTemplate(StringUtils.trimToNull(threshold.getAlarmTemplate()));
							profileThreshold.setThresholdExpression(StringUtils.trimToNull(threshold.getThresholdExpression()));
							isUpdate = true;
						}
					}
				}
				if (isUpdate) {
					profileCache.updateProfileThreshold(profileId, metricId, profileThresholds);
				}
			}
		}
		if (logger.isTraceEnabled()) {
			logger.trace("updateProfileMetricThreshold end");
		}
	}

	@Override
	public void updateProfileMetricflapping(long profileId, Map<String, Integer> flappings) throws ProfilelibException {
		if (logger.isTraceEnabled()) {
			logger.trace("updateProfileMetricflapping start");
		}
		List<ProfileMetric> profileMetrics = new ArrayList<ProfileMetric>(flappings.size());
		List<ProfileMetricPO> updateProfileMetricPOs = new ArrayList<ProfileMetricPO>();
		for (Entry<String, Integer> item : flappings.entrySet()) {
			ProfileMetricPO profileMetricPO = new ProfileMetricPO();
			profileMetricPO.setAlarmRepeat(item.getValue());
			profileMetricPO.setMetricId(item.getKey());
			profileMetricPO.setProfileId(profileId);
			updateProfileMetricPOs.add(profileMetricPO);
			ProfileMetric metric = profileCache.getProfileMetricBymetricId(profileId, item.getKey());
			if (metric != null) {
				profileMetrics.add(metric);
			}
		}

		try {
			profileMetricDAO.updateProfileMetrics(updateProfileMetricPOs);
		} catch (Exception e) {
			if (logger.isErrorEnabled()) {
				logger.error("updateProfileMetricMonitor error", e);
			}
			throw new ProfilelibException(ServerErrorCodeConstant.ERR_SERVER_OPERATE_DATABASE_ERROR, "updateProfileMetricMonitor");
		}
		// 更新缓存
		for (ProfileMetric profileMetric : profileMetrics) {
			int flapping = flappings.get(profileMetric.getMetricId());
			profileMetric.setAlarmFlapping(flapping);
			profileCache.updateProfileMetric(profileMetric);
		}
		if (logger.isTraceEnabled()) {
			logger.trace("updateProfileMetricflapping end");
		}
	}

	/**
	 * 根据mainInstId 找到表stm_profilelib_inst,如果找到数据说明已经加入监控
	 * 
	 * @param mainInstId
	 * @return
	 */
	private long checkMonitorDefaultProfileIdByInstId(long mainInstId) {
		long profileId = 0;
		ProfileInstRelationPO profileInstRelationPO = null;
		try {
			profileInstRelationPO = profileInstRelDAO.getInstRelationByInstId(mainInstId);
			if (profileInstRelationPO != null) {
				ProfileInfoPO profileInfo = profileDAO.getProfilePoById(profileInstRelationPO.getProfileId());
				if (profileInfo != null) {
					if (ProfileTypeEnum.DEFAULT.toString().equals(profileInfo.getProfileType())) {
						profileId = profileInstRelationPO.getProfileId();
					}
				}
			}
		} catch (Exception e) {
			if (logger.isErrorEnabled()) {
				logger.error("", e);
			}
		}
		return profileId;
	}

	private List<ProfileInfoPO> getDefaultProfileByResource(String resourceId) throws ProfilelibException {
		ProfileInfoPO profileInfoPO = new ProfileInfoPO();
		profileInfoPO.setResourceId(resourceId);
		profileInfoPO.setProfileType(ProfileTypeEnum.DEFAULT.toString());
		List<ProfileInfoPO> infos = null;
		try {
			infos = profileDAO.getProfileInfoPO(profileInfoPO);
		} catch (Exception e) {
			if (logger.isErrorEnabled()) {
				logger.error("getDefaultProfileInfo by resource error:", e);
			}
			throw new ProfilelibException(e);
		}
		return infos;
	}

	/**
	 * 批量通过InstanceId 检查 资源是否加入监控，如果策略ID>0则资源已加入监控
	 * 
	 * @param mainInstIds
	 * @return Map<Long, Long> 返回资源实例ID与策略ID的对应关系 key=instnceId，value=profileId
	 */
	private Map<Long, Long> checkMonitorDefaultProfileIdByInstId(List<Long> mainInstIds) {
		Map<Long, Long> result = null;
		if (mainInstIds != null) {
			result = new HashMap<Long, Long>(mainInstIds.size());
			for (Long instanceId : mainInstIds) {
				long profileId = checkMonitorDefaultProfileIdByInstId(instanceId);
				result.put(instanceId, profileId);
			}
		}
		return result;
	}

	private void deleteProfileToCollector(List<ProfileInstRelationPO> profileInstRelationPOs) {
		if (profileInstRelationPOs == null) {
			return;
		}
		List<ProfileChangePO> profileChangePOs = new ArrayList<>();
		Map<Long, InstanceLifeStateEnum> instStateEnumMap = new HashMap<>();
		Date date = new Date();
		for (ProfileInstRelationPO profileInstRelationPO : profileInstRelationPOs) {
			instStateEnumMap.put(profileInstRelationPO.getInstanceId(), InstanceLifeStateEnum.NOT_MONITORED);
			// 记录存放到数据库中
			long profileChangeId = ocProfileChangeSequence.next();
			ProfileChangePO profileChangePO = new ProfileChangePO();
			profileChangePO.setChangeTime(date);
			profileChangePO.setOperateMode(ProfileChangeEnum.DELETE_PROFILE.toString());
			profileChangePO.setOperateState(0);
			profileChangePO.setProfileChangeId(profileChangeId);
			profileChangePO.setSource(String.valueOf(profileInstRelationPO.getInstanceId()));
			profileChangePO.setProfileId(profileInstRelationPO.getProfileId());
			profileChangePOs.add(profileChangePO);
		}
		try {
			resourceInstanceService.updateResourceInstanceState(instStateEnumMap);
			profileChangeDAO.insertProfileChanges(profileChangePOs);
		} catch (Exception e) {
			if (logger.isErrorEnabled()) {
				logger.error("deleteProfileToCollector  error", e);
			}
		}
	}

	

	/**
	 * 过滤网络设备，主机已经宕了接口，不加入到默认策略中 过滤wmi的window主机设备宕服务，不加入到监控
	 */
	private List<ResourceInstance> filterDown(ResourceInstance mainInstance, List<ResourceInstance> subInsts) {
		String parentCategoryId = mainInstance.getParentCategoryId();
		List<ResourceInstance> result = null;
		if (subInsts != null) {
			result = new ArrayList<ResourceInstance>(subInsts.size());
			if (logger.isInfoEnabled()) {
				StringBuilder b = new StringBuilder();
				b.append(" parentInstanceId:").append(mainInstance.getId());
				b.append(" showName=").append(mainInstance.getShowName());
				b.append(" showIp=").append(mainInstance.getShowIP());
				b.append(" parentCategoryId=").append(parentCategoryId);
				logger.info(b);
			}
			for (ResourceInstance resourceInstance : subInsts) {
				if (ResourceOrMetricConst.WMI_HOST_SERVICE_RESOURCEID.equals(resourceInstance.getResourceId()) || CapacityConst.NETWORK_DEVICE.equals(parentCategoryId) || CapacityConst.SNMPOTHERS.equals(parentCategoryId) || CapacityConst.HOST.equals(parentCategoryId)) {
					if (ResourceTypeConsts.TYPE_SERVICE.equals(resourceInstance.getChildType()) || ResourceTypeConsts.TYPE_NETINTERFACE.equals(resourceInstance.getChildType())) {
						String[] data = resourceInstance.getModulePropBykey(ResourceOrMetricConst.RESOURCE_AVAILABILITY);
						StringBuilder b = new StringBuilder(100);
						b.append(" childInstanceId:").append(resourceInstance.getId());
						if (data != null) {
							String isDown = data[0];
							if (isDown != null && SUCCESS_STATE.equals(isDown)) {
								result.add(resourceInstance);
							}
							if (logger.isInfoEnabled()) {
								b.append(" availability metric data = ").append(isDown);
								logger.info(b);
							}
						} else {
							if (logger.isInfoEnabled()) {
								b.append(" availability metric data is null");
								logger.info(b);
							}
						}
					} else {
						// 不是网络接口，主机服务，直接添加
						result.add(resourceInstance);
					}
				} else {
					// 不是网络接口，主机服务，直接添加
					result.add(resourceInstance);
				}
			}
		} else {
			if (logger.isInfoEnabled()) {
				StringBuilder b = new StringBuilder();
				b.append(" parentInstanceId:").append(mainInstance.getId());
				b.append(" showName=").append(mainInstance.getShowName());
				b.append(" showIp=").append(mainInstance.getShowIP());
				b.append(" parentCategoryId=").append(parentCategoryId);
				b.append(" subInsts is null");
				logger.info(b);
			}
		}
		return result;
	}

	/**
	 * @Title: addMonitorDataToDB
	 * @Description: 将加入监控操作记录到数据库 stm_profilelib_change表
	 * @param list
	 * @param deleteLastBind
	 *            void
	 * @throws
	 */
	private void addMonitorDataToDB(List<ProfileInstRelationPO> list, Map<Long, Long> deleteLastBind) {
//		if (logger.isInfoEnabled()) {
//			logger.info("addMonitorDataToDB start, add Monitor Data size:"+list==null?0:list.size()+"\t delete last monitor data size:"+deleteLastBind==null?0:deleteLastBind.size());
//		}
		try {
			// 记录存放到数据库中
			List<ProfileChangePO> profileChangePOs = new ArrayList<>();
			if( deleteLastBind!=null && !deleteLastBind.isEmpty()){
				List<ProfileChangePO> lastProfileChangePOs = reomveLastMonitorToCollector(deleteLastBind);
				if (lastProfileChangePOs != null && !lastProfileChangePOs.isEmpty()) {
					profileChangePOs.addAll(lastProfileChangePOs);
				}
			}
			if(!CollectionUtils.isEmpty(list)){
				for (ProfileInstRelationPO profileInstRelationPO : list) {
					long profileChangeId = ocProfileChangeSequence.next();
					ProfileChangePO profileChangePO = new ProfileChangePO();
					profileChangePO.setChangeTime(calendarInstance.getTime());
					profileChangePO.setOperateMode(ProfileChangeEnum.ADD_MONITOR.toString());
					profileChangePO.setOperateState(0);
					profileChangePO.setProfileChangeId(profileChangeId);
					profileChangePO.setSource(String.valueOf(profileInstRelationPO.getInstanceId()));
					profileChangePO.setProfileId(profileInstRelationPO.getProfileId());
					profileChangePOs.add(profileChangePO);
				}
			}
			if (CollectionUtils.isEmpty(profileChangePOs)) {
				return;
			}
			if (!profileChangePOs.isEmpty()) {
				profileChangeDAO.insertProfileChanges(profileChangePOs);
			}
		} catch (Exception e) {
			if (logger.isErrorEnabled()) {
				logger.error("addMonitorDataToDB error", e);
			}
		}
	}

	private ProfileInstanceRelation transProfileInstRelationPO2BOs(List<ProfileInstRelationPO> relationsPOs) {
		ProfileInstanceRelation relation = new ProfileInstanceRelation();
		List<Instance> instances = new ArrayList<>(relationsPOs.size());
		for (ProfileInstRelationPO relationsPO : relationsPOs) {
			Instance instance = new Instance();
			instance.setInstanceId(relationsPO.getInstanceId());
			if (relationsPO.getParentInstanceId() != null) {
				instance.setParentInstanceId(relationsPO.getParentInstanceId());
			}
			instances.add(instance);
		}
		relation.setInstances(instances);
		return relation;
	}
	
	private ProfileInfoPO transProfileInfo(long profileId, ProfileInfo bo) {
		ProfileInfoPO po = new ProfileInfoPO();
		po.setProfileDesc(bo.getProfileDesc());
		po.setProfileId(profileId);
		po.setProfileName(bo.getProfileName());
		po.setProfileType(bo.getProfileType().toString());
		po.setResourceId(bo.getResourceId());
		po.setUpdateTime(bo.getUpdateTime());
		po.setUpdateUser(bo.getUpdateUser());
		po.setUpdateUserDomain(bo.getUpdateUserDomain());
		po.setDomainId(bo.getDomainId());
		po.setCreateUser(bo.getCreateUser());
		return po;
	}
	
	private ProfileInfo transProfileInfoPO2BO(ProfileInfoPO po) {
		ProfileInfo bo = new ProfileInfo();
		bo.setProfileDesc(po.getProfileDesc());
		bo.setProfileId(po.getProfileId());
		bo.setProfileName(po.getProfileName());
		bo.setProfileType(ProfileTypeEnum.valueOf(po.getProfileType()));
		bo.setResourceId(po.getResourceId());
		if (po.getUpdateTime() != null) {
			bo.setUpdateTime(po.getUpdateTime());
		}
		bo.setUpdateUser(po.getUpdateUser());
		bo.setUpdateUserDomain(po.getUpdateUserDomain());
		bo.setDomainId(po.getDomainId());
		bo.setUse("1".equals(po.getIsUse()) ? true : false);
		bo.setParentProfileId(po.getParentProfileId() == null ? 0 : po.getParentProfileId());
		bo.setPersonalize_instanceId(po.getResourceInstanceId());
		bo.setCreateUser(po.getCreateUser());
		return bo;
	}

	/**
	 * @Title: reomveLastMonitorToCollector
	 * @Description: 将取消监控操作记录到数据库 stm_profilelib_change表
	 * @param instances
	 * @return List<ProfileChangePO>
	 * @throws
	 */
	private List<ProfileChangePO> reomveLastMonitorToCollector(Map<Long, Long> instances) {
		if (instances == null || instances.isEmpty()) {
			if (logger.isDebugEnabled()) {
				logger.debug("reomveLastMonitorToCollector empty");
			}
			return null;
		}
		List<ProfileChangePO> profileChangePOs = new ArrayList<>();
		try {
			// 记录存放到数据库中
			for (Entry<Long, Long> item : instances.entrySet()) {
				long profileChangeId = ocProfileChangeSequence.next();
				ProfileChangePO profileChangePO = new ProfileChangePO();
				profileChangePO.setChangeTime(calendarInstance.getTime());
				profileChangePO.setOperateMode(ProfileChangeEnum.CANCEL_LAST_MONITOR.toString());
				profileChangePO.setOperateState(0);
				profileChangePO.setProfileChangeId(profileChangeId);
				profileChangePO.setSource(String.valueOf(item.getKey()));
				//profileChangePO.setProfileId(item.getValue());
				profileChangePOs.add(profileChangePO);
			}
			if (logger.isDebugEnabled()) {
				logger.debug("reomveLastMonitor instanceId=" + instances);
			}
		} catch (Exception e) {
			if (logger.isErrorEnabled()) {
				logger.error("reomveLastMonitor error! instanceId=" + instances, e);
			}
		}
		return profileChangePOs;
	}

	/**
	 * 添加实例到默认的策略 可以用一个统一的方法，不用区分父与子。在之前需要用组分开实例。 一个类型的模型只会创建一次，实例有可能有多个
	 * 
	 * @param mainResourceInstance
	 * @param subInstances
	 * @return
	 * @throws ProfilelibException
	 */
	private TempResult monitorInstanceDefault(ResourceInstance mainResourceInstance, List<ResourceInstance> subInstances, List<ProfileInstRelationPO> syncTOcollector, HashMap<Long, Long> deleted) throws ProfilelibException {

		String mainResourceId = mainResourceInstance.getResourceId();
		long newMainProfileId = 0;
		TempResult result = new TempResult();
	
		/**
		 * 通过主资源实例ResourceId在数据库查询默认策略，检查策略是否存在
		 */
		List<ProfileInfoPO> defaultMainProfiles = null;
		try {
			defaultMainProfiles = getDefaultProfileByResource(mainResourceId);
		} catch (Exception e1) {
			if (logger.isErrorEnabled()) {
				logger.error("query default profile error:", e1);
			}
			throw new ProfilelibException(ServerErrorCodeConstant.ERR_SERVER_OPERATE_DATABASE_ERROR, "query default profile error");
		}

		// 获取到的主资源对应的策略信息
		ProfileInfoPO selectProfileInfoPO = null;

		/**
		 * 检查主资源是否有主策略
		 * */
		boolean flag = false;
		if (defaultMainProfiles != null && !defaultMainProfiles.isEmpty()) {
			for (ProfileInfoPO profileInfoPO : defaultMainProfiles) {
				if (profileInfoPO.getDomainId() == 0) {
					selectProfileInfoPO = profileInfoPO;
					flag = true;
					break;
				}
			}
		}
		ProfileSwitchConvert convert = null;
		if (flag) {
			try {
				//有生成默认策略，属于策略切换
				boolean switchMonitorNotice = false;
				ProfileSwitchRelation relations = switchRemoveProfileInstanceRelByResource(mainResourceInstance.getId());
				if(relations.getBeforeDeletingAllRelation() != null){
					deleted.putAll(relations.getBeforeDeletingAllRelation());
				}
				Long lastParentProfileId = deleted.get(mainResourceInstance.getId());
				//用于临时存放子资源模型绑定的资源，方便通知使用
				HashMap<String,List<Long>> childMonitor = new HashMap<String, List<Long>>();
				//本次策略跟上次策略不一致需要通知-策略切换通知
				if(lastParentProfileId != null){
					if( lastParentProfileId.longValue() != selectProfileInfoPO.getProfileId()){
						switchMonitorNotice = true;
					}
				} else {
					lastParentProfileId = 0L;
				}
				List<ProfileInstRelationPO> profileInstRelationPOs = new ArrayList<ProfileInstRelationPO>();
				ProfileInstRelationPO parentProfileInstRelationPO = new ProfileInstRelationPO();
				parentProfileInstRelationPO.setInstanceId(mainResourceInstance.getId());
				parentProfileInstRelationPO.setProfileId(selectProfileInfoPO.getProfileId());
				profileInstRelationPOs.add(parentProfileInstRelationPO);
				Map<Long,InstanceLifeStateEnum> updatNotMonitored = new HashMap<Long, InstanceLifeStateEnum>();
				
				// 查找子策略信息
				ProfileInfoPO queryProfileInfoPO = new ProfileInfoPO();
				queryProfileInfoPO.setParentProfileId(selectProfileInfoPO.getProfileId());
				List<ProfileInfoPO> subProfilePOs = null;
				try {
					subProfilePOs = profileDAO.getProfileInfoPO(queryProfileInfoPO);
				} catch (Exception e1) {
					if (logger.isErrorEnabled()) {
						logger.error("getProfileInfoPO error!", e1);
					}
				}
				//如果子策略为空，通过模型创建子策略
				if(CollectionUtils.isEmpty(subProfilePOs)){
					subProfilePOs = new ArrayList<ProfileInfoPO>(5);
				}
				HashSet<String> profileResourceIds = new HashSet<String>();
				//判断发现的子模型是否都存在策略，如果不存在则创建策略
				for (ProfileInfoPO subProfile : subProfilePOs) {
					profileResourceIds.add(subProfile.getResourceId());
				}
				ResourceDef mainResource = capacityService.getResourceDefById(mainResourceId);
				if(mainResource!=null){
					ResourceDef[] childResources = mainResource.getChildResourceDefs();
					for (ResourceDef childResourceDef : childResources) {
						if(!profileResourceIds.contains(childResourceDef.getId())){
							ProfileInfoPO subProfileInfo = createDefaultChildProfileInfo(selectProfileInfoPO,childResourceDef);
							subProfilePOs.add(subProfileInfo);
						}
					}
				}else{
					if(logger.isErrorEnabled()){
						logger.error("getResourceDefById is null ,resourceId:"+mainResourceId+";profileId:"+selectProfileInfoPO.getProfileId());
					}
					return null;
				}
				if (CollectionUtils.isNotEmpty(subInstances)) {
					for (ResourceInstance resourceInstance : subInstances) {
						String resourceId = resourceInstance.getResourceId();
						if(switchMonitorNotice){
							List<Long> childInstanceIds = childMonitor.get(resourceId);
							if(childInstanceIds == null){
								childInstanceIds = new ArrayList<Long>();
							}
							childInstanceIds.add(resourceInstance.getId());
							childMonitor.put(resourceId, childInstanceIds);
						}
					}
					HashMap<String, Long> resourceAndProfile = new HashMap<>();
					for (ProfileInfoPO profileInfoPO : subProfilePOs) {
						resourceAndProfile.put(profileInfoPO.getResourceId(), profileInfoPO.getProfileId());
					}
					for (ResourceInstance resourceInstance : subInstances) {
//						if(filterMonitor){
//							if(resourceInstance.getLifeState() != InstanceLifeStateEnum.MONITORED){
//								continue;
//							}
//						}
						long tempProfileId = resourceAndProfile.get(resourceInstance.getResourceId());
						ProfileInstRelationPO childProfileInstRelationPO = new ProfileInstRelationPO();
						childProfileInstRelationPO.setInstanceId(resourceInstance.getId());
						childProfileInstRelationPO.setProfileId(tempProfileId);
						childProfileInstRelationPO.setParentInstanceId(mainResourceInstance.getId());
						profileInstRelationPOs.add(childProfileInstRelationPO);
					}
				}
				profileInstRelDAO.insertInstRels(profileInstRelationPOs);
				syncTOcollector.addAll(profileInstRelationPOs);
				if(!updatNotMonitored.isEmpty()){
					try {
						resourceInstanceService.updateResourceInstanceState(updatNotMonitored);
					} catch (Exception e) {
						if (logger.isErrorEnabled()) {
							logger.error("update resource instance state error:", e);
						}
						throw new ProfilelibException(ServerErrorCodeConstant.ERR_SERVER_OPERATE_DATABASE_ERROR, "updateResourceInstanceState error");
					}
				}
				if(switchMonitorNotice){
					//切换策略构造通知
					convert = new ProfileSwitchConvert();
					convert.setChildMonitor(childMonitor);
					convert.setCurrentProfileId(selectProfileInfoPO.getProfileId());
					convert.setLastProfileId(lastParentProfileId);
					convert.setParentInstanceId(mainResourceInstance.getId());
				}
			} catch (Exception e) {
				if (logger.isErrorEnabled()) {
					logger.error("monitorInstanceDefault error", e);
					syncTOcollector = null;
					throw new ProfilelibException(ProfilelibException.CODE_ERROR_VALIDATION + 3, "写入默认策略失败");
				}
			}
		} else {
			ResourceDef mainResource = capacityService.getResourceDefById(mainResourceId);
			// key: profieId+metricId
			try {
				newMainProfileId = ocProfilelibMainSequence.next();
				// 通过模型创建默认策略
				createDefaultSubProfileInfo(newMainProfileId, mainResource);

				// 如果存在这个默认策越，往表stm_profile_inst插入多条数据（主的和子的）
				ProfileInfoPO profileInfoPO = new ProfileInfoPO();
				profileInfoPO.setProfileId(newMainProfileId);
				profileInfoPO.setResourceId(mainResource.getId());
				List<ProfileInstRelationPO> profileInstRelationPOs = insertProfileInstances(profileInfoPO, mainResourceInstance, subInstances);
				syncTOcollector.addAll(profileInstRelationPOs);
			} catch (Exception e) {
				if(logger.isErrorEnabled()){
					logger.error("monitorInstanceDefault in createDefaultSubProfileInfo error:",e);
				}
			}
		}
		result.setProfileId(newMainProfileId);
		result.setProfileSwitchConvert(convert);
		return result;
	}

	/**
	 * @Title: removeProfileInstanceRelByResource
	 * @Description: 通过主资源实例ID删除策略与资源绑定关系
	 * @param parentInstanceId
	 *            主资源实例ID
	 * @return Map<Long,Long> key = instnceId ，Value=profileId
	 * @throws ProfilelibException
	 */
	private HashMap<Long, Long> removeProfileInstanceRelByResource(long parentInstanceId) throws ProfilelibException {
		if (logger.isDebugEnabled()) {
			logger.debug("removeLastProfileBind start parentInstance=" + parentInstanceId);
		}
		HashMap<Long, Long> result = null;
		try {
			List<ProfileInstRelationPO> profileInstRelationPOs = profileInstRelDAO.getInstRelationByParentInstId(parentInstanceId);
			if (profileInstRelationPOs != null) {
				result = new HashMap<Long, Long>(profileInstRelationPOs.size());
				for (ProfileInstRelationPO profileInstRelationPO : profileInstRelationPOs) {
					result.put(profileInstRelationPO.getInstanceId(), profileInstRelationPO.getProfileId());
				}
			}
			profileInstRelDAO.removeInstRelByparentInstId(parentInstanceId);
		} catch (Exception e) {
			if (logger.isErrorEnabled()) {
				logger.error("", e);
			}
			throw new ProfilelibException(5435, "delete last profileInstance error!");
		}
		if (logger.isDebugEnabled()) {
			logger.debug("removeLastProfileBind end parentInstance=" + parentInstanceId);
		}
		return result;
	}
	
	/**
	 * @Title: removeProfileInstanceRelByResource
	 * @Description: 通过主资源实例ID删除策略与资源绑定关系
	 * @param parentInstanceId
	 *            主资源实例ID
	 */
	private ProfileSwitchRelation switchRemoveProfileInstanceRelByResource(long parentInstanceId) throws ProfilelibException {
		if (logger.isDebugEnabled()) {
			logger.debug("switchRemoveProfileInstanceRelByResource start parentInstance=" + parentInstanceId);
		}
		HashMap<Long, Long> all = null;
		HashMap<Long, Long> onlyParent = null;
		try {
			long profileId = 0;
			List<ProfileInstRelationPO> profileInstRelationPOs = profileInstRelDAO.getInstRelationByParentInstId(parentInstanceId);
			if (profileInstRelationPOs != null) {
				all = new HashMap<Long, Long>(profileInstRelationPOs.size());
				for (ProfileInstRelationPO profileInstRelationPO : profileInstRelationPOs) {
					all.put(profileInstRelationPO.getInstanceId(), profileInstRelationPO.getProfileId());
					if(profileInstRelationPO.getInstanceId() == parentInstanceId){
						profileId = profileInstRelationPO.getProfileId();
					}
				}
			}
			profileInstRelDAO.removeInstRelByparentInstId(parentInstanceId);
			if(profileId != 0){
				onlyParent = new HashMap<Long, Long>(1);
				onlyParent.put(parentInstanceId, profileId);
			}
		} catch (Exception e) {
			if (logger.isErrorEnabled()) {
				logger.error("", e);
			}
			throw new ProfilelibException(5435, "delete last profileInstance error!");
		}
		if (logger.isDebugEnabled()) {
			logger.debug("switchRemoveProfileInstanceRelByResource end parentInstance=" + parentInstanceId);
		}
		return new ProfileSwitchRelation(all, onlyParent);
	}

	/**
	 * @Title: removeProfileInstanceRelByResource
	 * @Description: 通过主资源实例ID删除策略与资源绑定关系
	 * @param parentInstanceId
	 *            主资源实例ID
	 * @return Map<Long,Long> key = instnceId ，Value=profileId
	 * @throws ProfilelibException
	 */
	private HashMap<Long, Long> removeProfileInstanceRelByResource(List<Long> parentInstanceIds) throws ProfilelibException {
		if (logger.isInfoEnabled()) {
			logger.info("removeLastProfileBind start parentInstance =" + parentInstanceIds);
		}
		HashMap<Long, Long> result = null;
		HashSet<Long> set = null;
		if(CollectionUtils.isNotEmpty(parentInstanceIds)){
			set = new HashSet<>(parentInstanceIds);
		}
		try {
			//取消监控通知
			Map<Long,List<Long>> noticeData = new HashMap<Long, List<Long>>();
			List<ProfileInstRelationPO> profileInstRelationPOs = profileInstRelDAO.getInstRelationByParentInstIds(parentInstanceIds);
			if (profileInstRelationPOs != null) {
				result = new HashMap<Long, Long>(profileInstRelationPOs.size());
				for (ProfileInstRelationPO profileInstRelationPO : profileInstRelationPOs) {
					result.put(profileInstRelationPO.getInstanceId(), profileInstRelationPO.getProfileId());
					if(set.contains(profileInstRelationPO.getInstanceId())){
						List<Long> tempList = noticeData.get(profileInstRelationPO.getProfileId());
						if(tempList == null){
							tempList = new ArrayList<Long>();
							noticeData.put(profileInstRelationPO.getProfileId(), tempList);
						}
						tempList.add(profileInstRelationPO.getInstanceId());
					}
				}
			}
			profileInstRelDAO.removeInstRelByparentInstIds(parentInstanceIds);
			cancelMonitorNotice(noticeData);
		} catch (Exception e) {
			if (logger.isErrorEnabled()) {
				logger.error("", e);
			}
			throw new ProfilelibException(5435, "delete last profileInstance error!");
		}
		if (logger.isInfoEnabled()) {
			logger.info("lastProfileBind =" + result);
			logger.info("removeLastProfileBind end parentInstance=" + parentInstanceIds);
		}
		return result;
	}
	
	
	/**
	 * @Title: removeProfileInstanceRelByResource
	 * @Description: 通过主资源实例ID删除策略与资源绑定关系
	 * @param parentInstanceId
	 *            主资源实例ID
	 * @return Map<Long,Long> key = instnceId ，Value=profileId
	 * @throws ProfilelibException
	 */
	private ProfileSwitchRelation switchRemoveProfileInstanceRelByResources(List<Long> parentInstanceIds) throws ProfilelibException {
		if (logger.isInfoEnabled()) {
			logger.info("switchProfileInstanceRelByResources remove lastBand start parentInstance =" + parentInstanceIds);
		}
		HashSet<Long> set = null;
		if(CollectionUtils.isNotEmpty(parentInstanceIds)){
			set = new HashSet<>(parentInstanceIds);
		}
		HashMap<Long, Long> all = null;
		HashMap<Long, Long> onlyParent = null;
		try {
			List<ProfileInstRelationPO> profileInstRelationPOs = profileInstRelDAO.getInstRelationByParentInstIds(parentInstanceIds);
			if (profileInstRelationPOs != null) {
				onlyParent = new HashMap<Long, Long>(set.size());
				all = new HashMap<Long, Long>(profileInstRelationPOs.size());
				for (ProfileInstRelationPO profileInstRelationPO : profileInstRelationPOs) {
					all.put(profileInstRelationPO.getInstanceId(), profileInstRelationPO.getProfileId());
					if(set.contains(profileInstRelationPO.getInstanceId())){
						//父资源
						onlyParent.put(profileInstRelationPO.getInstanceId(), profileInstRelationPO.getProfileId());
					}
				}
			}
			profileInstRelDAO.removeInstRelByparentInstIds(parentInstanceIds);
		} catch (Exception e) {
			if (logger.isErrorEnabled()) {
				logger.error("", e);
			}
			throw new ProfilelibException(5435, "delete last profileInstance error!");
		}
		if (logger.isInfoEnabled()) {
			logger.info("lastProfileBind =" + all);
			logger.info("switchProfileInstanceRelByResources end parentInstance=" + parentInstanceIds);
		}
		return new ProfileSwitchRelation(all, onlyParent);
	}
	

	/**
	 * @Title: insertProfileInstances
	 * @Description: 添加主资源子资源与策略的绑定关系
	 * @param selectProfileInfoPO
	 * @param mainInst
	 * @param mainResourceId
	 * @param subInstances
	 * @return
	 * @throws ProfilelibException
	 *             List<ProfileInstRelationPO>
	 * @throws
	 */
	private List<ProfileInstRelationPO> insertProfileInstances(ProfileInfoPO mainProfile, ResourceInstance mainInstance, List<ResourceInstance> subInstances) throws ProfilelibException {
		// 查找子策略信息
		ProfileInfoPO queryProfileInfoPO = new ProfileInfoPO();
		queryProfileInfoPO.setParentProfileId(mainProfile.getProfileId());
		List<ProfileInfoPO> subProfilePOs = null;
		try {
			subProfilePOs = profileDAO.getProfileInfoPO(queryProfileInfoPO);
		} catch (Exception e1) {
			if (logger.isErrorEnabled()) {
				logger.error("getProfileInfoPO error!", e1);
			}
		}

		List<ProfileInstRelationPO> profileInstRelationPOs = new ArrayList<ProfileInstRelationPO>();
		// 构造父策略与实例对应关系
		ProfileInstRelationPO mainProfileInstRelationPO = new ProfileInstRelationPO();
		mainProfileInstRelationPO.setProfileId(mainProfile.getProfileId());
		mainProfileInstRelationPO.setInstanceId(mainInstance.getId());
		profileInstRelationPOs.add(mainProfileInstRelationPO);
		
		//如果子策略为空，通过模型创建子策略
		if(subProfilePOs==null || subProfilePOs.isEmpty()){
			ResourceDef mainResource = capacityService.getResourceDefById(mainProfile.getResourceId());
			if(mainResource!=null){
				ResourceDef[] childResources = mainResource.getChildResourceDefs();
				if (childResources != null) {
					for (ResourceDef resourceDef : childResources) {
						ProfileInfoPO subProfileInfo = createDefaultChildProfileInfo(mainProfile,resourceDef);
						subProfilePOs.add(subProfileInfo);
					}
				}
			}else{
				if(logger.isErrorEnabled()){
					logger.error("profile Service getResourceDefById error,resourceId:"+mainProfile.getResourceId()+";profileId:"+mainProfile.getProfileId());
				}
			}
		}else{
			
			List<String> profileResourceIds = new ArrayList<String>();
			//判断发现的子模型是否都存在策略，如果不存在则创建策略
			for (ProfileInfoPO subProfile : subProfilePOs) {
				if(!profileResourceIds.contains(subProfile.getResourceId())){
					profileResourceIds.add(subProfile.getResourceId());
				}
			}
			
			ResourceDef mainResource = capacityService.getResourceDefById(mainProfile.getResourceId());
			if(mainResource!=null){
				ResourceDef[] childResources = mainResource.getChildResourceDefs();
				
				for (ResourceDef childResourceDef : childResources) {
					if(!profileResourceIds.contains(childResourceDef.getId())){
						ProfileInfoPO subProfileInfo = createDefaultChildProfileInfo(mainProfile,childResourceDef);
						subProfilePOs.add(subProfileInfo);
					}
				}
			}else{
				if(logger.isErrorEnabled()){
					logger.error("profile Service getResourceDefById error,resourceId:"+mainProfile.getResourceId()+";profileId:"+mainProfile.getProfileId());
				}
			}
		}

		/*
		 * 够造子策略与实例对应
		 */
		if (null != subProfilePOs && !subProfilePOs.isEmpty()) {
			// 子资源去重
			HashSet<Long> instanceIds = new HashSet<Long>();
			for (ProfileInfoPO subProfileInfo : subProfilePOs) {
				for (ResourceInstance subInstance : subInstances) {
					if (subProfileInfo.getResourceId().equals(subInstance.getResourceId())) {
						if (instanceIds.contains(subInstance.getId())) {
							continue;
						}
						instanceIds.add(subInstance.getId());
						ProfileInstRelationPO subRelation = new ProfileInstRelationPO();
						subRelation.setProfileId(subProfileInfo.getProfileId());
						subRelation.setInstanceId(subInstance.getId());
						subRelation.setParentInstanceId(mainInstance.getId());
						profileInstRelationPOs.add(subRelation);
					}
				}
			}
		}
		try {
			profileInstRelDAO.insertInstRels(profileInstRelationPOs);
		} catch (Exception e) {
			if (logger.isErrorEnabled()) {
				logger.error("", e);
			}
		}
		return profileInstRelationPOs;
	}
	
	
	/**
	* @Title: createDefaultChildProfileInfo
	* @Description: 单独创建子策略，返回新创建的策略ID
	* @param mainProfileInfo
	* @param childResourceDef
	* @return
	* @throws ProfilelibException  long
	* @throws
	*/
	private ProfileInfoPO createDefaultChildProfileInfo(ProfileInfoPO mainProfileInfo,ResourceDef childResourceDef) throws ProfilelibException{
		StringBuilder b = new StringBuilder(100);
		
		Map<String,List<ProfileThreshold>> addToCacheProfileThreshold = new HashMap<String,List<ProfileThreshold>>();
		long subProfileId = ocProfilelibMainSequence.next();
		ProfileInfoPO childProfileInfoPO = new ProfileInfoPO();
		childProfileInfoPO.setProfileId(subProfileId);
		String subResId = childResourceDef.getId();
		childProfileInfoPO.setResourceId(subResId);
		childProfileInfoPO.setIsUse("1");
		childProfileInfoPO.setProfileType(ProfileTypeEnum.DEFAULT.toString());
		childProfileInfoPO.setParentProfileId(mainProfileInfo.getProfileId());
		childProfileInfoPO.setProfileName("默认策略" + childResourceDef.getName());
		childProfileInfoPO.setUpdateTime(null);
		b = new StringBuilder(100);
		b.append("系统默认对模型").append(childResourceDef.getId()).append("进行资源监控和告警");
		childProfileInfoPO.setProfileDesc(b.toString());
		
		
		List<ProfileMetricPO> childMetrics = getResourceMetricPOs(subProfileId, childResourceDef);
		
		
		List<ProfileThresholdPO> thresholdPOs = getThresholdPOs(subProfileId,childResourceDef,addToCacheProfileThreshold);
		
			
		try {
			List<ProfileMetric> profileMetrics = new ArrayList<>();
			profileDAO.insertProfile(childProfileInfoPO);
			if(!CollectionUtils.isEmpty(childMetrics)){
				profileMetricDAO.insertMetrics(childMetrics);
				profileMetrics = tranProfileMetricPO2BOs(childMetrics);
			}
			if(!CollectionUtils.isEmpty(thresholdPOs)){
				profileThresholdDAO.insertThresholds(thresholdPOs);
			}
			
			
			addToCache(profileMetrics, addToCacheProfileThreshold);
		} catch (Exception e) {
			if (logger.isErrorEnabled()) {
				logger.error("insert profiles error:", e);
			}
			throw new ProfilelibException(e);
		}
		return childProfileInfoPO;
	}
	

	/**
	 * @Title: createDefaultSubProfileInfo
	 * @Description: 创建默认策略
	 * @param mainProfileId
	 * @param mainResourceDef
	 * @throws ProfilelibException
	 *             void
	 * @throws
	 */
	private HashMap<String,Long> createDefaultSubProfileInfo(long mainProfileId, ResourceDef mainResourceDef) throws ProfilelibException {
		HashMap<String,Long> result = new HashMap<String, Long>();
		result.put(mainResourceDef.getId(), mainProfileId);
		
		List<ProfileInfoPO> createProfilePOs = new ArrayList<ProfileInfoPO>();
		Map<String, Long> subProfileIds = new HashMap<String, Long>();
		ProfileInfoPO profileInfoPO = new ProfileInfoPO();
		profileInfoPO.setProfileId(mainProfileId);
		profileInfoPO.setResourceId(mainResourceDef.getId());
		// 默认使用策略
		profileInfoPO.setIsUse("1");
		profileInfoPO.setProfileType(ProfileTypeEnum.DEFAULT.toString());
		profileInfoPO.setProfileName("默认策略" + mainResourceDef.getName());
		StringBuilder b = new StringBuilder(100);
		b.append("系统默认对模型").append(mainResourceDef.getName()).append("进行资源监控和告警");
		profileInfoPO.setProfileDesc(b.toString());
		createProfilePOs.add(profileInfoPO);

		ResourceDef[] childResources = mainResourceDef.getChildResourceDefs();
		if (childResources != null) {
			for (ResourceDef resourceDef : childResources) {
				long subProfileId = ocProfilelibMainSequence.next();
				ProfileInfoPO childProfileInfoPO = new ProfileInfoPO();
				childProfileInfoPO.setProfileId(subProfileId);
				String subResId = resourceDef.getId();
				childProfileInfoPO.setResourceId(subResId);
				childProfileInfoPO.setIsUse("1");
				childProfileInfoPO.setProfileType(ProfileTypeEnum.DEFAULT.toString());
				childProfileInfoPO.setParentProfileId(mainProfileId);
				childProfileInfoPO.setProfileName("默认策略" + resourceDef.getName());
				childProfileInfoPO.setUpdateTime(null);
				b = new StringBuilder(100);
				b.append("系统默认对模型").append(mainResourceDef.getId()).append("进行资源监控和告警");
				childProfileInfoPO.setProfileDesc(b.toString());
				createProfilePOs.add(childProfileInfoPO);
				subProfileIds.put(subResId, subProfileId);
			}
		}
		try {
			profileDAO.insertProfiles(createProfilePOs);
			List<ProfileMetric> profileMetrics = insertProfileMetrics(mainProfileId, subProfileIds, mainResourceDef, childResources);
			Map<String, List<ProfileThreshold>> profileThresholds = insertProfileThresholds(mainProfileId, subProfileIds, mainResourceDef, childResources);
			addToCache(profileMetrics, profileThresholds);
		} catch (Exception e) {
			if (logger.isErrorEnabled()) {
				logger.error("insert profiles error:", e);
			}
			throw new ProfilelibException(e);
		}
		result.putAll(subProfileIds);
		return result;
	}

	protected void removeCacheByProfileId(long profileId, List<TimelineInfo> timelines) {
		HashSet<String> metricIds = profileCache.getMetricIdByProfileId(profileId);
		if (metricIds != null) {
			for (String metricId : metricIds) {
				profileCache.deleteProfileMetric(profileId, metricId);
				profileCache.deleteProfileThreshold(profileId, metricId);
				if (timelines != null) {
					for (TimelineInfo timeline : timelines) {
						profileCache.deleteTimelineMetric(timeline.getTimeLineId(), metricId);
						profileCache.deleteTimelineThreshold(timeline.getTimeLineId(), metricId);
					}
				}
			}
		}
	}
	/**
	 * @Title: updateInstanceStates
	 * @Description: 更新资源状态
	 * @param insertInstRelationPOs
	 * @param deleted
	 * @param lifeStateEnum
	 * @throws ProfilelibException
	 *             void
	 * @throws
	 */
	private void updateInstanceStates(List<ProfileInstRelationPO> insertInstRelationPOs, HashMap<Long, Long> deleted, InstanceLifeStateEnum lifeStateEnum) throws ProfilelibException {
		if (insertInstRelationPOs != null) {
			Map<Long, InstanceLifeStateEnum> instStateEnumMap = new HashMap<Long, InstanceLifeStateEnum>();
			// 更新状态-已监控
			for (ProfileInstRelationPO profileInstRelationPO : insertInstRelationPOs) {
				Long instanceId = profileInstRelationPO.getInstanceId();
				if (deleted != null) {
					if (deleted.containsKey(instanceId) && deleted.get(instanceId).equals(profileInstRelationPO.getProfileId())) {
						deleted.remove(instanceId);
					}else{
						// 更新状态-未监控
						instStateEnumMap.put(instanceId, InstanceLifeStateEnum.NOT_MONITORED);
					}
				}
				instStateEnumMap.put(instanceId, lifeStateEnum);
			}
			try {
				resourceInstanceService.updateResourceInstanceState(instStateEnumMap);
			} catch (Exception e) {
				if (logger.isErrorEnabled()) {
					logger.error("update resource instance state error:", e);
				}
				throw new ProfilelibException(ServerErrorCodeConstant.ERR_SERVER_OPERATE_DATABASE_ERROR, "updateResourceInstanceState error");
			}
		}
	}
	
	private void addToCache(List<ProfileMetric> profileMetrics, Map<String, List<ProfileThreshold>> profileThresholds) {
		// 添加到缓存
		for (ProfileMetric profileMetric : profileMetrics) {
			profileCache.addProfileMetric(profileMetric);
		}
		for (List<ProfileThreshold> item : profileThresholds.values()) {
			ProfileThreshold profileThreshold = item.get(0);
			long profileId = profileThreshold.getProfileId();
			String metricId = profileThreshold.getMetricId();
			profileCache.addProfileThreshold(profileId, metricId, item);
		}
	}

	/**
	 * 添加策略默认指标
	 * 
	 * @param newMainProfileId
	 * @param subProfileIds
	 * @param mainResource
	 * @param childResources
	 * @throws Exception
	 */
	private List<ProfileMetric> insertProfileMetrics(long newMainProfileId, Map<String, Long> subProfileIds, ResourceDef mainResource, ResourceDef[] childResources) throws Exception {
		List<ProfileMetricPO> metricsAll = new ArrayList<ProfileMetricPO>();
		List<ProfileMetricPO> mainMetrics = getResourceMetricPOs(newMainProfileId, mainResource);
		metricsAll.addAll(mainMetrics);
		// 子
		if (childResources != null) {
			for (ResourceDef childResource : childResources) {
				long subProfileId = subProfileIds.get(childResource.getId());
				List<ProfileMetricPO> childMetrics = getResourceMetricPOs(subProfileId, childResource);
				metricsAll.addAll(childMetrics);
			}
		}
		try {
			profileMetricDAO.insertMetrics(metricsAll);
		} catch (Exception e) {
			if (logger.isErrorEnabled()) {
				logger.error("", e);
			}
			throw new ProfilelibException(ServerErrorCodeConstant.ERR_SERVER_OPERATE_DATABASE_ERROR, "insertMetrics error");
		}
		List<ProfileMetric> result = tranProfileMetricPO2BOs(metricsAll);
		return result;
	}

	/**
	 * 构造策略指标值
	 * 
	 * @param newMainProfileId
	 * @param mainResource
	 * @return
	 */
	private List<ProfileMetricPO> getResourceMetricPOs(long newMainProfileId, ResourceDef mainResource) {
		List<ProfileMetricPO> metrics = new ArrayList<ProfileMetricPO>();
		ResourceMetricDef[] metricDefs = mainResource.getMetricDefs();
		if (null != metricDefs) {
			for (ResourceMetricDef metricDef : metricDefs) {
				ProfileMetricPO metric = new ProfileMetricPO();
				metric.setMetricId(metricDef.getId());
				metric.setIsUse(metricDef.isMonitor() == true ? "1" : "0");
				metric.setDictFrequencyId(metricDef.getDefaultMonitorFreq().name());
				metric.setMkId(ocProfilelibMetricSequence.next());
				metric.setProfileId(newMainProfileId);
				metric.setIsAlarm(metricDef.isAlert() == true ? "1" : "0");
				metric.setAlarmRepeat(metricDef.getDefaultFlapping());
				metrics.add(metric);
			}
		}
		return metrics;
	}

	/**
	 * @Title: insertProfileThresholds
	 * @Description: 插入缺省策略阈值信息
	 * @param newMainProfileId
	 * @param subProfileIds
	 * @param mainResource
	 * @param childResources
	 * @return
	 * @throws Exception
	 *             Map<String,List<ProfileThreshold>>
	 * @throws
	 */
	private Map<String, List<ProfileThreshold>> insertProfileThresholds(long newMainProfileId, Map<String, Long> subProfileIds, ResourceDef mainResource, ResourceDef[] childResources) throws Exception {

		// 存放所有的resourceId 对应的指标信息 key: metricId value:ThresholdDef[]
		Map<String, ThresholdDef[]> allThresholds = new HashMap<String, ThresholdDef[]>();
		Map<String, String> metricId2ResourceId = new HashMap<String, String>();
		Map<String, ThresholdDef[]> mainThresholds = getThresholds(mainResource);
		// 转换父模型的指标阈值
		allThresholds.putAll(mainThresholds);
		Set<String> mainMetricIds = mainThresholds.keySet();
		for (String mainMetricId : mainMetricIds) {
			metricId2ResourceId.put(mainMetricId, mainResource.getId());
		}
		subProfileIds.put(mainResource.getId(), newMainProfileId);
		// 转换子模型指标阈值
		if (childResources != null) {
			for (ResourceDef childResource : childResources) {
				Map<String, ThresholdDef[]> subThresholds = getThresholds(childResource);
				allThresholds.putAll(subThresholds);

				Set<String> subMetricIds = subThresholds.keySet();
				for (String subMetricId : subMetricIds) {
					metricId2ResourceId.put(subMetricId, childResource.getId());
				}
			}
		}
		List<ProfileThresholdPO> allThresholdPOs = new ArrayList<ProfileThresholdPO>();
		Map<String, List<ProfileThreshold>> result = new HashMap<>(allThresholds.size());
		for (Entry<String, ThresholdDef[]> allThreshold : allThresholds.entrySet()) {
			String metricId = allThreshold.getKey();
			ThresholdDef[] defs = allThreshold.getValue();
			if (null != defs) {
				for (ThresholdDef def : defs) {
					String enumStr = def.getState().name();
					ProfileThresholdPO thresholdPO = new ProfileThresholdPO();
					thresholdPO.setMkId(ocProfilelibThresholdSequence.next());
					thresholdPO.setMetricId(metricId);
					thresholdPO.setDictMetricState(enumStr);
					thresholdPO.setExpressionDesc(def.getThresholdExpression());
					if(def.getOperator() != null){
						thresholdPO.setExpressionOperator(def.getOperator().toString());
					}
					thresholdPO.setThresholdValue(def.getDefaultvalue());
					String resourceId = metricId2ResourceId.get(metricId);
					long profileId = subProfileIds.get(resourceId);
					thresholdPO.setProfileId(profileId);
					allThresholdPOs.add(thresholdPO);

					String key = profileId + metricId;
					List<ProfileThreshold> thresholds = result.get(key);
					if (thresholds == null) {
						thresholds = new ArrayList<ProfileThreshold>(3);
						result.put(key, thresholds);
					}
					thresholds.add(transProfileThresholdPO2BO(thresholdPO));
				}
			}
		}
		try {
			profileThresholdDAO.insertThresholds(allThresholdPOs);
		} catch (Exception e) {
			if (logger.isErrorEnabled()) {
				logger.error("", e);
			}
			throw new ProfilelibException(ServerErrorCodeConstant.ERR_SERVER_OPERATE_DATABASE_ERROR, "insertThresholds error");
		}
		return result;
	}

	/**
	 * 指标id获取阈值定义
	 * 
	 * @param childResource
	 * @return
	 */
	private Map<String, ThresholdDef[]> getThresholds(ResourceDef childResource) {
		Map<String, ThresholdDef[]> thresholds = new HashMap<String, ThresholdDef[]>();
		ResourceMetricDef[] subMetricDefs = childResource.getMetricDefs();
		if (subMetricDefs != null && subMetricDefs.length > 0) {
			for (ResourceMetricDef subMetricDef : subMetricDefs) {
				if (subMetricDef.getThresholdDefs() != null) {
					thresholds.put(subMetricDef.getId(), subMetricDef.getThresholdDefs());
				}
			}
		}
		return thresholds;
	}

	private List<ProfileThreshold> transProfileThresholdPO2BOs(List<ProfileThresholdPO> thresholdPOs) {
		List<ProfileThreshold> thresholds = new ArrayList<ProfileThreshold>(3);
		for (ProfileThresholdPO thresholdPO : thresholdPOs) {
			ProfileThreshold threshold = transProfileThresholdPO2BO(thresholdPO);
			thresholds.add(threshold);
		}
		return thresholds;
	}

	@SuppressWarnings("deprecation")
	private ProfileThreshold transProfileThresholdPO2BO(ProfileThresholdPO po) {
		ProfileThreshold bo = new ProfileThreshold();
		String stateStr = po.getDictMetricState();
		PerfMetricStateEnum stateEnum = PerfMetricStateEnum.valueOf(stateStr);
		bo.setPerfMetricStateEnum(stateEnum);
		bo.setThresholdExpression(po.getExpressionDesc());
		bo.setExpressionOperator(po.getExpressionOperator());
		bo.setThresholdValue(po.getThresholdValue());
		bo.setMetricId(po.getMetricId());
		bo.setThreshold_mkId(po.getMkId());
		bo.setProfileId(po.getProfileId());
		bo.setTimelineId(po.getTimelineId());
		bo.setAlarmTemplate(StringUtils.trimToNull(po.getAlarmTemplate()));
		return bo;
	}

	private List<ProfileMetric> tranProfileMetricPO2BOs(List<ProfileMetricPO> metricsPOs) {
		List<ProfileMetric> bos = new ArrayList<ProfileMetric>();
		for (ProfileMetricPO metricsPO : metricsPOs) {
			ProfileMetric bo = tranProfileMetricPO2BO(metricsPO);
			bos.add(bo);
		}
		return bos;
	}

	private ProfileMetric tranProfileMetricPO2BO(ProfileMetricPO po) {
		ProfileMetric bo = new ProfileMetric();
		bo.setMonitor("1".equals(po.getIsUse()) ? true : false);
		bo.setMetricId(po.getMetricId());
		bo.setDictFrequencyId(po.getDictFrequencyId());
		bo.setProfileId(po.getProfileId());
		bo.setAlarm("1".equals(po.getIsAlarm()) ? true : false);
		bo.setAlarmFlapping(po.getAlarmRepeat());
		bo.setTimeLineId(po.getTimelineId());
		return bo;
	}

	

	public void start() {
		if (System.getProperty("testCase") != null) {
			return;
		}
		Lock lock = new ReentrantLock();
		boolean isUpdateCache;
		try {
			lock.lock();
			// 模型文件有没有升级变化。
			long profileId = 0;
			String metricId = "00000";
			 ResourceUpgrade upgrade = new ResourceUpgrade(this);
			 isUpdateCache = upgrade.load();
			//isUpdateCache = false;
			if (!isUpdateCache) {
				// 没有模型方面的变化，直接用之前的缓存
				if (profileCache.getProfileMetricBymetricId(profileId, metricId) != null) {
					if (logger.isInfoEnabled()) {
						logger.info("remote cache has load.");
					}
					return;
				}
			}
			load();
			ProfileMetric metric = new ProfileMetric();
			metric.setProfileId(profileId);
			metric.setMetricId(metricId);
			profileCache.addProfileMetric(metric);
		} catch (Exception e) {
			if (logger.isErrorEnabled()) {
				logger.error("", e);
			}
		} finally {
			lock.unlock();
		}
	}

	public void load() {
		long startTime = System.currentTimeMillis();
		if (logger.isInfoEnabled()) {
			logger.info("load profile,timeline metric and threshold to cache start");
		}

		List<ProfileMetricPO> allProfileMetric = null;
		List<ProfileThresholdPO> allProfileThreshold = null;
		try {
			allProfileMetric = profileMetricDAO.getAllMetric();
			allProfileThreshold = profileThresholdDAO.getAllThreshold();
			if (allProfileMetric != null) {
				Map<Long, HashSet<String>> profileMetricIds = new HashMap<Long, HashSet<String>>();
				Map<Long, HashSet<String>> timelineMetricIds = new HashMap<Long, HashSet<String>>();

				for (ProfileMetricPO profileMetricPO : allProfileMetric) {
					long profileId = profileMetricPO.getProfileId();
					ProfileMetric metric = tranProfileMetricPO2BO(profileMetricPO);
					if (profileMetricPO.getTimelineId() > 0) {
						// 基线
						profileCache.addTimelineMetric(metric);
						if(logger.isInfoEnabled()){
							logger.info("add timeline metric:" + metric);
						}
						HashSet<String> metricIds = timelineMetricIds.get(profileMetricPO.getTimelineId());
						if (metricIds == null) {
							metricIds = new HashSet<String>(20);
							profileMetricIds.put(profileMetricPO.getTimelineId(), metricIds);
						}
						metricIds.add(profileMetricPO.getMetricId());
					} else {
						// 策略
						profileCache.addProfileMetric(metric);
						if(logger.isInfoEnabled()){
							logger.info("add profile metric:" + metric);
						}
						HashSet<String> metricIds = profileMetricIds.get(profileId);
						if (metricIds == null) {
							metricIds = new HashSet<String>(20);
							profileMetricIds.put(profileId, metricIds);
						}
						metricIds.add(profileMetricPO.getMetricId());
					}
				}
				// set profile metric list
				if (!profileMetricIds.isEmpty()) {
					for (Entry<Long, HashSet<String>> item : profileMetricIds.entrySet()) {
						if(CollectionUtils.isNotEmpty(item.getValue())){
							profileCache.setProfileMetricKey(item.getKey(), item.getValue());
						}
					}
				}
				if (!timelineMetricIds.isEmpty()) {
					for (Entry<Long, HashSet<String>> item : timelineMetricIds.entrySet()) {
						if(CollectionUtils.isNotEmpty(item.getValue())){
							profileCache.setTimelineMetricKey(item.getKey(), item.getValue());
						}
					}
				}
			}
			if (allProfileThreshold != null) {
				Map<String, List<ProfileThreshold>> profileThresholdMap = new HashMap<>(allProfileThreshold.size());
				Map<String, List<ProfileThreshold>> timelineThresholdMap = new HashMap<>(allProfileThreshold.size() / 2);
				for (ProfileThresholdPO profileThresholdPO : allProfileThreshold) {
					if (profileThresholdPO.getTimelineId() > 0) {
						StringBuilder key = new StringBuilder(50);
						key.append(profileThresholdPO.getTimelineId());
						key.append(profileThresholdPO.getMetricId());
						String timelineKey = key.toString();
						// 基线
						List<ProfileThreshold> timelineThreshold = timelineThresholdMap.get(timelineKey);
						if (timelineThreshold == null) {
							timelineThreshold = new ArrayList<ProfileThreshold>(3);
							timelineThresholdMap.put(timelineKey, timelineThreshold);
						}
						timelineThreshold.add(transProfileThresholdPO2BO(profileThresholdPO));
					} else {
						StringBuilder key = new StringBuilder(50);
						key.append(profileThresholdPO.getProfileId());
						key.append(profileThresholdPO.getMetricId());
						String profileKey = key.toString();
						// 策略
						List<ProfileThreshold> profileThreshold = profileThresholdMap.get(profileKey);
						if (profileThreshold == null) {
							profileThreshold = new ArrayList<ProfileThreshold>(3);
							profileThresholdMap.put(profileKey, profileThreshold);
						}
						profileThreshold.add(transProfileThresholdPO2BO(profileThresholdPO));
					}
				}
				for (List<ProfileThreshold> item : profileThresholdMap.values()) {
					ProfileThreshold threshold = item.get(0);
					profileCache.addProfileThreshold(threshold.getProfileId(), threshold.getMetricId(), item);
				}
				for (List<ProfileThreshold> item : timelineThresholdMap.values()) {
					ProfileThreshold threshold = item.get(0);
					profileCache.addTimelineThreshold(threshold.getTimelineId(), threshold.getMetricId(), item);
				}
			}
		} catch (Exception e) {
			if (logger.isErrorEnabled()) {
				logger.error("load profile,timeline metric and threshold error!", e);
			}
			return;
		}
		long endTime = System.currentTimeMillis();
		if (logger.isInfoEnabled()) {
			long diff = endTime - startTime;
			StringBuilder b = new StringBuilder(100);
			b.append("load profile,timeline metric and threshold to cache end time=");
			b.append(diff / 1000.0).append(" seconds");
			logger.info(b);
		}
	}

	
	private ProfileSwitchData convertSwitchProfile(ProfileSwitchConvert convert,ProfileTypeEnum typeEnum){
		Profile p = null;
		try {
			p = getProfilesById(convert.getLastProfileId());
		} catch (ProfilelibException e) {
		}
		if(p == null){
			StringBuilder b = new StringBuilder(100);
			switch(typeEnum){
				case SPECIAL:
					b.append("addMonitorUseSpecial");
					break;
				case PERSONALIZE:
					b.append("addMonitorUsePersonalize");
					break;
				default:
					b.append("addMonitorUseDefault");
					break;
			}
			b.append(" last profile profileId=");
			b.append(convert.getLastProfileId());
			b.append(" query is null");
			logger.error(b.toString());
		}else{
			ProfileSwitchData changeData = new ProfileSwitchData();
			if(typeEnum == ProfileTypeEnum.SPECIAL && CollectionUtils.isNotEmpty(p.getTimeline())){
				//需要传人基线。判断当前是否在生效中。
				List<Timeline> newTimeline = new ArrayList<Timeline>(2);
				Date nowDate = new Date();
				for (Timeline timeline : p.getTimeline()) {
					TimelineInfo timelineInfo = timeline.getTimelineInfo();
					if (timelineInfo != null) {
						Date startTime = timelineInfo.getStartTime();
						Date endTime = timelineInfo.getEndTime();
						if (startTime != null && endTime != null) {
							if (nowDate.getTime() >= startTime.getTime() && nowDate.getTime() <= endTime.getTime()) {
								newTimeline.add(timeline);
							}
						}
					}
				}
				if(CollectionUtils.isEmpty(newTimeline)){
					p.setTimeline(null);
				}else{
					p.setTimeline(newTimeline);
				}
			}
			ProfileInstanceRelation r = new ProfileInstanceRelation();
			r.setProfileId(convert.getLastProfileId());
			Instance i = new Instance(convert.getParentInstanceId(), 0);
			r.setInstances(Arrays.asList(i));
			//把当前实例绑定到策略关系中
			p.setProfileInstanceRelations(r);
			//子策略绑定子实例
			List<Profile> childProfile = p.getChildren();
			HashMap<String,List<Long>> childMonitor = convert.getChildMonitor();
			if(childMonitor != null && CollectionUtils.isNotEmpty(childProfile)){
				for (Profile tempChildProfile : childProfile) {
					String resourceId = tempChildProfile.getProfileInfo().getResourceId();
					List<Long> instanceIds = childMonitor.get(resourceId);
					List<Instance> childInstances = new ArrayList<Instance>();
					if(CollectionUtils.isNotEmpty(instanceIds)){
						for (long id : instanceIds) {
							Instance tempI = new Instance(id, convert.getParentInstanceId());
							childInstances.add(tempI);
						}
						ProfileInstanceRelation rTemp = new ProfileInstanceRelation();
						rTemp.setInstances(childInstances);
						r.setProfileId(tempChildProfile.getProfileInfo().getProfileId());
						tempChildProfile.setProfileInstanceRelations(rTemp);
					}
				}
			}
			changeData.setProfile(p);
			return changeData;
		}
		return null;
	}
	
	private void switchProfileNotice(ProfileSwitchConvert convert,ProfileTypeEnum typeEnum){
		ProfileSwitchData data = convertSwitchProfile(convert,typeEnum);
		if(data != null){
			//策略切换通知
			try{
				profileSwitchManager.doProfileSwitchInterceptor(new ArrayList<>(Arrays.asList(data)));
			}catch(Exception e){
				if(logger.isErrorEnabled()){
					logger.error("", e);
				}
			}
			if(logger.isInfoEnabled()){
				logger.info(convert);
			}
		}
	}
	
    private void switchProfileNotices(List<ProfileSwitchConvert> converts,ProfileTypeEnum typeEnum){
    	List<ProfileSwitchData> datas = new ArrayList<ProfileSwitchData>();
		for (ProfileSwitchConvert convert : converts) {
			ProfileSwitchData data = convertSwitchProfile(convert,typeEnum);
			if(data != null){
				datas.add(data);
			}
			if(logger.isInfoEnabled()){
				logger.info(convert);
			}
		}
		if(CollectionUtils.isNotEmpty(datas)){
			//策略切换通知
			try{
				profileSwitchManager.doProfileSwitchInterceptor(datas);
			}catch(Exception e){
				if(logger.isErrorEnabled()){
					logger.error("", e);
				}
			}
		}
	}
	
	/**
	 * @Title: cancelMonitorToCollector
	 * @Description: 将取消监控记录到stm_profile_change表等待同步到DCS
	 * @param instances
	 *            void
	 * @throws
	 */
	private void cancelMonitorToCollector(List<Long> instanceIds) {
		if (instanceIds == null || instanceIds.isEmpty()) {
			return;
		}
		try {
			// 记录存放到数据库中
			List<ProfileChangePO> profileChangePOs = new ArrayList<>();
			for (long instanceId : instanceIds) {
				long profileChangeId = ocProfileChangeSequence.next();
				ProfileChangePO profileChangePO = new ProfileChangePO();
				profileChangePO.setChangeTime(calendarInstance.getTime());
				profileChangePO.setOperateMode(ProfileChangeEnum.CANCEL_MONITOR.toString());
				profileChangePO.setOperateState(0);
				profileChangePO.setProfileChangeId(profileChangeId);
				profileChangePO.setSource(String.valueOf(instanceId));
				profileChangePOs.add(profileChangePO);
			}
			profileChangeDAO.insertProfileChanges(profileChangePOs);
			if (logger.isDebugEnabled()) {
				logger.debug("cancelMonitor success, instanceIds=" + instanceIds);
			}
		} catch (Exception e) {
			if (logger.isErrorEnabled()) {
				logger.error("cancelMonitorToCollector error! instanceId=" + instanceIds, e);
			}
		}

	}


	/**
	 * @Title: copyProfileByPO
	 * @Description: 批量保存Copy的策略信息
	 * @param copyProfileIdInfos
	 * @throws Exception
	 *             void
	 * @throws
	 */
	private void copyProfileByPO(Map<Long, ProfileInfoPO> copyProfileIdInfos) throws Exception {
		List<ProfileInfoPO> saveProfileInfoPOs = new ArrayList<>();
		List<ProfileMetricPO> saveMetricPOs = new ArrayList<>();
		List<ProfileThresholdPO> saveProfileThresholdPOs = new ArrayList<>();
		if (null != copyProfileIdInfos && !copyProfileIdInfos.isEmpty()) {
			for (Map.Entry<Long, ProfileInfoPO> profileIdInfo : copyProfileIdInfos.entrySet()) {
				ProfileInfoPO profileInfoPO = profileIdInfo.getValue();
				long copyProfileId = profileIdInfo.getKey();
				profileInfoPO.setProfileType(ProfileTypeEnum.SPECIAL.toString());
				if (profileInfoPO.getUpdateTime() != null) {
					profileInfoPO.setUpdateTime(null);
				}
				profileInfoPO.setCopyProfileId(copyProfileId);
				List<ProfileMetricPO> metricPOs = profileMetricDAO.getMetricsByProfileId(copyProfileId);
				for (ProfileMetricPO profileMetricPO : metricPOs) {
					profileMetricPO.setMkId(ocProfilelibMetricSequence.next());
					profileMetricPO.setProfileId(profileInfoPO.getProfileId());
				}
				List<ProfileThresholdPO> profileThresholdPOs = profileThresholdDAO.getThresholdByProfileId(copyProfileId);
				for (ProfileThresholdPO profileThresholdPO : profileThresholdPOs) {
					profileThresholdPO.setMkId(ocProfilelibThresholdSequence.next());
					profileThresholdPO.setProfileId(profileInfoPO.getProfileId());
				}

				saveProfileInfoPOs.add(profileInfoPO);
				saveMetricPOs.addAll(metricPOs);
				saveProfileThresholdPOs.addAll(profileThresholdPOs);
			}
		}
		insertProfileInfos(saveProfileInfoPOs, saveMetricPOs, saveProfileThresholdPOs);
	}

	/**
	 * @Title: innerCreateChildProfile
	 * @Description: 在指定策略ID下插入新子策略
	 * @param parentProfileId 父策略
	 * @param childProfileInfo
	 * @return
	 * @throws ProfilelibException
	 *             long
	 * @throws
	 */
	private long innerCreateChildProfile(long parentProfileId, ProfileInfo childProfileInfo) throws ProfilelibException {
		String resourceId = childProfileInfo.getResourceId();
		long newProfileId = ocProfilelibMainSequence.next();
		if (logger.isInfoEnabled()) {
			logger.info("parentProfileId = " + parentProfileId);
		}
		
		ResourceDef resourceDef = capacityService.getResourceDefById(resourceId);
		if (resourceDef == null) {
			if (logger.isWarnEnabled()) {
				logger.warn("resourceDef is null,resourceId=" + resourceId);
			}
			throw new ProfilelibException(ServerErrorCodeConstant.ERR_SERVER_PARAMETER_ERROR, "resourceDef is null,resourceId=" + resourceId + " create fail.");
		}
		ProfileInfoPO profileInfoPO = getInfoPO(newProfileId, childProfileInfo, parentProfileId);
		profileInfoPO.setUpdateTime(null);
		Map<String, List<ProfileThreshold>> addToCacheProfileThreshold = new HashMap<String, List<ProfileThreshold>>(50);
		List<ProfileMetric> addToCacheProfileMetric = null;
		/**
		 * 组装策略里的指标设置信息和指标阈值信息
		 */
		try {
			List<ProfileMetricPO> profileMetricPOs = new ArrayList<>();
			profileMetricPOs.addAll(getResourceMetricPOs(newProfileId, resourceDef));
			addToCacheProfileMetric = tranProfileMetricPO2BOs(profileMetricPOs);
			List<ProfileThresholdPO> profileThresholdPOs = new ArrayList<>();
			profileThresholdPOs.addAll(getThresholdPOs(newProfileId, resourceDef, addToCacheProfileThreshold));

			// 插入缺省策略基本信息\指标信息\阈值信息
			List<ProfileInfoPO> profileInfoPOs = new ArrayList<ProfileInfoPO>(1);
			profileInfoPOs.add(profileInfoPO);
			insertProfileInfos(profileInfoPOs, profileMetricPOs, profileThresholdPOs);
		} catch (Exception e) {
			if (logger.isErrorEnabled()) {
				logger.error("addChildProfileInfo error!", e);
			}
			throw new ProfilelibException(ProfilelibException.CODE_ERROR_VALIDATION + 3, "写入默认策略失败");
		}
		// add to cache
		addToCache(addToCacheProfileMetric, addToCacheProfileThreshold);
		return newProfileId;
	}

	/**
	 * 转换为策略表信息
	 * 
	 * @param info
	 * @param parentId
	 * @return
	 */
	private ProfileInfoPO getInfoPO(long profileId, ProfileInfo info, long parentId) {
		ProfileInfoPO profileInfoPO = new ProfileInfoPO();
		profileInfoPO.setProfileId(profileId);
		profileInfoPO.setResourceId(info.getResourceId());
		profileInfoPO.setProfileDesc(info.getProfileDesc());
		if (info.getProfileType() != null) {
			profileInfoPO.setProfileType(info.getProfileType().toString());
		}
		profileInfoPO.setProfileName(info.getProfileName());
		profileInfoPO.setUpdateTime(info.getUpdateTime());
		profileInfoPO.setUpdateUser(info.getUpdateUser());
		profileInfoPO.setUpdateUserDomain(info.getUpdateUserDomain());
		if (parentId > 0) {
			profileInfoPO.setParentProfileId(parentId);
		}
		profileInfoPO.setDomainId(info.getDomainId());
		profileInfoPO.setIsUse(info.isUse() ? "1" : "0");
		profileInfoPO.setCreateUser(info.getCreateUser());
		return profileInfoPO;
	}

	private List<ProfileThresholdPO> getThresholdPOs(long profileId, ResourceDef resourceDef, Map<String, List<ProfileThreshold>> addToCacheProfileThreshold) {
		List<ProfileThresholdPO> thresholdPOs = new ArrayList<>();
		ResourceMetricDef[] metricDefs = resourceDef.getMetricDefs();
		for (ResourceMetricDef resourceMetricDef : metricDefs) {
			String metricId = resourceMetricDef.getId();
			ThresholdDef[] defs = resourceMetricDef.getThresholdDefs();
			if (defs != null && defs.length > 0) {
				String key = profileId + metricId;
				for (ThresholdDef def : defs) {
					ProfileThresholdPO allThresholdPO = new ProfileThresholdPO();
					allThresholdPO.setExpressionDesc(def.getThresholdExpression());
					if(def.getOperator() != null){
						allThresholdPO.setExpressionOperator(def.getOperator().toString());
					}
					allThresholdPO.setMkId(ocProfilelibThresholdSequence.next());
					allThresholdPO.setMetricId(metricId);
					allThresholdPO.setDictMetricState(def.getState().name());
					allThresholdPO.setProfileId(profileId);
					allThresholdPO.setThresholdValue(def.getDefaultvalue());
					thresholdPOs.add(allThresholdPO);

					List<ProfileThreshold> thresholds = addToCacheProfileThreshold.get(key);
					if (thresholds == null) {
						thresholds = new ArrayList<ProfileThreshold>(3);
						addToCacheProfileThreshold.put(key, thresholds);
					}
					thresholds.add(transProfileThresholdPO2BO(allThresholdPO));
				}
			}
		}
		return thresholdPOs;
	}

	private long convertPersonalizeProfileToPO(long parentProfileId, long parentInstanceId, Profile profile, List<ProfileInfoPO> profileInfoPOs, List<ProfileMetricPO> profileMetricPOs, List<ProfileThresholdPO> profileTholdPOs, List<ProfileInstRelationPO> profileInfInstRelationPOs, List<ProfileMetric> addToCacheProfileMetric, Map<String, List<ProfileThreshold>> addToCacheProfileThreshold) throws ProfilelibException {

		/**
		 * 组装策略里的指标设置信息和指标阈值信息
		 */
		ProfileInfo parentInfo = profile.getProfileInfo();

		long newProfileId = ocProfilelibMainSequence.next();
		parentInfo.setProfileId(newProfileId);
		parentInfo.setProfileType(ProfileTypeEnum.PERSONALIZE);
		ProfileInfoPO po = transProfileInfo(newProfileId, parentInfo);
		List<Instance> instances = null;
		if (profile.getProfileInstanceRelations() != null) {
			instances = profile.getProfileInstanceRelations().getInstances();
			po.setIsUse("1");
		} else {
			po.setIsUse("0");
		}
		if (parentProfileId != 0) {
			po.setParentProfileId(parentProfileId);
			if (instances != null && !instances.isEmpty()) {
				if (logger.isDebugEnabled()) {
					StringBuilder b = new StringBuilder();
					b.append("create personalize profile instanceRelation:resourceId=").append(profile.getProfileInfo().getResourceId());
					b.append(" instnaceId=");
					for (Instance instance : instances) {
						b.append(instance.getInstanceId());
						b.append(",");
					}
					logger.debug(b);
				}
			}
		} else {
			// 父
			po.setResourceInstanceId(parentInstanceId);
		}
		profileInfoPOs.add(po);

		// metric
		List<ProfileMetric> profileMetric = profile.getMetricSetting().getMetrics();
		if (profileMetric != null && !profileMetric.isEmpty()) {
			addToCacheProfileMetric.addAll(profileMetric);
			profileMetricPOs.addAll(tranProfileMetrics(newProfileId, profileMetric));
			logger.info("create personalize profile,resourceId=" + parentInfo.getResourceId());
			for (ProfileMetric childMetric : profileMetric) {
				logger.info("metricId="+ childMetric.getMetricId());
				List<ProfileThreshold> profileThreshold = childMetric.getMetricThresholds();
				// 阈值
				if (profileThreshold != null && !profileThreshold.isEmpty()) {
					String key = newProfileId + childMetric.getMetricId();
					addToCacheProfileThreshold.put(key, profileThreshold);
					profileTholdPOs.addAll(transProfileThresholds(newProfileId, profileThreshold));
				}
			}
		}
		if (instances != null) {
			// 实例关系-- 需要区分父与子的关系
			for (Instance instance : instances) {
				ProfileInstRelationPO profileInstRelationPO = new ProfileInstRelationPO();
				profileInstRelationPO.setInstanceId(instance.getInstanceId());
				if (parentProfileId != 0) {
					// 子
					profileInstRelationPO.setParentInstanceId(parentInstanceId);
				}
				profileInstRelationPO.setProfileId(newProfileId);
				profileInfInstRelationPOs.add(profileInstRelationPO);
			}
		}
		/*
		 * 子策略信息
		 */
		if (profile.getChildren() != null) {
			for (Profile childProfile : profile.getChildren()) {
				convertPersonalizeProfileToPO(newProfileId, parentInstanceId, childProfile, profileInfoPOs, profileMetricPOs, profileTholdPOs, profileInfInstRelationPOs, addToCacheProfileMetric, addToCacheProfileThreshold);
			}
		}
		return newProfileId;
	}
	
	//Map<Long,List<Long>>  key:profileId value:instanceIds
	//通知规则：父取消监控，只通知父资源实例。子实例取消监控只通知子实例
	private void cancelMonitorNotice(Map<Long,List<Long>> data){
		if(data != null && !data.isEmpty()){
			List<ProfileChangeData> changeDatas = new ArrayList<ProfileChangeData>();
			for (Entry<Long,List<Long>> item : data.entrySet()) {
				if(item.getValue() == null || item.getValue().isEmpty()){
					continue;
				}
				ProfileChangeData profileChangeData = new ProfileChangeData();
				profileChangeData.setProfileId(item.getKey());
				profileChangeData.setCancelInstanceIds(item.getValue());
				changeDatas.add(profileChangeData);
			}
			if(!changeDatas.isEmpty()){
				profileResourceCancelMonitorManager.doResourceMonitorChangeInterceptor(changeDatas);
			}
		}
	}

	@SuppressWarnings("deprecation")
	private ProfileThresholdPO transProfileThreshold(long profileId, ProfileThreshold bo) {
		ProfileThresholdPO po = new ProfileThresholdPO();
		po.setDictMetricState(bo.getPerfMetricStateEnum().toString());
		po.setExpressionDesc(bo.getThresholdExpression());
		po.setExpressionOperator(bo.getExpressionOperator());
		po.setThresholdValue(bo.getThresholdValue());
		po.setMetricId(bo.getMetricId());
		po.setProfileId(profileId);
		po.setAlarmTemplate(StringUtils.trimToNull(bo.getAlarmTemplate()));
		po.setMkId(ocProfilelibThresholdSequence.next());
		return po;
	}

	private List<ProfileThresholdPO> transProfileThresholds(long profileId, List<ProfileThreshold> bos) {
		List<ProfileThresholdPO> pos = new ArrayList<ProfileThresholdPO>();
		for (ProfileThreshold bo : bos) {
			pos.add(transProfileThreshold(profileId, bo));
		}
		return pos;
	}

	private List<ProfileMetricPO> tranProfileMetrics(long proflieId, List<ProfileMetric> metricsBOs) {
		List<ProfileMetricPO> bos = new ArrayList<>();
		for (ProfileMetric metricsBO : metricsBOs) {
			ProfileMetricPO po = tranProfileMetric(proflieId, metricsBO);
			bos.add(po);
		}
		return bos;
	}

	private ProfileMetricPO tranProfileMetric(long profileId, ProfileMetric bo) {
		ProfileMetricPO po = new ProfileMetricPO();
		po.setIsUse(bo.isMonitor() ? "1" : "0");
		po.setMetricId(bo.getMetricId());
		po.setDictFrequencyId(bo.getDictFrequencyId());
		po.setProfileId(profileId);
		po.setIsAlarm(bo.isAlarm() ? "1" : "0");
		po.setAlarmRepeat(bo.getAlarmFlapping());
		po.setMkId(ocProfilelibMetricSequence.next());
		return po;
	}

	public void setProfileDAO(ProfileDAO profileDAO) {
		this.profileDAO = profileDAO;
	}

	public void setProfileInstRelDAO(ProfileInstanceRelationDAO profileInstRelDAO) {
		this.profileInstRelDAO = profileInstRelDAO;
	}

	public void setProfileMetricDAO(ProfileMetricDAO profileMetricDAO) {
		this.profileMetricDAO = profileMetricDAO;
	}

	public void setProfileThresholdDAO(ProfileThresholdDAO profileThresholdDAO) {
		this.profileThresholdDAO = profileThresholdDAO;
	}

	public void setProfileChangeDAO(ProfileChangeDAO profileChangeDAO) {
		this.profileChangeDAO = profileChangeDAO;
	}

	public void setLastProfileDAO(LastProfileDAO lastProfileDAO) {
		this.lastProfileDAO = lastProfileDAO;
	}

	public void setResourceInstanceService(ResourceInstanceService resourceInstanceService) {
		this.resourceInstanceService = resourceInstanceService;
	}

	public void setResourceInstanceExtendService(ResourceInstanceExtendService resourceInstanceExtendService) {
		this.resourceInstanceExtendService = resourceInstanceExtendService;
	}

	public void setCapacityService(CapacityService capacityService) {
		this.capacityService = capacityService;
	}

	public void setTimelineService(TimelineService timelineService) {
		this.timelineService = timelineService;
	}

	public void setOcProfilelibMainSequence(ISequence ocProfilelibMainSequence) {
		this.ocProfilelibMainSequence = ocProfilelibMainSequence;
	}

	public void setOcProfilelibMetricSequence(ISequence ocProfilelibMetricSequence) {
		this.ocProfilelibMetricSequence = ocProfilelibMetricSequence;
	}

	public void setOcProfilelibThresholdSequence(ISequence ocProfilelibThresholdSequence) {
		this.ocProfilelibThresholdSequence = ocProfilelibThresholdSequence;
	}

	public void setOcProfileChangeSequence(ISequence ocProfileChangeSequence) {
		this.ocProfileChangeSequence = ocProfileChangeSequence;
	}

	public void setProfileCache(ProfileCache profileCache) {
		this.profileCache = profileCache;
	}

	public void setProfileMetricAlarmManager(ProfileChangeManager profileMetricAlarmManager) {
		this.profileMetricAlarmManager = profileMetricAlarmManager;
	}

	public void setProfileMetricMonitorManager(ProfileChangeManager profileMetricMonitorManager) {
		this.profileMetricMonitorManager = profileMetricMonitorManager;
	}

	public void setProfileResourceCancelMonitorManager(ProfileChangeManager profileResourceCancelMonitorManager) {
		this.profileResourceCancelMonitorManager = profileResourceCancelMonitorManager;
	}

	public ProfileChangeManager getProfileSwitchManager() {
		return profileSwitchManager;
	}


	public void setProfileSwitchManager(ProfileChangeManager profileSwitchManager) {
		this.profileSwitchManager = profileSwitchManager;
	}


	public ProfileDAO getProfileDAO() {
		return profileDAO;
	}

	public ProfileInstanceRelationDAO getProfileInstRelDAO() {
		return profileInstRelDAO;
	}

	public ProfileMetricDAO getProfileMetricDAO() {
		return profileMetricDAO;
	}

	public ProfileThresholdDAO getProfileThresholdDAO() {
		return profileThresholdDAO;
	}

	public ProfileChangeDAO getProfileChangeDAO() {
		return profileChangeDAO;
	}

	public LastProfileDAO getLastProfileDAO() {
		return lastProfileDAO;
	}

	public ResourceInstanceService getResourceInstanceService() {
		return resourceInstanceService;
	}

	public ResourceInstanceExtendService getResourceInstanceExtendService() {
		return resourceInstanceExtendService;
	}

	public CapacityService getCapacityService() {
		return capacityService;
	}

	public TimelineService getTimelineService() {
		return timelineService;
	}

	public ISequence getOcProfilelibMainSequence() {
		return ocProfilelibMainSequence;
	}

	public ISequence getOcProfilelibMetricSequence() {
		return ocProfilelibMetricSequence;
	}

	public ISequence getOcProfilelibThresholdSequence() {
		return ocProfilelibThresholdSequence;
	}

	public ISequence getOcProfileChangeSequence() {
		return ocProfileChangeSequence;
	}

	public ProfileCache getProfileCache() {
		return profileCache;
	}

	public ProfileChangeManager getProfileMetricAlarmManager() {
		return profileMetricAlarmManager;
	}

	public ProfileChangeManager getProfileMetricMonitorManager() {
		return profileMetricMonitorManager;
	}

	public ProfileChangeManager getProfileResourceCancelMonitorManager() {
		return profileResourceCancelMonitorManager;
	}

}
