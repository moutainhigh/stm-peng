package com.mainsteam.stm.home.workbench.topn.service.impl;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.annotation.Resource;

import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;

import com.mainsteam.stm.caplib.CapacityService;
import com.mainsteam.stm.caplib.dict.MetricIdConsts;
import com.mainsteam.stm.caplib.resource.ResourceDef;
import com.mainsteam.stm.caplib.resource.ResourceMetricDef;
import com.mainsteam.stm.common.metric.MetricDataService;
import com.mainsteam.stm.common.metric.MetricStateService;
import com.mainsteam.stm.common.metric.obj.MetricData;
import com.mainsteam.stm.common.metric.obj.MetricStateEnum;
import com.mainsteam.stm.home.workbench.resource.api.IResourceApi;
import com.mainsteam.stm.home.workbench.topn.api.IHomeTopnApi;
import com.mainsteam.stm.instancelib.ResourceInstanceService;
import com.mainsteam.stm.instancelib.exception.InstancelibException;
import com.mainsteam.stm.instancelib.obj.ResourceInstance;
import com.mainsteam.stm.portal.resource.api.InfoMetricQueryAdaptApi;
import com.mainsteam.stm.state.obj.MetricStateData;

/**
 * <li>文件名称: HomeTopnServiceImpl.java</li>
 * <li>公　　司: 武汉美新翔盛科技有限公司</li>
 * <li>版权所有: 版权所有(C)2019-2020</li>
 * <li>修改记录: ...</li>
 * <li>内容摘要: ...</li>
 * <li>其他说明: ...</li>
 * @version  ms.stm
 * @since   2019年9月18日
 * @author   zhangjunfeng
 */
public class HomeTopnServiceImpl implements IHomeTopnApi {
	/**
	 * Logger for this class
	 */
	private static final Logger logger = Logger.getLogger(HomeTopnServiceImpl.class);

	@Resource
	private MetricDataService metricDataService;
	@Resource
	private MetricStateService metricStateService;
	@Resource
	private ResourceInstanceService resourceInstanceService;
	@Resource
	private InfoMetricQueryAdaptApi infoMetricQueryAdaptService;
	
	@Resource
	private CapacityService capacityService;
	
	@Autowired
	@Qualifier("stm_home_workbench_resourceApi")
	private IResourceApi resourceApi;
	
	@Override
	public Map<String,Object> getHomeTopnData(String metricId,List<Long> resourceIds,int top,String sortMethod,String showPattern, boolean isSubMetrics) {
		if (logger.isDebugEnabled()) {
			logger.debug("getHomeTopnData(String, String, List<Long>) - start"); //$NON-NLS-1$
		}
		logger.info("malachi HomeTopnServiceImpl metricId = " + metricId);
		logger.info("malachi HomeTopnServiceImpl resourceIds = " + resourceIds);
		logger.info("malachi HomeTopnServiceImpl top = " + top);
		logger.info("malachi HomeTopnServiceImpl sortMethod = " + sortMethod);
		logger.info("malachi HomeTopnServiceImpl showPattern = " + showPattern);
		logger.info("malachi HomeTopnServiceImpl isSubMetrics = " + isSubMetrics);
		//通过resource实例ID获取topn
		List<MetricData> result = new ArrayList<MetricData>();
		if(metricId!=null){
			int sortType=0;//desc
			logger.info("malachi HomeTopnServiceImpl sortType = " + sortType);
			if(sortMethod.equalsIgnoreCase("asc")){
				 sortType=1;
			}
			logger.info("malachi HomeTopnServiceImpl sortType = " + sortType);
			if(resourceIds!=null && resourceIds.size()>0){
				long[] myInstanceIds = new long[resourceIds.size()];;
				for (int i=0;i<resourceIds.size();i++) {
					myInstanceIds[i]=resourceIds.get(i);
				}
				if(myInstanceIds!=null && myInstanceIds.length>0){
					if(metricId.equals("cpu")){
						result = metricDataService.findTop(MetricIdConsts.METRIC_CPU_RATE,myInstanceIds, top,sortType);
					}else if(metricId.equals("memory")){
						result = metricDataService.findTop(MetricIdConsts.METRIC_MEME_RATE,myInstanceIds, top,sortType);
					}else{
						result = metricDataService.findTop(metricId,myInstanceIds, top,sortType);
					}
				}
			}
		}
		logger.info("malachi HomeTopnServiceImpl result.size() = " + result.size());
		Map<String, Object> resultMap = new HashMap<String, Object>();
		List<Map<String, Object>> data = new ArrayList<Map<String, Object>>();
		List<String> cate = new ArrayList<String>();
		try {
			if (result != null && result.size() > 0) {
				logger.info("malachi HomeTopnServiceImpl result = " + result);
				for (MetricData metric : result) {
					ResourceInstance resourceInstance = resourceInstanceService.getResourceInstance(metric.getResourceInstanceId());
					Map<String, Object> dataObj = new HashMap<String, Object>();
					logger.info("malachi HomeTopnServiceImpl name = " + resourceInstance==null?"":getResourceName(resourceInstance));
					logger.info("malachi HomeTopnServiceImpl y = " + metric.getData()==null?0:metric.getData()[0]);
					logger.info("malachi HomeTopnServiceImpl resourceInstanceId = " + metric.getResourceInstanceId());

					dataObj.put("name", resourceInstance==null?"":getResourceName(resourceInstance));
					dataObj.put("y", metric.getData()==null?0:metric.getData()[0]);
					dataObj.put("resourceInstanceId", metric.getResourceInstanceId());
					if(isSubMetrics){
                        ResourceInstance parentInstance = resourceInstance.getParentInstance();
                        if("name".equals(showPattern)){
                            dataObj.put("name", parentInstance == null? "" : getResourceName(parentInstance));
                        }else{
                            dataObj.put("name", parentInstance == null? "" : getResourceIP(parentInstance));
                        }
                        dataObj.put("subName", resourceInstance == null? "" : getResourceName(resourceInstance));
						logger.info("malachi HomeTopnServiceImpl name = " + dataObj.get("name"));
						logger.info("malachi HomeTopnServiceImpl name = " + dataObj.get("subName"));
                    }
                    if("name".equals(showPattern)){
                        cate.add(getResourceName(resourceInstance));
                    }else{
                        cate.add(getResourceIP(resourceInstance));
                    }
					data.add(dataObj);
					this.setCpuOrMemoryStatus(dataObj, resourceInstance,metricId);
				}
				resultMap.put("data", data);
				resultMap.put("categories", cate);
                resultMap.put("isSubMetrics",isSubMetrics);
				logger.info("malachi HomeTopnServiceImpl end............ ");
			}
		} catch (InstancelibException e) {
			logger.error("getHomeTopnData(String, String, List<Long>)", e); //$NON-NLS-1$
			e.printStackTrace();
		}

		if (logger.isDebugEnabled()) {
			logger.debug("getHomeTopnData(String, String, List<Long>) - end"); //$NON-NLS-1$
		}
		logger.info("malachi HomeTopnServiceImpl end2............ ");
		return resultMap;
	}
	
