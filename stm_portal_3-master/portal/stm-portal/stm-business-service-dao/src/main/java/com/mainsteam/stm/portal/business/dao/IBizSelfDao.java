package com.mainsteam.stm.portal.business.dao;

import java.util.List;

import com.mainsteam.stm.portal.business.bo.BizSelfBo;

public interface IBizSelfDao {
	/**
	 * 添加自定义元素
	 */
	int insert(BizSelfBo bizSelfBo);
	/**
	 * 获取用户添加自定义元素
	 * @return
	 */
	List<BizSelfBo> getList();
	/**
	 * 根据ID删除自定义元素图片
	 * @param id
	 */
	void deleteById(long id);
	/**
	 * 根据ids查询返回的结果集合
	 * @param ids
	 * @return
	 */
	List<BizSelfBo> getListByIds(List<Long> ids);
}
