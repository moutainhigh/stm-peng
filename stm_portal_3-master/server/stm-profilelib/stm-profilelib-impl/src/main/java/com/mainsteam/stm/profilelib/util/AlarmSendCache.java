package com.mainsteam.stm.profilelib.util;

import com.mainsteam.stm.cache.IMemcache;
import com.mainsteam.stm.cache.MemCacheFactory;
import com.mainsteam.stm.profilelib.alarm.obj.AlarmSendCondition;

/**
 * 告警规则缓存类实现
 * @author xiaoruqiang
 */
@Deprecated
public class AlarmSendCache {

	private IMemcache<AlarmSendCondition> cache;
	
	private static String alarmRuleKeys = "";
	
	public AlarmSendCache(){
		cache = MemCacheFactory.getRemoteMemCache(AlarmSendCondition.class);
	}
	
	public void add(long ruleId){
		
	}
	
}
