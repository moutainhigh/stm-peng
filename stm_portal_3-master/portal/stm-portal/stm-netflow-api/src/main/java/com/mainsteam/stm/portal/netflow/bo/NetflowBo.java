/**
 * 
 */
package com.mainsteam.stm.portal.netflow.bo;

import java.io.Serializable;

/**
 * <li>文件名称: DeviceBo.java</li> <li>公　　司: 武汉美新翔盛科技有限公司</li> <li>版权所有:
 * 版权所有(C)2019-2020</li> <li>修改记录: ...</li> <li>内容摘要: ...</li> <li>其他说明: ...</li>
 * 
 * @version ms.stm
 * @since 2019年8月25日
 * @author lil
 */
public class NetflowBo implements Serializable {

	private static final long serialVersionUID = 1L;

	// 接口id
	private String ifId;
	// tos id
	private String tosId;
	// 应用id
	private String appId;
	// 协议id
	private String protoId;
	// 设备id
	private String id;
	// ip
	private String ip;
	// ip分组ID
	private String ipGroupId;
	// 接口分组ID
	private String ifGroupId;

	// 名称
	private String name;
	// 流入流量
	private String flowIn;
	// 流出流量
	private String flowOut;
	// 总流量
	private String flowTotal;
	// 流入包数
	private String packetIn;
	// 流出包数
	private String packetOut;
	// 总包数
	private String packetTotal;
	// 流入包数速率
	private String packetInSpeed;
	// 流出包数速率
	private String packetOutSpeed;
	// 总包数速率
	private String packetTotalSpeed;
	// 流入速率
	private String speedIn;
	// 流出速率
	private String speedOut;
	// 总速率
	private String speedTotal;
	// 流入带宽使用率
	private String flowInBwUsage;
	// 流出带宽使用率
	private String flowOutBwUsage;
	// 带宽使用率
	private String bwUsage;
	
	// 流量占比
	private String flowPctge;
	// 包占比
	private String packetPctge;
	//连接数占比
	private String connectPctge;
	
	// 带宽
	private String bwAll;
	// 接口名称
	private String ifName;
	// 下一跳
	private String nextHop;

	// 终端IP
	private String terminalIp;

	// 会话源IP
	private String srcIp;
	// 会话目的IP
	private String dstIp;

	// 流入连接数
	private String connectNumberIn;
	// 流出连接数
	private String connectNumberOut;
	// 总连接数
	private String connectNumberTotal;
	// 流入连接速率
	private String connectNumberInSpeed;
	// 流出连接速率
	private String connectNumberOutSpeed;
	// 总连接速率
	private String connectNumberTotalSpeed;
	// ip地址
	private String ipAddr;

	/**
	 * @return the packetPctge
	 */
	public String getPacketPctge() {
		return packetPctge;
	}

	/**
	 * @param packetPctge the packetPctge to set
	 */
	public void setPacketPctge(String packetPctge) {
		this.packetPctge = packetPctge;
	}

	/**
	 * @return the connectPctge
	 */
	public String getConnectPctge() {
		return connectPctge;
	}

	/**
	 * @param connectPctge the connectPctge to set
	 */
	public void setConnectPctge(String connectPctge) {
		this.connectPctge = connectPctge;
	}

	public String getPacketInSpeed() {
		return packetInSpeed;
	}

	public void setPacketInSpeed(String packetInSpeed) {
		this.packetInSpeed = packetInSpeed;
	}

	public String getPacketOutSpeed() {
		return packetOutSpeed;
	}

	public void setPacketOutSpeed(String packetOutSpeed) {
		this.packetOutSpeed = packetOutSpeed;
	}

	public String getPacketTotalSpeed() {
		return packetTotalSpeed;
	}

	public void setPacketTotalSpeed(String packetTotalSpeed) {
		this.packetTotalSpeed = packetTotalSpeed;
	}

	public String getConnectNumberIn() {
		return connectNumberIn;
	}

	public void setConnectNumberIn(String connectNumberIn) {
		this.connectNumberIn = connectNumberIn;
	}

	public String getConnectNumberOut() {
		return connectNumberOut;
	}

	public void setConnectNumberOut(String connectNumberOut) {
		this.connectNumberOut = connectNumberOut;
	}

	public String getConnectNumberTotal() {
		return connectNumberTotal;
	}

	public void setConnectNumberTotal(String connectNumberTotal) {
		this.connectNumberTotal = connectNumberTotal;
	}

	public String getConnectNumberInSpeed() {
		return connectNumberInSpeed;
	}

	public void setConnectNumberInSpeed(String connectNumberInSpeed) {
		this.connectNumberInSpeed = connectNumberInSpeed;
	}

	public String getConnectNumberOutSpeed() {
		return connectNumberOutSpeed;
	}

	public void setConnectNumberOutSpeed(String connectNumberOutSpeed) {
		this.connectNumberOutSpeed = connectNumberOutSpeed;
	}

	public String getConnectNumberTotalSpeed() {
		return connectNumberTotalSpeed;
	}

	public void setConnectNumberTotalSpeed(String connectNumberTotalSpeed) {
		this.connectNumberTotalSpeed = connectNumberTotalSpeed;
	}

