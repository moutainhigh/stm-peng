/**
 * 
 */
package com.mainsteam.stm.portal.netflow.bo;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Map;

/**
 * <li>文件名称: DeviceToTerminalBo.java</li> <li>公　　司: 武汉美新翔盛科技有限公司</li> <li>版权所有:
 * 版权所有(C)2019-2020</li> <li>修改记录: ...</li> <li>内容摘要: ...</li> <li>其他说明: ...</li>
 * 
 * @version ms.stm
 * @since 2019年7月
 * @author xiejr
 */

public class DeviceConditionBo {
	private String starttime;
	private String endtime;
	private long recordCount;
	// 排序的列
	private String sort;
	// 排序方式
	private String order;
	private String deviceIp;
	private long startRow;
	private long rowCount;
	// 获取表后缀的时间
	private String tableSubfixTime;
	// 获取计算速率的时间段
	private String timepart;
	// 计算所有的取出来的记录的流量的总和
	private String allterminalsFlows;

	private long stime;
	private long etime;

	// 为了在sql中取到当前要查询的终端的名字，因为有许多终端，所以添加当前变量，来设定当前查询的是哪个终端。
	private String currentTerminals;

	private List<String> terminalsIp;

	private List<Long> timePoints;

	private String showpagination;

	private String allsessionFlows;

	// 为了在sql中取到当前要查询的终端的名字，因为有许多终端，所以添加当前变量，来设定当前查询的是哪个终端。
	private String currentSrcIp;
	private String currentDstIp;

	private List<Map<String, String>> sessionips;

	private String allNextHopsFlows;

	// 为了在sql中取到当前要查询的终端的名字，因为有许多终端，所以添加当前变量，来设定当前查询的是哪个终端。
	private String currentNextHop;

	private List<String> nextHopsIp;

	private String allipgsFlows;
	// 为了在sql中取到当前要查询的终端的名字，因为有许多终端，所以添加当前变量，来设定当前查询的是哪个终端。
	private String currentipgIP;

	private List<String> ipgsIp;

	private String timePerid;

	private String wholeFlows;
	private String wholePackets;
	private String wholeConnects;
	private String type;

	public String getType() {
		return type;
	}

