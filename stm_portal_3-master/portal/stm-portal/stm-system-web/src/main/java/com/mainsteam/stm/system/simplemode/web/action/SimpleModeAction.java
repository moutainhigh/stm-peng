/**
 * 
 */
package com.mainsteam.stm.system.simplemode.web.action;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;

import com.alibaba.fastjson.JSONObject;
import com.mainsteam.stm.platform.system.config.bean.SystemConfigConstantEnum;
import com.mainsteam.stm.platform.system.config.service.ISystemConfigApi;
import com.mainsteam.stm.platform.web.action.BaseAction;

/**
 * <li>文件名称: SimpleModeAction.java</li>
 * <li>公　　司: 武汉美新翔盛科技有限公司</li>
 * <li>版权所有: 版权所有(C)2019-2020</li>
 * <li>修改记录: ...</li>
 * <li>内容摘要: ...</li>
 * <li>其他说明: ...</li>
 * @version  ms.stm
 * @since    2016年11月11日
 * @author   tandl
 */
@Controller
@RequestMapping("/system/simplemode/")
public class SimpleModeAction extends BaseAction{
	@Autowired
	private ISystemConfigApi configApi;
	private static long cfgId=SystemConfigConstantEnum.SYSTEM_CONFIG_SIMPLE_MODE.getCfgId(); 
	
	/**
	 * <pre>
	 * 获取极简模式相关的配置信息
	 * </pre>
	 * @return
	 */
	@RequestMapping("get")
	public JSONObject getSkin(){
		try{
			return toSuccess(configApi.getSystemConfigById(cfgId).getContent());
		}catch (Exception e) {
			String content ="{\"open\":true}";
			return toSuccess(content);
		}
	}
	
	
}
