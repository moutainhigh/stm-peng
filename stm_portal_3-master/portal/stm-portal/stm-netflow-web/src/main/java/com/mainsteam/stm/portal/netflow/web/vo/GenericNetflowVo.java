/**
 * 
 */
package com.mainsteam.stm.portal.netflow.web.vo;

import java.io.Serializable;

/**
 * <li>文件名称: DeviceListNetflowVo.java</li> <li>公　　司: 武汉美新翔盛科技有限公司</li> <li>版权所有:
 * 版权所有(C)2019-2020</li> <li>修改记录: ...</li> <li>内容摘要: ...</li> <li>其他说明: ...</li>
 * 
 * @version ms.stm
 * @since 2019年8月14日
 * @author lil
 */
public class GenericNetflowVo implements Serializable {

	/**
	 * @Fields serialVersionUID
	 */
	private static final long serialVersionUID = 1L;

	private String ifId;
	private String deviceIp;
	private long startRow;
	private long rowCount;
	private String timeInterval;
	
	private String wholeFlows;
	private String wholePackets;
	private String wholeConnects;
	
	private String wholeBw;
	private String stime;
	private String etime;
	private long querySize;
	private String startTime;
	private String endTime;
	private String sort;
	private String order;
	private String tableSuffix;
	private String ifGroupId;
	private String ipGroupId;
	private String timePerid;
	
	private String ipAddr;//设备IP
	private String name;//设备名称
	private String ifName;//接口名称

	private boolean needPagination;

	private long onePageRows;
	
	/**
	 * @return the ipAddr
	 */
	public String getIpAddr() {
		return ipAddr;
	}

	/**
	 * @param ipAddr the ipAddr to set
	 */
	public void setIpAddr(String ipAddr) {
		this.ipAddr = ipAddr;
	}

	/**
	 * @return the name
	 */
	public String getName() {
		return name;
	}

	/**
	 * @param name the name to set
	 */
	public void setName(String name) {
		this.name = name;
	}

	/**
	 * @return the ifName
	 */
	public String getIfName() {
		return ifName;
	}

	/**
	 * @param ifName the ifName to set
	 */
	public void setIfName(String ifName) {
		this.ifName = ifName;
	}

	/**
	 * @return the wholePackets
	 */
	public String getWholePackets() {
		return wholePackets;
	}

	/**
	 * @param wholePackets the wholePackets to set
	 */
	public void setWholePackets(String wholePackets) {
		this.wholePackets = wholePackets;
	}

	/**
	 * @return the wholeConnects
	 */
	public String getWholeConnects() {
		return wholeConnects;
	}

	/**
	 * @param wholeConnects the wholeConnects to set
	 */
	public void setWholeConnects(String wholeConnects) {
		this.wholeConnects = wholeConnects;
	}

	/**
	 * @return the onePageRows
	 */
	public long getOnePageRows() {
		return onePageRows;
	}

	/**
	 * @return the startTime
	 */
	public String getStartTime() {
		return startTime;
	}

	/**
	 * @param startTime
	 *            the startTime to set
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
	 * @param endTime
	 *            the endTime to set
	 */
	public void setEndTime(String endTime) {
		this.endTime = endTime;
	}

	/**
	 * @return the sort
	 */
	public String getSort() {
		return sort;
	}

	/**
	 * @param sort
	 *            the sort to set
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
	 * @param order
	 *            the order to set
	 */
	public void setOrder(String order) {
		this.order = order;
	}

	/**
	 * @return the timeInterval
	 */
	public String getTimeInterval() {
		return timeInterval;
	}

	/**
	 * @param timeInterval
	 *            the timeInterval to set
	 */
	public void setTimeInterval(String timeInterval) {
		this.timeInterval = timeInterval;
	}

	/**
	 * @return the wholeFlows
	 */
	public String getWholeFlows() {
		return wholeFlows;
	}

	/**
	 * @param wholeFlows
	 *            the wholeFlows to set
	 */
	public void setWholeFlows(String wholeFlows) {
		this.wholeFlows = wholeFlows;
	}

	/**
	 * @return the wholeBw
	 */
	public String getWholeBw() {
		return wholeBw;
	}

	/**
	 * @param wholeBw
	 *            the wholeBw to set
	 */
	public void setWholeBw(String wholeBw) {
		this.wholeBw = wholeBw;
	}

	/**
	 * @return the tableSuffix
	 */
	public String getTableSuffix() {
		return tableSuffix;
	}

	/**
	 * @param tableSuffix
	 *            the tableSuffix to set
	 */
	public void setTableSuffix(String tableSuffix) {
		this.tableSuffix = tableSuffix;
	}

	/**
	 * @return the stime
	 */
	public String getStime() {
		return stime;
	}

	/**
	 * @param stime
	 *            the stime to set
	 */
	public void setStime(String stime) {
		this.stime = stime;
	}

	/**
	 * @return the etime
	 */
	public String getEtime() {
		return etime;
	}

	/**
	 * @param etime
	 *            the etime to set
	 */
	public void setEtime(String etime) {
		this.etime = etime;
	}

	public String getDeviceIp() {
		return deviceIp;
	}

	public void setDeviceIp(String deviceIp) {
		this.deviceIp = deviceIp;
	}

	/**
	 * @return the querySize
	 */
	public long getQuerySize() {
		return querySize;
	}

	/**
	 * @param querySize
	 *            the querySize to set
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
	 * @param needPagination
	 *            the needPagination to set
	 */
	public void setNeedPagination(boolean needPagination) {
		this.needPagination = needPagination;
	}

	/**
	 * @return the ifId
	 */
	public String getIfId() {
		return ifId;
	}

	/**
	 * @param ifId
	 *            the ifId to set
	 */
	public void setIfId(String ifId) {
		this.ifId = ifId;
	}

	/**
	 * @return the ifGroupId
	 */
	public String getIfGroupId() {
		return ifGroupId;
	}

	/**
	 * @param ifGroupId
	 *            the ifGroupId to set
	 */
	public void setIfGroupId(String ifGroupId) {
		this.ifGroupId = ifGroupId;
	}

	/**
	 * @return the ipGroupId
	 */
	public String getIpGroupId() {
		return ipGroupId;
	}

	/**
	 * @param ipGroupId
	 *            the ipGroupId to set
	 */
	public void setIpGroupId(String ipGroupId) {
		this.ipGroupId = ipGroupId;
	}

	/**
	 * @return the startRow
	 */
	public long getStartRow() {
		return startRow;
	}

	/**
	 * @param startRow
	 *            the startRow to set
	 */
	public void setStartRow(long startRow) {
		this.startRow = startRow;
	}

	/**
	 * @return the rowCount
	 */
	public long getRowCount() {
		if (this.onePageRows > 0) {
			return onePageRows;
		}
		return rowCount;
	}

	/**
	 * @param rowCount
	 *            the rowCount to set
	 */
	public void setRowCount(long rowCount) {
		this.rowCount = rowCount;
	}

	public void setOnePageRows(long onePageRows) {
		this.setRowCount(onePageRows);
		this.onePageRows = onePageRows;
	}

	/**
	 * @return the timePerid
	 */
	public String getTimePerid() {
		return timePerid;
	}

	/**
	 * @param timePerid the timePerid to set
	 */
	public void setTimePerid(String timePerid) {
		this.timePerid = timePerid;
	}
	
}
