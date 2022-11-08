package com.mainsteam.stm.topo.service.impl;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.mainsteam.stm.license.LicenseModelEnum;
import com.mainsteam.stm.license.calc.api.ILicenseCalcApi;
import com.mainsteam.stm.topo.dao.ISubTopoDao;

@Service("topoMrLicenseCalc")
public class TopoMrLicenseCalcImpl extends ILicenseCalcApi {
	/**
	 * 子拓扑Dao
	 */
	@Autowired
	private ISubTopoDao subDao;
	@Override
	public String getType() {
		return LicenseModelEnum.stmMonitorTopMr.toString();
	}

	@Override
	public int getUsed() {
		return subDao.roomCount();
	}

	@Override
	public String getDesc() {
		return LicenseModelEnum.stmMonitorTopMr.getCode();
	}

}
