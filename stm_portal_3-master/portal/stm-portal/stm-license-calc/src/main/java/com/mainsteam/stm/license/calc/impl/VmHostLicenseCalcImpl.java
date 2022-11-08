package com.mainsteam.stm.license.calc.impl;

import java.util.List;

import javax.annotation.Resource;

import org.apache.log4j.Logger;
import org.springframework.stereotype.Service;

import com.mainsteam.stm.instancelib.exception.InstancelibException;
import com.mainsteam.stm.instancelib.obj.ResourceInstance;
import com.mainsteam.stm.license.LicenseModelEnum;
import com.mainsteam.stm.license.calc.api.ILicenseCalcApi;
import com.mainsteam.stm.system.license.api.ILicenseCategoryRelation;

@Service("vmHostLicenseCalc")
public class VmHostLicenseCalcImpl extends ILicenseCalcApi {
	
	private Logger logger = Logger.getLogger(VmHostLicenseCalcImpl.class);
	
	@Resource
	protected ILicenseCategoryRelation licenseCategoryRelation;
	
	@Override
	public String getType() {
		return LicenseModelEnum.stmMonitorVmHost.toString();
	}

	@Override
	public int getUsed() {
		int used = 0;
		try {
			List<ResourceInstance> riList = riService.getAllParentInstance();
			for (int i = 0; riList != null && i < riList.size(); i++) {
				ResourceInstance ri = riList.get(i);
				LicenseModelEnum lme = licenseCategoryRelation.categoryId2LicenseEnum(ri.getCategoryId());
				if (lme != null && getType().equals(lme.toString())) {
					++ used;
				}
			}
		} catch (InstancelibException e) {
			used = -1;
			logger.error("VmHostLicenseCalcImpl getUsed method:", e);
		}
		return used;
	}

	@Override
	public String getDesc() {
		return LicenseModelEnum.stmMonitorVmHost.getCode();
	}

}
