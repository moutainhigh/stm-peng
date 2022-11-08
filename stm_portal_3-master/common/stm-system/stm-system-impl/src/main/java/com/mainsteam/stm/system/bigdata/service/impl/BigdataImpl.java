package com.mainsteam.stm.system.bigdata.service.impl;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.alibaba.fastjson.JSONObject;
import com.mainsteam.stm.platform.system.config.bean.SystemConfigBo;
import com.mainsteam.stm.platform.system.config.bean.SystemConfigConstantEnum;
import com.mainsteam.stm.platform.system.config.service.ISystemConfigApi;
import com.mainsteam.stm.system.bigdata.api.IBigdataApi;
import com.mainsteam.stm.system.bigdata.bo.BigdataBo;

/**
 * <li>文件名称: BigdataImpl.java</li>
 * <li>公　　司: 武汉美新翔盛科技有限公司</li>
 * <li>版权所有: 版权所有(C)2019-2020</li>
 * <li>修改记录: ...</li>
 * <li>内容摘要: ...</li>
 * <li>其他说明: ...</li>
 * @version  ms.stm
 * @since    2019年12月26日
 * @author   ziwenwen
 */
@Service
public class BigdataImpl implements IBigdataApi {

	@Autowired
	private ISystemConfigApi configApi;
	
	private static final long configId=SystemConfigConstantEnum.SYSTEM_CONFIG_BIGDATA.getCfgId();
	
	@Override
	public BigdataBo get() {
		SystemConfigBo bo=configApi.getSystemConfigById(configId);
		BigdataBo bigdataBo=JSONObject.parseObject(bo.getContent(),BigdataBo.class);
		return bigdataBo;
	}

	@Override
	public int save(BigdataBo bigdata) {
		SystemConfigBo bo=new SystemConfigBo();
		bo.setId(configId);
		bo.setContent(JSONObject.toJSONString(bigdata));
		return configApi.updateSystemConfig(bo);
	}

}


