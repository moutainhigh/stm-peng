package com.mainsteam.stm.system.accesscontrol.web.action;

import javax.annotation.Resource;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.mainsteam.stm.platform.web.action.BaseAction;
import com.mainsteam.stm.system.accesscontrol.api.IAccessControlApi;
import com.mainsteam.stm.system.accesscontrol.bo.AccessControl;

/**
 * <li>文件名称: AccessControlAction.java</li>
 * <li>公　　司: 武汉美新翔盛科技有限公司</li>
 * <li>版权所有: 版权所有(C)2019-2020</li>
 * <li>修改记录: ...</li>
 * <li>内容摘要: ...</li>
 * <li>其他说明: ...</li>
 * @version  ms.stm
 * @since   2019年8月7日
 * @author   zjf
 */
@Controller
@RequestMapping("/system/accesscontrol")
public class AccessControlAction extends BaseAction {

	@Resource(name="systemAccessControlApi")
	private IAccessControlApi accessControlApi;
	
	@RequestMapping("getAccessControl")
	public JSONObject getAccessControl(){
		AccessControl accessControl = accessControlApi.getAccessControlIP();
		return toSuccess(JSON.toJSON(accessControl));
	}
	
	@RequestMapping("/updateAccessControl")
	public JSONObject updateAccessControl(AccessControl accessControl){
		return toSuccess(accessControlApi.updateAccessControlIp(accessControl));
	}
}
