package com.mainsteam.stm.executor;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import com.mainsteam.stm.caplib.CapacityService;
import com.mainsteam.stm.caplib.dict.MetricTypeEnum;
import com.mainsteam.stm.caplib.resource.ResourceDef;
import com.mainsteam.stm.caplib.resource.ResourceMetricDef;
import com.mainsteam.stm.common.metric.obj.MetricData;
import com.mainsteam.stm.dataprocess.MetricCalculateData;
import com.mainsteam.stm.executor.exception.MetricExecutorException;
import com.mainsteam.stm.executor.obj.CustomMetricPluginParameter;
import com.mainsteam.stm.executor.obj.InstanceMetricExecuteParameter;
import com.mainsteam.stm.executor.obj.MetricExecuteParameter;
import com.mainsteam.stm.instancelib.ResourceInstanceService;
import com.mainsteam.stm.instancelib.obj.ResourceInstance;
import com.mainsteam.stm.transfer.MetricDataTransferSender;
import com.mainsteam.stm.transfer.obj.TransferData;
import com.mainsteam.stm.transfer.obj.TransferDataType;
import com.mainsteam.stm.util.SpringBeanUtil;

public class MetricRpcExecutor implements MetricRpcExecutorMBean {

	private static final Log logger = LogFactory.getLog(MetricRpcExecutor.class);

	private MetricExecutor metricExecutor;
	private CapacityService capacityService;
	private ResourceInstanceService resourceInstanceService;
	private MetricDataTransferSender metricDataTransferSender;
	private HashMap<Long,String> collectInstance = new HashMap<>(20);
	
	public void setMetricExecutor(MetricExecutor metricExecutor) {
		this.metricExecutor = metricExecutor;
	}

	@Override
	public MetricData catchRealtimeMetricData(long instanceID, String metricID)throws MetricExecutorException {
		return metricExecutor.catchRealtimeMetricData(instanceID, metricID);
	}
	
	@Override
	public MetricData catchRealtimeMetricDataWithParameter(long instanceID,String metricID, Map<String, String> params) throws MetricExecutorException {
		return metricExecutor.catchRealtimeMetricData(instanceID, metricID,params);
	}

	@Override
	public List<MetricData> catchRealtimeMetricData(
			List<InstanceMetricExecuteParameter> parameters)
			throws MetricExecutorException {
		return metricExecutor.catchRealtimeMetricData(parameters);
	}

	@Override
	public MetricData getMetricDataWithCustomPlugin(
			CustomMetricPluginParameter customMetricPluginParameter)
			throws MetricExecutorException {
		return metricExecutor
				.catchMetricDataWithCustomPlugin(customMetricPluginParameter);
	}

	@Override
	public void fireCollectMetricDatas(
			List<InstanceMetricExecuteParameter> parameters) {
		if (parameters != null && parameters.size() > 0) {
			Date currentDate = new Date();
			List<MetricExecuteParameter> executeParameters = new ArrayList<>(
					parameters.size());
			for (InstanceMetricExecuteParameter instanceMetricExecuteParameter : parameters) {
				MetricExecuteParameter parameter = new MetricExecuteParameter();
				parameter.setExecuteTime(currentDate);
				parameter.setMetricId(instanceMetricExecuteParameter
						.getMetricId());
				parameter.setResourceInstanceId(instanceMetricExecuteParameter
						.getResourceInstanceId());
				executeParameters.add(parameter);
				if (logger.isInfoEnabled()) {
					StringBuilder b = new StringBuilder(
							"fireCollectMetricDatas ");
					b.append(" instanceId=").append(
							parameter.getResourceInstanceId());
					b.append(" metricId=").append(parameter.getMetricId());
					logger.info(b.toString());
				}
			}
			if (executeParameters.size() > 0) {
				metricExecutor.execute(executeParameters);
			}
		}
	}

	@Override
	public List<MetricData> catchRealtimeMetricDatas(List<Long> instanceIDes,String metricID) throws MetricExecutorException {
		
		List<MetricData> list=new ArrayList<MetricData>();
		for(long instanceID:instanceIDes){
			MetricData md=catchRealtimeMetricData(instanceID, metricID);
			if(md!=null){
				list.add(md);
			}
		}
		return list; 
	}
	
	@Override
	public boolean isMetricGather(long parentInstanceID){
		boolean result = false;
		synchronized (collectInstance) {
			String value =  collectInstance.get(parentInstanceID);
			if(value != null){
				result = true;
			}
		}
		return result;
	}
	
