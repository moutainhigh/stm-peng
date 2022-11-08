package com.mainsteam.stm.webService.metric;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;

import javax.annotation.Resource;
import javax.jws.WebService;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import com.alibaba.fastjson.JSONObject;
import com.mainsteam.stm.caplib.CapacityService;
import com.mainsteam.stm.caplib.common.CategoryDef;
import com.mainsteam.stm.caplib.common.DeviceType;
import com.mainsteam.stm.caplib.dict.MetricIdConsts;
import com.mainsteam.stm.caplib.resource.ResourceMetricDef;
import com.mainsteam.stm.common.instance.obj.InstanceStateEnum;
import com.mainsteam.stm.common.metric.InstanceStateService;
import com.mainsteam.stm.common.metric.MetricDataService;
import com.mainsteam.stm.common.metric.obj.MetricData;
import com.mainsteam.stm.instancelib.CustomModulePropService;
import com.mainsteam.stm.instancelib.ResourceInstanceService;
import com.mainsteam.stm.instancelib.exception.InstancelibException;
import com.mainsteam.stm.instancelib.obj.CustomModuleProp;
import com.mainsteam.stm.instancelib.obj.ResourceInstance;
import com.mainsteam.stm.instancelib.objenum.InstanceLifeStateEnum;
import com.mainsteam.stm.state.obj.InstanceStateData;
import com.mainsteam.stm.webService.obj.Result;
import com.mainsteam.stm.webService.obj.ResultCodeEnum;
/**
 * @Author: sunhailiang
 * @Date: 2017/12/15
 *
 */
@WebService
public class MetricWebServicesImpl implements MetricWebServices {
	//系统oid信息指标指标
	public static final String[] SYSOID_METRIC_ID = new String[]{"sysObjectID"};
	//网络接口个数信息指标
	public static final String[] IFNUM_METRIC_ID = new String[]{"ifNum"};
	//接口带宽信息指标
	public static final String[] IFSPEED_METRIC_ID = new String[]{"ifSpeed"};
	
	//接口子资源类型 RESOURCE_TYPE
	public static final String NETINTERFACE_RESOURCE_TYPE = "NetInterface";
	
	//主资源吞吐量指标
	public static final String THROUGHPUT_METRIC_ID = MetricIdConsts.METRIC_THROUGHPUT;
	
	public static final String[] FLOW_METRIC_IDS = {"ifInOctetsSpeed","ifOutOctetsSpeed","ifInUcastPktsRate","ifOutUcastPktsRate"};
	
	private static final Log logger = LogFactory.getLog(MetricWebServicesImpl.class);
	@Resource
	private CapacityService capacityService;
	@Resource
	private ResourceInstanceService resourceInstanceService;
	@Resource
	private InstanceStateService instanceStateService;
	@Resource
	private CustomModulePropService customModulePropService;
	@Resource
	private MetricDataService metricDataService;

	@Override
	public String getMetricInfoList(){
	    HashSet<String> categroyIdSet = getCategoryIdSet(CategoryTypeEnum.NETWORKDEVICE);
	    List<ResourceInstance> resourceInstanceList;
	    Map<Long,MetricInfoBean> beanMap = new HashMap<Long,MetricInfoBean>();
	    Result result = new Result();
		try {
			//1.获取网络设备资源实例
			resourceInstanceList = resourceInstanceService.getParentInstanceByCategoryIds(categroyIdSet);
		    if(resourceInstanceList != null && !resourceInstanceList.isEmpty()){
		    	//2.获取instanceIds
				long[] instanceIds = getInstanceIds(resourceInstanceList, beanMap,true);
				//3.获取sysOid
				List<MetricData> sysOidMetricList = getMetricInfoDataList(instanceIds, SYSOID_METRIC_ID);
				//4.获取设备信息
				doDeviceInfo(sysOidMetricList, beanMap); 
				//5.端口数量
				List<MetricData> ifNumList = getMetricInfoDataList(instanceIds, IFNUM_METRIC_ID);
				doIfNum(ifNumList, beanMap);
				//6.router接口带宽能力 
			    doIfSpeed(beanMap,resourceInstanceList);
		    } 
		} catch (InstancelibException e) {
			if(logger.isErrorEnabled()){
				logger.error(this.getClass().getName()+" getMetricInfoList method error:"+e);
			}
			result.setData("");
			result.setErrorMsg(e.getMessage());
			result.setResultcodeEnum(ResultCodeEnum.RESULT_QueryMainResourceError_CODE);
			return JSONObject.toJSONString(result);
		}
		
		if(beanMap != null && !beanMap.isEmpty()){
			List<MetricInfoBean> list = new ArrayList<MetricInfoBean>();
			for(Long id : beanMap.keySet()){
				list.add(beanMap.get(id));
			}
			result.setData(list);
			result.setResultcodeEnum(ResultCodeEnum.RESULT_NORMAL_CODE); 
		}else{
			result.setData("");
			result.setResultcodeEnum(ResultCodeEnum.RESULT_NULL_CODE); 
		}
		
		return JSONObject.toJSONString(result);
	}
    
