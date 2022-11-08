package com.mainsteam.stm.portal.business.dao;


import java.util.List;

import com.mainsteam.stm.portal.business.bo.BizDepBo;

public interface IBizDepDao {
	/**
	 * 新增业务单位(业务服务)
	 * @param bizDepBo
	 * @return
	 */
	int insert(BizDepBo bizDepBo);
	/**
	 * 删除业务单位(业务服务)
	 * @param id
	 * @return
	 */
	int del(long id);
	/**
	 * 查询所有的业务单位或者业务服务
	 * @param type 类型：0：业务单位；1：业务服务
	 * @return
	 */
	List<BizDepBo> getList(Integer type);
	/**
	 * 根据ids集合查询所有的业务单位或者业务服务
	 * @param ids
	 * @return
	 */
	List<BizDepBo> getListByIds(List<Long> ids);
	/**
	 * 根据业务单位或者业务服务id查询当前记录对象
	 * @param id
	 * @return
	 */
	BizDepBo get(long id);
	/**
	 * 更新业务单位或者业务服务
	 * @param bo
	 * @return
	 */
	int update(BizDepBo bo); 
	/**
	 * 查询名字是否重复
	 * @param name
	 * @param oldName更新时旧的名称
	 * @return
	 */
	int checkGroupNameIsExsit(String name,String oldName,int type);
}
