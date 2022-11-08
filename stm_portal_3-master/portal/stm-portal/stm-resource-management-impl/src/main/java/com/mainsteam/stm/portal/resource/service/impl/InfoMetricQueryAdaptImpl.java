package com.mainsteam.stm.portal.resource.service.impl;

import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.annotation.Resource;

import org.apache.log4j.Logger;

import com.mainsteam.stm.common.metric.MetricDataService;
import com.mainsteam.stm.common.metric.obj.MetricData;
import com.mainsteam.stm.instancelib.CustomModulePropService;
import com.mainsteam.stm.instancelib.obj.CustomModuleProp;
import com.mainsteam.stm.portal.resource.api.InfoMetricQueryAdaptApi;

public class InfoMetricQueryAdaptImpl implements InfoMetricQueryAdaptApi{

	Logger logger = Logger.getLogger(InfoMetricQueryAdaptImpl.class);
	
	private static final String ifSpeed = "ifSpeed";
	@Resource
	private MetricDataService metricDataService;
	@Resource
	private CustomModulePropService customModulePropService;
	
	
	public MetricData getMetricInfoData(long instanceId,String metricId){
		String[] value = new String[1];
		boolean ifUserSet = false;
		if(ifSpeed.equals(metricId)){
			List<CustomModuleProp> allModuleProp = customModulePropService.getCustomModuleProp();
			if(null!=allModuleProp){
				for(CustomModuleProp cmp:allModuleProp){
					  if(cmp.getInstanceId()==instanceId && cmp.getKey().equals(ifSpeed)){
						  value[0] = cmp.getUserValue();
						  ifUserSet = true;
						  break;
					  }
				 }
			}
		}
		MetricData md = metricDataService.getMetricInfoData(instanceId, metricId);
		if(ifUserSet && md!=null){
			md.setData(value);
			if(null==md.getCollectTime()){
				md.setCollectTime(new Date());
			}
		}
		return md;
	}
	
	public List<MetricData>  getMetricInfoDatas(long instanceId,String[] metricIds){
		String[] value = new String[1];
		boolean ifUserSet = false;
		for(String metricId:metricIds){
			if(ifSpeed.equals(metricId)){
				List<CustomModuleProp> allModuleProp = customModulePropService.getCustomModuleProp();
				if(null!=allModuleProp){
					for(CustomModuleProp cmp:allModuleProp){
						  if(cmp.getInstanceId()==instanceId && cmp.getKey().equals(ifSpeed)){
							  value[0] = cmp.getUserValue();
							  ifUserSet = true;
							  break;
						  }
					 }
				}
				break;
			}
		}
		List<MetricData> mdList = metricDataService.getMetricInfoDatas(instanceId, metricIds);
		if(ifUserSet && null!=mdList && mdList.size()>0){
			for(MetricData md:mdList){
				if(md.getMetricId().equals(ifSpeed)){
					md.setData(value);
					if(null==md.getCollectTime()){
						md.setCollectTime(new Date());
					}
					break;
				}
			}
		}
		return mdList;
	}
	
	public List<MetricData>  getMetricInfoDatas(long[] instanceIds,String[] metricIds){
		boolean ifUserSet = false;
		Map<Long,String[]> valueMap = new HashMap<Long,String[]>();
		for(String metricId:metricIds){
			if(ifSpeed.equals(metricId)){
				List<CustomModuleProp> allModuleProp = customModulePropService.getCustomModuleProp();
				if(null!=allModuleProp){
					 for(long instanceid:instanceIds){
						 for(CustomModuleProp cmp:allModuleProp){
							  if(instanceid==cmp.getInstanceId() && cmp.getKey().equals(ifSpeed)){
								  String[] value = new String[1];
								  value[0] = cmp.getUserValue();
								  valueMap.put(new Long(instanceid), value);
								  ifUserSet = true;
								  break;
							  }
						  }
					}
				}
				break;
			}
		}
		List<MetricData> mdList = metricDataService.getMetricInfoDatas(instanceIds, metricIds);
		
		if(ifUserSet && null!=mdList && mdList.size()>0){
			for(Long instanceid:valueMap.keySet()){
				for(MetricData md:mdList){
					if(md.getMetricId().equals(ifSpeed) && md.getResourceInstanceId()==instanceid){
						md.setData(valueMap.get(instanceid));
						if(null==md.getCollectTime()){
							md.setCollectTime(new Date());
						}
						break;
					}
				}
			}
		}
		return mdList;
	}
}
