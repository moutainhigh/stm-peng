package com.mainsteam.stm.instancelib.service.impl;

import java.util.ArrayList;
import java.util.List;

import org.apache.commons.lang.StringUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import com.mainsteam.stm.instancelib.CompositeInstanceService;
import com.mainsteam.stm.instancelib.CompositePropService;
import com.mainsteam.stm.instancelib.InstanceRelationService;
import com.mainsteam.stm.instancelib.RelationService;
import com.mainsteam.stm.instancelib.dao.CompositeInstanceDAO;
import com.mainsteam.stm.instancelib.dao.pojo.CompositeInstanceDO;
import com.mainsteam.stm.instancelib.exception.InstancelibException;
import com.mainsteam.stm.instancelib.obj.CompositeInstance;
import com.mainsteam.stm.instancelib.obj.CompositeProp;
import com.mainsteam.stm.instancelib.obj.Instance;
import com.mainsteam.stm.instancelib.obj.PathRelation;
import com.mainsteam.stm.instancelib.obj.Relation;
import com.mainsteam.stm.instancelib.objenum.InstanceTypeEnum;
import com.mainsteam.stm.instancelib.service.CompositePropExtendService;
import com.mainsteam.stm.instancelib.service.RelationExtendService;
import com.mainsteam.stm.instancelib.service.impl.querybean.QueryCompositeInstance;
import com.mainsteam.stm.platform.sequence.service.ISequence;

/**
 * 
 * 复合实例业务类
 * 
 * @author xiaoruqiang
 * 
 */
public class CompositeInstanceServiceImpl implements CompositeInstanceService {

	private static final Log logger = LogFactory
			.getLog(CompositeInstanceService.class);
	
	private CompositePropExtendService compositePropExtendService;
	
	private CompositePropService compositePropService;
	
	//复合实例关系
	private RelationService relationService;
	
	//复合实例关系
    private RelationExtendService relationExtendService;
		
	//复合实例主键值
	private ISequence instanceSeq;
	//复合实例数据库
	private CompositeInstanceDAO compositeInstanceDAO;
	//复合实例包含实例集合
	private InstanceRelationService instanceCollectionService;

	private CompositeInstanceDO convertToDO(CompositeInstance compositeInstance) {
		CompositeInstanceDO tdo = new CompositeInstanceDO();
		tdo.setInstanceId(compositeInstance.getId());
		tdo.setInstanceName(compositeInstance.getName());
		tdo.setInstanceType(compositeInstance.getInstanceType().toString());
		return tdo;
	}

	private QueryCompositeInstance convertToDef(CompositeInstanceDO tdo) {
		CompositeInstance def = new CompositeInstance();
		def.setId(tdo.getInstanceId());
		def.setName(tdo.getInstanceName());
		def.setInstanceType(InstanceTypeEnum.valueOf(tdo
				.getInstanceType()));
		return new QueryCompositeInstance(def,this);
	}

	/**
	 * 验证输入参数
	 * 
	 * @param tdo
	 */
	private void validate(CompositeInstance compositeInstance) {


		//验证复合实例其他参数
	}

