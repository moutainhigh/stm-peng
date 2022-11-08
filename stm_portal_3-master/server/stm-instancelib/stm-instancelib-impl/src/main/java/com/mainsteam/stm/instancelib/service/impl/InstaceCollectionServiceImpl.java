package com.mainsteam.stm.instancelib.service.impl;

import java.util.ArrayList;
import java.util.List;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import com.mainsteam.stm.instancelib.CompositeInstanceService;
import com.mainsteam.stm.instancelib.InstanceRelationService;
import com.mainsteam.stm.instancelib.ResourceInstanceService;
import com.mainsteam.stm.instancelib.dao.InstaceCollectionDAO;
import com.mainsteam.stm.instancelib.dao.pojo.InstanceCollectionPO;
import com.mainsteam.stm.instancelib.exception.InstancelibException;
import com.mainsteam.stm.instancelib.obj.CompositeInstance;
import com.mainsteam.stm.instancelib.obj.Instance;
import com.mainsteam.stm.instancelib.obj.ResourceInstance;
import com.mainsteam.stm.instancelib.objenum.InstanceTypeEnum;

public class InstaceCollectionServiceImpl implements InstanceRelationService{
	
	private static final Log logger = LogFactory
			.getLog(InstaceCollectionServiceImpl.class);	
	
	//资源实例
	private ResourceInstanceService resourceInstanceService;
	//复合实例
	private CompositeInstanceService compositeInstanceService;
	//复合实例数据访问DAO
	private InstaceCollectionDAO instaceCollectionDAO;
	

	private InstanceCollectionPO convertTOInstanceCollectionPOs(Instance instance){
		InstanceCollectionPO po = new InstanceCollectionPO();
		if (instance instanceof ResourceInstance) {
			po.setContainInstanceType(InstanceTypeEnum.RESOURCE.toString());
		} else {
			po.setContainInstanceType(InstanceTypeEnum.BUSINESS_APPLICATION.toString());
		}
		po.setContainInstanceId(instance.getId());
		return po;
	}
	
	
	
