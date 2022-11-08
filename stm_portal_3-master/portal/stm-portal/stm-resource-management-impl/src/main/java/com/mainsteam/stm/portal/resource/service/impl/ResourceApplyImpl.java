package com.mainsteam.stm.portal.resource.service.impl;

import java.text.DecimalFormat;
import java.text.SimpleDateFormat;
import java.util.*;
import java.util.regex.Pattern;

import javax.annotation.Resource;

import com.mainsteam.stm.util.MetricDataUtil;
import org.apache.log4j.Logger;

import com.mainsteam.stm.caplib.CapacityService;
import com.mainsteam.stm.caplib.dict.MetricTypeEnum;
import com.mainsteam.stm.caplib.resource.ResourceDef;
import com.mainsteam.stm.caplib.resource.ResourceMetricDef;
import com.mainsteam.stm.common.instance.obj.InstanceStateEnum;
import com.mainsteam.stm.common.metric.InstanceStateService;
import com.mainsteam.stm.common.metric.MetricDataService;
import com.mainsteam.stm.common.metric.MetricStateService;
import com.mainsteam.stm.common.metric.obj.MetricData;
import com.mainsteam.stm.common.metric.query.MetricRealtimeDataQuery;
import com.mainsteam.stm.executor.exception.MetricExecutorException;
import com.mainsteam.stm.instancelib.CustomModulePropService;
import com.mainsteam.stm.instancelib.ResourceInstanceService;
import com.mainsteam.stm.instancelib.exception.InstancelibException;
import com.mainsteam.stm.instancelib.obj.CustomModuleProp;
import com.mainsteam.stm.instancelib.obj.ResourceInstance;
import com.mainsteam.stm.instancelib.objenum.InstanceLifeStateEnum;
import com.mainsteam.stm.metric.CustomMetricService;
import com.mainsteam.stm.metric.exception.CustomMetricException;
import com.mainsteam.stm.metric.obj.CustomMetric;
import com.mainsteam.stm.metric.obj.CustomMetricInfo;
import com.mainsteam.stm.platform.web.vo.IDomain;
import com.mainsteam.stm.platform.web.vo.ILoginUser;
import com.mainsteam.stm.portal.resource.api.IResourceApplyApi;
import com.mainsteam.stm.portal.resource.api.InfoMetricQueryAdaptApi;
import com.mainsteam.stm.profilelib.ProfileService;
import com.mainsteam.stm.profilelib.exception.ProfilelibException;
import com.mainsteam.stm.profilelib.obj.Instance;
import com.mainsteam.stm.profilelib.obj.MetricSetting;
import com.mainsteam.stm.profilelib.obj.Profile;
import com.mainsteam.stm.profilelib.obj.ProfileInfo;
import com.mainsteam.stm.profilelib.obj.ProfileInstanceRelation;
import com.mainsteam.stm.profilelib.obj.ProfileMetric;
import com.mainsteam.stm.state.obj.InstanceStateData;
import com.mainsteam.stm.state.obj.MetricStateData;
import com.mainsteam.stm.util.UnitTransformUtil;

public class ResourceApplyImpl implements IResourceApplyApi{
	private static Logger logger = Logger.getLogger(ResourceApplyImpl.class);
	
