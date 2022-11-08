/**
 * 
 */
package com.mainsteam.stm.scheduler.obj;


/**
 * 周期性的时间计算接口
 * 
 * @author ziw
 * 
 */
public interface PeriodTimeFire {
	/**
	 * 获取下一次的调度时间
	 * 
	 * @param lastTime
	 *            上一次的调度时间
	 * @return Date 下一次的调度时间
	 */
	public long getNextTimeFire(long lastTime);
	public boolean equals(PeriodTimeFire p);
}
