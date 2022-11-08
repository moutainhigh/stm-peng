package com.mainsteam.stm.instancelib.util;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import com.mainsteam.stm.caplib.CapacityService;
import com.mainsteam.stm.caplib.common.CategoryDef;
import com.mainsteam.stm.caplib.dict.CapacityConst;
import com.mainsteam.stm.caplib.dict.MetricIdConsts;
import com.mainsteam.stm.instancelib.bean.TempCacheValue;
import com.mainsteam.stm.instancelib.obj.ResourceInstance;
import com.mainsteam.stm.instancelib.objenum.InstanceLifeStateEnum;
import com.mainsteam.stm.instancelib.service.bean.ComparerResult;
import com.mainsteam.stm.util.ResourceOrMetricConst;

/**
 * 资源实例重复验证类
 * 
 * @author shaw
 * 
 */
public class ResourceComparer {

	private static final Log logger = LogFactory.getLog(ResourceComparer.class);

	private CapacityService capacityService;

	public void setCapacityService(CapacityService capacityService) {
		this.capacityService = capacityService;
	}
	
	/**
	 * 两个设备mac地址相同的比例最小值。0.7 表示70%;
	 */
	private static float SAME_PROPORTION = 0.7f;

	public ComparerResult isSameDevice(ResourceInstance newResourceInstance){
		ComparerResult result = null;
		CategoryDef category = capacityService
				.getCategoryById(newResourceInstance.getCategoryId());
		if(category.getParentCategory() == null){
			//如果没有2层结构，默认用md5去判断，比如说链路
			result = isSameForOtherDevice(newResourceInstance);
			return result;
		}
		/*
		 * 只判断设备，设备下的子设备不判断
		 */
		switch (category.getParentCategory().getId()) {
			case CapacityConst.NETWORK_DEVICE:
			case CapacityConst.SNMPOTHERS:
				result = isSameForNetrowkDevice(newResourceInstance);
				break;
			/*
			 * 主机
			 */
			default:
				result = isSameForHostDevice(newResourceInstance);
				break;
		}
		return result;
	}
	
	public ComparerResult isSameForNetrowkDevice(ResourceInstance newResourceInstance){
		ComparerResult result = null;
		String[] old_sysoids = newResourceInstance.getModulePropBykey(ResourceOrMetricConst.RESOURCE_SYSOID);
		if(old_sysoids != null){
			List<TempCacheValue> v = UniqueInstanceCache.getInstanceCache().getNetworkUnique(old_sysoids[0]);
			if(v == null){
				if(logger.isInfoEnabled()){
					StringBuilder b = new StringBuilder(100);
					b.append("not found sysoid of network device for catetoryId=");
					b.append(newResourceInstance.getCategoryId());
					b.append(" name=").append(newResourceInstance.getName());
					b.append(" sysoid=").append(old_sysoids[0]);
					b.append(" network comparer is false");
					logger.info(b);
				}
				return result;
			}
			List<Long> repeatIds = new ArrayList<Long>();
			for (TempCacheValue tempCacheValue : v) {
				/**
				 * 同一类型判断重复
				 */
				//计算mac
				boolean mac = macComparer(newResourceInstance, tempCacheValue);
				if(!mac){
					continue;
				}
				//计算ip
				boolean ip = ipComparer(newResourceInstance, tempCacheValue);
				if(ip){
					repeatIds.add(tempCacheValue.getInstanceId());
				}
			}
			if(CollectionUtils.isNotEmpty(repeatIds)){
				result = new ComparerResult();
				result.setRepeatIds(repeatIds);
			}
		} else {
			// sysoid 为空 直接判断设备不是重复。
			if(logger.isInfoEnabled()){
				StringBuilder b = new StringBuilder(100);
				b.append("not found sysoid of network device for catetoryId=");
				b.append(newResourceInstance.getCategoryId());
				b.append(" name=").append(newResourceInstance.getName());
				b.append(" network comparer is false");
				logger.info(b);
			}
		}
		return result;
	}
	
