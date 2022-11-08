/**
 * 
 */
package com.mainsteam.stm.scheduler;

import com.mainsteam.stm.scheduler.obj.PeriodTimeFire;
import com.mainsteam.stm.scheduler.obj.SecondPeriodFire;

/**
 * @author ziw
 * 
 */
public class SecondPeriodTimeFire implements PeriodTimeFire {

	private int secondMillons;

	/**
	 * 
	 */
	public SecondPeriodTimeFire(int second) {
		this.secondMillons = second * 1000;
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
		return lastTime + secondMillons;
	}

	@Override
	public boolean equals(PeriodTimeFire p) {
		return p instanceof SecondPeriodFire
				&& this.secondMillons == ((SecondPeriodTimeFire) p).secondMillons;
	}
}
