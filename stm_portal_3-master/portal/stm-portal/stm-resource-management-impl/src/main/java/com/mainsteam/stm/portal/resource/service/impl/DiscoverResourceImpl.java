package com.mainsteam.stm.portal.resource.service.impl;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.net.URLDecoder;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;
import java.util.TimeZone;
import java.util.regex.Pattern;

import javax.annotation.Resource;

import org.apache.log4j.Logger;
import org.apache.poi.hssf.usermodel.HSSFCellStyle;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.apache.poi.hssf.util.HSSFColor;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.CellStyle;
import org.apache.poi.ss.usermodel.Font;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.springframework.web.multipart.MultipartFile;

import com.alibaba.fastjson.JSON;
import com.mainsteam.stm.caplib.CapacityService;
import com.mainsteam.stm.caplib.common.CategoryDef;
import com.mainsteam.stm.caplib.dict.CapacityConst;
import com.mainsteam.stm.caplib.dict.DiscoverWayEnum;
import com.mainsteam.stm.caplib.dict.MetricIdConsts;
import com.mainsteam.stm.caplib.dict.ResourceTypeConsts;
import com.mainsteam.stm.caplib.plugin.PluginInitParameter;
import com.mainsteam.stm.caplib.plugin.SupportValue;
import com.mainsteam.stm.caplib.resource.ResourceDef;
import com.mainsteam.stm.common.instance.ResourceInstanceDiscoveryService;
import com.mainsteam.stm.common.instance.obj.ResourceInstanceDiscoveryParameter;
import com.mainsteam.stm.common.metric.MetricDataService;
import com.mainsteam.stm.discovery.obj.DiscoverResourceIntanceResult;
import com.mainsteam.stm.executor.exception.MetricExecutorException;
import com.mainsteam.stm.instancelib.DiscoverPropService;
import com.mainsteam.stm.instancelib.ResourceInstanceService;
import com.mainsteam.stm.instancelib.exception.InstancelibException;
import com.mainsteam.stm.instancelib.obj.DiscoverProp;
import com.mainsteam.stm.instancelib.obj.ResourceInstance;
import com.mainsteam.stm.instancelib.obj.ResourceInstanceResult;
import com.mainsteam.stm.instancelib.objenum.InstanceLifeStateEnum;
import com.mainsteam.stm.license.calc.api.ILicenseCalcService;
import com.mainsteam.stm.platform.dict.api.IDictApi;
import com.mainsteam.stm.platform.dict.bo.Dict;
import com.mainsteam.stm.platform.sequence.service.ISequence;
import com.mainsteam.stm.platform.web.vo.ILoginUser;
import com.mainsteam.stm.portal.resource.api.IAccountApi;
import com.mainsteam.stm.portal.resource.api.ICustomResourceGroupApi;
import com.mainsteam.stm.portal.resource.api.IDiscoverResourceApi;
import com.mainsteam.stm.portal.resource.api.IReAccountInstanceApi;
import com.mainsteam.stm.portal.resource.bo.AccountBo;
import com.mainsteam.stm.portal.resource.bo.BatdisckvBo;
import com.mainsteam.stm.portal.resource.bo.ReAccountInstanceBo;
import com.mainsteam.stm.portal.resource.dao.IBatDiscKvDao;
import com.mainsteam.stm.portal.resource.dao.IReAccountInstanceDao;
import com.mainsteam.stm.profilelib.ProfileService;
import com.mainsteam.stm.profilelib.exception.ProfilelibException;
import com.mainsteam.stm.profilelib.obj.ProfileInfo;
import com.mainsteam.stm.profilelib.objenum.ProfileTypeEnum;
import com.mainsteam.stm.simple.search.api.ISearchApi;
import com.mainsteam.stm.simple.search.bo.ResourceBizRel;
import com.mainsteam.stm.system.um.domain.api.IDomainReferencerRelationshipApi;
import com.mainsteam.stm.util.FileUtil;
import com.mainsteam.stm.util.ResourceOrMetricConst;
import com.mainsteam.stm.util.SecureUtil;

public class DiscoverResourceImpl implements IDiscoverResourceApi, IDomainReferencerRelationshipApi {
	private Logger logger = Logger.getLogger(DiscoverResourceImpl.class);
	@Resource(name="ocProtalResourceBatdisckvSeq")
	private ISequence sequence;
	@Resource
	private ResourceInstanceDiscoveryService discoveryService;
	@Resource
	private IAccountApi accountApi;
	@Resource(name="protalResourceReAccountInstanceDao")
	private IReAccountInstanceDao reAccountInstanceDao;
	@Resource
	private CapacityService capacityService;
	@Resource
	private IReAccountInstanceApi reAccountInstanceApi;
	@Resource(name="protalResourceBatDiscKvDao")
	private IBatDiscKvDao batDiscKvDao;
	@Resource
	private ProfileService profileService;
	@Resource
	private ResourceInstanceService resourceInstanceService;
	@Resource
	private ISearchApi searchApi;
	@Resource
	private ICustomResourceGroupApi customResourceGroupApi;
	@Resource
	private DiscoverPropService discoverPropService;
	@Resource(name="dictApi")
	private IDictApi dictApi;
	@Resource
	private MetricDataService metricDataService;
	@Resource
	private ILicenseCalcService licenseCalcService;
	
	private final static String SEARCH_NAV = "????????????-????????????";
	private final static String BATDISCKV_TYPE_TITLE = "title";
	private final static String BATDISCKV_TYPE_CONTENT = "content";
	private final static String RESOURCETYPE_ID = "resourceId";
	private final static String RESOURCETYPE_NAME = "????????????";
	private final static String COLLECTTYPE_ID = "collectType";
	private final static String COLLECTTYPE_NAME = "????????????";
	private final static String RESOURCERENAME_ID = "resourceName";
	private final static String RESOURCERENAME_NAME = "????????????";
	private final static String NETWORK_SHEET_NAME = "????????????";
	private final static String DATABASE_SHEET_NAME = "?????????";
	private final static String HOST_SNMP_SHEET_NAME = "??????(SNMP)";
	private final static String HOST_EXCLUDE_SNMP_SHEET_NAME = "??????(Telnet,SSH,WMI)";
	private final static String STAND_SHEET_NAME = "????????????";
	private final static String BATCH_DISCOVER_RESULT_FILE = "result.xls";
	private final static String RES_DIS_REPEAT_INSTANCE = "resDisRepeatInstance";
	private final static String RES_DIS_REPEAT_INSTANCE_PARAMTER = "resDisRepeatInstanceParamter";
	private List<BatdisckvBo> batdisckvBoList;
	private Map<String, String> batdiscResourceDefKv = new HashMap<String, String>();
	
	@Override
	public Map<String, Object> discoverResource(Map paramter, ILoginUser user) {
		// ????????????????????????
		ResourceInstanceDiscoveryParameter discoverParameter = setDiscoverParamter(paramter);
		// ???????????????????????????
		Map<String, Object> result = new HashMap<String, Object>();
		long startTime = System.currentTimeMillis();
		ResourceDef resourceDef = capacityService.getResourceDefById((String)paramter.get("resourceId"));
		String categoryId = resourceDef.getCategory().getParentCategory().getId();
		try {
			if(licenseCalcService.isLicenseEnough(categoryId)){
				logger.info("start discover single resource:" + categoryId);
				// malachi ????????????????????????
				DiscoverResourceIntanceResult drir = discoveryService.discoveryResourceInstance(discoverParameter);
				ResourceInstance ri = drir.getResourceIntance();
				// ???????????????
				if(drir.isSuccess()){
					// ??????????????????????????????
					String newShowName = null;
					// ?????????????????????
					//if(DiscoverResourceIntanceResult.RESOURCEINSTANCE_REPEAT == drir.getCode()){
					if(drir.getRepeatIds() != null && drir.getRepeatIds().size() > 0){
						newShowName = reCreateShowName(createShowNameMap4RepeatInstance(ri), 1, ri.getShowName());
						ri.setShowName(newShowName);
						cacheRepeatResIns(ri, user);
						cacheRepeatResInsDiscoverParamter(paramter, user);
						result.put("repeatPrompt", true);
						// ????????????
						List<Map<String, String>> repeatResList = new ArrayList<Map<String, String>>();
						if(drir.getRepeatIds() != null){
							List<ResourceInstance> repeatRiList = resourceInstanceService.getResourceInstances(drir.getRepeatIds());
							for(int i = 0; repeatRiList != null && i < repeatRiList.size(); i++){
								ResourceInstance repeatRi = repeatRiList.get(i);
								Map<String, String> repeatResMap = new HashMap<String, String>();
								repeatResMap.put("id", String.valueOf(repeatRi.getId()));
								repeatResMap.put("name", repeatRi.getShowName());
								repeatResMap.put("ip", repeatRi.getShowIP());
								repeatResList.add(repeatResMap);
							}
						}
						result.put("repeatRes", repeatResList);
					}else{
						newShowName = reCreateShowName(createShowNameMap(ri), 1, ri.getShowName());
						resourceInstanceService.updateResourceInstanceName(ri.getId(), newShowName);
						ri.setShowName(newShowName);
						insertReAccountInstance(paramter, ri);
						result.put("repeatPrompt", false);
					}
					// ?????????????????????
					result.putAll(setResult(ri, paramter));
					result.put("status", "1");
				}else{
					result.put("failCode", drir.getCode());
					result.put("failMsg", drir.getErrorMsg());
					result.put("status", "0");
					result.put("categoryId", categoryId);
				}
				logger.info("end discover single resource:" + categoryId);
			}else{
				result.put("failCode", 100);
				result.put("status", "0");
			}
		} catch (Exception e) {
			result.put("failMsg", e.getMessage());
			result.put("status", "0");
			result.put("categoryId", categoryId);
			logger.error("portal discover No. 140", e);
		}
		// ??????????????????
		result.put("time", calcTime(System.currentTimeMillis() - startTime));
		return result;
	}

