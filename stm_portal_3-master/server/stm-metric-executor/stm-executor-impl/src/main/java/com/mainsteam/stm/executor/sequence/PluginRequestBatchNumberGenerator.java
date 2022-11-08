/**
 * 
 */
package com.mainsteam.stm.executor.sequence;

import com.mainsteam.stm.pluginserver.constant.PluginRequestEnum;

/**
 * @author ziw
 * 
 */
public class PluginRequestBatchNumberGenerator {

//	private Map<PluginRequestEnum, Number> batchMap;
	private long batch = 0;

	/**
	 * 
	 */
	public PluginRequestBatchNumberGenerator() {
	}

	public void start() {
//		batchMap = new HashMap<PluginRequestEnum,Number>();
	}

	public synchronized long generateBatch(PluginRequestEnum requestType) {
//		if (batchMap.containsKey(requestType)) {
//			return batchMap.get(requestType).next();
//		} else {
//			synchronized (requestType) {
//				if (batchMap.containsKey(requestType)) {
//					return batchMap.get(requestType).next();
//				} else {
//					Number n = new Number();
//					batchMap.put(requestType, n);
//					return n.next();
//				}
//			}
//		}
		return batch++;
	}

//	private class Number {
//		long batch = 0;
//
//		public synchronized long next() {
//			return batch++;
//		}
//	}
}
