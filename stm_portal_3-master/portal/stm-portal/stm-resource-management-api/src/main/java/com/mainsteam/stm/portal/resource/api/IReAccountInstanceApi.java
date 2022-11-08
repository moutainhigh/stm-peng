package com.mainsteam.stm.portal.resource.api;

import java.util.List;

import com.mainsteam.stm.portal.resource.bo.ReAccountInstanceBo;

public interface IReAccountInstanceApi {
	/**
	 * 插入预置账户关联资源实例
	 * 
	 * @param reAccountInstanceBo
	 * @return
	 */
	int insert(ReAccountInstanceBo reAccountInstanceBo);

	/**
	 * 获取预置账户关联资源实例
	 * 
	 * @return
	 */
	List<ReAccountInstanceBo> getList();

	/**
	 * 通过预置账户ID获取预置账户关联资源实例
	 * 
	 * @param accountId
	 * @return
	 */
	List<ReAccountInstanceBo> getByAccountId(long accountId);
	/**
	 * 通过资源实例id删除资源与预置账户关系
	 * @param instanceIds
	 * @return
	 */
	int deleteResourceAndAccountRelation(long[] instanceIds);
}
