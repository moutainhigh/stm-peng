package com.mainsteam.stm.instancelib.service.impl;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang.StringUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.context.ApplicationListener;
import org.springframework.context.event.ContextRefreshedEvent;

import com.mainsteam.stm.caplib.CapacityService;
import com.mainsteam.stm.caplib.common.CategoryDef;
import com.mainsteam.stm.caplib.dict.CapacityConst;
import com.mainsteam.stm.caplib.dict.DiscoverWayEnum;
import com.mainsteam.stm.caplib.dict.LinkResourceConsts;
import com.mainsteam.stm.caplib.resource.ResourceDef;
import com.mainsteam.stm.errorcode.ServerErrorCodeConstant;
import com.mainsteam.stm.instancelib.CustomModulePropService;
import com.mainsteam.stm.instancelib.CustomPropService;
import com.mainsteam.stm.instancelib.DiscoverPropService;
import com.mainsteam.stm.instancelib.ExtendPropService;
import com.mainsteam.stm.instancelib.ModulePropService;
import com.mainsteam.stm.instancelib.ResourceInstanceService;
import com.mainsteam.stm.instancelib.dao.PropDAO;
import com.mainsteam.stm.instancelib.dao.PropTypeDAO;
import com.mainsteam.stm.instancelib.dao.ResourceInstanceDAO;
import com.mainsteam.stm.instancelib.dao.pojo.PropDO;
import com.mainsteam.stm.instancelib.dao.pojo.ResourceInstanceDO;
import com.mainsteam.stm.instancelib.exception.InstancelibException;
import com.mainsteam.stm.instancelib.interceptor.InstancelibEvent;
import com.mainsteam.stm.instancelib.interceptor.InstancelibEventManager;
import com.mainsteam.stm.instancelib.obj.BatchResourceInstanceResult;
import com.mainsteam.stm.instancelib.obj.CustomModuleProp;
import com.mainsteam.stm.instancelib.obj.CustomProp;
import com.mainsteam.stm.instancelib.obj.DiscoverProp;
import com.mainsteam.stm.instancelib.obj.ExtendProp;
import com.mainsteam.stm.instancelib.obj.InstanceProp;
import com.mainsteam.stm.instancelib.obj.ModuleProp;
import com.mainsteam.stm.instancelib.obj.ResourceInstance;
import com.mainsteam.stm.instancelib.obj.ResourceInstanceResult;
import com.mainsteam.stm.instancelib.objenum.EventEnum;
import com.mainsteam.stm.instancelib.objenum.InstanceLifeStateEnum;
import com.mainsteam.stm.instancelib.objenum.PropTypeEnum;
import com.mainsteam.stm.instancelib.service.CustomPropExtendService;
import com.mainsteam.stm.instancelib.service.DiscoverPropExtendService;
import com.mainsteam.stm.instancelib.service.ExatendPropExtendService;
import com.mainsteam.stm.instancelib.service.ModulePropExtendService;
import com.mainsteam.stm.instancelib.service.ResourceInstanceExtendService;
import com.mainsteam.stm.instancelib.service.bean.ComparerResult;
import com.mainsteam.stm.instancelib.service.impl.querybean.QueryResourceInstance;
import com.mainsteam.stm.instancelib.util.CustomModulePropCache;
import com.mainsteam.stm.instancelib.util.InitLoadPropKeyUtil;
import com.mainsteam.stm.instancelib.util.LicenseUtil;
import com.mainsteam.stm.instancelib.util.PropCache;
import com.mainsteam.stm.instancelib.util.PropertyConverter;
import com.mainsteam.stm.instancelib.util.ResourceComparer;
import com.mainsteam.stm.instancelib.util.ResourceInstanceCache;
import com.mainsteam.stm.instancelib.util.UniqueInstanceCache;
import com.mainsteam.stm.license.License;
import com.mainsteam.stm.license.LicenseCheckException;//zw
import com.mainsteam.stm.license.LicenseModelEnum;
import com.mainsteam.stm.platform.sequence.service.ISequence;
import com.mainsteam.stm.system.license.api.ILicenseCategoryRelation;
import com.mainsteam.stm.util.SecureUtil;

/**
 * portal ?????????????????????
 *
 * @author xiaoruqiang
 *
 */
