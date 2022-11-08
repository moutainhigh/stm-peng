package com.mainsteam.stm.portal.resource.dao;

import java.util.List;

import com.mainsteam.stm.portal.resource.bo.Wbh4HomeBo;

public interface ICustomResGroupDao {
	
	public List<Wbh4HomeBo> getWbh4HomeLikeGroupId(long groupId);
	
	public int updateWbh4HomeSelfExtByPrimary(Wbh4HomeBo wbh4Home);
}