	@Resource
	private ResourceInstanceService resourceInstanceService;
	@Resource
	private MetricDataService metricDataService;
	@Resource
	private CapacityService capacityService;
	@Resource
	private MetricStateService metricStateService;
	@Resource
	private ProfileService profileService;
	@Resource
	private InstanceStateService instanceStateService;
	@Resource
	private CustomModulePropService customModulePropService;
	@Resource
	private CustomMetricService customMetricService;
	@Resource
	private InfoMetricQueryAdaptApi infoMetricQueryAdaptService;
	
	
	public Map<Long ,List<Map<String,String>>> getBizMetricInfo(Map<Long ,List<String>> queryMap,ResourceInstance parentResourceIns){
		Map<Long,ResourceInstance> insMap = new HashMap<Long,ResourceInstance>();
		Map<String,ResourceDef> rdfMap = new HashMap<String,ResourceDef>();
		long parentInstanceId = parentResourceIns.getId();
		
		Map<Long ,List<Map<String,String>>> resultMap = new HashMap<Long ,List<Map<String,String>>>();
		List<ResourceInstance> riList = new ArrayList<ResourceInstance>();
		riList.add(parentResourceIns);
		if(parentResourceIns.getLifeState()==InstanceLifeStateEnum.MONITORED){
			List<ResourceInstance> riListIn = parentResourceIns.getChildren();
			if(null!=riListIn && riListIn.size()>0){
				riList.addAll(riListIn);
			}
			
			//转为map
			for(ResourceInstance ri:riList){
				insMap.put(new Long(ri.getId()), ri);
			}
		}else{
			return resultMap;
		}
		
		//查询策略信息,指标监控状态
		Map<String,List<ProfileMetric>> profileMetricsMap = new HashMap<String,List<ProfileMetric>>();
		try {
			ProfileInfo pi = profileService.getBasicInfoByResourceInstanceId(parentInstanceId);
			if(pi!=null){
				Profile pf = profileService.getProfilesById(pi.getProfileId());
				if(pf!=null){
					MetricSetting msArr = pf.getMetricSetting();
					List<ProfileMetric> profileMetricList = msArr.getMetrics();
					profileMetricsMap.put(pi.getResourceId(), profileMetricList);
					
					List<Profile> childPro = pf.getChildren();
					if(null!=childPro&&childPro.size()>0){
						for(Profile pfItem:childPro){
							MetricSetting msArrItem = pfItem.getMetricSetting();
							List<ProfileMetric> profileMetricListItem = msArrItem.getMetrics();
							ProfileInfo pfinfo = pfItem.getProfileInfo();
							
							profileMetricsMap.put(pfinfo.getResourceId(), profileMetricListItem);
						}
					}
				}
			
			}
			
		}catch (ProfilelibException e) {
			logger.error("ResourceManagement---getBizMetricInfoByNodeId--ProfilelibException"+e.getMessage());
		}
	    
		Set<Long> insSet = queryMap.keySet();
		for(Long str:insSet){
			
			List<Map<String,String>> mapList = new ArrayList<Map<String,String>>();
			
			List<String> metricList = queryMap.get(str);
			
			if(insMap.containsKey(str)){
				ResourceInstance ri = insMap.get(str);
				String resourceId = ri.getResourceId();
				ResourceMetricDef[] rmdf;
				if(rdfMap.containsKey(resourceId)){
					ResourceDef rdf = rdfMap.get(resourceId);
					rmdf = rdf.getMetricDefs();
				}else{
					ResourceDef rdf = capacityService.getResourceDefById(resourceId);
					rdfMap.put(resourceId, rdf);
					rmdf = rdf.getMetricDefs();
				}
				
//				Set<String> infoMetricSet = new HashSet<String>();
				List<String> avaliMetricList = new ArrayList<String>();
				Set<String> perMetricSet = new HashSet<String>();
				String insName = "--";
				if(null!=ri.getShowName()&&!ri.getShowName().equals("")){
					insName = ri.getShowName();
				}else if(null!=ri.getName()&&!ri.getName().equals("")){
					insName = ri.getName();
				}
				
				for(String metric:metricList){
					Map<String,String> metricMap = new HashMap<String,String>();
					metricMap.put("metricId", metric);
					metricMap.put("isMonitor", "false");
					boolean flag = true;
					if(str==parentInstanceId){
						//是主资源,增加判断自定义指标
						List<CustomMetric> customMetrics = new ArrayList<CustomMetric>();
						try {
							customMetrics = customMetricService.getCustomMetricsByInstanceId(parentInstanceId);
						} catch (CustomMetricException e) {
							logger.error("ResourceManagement---getBizMetricInfoByNodeId--CustomMetricException"+e.getMessage());
						}
						if(null!=customMetrics){
							for(CustomMetric cm:customMetrics){
					    		CustomMetricInfo cmi = cm.getCustomMetricInfo();
					    		SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss"); 
								if(cmi.getId().equals(metric)){
									metricMap.put("isMonitor", cmi.isMonitor()?"true":"false");
									metricMap.put("metricName", "["+insName+"]:"+cmi.getName());
									metricMap.put("metricValue", "--");
									metricMap.put("metricDate", "--");
									
									switch(cmi.getStyle()){
									case InformationMetric:
										//暂无信息指标
//										infoMetricSet.add(metric);
										break;
									case AvailabilityMetric:
										MetricData md1 = metricDataService.getCustomerMetricData(parentInstanceId, metric);
										if(null!=md1&&null!=md1.getData()&&null!=md1.getData()[0]){
											metricMap.put("metricValue",UnitTransformUtil.transform(md1.getData()[0],cmi.getUnit()));
										}
										metricMap.put("metricDate", formatter.format(md1.getCollectTime()));
										break;
									case PerformanceMetric:
										MetricData md2 = metricDataService.getCustomerMetricData(parentInstanceId, metric);
										if(null!=md2&&null!=md2.getData()&&null!=md2.getData()[0]){
											metricMap.put("metricValue",UnitTransformUtil.transform(md2.getData()[0],cmi.getUnit()));
										}
										metricMap.put("metricDate", formatter.format(md2.getCollectTime()));
//										metricMap.put("metricValue", "--");
//										metricMap.put("metricDate", "--");
										
										break;
									}
									metricMap.put("metricType", cmi.getStyle().name());
									metricMap.put("metricUnit", cmi.getUnit());
									
									
									flag = false;
									break;
								}
					    	}
						}
					}
					if(flag){
						List<ProfileMetric> prmList = profileMetricsMap.get(resourceId);
						if(null!=prmList&&prmList.size()>0){
							for(ProfileMetric pm:prmList){
					    		if(str.equals(pm.getMetricId())){
					    			metricMap.put("isMonitor", pm.isMonitor()?"true":"false");
					    			break;
					    		}
					    	}
						}
			    		
			    	}
					
					for(ResourceMetricDef rmdef:rmdf){
						if(metric.equals(rmdef.getId())){
							metricMap.put("metricName", "["+insName+"]:"+rmdef.getName());
							metricMap.put("metricType", rmdef.getMetricType().name());
							metricMap.put("metricUnit", rmdef.getUnit());
							metricMap.put("metricValue", "--");
							metricMap.put("metricDate", "--");
							
							switch(rmdef.getMetricType()){
							case InformationMetric:
								//暂无信息指标
//								infoMetricSet.add(metric);
								break;
							case AvailabilityMetric:
								avaliMetricList.add(metric);
								break;
							case PerformanceMetric:
								perMetricSet.add(metric);
								break;
							}
							break;
						}
					}
					if(metricMap.size()>2){
						mapList.add(metricMap);
					}
				}
				List<Map<String, ?>> perMapList=new ArrayList<Map<String, ?>>();
				List<MetricStateData> avaliDataList=new ArrayList<MetricStateData>();
				if(perMetricSet.size()>0){
					String[] metricPerforman = new String[perMetricSet.size()] ;
					perMetricSet.toArray(metricPerforman);
					MetricRealtimeDataQuery mr = new MetricRealtimeDataQuery();
					mr.setMetricID(metricPerforman);
					long[] ids = new long[1];
					ids[0] = str;
					mr.setInstanceID(ids);
					perMapList = metricDataService.queryRealTimeMetricData(mr);
				}
				if(avaliMetricList.size()>0){
					List<Long> instanceIdList = new ArrayList<Long>();
					instanceIdList.add(str);
					avaliDataList = metricStateService.findMetricState(instanceIdList, avaliMetricList);
				}
				if(null!=perMapList && perMapList.size()>0){
					for(Map<String, ?> mapData:perMapList){
						if (null != mapData.get("instanceid")&& str == Long.parseLong(mapData.get("instanceid").toString())) {
							for(Map<String,String> metricInfo:mapList){
								String metricStr = metricInfo.get("metricId");
								if (null != mapData.get(metricStr)) {
									metricInfo.put("metricValue", UnitTransformUtil.transform(mapData.get(metricStr).toString(),metricInfo.get("metricUnit")));
									//特殊处理掉2016-1-1 1:00:01.1的后缀
									String dateStr = mapData.get(metricStr+"CollTime").toString();
									if(null!=dateStr&&!"".equals(dateStr)&&dateStr.indexOf(".")>0){
										dateStr = dateStr.split("\\.")[0];
									}
									metricInfo.put("metricDate", dateStr);
									MetricStateData stateData = metricStateService.getMetricState(str, metricStr);
									if(stateData == null){
										logger.error("stateData is null , instanceId = " + str + ",metricId = " + metricStr);
										System.out.println("stateData is null");
										metricInfo.put("metricState", null);
									}else{
										metricInfo.put("metricState", stateData.getState().toString());
									}
									continue;
								}
							}
							continue;
						}
					}
				}
				SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss"); 
				if(null!=avaliDataList && avaliDataList.size()>0){
					for(MetricStateData msd:avaliDataList){
						for(Map<String,String> metricInfo:mapList){
							String metricStr = metricInfo.get("metricId");
							if (msd.getMetricID().equals(metricStr)) {
								metricInfo.put("metricValue", msd.getState().name());
								metricInfo.put("metricDate", formatter.format(msd.getCollectTime()));
								continue;
							}
						}
					}
				}
			}else{
				continue;
			}
			
			if(mapList.size()>0){
				resultMap.put(str, mapList);
			}
		}
		
		return resultMap;
	}
	
	public ResourceMetricDef[] getHeaderInfoByMetricType(long instanceId, String metricType){
			
		return getHeadResourceMetricDefByMetricType(instanceId, metricType);
		
	}
	
	public ResourceMetricDef[] getHeaderInfoByResourceId(long instanceId, String resourceId){
		List<ResourceMetricDef> rmdListTemp = new ArrayList<ResourceMetricDef>();
		//存放可用性指标，用于灵活排序
		List<ResourceMetricDef> rmdListAvailTemp = new ArrayList<ResourceMetricDef>();
		
		ResourceDef rdf = capacityService.getResourceDefById(resourceId);
    	ResourceMetricDef[] rmdf = rdf.getMetricDefs();
    	for(ResourceMetricDef rmdef:rmdf){
			if(rmdef.isDisplay() && rmdef.isMonitor()){
				if(rmdef.getMetricType()==MetricTypeEnum.AvailabilityMetric){
					rmdListAvailTemp.add(rmdef);
				}else{
					rmdListTemp.add(rmdef);
				}
			}
		}
	
		
	ResourceMetricDef[] rmdArr = new ResourceMetricDef[rmdListTemp.size()];
	rmdListTemp.toArray(rmdArr);
	ResourceMetricDef[] rmdAvailArr = new ResourceMetricDef[rmdListAvailTemp.size()];
	rmdListAvailTemp.toArray(rmdAvailArr);
	
	//根据指标displayOrder大小排序
	popResourceMetricDef(rmdArr);
    boolean haveResourceState = false;
    //判断是否需要显示资源状态
    if(rmdListAvailTemp.size()==0){
    	for(ResourceMetricDef rmd:rmdListTemp){
			if(rmd.getMetricType()==MetricTypeEnum.AvailabilityMetric || rmd.getMetricType()==MetricTypeEnum.PerformanceMetric){
				haveResourceState = true;
				break;
			}
		}
    }else{
    	haveResourceState = true;
    }
    
	//手动添加前两位为资源状态和资源名称
    List<ResourceMetricDef> rmDefList = new ArrayList<ResourceMetricDef>();

	ResourceMetricDef rmdResourceName = new ResourceMetricDef();
	rmdResourceName.setId("resourceName");
	rmdResourceName.setName("资源名称");
	rmdResourceName.setMetricType(MetricTypeEnum.InformationMetric);
	rmdResourceName.setUnit("");
	rmdResourceName.setDisplay(true);
	rmdResourceName.setDisplayOrder("");
	
	rmDefList.add(rmdResourceName);
	
	if(haveResourceState){
		rmdResourceName.setId("resourceNameAndState");
		for (int i = 0; i < rmdAvailArr.length; i++) {
			rmDefList.add(rmdAvailArr[i]);
		}
	}
	for (int i = 0; i < rmdArr.length; i++) {
		rmDefList.add(rmdArr[i]);
	}
	
	return rmDefList.toArray(new ResourceMetricDef[rmDefList.size()]);
	
}
	
