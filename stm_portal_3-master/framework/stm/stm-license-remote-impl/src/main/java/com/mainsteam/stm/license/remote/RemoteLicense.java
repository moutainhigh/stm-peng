package com.mainsteam.stm.license.remote;


import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import com.mainsteam.stm.license.License;
import com.mainsteam.stm.license.LicenseCheckException;//zw
import com.mainsteam.stm.license.remote.RemoteLicenseMBean;

public class RemoteLicense implements RemoteLicenseMBean{

	private static final Log logger=LogFactory.getLog(RemoteLicense.class);
	
	@Override
	public int getDcsCount() {
		try {
			License lic = License.checkLicense();
			if(lic==null){
				logger.warn("License is null,get Dsc count faild.");
				return -1;
			}else{
				return lic.checkModelAvailableNum("stm-num-dcs");
			}
		} catch (LicenseCheckException e) {
			logger.warn(e.getMessage(),e);
			return -1;
		}
		
	}

	@Override
	public boolean isOverTime() {
		try {
			License lic = License.checkLicense();
			if(lic==null){
				logger.warn("License is null,get Dsc count faild.");
				return true;
			}else{
				return lic.isOverTime();
			}
		
		} catch (LicenseCheckException e) {
			logger.warn(e.getMessage(),e);
			return true;
		}
	}

}
