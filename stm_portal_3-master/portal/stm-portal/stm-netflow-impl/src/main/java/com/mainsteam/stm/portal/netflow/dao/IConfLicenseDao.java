package com.mainsteam.stm.portal.netflow.dao;

import com.mainsteam.stm.portal.netflow.bo.ConfLicense;

public interface IConfLicenseDao {

	ConfLicense getLicense(long id);

	int updateLicense(ConfLicense confLicense);
}
