package com.mainsteam.stm.ipmanage.api;

import java.util.List;

import com.mainsteam.stm.ipmanage.bo.NetGroup;



public interface NetGroupService {

	List<NetGroup> getNetGroupList();
	
	int insertNetGroup(NetGroup netGroup);
	
	int update(NetGroup netGroup);
	
	int delete(Integer id);
}
