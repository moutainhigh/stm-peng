package com.mainsteam.stm.instancelib.dao;

import java.util.List;

import com.mainsteam.stm.instancelib.dao.pojo.InstanceDependencePO;

/**
 * 资源实例关系DAO类
 * @author xiaoruqiang
 *
 */
public interface InstanceDependenceRelationDAO {

	public List<InstanceDependencePO> getAllDependence() throws Exception;
	
	public List<InstanceDependencePO> getPreviousDependence(long targertInstanceId) throws Exception;
	
	public List<InstanceDependencePO> getPreviousChildDependence(long targertChildInstanceId) throws Exception;
	
	public List<InstanceDependencePO> getNextDependence(long instanceId) throws Exception;
	
	public List<InstanceDependencePO> getNextChildDependence(long instanceId) throws Exception;
	
	public void insertDependence(InstanceDependencePO instanceDependencePO) throws Exception;
	
	public void insertDependences(List<InstanceDependencePO> instanceDependencePOs) throws Exception;
	
	public void removeDependence(long relationId) throws Exception;
	
	public void removeDependences(List<Long> relationIds) throws Exception;
	
	public void removeTopoLinkDependences() throws Exception;
	
	public void removeBusinessRelationByCompositeId(long compositeId) throws Exception; 
		
}
