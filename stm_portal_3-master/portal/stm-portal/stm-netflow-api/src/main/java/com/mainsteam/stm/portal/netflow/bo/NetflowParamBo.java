/**
 * 
 */
package com.mainsteam.stm.portal.netflow.bo;

import java.io.Serializable;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

/**
 * <li>文件名称: DeviceListNetflowBo.java</li> 
 * <li>公　　司: 武汉美新翔盛科技有限公司</li>
 * <li>版权所有: 版权所有(C)2019-2020</li>
 * <li>修改记录: ...</li>
 * <li>内容摘要: ...</li> 
 * <li>其他说明: ...</li>
 * 
 * @version ms.stm
 * @since 2019年8月14日
 * @author lil
 */
public class NetflowParamBo implements Serializable {

	/**
	 * @Fields serialVersionUID
	 */
	private static final long serialVersionUID = 1L;
	private long[] deviceIps;

	private long[] deviceIp;
	private long startRow;
	private long rowCount;
	private String timeInterval;
	
	private String wholeFlows;
	private String wholeBw;
	private String wholePackets;
	private String wholeConnects;
	
	private String stime;
	private String etime;
	private String appId;
	private String protoId;
	private String tosId;
	private long querySize;
	private String ifId;
	private String ipGroupId;
	private String ifGroupId;

	private String startTime;
	private String endTime;
	private String sort;
	private String order;
	private String tableSuffix;
	private String appName;
	private String timeParam;
	private String nextHop;
	private String terminalIp;

	private String srcIp;
	private String dstIp;
	
	private String timePerid;
	
	private String ipAddr;//设备IP
	private String name;//设备名称
	private String ifName;//接口名称
	
	private String type; //1.流量     2.包     3.连接数           4.带宽使用率

	private boolean needPagination;

	/**
	 * @return the type
	 */
	public String getType() {
		return type;
	}

	/**
	 * @param type the type to set
	 */
	public void setType(String type) {
		this.type = type;
	}

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

	public long[] getDeviceIps() {
		return deviceIps;
	}

	public void setDeviceIps(long[] deviceIps) {
		this.deviceIps = deviceIps;
	}

	/**
	 * @return the startTime
	 */
	public String getStartTime() {
		String st = "";
		if(null  != this.timePerid) {
			if("custom".equalsIgnoreCase(this.timePerid)) {//自定义时间
				st = this.startTime;
			} else { //非自定义时间
				String ret = getTimeFromPeroid(timePerid);
				st = ret.split("[|]")[0];
			}
		} 
		return st;
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
		String et = "";
		if(null != this.timePerid) {
			if("custom".equalsIgnoreCase(this.timePerid)) {
				et = this.endTime;
			} else {
				String ret = getTimeFromPeroid(timePerid);
				et = ret.split("[|]")[1];
			}
		}
		return et;
	}

	/**
	 * @param endTime
	 *            the endTime to set
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
		return this.timeInterval;
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

	public long[] getDeviceIp() {
		return deviceIp;
	}

	public void setDeviceIp(long[] deviceIp) {
		this.deviceIp = deviceIp;
	}

	/**
	 * @return the appName
	 */
	public String getAppName() {
		return appName;
	}

	/**
	 * @param appName
	 *            the appName to set
	 */
	public void setAppName(String appName) {
		this.appName = appName;
	}

	/**
	 * @return the appId
	 */
	public String getAppId() {
		return appId;
	}

	/**
	 * @param appId
	 *            the appId to set
	 */
	public void setAppId(String appId) {
		this.appId = appId;
	}

	/**
	 * @return the timeParam
	 */
	public String getTimeParam() {
		return timeParam;
	}

	/**
	 * @param timeParam
	 *            the timeParam to set
	 */
	public void setTimeParam(String timeParam) {
		this.timeParam = timeParam;
	}

	/**
	 * @return the protoId
	 */
	public String getProtoId() {
		return protoId;
	}