	@Override
	public void triggerMetricGather(final long parentInstanceID,final boolean containChild){
		Thread th=new Thread(new Runnable() {
			public void run() {
				try {
					if(metricDataTransferSender==null){
						metricDataTransferSender=SpringBeanUtil.getBean(MetricDataTransferSender.class);
					}
					
					ResourceInstance ins=resourceInstanceService.getResourceInstance(parentInstanceID);
					synchronized (collectInstance) {
						collectInstance.put(parentInstanceID, "");
					}
					List<InstanceMetricExecuteParameter> parms = new ArrayList<InstanceMetricExecuteParameter>(100);
					gatherMetric(ins,parms);
					
					if(containChild && ins.getParentId() < 1){
						List<ResourceInstance> children=ins.getChildren();
						if(children != null){
							for(ResourceInstance cins:children){
								gatherMetric(cins,parms);
							}
						}
					}
					if(logger.isInfoEnabled()){
						logger.info("Manual collect start instanceId =" + ins.getId());
					}
					List<MetricData> mDatas= catchRealtimeMetricData(parms);
					if(mDatas !=null && !mDatas.isEmpty()){
						for (MetricData m : mDatas) {
							MetricCalculateData md=new MetricCalculateData();
							md.setCollectTime(m.getCollectTime());
							md.setMetricData(m.getData());
							md.setMetricId(m.getMetricId());
							md.setResourceId(m.getResourceId());
							md.setResourceInstanceId(m.getResourceInstanceId());
							md.setCustomMetric(false);
							metricDataTransferSender.sendData(new TransferData(TransferDataType.MetricData,md));
							if(logger.isInfoEnabled()){
								logger.info("Manual collect result: " + md);
							}
						}
					}else if(logger.isWarnEnabled()){
						logger.warn("Manual collect instanceId =" + ins.getId() +" result is null,please check!");
					}
					if(logger.isInfoEnabled())
						logger.info("instance["+parentInstanceID+"] collect Metric finish!");
				} catch (Exception e) {
					logger.error(e.getMessage(),e);
				}finally{
					synchronized (collectInstance) {
						collectInstance.remove(parentInstanceID);
					}
				}
			}

			private void gatherMetric(ResourceInstance ins,List<InstanceMetricExecuteParameter> parms) {
				ResourceDef  rdef=capacityService.getResourceDefById(ins.getResourceId());
				for(ResourceMetricDef rmdef : rdef.getMetricDefs()){
					if(!rmdef.isDisplay() && !rmdef.isMonitor()){
						continue;
					}
					InstanceMetricExecuteParameter p = new InstanceMetricExecuteParameter();
					p.setMetricId(rmdef.getId());
					p.setResourceInstanceId(ins.getId());
					parms.add(p);
				}
			}
		});
		
		th.start();
		
		if(logger.isInfoEnabled())
			logger.info("instance["+parentInstanceID+"] start collect Metric!");
	}
	
	@Override
	public void triggerInfoMetricGather(final long parentInstanceID, final boolean containChild) {
		
		Thread th=new Thread(new Runnable() {
			public void run() {
				try {
					if(metricDataTransferSender==null){
						metricDataTransferSender=SpringBeanUtil.getBean(MetricDataTransferSender.class);
					}
					
					ResourceInstance ins=resourceInstanceService.getResourceInstance(parentInstanceID);
					synchronized (collectInstance) {
						collectInstance.put(parentInstanceID, "");
					}
					gatherInfoMetric(ins);
					
					if(containChild && ins.getParentId()<1){
						List<ResourceInstance> children=ins.getChildren();
						if(children != null){
							for(ResourceInstance cins:children){
								gatherInfoMetric(cins);
							}
						}
					}
					if(logger.isInfoEnabled())
						logger.info("instance["+parentInstanceID+"] collect infoMetric finish!");
				} catch (Exception e) {
					logger.error(e.getMessage(),e);
				}finally{
					synchronized (collectInstance) {
						collectInstance.remove(parentInstanceID);
					}
				}
			}

			private void gatherInfoMetric(ResourceInstance ins) throws MetricExecutorException {
				ResourceDef  rdef=capacityService.getResourceDefById(ins.getResourceId());
				for(ResourceMetricDef rmdef : rdef.getMetricDefs()){
					if(rmdef.getMetricType()==MetricTypeEnum.InformationMetric){
//						if(logger.isInfoEnabled())
//							logger.info("instance["+parentInstanceID+"] collect infoMetric["+rmdef.getId()+"] finish!");
						if(!rmdef.isDisplay() && !rmdef.isMonitor()){
							continue;
						}
						MetricData m=catchRealtimeMetricData(ins.getId(), rmdef.getId());
						if(m!=null){
//							if(logger.isInfoEnabled())
//								logger.info("send collected infoMetric:"+JSON.toJSONString(m));
							
							MetricCalculateData md=new MetricCalculateData();
							md.setCollectTime(m.getCollectTime());
							md.setMetricData(m.getData());
							md.setMetricId(m.getMetricId());
							md.setResourceId(m.getResourceId());
							md.setResourceInstanceId(m.getResourceInstanceId());
							
							metricDataTransferSender.sendData(new TransferData(TransferDataType.MetricData,md));
						}else if(logger.isWarnEnabled()){
							logger.warn("collect infoMetric result is null,please check!");
						}
					}
				}
			}
		});
		
		th.start();
		
		if(logger.isInfoEnabled())
			logger.info("instance["+parentInstanceID+"] start collect infoMetric!");
	}


	public void setCapacityService(CapacityService capacityService) {
		this.capacityService = capacityService;
	}

	public void setResourceInstanceService(ResourceInstanceService resourceInstanceService) {
		this.resourceInstanceService = resourceInstanceService;
	}
}
