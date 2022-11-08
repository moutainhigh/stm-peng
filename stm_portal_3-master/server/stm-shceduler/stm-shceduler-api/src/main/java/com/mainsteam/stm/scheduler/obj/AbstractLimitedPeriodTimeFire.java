/**
 * 
 */
package com.mainsteam.stm.scheduler.obj;

import java.util.Calendar;

/**
 * @author ziw
 * 
 */
public abstract class AbstractLimitedPeriodTimeFire implements
		LimitedPeriodTimeFire {

	private long start;

	private long end;

	private PeriodTimeFire periodTimeFire;

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * com.mainsteam.stm.schceduler.obj.LimitedPeriodTimeFire#setLimitedPeriodTime
	 * (java.util.Date, java.util.Date)
	 */
	@Override
	public void setLimitedPeriodTime(long start, long end) {
		this.start = start;
		this.end = end;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see com.mainsteam.stm.schceduler.obj.LimitedPeriodTimeFire#getStart()
	 */
	@Override
	public long getStart() {
		return this.start;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see com.mainsteam.stm.schceduler.obj.LimitedPeriodTimeFire#getEnd()
	 */
	@Override
	public long getEnd() {
		return this.end;
	}

	public PeriodTimeFire getPeriodTimeFire() {
		return periodTimeFire;
	}

	/**
	 * @param periodTimeFire
	 *            the periodTimeFire to set
	 */
	public final void setPeriodTimeFire(PeriodTimeFire periodTimeFire) {
		this.periodTimeFire = periodTimeFire;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * com.mainsteam.stm.schceduler.obj.LimitedPeriodTimeFire#isValid(java.
	 * util.Date)
	 */
	@Override
	public boolean isValid(long currentTime) {
		Calendar c = Calendar.getInstance();
		return compareDate(c, getStart(), getEnd(), currentTime);
	}

	public abstract void clearField(Calendar c);

	protected boolean compareDate(Calendar c, long a, long b, long currentTime) {
		c.setTimeInMillis(a);
		clearField(c);
		long start = c.getTimeInMillis();

		c.setTimeInMillis(currentTime);
		clearField(c);
		long current = c.getTimeInMillis();
		if (current >= start) {
			c.setTimeInMillis(b);
			clearField(c);
			long end = c.getTimeInMillis();
			if (current <= end) {
				return true;
			}
		}
		return false;
	}
}
