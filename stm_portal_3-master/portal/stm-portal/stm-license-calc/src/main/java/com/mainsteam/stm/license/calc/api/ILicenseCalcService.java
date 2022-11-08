package com.mainsteam.stm.license.calc.api;

import java.util.List;

import com.mainsteam.stm.license.LicenseModelEnum;
import com.mainsteam.stm.system.license.bo.LicenseUseInfoBo;

public interface ILicenseCalcService {
	/**
	 * 通过license枚举判断license是否足够
	 * @param lmEnum
	 * @return
	 */
	public boolean isLicenseEnough(LicenseModelEnum lmEnum);
	
	/**
	 * 通过资源categoryId判断该资源对应的license是否足够
	 * @param categoryId
	 * @return
	 */
	public boolean isLicenseEnough(String categoryId);
	
	/**
	 * 
	 * @return
	 */
	public List<LicenseUseInfoBo> getAllLicenseUseInfo();
}
