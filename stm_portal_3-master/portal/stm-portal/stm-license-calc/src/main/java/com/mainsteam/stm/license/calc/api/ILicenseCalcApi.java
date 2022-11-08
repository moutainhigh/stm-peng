package com.mainsteam.stm.license.calc.api;

import javax.annotation.Resource;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.stereotype.Service;

import com.mainsteam.stm.caplib.CapacityService;
import com.mainsteam.stm.instancelib.ResourceInstanceService;
import com.mainsteam.stm.license.License;
import com.mainsteam.stm.license.LicenseCheckException;//zw
import com.mainsteam.stm.system.license.api.ILicenseRemainCountApi;

@Service
public abstract class ILicenseCalcApi extends ILicenseRemainCountApi {
	private  final Log logger = LogFactory.getLog(ILicenseCalcApi.class);
	
	@Resource
	protected ResourceInstanceService riService;
	
	public abstract String getDesc();
	//malachi lic
	public int getAuthorCount(){
		int authorCount = 0;
		try {
			authorCount = License.checkLicense().checkModelAvailableNum(getType());
		} catch (LicenseCheckException e) {
			authorCount = 0;
			logger.error("ILicenseCalcApi getAuthorCount:", e);
		}
		return authorCount;
	}
}
