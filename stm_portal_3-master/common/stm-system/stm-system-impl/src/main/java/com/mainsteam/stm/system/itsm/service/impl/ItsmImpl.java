package com.mainsteam.stm.system.itsm.service.impl;

import java.io.UnsupportedEncodingException;
import java.net.URL;
import java.net.URLDecoder;

import javax.annotation.Resource;

import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.alibaba.fastjson.JSONObject;
import com.mainsteam.stm.platform.system.config.bean.SystemConfigBo;
import com.mainsteam.stm.platform.system.config.bean.SystemConfigConstantEnum;
import com.mainsteam.stm.platform.system.config.service.ISystemConfigApi;
import com.mainsteam.stm.platform.web.vo.ILoginUser;
import com.mainsteam.stm.system.itsm.api.IItsmApi;
import com.mainsteam.stm.system.itsm.bo.ItsmBo;
import com.mainsteam.stm.system.um.right.api.IRightApi;
import com.mainsteam.stm.system.um.right.bo.Right;
import com.mainsteam.stm.system.um.right.constants.RightConstants;

@Service
public class ItsmImpl implements IItsmApi {

	@Autowired
	private ISystemConfigApi configApi;

	@Resource(name = "stm_system_right_impl")
	IRightApi rightApi;

	/**
	 * 得到ITSM的ID
	 */
	private static final long configId = SystemConfigConstantEnum.SYSTEM_CONFIG_ITSM
			.getCfgId();

	/**
	 * ITSM webservice 告警接口配置ID
	 */
	private static final long webServiceCfgId = SystemConfigConstantEnum.SYSTEM_CONFIG_ITSM_ALARM_WEBSERVICE
			.getCfgId();

	private static final Logger LOGGER = Logger.getLogger(ItsmImpl.class);

	@Override
	public ItsmBo get() {
		SystemConfigBo configBo = configApi.getSystemConfigById(configId);
		ItsmBo itsmBo = JSONObject.parseObject(configBo.getContent(),
				ItsmBo.class);
		return itsmBo;
	}

	@Override
	public int save(ItsmBo itsmBo) {
		//得到数据库中的页签URL
		Right defaultRight = rightApi.get(ILoginUser.RIGHT_ITSM);

		SystemConfigBo bo = new SystemConfigBo();
		bo.setId(configId);
		bo.setContent(JSONObject.toJSONString(itsmBo));
		try {
			// 拼装最新的URL
			URL url = new URL(defaultRight.getUrl());
			String fileStr = url.getFile();

			String[] strArr = fileStr.split("&");
			String[] serviceArr = null;
			String oldService = "";
			String newServiceUrl = "";
			// 找到service=的字符串
			for (String string : strArr) {
				if (string.contains("service=")) {
					serviceArr = string.split("=");
					break;
				}
			}
			if (serviceArr != null) {
				// 记录原始service的url,并构建最新的serviceurl
				oldService = serviceArr[1];
				URL serviceUrl = new URL(URLDecoder.decode(oldService, "UTF-8"));
				newServiceUrl = new URL(serviceUrl.getProtocol(),
						itsmBo.getIp(), itsmBo.getPort(), serviceUrl.getFile())
						.toString();
			}
			String newFileStr = fileStr.replace(oldService, newServiceUrl);
			URL newUrl = new URL(url.getProtocol(), itsmBo.getIp(),
					itsmBo.getPort(), newFileStr);

			Right rightBo = new Right();
			rightBo.setId(ILoginUser.RIGHT_ITSM);
			rightBo.setName("STSM");
			rightBo.setFileId(RightConstants.DEFAULT_IMAGE_FILE_ID);
			rightBo.setUrl(newUrl.toString());
			rightBo.setSort(rightApi.getAll().size() + 1);
			rightBo.setPid(0L);
			if (itsmBo.isOpen()) {
				rightBo.setStatus(1);
			} else {
				rightBo.setStatus(0);
			}
			rightApi.save(rightBo);

			rightApi.updateStatus(rightBo);
		} catch (Exception e) {
			LOGGER.error("转换STSM页签URL失败", e);
		}
		return configApi.updateSystemConfig(bo);
	}

	@Override
	public String getWebService() {
		SystemConfigBo configBo = configApi
				.getSystemConfigById(webServiceCfgId);
		// ItsmWebServiceBo webServiceBo =
		// JSONObject.parseObject(configBo.getContent(),ItsmWebServiceBo.class);
		return configBo.getContent();
	}

	@Override
	public int saveWebService(String webServiceData) {
		SystemConfigBo configBo = new SystemConfigBo();
		configBo.setId(webServiceCfgId);
		configBo.setContent(webServiceData);
		return configApi.updateSystemConfig(configBo);
	}
	public static void main(String[] args) {
		try {
			String test1 = URLDecoder.decode("http%3A%2F%2F192.168.1.122%3A9080%2Fstsm%2F", "UTF-8");
			System.out.println(test1);
		} catch (UnsupportedEncodingException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
}
