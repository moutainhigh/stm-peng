/**
 * 
 */
package com.mainsteam.stm.scheduler.obj;

import java.util.Calendar;

/**
 * @author ziw
 * 
 */
public class DailyLimitedPeriodTimeFire extends AbstractLimitedPeriodTimeFire {

	/**
	 * 
	 */
	public DailyLimitedPeriodTimeFire() {
	}


	public void clearField(Calendar c) {
		c.set(Calendar.YEAR, 0);
		c.set(Calendar.MONTH, 0);
		c.set(Calendar.DAY_OF_MONTH, 0);
	}
}
