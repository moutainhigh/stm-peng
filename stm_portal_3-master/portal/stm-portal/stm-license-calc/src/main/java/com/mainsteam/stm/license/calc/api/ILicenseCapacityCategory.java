package com.mainsteam.stm.license.calc.api;


public interface ILicenseCapacityCategory {
	/**
	 * 通过CategoryId判断是否通过license授权
	 * @param categoryId
	 * @return
	 */
	public boolean isAllowCategory(String categoryId);

}
