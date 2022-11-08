package com.mainsteam.stm.portal.business.dao;


import java.util.List;

import com.mainsteam.stm.platform.mybatis.plugin.pagination.Page;
import com.mainsteam.stm.portal.business.bo.BizServiceBo;

public interface IBizServiceDao {
	/**
	 * 新增业务应用
	 * @param bizServicePo
	 * @return
	 */
	int insert(BizServiceBo bizServicePo);
    /**
     * 删除业务应用
     * @param id
     * @return
     */
	int del(long id);
	/**
	 * 更新应用
	 * @param bizServicePo
	 * @return
	 */
	int update(BizServiceBo bizServicePo);
	/**
	 * 根据id获取业务应用
	 * @param id
	 * @return
	 */
	BizServiceBo get(long id);
	/**
	 * 查询所有的业务应用集合
	 * @return
	 * @throws Exception
	 */
	List<BizServiceBo> getList() throws Exception;
	
	/**
	 * 按名字模糊查询
	 * 
	 * @param List<BizServiceBo>
	 * @return
	 */
	public List<BizServiceBo> getByName(String name);
	
	/**
	 * 根据业务应用ids查询所有的业务应用集合
	 * @param ids
	 * @return
	 */
	List<BizServiceBo> getListByIds(List<Long> ids);
	/**
	 * 查询名字是否重复
	 * @param name
	 * @param oldName更新时旧的名称
	 * @return
	 */
	int checkGroupNameIsExsit(String name,String oldName);
	/**
	 * 统一告警未恢复，已恢复包含的业务告警关联的业务应用ids集合接口方法
	 * @return
	 */
	public List<String> getSourceIds();
	/**
	 * 根据业务应用id获取业务最新状态
	 * @param id
	 * @return
	 */
	public String getStateById(Long id);
	/**
	 * 根据id查业务信息
	 * @param id
	 * @return
	 */
	public BizServiceBo getBizBuessinessById(long id);
	void getListPage(Page<BizServiceBo, Object> page) throws Exception;
}