	// 获取name
	private String getResourceName(ResourceInstance ri){
		String name = ri.getShowName();
		if(name == null || name.equals(""))
			name = ri.getName();
		if(name == null || name.equals("")){
			try {
				ResourceInstance pri = resourceInstanceService.getResourceInstance(ri.getParentId());
				if(pri != null){
					ResourceDef def = capacityService.getResourceDefById(ri.getResourceId());
					name = getResourceName(pri) + def.getName();
				}
			} catch (InstancelibException e) {
				logger.error(e.getMessage(), e);
				e.printStackTrace();
			}
			
		}
			
		return name;
	}
		
	// 获取IP地址
	private String getResourceIP(ResourceInstance ri){
		if(ri == null){
			return "";
		}else if(ri.getShowIP() != null && !"".equals(ri.getShowIP())){
			return ri.getShowIP();
		}else{
//			MetricData md = metricDataService.getMetricInfoData(ri.getId(), "ip");
			//查询信息指标需要过滤
			MetricData md = infoMetricQueryAdaptService.getMetricInfoData(ri.getId(), "ip");
			
			String ip = md != null && md.getData() != null && md.getData().length > 0 ? md.getData()[0] : "";
			//链路不存在ip地址，因此用showName代替
			if(ip == null || ip.equals("")){
				ip = ri.getShowName();
			}
			return ip;
		}
	}
	/**
	 * 设置查询资源cpu 或者memory 指标状态，用户topon 柱状图显示使用
	 * @param tag 查询到结果目标结果对象
	 * @param resourceInstance 
	 */
	private void  setCpuOrMemoryStatus(Map<String,Object> tag,ResourceInstance resourceInstance,String metricId){
		if(tag == null  || tag.isEmpty() || resourceInstance == null ){
			return;
		}
		ResourceMetricDef def = null;
		if("cpu".equals(metricId)){
			def = capacityService.getResourceMetricDef(resourceInstance.getResourceId(), MetricIdConsts.METRIC_CPU_RATE);
		}else if("memory".equals(metricId)){
			def = capacityService.getResourceMetricDef(resourceInstance.getResourceId(), MetricIdConsts.METRIC_MEME_RATE);
		}else{
			//这里加这个是因为首页topn查询资源的时候还查询其它类型指标
			def = capacityService.getResourceMetricDef(resourceInstance.getResourceId(), metricId);
		}
		
		if(def != null){
			MetricStateData metric = metricStateService.getMetricState(resourceInstance.getId(), def.getId());
			
			if(metric != null){
				tag.put("rawStatus", metric.getState());//提供给首页用的，建议后面统一成这种，颜色都属性尽量放到前台去处理儿不要放到后台，便于修改调整，bytandl
			}else{
				tag.put("rawStatus", MetricStateEnum.NORMAL);
			}
			
			tag.put("status", getMetricStateEnumString(metric));
		}else{
			logger.error("Get def error , resourceID : " + resourceInstance.getResourceId() + ", metric : " + metricId);
		}
		
	}
	
	private String getMetricStateEnumString(MetricStateData metricStateData){
		String rst = "gray";
		if(metricStateData == null ){
			return "green";
		}
		switch (metricStateData.getState()) {
			case CRITICAL:
				rst =  "red";
				break;
			case SERIOUS:
				rst =  "orange";
				break;
			case WARN:
				rst =  "yellow";
				break;
			case NORMAL:
			case NORMAL_NOTHING:
				rst =  "green";
				break;
			default :
				rst = "gray";
				break;
				
		}
		return rst;
	}
	
}