	@Override
	public long addCompositeInstance(CompositeInstance compositeInstance)
			throws InstancelibException {
		if (logger.isTraceEnabled()) {
			logger.trace("addCompositeInstance  start instanceId="
					+ compositeInstance.getId());
		}
		validate(compositeInstance);
		
		long instanceId = instanceSeq.next();
		compositeInstance.setId(instanceId);
		/*
		 * 添加到复合实例主表
		 */
		CompositeInstanceDO tdo = convertToDO(compositeInstance);
	
		try {
			compositeInstanceDAO.insertCompositeInstance(tdo);
		} catch (Exception e) {
			// TODO Auto-generated catch block
			if(logger.isErrorEnabled()){
				logger.error("", e);
			}
		}
		/*
		 * 添加复合实例属性
		 */
		List<CompositeProp> listProp = compositeInstance.getProps();
		if (listProp != null && !listProp.isEmpty()) {
			for (CompositeProp compositeProp : listProp) {
				compositeProp.setInstanceId(instanceId);
			}
			try {
				compositePropExtendService.addProps(listProp);
			} catch (Exception e) {
				// TODO Auto-generated catch block
				if(logger.isErrorEnabled()){
					logger.error("", e);
				}
			}
		}
		/*
		 * 添加实例集合
		 */
		List<Instance> instances = compositeInstance.getElements();
		if (instances != null && !instances.isEmpty()) {
			try {
				instanceCollectionService.insertInstanceCollectionPOs(instanceId,instances);
			} catch (Exception e) {
				// TODO Auto-generated catch block
				if(logger.isErrorEnabled()){
					logger.error("", e);
				}
			}
		}
		/*
		 * 添加实例关系
		 */
		List<Relation> listRelations = compositeInstance.getInstanceReatiom().getRelations();
		if (listRelations != null && !listRelations.isEmpty()) {
			for (Relation relation : listRelations) {
				if(relation instanceof PathRelation){
					PathRelation pathRelation = (PathRelation) relation;
					pathRelation.setInstanceId(compositeInstance.getId());
				}
			}
			try {
				relationExtendService.insertRelationPOs(listRelations);
			} catch (Exception e) {
				// TODO Auto-generated catch block
				if(logger.isErrorEnabled()){
					logger.error("", e);
				}
			}
		}
		if (logger.isTraceEnabled()) {
			logger.trace("addCompositeInstance end");
		}
		return instanceId;
	}

