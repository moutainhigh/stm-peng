package com.mainsteam.stm.profilelib.fault.obj;

import java.io.Serializable;

/**
 * <li>文件名称: ProfilelibFaultMetric</li>
 * <li>公　　司: 武汉美新翔盛科技有限公司</li>
 * <li>版权所有: 版权所有(C)2019-2020</li>
 * <li>修改记录: ...</li>
 * <li>内容摘要: 故障策略指标</li>
 * <li>其他说明: ...</li>
 * @version  ms.stm
 * @since    2019年6月23日 下午5:46:18
 * @author   zhangjunfeng
 */
public class ProfileFaultMetric implements Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = -6117188955641722925L;
	
	/**
	 * 策略ID
	 */
	private long profileId;
	/**
	 * 指标ID
	 */
	private String metricId;
	public long getProfileId() {
		return profileId;
	}
	public void setProfileId(long profileId) {
		this.profileId = profileId;
	}
	public String getMetricId() {
		return metricId;
	}
	public void setMetricId(String metricId) {
		this.metricId = metricId;
	}
	
	

}
