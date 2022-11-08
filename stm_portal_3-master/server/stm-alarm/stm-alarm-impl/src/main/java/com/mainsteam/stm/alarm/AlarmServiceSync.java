package com.mainsteam.stm.alarm;

import java.util.Date;

import com.mainsteam.stm.common.sync.DataSyncService;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import com.alibaba.fastjson.JSON;
import com.mainsteam.stm.alarm.obj.AlarmSenderParamter;
import com.mainsteam.stm.common.sync.DataSyncPO;
import com.mainsteam.stm.common.sync.DataSyncTypeEnum;

public class AlarmServiceSync implements AlarmService{
	private Log logger=LogFactory.getLog(AlarmServiceSync.class);
	
	private DataSyncService dataSyncService;

	public void setDataSyncService(DataSyncService dataSyncService) {
		this.dataSyncService = dataSyncService;
	}

	@Override
	public void notify(AlarmSenderParamter paramter) {
		if(paramter.getLevel()==null){
			throw new RuntimeException("alarmLevel is NULL,please check!");
		}
		
		if(logger.isInfoEnabled()){
			logger.info("AlarmSyncService receive AlarmSenderParamter "+JSON.toJSONString(paramter));
		}
		DataSyncPO po=new DataSyncPO();
		po.setType(DataSyncTypeEnum.ALARM);
		po.setData(JSON.toJSONString(paramter));
		po.setUpdateTime(new Date());
		po.setCreateTime(new Date());
		dataSyncService.save(po);
	}
}
