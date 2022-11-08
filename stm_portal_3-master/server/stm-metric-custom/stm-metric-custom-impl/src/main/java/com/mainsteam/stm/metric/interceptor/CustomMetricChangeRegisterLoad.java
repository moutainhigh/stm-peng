package com.mainsteam.stm.metric.interceptor;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.beans.BeansException;
import org.springframework.beans.factory.config.BeanPostProcessor;

public class CustomMetricChangeRegisterLoad implements BeanPostProcessor {

	private static final Log logger = LogFactory.getLog(CustomMetricChangeRegisterLoad.class);
	
	private CustomMetricChangeManager customMetricAlarmManager;
	
	private CustomMetricChangeManager customMetricMonitorManager;
	
	private CustomMetricChangeManager customResourceCancelManager;
	
	public void setCustomResourceCancelManager(
			CustomMetricChangeManager customResourceCancelManager) {
		this.customResourceCancelManager = customResourceCancelManager;
	}

	public void setCustomMetricAlarmManager(
			CustomMetricChangeManager customMetricAlarmManager) {
		this.customMetricAlarmManager = customMetricAlarmManager;
	}

	public void setCustomMetricMonitorManager(
			CustomMetricChangeManager customMetricMonitorManager) {
		this.customMetricMonitorManager = customMetricMonitorManager;
	}


	@Override
	public Object postProcessAfterInitialization(Object bean, String beanName)
			throws BeansException {
		if (logger.isTraceEnabled()) {
			logger.trace("ProfileChangeManager register start.");
		}
		if(bean instanceof CustomMetricAlarmChange){
			CustomMetricAlarmChange obj = (CustomMetricAlarmChange)bean;
			customMetricAlarmManager.register(obj);
			if (logger.isInfoEnabled()) {
				String message = "register CustomMetricAlarmChange interceptor bean beanId=" +obj.getClass().getName();
				logger.info(message);
			}
		}
		if(bean instanceof CustomMetricMonitorChange){
			CustomMetricMonitorChange obj = (CustomMetricMonitorChange)bean;
			customMetricMonitorManager.register(obj);
			if (logger.isInfoEnabled()) {
				String message = "register CustomMetricMonitorChange interceptor bean beanId=" +obj.getClass().getName();
				logger.info(message);
			}
		}
		if(bean instanceof CustomResourceMonitorChange){
			CustomResourceMonitorChange obj = (CustomResourceMonitorChange)bean;
			customResourceCancelManager.register(obj);
			if (logger.isInfoEnabled()) {
				String message = "register CustomResourceMonitorChange interceptor bean beanId=" +obj.getClass().getName();
				logger.info(message);
			}
		}
		if (logger.isTraceEnabled()) {
			logger.trace("ProfileChangeManager register end.");
		}
		return bean;
	}


	@Override
	public Object postProcessBeforeInitialization(Object arg0, String arg1)
			throws BeansException {
		return arg0;
	}

}
