package com.mainsteam.stm.system.itsm.api;

import com.mainsteam.stm.system.itsm.bo.ItsmBo;

public interface IItsmApi {
	
	/**
	 * 
	* @Title: get
	* @Description: 获取配置
	* @return  ItsmBo
	* @throws
	 */
	ItsmBo get();
	
	/**
	 * 
	* @Title: save
	* @Description: 保存配置
	* @param itsmBo
	* @return  int
	* @throws
	 */
	int save(ItsmBo itsmBo);
	
	/**
	 * 
	* @Title: getWebService
	* @Description: 查询webService配置
	* @return  ItsmWebServiceBo
	* @throws
	 */
	String getWebService();
	
	/**
	 * 
	* @Title: saveWebService
	* @Description: 保存webService配置信息
	* @param webServiceBo
	* @return  int
	* @throws
	 */
	int saveWebService(String webServiceData);
}
