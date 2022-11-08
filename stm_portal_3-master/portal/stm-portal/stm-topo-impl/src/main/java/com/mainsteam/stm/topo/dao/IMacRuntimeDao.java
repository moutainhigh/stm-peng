package com.mainsteam.stm.topo.dao;

import java.util.List;

import com.mainsteam.stm.platform.dao.IBaseDao;
import com.mainsteam.stm.platform.mybatis.plugin.pagination.Page;
import com.mainsteam.stm.platform.sequence.service.ISequence;
import com.mainsteam.stm.topo.bo.MacRuntimeBo;


/**
 * <li>实时表dao接口定义</li>
 * <li>文件名称: IMacRuntimeDao.java</li>
 * <li>公　　司: 武汉美新翔盛科技有限公司</li>
 * <li>版权所有: 版权所有(C)2019-2020</li>
 * <li>修改记录: ...</li>
 * <li>内容摘要: ...</li>
 * <li>其他说明: ...</li>
 * @version  ms.stm
 * @since  2019年9月10日
 * @author zwx
 */
public interface IMacRuntimeDao extends IBaseDao<MacRuntimeBo> {
	/**
	 * 根据主机ip查询ip-mac-port信息
	 * @return List<MacRuntimeBo>
	 */
	public List<MacRuntimeBo> getMacByHostIp(String hostIp);
	
	/**
	 * 根据主机mac查询ip-mac-port信息
	 * @return List<MacRuntimeBo>
	 */
	public List<MacRuntimeBo> getMacByHostMac(String hostMac);
	
	/**
	 * 根据mac更新设备名称
	 * @param mac
	 * @param hostName
	 * @return
	 */
	public int updateByMac(String mac,String hostName);
	
	/**
	 * 根据ip和接口名称查询设备ip-mac-port信息
	 * @return List<MacRuntimeBo>
	 */
	public List<MacRuntimeBo> getMacInfos(String ip,String intface);
	
	/**
	 * 查询所有实时表数据
	 * @return List<MacRuntimeBo>
	 */
	public List<MacRuntimeBo> getAllMacRuntimeBos();
	
	/**
	 * 清空实时表数据
	 * @return int 影响行数
	 */
	public int deleteAll();
	
	/**
	 * 插入数据
	 * @param macRuntimeBo
	 * @return int 影响行数
	 */
	public int insert(MacRuntimeBo macRuntimeBo);
	
	/**
	 * 根据ids查询macRuntime列表
	 * @param ids
	 * @return
	 */
	public List<MacRuntimeBo> getMacRuntimeBosByIds(Long[] ids);
	
	/**
	 * 根据分页查询设备实时表列表数据
	 * @param page
	 * @return
	 */
	public void selectByPage(Page<MacRuntimeBo, MacRuntimeBo> page);
	
	/**
	 * 获取主键ID
	 * @return id
	 */
	public ISequence getSeq();
	
	/**
	 * 获取所有数据
	 * @return
	 */
	public List<MacRuntimeBo> getAll();
}
