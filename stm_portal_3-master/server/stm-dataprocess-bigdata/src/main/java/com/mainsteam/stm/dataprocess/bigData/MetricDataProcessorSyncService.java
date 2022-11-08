package com.mainsteam.stm.dataprocess.bigData;

import java.io.UnsupportedEncodingException;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.HashMap;
import java.util.Map;

import com.mainsteam.stm.instancelib.exception.InstancelibException;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import com.alibaba.fastjson.JSON;
import com.mainsteam.stm.caplib.dict.LinkResourceConsts;
import com.mainsteam.stm.caplib.dict.MetricTypeEnum;
import com.mainsteam.stm.caplib.resource.ResourceMetricDef;
import com.mainsteam.stm.dataprocess.MetricCalculateData;
import com.mainsteam.stm.dataprocess.bigData.bo.MetricDataLine;
import com.mainsteam.stm.dataprocess.engine.MetricDataPersistence;
import com.mainsteam.stm.dataprocess.engine.MetricDataProcessor;
import com.mainsteam.stm.instancelib.ResourceInstanceService;
import com.mainsteam.stm.instancelib.obj.ResourceInstance;
import com.mainsteam.stm.metric.obj.CustomMetric;

public class MetricDataProcessorSyncService implements MetricDataProcessor {

	private static final Log logger=LogFactory.getLog(MetricDataProcessorSyncService.class);
	private ResourceInstanceService instanceService;
	private UdpSenderForBigData udpSender;

	private static final int SEND_BY_SOCKET = 2;
	private static final int SEND_BY_KAFKA = 1;

	public void setInstanceService(ResourceInstanceService instanceService) {
		this.instanceService = instanceService;
	}

	public void setUdpSender(UdpSenderForBigData udpSender) {
		this.udpSender = udpSender;
	}

	@Override
	public MetricDataPersistence process(MetricCalculateData data,ResourceMetricDef rdf, CustomMetric cm,Map<String, Object> contextData) throws Exception {
		
		if(!udpSender.allowSync() || data.getMetricData()==null || data.getMetricData()[0]==null){
			return null;
		}
		if(cm==null && (rdf.getMetricType() == MetricTypeEnum.PerformanceMetric || rdf.getMetricType() == MetricTypeEnum.InformationMetric)
				&& !data.getResourceId().equals(LinkResourceConsts.RESOURCE_LAYER2LINK_ID)){

			switch (UdpSenderForBigData.getSendMethod()) {
				case SEND_BY_KAFKA:
					sendByKafka(data);
					break;
				case SEND_BY_SOCKET:
					sendBySocket(data, rdf);
					break;
			}

		}
		return null;
	}


	public void sendByKafka(MetricCalculateData metricData) {
		Map<String,Map<String,String>> messageMap = new HashMap<>(1);
		Map<String, String> properties = new HashMap<String, String>(11);
		messageMap.put("content", properties);
		properties.put("sourceType", "ITM");
		properties.put("sourceId", udpSender.getItbaSourceId());// 告警唯一标志
		properties.put("dataType", "KPIPerf");
		DateFormat format = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
		properties.put("dateTime", format.format(metricData.getCollectTime()));
		try {
			ResourceInstance resourceInstance = instanceService.getResourceInstance(metricData.getResourceInstanceId());
			if(resourceInstance != null) {
				if(resourceInstance.getParentId() > 0) {
					properties.put("ciId", String.valueOf(resourceInstance.getParentId()));
					properties.put("subId", String.valueOf(resourceInstance.getId()));
				}else {
					properties.put("ciId", String.valueOf(resourceInstance.getId()));
					properties.put("subId", "");
				}

			}else {
				if(logger.isWarnEnabled()) {
					logger.warn("instance is null, when sending :" + metricData);
				}
			}
		} catch (InstancelibException e) {
			if(logger.isWarnEnabled()) {
				logger.warn(e.getMessage(), e);
			}
		}
		properties.put("metricId", metricData.getMetricId());
		properties.put("metricValue", null!=metricData.getMetricData()&&metricData.getMetricData().length !=0 ? metricData.getMetricData()[0]:"null");

		udpSender.sendMsg(JSON.toJSONString(messageMap));
	}

	public void sendBySocket(MetricCalculateData data, ResourceMetricDef rdf) throws InstancelibException, UnsupportedEncodingException {
		ResourceInstance ins=instanceService.getResourceInstance(data.getResourceInstanceId());
		if(ins!=null) {
			MetricDataLine md = new MetricDataLine();

			md.setDateTime(data.getCollectTime());
			md.setResourceId(data.getResourceId());
			md.setMetricId(rdf.getId());

			ResourceInstance pins = ins.getParentInstance();
			if (pins != null) {
				md.setInstanceId(pins.getId());
				md.setInstanceName(pins.getShowName() != null ? pins.getShowName() : pins.getName());
				md.setSubInstanceId(ins.getId());
				md.setSubInstanceName(ins.getShowName() != null ? ins.getShowName() : ins.getName());
			} else {
				md.setInstanceId(ins.getId());
				md.setInstanceName(ins.getShowName() != null ? ins.getShowName() : ins.getName());
			}

			md.setMetricValue(data.getMetricData()[0]);

			udpSender.sendMsg(JSON.toJSONString(md).getBytes("UTF-8"),
					rdf.getMetricType() == MetricTypeEnum.PerformanceMetric ? UdpSenderForBigData.SYNC_DATA_METRIC : UdpSenderForBigData.SYNC_DATA_INFO_METRIC);
		}
	}

	@Override
	public int getOrder() {
		return 20;
	}

}
