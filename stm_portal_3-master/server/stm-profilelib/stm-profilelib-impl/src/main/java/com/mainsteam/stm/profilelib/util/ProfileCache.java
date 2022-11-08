package com.mainsteam.stm.profilelib.util;

//import java.util.ArrayList;
import java.util.Collection;
import java.util.HashSet;
//import java.util.HashSet;
import java.util.List;

import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import com.mainsteam.stm.cache.IMemcache;
import com.mainsteam.stm.cache.MemCacheFactory;
//import com.mainsteam.stm.profilelib.bean.MetricThresholdKey;
import com.mainsteam.stm.profilelib.bean.ProfileMetricKey;
//import com.mainsteam.stm.profilelib.obj.ProfileInfo;
//import com.mainsteam.stm.profilelib.obj.ProfileInstanceRelation;
import com.mainsteam.stm.profilelib.obj.ProfileMetric;
import com.mainsteam.stm.profilelib.obj.ProfileThreshold;
//import com.mainsteam.stm.profilelib.obj.TimelineInfo;

/**
 * 策略缓存实现类
 */
public class ProfileCache {

	private static final Log logger = LogFactory.getLog(ProfileCache.class);
	
	//private IMemcache<ProfileInfo> profileInfoCache;
	
	private IMemcache<ProfileMetric> profileMetricCache;
	
	private IMemcache<ProfileThreshold> profileThresholdCache;
	
	//private IMemcache<ProfileInstanceRelation> profileInstanceCache;

	//private IMemcache<TimelineInfo> timelineCache;
	
	private IMemcache<ProfileMetricKey> profileMetricKeyCache;
	
	private static final String TIMELINEKEY = "timeline";
	//private IMemcache<>MetricThresholdKey metricThresholdKeyCache;
	
	public ProfileCache(){
		//profileInfoCache = MemCacheFactory.getRemoteMemCache(ProfileInfo.class);
		profileMetricCache = MemCacheFactory.getRemoteMemCache(ProfileMetric.class);
		profileThresholdCache = MemCacheFactory.getRemoteMemCache(ProfileThreshold.class);
		//timelineCache = MemCacheFactory.getRemoteMemCache(TimelineInfo.class);
		profileMetricKeyCache = MemCacheFactory.getRemoteMemCache(ProfileMetricKey.class);
		//metricThresholdKeyCache = MemCacheFactory.getRemoteMemCache(MetricThresholdKey.class);
		if(//profileInfoCache == null 
	             profileMetricCache == null 
				|| profileThresholdCache == null 
				//|| profileInstanceCache == null
				|| profileMetricKeyCache == null
		//		|| metricThresholdKeyCache == null
				){
			if(logger.isWarnEnabled()){
				logger.warn("memcache not load!");
			}
		}
	}
//	public ProfileInfo getProfileInfoByProfileId(long profileId){
//		ProfileInfo profileInfo = null;
//		if(profileInfoCache != null){
//			profileInfo = profileInfoCache.get(String.valueOf(profileId));
//		}
//		return profileInfo;
//	}
	
	public ProfileMetric getProfileMetricBymetricId(long profileId,String metricId){
		ProfileMetric profileMetric = null;
		if(profileMetricCache != null){
			StringBuilder key = new StringBuilder(30);
			key.append(profileId).append(metricId);
			profileMetric = profileMetricCache.get(key.toString());
		}
		return profileMetric;
	}
	
	public ProfileMetric getTimelineMetricBymetricId(long timelineId,String metricId){
		ProfileMetric profileMetric = null;
		if(profileMetricCache != null){
			StringBuilder key = new StringBuilder(50);
			key.append(TIMELINEKEY).append(timelineId).append(metricId);
			profileMetric = profileMetricCache.get(key.toString());
		}
		return profileMetric;
	}
	
	public List<ProfileThreshold> getProfileThresholdBymetricId(long profileId,String metricId){
		List<ProfileThreshold> profileThresholds = null;
		if(profileThresholdCache != null){
			StringBuilder key = new StringBuilder(30);
			key.append(profileId).append(metricId);
			Collection<ProfileThreshold> thresholds = profileThresholdCache.getCollection(key.toString());
			if(thresholds != null){
				profileThresholds = (List<ProfileThreshold>)thresholds;
				if(CollectionUtils.isEmpty(profileThresholds)){
					profileThresholds = null;
				}
			}
		}
		return profileThresholds;
	}
	
	public List<ProfileThreshold> getTimelineThresholdBymetricId(long timelineId,String metricId){
		List<ProfileThreshold> profileThresholds = null;
		if(profileThresholdCache != null){
			StringBuilder key = new StringBuilder(50);
			key.append(TIMELINEKEY).append(timelineId).append(metricId);
			Collection<ProfileThreshold> thresholds = profileThresholdCache.getCollection(key.toString());
			if(thresholds != null){
				profileThresholds = (List<ProfileThreshold>)thresholds;
				if(CollectionUtils.isEmpty(profileThresholds)){
					profileThresholds = null;
				}
			}
		}
		return profileThresholds;
	}
	
	public HashSet<String> getMetricIdByProfileId(long profileId){
		HashSet<String> result = null;
		if(profileMetricKeyCache != null){
			ProfileMetricKey profileMetricKey = profileMetricKeyCache.get(String.valueOf(profileId));
			if(profileMetricKey != null){
				result = profileMetricKey.getMetriceIds();
				if(CollectionUtils.isEmpty(result)){
					result = null;
				}
			}
		}
		return result;
	}
	
