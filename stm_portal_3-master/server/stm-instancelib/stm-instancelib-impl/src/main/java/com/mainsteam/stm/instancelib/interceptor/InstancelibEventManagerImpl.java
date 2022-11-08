package com.mainsteam.stm.instancelib.interceptor;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.ThreadFactory;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

/**
 * 管理事件拦截器，监听器 接受并处理事件对象
 * 
 * @author xiaoruqiang
 */
public class InstancelibEventManagerImpl implements InstancelibEventManager {

	private static final Log logger = LogFactory
			.getLog(InstancelibEventManager.class);

	private List<InstancelibListener> instancelibListenerList;

	private List<InstancelibInterceptor> instancelibInterceptorList;

	private ExecutorService threadPoolTaskExecutor;

	public InstancelibEventManagerImpl() {
		instancelibListenerList = new ArrayList<>();
		instancelibInterceptorList = new ArrayList<>();
		threadPoolTaskExecutor = Executors.newFixedThreadPool(20,
				new ThreadFactory() {
					private int counter = 0;

					@Override
					public Thread newThread(Runnable r) {
						return new Thread(r,"InstancelibEvent-Dispatcher-"
								+ counter++);
					}
				});
	}

	@Override
	public void register(InstancelibListener listener) {
		if (listener != null) {
			instancelibListenerList.add(listener);
			if(logger.isDebugEnabled()){
				logger.debug("listener load=" + listener.getClass().getName());
			}
		}
	}

	@Override
	public void register(InstancelibInterceptor interceptor) {
		if (interceptor != null) {
			instancelibInterceptorList.add(interceptor);
			if(logger.isDebugEnabled()){
				logger.debug("interceptor load=" + interceptor.getClass().getName());
			}
		}
	}

	@Override
	public void doInterceptor(InstancelibEvent instancelibEvent)
			throws Exception {
		for (InstancelibInterceptor instancelibInterceptor : instancelibInterceptorList) {
			instancelibInterceptor.interceptor(instancelibEvent);
		}
	}

	@Override
	public void doListen(final InstancelibEvent instancelibEvent)
			throws Exception {
		for (final InstancelibListener instancelibListener : instancelibListenerList) {
			threadPoolTaskExecutor.execute(new Runnable() {
				@Override
				public void run() {
					if(logger.isDebugEnabled()){
						logger.debug("instance listen has execute,class=" + instancelibListener.getClass().getName());
					}
					try {
						instancelibListener.listen(instancelibEvent);
					} catch (Exception e) {
						if (logger.isErrorEnabled()) {
							logger.error("instance listen error!", e);
						}
					}
				}
			});
		}
	}
}
