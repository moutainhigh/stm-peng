package com.mainsteam.stm.portal.netflow.service.impl;

import com.mainsteam.stm.license.LicenseModelEnum;
import com.mainsteam.stm.portal.netflow.dao.IDeviceDao;
import com.mainsteam.stm.system.license.api.ILicenseRemainCountApi;

public class NetflowLicenseRemainCountApi extends ILicenseRemainCountApi {

	private IDeviceDao deviceDao;

	public void setDeviceDao(IDeviceDao deviceDao) {
		this.deviceDao = deviceDao;
	}

	@Override
	public String getType() {
		return LicenseModelEnum.oc4MonitorFlow.toString();
	}

	@Override
	public int getUsed() {
		return this.deviceDao.queryInterfaceCount();
	}

}
