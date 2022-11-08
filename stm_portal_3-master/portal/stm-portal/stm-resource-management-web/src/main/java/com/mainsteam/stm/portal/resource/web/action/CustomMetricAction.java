package com.mainsteam.stm.portal.resource.web.action;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.annotation.Resource;

import org.apache.log4j.Logger;
import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;

import com.alibaba.fastjson.JSONObject;
import com.mainsteam.stm.alarm.event.AlarmEventTemplateService;
import com.mainsteam.stm.alarm.obj.AlarmEventTemplate;
import com.mainsteam.stm.alarm.obj.SysModuleEnum;
import com.mainsteam.stm.caplib.dict.FrequentEnum;
import com.mainsteam.stm.caplib.dict.MetricTypeEnum;
import com.mainsteam.stm.caplib.dict.PerfMetricStateEnum;
import com.mainsteam.stm.caplib.dict.PluginIdEnum;
import com.mainsteam.stm.common.instance.obj.InstanceStateEnum;
import com.mainsteam.stm.metric.CustomMetricService;
import com.mainsteam.stm.metric.exception.CustomMetricException;
import com.mainsteam.stm.metric.obj.CollectMeticSetting;
import com.mainsteam.stm.metric.obj.CustomMetric;
import com.mainsteam.stm.metric.obj.CustomMetricThreshold;
import com.mainsteam.stm.metric.objenum.CustomMetricDataProcessWayEnum;
import com.mainsteam.stm.platform.web.action.BaseAction;
import com.mainsteam.stm.platform.web.vo.ILoginUser;
import com.mainsteam.stm.portal.resource.api.ICustomMetricApi;
import com.mainsteam.stm.portal.resource.bo.CustomMetricBo;
import com.mainsteam.stm.portal.resource.bo.CustomMetricResourceBo;
import com.mainsteam.stm.portal.resource.bo.PortalThreshold;
import com.mainsteam.stm.portal.resource.web.vo.CustomMetricPageVo;
import com.mainsteam.stm.portal.resource.web.vo.CustomMetricVo;

/**
 * 
 * <li>文件名称: CustomMetricAction.java</li> <li>公　　司: 武汉美新翔盛科技有限公司</li> <li>版权所有:
 * 版权所有(C)2019-2020</li> <li>修改记录: ...</li> <li>内容摘要: ...</li> <li>其他说明: ...</li>
 * 
 * @version ms.stm
 * @since 2019年11月28日
 * @author Wang
 * @tags
 */
@Controller
@RequestMapping("/portal/resource/customMetric")
public class CustomMetricAction extends BaseAction {

	private Logger logger = Logger.getLogger(CustomResGroupAction.class);

	@Resource
	private ICustomMetricApi customMetricApi;
	
	@Resource
	private AlarmEventTemplateService alarmEventTemplateService;
	
	// 判断新增指标名称不能重复
	Map<String, String> metricNameMap = new HashMap<String, String>();
	
	@RequestMapping("/getEnumArrayByName")
	public JSONObject getEnumByName(String name){
		if("CustomMetricDataProcessWayEnum".equals(name)){
			CustomMetricDataProcessWayEnum[] dataProcessWayEnums=CustomMetricDataProcessWayEnum.values();
			return toSuccess(dataProcessWayEnums);
		}else if("FrequentEnum".equals(name)){
			FrequentEnum[] frequentEnum=FrequentEnum.values();
			return toSuccess(frequentEnum);
		}else{
			return null;
		}
	} 
	
	@RequestMapping("/getDefaultAlarmContent")
	public JSONObject getDefaultAlarmContent(String type){
		
		List<PortalThreshold> portalThresholds = new ArrayList<PortalThreshold>();
		
		AlarmEventTemplate content = alarmEventTemplateService.getDefaultTemplate(false, SysModuleEnum.MONITOR, MetricTypeEnum.valueOf(type));
		
		PortalThreshold result = new PortalThreshold();
		result.setPerfMetricStateEnum(PerfMetricStateEnum.Major);
		result.setAlarmContent(content.getContent().get(InstanceStateEnum.SERIOUS));
		portalThresholds.add(result);
		
		result = new PortalThreshold();
		result.setPerfMetricStateEnum(PerfMetricStateEnum.Minor);
		result.setAlarmContent(content.getContent().get(InstanceStateEnum.WARN));
		portalThresholds.add(result);
		
		result = new PortalThreshold();
		result.setPerfMetricStateEnum(PerfMetricStateEnum.Normal);
		result.setAlarmContent(content.getContent().get(InstanceStateEnum.NORMAL));
		portalThresholds.add(result);
		
		return toSuccess(portalThresholds);
		
	}
	
