/**
 * 
 */
package com.mainsteam.stm.portal.netflow.web.vo;

import java.io.Serializable;
import java.util.List;
import java.util.Map;

/**
 * <li>文件名称: DeviceToTerminalVo.java</li> <li>公　　司: 武汉美新翔盛科技有限公司</li> <li>版权所有:
 * 版权所有(C)2019-2020</li> <li>修改记录: ...</li> <li>内容摘要: ...</li> <li>其他说明: ...</li>
 * 
 * @version ms.stm
 * @since 2019年7月
 * @author xiejr
 */

public class SessionConditionVo implements Serializable {
	private static final long serialVersionUID = 1L;
	private String starttime;
	private String endtime;
	private String recordCount;
	// 排序的列
	private String sort;
	// 排序方式
	private String order;

	private String deviceIp;
	private String if_id;
	private String startRow;
	private String rowCount;

	// 获取表后缀的时间
	private String tableSubfixTime;
	// 获取计算速率的时间段
	private String timepart;
	private long stime;
	private long etime;
	private String showpagination;
	private String app_id;
	
	private String allapplicationflows;
	private String allprotoflows;
	private List<String> applicationsIp;
	private List<String> protosIp;
	
	private String allterminalsFlows;

	//为了在sql中取到当前要查询的终端的名字，因为有许多终端，所以添加当前变量，来设定当前查询的是哪个终端。
	private String currentTerminals;
	
	private List<String> terminalsIp;
	
	private List<Map<String,String>> sessionips;
	
	private String allsessionFlows;
	//包
	private String allsessionPackets;
	//连接数
	private String allsessionConnects;
	
	private String allipgsFlows;
	
	private String currentipgIP;
		
	private List<String> ipgsIp;
	
	private String currentSrcIp;
	private String currentDstIp;
	
	private String timePerid;
	
	
	/**
	 * @return the allsessionPackets
	 */
	public String getAllsessionPackets() {
		return allsessionPackets;
	}

	/**
	 * @param allsessionPackets the allsessionPackets to set
	 */
	public void setAllsessionPackets(String allsessionPackets) {
		this.allsessionPackets = allsessionPackets;
	}

	/**
	 * @return the allsessionConnects
	 */
	public String getAllsessionConnects() {
		return allsessionConnects;
	}

	/**
	 * @param allsessionConnects the allsessionConnects to set
	 */
	public void setAllsessionConnects(String allsessionConnects) {
		this.allsessionConnects = allsessionConnects;
	}

	public String getTimePerid() {
		return timePerid;
	}

	public void setTimePerid(String timePerid) {
		this.timePerid = timePerid;
	}

	public String getApp_id() {
		return app_id;
	}

	public void setApp_id(String app_id) {
		this.app_id = app_id;
	}

	public String getTableSubfixTime() {
		return tableSubfixTime;
	}

	public void setTableSubfixTime(String tableSubfixTime) {
		this.tableSubfixTime = tableSubfixTime;
	}

	public String getTimepart() {
		return timepart;
	}

	public void setTimepart(String timepart) {
		this.timepart = timepart;
	}

	public long getStime() {
		return stime;
	}

	public void setStime(long stime) {
		this.stime = stime;
	}

	public long getEtime() {
		return etime;
	}

	public void setEtime(long etime) {
		this.etime = etime;
	}

	public String getStartRow() {
		return startRow;
	}

	public void setStartRow(String startRow) {
		this.startRow = startRow;
	}

	public String getRowCount() {
		return rowCount;
	}

	public void setRowCount(String rowCount) {
		this.rowCount = rowCount;
	}

	public String getStarttime() {
		return starttime;
	}

	public void setStarttime(String starttime) {
		this.starttime = starttime;
	}

	public String getEndtime() {
		return endtime;
	}

	public void setEndtime(String endtime) {
		this.endtime = endtime;
	}

	public String getRecordCount() {
		return recordCount;
	}

	public void setRecordCount(String recordCount) {
		this.recordCount = recordCount;
	}

	public String getSort() {
		return sort;
	}

	public void setSort(String sort) {
		this.sort = sort;
	}

	public String getOrder() {
		return order;
	}

	public void setOrder(String order) {
		this.order = order;
	}

	public String getDeviceIp() {
		return deviceIp;
	}

	public void setDeviceIp(String deviceIp) {
		this.deviceIp = deviceIp;
	}

	public String getShowpagination() {
		return showpagination;
	}

	public void setShowpagination(String showpagination) {
		this.showpagination = showpagination;
	}

	public String getAllapplicationflows() {
		return allapplicationflows;
	}

	public void setAllapplicationflows(String allapplicationflows) {
		this.allapplicationflows = allapplicationflows;
	}

	public String getAllprotoflows() {
		return allprotoflows;
	}

	public void setAllprotoflows(String allprotoflows) {
		this.allprotoflows = allprotoflows;
	}

	public List<String> getApplicationsIp() {
		return applicationsIp;
	}

	public void setApplicationsIp(List<String> applicationsIp) {
		this.applicationsIp = applicationsIp;
	}

	public List<String> getProtosIp() {
		return protosIp;
	}

	public void setProtosIp(List<String> protosIp) {
		this.protosIp = protosIp;
	}

	public String getAllterminalsFlows() {
		return allterminalsFlows;
	}

	public void setAllterminalsFlows(String allterminalsFlows) {
		this.allterminalsFlows = allterminalsFlows;
	}

	public String getCurrentTerminals() {
		return currentTerminals;
	}

	public void setCurrentTerminals(String currentTerminals) {
		this.currentTerminals = currentTerminals;
	}

	public List<String> getTerminalsIp() {
		return terminalsIp;
	}

	public void setTerminalsIp(List<String> terminalsIp) {
		this.terminalsIp = terminalsIp;
	}

	public List<Map<String, String>> getSessionips() {
		return sessionips;
	}

	public void setSessionips(List<Map<String, String>> sessionips) {
		this.sessionips = sessionips;
	}

	public String getAllsessionFlows() {
		return allsessionFlows;
	}

	public void setAllsessionFlows(String allsessionFlows) {
		this.allsessionFlows = allsessionFlows;
	}

	public String getAllipgsFlows() {
		return allipgsFlows;
	}

	public void setAllipgsFlows(String allipgsFlows) {
		this.allipgsFlows = allipgsFlows;
	}

	public String getCurrentipgIP() {
		return currentipgIP;
	}

	public void setCurrentipgIP(String currentipgIP) {
		this.currentipgIP = currentipgIP;
	}

	public List<String> getIpgsIp() {
		return ipgsIp;
	}

	public void setIpgsIp(List<String> ipgsIp) {
		this.ipgsIp = ipgsIp;
	}

	public String getIf_id() {
		return if_id;
	}

	public void setIf_id(String if_id) {
		this.if_id = if_id;
	}

	public String getCurrentSrcIp() {
		return currentSrcIp;
	}

	public void setCurrentSrcIp(String currentSrcIp) {
		this.currentSrcIp = currentSrcIp;
	}

	public String getCurrentDstIp() {
		return currentDstIp;
	}

	public void setCurrentDstIp(String currentDstIp) {
		this.currentDstIp = currentDstIp;
	}
	
	
	
}
