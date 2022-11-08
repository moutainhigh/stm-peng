package com.mainsteam.stm.portal.resource.web.action;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.annotation.Resource;
import javax.servlet.http.HttpSession;

import org.apache.log4j.Logger;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;

import com.alibaba.fastjson.JSONObject;
import com.mainsteam.stm.caplib.resource.ResourceMetricDef;
import com.mainsteam.stm.platform.web.action.BaseAction;
import com.mainsteam.stm.platform.web.vo.ILoginUser;
import com.mainsteam.stm.portal.resource.api.IResourceApplyApi;
import com.mainsteam.stm.portal.resource.service.impl.AlarmProfileQueryImpl;
import com.mainsteam.stm.portal.resource.web.vo.ResourceApplyPageVo;
import com.mainsteam.stm.portal.resource.web.vo.ResourceApplyVo;

/**
 * <li>文件名称: ResourceApplyAction.java</li>
 * <li>公　　司: 武汉美新翔盛科技有限公司</li>
 * <li>版权所有: 版权所有(C)2019-2020</li>
 * <li>修改记录: ...</li>
 * <li>内容摘要: 发现资源相关的操作</li>
 * <li>其他说明:</li>
 * 
 * @version ms.stm
 * @since 2019年9月15日
 * @author tpl
 */
@Controller
@RequestMapping("/portal/resource/resourceApply")
public class ResourceApplyAction extends BaseAction {
	private static Logger logger = Logger.getLogger(ResourceApplyAction.class);
	@Resource
	private IResourceApplyApi resourceApplyApi;

	
	/**
	 * 获取前台datagrid要显示的列
	 * 
	 * @return
	 */
	@RequestMapping("/getDatagridHeaderInfo")
	public JSONObject getDatagridHeaderInfo(long instanceId, String metricType){
		
		ResourceMetricDef[] rmdArr = resourceApplyApi.getHeaderInfoByMetricType(instanceId, metricType);
		List<Map<String,String>> rmdList = new ArrayList<Map<String,String>>();
		for(ResourceMetricDef rmd:rmdArr){
			Map<String,String> map = new HashMap<String,String>();
			map.put("metricId", rmd.getId());
			map.put("name", rmd.getName().toUpperCase());
			map.put("style", null==rmd.getMetricType()?"InformationMetric":rmd.getMetricType().name());
			map.put("unit", rmd.getUnit());
			map.put("isDisplay", rmd.isDisplay()?"true":"false");
			map.put("displayOrder", rmd.getDisplayOrder());
			rmdList.add(map);
		}
		
		return toSuccess(rmdList);
	}
	
	@RequestMapping("/getDatagridHeaderInfoByResourceId")
	public JSONObject getDatagridHeaderInfoByResourceId(long instanceId, String resourceId){
		
		ResourceMetricDef[] rmdArr = resourceApplyApi.getHeaderInfoByResourceId(instanceId, resourceId);
		List<Map<String,String>> rmdList = new ArrayList<Map<String,String>>();
		for(ResourceMetricDef rmd:rmdArr){
			Map<String,String> map = new HashMap<String,String>();
			map.put("metricId", rmd.getId());
			map.put("name", rmd.getName().toUpperCase());
			map.put("style", null==rmd.getMetricType()?"InformationMetric":rmd.getMetricType().name());
			map.put("unit", rmd.getUnit());
			map.put("isDisplay", rmd.isDisplay()?"true":"false");
			map.put("displayOrder", rmd.getDisplayOrder());
			rmdList.add(map);
		}
		
		return toSuccess(rmdList);
	}
	/**
	 * 查询oracletop10
	 * @param instanceId
	 * @param resourceId
	 * @return
	 */
	@RequestMapping("/getDatagridData0ByResourceId")
	public JSONObject getDatagridData0ByResourceId(long instanceId, String resourceId,String type){
		Map<String,Object> rmdList = new HashMap<String, Object>();
		rmdList=resourceApplyApi.getMetricData(instanceId, resourceId, type);
		return toSuccess(rmdList);
		
	}
	
