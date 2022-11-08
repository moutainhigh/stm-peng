package com.mainsteam.stm.topo.dao;

import java.util.List;

import com.mainsteam.stm.platform.dao.IBaseDao;
import com.mainsteam.stm.platform.mybatis.plugin.pagination.Page;
import com.mainsteam.stm.platform.sequence.service.ISequence;
import com.mainsteam.stm.topo.bo.MacLatestBo;


public interface IMacLatestDao extends IBaseDao<MacLatestBo> {
	/**
	 * 根据mac更新设备名称
	 * @param mac
	 * @param hostName
	 * @return
	 */
	public int updateByMac(String mac,String hostName);
	
	/**
	 * 清空[新增MAC表]数据
	 * @return int 影响行数
	 */
	public int deleteAll();
	
	/**
	 * 根据ids列表删除[新增MAC表]数据
	 * @return int 影响行数
	 */
	public int deleteMacLatestByIds(List<Long> ids);
	
	/**
	 * 添加新增MAC表
	 * @param macLatestBo
	 * @return 影响行数
	 */
	public int insert(MacLatestBo macLatestBo) ;
	
	/**
	 * 查询所有macLatest列表
	 * @return
	 */
	public List<MacLatestBo> getAllMacLatestBos();
	
	/**
	 * 根据ids查询macLatest列表
	 * @param ids
	 * @return
	 */
	public List<MacLatestBo> getMacLatestBosByIds(Long[] ids);
	
	/**
	 * 根据分页查询设备新增MAC列表数据
	 * @param page
	 * @return
	 */
	public void selectByPage(Page<MacLatestBo, MacLatestBo> page);
	
	/**
	 * 获取主键ID
	 * @return id
	 */
	public ISequence getSeq();
	
	/**
	 * 获取所有数据
	 * @return
	 */
	public List<MacLatestBo> getAll();	
	
}
