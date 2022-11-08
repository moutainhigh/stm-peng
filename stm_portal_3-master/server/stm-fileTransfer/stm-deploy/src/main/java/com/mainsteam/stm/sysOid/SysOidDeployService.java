package com.mainsteam.stm.sysOid;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.beans.BeanUtils;
import org.springframework.util.StringUtils;

import com.alibaba.fastjson.JSON;
import com.mainsteam.stm.caplib.CapacityService;
import com.mainsteam.stm.caplib.common.DeviceType;
import com.mainsteam.stm.caplib.dict.CaplibAPIResult;
import com.mainsteam.stm.sysOid.obj.ModuleBo;

public class SysOidDeployService implements SysOidDeployServiceMBean{
	private static final Log logger=LogFactory.getLog(SysOidDeployService.class);
	private CapacityService capacityService;
	
	public void setCapacityService(CapacityService capacityService) {
		this.capacityService = capacityService;
	}
	
	@Override
	public CaplibAPIResult SysOidReload(ModuleBo moduleBo) {
		if(logger.isInfoEnabled())
			logger.info("SysOidReload(moduleBo):"+JSON.toJSON(moduleBo));
		
		String resourceId = moduleBo.getResourceId();
		if(StringUtils.isEmpty(resourceId)){
			return null;
		}
		DeviceType [] devices = capacityService.getDeviceTypeByResourceId(resourceId);
		DeviceType device = null;
		
		if(devices==null||devices.length==0){
//			return new CaplibAPIResult(false, CaplibAPIErrorCode.OK);
			device = new DeviceType();
		}else{
			device = new DeviceType(devices[0]);
		}
		BeanUtils.copyProperties(moduleBo,device);
		
		if(logger.isInfoEnabled())
			logger.info("SysOidReload(device):"+JSON.toJSON(device));
		
		device.setSysOid(moduleBo.getSysOid());
		return capacityService.addType(device);
		
	}

}
