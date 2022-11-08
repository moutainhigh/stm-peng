package com.mainsteam.stm.portal.config.dao;

import java.util.List;

import com.mainsteam.stm.portal.config.bo.ConfigScriptDirectoryBo;

/**
 * 
 * <li>文件名称: IConfigScriptDirectoryDao.java</li>
 * <li>公　　司: 武汉美新翔盛科技有限公司</li>
 * <li>版权所有: 版权所有(C)2019-2020</li>
 * <li>修改记录: ...</li>
 * <li>内容摘要: ...</li>
 * <li>其他说明: ...</li>
 * @version  ms.stm
 * @since    2019年12月24日
 * @author   tongpl
 */

public interface IConfigScriptDirectoryDao {
	
	/**
	 * 查询全部
	 * @param bo
	 * @return
	 */
	public List<ConfigScriptDirectoryBo> getAll();
	
	/**
	 * 根据parentId查询
	 * @param bo
	 * @return
	 */
	public List<ConfigScriptDirectoryBo> getConfigScriptDirectoryByParentId(Long id);
	
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
	public int update(ConfigScriptDirectoryBo csd);
	
	/**
	 * 删除
	 * @param bo
	 * @return
	 */
	public int delete(Long id);
	
	/**
	 * 新增
	 * @param bo
	 * @return
	 */
	public int insert(ConfigScriptDirectoryBo csd);
	
	/**
	 * 查询
	 * @param bo
	 * @return
	 */
	public ConfigScriptDirectoryBo selectByName(String name);
	
}
