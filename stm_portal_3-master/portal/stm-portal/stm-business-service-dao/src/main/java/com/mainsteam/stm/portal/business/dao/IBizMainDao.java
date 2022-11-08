package com.mainsteam.stm.portal.business.dao;

import java.util.Date;
import java.util.List;

import com.mainsteam.stm.portal.business.bo.BizMainBo;

public interface IBizMainDao {

	/**
	 * 添加一个业务系统(基本信息)
	 * @param bo
	 * @return
	 */
	public int insertBasicInfo(BizMainBo bo);
	
	/**
	 * 获取一个业务系统的基本信息
	 * @param id
	 * @return
	 */
	public BizMainBo getBasicInfo(long id);
	
	/**
	 * 获取一个业务系统的状态定义
	 * @param id
	 * @return
	 */
	public String getCanvasStatusDefine(long id);
	
	/**
	 * 获取所有的业务集合
	 * @return
	 */
	public List<BizMainBo> getAllList();
	
	/**
	 * 获取所有创建者及责任人信息
	 * @return
	 */
	public List<BizMainBo> getAllPermissionsInfoList();
	
	/**
	 * 获取所有的业务集合(状态定义)
	 * @return
	 */
	public List<BizMainBo> getAllStatusDefineList();
	
	/**
	 * 获取所有的业务集合
	 * @return
	 */
	public List<BizMainBo> getBizListForSearch(String name);
	
	/**
	 * 获取业务集合
	 * @param bo
	 * @date 20161205
	 * @author dfw
	 * @return
	 */
	public List<BizMainBo> getBizList(BizMainBo bo);
	
	/**
	 * 修改业务的状态定义
	 * @param bo
	 * @return
	 */
	public int updateBizStatusDefine(BizMainBo bo);
	
	/**
	 * 查询名字是否重复
	 * @param name
	 * @param oldName更新时旧的名称
	 * @return
	 */
	public int checkBizNameIsExsit(String name,String oldName);
	
	/**
	 * 修改业务的基本信息
	 * @param bo
	 * @return
	 */
	public int updateBasicInfo(BizMainBo bo);

	/**
	 * 删除业务系统
	 * @param id
	 * @return
	 */
	public int deleteBizById(long id);
	
	/**
	 * 查询创建时间
	 * @param id
	 * @return
	 */
	public Date getCreateTime(long id);
	
	/**
	 * 判断用户是否为业务系统责任人
	 * @param manangerId
	 * @return
	 */
	public int getBizCountByManagerId(long manangerId);
	
}