	private long[] getArrayByList(List<Long> source){
		if(source != null && !source.isEmpty()){
			long[] target = new long[source.size()];
		     for(int i = 0; i < source.size(); i++){
		    	 target[i] = source.get(i);
		     }
		     return target;
		}
		return null;
	}
	private void doIfSpeed(Map<Long,MetricInfoBean> beanMap,List<ResourceInstance> resourceInstanceList) throws InstancelibException {
		Map<Long,String> customModuleMap = getCustomModuleMap(IFSPEED_METRIC_ID[0]);
		for(ResourceInstance parent : resourceInstanceList){
			long pId = parent.getId();
			if(CategoryTypeEnum.ROUTER.getVal().equals(parent.getCategoryId()) && InstanceLifeStateEnum.MONITORED.equals(parent.getLifeState())){
			   List<ResourceInstance> childList = resourceInstanceService.getChildInstanceByParentId(pId);
			   if(childList != null && !childList.isEmpty() && beanMap.keySet().contains(pId)){
				   List<InfoMetricSumaryDataBean> netfaceInstanceList = new ArrayList<InfoMetricSumaryDataBean>();
				   Map<Long,InfoMetricSumaryDataBean> netInterfaceMap = new HashMap<Long,InfoMetricSumaryDataBean>();
				   List<Long> netInterIds = new ArrayList<Long>(3);
				   String netFaceResourceId = "";
				   int count = 0;
				   for(ResourceInstance child : childList){
					 if(NETINTERFACE_RESOURCE_TYPE.equals(child.getChildType()) && InstanceLifeStateEnum.MONITORED.equals(child.getLifeState())){
						 long  cId = child.getId();
						 InfoMetricSumaryDataBean mBean = new InfoMetricSumaryDataBean();
						 mBean.setInstanceId(cId);
						 mBean.setResourceId(child.getResourceId());
						 mBean.setMetricId(IFSPEED_METRIC_ID[0]);
						 mBean.setUserFlag(false);
						 mBean.setInstanceName(child.getName());
						 netFaceResourceId = child.getResourceId();
						 ResourceMetricDef metricDef = capacityService.getResourceMetricDef(netFaceResourceId,IFSPEED_METRIC_ID[0]);
						 mBean.setUnit(metricDef.getUnit());
						 netInterIds.add(cId);
						 netInterfaceMap.put(cId, mBean);
						 count ++;
					 }
				   }
				  
				   List<MetricData> mData = getMetricInfoDataList(getArrayByList(netInterIds), IFSPEED_METRIC_ID);
				   if(mData != null && !mData.isEmpty()){
					   for(MetricData d : mData){
						  long cId = d.getResourceInstanceId();
						  if(netInterfaceMap.keySet().contains(cId)){
							  InfoMetricSumaryDataBean infoBean = netInterfaceMap.get(cId);
							  infoBean.setCollectTime(d.getCollectTime() == null ? new Date() : d.getCollectTime());
							  infoBean.setProfileId(d.getProfileId());
							  infoBean.setTimelineId(d.getTimelineId());
							  if(customModuleMap.keySet().contains(cId)){
									infoBean.setData(customModuleMap.get(cId)); 
									infoBean.setUserFlag(true);
							  }else{
								  String[] data =  d.getData();
								  if(data != null && data.length >0 && data[0].trim().length() > 0){
									  infoBean.setData(data[0].trim());
								  }
							 }
							 netfaceInstanceList.add(infoBean);
						  }
					   }
				   }
				   MetricInfoBean bean = beanMap.get(pId);
				   bean.setNetFaceResourceId(netFaceResourceId);
				   bean.setNetInterfaceList(netfaceInstanceList);
				   bean.setMonitoredPortNum(count);
				   beanMap.put(pId, bean);
			   } 
			   
			}  
		}
	} 
	 
