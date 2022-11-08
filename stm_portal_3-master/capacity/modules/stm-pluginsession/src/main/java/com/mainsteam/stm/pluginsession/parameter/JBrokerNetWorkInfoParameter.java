package com.mainsteam.stm.pluginsession.parameter;


/**
 * 网卡信息
 * 
 * @author yuanlb
 *
 */
public class JBrokerNetWorkInfoParameter {
private String vss;//虚拟交换机
private String vnics;//虚拟网卡
public String getVss() {
	return vss;
}
public void setVss(String vss) {
	this.vss = vss;
}
public String getVnics() {
	return vnics;
}
@Override
public String toString() {
	return "\n网卡信息 [vss=" + vss + ", vnics=" + vnics + "]";
}
public void setVnics(String vnics) {
	this.vnics = vnics;
}
	
	/*public void setVss(List<JBrokerVirtualSwitchParameter> vss) {
		// TODO Auto-generated method stub
	}

	// 属性都是乱的 待修改

	public void setVnics(List<JBrokerVirtualNicParameter> vnics) {
		// TODO Auto-generated method stub

	}*/
}
