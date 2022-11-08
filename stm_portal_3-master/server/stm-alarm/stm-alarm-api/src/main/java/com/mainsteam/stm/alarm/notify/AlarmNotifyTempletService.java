package com.mainsteam.stm.alarm.notify;

import java.util.List;

import com.mainsteam.stm.alarm.obj.AlarmNotifyTemplet;
import com.mainsteam.stm.alarm.obj.AlarmProviderEnum;
import com.mainsteam.stm.alarm.obj.SysModuleEnum;

/**
 * @author cx
 */
@Deprecated
public interface AlarmNotifyTempletService {
	/**
	 * @param esourceID
	 * @return
	 */
	List<AlarmNotifyTemplet> findTempletBySysID(SysModuleEnum sysID,AlarmProviderEnum provider);
	
	/**
	 * @param esourceID
	 * @return
	 */
	void addTemplet( AlarmNotifyTemplet tmp);
}
