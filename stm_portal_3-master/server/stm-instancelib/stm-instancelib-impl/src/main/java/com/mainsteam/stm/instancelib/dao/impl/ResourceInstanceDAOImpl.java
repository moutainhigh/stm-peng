package com.mainsteam.stm.instancelib.dao.impl;

import java.util.ArrayList;
import java.util.List;

import org.apache.ibatis.session.ExecutorType;
import org.apache.ibatis.session.SqlSession;
import org.apache.ibatis.session.SqlSessionFactory;

import com.mainsteam.stm.instancelib.dao.ResourceInstanceDAO;
import com.mainsteam.stm.instancelib.dao.pojo.ResourceInstanceDO;

/**
 * 资源实例数据库操作实现类
 * @author xiaoruqiang
 *
 */
public class ResourceInstanceDAOImpl implements ResourceInstanceDAO {

	private SqlSession session;
	private SqlSessionFactory myBatisSqlSessionFactory;
	
	public void setSession(SqlSession session) {
		this.session = session;
	}
	public void setMyBatisSqlSessionFactory(
			SqlSessionFactory myBatisSqlSessionFactory) {
		this.myBatisSqlSessionFactory = myBatisSqlSessionFactory;
	}
	
	@Override
	public ResourceInstanceDO getResourceInstanceById(long instanceId) {
		return session.selectOne("getResourceInstanceById",instanceId);
	}

	@Override
	public void insertResourceInstances(List<ResourceInstanceDO> instances) throws Exception {
		try(SqlSession batchSession = myBatisSqlSessionFactory.openSession(ExecutorType.BATCH,false)){
			for(ResourceInstanceDO resourceInstanceDO : instances){
				batchSession.insert("insertResourceInstance", resourceInstanceDO);
			}
			batchSession.commit();
		}
	}

	@Override
	public void insertResourceInstance(ResourceInstanceDO instance) throws Exception {
		session.insert("insertResourceInstance", instance);
	}

	@Override
	public synchronized void updateResourceInstances(List<ResourceInstanceDO> instances) throws Exception {
		try(SqlSession batchSession = myBatisSqlSessionFactory.openSession(ExecutorType.BATCH,false)){
			for(ResourceInstanceDO instance : instances){
				batchSession.update("updateResourceInstance", instance);
			}
			batchSession.commit();
		}
	}

	@Override
	public void updateResourceInstance(ResourceInstanceDO instance) throws Exception{
		session.update("updateResourceInstance", instance);
	}

	@Override
	public int removeResourceInstanceById(long instanceId) throws Exception {
		return session.delete("removeResourceInstanceById", instanceId);
	}

	
	@Override
	public List<ResourceInstanceDO> getAllParentInstance() throws Exception {
		return session.selectList("getAllParentInstance");
	}

	public List<ResourceInstanceDO> getAllInstanceByNode(String discoverNode) throws Exception{
		return session.selectList("getAllInstanceByNode",discoverNode);
	}
	
	@Override
	public List<ResourceInstanceDO> getInstancesByResourceDO(ResourceInstanceDO instance) throws Exception  {
		return session.selectList("getInstancesByResourceDO", instance);
	}

	@Override
	public List<ResourceInstanceDO> getAllParentInstanceByNode(String groupNodeId){
		return session.selectList("getParentResourceInstanceByNode", groupNodeId);
	}
	
	@Override
	public List<ResourceInstanceDO> getAllParentInstanceByDomain(long domainId){
		return session.selectList("getAllParentInstanceByDomain", domainId);
	}

	@Override
	public List<ResourceInstanceDO> getAllInstance() {
		return session.selectList("getAllInstance");
	}
	@Override
	public void removeResourceInstanceByIds(List<Long> instanceIds)
			throws Exception {
		try(SqlSession batchSession = myBatisSqlSessionFactory.openSession(ExecutorType.BATCH,false)){
			for (Long instanceId : instanceIds) {
				batchSession.delete("removeResourceInstanceById",instanceId);
			}
			batchSession.commit();
		}
	}

