package com.mainsteam.stm.alarm.notify.dao;

import java.util.List;

import com.mainsteam.stm.alarm.obj.AlarmNotifyTemplet;
import com.mainsteam.stm.alarm.obj.AlarmProviderEnum;
import com.mainsteam.stm.alarm.obj.SysModuleEnum;

/**
 * @author cx
 *
 */
public interface AlarmNotifyTempletDAO {

	List<AlarmNotifyTemplet> findTempletBySysID(SysModuleEnum sysID,AlarmProviderEnum provider);

	void addTemplet(AlarmNotifyTemplet tmp);

}
