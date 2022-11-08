package com.mainsteam.stm.topo.web.action;

import java.util.ArrayList;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.mainsteam.stm.platform.web.action.BaseAction;
import com.mainsteam.stm.topo.api.ITopoAuthSettingApi;
import com.mainsteam.stm.topo.bo.TopoAuthSettingBo;

/**
 * <li>拓扑权限设置管理模块</li>
 * <li>文件名称: TopoAuthSettingAction.java</li>
 * <li>公　　司: 武汉美新翔盛科技有限公司</li>
 * <li>版权所有: 版权所有(C)2019-2020</li>
 * @version  ms.stm
 * @since  2019年11月21日
 * @author zwx
 */
@Controller
@RequestMapping(value="/topo/auth")
public class TopoAuthSettingAction extends BaseAction{
	private final Logger logger = LoggerFactory.getLogger(MacAction.class);
	@Autowired
	private ITopoAuthSettingApi topoAuthSettingApi;
	
	@RequestMapping(value="/save",method=RequestMethod.POST)
	public JSONObject save(Long subtopoId,String authSetting){
		try {
			JSONArray authUsers = JSONArray.parseArray(authSetting);
			List<TopoAuthSettingBo> users = new ArrayList<TopoAuthSettingBo>();
			for(Object o:authUsers){
				users.add(JSONObject.parseObject(o.toString(), TopoAuthSettingBo.class));
			}
			
			topoAuthSettingApi.save(subtopoId, users);
			return super.toSuccess("保存成功");
		} catch (Exception e) {
			logger.error("保存失败!",e);
			return toJsonObject(700, "保存失败");
		}
	}

	/**
	 * 获取拓扑权限用户列表
	 * @param topoId 拓扑id
	 * @return JSONObject
	 */
	@RequestMapping(value="/setting",method=RequestMethod.POST)
	public JSONObject getTopoAuthSetting(Long subtopoId){
		List<TopoAuthSettingBo> users = topoAuthSettingApi.getTopoAuthSetting(subtopoId);
		return toSuccess(users);
	}
	/**
	 * 检查当前用户是否有对子拓扑相应的操作权限
	 * @param topoId 子拓扑id
	 * @param mode 操作模式 edit/select
	 * @return
	 */
	@RequestMapping(value="hasAuth")
	public String hasAuth(Long topoId,String[] mode){
		JSONObject retn = new JSONObject();
		retn.put("auth", topoAuthSettingApi.hasAuth(topoId,mode));
		return retn.toJSONString();
	}
}
