/**
 * 
 */
package com.mainsteam.stm.scheduler.obj;

/**
 * @author ziw
 * 
 */
public class SecondPeriodFire implements PeriodTimeFire {

	private long offset;

	private long second;

	/**
	 * 
	 */
	public SecondPeriodFire() {
	}

	/**
	 * @return the second
	 */
	public final long getSecond() {
		return second;
	}

	/**
	 * @param second
	 *            the second to set
	 */
	public final void setSecond(long second) {
		this.second = second;
		offset = 1000 * this.second;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * com.mainsteam.stm.scheduler.obj.PeriodTimeFire#getNextTimeFire(java.
	 * util.Date)
	 */
	@Override
	public long getNextTimeFire(long lastTime) {
		// Date nextDate = new Date(lastTime.getTime() + offset);
		return lastTime + offset;
	}

	@Override
	public boolean equals(PeriodTimeFire p) {
		return p instanceof SecondPeriodFire
				&& this.second == ((SecondPeriodFire) p).second;
	}
}
