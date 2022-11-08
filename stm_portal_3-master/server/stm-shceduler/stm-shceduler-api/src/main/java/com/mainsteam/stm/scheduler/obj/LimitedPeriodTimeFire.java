/**
 * 
 */
package com.mainsteam.stm.scheduler.obj;


/**
 * 受限的周期性的时间计算接口
 * 
 * @author ziw
 * 
 */
public interface LimitedPeriodTimeFire {
	/**
	 * 设置有效的时间段
	 * 
	 * @param start
	 *            开始时间
	 * @param end
	 *            结束时间
	 */
	public void setLimitedPeriodTime(long start, long end);

	/**
	 * 判断当前时间是否有效
	 * 
	 * @param currentTime
	 *            当前时间
	 * @return true:有效，false:无效
	 */
	public boolean isValid(long currentTime);

	/**
	 * 获取开始时间
	 * 
	 * @return
	 */
	public long getStart();

	/**
	 * 获取结束时间
	 * 
	 * @return
	 */
	public long getEnd();
	
	/**
	 * 获取监控频度
	 * 
	 * @return
	 */
	public PeriodTimeFire getPeriodTimeFire();
}
