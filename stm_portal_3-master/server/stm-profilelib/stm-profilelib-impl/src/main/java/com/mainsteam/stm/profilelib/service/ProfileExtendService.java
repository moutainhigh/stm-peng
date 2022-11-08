package com.mainsteam.stm.profilelib.service;

import java.util.HashSet;
import java.util.List;

import com.mainsteam.stm.instancelib.obj.ResourceInstance;
import com.mainsteam.stm.profilelib.exception.ProfilelibException;

public interface ProfileExtendService {
	/**
	 * 删除资源实例，需要删除绑定关系
	 * @param instanceIds 父资源实例Id
	 * @throws ProfilelibException
	 */
	public void deleteInstances(List<ResourceInstance> instances) throws ProfilelibException;
	
	
	/**
	 * 物理删除策略相关信息，以及策略与资源绑定关系
	 * @param instanceIds 父资源实例Id
	 * @throws ProfilelibException
	 */
	public void deleteProfileByResourceIdAndInstanceId(List<Long> instanceIds,HashSet<String> resourceIds) throws ProfilelibException;
}
