/**
 * 
 */
package com.mainsteam.stm.profilelib.deploy;

import com.mainsteam.stm.profilelib.deploy.obj.ProfileDeployInfo;

/**
 * 采集器策略部署接口，用来修改对资源实例指标的调度设置进行修改，运行在DCS
 * 
 * @author ziw
 * 
 */
public interface ProfileCollectDeployMBean {
	/**
	 * 部署策略信息
	 * 
	 * @param deployInfos
	 */
	public void deployProfileInfo(ProfileDeployInfo[] deployInfos);
}
