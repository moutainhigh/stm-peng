package com.mainsteam.stm.portal.vm.dao.impl;

import java.util.List;

import org.mybatis.spring.SqlSessionTemplate;

import com.mainsteam.stm.platform.dao.BaseDao;
import com.mainsteam.stm.platform.mybatis.plugin.pagination.Page;
import com.mainsteam.stm.portal.vm.bo.VmResourceBo;
import com.mainsteam.stm.portal.vm.dao.IVmResourceTreeDao;
import com.mainsteam.stm.portal.vm.po.VmResourceTreePo;


public class VmResourceTreeDaoImpl extends BaseDao<VmResourceTreePo> implements IVmResourceTreeDao {

	public VmResourceTreeDaoImpl(SqlSessionTemplate session) {
		super(session, IVmResourceTreeDao.class.getName());
	}

	@Override
	public int insert(VmResourceTreePo vmTreePo) {
		return super.insert(vmTreePo);
	}

	@Override
	public VmResourceTreePo selectByUuid(String uuid) {
		return getSession().selectOne(getNamespace() + "selectByUuid", uuid);
	}
	
	@Override
	public int delResourcePoolByUuid(String uuid){
		return getSession().delete(getNamespace() + "delResourcePoolByUuid", uuid);
	}
	
	@Override
	public List<VmResourceTreePo> selectByPuuid(String puuid) {
		return getSession().selectList(getNamespace() + "selectByPuuid", puuid);
	}
	
	@Override
	public VmResourceTreePo selectByInstanceId(long instanceid) {
		return getSession().selectOne(getNamespace() + "selectByInstanceId", instanceid);
	}

	@Override
	public VmResourceTreePo selectVCenterByInstanceId(long instanceid) {
		return getSession().selectOne(getNamespace() + "selectVCenterByInstanceId", instanceid);
	}

	@Override
	public VmResourceTreePo selectDatacenterByInstanceId(long instanceid) {
		return getSession().selectOne(getNamespace() + "selectDatacenterByInstanceId", instanceid);
	}

	@Override
	public VmResourceTreePo selectClusterByInstanceId(long instanceid) {
		return getSession().selectOne(getNamespace() + "selectClusterByInstanceId", instanceid);
	}

	@Override
	public VmResourceTreePo selectHostByInstanceId(long instanceid) {
		return getSession().selectOne(getNamespace() + "selectHostByInstanceId", instanceid);
	}

	@Override
	public List<VmResourceTreePo> selectChildrenByInstanceId(long instanceid) {
		return getSession().selectList(getNamespace() + "selectChildrenByInstanceId", instanceid);
	}
	
	@Override
	public int delByInstanceId(long instanceid) {
		return getSession().delete(getNamespace() + "delByInstanceId", instanceid);
	}

	@Override
	public int updateByUuid(VmResourceTreePo vmTreePo) {
		return getSession().update(getNamespace() + "updateByUuid", vmTreePo);
	}

	@Override
	public int updateByInstanceId(VmResourceTreePo vmTreePo) {
		return getSession().update(getNamespace() + "updateByInstanceId", vmTreePo);
	}
	
	@Override
	public List<VmResourceTreePo> selectAllChildrenByInstanceId(long instanceid){
		return getSession().selectList(getNamespace() + "selectAllChildrenByInstanceId", instanceid);
	}

	@Override
	public List<VmResourceTreePo> getLeftNavigateTree() {
		return select("getLeftNavigateTree", null);
	}

	@Override
	public List<VmResourceTreePo> getTopologyData(VmResourceTreePo po) {
		return getSession().selectList(getNamespace() + "selectTopologyData", po);
	}

	@Override
	public List<VmResourceTreePo> selectRelationIdsByInstanceId(List<Long> instanceids) {
		return getSession().selectList(getNamespace() + "selectRelationIdsByInstanceId", instanceids);
	}

	@Override
	public int delByUuid(String uuid) {
		return getSession().delete(getNamespace() + "delByUuid", uuid);
	}

	@Override
	public int delByPuuid(String puuid) {
		return getSession().delete(getNamespace() + "delByPuuid", puuid);
	}
	
	@Override
	public void getVMTreeListByPage(Page<VmResourceTreePo, VmResourceBo> page){
		this.select("pageSelect", page);
	}
	
	@Override
	public List<VmResourceTreePo> getVMTreeListAll(VmResourceBo condition){
		return getSession().selectList(getNamespace() + "selectAll", condition);
	}
	
	@Override
	public List<VmResourceTreePo> getResourcePoolVM(String poolUuid){
		return getSession().selectList(getNamespace() + "getResourcePoolVM", poolUuid);
	}
	
	@Override
	public void getResourcePoolVMByPage(Page<VmResourceTreePo, VmResourceBo> page){
		this.select("getResourcePoolVMByPage", page);
	}

	@Override
	public List<VmResourceTreePo> selectByResourceId(List<String> resourceIds) {
		return getSession().selectList(getNamespace() + "selectByResourceId", resourceIds);
	}
	
}
