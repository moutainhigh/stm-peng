package com.mainsteam.stm.event;

import java.util.ArrayList;
import java.util.List;

import com.mainsteam.stm.alarm.notify.AlarmNotifyTempletSelector;
import com.mainsteam.stm.alarm.obj.AlarmNotifyTemplet;
import com.mainsteam.stm.alarm.obj.AlarmProviderEnum;
import com.mainsteam.stm.alarm.obj.AlarmSenderParamter;
import com.mainsteam.stm.alarm.obj.SysModuleEnum;
import com.mainsteam.stm.profilelib.alarm.obj.SendWayEnum;
import org.apache.commons.lang.StringUtils;

public class AlarmEventNotifyMessageSelector implements AlarmNotifyTempletSelector{
	
	private List<SendWayEnum> sendWays=new ArrayList<>();
	{
		sendWays.add(SendWayEnum.email);
		//sendWays.add(SendWayEnum.alert);
		sendWays.add(SendWayEnum.sms);
	}
	@Override
	public SysModuleEnum getSysID() {
		return SysModuleEnum.MONITOR;
	}

	@Override
	public AlarmProviderEnum getProvider() {
		return AlarmProviderEnum.OC4;
	}

	@Override
	public List<SendWayEnum> getSendWay() {
		return sendWays;
	}

	@Override
	public AlarmNotifyTemplet selector(List<AlarmNotifyTemplet> templetes, AlarmSenderParamter paramter) {
		AlarmNotifyTemplet tmp = new AlarmNotifyTemplet();
		tmp.setProvider(AlarmProviderEnum.OC4);
		return tmp;
	}

}
