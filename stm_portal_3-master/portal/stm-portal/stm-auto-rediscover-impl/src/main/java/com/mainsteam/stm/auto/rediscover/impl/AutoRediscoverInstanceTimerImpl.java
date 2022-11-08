package com.mainsteam.stm.auto.rediscover.impl;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Timer;
import java.util.TimerTask;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang.StringUtils;

import com.alibaba.fastjson.JSONObject;
import com.mainsteam.stm.auto.rediscover.AutoRediscoverInstance;
import com.mainsteam.stm.caplib.CapacityService;
import com.mainsteam.stm.caplib.common.CategoryDef;
import com.mainsteam.stm.caplib.dict.CapacityConst;
import com.mainsteam.stm.common.instance.ResourceInstanceDiscoveryService;
import com.mainsteam.stm.common.instance.obj.ResourceInstanceDiscoveryParameter;
import com.mainsteam.stm.discovery.obj.DiscoverResourceIntanceResult;
import com.mainsteam.stm.instancelib.DiscoverPropService;
import com.mainsteam.stm.instancelib.ResourceInstanceService;
import com.mainsteam.stm.instancelib.exception.InstancelibException;
import com.mainsteam.stm.instancelib.obj.DiscoverProp;
import com.mainsteam.stm.instancelib.obj.ResourceInstance;
import com.mainsteam.stm.instancelib.objenum.InstanceLifeStateEnum;
import com.mainsteam.stm.portal.vm.api.DiscoveryVmService;
import com.mainsteam.stm.profilelib.ProfileAutoRediscoverService;
import com.mainsteam.stm.profilelib.ProfileAutoRefreshService;
import com.mainsteam.stm.profilelib.ProfileService;
import com.mainsteam.stm.profilelib.exception.ProfilelibException;
import com.mainsteam.stm.profilelib.obj.ProfileAutoRefresh;
import com.mainsteam.stm.profilelib.obj.ProfilelibAutoRediscover;
import com.mainsteam.stm.profilelib.obj.ProfilelibAutoRediscoverInstance;
import com.mainsteam.stm.util.DateUtil;
import com.mainsteam.stm.util.SecureUtil;

public class AutoRediscoverInstanceTimerImpl implements AutoRediscoverInstance {
	
	private ResourceInstanceService resourceInstanceService;

	private CapacityService capacityService;

	private ProfileAutoRediscoverService profileAutoRediscoveryService;

	private DiscoverPropService discoverPropService;

	private ResourceInstanceDiscoveryService resourceInstanceDiscoveryService;

	private ProfileService profileService;

	private ProfileAutoRefreshService profileAutoRefreshService;

	private DiscoveryVmService discoveryVmService;
	
	private int jobExecuteTime;

	private static final Log logger = LogFactory.getLog(AutoRediscoverInstanceTimerImpl.class);
	
	private final static String DISCOVERYTYPE_VCENTER = "1";
	private final static String DISCOVERYTYPE_ESXI = "2";
	private final static String DISCOVERYTYPE_XENPOOl = "3";
	private final static String DISCOVERYTYPE_FUSIONCOMPUTESITE = "4";
	//5分钟
	private final static long PERIOD = 1000 * 60 * 5;
	
	public void init() {
		if (logger.isInfoEnabled()) {
			logger.info("Auto Re Discover Resource Instance Timer init!");
		}
		
		Timer instanceTimer = new Timer("instance_auto_refresh_timer");
		Calendar calendar = Calendar.getInstance();
		calendar.add(Calendar.MINUTE, 2);// 系统启动后2分钟
		instanceTimer.schedule(new TimerTask() {
			@Override
			public void run() {
				try {
					resourceRefresh();
				}catch(Exception e){
				}
			}
		}, calendar.getTime(), PERIOD);//每隔5分钟执行一次

		Timer vmTimer = new Timer("vm_auto_refresh_timer");
		calendar.add(Calendar.MINUTE, 3);// 系统启动后3分钟
		vmTimer.schedule(new TimerTask() {
			@Override
			public void run() {
				try {
					vmResourceRefresh();
				}catch(Exception e){
				}
			}
		}, calendar.getTime(), PERIOD);// 系统启动5分钟后开始执行，每隔5分钟执行一次
	}

