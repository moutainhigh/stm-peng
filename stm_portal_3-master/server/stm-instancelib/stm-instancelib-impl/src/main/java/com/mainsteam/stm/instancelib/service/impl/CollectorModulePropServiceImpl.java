/**
 * 
 */
package com.mainsteam.stm.instancelib.service.impl;

import java.util.List;

import com.mainsteam.stm.instancelib.ModulePropService;
import com.mainsteam.stm.instancelib.dao.pojo.PropDO;
import com.mainsteam.stm.instancelib.exception.InstancelibException;
import com.mainsteam.stm.instancelib.obj.InstanceProp;
import com.mainsteam.stm.instancelib.obj.ModuleProp;
import com.mainsteam.stm.instancelib.obj.ResourceInstance;
import com.mainsteam.stm.instancelib.objenum.PropTypeEnum;
import com.mainsteam.stm.instancelib.service.ModulePropExtendService;
import com.mainsteam.stm.instancelib.util.CollectorResourceInstanceCache;

/**
 * @author ziw
 * 
 */
public class CollectorModulePropServiceImpl implements ModulePropService,ModulePropExtendService{
	
	private CollectorResourceInstanceCache cache;
	
	private static final PropTypeEnum PROP_TYPE = PropTypeEnum.MODULE;
	/**
	 * @param cache
	 *            the cache to set
	 */
	public final void setCache(CollectorResourceInstanceCache cache) {
		this.cache = cache;
	}

	/**
	 * 
	 */
	public CollectorModulePropServiceImpl() {
	}


	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * com.mainsteam.stm.instancelib.ModulePropService#getPropByInstanceAndKey
	 * (long, java.lang.String)
	 */
	@Override
	public ModuleProp getPropByInstanceAndKey(long instanceId, String key)
			throws InstancelibException {
		InstanceProp instanceProp = cache.getPropCache().get(instanceId, key, PROP_TYPE);
		return instanceProp == null ? null :(ModuleProp) instanceProp;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * com.mainsteam.stm.instancelib.ModulePropService#getPropByInstanceId(
	 * long)
	 */
	@Override
	public List<ModuleProp> getPropByInstanceId(long instanceId)
			throws InstancelibException {
		ResourceInstance instance = cache.get(instanceId);
		return instance == null ? null : instance.getModuleProps();
	}

	@Override
	public List<PropDO> convertToDOs(InstanceProp prop) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public void updateProp(ModuleProp prop) throws InstancelibException {
		InstanceProp instanceProp = cache.getPropCache().get(prop.getInstanceId(), prop.getKey(), PROP_TYPE);
		ModuleProp moduleProp = null;
		if(instanceProp != null){
			moduleProp = (ModuleProp) instanceProp;
			moduleProp.setValues(prop.getValues());
		}
	}

	@Override
	public void updateProps(List<ModuleProp> prop) throws InstancelibException {
		for (ModuleProp moduleProp : prop) {
			updateProp(moduleProp);
		}
	}

	@Override
	public List<ModuleProp> getPropByInstanceAndKeys(long instanceId,
			List<String> keys) throws InstancelibException {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public void addProp(ModuleProp prop) throws InstancelibException {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void addProps(List<ModuleProp> props) throws InstancelibException {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void removePropByInstanceAndKey(long instanceId, String key)
			throws InstancelibException {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void removePropByInstance(long instanceId) throws InstancelibException {
		// TODO Auto-generated method stub
		
	}

	@Override
	public List<ModuleProp> convertToDef(List<PropDO> tdos) {
		// TODO Auto-generated method stub
		return null;
	}

}
