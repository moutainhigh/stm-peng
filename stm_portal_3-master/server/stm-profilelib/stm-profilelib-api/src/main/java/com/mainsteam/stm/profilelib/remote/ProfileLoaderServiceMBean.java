/**
 * 
 */
package com.mainsteam.stm.profilelib.remote;

import com.mainsteam.stm.profilelib.obj.MonitorProfile;

/**
 * 
 * 策略加载服务，运行在DHS
 * 
 * @author ziw
 * 
 */
public interface ProfileLoaderServiceMBean {
	/**
	 * 加载和指定节点相关的策略配置信息
	 * 
	 * @param nodeGroupId
	 *            节点组id
	 * @return MonitorProfile[] 策略列表
	 */
	public MonitorProfile[] loadProfile(int nodeGroupId);

	/**
	 * 加载Profile的详细信息
	 * 
	 * @param profileId
	 *            指定的策略id
	 * @return MonitorProfile[] 策略列表
	 */
	public MonitorProfile[] loadProfileByProfileId(long[] profileId,int nodeGroupId);
}
