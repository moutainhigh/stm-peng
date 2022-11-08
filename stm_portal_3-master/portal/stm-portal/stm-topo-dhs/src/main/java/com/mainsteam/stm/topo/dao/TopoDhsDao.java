package com.mainsteam.stm.topo.dao;

import java.util.List;

import com.mainsteam.stm.topo.bo.OtherNodeBo;
import com.mainsteam.stm.topo.bo.SubTopoBo;

public interface TopoDhsDao {

	List<OtherNodeBo> getCabinetByInstanceId(Long id);

	SubTopoBo getSubTopoById(Long subTopoId);

}
