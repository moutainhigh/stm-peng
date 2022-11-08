/**
 * 
 */
package com.mainsteam.stm.home.workbench.metric.web.action;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.annotation.Resource;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.mainsteam.stm.common.metric.obj.MetricSummaryType;
import com.mainsteam.stm.home.workbench.metric.api.IMetricDataApi;
import com.mainsteam.stm.platform.web.action.BaseAction;
import com.mainsteam.stm.portal.business.api.BizMainApi;
import com.mainsteam.stm.portal.business.service.bo.BizMetricHistoryValueBo;

/**
 * 
 * <li>文件名称:MetricDataAction.java</li> 
 * <li>公　　司: 武汉美新翔盛科技有限公司</li>
 * <li>版权所有:版权所有(C)2019-2020</li>
 * <li>修改记录: ...</li>
 * <li>内容摘要: ...</li>
 * <li>其他说明: ...</li>
 * 
 * @version ms.stm
 * @since 2017年4月1日 下午3:56:55
 * @author tandl
 */
@Controller
@RequestMapping(value="/home/workbench/metric")
public class MetricDataAction extends BaseAction{
	
	@Autowired
	private BizMainApi bizMainApi;
	
	@Resource
	private IMetricDataApi metricDataApi;
	
	@RequestMapping("/getDatas")
	public JSONObject getMetricDatas(long instanceId, String[] metricId, long dateStart,long dateEnd,String summaryType){
		MetricSummaryType mtyp = null;
		try{
			mtyp = MetricSummaryType.valueOf(summaryType);
		}catch(Exception e){}
		Map<String, Object> dat = null;
		if(mtyp != null){
			dat = metricDataApi.getSummaryMetricData(instanceId, metricId, new Date(dateStart), new Date(dateEnd), mtyp);
		}else{
			dat = metricDataApi.getMetricData(instanceId, metricId, new Date(dateStart), new Date(dateEnd));
		}

		return  toSuccess(dat);
	}
	
	/**
	 * 获取多个实例下的多个指标在某个时间段内的值
	 * @param query JSONArray string
	 * 			结构形如：[
	 * 						{	"metricId":"cpuRate,memRate",
	 *							"resourceType":"Host",
	 * 							"instanceId":2116,
	 * 							"summaryType":"hour",
	 * 							"dateStart":1491904595137,
	 * 							"dateEnd":1491990995137
	 * 						}
	 * 					]
	 *  		其中summaryType 可以是hour、H、SH、D、HH
	 * @return
	 */
	@RequestMapping("/get")
	public JSONObject getMetricData(String query){
		JSONObject output = new JSONObject(); 
		JSONArray qy = (JSONArray)JSONArray.parse(query);
		
		for(int i=0; i<qy.size(); i++){
			JSONObject mdq  = qy.getJSONObject(i);
			
			String summaryType = mdq.getString("summaryType");
			String resourceType = mdq.getString("resourceType");
			long instanceId = mdq.getLongValue("instanceId");
			String [] metricId = mdq.getString("metricId").split(",");
			long dateStart = mdq.getLongValue("dateStart");
			long dateEnd =mdq.getLongValue("dateEnd");
			
			MetricSummaryType mtyp = null;
			try{
				if(summaryType.equals("hour"))
					summaryType = "H";
				mtyp = MetricSummaryType.valueOf(summaryType);
			}catch(Exception e){}
			Map<String, Object> dat = null;
			if(resourceType !=null &&  resourceType.equals("business")){
				dat = new HashMap<>();
				for(String mId: metricId){
					List<BizMetricHistoryValueBo> bi = bizMainApi.getHistoryDataForHomeMetric(instanceId, new Date(dateStart), new Date(dateEnd), mtyp, mId);
					dat.put(mId, bi);
				}
			}else{
                dat = metricDataApi.getMetricData(instanceId, metricId, new Date(dateStart), new Date(dateEnd));
				/*if(mtyp != null){
					dat = metricDataApi.getSummaryMetricData(instanceId, metricId, new Date(dateStart), new Date(dateEnd), mtyp);
				}else{
					dat = metricDataApi.getMetricData(instanceId, metricId, new Date(dateStart), new Date(dateEnd));
				}*/
			}
			
			output.put(instanceId + "", dat);
		}
		
		return  toSuccess(output);
	}
	
	/**
	 * 查询多个指标的当前值
	 * @param query
	 * @return
	 */
	@RequestMapping(value= "/getRealData")
	public JSONObject getMetric(String query){
		JSONObject output = new JSONObject(); 
		JSONObject instances = (JSONObject)JSONObject.parse(query);
		
		//JSONObject instances = qy.getJSONObject("instance");
		for(String iid: instances.keySet()){
			
			JSONObject instance = instances.getJSONObject(iid);
			JSONArray metricTypes = instance.getJSONArray("metricType");
			String metrics = instance.getString("metric");
			String  resourceType = instance.getString("resourceType");
			JSONObject metricData = new JSONObject();
			for (int i = 0; i < metricTypes.size(); i++) {
				String mt = metricTypes.getString(i);
				if(mt.equals(""))
					continue;
				List<Map<String, Object>> tdata = null;
				if(resourceType!=null && resourceType.equals("business")){
					tdata= new ArrayList<>();
					String[] bmts = metrics.split(",");
					for(String bmt:bmts){
						Map<String, Object> dt = bizMainApi.getFocusMetricDataForHome(Long.parseLong(iid), bmt);
						metricData.put(dt.get("id").toString(),dt);
					}
				}else{
					tdata = metricDataApi.getMetricByType(Long.parseLong(iid), mt);
					for(int j=0; j< tdata.size();j++){
						Map<String, Object> dt = tdata.get(j);
						if(metrics.indexOf( dt.get("id").toString() )>-1){
							metricData.put(dt.get("id").toString(),dt);
						}
					}
				}
			}
			output.put(iid, metricData);
		}
		
		
		return toSuccess(output);
	}
	
	
	/**
	 * 获取多个资源的温度指标
	 * @param instanceIds
	 * @param metricType 多个指标类型
	 * @return
	 */
	@RequestMapping(value= "/getTemperatureMetrics")
	public JSONObject getTemperatureMetrics(Long[] instanceIds){
		HashMap<Long,Object> output = new HashMap<>();
		for(Long instanceId :instanceIds){
			//List<Map<String, Object>> metricData = new ArrayList<>();
			List<Map<String, Object>> datas = metricDataApi.getMetricByType(instanceId, "InformationMetric");
			for(Map<String, Object> dt : datas){
				if(dt.get("id").toString().equals("temperatureValue")){
						//metricData.add(dt);
						output.put(instanceId, dt);
						break;
				}
			}
			
		}
		
		return toSuccess(output);
	}
	
}