public class ResourceInstanceServiceImpl
		implements ResourceInstanceService, ResourceInstanceExtendService, ApplicationListener<ContextRefreshedEvent> {

	private static final Log logger = LogFactory.getLog(ResourceInstanceService.class);

	protected static final int INSTANCE_NAME_MAX_LENGTH = 256;

	private ResourceInstanceDAO resourceInstanceDAO;

	private PropDAO propDAO;

	private PropTypeDAO propTypeDAO;

	private ModulePropExtendService modulePropExtendService;

	private ModulePropService modulePropService;

	private DiscoverPropService discoverPropService;

	private DiscoverPropExtendService discoverPropExtendService;

	private CustomPropService customPropService;

	private CustomPropExtendService customPropExtendService;

	private ExtendPropService extendPropService;

	private ExatendPropExtendService extendPropExtendService;

	private ISequence instanceSeq;

	private InstancelibEventManager instancelibEventManager;

	private ResourceInstanceCache cache;

	private ResourceComparer resourceComparer;

	private InitLoadPropKeyUtil initLoadPropKey;

	private CapacityService capacityService;

	private PropCache propCache;

	private CustomModulePropCache customModulePropCache;

	private CustomModulePropService customModulePropService;
	// ????????????
	private static String REGEX = "[\u4e00-\u9fa5]";
	private Pattern p;

	private ILicenseCategoryRelation licenseCategoryRelation;

	private boolean isLoadUnique = false;

	/**
	 * ??????????????????????????????????????? ??????????????????????????????????????????
	 *
	 * @throws InstancelibException
	 */
	public void init() {
		if (System.getProperty("testCase") != null) {
			return;
		}
		Lock lock = new ReentrantLock();
		long instanceId = -9999999;
		long start = 0;
		try {
			lock.lock();
			// ????????????????????????????????????????????????????????????????????????
			if (cache.get(instanceId) != null) {
				if (logger.isInfoEnabled()) {
					logger.info("remote cache has load.");
				}
				return;
			}
			if (logger.isInfoEnabled()) {
				logger.info("load resourceInstance to remote cache start.");
			}
			start = System.currentTimeMillis();
			loadAllResourceInstance();
			// ??????????????????????????????
			cache.queryAdd(instanceId, new ResourceInstance());
		} catch (Exception e) {
			e.printStackTrace();
			if (logger.isErrorEnabled()) {
				logger.error("ResourceInstanceServiceImpl init error!", e);
			}
		} finally {
			lock.unlock();
		}
		long end = System.currentTimeMillis();
		long diff = end - start;
		if (logger.isInfoEnabled()) {
			StringBuilder loadBuilder = new StringBuilder(100);
			loadBuilder.append("load resourceInstance to remote cache end time=");
			loadBuilder.append(diff / 1000.0).append(" seconds");
			logger.info(loadBuilder);
		}
	}

	protected ResourceInstanceDO convertToDO(ResourceInstance resourceInstance) {
		ResourceInstanceDO tdo = new ResourceInstanceDO();
		tdo.setInstanceId(resourceInstance.getId());
		tdo.setCategoryId(resourceInstance.getCategoryId());
		tdo.setShowIP(resourceInstance.getShowIP());
		tdo.setDiscoverNode(resourceInstance.getDiscoverNode());
		if (resourceInstance.getDiscoverWay() != null) {
			tdo.setDiscoverWay(resourceInstance.getDiscoverWay().toString());
		}
		tdo.setInstanceName(resourceInstance.getName());
		tdo.setInstanceShowName(resourceInstance.getShowName());
		tdo.setLifeState(resourceInstance.getLifeState().name());
		Long parentId = null;
		if (resourceInstance.getParentInstance() == null) {
			long tempParentId = resourceInstance.getParentId();
			if (tempParentId != 0) {
				parentId = tempParentId;
			}
		} else {
			parentId = resourceInstance.getParentInstance().getId();
		}
		tdo.setParentId(parentId);
		tdo.setResourceId(resourceInstance.getResourceId().trim());
		tdo.setInstanceType(resourceInstance.getChildType());
		tdo.setDomainId(resourceInstance.getDomainId());
		tdo.setIsCore(resourceInstance.isCore() ? "1" : "0");
		tdo.setCreateUserAccount(resourceInstance.getCreateUserAccount());
		return tdo;
	}

	private ResourceInstance convertToResourceInstance(ResourceInstanceDO tdo, ResourceInstance parentInstance) {
		ResourceInstance def = null;
		if (parentInstance != null) {
			def = new ResourceInstance(parentInstance);
		} else {
			def = new ResourceInstance();
		}
		def.setId(tdo.getInstanceId());
		String categoryId = tdo.getCategoryId();
		if (StringUtils.isNotEmpty(categoryId)) {
			def.setCategoryId(categoryId);
			CategoryDef parentCategoryDef = capacityService.getCategoryById(categoryId);
			if (parentCategoryDef != null && parentCategoryDef.getParentCategory() != null) {
				def.setParentCategoryId(parentCategoryDef.getParentCategory().getId());
			}
		}
		def.setShowIP(tdo.getShowIP());
		def.setDiscoverNode(tdo.getDiscoverNode());
		if (StringUtils.isEmpty(tdo.getDiscoverWay())) {
			def.setDiscoverWay(DiscoverWayEnum.NONE);
		} else {
			def.setDiscoverWay(DiscoverWayEnum.valueOf(tdo.getDiscoverWay()));
		}
		def.setName(tdo.getInstanceName());
		def.setShowName(tdo.getInstanceShowName());
		def.setLifeState(InstanceLifeStateEnum.valueOf(tdo.getLifeState()));
		def.setParentId(tdo.getParentId() == null ? 0 : tdo.getParentId());
		def.setResourceId(tdo.getResourceId());
		def.setChildType(tdo.getInstanceType());
		def.setDomainId(tdo.getDomainId());
		def.setCreateUserAccount(tdo.getCreateUserAccount());
		return def;
	}

	private QueryResourceInstance convertTOQueryResourceInstance(ResourceInstance resourceInstance) {
		return new QueryResourceInstance(resourceInstance, this);
	}

	/**
	 * ??????????????????
	 *
	 * @param tdo
	 */
	protected void validate(ResourceInstance resourceInstance, boolean isParent) {
		// ????????????????????????,????????????????????????256,????????????????????????????????????3?????????
		String instanceName = resourceInstance.getName();
		if (StringUtils.isNotEmpty(instanceName)) {
			instanceName = instanceName.trim();
			p = Pattern.compile(REGEX);
			Matcher m = p.matcher(instanceName);
			int ccCount = 0;
			while (m.find()) {
				ccCount++;
			}
			int act_length = ccCount * 3 + (instanceName.length() - ccCount);
			if (act_length > INSTANCE_NAME_MAX_LENGTH) {
				instanceName = instanceName.substring(0, INSTANCE_NAME_MAX_LENGTH / 3);
				if (logger.isInfoEnabled()) {
					StringBuilder b = new StringBuilder();
					b.append("ResourceInstance length of more than 256,name has to be intercepted");
					b.append(" old name:").append(resourceInstance.getName());
					b.append(" new name:").append(instanceName);
					logger.info("ResourceInstance length of more than 256,name has to be intercepted");
				}
			}
		}
		resourceInstance.setName(instanceName);
		String showName = resourceInstance.getShowName();
		if (StringUtils.isNotEmpty(showName)) {
			showName = showName.trim();
			p = Pattern.compile(REGEX);
			Matcher m = p.matcher(showName);
			int ccCount = 0;
			while (m.find()) {
				ccCount++;
			}
			int act_length = ccCount * 3 + (showName.length() - ccCount);
			if (act_length > INSTANCE_NAME_MAX_LENGTH) {
				showName = showName.substring(0, INSTANCE_NAME_MAX_LENGTH / 3);
				if (logger.isInfoEnabled()) {
					StringBuilder b = new StringBuilder();
					b.append("ResourceInstance length of more than 256,showName has to be intercepted");
					b.append(" old showName:").append(resourceInstance.getShowName());
					b.append(" new showName:").append(showName);
					logger.info("ResourceInstance length of more than 256,name has to be intercepted");
				}
			}
		}
		resourceInstance.setShowName(showName);
		if (isParent) {
			String categoryId = resourceInstance.getCategoryId();
			if (StringUtils.isNotEmpty(categoryId)) {
				CategoryDef parentCategoryDef = capacityService.getCategoryById(categoryId);
				if (parentCategoryDef != null && parentCategoryDef.getParentCategory() != null) {
					resourceInstance.setParentCategoryId(parentCategoryDef.getParentCategory().getId());
				}
			}
		}
		List<ResourceInstance> children = resourceInstance.getChildren();
		if (CollectionUtils.isNotEmpty(children)) {
			for (ResourceInstance child : children) {
				validate(child, false);
			}
		}
	}

	@Override
	public synchronized ResourceInstanceResult addResourceInstance(ResourceInstance resourceInstance)
			throws InstancelibException {
		if (resourceInstance == null) {
			if (logger.isInfoEnabled()) {
				StringBuilder b = new StringBuilder();
				b.append("addResourceInstance start resourceInstance=null");
				b.append("addResourceInstance end resourceInstance=null");
				logger.info(b);
			}
			return null;
		}
		long start = System.currentTimeMillis();
		if (logger.isInfoEnabled()) {
			StringBuilder b = new StringBuilder();
			b.append("addResourceInstance start name=");
			b.append(resourceInstance.getName());
			b.append(" ShowIP:");
			b.append(resourceInstance.getShowIP());
			b.append(" domainId:");
			b.append(resourceInstance.getDomainId());
			logger.info(b);
		}
		LicenseUtil licenseUtil = null;
		// ????????????License?????????License??????
		boolean isCalcLicense = false;
		try {
			isCalcLicense = checkLicense(resourceInstance);
		} catch (InstancelibException e2) {
			throw new InstancelibException(e2.getCode(), e2.getMessage());
		}

		if (isCalcLicense) {
			licenseUtil = LicenseUtil.getLicenseUtil();
			// ??????License??????
			licenseUtil.addLicenseStorageNum();
		}

		/*
		 * ??????????????????????????????
		 */
		if (resourceInstance.isRepeatValidate()) {
			ComparerResult comparerResult = null;
			try {
				comparerResult = addCalcRepeat(resourceInstance);
			} catch (InstancelibException e) {
				if (isCalcLicense) {
					licenseUtil.deleteLicenseStorageNum();
					licenseUtil.deleteLicenseStorageNum();
				}
				if (logger.isErrorEnabled()) {
					logger.error("", e);
					throw e;
				}
			} catch (Exception e) {
				if (isCalcLicense) {
					licenseUtil.deleteLicenseStorageNum();
					licenseUtil.deleteLicenseStorageNum();
				}
				if (logger.isErrorEnabled()) {
					logger.error("", e);
				}
			}
			if (comparerResult != null) {
				if (comparerResult.getInstanceId() != 0) {
					// ????????????????????????????????????????????????????????????????????????
					if (comparerResult.getLifeState().equals(InstanceLifeStateEnum.DELETED)
							|| comparerResult.getLifeState().equals(InstanceLifeStateEnum.INITIALIZE)) {
						resourceInstance.setId(comparerResult.getInstanceId());
						if (logger.isDebugEnabled()) {
							logger.debug("add resourceInstance refresh:" + resourceInstance.getName());
						}
						Map<InstanceLifeStateEnum, List<Long>> refreshResult = null;
						if (resourceInstance.isAutoRefresh()) {
							validate(resourceInstance, true);
							refreshResult = refreshResourceInstance(resourceInstance);
						}
						long end = System.currentTimeMillis();
						long diff = end - start;
						if (logger.isInfoEnabled()) {
							StringBuilder b = new StringBuilder();
							b.append("addResourceInstance end name=");
							b.append(resourceInstance.getName());
							b.append(" showIP:");
							b.append(resourceInstance.getShowIP());
							b.append(" time:").append(diff / 1000.0).append(" seconds");
							logger.info(b);
						}
						if (isCalcLicense) {
							licenseUtil.deleteLicenseStorageNum();
						}
						return new ResourceInstanceResult(comparerResult.getInstanceId(), false,
								comparerResult.getRepeatIds(), refreshResult);
					} else {
						if (logger.isDebugEnabled()) {
							logger.debug("add resourceInstance repeat:" + resourceInstance.getName());
						}
						if (isCalcLicense) {
							licenseUtil.deleteLicenseStorageNum();
							licenseUtil.deleteLicenseStorageNum();
						}
						return new ResourceInstanceResult(comparerResult.getInstanceId(), true,
								comparerResult.getRepeatIds());
					}

				}
			}
		}

		validate(resourceInstance, true);

		List<ResourceInstance> allInstance = new ArrayList<>();

		List<PropDO> allPropDO = new ArrayList<PropDO>();
		long instanceId = 0;
		if (resourceInstance.getGeneratePrimaryKey()) {
			instanceId = instanceSeq.next();
			resourceInstance.setId(instanceId);
		}
		instanceId = resourceInstance.getId();
		convertToList(allInstance, allPropDO, resourceInstance, true);
		List<PropDO> allPropTypeDO = new ArrayList<>();
		HashSet<String> set = new HashSet<>();
		for (PropDO propDO : allPropDO) {
			String key = propDO.getInstanceId() + propDO.getPropKey() + propDO.getPropType();
			if (!set.contains(key)) {
				set.add(key);
				allPropTypeDO.add(propDO);
			}
		}
		set = null;
		/*
		 * ????????????????????????
		 */

		List<ResourceInstanceDO> allInstanceDO = new ArrayList<>();
		for (ResourceInstance tempInstance : allInstance) {
			allInstanceDO.add(convertToDO(tempInstance));
		}
		long dataStartTime = System.currentTimeMillis();
		try {
			// ??????????????????
			resourceInstanceDAO.insertResourceInstances(allInstanceDO);
			if (!allPropDO.isEmpty()) {
				if (logger.isInfoEnabled()) {
					for (PropDO propDO : allPropDO) {
						if (propDO.getPropValue() != null) {
							if (propDO.getPropValue().length() >= 699) {
								StringBuilder b = new StringBuilder(100);
								b.append("PropDO value >= 699 : valueSize=").append(propDO.getPropValue().length())
										.append(" ");
								b.append(propDO.getPropKey()).append(" ");
								b.append(propDO.getPropType()).append(" ");
								b.append(propDO.getPropValue());
								String result = propDO.getPropValue().substring(0, 699);
								propDO.setPropValue(result);
								b.append(" \n insert value=");
								b.append(result);
								logger.info(b);
							}
						} else {
							for (ResourceInstanceDO resourceInstanceDO : allInstanceDO) {
								if (resourceInstanceDO.getInstanceId() == propDO.getInstanceId()) {
									StringBuilder b = new StringBuilder(100);
									b.append("ResourceId=").append(resourceInstanceDO.getResourceId());
									b.append(" PropDO-key=").append(propDO.getPropKey());
									b.append(" type=").append(propDO.getPropType());
									b.append(",propValue is null");
									logger.info(b);
									break;
								}
							}
						}
					}
				}
				// ????????????????????????
				propDAO.insertPropDOs(allPropDO);
			}
			if (!allPropTypeDO.isEmpty()) {
				// ????????????????????????
				propTypeDAO.insertPropTypeDOs(allPropTypeDO);
			}
		} catch (Exception e1) {
			if (logger.isErrorEnabled()) {
				logger.error("addResourceInstance error", e1);
			}
			if (isCalcLicense) {
				licenseUtil.deleteLicenseStorageNum();
				licenseUtil.deleteLicenseStorageNum();
			}
			throw new InstancelibException(ServerErrorCodeConstant.ERR_SERVER_OPERATE_DATABASE_ERROR,
					"addResourceInstance error");
		}
		long dataEndTime = System.currentTimeMillis();
		long dataDiff = dataEndTime - dataStartTime;
		if (logger.isInfoEnabled()) {
			StringBuilder dbBuilder = new StringBuilder(50);
			dbBuilder.append("db time:").append(dataDiff / 1000.0).append(" seconds");
			logger.info(dbBuilder);
		}

		long interceptorStartTime = System.currentTimeMillis();
		// ????????????????????????
		final InstancelibEvent instancelibEvent = new InstancelibEvent(Arrays.asList(resourceInstance), null,
				EventEnum.INSTANCE_ADD_EVENT);
		// ????????????
		try {
			interceptorNotification(instancelibEvent);
		} catch (Exception e) {
			if (logger.isErrorEnabled()) {
				logger.error("addResourceInstance interceptorNotification error", e);
			}
			if (isCalcLicense) {
				licenseUtil.deleteLicenseStorageNum();
				licenseUtil.deleteLicenseStorageNum();
			}
			throw new InstancelibException(ServerErrorCodeConstant.ERR_SERVER_RESPO_INTERCEPTOR,
					"Add resource instance interceptor error!");
		}
		long interceptorEndTime = System.currentTimeMillis();
		long interceptorDiff = interceptorEndTime - interceptorStartTime;
		if (logger.isInfoEnabled()) {
			StringBuilder interceptorBuilder = new StringBuilder(50);
			interceptorBuilder.append("interceptor time:").append(interceptorDiff / 1000.0).append(" seconds");
			logger.info(interceptorBuilder);
		}

		long addCacheStartTime = System.currentTimeMillis();
		HashSet<Long> children = new HashSet<>(allInstance.size() - 1);
		// ??????????????????
		for (ResourceInstance tempInstance : allInstance) {
			List<ResourceInstance> childrens = tempInstance.getChildren() == null ? null
					: new ArrayList<>(tempInstance.getChildren());
			List<ModuleProp> moduleProps = tempInstance.getModuleProps() == null ? null
					: new ArrayList<>(tempInstance.getModuleProps());
			List<DiscoverProp> discoverProps = tempInstance.getDiscoverProps() == null ? null
					: new ArrayList<>(tempInstance.getDiscoverProps());
			tempInstance.setChildren(null);
			tempInstance.setModuleProps(null);
			tempInstance.setDiscoverProps(null);
			cache.queryAdd(tempInstance.getId(), tempInstance);
			tempInstance.setChildren(childrens);
			tempInstance.setModuleProps(moduleProps);
			tempInstance.setDiscoverProps(discoverProps);
			if (tempInstance.getParentId() != 0) {
				children.add(tempInstance.getId());
			}
		}
		/*
		 * ?????????????????????
		 */
		// ??????????????????????????????
		if (resourceInstance.getParentId() != 0) {
			cache.addChildInstanceList(resourceInstance.getParentId(), children);
		} else {
			// ?????????????????????????????????
			cache.addChildInstanceList(resourceInstance.getId(), children);
			// ?????????????????????
			cache.addParentInsatnceList(resourceInstance.getId());
			//????????????????????????????????????
			UniqueInstanceCache.getInstanceCache().addUnique(resourceInstance);
		}
		long addCacheEndTime = System.currentTimeMillis();
		long addCacheDiff = addCacheEndTime - addCacheStartTime;
		if (logger.isInfoEnabled()) {
			StringBuilder addCacheBuilder = new StringBuilder(50);
			addCacheBuilder.append("add to cache time:").append(addCacheDiff / 1000.0).append(" seconds");
			logger.info(addCacheBuilder);
		}
		// ????????????
		listenerNotification(instancelibEvent);

		long end = System.currentTimeMillis();
		long diff = end - start;
		if (logger.isInfoEnabled()) {
			StringBuilder b = new StringBuilder();
			b.append("addResourceInstance end name=");
			b.append(resourceInstance.getName());
			b.append(" showIP:");
			b.append(resourceInstance.getShowIP());
			b.append(" domainId:");
			b.append(resourceInstance.getDomainId());
			b.append(" time:").append(diff / 1000.0).append(" seconds");
			logger.info(b);
		}
		if (isCalcLicense) {
			licenseUtil.deleteLicenseStorageNum();
		}
		return new ResourceInstanceResult(instanceId, false, null);
	}

	@Override
	public synchronized BatchResourceInstanceResult addResourceInstances(
			List<ResourceInstance> resourceInstances)
			throws InstancelibException {
		if (logger.isInfoEnabled()) {
			StringBuilder b = new StringBuilder();
			b.append("batch addResourceInstance start");
			logger.info(b);
		}
		BatchResourceInstanceResult result = null;
		if(CollectionUtils.isEmpty(resourceInstances)){
			StringBuilder b = new StringBuilder();
			b.append("parameters is empty");
			b.append("batch addResourceInstance end");
			logger.info(b);
			return result;
		}
		long start = System.currentTimeMillis();
		if (logger.isInfoEnabled()) {
			StringBuilder b = new StringBuilder();
			for (ResourceInstance tempResourceInstance : resourceInstances) {
				b.append("[Name:");
				b.append(tempResourceInstance.getName());
				b.append(" ShowIP:");
				b.append(tempResourceInstance.getShowIP());
				b.append(" domainId:");
				b.append(tempResourceInstance.getDomainId());
				b.append("]");
			}
			logger.info(b);
		}
		/*
		 * ??????????????????????????????????????????????????????????????? DELETED???INITIALIZE?????????
		 */
		List<ResourceInstance> addResourceInstances = new ArrayList<ResourceInstance>();
		//?????????????????????????????????????????????????????? DELETED???INITIALIZE
		List<ResourceInstance> updateResourceInstances = new ArrayList<ResourceInstance>();
		//??????????????? DELETED???INITIALIZE????????????????????????????????????????????????????????????????????????
		List<ResourceInstance> deletedResourceInstances = new ArrayList<ResourceInstance>();
		//????????????????????????????????????????????? DELETED???INITIALIZE????????????????????????????????????????????????????????????
		List<ResourceInstance> refreshResourceInstances = new ArrayList<ResourceInstance>();

		for (ResourceInstance resourceInstance : resourceInstances) {
			/*
			 * ??????????????????????????????
			 */
			if (resourceInstance.isRepeatValidate()) {
				ComparerResult comparerResult = null;
				try {
					comparerResult = addCalcRepeat(resourceInstance);
				} catch (InstancelibException e) {
					if (logger.isErrorEnabled()) {
						logger.error("", e);
						throw e;
					}
				} catch (Exception e) {
					if (logger.isErrorEnabled()) {
						logger.error("", e);
					}
				}
				if (comparerResult != null) {
					if (comparerResult.getInstanceId() != 0) {
						// ????????????????????????????????????????????????????????????????????????
						resourceInstance.setId(comparerResult.getInstanceId());
						if (comparerResult.getLifeState().equals(InstanceLifeStateEnum.DELETED)
								|| comparerResult.getLifeState().equals(InstanceLifeStateEnum.INITIALIZE)) {
							if (logger.isDebugEnabled()) {
								logger.debug("add resourceInstance DELETED or INITIALIZE refresh:" + resourceInstance.getName());
							}
							deletedResourceInstances.add(resourceInstance);
						} else {
							if (logger.isDebugEnabled()) {
								logger.debug("add resourceInstance repeat:" + resourceInstance.getName());
							}
							updateResourceInstances.add(resourceInstance);
						}
						//?????????????????????
						refreshResourceInstances.add(resourceInstance);
					}else{
						addResourceInstances.add(resourceInstance);
					}
				}else{
					addResourceInstances.add(resourceInstance);
				}
			}
		}

		long dataEndTime = System.currentTimeMillis();
		long dataDiff = dataEndTime - start;
		if (logger.isInfoEnabled()) {
			StringBuilder dbBuilder = new StringBuilder(50);
			dbBuilder.append("repeat calc time:").append(dataDiff / 1000.0).append(" seconds");
			logger.info(dbBuilder);
		}
		long addResourceStartTime = System.currentTimeMillis();
		//??????????????????????????????
		try {
			List<ResourceInstance> allInstance = new ArrayList<>();
			List<ResourceInstanceDO> allInstanceDO = new ArrayList<>();
			List<PropDO> allPropTypeDO = new ArrayList<>();
			List<PropDO> allPropDO = new ArrayList<PropDO>();

			for (ResourceInstance addResourceInstance : addResourceInstances) {
				validate(addResourceInstance, true);
				long instanceId = 0;
				if (addResourceInstance.getGeneratePrimaryKey()) {
					instanceId = instanceSeq.next();
					addResourceInstance.setId(instanceId);
				}
				//instanceId = addResourceInstance.getId();
				//????????????????????????????????????
				convertToList(allInstance, allPropDO, addResourceInstance, false);
			}
			HashSet<String> set = new HashSet<>();
			for (PropDO propDO : allPropDO) {
				String key = propDO.getInstanceId() + propDO.getPropKey() + propDO.getPropType();
				if (!set.contains(key)) {
					set.add(key);
					allPropTypeDO.add(propDO);
				}
			}
			set = null;
			/*
			 * ????????????????????????
			 */
			for (ResourceInstance tempInstance : allInstance) {
				allInstanceDO.add(convertToDO(tempInstance));
			}

			if (!allPropDO.isEmpty()) {
				if (logger.isInfoEnabled()) {
					for (PropDO propDO : allPropDO) {
						if (propDO.getPropValue() != null) {
							if (propDO.getPropValue().length() >= 699) {
								StringBuilder b = new StringBuilder(100);
								b.append("PropDO value >= 699 : valueSize=").append(propDO.getPropValue().length())
										.append(" ");
								b.append(propDO.getPropKey()).append(" ");
								b.append(propDO.getPropType()).append(" ");
								b.append(propDO.getPropValue());
								String propResult = propDO.getPropValue().substring(0, 699);
								propDO.setPropValue(propResult);
								b.append(" \n insert value=");
								b.append(propResult);
								logger.info(b);
							}
						} else {
							for (ResourceInstanceDO resourceInstanceDO : allInstanceDO) {
								if (resourceInstanceDO.getInstanceId() == propDO.getInstanceId()) {
									StringBuilder b = new StringBuilder(100);
									b.append("ResourceId=").append(resourceInstanceDO.getResourceId());
									b.append(" PropDO-key=").append(propDO.getPropKey());
									b.append(" type=").append(propDO.getPropType());
									b.append(",propValue is null");
									logger.info(b);
									break;
								}
							}
						}
					}
				}

			}
			//long dataStartTime = System.currentTimeMillis();
			// ??????????????????
			if(!allInstanceDO.isEmpty()){
					logger.info("insert dao");
				for (ResourceInstanceDO dao : allInstanceDO) {
					logger.info(dao.getInstanceId() + " " + dao.getInstanceName());
				}
				resourceInstanceDAO.insertResourceInstances(allInstanceDO);
			}
			if (!allPropTypeDO.isEmpty()) {
				// ????????????????????????
				propTypeDAO.insertPropTypeDOs(allPropTypeDO);
			}
			if(!allPropDO.isEmpty()){
				// ????????????????????????
				propDAO.insertPropDOs(allPropDO);
			}
		} catch (Exception e1) {
			if (logger.isErrorEnabled()) {
				logger.error("batch addResourceInstance error", e1);
			}
			throw new InstancelibException(ServerErrorCodeConstant.ERR_SERVER_OPERATE_DATABASE_ERROR,
					"batch addResourceInstance error");
		}
		long addResourceEndTime = System.currentTimeMillis();
		dataDiff = addResourceEndTime - addResourceStartTime;
		if (logger.isInfoEnabled()) {
			StringBuilder dbBuilder = new StringBuilder(50);
			dbBuilder.append("batch addResource  time:").append(dataDiff / 1000.0).append(" seconds");
			logger.info(dbBuilder);
		}
		addResourceStartTime = System.currentTimeMillis();
		//????????????????????????..
		try {
			refreshResourceInstances(refreshResourceInstances);
		} catch (Exception e) {
			if (logger.isErrorEnabled()) {
				logger.error("batch refreshResourceInstance error", e);
			}
			throw new InstancelibException(ServerErrorCodeConstant.ERR_SERVER_OPERATE_DATABASE_ERROR,
					"batch addResourceInstance error");
		}
		dataDiff = System.currentTimeMillis() - addResourceStartTime;
		if (logger.isInfoEnabled()) {
			StringBuilder dbBuilder = new StringBuilder(50);
			dbBuilder.append("batch refreshResource  time:").append(dataDiff / 1000.0).append(" seconds");
			logger.info(dbBuilder);
		}
		long interceptorStartTime = System.currentTimeMillis();
		// ????????????????????????
		final InstancelibEvent addInstancelibEvent = new InstancelibEvent(addResourceInstances, null,
				EventEnum.INSTANCE_ADD_EVENT);
		// ????????????
		try {
			interceptorNotification(addInstancelibEvent);
		} catch (Exception e) {
			if (logger.isErrorEnabled()) {
				logger.error("batch addResourceInstance interceptorNotification error", e);
			}
			throw new InstancelibException(ServerErrorCodeConstant.ERR_SERVER_RESPO_INTERCEPTOR,
					"Add resource instance interceptor error!");
		}
		// ???????????????????????????
		final InstancelibEvent updateInstancelibEvent = new InstancelibEvent(updateResourceInstances, updateResourceInstances,
				EventEnum.INSTANCE_REFRESH_EVENT);
		// ????????????
		try {
			interceptorNotification(updateInstancelibEvent);
		} catch (Exception e) {
			if (logger.isErrorEnabled()) {
				logger.error("refreshResourceInstance interceptorNotification error!", e);
			}
			throw new InstancelibException(ServerErrorCodeConstant.ERR_SERVER_RESPO_INTERCEPTOR,
					"refreshResourceInstance interceptor error!");
		}

		//???????????????
		long interceptorEndTime = System.currentTimeMillis();
		long interceptorDiff = interceptorEndTime - interceptorStartTime;
		if (logger.isInfoEnabled()) {
			StringBuilder interceptorBuilder = new StringBuilder(50);
			interceptorBuilder.append("interceptor time:").append(interceptorDiff / 1000.0).append(" seconds");
			logger.info(interceptorBuilder);
		}

		long addCacheStartTime = System.currentTimeMillis();
		// ??????????????????
		long cacheStartTime = System.currentTimeMillis();
		//????????????
		for (ResourceInstance repeat : refreshResourceInstances) {
			List<ModuleProp> moduleProps = repeat.getModuleProps() == null ? null
					: new ArrayList<>(repeat.getModuleProps());
			List<DiscoverProp> discoverProps = repeat.getDiscoverProps() == null ? null
					: new ArrayList<>(repeat.getDiscoverProps());
			repeat.setModuleProps(null);
			repeat.setDiscoverProps(null);
			// ?????????????????????????????????
			cache.queryAdd(repeat.getId(), repeat);
			// ???????????????????????????
			//propCache.remove(repeat.getId());
			repeat.setModuleProps(moduleProps);
			repeat.setDiscoverProps(discoverProps);
//			cache.addParentInsatnceList(repeat.getId());
		}
		//????????????
		for (ResourceInstance repeat : addResourceInstances) {
			List<ModuleProp> moduleProps = repeat.getModuleProps() == null ? null
					: new ArrayList<>(repeat.getModuleProps());
			List<DiscoverProp> discoverProps = repeat.getDiscoverProps() == null ? null
					: new ArrayList<>(repeat.getDiscoverProps());
			repeat.setModuleProps(null);
			repeat.setDiscoverProps(null);
			// ?????????????????????????????????
			cache.queryAdd(repeat.getId(), repeat);
			// ???????????????????????????
			//propCache.remove(repeat.getId());
			repeat.setModuleProps(moduleProps);
			repeat.setDiscoverProps(discoverProps);
			cache.addParentInsatnceList(repeat.getId());
			//???????????????????????? ????????????????????????????????????
			UniqueInstanceCache.getInstanceCache().addUnique(repeat);
		}
		long cacheEndTime = System.currentTimeMillis();
		if (logger.isInfoEnabled()) {
			logger.info("Operation cache complete; use time:"
					+ ((cacheEndTime - cacheStartTime) / 1000) + "s!");
		}
		long addCacheEndTime = System.currentTimeMillis();
		long addCacheDiff = addCacheEndTime - addCacheStartTime;
		if (logger.isInfoEnabled()) {
			StringBuilder addCacheBuilder = new StringBuilder(50);
			addCacheBuilder.append("add to cache time:").append(addCacheDiff / 1000.0).append(" seconds");
			logger.info(addCacheBuilder);
		}
		// ????????????
		listenerNotification(addInstancelibEvent);
		listenerNotification(updateInstancelibEvent);
		if(CollectionUtils.isNotEmpty(deletedResourceInstances)){
			addResourceInstances.addAll(deletedResourceInstances);
		}
		result = new BatchResourceInstanceResult(addResourceInstances, updateResourceInstances);
		long end = System.currentTimeMillis();
		long diff = end - start;
		if (logger.isInfoEnabled()) {
			StringBuilder b = new StringBuilder();
			b.append("batch addResourceInstance end ");
			b.append(" time:").append(diff / 1000.0).append(" seconds");
			logger.info(b);
		}
		return result;
	}

	private ComparerResult addCalcRepeat(ResourceInstance resourceInstance) throws InstancelibException{
		long start = System.currentTimeMillis();
		if (logger.isInfoEnabled()) {
			StringBuilder b = new StringBuilder(100);
			b.append("repeat start:");
			b.append(resourceInstance.getName());
			logger.info(b);
		}
		ComparerResult comparerResult = null;
		// ?????????????????????
		if (resourceInstance.getParentInstance() != null || resourceInstance.getParentId() > 0) {
			/**
			 * ?????????????????????,?????????????????????????????????
			 */
			List<ResourceInstance> children = loadChildInstanceByParentId(resourceInstance.getParentId(), true);
			if (CollectionUtils.isNotEmpty(children)) {
				// ????????????????????????????????????????????????
				comparerResult = resourceComparer.childResourceInstanceSameDevice(children, resourceInstance);
			}
		} else {
			CategoryDef category = capacityService
					.getCategoryById(resourceInstance.getCategoryId());
			if(category.getParentCategory() != null){
				resourceInstance.setParentCategoryId(category.getParentCategory().getId());
			}
			HashSet<Long> uniqueResult = UniqueInstanceCache.getInstanceCache().getUnique(resourceInstance);
			if(uniqueResult != null){
				//????????????????????????????????????????????????
				comparerResult = new ComparerResult();
				List<Long> repeatIds = new ArrayList<>();
				for (long id : uniqueResult) {
					ResourceInstance tempInstance = getResourceInstanceById(id);
					if(tempInstance == null){
						continue;
					}
					comparerResult.setInstanceId(id);
					comparerResult.setLifeState(tempInstance.getLifeState());
					if (InstanceLifeStateEnum.NOT_MONITORED == tempInstance.getLifeState()
							|| InstanceLifeStateEnum.MONITORED == tempInstance.getLifeState()) {
						repeatIds.add(tempInstance.getId());
					}
					if (logger.isInfoEnabled()) {
						StringBuilder b = new StringBuilder(100);
						b.append("find instance ");
						b.append("oldInstanceId:").append(tempInstance.getId());
						b.append(" oldshowIP:").append(tempInstance.getShowIP());
					    b.append(" oldInstanceName:").append(tempInstance.getName());
						b.append(" newShowIP:").append(resourceInstance.getShowIP());
					    b.append(" newInstanceName:").append(resourceInstance.getName());
					    logger.info(b);
					}
				}
				comparerResult.setRepeatIds(repeatIds);
				return comparerResult;
			}
			// ???????????????
			comparerResult = parentInstanceValidate(resourceInstance);
		}
		long end = System.currentTimeMillis();
		long diff = end - start;
		if (logger.isInfoEnabled()) {
			StringBuilder b = new StringBuilder(100);
			b.append("repeat end:");
			b.append(resourceInstance.getName());
			b.append("time:").append(diff / 1000.0).append(" second");
			logger.info(b);
		}
		return comparerResult;
	}


	/**
	 * ???????????????????????????????????????
	 *
	 * @param parentId
	 *            ?????????Id
	 * @param isContainDeleted
	 *            ???????????????????????????
	 * @return
	 * @throws InstancelibException
	 */
	private List<ResourceInstance> loadChildInstanceByParentId(long parentId, boolean isContainDeleted)
			throws InstancelibException {
		if (logger.isTraceEnabled()) {
			StringBuilder b = new StringBuilder(100);
			b.append("loadChildInstanceByParentId start parentId=");
			b.append(parentId);
			b.append(" isContainDeleted=").append(isContainDeleted);
			logger.trace(b);
		}
		ResourceInstance parent = getResourceInstanceById(parentId);
		if (parent != null) {
			if ("StandardService".equals(parent.getParentCategoryId())) {
				return null;
			}
		}
		/**
		 * ?????????????????????
		 */
		HashSet<Long> children = cache.getChildInstance(parentId);
		List<ResourceInstance> result = null;
		if (children != null) {
			result = new ArrayList<>(children.size());
			for (long resourceInstanceId : children) {
				ResourceInstance resourceInstance = getResourceInstanceById(resourceInstanceId);
				if (resourceInstance != null) {
					if (isContainDeleted) {
						result.add(resourceInstance);
					} else {
						if (resourceInstance.getLifeState() != InstanceLifeStateEnum.DELETED
								&& resourceInstance.getLifeState() != InstanceLifeStateEnum.INITIALIZE) {
							result.add(resourceInstance);
						}
					}
				}
			}
			return result;
		}

		// ???????????????????????????
		ResourceInstanceDO queryDo = new ResourceInstanceDO();
		queryDo.setParentId(parentId);
		List<ResourceInstanceDO> resourceInstanceDOs = null;
		try {
			resourceInstanceDOs = resourceInstanceDAO.getInstancesByResourceDO(queryDo);
		} catch (Exception e) {
			if (logger.isErrorEnabled()) {
				logger.error("getChildInstanceByParentId error", e);
			}
			throw new InstancelibException(ServerErrorCodeConstant.ERR_SERVER_OPERATE_DATABASE_ERROR,
					"getChildInstanceByParentId error!");
		}
		if (CollectionUtils.isNotEmpty(resourceInstanceDOs)) {
			result = new ArrayList<ResourceInstance>(resourceInstanceDOs.size());
			HashSet<Long> childIds = new HashSet<Long>(resourceInstanceDOs.size());
			ResourceInstance parentResourceInstance = getResourceInstanceById(parentId);
			for (ResourceInstanceDO resourceInstanceDO : resourceInstanceDOs) {
				ResourceInstance resourceInstance = convertToResourceInstance(resourceInstanceDO,
						parentResourceInstance);
				QueryResourceInstance queryResourceInstance = convertTOQueryResourceInstance(resourceInstance);
				if (isContainDeleted) {
					result.add(queryResourceInstance);
				} else {
					if (!InstanceLifeStateEnum.DELETED.name().equals(resourceInstanceDO.getLifeState())) {
						result.add(queryResourceInstance);
					}
				}
				cache.queryAdd(resourceInstance.getId(), resourceInstance);
				childIds.add(resourceInstance.getId());
			}
			cache.setChildInstance(parentId, childIds);
		}
		if (logger.isTraceEnabled()) {
			StringBuilder b = new StringBuilder(100);
			b.append("loadChildInstanceByParentId end parentId=");
			b.append(parentId);
			b.append(" isContainDeleted=").append(isContainDeleted);
			logger.trace(b);
		}
		return result;
	}

	private ComparerResult refreshCalcRepeat(String childrenMd5, ResourceInstance validateInstance,
			Map<String, Map<String, Long>> oldInstanceMd5forId) {
		if (logger.isDebugEnabled()) {
			StringBuilder b = new StringBuilder(100);
			b.append("refresh resourceInstance repeat validate start: validateInstanceName=");
			b.append(validateInstance.getName());
			logger.debug(b);
		}
		ComparerResult comparerResult = null;
		if (null != childrenMd5 && !childrenMd5.isEmpty()) {
			String newInstanceMd5Str = resourceComparer.getChildResourceInstanceMd5Str(validateInstance);
			// ????????????????????????????????????????????????
			boolean isSame = resourceComparer.childResourceInstanceSameDevice(childrenMd5, newInstanceMd5Str);
			if (isSame) {
				Map<String, Long> childInstanceIds = oldInstanceMd5forId.get(validateInstance.getChildType());
				ResourceInstance oldInstance = null;
				if (null != childInstanceIds && childInstanceIds.size() > 0) {
					oldInstance = loadResourceInstance(childInstanceIds.get(newInstanceMd5Str));
				}
				if (null != oldInstance) {
					comparerResult = new ComparerResult();
					comparerResult.setInstanceId(oldInstance.getId());
					comparerResult.setLifeState(oldInstance.getLifeState());
				}
			}
		}
		if (logger.isDebugEnabled()) {
			StringBuilder b = new StringBuilder(100);
			b.append("refresh resourceInstance repeat validate end: validateInstanceName=");
			b.append(validateInstance.getName());
			logger.debug(b);
		}
		return comparerResult;
	}

	// private ComparerResult refreshCalcRepeat(List<ResourceInstance>
	// children,ResourceInstance validateInstance){
	// if (logger.isDebugEnabled()) {
	// StringBuilder b = new StringBuilder(100);
	// b.append("refresh resourceInstance repeat validate start:
	// validateInstanceName=");
	// b.append(validateInstance.getName());
	// logger.debug(b);
	// }
	// ComparerResult comparerResult = null;
	// if (CollectionUtils.isNotEmpty(children)) {
	// //????????????????????????????????????????????????
	// comparerResult = resourceComparer.childResourceInstanceSameDevice(
	// children, validateInstance);
	// }
	// if (logger.isDebugEnabled()) {
	// StringBuilder b = new StringBuilder(100);
	// b.append("refresh resourceInstance repeat validate start:
	// validateInstanceName=");
	// b.append(validateInstance.getName());
	// logger.debug(b);
	// }
	// return comparerResult;
	// }

	@Override
	public synchronized void addResourceInstanceForLink(List<ResourceInstance> resourceInstances, boolean isAll)
			throws InstancelibException {
		//license ????????????
		LicenseUtil.getLicenseUtil().cleanLicense();
		if (resourceInstances == null || resourceInstances.isEmpty()) {
			if (logger.isErrorEnabled()) {
				logger.error("addResourceInstance for link null");
			}
			return;
		}
		if (logger.isInfoEnabled()) {
			StringBuilder b = new StringBuilder(100);
			b.append("addResourceInstance for link start isAll=");
			b.append(isAll);
			b.append(" size=").append(resourceInstances.size());
			logger.info(b);
		}
		/*
		 * ?????????????????????
		 */
		List<ResourceInstanceDO> allInstanceDO = new ArrayList<>();
		List<PropDO> allPropDO = new ArrayList<PropDO>();
		// List<PropDO> allPropTypeDO = new ArrayList<>();

		List<ResourceInstance> allParentInstance = getAllParentInstance(true);
		List<ResourceInstance> allParentByCategroyId = null;

		List<Long> allLink = null;
		// ???????????????????????????
		if (CollectionUtils.isNotEmpty(allParentInstance)) {
			allParentByCategroyId = new ArrayList<>(50);
			allLink = new ArrayList<Long>(100);
			for (ResourceInstance resourceInstance : allParentInstance) {
				if (CapacityConst.LINK.equals(resourceInstance.getCategoryId())) {
					allParentByCategroyId.add(resourceInstance);
					allLink.add(resourceInstance.getId());
				}
			}
		}
		if (logger.isInfoEnabled()) {
			logger.info("addResourceInstance for link: QueryAll Link instance allLink is:" + allLink == null ? null
					: allLink.size());
		}
		int index = 0;
		int count = 0;
		StringBuilder bLink = null;
		if (logger.isInfoEnabled()) {
			bLink = new StringBuilder(10000);
			bLink.append("link list:\n");
		}
		// ?????????????????????
		List<Long> repeatLinkInstanceId = new ArrayList<>(50);
		for (ResourceInstance tempInstance : resourceInstances) {
			if (tempInstance == null) {
				index++;
				continue;
			}
			// ??????????????????????????????MD5??????
			try {
				List<ModuleProp> moduleProps = tempInstance.getModuleProps();
				if (CollectionUtils.isNotEmpty(moduleProps)) {
					String[] md5Value = new String[4];
					for (ModuleProp moduleProp : moduleProps) {
						switch (moduleProp.getKey()) {
						case LinkResourceConsts.PROP_SRC_MAININST_ID:
							md5Value[0] = moduleProp.getValues()[0];
							break;
						case LinkResourceConsts.PROP_DEST_MAININST_ID:
							md5Value[1] = moduleProp.getValues()[0];
							break;
						case LinkResourceConsts.PROP_SRC_SUBINST_ID:
							md5Value[2] = moduleProp.getValues()[0];
							break;
						case LinkResourceConsts.PROP_DEST_SUBINST_ID:
							md5Value[3] = moduleProp.getValues()[0];
							break;
						}
					}
					StringBuilder b = new StringBuilder(50);
					for (String value : md5Value) {
						b.append(value);
					}
					String INST_IDENTY_KEY = SecureUtil.md5Encryp(b.toString());
					ModuleProp prop = new ModuleProp();
					prop.setInstanceId(tempInstance.getId());
					prop.setKey("INST_IDENTY_KEY");
					prop.setValues(new String[] { INST_IDENTY_KEY });
					moduleProps.add(prop);
					tempInstance.setModuleProps(moduleProps);
				} else {
					if (logger.isInfoEnabled()) {
						logger.info("parm link name: " + tempInstance.getName() + "moduleProp is null");
					}
				}
			} catch (Exception e) {
				if (logger.isErrorEnabled()) {
					StringBuilder bb = new StringBuilder(50);
					bb.append("add link instanceName=").append(tempInstance.getName()).append("error.");
					logger.error(bb.toString(), e);
				}
				// ???????????????
				continue;
			}
			// ???????????????
			ComparerResult comparerResult = parentInstanceValidate(tempInstance);
			count++;
			if (comparerResult != null) {
				if (comparerResult.getInstanceId() != 0) {
					// ???????????????????????????
					tempInstance.setId(comparerResult.getInstanceId());
					if (logger.isInfoEnabled()) {
						logger.info("add resourceInstance refresh:" + tempInstance.getName());
						bLink.append(count).append(" -repeat link id:").append(comparerResult.getInstanceId());
						bLink.append(" name:").append(tempInstance.getName()).append("\n");
					}
					refreshResourceInstance(tempInstance);
					repeatLinkInstanceId.add(tempInstance.getId());
					continue;
				}
			}
			/**
			 * ???????????????????????????id????????????????????????Portal????????????
			 */
			// if(tempInstance.getGeneratePrimaryKey()){
			long instanceId = instanceSeq.next();
			tempInstance.setId(instanceId);
			// }
			if (logger.isInfoEnabled()) {
				bLink.append(count).append(" -link id:").append(tempInstance.getId());
				bLink.append(" name:").append(tempInstance.getName()).append("\n");
			}
			// ????????????
			List<ModuleProp> moduleProps = tempInstance.getModuleProps();
			if (CollectionUtils.isNotEmpty(moduleProps)) {
				for (ModuleProp moduleProp : moduleProps) {
					moduleProp.setInstanceId(tempInstance.getId());
					List<PropDO> propDOs = modulePropExtendService.convertToDOs(moduleProp);
					if (propDOs != null) {
						allPropDO.addAll(propDOs);
					} else {
						if (logger.isInfoEnabled()) {
							logger.info("convertToDOs link name: " + tempInstance.getName() + "moduleProp is null");
						}
					}
				}
			}
			// HashSet<String> set = new HashSet<>();
			// for (PropDO propDO : allPropDO) {
			// String key = propDO.getInstanceId() + propDO.getKey()
			// + propDO.getType();
			// if (!set.contains(key)) {
			// set.add(key);
			// allPropTypeDO.add(propDO);
			// }
			// }
			// set = null;
			/*
			 * ??????????????????
			 */
			ResourceInstanceDO resourceInstanceDO = convertToDO(tempInstance);
			allInstanceDO.add(resourceInstanceDO);
		}
		if (logger.isInfoEnabled()) {
			logger.info(bLink.toString());
		}
		if (!allInstanceDO.isEmpty()) {
			try {
				// ??????????????????
				resourceInstanceDAO.insertResourceInstances(allInstanceDO);
				if (!allPropDO.isEmpty()) {
					// ????????????????????????
					propDAO.insertPropDOs(allPropDO);
				}
				// if (!allPropTypeDO.isEmpty()) {
				// ????????????????????????
				// propTypeDAO.insertPropTypeDOs(allPropTypeDO);
				// }
				// ?????????????????????,????????????????????????????????????????????????????????????????????????
				if (isAll) {
					if (allLink != null) {
						allLink.removeAll(repeatLinkInstanceId);
						if (!allLink.isEmpty()) {
							removeResourceInstanceByLinks(allLink);
						}
					}
				}
			} catch (Exception e1) {
				if (logger.isErrorEnabled()) {
					logger.error("addResourceInstance for link error", e1);
				}
				throw new InstancelibException(ServerErrorCodeConstant.ERR_SERVER_OPERATE_DATABASE_ERROR,
						"addResourceInstance for link error");
			}
		}
		if (index != 0) {
			if (logger.isInfoEnabled()) {
				logger.info("ResourceInsntace link is null.size=" + index);
			}
		}
		// ????????????????????????
		final InstancelibEvent instancelibEvent = new InstancelibEvent(resourceInstances, isAll,
				EventEnum.INSTANCE_LINK_ADD_EVENT);
		// ???????????????
		for (ResourceInstance tempInstance : resourceInstances) {
			cache.queryAdd(tempInstance.getId(), tempInstance);
			// ?????????????????????
			cache.addParentInsatnceList(tempInstance.getId());
		}
		// ????????????
		listenerNotification(instancelibEvent);
		if (logger.isInfoEnabled()) {
			logger.info("addResourceInstance for link end");
		}
	}

	@Override
	public Map<InstanceLifeStateEnum, List<Long>> refreshResourceInstance(ResourceInstance resourceInstance)
			throws InstancelibException {
		return refreshResourceInstance(resourceInstance, true);
	}

	@Override
	public synchronized Map<InstanceLifeStateEnum, List<Long>> refreshResourceInstance(
			ResourceInstance resourceInstance, boolean isAutoDeleteChildInstance) throws InstancelibException {
		if (logger.isInfoEnabled()) {
			logger.info("refreshResourceInstance start instanceId=" + resourceInstance.getId() + "\t,instanceLifeState:"
					+ resourceInstance.getLifeState().name() + ",\t isAutoDeleteChildInstance:"
					+ isAutoDeleteChildInstance);
			logger.info("child:" + resourceInstance.getChildren());
		}

		Map<InstanceLifeStateEnum, List<Long>> result = new HashMap<InstanceLifeStateEnum, List<Long>>();
		List<Long> newerInstanceIds = new ArrayList<Long>();
		List<Long> removedInstanceIds = new ArrayList<>();
		List<Long> deletedInstanceIds = new ArrayList<>();
		// ?????????????????????
		ResourceInstance oldResourceInstance = null;
		try {
			oldResourceInstance = getResourceInstanceById(resourceInstance.getId());
		} catch (Exception e1) {
			if (logger.isErrorEnabled())
				logger.error("", e1);
		}

		if (oldResourceInstance == null) {
			if (logger.isErrorEnabled()) {
				logger.error("refreshResourceInstance failed,old instance [" + resourceInstance.getId() + "-"
						+ resourceInstance.getName() + "]is null!");
			}
			return null;
		}
		validate(resourceInstance, true);
		// ?????????????????????????????????????????????
		if (CollectionUtils.isNotEmpty(oldResourceInstance.getCustomProps())) {
			resourceInstance.setCustomProps(oldResourceInstance.getCustomProps());
		}
		if (CollectionUtils.isNotEmpty(oldResourceInstance.getExtendProps())) {
			resourceInstance.setExtendProps(oldResourceInstance.getExtendProps());
		}

		/*
		 * ???????????? ??????????????????????????????????????????????????????????????????????????????????????????(???????????????????????????)
		 */
		List<ResourceInstance> allChildrenResourceInstances = new ArrayList<ResourceInstance>();
		convertAllChildren(resourceInstance, allChildrenResourceInstances);
		// ?????????????????????
		HashSet<Long> repeatInstanceId = new HashSet<>();
		HashSet<ResourceInstance> repeatInstance = new HashSet<>();
		/*
		 * ????????????????????????????????????????????? ????????????????????????????????????convertToList ??????????????????????????????
		 */
		repeatInstance.add(resourceInstance);
		repeatInstanceId.add(resourceInstance.getId());
		// ??????????????????????????????
		List<ResourceInstance> addInstance = new ArrayList<ResourceInstance>();
		/*
		 * ?????????????????? ?????????????????????????????????????????????????????????????????????
		 */
		if (oldResourceInstance.getLifeState() == InstanceLifeStateEnum.DELETED
				|| oldResourceInstance.getLifeState() == InstanceLifeStateEnum.INITIALIZE) {
			resourceInstance.setLifeState(InstanceLifeStateEnum.NOT_MONITORED);
		} else {
			resourceInstance.setLifeState(oldResourceInstance.getLifeState());
		}
		/*
		 * ?????????????????????????????????ip ??????????????????????????????
		 */
		if (StringUtils.isNotEmpty(oldResourceInstance.getShowName())
				&& oldResourceInstance.getLifeState() != InstanceLifeStateEnum.DELETED) {
			resourceInstance.setShowName(oldResourceInstance.getShowName());
		}
		if (StringUtils.isNotEmpty(oldResourceInstance.getShowIP())
				&& oldResourceInstance.getLifeState() != InstanceLifeStateEnum.DELETED) {
			resourceInstance.setShowIP(oldResourceInstance.getShowIP());
		}
		if (StringUtils.isNotEmpty(oldResourceInstance.getDiscoverNode())
				&& oldResourceInstance.getLifeState() != InstanceLifeStateEnum.DELETED) {
			resourceInstance.setDiscoverNode(oldResourceInstance.getDiscoverNode());
		}
		if (oldResourceInstance.getLifeState() != InstanceLifeStateEnum.DELETED) {
			resourceInstance.setDomainId(oldResourceInstance.getDomainId());
		}
		/**
		 * ?????????????????????,?????????????????????????????????
		 */
		List<ResourceInstance> children = loadChildInstanceByParentId(resourceInstance.getId(), true);
		if (children != null) {
			// ?????????????????????????????????????????????????????????
			Map<String, List<ResourceInstance>> instanceTypeResource = new HashMap<>();
			for (ResourceInstance childrenInstance : children) {
				List<ResourceInstance> typeInstances = instanceTypeResource.get(childrenInstance.getChildType());
				if (typeInstances != null) {
					typeInstances.add(childrenInstance);
				} else {
					typeInstances = new ArrayList<>();
					typeInstances.add(childrenInstance);
					instanceTypeResource.put(childrenInstance.getChildType(), typeInstances);
				}
			}

			// ???????????????????????????????????????MD5Str
			Map<String, Map<String, Long>> oldInstanceMd5forId = new HashMap<String, Map<String, Long>>();
			Map<String, String> typeInstanceMd5Str = new HashMap<String, String>();
			for (Map.Entry<String, List<ResourceInstance>> typeInstance : instanceTypeResource.entrySet()) {
				Map<String, Long> childMd5ForId = new HashMap<String, Long>(typeInstance.getValue().size());
				String instanceGroupMd5Str = resourceComparer.getChildResourceInstanceMd5Str(typeInstance.getValue(),
						childMd5ForId);
				typeInstanceMd5Str.put(typeInstance.getKey(), instanceGroupMd5Str);
				oldInstanceMd5forId.put(typeInstance.getKey(), childMd5ForId);
			}
			// String childrenMd5Str =
			// resourceComparer.getChildResourceInstanceMd5Str(children,oldInstanceMd5forId);
			for (ResourceInstance childInstance : allChildrenResourceInstances) {
				// ?????????????????????????????????????????????????????????
				if (StringUtils.isNotEmpty(resourceInstance.getDiscoverNode())) {
					childInstance.setDiscoverNode(resourceInstance.getDiscoverNode());
				}
				childInstance.setDomainId(resourceInstance.getDomainId());
				String childTypeMd5Str = typeInstanceMd5Str.get(childInstance.getChildType());
				ComparerResult comparerResult = refreshCalcRepeat(childTypeMd5Str, childInstance, oldInstanceMd5forId);
				if (comparerResult == null) {
					if (logger.isInfoEnabled()) {
						logger.info(" comparerResult is null," + childInstance.getName() + " ,is new childInstance!");
					}
					// ??????????????????
					addInstance.add(childInstance);
				} else {
					if (comparerResult.getInstanceId() != 0) {
						if (logger.isInfoEnabled()) {
							logger.info(childInstance.getName() + ",is old childInstance! compareInstanceId:"
									+ comparerResult.getInstanceId() + " compareLifeState:"
									+ comparerResult.getLifeState());
						}
						// ?????????????????????,????????????????????????????????????????????????????????????
						ResourceInstance oldChild = loadResourceInstance(comparerResult.getInstanceId());
						if (comparerResult.getLifeState().equals(InstanceLifeStateEnum.DELETED)) {
							if (logger.isInfoEnabled()) {
								logger.info("set childInstance lifestate to NOT_MONITORED! instanceId="
										+ comparerResult.getInstanceId() + ";instanceName:" + oldChild.getName());
							}
							childInstance.setLifeState(InstanceLifeStateEnum.NOT_MONITORED);
							newerInstanceIds.add(comparerResult.getInstanceId());
						} else {
							if (oldChild == null) {
								if (logger.isInfoEnabled()) {
									logger.info("compare oldChildInstance is null,set lifestate to NOT_MONITORED");
								}
								childInstance.setLifeState(InstanceLifeStateEnum.NOT_MONITORED);
								newerInstanceIds.add(comparerResult.getInstanceId());
							} else {
								if (logger.isInfoEnabled()) {
									logger.info("compare oldChildInstance is not null,set lifestate to old liftState :"
											+ oldChild.getLifeState());
								}
								childInstance.setLifeState(oldChild.getLifeState());
							}
						}
						// ???????????????
						childInstance.setId(comparerResult.getInstanceId());
						repeatInstanceId.add(childInstance.getId());
						repeatInstance.add(childInstance);
					} else {
						// ??????????????????
						if (logger.isInfoEnabled()) {
							logger.info(" comparerResult is not null," + childInstance.getName()
									+ " is new childInstance!");
						}
						addInstance.add(childInstance);
					}
				}
			}
		} else {
			if (logger.isInfoEnabled()) {
				logger.info("refresh repeat all instance!");
			}
			/*
			 * ???????????????????????????????????????????????????
			 */
			for (ResourceInstance childInstance : allChildrenResourceInstances) {
				addInstance.add(childInstance);
			}
		}

		/**
		 * ???????????????????????????
		 */
		List<PropDO> allUpdatePropDOs = new ArrayList<PropDO>();
		List<ResourceInstanceDO> allUpdateInstanceDO = new ArrayList<>();
		try {
			for (ResourceInstance tempRepeatInstance : repeatInstance) {
				if (tempRepeatInstance.getId() != resourceInstance.getId()) {
					tempRepeatInstance.setParentId(resourceInstance.getId());
				} else {
					// ??????????????????????????????,??????????????????????????????????????????
					calcProp(oldResourceInstance, tempRepeatInstance);
				}
				convertToList(null, allUpdatePropDOs, tempRepeatInstance, false);
				ResourceInstanceDO d = convertToDO(tempRepeatInstance);
				allUpdateInstanceDO.add(d);
			}
		} catch (Exception e1) {
			if (logger.isErrorEnabled()) {
				logger.error("check all update prop error:" + e1);
			}
		}
		/**
		 * ???????????????????????????
		 */
		List<ResourceInstance> oldChildInstances = oldResourceInstance.getChildren();
		List<Long> oldChildInstanceIds = new ArrayList<Long>();
		if (oldChildInstances != null) {
			for (ResourceInstance resourceInstance2 : oldChildInstances) {
				oldChildInstanceIds.add(resourceInstance2.getId());
			}
		}
		HashSet<Long> oldChildrenCache = new HashSet<>(oldChildInstanceIds); // cache.getChildInstance(resourceInstance.getId());
		if (oldChildrenCache != null) {
			// ?????????????????????????????????-(?????????????????????????????????Id??????????????????????????????)
			oldChildrenCache.removeAll(repeatInstanceId);

			List<ResourceInstance> oldChilds = getChildInstanceByParentId(oldResourceInstance.getId(), true);
			if (!CollectionUtils.isEmpty(oldChilds)) {
				for (ResourceInstance ri : oldChilds) {
					if (ri.getLifeState().equals(InstanceLifeStateEnum.DELETED)) {
						deletedInstanceIds.add(ri.getId());
					}
				}
			}

			for (long deletedId : oldChildrenCache) {
				ResourceInstance deleteIntance = loadResourceInstance(deletedId);
				if (deleteIntance == null || deleteIntance.getChildType().equals("Process")) {
					continue;
				}
				if (deleteIntance.getLifeState().equals(InstanceLifeStateEnum.MONITORED)
						|| deleteIntance.getLifeState().equals(InstanceLifeStateEnum.NOT_MONITORED)) {
					removedInstanceIds.add(deleteIntance.getId());
				}
				if (isAutoDeleteChildInstance) {
					cache.remove(deletedId);
					// ???????????????????????????
					propCache.remove(deletedId);
					deleteIntance.setLifeState(InstanceLifeStateEnum.DELETED);
					ResourceInstanceDO d = convertToDO(deleteIntance);
					allUpdateInstanceDO.add(d);
					// ???????????????????????????????????????(???????????????????????????update)
					// repeatInstanceId.add(deletedId);
					// convertToList(null, allUpdatePropDOs, deleteIntance, false);
				}
			}
		}
		try {
			long dbStartTime = System.currentTimeMillis();
			if (logger.isInfoEnabled()) {
				logger.info("instanceId:" + resourceInstance.getId() + "  start operation database!");
			}
			// ????????????????????????
			List<Long> ids = new ArrayList<>(repeatInstanceId);
			// ??????????????????????????????????????????????????????
			// resourceInstanceDAO.removeResourceInstanceByIds(ids);
			resourceInstanceDAO.updateResourceInstances(allUpdateInstanceDO);
			// resourceInstanceDAO.insertResourceInstances(allUpdateInstanceDO);
			if (!allUpdatePropDOs.isEmpty()) {
				propDAO.removePropDOByInstances(ids);
				propTypeDAO.removePropTypeDOByInstances(ids);
				propDAO.insertPropDOs(allUpdatePropDOs);
				propTypeDAO.insertPropTypeDOs(allUpdatePropDOs);
			}
			/**
			 * ??????????????????????????????
			 */
			if (!addInstance.isEmpty()) {
				addAllChildrenIntance(resourceInstance.getId(), addInstance);
				for (ResourceInstance addChildInstance : addInstance) {
					newerInstanceIds.add(addChildInstance.getId());
				}
			}
			long dbEndTime = System.currentTimeMillis();
			if (logger.isInfoEnabled()) {
				logger.info("instanceId:" + resourceInstance.getId() + "  Operation database complete; use time:"
						+ ((dbEndTime - dbStartTime) / 1000) + "s!");
			}
		} catch (Exception e) {
			if (logger.isErrorEnabled()) {
				logger.error("refreshResourceInstance error!", e);
			}
			throw new InstancelibException(ServerErrorCodeConstant.ERR_SERVER_OPERATE_DATABASE_ERROR,
					" addResourceInstanceerror");
		}
		// ???????????????????????????
		final InstancelibEvent instancelibEvent = new InstancelibEvent(Arrays.asList(resourceInstance), Arrays.asList(resourceInstance),
				EventEnum.INSTANCE_REFRESH_EVENT);
		// ????????????
		try {
			interceptorNotification(instancelibEvent);
		} catch (Exception e) {
			if (logger.isErrorEnabled()) {
				logger.error("refreshResourceInstance interceptorNotification error!", e);
			}
			throw new InstancelibException(ServerErrorCodeConstant.ERR_SERVER_RESPO_INTERCEPTOR,
					"refreshResourceInstance interceptor error!");
		}
		long cacheStartTime = System.currentTimeMillis();
		// ???????????????????????????????????????
		if (oldChildrenCache != null) {
			if (isAutoDeleteChildInstance) {
				// ???????????????????????????????????????
				List<ResourceInstance> deletedInstances = new ArrayList<ResourceInstance>();
				for (long deleteId : oldChildrenCache) {
					ResourceInstance resourceIntance = loadResourceInstance(deleteId);
					if (resourceIntance != null) {
						resourceIntance.setLifeState(InstanceLifeStateEnum.DELETED);
						deletedInstances.add(resourceIntance);
					}
				}
				for (ResourceInstance deleteInstance : deletedInstances) {
					cache.queryAdd(deleteInstance.getId(), deleteInstance);
					// ???????????????????????????
					propCache.remove(deleteInstance.getId());
				}
			}
		}
		for (ResourceInstance repeat : repeatInstance) {
			List<ResourceInstance> childrens = repeat.getChildren() == null ? null
					: new ArrayList<>(repeat.getChildren());
			List<ModuleProp> moduleProps = repeat.getModuleProps() == null ? null
					: new ArrayList<>(repeat.getModuleProps());
			List<DiscoverProp> discoverProps = repeat.getDiscoverProps() == null ? null
					: new ArrayList<>(repeat.getDiscoverProps());
			repeat.setChildren(null);
			repeat.setModuleProps(null);
			repeat.setDiscoverProps(null);
			// ?????????????????????????????????
			cache.queryAdd(repeat.getId(), repeat);
			// ???????????????????????????
			propCache.remove(repeat.getId());
			repeat.setChildren(childrens);
			repeat.setModuleProps(moduleProps);
			repeat.setDiscoverProps(discoverProps);
		}
		long cacheEndTime = System.currentTimeMillis();
		if (logger.isInfoEnabled()) {
			logger.info("instanceId:" + resourceInstance.getId() + "  Operation cache complete; use time:"
					+ ((cacheEndTime - cacheStartTime) / 1000) + "s!");
		}
		// ????????????
		listenerNotification(instancelibEvent);
		if (logger.isInfoEnabled()) {
			logger.info("refreshResourceInstance end instanceId= " + resourceInstance.getId());
		}
		result.put(InstanceLifeStateEnum.NEWER, newerInstanceIds);
		result.put(InstanceLifeStateEnum.REMOVED, removedInstanceIds);
		result.put(InstanceLifeStateEnum.DELETED, deletedInstanceIds);
		return result;
	}

	/*
	 * ????????????????????????????????????
	 */
	public synchronized void refreshResourceInstances(List<ResourceInstance> resourceInstances) throws InstancelibException {
		if (logger.isInfoEnabled()) {
			logger.info("batch refreshResourceInstance start");
		}
		if(CollectionUtils.isEmpty(resourceInstances)){
			if (logger.isInfoEnabled()) {
				logger.info("batch refreshResourceInstance params is empty");
				logger.info("batch refreshResourceInstance end");
			}
			return;
		}
		List<ResourceInstanceDO> allUpdateInstanceDO = new ArrayList<>();
		List<PropDO> allUpdatePropDOs = new ArrayList<PropDO>();
		// ?????????????????????
		HashSet<Long> repeatInstanceId = new HashSet<>();
		for (ResourceInstance resourceInstance : resourceInstances) {
			// ?????????????????????
			ResourceInstance oldResourceInstance = null;
			try {
				oldResourceInstance = getResourceInstanceById(resourceInstance.getId());
			} catch (Exception e1) {
				if (logger.isErrorEnabled())
					logger.error("", e1);
			}
			if (oldResourceInstance == null) {
				if (logger.isErrorEnabled()) {
					logger.error("refreshResourceInstance failed,old instance [" + resourceInstance.getId() + "-"
							+ resourceInstance.getName() + "]is null!");
				}
				return;
			}
			validate(resourceInstance, true);
			// ?????????????????????????????????????????????
			if (CollectionUtils.isNotEmpty(oldResourceInstance.getCustomProps())) {
				resourceInstance.setCustomProps(oldResourceInstance.getCustomProps());
			}
			if (CollectionUtils.isNotEmpty(oldResourceInstance.getExtendProps())) {
				resourceInstance.setExtendProps(oldResourceInstance.getExtendProps());
			}

			// ??????????????????????????????
		//	List<ResourceInstance> addInstance = new ArrayList<ResourceInstance>();
			/*
			 * ?????????????????? ?????????????????????????????????????????????????????????????????????
			 */
			if (oldResourceInstance.getLifeState() == InstanceLifeStateEnum.DELETED
					|| oldResourceInstance.getLifeState() == InstanceLifeStateEnum.INITIALIZE) {
				resourceInstance.setLifeState(InstanceLifeStateEnum.NOT_MONITORED);
			} else {
				resourceInstance.setLifeState(oldResourceInstance.getLifeState());
			}
			/*
			 * ?????????????????????????????????ip ??????????????????????????????
			 */
			if (StringUtils.isNotEmpty(oldResourceInstance.getShowName())
					&& oldResourceInstance.getLifeState() != InstanceLifeStateEnum.DELETED) {
				resourceInstance.setShowName(oldResourceInstance.getShowName());
			}
			if (StringUtils.isNotEmpty(oldResourceInstance.getShowIP())
					&& oldResourceInstance.getLifeState() != InstanceLifeStateEnum.DELETED) {
				resourceInstance.setShowIP(oldResourceInstance.getShowIP());
			}
			if (StringUtils.isNotEmpty(oldResourceInstance.getDiscoverNode())
					&& oldResourceInstance.getLifeState() != InstanceLifeStateEnum.DELETED) {
				resourceInstance.setDiscoverNode(oldResourceInstance.getDiscoverNode());
			}
			if (oldResourceInstance.getLifeState() != InstanceLifeStateEnum.DELETED) {
				resourceInstance.setDomainId(oldResourceInstance.getDomainId());
			}
			try {
				calcProp(oldResourceInstance, resourceInstance);
				convertToList(null, allUpdatePropDOs, resourceInstance, false);
				ResourceInstanceDO d = convertToDO(resourceInstance);
				allUpdateInstanceDO.add(d);
				repeatInstanceId.add(resourceInstance.getId());
			} catch (Exception e1) {
				if (logger.isErrorEnabled()) {
					logger.error("check all update prop error:" + e1);
				}
			}
		}
		try {
			long dbStartTime = System.currentTimeMillis();
			// ????????????????????????
			List<Long> ids = new ArrayList<>(repeatInstanceId);
			resourceInstanceDAO.updateResourceInstances(allUpdateInstanceDO);
			if (!allUpdatePropDOs.isEmpty()) {
				propDAO.removePropDOByInstances(ids);
				propTypeDAO.removePropTypeDOByInstances(ids);
				propDAO.insertPropDOs(allUpdatePropDOs);
				propTypeDAO.insertPropTypeDOs(allUpdatePropDOs);
			}
			long dbEndTime = System.currentTimeMillis();
			if (logger.isInfoEnabled()) {
				logger.info(" Operation database complete; use time:" + ((dbEndTime - dbStartTime) / 1000) + "s!");
			}
		} catch (Exception e) {
			if (logger.isErrorEnabled()) {
				logger.error("refreshResourceInstance error!", e);
			}
			throw new InstancelibException(ServerErrorCodeConstant.ERR_SERVER_OPERATE_DATABASE_ERROR,
					" addResourceInstanceerror");
		}
		if (logger.isInfoEnabled()) {
			logger.info("batch refreshResourceInstance end");
		}
	}

	private void addAllChildrenIntance(long parentId, List<ResourceInstance> childrenInstaces) {
		if (logger.isInfoEnabled()) {
			logger.info("addAllChildrenIntance begin!parentId:" + parentId + ";childrenInstaces.size:"
					+ childrenInstaces.size());
		}
		List<ResourceInstance> allInstance = new ArrayList<>();
		List<PropDO> allPropDO = new ArrayList<PropDO>();
		for (ResourceInstance child : childrenInstaces) {
			if (child.getGeneratePrimaryKey()) {
				long instanceId = instanceSeq.next();
				child.setId(instanceId);
			}
			child.setParentId(parentId);
			convertToList(allInstance, allPropDO, child, false);
		}
		List<PropDO> allPropTypeDO = new ArrayList<>();
		HashSet<String> set = new HashSet<>();
		for (PropDO propDO : allPropDO) {
			String key = propDO.getInstanceId() + propDO.getPropKey() + propDO.getPropType();
			if (!set.contains(key)) {
				set.add(key);
				allPropTypeDO.add(propDO);
			}
		}
		set = null;
		/*
		 * ????????????????????????
		 */
		List<ResourceInstanceDO> allInstanceDO = new ArrayList<>();
		for (ResourceInstance tempInstance : allInstance) {
			allInstanceDO.add(convertToDO(tempInstance));
		}
		try {
			// ??????????????????
			resourceInstanceDAO.insertResourceInstances(allInstanceDO);
			if (!allPropDO.isEmpty()) {
				// ????????????????????????
				propDAO.insertPropDOs(allPropDO);
			}
			if (!allPropTypeDO.isEmpty()) {
				// ????????????????????????
				propTypeDAO.insertPropTypeDOs(allPropTypeDO);
			}
		} catch (Exception e1) {
			if (logger.isErrorEnabled()) {
				logger.error("addResourceInstance error", e1);
			}
		}
		HashSet<Long> children = new HashSet<>(allInstance.size() - 1);
		// ??????????????????
		for (ResourceInstance tempInstance : allInstance) {
			cache.queryAdd(tempInstance.getId(), tempInstance);
			if (tempInstance.getParentId() != 0) {
				children.add(tempInstance.getId());
			}
		}
		/*
		 * ?????????????????????
		 */
		// ??????????????????????????????
		cache.addChildInstanceList(parentId, children);
		if (logger.isInfoEnabled()) {
			logger.info("parentId:" + parentId + "  addAllChildrenIntance end!");
		}
	}

	private void convertAllChildren(ResourceInstance resourceInstance, List<ResourceInstance> allResourceInstances) {
		List<ResourceInstance> children = resourceInstance.getChildren();
		if (children != null) {
			for (ResourceInstance child : children) {
				if (allResourceInstances != null) {
					allResourceInstances.add(child);
				}
				convertAllChildren(child, allResourceInstances);
			}
		}
	}

	@Override
	public void updateResourceInstanceState(Map<Long, InstanceLifeStateEnum> states) throws InstancelibException {
		if (logger.isTraceEnabled()) {
			logger.trace("updateResourceInstanceState start =" + states);
		}
		// ?????????
		List<ResourceInstance> oldResourceInstances = new ArrayList<>();
		// ???????????????
		List<ResourceInstance> currentResourceInstances = new ArrayList<>();

		List<ResourceInstanceDO> resourceInstances = new ArrayList<>();
		for (Entry<Long, InstanceLifeStateEnum> state : states.entrySet()) {
			/*
			 * ??????????????????????????????
			 */
			ResourceInstanceDO queryDo = new ResourceInstanceDO();
			queryDo.setInstanceId(state.getKey());
			queryDo.setLifeState(state.getValue().name());
			resourceInstances.add(queryDo);

			/*
			 * ??????????????????????????????????????????
			 */
			ResourceInstance resourceInstance = getResourceInstanceById(state.getKey());
			// ResourceInstance currentResourceInstance =
			// convertTOQueryResourceInstance(resourceInstance);
			// currentResourceInstance.setLifeState(state.getValue());
			if (resourceInstance == null) {
				continue;
			}
			oldResourceInstances.add(resourceInstance);
			try {
				ResourceInstance currentInstance = loadResourceInstance(state.getKey()).clone();
				currentInstance.setLifeState(state.getValue());
				currentResourceInstances.add(currentInstance);
			} catch (CloneNotSupportedException e) {
				if (logger.isErrorEnabled()) {
					logger.error(e.getMessage(), e);
				}
			}
		}
		// ??????
		final InstancelibEvent instancelibEvent = new InstancelibEvent(oldResourceInstances, currentResourceInstances,
				EventEnum.INSTANCE_UPDATE_STATE_EVENT);

		if (!resourceInstances.isEmpty()) {
			try {
				resourceInstanceDAO.updateResourceInstances(resourceInstances);
			} catch (Exception e) {
				if (logger.isErrorEnabled()) {
					logger.error("updateResourceInstanceState error!", e);
				}
				throw new InstancelibException(ServerErrorCodeConstant.ERR_SERVER_OPERATE_DATABASE_ERROR,
						"updateResourceInstanceState error!");
			}
		}
		// ????????????
		try {
			interceptorNotification(instancelibEvent);
		} catch (Exception e) {
			if (logger.isErrorEnabled()) {
				logger.error("updateResourceInstanceState interceptorNotification error!", e);
			}
			throw new InstancelibException(ServerErrorCodeConstant.ERR_SERVER_RESPO_INTERCEPTOR,
					"updateResourceInstanceState interceptor error!");
		}
		// ????????????
		for (Entry<Long, InstanceLifeStateEnum> state : states.entrySet()) {
			ResourceInstance cacheInstance = cache.get(state.getKey());
			if (cacheInstance != null) {
				cacheInstance.setLifeState(state.getValue());
				cache.update(cacheInstance.getId(), cacheInstance);
			}
		}
		// ????????????
		listenerNotification(instancelibEvent);
		if (logger.isTraceEnabled()) {
			logger.trace("updateResourceInstanceState end");
		}
	}

	@Override
	public void updateResourceInstanceName(long instanceId, String name) throws InstancelibException {
		if (logger.isTraceEnabled()) {
			logger.trace("updateResourceInstanceName start name=" + name);
		}
		// ?????????????????????
		ResourceInstance oldResourceInstance = loadResourceInstance(instanceId);
		// ????????????????????????
		ResourceInstance currentResourceInstance = null;
		try {
			currentResourceInstance = oldResourceInstance.clone();
		} catch (CloneNotSupportedException e) {
			if (logger.isErrorEnabled()) {
				logger.error("resourceinstance clone error!", e);
			}
			return;
		}
		currentResourceInstance.setShowName(name);
		// ????????????????????????????????????
		final InstancelibEvent instancelibEvent = new InstancelibEvent(oldResourceInstance, currentResourceInstance,
				EventEnum.INSTANCE_UPDATE_NAME_EVENT);

		ResourceInstanceDO resourceInstanceDO = new ResourceInstanceDO();
		resourceInstanceDO.setInstanceShowName(name);
		resourceInstanceDO.setInstanceId(instanceId);

		try {
			resourceInstanceDAO.updateResourceInstance(resourceInstanceDO);
		} catch (Exception e) {
			if (logger.isErrorEnabled()) {
				logger.error("updateResourceInstanceName error!", e);
			}
			throw new InstancelibException(ServerErrorCodeConstant.ERR_SERVER_OPERATE_DATABASE_ERROR,
					"updateResourceInstanceName error!");
		}
		// ????????????
		try {
			interceptorNotification(instancelibEvent);
		} catch (Exception e) {
			if (logger.isErrorEnabled()) {
				logger.error("updateResourceInstanceName interceptorNotification error!", e);
			}
			throw new InstancelibException(ServerErrorCodeConstant.ERR_SERVER_RESPO_INTERCEPTOR,
					"updateResourceInstanceName interceptor error!");
		}
		// ????????????
		ResourceInstance cacheInstance = cache.get(instanceId);
		if (cacheInstance != null) {
			cacheInstance.setShowName(name);
			cache.update(cacheInstance.getId(), cacheInstance);
		}
		// ????????????
		listenerNotification(instancelibEvent);
		if (logger.isTraceEnabled()) {
			logger.trace("updateResourceInstanceState end ");
		}
	}

	@Override
	public void updateResourceInstanceDomain(Map<Long, Long> domainIds) throws InstancelibException {
		if (logger.isTraceEnabled()) {
			logger.trace("updateResourceInstanceDomain start ");
		}
		// ?????????
		List<ResourceInstance> oldResourceInstances = new ArrayList<>();
		// ???????????????
		List<ResourceInstance> currentResourceInstances = new ArrayList<>();

		List<ResourceInstanceDO> resourceInstances = new ArrayList<>();
		for (Entry<Long, Long> state : domainIds.entrySet()) {
			/*
			 * ??????????????????????????????
			 */
			ResourceInstanceDO queryDo = new ResourceInstanceDO();
			queryDo.setInstanceId(state.getKey());
			queryDo.setDomainId(state.getValue());
			resourceInstances.add(queryDo);
			// ??????????????????????????????ITBA ??????????????????????????????
			HashSet<Long> childIds = cache.getChildInstance(state.getKey());
			if (childIds != null) {
				for (long instanceId : childIds) {
					queryDo = new ResourceInstanceDO();
					queryDo.setInstanceId(instanceId);
					queryDo.setDomainId(state.getValue());
					resourceInstances.add(queryDo);
				}
			}

			/*
			 * ??????????????????????????????????????????
			 */
			ResourceInstance resourceInstance = getResourceInstanceById(state.getKey());
			oldResourceInstances.add(resourceInstance);
			try {
				ResourceInstance currentInstance = loadResourceInstance(state.getKey()).clone();
				currentInstance.setDomainId(state.getValue());
				currentResourceInstances.add(currentInstance);
			} catch (CloneNotSupportedException e) {
				if (logger.isErrorEnabled()) {
					logger.error(e.getMessage(), e);
				}
			}
		}
		// ??????
		final InstancelibEvent instancelibEvent = new InstancelibEvent(oldResourceInstances, currentResourceInstances,
				EventEnum.INSTANCE_UPDATE_STATE_EVENT);

		if (!resourceInstances.isEmpty()) {
			try {
				resourceInstanceDAO.updateResourceInstances(resourceInstances);
			} catch (Exception e) {
				if (logger.isErrorEnabled()) {
					logger.error("updateResourceInstanceState error!", e);
				}
				throw new InstancelibException(ServerErrorCodeConstant.ERR_SERVER_OPERATE_DATABASE_ERROR,
						"updateResourceInstanceState error!");
			}
		}
		// ????????????
		try {
			interceptorNotification(instancelibEvent);
		} catch (Exception e) {
			if (logger.isErrorEnabled()) {
				logger.error("updateResourceInstanceState interceptorNotification error!", e);
			}
			throw new InstancelibException(ServerErrorCodeConstant.ERR_SERVER_RESPO_INTERCEPTOR,
					"updateResourceInstanceState interceptor error!");
		}
		// ????????????
		for (Entry<Long, Long> state : domainIds.entrySet()) {
			ResourceInstance cacheInstance = cache.get(state.getKey());
			if (cacheInstance != null) {
				cacheInstance.setDomainId(state.getValue());
				cache.update(cacheInstance.getId(), cacheInstance);
			}
		}
		if (logger.isTraceEnabled()) {
			logger.trace("updateResourceInstanceDomain end ");
		}
	}

	@Override
	public void updateResourceInstanceShowIP(long instanceId, String showIP) throws InstancelibException {
		if (logger.isTraceEnabled()) {
			logger.trace("updateResourceInstanceName start showIP=" + showIP);
		}
		// ?????????????????????
		ResourceInstance oldResourceInstance = loadResourceInstance(instanceId);
		// ????????????????????????
		ResourceInstance currentResourceInstance = null;
		try {
			currentResourceInstance = oldResourceInstance.clone();
		} catch (CloneNotSupportedException e1) {
			if (logger.isErrorEnabled()) {
				logger.error("resourceinstance clone error!", e1);
			}
			return;
		}
		currentResourceInstance.setShowIP(showIP);

		final InstancelibEvent instancelibEvent = new InstancelibEvent(oldResourceInstance, currentResourceInstance,
				EventEnum.INSTANCE_UPDATE_DISCOVER_IP_EVENT);

		ResourceInstanceDO resourceInstanceDO = new ResourceInstanceDO();
		resourceInstanceDO.setShowIP(showIP);
		resourceInstanceDO.setInstanceId(instanceId);

		try {
			resourceInstanceDAO.updateResourceInstance(resourceInstanceDO);
		} catch (Exception e) {
			if (logger.isErrorEnabled()) {
				logger.error("updateResourceInstanceShowIP error!", e);
			}
			throw new InstancelibException(ServerErrorCodeConstant.ERR_SERVER_OPERATE_DATABASE_ERROR,
					"updateResourceInstanceShowIP error!");
		}
		// ????????????
		try {
			interceptorNotification(instancelibEvent);
		} catch (Exception e) {
			if (logger.isErrorEnabled()) {
				logger.error("updateResourceInstanceShowIP interceptorNotification error!", e);
			}
			throw new InstancelibException(ServerErrorCodeConstant.ERR_SERVER_RESPO_INTERCEPTOR,
					"updateResourceInstanceShowIP interceptor error!");
		}
		// ????????????
		ResourceInstance cacheInstance = cache.get(instanceId);
		if (cacheInstance != null) {
			cacheInstance.setShowIP(showIP);
			cache.update(cacheInstance.getId(), cacheInstance);
		}
		// ????????????
		listenerNotification(instancelibEvent);
		if (logger.isTraceEnabled()) {
			logger.trace("updateResourceInstanceState end showIP=" + showIP);
		}
	}

	@Override
	public void updateResourceInstanceDiscoverNode(long instanceId, String discoverNode) throws InstancelibException {
		if (logger.isTraceEnabled()) {
			logger.trace("updateResourceInstanceName start discoverNode=" + discoverNode);
		}
		// ?????????????????????
		ResourceInstance oldResourceInstance = loadResourceInstance(instanceId);
		// ????????????????????????
		ResourceInstance currentResourceInstance = null;
		try {
			currentResourceInstance = oldResourceInstance.clone();
		} catch (CloneNotSupportedException e1) {
			if (logger.isErrorEnabled()) {
				logger.error("resourceinstance clone error!", e1);
			}
			return;
		}
		currentResourceInstance.setDiscoverNode(discoverNode);

		final InstancelibEvent instancelibEvent = new InstancelibEvent(oldResourceInstance, currentResourceInstance,
				EventEnum.INSTANCE_UPDATE_DISCOVER_NODE_EVENT);
		// ????????????
		try {
			interceptorNotification(instancelibEvent);
		} catch (Exception e) {
			if (logger.isErrorEnabled()) {
				logger.error("updateResourceInstanceDiscoverNode interceptorNotification error!", e);
			}
			throw new InstancelibException(ServerErrorCodeConstant.ERR_SERVER_RESPO_INTERCEPTOR,
					"updateResourceInstanceDiscoverNode interceptor error!");
		}

		ResourceInstanceDO resourceInstanceDO = new ResourceInstanceDO();
		resourceInstanceDO.setDiscoverNode(discoverNode);
		resourceInstanceDO.setInstanceId(instanceId);

		try {
			resourceInstanceDAO.updateResourceInstance(resourceInstanceDO);
		} catch (Exception e) {
			if (logger.isErrorEnabled()) {
				logger.error("updateResourceInstanceDiscoverNode error!", e);
			}
			throw new InstancelibException(ServerErrorCodeConstant.ERR_SERVER_OPERATE_DATABASE_ERROR,
					"updateResourceInstanceDiscoverNode error!");
		}

		// ????????????
		ResourceInstance cacheInstance = cache.get(instanceId);
		if (cacheInstance != null) {
			cacheInstance.setDiscoverNode(discoverNode);
			cache.update(cacheInstance.getId(), cacheInstance);
		}

		// ????????????
		listenerNotification(instancelibEvent);
		if (logger.isTraceEnabled()) {
			logger.trace("updateResourceInstanceState end discoverNode=" + discoverNode);
		}
	}

	@Override
	public void removeResourceInstance(long instanceId) throws InstancelibException {
		if (logger.isTraceEnabled()) {
			logger.trace("removeResourceInstance start instanceId=" + instanceId);
		}
		// ????????????????????????
		ResourceInstance currentResourceInstance = getResourceInstanceById(instanceId);
		if (currentResourceInstance == null) {
			return;
		}
		List<Long> deleteIds = getAllInstanceId(currentResourceInstance);
		List<ResourceInstanceDO> allResourceInstanceDOs = new ArrayList<>();
		for (long item : deleteIds) {
			ResourceInstanceDO updateDo = new ResourceInstanceDO();
			updateDo.setInstanceId(item);
			updateDo.setLifeState(InstanceLifeStateEnum.DELETED.name());
			allResourceInstanceDOs.add(updateDo);
		}
		try {
			resourceInstanceDAO.removeResourceInstance(deleteIds);
			// resourceInstanceDAO.updateResourceInstances(allResourceInstanceDOs);
			customModulePropService.removeCustomModulePropByInstanceIds(deleteIds);
		} catch (Exception e) {
			if (logger.isErrorEnabled()) {
				logger.error("removeResourceInstance(update lifestate='deleted') error", e);
			}
		}
		List<Long> instance = new ArrayList<Long>(1);
		instance.add(instanceId);
		final InstancelibEvent instancelibEvent = new InstancelibEvent(instance, null, EventEnum.INSTANCE_DELETE_EVENT);
		// ????????????
		try {
			interceptorNotification(instancelibEvent);
		} catch (Exception e) {
			if (logger.isErrorEnabled()) {
				logger.error("removeResourceInstance interceptor error!", e);
			}
			throw new InstancelibException(ServerErrorCodeConstant.ERR_SERVER_RESPO_INTERCEPTOR,
					"removeResourceInstance interceptor error");
		}
		// ??????????????????--??????????????????????????????????????????????????????
		List<ResourceInstance> cacheInstances = new ArrayList<>(deleteIds.size());
		for (long item : deleteIds) {
			ResourceInstance cacheInstance = cache.get(item);
			if (cacheInstance != null) {
				cacheInstances.add(cacheInstance);
			}
		}
		for (ResourceInstance resourceInstance : cacheInstances) {
			resourceInstance.setLifeState(InstanceLifeStateEnum.DELETED);
			cache.update(resourceInstance.getId(), resourceInstance);
			customModulePropCache.remove(resourceInstance.getId());
		}
		LicenseUtil.getLicenseUtil().deleleResourceInstanceUpdateLicense(instanceId);
		// ????????????
		listenerNotification(instancelibEvent);
		if (logger.isTraceEnabled()) {
			logger.trace("removeResourceInstance end");
		}
	}

	@Override
	public void removeChildResourceInstance(long instanceId) throws InstancelibException {
		if (logger.isInfoEnabled()) {
			logger.info("removeChildResourceInstance PHYSICAL_DELETE start instanceId=" + instanceId);
		}
		// ????????????????????????
		ResourceInstance currentResourceInstance = loadResourceInstance(instanceId);
		List<Long> deleteIds = new ArrayList<Long>(1);
		if (currentResourceInstance != null) {
			if (currentResourceInstance.getParentId() > 0) {
				deleteIds.add(instanceId);
			} else {
				if (logger.isErrorEnabled()) {
					logger.error("removeChildResourceInstance only delete parnetId,instanceId=" + instanceId);
				}
				return;
			}
		} else {
			if (logger.isInfoEnabled()) {
				logger.info("removeChildResourceInstance end instanceId=" + instanceId);
			}
			return;
		}
		try {
			resourceInstanceDAO.removeResourceInstanceByIds(deleteIds);
			propDAO.removePropDOByInstances(deleteIds);
			propTypeDAO.removePropTypeDOByInstances(deleteIds);
			customModulePropService.removeCustomModulePropByInstanceIds(deleteIds);
		} catch (Exception e) {
			if (logger.isErrorEnabled()) {
				logger.error("removeChildResourceInstance error", e);
			}
			throw new InstancelibException(ServerErrorCodeConstant.ERR_SERVER_OPERATE_DATABASE_ERROR,
					"removeResourceInstance error");
		}
		final InstancelibEvent instancelibEvent = new InstancelibEvent(Arrays.asList(currentResourceInstance), null,
				EventEnum.INSTANCE_CHILDREN_DELETE_EVENT);
		// ????????????
		try {
			interceptorNotification(instancelibEvent);
		} catch (Exception e) {
			if (logger.isErrorEnabled()) {
				logger.error("removeChildResourceInstance interceptor error!", e);
			}
			throw new InstancelibException(ServerErrorCodeConstant.ERR_SERVER_RESPO_INTERCEPTOR,
					"removeChildResourceInstance interceptor error");
		}
		for (long item : deleteIds) {
			cache.remove(item);
			propCache.remove(item);
			customModulePropCache.remove(item);
		}
		// ????????????
		listenerNotification(instancelibEvent);
		if (logger.isInfoEnabled()) {
			logger.info("removeChildResourceInstance PHYSICAL_DELETE end instanceId=" + instanceId);
		}
	}

	@Override
	public void removeChildResourceInstance(List<Long> instanceIds) throws InstancelibException {
		if (logger.isInfoEnabled()) {
			logger.info("removeChildResourceInstance PHYSICAL_DELETE start instanceId=" + instanceIds);
		}

		if (CollectionUtils.isEmpty(instanceIds)) {
			if (logger.isInfoEnabled()) {
				logger.info("removeChildResourceInstance PHYSICAL_DELETE end instanceId is empty!");
			}
			return;
		}
		HashSet<Long> deleteIds = new HashSet<>();
		List<ResourceInstance> noticeResource = new ArrayList<>();
		for (long instanceId : instanceIds) {
			// ??????????????????ID
			ResourceInstance currentResourceInstance = getResourceInstanceById(instanceId);
			if (currentResourceInstance != null) {
				if (currentResourceInstance.getParentId() > 0) {
					// ??????????????????
					deleteIds.add(instanceId);
					noticeResource.add(currentResourceInstance);
				}
			}
		}
		if (CollectionUtils.isEmpty(deleteIds)) {
			if (logger.isInfoEnabled()) {
				logger.info("removeChildResourceInstance PHYSICAL_DELETE end instanceId is empty!");
			}
			return;
		}
		List<Long> result = null;
		try {
			result = new ArrayList<Long>(deleteIds);
			resourceInstanceDAO.removeResourceInstanceByIds(result);
			propDAO.removePropDOByInstances(result);
			propTypeDAO.removePropTypeDOByInstances(result);
			customModulePropService.removeCustomModulePropByInstanceIds(result);
		} catch (Exception e) {
			if (logger.isErrorEnabled()) {
				logger.error("removeChildResourceInstance error", e);
			}
			throw new InstancelibException(ServerErrorCodeConstant.ERR_SERVER_OPERATE_DATABASE_ERROR,
					"removeResourceInstance error");
		}

		final InstancelibEvent instancelibEvent = new InstancelibEvent(noticeResource, null,
				EventEnum.INSTANCE_CHILDREN_DELETE_EVENT);
		// ????????????
		try {
			interceptorNotification(instancelibEvent);
		} catch (Exception e) {
			if (logger.isErrorEnabled()) {
				logger.error("removeChildResourceInstance interceptor error!", e);
			}
			throw new InstancelibException(ServerErrorCodeConstant.ERR_SERVER_RESPO_INTERCEPTOR,
					"removeChildResourceInstance interceptor error");
		}
		// ??????????????????--??????????????????????????????????????????????????????
		for (long item : deleteIds) {
			cache.remove(item);
			propCache.remove(item);
			customModulePropCache.remove(item);
		}
		// ????????????
		listenerNotification(instancelibEvent);
		if (logger.isInfoEnabled()) {
			logger.info("removeChildResourceInstance PHYSICAL_DELETE end instanceId=" + result);
		}
	}

	/**
	 * ????????????????????????,????????????????????????????????????????????????????????????????????????????????? ??????????????????????????????????????????
	 */
	@Override
	public List<Long> deleteResourceInstanceByResourceIds(HashSet<String> resourceIds) throws InstancelibException {
		if (logger.isInfoEnabled()) {
			logger.info("deleteResourceInstanceByResourceIds PHYSICAL_DELETE start resourceId=" + resourceIds);
		}
		List<ResourceInstance> resourceIntanceIds = new ArrayList<ResourceInstance>(100);
		List<ResourceInstance> allParentInstance = getAllParentInstance(true);
		if (CollectionUtils.isNotEmpty(allParentInstance)) {
			for (ResourceInstance instance : allParentInstance) {
				List<ResourceInstance> children = getChildInstanceByParentId(instance.getId());
				if (children != null) {
					for (ResourceInstance child : children) {
						if (resourceIds.contains(child.getResourceId())) {
							resourceIntanceIds.add(child);
						}
					}
				}
			}
		}
		List<Long> deleteIds = null;
		if (resourceIntanceIds != null && !resourceIntanceIds.isEmpty()) {
			deleteIds = new ArrayList<Long>();
			for (ResourceInstance resourceInstance : resourceIntanceIds) {
				deleteIds.add(resourceInstance.getId());
			}
			deleteResourceInstanceByIds(deleteIds, resourceIds);
		}
		if (logger.isInfoEnabled()) {
			logger.info("deleteResourceInstanceByResourceIds end resourceId=" + resourceIds);
		}
		return deleteIds;
	}

	/**
	 * ????????????????????????
	 */
	private void deleteResourceInstanceByIds(List<Long> deleteIds, HashSet<String> resourceIds)
			throws InstancelibException {
		if (logger.isInfoEnabled()) {
			logger.info("deleteResourceInstanceByIds start instanceId=" + deleteIds);
		}
		try {
			resourceInstanceDAO.removeResourceInstanceByIds(deleteIds);
			propDAO.removePropDOByInstances(deleteIds);
			propTypeDAO.removePropTypeDOByInstances(deleteIds);
		} catch (Exception e) {
			if (logger.isErrorEnabled()) {
				logger.error("deleteResourceInstanceByIds error", e);
			}
			throw new InstancelibException(ServerErrorCodeConstant.ERR_SERVER_OPERATE_DATABASE_ERROR,
					"removeResourceInstance(update lifestate='deleted') error");
		}
		final InstancelibEvent instancelibEvent = new InstancelibEvent(deleteIds, null, resourceIds,
				EventEnum.INSTANCE_PHYSICAL_DELETE_EVENT);
		// ????????????
		try {
			interceptorNotification(instancelibEvent);
		} catch (Exception e) {
			if (logger.isErrorEnabled()) {
				logger.error("deleteResourceInstanceByIds interceptor error!", e);
			}
			throw new InstancelibException(ServerErrorCodeConstant.ERR_SERVER_RESPO_INTERCEPTOR,
					"removeResourceInstanceByLink interceptor error");
		}
		// ??????????????????--??????????????????????????????????????????????????????
		for (long item : deleteIds) {
			ResourceInstance deletedResourceInstance = getResourceInstanceById(item);
			if(deletedResourceInstance != null){
				UniqueInstanceCache.getInstanceCache().removeUnique(deletedResourceInstance);
			}
			cache.remove(item);
			propCache.remove(item);
		}
		// ????????????
		listenerNotification(instancelibEvent);
		if (logger.isInfoEnabled()) {
			logger.info("deleteResourceInstanceByIds end instanceId=" + deleteIds);
		}
	}

	public void removeResourceInstanceByLinks(List<Long> deleteIds) throws InstancelibException {
		if (logger.isInfoEnabled()) {
			logger.info("removeResourceInstanceByLinks start instanceId=" + deleteIds);
		}
		if (CollectionUtils.isEmpty(deleteIds)) {
			if (logger.isInfoEnabled()) {
				logger.info("removeResourceInstanceByLinks end instanceId is empty");
			}
			return;
		}
		try {
			resourceInstanceDAO.removeResourceInstanceByIds(deleteIds);
			propDAO.removePropDOByInstances(deleteIds);
			propTypeDAO.removePropTypeDOByInstances(deleteIds);
		} catch (Exception e) {
			if (logger.isErrorEnabled()) {
				logger.error("removeResourceInstanceByLink error", e);
			}
			throw new InstancelibException(ServerErrorCodeConstant.ERR_SERVER_OPERATE_DATABASE_ERROR,
					"removeResourceInstance(update lifestate='deleted') error");
		}
		final InstancelibEvent instancelibEvent = new InstancelibEvent(deleteIds, null,
				EventEnum.INSTANCE_DELETE_EVENT);
		// ????????????
		try {
			interceptorNotification(instancelibEvent);
		} catch (Exception e) {
			if (logger.isErrorEnabled()) {
				logger.error("removeResourceInstanceByLink interceptor error!", e);
			}
			throw new InstancelibException(ServerErrorCodeConstant.ERR_SERVER_RESPO_INTERCEPTOR,
					"removeResourceInstanceByLink interceptor error");
		}
		// ??????????????????--??????????????????????????????????????????????????????
		for (long item : deleteIds) {
			ResourceInstance deletedResourceInstance = getResourceInstanceById(item);
			if(deletedResourceInstance != null){
				UniqueInstanceCache.getInstanceCache().removeUnique(deletedResourceInstance);
			}
			cache.remove(item);
			customModulePropCache.remove(item);
			HashSet<Long> parentIds = cache.getParentInstances();
			if (parentIds != null) {
				parentIds.remove(item);
			}
		}
		// ????????????
		listenerNotification(instancelibEvent);
		if (logger.isInfoEnabled()) {
			logger.info("removeResourceInstanceByLinks end instanceId=" + deleteIds);
		}
	}

	@Override
	public void removeResourceInstances(List<Long> instanceIds) throws InstancelibException {
		if (logger.isTraceEnabled()) {
			logger.trace("removeResourceInstances start instanceId=" + instanceIds);
		}

		List<Long> deleteIds = new ArrayList<>();
		for (long instanceId : instanceIds) {
			// ??????????????????ID
			ResourceInstance currentResourceInstance = getResourceInstance(instanceId);
			if (currentResourceInstance != null) {
				deleteIds.addAll(getAllInstanceId(currentResourceInstance));
			}
		}

		List<ResourceInstanceDO> allResourceInstanceDOs = new ArrayList<>();
		for (long item : deleteIds) {
			ResourceInstanceDO updateDo = new ResourceInstanceDO();
			updateDo.setInstanceId(item);
			updateDo.setLifeState(InstanceLifeStateEnum.DELETED.name());
			allResourceInstanceDOs.add(updateDo);
		}
		try {
			// resourceInstanceDAO.updateResourceInstances(allResourceInstanceDOs);
			resourceInstanceDAO.removeResourceInstance(deleteIds);
			customModulePropService.removeCustomModulePropByInstanceIds(deleteIds);
		} catch (Exception e) {
			if (logger.isErrorEnabled()) {
				logger.error("removeResourceInstance(update lifestate='deleted') error", e);
			}
			throw new InstancelibException(ServerErrorCodeConstant.ERR_SERVER_OPERATE_DATABASE_ERROR,
					"removeResourceInstance(update lifestate='deleted') error");
		}
		final InstancelibEvent instancelibEvent = new InstancelibEvent(instanceIds, null,
				EventEnum.INSTANCE_DELETE_EVENT);
		// ????????????
		try {
			interceptorNotification(instancelibEvent);
		} catch (Exception e) {
			if (logger.isErrorEnabled()) {
				logger.error("removeResourceInstance interceptor error!", e);
			}
			throw new InstancelibException(ServerErrorCodeConstant.ERR_SERVER_RESPO_INTERCEPTOR,
					"removeResourceInstance interceptor error");
		}
		List<ResourceInstance> cacheInstances = new ArrayList<>(deleteIds.size());
		// ??????????????????--??????????????????????????????????????????????????????
		for (long item : deleteIds) {
			ResourceInstance cacheInstance = cache.get(item);
			if (cacheInstance != null) {
				cacheInstances.add(cacheInstance);
			}
		}
		for (ResourceInstance resourceInstance : cacheInstances) {
			resourceInstance.setLifeState(InstanceLifeStateEnum.DELETED);
			cache.update(resourceInstance.getId(), resourceInstance);
			customModulePropCache.remove(resourceInstance.getId());
		}
		//??????license??????
		for (long resourceInstanceId : instanceIds) {
			LicenseUtil.getLicenseUtil().deleleResourceInstanceUpdateLicense(resourceInstanceId);
		}
		// ????????????
		listenerNotification(instancelibEvent);
		if (logger.isTraceEnabled()) {
			logger.trace("removeResourceInstances end");
		}
	}

	@Override
	public ResourceInstance getResourceInstance(long instanceId) {
		if (logger.isTraceEnabled()) {
			logger.trace("getResourceInstances start instanceId=" + instanceId);
		}
		if (instanceId <= 0) {
			return null;
		}
		QueryResourceInstance result = null;
		ResourceInstance resourceInstance = loadResourceInstance(instanceId);
		if (resourceInstance == null) {
			if (logger.isDebugEnabled()) {
				logger.debug("resourceInstance not found,instanceId=" + instanceId);
			}
		} else {
			if (resourceInstance.getLifeState() == InstanceLifeStateEnum.DELETED
					|| resourceInstance.getLifeState() == InstanceLifeStateEnum.INITIALIZE) {
				if (logger.isDebugEnabled()) {
					logger.debug("resourceInstance has deleted or not initialize!,instanceId=" + instanceId);
				}
			} else {
				result = convertTOQueryResourceInstance(resourceInstance);
			}
		}
		if (logger.isTraceEnabled()) {
			logger.trace("getResourceInstances end");
		}
		return result;
	}

	@Override
	public ResourceInstance getResourceInstanceWithDeleted(long instanceId) throws InstancelibException {
		if (logger.isTraceEnabled()) {
			logger.trace("getResourceInstanceWithDeleted start instanceId=" + instanceId);
		}
		QueryResourceInstance result = null;
		ResourceInstance resourceInstance = loadResourceInstance(instanceId);
		if (resourceInstance == null) {
			if (logger.isDebugEnabled()) {
				logger.debug("resourceInstance not found,instanceId=" + instanceId);
			}
		} else {
			if (resourceInstance.getLifeState() == InstanceLifeStateEnum.INITIALIZE) {
				if (logger.isDebugEnabled()) {
					logger.debug("resourceInstance has deleted or not initialize!,instanceId=" + instanceId);
				}
			} else {
				result = convertTOQueryResourceInstance(resourceInstance);
			}
		}
		if (logger.isTraceEnabled()) {
			logger.trace("getResourceInstanceWithDeleted end");
		}
		return result;
	}

	/**
	 * ????????????(?????????????????????????????????)
	 */
	public ResourceInstance getResourceInstanceById(long instanceId) throws InstancelibException {
		if (logger.isTraceEnabled()) {
			logger.trace("getResourceInstanceById start instanceId=" + instanceId);
		}
		if (instanceId <= 0) {
			return null;
		}
		ResourceInstance resourceInstance = loadResourceInstance(instanceId);
		if (resourceInstance == null) {
			if (logger.isDebugEnabled()) {
				logger.debug("resourceInstance not found,instanceId=" + instanceId);
			}
		}
		QueryResourceInstance result = null;
		if (resourceInstance != null) {
			result = convertTOQueryResourceInstance(resourceInstance);
		}
		if (logger.isTraceEnabled()) {
			logger.trace("getResourceInstanceById end instanceId=" + instanceId);
		}
		return result;
	}

	@Override
	public List<ResourceInstance> getResourceInstances(List<Long> instanceIds) throws InstancelibException {
		if (logger.isTraceEnabled()) {
			logger.trace("getResourceInstance start instanceId=" + instanceIds);
		}
		List<ResourceInstance> result = new ArrayList<ResourceInstance>();
		for (long instanceId : instanceIds) {
			ResourceInstance resourceInstance = getResourceInstance(instanceId);
			if (resourceInstance != null) {
				result.add(resourceInstance);
			}
		}
		if (logger.isTraceEnabled()) {
			logger.trace("getResourceInstance end");
		}
		return result;
	}

	private List<Long> getAllInstanceId(ResourceInstance currentResourceInstance) {
		List<Long> instanceIds = new ArrayList<>();
		instanceIds.add(currentResourceInstance.getId());
		List<ResourceInstance> children = currentResourceInstance.getChildren();
		if (CollectionUtils.isNotEmpty(children)) {
			for (ResourceInstance resourceInstance : children) {
				instanceIds.addAll(getAllInstanceId(resourceInstance));
			}
		}
		return instanceIds;
	}

	/**
	 * ?????????????????????????????????????????????????????????????????????
	 *
	 * @param instanceId
	 *            ??????ID
	 * @return ????????????
	 * @throws Exception
	 */
	private ResourceInstance loadResourceInstance(long instanceId) {

		// if (logger.isDebugEnabled()) {
		// logger.debug("loadResourceInstance start instanceId=" + instanceId);
		// }
		// ???????????????
		ResourceInstance resourceInstance = cache.get(instanceId);
		// if (logger.isDebugEnabled()) {
		// logger.debug("get data from cache end instanceId=" + instanceId);
		// }
		if (resourceInstance != null) {
			return resourceInstance;
		}
		// if (logger.isDebugEnabled()) {
		// logger.debug("get data from db start instanceId=" + instanceId);
		// }
		// ???????????????????????????
		ResourceInstanceDO tdo = resourceInstanceDAO.getResourceInstanceById(instanceId);
		// if (logger.isDebugEnabled()) {
		// logger.debug("get data from db end instanceId=" + instanceId);
		// }

		if (tdo != null) {
			/**
			 * ?????????id????????????????????????????????????
			 */
			ResourceInstance parentInstance = null;
			if (tdo.getParentId() != null) {
				// if (logger.isDebugEnabled()) {
				// logger.debug("loadResourceInstance parentId=" +
				// tdo.getParentId());
				// }
				parentInstance = loadResourceInstance(tdo.getParentId());
			}
			resourceInstance = convertToResourceInstance(tdo, parentInstance);
			// ???????????????
			cache.queryAdd(instanceId, resourceInstance);
		}
		// if (logger.isDebugEnabled()) {
		// logger.debug("loadResourceInstance end instanceId=" + instanceId);
		// }
		return resourceInstance;
	}

	@Override
	public List<ResourceInstance> getAllParentInstance() throws InstancelibException {
		if (logger.isDebugEnabled()) {
			logger.debug("getAllParentInstance start");
		}
		List<ResourceInstance> all = getAllParentInstance(false);
		List<ResourceInstance> result = new ArrayList<ResourceInstance>();
		// ????????????
		if (all != null) {
			for (ResourceInstance resourceInstnace : all) {
				if (!CapacityConst.LINK.equals(resourceInstnace.getCategoryId())) {
					QueryResourceInstance queryResourceInstance = convertTOQueryResourceInstance(resourceInstnace);
					result.add(queryResourceInstance);
				}
			}
		}
		if (logger.isDebugEnabled()) {
			logger.debug("getAllParentInstance end");
		}
		return result;
	}

	/**
	 * ??????CategoryId????????????????????????????????????,????????????????????????License?????????????????????
	 *
	 * @param categoryId
	 * @return
	 * @throws InstancelibException
	 */
	@Override
	public List<ResourceInstance> getParentInstanceByCategoryIdsFordb(Set<String> categoryIds)
			throws InstancelibException {
		List<ResourceInstanceDO> resourceInstanceDOs = null;
		List<ResourceInstance> instances = new ArrayList<ResourceInstance>();
		try {
			resourceInstanceDOs = resourceInstanceDAO.getAllParentInstance();
		} catch (Exception e) {
			if (logger.isErrorEnabled()) {
				logger.error("getAllParentInstance for db error", e);
			}
			throw new InstancelibException(ServerErrorCodeConstant.ERR_SERVER_OPERATE_DATABASE_ERROR,
					"getAllParentInstance error!");
		}
		if (!CollectionUtils.isEmpty(resourceInstanceDOs)) {
			List<QueryResourceInstance> queryResourceInstances = new ArrayList<QueryResourceInstance>();
			for (ResourceInstanceDO resourceInstanceDO : resourceInstanceDOs) {
				ResourceInstance resourceInstance = convertToResourceInstance(resourceInstanceDO, null);
				if (resourceInstance.getLifeState() == InstanceLifeStateEnum.MONITORED
						|| resourceInstance.getLifeState() == InstanceLifeStateEnum.NOT_MONITORED) {
					QueryResourceInstance queryResourceInstance = convertTOQueryResourceInstance(resourceInstance);
					queryResourceInstances.add(queryResourceInstance);
				}
			}
			if (!CollectionUtils.isEmpty(queryResourceInstances)) {
				for (String categoryId : categoryIds) {
					String parentCategoryId = getParentCategoryDef(categoryId).getId();
					List<QueryResourceInstance> queryInstance = new ArrayList<QueryResourceInstance>();
					for (QueryResourceInstance resourceInstance : queryResourceInstances) {
						CategoryDef parentCategoryDef = getParentCategoryDef(resourceInstance.getCategoryId());
						if (!parentCategoryId.equals(categoryId)) {
							if (resourceInstance.getCategoryId().equals(categoryId)) {
								queryInstance.add(resourceInstance);
							}
						} else {
							if (parentCategoryDef.getId().equals(categoryId)) {
								queryInstance.add(resourceInstance);
							}
						}
					}
					instances.addAll(queryInstance);
				}
			}
		}
		return instances;
	}

	public List<ResourceInstance> getAllParentInstance(boolean isAll) throws InstancelibException {
		List<ResourceInstance> result = null;
		/*
		 * ???????????????????????????????????????Id
		 */
		long startTime = System.currentTimeMillis();
		if (logger.isDebugEnabled()) {
			logger.debug("getAllParentInstance data form cache start");
		}
		HashSet<Long> parentInstanceId = cache.getParentInstances();
		long endTime = System.currentTimeMillis();
		long diff = endTime - startTime;
		if (logger.isDebugEnabled()) {
			logger.debug("getAllParentInstance data form cache end time= " + diff);
		}
		if (CollectionUtils.isNotEmpty(parentInstanceId)) {
			result = new ArrayList<>(parentInstanceId.size());
			for (Long instanceId : parentInstanceId) {
				ResourceInstance resourceInstance = getResourceInstanceById(instanceId);
				if (resourceInstance != null) {
					if (isAll) {
						result.add(resourceInstance);
					} else {
						if (resourceInstance.getLifeState() != InstanceLifeStateEnum.DELETED
								&& resourceInstance.getLifeState() != InstanceLifeStateEnum.INITIALIZE) {
							result.add(resourceInstance);
						}
					}
					if(!isLoadUnique){
						UniqueInstanceCache.getInstanceCache().addUnique(resourceInstance);
					}
				} else {
					if (logger.isWarnEnabled()) {
						logger.warn("getAllParentInstance resourceInstance not found,instanceId=" + instanceId);
					}
				}
			}

		} else {
			/*
			 * ????????????????????????????????????
			 */
			List<ResourceInstanceDO> resourceInstanceDOs = null;
			try {
				resourceInstanceDOs = resourceInstanceDAO.getAllParentInstance();
			} catch (Exception e) {
				if (logger.isErrorEnabled()) {
					logger.error("getAllParentInstance error", e);
				}
				throw new InstancelibException(ServerErrorCodeConstant.ERR_SERVER_OPERATE_DATABASE_ERROR,
						"getAllParentInstance error!");
			}
			if (resourceInstanceDOs != null && !resourceInstanceDOs.isEmpty()) {
				result = new ArrayList<ResourceInstance>();
				HashSet<Long> parentIds = new HashSet<Long>();
				boolean cacheActivate = cache.getResourceInstanceCacheActivate();
				for (ResourceInstanceDO resourceInstanceDO : resourceInstanceDOs) {
					ResourceInstance resourceInstance = convertToResourceInstance(resourceInstanceDO, null);
					if (isAll) {
						QueryResourceInstance queryResourceInstance = convertTOQueryResourceInstance(resourceInstance);
						result.add(queryResourceInstance);
					} else {
						if (resourceInstance.getLifeState() != InstanceLifeStateEnum.DELETED
								&& resourceInstance.getLifeState() != InstanceLifeStateEnum.INITIALIZE) {
							QueryResourceInstance queryResourceInstance = convertTOQueryResourceInstance(
									resourceInstance);
							result.add(queryResourceInstance);
						}
					}
					parentIds.add(resourceInstanceDO.getInstanceId());
					if (cacheActivate) {
						cache.queryAdd(resourceInstanceDO.getInstanceId(), resourceInstance);
					}
					if(!isLoadUnique){
						UniqueInstanceCache.getInstanceCache().addUnique(resourceInstance);
					}
				}
				// ???????????????
				cache.setParentInstance(parentIds);
			}
		}
		isLoadUnique = true;
		return result;
	}

	@Override
	public List<ResourceInstance> getParentInstanceByCategoryId(String categoryId) throws InstancelibException {
		if (logger.isTraceEnabled()) {
			logger.trace("getParentInstanceByCategoryId start categoryId=" + categoryId);
		}
		List<ResourceInstance> instances = new ArrayList<ResourceInstance>();
		String parentCategoryId = getParentCategoryDef(categoryId).getId();
		HashSet<Long> parentInstanceId = cache.getParentInstance(parentCategoryId);
		if (parentInstanceId != null) {
			for (Long instanceId : parentInstanceId) {
				ResourceInstance resourceInstance = getResourceInstanceById(instanceId);
				if (resourceInstance != null) {
					if (resourceInstance.getLifeState() != InstanceLifeStateEnum.DELETED
							&& resourceInstance.getLifeState() != InstanceLifeStateEnum.INITIALIZE) {
						if (!parentCategoryId.equals(categoryId)) {
							if (resourceInstance.getCategoryId().equals(categoryId)) {
								instances.add(resourceInstance);
							}
						} else {
							instances.add(resourceInstance);
						}
					}
				} else {
					if (logger.isWarnEnabled()) {
						logger.warn("getAllParentInstance resourceInstance not found,instanceId=" + instanceId);
					}
				}
			}
		} else {
			/*
			 * ????????????????????????????????????
			 */
			List<ResourceInstanceDO> resourceInstanceDOs = null;
			try {
				resourceInstanceDOs = resourceInstanceDAO.getAllParentInstance();
			} catch (Exception e) {
				if (logger.isErrorEnabled()) {
					logger.error("getAllParentInstance error", e);
				}
				throw new InstancelibException(ServerErrorCodeConstant.ERR_SERVER_OPERATE_DATABASE_ERROR,
						"getAllParentInstance error!");
			}
			if (resourceInstanceDOs != null && !resourceInstanceDOs.isEmpty()) {
				instances = new ArrayList<ResourceInstance>();
				HashSet<Long> parentIds = new HashSet<Long>();
				for (ResourceInstanceDO resourceInstanceDO : resourceInstanceDOs) {
					ResourceInstance resourceInstance = convertToResourceInstance(resourceInstanceDO, null);
					if (resourceInstance.getLifeState() != InstanceLifeStateEnum.DELETED
							&& resourceInstance.getLifeState() != InstanceLifeStateEnum.INITIALIZE) {
						QueryResourceInstance queryResourceInstance = convertTOQueryResourceInstance(resourceInstance);
						CategoryDef parentCategoryDef = getParentCategoryDef(resourceInstance.getCategoryId());
						if (!parentCategoryId.equals(categoryId)) {
							if (resourceInstance.getCategoryId().equals(categoryId)) {
								instances.add(queryResourceInstance);
							}
						} else {
							if (parentCategoryDef.getId().equals(categoryId)) {
								instances.add(queryResourceInstance);
							}
						}
						logger.info("old resournce load: id=" + resourceInstance.getId() + " name"
								+ resourceInstance.getName() + " categoryId=" + resourceInstance.getCategoryId());
						parentIds.add(resourceInstanceDO.getInstanceId());
						cache.queryAdd(resourceInstanceDO.getInstanceId(), resourceInstance);
					}
				}
				// ???????????????
				cache.setParentInstance(parentIds);
			}
		}

		if (logger.isTraceEnabled()) {
			logger.trace("getParentInstanceByCategoryId end");
		}
		return instances;
	}

	@Override
	public List<ResourceInstance> getParentInstanceByCategoryIds(HashSet<String> categoryIds)
			throws InstancelibException {
		if (logger.isTraceEnabled()) {
			logger.trace("getParentInstanceByCategoryId start categoryId=" + categoryIds);
		}
		List<ResourceInstance> result = null;
		if (categoryIds != null) {
			result = new ArrayList<ResourceInstance>();
			for (String categoryId : categoryIds) {
				List<ResourceInstance> instances = getParentInstanceByCategoryId(categoryId);
				if (instances != null) {
					result.addAll(instances);
					logger.info("?????????" + categoryId + "???????????????????????????" + instances.size());
				}
			}
		}
		return result;
	}

	@Override
	public List<ResourceInstance> getResourceInstanceByResourceId(String resourceId) throws InstancelibException {
		if (logger.isTraceEnabled()) {
			logger.trace("getResourceInstanceByResourceId start resourceId" + resourceId);
		}
		ResourceDef def = capacityService.getResourceDefById(resourceId);
		String parentResourceId = null;
		boolean isChild = false;
		if (def.getParentResourceDef() != null) {
			isChild = true;
			parentResourceId = def.getParentResourceDef().getId();
		}
		List<ResourceInstance> result = new ArrayList<ResourceInstance>(100);
		List<ResourceInstance> allParentInstance = getAllParentInstance(false);
		if (allParentInstance != null) {
			for (ResourceInstance instance : allParentInstance) {
				if (isChild && parentResourceId.equals(instance.getResourceId())) {
					List<ResourceInstance> children = getChildInstanceByParentId(instance.getId());
					if (children != null) {
						for (ResourceInstance child : children) {
							if (resourceId.equals(child.getResourceId())) {
								result.add(child);
							}
						}
					}
				} else {
					if (resourceId.equals(instance.getResourceId())) {
						result.add(instance);
					}
				}
			}
		}
		if (logger.isTraceEnabled()) {
			logger.trace("getResourceInstanceByResourceId end");
		}
		return result;
	}

	private List<ResourceInstance> getResourceInstanceByResourceId(HashSet<String> resourceIds, boolean isAll)
			throws InstancelibException {
		// key:parentResourceId value:
		// childResourceId(key:childResourceId,value:null)
		HashMap<String, HashMap<String, Object>> map = new HashMap<>(resourceIds.size());
		HashMap<String, Boolean> isParentResourceId = new HashMap<String, Boolean>();
		for (String resourceId : resourceIds) {
			ResourceDef def = capacityService.getResourceDefById(resourceId);
			if (def.getParentResourceDef() != null) {
				// ???
				String parentResourceId = def.getParentResourceDef().getId();
				HashMap<String, Object> childMap = map.get(parentResourceId);
				if (childMap == null) {
					childMap = new HashMap<String, Object>();
					map.put(parentResourceId, childMap);
				}
				childMap.put(resourceId, null);
			} else {
				// ???
				HashMap<String, Object> childMap = map.get(resourceId);
				if (childMap == null) {
					map.put(resourceId, null);
				}
				isParentResourceId.put(resourceId, true);
			}
		}
		List<ResourceInstance> result = new ArrayList<ResourceInstance>(100);
		List<ResourceInstance> allParentInstance = getAllParentInstance(isAll);
		if (CollectionUtils.isNotEmpty(allParentInstance)) {
			for (ResourceInstance instance : allParentInstance) {
				String parentResourceId = instance.getResourceId();
				if (map.containsKey(parentResourceId)) {
					// ???????????????????????????????????????
					if (isParentResourceId.containsKey(parentResourceId)) {
						result.add(instance);
					}
					// ?????????
					HashMap<String, Object> childMap = map.get(parentResourceId);
					if (childMap != null && !childMap.isEmpty()) {
						List<ResourceInstance> children = getChildInstanceByParentId(instance.getId());
						if (children != null) {
							for (ResourceInstance child : children) {
								if (childMap.containsKey(child.getResourceId())) {
									result.add(child);
								}
							}
						}
					}
				}
			}
		}
		return result;
	}

	@Override
	public List<ResourceInstance> getResourceInstanceByResourceId(HashSet<String> resourceIds)
			throws InstancelibException {
		if (logger.isTraceEnabled()) {
			logger.trace("getResourceInstanceByResourceId start resourceIds" + resourceIds);
		}
		List<ResourceInstance> result = getResourceInstanceByResourceId(resourceIds, false);
		if (logger.isTraceEnabled()) {
			logger.trace("getResourceInstanceByResourceId end");
		}
		return result;
	}

	@Override
	public List<ResourceInstance> getExistParentInstanceByResourceId(HashSet<String> resourceIds)
			throws InstancelibException {
		if (logger.isTraceEnabled()) {
			logger.trace("getExistParentInstanceByResourceId start resourceIds" + resourceIds);
		}
		List<ResourceInstance> result = null;
		// List<ResourceInstanceDO> resourceInstanceDOs = resourceInstanceDAO
		// .getExistParentInstanceByResourceId(new ArrayList<String>(resourceIds));
		List<ResourceInstanceDO> resourceInstanceDOs = resourceInstanceDAO
				.getExistChildInstanceByResourceId(new ArrayList<String>(resourceIds));
		if (CollectionUtils.isNotEmpty(resourceInstanceDOs)) {
			result = new ArrayList<>(resourceInstanceDOs.size());
			for (ResourceInstanceDO resourceInstanceDO : resourceInstanceDOs) {
				ResourceInstance resourceInstance = convertToResourceInstance(resourceInstanceDO, null);
				result.add(resourceInstance);
			}
		}
		if (logger.isTraceEnabled()) {
			logger.trace("getExistParentInstanceByResourceId end");
		}
		return result;
	}

	@Override
	public List<ResourceInstance> getChildInstanceByParentId(long parentId) throws InstancelibException {
		if (logger.isTraceEnabled()) {
			logger.trace("getChildInstanceByParentId start parentId=" + parentId);
		}
		List<ResourceInstance> result = loadChildInstanceByParentId(parentId, false);
		if (logger.isTraceEnabled()) {
			logger.trace("getChildInstanceByParentId end");
		}
		return result;
	}

	@Override
	public List<ResourceInstance> getChildInstanceByParentId(long parentId, boolean isAll) throws InstancelibException {
		if (logger.isTraceEnabled()) {
			logger.trace("getChildInstanceByParentId start parentId=" + parentId);
		}
		List<ResourceInstance> result = loadChildInstanceByParentId(parentId, isAll);
		if (logger.isTraceEnabled()) {
			logger.trace("getChildInstanceByParentId end");
		}
		return result;
	}

	@Override
	public List<ResourceInstance> getParentResourceInstanceByLifeState(InstanceLifeStateEnum lifeState)
			throws InstancelibException {
		if (logger.isTraceEnabled()) {
			logger.trace("getParentResourceInstanceByLifeState start lifeState" + lifeState.toString());
		}
		List<ResourceInstance> resourceInstances = getAllParentInstance();
		List<ResourceInstance> resourceInstanceLifeState = new ArrayList<>();
		if (CollectionUtils.isNotEmpty(resourceInstances)) {
			for (ResourceInstance resourceInstance : resourceInstances) {
				if (resourceInstance.getLifeState() == lifeState) {
					resourceInstanceLifeState.add(resourceInstance);
				}
			}
		}
		if (logger.isTraceEnabled()) {
			logger.trace("getParentResourceInstanceByLifeState end lifeState" + lifeState.toString());
		}
		return resourceInstanceLifeState;
	}

	@Override
	public List<ResourceInstance> getParentResourceInstanceByNode(String groudNodeId) throws InstancelibException {
		if (logger.isTraceEnabled()) {
			logger.trace("getParentResourceInstanceByNode start groudNodeId" + groudNodeId);
		}
		List<ResourceInstance> result = null;
		List<ResourceInstance> resourceInstances = getAllParentInstance();
		if (CollectionUtils.isNotEmpty(resourceInstances)) {
			result = new ArrayList<>(resourceInstances.size());
			for (ResourceInstance resourceInstance : resourceInstances) {
				if (resourceInstance.getLifeState() != InstanceLifeStateEnum.DELETED
						&& resourceInstance.getLifeState() != InstanceLifeStateEnum.INITIALIZE) {
					if (groudNodeId.equals(resourceInstance.getDiscoverNode())) {
						result.add(resourceInstance);
					}
				}
			}
		}
		if (logger.isTraceEnabled()) {
			logger.trace("getParentResourceInstanceByLifeState start groudNodeId" + groudNodeId);
		}
		return result;
	}

	@Override
	public List<ResourceInstance> getParentResourceInstanceByDomainIds(Set<Long> domainIds)
			throws InstancelibException {
		if (logger.isTraceEnabled()) {
			logger.trace("getParentResourceInstanceByDomain start domainId" + domainIds);
		}
		List<ResourceInstance> results = null;
		List<ResourceInstance> resourceInstances = getAllParentInstance();
		if (CollectionUtils.isNotEmpty(resourceInstances)) {
			results = new ArrayList<>(resourceInstances.size());
			for (ResourceInstance resourceInstance : resourceInstances) {
				if (resourceInstance.getLifeState() != InstanceLifeStateEnum.DELETED
						&& resourceInstance.getLifeState() != InstanceLifeStateEnum.INITIALIZE) {
					if (domainIds.contains(resourceInstance.getDomainId())) {
						results.add(resourceInstance);
					}
				}
			}
		}
		if (logger.isTraceEnabled()) {
			logger.trace("getParentResourceInstanceByDomain end domainId" + domainIds);
		}
		return results;
	}

	@Override
	public List<ResourceInstance> getParentResourceInstanceByDiscoverWay(DiscoverWayEnum way)
			throws InstancelibException {
		if (logger.isTraceEnabled()) {
			logger.trace("getParentResourceInstanceByDomain start discoverWay" + way);
		}
		List<ResourceInstance> results = null;
		List<ResourceInstance> resourceInstances = getAllParentInstance();
		if (CollectionUtils.isNotEmpty(resourceInstances)) {
			results = new ArrayList<>(resourceInstances.size());
			for (ResourceInstance resourceInstance : resourceInstances) {
				if (resourceInstance.getLifeState() != InstanceLifeStateEnum.DELETED
						&& resourceInstance.getLifeState() != InstanceLifeStateEnum.INITIALIZE) {
					if (way == resourceInstance.getDiscoverWay()) {
						results.add(resourceInstance);
					}
				}
			}
		}
		if (logger.isTraceEnabled()) {
			logger.trace("getParentResourceInstanceByDomain end discoverWay" + way);
		}
		return results;
	}

	private void calcProp(ResourceInstance oldInstance, ResourceInstance newInstance) {
		// ??????????????????????????????????????????????????????????????????
		List<DiscoverProp> oldDiscoverProp = oldInstance.getDiscoverProps();
		if (CollectionUtils.isNotEmpty(oldDiscoverProp)) {
			List<DiscoverProp> newDiscoverProp = newInstance.getDiscoverProps();
			HashMap<String, DiscoverProp> newDiscoverPropMap = new HashMap<String, DiscoverProp>();
			if (CollectionUtils.isNotEmpty(newDiscoverProp)) {
				for (DiscoverProp discover : newDiscoverProp) {
					StringBuilder bb = new StringBuilder(50);
					bb.append(discover.getKey().trim());
					bb.append(PropTypeEnum.DISCOVER.toString());
					String key = bb.toString();
					newDiscoverPropMap.put(key, discover);
				}
				for (DiscoverProp discover : oldDiscoverProp) {
					StringBuilder bb = new StringBuilder(50);
					bb.append(discover.getKey().trim());
					bb.append(PropTypeEnum.DISCOVER.toString());
					String key = bb.toString();
					if (!newDiscoverPropMap.containsKey(key)) {
						newDiscoverProp.add(discover);
					}
				}
			}
		}
	}

	protected void convertToList(List<ResourceInstance> allResourceInstances, List<PropDO> props,
			ResourceInstance resourceInstance, boolean isCalcChild) {

		if (allResourceInstances != null) {
			allResourceInstances.add(resourceInstance);
		}
		// ????????????
		List<ModuleProp> moduleProps = resourceInstance.getModuleProps();
		if (CollectionUtils.isNotEmpty(moduleProps)) {
			for (ModuleProp moduleProp : moduleProps) {
				moduleProp.setInstanceId(resourceInstance.getId());
				List<PropDO> propDOs = modulePropExtendService.convertToDOs(moduleProp);
				if (propDOs != null) {
					props.addAll(propDOs);
				}
			}
		}
		// ????????????-- ?????????????????????
		// modify by ziw at 2017???11???23??? ??????4:10:26 -- ????????????????????????
		// if(resourceInstance.getParentId() <= 0){
		List<DiscoverProp> discoverProps = resourceInstance.getDiscoverProps();
		if (CollectionUtils.isNotEmpty(discoverProps) && (resourceInstance.getParentInstance() == null
				|| (resourceInstance.getParentInstance() != null
				&& discoverProps != resourceInstance.getParentInstance().getDiscoverProps()))) {
			for (DiscoverProp discoverProp : discoverProps) {
				discoverProp.setInstanceId(resourceInstance.getId());
				List<PropDO> propDOs = discoverPropExtendService.convertToDOs(discoverProp);
				if (propDOs != null) {
					props.addAll(propDOs);
				}
			}
		}
		// }
		// ????????????
		List<ExtendProp> extendProps = resourceInstance.getExtendProps();
		if (CollectionUtils.isNotEmpty(extendProps)) {
			for (ExtendProp extendProp : extendProps) {
				extendProp.setInstanceId(resourceInstance.getId());
				List<PropDO> propDOs = extendPropExtendService.convertToDOs(extendProp);
				if (propDOs != null) {
					props.addAll(propDOs);
				}
			}
		}
		List<CustomProp> customProps = resourceInstance.getCustomProps();
		// ???????????????
		if (CollectionUtils.isNotEmpty(customProps)) {
			for (CustomProp customProp : customProps) {
				customProp.setInstanceId(resourceInstance.getId());
				List<PropDO> propDOs = customPropExtendService.convertToDOs(customProp);
				if (propDOs != null) {
					props.addAll(propDOs);
				}
			}
		}
		if (isCalcChild) {
			// ?????????????????????
			List<ResourceInstance> children = resourceInstance.getChildren();
			if (CollectionUtils.isNotEmpty(children)) {
				for (ResourceInstance childResourceInstance : children) {
					if (childResourceInstance.getGeneratePrimaryKey()) {
						childResourceInstance.setId(instanceSeq.next());
					}
					childResourceInstance.setParentId(resourceInstance.getId());
					convertToList(allResourceInstances, props, childResourceInstance, false);
				}
			}
		}
	}

	@SuppressWarnings("unchecked")
	private void loadAllResourceInstance() throws Exception {
		long startTime = 0;
		if (logger.isInfoEnabled()) {
			startTime = System.currentTimeMillis();
			logger.info("load getAllInstance start");
		}
		// ????????????????????????????????????????????????,??????????????????
		List<ResourceInstanceDO> allResourceInstanceDOs = resourceInstanceDAO.getAllInstance();
		if (logger.isInfoEnabled()) {
			long endTime = System.currentTimeMillis();
			long diff = endTime - startTime;
			logger.info("load AllInstance from db end time=" + diff / 1000.0 + " seconds");
		}
		if (CollectionUtils.isNotEmpty(allResourceInstanceDOs)) {
			// ?????????????????????????????????????????????
			List<String> discoverKeys = initLoadPropKey.getDiscoverKeys();
			List<String> moduleKeys = initLoadPropKey.getModuleKeys();
			long tempStart = System.currentTimeMillis();
			List<PropDO> allModuleAndDiscoverDOs = propDAO.getAllModuleAndDiscoverProp(moduleKeys, discoverKeys);
			if (logger.isInfoEnabled()) {
				long endTime = System.currentTimeMillis();
				long diff = endTime - tempStart;
				logger.info("load AllProp from db end time=" + diff / 1000.0 + " seconds");
			}
			tempStart = System.currentTimeMillis();
			Map<Long, List<? extends InstanceProp>>[] propsMap = PropertyConverter
					.convertProps(allModuleAndDiscoverDOs);
			Map<Long, List<? extends InstanceProp>> modulePropsMap = propsMap[0];
			Map<Long, List<? extends InstanceProp>> discoveryPropsMap = propsMap[1];
			// ????????????????????????
			if (logger.isInfoEnabled()) {
				long endTime = System.currentTimeMillis();
				long diff = endTime - tempStart;
				logger.info("prop convert end time=" + diff / 1000.0 + " seconds");
			}
			HashMap<Long, HashSet<Long>> relations = new HashMap<Long, HashSet<Long>>();
			List<ResourceInstance> allInstance = new ArrayList<ResourceInstance>(allResourceInstanceDOs.size());
			for (ResourceInstanceDO resourceInstanceDO : allResourceInstanceDOs) {
				ResourceInstance resourceInstance = null;
				Long id = resourceInstanceDO.getInstanceId();
				resourceInstance = convertToResourceInstance(resourceInstanceDO, null);
				allInstance.add(resourceInstance);
				// ???
				if (resourceInstance.getParentId() > 0) {
					// ?????????????????????????????????????????????????????????
					if (!relations.containsKey(resourceInstance.getParentId())) {
						// ????????????
						relations.put(resourceInstance.getParentId(), null);
					}
					HashSet<Long> children = relations.get(resourceInstance.getParentId());
					if (children == null) {
						children = new HashSet<Long>();
						relations.put(resourceInstance.getParentId(), children);
					}
					children.add(resourceInstance.getId());
				} else {
					// ?????????????????????????????????????????????????????????.. (????????????????????????????????????????????????)
					if (!relations.containsKey(resourceInstance.getId())) {
						relations.put(id, null);
					}
				}
			}
			cache.load(allInstance, relations);
			if (logger.isInfoEnabled()) {
				long endTime = System.currentTimeMillis();
				long diff = endTime - tempStart;
				logger.info("instance load to cache end time=" + diff / 1000.0 + " seconds");
			}
			tempStart = System.currentTimeMillis();
			if (modulePropsMap != null) {
				for (List<? extends InstanceProp> props : modulePropsMap.values()) {
					if (CollectionUtils.isNotEmpty(props)) {
						List<ModuleProp> moduleProps = (List<ModuleProp>) props;
						for (ModuleProp instanceProp : moduleProps) {
							propCache.add(instanceProp.getInstanceId(), PropTypeEnum.MODULE, instanceProp);
						}
					}
				}
			}
			if (logger.isInfoEnabled()) {
				long endTime = System.currentTimeMillis();
				long diff = endTime - tempStart;
				logger.info("moduleprop load to cache end time=" + diff / 1000.0 + " seconds");
			}
			tempStart = System.currentTimeMillis();
			if (discoveryPropsMap != null) {
				for (List<? extends InstanceProp> props : discoveryPropsMap.values()) {
					if (CollectionUtils.isNotEmpty(props)) {
						List<DiscoverProp> discoverProps = (List<DiscoverProp>) props;
						for (DiscoverProp instanceProp : discoverProps) {
							propCache.add(instanceProp.getInstanceId(), PropTypeEnum.DISCOVER, instanceProp);
						}
					}
				}
			}
			if (logger.isInfoEnabled()) {
				long endTime = System.currentTimeMillis();
				long diff = endTime - tempStart;
				logger.info("discoverprop load to cache end time=" + diff / 1000.0 + " seconds");
			}
			List<CustomModuleProp> customModuleProps = customModulePropService.getCustomModuleProp();
			if (CollectionUtils.isNotEmpty(customModuleProps)) {
				for (CustomModuleProp customModuleProp : customModuleProps) {
					customModulePropCache.add(customModuleProp.getInstanceId(), customModuleProp);
				}
			}
		}
		if (logger.isInfoEnabled()) {
			long endTime = System.currentTimeMillis();
			long diff = endTime - startTime;
			logger.info("load getAllInstance end time=" + diff / 1000.0 + " seconds");
		}
	}

	/**
	 * ???????????????????????????,?????????????????????????????????
	 *
	 * @param nodeGroupId
	 *            ?????????0--??????????????????
	 * @return ?????????????????????
	 * @throws Exception
	 */
	@SuppressWarnings("unchecked")
	public List<ResourceInstance> loadAllResourceInstance(long nodeGroupId) throws Exception {
		long startTime = 0;
		if (logger.isInfoEnabled()) {
			startTime = System.currentTimeMillis();
			logger.info("load from db start,nodeGroupId=" + nodeGroupId);
		}
		if (nodeGroupId <= 0) {
			return new ArrayList<ResourceInstance>();
		}
		List<ResourceInstance> result = null;
		List<ResourceInstanceDO> allResourceInstanceDOs = null;
		if (nodeGroupId > 0) {
			// ???????????????????????????????????????????????????,??????????????????
			allResourceInstanceDOs = resourceInstanceDAO.getAllInstanceByNode(String.valueOf(nodeGroupId));
			if (logger.isInfoEnabled()) {
				long endTime = System.currentTimeMillis();
				long diff = endTime - startTime;
				logger.info("load resource from db end,time=" + diff / 1000.0 + " seconds");
			}
		}
		Map<Long, ResourceInstance> allMapResourceInstances = null;
		if (CollectionUtils.isNotEmpty(allResourceInstanceDOs)) {
			allMapResourceInstances = new HashMap<>(allResourceInstanceDOs.size());
			result = new ArrayList<>(100);

			List<PropDO> allModuleAndDiscoverDOs = null;
			if (nodeGroupId > 0) {
				long tempstartTime = System.currentTimeMillis();
				// ???????????????DCS??????????????????????????????
				logger.info("load prop start");
				allModuleAndDiscoverDOs = propDAO.getAllModuleAndDiscoverProp(null, null);
				if (logger.isInfoEnabled()) {
					long endTime = System.currentTimeMillis();
					long diff = endTime - tempstartTime;
					logger.info("load prop from db end,time=" + diff / 1000.0 + " seconds");
				}
			}
			startTime = System.currentTimeMillis();
			Map<Long, List<? extends InstanceProp>>[] propsMap = PropertyConverter
					.convertProps(allModuleAndDiscoverDOs);
			Map<Long, List<? extends InstanceProp>> modulePropsMap = propsMap[0];
			Map<Long, List<? extends InstanceProp>> discoveryPropsMap = propsMap[1];

			// ????????????????????????
			for (ResourceInstanceDO resourceInstanceDO : allResourceInstanceDOs) {
				ResourceInstance resourceInstance = null;
				Long id = resourceInstanceDO.getInstanceId();
				if (resourceInstanceDO.getParentId() == null) {
					resourceInstance = convertToResourceInstance(resourceInstanceDO, null);
					if (allMapResourceInstances.containsKey(id)) {
						ResourceInstance lastOne = allMapResourceInstances.get(id);
						lastOne.setCategoryId(resourceInstance.getCategoryId());
						lastOne.setShowIP(resourceInstance.getShowIP());
						lastOne.setDiscoverNode(resourceInstance.getDiscoverNode());
						lastOne.setDiscoverWay(resourceInstance.getDiscoverWay());
						lastOne.setName(resourceInstance.getName());
						lastOne.setShowName(resourceInstance.getShowName());
						lastOne.setLifeState(resourceInstance.getLifeState());
						lastOne.setResourceId(resourceInstance.getResourceId());
						lastOne.setDomainId(resourceInstance.getDomainId());
						result.add(lastOne);

						resourceInstance = lastOne;
					} else {
						result.add(resourceInstance);
					}
				} else {
					ResourceInstance parentResourceInstance = allMapResourceInstances
							.get(resourceInstanceDO.getParentId());
					if (parentResourceInstance == null) {
						parentResourceInstance = new ResourceInstance();
						parentResourceInstance.setId(resourceInstanceDO.getParentId().longValue());
						allMapResourceInstances.put(resourceInstanceDO.getParentId(), parentResourceInstance);
						if (logger.isErrorEnabled()) {
							logger.error("loadAllResourceInstance resourceInstance.id=" + id + " parentId="
									+ resourceInstanceDO.getParentId());
						}
					}
					resourceInstance = convertToResourceInstance(resourceInstanceDO, parentResourceInstance);
					if (parentResourceInstance.getChildren() == null) {
						parentResourceInstance.setChildren(new ArrayList<ResourceInstance>(100));
					}
					parentResourceInstance.getChildren().add(resourceInstance);
				}
				allMapResourceInstances.put(id, resourceInstance);

				if (modulePropsMap.containsKey(id)) {
					resourceInstance.setModuleProps((List<ModuleProp>) modulePropsMap.get(id));
				}

				if (discoveryPropsMap.containsKey(id)) {
					resourceInstance.setDiscoverProps((List<DiscoverProp>) discoveryPropsMap.get(id));
				}
			}
		}
		if (logger.isInfoEnabled()) {
			long endTime = System.currentTimeMillis();
			long diff = endTime - startTime;
			logger.info("load from db end time=" + diff / 1000.0 + " second,nodeGroupId=" + nodeGroupId);
		}
		return result;
	}

	/**
	 * ???????????????
	 */
	private ComparerResult parentInstanceValidate(ResourceInstance validateInstance) throws InstancelibException {
		ComparerResult comparerResult = resourceComparer.isSameDevice(validateInstance);
		if(comparerResult != null){
			List<Long> repeatIds = new ArrayList<>();
			for (long tempInstanceId : comparerResult.getRepeatIds()) {
				ResourceInstance tempInstance = getResourceInstanceById(tempInstanceId);
				if(tempInstance == null){
					continue;
				}
				comparerResult.setInstanceId(tempInstanceId);
				comparerResult.setLifeState(tempInstance.getLifeState());
				if (InstanceLifeStateEnum.NOT_MONITORED == tempInstance.getLifeState()
						|| InstanceLifeStateEnum.MONITORED == tempInstance.getLifeState()) {
					repeatIds.add(tempInstance.getId());
				}
			}
			comparerResult.setRepeatIds(repeatIds);
		}
		if(comparerResult == null){
			comparerResult = new ComparerResult();
		}
		return comparerResult;
	}

	@Override
	public List<Long> getAllChildrenInstanceIdbyParentId(Set<Long> parentIds) {
		List<Long> result = null;
		if (!CollectionUtils.isEmpty(parentIds)) {
			result = new ArrayList<Long>();
			List<Long> queryParentIds = new ArrayList<Long>(parentIds.size());
			for (long parentId : parentIds) {
				Set<Long> ids = cache.getChildInstance(parentId);
				if (CollectionUtils.isEmpty(ids)) {
					queryParentIds.add(parentId);
				} else {
					result.addAll(ids);
				}
			}
			if (CollectionUtils.isNotEmpty(queryParentIds)) {
				// ???????????????
				List<ResourceInstanceDO> resultDOs = resourceInstanceDAO
						.getAllChildrenInstanceIdbyParentIds(new ArrayList<Long>(parentIds));
				if (CollectionUtils.isNotEmpty(resultDOs)) {
					for (ResourceInstanceDO resourceInstanceDO : resultDOs) {
						if (InstanceLifeStateEnum.MONITORED.toString().equals(resourceInstanceDO.getLifeState())
								|| InstanceLifeStateEnum.NOT_MONITORED.toString()
										.equals(resourceInstanceDO.getLifeState())) {
							result.add(resourceInstanceDO.getInstanceId());
						}
					}
				}
			}
		}
		return result;
	}

	@Override
	public List<Long> getAllChildrenInstanceIdbyParentId(Set<Long> parentIds, InstanceLifeStateEnum state) {
		List<Long> childInstanceIds = null;
		if (null != parentIds && parentIds.size() > 0) {
			if (state.equals(InstanceLifeStateEnum.MONITORED)) {
				childInstanceIds = resourceInstanceDAO
						.getAllMonirotedChildrenInstanceIdByParentIds(new ArrayList<Long>(parentIds));
			} else if (state.equals(InstanceLifeStateEnum.NOT_MONITORED)) {
				childInstanceIds = resourceInstanceDAO
						.getAllNotMonirotedChildrenInstanceIdByParentIds(new ArrayList<Long>(parentIds));
			}
		}
		return childInstanceIds;
	}

	@Override
	public List<ResourceInstance> getAllResourceInstancesForLink() {
		List<ResourceInstanceDO> resourceInstanceDOs = resourceInstanceDAO.getAllResourceInstanceForLink();
		List<ResourceInstance> result = new ArrayList<ResourceInstance>();
		if (resourceInstanceDOs != null && resourceInstanceDOs.size() > 0) {
			for (ResourceInstanceDO resourceInstanceDO : resourceInstanceDOs) {
				if (resourceInstanceDO != null) {
					if (resourceInstanceDO.getLifeState().equals(InstanceLifeStateEnum.MONITORED.toString())
							|| resourceInstanceDO.getLifeState()
									.equals(InstanceLifeStateEnum.NOT_MONITORED.toString())) {
						ResourceInstance resourceInstance = convertToResourceInstance(resourceInstanceDO, null);
						result.add(resourceInstance);
					}
				}

			}
		}
		return result;
	}

	private CategoryDef getParentCategoryDef(String categoryId) {
		CategoryDef categoryDef = capacityService.getCategoryById(categoryId);
		if (categoryDef == null && logger.isInfoEnabled()) {
			StringBuilder b = new StringBuilder(50);
			b.append("categoryId: ").append(categoryId).append(" query CategoryDef is null");
			logger.info(b);
		}
		if (categoryDef != null && categoryDef.getParentCategory() != null
				&& (!capacityService.getRootCategory().getId().equals(categoryDef.getParentCategory().getId()))) {
			categoryDef = getParentCategoryDef(categoryDef.getParentCategory().getId());
		}
		return categoryDef;
	}

	/**
	 * ??????????????????????????????????????????
	 *
	 * @param instancelibEvent
	 *            ??????
	 * @throws Exception
	 */
	protected void listenerNotification(final InstancelibEvent instancelibEvent) {
		// ??????????????????
		try {
			instancelibEventManager.doListen(instancelibEvent);
		} catch (Exception e) {
			// if (logger.isErrorEnabled()) {
			// logger.error("",e);
			// }
		}
	}

	public HashSet<String> getAllSoftwareCategoryId() {
		LicenseUtil licenseUtil = LicenseUtil.getLicenseUtil();
		HashSet<String> usedIds = licenseUtil.getUsedCategoryIds();
		HashSet<String> result = new HashSet<String>();
		List<CategoryDef> categoryDefs = capacityService.getCategoryList(1);
		for (CategoryDef categoryDef : categoryDefs) {
			if (!usedIds.contains(categoryDef.getId()) && !categoryDef.getId().equals("Links")
					&& !categoryDef.getId().equals(CapacityConst.VM)) {
				result.add(categoryDef.getId());
			}
		}
		return result;
	}

	/**
	 * ??????License
	 *
	 * @param resourceInstance
	 * @param isCalcLicense
	 * @throws InstancelibException
	 */
	@Override
	public boolean checkLicense(ResourceInstance resourceInstance) throws InstancelibException {
		boolean isCalcLicense = false;
		if (!resourceInstance.getGeneratePrimaryKey()) {
			// ??????????????????????????????license
			return isCalcLicense;
		}
		LicenseUtil licenseUtil = LicenseUtil.getLicenseUtil();
		// ????????????????????????License
		if (resourceInstance.getParentId() <= 0
				&& resourceInstance.getLifeState() != InstanceLifeStateEnum.INITIALIZE) {
			String parentCategoryId = getParentCategoryDef(resourceInstance.getCategoryId()).getId();
			LicenseModelEnum licenseModel = licenseCategoryRelation.categoryId2LicenseEnum(parentCategoryId);
			if (logger.isInfoEnabled()) {
				logger.info("begin calculation " + licenseModel + " License number; categoryId:" + parentCategoryId);
			}
			HashSet<String> licenseCategoryIds = null;
			if (licenseModel != null) {
				isCalcLicense = true;
				if (logger.isInfoEnabled()) {
					logger.info(
							"???????????????" + resourceInstance.getCategoryId() + "->" + parentCategoryId + "???????????????License?????????");
				}
				if (licenseModel.equals(LicenseModelEnum.stmMonitorSh)) {//stmMonitorRd
					licenseCategoryIds = getAllSoftwareCategoryId();
				} else {
					licenseCategoryIds = licenseUtil.getCategorysByLicenseKey(licenseModel);
				}
			} else {
				if (logger.isInfoEnabled()) {
					logger.info(
							"???????????????" + resourceInstance.getCategoryId() + "->" + parentCategoryId + "?????????????????? License?????????");
				}
				isCalcLicense = false;
			}

			if (isCalcLicense) {
				List<ResourceInstance> resourceInstances = null;
				if (licenseCategoryIds != null && licenseCategoryIds.size() > 0) {
					resourceInstances = getParentInstanceByCategoryIdsFordb(licenseCategoryIds);
				}

				try {
					/**
					 * ??????License?????????????????????????????????
					 */
					License license = License.checkLicense();
					int avaNumber = license.checkModelAvailableNum(licenseModel);
					if (logger.isInfoEnabled()) {
						logger.info(" License model:" + licenseModel + " total number:" + avaNumber + " categoryId:"
								+ parentCategoryId);
					}
					int resourceInstanceSize = resourceInstances == null ? 0 : resourceInstances.size();
					int currentSize = licenseUtil.getLicenseStorageNum();
					int stockNumber = resourceInstanceSize + currentSize;
					if (logger.isInfoEnabled()) {
						logger.info(" db resourceInstanceSize:" + resourceInstanceSize + " remaining size:" + avaNumber
								+ " categoryId:" + parentCategoryId);
					}
					if (stockNumber > avaNumber) {
						// long end = System.currentTimeMillis();
						if (logger.isInfoEnabled()) {
							StringBuilder b = new StringBuilder();
							b.append("addResourceInstance end name=");
							b.append(resourceInstance.getName());
							b.append(" showIP:");
							b.append(resourceInstance.getShowIP());
							b.append("\tLicense?????????????????????License???????????????" + avaNumber + ";????????????????????????" + stockNumber + ";");
							logger.info(b);
						}

						// ???????????????????????????????????????????????????????????????????????????????????????????????????????????????????????????License???????????????
						ResourceInstance isInstance = loadResourceInstance(resourceInstance.getId());
						if (isInstance != null && isInstance.getLifeState() != InstanceLifeStateEnum.MONITORED
								&& isInstance.getLifeState() != InstanceLifeStateEnum.NOT_MONITORED) {
							throw new InstancelibException(ServerErrorCodeConstant.ERR_SERVER_LICENSE_ERROR,
									"License ?????????????????????");
						} else {
							// ????????????????????????????????????????????????????????????License??????
							isCalcLicense = false;
						}
					}
				} catch (LicenseCheckException e) {
					if (logger.isErrorEnabled()) {
						logger.error("check license error:", e);
					}
					if (logger.isInfoEnabled()) {
						StringBuilder b = new StringBuilder();
						b.append("addResourceInstance end name=");
						b.append(resourceInstance.getName());
						b.append(" showIP:");
						b.append(resourceInstance.getShowIP());
						logger.info(b);
					}
					throw new InstancelibException(ServerErrorCodeConstant.ERR_SERVER_LICENSE_ERROR, e.getMessage());
				}
				// ???????????????????????????????????????????????????????????????????????????license
				if (isCalcLicense) {
					// ??????License??????
					licenseUtil.addLicenseStorageNum();
				}
			}
		}
		return isCalcLicense;
	}

	/**
	 * ????????????????????????????????????
	 *
	 * @param instancelibEvent
	 *            ??????
	 * @throws Exception
	 */
	protected void interceptorNotification(final InstancelibEvent instancelibEvent) throws Exception {
		// ????????????
		instancelibEventManager.doInterceptor(instancelibEvent);
	}

	public void setResourceInstanceDAO(ResourceInstanceDAO resourceInstanceDAO) {
		this.resourceInstanceDAO = resourceInstanceDAO;
	}

	public void setInstanceSeq(ISequence instanceSeq) {
		this.instanceSeq = instanceSeq;
	}

	public ResourceInstanceDAO getResourceInstanceDAO() {
		return resourceInstanceDAO;
	}

	public ISequence getInstanceSeq() {
		return instanceSeq;
	}

	public void setInstancelibEventManager(InstancelibEventManager instancelibEventManager) {
		this.instancelibEventManager = instancelibEventManager;
	}

	public void setPropDAO(PropDAO propDAO) {
		this.propDAO = propDAO;
	}

	public void setPropTypeDAO(PropTypeDAO propTypeDAO) {
		this.propTypeDAO = propTypeDAO;
	}

	public void setCustomPropExtendService(CustomPropExtendService customPropExtendService) {
		this.customPropExtendService = customPropExtendService;
	}

	public void setModulePropExtendService(ModulePropExtendService modulePropExtendService) {
		this.modulePropExtendService = modulePropExtendService;
	}

	public DiscoverPropService getDiscoverPropService() {
		return discoverPropService;
	}

	public void setDiscoverPropService(DiscoverPropService discoverPropService) {
		this.discoverPropService = discoverPropService;
	}

	public ExtendPropService getExtendPropService() {
		return extendPropService;
	}

	public void setExtendPropService(ExtendPropService extendPropService) {
		this.extendPropService = extendPropService;
	}

	public void setDiscoverPropExtendService(DiscoverPropExtendService discoverPropExtendService) {
		this.discoverPropExtendService = discoverPropExtendService;
	}

	public void setExtendPropExtendService(ExatendPropExtendService extendPropExtendService) {
		this.extendPropExtendService = extendPropExtendService;
	}

	public void setInitLoadPropKey(InitLoadPropKeyUtil initLoadPropKey) {
		this.initLoadPropKey = initLoadPropKey;
	}

	public void setCache(ResourceInstanceCache cache) {
		this.cache = cache;
	}

	public void setResourceComparer(ResourceComparer resourceComparer) {
		this.resourceComparer = resourceComparer;
	}

	public ModulePropService getModulePropService() {
		return modulePropService;
	}

	public void setModulePropService(ModulePropService modulePropService) {
		this.modulePropService = modulePropService;
	}

	public void setCapacityService(CapacityService capacityService) {
		this.capacityService = capacityService;
	}

	public CustomPropService getCustomPropService() {
		return customPropService;
	}

	public void setCustomPropService(CustomPropService customPropService) {
		this.customPropService = customPropService;
	}

	public void setPropCache(PropCache propCache) {
		this.propCache = propCache;
	}

	public void setLicenseCategoryRelation(ILicenseCategoryRelation licenseCategoryRelation) {
		this.licenseCategoryRelation = licenseCategoryRelation;
	}

	public void setCustomModulePropCache(CustomModulePropCache customModulePropCache) {
		this.customModulePropCache = customModulePropCache;
	}

	public void setCustomModulePropService(CustomModulePropService customModulePropService) {
		this.customModulePropService = customModulePropService;
	}

	@Override
	public void onApplicationEvent(ContextRefreshedEvent contextRefreshedEvent) {
		if (null == contextRefreshedEvent.getApplicationContext().getParent()) {
			init();
		}
	}

}
