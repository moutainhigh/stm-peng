package com.mainsteam.stm.license.remote;

public interface RemoteLicenseMBean {
	
	/**
	 * 获取License 注册的采集器数量
	 * @return
	 */
	public int getDcsCount();
	
	/**
	 * 获取License 是否过期
	 * @return
	 */
	public boolean isOverTime();
}