	//排序
	private void popResourceMetricDef(ResourceMetricDef[] resourceMetricDefArray){
		int length = resourceMetricDefArray.length-1;
		for(int i=0;i<length;i++){
			for(int x=0;x<length-i;x++){
					ResourceMetricDef rmd = resourceMetricDefArray[x];
					ResourceMetricDef rmd2 = resourceMetricDefArray[x+1];
					int index1 = Integer.parseInt(((null==rmd.getDisplayOrder()||"".equals(rmd.getDisplayOrder()))?"0":rmd.getDisplayOrder())) ;
					int index2 = Integer.parseInt(((null==rmd2.getDisplayOrder()||"".equals(rmd2.getDisplayOrder()))?"0":rmd2.getDisplayOrder())) ;
	                if(index1>index2 ){
	                	resourceMetricDefArray[x] = resourceMetricDefArray[x+1];
	                	resourceMetricDefArray[x+1] = rmd;
					}
			}
		}
	}
	
	
	//获取展示表头
	private ResourceMetricDef[] getHeadResourceMetricDefByMetricType(long instanceId, String metricType){

			List<ResourceMetricDef> rmdListTemp = new ArrayList<ResourceMetricDef>();
			//存放可用性指标，用于灵活排序
//			List<ResourceMetricDef> rmdListAvailTemp = new ArrayList<ResourceMetricDef>();
			
			try{
				ProfileInfo pi = profileService.getBasicInfoByResourceInstanceId(instanceId);
			    Profile pf = profileService.getProfilesById(pi.getProfileId());
			    ProfileInfo pfInfo;
				
			    
			    
			    //正常子资源展示
			    switch (metricType) {
				case "Standard_Application_Dialog":
					//标准服务直接取主资源策略信息
					pfInfo = pf.getProfileInfo();
				    if(null!=pfInfo){
				    	ResourceDef rdf = capacityService.getResourceDefById(pfInfo.getResourceId());
				    	MetricSetting mSet = pf.getMetricSetting();
				    	ResourceMetricDef[] rmdf = rdf.getMetricDefs();
				    	for(ResourceMetricDef rmdef:rmdf){
		    				for(ProfileMetric pm:mSet.getMetrics()){
		    					if(pm.getMetricId().equals(rmdef.getId()) && rmdef.isDisplay() && (pm.isMonitor()||pm.getTimeLineId()>0)){
		    						if(rmdef.getMetricType()==MetricTypeEnum.AvailabilityMetric){
//										rmdListAvailTemp.add(rmdef);
		    							rmdListTemp.add(rmdef);
									}else{
										rmdListTemp.add(rmdef);
									}
		    					}
		    				}
		    			}
				    }
					break;
					
				default:
					Map<String ,String> metricMap = new HashMap<String ,String>();
					
				    List<Profile> pfList = pf.getChildren();
				    if(null!=pfList){
				    	for(Profile pFile:pfList){
				    		pfInfo = pFile.getProfileInfo();
						    if(null!=pfInfo){
						    	ResourceDef rdf = capacityService.getResourceDefById(pfInfo.getResourceId());
						    	if(null!=rdf){
						    		if(metricType.equals(rdf.getType())){
						    			MetricSetting mSet = pFile.getMetricSetting();
						    			ResourceMetricDef[] rmdf = rdf.getMetricDefs();
						    			
//						    			List<String> list = new ArrayList<String>();
						    			
						    			for(ResourceMetricDef rmdef:rmdf){
						    				for(ProfileMetric pm:mSet.getMetrics()){
						    					if(pm.getMetricId().equals(rmdef.getId()) && rmdef.isDisplay() && (pm.isMonitor()||pm.getTimeLineId()>0)){
						    						//同种类型的子策略可能有两条
						    						if(!metricMap.containsKey(pm.getMetricId())){
						    							metricMap.put(pm.getMetricId(), "");
						    							if(rmdef.getMetricType()==MetricTypeEnum.AvailabilityMetric){
//															rmdListAvailTemp.add(rmdef);
						    								rmdListTemp.add(rmdef);
														}
//						    							else if(rmdef.getMetricType()==MetricTypeEnum.InformationMetric){
//						    								list.add(pm.getMetricId());
//						    							}
						    							else{
															rmdListTemp.add(rmdef);
														}
						    						}
						    						
						    					}
						    				}
						    			}
						    		}
						    	}
						    }
						   
					    }
				    }
					break;
				}
			    
//				if("Standard_Application_Dialog".equals(metricType)){
//					ProfileInfo pfInfo = pf.getProfileInfo();
//				    if(null!=pfInfo){
//				    	ResourceDef rdf = capacityService.getResourceDefById(pfInfo.getResourceId());
//				    	MetricSetting mSet = pf.getMetricSetting();
//				    	ResourceMetricDef[] rmdf = rdf.getMetricDefs();
//				    	for(ResourceMetricDef rmdef:rmdf){
//		    				for(ProfileMetric pm:mSet.getMetrics()){
//		    					if(pm.getMetricId().equals(rmdef.getId()) && rmdef.isDisplay() && pm.isMonitor()){
//		    						if(rmdef.getMetricType()==MetricTypeEnum.AvailabilityMetric){
//										rmdListAvailTemp.add(rmdef);
//									}else{
//										rmdListTemp.add(rmdef);
//									}
//		    					}
//		    				}
//		    			}
//				    }
//				}else{
//					Map<String ,String> metricMap = new HashMap<String ,String>();
//					
//				    List<Profile> pfList = pf.getChildren();
//				    if(null!=pfList){
//				    	for(Profile pFile:pfList){
//				    		ProfileInfo pfInfo = pFile.getProfileInfo();
//						    if(null!=pfInfo){
//						    	ResourceDef rdf = capacityService.getResourceDefById(pfInfo.getResourceId());
//						    	if(null!=rdf){
//						    		rdf.getType();
//						    		if(metricType.equals(rdf.getType())){
//						    			MetricSetting mSet = pFile.getMetricSetting();
//						    			ResourceMetricDef[] rmdf = rdf.getMetricDefs();
//						    			
////						    			List<String> list = new ArrayList<String>();
//						    			
//						    			for(ResourceMetricDef rmdef:rmdf){
//						    				for(ProfileMetric pm:mSet.getMetrics()){
//						    					if(pm.getMetricId().equals(rmdef.getId()) && rmdef.isDisplay() && pm.isMonitor()){
//						    						if(!metricMap.containsKey(pm.getMetricId())){
//						    							metricMap.put(pm.getMetricId(), "");
//						    							if(rmdef.getMetricType()==MetricTypeEnum.AvailabilityMetric){
//															rmdListAvailTemp.add(rmdef);
//														}
////						    							else if(rmdef.getMetricType()==MetricTypeEnum.InformationMetric){
////						    								list.add(pm.getMetricId());
////						    							}
//						    							else{
//															rmdListTemp.add(rmdef);
//														}
//						    						}
//						    						
//						    					}
//						    				}
//						    			}
//						    		}
//						    	}
//						    }
//						   
//					    }
//				    }
//				}
			}catch(Exception e){
				logger.error(e.getMessage());
				return new ResourceMetricDef[0];
			}
			
				
			ResourceMetricDef[] rmdArr = new ResourceMetricDef[rmdListTemp.size()];
			rmdListTemp.toArray(rmdArr);
//			ResourceMetricDef[] rmdAvailArr = new ResourceMetricDef[rmdListAvailTemp.size()];
//			rmdListAvailTemp.toArray(rmdAvailArr);
			
			//根据指标displayOrder大小排序
			popResourceMetricDef(rmdArr);
		    boolean haveResourceState = false;
		    //判断是否需要显示资源状态
//		    if(rmdListAvailTemp.size()==0){
		    	for(ResourceMetricDef rmd:rmdListTemp){
					if(rmd.getMetricType()==MetricTypeEnum.AvailabilityMetric || rmd.getMetricType()==MetricTypeEnum.PerformanceMetric){
						haveResourceState = true;
						break;
					}
				}
//		    }else{
//		    	haveResourceState = true;
//		    }
		    
			//手动添加前两位为资源状态和资源名称
		    List<ResourceMetricDef> rmDefList = new ArrayList<ResourceMetricDef>();

			ResourceMetricDef rmdResourceName = new ResourceMetricDef();
			rmdResourceName.setId("resourceName");
			rmdResourceName.setName("资源名称");
			rmdResourceName.setMetricType(MetricTypeEnum.InformationMetric);
			rmdResourceName.setUnit("");
			rmdResourceName.setDisplay(true);
			rmdResourceName.setDisplayOrder("");
			
			rmDefList.add(rmdResourceName);
			
			// 如果是标准服务的详细页面 加入显示名称
	/*		if("Standard_Application_Dialog".equals(metricType)){
				ResourceMetricDef rmdResourceShowName = new ResourceMetricDef();
				rmdResourceShowName.setId("resourceShowName");
				rmdResourceShowName.setName("资源显示名称");
				rmdResourceShowName.setMetricType(MetricTypeEnum.InformationMetric);
				rmdResourceShowName.setUnit("");
				rmdResourceShowName.setDisplay(true);
				rmdResourceShowName.setDisplayOrder("");
				rmDefList.add(rmdResourceShowName);
			}*/
			
			if(haveResourceState){
				rmdResourceName.setId("resourceNameAndState");
//				for (int i = 0; i < rmdAvailArr.length; i++) {
//					rmDefList.add(rmdAvailArr[i]);
//				}
			}
			for (int i = 0; i < rmdArr.length; i++) {
				rmDefList.add(rmdArr[i]);
			}
			
			return rmDefList.toArray(new ResourceMetricDef[rmDefList.size()]);
			/*
			if(haveResourceState){
				ResourceMetricDef rmdResourceName = new ResourceMetricDef();
				rmdResourceName.setId("resourceNameAndState");
				rmdResourceName.setName("资源名称");
				rmdResourceName.setMetricType(MetricTypeEnum.InformationMetric);
				rmdResourceName.setUnit("");
				rmdResourceName.setDisplay(true);
				rmdResourceName.setDisplayOrder("");
				int addSize = 1+rmdAvailArr.length;
				ResourceMetricDef[] headArr = new ResourceMetricDef[rmdArr.length+addSize];
				headArr[0] = rmdResourceName;
				//将可用性指标排在资源名称之后
				for(int avai=0;avai<rmdAvailArr.length;avai++){
					headArr[avai+1] = rmdAvailArr[avai];
				}
				for(int oth=0;oth<rmdArr.length;oth++){
					headArr[oth+addSize] = rmdArr[oth];
				}
				return headArr;
			}else{
				ResourceMetricDef rmdResourceName = new ResourceMetricDef();
				rmdResourceName.setId("resourceName");
				rmdResourceName.setName("资源名称");
				rmdResourceName.setMetricType(MetricTypeEnum.InformationMetric);
				rmdResourceName.setUnit("");
				rmdResourceName.setDisplay(true);
				rmdResourceName.setDisplayOrder("");
				ResourceMetricDef[] headArr = new ResourceMetricDef[rmdArr.length+1];
				headArr[0] = rmdResourceName;
				for(int oth=0;oth<rmdArr.length;oth++){
					headArr[oth+1] = rmdArr[oth];
				}
				return headArr;
			}
			*/
	}
//	private ResourceMetricDef[] getHeadResourceMetricDefByMetricType(long instanceId, String metricType){
//		ResourceInstance met = getResourceIdForResourceDef(instanceId,metricType);
//		ResourceDef rd = null;
//		if(null!=met){
//			
//			rd = capacityService.getResourceDefById(met.getResourceId());
//			
//		}else{
//			return new ResourceMetricDef[0];
//		}
//		
//		if(null!=rd){
//			ResourceMetricDef[] rdf = rd.getMetricDefs();
//			List<ProfileMetric> pmList = new ArrayList<ProfileMetric>();
//			try{
//				
//			    pmList = profileService.getMetricByInstanceId(met.getId());
//			    ProfileInfo pi = profileService.getBasicInfoByResourceInstanceId(instanceId);
//			    
//			    Profile pf = profileService.getProfilesById(pi.getProfileId());
//			    pf.getChildren();
//			    pf.getProfileInfo().getResourceId();
//			    
//			    capacityService.getResourceDefById(pf.getChildren().get(0).getProfileInfo().getResourceId());
//			}catch(Exception e){
//				logger.error(e.getMessage());
//				return new ResourceMetricDef[0];
//			}
//			if(null!=pmList && pmList.size()!=0){
//				List<ResourceMetricDef> rmdListTemp = new ArrayList<ResourceMetricDef>();
//				//存放可用性指标，用于灵活排序
//				List<ResourceMetricDef> rmdListAvailTemp = new ArrayList<ResourceMetricDef>();
//				
//				for(int i=0;i<pmList.size();i++){
//					for(ResourceMetricDef rmd:rdf){
//						//添加根据模型xml判断是否前台显示     rmd.isDisplay() &&
//						if(   rmd.isDisplay() && rmd.getId().equals(pmList.get(i).getMetricId()) && pmList.get(i).isMonitor()){
//							
//							if(rmd.getMetricType()==MetricTypeEnum.AvailabilityMetric){
//								rmdListAvailTemp.add(rmd);
//							}else{
//								rmdListTemp.add(rmd);
//							}
//						}
//					}
//				}
//				
//				ResourceMetricDef[] rmdArr = new ResourceMetricDef[rmdListTemp.size()];
//				rmdListTemp.toArray(rmdArr);
//				ResourceMetricDef[] rmdAvailArr = new ResourceMetricDef[rmdListAvailTemp.size()];
//				rmdListAvailTemp.toArray(rmdAvailArr);
//				
//				//根据指标displayOrder大小排序
//				popResourceMetricDef(rmdArr);
//			    boolean haveResourceState = false;
//				for(ResourceMetricDef rmd:rdf){
//					if(rmd.getMetricType()==MetricTypeEnum.AvailabilityMetric || rmd.getMetricType()==MetricTypeEnum.PerformanceMetric){
//						haveResourceState = true;
//						break;
//					}
//				}
//				//手动添加前两位为资源状态和资源名称
//				if(haveResourceState){
////					ResourceMetricDef rmdResourceState = new ResourceMetricDef();
////					ResourceMetricDef rmdResourceName = new ResourceMetricDef();
////					rmdResourceState.setId("resourceState");
////					rmdResourceState.setName("资源状态");
////					rmdResourceState.setMetricType(MetricTypeEnum.AvailabilityMetric);
////					rmdResourceState.setUnit("");
////					rmdResourceState.setDisplay(true);
////					rmdResourceState.setDisplayOrder("");
////					rmdResourceName.setId("resourceName");
////					rmdResourceName.setName("资源名称");
////					rmdResourceName.setMetricType(MetricTypeEnum.InformationMetric);
////					rmdResourceName.setUnit("");
////					rmdResourceName.setDisplay(true);
////					rmdResourceName.setDisplayOrder("");
////					ResourceMetricDef[] headArr = new ResourceMetricDef[rmdArr.length+2];
////					headArr[0] = rmdResourceState;
////					headArr[1] = rmdResourceName;
////					for(int oth=0;oth<rmdArr.length;oth++){
////						headArr[oth+2] = rmdArr[oth];
////					}
////					return headArr;
//					ResourceMetricDef rmdResourceName = new ResourceMetricDef();
//					rmdResourceName.setId("resourceNameAndState");
//					rmdResourceName.setName("资源名称");
//					rmdResourceName.setMetricType(MetricTypeEnum.InformationMetric);
//					rmdResourceName.setUnit("");
//					rmdResourceName.setDisplay(true);
//					rmdResourceName.setDisplayOrder("");
//					int addSize = 1+rmdAvailArr.length;
//					ResourceMetricDef[] headArr = new ResourceMetricDef[rmdArr.length+addSize];
//					headArr[0] = rmdResourceName;
//					//将可用性指标排在资源名称之后
//					for(int avai=0;avai<rmdAvailArr.length;avai++){
//						headArr[avai+1] = rmdAvailArr[avai];
//					}
//					for(int oth=0;oth<rmdArr.length;oth++){
//						headArr[oth+addSize] = rmdArr[oth];
//					}
//					return headArr;
//				}else{
//					ResourceMetricDef rmdResourceName = new ResourceMetricDef();
//					rmdResourceName.setId("resourceName");
//					rmdResourceName.setName("资源名称");
//					rmdResourceName.setMetricType(MetricTypeEnum.InformationMetric);
//					rmdResourceName.setUnit("");
//					rmdResourceName.setDisplay(true);
//					rmdResourceName.setDisplayOrder("");
//					ResourceMetricDef[] headArr = new ResourceMetricDef[rmdArr.length+1];
//					headArr[0] = rmdResourceName;
//					for(int oth=0;oth<rmdArr.length;oth++){
//						headArr[oth+1] = rmdArr[oth];
//					}
//					return headArr;
//				}
//			}else{
//				return new ResourceMetricDef[0];
//			}
//		}else{
//			return new ResourceMetricDef[0];
//		}
//	}
	
