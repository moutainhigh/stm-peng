package com.mainsteam.stm.dataprocess;

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

public class InstanceSyncUtils {

	final static Map<Long,Long> syncMap=Collections.synchronizedMap(new HashMap<Long,Long>());
	
	public static Long getSyncObj(Long key){
		Long syncObj=syncMap.get(key);
		if(syncObj==null){
			synchronized(syncMap){
				syncObj=syncMap.get(key);
				if(syncObj==null){
					syncObj=key;
					syncMap.put(key, syncObj);
				}
			}
		}
		return syncObj;
	}
	
}
