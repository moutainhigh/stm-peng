package com.mainsteam.stm.util;

import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;
import org.springframework.context.ApplicationEvent;
import org.springframework.context.ApplicationListener;
import org.springframework.context.event.ContextClosedEvent;
import org.springframework.context.event.ContextStartedEvent;
import org.springframework.context.event.ContextStoppedEvent;

/**
 * 
 * <li>文件名称: SpringBeanUtil.java</li> <li>公　　司: 武汉美新翔盛科技有限公司</li> <li>版权所有:
 * 版权所有(C)2019-2020</li> <li>修改记录: ...</li> <li>内容摘要: ...</li> <li>其他说明: ...</li>
 * 
 * @version ms.stm
 * @since 2019年7月11日
 * @author xiaoruqiang
 */
public class SpringBeanUtil implements ApplicationContextAware,
		ApplicationListener<ApplicationEvent> {
	/**
	 * 当前IOC
	 */
	private static ApplicationContext applicationContext;

	private static boolean hasStarted = false;

	/**
	 * 设置当前上下文环境，此方法由spring自动装配
	 */
	public void setApplicationContext(ApplicationContext applicationContext) {
		SpringBeanUtil.applicationContext = applicationContext;
	}

	public static boolean isSpringContextReady() {
		return applicationContext != null && hasStarted;
	}

	/**
	 * 从当前IOC获取bean
	 * 
	 * @param id
	 *            bean的id
	 * @return
	 */
	public static Object getObject(String id) {
		return applicationContext == null ? null : applicationContext
				.getBean(id);
	}

	public static <T> T getBean(Class<T> clazz) {
		return applicationContext == null ? null : applicationContext
				.getBean(clazz);
	}

	@Override
	public void onApplicationEvent(ApplicationEvent e) {
		if (e instanceof ContextStartedEvent) {
			hasStarted = true;
		} else if (e instanceof ContextStoppedEvent
				|| e instanceof ContextClosedEvent) {
			hasStarted = false;
		}
	}
}
