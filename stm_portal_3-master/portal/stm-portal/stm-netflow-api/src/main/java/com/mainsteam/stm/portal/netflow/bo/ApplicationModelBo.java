/**
 * 
 */
package com.mainsteam.stm.portal.netflow.bo;

import java.io.Serializable;

/**
 * <li>文件名称: TerminalBo.java</li> <li>公　　司: 武汉美新翔盛科技有限公司</li> <li>版权所有:
 * 版权所有(C)2019-2020</li> <li>修改记录: ...</li> <li>内容摘要: ...</li> <li>其他说明: ...</li>
 * 
 * @version ms.stm
 * @since 2019年8月
 * @author xiejr
 */

public class ApplicationModelBo implements Serializable {

	private static final long serialVersionUID = 1L;
	private int ID;
	private String device_Ip;
	private String interface_Ip;
	private String app_name;
	private String app_id;
	private String proto_name;
	private String proto;
	private String in_flows;
	private String out_flows;
	private String total_flows;
	private String in_packages;
	private String out_packages;
	private String total_packages;
	private String in_speed;
	private String out_speed;
	private String total_speed;
	private String flows_rate;
	private String start_time;
	private String end_time;
	private String terminal_name;
	private String src_ip;
	private String dst_ip;
	private String ipgroup_name;
	private String ipgroup_id;
	// 流入包数速率
	private String packetInSpeed;
	// 流出包数速率
	private String packetOutSpeed;
	// 总包数速率
	private String packetTotalSpeed;
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

	// 流量占比
	private String flowPctge;
	// 包占比
	private String packetPctge;
	// 连接数占比
	private String connectPctge;

	public String getFlowPctge() {
		return flowPctge;
	}

	public void setFlowPctge(String flowPctge) {
		this.flowPctge = flowPctge;
	}

	public String getPacketPctge() {
		return packetPctge;
	}

	public void setPacketPctge(String packetPctge) {
		this.packetPctge = packetPctge;
	}

	public String getConnectPctge() {
		return connectPctge;
	}

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

	public String getStart_time() {
		return start_time;
	}

	public void setStart_time(String start_time) {
		this.start_time = start_time;
	}

	public String getEnd_time() {
		return end_time;
	}

	public void setEnd_time(String end_time) {
		this.end_time = end_time;
	}

	public int getID() {
		return ID;
	}

	public void setID(int iD) {
		ID = iD;
	}

	public String getIn_flows() {
		return in_flows;
	}

	public void setIn_flows(String in_flows) {
		this.in_flows = in_flows;
	}

	public String getOut_flows() {
		return out_flows;
	}

	public void setOut_flows(String out_flows) {
		this.out_flows = out_flows;
	}

	public String getTotal_flows() {
		return total_flows;
	}

	public void setTotal_flows(String total_flows) {
		this.total_flows = total_flows;
	}

	public String getIn_packages() {
		return in_packages;
	}

	public void setIn_packages(String in_packages) {
		this.in_packages = in_packages;
	}

	public String getOut_packages() {
		return out_packages;
	}

	public void setOut_packages(String out_packages) {
		this.out_packages = out_packages;
	}

	public String getTotal_packages() {
		return total_packages;
	}

	public void setTotal_packages(String total_packages) {
		this.total_packages = total_packages;
	}

	public String getIn_speed() {
		return in_speed;
	}

	public void setIn_speed(String in_speed) {
		this.in_speed = in_speed;
	}

	public String getOut_speed() {
		return out_speed;
	}

	public void setOut_speed(String out_speed) {
		this.out_speed = out_speed;
	}

	public String getTotal_speed() {
		return total_speed;
	}

	public void setTotal_speed(String total_speed) {
		this.total_speed = total_speed;
	}

	public String getFlows_rate() {
		return flows_rate;
	}

	public void setFlows_rate(String flows_rate) {
		this.flows_rate = flows_rate;
	}

	public String getDevice_Ip() {
		return device_Ip;
	}

	public void setDevice_Ip(String device_Ip) {
		this.device_Ip = device_Ip;
	}

	public String getInterface_Ip() {
		return interface_Ip;
	}

	public void setInterface_Ip(String interface_Ip) {
		this.interface_Ip = interface_Ip;
	}

	public String getApp_name() {
		return app_name;
	}

	public void setApp_name(String app_name) {
		this.app_name = app_name;
	}

	public String getApp_id() {
		return app_id;
	}

	public void setApp_id(String app_id) {
		this.app_id = app_id;
	}

	public String getProto_name() {
		return proto_name;
	}

	public void setProto_name(String proto_name) {
		this.proto_name = proto_name;
	}

	public String getProto() {
		return proto;
	}

	public void setProto(String proto) {
		this.proto = proto;
	}

	public String getTerminal_name() {
		return terminal_name;
	}

	public void setTerminal_name(String terminal_name) {
		this.terminal_name = terminal_name;
	}

	public String getSrc_ip() {
		return src_ip;
	}

	public void setSrc_ip(String src_ip) {
		this.src_ip = src_ip;
	}

	public String getDst_ip() {
		return dst_ip;
	}

	public void setDst_ip(String dst_ip) {
		this.dst_ip = dst_ip;
	}

	public String getIpgroup_name() {
		return ipgroup_name;
	}

	public void setIpgroup_name(String ipgroup_name) {
		this.ipgroup_name = ipgroup_name;
	}

	public String getIpgroup_id() {
		return ipgroup_id;
	}

	public void setIpgroup_id(String ipgroup_id) {
		this.ipgroup_id = ipgroup_id;
	}

}
