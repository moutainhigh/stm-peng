/**
 * 
 */
package com.mainsteam.stm.node;

import java.io.Serializable;
import java.util.Map;

/**
 * @author ziw
 *
 */
public class NodeState implements Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = -7443669228555929384L;
	private boolean running = true;
	private Map<String,Serializable> statesMap;
	/**
	 * 
	 */
	public NodeState() {
	}

	public boolean isRunning() {
		return running;
	}

	public void setRunning(boolean running) {
		this.running = running;
	}

	public Map<String, Serializable> getStatesMap() {
		return statesMap;
	}

	public void setStatesMap(Map<String, Serializable> statesMap) {
		this.statesMap = statesMap;
	}
}
