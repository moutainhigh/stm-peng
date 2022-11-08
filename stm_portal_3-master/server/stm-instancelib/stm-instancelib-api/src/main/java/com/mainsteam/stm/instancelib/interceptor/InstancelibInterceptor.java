package com.mainsteam.stm.instancelib.interceptor;

/**
 * 实例操作前置拦截（入库之前执行）
 * @author xiaoruqiang
 */
public interface InstancelibInterceptor {
	/**
	 * 拦截执行代码
	 * @param instancelibEvent 拦截的事件
	 */
	void interceptor(InstancelibEvent instancelibEvent) throws Exception;
}
