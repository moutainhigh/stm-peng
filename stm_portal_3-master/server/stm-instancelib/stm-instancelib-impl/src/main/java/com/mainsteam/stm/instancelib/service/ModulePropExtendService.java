package com.mainsteam.stm.instancelib.service;

import java.util.List;

import com.mainsteam.stm.instancelib.dao.pojo.PropDO;
import com.mainsteam.stm.instancelib.exception.InstancelibException;
import com.mainsteam.stm.instancelib.obj.ModuleProp;


public interface ModulePropExtendService extends PropService{
	
	public List<ModuleProp> getPropByInstanceAndKeys(long instanceId,
			List<String> keys) throws Exception;
	
	public void addProp(ModuleProp prop) throws InstancelibException;
	
	public void addProps(List<ModuleProp> props) throws InstancelibException;
	
	public void removePropByInstanceAndKey(long instanceId, String key) throws InstancelibException;
	public void removePropByInstance(long instanceId) throws InstancelibException;
	
	public List<ModuleProp> convertToDef(List<PropDO> tdos);
}
