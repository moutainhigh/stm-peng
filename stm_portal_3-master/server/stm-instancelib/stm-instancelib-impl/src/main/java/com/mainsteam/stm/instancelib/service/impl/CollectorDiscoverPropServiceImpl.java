/**
 * 
 */
package com.mainsteam.stm.instancelib.service.impl;

import java.util.List;

import com.mainsteam.stm.instancelib.DiscoverPropService;
import com.mainsteam.stm.instancelib.exception.InstancelibException;
import com.mainsteam.stm.instancelib.obj.DiscoverProp;
import com.mainsteam.stm.instancelib.obj.InstanceProp;
import com.mainsteam.stm.instancelib.obj.ResourceInstance;
import com.mainsteam.stm.instancelib.objenum.PropTypeEnum;
import com.mainsteam.stm.instancelib.util.CollectorResourceInstanceCache;

/**
 * @author ziw
 * 
 */
public class CollectorDiscoverPropServiceImpl implements DiscoverPropService {
	private CollectorResourceInstanceCache cache;

	private static final PropTypeEnum PROP_TYPE = PropTypeEnum.DISCOVER;
	
	/**
	 * 
	 */
	public CollectorDiscoverPropServiceImpl() {
	}

	/**
	 * @param cache
	 *            the cache to set
	 */
	public final void setCache(CollectorResourceInstanceCache cache) {
		this.cache = cache;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * com.mainsteam.stm.instancelib.DiscoverPropService#updateProp(com.mainsteam
	 * .oc.instancelib.obj.DiscoverProp)
	 */
	@Override
	public void updateProp(DiscoverProp prop) throws InstancelibException {
		cache.getPropCache().update(prop.getInstanceId(),PROP_TYPE,prop);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * com.mainsteam.stm.instancelib.DiscoverPropService#updateProps(java.util
	 * .List)
	 */
	@Override
	public void updateProps(List<DiscoverProp> prop) throws InstancelibException {
		for (DiscoverProp discoverProp : prop) {
			cache.getPropCache().update(discoverProp.getInstanceId(), PROP_TYPE,discoverProp);
		}
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * com.mainsteam.stm.instancelib.DiscoverPropService#getPropByInstanceAndKey
	 * (long, java.lang.String)
	 */
	@Override
	public DiscoverProp getPropByInstanceAndKey(long instanceId, String key)
			throws InstancelibException {
		InstanceProp instanceProp = cache.getPropCache().get(instanceId, key, PROP_TYPE);
		return instanceProp == null ? null : (DiscoverProp) instanceProp;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * com.mainsteam.stm.instancelib.DiscoverPropService#getPropByInstanceId
	 * (long)
	 */
	@Override
	public List<DiscoverProp> getPropByInstanceId(long instanceId)
			throws InstancelibException {
		ResourceInstance instance = cache.get(instanceId);
		return instance == null ? null : instance.getDiscoverProps();
	}

}
