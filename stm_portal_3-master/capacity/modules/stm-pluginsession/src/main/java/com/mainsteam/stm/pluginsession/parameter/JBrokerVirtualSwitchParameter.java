package com.mainsteam.stm.pluginsession.parameter;

/**
 * 存储 虚拟交换机
 * @author yuanlb
 *
 */
public class JBrokerVirtualSwitchParameter {
	private String name;
	

	@Override
	public String toString() {
		return "\n 存储 虚拟交换机 [(名称)name=" + name + "]";
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public void addPhysicalMAC(String string) {
		// TODO Auto-generated method stub
	}

	public void addPortgroup(String string) {
		// TODO Auto-generated method stub
	}
}
