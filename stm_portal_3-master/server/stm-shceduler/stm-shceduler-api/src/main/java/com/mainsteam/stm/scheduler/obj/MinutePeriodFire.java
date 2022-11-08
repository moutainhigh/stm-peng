/**
 * 
 */
package com.mainsteam.stm.scheduler.obj;


/**
 * @author ziw
 *
 */
public class MinutePeriodFire implements PeriodTimeFire {

	private int minute;
	
	/**
	 * 
	 */
	public MinutePeriodFire() {
	}
	
	/**
	 * @return the minute
	 */
	public final int getMinute() {
		return minute;
	}



	/**
	 * @param minute the minute to set
	 */
	public final void setMinute(int minute) {
		this.minute = minute;
	}



	/* (non-Javadoc)
	 * @see com.mainsteam.stm.schceduler.obj.PeriodTimeFire#getNextTimeFire(java.util.Date)
	 */
	@Override
	public long getNextTimeFire(long lastTime) {
		long offset = 60000*this.minute;
		return lastTime+offset;
	}
	
	@Override
	public boolean equals(PeriodTimeFire p) {
		return p instanceof MinutePeriodFire
				&& this.minute == ((MinutePeriodFire) p).minute;
	}
}
