package com.mainsteam.stm.instancelib.interceptor;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
//import org.springframework.beans.BeansException;
import org.springframework.beans.factory.config.BeanPostProcessor;

public class InstancelibEventRegisterLoad implements  BeanPostProcessor{

	private static final Log logger = LogFactory.getLog(InstancelibEventRegisterLoad.class);
	
	//private static String INTERCEPTOR_PATH = "com.mainsteam.stm.instancelib.interceptor.InstancelibInterceptor";
	
	//private static String LISTENER_PATH = "com.mainsteam.stm.instancelib.interceptor.InstancelibListener";
	
	private InstancelibEventManager instancelibEventManager;

	public void setInstancelibEventManager(
			InstancelibEventManager instancelibEventManager) {
		this.instancelibEventManager = instancelibEventManager;
	}

	@Override
	public Object postProcessAfterInitialization(Object bean, String beanName)
			 {
		if (logger.isTraceEnabled()) {
			logger.trace("InstancelibEvent register start.");
		}
		/**
		 * 注册interceptor
		 */
		if(bean instanceof InstancelibInterceptor){
			InstancelibInterceptor obj = (InstancelibInterceptor)bean;
			instancelibEventManager.register(obj);
			if (logger.isDebugEnabled()) {
				String message = "register resource instance interceptor bean beanId=" + obj.getClass().getName();
				logger.debug(message);
			}
		}
		/**
		 * 注册listener
		 */
		if(bean instanceof InstancelibListener){
			InstancelibListener obj = (InstancelibListener)bean;
			instancelibEventManager.register(obj);
			if (logger.isDebugEnabled()) {
				String message = "register resource instance listener bean beanId=" + obj.getClass().getName();
				logger.debug(message);
			}
		}
		
//		Class<?> beanClass = bean.getClass();
//		//获取实现bean的所有实现接口
//		Class<?>[] interfaces = beanClass.getInterfaces();
//		if (interfaces != null) {
//			for (Class<?> interfaceClass : interfaces) {
//				if (INTERCEPTOR_PATH.equals(interfaceClass.getName())) {
//					/**
//					 * 注册interceptor
//					 */
//					InstancelibInterceptor obj = (InstancelibInterceptor)bean;
//					instancelibEventManager.register(obj);
//					if (logger.isDebugEnabled()) {
//						String message = "register resource instance interceptor bean beanId=" +beanClass.getName();
//						logger.debug(message);
//					}
//				}else if(LISTENER_PATH.equals(interfaceClass.getName())){
//					/**
//					 * 注册listener
//					 */
//					InstancelibListener obj = (InstancelibListener)bean;
//					instancelibEventManager.register(obj);
//					if (logger.isDebugEnabled()) {
//						String message = "register resource instance listener bean beanId=" +beanClass.getName();
//						logger.debug(message);
//					}
//				}
//			}
//		}
		if (logger.isTraceEnabled()) {
			logger.trace("InstancelibEvent register end.");
		}
		return bean;
	}


	@Override
	public Object postProcessBeforeInitialization(Object arg0, String arg1){
		return arg0;
	}

}
