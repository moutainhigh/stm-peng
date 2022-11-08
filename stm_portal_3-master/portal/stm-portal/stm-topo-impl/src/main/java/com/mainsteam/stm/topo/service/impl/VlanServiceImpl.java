package com.mainsteam.stm.topo.service.impl;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.Assert;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.mainsteam.stm.topo.api.VlanService;
import com.mainsteam.stm.topo.bo.NodeBo;
import com.mainsteam.stm.topo.bo.VlanBo;
import com.mainsteam.stm.topo.dao.INodeDao;
import com.mainsteam.stm.topo.dao.VlanDao;
@Service
public class VlanServiceImpl implements VlanService{
	@Autowired
	private VlanDao vlanDao;
	@Autowired
	private INodeDao nodeDao;

	@Override
	public JSONArray getVlanForNodeBo(Long nodeId) {
		NodeBo node = nodeDao.getById(nodeId);
		Assert.notNull(node);
		Long queryId = nodeId;
		if(node.getParentId()!=null){
			queryId=node.getParentId();
		}
		List<VlanBo> vlanBos = vlanDao.getVlanForNodeBo(queryId);
		return (JSONArray) JSON.toJSON(vlanBos);
	}
}
