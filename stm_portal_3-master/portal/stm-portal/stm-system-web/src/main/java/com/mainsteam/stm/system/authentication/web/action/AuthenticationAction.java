package com.mainsteam.stm.system.authentication.web.action;


import javax.annotation.Resource;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;

import com.alibaba.fastjson.JSONObject;
import com.mainsteam.stm.platform.web.action.BaseAction;
import com.mainsteam.stm.system.authentication.api.IAuthenticationApi;
import com.mainsteam.stm.system.authentication.bo.Authentication;
@Controller
@RequestMapping("/system/authentication")
public class AuthenticationAction extends BaseAction{
	@Resource(name="stm_system_AuthenticationApi")
	private IAuthenticationApi authenticationApi;
	@RequestMapping("/insert")
	public JSONObject insert (Authentication authentication) {
		 authenticationApi.insert(authentication);
		 return toSuccess(1);
	}
	@RequestMapping("/get")
	public JSONObject get(){
		Authentication authentication= authenticationApi.getAuthentication();
		return toSuccess(authentication);
	}

}