	private ResourceInstance getResourceIdForResourceDef(long instanceId, String metricType){
		ResourceInstance rit = getResourceInstance(instanceId);
		if("Standard_Application_Dialog".equals(metricType)){
			if(InstanceLifeStateEnum.MONITORED==rit.getLifeState()){
				return rit;
			}else{
				return null;
			}
		}else{
			
			List<ResourceInstance> list = rit.getChildren();
			
			ResourceInstance met = new ResourceInstance();
			
			for(ResourceInstance rits:list){
				if(rits.getChildType().equals(metricType)){
					if(InstanceLifeStateEnum.MONITORED==rits.getLifeState()){
						met = rits;
						break;
					}
				}
			}
			return met;
		}
		
	}
	
	
	//获取要查询的指标
	private ResourceMetricDef[] getResourceMetricDefByMetricType(long instanceId, String metricType){
		List<ResourceMetricDef> rmdList = new ArrayList<ResourceMetricDef>();
		
		try{
			ProfileInfo pi = profileService.getBasicInfoByResourceInstanceId(instanceId);
		    Profile pf = profileService.getProfilesById(pi.getProfileId());
			//标准服务直接取主资源策略信息
			if("Standard_Application_Dialog".equals(metricType)){
				ProfileInfo pfInfo = pf.getProfileInfo();
			    if(null!=pfInfo){
			    	ResourceDef rdf = capacityService.getResourceDefById(pfInfo.getResourceId());
			    	MetricSetting mSet = pf.getMetricSetting();
			    	ResourceMetricDef[] rmdf = rdf.getMetricDefs();
			    	for(ResourceMetricDef rmdef:rmdf){
	    				for(ProfileMetric pm:mSet.getMetrics()){
	    					if(pm.getMetricId().equals(rmdef.getId()) && rmdef.isDisplay() && (pm.isMonitor()||pm.getTimeLineId()>0)){
    							rmdList.add(rmdef);
	    					}
	    				}
	    			}
			    }
			}else{
				Map<String ,String> metricMap = new HashMap<String ,String>();
				
			    List<Profile> pfList = pf.getChildren();
			    for (int i = 0; null != pfList && i < pfList.size(); i++) {
			    	Profile pFile = pfList.get(i);
		    		ProfileInfo pfInfo = pFile.getProfileInfo();
				    if(null != pfInfo){
				    	ResourceDef rdf = capacityService.getResourceDefById(pfInfo.getResourceId());
				    	if(null != rdf && metricType.equals(rdf.getType())){
			    			MetricSetting mSet = pFile.getMetricSetting();
			    			ResourceMetricDef[] rmdf = rdf.getMetricDefs();
			    			for(ResourceMetricDef rmdef : rmdf){
			    				for(ProfileMetric pm : mSet.getMetrics()){
			    					if(pm.getMetricId().equals(rmdef.getId()) && (pm.isMonitor()||pm.getTimeLineId()>0)
			    							&& (rmdef.isDisplay() || ("ifType".equals(rmdef.getId()) && "NetInterface".equals(metricType)))){
			    						if(!metricMap.containsKey(pm.getMetricId())){
			    							metricMap.put(pm.getMetricId(), "");
			    							rmdList.add(rmdef);
			    						}
			    					}
			    				}
			    			}
				    	}
				    }
				}
			}
		}catch(Exception e){
			logger.error(e.getMessage());
			return new ResourceMetricDef[0];
		}
		ResourceMetricDef[] rmdArr = new ResourceMetricDef[rmdList.size()];
		rmdList.toArray(rmdArr);
		return rmdArr;
	}
//	private ResourceMetricDef[] getResourceMetricDefByMetricType(long instanceId, String metricType){
//		ResourceInstance met = getResourceIdForResourceDef(instanceId,metricType);
//		ResourceDef rd = null;
//		if(null!=met){
//			
//			rd = capacityService.getResourceDefById(met.getResourceId());
//			
//		}else{
//			return new ResourceMetricDef[0];
//		}
//		
//		if(null!=rd){
//			ResourceMetricDef[] rdf = rd.getMetricDefs();
//			List<ProfileMetric> pmList = new ArrayList<ProfileMetric>();
//			try{
//				
//			    pmList = profileService.getMetricByInstanceId(met.getId());
//			    
//			}catch(Exception e){
//				logger.error(e.getMessage());
//				return new ResourceMetricDef[0];
//			}
//			if(null!=pmList && pmList.size()!=0){
//				List<ResourceMetricDef> rmdListTemp = new ArrayList<ResourceMetricDef>();
//				for(int i=0;i<pmList.size();i++){
//					for(ResourceMetricDef rmd:rdf){
//						//添加根据模型xml判断是否前台显示     rmd.isDisplay() &&
//						if(rmd.isDisplay() && rmd.getId().equals(pmList.get(i).getMetricId()) && pmList.get(i).isMonitor()){
//							rmdListTemp.add(rmd);
//						}
//					}
//				}
//				
//				ResourceMetricDef[] rmdArr = new ResourceMetricDef[rmdListTemp.size()];
//				rmdListTemp.toArray(rmdArr);
//				
//				return rmdArr;
//			}else{
//				return new ResourceMetricDef[0];
//			}
//		}else{
//			return new ResourceMetricDef[0];
//		}
//	}
	@Override
	public boolean updateIfSpeedValue(String key,String value,String realTimeValue,Long instanceId){
		
		CustomModuleProp  cmp = customModulePropService.getCustomModulePropByInstanceIdAndKey(instanceId,key);
		if(null==cmp){
			cmp = new CustomModuleProp();
			cmp.setKey(key);
			cmp.setInstanceId(instanceId);
			cmp.setUserValue(value);
			cmp.setRealtimeValue(realTimeValue);
			customModulePropService.addCustomModuleProp(cmp);
		}else{
			cmp.setKey(key);
			cmp.setUserValue(value);
			customModulePropService.updateCustomModuleProp(cmp);
		}
		
		
		
		return true;
	}
	
