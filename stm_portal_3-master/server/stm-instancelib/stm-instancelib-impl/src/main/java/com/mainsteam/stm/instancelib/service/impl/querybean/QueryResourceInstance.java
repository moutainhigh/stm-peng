package com.mainsteam.stm.instancelib.service.impl.querybean;

import java.util.List;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import com.mainsteam.stm.caplib.dict.DiscoverWayEnum;
import com.mainsteam.stm.instancelib.exception.InstancelibException;
import com.mainsteam.stm.instancelib.exception.InstancelibRuntimeException;
import com.mainsteam.stm.instancelib.obj.CustomProp;
import com.mainsteam.stm.instancelib.obj.DiscoverProp;
import com.mainsteam.stm.instancelib.obj.ExtendProp;
import com.mainsteam.stm.instancelib.obj.ModuleProp;
import com.mainsteam.stm.instancelib.obj.ResourceInstance;
import com.mainsteam.stm.instancelib.objenum.InstanceLifeStateEnum;
import com.mainsteam.stm.instancelib.service.impl.ResourceInstanceServiceImpl;

/**
 * <li>用于提供查询资源实例是实体 防止用户再次用这个类做一些set操作,影响缓存数据 资源实例引用的属性，子实例通过懒加载的方式加载数据</li>
 * 
 * @author xiaoruqiang
 */
public class QueryResourceInstance extends ResourceInstance {

	/**
	 * 
	 */
	private static final long serialVersionUID = 5530721278309758467L;

	private static final Log logger = LogFactory.getLog(QueryResourceInstance.class);

	private ResourceInstanceServiceImpl resourceInstanceService;

	public static long getSerialversionuid() {
		return serialVersionUID;
	}

	public QueryResourceInstance(ResourceInstance resourceInstance,
			ResourceInstanceServiceImpl resourceInstanceService) {
		super.setCategoryId(resourceInstance.getCategoryId());
		super.setParentCategoryId(resourceInstance.getParentCategoryId());
		super.setChildType(resourceInstance.getChildType());
		super.setShowIP(resourceInstance.getShowIP());
		super.setDiscoverNode(resourceInstance.getDiscoverNode());
		super.setDiscoverWay(resourceInstance.getDiscoverWay());
		super.setId(resourceInstance.getId());
		super.setLifeState(resourceInstance.getLifeState());
		super.setName(resourceInstance.getName());
		super.setShowName(resourceInstance.getShowName());
		super.setParentId(resourceInstance.getParentId());
		super.setResourceId(resourceInstance.getResourceId());
		super.setDomainId(resourceInstance.getDomainId());
		if (super.getParentId() != 0) {
			ResourceInstance parentInstance = null;
			try {
				parentInstance = resourceInstanceService.getResourceInstanceWithDeleted(super.getParentId());
			} catch (InstancelibException e) {
			}
			super.setParentInstance(parentInstance);
		}
		this.resourceInstanceService = resourceInstanceService;
	}

	@Override
	public void setDiscoverWay(DiscoverWayEnum discoverWay) {
		throw new InstancelibRuntimeException(InstancelibRuntimeException.CODE_ERROR_VALIDATE,
				"Operating resource instances, please create a new object ResourceInstance!");
	}

	@Override
	public void setResourceId(String resourceId) {
		throw new InstancelibRuntimeException(InstancelibRuntimeException.CODE_ERROR_VALIDATE,
				"Operating resource instances, please create a new object ResourceInstance!");
	}

	@Override
	public void setLifeState(InstanceLifeStateEnum lifeState) {
		throw new InstancelibRuntimeException(InstancelibRuntimeException.CODE_ERROR_VALIDATE,
				"Operating resource instances, please create a new object ResourceInstance!");
	}

	@Override
	public void setModuleProps(List<ModuleProp> moduleProps) {
		throw new InstancelibRuntimeException(InstancelibRuntimeException.CODE_ERROR_VALIDATE,
				"Operating resource instances, please create a new object ResourceInstance!");
	}

	@Override
	public void setDiscoverProps(List<DiscoverProp> discoverProps) {
		throw new InstancelibRuntimeException(InstancelibRuntimeException.CODE_ERROR_VALIDATE,
				"Operating resource instances, please create a new object ResourceInstance!");
	}

