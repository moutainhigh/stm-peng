package com.mainsteam.stm.state.util;

import com.alibaba.fastjson.JSONObject;
import com.mainsteam.stm.cache.IMemcache;
import com.mainsteam.stm.cache.MemCacheFactory;
import com.mainsteam.stm.state.obj.AvailabilityMetricData;
import com.mainsteam.stm.state.obj.InstanceStateData;
import com.mainsteam.stm.state.obj.MetricStateData;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import java.util.HashMap;
import java.util.Map;

public class StateCacheUtils {
	private static final Log logger = LogFactory.getLog(StateCacheUtils.class);
	private IMemcache<InstanceStateData> instanceStateDataIMemcache; //资源状态数据缓存
	private IMemcache<MetricStateData> metricStateDataIMemcache;// 指标状态数据缓存
	private IMemcache<AvailabilityMetricData> availabilityMetricDataIMemcache; //可用性指标值状态，用于能力库计算资源状态

	public StateCacheUtils() {
		if (logger.isInfoEnabled()) {
			logger.info("Starts to init StateCacheUtils...");
		}
		instanceStateDataIMemcache = (IMemcache<InstanceStateData>) getIMemcache(InstanceStateData.class, true);
		metricStateDataIMemcache = (IMemcache<MetricStateData>) getIMemcache(MetricStateData.class, true);
		availabilityMetricDataIMemcache = (IMemcache<AvailabilityMetricData>) getIMemcache(AvailabilityMetricData.class, true);

	}

	private Object getIMemcache(Class objClass, boolean isCreateNew){
		switch (objClass.getSimpleName()){
			case "InstanceStateData":
				if(isCreateNew || null == instanceStateDataIMemcache){
					instanceStateDataIMemcache = MemCacheFactory.getRemoteMemCache(objClass);
					if (logger.isDebugEnabled())
						logger.debug("instanceStateDataIMemcache validation is " + instanceStateDataIMemcache.isActivate());
				}
				return instanceStateDataIMemcache;

			case "MetricStateData":
				if(isCreateNew || null == metricStateDataIMemcache){
					metricStateDataIMemcache = MemCacheFactory.getRemoteMemCache(objClass);
					if (logger.isDebugEnabled())
						logger.debug("metricStateDataIMemcache validation is " + metricStateDataIMemcache.isActivate());
				}
				return metricStateDataIMemcache;

			case "AvailabilityMetricData":
				if(isCreateNew || null == availabilityMetricDataIMemcache){
					availabilityMetricDataIMemcache = MemCacheFactory.getRemoteMemCache(objClass);
					if (logger.isDebugEnabled())
						logger.debug("availabilityMetricDataIMemcache validation is " + availabilityMetricDataIMemcache.isActivate());
				}
				return availabilityMetricDataIMemcache;
		}
		return null;
	}


	public boolean setInstanceState(InstanceStateData obj) {

		InstanceStateData instanceStateData = getInstanceState(obj.getInstanceID());
		if(instanceStateData !=null) {
			boolean updated = instanceStateDataIMemcache.update(String.valueOf(obj.getInstanceID()), obj, 60*60);
			if(!updated) {
				if(!instanceStateDataIMemcache.isActivate()){
					instanceStateDataIMemcache = (IMemcache<InstanceStateData>) getIMemcache(InstanceStateData.class, true);
				}
				boolean isSuccess = instanceStateDataIMemcache.update(String.valueOf(obj.getInstanceID()), obj, 60*60);
				if(!isSuccess){
					if(logger.isWarnEnabled()) {
						logger.warn("failed to update instance state cache, try to cache again. " + obj);
					}
				}
				return isSuccess;
			}
			return updated;
		}else {
			boolean flag = instanceStateDataIMemcache.set(String.valueOf(obj.getInstanceID()), obj, 60*60);
			if(!flag){
				if(instanceStateDataIMemcache.isActivate()) {
					if(logger.isWarnEnabled()) {
						logger.warn("Failed to cached instance state " + obj);
					}
				}else {
					instanceStateDataIMemcache = (IMemcache<InstanceStateData>) getIMemcache(InstanceStateData.class, true);
					boolean finalFlag = instanceStateDataIMemcache.set(String.valueOf(obj.getInstanceID()), obj, 60*60);
					if(!finalFlag) {
						if (logger.isWarnEnabled()) {
							logger.warn("retry to cached instance state, but still failed " + obj);
						}
					}
					return finalFlag;
				}
			}
			return flag;
		}
	}