	/**
	 * ??????????????????????????????????????????
	 * 
	 * @param jsonData
	 * @return
	 */
	private ResourceInstanceDiscoveryParameter setDiscoverParamter(Map paramter) {
		//???map?????????password?????????????????????
		for (Object key : paramter.keySet()) {
			if (SecureUtil.isPassswordKey((String)key)) {
				paramter.put(key, SecureUtil.pwdEncrypt((String)paramter.get(key)));
			}
			//??????????????????
			if(((String)key).equals("KGValue")){
				paramter.put(key, SecureUtil.pwdEncrypt((String)paramter.get(key)));
			}
		}
		ResourceInstanceDiscoveryParameter disParamter = new ResourceInstanceDiscoveryParameter();
		if (paramter.containsKey("collectType")) {
			disParamter.setDiscoveryWay((String) paramter.get("collectType"));
		}
		String resourceId = (String) paramter.get("resourceId");
		disParamter.setResourceId(resourceId);
		String parentId = capacityService.getResourceDefById(resourceId).getCategory().getParentCategory().getId();
		if(CapacityConst.NETWORK_DEVICE.equals(parentId) || CapacityConst.SNMPOTHERS.equals(parentId)){
			disParamter.setAnonymousNetworkDevice(true);
		}
		if(paramter.containsKey("nodeGroupId")
				&& null != paramter.get("nodeGroupId") && !"".equals(paramter.get("nodeGroupId"))){
			disParamter.setNodeGroupId(Integer.valueOf((String) paramter.get("nodeGroupId")));
		}
		if(paramter.get("domainId") != null){
			disParamter.setDomainId(Long.valueOf((String)paramter.get("domainId")));
		}
		Map<String, String> discoveryInfos = new HashMap<String, String>();
		discoveryInfos.putAll(paramter);
		disParamter.setDiscoveryInfos(discoveryInfos);
		return disParamter;
	}

	private void insertReAccountInstance(Map paramter, ResourceInstance instance) {
		// ??????????????????????????????
		if (null != paramter.get("account_id") && !"".equals(paramter.get("account_id"))) {
			AccountBo accountBo = new AccountBo();
			accountBo.setAccount_id(Long.valueOf((String) paramter.get("account_id")));
			accountBo.setUsername((String) paramter.get("username"));
			accountBo.setPassword((String) paramter.get("password"));
			// ????????????????????????????????????????????????
			AccountBo accountBoFromDB = accountApi.get(accountBo.getAccount_id());
			if (null != accountBoFromDB && accountBoFromDB.getUsername().equals(accountBo.getUsername())
					&& accountBoFromDB.getPassword().equals(accountBo.getPassword())) {
				ReAccountInstanceBo reAccountInstanceBo = new ReAccountInstanceBo();
				reAccountInstanceBo.setAccount_id(accountBo.getAccount_id());
				reAccountInstanceBo.setInstanceid(instance.getId());
				reAccountInstanceBo.setInstancename(instance.getName());
				reAccountInstanceBo.setInstanceip(instance.getShowIP());
				reAccountInstanceBo.setInstancetype(capacityService.getCategoryById(instance.getCategoryId()).getName());
				reAccountInstanceBo.setStatus("1");
				reAccountInstanceBo.setEntry_datetime(new Date());
				reAccountInstanceApi.insert(reAccountInstanceBo);
			}
		}
	}