	@Override
	public void setCustomProps(List<CustomProp> customProps) {
		throw new InstancelibRuntimeException(InstancelibRuntimeException.CODE_ERROR_VALIDATE,
				"Operating resource instances, please create a new object ResourceInstance!");
	}

	@Override
	public void setExtendProps(List<ExtendProp> extendProps) {
		throw new InstancelibRuntimeException(InstancelibRuntimeException.CODE_ERROR_VALIDATE,
				"Operating resource instances, please create a new object ResourceInstance!");
	}

	@Override
	public void setParentId(long parentId) {
		throw new InstancelibRuntimeException(InstancelibRuntimeException.CODE_ERROR_VALIDATE,
				"Operating resource instances, please create a new object ResourceInstance!");
	}

	@Override
	public void setShowIP(String showIP) {
		throw new InstancelibRuntimeException(InstancelibRuntimeException.CODE_ERROR_VALIDATE,
				"Operating resource instances, please create a new object ResourceInstance!");
	}

	@Override
	public void setDiscoverNode(String discoverNode) {
		throw new InstancelibRuntimeException(InstancelibRuntimeException.CODE_ERROR_VALIDATE,
				"Operating resource instances, please create a new object ResourceInstance!");
	}

	@Override
	public void setCategoryId(String categoryId) {
		throw new InstancelibRuntimeException(InstancelibRuntimeException.CODE_ERROR_VALIDATE,
				"Operating resource instances, please create a new object ResourceInstance!");
	}

	@Override
	public void setChildType(String childType) {
		throw new InstancelibRuntimeException(InstancelibRuntimeException.CODE_ERROR_VALIDATE,
				"Operating resource instances, please create a new object ResourceInstance!");
	}

	@Override
	public void setChildren(List<ResourceInstance> children) {
		throw new InstancelibRuntimeException(InstancelibRuntimeException.CODE_ERROR_VALIDATE,
				"Operating resource instances, please create a new object ResourceInstance!");
	}

	@Override
	public void setId(long id) {
		throw new InstancelibRuntimeException(InstancelibRuntimeException.CODE_ERROR_VALIDATE,
				"Operating resource instances, please create a new object ResourceInstance!");
	}

	@Override
	public void setName(String name) {
		throw new InstancelibRuntimeException(InstancelibRuntimeException.CODE_ERROR_VALIDATE,
				"Operating resource instances, please create a new object ResourceInstance!");
	}

	@Override
	public List<ModuleProp> getModuleProps() {
		long intanceId = super.getId();
		List<ModuleProp> moduleProps = null;
		try {
			moduleProps = resourceInstanceService.getModulePropService().getPropByInstanceId(intanceId);
		} catch (InstancelibException e) {
			if (logger.isErrorEnabled()) {
				logger.error("getDiscoverProps error!", e);
			}
		}
		return moduleProps;
	}

	@Override
	public List<DiscoverProp> getDiscoverProps() {
		/**
		 * 如果是子资源实例，则返回父资源实例的发现属性
		 */
		long intanceId = 0;
		List<DiscoverProp> discoverProps = null;
		if (super.getParentId() != 0) {
			//modify by ziw at 2017年11月24日 上午9:53:11 -- 支持多点监控的子资源实例有自己的发现属性。
			if (getParentInstance() != null && isDiscoveryAlone()) {
				intanceId = super.getId();
			}else{
				intanceId = super.getParentId();
			}
		} else {
			intanceId = super.getId();
		}
		try {
			discoverProps = resourceInstanceService.getDiscoverPropService().getPropByInstanceId(intanceId);
		} catch (InstancelibException e) {
			if (logger.isErrorEnabled()) {
				logger.error("getDiscoverProps error!", e);
			}
		}
		return discoverProps;
	}

	@Override
	public List<CustomProp> getCustomProps() {
		long intanceId = super.getId();
		List<CustomProp> customProps = null;
		try {
			customProps = resourceInstanceService.getCustomPropService().getPropByInstanceId(intanceId);
		} catch (InstancelibException e) {
			if (logger.isErrorEnabled()) {
				logger.error("getDiscoverProps error!", e);
			}
		}
		return customProps;
	}

	@Override
	public List<ExtendProp> getExtendProps() {
		long intanceId = super.getId();
		List<ExtendProp> extendProps = null;
		try {
			extendProps = resourceInstanceService.getExtendPropService().getPropByInstanceId(intanceId);
		} catch (InstancelibException e) {
			if (logger.isErrorEnabled()) {
				logger.error("getDiscoverProps error!", e);
			}
		}
		return extendProps;
	}

