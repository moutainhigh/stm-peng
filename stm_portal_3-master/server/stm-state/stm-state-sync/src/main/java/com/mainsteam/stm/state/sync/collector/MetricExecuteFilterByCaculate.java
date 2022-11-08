package com.mainsteam.stm.state.sync.collector;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.beans.factory.InitializingBean;

import com.alibaba.fastjson.JSON;
import com.mainsteam.stm.caplib.CapacityService;
import com.mainsteam.stm.caplib.dict.MetricTypeEnum;
import com.mainsteam.stm.caplib.resource.ResourceMetricDef;
import com.mainsteam.stm.common.instance.obj.InstanceStateEnum;
import com.mainsteam.stm.executor.MetricExecuteFilter;
import com.mainsteam.stm.executor.obj.MetricExecuteParameter;
import com.mainsteam.stm.instancelib.ResourceInstanceService;
import com.mainsteam.stm.instancelib.obj.ResourceInstance;
import com.mainsteam.stm.node.NodeFunc;
import com.mainsteam.stm.rpc.client.OCRPCClient;
import com.mainsteam.stm.state.obj.InstanceStateData;
import com.mainsteam.stm.state.sync.processer.InstanceStateCatchMBean;

public class MetricExecuteFilterByCaculate implements MetricExecuteFilter,InitializingBean{
	private static final Log logger=LogFactory.getLog(MetricExecuteFilterByCaculate.class);
	private ResourceInstanceService resourceInstanceService;
	private CapacityService capacityService;
	public static final Map<Long,Boolean> InstanceFaultMap=new HashMap<>();
	private OCRPCClient client;


	public void setClient(OCRPCClient client) {
		this.client = client;
	}
	public void setResourceInstanceService(ResourceInstanceService resourceInstanceService) {
		this.resourceInstanceService = resourceInstanceService;
	}
	public void setCapacityService(CapacityService capacityService) {
		this.capacityService = capacityService;
	}
	
	@Override
	public boolean filter(MetricExecuteParameter parameter) {
		boolean rst=false;
		try {
			ResourceInstance instance = resourceInstanceService.getResourceInstance(parameter.getResourceInstanceId());
			if(instance!=null){
				if(instance.getParentId()>0){
					Boolean fault=InstanceFaultMap.get(instance.getParentId());
					if(fault!=null && fault){
						rst= true;
					}
				}
				if(rst!=true){
					Boolean fault=InstanceFaultMap.get( parameter.getResourceInstanceId());
					if(fault!=null && fault){
						ResourceMetricDef rmd=capacityService.getResourceMetricDef(instance.getResourceId(), parameter.getMetricId());
						rst=  MetricTypeEnum.AvailabilityMetric !=rmd.getMetricType();
					}else {
						rst=false;
					}
				}
			}else{
				if(logger.isWarnEnabled())
					logger.warn("can't find instance:["+parameter.getResourceInstanceId()+"]");
			}
		} catch (Exception e) {
			if(logger.isErrorEnabled())
				logger.error(e.getMessage(),e);
		}
		if(logger.isDebugEnabled())
			logger.debug("filter result["+rst+"] for:"+parameter.getResourceInstanceId()+","+parameter.getMetricId());
		return rst;
	}
	@Override
	public void afterPropertiesSet() throws Exception {
		try{
			InstanceStateCatchMBean isc = client.getParentRemoteSerivce(NodeFunc.processer,InstanceStateCatchMBean.class);
			
			List<InstanceStateData> list=isc.catchState();
			if(logger.isInfoEnabled())
				logger.info("sync instanceState:"+JSON.toJSONString(list));
			for(InstanceStateData ist:list){
				boolean filter=InstanceStateEnum.isCriticalForIns(ist.getState());
//				boolean filter=InstanceStateEnum.isCriticalForIns(ist.getState())||InstanceStateEnum.isUnknownForIns(ist.getState());
//				if(filter){
//					ResourceInstance ins=resourceInstanceService.getResourceInstance(ist.getCauseByInstance());
//					ResourceMetricDef  rmd=capacityService.getResourceMetricDef(ins.getResourceId(), ist.getCauseBymetricID());
//					if(rmd!=null){
//						filter=(rmd.getMetricType()==MetricTypeEnum.AvailabilityMetric);
//					}else{
//						logger.warn("can't find ResourceMetricDef for instance["+ist.getCauseByInstance()+","+ist.getCauseBymetricID()+"]");
//					}
//				}
				InstanceFaultMap.put(ist.getInstanceID(),filter);
			}
		}catch(Exception e){
			logger.error(e.getMessage(),e);
		}
	}
}
