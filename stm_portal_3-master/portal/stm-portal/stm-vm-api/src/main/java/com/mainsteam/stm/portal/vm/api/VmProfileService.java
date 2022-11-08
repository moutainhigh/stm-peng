package com.mainsteam.stm.portal.vm.api;

import java.util.List;

import com.mainsteam.stm.caplib.common.CategoryDef;

public interface VmProfileService {
	
	/**
	 * 获取虚拟化资源的类别
	 * @return
	 */
	public List<CategoryDef> getVmCategoryDef();
	
}
