package com.mainsteam.stm.common.metric.sync;

import java.util.Date;
import java.util.List;

import com.mainsteam.stm.common.sync.DataSyncService;
import com.mainsteam.stm.profilelib.interceptor.ProfileMetricMonitorChange;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import com.alibaba.fastjson.JSON;
import com.mainsteam.stm.common.sync.DataSyncPO;
import com.mainsteam.stm.common.sync.DataSyncTypeEnum;
import com.mainsteam.stm.profilelib.interceptor.ProfileMetricAlarmChange;
import com.mainsteam.stm.profilelib.obj.ProfileChangeData;

public class MetricAlarmChangeMoinitor implements ProfileMetricAlarmChange,ProfileMetricMonitorChange{
	Logger logger=LoggerFactory.getLogger(MetricAlarmChangeMoinitor.class);
	private DataSyncService dataSyncService;

	public void setDataSyncService(DataSyncService dataSyncService) {
		this.dataSyncService = dataSyncService;
	}

	@Override
	public void metricAlarmChange(List<ProfileChangeData> list) throws Exception {
		deal(list);
	}

	@Override
	public void metricMonitorChange(List<ProfileChangeData> ProfileMetricChanges) throws Exception {
		deal(ProfileMetricChanges);
	}

	private void deal(List<ProfileChangeData> list) throws Exception{
		if(logger.isInfoEnabled())
			logger.info("metricAlarmChange:"+JSON.toJSONString(list));

		for(ProfileChangeData change:list){

			MetricProfileChange pc=new MetricProfileChange();
			pc.setProfileID(change.getProfileId());
			pc.setMetricID(change.getMetricId());
			pc.setIsAlarm(change.getIsAlarm());
			pc.setIsMonitor(change.getIsMonitor());
			pc.setCustomMetric(false);

			DataSyncPO po=new DataSyncPO();
			po.setCreateTime(new Date());
			po.setUpdateTime(po.getCreateTime());
			po.setType(DataSyncTypeEnum.METRIC_STATE);
			po.setData(JSON.toJSONString(pc));

			dataSyncService.save(po);
			logger.info("save metric["+change.getProfileId()+","+change.getMetricId()+"] profile Change");
		}
		logger.info("metricAlarmChange finish!");
	}
}