	public ComparerResult isSameForHostDevice(ResourceInstance newResourceInstance){
		ComparerResult result = null;
		List<TempCacheValue> v = UniqueInstanceCache.getInstanceCache().getHostUnique(newResourceInstance.getCategoryId());
		if(v == null){
			if(logger.isInfoEnabled()){
				StringBuilder b = new StringBuilder("100");
				b.append("not found host device for catetoryId=");
				b.append(newResourceInstance.getCategoryId());
				b.append(" host comparer is false");
				logger.info(b);
			}
			return result;
		}
		
		List<Long> repeatIds = new ArrayList<Long>();
		for (TempCacheValue tempCacheValue : v) {
			/**
			 * 同一类型判断重复
			 */
			//计算mac
			boolean mac = macComparer(newResourceInstance, tempCacheValue);
			if(!mac){
				continue;
			}
			//计算ip
			boolean ip = ipComparer(newResourceInstance, tempCacheValue);
			if(ip){
				repeatIds.add(tempCacheValue.getInstanceId());
			}
		}
		if(CollectionUtils.isNotEmpty(repeatIds)){
			result = new ComparerResult();
			result.setRepeatIds(repeatIds);
		}
		return result;
	}
	
	public ComparerResult isSameForOtherDevice(ResourceInstance newResourceInstance){
		ComparerResult result = null;
		HashSet<Long> v = UniqueInstanceCache.getInstanceCache().getUnique(newResourceInstance);
		if(v != null){
			result = new ComparerResult();
			result.setRepeatIds(new ArrayList<Long>(v));
		} 
		return result;
	}

	private boolean macComparer(ResourceInstance newResourceInstance,TempCacheValue cahceValue ){
		boolean result = false;
		String[] oldMacList = cahceValue.getMac();
		String[] newMacList = newResourceInstance
				.getModulePropBykey(MetricIdConsts.METRIC_MACADDRESS);
		if (oldMacList == null || newMacList == null) {
			if (logger.isInfoEnabled()) {
				if (oldMacList == null) {
					logger.info("oldMacList:null instanceId=" + cahceValue.getInstanceId());
				}
				if (newMacList == null) {
					logger.info("newMacList:null name=" + newResourceInstance.getName());
				}
				logger.info("result:" + result);
			}
			return result;
		}
		return proportionComparer(oldMacList,newMacList);
	}
	
	private boolean ipComparer(ResourceInstance newResourceInstance,TempCacheValue cahceValue ){
		boolean result = false;
		String[] oldIPList = cahceValue.getIp();
		String[] newIPList = newResourceInstance
				.getModulePropBykey(MetricIdConsts.METRIC_IP);
		if (oldIPList == null || newIPList == null) {
			if (logger.isInfoEnabled()) {
				if (oldIPList == null) {
					logger.info("oldIPList:null instanceId=" + cahceValue.getInstanceId());
				}
				if (newIPList == null) {
					logger.info("newIPList:null name=" + newResourceInstance.getName());
				}
				logger.info("result:" + result);
			}
			return result;
		}
		return proportionComparer(oldIPList,newIPList);
	}
	
	private boolean proportionComparer(String[] oldList,String[] newList){
		boolean result = false;
		/*
		 * （交集数量/最多数） > 规定的比例 表示一个设备
		 */
		int maxSize = 0;
		int oldSize = oldList.length;
		int newSize = newList.length;
		if (oldSize >= newSize) {
			maxSize = oldSize;
		} else {
			maxSize = newSize;
		}
		List<String> oldValue = Arrays.asList(oldList);
		List<String> newValue = Arrays.asList(newList);
		// 取两个集合的交集
		Collection<?> intersection = CollectionUtils.intersection(oldValue,newValue);
		float actualMacProportion = (float) intersection.size() / maxSize;
		/*
		 * 交集个数如果大于或等于定义的比例 表示同一设备，否则不是同一设备
		 */
		if (actualMacProportion >= SAME_PROPORTION || intersection.size() >= oldSize || intersection.size() >= newSize) {
			result = true;
		}
		if (logger.isInfoEnabled()) {
			StringBuilder b = new StringBuilder(100);
		    b.append("\n old: " + oldValue);
			b.append("\n new: " + newValue);
			b.append("\n actualProportion: " + actualMacProportion);
			b.append("\n result: " + result);
			logger.info(b);
		}
		return result;
	}