	public String getIpAddr() {
		return ipAddr;
	}

	public void setIpAddr(String ipAddr) {
		this.ipAddr = ipAddr;
	}

	/**
	 * @return the id
	 */
	public String getId() {
		return id;
	}

	/**
	 * @param id
	 *            the id to set
	 */
	public void setId(String id) {
		this.id = id;
	}

	/**
	 * @return the name
	 */
	public String getName() {
		return name;
	}

	/**
	 * @param name
	 *            the name to set
	 */
	public void setName(String name) {
		this.name = name;
	}

	/**
	 * @return the flowIn
	 */
	public String getFlowIn() {
		return flowIn;
	}

	/**
	 * @param flowIn
	 *            the flowIn to set
	 */
	public void setFlowIn(String flowIn) {
		this.flowIn = flowIn;
	}

	/**
	 * @return the flowOut
	 */
	public String getFlowOut() {
		return flowOut;
	}

	/**
	 * @param flowOut
	 *            the flowOut to set
	 */
	public void setFlowOut(String flowOut) {
		this.flowOut = flowOut;
	}

	/**
	 * @return the flowTotal
	 */
	public String getFlowTotal() {
		return flowTotal;
	}

	/**
	 * @param flowTotal
	 *            the flowTotal to set
	 */
	public void setFlowTotal(String flowTotal) {
		this.flowTotal = flowTotal;
	}

	/**
	 * @return the packetIn
	 */
	public String getPacketIn() {
		return packetIn;
	}

	/**
	 * @param packetIn
	 *            the packetIn to set
	 */
	public void setPacketIn(String packetIn) {
		this.packetIn = packetIn;
	}

	/**
	 * @return the packetOut
	 */
	public String getPacketOut() {
		return packetOut;
	}

	/**
	 * @param packetOut
	 *            the packetOut to set
	 */
	public void setPacketOut(String packetOut) {
		this.packetOut = packetOut;
	}

	/**
	 * @return the packetTotal
	 */
	public String getPacketTotal() {
		return packetTotal;
	}

	/**
	 * @param packetTotal
	 *            the packetTotal to set
	 */
	public void setPacketTotal(String packetTotal) {
		this.packetTotal = packetTotal;
	}

	/**
	 * @return the speedIn
	 */
	public String getSpeedIn() {
		return speedIn;
	}

	/**
	 * @param speedIn
	 *            the speedIn to set
	 */
	public void setSpeedIn(String speedIn) {
		this.speedIn = speedIn;
	}

	/**
	 * @return the speedOut
	 */
	public String getSpeedOut() {
		return speedOut;
	}

	/**
	 * @param speedOut
	 *            the speedOut to set
	 */
	public void setSpeedOut(String speedOut) {
		this.speedOut = speedOut;
	}

	/**
	 * @return the speedTotal
	 */
	public String getSpeedTotal() {
		return speedTotal;
	}

	/**
	 * @param speedTotal
	 *            the speedTotal to set
	 */
	public void setSpeedTotal(String speedTotal) {
		this.speedTotal = speedTotal;
	}

	/**
	 * @return the flowInBwUsage
	 */
	public String getFlowInBwUsage() {
		return flowInBwUsage;
	}

	/**
	 * @param flowInBwUsage
	 *            the flowInBwUsage to set
	 */
	public void setFlowInBwUsage(String flowInBwUsage) {
		this.flowInBwUsage = flowInBwUsage;
	}

	/**
	 * @return the flowOutBwUsage
	 */
	public String getFlowOutBwUsage() {
		return flowOutBwUsage;
	}

	/**
	 * @param flowOutBwUsage
	 *            the flowOutBwUsage to set
	 */
	public void setFlowOutBwUsage(String flowOutBwUsage) {
		this.flowOutBwUsage = flowOutBwUsage;
	}

	/**
	 * @return the bwUsage
	 */
	public String getBwUsage() {
		return bwUsage;
	}

	/**
	 * @param bwUsage
	 *            the bwUsage to set
	 */
	public void setBwUsage(String bwUsage) {
		this.bwUsage = bwUsage;
	}

	/**
	 * @return the flowPctge
	 */
	public String getFlowPctge() {
		return flowPctge;
	}

	/**
	 * @param flowPctge
	 *            the flowPctge to set
	 */
	public void setFlowPctge(String flowPctge) {
		this.flowPctge = flowPctge;
	}

	public String getIp() {
		return ip;
	}

	public void setIp(String ip) {
		this.ip = ip;
	}

	/**
	 * @return the bwAll
	 */
	public String getBwAll() {
		return bwAll;
	}

	/**
	 * @param bwAll
	 *            the bwAll to set
	 */
	public void setBwAll(String bwAll) {
		this.bwAll = bwAll;
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
	 * @return the tosId
	 */
	public String getTosId() {
		return tosId;
	}

	/**
	 * @param tosId
	 *            the tosId to set
	 */
	public void setTosId(String tosId) {
		this.tosId = tosId;
	}

	/**
	 * @return the ifName
	 */
	public String getIfName() {
		return ifName;
	}

	/**
	 * @param ifName
	 *            the ifName to set
	 */
	public void setIfName(String ifName) {
		this.ifName = ifName;
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

}
