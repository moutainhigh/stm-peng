package com.mainsteam.stm.portal.business.api;

public interface BizHealthHisApi {

	/**
	 * 获取指定业务的当前状态
	 * @param id
	 * @return
	 */
	public int getBizStatus(long id);
	
}