	private boolean notContainsMAC(ResourceInstance oldResourceInstance,
			ResourceInstance newResourceInstance) {
		boolean isSame = false;
		if (!oldResourceInstance.getResourceId().equals(
				newResourceInstance.getResourceId())) {
			if (logger.isInfoEnabled()) {
				logger.info("oldResourceId:" + oldResourceInstance.getResourceId());
				logger.info("newResourceId:" + newResourceInstance.getResourceId());
				logger.info("isSame:" + isSame);
			}
			return isSame;
		}
		String[] oldModuleProps = oldResourceInstance.getModulePropBykey(ResourceOrMetricConst.INST_IDENTY_KEY);

		String[] newModuleProps = newResourceInstance.getModulePropBykey(ResourceOrMetricConst.INST_IDENTY_KEY);

		if (oldModuleProps == null || oldModuleProps.length == 0) {
			if (logger.isInfoEnabled()) {
				logger.info("oldModuleProps:null");
				logger.info("isSame:" + isSame);
			}
			return isSame;
		}
		if (newModuleProps == null || newModuleProps.length == 0) {
			if (logger.isInfoEnabled()) {
				logger.info("newModuleProps:null");
				logger.info("isSame:" + isSame);
			}
			return isSame;
		}
		/*
		 * 获取模型属性MD5的值，MD5的值一样，表示同一个设备
		 */
		String oldMD5Value = oldModuleProps[0];
		String newMD5Value = newModuleProps[0];
		
		if (oldMD5Value.equals(newMD5Value)) {
			isSame = true;
		}
		if (logger.isInfoEnabled()) {
			logger.info("oldMD5Value:" + oldMD5Value);
			logger.info("newMD5Value:" + newMD5Value);
			logger.info("isSame:" + isSame);
		}
		return isSame;
	}

	public String getChildResourceInstanceMd5Str(ResourceInstance resourceInstance){
		StringBuilder resourceInstanceMd5Str = new StringBuilder();
		String[] oldModuleProps = resourceInstance.getModulePropBykey(ResourceOrMetricConst.INST_IDENTY_KEY);
		if(oldModuleProps!=null && oldModuleProps.length>0){
			resourceInstanceMd5Str.append(oldModuleProps[0]);
		}
		return resourceInstanceMd5Str.toString();
	}
	
	public String getChildResourceInstanceMd5Str(List<ResourceInstance> resourceInstances,Map<String, Long> md5forid){
		StringBuffer resourceInstanceMd5Str = new StringBuffer();
		for (ResourceInstance resourceInstance : resourceInstances) {
			String[] oldModuleProps = resourceInstance.getModulePropBykey(ResourceOrMetricConst.INST_IDENTY_KEY);
			if(oldModuleProps!=null && oldModuleProps.length>0){
				resourceInstanceMd5Str.append("/|-|\\").append(oldModuleProps[0]);
				md5forid.put(oldModuleProps[0], resourceInstance.getId());
			}
		}
		return resourceInstanceMd5Str.toString();
	}
	
	public ComparerResult childResourceInstanceSameDevice(
			List<ResourceInstance> oldResourceInstance,
			ResourceInstance newResourceInstance) {
		ComparerResult comparerResult = new ComparerResult();
		/*
		 *除进程外，其余的子资源跟父资源的应用，中间件。(页面修改)
		 *判断类型方法一样（MD5内容判断）是否同一个实例
		 */
		Iterator<ResourceInstance> insIterator_it = oldResourceInstance.iterator();
		while(insIterator_it.hasNext()){
			ResourceInstance resourceInstance = insIterator_it.next();
			boolean isSame = notContainsMAC(resourceInstance,newResourceInstance);
			if(logger.isInfoEnabled()){
				StringBuilder sb = new StringBuilder(200);
				sb.append("old:instanceId=").append(resourceInstance.getId());
				sb.append(" instanceName=").append(resourceInstance.getName());
				sb.append(" old childType=").append(resourceInstance.getChildType());
				logger.info(sb);
				sb = new StringBuilder(200);
				sb.append("new:instanceName=");
				sb.append(resourceInstance.getName());
				sb.append(" validate childType=").append(resourceInstance.getChildType());
				logger.info(sb);
			}
			if (isSame) {
				comparerResult.setInstanceId(resourceInstance.getId());
				comparerResult.setLifeState(resourceInstance.getLifeState());
				if (resourceInstance.getLifeState() == InstanceLifeStateEnum.DELETED) {
					//已删除的设备
					continue;
				}
				oldResourceInstance.remove(resourceInstance);
				break;
			}
		}
		return comparerResult;
	}
	
	public boolean childResourceInstanceSameDevice(
			String oldResourceInstanceMd5Str,
			String validateInstanceMd5Str) {
		boolean isSame = false;
		if(validateInstanceMd5Str.isEmpty()){
			isSame=false;
		}else{
			isSame = oldResourceInstanceMd5Str.contains(validateInstanceMd5Str);
		}
		return isSame;
	}

}