	/**
	 * 获取资源子资源信息
	 * 
	 * @return
	 */
	@RequestMapping("/getMetricInfo")
	public JSONObject getMetricInfo(ResourceApplyVo resourceApplyVo,HttpSession session){
		//获取当前登录用户
		ILoginUser user = getLoginUser(session);
		
		List<Map<String,String>> list = resourceApplyApi.getMetricInfo(resourceApplyVo.getInstanceId(),resourceApplyVo.getMetricType(),resourceApplyVo.getResourceType(),user);
		
		ResourceApplyPageVo racPageVo = new ResourceApplyPageVo();
		racPageVo.setTotalRecord(list.size());
		racPageVo.setRows(list);
		racPageVo.setRowCount(list.size());
		
		return  toSuccess(racPageVo);
	}
	
	/**
	 * 手动设置接口带宽
	 * 
	 * @return
	 */
	@RequestMapping("/updateIfSpeedValue")
	public JSONObject updateIfSpeedValue(String key,String value,String realTimeValue,Long instanceId){
		
		return toSuccess(resourceApplyApi.updateIfSpeedValue(key,value,realTimeValue, instanceId));
	}
	
	/**
	 * 接口带宽恢复采集
	 * 
	 * @return
	 */
	@RequestMapping("/updateIfSpeedCollection")
	public JSONObject updateIfSpeedCollection(Long instanceId,String key){
		
		return toSuccess(resourceApplyApi.updateIfSpeedCollection(instanceId,key));
	}
	
	
	@RequestMapping("/getMetricInfoByResourceId")
	public JSONObject getMetricInfoByResourceId(long mainInstanceId,String resourceId,Long[] instanceIdArr){
		List<Long> instanceIdList = new ArrayList<Long>();
		for(Long instanceids:instanceIdArr){
			instanceIdList.add(instanceids);
		}
		
		List<Map<String,String>> list = resourceApplyApi.getMetricInfoByResourceId(mainInstanceId,resourceId,instanceIdList);
		
		ResourceApplyPageVo racPageVo = new ResourceApplyPageVo();
		racPageVo.setTotalRecord(list.size());
		racPageVo.setRows(list);
		racPageVo.setRowCount(list.size());
		
		return  toSuccess(racPageVo);
	}

	/**
	 * 标准应用页面弹出窗动态datagrid
	 * 
	 * @return
	 */
	@RequestMapping("/getStandardApplicationHeaderInfo")
	public JSONObject getStandardApplicationHeaderInfo(Long instanceId){
		
		ResourceMetricDef[] rmdArr = resourceApplyApi.getHeaderInfoByMetricType(instanceId, "Standard_Application_Dialog");
		List<Map<String,String>> rmdList = new ArrayList<Map<String,String>>();
		for(ResourceMetricDef rmd:rmdArr){
			Map<String,String> map = new HashMap<String,String>();
			map.put("metricId", rmd.getId());
			map.put("name", rmd.getName().toUpperCase());
			map.put("style", null==rmd.getMetricType()?"InformationMetric":rmd.getMetricType().name());
			map.put("unit", rmd.getUnit());
			map.put("isDisplay", rmd.isDisplay()?"true":"false");
			map.put("displayOrder", rmd.getDisplayOrder());
			rmdList.add(map);
		}
		
		return toSuccess(rmdList);
	}
	
	@RequestMapping("/getStandardApplicationMetricInfo")
	public JSONObject getStandardApplicationMetricInfo(Long instanceId){
		
		List<Map<String,String>> list = resourceApplyApi.getMetricInfo(instanceId,"Standard_Application_Dialog","",null);
		
		ResourceApplyPageVo racPageVo = new ResourceApplyPageVo();
		racPageVo.setTotalRecord(list.size());
		racPageVo.setRows(list);
		racPageVo.setRowCount(list.size());
		
		return  toSuccess(racPageVo);
	}
	
	@RequestMapping("/getStandardApplicationCurrentState")
	public JSONObject getStandardApplicationCurrentState(Long instanceId){
		
		String stateStr = resourceApplyApi.getStandardApplicationCurrentState(instanceId);
		
		return  toSuccess(stateStr);
	}
	
	/**
	 * 修改资源显示名称
	 * 
	 * @return
	 */
	@RequestMapping("/updateResourceShowName")
	public JSONObject updateResourceShowName(Long instanceId,String showName){
		return toSuccess(resourceApplyApi.updateResourceShowName(instanceId, showName));
	}
}
