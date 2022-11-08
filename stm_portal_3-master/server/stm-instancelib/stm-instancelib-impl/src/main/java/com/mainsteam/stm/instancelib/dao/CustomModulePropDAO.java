package com.mainsteam.stm.instancelib.dao;

import java.util.List;

import com.mainsteam.stm.instancelib.dao.pojo.CustomModulePropDO;


public interface CustomModulePropDAO {
	
	public void addCustomModuleProDO(CustomModulePropDO proDO)throws Exception;
	
	public void addCustomModuleProDOs(List<CustomModulePropDO> proDOs)throws Exception;
	
	public List<CustomModulePropDO> getCustomModulePropDOsById(long instanceId)throws Exception;
	
	public CustomModulePropDO getCustomModulePropDOsByIdAndKey(long instanceId,String key)throws Exception;
	
	public void updateCustomModulePropDOs(List<CustomModulePropDO> propDOs) throws Exception;
	
	public void updateCustomModulePropDO(CustomModulePropDO propDO) throws Exception;
	
	public void removeCustomProDOById(long instanceId);
	
	public void removeCustomProDOByIds(List<Long> instanceIds);
	
	public void removeCustomProDOByIdAndKey(long instanceId,String key);
	
	public List<CustomModulePropDO> getCustomPropDOs(); 
}
