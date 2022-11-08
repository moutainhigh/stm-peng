package com.mainsteam.stm.topo.dao;

import java.util.List;

import com.mainsteam.stm.platform.sequence.service.ISequence;
import com.mainsteam.stm.topo.bo.SettingBo;
import com.mainsteam.stm.topo.po.SettingPo;

/**
 * 拓扑发现设置DAO接口
 * @author zwx
 */
public interface ISettingDao {
	/**
	 * 获取其他图元关联的子拓扑id
	 * @param subTopoId
	 * @return
	 */
	List<SettingBo> getOtherSetting(Long subTopoId);
	
	/**
	 * 获取主键ID
	 * @return id
	 */
	public ISequence getSeq();
	
	/**
	 * 获取所有数据
	 * @return
	 */
	public List<SettingBo> getAll();
	
	/**
	 * 清空表
	 */
	public void truncateAll();
	
	/**
	 * 新增拓扑发现配置信息
	 * @param settingPo
	 * @return
	 */
	int save(SettingBo bo);

	SettingBo getCfg(String key);

	int updateCfg(SettingBo bo);
	
	public int deleteCfg(String key);
}
