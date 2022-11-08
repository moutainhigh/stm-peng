/**
 * 
 */
package com.mainsteam.stm.scheduler.obj;

import java.util.Calendar;

/**
 * @author ziw
 *
 */
public class WeeklyLimitedPeriodTimeFire extends AbstractLimitedPeriodTimeFire {

	/**
	 * 
	 */
	public WeeklyLimitedPeriodTimeFire() {
	}

	/* (non-Javadoc)
	 * @see com.mainsteam.stm.scheduler.obj.AbstractLimitedPeriodTimeFire#clearField(java.util.Calendar)
	 */
	@Override
	public void clearField(Calendar c) {
		c.set(Calendar.YEAR, 0);
		c.set(Calendar.MONTH, 0);
		c.set(Calendar.WEEK_OF_MONTH, 0);
	}
}
