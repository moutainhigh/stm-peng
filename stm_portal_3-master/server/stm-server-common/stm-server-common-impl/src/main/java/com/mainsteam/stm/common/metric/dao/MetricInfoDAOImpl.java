package com.mainsteam.stm.common.metric.dao;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.ibatis.session.SqlSession;

import com.mainsteam.stm.caplib.dict.MetricTypeEnum;
import com.mainsteam.stm.common.metric.MetricDataBatchPersister;
import com.mainsteam.stm.common.metric.MetricDataBatchPersisterFactory;
import com.mainsteam.stm.common.metric.obj.MetricData;
import com.mainsteam.stm.common.metric.obj.MetricInfoDataPO;


public class MetricInfoDAOImpl implements MetricInfoDAO {

	private SqlSession session;
	
	private MetricDataBatchPersister metricDataBatchPersister;

	public void setMetricDataBatchPersisterFactory(
			MetricDataBatchPersisterFactory metricDataBatchPersisterFactory) {
		this.metricDataBatchPersister = metricDataBatchPersisterFactory
				.getMetricDataBatchPersister(MetricTypeEnum.InformationMetric);
	}
	
	public void setSession(SqlSession session) {
		this.session = session;
	}	
	@Override
	public void addMetricInfoData(MetricData data) {
		metricDataBatchPersister.saveData(MetricInfoDataPO.convert(data));
	}
	
	public void start(){
	}
	
	
	@Override
	public MetricData getMetricInfoData(long instanceID, String metricID) {
		Map<String,Object> map=new HashMap<>();
		map.put("instanceID", instanceID);
		map.put("metricID", metricID);
		return this.session.selectOne("getMetricInfoData",map);
	}
	@Override
	public List<MetricData> getMetricInfoDatas(long[] instanceIDes,String[] metricID) {
		Map<String,Object> map=new HashMap<>();
		map.put("instanceIDes", instanceIDes);
		map.put("metricID", metricID);
		return this.session.selectList("getMetricInfoDatas",map);
	}

}
