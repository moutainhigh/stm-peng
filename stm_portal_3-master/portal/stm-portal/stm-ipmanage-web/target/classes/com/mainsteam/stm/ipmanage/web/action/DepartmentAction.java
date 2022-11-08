package com.mainsteam.stm.ipmanage.web.action;

import javax.annotation.Resource;

import org.apache.log4j.Logger;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import com.alibaba.fastjson.JSONObject;
import com.mainsteam.stm.ipmanage.api.DepartService;
import com.mainsteam.stm.ipmanage.bo.Depart;
import com.mainsteam.stm.platform.web.action.BaseAction;

@Controller
@RequestMapping({ "/portal/ipmanage/department" })
public class DepartmentAction extends BaseAction{
	private Logger logger = Logger.getLogger(DepartmentAction.class);
	@Resource
	private DepartService departService;
	
	@RequestMapping("/departments")
	@ResponseBody
	public JSONObject getDepartments(){
		return toSuccess(departService.getDepartList());
	}
	@RequestMapping("/insertDepartment")
	@ResponseBody
	public JSONObject insertDepartment(Depart depart){
		return toSuccess(departService.insert(depart));
	}
	@RequestMapping("/delDepartment")
	@ResponseBody
	public JSONObject delDepartment(Integer id){
		return toSuccess(departService.del(id));
	}
}
