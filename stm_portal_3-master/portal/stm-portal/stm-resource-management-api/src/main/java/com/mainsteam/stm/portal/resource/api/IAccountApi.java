package com.mainsteam.stm.portal.resource.api;


import java.util.List;
import java.util.Set;

import com.mainsteam.stm.platform.web.vo.IDomain;
import com.mainsteam.stm.portal.resource.bo.AccountBo;


public interface IAccountApi {
	/**
	 * 新增用户
	 * 
	 * @param accountBo
	 * @return
	 */
	int insert(AccountBo accountBo);

	/**
	 * 删除用户
	 * 
	 * @param id
	 * @return
	 */
	int del(long id);

	/**
	 * 修改用户
	 * 
	 * @param accountBo
	 * @return
	 */
	int update(AccountBo accountBo);

	/**
	 * 获取单个用户
	 * 
	 * @param id
	 * @return
	 */
	AccountBo get(long id);

	/**
	 * 获取所有用户
	 * 
	 * @return
	 */
	List<AccountBo> getList(Set<IDomain> domains);
}
