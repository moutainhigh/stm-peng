package com.mainsteam.stm.portal.config.dao;

import java.util.List;

import com.mainsteam.stm.portal.config.bo.ConfigScriptBo;
import com.mainsteam.stm.portal.config.bo.ConfigScriptDirectoryBo;

/**
 * 
 * <li>文件名称: IConfigScriptDao.java</li>
 * <li>公　　司: 武汉美新翔盛科技有限公司</li>
 * <li>版权所有: 版权所有(C)2019-2020</li>
 * <li>修改记录: ...</li>
 * <li>内容摘要: ...</li>
 * <li>其他说明: ...</li>
 * @version  ms.stm
 * @since    2019年12月24日
 * @author   tongpl
 */

public interface IConfigScriptDao {
	
	/**
	 * 查询全部
	 * @param bo
	 * @return
	 */
	public List<ConfigScriptBo> getAll();
	
	/**
	 * 查询
	 * @param bo
	 * @return
	 */
	public List<ConfigScriptBo> queryByBo(ConfigScriptBo csb);
	
	public List<ConfigScriptBo> queryEqualsByBo(ConfigScriptBo csb);
	
	/**
	 * 根据ID查询
	 * @param bo
	 * @return
	 */
	public ConfigScriptBo getConfigScriptById(Long id);
	
	/**
	 * 根据directoryId查询
	 * @param bo
	 * @return
	 */
	public List<ConfigScriptBo> getConfigScriptByDirectoryId(Long directoryId);
	
	/**
	 * 更新
	 * @param bo
	 * @return
	 */
	public int update(ConfigScriptBo csb);
	
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
	public int insert(ConfigScriptBo csb);
}
