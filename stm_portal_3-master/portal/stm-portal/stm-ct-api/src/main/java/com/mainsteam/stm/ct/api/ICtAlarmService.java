package com.mainsteam.stm.ct.api;

import com.mainsteam.stm.ct.bo.MsCtAlarm;
import com.mainsteam.stm.platform.mybatis.plugin.pagination.Page;

public interface ICtAlarmService{

    void getAlarmPage(Page<MsCtAlarm,MsCtAlarm> page);

    int insertAlarm(MsCtAlarm msCtAlarm);

    int editAlarm(MsCtAlarm msCtAlarm);

	MsCtAlarm getById(String string);
}
