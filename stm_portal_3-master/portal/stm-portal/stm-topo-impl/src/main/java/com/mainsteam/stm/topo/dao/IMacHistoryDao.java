package com.mainsteam.stm.topo.dao;

import java.util.List;

import com.mainsteam.stm.platform.dao.IBaseDao;
import com.mainsteam.stm.platform.mybatis.plugin.pagination.Page;
import com.mainsteam.stm.platform.sequence.service.ISequence;
import com.mainsteam.stm.topo.bo.MacHistoryBo;


public interface IMacHistoryDao extends IBaseDao<MacHistoryBo> {
	
	/**
	 * 清空表
	 */
	public void truncateAll();
	
	/**
	 * 插入数据
	 * @param macHistoryBo
	 * @return int 影响行数
	 */
	public int insert(MacHistoryBo macHistoryBo);
	
	/**
	 * 根据ids列表删除[历史变更表]数据
	 * @return int 影响行数
	 */
	public int deleteHistoryIds(Long ids[]);
	
	/**
	 * 根据ids查询macHistory列表
	 * @param ids
	 * @return
	 */
	public List<MacHistoryBo> getMacHistoryBosByIds(Long[] ids);
	
	/**
	 * 根据MAC地址列表删除[历史变更表]数据
	 * @return int 影响行数
	 */
	public int deleteHistoryByMac(String macs[]);
	
	/**
	 * 根据条件分页查询[历史变更]数据(mac重复,时间排除最近时间一条)
	 * @param page
	 * @return
	 */
	public void selectSubHistoryByPage(Page<MacHistoryBo, MacHistoryBo> page);
	
	/**
	 * 根据分页查询设备[历史变更]列表数据
	 * @param page
	 * @return
	 */
	public void selectByPage(Page<MacHistoryBo, MacHistoryBo> page);
	
	/**
	 * 获取主键ID
	 * @return id
	 */
	public ISequence getSeq();
	
	/**
	 * 获取所有数据
	 * @return
	 */
	public List<MacHistoryBo> getAll();	
}
