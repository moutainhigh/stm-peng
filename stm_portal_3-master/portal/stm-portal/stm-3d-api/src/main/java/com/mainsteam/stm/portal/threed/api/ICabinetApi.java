package com.mainsteam.stm.portal.threed.api;

import java.util.List;

import com.mainsteam.stm.platform.mybatis.plugin.pagination.Page;
import com.mainsteam.stm.portal.threed.bo.BaseResult;
import com.mainsteam.stm.portal.threed.bo.CabinetBo;

public interface ICabinetApi {
	/**
	 * 批量添加设备到机柜中
	 * @param boList 添加的设备数据
	 * @return
	 */
	public int batchAdd(List<CabinetBo> boList) throws Exception;
	/**
	 * 更新设备的在机柜中的信息
	 * @param bo
	 * @return
	 */
	public int update(CabinetBo bo) throws Exception;
	/**
	 * 从机柜中批量移除设备
	 * @param ids
	 * @return
	 */
	public int batchRemove(long[] ids) throws Exception;
	/**
	 * 获取机柜结构
	 * @return
	 * @throws Exception
	 */
	public String getNodeTree() throws Exception;
	/**
	 * 通过条件查询机柜中的设备
	 * @param page
	 */
	public void getPage(Page<CabinetBo, String> page);
	/**
	 * 推送监控数据
	 * @return
	 * @throws Exception
	 */
	public BaseResult pushMoniter() throws Exception;
	/**
	 * 获取所有机柜中设备
	 */
	public List<CabinetBo> getAllCabinetResource() throws Exception;
}
