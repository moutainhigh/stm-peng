package com.mainsteam.stm.home.workbench.netdevice.web.action;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;

import com.alibaba.fastjson.JSONObject;
import com.mainsteam.stm.home.layout.bo.HomeDefaultInterfaceBo;
import com.mainsteam.stm.home.workbench.netdevice.api.NetDeviceApi;
import com.mainsteam.stm.platform.web.action.BaseAction;
import com.mainsteam.stm.platform.web.vo.ILoginUser;

/**
 * <li>文件名称: com.mainsteam.stm.home.workbench.netdevice.web.action.NetDeviceAction.java</li>
 * <li>文件描述: 网络设备信息查询</li>
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
@RequestMapping(value="/home/workbench/netdevice")
public class NetDeviceAction extends BaseAction {
	@Autowired
	private NetDeviceApi deviceApi;
	
	@RequestMapping(value="/getInitData")
	public JSONObject getInitData(Long id, String ext1){
		return toSuccess(deviceApi.getNetDeviceInfo(id, ext1));
	}
	
	@RequestMapping(value="/getNewInitData")
	public JSONObject getNewInitData(Long id, String ext1){
		ILoginUser user = getLoginUser();
		return toSuccess(deviceApi.getNewNetDeviceInfo(id, ext1,user.getId()));
	}
	
	@RequestMapping(value="/setDefaultInterface")
	public JSONObject setDefaultInterface(long id, long resourceId, long defaultInterfaceId){
		HomeDefaultInterfaceBo hb = new HomeDefaultInterfaceBo();
		hb.setId(id);
		hb.setDefaultInterfaceId(defaultInterfaceId);
		hb.setUserId(getLoginUser().getId());
		hb.setResourceId(resourceId);
		return toSuccess(deviceApi.setDefaultInterface(hb));
	}
	
	@RequestMapping(value="/getInterfaceIndicators")
	public JSONObject getInterfaceIndicators(Long id){
		return toSuccess(deviceApi.getInterfaceIndicators(id));
	}
	
}
