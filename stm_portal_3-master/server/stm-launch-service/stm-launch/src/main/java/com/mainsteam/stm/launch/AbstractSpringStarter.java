/**
 * 
 */
package com.mainsteam.stm.launch;

import java.util.List;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.beans.factory.config.BeanFactoryPostProcessor;
import org.springframework.beans.factory.support.BeanDefinitionRegistryPostProcessor;
import org.springframework.beans.factory.support.GenericBeanDefinition;
import org.springframework.beans.factory.support.SimpleBeanDefinitionRegistry;
import org.springframework.context.support.ClassPathXmlApplicationContext;

/**
 * 加载spring配置文件，启动系统
 * 
 * @author ziw
 * 
 */
public abstract class AbstractSpringStarter {

	private static final Log logger = LogFactory
			.getLog(AbstractSpringStarter.class);

	protected static final String[] PUBLIC_SPRING_BEANS = { "server-*-beans.xml",
			"public-*-beans.xml" };

	public static final String PUBLIC_SPRING_BEANS_URI = "classpath:META-INF/services/";

	public static final String ASSEMBLY_SPRING_BEANS_URI = "assembly";

	private ClassPathXmlApplicationContext context;

	/**
	 * 
	 */
	public AbstractSpringStarter() {
	}

	public abstract String[] getSpringBeanNames();

	public void startServer() {
		// TODO:license 验证
		// TODO:系统运行环境检查
		// TODO:启动shutdown监听
		String[] springBeanCfgs = getSpringBeanNames();
		int oldLength = 0;
		if (springBeanCfgs == null) {
			springBeanCfgs = new String[PUBLIC_SPRING_BEANS.length];
			oldLength = springBeanCfgs.length;
		} else {
			oldLength = springBeanCfgs.length;
			springBeanCfgs = new String[oldLength + PUBLIC_SPRING_BEANS.length];
			System.arraycopy(springBeanCfgs, 0, springBeanCfgs, 0, oldLength);
		}
		System.arraycopy(PUBLIC_SPRING_BEANS, 0, springBeanCfgs, oldLength,
				PUBLIC_SPRING_BEANS.length);

		for (int i = 0; i < springBeanCfgs.length; i++) {
			springBeanCfgs[i] = PUBLIC_SPRING_BEANS_URI + springBeanCfgs[i];
			if (logger.isDebugEnabled()) {
				logger.debug("startServer spell out the spring config of "
						+ springBeanCfgs[i]);
			}
		}

		// 装载服务的SpringBean
		context = new ClassPathXmlApplicationContext();
		context.setConfigLocations(springBeanCfgs);
		// 将本对象注入到Spring容器，提供给容器管理类使用
		if (logger.isDebugEnabled()) {
			logger.debug("startServer register current bean to spring.");
		}
		SimpleBeanDefinitionRegistry registry = new SimpleBeanDefinitionRegistry();
		GenericBeanDefinition beanDef = new GenericBeanDefinition();
		beanDef.setBeanClass(this.getClass());
		beanDef.setSource(this);
		registry.registerBeanDefinition("SpringStartBean", beanDef);
		List<BeanFactoryPostProcessor> processors = context
				.getBeanFactoryPostProcessors();
		for (BeanFactoryPostProcessor beanFactoryPostProcessor : processors) {
			if (beanFactoryPostProcessor instanceof BeanDefinitionRegistryPostProcessor) {
				((BeanDefinitionRegistryPostProcessor) beanFactoryPostProcessor)
						.postProcessBeanDefinitionRegistry(registry);
				break;
			}
		}
		context.registerShutdownHook();
		if (logger.isDebugEnabled()) {
			logger.debug("startServer spring starting.");
		}
		context.start();
		if (logger.isInfoEnabled()) {
			logger.info("startServer start spring success.");
		}
	}

	public void stopServer() {
		if (logger.isInfoEnabled()) {
			logger.info("stopServer...");
		}
		if (context != null) {
			context.close();
		}
		if (logger.isInfoEnabled()) {
			logger.info("stopServer success.");
		}
	}

	public static void main(String[] args) {

	}

}
