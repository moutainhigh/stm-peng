package com.mainsteam.stm.common.metric.obj;

import org.apache.commons.lang.StringUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import com.alibaba.fastjson.JSON;

public class MetricInfoDataPO extends MetricData{
	private static final Log logger=LogFactory.getLog(MetricInfoDataPO.class);
	private static final long serialVersionUID = 1L;

	private String metricData;
	
	public String getMetricData() {
		
		String data[]=super.getData();
		return data!=null&&data.length>0?JSON.toJSONString(data):null;
	}

	public void setMetricData(String metricData) {
		this.metricData = metricData;
		if(!StringUtils.isEmpty(metricData)){
			try{
				super.setData(JSON.parseArray(metricData, String.class).toArray(new String[]{}));
			}catch(Exception e){
				logger.error(e.getMessage(),e);
				super.setData(new String[]{metricData});
			}
		}
	}
	public static MetricInfoDataPO convert(MetricData md){
		MetricInfoDataPO po=new MetricInfoDataPO();
		po.setCollectTime(md.getCollectTime());
		po.setMetricId(md.getMetricId());
		po.setResourceId(md.getResourceId());
		po.setProfileId(md.getProfileId());
		po.setTimelineId(md.getTimelineId());
		po.setResourceInstanceId(md.getResourceInstanceId());
		po.setData(md.getData());
//		if(md.getData()!=null&&md.getData().length>0){
//			po.setMetricData(JSON.toJSONString(md.getData()));
//		}
		return po;
	}
}
