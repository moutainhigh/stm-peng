package com.mainsteam.stm.system.authentication.service.impl;


import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.util.StringUtils;
import com.alibaba.fastjson.JSONObject;
import com.mainsteam.stm.platform.system.config.bean.SystemConfigBo;
import com.mainsteam.stm.platform.system.config.bean.SystemConfigConstantEnum;
import com.mainsteam.stm.platform.system.config.service.ISystemConfigApi;
import com.mainsteam.stm.system.authentication.api.IAuthenticationApi;
import com.mainsteam.stm.system.authentication.bo.Authentication;

public class AuthenticationImpl implements IAuthenticationApi {
	
	@Autowired
	@Qualifier("systemConfigApi")
	private ISystemConfigApi systemConfigApi;
	
	private static final long AUTHENTICATION_CONFIG_ID = SystemConfigConstantEnum.SYSTEM_CONFIG_AUTHENTICATION_CFG.getCfgId();
	
	@Override
	public void insert(Authentication authentication) {
		SystemConfigBo config = systemConfigApi.getSystemConfigById(AUTHENTICATION_CONFIG_ID);
		String authConfigStr = authentication==null?"":JSONObject.toJSONString(authentication);
		if(config!=null){
			config.setContent(authConfigStr);
			systemConfigApi.updateSystemConfig(config);
		}else{
			config = new SystemConfigBo(AUTHENTICATION_CONFIG_ID,authConfigStr,"系统管理，认证方式配置文件");
			systemConfigApi.insertSystemConfig(config);
		}
	}

	@Override
	public Authentication getAuthentication() {
		SystemConfigBo config = systemConfigApi.getSystemConfigById(AUTHENTICATION_CONFIG_ID);
		if(config!=null){
			if(!StringUtils.isEmpty(config.getContent())){
				return JSONObject.parseObject(config.getContent(),Authentication.class);
			}
		}
		return new Authentication();
	}
}
