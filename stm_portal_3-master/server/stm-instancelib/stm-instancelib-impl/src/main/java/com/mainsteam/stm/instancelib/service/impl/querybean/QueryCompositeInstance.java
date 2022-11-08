package com.mainsteam.stm.instancelib.service.impl.querybean;

import java.util.List;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import com.mainsteam.stm.instancelib.exception.InstancelibRuntimeException;
import com.mainsteam.stm.instancelib.obj.CompositeInstance;
import com.mainsteam.stm.instancelib.obj.CompositeProp;
import com.mainsteam.stm.instancelib.obj.Instance;
import com.mainsteam.stm.instancelib.obj.InstanceRelation;
import com.mainsteam.stm.instancelib.obj.Relation;
import com.mainsteam.stm.instancelib.objenum.InstanceTypeEnum;
import com.mainsteam.stm.instancelib.service.impl.CompositeInstanceServiceImpl;
/**
 * <li>
 * 用于提供查询资源实例是实体
 * 防止用户再次用这个类做一些set操作.
 * 提供懒加载加载数据
 * </li>
 * @author xiaoruqiang
 */
public class QueryCompositeInstance extends CompositeInstance {

	/**
	 * 
	 */
	private static final long serialVersionUID = -1442501426715607610L;

	private static final Log logger = LogFactory
			.getLog(QueryCompositeInstance.class);
	
	private CompositeInstanceServiceImpl compositeInstanceService;
	
	public QueryCompositeInstance(CompositeInstance compositeInstance,CompositeInstanceServiceImpl compositeInstanceService){
		super.setId(compositeInstance.getId());
		super.setInstanceType(compositeInstance.getInstanceType());
		super.setName(compositeInstance.getName());
		this.compositeInstanceService = compositeInstanceService;
	}
	
	@Override
	public void setElements(List<Instance> elements) {
		throw new InstancelibRuntimeException(InstancelibRuntimeException.CODE_ERROR_VALIDATE, "Operating resource instances, please create a new object ResourceInstance!");
	}


	@Override
	public void setProps(List<CompositeProp> props) {
		throw new InstancelibRuntimeException(InstancelibRuntimeException.CODE_ERROR_VALIDATE, "Operating resource instances, please create a new object ResourceInstance!");
	}


	@Override
	public void setInstanceReatiom(InstanceRelation instanceReatiom) {
		throw new InstancelibRuntimeException(InstancelibRuntimeException.CODE_ERROR_VALIDATE, "Operating resource instances, please create a new object ResourceInstance!");
	}


	@Override
	public void setInstanceType(InstanceTypeEnum instanceType) {
		throw new InstancelibRuntimeException(InstancelibRuntimeException.CODE_ERROR_VALIDATE, "Operating resource instances, please create a new object ResourceInstance!");
	}


	@Override
	public void setId(long id) {
		throw new InstancelibRuntimeException(InstancelibRuntimeException.CODE_ERROR_VALIDATE, "Operating resource instances, please create a new object ResourceInstance!");
	}


	@Override
	public void setName(String name) {
		throw new InstancelibRuntimeException(InstancelibRuntimeException.CODE_ERROR_VALIDATE, "Operating resource instances, please create a new object ResourceInstance!");
	}


	@Override
	public List<Instance> getElements() {
		if(logger.isDebugEnabled()){
			logger.debug("Lazy Instance of CompositeInstance start instanceId =" + super.getId());
		}
		try {
			List<Instance> elements = compositeInstanceService.getInstanceCollectionService().getInstaceCollectPOsByInstanceId(super.getId());
			if (elements != null && !elements.isEmpty()) {
				super.setElements(elements);
			}
		} catch (Exception e) {
			logger.error(e.getMessage());
		}
		if(logger.isDebugEnabled()){
			logger.debug("Lazy Instance of CompositeInstance end instanceId =" + super.getId());
		}
		return super.getElements();
	}

	@Override
	public List<CompositeProp> getProps() {
		if(logger.isDebugEnabled()){
			logger.debug("Lazy Prop of CompositeInstance start instanceId =" + super.getId());
		}
		List<CompositeProp> allProp = null;
		try {
			allProp= compositeInstanceService.getCompositePropService().getPropByInstanceId(super.getId());
		} catch (Exception e) {
			logger.error(e.getMessage());
		}
		if(logger.isDebugEnabled()){
			logger.debug("Lazy Prop of CompositeInstance end instanceId =" + super.getId());
		}
		return allProp;
	}

	@Override
	public InstanceRelation getInstanceReatiom() {
		if(logger.isDebugEnabled()){
			logger.debug("Lazy InstanceReatiom of CompositeInstance start instanceId =" + super.getId());
		}
		InstanceRelation instanceRelation = new InstanceRelation();
		try {
			List<Relation> listRelations  = compositeInstanceService.getRelationService().getRelationByInstanceId(super.getId());
			if(listRelations != null){
				instanceRelation.setRelations(listRelations);
			}
		} catch (Exception e) {
			logger.error(e.getMessage());
		}
		if(logger.isDebugEnabled()){
			logger.debug("Lazy InstanceReatiom of CompositeInstance end instanceId =" + super.getId());
		}
		return instanceRelation;
	}


	public static long getSerialversionuid() {
		return serialVersionUID;
	}
	
}