	@Override
	public List<ResourceInstanceDO> getExistParentInstanceByResourceId(List<String> resourceIds) {
		return session.selectList("getExistParentInstanceByResourceId",resourceIds);
	}
	
	@Override
	public List<ResourceInstanceDO> getExistChildInstanceByResourceId(List<String> resourceIds) {
		return session.selectList("getExistChildInstanceByResourceId",resourceIds);
	}
	
	@Override
	public synchronized void removeResourceInstance(List<Long> instancesIds) {
		try(SqlSession batchSession = myBatisSqlSessionFactory.openSession(ExecutorType.BATCH,false)){
			for (Long instanceId : instancesIds) {
				batchSession.update("batchUpdateResourceInstanceToDelete",instanceId);
			}
			batchSession.commit();
		}
	}
	@Override
	public List<ResourceInstanceDO> getAllChildrenInstanceIdbyParentIds(List<Long> parentIds) {
		return session.selectList("getAllChildrenInstanceIdbyParentIds",parentIds);
	}
	@Override
	public List<Long> getAllMonirotedChildrenInstanceIdByParentIds(List<Long> parentIds) {
		List<Long> result = new ArrayList<Long>();
		if(parentIds!=null && parentIds.size()>0){
			//oracle in 查询参数最大支持1000，为避免出错，将parentIds分组，每组最多999个
			int signSize = 999;
			if(parentIds.size()>signSize){
				List<List<Long>> allParentIds = new ArrayList<List<Long>>();
				double parentIdSize = parentIds.size();
				double sheetCount=(double) (parentIdSize/signSize);
				int sc = (int) Math.ceil(sheetCount);
				for (int i = 0; i <sc; i++) {
					List<Long> signList = new ArrayList<>();
					if(i+1==sc){
						signList = parentIds.subList((int)(i*signSize), parentIds.size());
					}else{
						signList = parentIds.subList((int)(i*signSize),(int)((i+1)*signSize));
					}
					allParentIds.add(signList);
				}
				
				if(allParentIds!=null && allParentIds.size()>0){
					for (List<Long> signParams : allParentIds) {
						List<Long> signResult = session.selectList("getAllMonirotedChildrenInstanceIdByParentIds",signParams);
						if(signResult!=null && signResult.size()>0){
							result.addAll(signResult);
						}
					}
				}
			}else{
				result = session.selectList("getAllMonirotedChildrenInstanceIdByParentIds",parentIds);
			}
		}
		return result;
	}
	@Override
	public List<Long> getAllNotMonirotedChildrenInstanceIdByParentIds(
			List<Long> parentIds) {
		List<Long> result = new ArrayList<Long>();
		if(parentIds!=null && parentIds.size()>0){
			//oracle in 查询参数最大支持1000，为避免出错，将parentIds分组，每组最多999个
			int signSize = 999;
			if(parentIds.size()>signSize){
				List<List<Long>> allParentIds = new ArrayList<List<Long>>();
				double parentIdSize = parentIds.size();
				double sheetCount=(double) (parentIdSize/signSize);
				int sc = (int) Math.ceil(sheetCount);
				for (int i = 0; i <sc; i++) {
					List<Long> signList = new ArrayList<>();
					if(i+1==sc){
						signList = parentIds.subList((int)(i*signSize), parentIds.size());
					}else{
						signList = parentIds.subList((int)(i*signSize),(int)((i+1)*signSize));
					}
					allParentIds.add(signList);
				}
				
				if(allParentIds!=null && allParentIds.size()>0){
					for (List<Long> signParams : allParentIds) {
						List<Long> signResult = session.selectList("getAllNotMonirotedChildrenInstanceIdByParentIds",signParams);
						if(signResult!=null && signResult.size()>0){
							result.addAll(signResult);
						}
					}
				}
			}else{
				result = session.selectList("getAllNotMonirotedChildrenInstanceIdByParentIds",parentIds);
			}
		}
		return result;
	}
	@Override
	public List<ResourceInstanceDO> getAllResourceInstanceForLink() {
		return session.selectList("getAllResourceInstanceForLink");
	}
}
