package com.mainsteam.stm.resourcelog.formatmsg.syslogmsg;

import java.util.ArrayList;
import java.util.List;

import com.mainsteam.stm.alarm.notify.AlarmNotifyTempletSelector;
import com.mainsteam.stm.alarm.obj.AlarmNotifyTemplet;
import com.mainsteam.stm.alarm.obj.AlarmProviderEnum;
import com.mainsteam.stm.alarm.obj.AlarmSenderParamter;
import com.mainsteam.stm.alarm.obj.SysModuleEnum;
import com.mainsteam.stm.profilelib.alarm.obj.SendWayEnum;

public class SelectorAlertMsg implements AlarmNotifyTempletSelector{

	private List<SendWayEnum> sendWays = new ArrayList<>();
	{
		sendWays.add(SendWayEnum.alert);
	}

	@Override
	public SysModuleEnum getSysID() {
		return SysModuleEnum.SYSLOG;
	}

	@Override
	public AlarmProviderEnum getProvider() {
		return AlarmProviderEnum.OTHER_SYS;
	}

	@Override
	public List<SendWayEnum> getSendWay() {
		return sendWays;
	}

	@Override
	public AlarmNotifyTemplet selector(List<AlarmNotifyTemplet> paramList,
			AlarmSenderParamter paramter) {
		AlarmNotifyTemplet tmp = new AlarmNotifyTemplet();
		tmp.setContent(paramter.getDefaultMsg());
		tmp.setProvider(AlarmProviderEnum.OTHER_SYS);
		return tmp;
	}

}
