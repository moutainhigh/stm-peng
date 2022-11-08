package com.mainsteam.stm.portal.resource.dao.impl;

import java.util.List;

import org.mybatis.spring.SqlSessionTemplate;

import com.mainsteam.stm.platform.dao.BaseDao;
import com.mainsteam.stm.portal.resource.dao.IResVmResourceTreeDao;
import com.mainsteam.stm.portal.resource.po.ResVmResourceTreePo;


public class ResVmResourceTreeDaoImpl extends BaseDao<ResVmResourceTreePo> implements IResVmResourceTreeDao {

	public ResVmResourceTreeDaoImpl(SqlSessionTemplate session) {
		super(session, IResVmResourceTreeDao.class.getName());
	}

	@Override
	public int insert(ResVmResourceTreePo vmTreePo) {
		return super.insert(vmTreePo);
	}

	@Override
	public ResVmResourceTreePo selectByUuid(String uuid) {
		return getSession().selectOne(getNamespace() + "selectByUuid", uuid);
	}

	@Override
	public List<ResVmResourceTreePo> selectListByUuids(List<String> uuids) {
		return getSession().selectList(getNamespace() + "selectListByUuids", uuids);
	}
	
	@Override
	public ResVmResourceTreePo selectByInstanceId(long instanceid) {
		return getSession().selectOne(getNamespace() + "selectByInstanceId", instanceid);
	}

	@Override
	public ResVmResourceTreePo selectVCenterByInstanceId(long instanceid) {
		return getSession().selectOne(getNamespace() + "selectVCenterByInstanceId", instanceid);
	}

	@Override
	public ResVmResourceTreePo selectDatacenterByInstanceId(long instanceid) {
		return getSession().selectOne(getNamespace() + "selectDatacenterByInstanceId", instanceid);
	}

	@Override
	public ResVmResourceTreePo selectClusterByInstanceId(long instanceid) {
		return getSession().selectOne(getNamespace() + "selectClusterByInstanceId", instanceid);
	}

	@Override
	public ResVmResourceTreePo selectHostByInstanceId(long instanceid) {
		return getSession().selectOne(getNamespace() + "selectHostByInstanceId", instanceid);
	}

	@Override
	public List<ResVmResourceTreePo> selectChildrenByInstanceId(long instanceid) {
		return getSession().selectList(getNamespace() + "selectChildrenByInstanceId", instanceid);
	}
	
	@Override
	public int delByInstanceId(long instanceid) {
		return getSession().delete(getNamespace() + "delByInstanceId", instanceid);
	}

	@Override
	public int updateByUuid(ResVmResourceTreePo vmTreePo) {
		return getSession().update(getNamespace() + "updateByUuid", vmTreePo);
	}

	@Override
	public int updateByInstanceId(ResVmResourceTreePo vmTreePo) {
		return getSession().update(getNamespace() + "updateByInstanceId", vmTreePo);
	}
	
	@Override
	public List<ResVmResourceTreePo> selectAllChildrenByInstanceId(long instanceid){
		return getSession().selectList(getNamespace() + "selectAllChildrenByInstanceId", instanceid);
	}

	
}
