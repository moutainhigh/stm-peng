/**
 * 
 */
package com.mainsteam.stm.pluginsession;

import java.util.Date;

/**
 * Pluingsession执行的上下文，针对同一个plugin session在同一个初始化参数下的运行提供执行信息存放和查询
 * 
 * @author ziw
 * 
 */
public interface PluginSessionContext {
	
	/**
	 * 获取模型id
	 * 
	 * @return
	 */
	public String getResourceId();
	
	/**
	 * 获取资源实例id
	 * 
	 * @return
	 */
	public long getResourceInstanceId();
	
	/**
	 * 获取指标id
	 * 
	 * @return
	 */
	public String getMetricId();
	
	/**
	 * 获取指标采值时间
	 * 
	 * @return
	 */
	public Date getMetricCollectTime();
	
	/**
	 * 设置运行时的值
	 * 
	 * @param key
	 * @param value
	 */
	public void setRuntimeValue(String key,Object value);
	
	/**
	 * 获取运行时的值
	 * 
	 * @param key 
	 * @return
	 */
	public Object getRuntimeValue(String key);
	
	/**
	 * 设置线程相关的运行时值
	 * 
	 * @param key
	 * @param value
	 */
	public void setThreadLocalRuntimeValue(String key,Object value);
	
	/**
	 * 获取线程相关的运行时值
	 * 
	 * @param key 
	 * @return
	 */
	public Object getThreadLocalRuntimeValue(String key);
}