	/**
	 * get CustomMetrics By InstanceId
	 * @param instanceId
	 * @return
	 */
	@RequestMapping("/getCustomMetricsByInstanceId")
	public JSONObject getCustomMetricsByInstanceId(long instanceId){
		List<CustomMetricBo> customMetricBos=customMetricApi.getCustomMetricsByInstanceId(instanceId);
		
		List<CustomMetricVo> customMetricVos=new ArrayList<CustomMetricVo>();
		
		for(CustomMetricBo customMetricBo:customMetricBos){
			CustomMetricVo customMetricVo=new CustomMetricVo();
			BeanUtils.copyProperties(customMetricBo, customMetricVo);
			customMetricVos.add(customMetricVo);
		}
		
		CustomMetricPageVo page=new CustomMetricPageVo();
		page.setCustomMetricData(customMetricVos);
		page.setStartRow(0);
		page.setTotalRecord(customMetricVos.size());
		
		return toSuccess(page);
		
	}
	
	
	
	/**
	 * 获取自定义指标列表
	 * 
	 * @return
	 */
	@RequestMapping("/getCustomMetrics")
	public JSONObject getCustomMetrics(CustomMetricPageVo page) {
		if(page==null){
			logger.error("Can't found the CustomMetricPageVo from AJAX");
			return null;
		}
		CustomMetricBo cb = new CustomMetricBo();
		if(page.getCondition()!=null){
			
			if(page.getCondition().getName()!=null&&!"".equals(page.getCondition().getName())){
				cb.setName(page.getCondition().getName());
			}
			if(page.getCondition().getDiscoverWay()!=null&&!"".equals(page.getCondition().getDiscoverWay())){
				cb.setDiscoverWay(page.getCondition().getDiscoverWay());
			}
			if(page.getCondition().getMetricType()!=null&&!"".equals(page.getCondition().getMetricType())){
				cb.setMetricType(page.getCondition().getMetricType());
			}
		}
		
			
		List<CustomMetricBo> customMetricBos=customMetricApi.getCustomMetrics(page.getStartRow(), page.getRowCount(),cb);	
		int count = customMetricApi.getCustomMetricCount();
		List<CustomMetricVo> customMetricVos=new ArrayList<CustomMetricVo>();
		
		for(CustomMetricBo customMetricBo:customMetricBos){
			if(customMetricBo == null){
				continue;
			}
			CustomMetricVo customMetricVo=new CustomMetricVo();
			BeanUtils.copyProperties(customMetricBo, customMetricVo);
			customMetricVos.add(customMetricVo);
			
			metricNameMap.put(customMetricBo.getId(), customMetricBo.getName());
		}
		page.setCustomMetricData(customMetricVos);
		page.setTotalRecord(count);
		page.setPlaceHolder(CustomMetricThreshold.PLACEHOLDER);
//		page.setCustomMetricData(getTestData());
		return toSuccess(page);
	}
	
	/**
	 * 获取自定义指标列表
	 * 
	 * @return
	 */
	@RequestMapping("/getCustomMetricsByMetricId")
	public JSONObject getCustomMetricsByMetricId(String metricId) {
		
		return toSuccess(customMetricApi.getCustomMetric(metricId));
		
	}
	
	/**
	 * 添加 自定义指标列表
	 * 
	 * @param customMetric
	 * @return
	 */
	@RequestMapping("/addCustomMetric")
	public JSONObject addgetCustomMetric(String customMetricString) {
		
		CustomMetricVo customMetricVo = JSONObject.parseObject(customMetricString,CustomMetricVo.class);
		String name=customMetricVo.getName();
		//指标名称重复
		if(metricNameMap.containsValue(name)){
			return toSuccess("DuplicateName");
		}
		
		String metricId=customMetricApi.createCustomMetric(customMetricVo2Bo(customMetricVo));
		
		
		return toSuccess(metricId);
	}

	/**
	 * 更新 自定义指标列表
	 * 
	 * @param customMetric
	 * @return
	 */
	@RequestMapping("/updateCustomMetric")
	public JSONObject updateCustomMetric(String customMetricString) {
		
		CustomMetricVo customMetricVo = JSONObject.parseObject(customMetricString,CustomMetricVo.class);
		
		metricNameMap.put(customMetricVo.getId(), customMetricVo.getName());
		
		customMetricApi.updateCustomMetric(customMetricVo2Bo(customMetricVo));
		
		return toSuccess("sucessful");
	}
	
