/**
 * 
 */
package com.mainsteam.stm.rpc.server;

import java.util.HashMap;
import java.util.Map;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.beans.BeansException;
import org.springframework.beans.factory.config.BeanPostProcessor;

/**
 * @author ziw
 * 
 */
public class JmxBeanBeanPostProcessors implements BeanPostProcessor {

	private static final Log logger = LogFactory
			.getLog(JmxBeanBeanPostProcessors.class);

	private Map<Class<?>, Object> mbeanObjMap = new HashMap<>();

	/**
	 * 
	 */
	public JmxBeanBeanPostProcessors() {
	}

	/**
	 * @return the mbeanObjMap
	 */
	public final Map<Class<?>, Object> getMbeanObjMap() {
		return mbeanObjMap;
	}

	public void clear() {
		if (mbeanObjMap != null) {
			mbeanObjMap.clear();
			mbeanObjMap = null;
		}
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.springframework.beans.factory.config.BeanPostProcessor#
	 * postProcessAfterInitialization(java.lang.Object, java.lang.String)
	 */
	@Override
	public Object postProcessAfterInitialization(Object bean, String beanName)
			throws BeansException {
		if (logger.isTraceEnabled()) {
			logger.trace("postProcessAfterInitialization start");
		}
		Class<?>[] interfaces = bean.getClass().getInterfaces();
		if (interfaces != null && interfaces.length > 0) {
			/**
			 * 查找MBean接口
			 */
			for (Class<?> f : interfaces) {
				if (f.getName().endsWith("MBean")
						&& f.getName().startsWith("com.mainsteam.stm")) {
					/**
					 * 准备注册MBean
					 */
					mbeanObjMap.put(f, bean);
					if (logger.isInfoEnabled()) {
						StringBuffer b = new StringBuffer();
						b.append("ready to register jmx bean beanId=");
						b.append(beanName);
						b.append(" beanClass=");
						b.append(bean.getClass());
						b.append(" mbean=");
						b.append(f);
						if (logger.isInfoEnabled()) {
							logger.info(b.toString());
						}
					}
				}
			}
		}
		if (logger.isTraceEnabled()) {
			logger.trace("postProcessAfterInitialization end");
		}
		return bean;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.springframework.beans.factory.config.BeanPostProcessor#
	 * postProcessBeforeInitialization(java.lang.Object, java.lang.String)
	 */
	@Override
	public Object postProcessBeforeInitialization(Object bean, String beanName)
			throws BeansException {
		return bean;
	}

}
