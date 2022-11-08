package com.mainsteam.stm.dataprocess;

import java.util.Map;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import com.mainsteam.stm.caplib.dict.MetricTypeEnum;
import com.mainsteam.stm.caplib.resource.ResourceMetricDef;
import com.mainsteam.stm.common.metric.MetricDataService;
import com.mainsteam.stm.common.metric.obj.MetricData;
import com.mainsteam.stm.dataprocess.MetricCalculateData;
import com.mainsteam.stm.dataprocess.engine.MetricDataPersistence;
import com.mainsteam.stm.dataprocess.engine.MetricDataProcessor;
import com.mainsteam.stm.metric.obj.CustomMetric;

/**
 * @author cx
 * 存储指标数据
 */
public class MetricDataStoreService implements MetricDataProcessor {
	
	private static final Log logger = LogFactory.getLog(MetricDataStoreService.class);
	private MetricDataService metricDataService;
	
	
	public void setMetricDataService(MetricDataService metricDataService) {
		this.metricDataService = metricDataService;
	}
	
	@Override
	public MetricDataPersistence process(MetricCalculateData calculateData,ResourceMetricDef rdf,CustomMetric cm,Map<String, Object> contextData) throws Exception {
//		if (calculateData.getProfileId() <= 0 && calculateData.getTimelineId() <= 0){
//			return null;
//		}
		//保存数据
		MetricData data=new MetricData();
		data.setCollectTime(calculateData.getCollectTime());
		if(calculateData.getMetricData()==null ||calculateData.getMetricData().length<1){
			data.setData(null);
		}else{
			data.setData(calculateData.getMetricData());
		}
		data.setMetricId(calculateData.getMetricId());
		if(calculateData.getTimelineId()>0)
			data.setTimelineId(calculateData.getTimelineId());
		if(calculateData.getProfileId()>0)
			data.setProfileId(calculateData.getProfileId());
		data.setResourceId(calculateData.getResourceId());
		data.setResourceInstanceId(calculateData.getResourceInstanceId());
		
		if(calculateData.isCustomMetric()){
			MetricTypeEnum metricType=cm.getCustomMetricInfo().getStyle();
			data.setMetricType(metricType);
			data.setCustomMetric(true);
			if(metricType==MetricTypeEnum.PerformanceMetric){
				String val=data.getData()[0];
				try{
					Double.valueOf(val);
				}catch(Exception e){
					logger.error("illegal PerformanceMetric value:"+val);
					data.setData(new String[]{null});
				}
			}
			metricDataService.addCustomerMetricData(data);
		}else{
			MetricTypeEnum type=rdf.getMetricType();
			if(MetricTypeEnum.InformationMetric.equals(type)){
				metricDataService.addMetricInfoData(data);
			}else if(MetricTypeEnum.AvailabilityMetric.equals(type) ){
				metricDataService.updateAvailableMetricData(data);
			}else if(MetricTypeEnum.PerformanceMetric.equals(type)){
				metricDataService.updatePerformanceMetricData(data);
			}
		}
		return null;
	}
	
	@Override
	public int getOrder() {
		return 0;
	}

}
