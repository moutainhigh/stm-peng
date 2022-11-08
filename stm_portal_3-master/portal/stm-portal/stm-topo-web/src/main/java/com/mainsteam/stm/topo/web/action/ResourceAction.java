package com.mainsteam.stm.topo.web.action;

import java.util.List;

import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.mainsteam.stm.instancelib.ResourceInstanceService;
import com.mainsteam.stm.instancelib.exception.InstancelibException;
import com.mainsteam.stm.instancelib.obj.ResourceInstance;
import com.mainsteam.stm.platform.web.action.BaseAction;
import com.mainsteam.stm.topo.api.ResourceService;

@Controller(value="topoResourceAction")
@RequestMapping("topo/resource")
public class ResourceAction extends BaseAction {
	private Logger logger = Logger.getLogger(ResourceAction.class);
	@Autowired
	private ResourceInstanceService resourceInstanceService;
	@Autowired
	private ResourceService resourceService;
	/**
	 * 获取所有资源实例的简要信息
	 * @return
	 */
	@ResponseBody
	@RequestMapping(value="all",method=RequestMethod.POST)
	public JSONArray getAll(){
		JSONArray items = new JSONArray();
		try {
			List<ResourceInstance> instances = resourceInstanceService.getAllParentInstance();
			logger.error("总共资源实例数量"+instances.size());
//			int id = 0;
			for(ResourceInstance re : instances){
				JSONObject item = new JSONObject();
				item.put("instanceId", re.getId());
				item.put("ip", re.getShowIP());
				item.put("showName", re.getShowName());
				item.put("typeName", re.getParentCategoryId());
				item.put("id", re.getId());
				items.add(item);
				/*for (int i = 0;i<100;i++){
					item.put("id", id++);
					items.add(item);
				}*/
			}
		} catch (InstancelibException e) {
			logger.error(e);
		}
		return items;
	}
	@ResponseBody
	@RequestMapping(value="alarm",method=RequestMethod.POST)
	public JSONObject alarmInfo(Long instanceId){
		JSONObject info = new JSONObject();
		String msg = resourceService.getAlarmInfo(instanceId);
		info.put("alarmInfo", msg);
		return info;
	}
	@ResponseBody
	@RequestMapping(value="nodeTooltip",method=RequestMethod.POST)
	public JSONObject nodeTooltip(Long instanceId){
		JSONObject info = resourceService.nodeTooltipInfo(instanceId);
		return info;
	}
	@ResponseBody
	@RequestMapping(value="homeMapTooltip")
	public JSONObject homeMapTooltip(Long instanceId){
		//为首页提供节点tooltip信息
		JSONObject info = resourceService.nodeTooltipInfo(instanceId);
		String msg = resourceService.getAlarmInfo(instanceId);
		info.put("alarmInfo", msg);
		return info;
	}
}
