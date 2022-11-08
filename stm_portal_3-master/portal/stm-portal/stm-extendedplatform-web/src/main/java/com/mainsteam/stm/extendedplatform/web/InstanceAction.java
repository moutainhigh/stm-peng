package com.mainsteam.stm.extendedplatform.web;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import com.alibaba.fastjson.JSONObject;
import com.mainsteam.stm.instancelib.obj.ResourceInstance;
import com.mainsteam.stm.portal.extendedplatform.resourceprofile.api.InstanceService;
import com.mainsteam.stm.profilelib.obj.ProfileInstanceRelation;

@Controller
@RequestMapping("/extendedplatform/instance")
public class InstanceAction {

	@Autowired
	@Qualifier("extendInstanceService")
	private InstanceService instanceService;
	
	@RequestMapping("search")
	public String searchInstance(long id){
		List<ResourceInstance> instaceList = instanceService.queryInstanceById(id);
		return instaceList==null?"":JSONObject.toJSONString(instaceList);
	}
	
	@RequestMapping("instanceProfileRelatione")
	public String queryInstanceProfileRelatione(long instanceId){
		Map<String, Object> result = new HashMap<String, Object>();
		result.put("resourceInstance", instanceService.queryProfileInstancePos(instanceId));
		result.put("resourceInstanceLast", instanceService.queryProfileInstanceLastPos(instanceId));
		return JSONObject.toJSONString(result);
	}
	
	@RequestMapping("deleteRelatione")
	public String deleteRelatione(long instanceId){
		return instanceService.deleteProfileInstanceRel(instanceId)>0?"true":"false";
	}
	
	@RequestMapping("deleteLastRelatione")
	public String deleteLastRelatione(long instanceId){
		return instanceService.deleteProfileInstanceLastRel(instanceId)>0?"true":"false";
	}
	
	/**
	* @Title: deleteInstance
	* @Description: TODO(这里用一句话描述这个方法的作用)
	* @param instanceId
	* @return  String
	* @throws
	*/
	@RequestMapping("deleteInstance")
	public String deleteInstance(long instanceId){
		return instanceService.deleteInstance(instanceId)>0?"true":"false";
	}
	
}
