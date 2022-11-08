package com.mainsteam.stm.portal.netflow.api;

import com.mainsteam.stm.portal.netflow.bo.SystemParameterBo;

public interface ISystemParameterApi {

	SystemParameterBo getServiePort();

	int update(int port);
}