	/**
	 * @param protoId
	 *            the protoId to set
	 */
	public void setProtoId(String protoId) {
		this.protoId = protoId;
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
	 * @return the tosId
	 */
	public String getTosId() {
		return tosId;
	}

	/**
	 * @param tosId the tosId to set
	 */
	public void setTosId(String tosId) {
		this.tosId = tosId;
	}

	/**
	 * @return the nextHop
	 */
	public String getNextHop() {
		return nextHop;
	}

	/**
	 * @param nextHop
	 *            the nextHop to set
	 */
	public void setNextHop(String nextHop) {
		this.nextHop = nextHop;
	}

	/**
	 * @return the terminalIp
	 */
	public String getTerminalIp() {
		return terminalIp;
	}

	/**
	 * @param terminalIp
	 *            the terminalIp to set
	 */
	public void setTerminalIp(String terminalIp) {
		this.terminalIp = terminalIp;
	}

	/**
	 * @return the srcIp
	 */
	public String getSrcIp() {
		return srcIp;
	}

	/**
	 * @param srcIp
	 *            the srcIp to set
	 */
	public void setSrcIp(String srcIp) {
		this.srcIp = srcIp;
	}

	/**
	 * @return the dstIp
	 */
	public String getDstIp() {
		return dstIp;
	}

	/**
	 * @param dstIp
	 *            the dstIp to set
	 */
	public void setDstIp(String dstIp) {
		this.dstIp = dstIp;
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
		if("1hour".equals(period)){
			minute = minute%10;
			now.add(Calendar.MINUTE, -minute);
			now.add(Calendar.SECOND, -second);
			endTime = dtf.format(new Date(now.getTimeInMillis()));
			now.add(Calendar.HOUR_OF_DAY, -1);
			startTime = dtf.format(new Date(now.getTimeInMillis()));
		}else if("6hour".equals(period)){
			minute = minute%10;
			now.add(Calendar.MINUTE, -minute);
			now.add(Calendar.SECOND, -second);
			endTime = dtf.format(new Date(now.getTimeInMillis()));
			now.add(Calendar.HOUR_OF_DAY, -6);
			startTime = dtf.format(new Date(now.getTimeInMillis()));
		}else if("1day".equals(period)){
			minute = minute%10;
			now.add(Calendar.MINUTE, -minute);
			now.add(Calendar.SECOND, -second);
			endTime = dtf.format(new Date(now.getTimeInMillis()));
			now.add(Calendar.DAY_OF_MONTH, -1);
			startTime = dtf.format(new Date(now.getTimeInMillis()));
		}else if("7day".equals(period)){
			hour = hour%2;
			now.add(Calendar.HOUR, -hour);
			now.add(Calendar.MINUTE, -minute);
			now.add(Calendar.SECOND, -second);
			endTime = dtf.format(new Date(now.getTimeInMillis()));
			now.add(Calendar.DAY_OF_MONTH, -7);
			startTime = dtf.format(new Date(now.getTimeInMillis()));
		}else if("30day".equals(period)){
			now.add(Calendar.HOUR, -hour);
			now.add(Calendar.MINUTE, -minute);
			now.add(Calendar.SECOND, -second);
			endTime = dtf.format(new Date(now.getTimeInMillis()));
			now.add(Calendar.DAY_OF_MONTH, -30);
			startTime = dtf.format(new Date(now.getTimeInMillis()));
		}else if("90day".equals(period)){
			now.add(Calendar.HOUR, -hour);
			now.add(Calendar.MINUTE, -minute);
			now.add(Calendar.SECOND, -second);
			endTime = dtf.format(new Date(now.getTimeInMillis()));
			now.add(Calendar.DAY_OF_YEAR, -90);
			startTime = dtf.format(new Date(now.getTimeInMillis()));
		}else if("yesterday".equals(period)){
			now.add(Calendar.HOUR, -hour);
			now.add(Calendar.MINUTE, -minute);
			now.add(Calendar.SECOND, -second);
			endTime = dtf.format(new Date(now.getTimeInMillis()));
			now.add(Calendar.DAY_OF_MONTH, -1);
			startTime = dtf.format(new Date(now.getTimeInMillis()));
		}else if("lastweek".equals(period)){
			now.add(Calendar.DAY_OF_MONTH, -(dayOfWeek-2));
			now.add(Calendar.HOUR, -hour);
			now.add(Calendar.MINUTE, -minute);
			now.add(Calendar.SECOND, -second);
			endTime = dtf.format(new Date(now.getTimeInMillis()));
			now.add(Calendar.DAY_OF_MONTH, -7);
			startTime = dtf.format(new Date(now.getTimeInMillis()));
		}else if("lastmonth".equals(period)){
			now.add(Calendar.DAY_OF_MONTH, -(dayOfMonth-1));
			now.add(Calendar.HOUR, -hour);
			now.add(Calendar.MINUTE, -minute);
			now.add(Calendar.SECOND, -second);
			endTime = dtf.format(new Date(now.getTimeInMillis()));
			now.add(Calendar.DAY_OF_MONTH, -1);
			int lastMonth = now.get(Calendar.DAY_OF_MONTH);
			now.add(Calendar.DAY_OF_MONTH, -(lastMonth-1));
			startTime = dtf.format(new Date(now.getTimeInMillis()));
		}else if("today".equals(period)){
			int minutes = minute%10;
			now.add(Calendar.MINUTE, -minutes);
			now.add(Calendar.SECOND, -second);
			endTime = dtf.format(new Date(now.getTimeInMillis()));
			now.add(Calendar.HOUR, -hour);
			now.add(Calendar.MINUTE, -(minute-minutes));
			startTime = dtf.format(new Date(now.getTimeInMillis()));
		}else if("currweek".equals(period)){
			if(dayOfWeek>2){
				int hours = hour%2;
				now.add(Calendar.HOUR, -hours);
				now.add(Calendar.MINUTE, -minute);
				now.add(Calendar.SECOND, -second);
				endTime = dtf.format(new Date(now.getTimeInMillis()));
				now.add(Calendar.HOUR, -(hour-hours));
				now.add(Calendar.DAY_OF_MONTH, -(dayOfWeek-2));
				startTime = dtf.format(new Date(now.getTimeInMillis()));
			}else{
				int minutes = minute%10;
				now.add(Calendar.MINUTE, -minutes);
				now.add(Calendar.SECOND, -second);
				endTime = dtf.format(new Date(now.getTimeInMillis()));
				now.add(Calendar.DAY_OF_MONTH, -(dayOfWeek-2));
				now.add(Calendar.HOUR, -hour);
				now.add(Calendar.MINUTE, -(minute-minutes));
				startTime = dtf.format(new Date(now.getTimeInMillis()));
			}
		}else if("currmonth".equals(period)){
			if(dayOfMonth==1){
				int minutes = minute%10;
				now.add(Calendar.MINUTE, -minutes);
				now.add(Calendar.SECOND, -second);
				endTime = dtf.format(new Date(now.getTimeInMillis()));
				now.add(Calendar.HOUR, -hour);
				now.add(Calendar.MINUTE, -(minute-minutes));
				startTime = dtf.format(new Date(now.getTimeInMillis()));
			}else if(dayOfMonth>1 && dayOfMonth<=7){
				int hours = hour%2;
				now.add(Calendar.HOUR, -hours);
				now.add(Calendar.MINUTE, -minute);
				now.add(Calendar.SECOND, -second);
				endTime = dtf.format(new Date(now.getTimeInMillis()));
				now.add(Calendar.HOUR, -(hour-hours));
				now.add(Calendar.DAY_OF_MONTH, -(dayOfMonth-1));
				startTime = dtf.format(new Date(now.getTimeInMillis()));
				startTime = dtf.format(new Date(now.getTimeInMillis()));
			}else if(dayOfMonth>7){
				now.add(Calendar.HOUR, -hour);
				now.add(Calendar.MINUTE, -minute);
				now.add(Calendar.SECOND, -second);
				endTime = dtf.format(new Date(now.getTimeInMillis()));
				now.add(Calendar.DAY_OF_MONTH, -(dayOfMonth-1));
				startTime = dtf.format(new Date(now.getTimeInMillis()));
			}
		}else{
			return "custom";
		}
		return startTime + "|" + endTime;
	}
	
}