	@Override
	public void updateCompositeInstance(CompositeInstance compositeInstance)
			throws InstancelibException {
		if (logger.isTraceEnabled()) {
			logger.trace("updateCompositeInstance start instanceId="
					+ compositeInstance.getId());
		}
		validate(compositeInstance);
		
		CompositeInstanceDO tdo = convertToDO(compositeInstance);
		
		try {
			compositeInstanceDAO.updateCompositeInstance(tdo);
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		List<CompositeProp> listProp = compositeInstance.getProps();
		/*
		 * 更新复合实例属性
		 */
		if (listProp != null && !listProp.isEmpty()) {
			for (CompositeProp compositeProp : listProp) {
				compositeProp.setInstanceId(compositeInstance.getId());
			}
			try {
				compositePropService.updateProps(listProp);
			} catch (Exception e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}else{
			//清空所有的属性
			compositePropService.removePropByInstance(compositeInstance.getId());
		}
		/*
		 * 更新实例集合
		 */
		List<Instance> instances = compositeInstance.getElements();
		if (instances != null && !instances.isEmpty()) {
			try {
				instanceCollectionService.updateInstanceCollectionPOs(compositeInstance.getId(),instances);
			} catch (Exception e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}else{
			//清空该复合实例所有集合数据
			instanceCollectionService.removeInstanceCollectionPOByInstanceId(compositeInstance.getId());
		}

		/*
		 * 更新实例关系
		 */
		List<Relation> listRelations = compositeInstance.getInstanceReatiom().getRelations();
		if (listRelations != null && !listRelations.isEmpty()) {
			for (Relation relation : listRelations) {
				if(relation instanceof PathRelation){
					PathRelation pathRelation = (PathRelation) relation;
					pathRelation.setInstanceId(compositeInstance.getId());
				}
			}
			try {
				relationService.updateRelation(listRelations);
			} catch (Exception e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}else{
			//清空该复合实例所有连线关系
			relationService.removeRelation(compositeInstance.getId());
		}
		//TODO 是否需要删除缓存。待验证
		if (logger.isTraceEnabled()) {
			logger.trace("updateCompositeInstance  end");
		}
	}

	@Override
	public void removeCompositeInstance(long instanceId) throws InstancelibException {
		if (logger.isTraceEnabled()) {
			logger.trace("removeCompositeInstance start instanceId="
					+ instanceId);
		}
		
		try {
			compositeInstanceDAO.removeCompositeInstanceById(instanceId);
			/*
			 * 删除复合实例属性
			 */
			compositePropService.removePropByInstance(instanceId);

			/*
			 * 删除实例集合
			 */
			instanceCollectionService.removeInstanceCollectionPOByInstanceId(instanceId);
			/*
			 * 删除复合实例关系
			 */
			relationExtendService.removeRelationPOByInstanceId(instanceId);
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		

		//cache.delete(instanceId+"");
		if (logger.isTraceEnabled()) {
			logger.trace("removeCompositeInstance end");
		}

	}

	@Override
	public CompositeInstance getCompositeInstance(long instanceId)
			throws InstancelibException {
		if (logger.isTraceEnabled()) {
			logger.trace("getCompositeInstance start instanceId=" + instanceId);
		}
		//从缓存中获取数据
		QueryCompositeInstance result = null;//cache.get(instanceId+"");
//		if(result != null){
//			return result;
//		}
	
		CompositeInstanceDO tdo = null;
		try {
			tdo = compositeInstanceDAO
					.getCompositeInstanceById(instanceId);
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		if (tdo != null) {
			result = convertToDef(tdo);
		}
		if (logger.isTraceEnabled()) {
			logger.trace("getCompositeInstance end");
		}
		return result;
	}

	@Override
	public List<CompositeInstance> getCompositeInstanceByInstanceType(
			InstanceTypeEnum type) throws InstancelibException {
		if (logger.isTraceEnabled()) {
			logger.trace("getCompositeInstanceByInstanceType start instanceType="
					+ type);
		}
		List<CompositeInstance> results = null;

		List<CompositeInstanceDO> tdos = null;
		try {
			tdos = compositeInstanceDAO
					.getCompositeInstanceByInstanceType(type.toString());
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		if (tdos != null) {
			results = new ArrayList<>();
			for (CompositeInstanceDO tdo : tdos) {

				CompositeInstance result = getCompositeInstance(tdo
						.getInstanceId());

				results.add(result);
			}
		}

		if (logger.isTraceEnabled()) {
			logger.trace("getCompositeInstanceByInstanceType end");
		}
		return results;
	}

	@Override
	public List<CompositeInstance> getCompositeInstanceLikeName(String name)
			throws InstancelibException {
		if (logger.isTraceEnabled()) {
			logger.trace("getCompositeInstanceLikeName start instanceType="
					+ name);
		}
		List<CompositeInstance> results = null;
		if (StringUtils.isNotEmpty(name)) {
			List<CompositeInstanceDO> tdos = null;
			try {
				tdos = compositeInstanceDAO
						.getCompositeInstanceLikeName(name);
			} catch (Exception e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			if (tdos != null) {
				results = new ArrayList<>();
				for (CompositeInstanceDO tdo : tdos) {

					CompositeInstance result = getCompositeInstance(tdo
							.getInstanceId());

					results.add(result);
				}
			}
		}
		if (logger.isTraceEnabled()) {
			logger.trace("getCompositeInstanceLikeName end");
		}
		return results;
	}

	

	public void setCompositeInstanceDAO(
			CompositeInstanceDAO compositeInstanceDAO) {
		this.compositeInstanceDAO = compositeInstanceDAO;
	}


	public void setInstanceSeq(ISequence instanceSeq) {
		this.instanceSeq = instanceSeq;
	}

	public CompositePropExtendService getCompositePropExtendService() {
		return compositePropExtendService;
	}

	public void setCompositePropExtendService(
			CompositePropExtendService compositePropExtendService) {
		this.compositePropExtendService = compositePropExtendService;
	}

	public CompositePropService getCompositePropService() {
		return compositePropService;
	}

	public void setCompositePropService(CompositePropService compositePropService) {
		this.compositePropService = compositePropService;
	}

	public void setRelationExtendService(RelationExtendService relationExtendService) {
		this.relationExtendService = relationExtendService;
	}

	public InstanceRelationService getInstanceCollectionService() {
		return instanceCollectionService;
	}

	public RelationService getRelationService() {
		return relationService;
	}

	public void setInstanceCollectionService(
			InstanceRelationService instanceCollectionService) {
		this.instanceCollectionService = instanceCollectionService;
	}

	public void setRelationService(RelationService relationService) {
		this.relationService = relationService;
	}

	@Override
	public List<CompositeInstance> getCompositeInstancesByContainInstanceId(long containInstanceId) {
		List<CompositeInstance> results = null;
		List<CompositeInstanceDO> tdos = null;
		try {
			tdos = compositeInstanceDAO.getCompositeInstanceByContainInstanceId(containInstanceId);
			if (tdos != null) {
				results = new ArrayList<>();
				for (CompositeInstanceDO tdo : tdos) {
					CompositeInstance result = getCompositeInstance(tdo.getInstanceId());
					results.add(result);
				}
			}
		}catch (Exception e) {
			e.printStackTrace();
		}
		return results;
	}
}
