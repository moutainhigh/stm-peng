package com.mainsteam.stm.portal.threed.dao;

import java.util.List;

import com.mainsteam.stm.platform.mybatis.plugin.pagination.Page;
import com.mainsteam.stm.portal.threed.bo.CabinetBo;

/**
 * 
 * <li>文件名称: ICabinetDao.java</li>
 * <li>公　　司: 武汉美新翔盛科技有限公司</li>
 * <li>版权所有: 版权所有(C)2019-2020</li>
 * <li>修改记录: ...</li>
 * <li>内容摘要: 机柜中设备</li>
 * <li>其他说明: ...</li>
 * @version  ms.stm
 * @since    2019年11月26日
 * @author   liupeng
 */
public interface ICabinetDao {
	/**
	 * 向机柜中添加设备
	 * @param boList
	 * @return
	 */
	int batchAdd(List<CabinetBo> boList);
	/**
	 * 更新机柜中设备信息
	 * @param bo
	 * @return
	 */
	int update(CabinetBo bo);
	/**
	 * 将设备从机柜中移除
	 * @param ids
	 * @return
	 */
	int batchRemoveByArray(long[] ids);
	/**
	 * 
	 * @param boList
	 * @return
	 */
	int batchRemoveByList(List<CabinetBo> boList);
	/**
	 * 通过机柜查询机柜中的设备
	 * @param page
	 */
	List<CabinetBo> getPage(Page<CabinetBo, String> page);
	/**
	 * 查询所有已加入机柜中设备
	 * @param bo
	 * @return
	 */
	List<CabinetBo> queryAllDevice();
	/**
	 * 通过id获取机柜中设备信息
	 * @param id
	 * @return
	 */
	CabinetBo get(Long id);
}
