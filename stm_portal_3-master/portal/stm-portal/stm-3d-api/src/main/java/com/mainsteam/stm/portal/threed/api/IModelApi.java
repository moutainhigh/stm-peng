package com.mainsteam.stm.portal.threed.api;

import java.util.List;

import com.mainsteam.stm.portal.threed.bo.ModelBo;

public interface IModelApi {
	/**
	 * 得到所有的型号关联数据
	 * @return
	 */
	public List<ModelBo> getAll();
	/**
	 * 通过设备型号修改关联型号
	 * @param bo
	 * @return
	 */
	public int update(ModelBo bo);
	/**
	 * 从3D webservice获取产品型号 
	 * @return
	 * @throws Exception
	 */
	public String getProductInfo() throws Exception;
}
