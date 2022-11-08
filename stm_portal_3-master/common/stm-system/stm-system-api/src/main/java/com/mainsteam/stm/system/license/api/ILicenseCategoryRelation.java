package com.mainsteam.stm.system.license.api;

import com.mainsteam.stm.license.LicenseModelEnum;

public interface ILicenseCategoryRelation {
	
	/**
	 * 把categoryId转化成相对应的License枚举
	 * 不纳入计算的categoryId返回NULL
	 * @param categoryId
	 * @return
	 */
	public LicenseModelEnum categoryId2LicenseEnum(String categoryId) ;

}
