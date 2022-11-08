package com.mainsteam.stm.portal.resource.web.action;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.annotation.Resource;

import org.apache.log4j.Logger;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.multipart.MultipartFile;












import com.alibaba.fastjson.JSONObject;
import com.mainsteam.stm.platform.web.action.BaseAction;
import com.mainsteam.stm.platform.web.vo.ILoginUser;
import com.mainsteam.stm.portal.resource.api.IResourceDetailInfoApi;
import com.mainsteam.stm.portal.resource.web.vo.MetricGroupVo;
import com.mainsteam.stm.portal.resource.web.vo.PageParamterVo;



/**
 * 信息详细信息展示
 *
 */
@Controller
@RequestMapping(value = "/portal/resource/resourceDetailInfo")
public class ResourceDetailInfoAction extends BaseAction {
	private Logger log = Logger.getLogger(ResourceDetailInfoAction.class);
	@Resource(name = "resourceDetailInfoApi")
	private IResourceDetailInfoApi resourceDetailInfoApi;

	/**
	 * 查询资源详细信息
	 * 
	 * @param instanceId
	 * @return
	 */
	@RequestMapping(value="/getResourceInfo")
	public JSONObject getResourceInfo(Long instanceId){
		Map<String, Object> result = resourceDetailInfoApi.getResourceInfo(instanceId);
		return toSuccess(result);
	}
	@RequestMapping(value="/getAllMetricinfo")
	public JSONObject getAllMetricinfo(MetricGroupVo metricGroup){

		List<Map<String, Object>> metricData = resourceDetailInfoApi.getAllMetric(metricGroup.getInstanceId());
		Map<String, Object> results = new HashMap<String, Object>();
		MetricGroupVo metricGVo = new MetricGroupVo();
		results.put("totals", metricData.size());
		results.put("totalRecords", metricData.size());
		results.put("rows", metricData);
		return toSuccess(results);
	}
	
	/**
	 * 获取指标
	 * @param instanceId
	 * @param metricType
	 * @return
	 */
	@RequestMapping(value= "/getMetric")
	public JSONObject getMetric(Long instanceId, String metricType){
		List<Map<String, Object>> metricList = resourceDetailInfoApi.getMetricByType(instanceId, metricType,false);
		Map<String, Object> result = new HashMap<String, Object>();
		result.put("total", metricList.size());
		result.put("totalRecord", metricList.size());
		result.put("rows", metricList);
		return toSuccess(result);
	}
	
	@RequestMapping(value="/getMetric4General")
	public JSONObject getMetric4General(Long instanceId){
		ILoginUser user= getLoginUser();
		return toSuccess(resourceDetailInfoApi.getMetricFromXML(instanceId,user));
	}
	@RequestMapping(value="/getMetric4Generalnew")
	public JSONObject getMetric4Generalnew(Long instanceId){
		
		
		
		return toSuccess(getXMLInfo(instanceId));
	}
	
	public List<List<Map<String, Object>>> getXMLInfo(Long instanceId){

		// TODO Auto-generated method stub
		/***
		 * list 包含三个div块的内容
					 */
		List<List<Map<String, Object>>> all = new ArrayList<List<Map<String,Object>>>();
			//每个tr
			List<Map<String, Object>> list= new ArrayList<Map<String, Object>>();
			//每个td块
			Map<String, Object> map = new HashMap<String, Object>();
			//div块的内容信息
			List<PageParamterVo> contentlist= new ArrayList<PageParamterVo>();
			PageParamterVo bean = new PageParamterVo();
			bean.setTitle("基础信息");
			bean.setWidth("30%");
			bean.setComponentType("form");
			List<PageParamterVo> childs= new ArrayList<PageParamterVo>();
			PageParamterVo childBean = new PageParamterVo();
			childBean.setType("text");
			childBean.setName("showname");
			childBean.setWidth("10%");
			PageParamterVo childBean2 = new PageParamterVo();
			childBean2.setType("text");
			childBean2.setName("ip");
			childBean2.setWidth("10%");
			childs.add(childBean2);
			PageParamterVo childBean3 = new PageParamterVo();
			childBean3.setType("text");
			childBean3.setName("resourcename");
			childBean3.setWidth("10%");
			childs.add(childBean3);
			bean.setChildParamter(childs);
			
			contentlist.add(bean);
			//取div内容并填充值
			map.put("width", "100%");
			map.put("ComponentType", "tr");
			map.put("divcontent", contentlist);
			
			Map<String, Object> map2 = new HashMap<String, Object>();
			//div块的内容信息
			List<PageParamterVo> contentlist2= new ArrayList<PageParamterVo>();
			PageParamterVo bean2 = new PageParamterVo();
			bean2.setTitle("性能指标");
			bean2.setWidth("40%");
			bean2.setComponentType("div");
	/*		List<PageParamterVo> childs= new ArrayList<PageParamterVo>();
			PageParamterVo childBean = new PageParamterVo();
			childBean.setType("text");
			childBean.setName("showname");
			childBean.setWidth("10%");
			PageParamterVo childBean2 = new PageParamterVo();
			childBean2.setType("text");
			childBean2.setName("ip");
			childBean2.setWidth("10%");
			childs.add(childBean2);
			PageParamterVo childBean3 = new PageParamterVo();
			childBean3.setType("text");
			childBean3.setName("resourcename");
			childBean3.setWidth("10%");
			childs.add(childBean3);
			bean2.setChildParamter(childs);*/
			contentlist2.add(bean2);
			
			//取div内容并填充值]
			map2.put("width", "100%");
			map2.put("ComponentType", "tr");
			map2.put("divcontent", contentlist2);
			
			Map<String, Object> map3 = new HashMap<String, Object>();
			//div块的内容信息
			List<PageParamterVo> contentlist3= new ArrayList<PageParamterVo>();
			PageParamterVo bean3 = new PageParamterVo();
			bean3.setTitle("分区利用率");
			bean3.setWidth("30%");
			bean3.setComponentType("div");
			contentlist3.add(bean3);
			//取div内容并填充值
			map3.put("width", "100%");
			map3.put("ComponentType", "tr");
			map3.put("divcontent", contentlist3);
			
			list.add(map);
			list.add(map2);
			list.add(map3);
			all.add(list);
			System.out.println(all);


	
		
		return all;
		
		
	}
	
