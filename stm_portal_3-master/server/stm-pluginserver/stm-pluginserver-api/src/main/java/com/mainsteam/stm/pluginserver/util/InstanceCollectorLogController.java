/**
 * 
 */
package com.mainsteam.stm.pluginserver.util;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * @author ziw
 * 
 */
public class InstanceCollectorLogController {

	public static final InstanceCollectorLogController _self = new InstanceCollectorLogController();

	private Map<Long, Long> instanceIdMap;

	/**
	 * 
	 */
	private InstanceCollectorLogController() {
		instanceIdMap = new HashMap<>();
	}

	public static final InstanceCollectorLogController getInstance() {
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
	
	public static void main(String[] args) {
		getInstance().openLog(100);
		System.out.println(getInstance().isLog(100));
	}
}
