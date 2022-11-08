package com.mainsteam.stm.portal.business.state.api;

import com.mainsteam.stm.common.instance.obj.InstanceStateEnum;

public interface IBizSerStateApi {
	/**
	 * 根据业务应用id查询业务应用状态
	 * @param id
	 * @return
	 */
	public InstanceStateEnum getStateById(Long id);
}