	public void setType(String type) {
		this.type = type;
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
	 * @return the wholePackets
	 */
	public String getWholePackets() {
		return wholePackets;
	}

	/**
	 * @param wholePackets
	 *            the wholePackets to set
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
	 * @param wholeConnects
	 *            the wholeConnects to set
	 */
	public void setWholeConnects(String wholeConnects) {
		this.wholeConnects = wholeConnects;
	}

	public String getStarttime() {
		String st = "";
		if (null != this.timePerid) {
			if("custom".equalsIgnoreCase(this.timePerid)) {
				st = this.starttime;
			} else {
				String ret = getTimeFromPeroid(timePerid);
				st = ret.split("[|]")[0];
			}
		}
		return st;
	}

	public void setStarttime(String starttime) {
		this.starttime = starttime;
	}

	public String getEndtime() {
		String et = "";
		if (null != this.timePerid) {
			if("custom".equalsIgnoreCase(this.timePerid)) {
				et = this.endtime;
			} else {
				String ret = getTimeFromPeroid(timePerid);
				et = ret.split("[|]")[1];
			}
		}
		return et;
	}

	public void setEndtime(String endtime) {
		this.endtime = endtime;
	}

	public long getRecordCount() {
		return recordCount;
	}

	public void setRecordCount(long recordCount) {
		this.recordCount = recordCount;
	}

	public String getDeviceIp() {
		return deviceIp;
	}

	public void setDeviceIp(String deviceIp) {
		this.deviceIp = deviceIp;
	}

	public long getStartRow() {
		return startRow;
	}

	public void setStartRow(long startRow) {
		this.startRow = startRow;
	}

	public long getRowCount() {
		return rowCount;
	}

	public void setRowCount(long rowCount) {
		this.rowCount = rowCount;
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

	public void timeFomratChange() {

		String starttime = this.getStarttime();
		String endtime = this.getEndtime();
		long stime = 0;
		long etime = 0;
		SimpleDateFormat df_time = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
		try {
			stime = df_time.parse(starttime).getTime() / 1000;
			etime = df_time.parse(endtime).getTime() / 1000;
		} catch (ParseException e) {
			e.printStackTrace();
		}
		this.setStime(stime);
		this.setEtime(etime);
	}

	public List<String> getTerminalsIp() {
		return terminalsIp;
	}

	public void setTerminalsIp(List<String> terminalsIp) {
		this.terminalsIp = terminalsIp;
	}

	public List<Long> getTimePoints() {
		return timePoints;
	}

	public void setTimePoints(List<Long> timePoints) {
		this.timePoints = timePoints;
		for (int loop = 0; loop < this.timePoints.size(); loop++) {
			this.timePoints.set(loop, this.timePoints.get(loop) / 1000);
		}

	}

	public String getCurrentTerminals() {
		return currentTerminals;
	}

	public void setCurrentTerminals(String currentTerminals) {
		this.currentTerminals = currentTerminals;
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

	public List<Map<String, String>> getSessionips() {
		return sessionips;
	}

	public void setSessionips(List<Map<String, String>> sessionips) {
		this.sessionips = sessionips;
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

	private String getTimeFromPeroid(String period) {

		Calendar now = Calendar.getInstance();
		int dayOfMonth = now.get(Calendar.DAY_OF_MONTH);
		int dayOfWeek = now.get(Calendar.DAY_OF_WEEK);
		int hour = now.get(Calendar.HOUR_OF_DAY);
		int minute = now.get(Calendar.MINUTE);
		int second = now.get(Calendar.SECOND);
		String startTime = null;
		String endTime = null;
		DateFormat dtf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
		if ("1hour".equals(period)) {
			minute = minute % 10;
			now.add(Calendar.MINUTE, -minute);
			now.add(Calendar.SECOND, -second);
			endTime = dtf.format(new Date(now.getTimeInMillis()));
			now.add(Calendar.HOUR_OF_DAY, -1);
			startTime = dtf.format(new Date(now.getTimeInMillis()));
		} else if ("6hour".equals(period)) {
			minute = minute % 10;
			now.add(Calendar.MINUTE, -minute);
			now.add(Calendar.SECOND, -second);
			endTime = dtf.format(new Date(now.getTimeInMillis()));
			now.add(Calendar.HOUR_OF_DAY, -6);
			startTime = dtf.format(new Date(now.getTimeInMillis()));
		} else if ("1day".equals(period)) {
			minute = minute % 10;
			now.add(Calendar.MINUTE, -minute);
			now.add(Calendar.SECOND, -second);
			endTime = dtf.format(new Date(now.getTimeInMillis()));
			now.add(Calendar.DAY_OF_MONTH, -1);
			startTime = dtf.format(new Date(now.getTimeInMillis()));
		} else if ("7day".equals(period)) {
			hour = hour % 2;
			now.add(Calendar.HOUR, -hour);
			now.add(Calendar.MINUTE, -minute);
			now.add(Calendar.SECOND, -second);
			endTime = dtf.format(new Date(now.getTimeInMillis()));
			now.add(Calendar.DAY_OF_MONTH, -7);
			startTime = dtf.format(new Date(now.getTimeInMillis()));
		} else if ("30day".equals(period)) {
			now.add(Calendar.HOUR, -hour);
			now.add(Calendar.MINUTE, -minute);
			now.add(Calendar.SECOND, -second);
			endTime = dtf.format(new Date(now.getTimeInMillis()));
			now.add(Calendar.DAY_OF_MONTH, -30);
			startTime = dtf.format(new Date(now.getTimeInMillis()));
		} else if ("90day".equals(period)) {
			now.add(Calendar.HOUR, -hour);
			now.add(Calendar.MINUTE, -minute);
			now.add(Calendar.SECOND, -second);
			endTime = dtf.format(new Date(now.getTimeInMillis()));
			now.add(Calendar.DAY_OF_YEAR, -90);
			startTime = dtf.format(new Date(now.getTimeInMillis()));
		} else if ("yesterday".equals(period)) {
			now.add(Calendar.HOUR, -hour);
			now.add(Calendar.MINUTE, -minute);
			now.add(Calendar.SECOND, -second);
			endTime = dtf.format(new Date(now.getTimeInMillis()));
			now.add(Calendar.DAY_OF_MONTH, -1);
			startTime = dtf.format(new Date(now.getTimeInMillis()));
		} else if ("lastweek".equals(period)) {
			now.add(Calendar.DAY_OF_MONTH, -(dayOfWeek - 2));
			now.add(Calendar.HOUR, -hour);
			now.add(Calendar.MINUTE, -minute);
			now.add(Calendar.SECOND, -second);
			endTime = dtf.format(new Date(now.getTimeInMillis()));
			now.add(Calendar.DAY_OF_MONTH, -7);
			startTime = dtf.format(new Date(now.getTimeInMillis()));
		} else if ("lastmonth".equals(period)) {
			now.add(Calendar.DAY_OF_MONTH, -(dayOfMonth - 1));
			now.add(Calendar.HOUR, -hour);
			now.add(Calendar.MINUTE, -minute);
			now.add(Calendar.SECOND, -second);
			endTime = dtf.format(new Date(now.getTimeInMillis()));
			now.add(Calendar.DAY_OF_MONTH, -1);
			int lastMonth = now.get(Calendar.DAY_OF_MONTH);
			now.add(Calendar.DAY_OF_MONTH, -(lastMonth - 1));
			startTime = dtf.format(new Date(now.getTimeInMillis()));
		} else if ("today".equals(period)) {
			int minutes = minute % 10;
			now.add(Calendar.MINUTE, -minutes);
			now.add(Calendar.SECOND, -second);
			endTime = dtf.format(new Date(now.getTimeInMillis()));
			now.add(Calendar.HOUR, -hour);
			now.add(Calendar.MINUTE, -(minute - minutes));
			startTime = dtf.format(new Date(now.getTimeInMillis()));
		} else if ("currweek".equals(period)) {
			if (dayOfWeek > 2) {
				int hours = hour % 2;
				now.add(Calendar.HOUR, -hours);
				now.add(Calendar.MINUTE, -minute);
				now.add(Calendar.SECOND, -second);
				endTime = dtf.format(new Date(now.getTimeInMillis()));
				now.add(Calendar.HOUR, -(hour - hours));
				now.add(Calendar.DAY_OF_MONTH, -(dayOfWeek - 2));
				startTime = dtf.format(new Date(now.getTimeInMillis()));
			} else {
				int minutes = minute % 10;
				now.add(Calendar.MINUTE, -minutes);
				now.add(Calendar.SECOND, -second);
				endTime = dtf.format(new Date(now.getTimeInMillis()));
				now.add(Calendar.DAY_OF_MONTH, -(dayOfWeek - 2));
				now.add(Calendar.HOUR, -hour);
				now.add(Calendar.MINUTE, -(minute - minutes));
				startTime = dtf.format(new Date(now.getTimeInMillis()));
			}
		} else if ("currmonth".equals(period)) {
			if (dayOfMonth == 1) {
				int minutes = minute % 10;
				now.add(Calendar.MINUTE, -minutes);
				now.add(Calendar.SECOND, -second);
				endTime = dtf.format(new Date(now.getTimeInMillis()));
				now.add(Calendar.HOUR, -hour);
				now.add(Calendar.MINUTE, -(minute - minutes));
				startTime = dtf.format(new Date(now.getTimeInMillis()));
			} else if (dayOfMonth > 1 && dayOfMonth <= 7) {
				int hours = hour % 2;
				now.add(Calendar.HOUR, -hours);
				now.add(Calendar.MINUTE, -minute);
				now.add(Calendar.SECOND, -second);
				endTime = dtf.format(new Date(now.getTimeInMillis()));
				now.add(Calendar.HOUR, -(hour - hours));
				now.add(Calendar.DAY_OF_MONTH, -(dayOfMonth - 1));
				startTime = dtf.format(new Date(now.getTimeInMillis()));
				startTime = dtf.format(new Date(now.getTimeInMillis()));
			} else if (dayOfMonth > 7) {
				now.add(Calendar.HOUR, -hour);
				now.add(Calendar.MINUTE, -minute);
				now.add(Calendar.SECOND, -second);
				endTime = dtf.format(new Date(now.getTimeInMillis()));
				now.add(Calendar.DAY_OF_MONTH, -(dayOfMonth - 1));
				startTime = dtf.format(new Date(now.getTimeInMillis()));
			}
		} else {
			return "custom";
		}
		return startTime + "|" + endTime;
	}

	public String getTimePerid() {
		return timePerid;
	}

	public void setTimePerid(String timePerid) {
		this.timePerid = timePerid;
	}

}