	@RequestMapping("/deleteCustomMetric")
	public JSONObject deleteCustomMetric(String[] metricIds) {

		List<String> metricIdList = Arrays.asList(metricIds);
		for(String metricId : metricIdList){
			if(metricNameMap.containsKey(metricId)){
				metricNameMap.remove(metricId);
			}
		}
		customMetricApi.deleteCustomMetrics(metricIdList);
		
		return toSuccess("sucessful");
	}
	
	
	
	/**
	 * 更新指标设置
	 * @param customMetricVo
	 * @return
	 */
	@RequestMapping("/updateCustomMetricSetting")
	public JSONObject updateCustomMetricSetting(CustomMetricVo customMetricVo) {
		
		
		String metricId=customMetricVo.getId();
		boolean monitor=customMetricVo.isMonitor();
		boolean alert=customMetricVo.isAlert();
		
		CollectMeticSetting collectMeticSetting=new CollectMeticSetting();
		collectMeticSetting.setMetricId(metricId);
		collectMeticSetting.setMonitor(monitor);
		collectMeticSetting.setAlert(alert);
		collectMeticSetting.setFlapping(1);
		collectMeticSetting.setFreq(FrequentEnum.valueOf(customMetricVo.getFrequent()));
		
		customMetricApi.updateCustomMeticSetting(collectMeticSetting);
		
		return toSuccess("sucessful");
	}
	
	/**
	 * 获取未绑定资源
	 * @param domainId
	 * @param categoryId
	 * @return
	 */
	@RequestMapping("/getUnBindResources")
	public JSONObject getUnBindResources(String metricId,Long domainId,String categoryId,String rightDatas,String pluginId){
		
		List<CustomMetricResourceBo> bindResources = JSONObject.parseArray(rightDatas, CustomMetricResourceBo.class);
		
		ILoginUser user =getLoginUser();

		List<CustomMetricResourceBo> unBindResources= customMetricApi.getResourceInstances(user, categoryId, domainId,PluginIdEnum.valueOf(pluginId));
		
		
		for(CustomMetricResourceBo bindResource:bindResources){
			if(unBindResources.contains(bindResource)){
				unBindResources.remove(bindResource);
			}
		}
		
		
		//去重复资源,前端
		if(bindResources!=null){
			unBindResources.removeAll(bindResources);
		}
		//去重复资源，后端
		bindResources=customMetricApi.getCustomMetricBinds(metricId);
		if(bindResources!=null){
			unBindResources.removeAll(bindResources);
		}
		
		return toSuccess(unBindResources);
	}
	
	/**
	 * 获取已经绑定的资源
	 * @param metricId
	 * @return
	 */
	@RequestMapping("/getBindResources")
	public JSONObject getBindResources(String metricId){
		
		List<CustomMetricResourceBo> bindResources=customMetricApi.getCustomMetricBinds(metricId);
		
		return toSuccess(bindResources);
	}
	
	@RequestMapping("/bindResources")
	public JSONObject bindResources(String metricId,String pluginId,String rightDatas){
		
		List<CustomMetricResourceBo> bindResources = JSONObject.parseArray(rightDatas, CustomMetricResourceBo.class);
		
		customMetricApi.updateResuorceBind(metricId,pluginId, bindResources);

		return toSuccess("bindResources");
	}
	
	
	/**===========================================
	 * 对象转换
	 * @param customMetricVo
	 */
	public CustomMetricBo customMetricVo2Bo(CustomMetricVo customMetricVo){
		CustomMetricBo bo=new CustomMetricBo();
		BeanUtils.copyProperties(customMetricVo, bo);
		return bo;
	}
	
	public List<CustomMetricVo>  getTestData(){
		List<CustomMetricVo> vos=new ArrayList<CustomMetricVo>();
		
		CustomMetricVo vo=new CustomMetricVo();
		vo.setId("111");
		vo.setAlert(true);
		vo.setMonitor(true);
		
		vo.setName("Test");
		vo.setMetricType(MetricTypeEnum.PerformanceMetric.name());
		vo.setUnit("%");
		vo.setFrequent("min10");
		
//		vo.setGreenOperator("<");
//		vo.setGreenValue("30");
//		vo.setYellowOperator(">");
//		vo.setYellowValue("60");
//		
//		vo.setRedOperator(">");
//		vo.setRedValue("90");
		
		vo.setDiscoverWay("SnmpPlugin");
		
		vo.setOid("1.1.1.1.1.1");
		vo.setDataProcessWay(CustomMetricDataProcessWayEnum.AVG.name());
		vo.setCommand("XXXXXXXXXXXXXX");
		vos.add(vo);
		
		return vos;
	}
	
}