	private void vmResourceRefresh(){
		if (logger.isInfoEnabled()) {
			logger.info("Auto vm Discover start execute");
		}
		List<ProfileAutoRefresh> allProfileAutoRefresh = profileAutoRefreshService.getAllAutoRefreshProfile();
		if (CollectionUtils.isEmpty(allProfileAutoRefresh)) {
			if (logger.isInfoEnabled()) {
				logger.info("NO VM refresh Instance");
			}
			return;
		}
		for (ProfileAutoRefresh profileAutoRefresh : allProfileAutoRefresh) {
			if(logger.isInfoEnabled()){
				StringBuilder b = new StringBuilder(100);
				b.append("vm instanceId=").append(profileAutoRefresh.getInstanceId());
				b.append(" lastExecuteDate=");
				if(profileAutoRefresh.getExecuteTime() != null){
					b.append(DateUtil.format(profileAutoRefresh.getExecuteTime()));
				}
				logger.info(b);
			}
			// 计算执行时间
			if (calcExecuteTime(profileAutoRefresh.getNextExecuteTime())) {
				// 通过资源实例ID获取资源的发现属性
				try {
					if (logger.isInfoEnabled()) {
						logger.info("VM instance:" + profileAutoRefresh.getInstanceId() + " Accord with automatic discover condition");
					}
					reDiscover(profileAutoRefresh.getInstanceId(),true);
				} catch (InstancelibException e) {
					if (logger.isErrorEnabled()) {
						logger.error("auto rediscover VM discover error:" + e);
					}
				} catch (ProfilelibException e) {
					if (logger.isErrorEnabled()) {
						logger.error("auto rediscover VM enable monitor error: " + e);
					}
				} catch (Exception e) {
					if (logger.isInfoEnabled()) {
						logger.error("auto refresh error:" + e.getMessage());
						logger.error(e.getStackTrace());
					}
				}

				if (logger.isInfoEnabled()) {
					logger.info("VM instance:" + profileAutoRefresh.getInstanceId() + " refresh success,update execute time!");
				}
				
				Calendar currentCalendar = Calendar.getInstance();
				currentCalendar.set(Calendar.HOUR_OF_DAY, jobExecuteTime);
				currentCalendar.set(Calendar.MINUTE,0);
				currentCalendar.set(Calendar.SECOND,0);
				profileAutoRefresh.setExecuteTime(currentCalendar.getTime());
				// 自动发现完成后更新策略的执行时间及下次执行时间
				currentCalendar.add(Calendar.DATE, profileAutoRefresh.getExecutRepeat());
				profileAutoRefresh.setNextExecuteTime(currentCalendar.getTime());
				profileAutoRefreshService.autoUpdateAutoRefreshProfile(profileAutoRefresh);
			}
		}
		if (logger.isInfoEnabled()) {
			logger.info("Auto vm Discover end execute");
		}
	}
	
