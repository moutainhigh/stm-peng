package com.mainsteam.stm.alarm.notify;

import java.util.List;

import com.mainsteam.stm.alarm.notify.dao.AlarmNotifyTempletDAO;
import com.mainsteam.stm.alarm.obj.AlarmProviderEnum;
import com.mainsteam.stm.alarm.obj.SysModuleEnum;
import com.mainsteam.stm.alarm.obj.AlarmNotifyTemplet;
@Deprecated
public class AlarmNotifyTempletServiceImpl implements AlarmNotifyTempletService {

	private  AlarmNotifyTempletDAO  alarmNotifyTempletDao;
	
	public void setAlarmNotifyTempletDao(AlarmNotifyTempletDAO alarmNotifyTempletDao) {
		this.alarmNotifyTempletDao = alarmNotifyTempletDao;
	}
	
	@Override
	public List<AlarmNotifyTemplet> findTempletBySysID(SysModuleEnum sysID,AlarmProviderEnum provider) {
		return alarmNotifyTempletDao.findTempletBySysID(sysID,provider);
	}
	
	@Override
	public void addTemplet( AlarmNotifyTemplet tmp) {
		alarmNotifyTempletDao.addTemplet(tmp);
	}

}
