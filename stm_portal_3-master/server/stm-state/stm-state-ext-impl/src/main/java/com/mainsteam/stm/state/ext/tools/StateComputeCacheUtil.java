package com.mainsteam.stm.state.ext.tools;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.TypeReference;
import com.mainsteam.stm.common.metric.obj.MetricStateEnum;
import com.mainsteam.stm.state.ext.process.bean.AlarmStateQueue;
import com.mainsteam.stm.state.obj.InstanceStateData;
import com.mainsteam.stm.state.obj.MetricStateData;
import org.apache.commons.lang.StringUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.stereotype.Component;
import com.mainsteam.stm.cache.IMemcache;
import com.mainsteam.stm.cache.MemCacheFactory;

import java.util.*;

@Component("stateComputeCacheUtil")
public class StateComputeCacheUtil {
	private static final Log logger = LogFactory.getLog(StateComputeCacheUtil.class);

	public StateComputeCacheUtil() {
		if (logger.isInfoEnabled()) {
			logger.info("Starts to init StateCacheUtils...");
		}
	}

	public boolean setInstanceState(String key, InstanceStateData obj) {
		IMemcache<String> client = MemCacheFactory.getRemoteMemCache(String.class);
		String s = JSON.toJSONString(obj);
		key = "InstanceStateData_" + key;
		return client.set(key, s);
	}

	/**
	 * 根据资源实例ID获取资源实例状态缓存
	 * @param instanceID
	 * @return
	 */
	public InstanceStateData getInstanceState(String instanceID) {
		IMemcache<String> cacheClient = MemCacheFactory.getRemoteMemCache(String.class);
		String jsonString = cacheClient.get("InstanceStateData_" + instanceID);
		InstanceStateData instanceStateData = JSON.parseObject(jsonString, InstanceStateData.class);
		return instanceStateData;
	}

	public boolean setAlarmStateQueue(String key, AlarmStateQueue obj) {
		IMemcache<String> cacheClient = MemCacheFactory.getRemoteMemCache(String.class);
		return cacheClient.set(key + "_alarmStateQueue", obj.serialize());
	}

	/**
	 * 根据资源实例ID获取资源实例状态缓存
	 * @param instanceID
	 * @return
	 */
	public AlarmStateQueue getAlarmStateQueue(String instanceID) {
		IMemcache<String> cacheClient = MemCacheFactory.getRemoteMemCache(String.class);
		String str = cacheClient.get(instanceID + "_alarmStateQueue");
		AlarmStateQueue stateQueue = new AlarmStateQueue();
		if(StringUtils.isEmpty(str))
			return stateQueue;

		stateQueue.deserialize(str);
		return stateQueue;
	}

	public boolean removeAlarmStateQueue(String instanceId) {
		IMemcache<String> cacheClient = MemCacheFactory.getRemoteMemCache(String.class);
		return cacheClient.delete(instanceId + "_alarmStateQueue");
	}

	public boolean setMetricState(MetricStateData obj){
		String key =  "MetricStateData_" + obj.getInstanceID() + obj.getMetricID();
		IMemcache<String> cacheClient = MemCacheFactory.getRemoteMemCache(String.class);
		return cacheClient.set(key, JSON.toJSONString(obj));
	}

	public MetricStateData getMetricState(long instanceID,String metricID){
		IMemcache<String> cacheClient = MemCacheFactory.getRemoteMemCache(String.class);
		String key = "MetricStateData_" + instanceID + metricID;
		String jsonString = cacheClient.get(key);
		return JSON.parseObject(jsonString, MetricStateData.class);
	}

	void setAvailabilityMetricData(long instanceID, Map<String, MetricStateEnum> dataMap) {
		if(dataMap != null && !dataMap.isEmpty()) {
			String jsonString = JSON.toJSONString(dataMap);
			IMemcache<String> cacheClient = MemCacheFactory.getRemoteMemCache(String.class);
			String key = String.valueOf(instanceID) + "_metricValue";
			cacheClient.set(key, jsonString);
		}
	}

	boolean setAvailabilityMetricData(long instanceID, String metricID, MetricStateEnum metricData) {
		IMemcache<String> cacheClient = MemCacheFactory.getRemoteMemCache(String.class);
		String key = String.valueOf(instanceID) + "_metricValue";
		String cacheMetricData = cacheClient.get(key);
		Map<String, MetricStateEnum> data;
		if(cacheMetricData != null) {
			data = JSON.parseObject(cacheMetricData, new TypeReference<Map<String, MetricStateEnum>>(){});
		}else {
			data =new HashMap<>(1);
		}
		data.put(metricID, metricData);
		return cacheClient.set(key, JSON.toJSONString(data));
	}

	public Map<String, MetricStateEnum> getAvailabilityMetricData(long instanceID) {
		IMemcache<String> cacheClient = MemCacheFactory.getRemoteMemCache(String.class);
		String metricData = cacheClient.get(String.valueOf(instanceID) + "_metricValue");
		if(null != metricData) {
			return JSON.parseObject(metricData, new TypeReference<Map<String, MetricStateEnum>>(){});
		}else{
			return null;
		}
	}

	public void deleteAvailabilityMetricData(long instanceID) {
		IMemcache<String> cacheClient = MemCacheFactory.getRemoteMemCache(String.class);
		cacheClient.delete(String.valueOf(instanceID) + "_metricValue");
	}

	public void deleteMetricFromAvailabilityMetricData(long instanceID, String metricID) {
		IMemcache<String> cacheClient = MemCacheFactory.getRemoteMemCache(String.class);
		String key = String.valueOf(instanceID) + "_metricValue";
		String metricData = cacheClient.get(key);
		if(null != metricData) {
			Map map = JSON.parseObject(metricData, new TypeReference<Map<String, MetricStateEnum>>(){});
			map.remove(metricID);
			cacheClient.set(key, JSON.toJSONString(map));
		}
	}

	public void deleteInstanceState(String instanceID) {
		IMemcache<String> cacheClient = MemCacheFactory.getRemoteMemCache(String.class);
		cacheClient.delete("InstanceStateData_" + instanceID);
	}

	public void deleteMetricState(long instanceID, String metricID) {
		String key = "MetricStateData_" + instanceID + metricID;
		IMemcache<String> cacheClient = MemCacheFactory.getRemoteMemCache(String.class);
		cacheClient.delete(key);
	}

}