	private void resourceRefresh(){
		if (logger.isInfoEnabled()) {
			logger.info("Auto instance Discover start execute");
		}
		List<ProfilelibAutoRediscover> profilelibAutoRediscovers = profileAutoRediscoveryService.queryAllAutoRediscoverProfileByUsed();
		if (CollectionUtils.isEmpty(profilelibAutoRediscovers)) {
			if (logger.isInfoEnabled()) {
				logger.info("NO refresh instance");
			}
			return;
		}
		for (ProfilelibAutoRediscover profilelibAutoRediscover : profilelibAutoRediscovers) {
			List<ProfilelibAutoRediscoverInstance> instances = profilelibAutoRediscover.getProfileInstanceRel();
			if (!CollectionUtils.isEmpty(instances)) {
				for (ProfilelibAutoRediscoverInstance instance : instances) {
					if(logger.isInfoEnabled()){
						StringBuilder b = new StringBuilder(100);
						b.append("instanceId=").append(instance.getInstanceId());
						b.append(" lastExecuteDate=");
						if(instance.getExecuteTime() != null){
							b.append(DateUtil.format(instance.getExecuteTime()));
						}
						logger.info(b);
					}
					if (calcExecuteTime(instance.getNextExecuteTime())) {
						try {
							// 策略中是否删除历史记录 1:删除，0:不删除，如果不删除旧资源依然加入监控
							boolean isAutoDeleteInstance = true;
							if (profilelibAutoRediscover.getIsRemoveHistory() <= 0) {
								isAutoDeleteInstance = false;
							}
							if (logger.isInfoEnabled()) {
								logger.info("instance:" + instance.getInstanceId() + " Accord with automatic discover condition");
							}
							Map<InstanceLifeStateEnum, List<Long>> result = reDiscover(instance.getInstanceId(),isAutoDeleteInstance);
							if(null!=result && !CollectionUtils.isEmpty(result.get(InstanceLifeStateEnum.NEWER))){
								profileService.autoRefreshChildEnableMonitor(instance.getInstanceId(),result.get(InstanceLifeStateEnum.NEWER));
							}
							/*
							 * 刷新只针对新增加的子资源加入监控
							 */
//							//获取刷新后的所有未监控子资源
//							List<ResourceInstance> childInstanceIds = resourceInstanceService.getChildInstanceByParentId(instance.getInstanceId(),false);
//							if (logger.isInfoEnabled()) {
//								for (ResourceInstance resourceInstance : childInstanceIds) {
//									logger.info("childInstanceIds = " + resourceInstance.getId());
//								}
//							}
//							List<Long> notMonitoringInstanceIds = null;
//							if (CollectionUtils.isNotEmpty(childInstanceIds)) {
//								notMonitoringInstanceIds = new ArrayList<Long>(200);
//								for (ResourceInstance resourceInstance : childInstanceIds) {
//									if (resourceInstance != null && resourceInstance.getLifeState() == InstanceLifeStateEnum.NOT_MONITORED) {
//										notMonitoringInstanceIds.add(resourceInstance.getId());
//									}
//								}
//							}
//							if (logger.isInfoEnabled()) {
//								logger.info("notMonitoringInstanceIds = " + notMonitoringInstanceIds);
//							}
//							//把未监控的子资源加入监控(以前是不可用的接口,现在可用,加入监控)
//							if (CollectionUtils.isNotEmpty(notMonitoringInstanceIds)) {
//								profileService.autoRefreshChildEnableMonitor(instance.getInstanceId(), notMonitoringInstanceIds);
//							}
						} catch (InstancelibException e) {
							if (logger.isErrorEnabled()) {
								logger.error("auto rediscover instance discover error:" + e);
							}
						} catch (ProfilelibException e) {
							if (logger.isErrorEnabled()) {
								logger.error("auto rediscover instance enable monitor error: " + e);
							}
						} catch (Exception e) {
							StackTraceElement[] errors = e.getStackTrace();
							logger.error("auto refresh error:" + e.getMessage());
							if (null != errors && errors.length > 0) {
								for (StackTraceElement stackTraceElement : errors) {
									logger.error(stackTraceElement.toString());
								}
							}
						}
						if (logger.isInfoEnabled()) {
							logger.info("instance:" + instance.getInstanceId() + " refresh success,update execute time!");
						}
						Calendar currentCalendar = Calendar.getInstance();
						currentCalendar.set(Calendar.HOUR_OF_DAY, jobExecuteTime);
						currentCalendar.set(Calendar.MINUTE,00);
						currentCalendar.set(Calendar.SECOND,00);
						// 自动发现完成后更新策略的执行时间及下次执行时间
						instance.setExecuteTime(currentCalendar.getTime());
						currentCalendar.add(Calendar.DATE, profilelibAutoRediscover.getExecutRepeat());
						instance.setNextExecuteTime(currentCalendar.getTime());
						profileAutoRediscoveryService.updateProfileInstanceExecuteTime(instance);
					}
				}
			}
		}
		if (logger.isInfoEnabled()) {
			logger.info("Auto instance Discover end execute");
		}
	}
	
	private boolean calcExecuteTime(Date nextExecuteTime) {
		Calendar currentCalendar = Calendar.getInstance();
		Calendar nextExecuteCalendar = Calendar.getInstance();
		if (nextExecuteTime != null) {
			nextExecuteCalendar.setTime(nextExecuteTime);
		}
		// 计算执行时间
		if (logger.isInfoEnabled()) {
			StringBuilder b = new StringBuilder();
			b.append("currentDate:").append(DateUtil.format(currentCalendar.getTime()));
			b.append(" nextDate:");
			if(nextExecuteTime != null){
				b.append(DateUtil.format(nextExecuteTime));
			}
			logger.info(b.toString());
		}
		// 如果执行时间或下次执行时间为空，马上执行一次
		return (nextExecuteTime == null || nextExecuteCalendar.getTimeInMillis() <= currentCalendar.getTimeInMillis());
	}