	/**
	 * ????????????????????????????????????
	 * 
	 * @param instance
	 * @return
	 */
	private Map<String, Object> setResult(ResourceInstance instance, Map discoverParameter) {
		Map<String, Object> result = new HashMap<String, Object>();
		// ?????????????????????ID
		long instanceId = instance.getId();
		result.put("instanceType", capacityService.getCategoryById(instance.getCategoryId()).getName());
		result.put("instanceId", instanceId);
		result.put("instanceName", instance.getName());
		result.put("instanceShowName", instance.getShowName());
		
		List<Map<String, String>> ipList = getIpList(instance);
		result.put("instanceIP", ipList);
		// ???????????????????????????				
		result.put("childInstance", getChildInstanceInParent(instance));
		return result;
	}
	/**
	 * ???????????????????????????
	 * @param parentInstance
	 * @return
	 */
	private Map<String, List<Map<String, String>>> getChildInstanceInParent(ResourceInstance parentInstance){
		List<ResourceInstance> childInstanceList = null;
		try {
			childInstanceList = resourceInstanceService.getChildInstanceByParentId(parentInstance.getId(), true);
		} catch (InstancelibException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}//parentInstance.getChildren();
		/**
		 * 1.??????????????????????????? 2.??????????????????????????????
		 */
		Set<String> groups = new HashSet<String>();
		Map<String, List<Map<String, String>>> content = new HashMap<String, List<Map<String, String>>>();
		for (int i = 0; null != childInstanceList
				&& i < childInstanceList.size(); i++) {
			groups.add(childInstanceList.get(i).getResourceId());
		}
		for (String key : groups) {
			List<Map<String, String>> keyList = new ArrayList<Map<String, String>>();
			for (int i = 0; i < childInstanceList.size(); i++) {
				ResourceInstance childInstance = childInstanceList.get(i);
				if (key.equals(childInstance.getResourceId())) {
					Map<String, String> map = new HashMap<String, String>();
					map.put("name", childInstance.getName());
					map.put("lifeState", childInstance.getLifeState().name());
					map.put("childInstanceId", String.valueOf(childInstance.getId()));
					// ?????????????????? 1???????????? 0???????????????
					String availability = "1";
					// ???????????? ??????
					if(ResourceTypeConsts.TYPE_NETINTERFACE.equals(childInstance.getChildType())){
						if(CapacityConst.NETWORK_DEVICE.equals(parentInstance.getParentCategoryId())
								|| CapacityConst.STORAGE.equals(parentInstance.getParentCategoryId())
								|| CapacityConst.SNMPOTHERS.equals(parentInstance.getParentCategoryId())
								  || CapacityConst.HOST.equals(parentInstance.getParentCategoryId())){
							String[] moduleProp = childInstance.getModulePropBykey("availability");
							availability = moduleProp != null && moduleProp.length > 0 ? moduleProp[0] : "0";
						}
					}
					// WMI?????? ??????
					if(ResourceTypeConsts.TYPE_SERVICE.equals(childInstance.getChildType())
							&& ResourceOrMetricConst.WMI_HOST_SERVICE_RESOURCEID.equals(childInstance.getResourceId())){
						String[] moduleProp = childInstance.getModulePropBykey("availability");
						availability = moduleProp != null && moduleProp.length > 0 ? moduleProp[0] : "0";
					}
					map.put("availability", availability);
					keyList.add(map);
				}
			}
			content.put(capacityService.getResourceDefById(key).getName(), keyList);
		}
		return content;
	}
	private Map<String, List<Map<String, String>>> getChildInstanceInParentStauts(ResourceInstance parentInstance,Map<InstanceLifeStateEnum, List<Long>> refreshmap){
	//	List<ResourceInstance> childInstanceList = parentInstance.getChildren();
		List<ResourceInstance> childInstanceList = null;
		try {
			childInstanceList = resourceInstanceService.getChildInstanceByParentId(parentInstance.getId(), true);
		} catch (InstancelibException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		/**
		 * 1.??????????????????????????? 2.??????????????????????????????
		 */
		Set<String> groups = new HashSet<String>();
		List<Long> newids= new ArrayList<Long>();//??????????????????
		List<Long> delids= new ArrayList<Long>();
		List<Long> removeids= new ArrayList<Long>();//????????????
		Map<String, List<Map<String, String>>> content = new HashMap<String, List<Map<String, String>>>();
		for (int i = 0; null != childInstanceList
				&& i < childInstanceList.size(); i++) {
			groups.add(childInstanceList.get(i).getResourceId());
		}
		 for (InstanceLifeStateEnum key : refreshmap.keySet()) {
			 if(key.name().equals(InstanceLifeStateEnum.NEWER.name())){//??????
				 newids=refreshmap.get(key);
			 }else if(key.name().equals(InstanceLifeStateEnum.DELETED.name())){//??????
				 delids=refreshmap.get(key);
			 }else if(key.name().equals(InstanceLifeStateEnum.REMOVED.name())){//??????
				 removeids=refreshmap.get(key);
			 }
			
			  }	
		 logger.error("removeids"+ removeids);
		 logger.error("newids"+ newids);
		 logger.error("delids"+ newids);
		 
		for (String key : groups) {
			List<Map<String, String>> keyList = new ArrayList<Map<String, String>>();
			for (int i = 0; i < childInstanceList.size(); i++) {
				ResourceInstance childInstance = childInstanceList.get(i);
				if (key.equals(childInstance.getResourceId())) {
					Map<String, String> map = new HashMap<String, String>();
					map.put("name", childInstance.getName());
					map.put("lifeState", childInstance.getLifeState().name());
					map.put("childInstanceId", String.valueOf(childInstance.getId()));
					map.put("State", "");
					if(newids!=null){
						for (int j=0;j<newids.size();j++) {
							if(newids.get(j).equals(childInstance.getId())){
								map.put("State", "add");
							}
						}
					}
//					if(delids!=null){
//						for (int j=0;j<delids.size();j++) {
//							if(delids.get(j).equals(childInstance.getId())){
//								map.put("State", "deleted");
//							}
//						}
//					}
					if(removeids!=null){
						for (int j=0;j<removeids.size();j++) {
							if(removeids.get(j).equals(childInstance.getId())){
								map.put("State", "removed");
							}
						}
					}
				
					// ?????????????????? 1???????????? 0???????????????
					String availability = "1";
					// ???????????? ??????
					if(ResourceTypeConsts.TYPE_NETINTERFACE.equals(childInstance.getChildType())){
						if(CapacityConst.NETWORK_DEVICE.equals(parentInstance.getParentCategoryId())
								|| CapacityConst.STORAGE.equals(parentInstance.getParentCategoryId())
								|| CapacityConst.SNMPOTHERS.equals(parentInstance.getParentCategoryId())
								|| CapacityConst.HOST.equals(parentInstance.getParentCategoryId())){
							String[] moduleProp = childInstance.getModulePropBykey("availability");
							availability = moduleProp != null && moduleProp.length > 0 ? moduleProp[0] : "0";
						}
					}
					// WMI?????? ??????
					if(ResourceTypeConsts.TYPE_SERVICE.equals(childInstance.getChildType())
							&& ResourceOrMetricConst.WMI_HOST_SERVICE_RESOURCEID.equals(childInstance.getResourceId())){
						String[] moduleProp = childInstance.getModulePropBykey("availability");
						availability = moduleProp != null && moduleProp.length > 0 ? moduleProp[0] : "0";
					}
					map.put("availability", availability);
					keyList.add(map);
				}
			}
			
			content.put(capacityService.getResourceDefById(key).getName(), keyList);
		}
		return content;
	}
	
	/**
	 * ???Long??????????????????????????????
	 * 
	 * @param time
	 * @return
	 */
	private String calcTime(long time) {
		SimpleDateFormat sdf = new SimpleDateFormat("HH:mm:ss");
		String hms = sdf.format(time - TimeZone.getDefault().getRawOffset());
		return hms;
	}
	
	/**
	 * ???????????????????????????????????????
	 * @param instance
	 */
	private void cacheRepeatResIns(ResourceInstance instance, ILoginUser user){
		user.getCache().put(RES_DIS_REPEAT_INSTANCE + user.getId(), instance);
//		IMemcache<ResourceInstance> iMem = MemCacheFactory.getRemoteMemCache(ResourceInstance.class);
//		iMem.set(RES_DIS_REPEAT_INSTANCE + user.getId(), instance, 100000);
	}

	/**
	 * ?????????????????????????????????????????????
	 * @param instance
	 */
	private void cacheRepeatResInsDiscoverParamter(Map paramter, ILoginUser user){
		user.getCache().put(RES_DIS_REPEAT_INSTANCE_PARAMTER + user.getId(), paramter);
//		IMemcache<Map> iMem = MemCacheFactory.getRemoteMemCache(Map.class);
//		iMem.set(RES_DIS_REPEAT_INSTANCE_PARAMTER + user.getId(), paramter, 100000);
	}
	
	/**
	 * ????????????????????????????????????
	 * @return
	 */
	private ResourceInstance getRepeatResInsCache(ILoginUser user){
//		IMemcache<ResourceInstance> iMem = MemCacheFactory.getRemoteMemCache(ResourceInstance.class);
//		user.getCache().get(RES_DIS_REPEAT_INSTANCE + user.getId());
		return (ResourceInstance)user.getCache().get(RES_DIS_REPEAT_INSTANCE + user.getId());
	}
	/**
	 * ??????????????????????????????????????????
	 * @return
	 */
	private Map getRepeatResInsDiscoverParamterCache(ILoginUser user){
//		IMemcache<Map> iMem = MemCacheFactory.getRemoteMemCache(Map.class);
//		Map paramter = iMem.get(RES_DIS_REPEAT_INSTANCE_PARAMTER + user.getId());
		return (Map)user.getCache().get(RES_DIS_REPEAT_INSTANCE_PARAMTER + user.getId());
	}
	
	/**
	 * ??????????????????
	 */
	private void deleteRepeatResInsCache(ILoginUser user){
//		IMemcache<ResourceInstance> iMem = MemCacheFactory.getRemoteMemCache(ResourceInstance.class);
//		iMem.delete(RES_DIS_REPEAT_INSTANCE + user.getId());
		user.getCache().remove(RES_DIS_REPEAT_INSTANCE + user.getId());
	}

	/**
	 * ????????????????????????
	 */
	private void deleteRepeatResInsDiscoverParamterCache(ILoginUser user){
//		IMemcache<Map> iMem = MemCacheFactory.getRemoteMemCache(Map.class);
//		iMem.delete(RES_DIS_REPEAT_INSTANCE_PARAMTER + user.getId());
		user.getCache().remove(RES_DIS_REPEAT_INSTANCE_PARAMTER + user.getId());
	}
	/**
	 * ????????????
	 * 
	 * @throws Exception
	 */
	@Override
	public Map<String, Object> resourceBatchDiscover(MultipartFile file, String domainId, String nodeGroupId, ILoginUser user) {
		// ???????????????????????????
		Map<String, Object> userCache = initialBatchDiscoveryStatus(user);
		// ??????????????????
		Map<String, Object> result = new HashMap<String, Object>();
		int successcnt = 0, failurecnt = 0, maxDiscoverNum = 500;
		Long currentTime = System.currentTimeMillis(); // ??????????????????
		// ????????????????????????
		loadBatDiscKv();
		// ??????excel??????
		String errorMsg = "";
		Workbook wb = null;
		try {
			wb = getWorkbook(file);
		} catch (Exception e) {
			errorMsg = e.getMessage();
			logger.error("fileInputstream2List error:", e);
		}
		// ??????excel
		if (wb != null) {
			if (!verifyExcel(wb)) {
				errorMsg = "?????????excel?????????????????????";
			}
		}
		// excel??????????????????
		if (wb != null && "".equals(errorMsg)) {
			CellStyle style = configErrorCellStyle(wb);
			Map<String, Dict> failureCodeMap = getdiscoveryFailureCodes();
			List<List<Map<String, String>>> sheetList = fileInputstream2List(wb);
			// ???????????????????????????
			int amount = 0;
			for(int i = 0; i < sheetList.size(); i++){
				amount += sheetList.get(i).size();
			}
			userCache.put("batchDiscoverAmount", amount);
			// ??????????????????
			int currentDiscoverNum = 0;
			boolean continueModifyExcel = true;
			for (int i = 0; i < sheetList.size(); i++) {
				List<Map<String, String>> sheet = sheetList.get(i);
				for (int j = 0; j < sheet.size(); j++) {
					Map<String, String> paramter = sheet.get(j);
					logger.error("paramter = "+paramter);
					// ???????????????????????????????????????????????????
					if (++currentDiscoverNum > maxDiscoverNum){
						paramter.put("errorInfo", "???????????????????????????" + maxDiscoverNum);
					}
					if ((boolean) userCache.get("batchDiscoverCancel")) {
						paramter.put("errorInfo", "????????????");
					}
					if (!paramter.containsKey("errorInfo") && !paramter.isEmpty()) {
						try {
							String resourceId = (String)paramter.get("resourceId");
							//??????????????????
							String resName = (String)paramter.get("resourceName");
							ResourceDef resourceDef = capacityService.getResourceDefById(resourceId);
							String categoryId = resourceDef.getCategory().getParentCategory().getId();
							// license??????
							if(licenseCalcService.isLicenseEnough(categoryId)){
								logger.info("start discover batch resource:" + categoryId);
								paramter.put("nodeGroupId", nodeGroupId);
								paramter.put("domainId", domainId);
								ResourceInstanceDiscoveryParameter discoverParameter = setDiscoverParamter(paramter);
								DiscoverResourceIntanceResult drir = discoveryService.discoveryResourceInstance(discoverParameter);
								// ??????????????????
								if(drir.isSuccess()){
									ResourceInstance ri = drir.getResourceIntance();
									
									// ??????????????????
									//if(DiscoverResourceIntanceResult.RESOURCEINSTANCE_REPEAT == drir.getCode()){
									if(drir.getRepeatIds() != null && drir.getRepeatIds().size() > 0){
										failurecnt++;
										paramter.put("errorInfo", "????????????");
									}
									if (!paramter.containsKey("errorInfo")) {
										// ??????????????????
										if(resName != null && !"".equals(resName)){
											resourceInstanceService.updateResourceInstanceName(ri.getId(), resName);
										}else{
											String newShowName = reCreateShowName(createShowNameMap(ri), 1, ri.getShowName());
											resourceInstanceService.updateResourceInstanceName(ri.getId(), newShowName);
										}
										// ????????????
										try {
											profileService.addMonitorUseDefault(ri.getId());
										} catch (Exception e) {
											logger.error("resourceBatchDiscover-addMonitorUseDefault", e);
										}
										successcnt++;
									}
								}else{
									failurecnt++;
									String failureCode = String.valueOf(drir.getCode());
									String failureMsg = "??????[" + failureCode + "]";
									if(failureCodeMap.containsKey(failureCode)){
										failureMsg = failureCodeMap.get(failureCode).getDescription();
									}
									// ????????????
									paramter.put("errorInfo", failureMsg);
									logger.error("???????????????[" + JSON.toJSONString(paramter) + "]");
								}
								logger.info("end discover batch resource:" + categoryId);
							}else{
								failurecnt++;
								paramter.put("errorInfo", "???????????????????????????License????????????");
							}
						} catch (Exception e) {
							// ???????????????1
							failurecnt++;
							paramter.put("errorInfo", e.getMessage());
							logger.error("batchDiscoverResouce:", e);
						}
					} else {
						// ???????????????1
						failurecnt++;
					}
					// ??????excel????????????????????????????????????????????????
					if (paramter.containsKey("errorInfo") && continueModifyExcel) {
						// ???????????????????????????????????????
						Sheet currentSheet = wb.getSheetAt(i);
						Row secondRow = currentSheet.getRow(2);
						int lastCellNum = secondRow.getLastCellNum();
						Row row = currentSheet.getRow(j + 3);
						// ????????????
						if(row == null){
							continue;
						}
						// excel????????????
						// row.setRowStyle(style);
						// Iterator<Cell> iter = row.iterator();
						for(int colNum = 0; colNum < lastCellNum; colNum++){
							Cell cell = row.getCell(colNum);
							cell = cell == null ? row.createCell(colNum) : cell;
							cell.setCellStyle(style);
						}
						// ????????????
						String errorInfo = "???????????????" + paramter.get("errorInfo");
						// ???????????????????????????
						if((boolean) userCache.get("batchDiscoverCancel")){
							errorInfo = "?????????????????????????????????...";
							continueModifyExcel = false;
						// ????????????????????????
						}else if(currentDiscoverNum > maxDiscoverNum){
							errorInfo = "??????????????????????????????????????????" + maxDiscoverNum;
							continueModifyExcel = false;
						}
						Cell errorInfoCell = row.createCell(lastCellNum);
						errorInfoCell.setCellType(Cell.CELL_TYPE_STRING);
						errorInfoCell.setCellValue(errorInfo);
						errorInfoCell.setCellStyle(style);
						// ??????????????????????????????
						currentSheet.autoSizeColumn(lastCellNum, true);
					}
					userCache.put("batchDiscoverSuccessNum", successcnt);
					userCache.put("batchDiscoverFailureNum", failurecnt);
				}
				
			}
		}
		// ???????????????????????????????????????excel?????????wb??????
		if(wb != null && "".equals(errorMsg)){
			try {
				saveExcelFile(wb, user);
			} catch (IOException e) {
				errorMsg = e.getMessage();
				logger.error("saveExcelFile", e);
			}
		}
		// ????????????????????????
		setBatchDiscoveryResult(result, errorMsg, successcnt, failurecnt, currentTime, user);
		return result;
	}
	
	/**
	 * ???????????????excel??????
	 * @param wb
	 * @return
	 */
	private boolean verifyExcel(Workbook wb){
		boolean flag = true;
		List<String> sheetNameList = new ArrayList<String>();
		sheetNameList.add(DATABASE_SHEET_NAME);
		sheetNameList.add(HOST_SNMP_SHEET_NAME);
		sheetNameList.add(HOST_EXCLUDE_SNMP_SHEET_NAME);
		sheetNameList.add(NETWORK_SHEET_NAME);
		sheetNameList.add(STAND_SHEET_NAME);
		int sheetNum = wb.getNumberOfSheets();
		for(int i = 0; i < sheetNum; i++){ // ???????????????sheet
			Sheet sheet = wb.getSheetAt(i);
			if(!sheetNameList.contains(sheet.getSheetName())){
				flag = false;
				break;
			}
		}
		return flag;
	}
	/**
	 * ???????????????excel??????
	 * @param wb
	 * @return
	 */
	private CellStyle configErrorCellStyle(Workbook wb){
		CellStyle style = wb.createCellStyle();
		style.setFillPattern(HSSFCellStyle.SOLID_FOREGROUND);
		style.setFillForegroundColor(HSSFColor.ROSE.index);
		// ??????????????????
		style.setBorderTop(CellStyle.BORDER_THIN);
		style.setTopBorderColor(HSSFColor.BLACK.index);
		style.setBorderBottom(CellStyle.BORDER_THIN);
		style.setBottomBorderColor(HSSFColor.BLACK.index);
		style.setBorderLeft(CellStyle.BORDER_THIN);
		style.setLeftBorderColor(HSSFColor.BLACK.index);
		style.setBorderRight(CellStyle.BORDER_THIN);
		style.setRightBorderColor(HSSFColor.BLACK.index);
		// ??????
		Font font = wb.createFont();
		font.setFontHeightInPoints((short)10);
		style.setFont(font);
		return style;
	}
	
	/**
	 * ???????????????????????????
	 * @param user
	 * @param sheetList
	 */
	private Map<String, Object> initialBatchDiscoveryStatus(ILoginUser user){
		Map<String, Object> userCache = user.getCache();
		userCache.put("batchDiscoverSuccessNum", 0);
		userCache.put("batchDiscoverFailureNum", 0);
		userCache.put("batchDiscoverFinish", false);
		userCache.put("batchDiscoverCancel", false);
		return userCache;
	}
	
	/**
	 * ????????????????????????
	 * @return
	 */
	private Map<String, Dict> getdiscoveryFailureCodes(){
		List<Dict> failureCodeList = dictApi.get("discoveryFailureCode");
		Map<String, Dict> failureCodeMap = new HashMap<String, Dict>();
		failureCodeList = failureCodeList == null ? new ArrayList<Dict>() : failureCodeList;
		for(int i = 0; i < failureCodeList.size(); i++){
			failureCodeMap.put(failureCodeList.get(i).getCode(), failureCodeList.get(i));
		}
		return failureCodeMap;
	}
	/**
	 * ??????????????????????????????
	 * @param result
	 * @param errorMsg
	 * @param successcnt
	 * @param failurecnt
	 * @param currentTime
	 */
	private void setBatchDiscoveryResult(Map<String, Object> result,
			String errorMsg, int successcnt, int failurecnt, Long currentTime,
			ILoginUser user) {
		List<Map<String, String>> dgd = new ArrayList<Map<String, String>>();
		// ??????excel?????????
		if("".equals(errorMsg)){
			result.put("time", calcTime(System.currentTimeMillis() - currentTime));
			Map<String, String> successMap = new HashMap<String, String>();
			successMap.put("code", "1");
			successMap.put("result", "????????????");
			successMap.put("count", String.valueOf(successcnt));
			Map<String, String> failureMap = new HashMap<String, String>();
			failureMap.put("code", "0");
			failureMap.put("result", "????????????");
			failureMap.put("count", String.valueOf(failurecnt));
			dgd.add(successMap);
			dgd.add(failureMap);
			result.put("datagrid", dgd);
			result.put("isErrorMsg", false);
		}else{
			result.put("time", "00:00:00");
			Map<String, String> failureMap = new HashMap<String, String>();
			failureMap.put("code", "0");
			failureMap.put("result", "????????????");
			failureMap.put("count", errorMsg);
			dgd.add(failureMap);
			result.put("datagrid", dgd);
			result.put("isErrorMsg", true);
		}
		// ????????????????????????
		user.getCache().put("batchDiscoverFinish", true);
	}
	
	/**
	 * ??????batdisckv???
	 */
	private void loadBatDiscKv() {
		// ????????????????????????????????????????????????????????????
		if(batdisckvBoList == null || batdisckvBoList.isEmpty()){
			logger.error(" CreatBatTable ");
			batdisckvBoList = createBatDiscKvTable();
		}
	}

	private List<BatdisckvBo> createBatDiscKvTable(){
		List<BatdisckvBo> batdisckvBoList = new ArrayList<BatdisckvBo>();
		// ?????????????????? collectType
		BatdisckvBo collectTypeTitle = new BatdisckvBo();
		collectTypeTitle.setId(sequence.next());
		collectTypeTitle.setCellkey(COLLECTTYPE_NAME);
		collectTypeTitle.setCellvalue(COLLECTTYPE_ID);
		collectTypeTitle.setCelltype(BATDISCKV_TYPE_TITLE);
		// ?????????????????? resourceId
		BatdisckvBo resourceTitle = new BatdisckvBo();
		resourceTitle.setId(sequence.next());
		resourceTitle.setCellkey(RESOURCETYPE_NAME);
		resourceTitle.setCellvalue(RESOURCETYPE_ID);
		resourceTitle.setCelltype(BATDISCKV_TYPE_TITLE);
		
		// ?????????????????? resourceName
		BatdisckvBo reNameTitle = new BatdisckvBo();
		reNameTitle.setId(sequence.next());
		reNameTitle.setCellkey(RESOURCERENAME_NAME);
		reNameTitle.setCellvalue(RESOURCERENAME_ID);
		reNameTitle.setCelltype(BATDISCKV_TYPE_TITLE);
		
		scanAllResourceDefs(capacityService.getRootCategory(), batdisckvBoList, collectTypeTitle, resourceTitle);
		batdisckvBoList.add(collectTypeTitle);
		batdisckvBoList.add(resourceTitle);
		batdisckvBoList.add(reNameTitle);
		// ?????????????????????
		batDiscKvDao.initBatDiscKvTable(batdisckvBoList);
		return batdisckvBoList;
	}
	/**
	 * ???????????????????????????
	 * @param rootCategory
	 * @param batdisckvBoList
	 * @param collectTypeTitle
	 * @param resourceTitle
	 */
	private void scanAllResourceDefs(CategoryDef rootCategory, List<BatdisckvBo> batdisckvBoList, BatdisckvBo collectTypeTitle, BatdisckvBo resourceTitle){
		if(rootCategory.getChildCategorys() != null){
			CategoryDef[] categoryDefs = rootCategory.getChildCategorys();
			for (int i = 0; i < categoryDefs.length; i++) {
				scanAllResourceDefs(categoryDefs[i], batdisckvBoList, collectTypeTitle, resourceTitle);
			}
		} else if(rootCategory.getResourceDefs() != null){
			ResourceDef[] resourceDefs = rootCategory.getResourceDefs();
			// ?????????resourceDef
			for (int i = 0; i < resourceDefs.length; i++) {
				ResourceDef resourceDef = resourceDefs[i];
				batdiscResourceDefKv.put(resourceDef.getName(), resourceDef.getId());
				BatdisckvBo resourcekv = new BatdisckvBo();
				resourcekv.setId(sequence.next());
				resourcekv.setCellkey(resourceDef.getName());
				resourcekv.setCellvalue(resourceDef.getId());
				resourcekv.setCelltype(BATDISCKV_TYPE_CONTENT);
				resourcekv.setTitleId(resourceTitle.getId());
				resourcekv.setResourceId(resourceDef.getId());
				if(!batdisckvBoList.contains(resourcekv)){
					batdisckvBoList.add(resourcekv);
				}
				Map<String, PluginInitParameter[]> initParameterMap = resourceDef.getPluginInitParameterMap();
				Set<String> keys = initParameterMap.keySet();
				Iterator<String> iter = keys.iterator();
				while(iter.hasNext()){
					String key = iter.next();
					PluginInitParameter[] initParameters = initParameterMap.get(key);
					List<BatdisckvBo> initParameterList = createInitParamter(initParameters, resourceDef);
					for (int j = 0; j < initParameterList.size(); j++) {
						if(!batdisckvBoList.contains(initParameterList.get(j))){
							batdisckvBoList.add(initParameterList.get(j));
						}
					}
				}
				// ??????plugin
				Map<String, String> optionPluginMap = resourceDef.getOptionCollPluginIds();
				if(!optionPluginMap.isEmpty()){
					Iterator<Entry<String, String>> iterEntry = optionPluginMap.entrySet().iterator();
					while(iterEntry.hasNext()){
						Entry<String, String> entry = iterEntry.next();
						BatdisckvBo collType = new BatdisckvBo();
						collType.setId(sequence.next());
						collType.setCelltype(BATDISCKV_TYPE_CONTENT);
						collType.setCellkey(entry.getValue());
						collType.setCellvalue(entry.getValue());
						collType.setTitleId(collectTypeTitle.getId());
						collType.setResourceId(resourceDef.getId());
						if(!batdisckvBoList.contains(collType)){
							batdisckvBoList.add(collType);
						}
					}
				}
			}
		}
	}
	/**
	 * ?????????????????????????????????????????????
	 * @param initParameters
	 * @return
	 */
	private List<BatdisckvBo> createInitParamter(PluginInitParameter[] initParameters, ResourceDef resourceDef){
		List<BatdisckvBo> initParameterList = new ArrayList<BatdisckvBo>();
		for (int j = 0; j < initParameters.length; j++) {
			PluginInitParameter initParameter = initParameters[j];
			BatdisckvBo paramterkv = new BatdisckvBo();
			paramterkv.setId(sequence.next());
			paramterkv.setCelltype(BATDISCKV_TYPE_TITLE);
			paramterkv.setCellkey(initParameter.getName());
			paramterkv.setCellvalue(initParameter.getId());
			paramterkv.setResourceId(resourceDef.getId());
			if(!initParameterList.contains(paramterkv)){
				initParameterList.add(paramterkv);
				if(initParameter.getSupportValues() != null){
					List<BatdisckvBo> batdisckvBos = createSupportKv(paramterkv, initParameter.getSupportValues(), resourceDef);
					for (int k = 0; k < batdisckvBos.size(); k++) {
						if(!initParameterList.contains(batdisckvBos.get(k)))
							initParameterList.add(batdisckvBos.get(k));
					}
				}
			}
		}
		return initParameterList;
	}
	/**
	 * ???????????????supportkv
	 * @param paramterkv
	 * @param supportValues
	 * @return
	 */
	private List<BatdisckvBo> createSupportKv(BatdisckvBo paramterkv,
			SupportValue[] supportValues, ResourceDef resourceDef) {
		List<BatdisckvBo> supportList = new ArrayList<BatdisckvBo>();
		// ??????supportValue
		for (int k = 0; k < supportValues.length; k++) {
			SupportValue supportValue = supportValues[k];
			BatdisckvBo supportkv = new BatdisckvBo();
			supportkv.setId(sequence.next());
			supportkv.setCelltype(BATDISCKV_TYPE_CONTENT);
			supportkv.setCellkey(supportValue.getName());
			supportkv.setCellvalue(supportValue.getValue());
			supportkv.setReg(supportValue.getValue());
			supportkv.setTitleId(paramterkv.getId());
			supportkv.setResourceId(resourceDef.getId());
			if (!supportList.contains(supportkv)) {
				supportList.add(supportkv);
			}
		}
		return supportList;
	}

	private void saveExcelFile(Workbook wb, ILoginUser user) throws IOException{
		// ???????????????
		String filePath = this.getClass().getClassLoader().getResource("").getPath();
		filePath = URLDecoder.decode(filePath, "UTF-8");
		String classPath = this.getClass().getPackage().getName().replace(".", File.separator);
		// ????????????
		File outFile = new File(filePath + classPath + File.separator + user.getId() + File.separator + BATCH_DISCOVER_RESULT_FILE);
		if(!outFile.exists()){
			FileUtil.createFile(outFile);
		}
		FileOutputStream outStream = new FileOutputStream(outFile);
		wb.write(outStream);
		outStream.flush();
		outStream.close();
	}
	
	/**
	 * ??????workbook
	 * @param fileModel
	 * @return
	 * @throws Exception
	 */
	private Workbook getWorkbook(MultipartFile file) throws Exception{
		String fileName = file.getOriginalFilename();
		boolean isE2007 = false;
		if (fileName.endsWith(".xlsx")) {
			isE2007 = true;
		} else if (fileName.endsWith(".xls")) {
			isE2007 = false;
		} else {
			throw new Exception("????????????excel??????");
		}
		Workbook wb = null;
		if (isE2007) {
			wb = new XSSFWorkbook(file.getInputStream());
		} else {
			wb = new HSSFWorkbook(file.getInputStream());
		}
		return wb;
	}
	/**
	 * ????????????excle???????????????map??????
	 * 
	 * @param fileModel
	 * @return
	 * @throws Exception 
	 */
	private List<List<Map<String, String>>> fileInputstream2List(Workbook wb){
		List<List<Map<String, String>>> result = new ArrayList<List<Map<String, String>>>();
		// ???????????????sheet
		for(int i = 0; i < wb.getNumberOfSheets(); i++){
			List<Map<String, String>> sheetList = new ArrayList<Map<String, String>>();
			Sheet sheet = wb.getSheetAt(i);
			String sheetName = sheet.getSheetName();
			Iterator<Row> rowIter = sheet.iterator();
			List<String> titles = new ArrayList<String>();
			// ???????????????
			while(rowIter.hasNext()){
				Row row = rowIter.next();
				if(row == null){
					continue;
				}
				int rowNum = row.getRowNum();
				Map<String, String> rowMap = new HashMap<String, String>();
				if(rowNum < 2){
					// ????????????
					continue;
				}else if(rowNum == 2){
					// ??????
					Iterator<Cell> cellIter = row.iterator();
					while(cellIter.hasNext()){
						Cell cell = cellIter.next();
						String cellText = getCellValue(cell);
						titles.add(cellText);
					}
				}else if(rowNum > 2){
					// ??????
					if(row.getPhysicalNumberOfCells() == 0){
						continue;
					}
					String errorInfo = "";
					// ????????????????????????id
					Cell firstCell = row.getCell(0);
					// ?????????????????????????????????????????????sheet????????????????????????
					if(firstCell == null){
						errorInfo = "?????????????????????????????????";
					}
					// ??????????????????ID
					String resourceId = null;
					if("".equals(errorInfo)){
						resourceId = getCellValue(row.getCell(0));
						// ?????????????????????
						if(NETWORK_SHEET_NAME.equals(sheetName)){
							try {
								CategoryDef switchCategoryDef = capacityService.getCategoryById(CapacityConst.SWITCH);
								resourceId = switchCategoryDef.getResourceDefs()[0].getName();
							} catch (Exception e) {
								logger.error("fileInputstream2List capacityService.getCategoryById", e);
							}
						}
						resourceId = batdiscResourceDefKv.get(resourceId);
					}
					if(resourceId == null){
						errorInfo = "???????????????????????????????????????????????????";
					}
					// ?????????????????????????????????
					if("".equals(errorInfo)){
						errorInfo = validateRowData(titles, row, resourceId);
					}
					// ??????????????????????????????
					if("".equals(errorInfo)){
						Iterator<Cell> cellIter = row.iterator();
						while(cellIter.hasNext()){
							Cell cell = cellIter.next();
							String value = getCellValue(cell);
							BatdisckvBo contentBo = getBatdisckvBoByKey(value, resourceId, BATDISCKV_TYPE_CONTENT);
							value = contentBo != null ? contentBo.getCellvalue() : value;
							if(value != null){
								String title = titles.get(cell.getColumnIndex());
								BatdisckvBo titleKvBo = getBatdisckvBoByKey(title, resourceId, BATDISCKV_TYPE_TITLE);
								// ????????????????????????????????????resourceId
								if (RESOURCETYPE_NAME.equals(title) && NETWORK_SHEET_NAME.equals(sheetName)) {
									value = resourceId;
								}
								if (titleKvBo != null) {
									rowMap.put(titleKvBo.getCellvalue(), value);
								}
							}
						}
					}else{
						rowMap.put("errorInfo", errorInfo);
					}
				}
				// ????????????????????????
				if(!rowMap.isEmpty()){
					// ?????????????????????
					if(rowMap.size() <= 1){
						rowMap.put("errorInfo", "????????????????????????????????????");
					}else{
						// ???????????????
						// ????????????????????????????????????????????????dbType
						if(DATABASE_SHEET_NAME.equals(sheetName)){
							String resourceId = rowMap.get(RESOURCETYPE_ID);
							rowMap.put("dbType", resourceIdMapDbType(resourceId));
						}
						// ??????????????????telnet??????????????????hostType
						if(HOST_EXCLUDE_SNMP_SHEET_NAME.equals(sheetName)){
							String resourceId = rowMap.get(RESOURCETYPE_ID);
							if(DiscoverWayEnum.TELNET.toString().equals(rowMap.get(COLLECTTYPE_ID))){
								rowMap.put("hostType", resourceIdMapHostType(resourceId));
							}
						}
						// ????????????????????????SNMP???????????????
						if(NETWORK_SHEET_NAME.equals(sheetName) || HOST_SNMP_SHEET_NAME.equals(sheetName)){
							// ????????????
							if(!rowMap.containsKey("community")){
								rowMap.put("community", "public");
							}
							// SNMP ??????
							if(!rowMap.containsKey("snmpPort")){
								rowMap.put("snmpPort", "161");
							}
						}
					}
					sheetList.add(rowMap);
				}
			}
			result.add(sheetList);
		}
		return result;
	}

	/**
	 * ?????????resource?????????dbType
	 * @param resourceId
	 * @return
	 */
	private String resourceIdMapDbType(String resourceId){
		String dbType = "";
		CategoryDef parentCategory = capacityService.getResourceDefById(resourceId).getCategory();
		switch (parentCategory.getId()) {
		case "Oracles":
			if(resourceId.toLowerCase().startsWith("OracleRAC".toLowerCase())){
				dbType = "oracleRAC";
			}else{
				dbType = "Oracle";
			}
			break;
		case "SQLServers":
			dbType = "SQLServer";
			break;
		case "DB2s":
			dbType = "DB2";
			break;
		case "Informixs":
			dbType = "Informix";
			break;
		case "MySQLs":
			dbType = "MySQL";
			break;
		case "Sybases":
			dbType = "Sybase";
			break;
		case "PostgreSQLs":
			dbType = "PostgreSQL";
			break;
		case "DMs":
			dbType = "DM";
			break;
		case "ShenzhouSQLs":
			dbType = "ShenzhouSQL";
			break;
		case "caches":
			dbType = "Cache";
			break;
		}
		return dbType;
	}

	/**
	 * ????????????resource?????????hostType
	 * @param resourceId
	 * @return
	 */
	private String resourceIdMapHostType(String resourceId){
		String hostType = "";
		switch (resourceId) {
		case "Linux":
			hostType = "Linux";
			break;
		case "AIX":
			hostType = "AIX";
			break;
		case "HPUX":
			hostType = "HPUX";
			break;
		case "Solaris":
			hostType = "Solaris";
			break;
		case "Openserver":
			hostType = "Openserver";
			break;
		case "Scounixware":
			hostType = "Unixware";
			break;
		case "Freebsd":
			hostType = "FreeBSD";
			break;
		}
		return hostType;
	}
	/**
	 * ????????????cell?????????
	 * 
	 * @param cell
	 * @return
	 */
	private String getCellValue(Cell cell) {
		String value = null;
		switch (cell.getCellType()) {
		case Cell.CELL_TYPE_STRING:
			value = cell.getStringCellValue();
			break;
		case Cell.CELL_TYPE_FORMULA:
		case Cell.CELL_TYPE_NUMERIC:
			String valueStr = String.valueOf(cell.getNumericCellValue());
			if(valueStr != null){
				value = (valueStr.endsWith(".0") ? valueStr.substring(0, valueStr.indexOf(".0")) : valueStr).trim();
			}
			break;
		case Cell.CELL_TYPE_BOOLEAN:
			value = String.valueOf(cell.getBooleanCellValue());
		}
		return value != null ? value.trim() : value;
	}
	
	/**
	 * ?????????????????????????????????
	 */
	private String validateRowData(List<String> titles, Row row, String resourceId){
		StringBuffer errorInfo = new StringBuffer();
		// ????????????????????????
		if(row.getRowNum() >= 2){
			Iterator<Cell> iter = row.iterator();
			while (iter.hasNext()){
				Cell cell = iter.next();
				// ???????????????????????????????????????
				if(cell.getColumnIndex() >= titles.size()){
					continue;
				}
				String text = getCellValue(cell);
				BatdisckvBo contentBo = getBatdisckvBoByKey(text, resourceId, BATDISCKV_TYPE_CONTENT);
				BatdisckvBo titleBo = getBatdisckvBoByKey(titles.get(cell.getColumnIndex()), resourceId, BATDISCKV_TYPE_TITLE);
				List<BatdisckvBo> childrenBo = null;
				if(titleBo != null){
					childrenBo = getchildContentBatdisckvBo(titleBo.getId(), resourceId);
				}
				// ?????????????????????
				if(childrenBo != null && contentBo != null && !childrenBo.contains(contentBo)){
					errorInfo.append(titleBo.getCellkey()).append("????????????;");
					continue;
				}
				// ????????????????????????
				String reg = null;
				if(titleBo != null){
					reg = titleBo.getReg();
				}
				if (reg != null && !"".equals(reg.trim()) && !Pattern.matches(reg, text)) {
					errorInfo.append(titleBo.getCellkey()).append("????????????;");
					continue;
				}
			}
		}
		return errorInfo.toString();
	}
	/**
	 * ??????key???????????????bo??????
	 * @param text
	 * @return
	 */
	private BatdisckvBo getBatdisckvBoByKey(String text, String resourceId, String cellType) {
		BatdisckvBo bo = null;
		if(RESOURCETYPE_NAME.equals(text) || COLLECTTYPE_NAME.equals(text) || RESOURCERENAME_NAME.equals(text)){
			resourceId = "";
		}
		for (int i = 0; i < batdisckvBoList.size(); i++) {
			BatdisckvBo tmp = batdisckvBoList.get(i);
			if(text != null
					&& text.equals(tmp.getCellkey())
					&& resourceId.equals(tmp.getResourceId())
					&& cellType.equals(tmp.getCelltype())){
				bo = tmp;
				break;
			}
		}
		return bo;
	}

	/**
	 * ??????id??????????????????bo??????
	 * 
	 * @param titleId
	 * @return
	 */
	private List<BatdisckvBo> getchildContentBatdisckvBo(long titleId, String resourceId) {
		List<BatdisckvBo> batdisckvBo = new ArrayList<BatdisckvBo>();
		for (int i = 0; i < batdisckvBoList.size(); i++) {
			BatdisckvBo tmp = batdisckvBoList.get(i);
			if (BATDISCKV_TYPE_CONTENT.equals(tmp.getCelltype())
					&& tmp.getTitleId() != null && tmp.getTitleId() == titleId
					&& tmp.getResourceId() != null && tmp.getResourceId().equals(resourceId)) {
				batdisckvBo.add(tmp);
			}
		}
		return batdisckvBo.isEmpty() ? null : batdisckvBo;
	}

	/**
	 * ??????ip??????
	 * id\name
	 * @param instance
	 * @return
	 */
	private List<Map<String, String>> getIpList(ResourceInstance instance){
		List<Map<String, String>> ipList = new ArrayList<Map<String, String>>();
		if(instance.getShowIP() != null){
			Map<String, String> discoverIpMap = new HashMap<String, String>();
			discoverIpMap.put("id", instance.getShowIP());
			discoverIpMap.put("name", instance.getShowIP());
			ipList.add(discoverIpMap);
		}
		
		String[] ips = instance.getModulePropBykey(MetricIdConsts.METRIC_IP);
		for(int i = 0; ips != null && i < ips.length; i++){
			String ip = ips[i];
			Map<String, String> ipMap = new HashMap<String, String>();
			ipMap.put("id", ip);
			ipMap.put("name", ip);
			if(!ipList.contains(ipMap)){
				ipList.add(ipMap);
			}
		}
		// ??????ip?????????host
		if(ipList.isEmpty()){
			String[] host = instance.getDiscoverPropBykey("host");
			if(host != null && host.length > 0){
				Map<String, String> ipMap = new HashMap<String, String>();
				ipMap.put("id", host[0]);
				ipMap.put("name", host[0]);
				ipList.add(ipMap);
			}
		}
		return ipList;
	}
	@Override
	public File getBatchResultFile(ILoginUser user) throws Exception {
		String filePath = this.getClass().getClassLoader().getResource("").getPath();
		filePath = URLDecoder.decode(filePath, "UTF-8");
		String classPath = this.getClass().getPackage().getName().replace(".", File.separator);
		File outFile = new File(filePath + classPath + File.separator + user.getId() + File.separator + BATCH_DISCOVER_RESULT_FILE);
		return outFile;
	}

	@Override
	public List<Map<String, String>> getProfileByInstanceId(Long instanceId) {
		List<Map<String, String>> result = new ArrayList<Map<String, String>>();
		// ????????????????????????
		Map<String, String> baseProfile = new HashMap<String, String>();
		baseProfile.put("id", "1");
		baseProfile.put("name", "??????????????????");
		result.add(baseProfile);
		try {
			ResourceInstance instance = resourceInstanceService.getResourceInstance(instanceId);
			String resourceId = instance.getResourceId();
			List<String> resourceIds = new ArrayList<String>();
			resourceIds.add(resourceId);
			List<ProfileInfo> profileInfos = profileService.getProfileBasicInfoByResourceId(resourceIds, ProfileTypeEnum.SPECIAL);
			for (int i = 0; i < profileInfos.size(); i++) {
				ProfileInfo profileInfo = profileInfos.get(i);
				// ????????????????????????????????????
				if(profileInfo.getDomainId() != instance.getDomainId()){
					break;
				}
				Long profileId = profileInfo.getProfileId();
				String profileName = profileInfo.getProfileName();
				Map<String, String> map = new HashMap<String, String>();
				map.put("id", String.valueOf(profileId));
				map.put("name", profileName);
				result.add(map);
			}
		} catch (Exception e) {
			logger.error("getProfileByInstanceId", e);
		}
		return result;
	}

	/**
	 * status 2:??????License???????????? 1:???????????? 0:????????????
	 */
	@Override
	public Map<String, String> addMonitor(Long resourceGroupId, String newInstanceName, Long mainInstanceId, Long[] childInstanceIds) {
		Map<String, String> result = new HashMap<String, String>();
		try {
			// ??????????????????
			updateInstanceName(mainInstanceId, newInstanceName);
			// ?????????????????????????????????License????????????
			profileService.addMonitorUseDefault(mainInstanceId, Arrays.asList(childInstanceIds));
			if(resourceGroupId != null && resourceGroupId != 0){
				customResourceGroupApi.insertGroupAndResourceRelation(resourceGroupId, String.valueOf(mainInstanceId));
			}
			saveSearchRel(mainInstanceId);
			result.put("status", "1");
		} catch (Exception e) {
			result.put("status", "0");
			logger.error("addMonitor", e);
		}
		return result;
	}
	
	@Override
	public Map<String, Object> handleRepeatInstance(String method, ILoginUser user) {
		Map<String, Object> result = new HashMap<String, Object>();
		try {
			// ??????
			ResourceInstance instance = null;
			if("0".equals(method)){
				ResourceInstance resourceInstance = getRepeatResInsCache(user);
				ResourceInstanceResult rir = resourceInstanceService.addResourceInstance(resourceInstance);
				instance = resourceInstanceService.getResourceInstance(rir.getResourceInstanceId());
				result.put("instanceId", String.valueOf(rir.getResourceInstanceId()));
				// ?????????????????????????????????
				Map discoverParamter = getRepeatResInsDiscoverParamterCache(user);
				insertReAccountInstance(discoverParamter, instance);
			// ??????
			}else if("1".equals(method)){
				ResourceInstance resourceInstance = getRepeatResInsCache(user);
				resourceInstanceService.refreshResourceInstance(resourceInstance);
//				collectResourceMetrics(resourceInstance);
				instance = resourceInstanceService.getResourceInstance(resourceInstance.getId());
			}
			if(user!=null){
				deleteRepeatResInsCache(user);
				deleteRepeatResInsDiscoverParamterCache(user);
			}
			
			
			result.put("childInstance", getChildInstanceInParent(instance));
			result.put("status", "1");
		} catch (Exception e) {
			result.put("status", "0");
			logger.error("handleRepeatInstance", e);
		}
		return result;
	}
	/**
	 * ???????????????????????????
	 * @param instanceId
	 */
	private void saveSearchRel(long instanceId){
		ResourceBizRel rbr = new ResourceBizRel(instanceId, instanceId, SEARCH_NAV);
		searchApi.saveSearchResource(rbr);
	}
	/**
	 * ??????????????????
	 */
	@Override
	public int updateInstanceName(Long instanceId, String name) {
		int flag = 1;
		try {
			if(!isShowNameRepeat(instanceId, name)){
				resourceInstanceService.updateResourceInstanceName(instanceId, name);
				resourceInstanceService.getResourceInstance(instanceId);
			}else{
				flag = 2;
			}
		} catch (InstancelibException e) {
			flag = 0;
			logger.error("updateInstanceName", e);
		}
		return flag;
	}
	/**
	 * ???????????????????????? status : 0 ?????? 1 ??????
	 * ????????????????????????Map??????
	 * 
	 * @param resourceId
	 * @return
	 */
	@Override
	public int updateDiscoverParamter(Map paramter, long instanceId){
		int result = 1;
		List<DiscoverProp> DiscoverProps = new ArrayList<DiscoverProp>();
		Iterator<String> iter = paramter.keySet().iterator();
		while(iter.hasNext()){
			String key = iter.next();
			DiscoverProp discoverProp = new DiscoverProp();
			discoverProp.setInstanceId(instanceId);
			discoverProp.setKey(key);
			// ?????????????????????value???
			String value = (String)paramter.get(key);
			if (SecureUtil.isPassswordKey(key)) {
				value = SecureUtil.pwdEncrypt(value);
			}
			discoverProp.setValues(new String[]{value});
			DiscoverProps.add(discoverProp);
		}
		if(!DiscoverProps.isEmpty()){
			try {
				ResourceInstanceDiscoveryParameter ridp = setDiscoverParamter(paramter);
				ridp.setOnlyDiscover(true);
				DiscoverResourceIntanceResult drir = discoveryService.discoveryResourceInstance(ridp);
				if(drir.isSuccess()){
					// ????????????????????????
					discoverPropService.updateProps(DiscoverProps);
					// ??????????????????????????????
					collectResourceMetrics(resourceInstanceService.getResourceInstance(instanceId));
					// ????????????????????????????????????
					ResourceInstance ri = drir.getResourceIntance();
					long[] instanceIds = {instanceId};
					reAccountInstanceDao.deleteResourceAndAccountRelation(instanceIds);
					// ??????????????????????????????
					ri.setId(instanceId);
					insertReAccountInstance(paramter, ri);
					// ????????????IP
					String IP = (String)paramter.get("IP");
					if(IP != null && !"".equals(IP)){
						resourceInstanceService.updateResourceInstanceShowIP(instanceId, IP);
					}
				}else{
					result = 2;
				}
			} catch (InstancelibException e) {
				result = 0;
				logger.error("updateDiscoverParamter", e);
			} catch (MetricExecutorException e) {
				result = 0;
				logger.error("collectResourceMetrics", e);
			}
		}
		return result;
	}

	/**
	 * ?????????????????? status : 0 ?????? 1 ?????? 2 ????????????
	 * ????????????????????????Map??????
	 * 
	 * @param resourceId
	 * @return
	 */
	@Override
	public int reDiscover(Map paramter, long instanceId) {
		int result = 1;
		ResourceInstanceDiscoveryParameter ridp = setDiscoverParamter(paramter);
		ridp.setOnlyDiscover(true);
		DiscoverResourceIntanceResult drir = discoveryService.discoveryResourceInstance(ridp);
		if(drir.isSuccess()){
			try {
				ResourceInstance ri = drir.getResourceIntance();
				ri.setId(instanceId);
				resourceInstanceService.refreshResourceInstance(ri);
				// ??????????????????????????????
				collectResourceMetrics(ri);
				// ????????????????????????????????????
				long[] instanceIds = {ri.getId()};
				reAccountInstanceDao.deleteResourceAndAccountRelation(instanceIds);
				// ??????????????????????????????
				insertReAccountInstance(paramter, ri);
			} catch (InstancelibException e) {
				result = 2;
				logger.error("rediscover refreshResourceInstance error:", e);
			} catch (MetricExecutorException e){
				result = 2;
				logger.error("rediscover collectResourceMetrics error:", e);
			}
		}else{
			logger.error("rediscover refresh error code:" + drir.getCode());
			result = 0;
		}
		return result;
	}

	/**
	 * ??????????????????????????????????????????????????????????????????
	 * @param ri
	 * @throws ProfilelibException
	 * @throws MetricExecutorException 
	 */
	private void collectResourceMetrics(ResourceInstance ri) throws MetricExecutorException{
		if(InstanceLifeStateEnum.MONITORED.equals(ri.getLifeState())){
			metricDataService.triggerInfoMetricGather(ri.getId(), true);
		}
	}

	@Override
	public int testDiscover(Map paramter, long instanceId) {
		int result = 0;
		ResourceInstanceDiscoveryParameter ridp = setDiscoverParamter(paramter);
		ridp.setOnlyDiscover(true);
		DiscoverResourceIntanceResult drir = discoveryService.discoveryResourceInstance(ridp);
		if(drir.isSuccess()){
			result = 1;
		}else{
			result = drir.getCode();
		}
		return result;
	}

	@Override
	public boolean isShowNameRepeat(long instanceId, String showName) {
		boolean isRepeat = false;
		try {
			if(showName != null && !"".equals(showName)){
				ResourceInstance ri = resourceInstanceService.getResourceInstance(instanceId);
				List<ResourceInstance> riList = resourceInstanceService.getAllParentInstance();
				for(int i = 0; riList != null && i < riList.size(); i++){
					ResourceInstance otherRi = riList.get(i);
					InstanceLifeStateEnum otherRiLife = otherRi.getLifeState();
					if ((otherRiLife == InstanceLifeStateEnum.MONITORED || otherRiLife == InstanceLifeStateEnum.NOT_MONITORED)
							&& ri.getId() != otherRi.getId()) {
						// ??????IP??????????????????IP
						if(ri.getShowIP() == null || "".equals(ri.getShowIP())){
							if(showName.equals(otherRi.getShowName()) && ri.getResourceId().equals(otherRi.getResourceId())){
								isRepeat = true;
								break;
							}
						}else{
							if(showName.equals(otherRi.getShowName()) && ri.getShowIP().equals(otherRi.getShowIP())
									&& ri.getResourceId().equals(otherRi.getResourceId())){
								isRepeat = true;
								break;
							}
						}
					}
				}
			}
		} catch (InstancelibException e) {
			logger.error("isShowNameRepeat", e);
		}
		return isRepeat;
	}
	
	@Override
	public String reCreateShowName(Map<String, String> showNameMap, int repeatNum, String showName){
		String newShowName = showName != null ? showName.trim() : showName;
		if(repeatNum != 1){
			newShowName = newShowName + "(" + repeatNum + ")";
		}
		if(showNameMap.containsKey(newShowName)){
			newShowName = reCreateShowName(showNameMap, ++ repeatNum, showName);
		}
		return newShowName;
	}
	private Map<String, String> createShowNameMap4RepeatInstance(ResourceInstance ri){
		Map<String, String> showNameMap = new HashMap<String, String>();
		try {
			String showName = ri.getShowName();
			if(showName != null){
				List<ResourceInstance> riList = resourceInstanceService.getResourceInstanceByResourceId(ri.getResourceId());
				for(int i = 0; riList != null && i < riList.size(); i++){
					ResourceInstance otherRi = riList.get(i);
					// ??????IP??????????????????IP
					if(ri.getShowIP() == null || "".equals(ri.getShowIP())){
						showNameMap.put(otherRi.getShowName(), otherRi.getShowName());
					}else{
						if(ri.getShowIP().equals(otherRi.getShowIP())){
							showNameMap.put(otherRi.getShowName(), otherRi.getShowName());
						}
					}
				}
			}
		} catch (InstancelibException e) {
			logger.error("showNameRepeatNum", e);
		}
		return showNameMap;
	}
	private Map<String, String> createShowNameMap(ResourceInstance ri){
		Map<String, String> showNameMap = new HashMap<String, String>();
		try {
			String showName = ri.getShowName();
			if(showName != null){
				List<ResourceInstance> riList = resourceInstanceService.getResourceInstanceByResourceId(ri.getResourceId());
				for(int i = 0; riList != null && i < riList.size(); i++){
					ResourceInstance otherRi = riList.get(i);
					if (ri.getId() != otherRi.getId()) {
						// ??????IP??????????????????IP
						if(ri.getShowIP() == null || "".equals(ri.getShowIP())){
							showNameMap.put(otherRi.getShowName(), otherRi.getShowName());
						}else{
							if(ri.getShowIP().equals(otherRi.getShowIP())){
								showNameMap.put(otherRi.getShowName(), otherRi.getShowName());
							}
						}
					}
				}
			}
		} catch (InstancelibException e) {
			logger.error("showNameRepeatNum", e);
		}
		return showNameMap;
	}
	
	/**
	 * ???????????????????????????????????????
	 */
	@Override
	public boolean checkDomainIsRel(long domainId) {
		Set<Long> domainSet = new HashSet<Long>();
		domainSet.add(domainId);
		try {
			List<ResourceInstance> riList = resourceInstanceService.getParentResourceInstanceByDomainIds(domainSet);
			if(riList != null && riList.size() > 0){
				return true;
			}
		} catch (InstancelibException e) {
			logger.error("DiscoverResourceImpl:checkDomainIsRel", e);
		}
		return false;
	}

	@Override
	public Map<String, Object> refreshDiscover(Map paramter, long instanceId,ILoginUser user) {
		Map<String, Object> resMap= new HashMap<String, Object>();
		Map<InstanceLifeStateEnum, List<Long>> refreshmap = new HashMap<InstanceLifeStateEnum, List<Long>>();
		int result = 1;
		ResourceInstanceDiscoveryParameter ridp = setDiscoverParamter(paramter);
		ridp.setOnlyDiscover(true);
		long startTime = System.currentTimeMillis();
		DiscoverResourceIntanceResult drir = discoveryService.discoveryResourceInstance(ridp);
		resMap.put("instanceId", instanceId);
		if(drir.isSuccess()){
			try {
				ResourceInstance ri = drir.getResourceIntance();
				ri.setId(instanceId);
				
				refreshmap=resourceInstanceService.refreshResourceInstance(ri, false);
//				List<Long> list = new ArrayList<Long>();
//				list.add((long) 6502);
//				list.add((long) 6501);
//				List<Long> list2 = new ArrayList<Long>();
//				list2.add((long) 6217);
//				list2.add((long) 6218);
//				refreshmap.put(InstanceLifeStateEnum.DELETED, list );
//				refreshmap.put(InstanceLifeStateEnum.REMOVED, list2 );

				// ??????????????????????????????
				collectResourceMetrics(ri);
				// ????????????????????????????????????
				long[] instanceIds = {ri.getId()};
				reAccountInstanceDao.deleteResourceAndAccountRelation(instanceIds);
				// ??????????????????????????????
				insertReAccountInstance(paramter, ri);
				//?????????????????????
				resMap.putAll(setResult(ri, paramter));
				resMap.put("childInstance", getChildInstanceInParentStauts(ri,refreshmap));
			} catch (InstancelibException e) {
				
				result = 2;
			
				resMap.put("childInstance", null);
				logger.error("rediscover refreshResourceInstance error:", e);
			} catch (MetricExecutorException e){
				result = 2;
				resMap.put("childInstance", null);
				logger.error("rediscover collectResourceMetrics error:", e);
			}
		}else{
			logger.error("rediscover refresh error code:" + drir.getCode());
			result = 0;
			resMap.put("childInstance", null);
		}
		resMap.put("result", result);
		resMap.put("time", calcTime(System.currentTimeMillis() - startTime));
		return resMap;
	}

	@Override
	public Map<String, String> refAddMonitor(String newInstanceName,
			Long mainInstanceId, Long[] childInstanceIds,Long[] delchildInstanceIdLong,Long[] cancleInstanceIds) {
		Map<String, String> result = new HashMap<String, String>();
		try {
			// ??????????????????
			updateInstanceName(mainInstanceId, newInstanceName);
			if(Arrays.asList(childInstanceIds)!=null){
				
				profileService.enableMonitor(Arrays.asList(childInstanceIds));//????????????
			}
			if(Arrays.asList(cancleInstanceIds)!=null){
				profileService.cancleMonitor(Arrays.asList(cancleInstanceIds));//????????????
			}
			saveSearchRel(mainInstanceId);
			if(Arrays.asList(delchildInstanceIdLong)!=null){
				resourceInstanceService.removeChildResourceInstance(Arrays.asList(delchildInstanceIdLong));
//				for (Long id : delchildInstanceIdLong) {//????????????????????????????????????
//					resourceInstanceService.removeChildResourceInstance(Arrays.asList(delchildInstanceIdLong));
//				}
			}
			result.put("status", "1");
			logger.error("addIds:" + Arrays.asList(childInstanceIds));
			logger.error("delIds:" + Arrays.asList(cancleInstanceIds));
		} catch (Exception e) {
			result.put("status", "0");
			logger.error("addMonitor", e);
		}
		return result;
	}

	@Override
	public int delResourceInstance(Long instanceId) {
		return 1;
	}
}
