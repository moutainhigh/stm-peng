package com.mainsteam.stm.portal.netflow.api;

import com.mainsteam.stm.portal.netflow.bo.ConfLicense;

public interface IConfLicenseApi {

	ConfLicense getLicense(long id);

	int updateLicense(ConfLicense confLicense);
}