	public HashSet<String> getMetricIdByTimelineId(long timelineId){
		HashSet<String> result = null;
		if(profileMetricKeyCache != null){
			StringBuilder keyBuilder = new StringBuilder(30);
			keyBuilder.append(TIMELINEKEY).append(timelineId);
			ProfileMetricKey profileMetricKey = profileMetricKeyCache.get(keyBuilder.toString());
			if(profileMetricKey != null){
				result = profileMetricKey.getMetriceIds();
				if(CollectionUtils.isEmpty(result)){
					result = null;
				}
			}
		}
		return result;
	}
	
//	public void addProfileInstance(ProfileInstanceRelation profileInstance){
//		long profileId = profileInstance.getProfileId();
//		long instnaceId = profileInstance.get
//	}
	
	public void addProfileThreshold(long profileId,String metricId,List<ProfileThreshold> profileThresholds){
		if(profileThresholdCache != null){
			StringBuilder keyBuilder = new StringBuilder(30);
			keyBuilder.append(profileId).append(metricId);
			profileThresholdCache.setCollection(keyBuilder.toString(), profileThresholds);
		}
	}
	
	public void addProfileMetric(ProfileMetric profileMetric){
		if(profileMetricCache != null){
			StringBuilder keyBuilder = new StringBuilder(30);
			keyBuilder.append(profileMetric.getProfileId()).append(profileMetric.getMetricId());
			profileMetricCache.set(keyBuilder.toString(), profileMetric);
		}
	}
	
	public void setProfileMetricKey(long profileId,HashSet<String> metricIds){
		if(profileMetricKeyCache != null){
			//添加策略与指标的关系
			String key = String.valueOf(profileId);
			ProfileMetricKey profileMetricKey = new ProfileMetricKey(metricIds);
			profileMetricKeyCache.set(key, profileMetricKey);
		}
	}
	
	public void setTimelineMetricKey(long timelineId,HashSet<String> metricIds){
		if(profileMetricKeyCache != null){
			//添加策略与指标的关系
			StringBuilder keyBuilder = new StringBuilder(40);
			keyBuilder.append(TIMELINEKEY).append(timelineId);
			ProfileMetricKey profileMetricKey =  new ProfileMetricKey(metricIds);
			profileMetricKeyCache.set(keyBuilder.toString(), profileMetricKey);
		}
	}
	
	public void updateProfileMetric(ProfileMetric profileMetric){
		if(profileMetricCache != null){
			StringBuilder keyBuilder = new StringBuilder(30);
			keyBuilder.append(profileMetric.getProfileId()).append(profileMetric.getMetricId());
			profileMetricCache.set(keyBuilder.toString(), profileMetric);
		}
	}
	
	public void updateProfileThreshold(long profileId,String metricId,List<ProfileThreshold> profileThresholds){
		addProfileThreshold(profileId, metricId, profileThresholds);
	}
	
//	public void addProfileInfo(ProfileInfo profileInfo){
//		if(profileInfoCache != null){
//			String key = String.valueOf(profileInfo.getProfileId());
//			profileInfoCache.set(key, profileInfo);
//		}
//	}
	
//	public void addTimelineInfo(TimelineInfo timeline){
//		if(timelineCache != null){
//			StringBuilder key = new StringBuilder(40);
//			key.append(timeline.getProfileId()).append(timeline.getTimeLineId());
//			timelineCache.set(key.toString(), timeline);
//		}
//	}
	
	public void addTimelineMetric(ProfileMetric profileMetric){
		if(profileMetricCache != null){
			StringBuilder keyBuilder = new StringBuilder(40);
			keyBuilder.append(TIMELINEKEY).append(profileMetric.getTimeLineId()).append(profileMetric.getMetricId());
			String key = keyBuilder.toString();
			profileMetricCache.set(key, profileMetric);
		}
	}

	
	public void updateTimelineMetric(ProfileMetric profileMetric){
		if(profileMetricCache != null){
			StringBuilder keyBuilder = new StringBuilder(30);
			keyBuilder.append(TIMELINEKEY).append(profileMetric.getTimeLineId()).append(profileMetric.getMetricId());
			String key = keyBuilder.toString();
			profileMetricCache.set(key, profileMetric);
		}
	}
	
	public void addTimelineThreshold(long timelineId,String metricId,List<ProfileThreshold> profileThresholds){
		if(profileThresholdCache != null){
			StringBuilder key = new StringBuilder(30);
			key.append(TIMELINEKEY).append(timelineId).append(metricId);
			profileThresholdCache.setCollection(key.toString(), profileThresholds);
		}
	}
	
	public void updateTimelineThreshold(long timelineId,String metricId,List<ProfileThreshold> profileThresholds){
		addTimelineThreshold(timelineId, metricId, profileThresholds);
	}
	
	public void deleteProfileMetric(long profileId,String metricId){
		StringBuilder keyBuilder = new StringBuilder(30);
		keyBuilder.append(profileId).append(metricId);
		profileMetricCache.delete(keyBuilder.toString());
	}
	
	public void deleteProfileThreshold(long profileId,String metricId){
		StringBuilder keyBuilder = new StringBuilder(30);
		keyBuilder.append(profileId).append(metricId);
		profileThresholdCache.delete(keyBuilder.toString());
	}
	
	public void deleteTimelineMetric(long timelineId,String metricId){
		StringBuilder key = new StringBuilder(30);
		key.append(TIMELINEKEY).append(timelineId).append(metricId);
		profileMetricCache.delete(key.toString());
	}
	
	public void deleteTimelineThreshold(long timelineId,String metricId){
		StringBuilder key = new StringBuilder(30);
		key.append(TIMELINEKEY).append(timelineId).append(metricId);
		profileThresholdCache.delete(key.toString());
	}
}
