package com.mainsteam.stm.ct.impl;


import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import com.mainsteam.stm.ct.api.ICtAlarmService;
import com.mainsteam.stm.ct.bo.MsCtAlarm;
import com.mainsteam.stm.ct.dao.CtAlarmMapper;
import com.mainsteam.stm.platform.mybatis.plugin.pagination.Page;

public class CtAlarmServiceImpl implements ICtAlarmService {
	Logger log=Logger.getLogger(CtAlarmServiceImpl.class);
    @Autowired
    private CtAlarmMapper ctAlarmMapper;


	@Override
	public void getAlarmPage(Page<MsCtAlarm, MsCtAlarm> page) {
		// TODO Auto-generated method stub
		
		ctAlarmMapper.getAlarmPage(page);
	}

	@Override
	public int insertAlarm(MsCtAlarm msCtAlarm) {
		// TODO Auto-generated method stub
		return ctAlarmMapper.insertAlarm(msCtAlarm);
	}

	@Override
	public int editAlarm(MsCtAlarm msCtAlarm) {
		// TODO Auto-generated method stub
		int i=0;
		try {
			log.error("alarm:"+msCtAlarm.getMessage()+"confi:"+msCtAlarm.getConfirmed());
			i=ctAlarmMapper.editAlarm(msCtAlarm);
		} catch (Exception e) {
			log.error(e.getMessage());
			return 0;
		}
		return i;
	}

	@Override
	public MsCtAlarm getById(String string) {
		// TODO Auto-generated method stub
		log.error("service id:"+string);
		try {
			return ctAlarmMapper.getById(Long.parseLong(string));
		} catch (Exception e) {
			// TODO: handle exception
			log.error("Exception:"+e.getMessage());
			return null;
		}
		
	}
}
