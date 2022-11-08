package com.mainsteam.stm.license.calc.impl;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import javax.annotation.Resource;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.beans.BeansException;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;
import org.springframework.stereotype.Service;

import com.mainsteam.stm.license.LicenseModelEnum;
import com.mainsteam.stm.license.calc.api.ILicenseCalcApi;
import com.mainsteam.stm.license.calc.api.ILicenseCalcService;
import com.mainsteam.stm.system.license.api.ILicenseCategoryRelation;
import com.mainsteam.stm.system.license.bo.LicenseUseInfoBo;

@Service("licenseCalcService")
public class LicenseCalcServiceImpl implements ILicenseCalcService, ApplicationContextAware {

	@Resource(name="resShLicenseCalc")
	private ILicenseCalcApi resShLicenseCalc;

	@Resource(name="resHdLicenseCalc")
	private ILicenseCalcApi resHdLicenseCalc;
	
	@Resource(name="resRdLicenseCalc")
	private ILicenseCalcApi resRdLicenseCalc;
	
	@Resource(name="resStorLicenseCalc")
	private ILicenseCalcApi resStorLicenseCalc;
	
	@Resource(name="vmHostLicenseCalc")
	private ILicenseCalcApi vmHostLicenseCalc;
	
	@Resource(name="vmVmLicenseCalc")
	private ILicenseCalcApi vmVmLicenseCalc;
	
	@Resource(name="busiLicenseCalc")
	private ILicenseCalcApi busiLicenseCalc;
	
	@Resource(name="topoMrLicenseCalc")
	private ILicenseCalcApi topoMrLicenseCalc;

	@Resource(name="appLicenseCalc")
	private ILicenseCalcApi appLicenseCalc;
	
	@Resource
	protected ILicenseCategoryRelation licenseCategoryRelation;
	
	private  final Log logger = LogFactory.getLog(LicenseCalcServiceImpl.class);
	
	private ApplicationContext context;
	
	private List<ILicenseCalcApi> remains;
	
	@SuppressWarnings("incomplete-switch")
	@Override
	public boolean isLicenseEnough(LicenseModelEnum lmEnum) {
		switch (lmEnum) {
//		case stmMonitorHd:
//			return resHdLicenseCalc.getRemain() > 0;
//		case stmMonitorRd:
//			return resRdLicenseCalc.getRemain() > 0;
		case stmMonitorSh:
			return resShLicenseCalc.getRemain() > 0;
		case stmMonitorBusi:
			return busiLicenseCalc.getRemain() > 0;
		case stmMonitorStor:
			return resStorLicenseCalc.getRemain() > 0;
		case stmMonitorTopMr:
			return topoMrLicenseCalc.getRemain() > 0;
		case stmMonitorVmHost:
			return vmHostLicenseCalc.getRemain() > 0;
		case stmMonitorVmVm:
			return vmVmLicenseCalc.getRemain() > 0;
		case stmMonitorApp:
			return appLicenseCalc.getRemain() > 0;
		}
		return false;
	}

	@Override
	public boolean isLicenseEnough(String categoryId) {
		LicenseModelEnum lmEnum = licenseCategoryRelation.categoryId2LicenseEnum(categoryId);
		//不计算license的设备
		if(lmEnum == null){
			return true;
		}else{
			return isLicenseEnough(lmEnum);
		}
	}

	@Override
	public void setApplicationContext(ApplicationContext context) throws BeansException {
		this.context = context;
	}
	//malachi lic
	@Override
	public List<LicenseUseInfoBo> getAllLicenseUseInfo() {
		if(remains==null || remains.size()==0){
			Collection<ILicenseCalcApi> tempRemains = context.getBeansOfType(ILicenseCalcApi.class).values();
			remains = new ArrayList<ILicenseCalcApi>();
			for(ILicenseCalcApi api : tempRemains){
				remains.add(api);
			}
		}
		
		List<LicenseUseInfoBo> luiBoList = new ArrayList<LicenseUseInfoBo>();
		for (int i = 0; remains != null && i < remains.size(); i++) {
			ILicenseCalcApi remain = remains.get(i);
			LicenseUseInfoBo licenseUseInfoBo = new LicenseUseInfoBo();
			licenseUseInfoBo.setLmEnum(remain.getType());
			licenseUseInfoBo.setLicenseNameCN(remain.getDesc());
			licenseUseInfoBo.setUsedCount(remain.getUsed());
			licenseUseInfoBo.setAuthorCount(remain.getAuthorCount());
			licenseUseInfoBo.setRemainCount(remain.getRemain());
			luiBoList.add(licenseUseInfoBo);
		}
		
		return luiBoList;
	}
}
