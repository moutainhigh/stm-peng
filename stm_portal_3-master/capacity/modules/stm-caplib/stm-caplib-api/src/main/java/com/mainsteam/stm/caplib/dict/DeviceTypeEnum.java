package com.mainsteam.stm.caplib.dict;

/**
 * 
 * <li>文件名称: DeviceTypeEnum.java</li><br/>
 * <li>文件描述: 设备类型枚举</li><br/>
 * <li>版权所有: 版权所有(C)2014</li><br/>
 * <li>公司: 美新翔盛</li> <br/>
 * <li>内容摘要: 设备类型枚举定义</li> <br/>
 * <li>其他说明:</li><br/>
 * <li>完成日期：2014.8.12</li><br/>
 * <li>修改记录1:新建</li>
 * 
 * @version 3
 * @author sunsht
 */
public enum DeviceTypeEnum {

	Other(1),

	L2Switch(2),

	Router(3),

	RouterSwitch(4),

	Firewall(5),

	LoadBalancer(6),
	
	Wireless(7),
	
	WirelessAP(8),

	Server(9),

	Host(10),
	
	Printer(11),
	
	Copier(12),
	
	WirelessAC(13),
	
	VideoDevice(14),
	
	TrafficManage(15),
	
	UPS(16),
	
	AirConditioner(17),
	
	Sensor(18),
	
	PDU(19);
	
	public boolean isHost(){
		if(this.typeVal == 9 || this.typeVal == 10){
			return true;
		}
		return false;
	}

	private int typeVal;

	DeviceTypeEnum(int stateVal) {
		this.typeVal = stateVal;
	}

	public int getStateVal() {
		return this.typeVal;
	}
}
