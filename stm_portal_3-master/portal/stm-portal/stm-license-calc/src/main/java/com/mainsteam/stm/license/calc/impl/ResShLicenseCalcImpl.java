package com.mainsteam.stm.license.calc.impl;

import com.mainsteam.stm.instancelib.exception.InstancelibException;
import com.mainsteam.stm.instancelib.obj.ResourceInstance;
import com.mainsteam.stm.license.LicenseModelEnum;
import com.mainsteam.stm.license.calc.api.ILicenseCalcApi;
import com.mainsteam.stm.system.license.api.ILicenseCategoryRelation;
import org.apache.log4j.Logger;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.List;

@Service("resShLicenseCalc")
public class ResShLicenseCalcImpl extends ILicenseCalcApi {
	
	private Logger logger = Logger.getLogger(ResShLicenseCalcImpl.class);
	
	@Resource
	protected ILicenseCategoryRelation licenseCategoryRelation;
	
	@Override
	public String getType() {
		return LicenseModelEnum.stmMonitorSh.toString();//stmMonitorHd
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
			logger.error("ResHdLicenseCalcImpl getUsed method:", e);
		}
		return used;
	}

	@Override
	public String getDesc() {
		return LicenseModelEnum.stmMonitorSh.getCode();
	}
}
