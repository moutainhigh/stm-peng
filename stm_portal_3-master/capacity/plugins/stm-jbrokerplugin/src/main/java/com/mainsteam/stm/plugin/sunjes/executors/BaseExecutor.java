package com.mainsteam.stm.plugin.sunjes.executors;

import com.mainsteam.stm.plugin.sunjes.amx.DottedNames;
import com.mainsteam.stm.plugin.sunjes.util.SunJESConnInfo;

/**
 * 抽象执行器 <br>
 */
public class BaseExecutor {
	

	public SunJESConnInfo getConnInfo() {
		return m_connInfo;
	}

	public DottedNames getDottedNames() {
		return m_dottedNames;
	}
	
	
	/**
	 * <code>m_connInfo</code> - 连接信息
	 */
	protected SunJESConnInfo m_connInfo = null;

	/**
	 * <code>dottedNames</code> - DottedNames 点分割对象的名字
	 */
	protected DottedNames m_dottedNames = null;


}
