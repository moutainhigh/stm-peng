package com.mainsteam.stm.topo.service.impl;

import org.springframework.beans.factory.annotation.Autowired;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.mainsteam.stm.topo.api.ISettingApi;
import com.mainsteam.stm.topo.bo.NodeBo;
import com.mainsteam.stm.topo.bo.SettingBo;
import com.mainsteam.stm.topo.dao.INodeDao;
import com.mainsteam.stm.topo.dao.ISettingDao;

/**
 * 拓扑发现设置DAO实现
 * @author zwx
 */
public class SettingImpl implements ISettingApi{
	private ISettingDao settingDao;
	@Autowired
	private INodeDao nodeDao;
	
	@Override
	public String getCfg(String key) {
		SettingBo sp = settingDao.getCfg(key);
		if(null!=sp){
			return sp.getValue();
		}else{
			return "{}";
		}
	}
	
	/**
	 * 查询拓扑发现使用的DCS（grounpId）
	 * @return
	 */
	public Integer getTopoFindGroupId(){
		Integer groupId = null;
		SettingBo setting = settingDao.getCfg("topoSetting");
		if(null != setting){
			JSONObject val = JSON.parseObject(setting.getValue());
			if(null != val.getInteger("groupId")){
				groupId = val.getInteger("groupId");
			}
		}
		return groupId;
	}
	
	/**
	 * 保存拓扑发现配置信息
	 */
	@Override
	public synchronized int addSettingInfo(SettingBo newSetting) {
		int rows = 0;
		String key = newSetting.getKey();
		SettingBo oldSetting = settingDao.getCfg(key);
		
		if(null!=oldSetting){
			if("globalSetting".equals(key)){	//全局设置拓扑 图元尺寸
				JSONObject oldVal = JSONObject.parseObject(oldSetting.getValue());
				JSONObject newVal = JSONObject.parseObject(newSetting.getValue());
				double oldPelSize = oldVal.getJSONObject("topo").getDoubleValue("pelSize");
				double newPelSize = newVal.getJSONObject("topo").getDoubleValue("pelSize");
				if(newPelSize != oldPelSize){
					NodeBo node = new NodeBo();
					node.setIconWidth(node.getIconWidth()*newPelSize);
					node.setIconHeight(node.getIconHeight()*newPelSize);
					nodeDao.updatePelSize(node);
				}
			}
			rows = settingDao.updateCfg(newSetting);
		}else{
			rows = settingDao.save(newSetting);
		}
		return rows;
	}

	public ISettingDao getSettingDao() {
		return settingDao;
	}

	public void setSettingDao(ISettingDao settingDao) {
		this.settingDao = settingDao;
	}
	
}
