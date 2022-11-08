package com.mainsteam.stm.instancelib.service;

import java.util.List;

import com.mainsteam.stm.instancelib.dao.pojo.PropDO;
import com.mainsteam.stm.instancelib.obj.InstanceProp;

public interface PropService {
	
	public List<PropDO> convertToDOs(InstanceProp prop);
	
}