	/**
	 * 获取用户手动设置指标值
	 * @param metricId
	 * @return
	 */
	private Map<Long,String> getCustomModuleMap(String metricId){
		Map<Long,String> map = new HashMap<Long,String>();
		List<CustomModuleProp> allModuleProp = customModulePropService.getCustomModuleProp();
		if(null != allModuleProp && !allModuleProp.isEmpty()){
			for(CustomModuleProp cmp:allModuleProp){
				  if(cmp.getKey().equals(metricId)){
					  map.put(cmp.getInstanceId(),cmp.getUserValue());
				  }
			 }
		}
		return map;
	}
   
	private void doIfNum(List<MetricData> ifNumList,Map<Long,MetricInfoBean> beanMap){
		if(ifNumList != null && !ifNumList.isEmpty()){
			String ifNum = "";
			for(MetricData d : ifNumList){
				String[] data = d.getData();
				if(data != null && data.length > 0){
					ifNum = (data[0] == null) ? "" : data[0].trim();
				}
				if(ifNum.length() > 0){
					long instanceId = d.getResourceInstanceId();
					if(beanMap.keySet().contains(instanceId)){
						MetricInfoBean bean = beanMap.get(instanceId);
						bean.setPortNum(ifNum);
						beanMap.put(instanceId,bean);
					}
				}
				
			}
		}
	}
	
	private void doDeviceInfo(List<MetricData> sysOidMetricList,Map<Long,MetricInfoBean> beanMap){
		if(sysOidMetricList != null && !sysOidMetricList.isEmpty()){
			DeviceType device;
			String sysOid = "";
			for(MetricData d : sysOidMetricList){
				 String[] data = d.getData(); 
				 if(data != null && data.length >0){
					 sysOid = (data[0] == null) ? "" : data[0].trim();
				 }
				 device = capacityService.getDeviceType(sysOid);
				 if(device != null){
					 long id = d.getResourceInstanceId();
					 if(beanMap.keySet().contains(id)){
						 MetricInfoBean bean = beanMap.get(id);
						 bean.setDeviceSoftVersion(device.getSysOid());
						 bean.setDeviceSerialNo(device.getSeries());
						 bean.setDeviceModel(device.getModelNumber());
						 bean.setVendorName(device.getVendorName());
						 beanMap.put(id, bean);
					 } 
				 }
			}
		} 
	}
	
	private long[] getInstanceIds(List<ResourceInstance> resourceInstanceList,Map<Long,MetricInfoBean> beanMap,boolean flag){
		if(resourceInstanceList != null && !resourceInstanceList.isEmpty()){
			long[] instanceIds = new long[resourceInstanceList.size()];
			long id;
			ResourceInstance instance;
			for(int i = 0; i < resourceInstanceList.size(); i++){
				instance = resourceInstanceList.get(i);
				if(InstanceLifeStateEnum.MONITORED.equals(instance.getLifeState())){
					id = instance.getId();
					MetricInfoBean bean = new MetricInfoBean();
					bean.setInstanceId(id);
					bean.setIp(instance.getShowIP()); 
					bean.setResourceId(instance.getResourceId());
					bean.setDeviceName(instance.getName());
					if(flag){
						InstanceStateData isd = instanceStateService.getState(id);
						bean.setCurrentState(isd == null ? InstanceStateEnum.NORMAL.toString() : isd.getState().toString());
					}
					CategoryDef categoryDef = capacityService.getCategoryById(instance.getCategoryId());
					bean.setCategoryId(categoryDef.getId());
					bean.setCategoryName(categoryDef.getName());
					beanMap.put(id, bean);
					instanceIds[i]=id;
				}
			}
			return instanceIds;
		}
		return new long[0];
	}
	
	private HashSet<String> getCategoryIdSet(CategoryTypeEnum typeEnum) {
		if (typeEnum == null) {
			typeEnum = CategoryTypeEnum.NETWORKDEVICE;
		}
		HashSet<String> categroyIds = new HashSet<String>(3);
		if (typeEnum.equals(CategoryTypeEnum.NETWORKDEVICE)) {
			Set<CategoryTypeEnum> typeSet = CategoryTypeEnum.NETWORKDEVICE
					.getChild();
			for (CategoryTypeEnum e : typeSet) {
				categroyIds.add(e.getVal());
			}
		}
		return categroyIds;
	} 
    
