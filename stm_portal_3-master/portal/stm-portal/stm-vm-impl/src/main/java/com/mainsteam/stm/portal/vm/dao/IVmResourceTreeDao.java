package com.mainsteam.stm.portal.vm.dao;

import java.util.List;

import com.mainsteam.stm.platform.mybatis.plugin.pagination.Page;
import com.mainsteam.stm.portal.vm.bo.VmResourceBo;
import com.mainsteam.stm.portal.vm.po.VmResourceTreePo;

public interface IVmResourceTreeDao {
	public int insert(VmResourceTreePo vmTreePo);

	public VmResourceTreePo selectByUuid(String uuid);
	
	public List<VmResourceTreePo> selectByPuuid(String puuid);
	
	public VmResourceTreePo selectByInstanceId(long instanceid);
	
	public VmResourceTreePo selectVCenterByInstanceId(long instanceid);
	
	public VmResourceTreePo selectDatacenterByInstanceId(long instanceid);
	
	public VmResourceTreePo selectClusterByInstanceId(long instanceid);
	
	public VmResourceTreePo selectHostByInstanceId(long instanceid);

	public List<VmResourceTreePo> selectRelationIdsByInstanceId(List<Long> instanceids);
	
	public List<VmResourceTreePo> selectChildrenByInstanceId(long instanceid);
	
	public List<VmResourceTreePo> selectAllChildrenByInstanceId(long instanceid);
	
	public int delByInstanceId(long instanceid);
	
	public int updateByUuid(VmResourceTreePo vmTreePo);

	public int updateByInstanceId(VmResourceTreePo vmTreePo);
	
	public void getVMTreeListByPage(Page<VmResourceTreePo, VmResourceBo> page);
	
	public List<VmResourceTreePo> getVMTreeListAll(VmResourceBo condition);
	
	public List<VmResourceTreePo> getResourcePoolVM(String poolUuid);
	
	public void getResourcePoolVMByPage(Page<VmResourceTreePo, VmResourceBo> page);
	
	public int delResourcePoolByUuid(String uuid);
	/**
	 * 查询所有topology左侧导航栏树(vecenter__datacenter)
	 * @return
	 */
	public List<VmResourceTreePo> getLeftNavigateTree();
	/**
	 * 查询当前数据中心topology关系list数据(根据数据中心vmfullname)
	 * @param vmfullname
	 * @return
	 */
	public List<VmResourceTreePo> getTopologyData(VmResourceTreePo po);
	
	public int delByUuid(String uuid);
	
	public int delByPuuid(String puuid);
	
	public List<VmResourceTreePo> selectByResourceId(List<String> resourceIds);
}