	@Override
	public boolean updateIfSpeedCollection(Long instanceId,String key){
		
		customModulePropService.removeCustomModulePropByInstanceIdAndKey(instanceId, key);
		
		return true;
	}
	
	public List<Map<String,String>> getMetricInfo(long instanceId, String metricType,String resourceType,ILoginUser user){
		ResourceInstance rit = getResourceInstance(instanceId);
		List<ResourceInstance> list =new ArrayList<ResourceInstance>();
		List<String> metricInfoArray =new ArrayList<String>();
		List<String> metricAvailabArray =new ArrayList<String>();
		List<String> metricPerformanArray =new ArrayList<String>();
		Map<String,String> metricUnit =new HashMap<String,String>();
		long[] instanceIdArray ;
		String ifSpeedHandleState = "false";
		
		if("Standard_Application_Dialog".equals(metricType)){
			list.add(rit);
		}else{
			long insDomainId = rit.getDomainId();
			if(user.isSystemUser()){
				ifSpeedHandleState = "true";
			}else if(user.isDomainUser()||user.isManagerUser()){
				Set<IDomain> domains = user.getDomainManageDomains();
				if(null!=domains){
					for(IDomain id:domains){
						if(insDomainId==id.getId()){
							ifSpeedHandleState = "true";
							break;
						}
					}
				}
				
			}else if(user.isCommonUser()){
			}
			
			List<ResourceInstance> listChildren = rit.getChildren();
			if(listChildren!=null){
				for(ResourceInstance rits:listChildren){
					logger.error("println childResource rits id: "+rits.getId()+"--"+rits.getName());
					if(rits.getChildType().equals(metricType) && rits.getLifeState()==InstanceLifeStateEnum.MONITORED){
						list.add(rits);
					}
				}	
			}
		
		}
		
		
		ResourceMetricDef[] rmdf = getResourceMetricDefByMetricType(instanceId, metricType);
		for(int len = 0;len<rmdf.length;len++){
			if( rmdf[len].getMetricType() == MetricTypeEnum.PerformanceMetric){
				metricPerformanArray.add(rmdf[len].getId());
				metricUnit.put(rmdf[len].getId(), rmdf[len].getUnit());
			}else if(rmdf[len].getMetricType() == MetricTypeEnum.AvailabilityMetric){
				metricAvailabArray.add(rmdf[len].getId());
				metricUnit.put(rmdf[len].getId(), rmdf[len].getUnit());
			}else if(rmdf[len].getMetricType() == MetricTypeEnum.InformationMetric){
				metricInfoArray.add(rmdf[len].getId());
				metricUnit.put(rmdf[len].getId(), rmdf[len].getUnit());
			}
		}
		
		instanceIdArray = new long[list.size()];
		for(int i=0;i<list.size();i++){
			instanceIdArray[i] = list.get(i).getId();
		}
		String[] metricPerforman = new String[metricPerformanArray.size()] ;
		String[] metricAvailab = new String[metricAvailabArray.size()] ;
		String[] metricInfo = new String[metricInfoArray.size()] ;
		metricPerformanArray.toArray(metricPerforman);
		metricAvailabArray.toArray(metricAvailab);
		metricInfoArray.toArray(metricInfo);
		
		MetricRealtimeDataQuery mr = new MetricRealtimeDataQuery();
		mr.setMetricID(metricPerforman);
		mr.setInstanceID(instanceIdArray);
		
		List<Map<String, ?>> mapList = metricDataService.queryRealTimeMetricData(mr);
		
//		List<Map<String, ?>> mapList = page.getDatas();
		List<Map<String,String>> listMap = new ArrayList<Map<String,String>>();
		
		List<CustomModuleProp> allModuleProp = null;
		if("NetInterface".equals(metricType)){
			allModuleProp = customModulePropService.getCustomModuleProp();
		}
		
		for(ResourceInstance resourceIns:list){
			Map<String , String> mapOne = new HashMap<String,String>();
			
			long insId = resourceIns.getId();
			if("NetInterface".equals(metricType)){
				Pattern pattern = Pattern.compile("[0-9]*");
				String[] ifIndex = resourceIns.getModulePropBykey("ifIndex");
				int index = ifIndex != null && !"".equals(ifIndex[0]) && pattern.matcher(ifIndex[0]).matches() ? Integer.valueOf(ifIndex[0]) : Integer.MAX_VALUE;
				mapOne.put("ifIndex", String.valueOf(index));
			}
			
			//固定的两列
			String showName = (null==resourceIns.getShowName()?resourceIns.getName():resourceIns.getShowName());
			mapOne.put("resourceRealName", resourceIns.getName());
			mapOne.put("resourceName", showName);
			mapOne.put("resourceName_ForSort", showName);
			mapOne.put("resourceNameAndState_ForSort", showName);
			mapOne.put("categoryId", resourceIns.getCategoryId());
			InstanceStateData isd = instanceStateService.getState(resourceIns.getId());
			//告警状态
			if(null!=isd && null!=isd.getState()){
				mapOne.put("resourceState", isd.getState().toString());
			}else{
				mapOne.put("resourceState", InstanceStateEnum.UNKNOWN_NOTHING.name());
			}
			// 如果是标准服务的资源详情
			if("Standard_Application_Dialog".equals(metricType)){
				mapOne.put("resourceShowName", showName);
				mapOne.put("resourceShowName_ForSort", showName);
			}
			
			mapOne.put("instanceid", String.valueOf(resourceIns.getId()));
			//获取信息指标
			List<MetricData> md = metricDataService.getMetricInfoDatas(resourceIns.getId(), metricInfo);
			if(null!=md){
				for(String str:metricInfo){
					boolean haveValue = false;
					boolean metricFlag = true; 
					mapOne.put("ifSpeedHandleState", ifSpeedHandleState);
					if("NetworkDevice".equals(resourceType) && "ifSpeed".equals(str)){
					  
      				  //查询是否经过手动修改
					  String value = null,realTimeValue = null;
					  if(null!=allModuleProp){
						  for(CustomModuleProp cmp:allModuleProp){
							  if(cmp.getInstanceId()==insId && cmp.getKey().equals("ifSpeed")){
								  value = cmp.getUserValue();
								  realTimeValue = cmp.getRealtimeValue();
								  break;
							  }
						  }
					  }
					  if(null!=value){
						  String realTimeValueData = "--";
						  if(null!=realTimeValue && !"".equals(realTimeValue)){
							  realTimeValueData = parseArrayToString(new String[]{realTimeValue},metricUnit.get(str));
						  }
						  mapOne.put(str, realTimeValueData);
						  String[] setValue = {value};
						  mapOne.put(str+"_HandSetValue", parseArrayToString(setValue,metricUnit.get(str)));//value+"Mbps");
	      				  //用于前台排序
						  float value_forSort = Float.parseFloat(value);//*1000*1000;
						  
//						  mapOne.put(str+"_RealTimeForSort", realTimeValue==null?"0":realTimeValue);
	      				  mapOne.put(str+"_ForSort", String.valueOf(value_forSort));
	      				  mapOne.put("ifSpeed_HandSet", "true");
		        		  haveValue = true;
		        		  metricFlag = false;
					  }
      			  	}
						for(MetricData mds:md){
	   	        		  if(str.equals(mds.getMetricId())){
	   	        			  if(null!=mds.getData()&&0!=mds.getData().length){
	   	        				  if(metricFlag){
	   	        					  mapOne.put(str, parseArrayToString(mds.getData(),metricUnit.get(str)));
		   	        				  //用于前台排序
		   	        				  mapOne.put(str+"_ForSort", parseArrayToString(mds.getData(),""));
		   		        			  haveValue = true;
	   	        				  }
	   	        				  mapOne.put(str+"_RealTimeForSort", parseArrayToString(mds.getData(),""));
	   	        			  }
	   	        		  }
		               	 }
				    if(!haveValue){
				    	mapOne.put(str, "--");
				    	mapOne.put(str+"_ForSort", "--");
				    }
				    
                }
			}else{
				for(String str:metricInfo){
					mapOne.put(str+"_ForSort", "--");
                }
			}
			//获取可用性指标
			for(String str:metricAvailab){
				
				MetricStateData msd = metricStateService.getMetricState(insId, str);
				//资源状态
				if(null!=msd&&null!=msd.getState()){
					mapOne.put(str, msd.getState().toString());//+" "+metricUnit.get(str)
					mapOne.put(str+"_ForSort", msd.getState().toString());
				}else{
					//默认NORMAL,方便前台过滤
					mapOne.put(str, "NORMAL");
					mapOne.put(str+"_ForSort", "NORMAL");
				}
			}
			//获取性能指标
			boolean haveValue = false;
			for (Map<String, ?> map : mapList) {
				if (null != map.get("instanceid")&& resourceIns.getId() == Long.parseLong(map.get("instanceid").toString())) {
					for(String str:metricPerforman){
						if (null != map.get(str)) {
							haveValue = true;
							DecimalFormat df = new DecimalFormat("###.##");
//							mapOne.put(str, map.get(str).toString()+" "+metricUnit.get(str));
							mapOne.put(str, UnitTransformUtil.transform(df.format(map.get(str)).toString(),metricUnit.get(str)));
							//用于前台排序
							mapOne.put(str+"_ForSort", map.get(str).toString());
						}else{
							mapOne.put(str, "--");
							mapOne.put(str+"_ForSort", "--");
						}
					}
				}
			}
			
			listMap.add(mapOne);
		}
		//按策略对子资源某些未监控的指标赋值:N/A
		setUnMonitorMetricValue(listMap,instanceId,metricType,rmdf);
		// 接口进行排序
		if("NetInterface".equals(metricType)){
			Collections.sort(listMap, new Comparator<Map<String, String>>(){
				Pattern pattern = Pattern.compile("[0-9]+");
				@Override
				public int compare(Map<String, String> o1, Map<String, String> o2) {
					if(!o1.containsKey("ifIndex") || !pattern.matcher(o1.get("ifIndex")).matches()){
						o1.put("ifIndex", String.valueOf(Integer.MAX_VALUE));
					}
					if(!o2.containsKey("ifIndex") || !pattern.matcher(o2.get("ifIndex")).matches()){
						o2.put("ifIndex", String.valueOf(Integer.MAX_VALUE));
					}
					return Integer.valueOf(o1.get("ifIndex")) - Integer.valueOf(o2.get("ifIndex"));
				}
			});
		}
		return listMap;
	}
	
