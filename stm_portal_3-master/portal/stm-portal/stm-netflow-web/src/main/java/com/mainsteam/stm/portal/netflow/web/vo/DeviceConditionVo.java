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

public class DeviceConditionVo implements Serializable {
	private static final long serialVersionUID = 1L;
	private String starttime;
	private String endtime;
	private String recordCount;
	// 排序的列
	private String sort;
	// 排序方式
	private String order;

	private String deviceIp;
	private String startRow;
	private String rowCount;

	// 获取表后缀的时间
	private String tableSubfixTime;
	// 获取计算速率的时间段
	private String timepart;
	// 计算所有的取出来的记录的流量的总和
	private String allterminalsFlows;

	private long stime;
	private long etime;
	
	private String showpagination;
	
	private String allsessionFlows;
	
	private List<Map<String,String>> sessionips;
	
		
	private List<String> terminalsIp;
	
	
	private String allNextHopsFlows;
	
	//为了在sql中取到当前要查询的终端的名字，因为有许多终端，所以添加当前变量，来设定当前查询的是哪个终端。
	private String currentNextHop;
		
	private List<String> nextHopsIp;	
	
	private String allipgsFlows;
	//为了在sql中取到当前要查询的终端的名字，因为有许多终端，所以添加当前变量，来设定当前查询的是哪个终端。
	private String currentipgIP;
		
	private List<String> ipgsIp;
	
	private String timePerid;
	
	

	public String getTimePerid() {
		return timePerid;
	}

	public void setTimePerid(String timePerid) {
		this.timePerid = timePerid;
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

	public String getAllterminalsFlows() {
		return allterminalsFlows;
	}

	public void setAllterminalsFlows(String allterminalsFlows) {
		this.allterminalsFlows = allterminalsFlows;
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

	public String getAllsessionFlows() {
		return allsessionFlows;
	}

	public void setAllsessionFlows(String allsessionFlows) {
		this.allsessionFlows = allsessionFlows;
	}

	public List<Map<String, String>> getSessionips() {
		return sessionips;
	}

	public void setSessionips(List<Map<String, String>> sessionips) {
		this.sessionips = sessionips;
	}

	public List<String> getTerminalsIp() {
		return terminalsIp;
	}

	public void setTerminalsIp(List<String> terminalsIp) {
		this.terminalsIp = terminalsIp;
	}

	public String getAllNextHopsFlows() {
		return allNextHopsFlows;
	}

	public void setAllNextHopsFlows(String allNextHopsFlows) {
		this.allNextHopsFlows = allNextHopsFlows;
	}

	public String getCurrentNextHop() {
		return currentNextHop;
	}

	public void setCurrentNextHop(String currentNextHop) {
		this.currentNextHop = currentNextHop;
	}

	public List<String> getNextHopsIp() {
		return nextHopsIp;
	}

	public void setNextHopsIp(List<String> nextHopsIp) {
		this.nextHopsIp = nextHopsIp;
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


	
}
