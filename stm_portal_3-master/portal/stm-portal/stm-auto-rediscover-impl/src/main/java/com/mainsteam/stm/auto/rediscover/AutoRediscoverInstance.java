package com.mainsteam.stm.auto.rediscover;

import java.util.List;
import java.util.Map;

import com.mainsteam.stm.instancelib.exception.InstancelibException;
import com.mainsteam.stm.instancelib.objenum.InstanceLifeStateEnum;
import com.mainsteam.stm.profilelib.exception.ProfilelibException;

public interface AutoRediscoverInstance{

	Map<InstanceLifeStateEnum, List<Long>> reDiscover(long instanceId,boolean isAutoDeleteChildInstance) throws InstancelibException,ProfilelibException;
}
