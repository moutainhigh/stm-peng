package com.mainsteam.stm.license.calc.impl;

import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.mainsteam.stm.license.LicenseModelEnum;
import com.mainsteam.stm.license.calc.api.ILicenseCalcApi;
import com.mainsteam.stm.portal.business.api.BizMainApi;
import com.mainsteam.stm.portal.business.service.bo.BizMainBo;

@Service("busiLicenseCalc")
public class BusiLicenseCalcImpl extends ILicenseCalcApi{

	private static final Logger logger=LoggerFactory.getLogger(BusiLicenseCalcImpl.class);
	
	@Autowired
	private BizMainApi bizMainApi;
	
	@Override
	public String getType() {
		return LicenseModelEnum.stmMonitorBusi.toString();
	}

	@Override
	public String getDesc() {
		return LicenseModelEnum.stmMonitorBusi.getCode();
	}
	
	@Override
	public int getUsed() {
		List<BizMainBo> allBussinessList = null;
		try {
			allBussinessList = bizMainApi.getAllPermissionsInfoList();
			if(allBussinessList == null){
				return 0;
			}else{
				return allBussinessList.size();
			}
		} catch (Exception e) {
			logger.error(e.getMessage(),e);
			return 0;
		}
	}

}
