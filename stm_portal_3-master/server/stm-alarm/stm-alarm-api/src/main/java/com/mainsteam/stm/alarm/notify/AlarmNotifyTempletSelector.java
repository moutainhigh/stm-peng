package com.mainsteam.stm.alarm.notify;

import java.util.List;

import com.mainsteam.stm.alarm.obj.AlarmNotifyTemplet;
import com.mainsteam.stm.alarm.obj.AlarmProviderEnum;
import com.mainsteam.stm.alarm.obj.AlarmSenderParamter;
import com.mainsteam.stm.alarm.obj.SysModuleEnum;
import com.mainsteam.stm.profilelib.alarm.obj.SendWayEnum;

@Deprecated
public interface AlarmNotifyTempletSelector {

	public SysModuleEnum getSysID();
	
	public AlarmProviderEnum getProvider();	
	
	public List<SendWayEnum> getSendWay();
	
	public AlarmNotifyTemplet selector(List<AlarmNotifyTemplet> templetes, AlarmSenderParamter paramter);
}
