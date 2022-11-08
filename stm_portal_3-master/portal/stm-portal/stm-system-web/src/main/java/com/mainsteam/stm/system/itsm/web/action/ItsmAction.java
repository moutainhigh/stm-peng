package com.mainsteam.stm.system.itsm.web.action;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;

import com.alibaba.fastjson.JSONObject;
import com.mainsteam.stm.platform.web.action.BaseAction;
import com.mainsteam.stm.system.itsm.api.IItsmApi;
import com.mainsteam.stm.system.itsm.bo.ItsmBo;

@Controller
@RequestMapping("/system/itsm/")
public class ItsmAction extends BaseAction{
	
	@Autowired
	private IItsmApi itsmApi;
	
	/**
	 * 
	* @Title: get
	* @Description: 获取配置
	* @return  JSONObject
	* @throws
	 */
	@RequestMapping("get")
	public JSONObject get() {
		return toSuccess(itsmApi.get());
	}
	
	/**
	 * 
	* @Title: save
	* @Description: 修改配置
	* @param itsmData
	* @return  JSONObject
	* @throws
	 */
	@RequestMapping("save")
	public JSONObject save(String itsmData,int port) {
		String userName = getLoginUser().getAccount();
		ItsmBo itsmBo = JSONObject.parseObject(itsmData, ItsmBo.class);
		itsmBo.setUserName(userName);
		itsmBo.setPort(port);
		return toSuccess(itsmApi.save(itsmBo));
	}
	
	/**
	 * 
	* @Title: getWebService
	* @Description: 得到webservice配置
	* @return  JSONObject
	* @throws
	 */
	@RequestMapping("getWebService")
	public JSONObject getWebService() {
		return toSuccess(itsmApi.getWebService());
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
//		ItsmWebServiceBo webServiceBo = JSONObject.parseObject(webServiceData, ItsmWebServiceBo.class);
		return toSuccess(itsmApi.saveWebService(webServiceData));
	}
}
