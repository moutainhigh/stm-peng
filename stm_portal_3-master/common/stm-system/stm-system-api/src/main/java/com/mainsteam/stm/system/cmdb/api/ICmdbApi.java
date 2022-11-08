package com.mainsteam.stm.system.cmdb.api;


public interface ICmdbApi {
	
	/**
	 * 
	* @Title: getWebService
	* @Description: 查询webService配置
	* @return  CmdbWebServiceBo
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
