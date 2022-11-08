package com.mainsteam.stm.ipmanage.impl;

import java.util.List;

import javax.annotation.Resource;

import org.apache.log4j.Logger;

import com.mainsteam.stm.ipmanage.api.NetGroupService;
import com.mainsteam.stm.ipmanage.bo.NetGroup;
import com.mainsteam.stm.ipmanage.impl.dao.NetGroupMapper;

public class NetGroupServiceImpl implements NetGroupService {
	Logger logger = Logger.getLogger(NetGroupServiceImpl.class);

	@Resource
	private NetGroupMapper netGroupMapper;
	@Override
	public List<NetGroup> getNetGroupList() {
		// TODO Auto-generated method stub
		return netGroupMapper.getNetGroupList();
	}

	@Override
	public int insertNetGroup(NetGroup netGroup) {
		// TODO Auto-generated method stub
		return netGroupMapper.insertNetGroup(netGroup);
	}

	@Override
	public int update(NetGroup netGroup) {
		// TODO Auto-generated method stub
		return netGroupMapper.update(netGroup);
	}

	@Override
	public int delete(Integer id) {
		// TODO Auto-generated method stub
		return netGroupMapper.delete(id);
	}
	
}
