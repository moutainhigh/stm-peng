package com.mainsteam.stm.topo.api;

import com.mainsteam.stm.topo.bo.SettingBo;

/**
 * 拓扑发现配置接口定义
 * @author zwx
 */
public interface ISettingApi {
	/**
	 * 查询拓扑发现使用的DCS（grounpId）
	 * @return
	 */
	public Integer getTopoFindGroupId();
	
	/**
	 * 保存拓扑发现配置信息
	 * @param SettingBo setting
	 * @return 被影响数据行数
	 */
	public int addSettingInfo(SettingBo settingBo);

	/**
	 * 获取设置信息
	 * @param key
	 * @return
	 */
	public String getCfg(String key);
}
