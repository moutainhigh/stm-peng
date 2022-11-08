package com.mainsteam.stm.home.workbench.host.web.action;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;

import com.alibaba.fastjson.JSONObject;
import com.mainsteam.stm.home.workbench.host.api.HostApi;
import com.mainsteam.stm.platform.web.action.BaseAction;

/**
 * <li>文件名称: com.mainsteam.stm.home.workbench.host.web.action.HostAction.java</li>
 * <li>文件描述: 本类描述</li>
 * <li>版权所有: 版权所有(C)2019-2020</li>
 * <li>公    司: 武汉美新翔盛科技有限公司</li>
 * <li>内容摘要: 无</li>
 * <li>其他说明:无</li>
 * <li>完成日期：@date</li>
 * <li>修改记录: 无</li>
 * @version 3.0
 * @author  ziwen
 * @date	2019年9月15日
 */
@Controller
@RequestMapping(value="/home/workbench/host")
public class HostAction extends BaseAction {
	
	@Autowired
	private HostApi hostApi;
	
	@RequestMapping(value="/getInitData")
	public JSONObject getInitData(Long id){
		return toSuccess(hostApi.getHostInfo(id));
	}
	
}
