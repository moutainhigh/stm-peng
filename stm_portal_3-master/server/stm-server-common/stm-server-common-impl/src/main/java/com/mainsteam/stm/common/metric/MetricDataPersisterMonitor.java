package com.mainsteam.stm.common.metric;

import java.util.Date;

public interface MetricDataPersisterMonitor {

	/**
	 * 待处理的指标格式
	 * @return
	 */
	public int getRemainingSize();

	/**
	 * 正在处理的指标个数
	 * @return
	 */
	public int getActiveSize();

	/**
	 * 已经处理完成的指标总个数
	 * 
	 * @return
	 */
	public long getOverSumSize();

	/**
	 * 总消耗时间
	 * 
	 * @return
	 */
	public long getLossTime();
	
	/**
	 * 第一次指标最近处理的时间
	 * 
	 * @return
	 */
	public Date getStartDateTime();
	
	/**
	 * 处理失败的指标格式
	 * 
	 * @return
	 */
	public long getErrCount();
	
	/**
	 * 最近处理的指标个数
	 * @return
	 */
	public long getBatchLossTime();
	
	/**
	 * 最近处理的指标消耗的个数
	 * @return
	 */
	public int getBatchMetricCount();
	
	
	/**
	 * 最近处理的指标的吞吐量
	 * @return
	 */
	public int getBatchMetricSpeed();	
	
	/**
	 * 最近指标开始处理时间
	 * 
	 * @return
	 */
	public Date getBatchDateTime();

	/**
	 * 平均处理的指标的吞吐量
	 * 
	 * @return
	 */
	int getAverageSpeed();
}
