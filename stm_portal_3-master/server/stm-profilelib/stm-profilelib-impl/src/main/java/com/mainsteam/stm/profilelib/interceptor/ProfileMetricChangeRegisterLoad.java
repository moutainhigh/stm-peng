package com.mainsteam.stm.profilelib.interceptor;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.beans.BeansException;
import org.springframework.beans.factory.config.BeanPostProcessor;

public class ProfileMetricChangeRegisterLoad implements BeanPostProcessor {

	private static final Log logger = LogFactory.getLog(ProfileMetricChangeRegisterLoad.class);
	
    private ProfileChangeManager profileMetricAlarmManager;
	
	private ProfileChangeManager profileMetricMonitorManager;
	
	private ProfileChangeManager profileResourceMonitorManager;
	
	private ProfileChangeManager profileSwitchChangeManager;

	public void setProfileMetricAlarmManager(
			ProfileChangeManager profileMetricAlarmManager) {
		this.profileMetricAlarmManager = profileMetricAlarmManager;
	}

	public void setProfileMetricMonitorManager(
			ProfileChangeManager profileMetricMonitorManager) {
		this.profileMetricMonitorManager = profileMetricMonitorManager;
	}

	public void setProfileResourceMonitorManager(
			ProfileChangeManager profileResourceMonitorManager) {
		this.profileResourceMonitorManager = profileResourceMonitorManager;
	}
	
	public ProfileChangeManager getProfileSwitchChangeManager() {
		return profileSwitchChangeManager;
	}

	public void setProfileSwitchChangeManager(
			ProfileChangeManager profileSwitchChangeManager) {
		this.profileSwitchChangeManager = profileSwitchChangeManager;
	}

	@Override
	public Object postProcessAfterInitialization(Object bean, String beanName)
			throws BeansException {
		if (logger.isTraceEnabled()) {
			logger.trace("ProfileChangeManager register start.");
		}
		
		//不使用if else, 使用者有可能一个实现类实现了多少个接口
		if(bean instanceof ProfileMetricAlarmChange){
			ProfileMetricAlarmChange obj = (ProfileMetricAlarmChange)bean;
			profileMetricAlarmManager.register(obj);
			if (logger.isInfoEnabled()) {
				String message = "register ProfileMetricAlarmChange interceptor bean beanId=" +obj.getClass().getName();
				logger.info(message);
			}
		}
		
	    if(bean instanceof ProfileMetricMonitorChange){
	    	ProfileMetricMonitorChange obj = (ProfileMetricMonitorChange)bean;
	    	profileMetricMonitorManager.register(obj);
	    	if (logger.isInfoEnabled()) {
				String message = "register ProfileMetricMonitorChange interceptor bean beanId=" +obj.getClass().getName();
				logger.info(message);
			}
		}
	    
	    if(bean instanceof ResourceMonitorChange){
	    	ResourceMonitorChange obj = (ResourceMonitorChange)bean;
	    	profileResourceMonitorManager.register(obj);
	    	if (logger.isInfoEnabled()) {
				String message = "register ResourceMonitorChange interceptor bean beanId=" +obj.getClass().getName();
				logger.info(message);
			}
		}
	    
	    if(bean instanceof ProfileSwitchChange){
	    	ProfileSwitchChange obj = (ProfileSwitchChange)bean;
	    	profileSwitchChangeManager.register(obj);
	    	if (logger.isInfoEnabled()) {
				String message = "register ProfileSwitchChange interceptor bean beanId=" +obj.getClass().getName();
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