	@Override
	public long getDomainId() {
		if (super.getParentId() != 0) {
			return getParentInstance().getDomainId();
		} else {
			return super.getDomainId();
		}
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see com.mainsteam.stm.instancelib.obj.ResourceInstance#getShowIP()
	 */
	@Override
	public String getShowIP() {
		if (super.getParentId() != 0) {
			return getParentInstance().getShowIP();
		} else {
			return super.getShowIP();
		}
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * com.mainsteam.stm.instancelib.obj.ResourceInstance#getDiscoverNode()
	 */
	@Override
	public String getDiscoverNode() {
		return super.getDiscoverNode();
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see com.mainsteam.stm.instancelib.obj.ResourceInstance#getDiscoverWay()
	 */
	@Override
	public DiscoverWayEnum getDiscoverWay() {
		if (super.getParentId() != 0) {
			return getParentInstance().getDiscoverWay();
		} else {
			return super.getDiscoverWay();
		}
	}

	@Override
	public List<ResourceInstance> getChildren() {
		if (logger.isDebugEnabled()) {
			logger.debug("Lazy  child ResourceInstance start instanceId =" + super.getId());
		}
		List<ResourceInstance> childResourceInstances = null;
		try {
			childResourceInstances = resourceInstanceService.getChildInstanceByParentId(super.getId());
		} catch (Exception e) {
			logger.error(e.getMessage());
		}
		if (logger.isDebugEnabled()) {
			logger.debug("Lazy  child ResourceInstance end instanceId =" + super.getId());
		}
		return childResourceInstances;
	}

	@Override
	public String[] getModulePropBykey(String key) {
		String[] result = null;
		try {
			ModuleProp prop = resourceInstanceService.getModulePropService().getPropByInstanceAndKey(super.getId(),
					key);
			if (prop != null) {
				result = prop.getValues();
			}
		} catch (Exception e1) {
			if (logger.isErrorEnabled()) {
				logger.error("getModulePropBykey", e1);
			}
		}
		return result;
	}

	@Override
	public String[] getDiscoverPropBykey(String key) {
		/**
		 * 如果是子资源实例，则返回父资源实例的发现属性
		 */
		long intanceId = 0;
		//modify by ziw at 2017年11月24日 上午9:53:11 -- 支持多点监控的子资源实例有自己的发现属性。
		if (super.getParentId() != 0 && !isDiscoveryAlone()) {
			intanceId = super.getParentId();
		} else {
			intanceId = super.getId();
		}
		String[] result = null;
		try {
			DiscoverProp prop = resourceInstanceService.getDiscoverPropService().getPropByInstanceAndKey(intanceId,
					key);
			if (prop != null) {
				result = prop.getValues();
			}
		} catch (Exception e1) {
			if (logger.isErrorEnabled()) {
				logger.error("getDiscoverPropBykey", e1);
			}
		}
		return result;
	}

	/**
	 *
	 * 判断子资源实例是否需要单独处理发现属性。
	 * 
	 * modify by ziw at 2017年11月24日 上午9:44:17
	 * 
	 * @return
	 */
	private boolean isDiscoveryAlone() {
		//TODO:通过能力库的api来判断
		return "DistributedProbed".equals(getParentInstance().getParentCategoryId());
	}

	@Override
	public String[] getExtendPropBykey(String key) {
		String[] result = null;
		try {
			ExtendProp prop = resourceInstanceService.getExtendPropService().getPropByInstanceAndKey(super.getId(),
					key);
			if (prop != null) {
				result = prop.getValues();
			}
		} catch (Exception e1) {
			if (logger.isErrorEnabled()) {
				logger.error("getExtendPropBykey", e1);
			}
		}
		return result;
	}

	@Override
	public String[] getCustomPropBykey(String key) {
		String[] result = null;
		try {
			CustomProp prop = resourceInstanceService.getCustomPropService().getPropByInstanceAndKey(super.getId(),
					key);
			if (prop != null) {
				result = prop.getValues();
			}
		} catch (Exception e1) {
			if (logger.isErrorEnabled()) {
				logger.error("getCustomPropBykey", e1);
			}
		}
		return result;
	}

}
