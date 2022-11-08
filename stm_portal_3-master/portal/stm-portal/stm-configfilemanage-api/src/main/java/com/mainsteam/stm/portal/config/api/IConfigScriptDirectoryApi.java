package com.mainsteam.stm.portal.config.api;

import java.util.List;

import com.mainsteam.stm.portal.config.bo.ConfigScriptDirectoryBo;

public interface IConfigScriptDirectoryApi {
	/**
	 * 查询全部
	 * @param bo
	 * @return
	 */
	public List<ConfigScriptDirectoryBo> getAllConfigScriptDirectory();
	
	/**
	 * 根据ID查询
	 * @param bo
	 * @return
	 */
	public ConfigScriptDirectoryBo getConfigScriptDirectoryById(Long id);
	
	/**
	 * 更新
	 * @param bo
	 * @return
	 */
	public boolean updateConfigScriptDirectory(ConfigScriptDirectoryBo csd);
	
	/**
	 * 删除
	 * @param bo
	 * @return
	 */
	public boolean deleteConfigScriptDirectoryById(Long id);
	
	/**
	 * 删除目录及目录下所有内容
	 * @param bo
	 * @return
	 */
	public boolean deleteConfigScriptDirectoryALL(Long directoryId);
	
	/**
	 * 只删除目录,目录下内容转移到母目录
	 * @param bo
	 * @return
	 */
	public boolean deleteConfigScriptDirectoryONLY(Long directoryId);
		
	
	/**
	 * 更新
	 * @param bo
	 * @return
	 */
	public boolean addORupdate(ConfigScriptDirectoryBo csd);
	
	/**
	 * 根据名字查询
	 * @param bo
	 * @return
	 */
	public ConfigScriptDirectoryBo selectByName(String name);
}
