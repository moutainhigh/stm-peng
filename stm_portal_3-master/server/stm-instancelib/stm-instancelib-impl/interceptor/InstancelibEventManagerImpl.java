package com.mainsteam.stm.instancelib.interceptor;

import java.util.ArrayList;
import java.util.List;

/**
 * 管理事件拦截器，监听器 接受并处理事件对象
 * 
 * @author xiaoruqiang
 */
public class InstancelibEventManagerImpl  implements InstancelibEventManager {

	private List<InstancelibListener> instancelibListenerList;

	private List<InstancelibInterceptor> instancelibInterceptorList;

	public InstancelibEventManagerImpl() {
		instancelibListenerList = new ArrayList<>();
		instancelibInterceptorList = new ArrayList<>();
	}

	@Override
	public void register(InstancelibListener listener) {
		if (listener != null) {
			instancelibListenerList.add(listener);
		}
	}

	@Override
	public void register(InstancelibInterceptor interceptor) {
		if (interceptor != null) {
			instancelibInterceptorList.add(interceptor);
		}
	}

	@Override
	public void doInterceptor(InstancelibEvent instancelibEvent) throws Exception {
		for (InstancelibInterceptor instancelibInterceptor : instancelibInterceptorList) {
			instancelibInterceptor.interceptor(instancelibEvent);
		}
	}

	@Override
	public void doListen(InstancelibEvent instancelibEvent) throws Exception {
		for (InstancelibListener instancelibListener : instancelibListenerList) {
			instancelibListener.listen(instancelibEvent);
		}
	}
}