	/**
	 * 根据资源实例ID获取资源实例状态缓存
	 * @param instanceID
	 * @return
	 */
	public InstanceStateData getInstanceState(Long instanceID) {
		if(null == instanceStateDataIMemcache){
			instanceStateDataIMemcache = (IMemcache<InstanceStateData>) getIMemcache(InstanceStateData.class, true);
		}
		InstanceStateData instanceStateData = instanceStateDataIMemcache.get(String.valueOf(instanceID));
		if(null == instanceStateData) {
			if(instanceStateDataIMemcache.isActivate())
				return null;
			else {
				if(logger.isInfoEnabled()) {
					logger.info("retry to obtain instanceStateDataIMemcache while querying cache,using " + instanceID);
				}
				instanceStateDataIMemcache = (IMemcache<InstanceStateData>) getIMemcache(InstanceStateData.class, true);
			}
			return instanceStateDataIMemcache.get(String.valueOf(instanceID));
		}else {
			return instanceStateData;
		}

	}

	public boolean setMetricState(MetricStateData obj){

		StringBuffer stringBuffer = new StringBuffer();
		stringBuffer.append(obj.getInstanceID()).append(obj.getMetricID());
		String key = stringBuffer.toString();

		MetricStateData metricStateData = getMetricState(obj.getInstanceID(), obj.getMetricID());
		if(null != metricStateData) {
			boolean updated = metricStateDataIMemcache.update(key, obj, 60*60);
			if(!updated) {
				if(!metricStateDataIMemcache.isActivate())
					metricStateDataIMemcache = (IMemcache<MetricStateData>) getIMemcache(MetricStateData.class, true);
				boolean isSuccess = metricStateDataIMemcache.update(key, obj, 60*60);
				if(!isSuccess) {
					if(logger.isWarnEnabled()) {
						logger.warn("failed to update metric state cache, try to cache again. " + obj);
					}
				}
				return isSuccess;
			}
			return updated;
		}else {
			boolean flag = metricStateDataIMemcache.set(stringBuffer.toString(), obj, 60*60);
			if(!flag){
				if(metricStateDataIMemcache.isActivate()) {
					if(logger.isWarnEnabled()) {
						logger.warn("Failed to cached metric state " + obj);
					}
				}else {
					metricStateDataIMemcache = (IMemcache<MetricStateData>) getIMemcache(MetricStateData.class, true);
					boolean finalFlag = metricStateDataIMemcache.set(stringBuffer.toString(), obj, 60*60);
					if(!finalFlag) {
						if(logger.isWarnEnabled()) {
							logger.warn("retry to cached metric state, but still failed, " + obj);
						}
					}
					return finalFlag;
				}
			}
			return flag;

		}
	}

	public MetricStateData getMetricState(long instanceID,String metricID){
		if(null == metricStateDataIMemcache)
			metricStateDataIMemcache = (IMemcache<MetricStateData>) getIMemcache(MetricStateData.class, true);
		StringBuffer stringBuffer = new StringBuffer();
		stringBuffer.append(instanceID).append(metricID);
		MetricStateData metricStateData = metricStateDataIMemcache.get(stringBuffer.toString());
		if(null == metricStateData) {
			if(metricStateDataIMemcache.isActivate())
				return null;
			else {
				if(logger.isInfoEnabled()) {
					StringBuffer sb = new StringBuffer();
					sb.append("retry to obtain metricStateDataIMemcache while querying cache,using {");
					sb.append(instanceID).append(":");
					sb.append(metricID).append("}");
					logger.info(sb.toString());
				}
				metricStateDataIMemcache = (IMemcache<MetricStateData>) getIMemcache(MetricStateData.class, true);
				return metricStateDataIMemcache.get(stringBuffer.toString());
			}
		}else {
			return metricStateData;
		}

	}

