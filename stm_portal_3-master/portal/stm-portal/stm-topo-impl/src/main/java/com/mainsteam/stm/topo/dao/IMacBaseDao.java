package com.mainsteam.stm.topo.dao;

import java.util.List;

import com.mainsteam.stm.platform.dao.IBaseDao;
import com.mainsteam.stm.platform.mybatis.plugin.pagination.Page;
import com.mainsteam.stm.platform.sequence.service.ISequence;
import com.mainsteam.stm.topo.bo.MacBaseBo;


public interface IMacBaseDao extends IBaseDao<MacBaseBo> {
	
	/**
	 * 新增或修改
	 * @param macBaseBo
	 */
	public int insertOrUpdate(MacBaseBo macBaseBo);

	/**
	 * 插入数据
	 * @param macBaseBo
	 * @return int 影响行数
	 */
	public int insert(MacBaseBo macBaseBo);
	
	/**
	 * 更新数据
	 * @param macBaseBo
	 * @return
	 */
	public int update(MacBaseBo macBaseBo);
	
	/**
	 * 查询所有基准mac数据
	 * @return List<MacBaseBo>
	 */
	public List<MacBaseBo> getAllMacBaseBos();
	
	/**
	 * 根据MAC地址列表删除[历史变更表]数据
	 * @return int 影响行数
	 */
	/**
	 * 根据MAC地址查询[基准表]数据
	 * @param mac
	 * @return MacBaseBo
	 */
	public MacBaseBo getMacBaseByMac(String mac);
	
	/**
	 * 清空[基准表]mac
	 * @return int 影响行数
	 */
	public int deleteAll();
	
	/**
	 * 根据ids删除[基准表]mac
	 * @param ids
	 * @return int 影响行数
	 */
	public int deleteByIds(Long[] ids);
	
	/**
	 * 根据macs查询基准列表
	 * @param macs
	 * @return
	 */
	public List<MacBaseBo> getMacBaseBosByMacs(List<String> macs);
	
	/**
	 * 根据ids查询mac列表
	 * @param ids
	 * @return
	 */
	public List<MacBaseBo> getMacBaseBosByIds(Long[] ids);
	
	/**
	 * 插入或更新数据
	 * @param macBaseBo
	 * @return int 影响行数
	 */
	public int addOrUpdate(MacBaseBo macBaseBo);
	
	/**
	 * 根据分页查询设备基准表列表数据
	 * @param page
	 * @return
	 */
	public void selectByPage(Page<MacBaseBo, MacBaseBo> page);
	
	/**
	 * 获取主键ID
	 * @return id
	 */
	public ISequence getSeq();
	
	/**
	 * 获取所有数据
	 * @return
	 */
	public List<MacBaseBo> getAll();
	
}
