package com.mainsteam.stm.topo.dao;

import java.util.List;

import com.mainsteam.stm.topo.bo.VlanBo;

public interface VlanDao {

	void saveVlan(VlanBo vb);

	List<VlanBo> getVlanForNodeBo(Long queryId);

}
