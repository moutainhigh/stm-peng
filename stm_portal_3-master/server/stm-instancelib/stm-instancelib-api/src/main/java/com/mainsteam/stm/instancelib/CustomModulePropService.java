package com.mainsteam.stm.instancelib;

import java.util.List;

import com.mainsteam.stm.instancelib.exception.InstancelibException;
import com.mainsteam.stm.instancelib.obj.CustomModuleProp;

public interface CustomModulePropService {
	
	/**
	 * 
	 * @param customModuleProp
	 */
	public void addCustomModuleProp(CustomModuleProp customModuleProp);
	
	/**
	 * 
	 * @param customModuleProps
	 */
	public void addCustomModuleProps(List<CustomModuleProp> customModuleProps);
	
	
	/**
	 * 
	 * @param instanceId
	 * @param key
	 * @return
	 * @throws InstancelibException
	 */
	public CustomModuleProp getCustomModulePropByInstanceIdAndKey(long instanceId, String key);
	
	/**
	 * 
	 * @param instanceId
	 * @return
	 * @throws InstancelibException
	 */
	public List<CustomModuleProp> getCustomModulePropByInstanceId(long instanceId);
	
	/**
	 * 
	 * @param instanceId
	 * @return
	 * @throws InstancelibException
	 */
	public List<CustomModuleProp> getCustomModuleProp();
	
	/**
	 * 
	 * @param instanceId
	 * @param key
	 */
	public void removeCustomModulePropByInstanceIdAndKey(long instanceId,String key);
	
	/**
	 * 
	 * @param instanceId
	 */
	public void removeCustomModulePropByInstanceId(long instanceId);
	
	/**
	 * 
	 * @param instanceId
	 */
	public void removeCustomModulePropByInstanceIds(List<Long> instanceIds);
	
	/**
	 * 更新指定的属性
	 * 
	 * @param prop 更新的属性。
	 * @throws Exception
	 */
	public void updateCustomModuleProp(CustomModuleProp prop);

	/**
	 * 更新指定的属性
	 * 
	 * @param prop  更新的属性。
	 * @throws Exception
	 */
	public void updateCustomModuleProps(List<CustomModuleProp> props);
	
	
}