	private List<MetricData> getMetricInfoDataList(long[] instanceIds,String[] metricIds){
		if(instanceIds == null || instanceIds.length == 0){
			return null;
		}
		List<MetricData> list = metricDataService.getMetricInfoDatas(instanceIds, metricIds);
		return list;
	}
	 
    /**********************************接口二**********************************************************/
	
	@Override
	public String getMetricRealSumaryList() {
		HashSet<String> categroyIdSet = getCategoryIdSet(CategoryTypeEnum.NETWORKDEVICE);
	    List<ResourceInstance> resourceInstanceList;
	    Map<Long,MetricRealSummaryBean> beanMap = new HashMap<Long,MetricRealSummaryBean>();
	    Map<Long,MetricInfoBean> InfoBeanMap = new HashMap<Long,MetricInfoBean>();
	    Result result = new Result();
		try {
			//1.获取网络设备资源实例
			resourceInstanceList = resourceInstanceService.getParentInstanceByCategoryIds(categroyIdSet);
			if(resourceInstanceList != null && !resourceInstanceList.isEmpty()){
				//2.获取instanceIds
				long[] instanceIds = getInstanceIds(resourceInstanceList, InfoBeanMap,false);	
				doMetricRealSummaryBeanMap(InfoBeanMap,beanMap);
				Date[] dates = getDates();
				doThroughput(beanMap,dates);
				//<Long 主资源实例id，Map<Long:接口子资源实例id,RealMetricDetailBean>>
				Map<Long,Map<Long,RealMetricDetailBean>> netInterMap = getNetInterface(resourceInstanceList,beanMap);
				if(logger.isDebugEnabled()){
			    	SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
			    	logger.debug(" request operate startDate:"+sdf.format(dates[0])+",endDate:"+sdf.format(dates[1]));
			    }
				doNetInterface(netInterMap,beanMap,dates);
			}
		} catch (InstancelibException e) {
			if(logger.isErrorEnabled()){
				logger.error(this.getClass().getName()+" getMetricRealSumaryList method error:"+e);
			}
			result.setData("");
			result.setErrorMsg(e.getMessage());
			result.setResultcodeEnum(ResultCodeEnum.RESULT_QueryMainResourceError_CODE);
			return JSONObject.toJSONString(result);
		}
		if(beanMap != null && !beanMap.isEmpty()){
			List<MetricRealSummaryBean> list = new ArrayList<MetricRealSummaryBean>();
			for(Long id : beanMap.keySet()){
				list.add(beanMap.get(id));
			}
			result.setData(list);
			result.setResultcodeEnum(ResultCodeEnum.RESULT_NORMAL_CODE); 
		}else{
			result.setData("");
			result.setResultcodeEnum(ResultCodeEnum.RESULT_NULL_CODE); 
		}
		return JSONObject.toJSONString(result);
	} 

