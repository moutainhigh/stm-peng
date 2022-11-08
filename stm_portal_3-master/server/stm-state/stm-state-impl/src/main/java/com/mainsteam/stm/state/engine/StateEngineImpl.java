package com.mainsteam.stm.state.engine;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.*;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.beans.BeansException;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.beans.factory.config.BeanPostProcessor;
import org.springframework.context.ApplicationEvent;
import org.springframework.context.ApplicationListener;
import org.springframework.context.event.ContextRefreshedEvent;
import com.mainsteam.stm.state.obj.InstanceStateChangeData;
import com.mainsteam.stm.state.obj.MetricStateChangeData;

public class StateEngineImpl implements StateEngine,InitializingBean,BeanPostProcessor,ApplicationListener<ApplicationEvent> {
	private static final Log logger = LogFactory.getLog(StateEngineImpl.class);

	LinkedBlockingQueue<MetricStateChangeData> dataQueue=new LinkedBlockingQueue<MetricStateChangeData>();
	LinkedBlockingQueue<InstanceStateChangeData> insDataQueue=new LinkedBlockingQueue<InstanceStateChangeData>();

	private final ExecutorService threadExecutor = new ThreadPoolExecutor(20,100,60L, TimeUnit.SECONDS,new LinkedBlockingQueue<Runnable>(),new ThreadFactory() {
		private int counter = 0;
		@Override
		public Thread newThread(Runnable runnable) {
			Thread t = new Thread(runnable,"StateEngine-"+ counter++);
			if (t.isDaemon())
				t.setDaemon(false);
			if (t.getPriority() != Thread.NORM_PRIORITY)
				t.setPriority(Thread.NORM_PRIORITY);
			return t;
		}
	});

	
	private final List<StateHandle> handles=new ArrayList<>();
	
	private volatile boolean runnable=false;
	
	@Override
	public void handleMetricStateChange(MetricStateChangeData state) {
		dataQueue.add(state);
	}

	@Override
	public void handleInstanceStateChange(final InstanceStateChangeData state) {
		insDataQueue.add(state);
	}

	@Override
	public Object postProcessBeforeInitialization(Object bean, String beanName)throws BeansException {
		return bean;
	}
	@Override
	public Object postProcessAfterInitialization(Object bean, String beanName) throws BeansException {
		if(bean instanceof StateHandle){
			if(logger.isInfoEnabled()) {
				logger.info("Initialization stateEngine " + beanName);
			}
			handles.add((StateHandle)bean);
		}
		return bean;
	}

	@Override
	public void afterPropertiesSet() throws Exception {
			//指标告警
			Thread th = new Thread(){
				@Override public void run() {
					while(true){
						try {
							if(!runnable){
								Thread.sleep(5000);
								continue;
							}
							final MetricStateChangeData state = dataQueue.take();
							for (final StateHandle handle : handles) {
								try {
									threadExecutor.execute(new Runnable() {
										@Override
										public void run() {
											handle.onMetricStateChange(state);
										}
									});

								} catch (Exception e) {
									logger.error("handleInstanceStateChange error:"+e.getMessage(),e);
								}
							}
						} catch (Exception e) {
							logger.error("handleMetricStateChange error:"+e.getMessage(),e);
						}
					}
				}
			};
			th.setName("stateEngineForMetricThead-");
			th.setDaemon(false);
			th.start();

			//资源告警
			Thread incThread=new Thread(){
				@Override public void run() {
					while(true){
						try {
							if(!runnable){
								Thread.sleep(5000);
								continue;
							}
							final InstanceStateChangeData state = insDataQueue.take();
							for (final StateHandle handle : handles) {
								try {
									threadExecutor.execute(new Runnable() {
										@Override
										public void run() {
											handle.onInstanceStateChange(state);
										}
									});
								} catch (Exception e) {
									logger.error("handleInstanceStateChange error:"+e.getMessage(),e);
								}
							}
						} catch (Exception e) {
							logger.error("handleInstanceStateChange error:"+e.getMessage(),e);
						}
					}
				}
			};
			incThread.setName("stateEngineForInstanceThead-");
			incThread.setDaemon(false);
			incThread.start();
	}

	@Override
	public void onApplicationEvent(ApplicationEvent event) {
		if(!runnable) {
			if (event instanceof ContextRefreshedEvent) {
				if(logger.isInfoEnabled()) {
					logger.info(event.getClass().getSimpleName() + ", it happened.");
				}
				runnable = true;
			}
		}
	}

	
}
