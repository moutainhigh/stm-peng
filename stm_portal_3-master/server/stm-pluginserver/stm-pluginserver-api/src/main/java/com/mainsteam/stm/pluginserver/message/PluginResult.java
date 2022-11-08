/**
 * 
 */
package com.mainsteam.stm.pluginserver.message;

import com.mainsteam.stm.exception.BaseException;
import com.mainsteam.stm.pluginprocessor.ResultSet;

/**
 * plugin执行完毕后的响应数据
 * 
 * @author ziw
 * 
 */
public interface PluginResult {
	/**
	 * 获取响应数据
	 * 
	 * @return
	 */
	public ResultSet getResultData();

	/**
	 * 获取请求的id
	 * 
	 * @return
	 */
	public long getRequestId();

	/**
	 * 获取响应的结果状态码
	 * 
	 * @return
	 */
	public int getResultCode();

	/**
	 * 获取失败的原因
	 * 
	 * @return
	 */
	public BaseException getCause();
}
