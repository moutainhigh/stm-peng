package com.mainsteam.stm.resourcelog.formatmsg.trapmsg;

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

public class TrapSelectorEmailMsg implements AlarmNotifyTempletSelector{

	private List<SendWayEnum> sendWays = new ArrayList<>();
	{
		sendWays.add(SendWayEnum.email);
	}

	@Override
	public SysModuleEnum getSysID() {
		return SysModuleEnum.TRAP;
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
		tmp.setContent("资源名称:" + paramMap.get(MsgEnum.RESNAME.toString())
				+ ",资源IP地址:" + paramMap.get(MsgEnum.RESIP.toString())
				+ ",trap类型:" + paramMap.get(MsgEnum.LEVEL.toString())
				+ ",trap内容:" + paramter.getDefaultMsg()
				+ ",产生时间:" + paramMap.get(MsgEnum.OCCURTIME.toString()));
		tmp.setTitle(paramter.getDefaultMsgTitle());
		tmp.setProvider(AlarmProviderEnum.OTHER_SYS);
		return tmp;
	}

}
