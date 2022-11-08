/**
 * 
 */
package com.mainsteam.stm.executor.sequence;


/**
 * @author ziw
 * 
 */
public class PluginRequestIdGenerator {

	private long requestId;

	/**
	 * 
	 */
	public PluginRequestIdGenerator() {
	}

	public void start() {
		requestId = 0;
	}

	public synchronized long generate() {
		return requestId++;
	}
}
