/**
 * 
 */
package com.mainsteam.stm.pluginsession;

/**
 * PluginSession回调接口
 * 
 * @author ziw
 *
 */
public interface PluginSessionCallback {
	/**
	 * 返回PluginSession的处理结果
	 * 
	 * @param resultSet
	 */
	public void returnPluginResultSet(PluginResultSet resultSet);
}
