package com.mainsteam.stm.ct.dao.impl;

import org.apache.log4j.Logger;
import org.mybatis.spring.SqlSessionTemplate;

import com.mainsteam.stm.ct.bo.MsCtAlarm;
import com.mainsteam.stm.ct.dao.CtAlarmMapper;
import com.mainsteam.stm.platform.dao.BaseDao;
import com.mainsteam.stm.platform.mybatis.plugin.pagination.Page;

public class CtAlarmMapperImpl extends BaseDao<MsCtAlarm> implements CtAlarmMapper {
	Logger logger=Logger.getLogger(CtAlarmMapperImpl.class);
	public CtAlarmMapperImpl(SqlSessionTemplate session) {
		super(session, CtAlarmMapper.class.getName());
		// TODO Auto-generated constructor stub
	}

	@Override
	public void getAlarmPage(Page<MsCtAlarm, MsCtAlarm> page) {
		// TODO Auto-generated method stub
		logger.error(page.getCondition().getConfirmed());
		this.select("getAlarmPage",page);
	}

	@Override
	public int insertAlarm(MsCtAlarm msCtAlarm) {
		// TODO Auto-generated method stub
		return super.insert(msCtAlarm);
	}

	@Override
	public int editAlarm(MsCtAlarm msCtAlarm) {
		// TODO Auto-generated method stub
		return super.update(msCtAlarm);
	}

	@Override
	public MsCtAlarm getById(long id) {
		// TODO Auto-generated method stub
		return super.get("getById", id);
	}

}
