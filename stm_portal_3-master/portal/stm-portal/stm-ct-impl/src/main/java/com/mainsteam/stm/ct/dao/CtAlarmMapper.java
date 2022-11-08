package com.mainsteam.stm.ct.dao;

import org.apache.ibatis.annotations.Param;

import com.mainsteam.stm.ct.bo.MsCtAlarm;
import com.mainsteam.stm.platform.mybatis.plugin.pagination.Page;

public interface CtAlarmMapper{

    void getAlarmPage(Page<MsCtAlarm,MsCtAlarm> page);

	int insertAlarm(MsCtAlarm msCtAlarm);

	int editAlarm(MsCtAlarm msCtAlarm);

	MsCtAlarm getById(@Param("id")long id);
}