	// 通过发现属性组装成发现参数
	private ResourceInstanceDiscoveryParameter setDiscoverParamter(List<DiscoverProp> discoverProps) {
		Map<String, String> paramter = new HashMap<String, String>();
		for (DiscoverProp discoverProp : discoverProps) {
			String[] values = discoverProp.getValues();
			String value = "";
			if (values != null && values.length > 0) {
				value = values[0];
			}
			paramter.put(discoverProp.getKey(), value);
		}
		ResourceInstanceDiscoveryParameter disParamter = new ResourceInstanceDiscoveryParameter();
		if (paramter.containsKey("collectType")) {
			disParamter.setDiscoveryWay((String) paramter.get("collectType"));
		}
		String resourceId = (String) paramter.get("resourceId");
		if (!StringUtils.isEmpty(resourceId)) {
			disParamter.setResourceId(resourceId);
			String parentId = capacityService.getResourceDefById(resourceId).getCategory().getParentCategory().getId();
			if (CapacityConst.NETWORK_DEVICE.equals(parentId) || CapacityConst.SNMPOTHERS.equals(parentId)) {
				disParamter.setAnonymousNetworkDevice(true);
			}
		}
		if (paramter.containsKey("nodeGroupId") && null != paramter.get("nodeGroupId") && !"".equals(paramter.get("nodeGroupId"))) {
			disParamter.setNodeGroupId(Integer.valueOf((String) paramter.get("nodeGroupId")));
		}
		if (paramter.get("domainId") != null) {
			disParamter.setDomainId(Long.valueOf((String) paramter.get("domainId")));
		}
		Map<String, String> discoveryInfos = new HashMap<String, String>();
		discoveryInfos.putAll(paramter);
		disParamter.setDiscoveryInfos(discoveryInfos);
		return disParamter;
	}

	private CategoryDef getParentCategoryDef(String categoryId) {
		CategoryDef categoryDef = capacityService.getCategoryById(categoryId);
		if (categoryDef != null && categoryDef.getParentCategory() != null && (!capacityService.getRootCategory().getId().equals(categoryDef.getParentCategory().getId()))) {
			categoryDef = getParentCategoryDef(categoryDef.getParentCategory().getId());
		}
		return categoryDef;
	}

