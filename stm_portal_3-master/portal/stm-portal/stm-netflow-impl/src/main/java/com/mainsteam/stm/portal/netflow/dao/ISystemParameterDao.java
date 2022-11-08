package com.mainsteam.stm.portal.netflow.dao;

import com.mainsteam.stm.portal.netflow.bo.SystemParameterBo;

public interface ISystemParameterDao {

	SystemParameterBo getServiePort();
	
	int update(int port);
}
