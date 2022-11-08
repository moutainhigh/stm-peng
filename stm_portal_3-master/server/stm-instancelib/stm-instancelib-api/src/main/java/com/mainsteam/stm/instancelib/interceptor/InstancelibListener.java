package com.mainsteam.stm.instancelib.interceptor;

import java.util.EventListener;

/**
 * 实例操作后置监听（数据入库成功后执行）
 * @author xiaoruqiang 
 */
public interface InstancelibListener extends EventListener {
	
	/**
	 * 后置监听执行功能
	 * @param instancelibEvent  监听事件
	 */
	void listen(InstancelibEvent instancelibEvent) throws Exception;
	
}
