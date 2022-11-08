package com.mainsteam.stm.instancelib.interceptor;

/**
 * 管理事件拦截器，监听器
 * 接受并处理事件对象
 * @author xiaoruqiang
 */
public interface InstancelibEventManager {

	/**
	 * 注册事件监听器
	 */
	void register(InstancelibListener listener);
	
	/**
	 * 注册拦截器
	 */
	void register(InstancelibInterceptor interceptor);
	
	/**
	 * 执行拦截器
	 */
	void doInterceptor(InstancelibEvent instancelibEvent) throws Exception;
	
	/**
	 * 执行监听器
	 */
	void doListen(InstancelibEvent instancelibEvent) throws Exception;
}
