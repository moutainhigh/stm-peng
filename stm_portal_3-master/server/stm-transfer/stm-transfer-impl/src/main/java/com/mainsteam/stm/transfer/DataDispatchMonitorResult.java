/**
 * 
 */
package com.mainsteam.stm.transfer;

import com.mainsteam.stm.transfer.obj.TransferDataType;

/**
 * @author ziw
 *
 */
public class DataDispatchMonitorResult {
	
	private TransferDataType dataType;
	
	private int activeCount;
	private int remainingCount;
	private long allFlowCount;
	private float throughput;
	private boolean monitor;

	/**
	 * 
	 */
	public DataDispatchMonitorResult() {
	}

	/**
	 * @return the dataType
	 */
	public final TransferDataType getDataType() {
		return dataType;
	}

	/**
	 * @param dataType the dataType to set
	 */
	public final void setDataType(TransferDataType dataType) {
		this.dataType = dataType;
	}

	/**
	 * @return the activeCount
	 */
	public final int getActiveCount() {
		return activeCount;
	}

	/**
	 * @param activeCount the activeCount to set
	 */
	public final void setActiveCount(int activeCount) {
		this.activeCount = activeCount;
	}

	/**
	 * @return the remainingCount
	 */
	public final int getRemainingCount() {
		return remainingCount;
	}

	/**
	 * @param remainingCount the remainingCount to set
	 */
	public final void setRemainingCount(int remainingCount) {
		this.remainingCount = remainingCount;
	}

	/**
	 * @return the allFlowCount
	 */
	public final long getAllFlowCount() {
		return allFlowCount;
	}

	/**
	 * @param allFlowCount the allFlowCount to set
	 */
	public final void setAllFlowCount(long allFlowCount) {
		this.allFlowCount = allFlowCount;
	}

	/**
	 * @return the throughput
	 */
	public final float getThroughput() {
		return throughput;
	}

	/**
	 * @param throughput the throughput to set
	 */
	public final void setThroughput(float throughput) {
		this.throughput = throughput;
	}

	/**
	 * @return the monitor
	 */
	public final boolean isMonitor() {
		return monitor;
	}

	/**
	 * @param monitor the monitor to set
	 */
	public final void setMonitor(boolean monitor) {
		this.monitor = monitor;
	}
}
