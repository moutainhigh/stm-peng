package com.mainsteam.stm.discovery;

import java.util.List;

import com.mainsteam.stm.caplib.CapacityService;
import com.mainsteam.stm.caplib.common.CategoryDef;
import com.mainsteam.stm.caplib.resource.ResourceDef;

public class CategoryTest {

	public CategoryTest() {
		// TODO Auto-generated constructor stub
	}

	public static void main(String[] args) {
		// TODO Auto-generated method stub
		ResourceDef def = null;
		CapacityService service = null;
		List<CategoryDef> categorys = service.getCategoryList(2);
		for (CategoryDef categoryDef : categorys) {
			ResourceDef[] defs = categoryDef.getResourceDefs();
			for (ResourceDef resourceDef : defs) {
				resourceDef.getId();
				resourceDef.getName();
			}
		}
	}

}
