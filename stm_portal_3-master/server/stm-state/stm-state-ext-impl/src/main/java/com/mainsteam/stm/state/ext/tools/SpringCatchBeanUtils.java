package com.mainsteam.stm.state.ext.tools;

import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;
import org.springframework.context.ApplicationEvent;
import org.springframework.context.ApplicationListener;
import org.springframework.context.event.ContextClosedEvent;
import org.springframework.context.event.ContextStartedEvent;
import org.springframework.context.event.ContextStoppedEvent;
import org.springframework.stereotype.Component;

import java.util.Map;

/**
 * Created by Xiaopf on 2017/7/11.
 */
@Deprecated
//@Component("springCatchBeanUtils")
public class SpringCatchBeanUtils implements ApplicationContextAware, ApplicationListener<ApplicationEvent> {

    private ApplicationContext applicationContext;
    private static boolean hasStarted = false;

    public void setApplicationContext(ApplicationContext applicationContext) {
        this.applicationContext = applicationContext;
    }

    public boolean isSpringContextReady() {
        return applicationContext != null && hasStarted;
    }

    public Object getObject(String id) {
        return applicationContext == null?null:applicationContext.getBean(id);
    }

    public <T> T getBean(Class<T> clazz) {
        return applicationContext == null?null:applicationContext.getBean(clazz);
    }

    public <T> Map<String, T> getBeans(Class<T> tClass) {
        return null == applicationContext ? null : applicationContext.getBeansOfType(tClass);
    }

    public void onApplicationEvent(ApplicationEvent e) {
        if(e instanceof ContextStartedEvent) {
            hasStarted = true;
        } else if(e instanceof ContextStoppedEvent || e instanceof ContextClosedEvent) {
            hasStarted = false;
        }

    }
}
