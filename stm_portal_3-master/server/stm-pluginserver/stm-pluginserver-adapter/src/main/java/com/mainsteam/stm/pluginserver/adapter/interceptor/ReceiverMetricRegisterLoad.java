package com.mainsteam.stm.pluginserver.adapter.interceptor;

import org.springframework.beans.BeansException;
import org.springframework.beans.factory.config.BeanPostProcessor;

public class ReceiverMetricRegisterLoad implements BeanPostProcessor {
	
	private static String path = "com.mainsteam.stm.pluginserver.adapter.interceptor.ReceiverMetricData"; 
	
	private MetricDataManager metricDataManager;
	
	public void setMetricDataManager(MetricDataManager metricDataManager) {
		this.metricDataManager = metricDataManager;
	}

	@Override
	public Object postProcessAfterInitialization(Object bean, String beanName)
			throws BeansException {
		Class<?> beanClass = bean.getClass();
		//获取实现bean的所有实现接口
		Class<?>[] interfaces = beanClass.getInterfaces();
		if (interfaces != null) {
			for (Class<?> interfaceClass : interfaces) {
				if (path.equals(interfaceClass.getName())) {
					ReceiverMetricData obj = (ReceiverMetricData)bean;
					metricDataManager.register(obj);
				}
			}
		}
		return bean;
	}

	@Override
	public Object postProcessBeforeInitialization(Object arg0, String arg1)
			throws BeansException {
		return arg0;
	}

}
