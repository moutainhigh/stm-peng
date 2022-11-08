package com.mainsteam.stm.portal.vm.service.impl;

import java.util.ArrayList;
import java.util.List;

import javax.annotation.Resource;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import com.mainsteam.stm.caplib.CapacityService;
import com.mainsteam.stm.caplib.common.CategoryDef;
import com.mainsteam.stm.portal.vm.api.VmProfileService;

public class VmProfileServiceImpl implements VmProfileService{
	
	private static final Log logger = LogFactory.getLog(VmProfileServiceImpl.class);
	
	private final static String VM_CATEGORY_ID = "VM";
	
	@Resource
	private CapacityService capacityService;
	
	@Override
	public List<CategoryDef> getVmCategoryDef() {
		CategoryDef vmDef = capacityService.getCategoryById(VM_CATEGORY_ID);
		List<CategoryDef> vmCategorys = new ArrayList<CategoryDef>();
		
		for(int i = 0; vmDef.getChildCategorys() != null && i < vmDef.getChildCategorys().length; i++){
			CategoryDef vmCategroy = new CategoryDef();
			vmCategroy.setId(vmDef.getChildCategorys()[i].getId());
			vmCategroy.setName(vmDef.getChildCategorys()[i].getName());		
			vmCategorys.add(vmCategroy);
		}
		return vmCategorys;
	}

}
