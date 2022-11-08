package com.mainsteam.stm.system.cmdb.service.impl;

import javax.annotation.Resource;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.mainsteam.stm.platform.system.config.bean.SystemConfigBo;
import com.mainsteam.stm.platform.system.config.bean.SystemConfigConstantEnum;
import com.mainsteam.stm.platform.system.config.service.ISystemConfigApi;
import com.mainsteam.stm.system.cmdb.api.ICmdbApi;
import com.mainsteam.stm.system.um.right.api.IRightApi;

@Service
public class CmdbImpl implements ICmdbApi {

	@Autowired
	private ISystemConfigApi configApi;

	@Resource(name = "stm_system_right_impl")
	IRightApi rightApi;

	/**
	 * cmdb webservice 同步接口配置ID
	 */
	private static final long webServiceCfgId = SystemConfigConstantEnum.SYSTEM_CONFIG_CMDB_WEBSERVICE
			.getCfgId();
	
	/**
	 * cmdb默认配置
	 */
	private static final String DEFAULTCONTENT = "{\"URL\":\"http://172.16.8.59:9080/cms/schemas/moservice?wsdl\",\"open\":false}";
	
	/**
	 * cmdb默认说明
	 */
	private static final String DEFAULTDES = "CMDB Webservice";
	
	@Override
	public String getWebService() {
		SystemConfigBo configBo = configApi
				.getSystemConfigById(webServiceCfgId);
		if (configBo == null) {//如果不存在，则插入默认值
			SystemConfigBo config = new SystemConfigBo(webServiceCfgId,DEFAULTCONTENT,DEFAULTDES);
			configApi.insertSystemConfig(config);
			return DEFAULTCONTENT;
		} else {
			return configBo.getContent();
		}
	}

	@Override
	public int saveWebService(String webServiceData) {
		SystemConfigBo configBo = new SystemConfigBo();
		configBo.setId(webServiceCfgId);
		configBo.setContent(webServiceData);
		return configApi.updateSystemConfig(configBo);
	}
}