	private void doNetInterface(
			Map<Long, Map<Long, RealMetricDetailBean>> netInterMap,
			Map<Long, MetricRealSummaryBean> beanMap,Date[] dates) {
		if(netInterMap != null && !netInterMap.isEmpty()){
			for(Entry<Long,Map<Long,RealMetricDetailBean>> entry : netInterMap.entrySet()){
			 	long pId = entry.getKey();
			 	Map<Long,RealMetricDetailBean> netChildMap = entry.getValue();
			 	MetricRealSummaryBean bean = beanMap.get(pId);
			 	String netFaceResourceId = bean.getNetFaceResourceId();
			 	List<Long> netChildInstanceIdList = bean.getNetChildInstanceIdList();
			 	//流量指标相关采集数据
			 	for(String metricId : FLOW_METRIC_IDS){
			 		if(logger.isDebugEnabled()){
			 			logger.debug(pId+",metricId:"+metricId);
			 		}
			 		if(netChildMap != null && !netChildMap.isEmpty()){
			 			Map<Long,List<MetricDataBean>> mericMap = queryHistoryMetricData(netChildInstanceIdList, metricId, dates);
			 			if(mericMap != null && !mericMap.isEmpty()){
			 				for(Entry<Long,RealMetricDetailBean> e : netChildMap.entrySet()){
				 				long childId = e.getKey();
				 			    if(mericMap.containsKey(childId)){
				 			    	RealMetricDetailBean real = e.getValue(); 
				 			    	RealMetricDetailBean realMetric;
				 			    	if(real == null){
				 			    		realMetric = new RealMetricDetailBean();
				 			    		realMetric.setInstanceId(childId);
				 			    		realMetric.setMain(false);
				 			    	}else{
				 			    		realMetric = new RealMetricDetailBean(real);
				 			    	}
				 			    	realMetric.setMetricId(metricId);
				 			    	realMetric.setMetricDataList(mericMap.get(childId));
				 			    	ResourceMetricDef metricDef = capacityService.getResourceMetricDef(netFaceResourceId,metricId);
				 			    	if(logger.isDebugEnabled()){
				 			    		logger.debug("pId:"+pId+",childId:"+childId+", metricId:"+metricId+",unit:"+metricDef.getUnit()+",data:"+mericMap.get(childId));
				 			    	}
				 			    	realMetric.setUnit(metricDef.getUnit()); 
				 			    	List<RealMetricDetailBean> list = bean.getRealMetricMap().get(metricId);
				 			    	if(list == null){ 
				 			    		 bean.getRealMetricMap().put(metricId, new ArrayList<RealMetricDetailBean>());
				 			    	}
				 			    	bean.getRealMetricMap().get(metricId).add(realMetric);
				 			    }
				 			}
			 			}
			 			
			 		}
			 		
 			    	if(bean.getRealMetricMap().get(metricId) == null){
 			    		bean.getRealMetricMap().put(metricId, new ArrayList<RealMetricDetailBean>());
 			    	} 
 			    	beanMap.put(pId, bean);
			 	}
			 	
			}
		}
	}
    
	private Map<Long,Map<Long,RealMetricDetailBean>> getNetInterface(List<ResourceInstance> resourceInstanceList,
			Map<Long, MetricRealSummaryBean> beanMap) throws InstancelibException {
		Map<Long,Map<Long,RealMetricDetailBean>> netInterMap = new HashMap<Long,Map<Long,RealMetricDetailBean>>();
		for(ResourceInstance parent : resourceInstanceList){
			long pId = parent.getId();
			if((CategoryTypeEnum.ROUTER.getVal().equals(parent.getCategoryId()) || CategoryTypeEnum.FIREWALL.getVal().equals(parent.getCategoryId())) && InstanceLifeStateEnum.MONITORED.equals(parent.getLifeState())){
			   List<ResourceInstance> childList = resourceInstanceService.getChildInstanceByParentId(pId);
			   List<Long> netChildInstanceIdList = new ArrayList<Long>();
			   if(childList != null && !childList.isEmpty() && beanMap.keySet().contains(pId)){
				   Map<Long,RealMetricDetailBean> map = new HashMap<Long,RealMetricDetailBean>();
				   String netFaceResourceId = "";
				   for(ResourceInstance child : childList){
					 if(NETINTERFACE_RESOURCE_TYPE.equals(child.getChildType()) && InstanceLifeStateEnum.MONITORED.equals(child.getLifeState())){
						 long cId = child.getId();
						 RealMetricDetailBean childBean = new RealMetricDetailBean();
						 childBean.setInstanceId(cId);
						 childBean.setInstanceName(child.getName());
						 childBean.setMain(false); 
						 map.put(cId, childBean);
						 netChildInstanceIdList.add(cId);
						 netFaceResourceId = child.getResourceId();
					 }
				   } 
				   
				   MetricRealSummaryBean bean = beanMap.get(pId);
				   bean.setNetFaceResourceId(netFaceResourceId);
				   bean.setNetChildInstanceIdList(netChildInstanceIdList);
				   beanMap.put(pId, bean);
				   netInterMap.put(pId, map);
			   } 
			   
			}  
		}
		return netInterMap;
	}
	
