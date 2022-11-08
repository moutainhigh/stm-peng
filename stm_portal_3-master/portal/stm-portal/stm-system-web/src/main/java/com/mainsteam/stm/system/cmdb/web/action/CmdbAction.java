package com.mainsteam.stm.system.cmdb.web.action;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;

import com.alibaba.fastjson.JSONObject;
import com.mainsteam.stm.platform.web.action.BaseAction;
import com.mainsteam.stm.system.cmdb.api.ICmdbApi;

@Controller
@RequestMapping("/system/cmdb/")
public class CmdbAction extends BaseAction{
	
	@Autowired
	private ICmdbApi cmdbApi;
	
	/**
	 * 
	* @Title: getWebService
	* @Description: 得到webservice配置
	* @return  JSONObject
	* @throws
	 */
	@RequestMapping("getWebService")
	public JSONObject getWebService() {
		return toSuccess(cmdbApi.getWebService());
	}
	
	/**
	 * 
	* @Title: saveWebService
	* @Description: 保存webservice配置
	* @return  JSONObject
	* @throws
	 */
	@RequestMapping("saveWebService")
	public JSONObject saveWebService(String webServiceData) {
		return toSuccess(cmdbApi.saveWebService(webServiceData));
	}
}
