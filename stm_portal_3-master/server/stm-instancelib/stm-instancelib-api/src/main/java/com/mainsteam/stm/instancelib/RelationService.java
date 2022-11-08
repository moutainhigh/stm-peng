package com.mainsteam.stm.instancelib;

import java.util.List;

import com.mainsteam.stm.instancelib.exception.InstancelibException;
import com.mainsteam.stm.instancelib.obj.Relation;
import com.mainsteam.stm.instancelib.objenum.InstanceTypeEnum;

public interface RelationService {

	/**
	 * 更新指定的实例的关系
	 * 
	 * @param prop 更新的属性。
	 * @throws Exception
	 */
	public void updateRelation(List<Relation> relations) throws InstancelibException;
	
	/**
	 * 删除关系通过类型和资源实例Id
	 * @param instanceId 资源实例ID
	 * @param type 实例类型
	 */
	public void removeRelation(long instanceId,InstanceTypeEnum type) throws InstancelibException;
	
	/**
	 * 删除关系通过资源实例Id
	 * @param instanceId 资源实例ID
	 * @param type 实例类型
	 */
	public void removeRelation(long instanceId) throws InstancelibException;
	/**
	 * 通过实例Id 查询 实例关系
	 * @param instanceId 实例Id
	 * @throws Exception 
	 */
	public List<Relation> getRelationByInstanceId(long instanceId) throws InstancelibException;
	
	/**
	 * 通过类型查找复合实例关系，只要关系一端包含该类型。
	 * @param instanceTypeEnum 类型
	 * @return
	 * @throws InstancelibException
	 */
	public List<Relation> getRelationByInstanceType(InstanceTypeEnum instanceTypeEnum) throws InstancelibException;
}