	public List<Map<String,String>> getMetricInfoByResourceId(long instanceId, String resourceId,List<Long> instanceidList){

		List<ResourceInstance> resourceInstanceList = new ArrayList<ResourceInstance>();
		try {
			resourceInstanceList = resourceInstanceService.getResourceInstances(instanceidList);
		} catch (InstancelibException e) {
			logger.error(e.getMessage());
		}
		List<String> metricInfoArray =new ArrayList<String>();
		List<String> metricAvailabArray =new ArrayList<String>();
		List<String> metricPerformanArray =new ArrayList<String>();
		Map<String,String> metricUnit =new HashMap<String,String>();
		long[] instanceIdArray ;
		
		ResourceMetricDef[] rmdf = getHeaderInfoByResourceId(instanceId, resourceId);
		for(int len = 0;len<rmdf.length;len++){
			if( rmdf[len].getMetricType() == MetricTypeEnum.PerformanceMetric){
				metricPerformanArray.add(rmdf[len].getId());
				metricUnit.put(rmdf[len].getId(), rmdf[len].getUnit());
			}else if(rmdf[len].getMetricType() == MetricTypeEnum.AvailabilityMetric){
				metricAvailabArray.add(rmdf[len].getId());
				metricUnit.put(rmdf[len].getId(), rmdf[len].getUnit());
			}else if(rmdf[len].getMetricType() == MetricTypeEnum.InformationMetric){
				metricInfoArray.add(rmdf[len].getId());
				metricUnit.put(rmdf[len].getId(), rmdf[len].getUnit());
			}
		}
		
		instanceIdArray = new long[resourceInstanceList.size()];
		for(int i=0;i<resourceInstanceList.size();i++){
			instanceIdArray[i] = resourceInstanceList.get(i).getId();
		}
		String[] metricPerforman = new String[metricPerformanArray.size()] ;
		String[] metricAvailab = new String[metricAvailabArray.size()] ;
		String[] metricInfo = new String[metricInfoArray.size()] ;
		metricPerformanArray.toArray(metricPerforman);
		metricAvailabArray.toArray(metricAvailab);
		metricInfoArray.toArray(metricInfo);
		
		MetricRealtimeDataQuery mr = new MetricRealtimeDataQuery();
		mr.setMetricID(metricPerforman);
		mr.setInstanceID(instanceIdArray);
		
		List<Map<String, ?>> mapList = metricDataService.queryRealTimeMetricData(mr);//queryRealTimeMetricDatas(mr, 0, 10000);
		 
//		List<Map<String, ?>> mapList = page.getDatas();
		List<Map<String,String>> listMap = new ArrayList<Map<String,String>>();
		
		for(ResourceInstance resourceIns:resourceInstanceList){
			Map<String , String> mapOne = new HashMap<String,String>();
			//固定的两列
			mapOne.put("resourceName", resourceIns.getName());
			mapOne.put("resourceName_ForSort", resourceIns.getName());
			mapOne.put("resourceNameAndState_ForSort", resourceIns.getName());
			
			InstanceStateData isd = instanceStateService.getState(resourceIns.getId());
			 
			if(null!=isd && null!=isd.getState()){
				mapOne.put("resourceState", isd.getState().name());
			}else{
//				mapOne.put("resourceState", InstanceStateEnum.UNKOWN.name());
				mapOne.put("resourceState", InstanceStateEnum.NORMAL.name());
			}
			
			mapOne.put("instanceid", String.valueOf(resourceIns.getId()));
			//获取信息指标    查询信息指标需要过滤
//			List<MetricData> md = metricDataService.getMetricInfoDatas(resourceIns.getId(), metricInfo);
			List<MetricData> md = infoMetricQueryAdaptService.getMetricInfoDatas(resourceIns.getId(), metricInfo);
			if(null!=md){
				for(String str:metricInfo){
					boolean haveValue = false;
				    for(MetricData mds:md){
	        		  if(str.equals(mds.getMetricId())){
	        			  if(null!=mds.getData()&&0!=mds.getData().length){
	        				  mapOne.put(str, parseArrayToString(mds.getData(),metricUnit.get(str)));
	        				  //用于前台排序
	        				  mapOne.put(str+"_ForSort", parseArrayToString(mds.getData(),""));
		        			  haveValue = true;
	        			  }
	        		  }
            	    }
				    if(!haveValue){
				    	mapOne.put(str, "--");
				    	mapOne.put(str+"_ForSort", "--");
				    }
				    
                }
			}else{
				for(String str:metricInfo){
					mapOne.put(str+"_ForSort", "--");
                }
			}
			//获取可用性指标
			for(String str:metricAvailab){
				
				MetricStateData msd = metricStateService.getMetricState(resourceIns.getId(), str);
				
				if(null!=msd&&null!=msd.getState()){
					mapOne.put(str, msd.getState().name());//+" "+metricUnit.get(str)
					mapOne.put(str+"_ForSort", msd.getState().name());
				}else{
					mapOne.put(str, "--");
					mapOne.put(str+"_ForSort", "--");
				}
			}
			//获取性能指标
			for (Map<String, ?> map : mapList) {
				
				if (null != map.get("instanceid")&& resourceIns.getId() == Long.parseLong(map.get("instanceid").toString())) {
					for(String str:metricPerforman){
						if (null != map.get(str)) {
//							mapOne.put(str, map.get(str).toString()+" "+metricUnit.get(str));
							mapOne.put(str, UnitTransformUtil.transform(map.get(str).toString(),metricUnit.get(str)));
							//用于前台排序
							mapOne.put(str+"_ForSort", map.get(str).toString());
						}else{
							mapOne.put(str, "--");
							mapOne.put(str+"_ForSort", "--");
						}
					}
				}
		   }
		   listMap.add(mapOne);
	  }
	  //按策略对子资源某些未监控的指标赋值:N/A
//		setUnMonitorMetricValue(listMap,instanceId,metricType,rmdf);
	return listMap;

	}
	private void setUnMonitorMetricValue(List<Map<String, String>> listMap, long instanceId, String metricType, ResourceMetricDef[] rmdf){	
		try{
			//标准服务直接取主资源策略信息
			if(!"Standard_Application_Dialog".equals(metricType)){
				ProfileInfo pi = profileService.getBasicInfoByResourceInstanceId(instanceId);
			    Profile pf = profileService.getProfilesById(pi.getProfileId());
				
			    List<Profile> pfList = pf.getChildren();
			    for (int i = 0; null != pfList && i < pfList.size(); i++) {
			    	Profile pFile = pfList.get(i);
		    		ProfileInfo pfInfo = pFile.getProfileInfo();
				    if(null != pfInfo){
				    	ResourceDef rdf = capacityService.getResourceDefById(pfInfo.getResourceId());
				    	if(null!=rdf && metricType.equals(rdf.getType())){
			    			MetricSetting metricSet = pFile.getMetricSetting();
			    			for(ResourceMetricDef rmd : rmdf){
			    				boolean flag = false;
			    				for(ProfileMetric pm : metricSet.getMetrics()){
			    					if((pm.getMetricId().equals(rmd.getId()) && rmd.isDisplay() && (pm.isMonitor()||pm.getTimeLineId()>0))
			    							|| ("ifType".equals(rmd.getId()) && "NetInterface".equals(metricType))){
			    						flag = true;
			    						break;
			    					}
			    				}
			    				if(!flag){
			    					ProfileInstanceRelation pir = pFile.getProfileInstanceRelations();
					    			for(Instance ins:pir.getInstances()){
					    				for(Map<String, String> map:listMap){
					    					if(map.get("instanceid").equals(String.valueOf(ins.getInstanceId()))){
					    						map.put(rmd.getId(), "N/A");
					    						break;
					    					}
					    				}
					    			}
			    				}
			    			}
				    	}
				    }
				}
			}
		}catch(Exception e){
			logger.error(e.getMessage());
		}
	}
	
