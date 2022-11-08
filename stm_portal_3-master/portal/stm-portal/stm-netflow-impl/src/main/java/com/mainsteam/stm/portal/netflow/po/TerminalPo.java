/**
 * 
 */
package com.mainsteam.stm.portal.netflow.po;

/**
 * <li>文件名称: TerminalPo.java</li> <li>公　　司: 武汉美新翔盛科技有限公司</li> <li>版权所有:
 * 版权所有(C)2019-2020</li> <li>修改记录: ...</li> <li>内容摘要: ...</li> <li>其他说明: ...</li>
 * 
 * @version ms.stm
 * @since 2019年7月
 * @author xiejr
 */

public class TerminalPo {
	private int ID;
	private String TerminalName;
	private String in_flows;
	private String out_flows;
	private String total_flows;
	private String in_packages;
	private String out_packages;
	private String total_package;
	private String in_speed;
	private String out_speed;
	private String total_speed;
	private String flows_rate;

	public int getID() {
		return ID;
	}

	public void setID(int iD) {
		ID = iD;
	}

	public String getTerminalName() {
		return TerminalName;
	}

	public void setTerminalName(String terminalName) {
		TerminalName = terminalName;
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

	public String getTotal_package() {
		return total_package;
	}

	public void setTotal_package(String total_package) {
		this.total_package = total_package;
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

}
