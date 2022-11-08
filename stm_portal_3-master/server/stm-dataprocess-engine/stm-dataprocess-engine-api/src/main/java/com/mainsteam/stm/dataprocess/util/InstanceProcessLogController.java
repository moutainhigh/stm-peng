/**
 * 
 */
package com.mainsteam.stm.dataprocess.util;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * @author Xiao Ruqiang
 */
public class InstanceProcessLogController {

	public static final InstanceProcessLogController _self = new InstanceProcessLogController();

	private Map<Long, Long> instanceIdMap;

	/**
	 * 
	 */
	private InstanceProcessLogController() {
		instanceIdMap = new HashMap<>();
	}

	public static final InstanceProcessLogController getInstance() {
		return _self;
	}

	public List<Long> getInstanceIds() {
		return new ArrayList<>(instanceIdMap.values());
	}

	public synchronized void openLog(long instanceId) {
		Long id = instanceId;
		instanceIdMap.put(id, id);
	}

	public synchronized void closeLog(long instanceId) {
		Long id = instanceId;
		instanceIdMap.remove(id);
	}

	public synchronized void closeAllLog() {
		instanceIdMap.clear();
	}

	public boolean isLog(long instanceId) {
		return instanceIdMap.size()> 0 && instanceIdMap.containsKey(instanceId);
	}
	
}
