package com.mainsteam.stm.topo.web.action;

import com.mainsteam.stm.topo.bo.LinkBo;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.mainsteam.stm.platform.web.action.BaseAction;
import com.mainsteam.stm.topo.api.ISettingApi;
import com.mainsteam.stm.topo.api.ITopoAuthSettingApi;
import com.mainsteam.stm.topo.bo.SettingBo;
import com.mainsteam.stm.topo.dao.ILinkDao;

import java.util.List;

/**
 * 负责拓扑管理中所有的设置项
 * <li>文件名称: SettingAction.java</li>
 * <li>公　　司: 武汉美新翔盛科技有限公司</li>
 * <li>版权所有: 版权所有(C)2019-2020</li>
 * <li>修改记录: ...</li>
 * <li>内容摘要: 访问根地址/topo/setting</li>
 * <li>其他说明: ...</li>
 * @version  ms.stm
 * @since   2019年8月7日
 * @author  xfq
 */
@Controller
@RequestMapping(value="/topo/setting")
public class SettingAction extends BaseAction{
	
	@SuppressWarnings("unused")
	private final Logger logger = LoggerFactory.getLogger(SettingAction.class);
	@Autowired
	private ISettingApi settingApi;
	//权限控制
	@Autowired
	private ITopoAuthSettingApi authSvc;
	@Autowired
	private ILinkDao ldao;

	/**
	 * 保存配置信息
	 */
	@RequestMapping(value="/save",method=RequestMethod.POST)
	public JSONObject saveSetting(SettingBo settingBo){
		if(authSvc.hasAuth(null,null)){
			if(null != settingBo){
				settingApi.addSettingInfo(settingBo);
				//获取链路转换的规则
				if ("globalSetting".equals(settingBo.getKey())){
					String strLinkType = getLinkType(settingBo);
					if (strLinkType.equals("0")){//两端都关联接口才视为链路
						//原来的只关联了一端接口的链路转化为连线
						transformLinkToLineByLinkType();
					}
//					else if(strLinkType.equals("1")){//至少一端关联接口
//						//原来关联了一个接口的连线自动转换为链路并加入监控
//						transformLineToLinkByLinkType();
//					}
				}
			}

			return toSuccess("保存成功");
		}else{
			return toSuccess("只有系统管理员，域管理员有保存配置的权限");
		}
	}
	
	/**
	 * 获取配置信息
	 */
	@ResponseBody
	@RequestMapping(value="/get/{key}",method={RequestMethod.GET,RequestMethod.POST})
	public Object getCfgInfo(@PathVariable(value="key") String key){
		String retn = settingApi.getCfg(key);
		return JSON.parse(retn);
	}

	/**
	 * 获取链路配置规则信息
	 */
	public String getLinkType(SettingBo settingBo){
		JSONObject jsStrLink = JSONObject.parseObject(settingBo.getValue());
		String strLink = jsStrLink.getString("link");
		JSONObject jsStrLinkType = JSONObject.parseObject(strLink);
		return jsStrLinkType.getString("defType");
	}

	/**
	 * 原来的只关联了一端接口的链路转化为连线
	 */
	void transformLinkToLineByLinkType(){
		//查询数据库中已存的单接口的链路（有instanceId的）
		List<LinkBo> links = ldao.getSingleInterfaceLinkInstances();
		Long id = null;
		for(LinkBo linkBo:links){
			id=linkBo.getId();
			//链路转化为连线:TYPE置为line，INSTANCE_ID置为NULL
			ldao.transformLinkToLineById(id);
		}
	}

	/**
	 * 原来关联了一个接口的连线自动转换为链路并加入监控
	 */
	void transformLineToLinkByLinkType(){
		//查询数据库中已存的单接口的连线(instance_id为空)
		List<LinkBo> lines = ldao.getSingleInterfaceLine();
		Long id = null;
		for(LinkBo linkBo:lines){
			id=linkBo.getId();
			//连线转为链路
			//ldao.transformLinkToLineById(id);
		}
	}
}
