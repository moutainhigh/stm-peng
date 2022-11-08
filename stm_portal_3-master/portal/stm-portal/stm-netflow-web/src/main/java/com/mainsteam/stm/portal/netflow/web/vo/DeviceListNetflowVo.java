/**
 * 
 */
package com.mainsteam.stm.portal.netflow.web.vo;

import java.io.Serializable;

/**
 * <li>文件名称: DeviceListNetflowVo.java</li>
 * <li>公　　司: 武汉美新翔盛科技有限公司</li>
 * <li>版权所有: 版权所有(C)2019-2020</li>
 * <li>修改记录: ...</li>
 * <li>内容摘要: ...</li>
 * <li>其他说明: ...</li>
 * @version  ms.stm
 * @since    2019年8月14日
 * @author   lil
 */
public class DeviceListNetflowVo implements Serializable {

	/** 
	 * @Fields serialVersionUID
	 */
	private static final long serialVersionUID = 1L;
	
	private long deviceIp;
	private long startRow;
	private long rowCount;
	private long timeInterval;
	private long wholeFlows;
	private long wholeBw;
	private long stime;
	private long etime;
	private long querySize;
	private String startTime; 
	private String endTime;
	private String sort;
	private String order;
	private String tableSuffix;
	
	private boolean needPagination;
	
	/**
	 * @return the startTime
	 */
	public String getStartTime() {
		return startTime;
	}
	/**
	 * @param startTime the startTime to set
	 */
	public void setStartTime(String startTime) {
		this.startTime = startTime;
	}
	/**
	 * @return the endTime
	 */
	public String getEndTime() {
		return endTime;
	}
	/**
	 * @param endTime the endTime to set
	 */
	public void setEndTime(String endTime) {
		this.endTime = endTime;
	}
	/**
	 * @return the startRow
	 */
	public long getStartRow() {
		return startRow;
	}
	/**
	 * @param startRow the startRow to set
	 */
	public void setStartRow(long startRow) {
		this.startRow = startRow;
	}
	/**
	 * @return the rowCount
	 */
	public long getRowCount() {
		return rowCount;
	}
	/**
	 * @param rowCount the rowCount to set
	 */
	public void setRowCount(long rowCount) {
		this.rowCount = rowCount;
	}
	/**
	 * @return the sort
	 */
	public String getSort() {
		return sort;
	}
	/**
	 * @param sort the sort to set
	 */
	public void setSort(String sort) {
		this.sort = sort;
	}
	/**
	 * @return the order
	 */
	public String getOrder() {
		return order;
	}
	/**
	 * @param order the order to set
	 */
	public void setOrder(String order) {
		this.order = order;
	}
	/**
	 * @return the timeInterval
	 */
	public long getTimeInterval() {
		return timeInterval;
	}
	/**
	 * @param timeInterval the timeInterval to set
	 */
	public void setTimeInterval(long timeInterval) {
		this.timeInterval = timeInterval;
	}
	/**
	 * @return the wholeFlows
	 */
	public long getWholeFlows() {
		return wholeFlows;
	}
	/**
	 * @param wholeFlows the wholeFlows to set
	 */
	public void setWholeFlows(long wholeFlows) {
		this.wholeFlows = wholeFlows;
	}
	/**
	 * @return the wholeBw
	 */
	public long getWholeBw() {
		return wholeBw;
	}
	/**
	 * @param wholeBw the wholeBw to set
	 */
	public void setWholeBw(long wholeBw) {
		this.wholeBw = wholeBw;
	}
	/**
	 * @return the tableSuffix
	 */
	public String getTableSuffix() {
		return tableSuffix;
	}
	/**
	 * @param tableSuffix the tableSuffix to set
	 */
	public void setTableSuffix(String tableSuffix) {
		this.tableSuffix = tableSuffix;
	}
	/**
	 * @return the stime
	 */
	public long getStime() {
		return stime;
	}
	/**
	 * @param stime the stime to set
	 */
	public void setStime(long stime) {
		this.stime = stime;
	}
	/**
	 * @return the etime
	 */
	public long getEtime() {
		return etime;
	}
	/**
	 * @param etime the etime to set
	 */
	public void setEtime(long etime) {
		this.etime = etime;
	}
	/**
	 * @return the deviceIp
	 */
	public long getDeviceIp() {
		return deviceIp;
	}
	/**
	 * @param deviceIp the deviceIp to set
	 */
	public void setDeviceIp(long deviceIp) {
		this.deviceIp = deviceIp;
	}
	/**
	 * @return the querySize
	 */
	public long getQuerySize() {
		return querySize;
	}
	/**
	 * @param querySize the querySize to set
	 */
	public void setQuerySize(long querySize) {
		this.querySize = querySize;
	}
	/**
	 * @return the needPagination
	 */
	public boolean isNeedPagination() {
		return needPagination;
	}
	/**
	 * @param needPagination the needPagination to set
	 */
	public void setNeedPagination(boolean needPagination) {
		this.needPagination = needPagination;
	}
	
}
