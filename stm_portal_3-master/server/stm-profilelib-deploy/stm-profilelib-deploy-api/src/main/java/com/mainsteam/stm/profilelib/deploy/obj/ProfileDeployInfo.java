/**
 * 
 */
package com.mainsteam.stm.profilelib.deploy.obj;

import java.io.Serializable;

/**
 * @author ziw
 *
 */
public class ProfileDeployInfo implements Serializable {
	
	/**
	 * 
	 */
	private static final long serialVersionUID = 7977169995855597164L;
	

	private String action;
	
	private Serializable deployData;
	
	/**
	 * 
	 */
	public ProfileDeployInfo() {
	}

	/**
	 * @return the action
	 */
	public final String getAction() {
		return action;
	}

	/**
	 * @param action the action to set
	 */
	public final void setAction(String action) {
		this.action = action;
	}

	/**
	 * @return the deployData
	 */
	public final Serializable getDeployData() {
		return deployData;
	}

	/**
	 * @param deployData the deployData to set
	 */
	public final void setDeployData(Serializable deployData) {
		this.deployData = deployData;
	}
}
