package com.mainsteam.stm.ipmanage.impl.dao;

import java.util.List;

import com.mainsteam.stm.ipmanage.bo.NetGroup;

public interface NetGroupMapper {
	
	List<NetGroup> getNetGroupList();
	
	int insertNetGroup(NetGroup netGroup);
	
	int update(NetGroup netGroup);
	
	int delete(Integer id);
}
