package com.mainsteam.stm.pluginsession.parameter;

/**
 * 虚拟网卡
 * 
 * @author yuanlb
 *
 */
public class JBrokerVirtualNicParameter {
	private String name;
	private String portgroup;// 端口组
	private String Mac;// 网卡地址

	@Override
	public String toString() {
		return "\n虚拟网卡(名称)[name=" + name + ", (端口组)portgroup=" + portgroup
				+ ", (MAC地址)Mac=" + Mac + "]";
	}

	public String getName() {
		return name;
	}

	public String getPortgroup() {
		return portgroup;
	}

	public String getMac() {
		return Mac;
	}

	public void setName(String device) {
		// TODO Auto-generated method stub
	}

	// 属性都是乱的 待修改

	public void setPortgroup(String portgroup) {
		// TODO Auto-generated method stub

	}

	public void setMac(String mac) {
		// TODO Auto-generated method stub

	}
}