	public boolean setAvailabilityMetricDataIMemcache(long instanceID, String metricID, String metricData) {

		AvailabilityMetricData availabilityMetricData = availabilityMetricDataIMemcache.get(String.valueOf(instanceID));
		if(availabilityMetricData == null) {
			availabilityMetricData = new AvailabilityMetricData();
			availabilityMetricData.setInstanceID(instanceID);
			Map metricMap = new HashMap(2);
			metricMap.put(metricID, metricData);
			availabilityMetricData.setMetricData(metricMap);
		}else {
			Map mapMetric = availabilityMetricData.getMetricData();
			mapMetric.put(metricID, metricData);
			availabilityMetricData.setMetricData(mapMetric);
		}
		boolean flag = availabilityMetricDataIMemcache.set(String.valueOf(instanceID), availabilityMetricData);
		if(!flag) {
			if(!availabilityMetricDataIMemcache.isActivate())
				availabilityMetricDataIMemcache = (IMemcache<AvailabilityMetricData>) getIMemcache(AvailabilityMetricData.class, true);
			boolean second = availabilityMetricDataIMemcache.set(String.valueOf(instanceID), availabilityMetricData);
			if(!second) {
				if(logger.isWarnEnabled()) {
					logger.warn("availabilityMetricDataIMemcache set value failed " + JSONObject.toJSONString(availabilityMetricData));
				}
			}
			return second;
		}
		return flag;
	}

	public AvailabilityMetricData getAvailabilityMetricDataIMemcache(long instanceID) {
		if(null == availabilityMetricDataIMemcache)
			availabilityMetricDataIMemcache = (IMemcache<AvailabilityMetricData>) getIMemcache(AvailabilityMetricData.class, true);
		AvailabilityMetricData availabilityMetricData = availabilityMetricDataIMemcache.get(String.valueOf(instanceID));
		if(null == availabilityMetricData) {
			if(availabilityMetricDataIMemcache.isActivate())
				return null;
			else {
				if(logger.isInfoEnabled() ) {
					logger.info("retry to obtain availabilityMetricDataIMemcache while querying cache,using " + instanceID);
				}
				availabilityMetricDataIMemcache = (IMemcache<AvailabilityMetricData>) getIMemcache(AvailabilityMetricData.class, true);
				return availabilityMetricDataIMemcache.get(String.valueOf(instanceID));
			}
		}else {
			return availabilityMetricData;
		}

	}

	public void deleteAvailabilityMetricDataIMemcache(long instanceID) {
		if(availabilityMetricDataIMemcache != null && availabilityMetricDataIMemcache.isActivate()) {
			availabilityMetricDataIMemcache.delete(String.valueOf(instanceID));
		}else {
			if(logger.isWarnEnabled())
				logger.warn("availabilityMetricDataIMemcache is invalid while remove cache using " + instanceID);
		}
	}

	public void deleteMetricFromAvailabilityMetricDataIMemcache(long instanceID, String metricID) {
		if(availabilityMetricDataIMemcache != null && availabilityMetricDataIMemcache.isActivate()) {
			AvailabilityMetricData availabilityMetricData = getAvailabilityMetricDataIMemcache(instanceID);
			if(availabilityMetricData !=null) {
				availabilityMetricData.getMetricData().remove(metricID);
				availabilityMetricDataIMemcache.set(String.valueOf(instanceID), availabilityMetricData);
			}
		}else {
			if(logger.isWarnEnabled()){
				StringBuffer stringBuffer = new StringBuffer();
				stringBuffer.append("availabilityMetricDataIMemcache is invalid while removing, cache using {");
				stringBuffer.append(instanceID).append(":");
				stringBuffer.append(metricID).append("}");
				logger.warn(stringBuffer.toString());
			}
		}
	}

	public void deleteInstanceState(Long instanceID) {
		if(instanceStateDataIMemcache != null && instanceStateDataIMemcache.isActivate())
			instanceStateDataIMemcache.delete(String.valueOf(instanceID));
		else {
			if(logger.isWarnEnabled())
				logger.warn("instanceStateDataIMemcache is invalid while removing cache, using " + instanceID);
		}
	}

	public void deleteMetricState(long instanceID, String metricID) {
		if(metricStateDataIMemcache != null){
			StringBuffer stringBuffer = new StringBuffer();
			stringBuffer.append(instanceID).append(metricID);
			metricStateDataIMemcache.delete(stringBuffer.toString());
		}else {
			if (logger.isWarnEnabled()){
				StringBuffer stringBuffer = new StringBuffer();
				stringBuffer.append("metricStateDataIMemcache is invalid while removing cache, using {");
				stringBuffer.append(instanceID).append(":");
				stringBuffer.append(metricID).append("}");
				logger.warn(stringBuffer.toString());
			}
		}
	}

}
