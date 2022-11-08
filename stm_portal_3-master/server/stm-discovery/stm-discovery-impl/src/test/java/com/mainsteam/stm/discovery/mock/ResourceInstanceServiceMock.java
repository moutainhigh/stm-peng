/**
 * 
 */
package com.mainsteam.stm.discovery.mock;

import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import com.mainsteam.stm.instancelib.ResourceInstanceService;
import com.mainsteam.stm.instancelib.exception.InstancelibException;
import com.mainsteam.stm.instancelib.obj.ResourceInstance;
import com.mainsteam.stm.instancelib.objenum.InstanceLifeStateEnum;

/**
 * @author ziw
 *
 */
public class ResourceInstanceServiceMock implements ResourceInstanceService {

	/**
	 * 
	 */
	public ResourceInstanceServiceMock() {
		// TODO Auto-generated constructor stub
	}

	/* (non-Javadoc)
	 * @see com.mainsteam.stm.instancelib.ResourceInstanceService#addResourceInstance(com.mainsteam.stm.instancelib.obj.ResourceInstance)
	 */
	@Override
	public long addResourceInstance(ResourceInstance arg0)
			throws InstancelibException {
		// TODO Auto-generated method stub
		return 0;
	}

	/* (non-Javadoc)
	 * @see com.mainsteam.stm.instancelib.ResourceInstanceService#getAllParentInstance()
	 */
	@Override
	public List<ResourceInstance> getAllParentInstance()
			throws InstancelibException {
		// TODO Auto-generated method stub
		return null;
	}

	/* (non-Javadoc)
	 * @see com.mainsteam.stm.instancelib.ResourceInstanceService#getChildInstanceByParentId(long)
	 */
	@Override
	public List<ResourceInstance> getChildInstanceByParentId(long arg0)
			throws InstancelibException {
		// TODO Auto-generated method stub
		return null;
	}

	/* (non-Javadoc)
	 * @see com.mainsteam.stm.instancelib.ResourceInstanceService#getParentInstanceByCategoryId(java.lang.String)
	 */
	@Override
	public List<ResourceInstance> getParentInstanceByCategoryId(String arg0)
			throws InstancelibException {
		// TODO Auto-generated method stub
		return null;
	}

	/* (non-Javadoc)
	 * @see com.mainsteam.stm.instancelib.ResourceInstanceService#getParentResourceInstanceByDomain(long)
	 */
	public List<ResourceInstance> getParentResourceInstanceByDomain(long arg0)
			throws InstancelibException {
		// TODO Auto-generated method stub
		return null;
	}

	/* (non-Javadoc)
	 * @see com.mainsteam.stm.instancelib.ResourceInstanceService#getParentResourceInstanceByLifeState(com.mainsteam.stm.instancelib.objenum.InstanceLifeStateEnum)
	 */
	@Override
	public List<ResourceInstance> getParentResourceInstanceByLifeState(
			InstanceLifeStateEnum arg0) throws InstancelibException {
		// TODO Auto-generated method stub
		return null;
	}

	/* (non-Javadoc)
	 * @see com.mainsteam.stm.instancelib.ResourceInstanceService#getParentResourceInstanceByNode(java.lang.String)
	 */
	@Override
	public List<ResourceInstance> getParentResourceInstanceByNode(String arg0)
			throws InstancelibException {
		// TODO Auto-generated method stub
		return null;
	}

	/* (non-Javadoc)
	 * @see com.mainsteam.stm.instancelib.ResourceInstanceService#getResourceInstance(long)
	 */
	@Override
	public ResourceInstance getResourceInstance(long arg0)
			throws InstancelibException {
		// TODO Auto-generated method stub
		return null;
	}

	/* (non-Javadoc)
	 * @see com.mainsteam.stm.instancelib.ResourceInstanceService#getResourceInstanceByResourceId(java.lang.String)
	 */
	@Override
	public List<ResourceInstance> getResourceInstanceByResourceId(String arg0)
			throws InstancelibException {
		// TODO Auto-generated method stub
		return null;
	}

	/* (non-Javadoc)
	 * @see com.mainsteam.stm.instancelib.ResourceInstanceService#refreshResourceInstance(com.mainsteam.stm.instancelib.obj.ResourceInstance)
	 */
	@Override
	public void refreshResourceInstance(ResourceInstance arg0)
			throws InstancelibException {
		// TODO Auto-generated method stub

	}

	/* (non-Javadoc)
	 * @see com.mainsteam.stm.instancelib.ResourceInstanceService#removeResourceInstance(long)
	 */
	@Override
	public void removeResourceInstance(long arg0) throws InstancelibException {
		// TODO Auto-generated method stub

	}

	/* (non-Javadoc)
	 * @see com.mainsteam.stm.instancelib.ResourceInstanceService#removeResourceInstances(java.util.List)
	 */
	@Override
	public void removeResourceInstances(List<Long> arg0)
			throws InstancelibException {
		// TODO Auto-generated method stub

	}

	/* (non-Javadoc)
	 * @see com.mainsteam.stm.instancelib.ResourceInstanceService#updateResourceInstanceDiscoverIP(long, java.lang.String)
	 */
	@Override
	public void updateResourceInstanceDiscoverIP(long arg0, String arg1)
			throws InstancelibException {
		// TODO Auto-generated method stub

	}

	/* (non-Javadoc)
	 * @see com.mainsteam.stm.instancelib.ResourceInstanceService#updateResourceInstanceDiscoverNode(long, java.lang.String)
	 */
	@Override
	public void updateResourceInstanceDiscoverNode(long arg0, String arg1)
			throws InstancelibException {
		// TODO Auto-generated method stub

	}

	/* (non-Javadoc)
	 * @see com.mainsteam.stm.instancelib.ResourceInstanceService#updateResourceInstanceName(long, java.lang.String)
	 */
	@Override
	public void updateResourceInstanceName(long arg0, String arg1)
			throws InstancelibException {
		// TODO Auto-generated method stub

	}

	/* (non-Javadoc)
	 * @see com.mainsteam.stm.instancelib.ResourceInstanceService#updateResourceInstanceState(java.util.Map)
	 */
	@Override
	public void updateResourceInstanceState(
			Map<Long, InstanceLifeStateEnum> arg0) throws InstancelibException {
		// TODO Auto-generated method stub

	}

	public List<ResourceInstance> getParentResourceInstanceByDomainIds(
			HashSet<Long> arg0) throws InstancelibException {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public void addResourceInstanceForLink(List<ResourceInstance> arg0)
			throws InstancelibException {
		// TODO Auto-generated method stub
		
	}

	@Override
	public List<ResourceInstance> getResourceInstances(List<Long> arg0)
			throws InstancelibException {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public List<ResourceInstance> getParentResourceInstanceByDomainIds(
			Set<Long> arg0) throws InstancelibException {
		// TODO Auto-generated method stub
		return null;
	}

}
