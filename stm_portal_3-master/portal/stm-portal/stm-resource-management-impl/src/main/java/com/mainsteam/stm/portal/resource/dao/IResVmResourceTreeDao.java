package com.mainsteam.stm.portal.resource.dao;

import java.util.List;

import com.mainsteam.stm.portal.resource.po.ResVmResourceTreePo;


public interface IResVmResourceTreeDao {
	public int insert(ResVmResourceTreePo vmTreePo);

	public ResVmResourceTreePo selectByUuid(String uuid);
	
	public List<ResVmResourceTreePo> selectListByUuids(List<String> uuids);
	
	public ResVmResourceTreePo selectByInstanceId(long instanceid);
	
	public ResVmResourceTreePo selectVCenterByInstanceId(long instanceid);
	
	public ResVmResourceTreePo selectDatacenterByInstanceId(long instanceid);
	
	public ResVmResourceTreePo selectClusterByInstanceId(long instanceid);
	
	public ResVmResourceTreePo selectHostByInstanceId(long instanceid);

	public List<ResVmResourceTreePo> selectChildrenByInstanceId(long instanceid);
	
	public List<ResVmResourceTreePo> selectAllChildrenByInstanceId(long instanceid);
	
	public int delByInstanceId(long instanceid);
	
	public int updateByUuid(ResVmResourceTreePo vmTreePo);

	public int updateByInstanceId(ResVmResourceTreePo vmTreePo);
}
