package com.mainsteam.stm.license.calc.impl;

import org.apache.log4j.Logger;
import org.springframework.stereotype.Service;

import com.mainsteam.stm.caplib.dict.CapacityConst;
import com.mainsteam.stm.license.License;
import com.mainsteam.stm.license.LicenseCheckException;//zw
import com.mainsteam.stm.license.LicenseModelEnum;
import com.mainsteam.stm.license.calc.api.ILicenseCapacityCategory;

@Service("licenseCapacityCategory")
public class LicenseCapacityCategoryImpl implements ILicenseCapacityCategory {
	
	private Logger logger = Logger.getLogger(LicenseCapacityCategoryImpl.class);

	@Override
	public boolean isAllowCategory(String categoryId) {
		
		//判断是否拥有存储模块
		try {
			if(CapacityConst.STORAGE.equals(categoryId) && 
					License.checkLicense().checkModelAvailableNum(LicenseModelEnum.stmModelStor) == 0){
				return false;
			}
		} catch (LicenseCheckException e) {
			logger.error(e.getMessage(),e);
		}
		
		//判断是否拥有虚拟化模块
		try {
			if(CapacityConst.VM.equals(categoryId) && 
					License.checkLicense().checkModelAvailableNum(LicenseModelEnum.stmModelVm) == 0){
				return false;
			}
		} catch (LicenseCheckException e) {
			logger.error(e.getMessage(),e);
		}
		
		return true;
	}

}