	/**
	 * 根据实例ID，查询改实例下的所有复合实例集合信息列表
	 * 复合实例引用的外键通过懒加载方式，在需要的时候加载数据
	 * @param instanceId  实例ID
	 * @return List<Instance> 复合实例集合信息列表
	 */
	public List<Instance> getInstaceCollectPOsByInstanceId(long instanceId)  throws InstantiationException {
		if (logger.isTraceEnabled()) {
			logger.trace("getInstaceCollectPOsByInstanceId   start instanceId" + instanceId);
		}
		List<Instance> elements = null;
		List<InstanceCollectionPO> listInstances = null;
		try {
			listInstances = instaceCollectionDAO.getInstaceCollectPOsByInstanceId(instanceId);
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		if(listInstances != null && !listInstances.isEmpty()){
			elements = new ArrayList<>();
			for (InstanceCollectionPO instanceCollectionPO : listInstances) {
				Instance instance = null;
				//资源实例
				if (instanceCollectionPO.getContainInstanceType().equals(
						InstanceTypeEnum.RESOURCE.toString())) {
					try {
						instance = resourceInstanceService.getResourceInstance(instanceCollectionPO.getContainInstanceId());
					} catch (InstancelibException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
				} else { 
					//复合实例
					CompositeInstance compositeInstance = null;
					try {
						compositeInstance = compositeInstanceService.getCompositeInstance(instanceCollectionPO.getContainInstanceId());
					} catch (InstancelibException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
					if (compositeInstance != null) {
						instance = compositeInstance;
					}
					// 复合实例参数信息获取将通过懒加载方式获取

				}
				if (instance != null) {
					elements.add(instance);
				}
			}
		}
		
		if (logger.isTraceEnabled()) {
			logger.trace("getInstaceCollectPOsByInstanceId   end");
		}
		return elements;
	}
	
	/**
	 * 批量插入复合实例集合信息
	 * 
	 * @param instances
	 */
	public void insertInstanceCollectionPOs(long instanceId,List<Instance> instances)  throws InstancelibException {
		if (logger.isTraceEnabled()) {
			logger.trace("insertInstanceCollectionPOs   start ");
		}
		if(instances == null || instances.isEmpty()){
			throw new InstancelibException(InstancelibException.CODE_ERROR_VALIDATE, "insert collection of compositeInstance for instances parameter is null or empty");
		}
		List<InstanceCollectionPO> listPos = new ArrayList<>();
		for (Instance instance : instances) {
			InstanceCollectionPO po = convertTOInstanceCollectionPOs(instance);
			po.setInstanceId(instanceId);
			listPos.add(po);
		}
		if(!listPos.isEmpty()){
			try {
				instaceCollectionDAO.insertInstanceCollectionPOs(listPos);
			} catch (Exception e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
		if (logger.isTraceEnabled()) {
			logger.trace("insertInstanceCollectionPOs   end");
		}
	}

	/**
	 * 插入复合实例集合信息
	 * 
	 * @param POs
	 */
	public void insertInstanceCollectionPO(long instanceId,Instance instance)  throws InstancelibException {
		if (logger.isTraceEnabled()) {
			logger.trace("insertInstanceCollectionPOs   start ");
		}
	
		if(instance == null){
			throw new InstancelibException(InstancelibException.CODE_ERROR_VALIDATE, "Add collection of compositeInstance for instance parameter is null");
		}
		InstanceCollectionPO po = convertTOInstanceCollectionPOs(instance);
		po.setInstanceId(instanceId);
		try {
			instaceCollectionDAO.insertInstanceCollectionPO(po);
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	
		if (logger.isTraceEnabled()) {
			logger.trace("insertInstanceCollectionPOs   end");
		}
	}
	
	/**
	 * 批量更新复合实例集合信息
	 * 
	 * @param instances
	 */
	public void updateInstanceCollectionPOs(long instanceId,List<Instance> instances)  throws InstancelibException {
		if (logger.isTraceEnabled()) {
			logger.trace("updateInstanceCollectionPOs   start ");
		}
		if(instances == null || instances.isEmpty()){
			throw new InstancelibException(InstancelibException.CODE_ERROR_VALIDATE, "Update collection of compositeInstance for instances parameter is null or empty");
		}
		List<InstanceCollectionPO> listPos = new ArrayList<>();
		for (Instance instance : instances) {
			InstanceCollectionPO po = convertTOInstanceCollectionPOs(instance);
			po.setInstanceId(instanceId);
			listPos.add(po);
		}
		if(!listPos.isEmpty()){
			try {
				instaceCollectionDAO.updateInstanceCollectionPOs(listPos);
			} catch (Exception e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
		if (logger.isTraceEnabled()) {
			logger.trace("updateInstanceCollectionPOs   end ");
		}
	}

	
	/**
	 * 批量更新复合实例集合信息
	 * 
	 * @param instance
	 */
	public void updateInstanceCollectionPO(long instanceId,Instance instance)  throws InstancelibException {
		if (logger.isTraceEnabled()) {
			logger.trace("updateInstanceCollectionPO   start instanceId=" + instance.getId());
		}
		if(instance == null){
			throw new InstancelibException(InstancelibException.CODE_ERROR_VALIDATE, "update collection of compositeInstance for instance parameter is null");
		}
		
		InstanceCollectionPO po = convertTOInstanceCollectionPOs(instance);
		po.setInstanceId(instanceId);
		try {
			instaceCollectionDAO.updateInstanceCollectionPO(po);
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		if (logger.isTraceEnabled()) {
			logger.trace("updateInstanceCollectionPO   end ");
		}
	}
	
	/**
	 * 删除实例的某个复合实例集合信息
	 * 
	 * @instanceId 实例ID
	 * @return 删除的行数
	 */
	public void removeInstanceCollectionPOByInstanceId(long instanceId)  throws InstancelibException {
		if (logger.isTraceEnabled()) {
			logger.trace("removeInstanceCollectionPOByInstanceId   start instanceId=" + instanceId);
		}
		try {
			instaceCollectionDAO.removeInstanceCollectionPOByInstanceId(instanceId);
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		if (logger.isTraceEnabled()) {
			logger.trace("removeInstanceCollectionPOByInstanceId   end ");
		}
	}

	public void setResourceInstanceService(
			ResourceInstanceService resourceInstanceService) {
		this.resourceInstanceService = resourceInstanceService;
	}

	public void setCompositeInstanceService(
			CompositeInstanceService compositeInstanceService) {
		this.compositeInstanceService = compositeInstanceService;
	}

	public void setInstaceCollectionDAO(InstaceCollectionDAO instaceCollectionDAO) {
		this.instaceCollectionDAO = instaceCollectionDAO;
	}
}
