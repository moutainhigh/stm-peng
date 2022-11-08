package com.mainsteam.stm.system.itsmuser.dao;

import java.util.List;

import com.mainsteam.stm.platform.mybatis.plugin.pagination.Page;
import com.mainsteam.stm.system.itsmuser.bo.ItsmSystemBo;

public interface IItsmSystemDao {

	/**
	 * 保存第三方系统信息
	 * @param itsmSystemBo
	 * @return
	 */
	int saveItsmSystem(ItsmSystemBo itsmSystemBo);
	
	/**
	 * 分页查询第三方系统信息
	 * @param page
	 * @return
	 */
	List<ItsmSystemBo> queryItsmSystem(Page<ItsmSystemBo, ItsmSystemBo> page);
	
	/**
	 * 查询所有的信息
	 * @return
	 */
	List<ItsmSystemBo> queryAllItsmSystem();
	
	/**
	 * 批量删除
	 * @param ids
	 * @return
	 */
	int batchDel(Long[] ids);
	
	/**
	 * 根据ID得到基本信息
	 * @param id
	 * @return
	 */
	ItsmSystemBo getItsmSystemById(Long id);
	
	/**
	 * 更新基本信息
	 * @param itsmSystemBo
	 * @return
	 */
	int updateItsmSystem(ItsmSystemBo itsmSystemBo);
	
	/**
	 * 更新是开启还是关闭
	 * @return
	 */
	int updateSystemStartState(Long[] ids, int startState);
	
	/**
	 * 更新同步状态
	 * @param ids
	 * @param syncState
	 * @return
	 */
	int updateSyncState(ItsmSystemBo itsmSystemBo);
	
	/**
	 * 判断wsdlURL是否存在
	 * @param itsmSystemBo
	 * @return
	 */
	boolean isWsdlURLExist(ItsmSystemBo itsmSystemBo);
}
