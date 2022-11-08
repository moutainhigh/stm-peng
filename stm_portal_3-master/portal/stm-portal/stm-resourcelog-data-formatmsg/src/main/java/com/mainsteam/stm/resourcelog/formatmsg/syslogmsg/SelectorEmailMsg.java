package com.mainsteam.stm.resourcelog.formatmsg.syslogmsg;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import com.mainsteam.stm.alarm.notify.AlarmNotifyTempletSelector;
import com.mainsteam.stm.alarm.obj.AlarmNotifyTemplet;
import com.mainsteam.stm.alarm.obj.AlarmProviderEnum;
import com.mainsteam.stm.alarm.obj.AlarmSenderParamter;
import com.mainsteam.stm.alarm.obj.SysModuleEnum;
import com.mainsteam.stm.profilelib.alarm.obj.SendWayEnum;
import com.mainsteam.stm.resourcelog.formatmsg.util.MsgEnum;

public class SelectorEmailMsg implements AlarmNotifyTempletSelector {

	private List<SendWayEnum> sendWays = new ArrayList<>();
	{
		sendWays.add(SendWayEnum.email);
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
		Map<String, String> paramMap = paramter.getProp().get(0).getMap();
		AlarmNotifyTemplet tmp = new AlarmNotifyTemplet();
		tmp.setContent("syslog来源:" + paramMap.get(MsgEnum.ORIGIN.toString())
				+ ",syslog级别:" + paramMap.get(MsgEnum.LEVEL.toString())
				+ ",关键字:" + paramMap.get(MsgEnum.KEYWORDS.toString())
				+ ",syslog内容如下:" + paramter.getDefaultMsg());
		tmp.setTitle(paramter.getDefaultMsgTitle());
		tmp.setProvider(AlarmProviderEnum.OTHER_SYS);
		return tmp;
	}

}
