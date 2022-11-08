package com.mainsteam.stm.profilelib.fault.obj;

import java.io.Serializable;

/**
 * <li>文件名称: ProfilelibFaultInstance</li>
 * <li>公　　司: 武汉美新翔盛科技有限公司</li>
 * <li>版权所有: 版权所有(C)2019-2020</li>
 * <li>修改记录: ...</li>
 * <li>内容摘要: 策略资源实例</li>
 * <li>其他说明: ...</li>
 * @version  ms.stm
 * @since    2019年6月23日 下午5:16:23
 * @author   zhangjunfeng
 */
public class ProfileFaultInstance implements Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = 9048987574741504710L;

	/**
	 * 策略ID
	 */
	private long profileId;
	/**
	 * 资源实例ID
	 */
	private String instanceId;
	public long getProfileId() {
		return profileId;
	}
	public void setProfileId(long profileId) {
		this.profileId = profileId;
	}
	public String getInstanceId() {
		return instanceId;
	}
	public void setInstanceId(String instanceId) {
		this.instanceId = instanceId;
	}
	
	
	
}
