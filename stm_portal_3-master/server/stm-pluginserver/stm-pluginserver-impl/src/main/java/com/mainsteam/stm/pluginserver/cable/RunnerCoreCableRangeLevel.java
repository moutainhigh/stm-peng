/**
 * 
 */
package com.mainsteam.stm.pluginserver.cable;

/**
 * @author ziw
 * 
 */
public enum RunnerCoreCableRangeLevel {
	FIRST(-100, 100, 50, 5), SECOND(101, 200, 100, 10), THREAD(1000, 30000, 400,
			100), LATEST(30001, Integer.MAX_VALUE, 1000, 200), DISCOVERY(-100,
			Integer.MAX_VALUE, 20, 5);
	/**
	 * 最大调度时间为1天，如果session超过一天又10分钟没有了数据，则停止监控。
	 */
	protected static int MAX_RANGE_TOP = 86460000;

	private long[] range;
	private int maxActiveSize;
	private int minIdel;

	/**
	 * @return the range
	 */
	public final long[] getRange() {
		return range;
	}

	/**
	 * @return the maxActiveSize
	 */
	public final int getMaxActiveSize() {
		return maxActiveSize;
	}

	/**
	 * @return the minIdel
	 */
	public final int getMinIdel() {
		return minIdel;
	}

	private RunnerCoreCableRangeLevel(long start, long end, int maxActiveSize,
			int minIdel) {
		range = new long[2];
		range[0] = start;
		range[1] = end;
		this.maxActiveSize = maxActiveSize;
		this.minIdel = minIdel;
	}

	/**
	 * @return the mAX_RANGE_TOP
	 */
	public static final int getMaxRangeTop() {
		return MAX_RANGE_TOP;
	}
}
