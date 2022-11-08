package com.mainsteam.stm.system.license.service.impl;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import javax.annotation.Resource;

import com.mainsteam.stm.caplib.CapacityService;
import com.mainsteam.stm.caplib.common.CategoryDef;
import com.mainsteam.stm.caplib.dict.CapacityConst;
import com.mainsteam.stm.license.LicenseModelEnum;
import com.mainsteam.stm.system.license.api.ILicenseCategoryRelation;

public class LicenseCategoryRelation implements ILicenseCategoryRelation{
	
	@Resource
	protected CapacityService capacService;
	
	/**
	 * 把categoryId转化成相对应的License枚举
	 * 不纳入计算的categoryId返回NULL
	 * //malachi lic
	 * @param categoryId
	 * @return
	 */
	public LicenseModelEnum categoryId2LicenseEnum(String categoryId) {
		CategoryDef categoryDef = getFirstLevelCategory(categoryId);
		//不需要使用license的类别集合
		List<String> ignoreLicenseList = Arrays.asList("SurveillancePlatform", "SurveillanceCamera");
		if (categoryDef != null) {
			// 虚拟化资源
			if (CapacityConst.VM.equals(categoryDef.getId())){
//				switch (categoryId) {
//				case "VirtualHost":
//				case "XenHosts":
//				case "FusionComputeHosts":
//					return LicenseModelEnum.stmMonitorVmHost;
//				case "VirtualVM":
//				case "XenVMs":
//				case "FusionComputeVMs":
//					return LicenseModelEnum.stmMonitorVmVm;
//				}
				String upperCategoryId = categoryId.toUpperCase();
				if(upperCategoryId.contains("HOST")) {
					return LicenseModelEnum.stmMonitorVmHost;
				} else if(upperCategoryId.contains("VM") || upperCategoryId.contains("ECS")) {
					return LicenseModelEnum.stmMonitorVmVm;
				} else {
					return null;
				}
			}
			if(ignoreLicenseList.contains(categoryId)){
				return null;
			}
			// 虚拟化以外的资源
			switch (categoryDef.getId()) {
			case "Hardware":
				//硬件监控不计算license
				return null;
			case CapacityConst.SNMPOTHERS:
			case CapacityConst.NETWORK_DEVICE:
				return LicenseModelEnum.stmMonitorSh;//stmMonitorHd
			case CapacityConst.STANDARDSERVICE:
				return LicenseModelEnum.stmMonitorApp;
			case CapacityConst.STORAGE:
				return LicenseModelEnum.stmMonitorStor;
			default:
				return LicenseModelEnum.stmMonitorSh;//stmMonitorRd
			}
		}
		return null;
	}
	
	/**
	 * 获取rootCategory下的第一层节点
	 * @param categoryId
	 * @return
	 */
	private CategoryDef getFirstLevelCategory(String categoryId){
		CategoryDef catDef = capacService.getCategoryById(categoryId);
		if(catDef != null && catDef.getParentCategory() != null){
			if(!capacService.getRootCategory().getId().equals(catDef.getParentCategory().getId())){
				catDef = getFirstLevelCategory(catDef.getParentCategory().getId());
			}
		}
		return catDef;
	}
}