	private static String parseArrayToString(String[] strArr , String unit){
		StringBuffer sb = new StringBuffer();
		for(int i=0;i<strArr.length;i++){
//			sb.append(strArr[i]+unit);
//			if(i!=(strArr.length-1)){
//				sb.append(",");
//			}
			if(i==0){
				sb.append(strArr[i]);
			}else{
				sb.append(","+strArr[i]);
			}
		}
		
		return UnitTransformUtil.transform(sb.toString(), unit);
	}
	
	private ResourceInstance getResourceInstance(long instanceId){
        try {
        	
        	ResourceInstance rit = resourceInstanceService.getResourceInstance(instanceId);
        	
			return rit;
		} catch (Exception e) {
			logger.error(e.getMessage());
			return null;
		}
	}
	
	public String getStandardApplicationCurrentState(Long instanceId){
		try{
		
		InstanceStateData  isd = instanceStateService.getState(instanceId);//.catchRealtimeMetricData(instanceId);
		
		
		String state = null;
		if(null!=isd&&null!=isd.getState()){
			state = isd.getState().name();
		}else{
//			state = InstanceStateEnum.UNKOWN.name();
			state = InstanceStateEnum.NORMAL.name();
		}
		
		return state;
		}catch(Exception e){
			e.printStackTrace();
			logger.error(e.getMessage());
			return null;
		}
         
	}
	
