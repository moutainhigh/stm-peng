package com.mainsteam.stm.instancelib.dao.impl;

import java.util.List;

import org.apache.ibatis.session.SqlSession;

import com.mainsteam.stm.instancelib.dao.CompositeInstanceDAO;
import com.mainsteam.stm.instancelib.dao.pojo.CompositeInstanceDO;

/**
 * 复合实例数据库操作实现类
 * @author xiaoruqiang
 *
 */
public class CompositeInstanceDAOImpl implements CompositeInstanceDAO {

	private SqlSession session;
	
	public void setSession(SqlSession session) {
		this.session = session;
	}
	
	@Override
	public CompositeInstanceDO getCompositeInstanceById(long instanceId) throws Exception {
		return session.selectOne("com.mainsteam.stm.instancelib.dao.CompositeInstanceDAO.getCompositeInstanceById",instanceId);
	}
	
	@Override
	public List<CompositeInstanceDO> getCompositeInstanceByInstanceType(String type)throws Exception {
		return session.selectList("com.mainsteam.stm.instancelib.dao.CompositeInstanceDAO.getCompositeInstanceByInstanceType", type);
	}

	@Override
	public List<CompositeInstanceDO> getCompositeInstanceLikeName(String name) throws Exception{
	    return session.selectList("com.mainsteam.stm.instancelib.dao.CompositeInstanceDAO.getCompositeInstanceLikeName", name);
	}

	@Override
	public void insertCompositeInstances(List<CompositeInstanceDO> instances)throws Exception {
		for(CompositeInstanceDO instance : instances){
			session.insert("com.mainsteam.stm.instancelib.dao.CompositeInstanceDAO.insertCompositeInstance", instance);
		}
	}

	@Override
	public void insertCompositeInstance(CompositeInstanceDO instance) throws Exception{
		session.insert("com.mainsteam.stm.instancelib.dao.CompositeInstanceDAO.insertCompositeInstance", instance);
	}

	@Override
	public void updateCompositeInstances(List<CompositeInstanceDO> instances) throws Exception{
		for(CompositeInstanceDO instance : instances){
			session.update("com.mainsteam.stm.instancelib.dao.CompositeInstanceDAO.updateCompositeInstance", instance);
		}
	}

	@Override
	public void updateCompositeInstance(CompositeInstanceDO instance) throws Exception{
		session.update("com.mainsteam.stm.instancelib.dao.CompositeInstanceDAO.updateCompositeInstance", instance);
	}

	@Override
	public int removeCompositeInstanceById(long instanceId) throws Exception{
		return session.delete("com.mainsteam.stm.instancelib.dao.CompositeInstanceDAO.removeCompositeInstanceById", instanceId);
	}

	@Override
	public List<CompositeInstanceDO> getCompositeInstanceByContainInstanceId(long containInstanceId) throws Exception {
		// TODO Auto-generated method stub
		return session.selectList("com.mainsteam.stm.instancelib.dao.CompositeInstanceDAO.getCompositeInstanceByContainInstanceId",containInstanceId);
	}

	

}
