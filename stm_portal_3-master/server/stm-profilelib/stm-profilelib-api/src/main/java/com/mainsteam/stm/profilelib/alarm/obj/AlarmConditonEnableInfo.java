/**
 * 
 */
package com.mainsteam.stm.profilelib.alarm.obj;

/**
 * @author ziw
 *
 */
public class AlarmConditonEnableInfo extends AlarmConditonQuery {

	private boolean enabled;
	
	/**
	 * 
	 */
	public AlarmConditonEnableInfo() {
	}

	/**
	 * @return the enabled
	 */
	public final boolean isEnabled() {
		return enabled;
	}

	/**
	 * @param enabled the enabled to set
	 */
	public final void setEnabled(boolean enabled) {
		this.enabled = enabled;
	}
}
