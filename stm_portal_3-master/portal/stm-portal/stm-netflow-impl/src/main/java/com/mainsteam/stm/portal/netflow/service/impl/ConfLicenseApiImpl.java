package com.mainsteam.stm.portal.netflow.service.impl;

import com.mainsteam.stm.portal.netflow.api.IConfLicenseApi;
import com.mainsteam.stm.portal.netflow.bo.ConfLicense;
import com.mainsteam.stm.portal.netflow.dao.IConfLicenseDao;

public class ConfLicenseApiImpl implements IConfLicenseApi {

	private IConfLicenseDao confLicenseDao;

	public void setConfLicenseDao(IConfLicenseDao confLicenseDao) {
		this.confLicenseDao = confLicenseDao;
	}

	@Override
	public ConfLicense getLicense(long id) {
		return this.confLicenseDao.getLicense(id);
	}

	@Override
	public int updateLicense(ConfLicense confLicense) {
		return this.confLicenseDao.updateLicense(confLicense);
	}

}