	@RequestMapping(value="/resouceImgUpload", headers="content-type=multipart/*", method=RequestMethod.POST)
	@ResponseBody
	public long resouceImgUpload(@RequestParam("file") MultipartFile file,@RequestParam("instanceId")long instanceId) {
		
		long fileId = resourceDetailInfoApi.saveResourceImg(file, instanceId);
		
		return fileId;
	}
	
	@RequestMapping(value="/getChildInstance")
	public JSONObject getChildInstanceMetric(Long instanceId, String childType){
		List<Map<String, Object>> allmetrcis = resourceDetailInfoApi.getChildInstance(instanceId, childType);
		return toSuccess(allmetrcis);
	}
	
	@RequestMapping(value="/getAllMetric")
	public JSONObject getAllMetric(Long instanceId){
		List<Map<String, Object>> metricData = resourceDetailInfoApi.getAllMetric(instanceId);
		return toSuccess(metricData);
	}
	/**
	 * 手动获取信息指标
	 * @param instanceId
	 */
	@RequestMapping(value="/getMetricHand")
	public JSONObject getMetricHand(Long instanceId) {
		String metricHandData = resourceDetailInfoApi.getMetricHand(instanceId);
		return toSuccess(metricHandData);
	}
	/**
	 * 查询是否在手动采集
	 * @param instanceId
	 * @return
	 */
	@RequestMapping(value="/isMetricHand")
	public JSONObject isMetricHand(Long instanceId) {
		String isHand = resourceDetailInfoApi.isMetricHand(instanceId);
		return toSuccess(isHand);
	}
	
	/**
	 * 更改端口管理状态
	 * 
	 * @param instanceId
	 * @return
	 */
	@RequestMapping(value="/editPortStatus")
	public JSONObject editPortStatus(long instanceId, String condition){
		return toSuccess(resourceDetailInfoApi.editPortStatus(instanceId, condition));
	}

	/***
	 * 查所有进程数据
	 * @param instanceId
	 * @return
	 */
	@RequestMapping(value="/getMerticInfos")
	public JSONObject getMerticInfos(long instanceId,String type){
		return toSuccess(resourceDetailInfoApi.getMerticinfos(instanceId,type));
	}

	
	@RequestMapping(value="/getcustomMetric")
	public JSONObject getcustomMetric(MetricGroupVo metricGroup){//.getcustomMetric(metricGroup.getInstanceId(),metricGroup.getMetricName());
		List<Map<String, Object>> metricData = resourceDetailInfoApi.getcustomMetric(metricGroup.getInstanceId(),metricGroup.getMetricName());
		Map<String, Object> results = new HashMap<String, Object>();
		results.put("totals", metricData.size());
		results.put("totalRecords", metricData.size());
		results.put("rows", metricData);
		return toSuccess(results);
	}
	
	@RequestMapping(value="/getAllMetricTop")
	public JSONObject getAllMetricTop(MetricGroupVo metricGroup){//.getcustomMetric(metricGroup.getInstanceId(),metricGroup.getMetricName());
		List<Map<String, Object>> metricList = resourceDetailInfoApi.getMetricByType(metricGroup.getInstanceId(), "PerformanceMetric",false);
		String ids="";
		if(!metricList.isEmpty()){
			for (int i = 0; i < metricList.size(); i++) {
				Map<String, Object> map=metricList.get(i);
				if(i<2){
					if(metricList.size()==1 ){
						ids+=(String) map.get("id");	
					}else{
						ids+=(String) map.get("id")+",";	
					}
					
				}
			
			}
		}
		List<Map<String, Object>> metricData = resourceDetailInfoApi.getcustomMetric(metricGroup.getInstanceId(),metricGroup.getMetricName());
		Map<String, Object> results = new HashMap<String, Object>();
		results.put("totals", metricData.size());
		results.put("totalRecords", metricData.size());
		results.put("rows", metricData);
		results.put("perMetrics", ids);
		return toSuccess(results);
	}
	/**
	 * 判断资源是否有查看权限
	 * @param instanceid
	 * @return
	 */
	@RequestMapping(value="/getDomainHave")
	public JSONObject getDomainHave(long instanceid){
		return toSuccess(resourceDetailInfoApi.getDomainHave(instanceid));
		
	}
	/**
	 * 接口流量top5
	 * @param instanceId
	 * @return
	 */
	@RequestMapping(value="/getMetricTop5")
 public JSONObject  getMetricTop5(long instanceId){
	 Map<String, Object> topMap=resourceDetailInfoApi.getMetricTop5(instanceId);
	 
	return toSuccess(topMap);
 }
}
