package com.mainsteam.stm.instancelib.service;

import java.util.List;

import com.mainsteam.stm.instancelib.dao.pojo.PropDO;
import com.mainsteam.stm.instancelib.exception.InstancelibException;
import com.mainsteam.stm.instancelib.obj.DiscoverProp;



public interface DiscoverPropExtendService extends PropService{
	
	
	public List<DiscoverProp> getPropByInstanceAndKeys(long instanceId,
			List<String> keys) throws InstancelibException;
	
	public void addProp(final DiscoverProp prop) throws InstancelibException;
	
	public void addProps(List<DiscoverProp> props) throws InstancelibException;
	
	public void removePropByInstanceAndKey(long instanceId, String key) throws InstancelibException ;
	
	public void removePropByInstance(long instanceId) throws InstancelibException;
	
	public List<DiscoverProp> convertToDef(List<PropDO> tdos);
}