	public boolean updateResourceShowName(Long instanceId,String showName){
		try {
			resourceInstanceService.updateResourceInstanceName(instanceId, showName);
			return true;
		} catch (InstancelibException e) {
			e.printStackTrace();
			logger.error(e.getMessage());
			return false;
		}
	}

	@Override
	public Map<String, Object> getMetricData(long instanceId,
			String metricType,String type) {
		Map<String, Object> map= new HashMap<String, Object>();
		MetricData metricData= new MetricData();
		//获取信息指标
		try {
			if(type.equals("0")){             
				metricData=	metricDataService.catchRealtimeMetricData(instanceId, "sqlTop10CPUTime");
			}else if(type.equals("1")){
				metricData=	metricDataService.catchRealtimeMetricData(instanceId, "sqlTop10DiskReads");
			}else if(type.equals("2")){
				metricData=	metricDataService.catchRealtimeMetricData(instanceId, "sqlTop10BufferGets");
			}else if(type.equals("3")){
				metricData=	metricDataService.catchRealtimeMetricData(instanceId, "sqlTop10ElapseTime");	
			}
		} catch (MetricExecutorException e) {
			e.printStackTrace();
			logger.error("getMetricData", e);
		}
		List<String> list= new ArrayList<String>();
        String[] data1 = metricData.getData();
        if(metricData!=null && data1 !=null){
            List<Map<String, String>> maps = MetricDataUtil.parseTableResultSet(data1);
            for(Map<String, String> mapInfo : maps){
                Set<String> strings = mapInfo.keySet();
                for(String key : strings){
                    String s = mapInfo.get(key);
                    list.add(s);
                }
            }
            //for (String data : data1) {
			//	list.add(data);
			//}
		}
		map.put("metricData", list);
		return map;
	}
	
	
}