	@Override
	public Map<InstanceLifeStateEnum, List<Long>> reDiscover(long instanceId,boolean isAutoDeleteChildInstance) throws InstancelibException, ProfilelibException {
		ResourceInstance instance = resourceInstanceService.getResourceInstance(instanceId);
		List<DiscoverProp> discoverProps = discoverPropService.getPropByInstanceId(instanceId);
		if(null!=instance){
			ResourceInstanceDiscoveryParameter ridp = null;
			String mainParentCategoryId = getParentCategoryDef(instance.getCategoryId()).getId();
			if (mainParentCategoryId.equals(CapacityConst.VM)) {
				ridp = new ResourceInstanceDiscoveryParameter();
				String resourceId = instance.getResourceId();
				ridp.setResourceId(resourceId);
				String discoverType = "";
				if(resourceId.equals("VMWareVCenter")){
					discoverType=DISCOVERYTYPE_VCENTER;
				}else if(resourceId.equals("vmESXi")){
					discoverType=DISCOVERYTYPE_ESXI;
				}else if(resourceId.equals("XenPool")){
					discoverType=DISCOVERYTYPE_XENPOOl;
				}else if(resourceId.equals("FusionComputeSite")){
					discoverType=DISCOVERYTYPE_FUSIONCOMPUTESITE;
				}
				ridp.setNodeGroupId(Integer.valueOf(instance.getDiscoverNode()));
				ridp.setDomainId(instance.getDomainId());
				ridp.setOnlyDiscover(true);
				Map<String, String> discoveryInfos = new HashMap<>();
				if(!CollectionUtils.isEmpty(discoverProps)){
					for (DiscoverProp discoverProp : discoverProps) {
						String key = discoverProp.getKey();
						String[] values = discoverProp.getValues();
						String value="";
						if(values!=null && values.length>0){
							value=values[0];
						}
						
						if(key.equals("ip") || key.equals("IP")){
							discoveryInfos.put("IP", value);
						}
						
						if(key.equals("username") || key.equals("userName")){
							discoveryInfos.put("username", value);
						}
						
						if(key.equals("password")){
							discoveryInfos.put("password", value);
						}
					}
				}
				discoveryInfos.put("discoveryType",discoverType);
				ridp.setDiscoveryInfos(discoveryInfos);
				ridp.setDiscoveryWay(null);
			}else{
				ridp = setDiscoverParamter(discoverProps);
				if (ridp.getNodeGroupId() <= 0) {
					ridp.setNodeGroupId(Integer.valueOf(instance.getDiscoverNode()));
				}
				if (ridp.getDomainId() <= 0) {
					ridp.setDomainId(instance.getDomainId());
				}
				ridp.setOnlyDiscover(true);
			}
			if (null!=ridp) {
				try {
					// 重新发现设备
					if (logger.isInfoEnabled()) {
						logger.info("rediscover begin discover instance:" + instanceId);
						StringBuffer stringBuffer = new StringBuffer("discover params:").append("\t");
						stringBuffer.append("discoveryWay:").append(ridp.getDiscoveryWay());
						stringBuffer.append(",domainId:").append(ridp.getDomainId());
						stringBuffer.append(",NodeGroupId:").append(ridp.getNodeGroupId());
						stringBuffer.append(",ResourceId:").append(ridp.getResourceId());
						stringBuffer.append(ridp.getDiscoveryInfos());
						logger.info(stringBuffer.toString());
					}
					DiscoverResourceIntanceResult drir = resourceInstanceDiscoveryService.discoveryResourceInstance(ridp);
					if (logger.isInfoEnabled()) {
						logger.info("rediscover discover instance " + instanceId + " end!");
					}
					if (drir.isSuccess()) {
						ResourceInstance refreshResourceInstance = drir.getResourceIntance();
						String categoryId = getParentCategoryDef(refreshResourceInstance.getCategoryId()).getId();
						if (categoryId.equals(CapacityConst.VM)) {
							// 虚拟化设备特殊处理
							Map<String, String> discoveryInfos = ridp.getDiscoveryInfos();
							if(discoveryInfos.containsKey("password")){
								String password = discoveryInfos.get("password");
								discoveryInfos.put("password", SecureUtil.pwdDecrypt(password));
							}
							ridp.setResourceInstanceId(instanceId);
							if (logger.isInfoEnabled()){
								StringBuilder bb = new StringBuilder(100);
								bb.append("execute vm refresh! instanceId=");
								bb.append(instanceId);
								bb.append(" \n execute com.mainsteam.stm.portal.vm.api.DiscoveryVmService.autoRerfreshVmResource method");
								String[] resourceTrees = refreshResourceInstance.getModulePropBykey("resourceTree");
								Map<?, ?> resourceTreeMap = JSONObject.parseObject(resourceTrees[0], HashMap.class);
								bb.append(" \n vmTreeMap:").append(resourceTreeMap);
								logger.info(bb);
							}
					 		discoveryVmService.autoRerfreshVmResource(ridp, drir);
						} else {
							if (logger.isInfoEnabled())
								logger.info("execute base instance refresh!");
							// 普通设备直接刷新
							refreshResourceInstance.setId(instanceId);
							Map<InstanceLifeStateEnum, List<Long>> result = resourceInstanceService.refreshResourceInstance(refreshResourceInstance,isAutoDeleteChildInstance);
							return result;
						}
					}
				} catch (Exception e) {
					if (logger.isErrorEnabled())
						logger.error(e.getStackTrace());
				}
			}
		}
		return null;
	}

	public void setResourceInstanceService(ResourceInstanceService resourceInstanceService) {
		this.resourceInstanceService = resourceInstanceService;
	}

	public void setCapacityService(CapacityService capacityService) {
		this.capacityService = capacityService;
	}

	public void setProfileAutoRediscoveryService(ProfileAutoRediscoverService profileAutoRediscoveryService) {
		this.profileAutoRediscoveryService = profileAutoRediscoveryService;
	}

	public void setDiscoverPropService(DiscoverPropService discoverPropService) {
		this.discoverPropService = discoverPropService;
	}

	public void setResourceInstanceDiscoveryService(ResourceInstanceDiscoveryService resourceInstanceDiscoveryService) {
		this.resourceInstanceDiscoveryService = resourceInstanceDiscoveryService;
	}

	public void setProfileService(ProfileService profileService) {
		this.profileService = profileService;
	}

	public void setDiscoveryVmService(DiscoveryVmService discoveryVmService) {
		this.discoveryVmService = discoveryVmService;
	}

	public void setProfileAutoRefreshService(ProfileAutoRefreshService profileAutoRefreshService) {
		this.profileAutoRefreshService = profileAutoRefreshService;
	}
	public void setJobExecuteTime(int jobExecuteTime) {
		this.jobExecuteTime = jobExecuteTime;
	}
}