	/**
	 * 清算主资源吞吐量
	 * @param beanMap
	 */
	private void doThroughput(Map<Long, MetricRealSummaryBean> beanMap,Date[] dates) {
		 if(beanMap != null && !beanMap.isEmpty()){
			 for(Long instanceId : beanMap.keySet()){
				 MetricRealSummaryBean bean = beanMap.get(instanceId);
				 List<Long> instanceIdList = new ArrayList<Long>(1);
				 instanceIdList.add(instanceId);
				 Map<Long,List<MetricDataBean>>  throughputMap =  queryHistoryMetricData(instanceIdList, THROUGHPUT_METRIC_ID,dates);
				 Map<String, List<RealMetricDetailBean>> realMetricMap = new HashMap<String, List<RealMetricDetailBean>>(1);
				 RealMetricDetailBean detailBean = new RealMetricDetailBean();
				 detailBean.setInstanceId(instanceId);
				 detailBean.setMetricId(THROUGHPUT_METRIC_ID);
				 detailBean.setMain(true);
				 detailBean.setMetricDataList(throughputMap.get(instanceId));
				 ResourceMetricDef metricDef = capacityService.getResourceMetricDef(bean.getResourceId(),THROUGHPUT_METRIC_ID);
			     detailBean.setUnit(metricDef.getUnit());
				 List<RealMetricDetailBean> list = new ArrayList<RealMetricDetailBean>(1);
				 list.add(detailBean);
				 realMetricMap.put(THROUGHPUT_METRIC_ID, list);
				 bean.setRealMetricMap(realMetricMap);
				 beanMap.put(instanceId, bean);
			 }
		 }
	}
    
	private void doMetricRealSummaryBeanMap(
			Map<Long, MetricInfoBean> infoBeanMap,
			Map<Long, MetricRealSummaryBean> beanMap) {
      if(infoBeanMap != null && !infoBeanMap.isEmpty()){
    	  for(Entry<Long, MetricInfoBean> entry : infoBeanMap.entrySet()){
    		  beanMap.put(entry.getKey(), getMetricRealSummaryBean(entry.getValue()));
    	  }
      }
	}
	
	private MetricRealSummaryBean getMetricRealSummaryBean(MetricInfoBean infoBean){
		MetricRealSummaryBean bean = new MetricRealSummaryBean();
		bean.setInstanceId(infoBean.getInstanceId());
		bean.setIp(infoBean.getIp());
		bean.setResourceId(infoBean.getResourceId());
		bean.setCategoryId(infoBean.getCategoryId());
		bean.setCategoryName(infoBean.getCategoryName());
		return bean;
	}

	private Map<Long,List<MetricDataBean>> queryHistoryMetricData(List<Long> instanceIds,String metricId,Date[] dates){
		Map<Long,List<MetricDataBean>> map = new HashMap<Long,List<MetricDataBean>>(3);
		if(instanceIds != null && !instanceIds.isEmpty()){
			for(Long instanceId : instanceIds){
			  List<MetricDataBean>  metricDatas = getMetricSummary(metricId,instanceId,dates);
			  map.put(instanceId, metricDatas);
			}
		}
		return map;
	}
	
	private List<MetricDataBean> getMetricSummary(String metricId, long instanceId,Date[] dates){
		List<MetricDataBean> dataList = new ArrayList<MetricDataBean>();
		try{
		   List<MetricData> metricDatas  = metricDataService.queryHistoryMetricData(metricId, instanceId, dates[0], dates[1]);
		   if(metricDatas != null && !metricDatas.isEmpty()){
			   for(MetricData d :metricDatas){
				   MetricDataBean bean = new MetricDataBean();
				   bean.setProfileId(d.getProfileId());
				   bean.setTimelineId(d.getTimelineId());
				   bean.setMetricId(metricId);
				   bean.setResourceInstanceId(d.getResourceInstanceId());
				   bean.setCollectTime(d.getCollectTime());
				   String[] mValue = d.getData();
				    
				   if(mValue != null && mValue.length >0 && mValue[0].trim().length() >0){
					  bean.setData(mValue[0].trim());
				   }  
				   dataList.add(bean);
			   }
		   }
	   }catch(Exception e){
		   if(logger.isErrorEnabled()){
			   logger.error(this.getClass().getName()+" metricDataService.queryHistoryMetricData method error:"+e);
		   }
		   return null;
	   }
	   return dataList;
	}
	
	private Date[] getDates(){
		Date[] date = new Date[2];
		Calendar calendar = Calendar.getInstance();
		date[1] = calendar.getTime();
		calendar.add(Calendar.MINUTE, -60);
		date[0] = calendar.getTime(); 
		return date;
	}

}
