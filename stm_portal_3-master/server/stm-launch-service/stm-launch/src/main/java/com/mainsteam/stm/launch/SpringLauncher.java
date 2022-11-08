/**
 * 
 */
package com.mainsteam.stm.launch;

import java.lang.reflect.Method;
import java.util.List;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.beans.factory.config.BeanFactoryPostProcessor;
import org.springframework.beans.factory.support.BeanDefinitionRegistryPostProcessor;
import org.springframework.beans.factory.support.GenericBeanDefinition;
import org.springframework.beans.factory.support.SimpleBeanDefinitionRegistry;
import org.springframework.context.support.ClassPathXmlApplicationContext;

import com.mainsteam.stm.bootstrap.BootStrapEvn;
import com.mainsteam.stm.bootstrap.DefaultBootStrapEvn;
import com.mainsteam.stm.util.SpringBeanUtil;

/**
 * @author ziw
 * 
 */
public class SpringLauncher {

	private static final Log logger = LogFactory.getLog(SpringLauncher.class);

	public static final String[] server_types = { "processer", "collector" };

	public static final String portal_type = "portal";

	public static final String trap_type = "trap";

	public static final String[] PORTAL_SPRING_BEANS = { "public-*-beans.xml", "portal-*-beans.xml" };
	public static final String[] TRAP_SPRING_BEANS = { "public-*-beans.xml", "trap-*-beans.xml" };
	public static final String[] PUBLIC_SPRING_BEANS = { "public-*-beans.xml", "server-commons-*-beans.xml" };

	public static final String SERVER_TYPE_SPRING_BEANS = "server-{serverType}-*-beans.xml";

	// public static final String SERVER_TYPE_EXTESION_SPRING_BEANS =
	// "EXTENSION_SPRING_BEANS";

	public static final String PUBLIC_SPRING_BEANS_URI = "classpath*:META-INF/services/";

	public static final String SYSTEM_ENV_SERVERTYPE = "serverType";

	public static final String SYSTEM_ENV_SERVERTYPE_SPLIT = ";";

	private static SpringLauncher _self;

	private ClassPathXmlApplicationContext context;

	private BootStrapEvn bootStrapEvn;

	private transient boolean running = false;

	/**
	 * 
	 */
	private SpringLauncher() {
		_self = this;

	}

	public static SpringLauncher getInstance() {
		return _self;
	}

	public BootStrapEvn getBootStrapEvn() {
		if (bootStrapEvn == null) {
			bootStrapEvn = new DefaultBootStrapEvn();
		}
		return bootStrapEvn;
	}

	public void setBootStrapEvn(BootStrapEvn bootStrapEvn) {
		this.bootStrapEvn = bootStrapEvn;
	}

	public String[] getSpringBeanName() {
		String[] springBeanCfgs = null;
		String serverType = getBootStrapEvn().getEnv(SYSTEM_ENV_SERVERTYPE);
		if (serverType == null || serverType.equals("")) {
			String msg = "getSpringBeanNames serverType is null";
			if (logger.isErrorEnabled()) {
				logger.error(msg);
			}
			RuntimeException errorException = new RuntimeException(msg);
			throw errorException;
		}

		if (logger.isInfoEnabled()) {
			logger.info("startServer[" + serverType + "]...");
		}
		String springBeanCfg = null;
		if (portal_type.equals(serverType)) {
			springBeanCfgs = PORTAL_SPRING_BEANS;
		} else if (trap_type.equals(serverType)) {
			springBeanCfgs = TRAP_SPRING_BEANS;
		} else {
			springBeanCfg = SERVER_TYPE_SPRING_BEANS.replaceAll("\\{serverType\\}", serverType);
			if (springBeanCfg != null) {
				springBeanCfgs = new String[1 + PUBLIC_SPRING_BEANS.length];
				System.arraycopy(PUBLIC_SPRING_BEANS, 0, springBeanCfgs, 0, PUBLIC_SPRING_BEANS.length);
				springBeanCfgs[PUBLIC_SPRING_BEANS.length] = springBeanCfg;
			} else {
				springBeanCfgs = PUBLIC_SPRING_BEANS;
			}
		}

		// String extentionSpringBeans = getBootStrapEvn().getEnv(
		// SERVER_TYPE_EXTESION_SPRING_BEANS);
		// if (StringUtils.isNotEmpty(extentionSpringBeans)) {
		// String[] extentionSpringBeanNames = extentionSpringBeans.split(",");
		// if (extentionSpringBeanNames != null
		// && extentionSpringBeanNames.length < 0) {
		// String[] newCfgs = new String[springBeanCfgs.length
		// + extentionSpringBeanNames.length];
		// System.arraycopy(springBeanCfgs, 0, newCfgs, 0,
		// springBeanCfgs.length);
		// System.arraycopy(extentionSpringBeanNames, 0, newCfgs, 0,
		// extentionSpringBeanNames.length);
		// springBeanCfgs = newCfgs;
		// }
		// }

		for (int i = 0; i < springBeanCfgs.length; i++) {
			springBeanCfgs[i] = PUBLIC_SPRING_BEANS_URI + springBeanCfgs[i];
			if (logger.isDebugEnabled()) {
				logger.debug("startServer spell out the spring config of " + springBeanCfgs[i]);
			}
		}
		return springBeanCfgs;
	}

	public void startServer() throws Exception {
		// malachi 将beans动态添加到spring容器
		long temp = System.currentTimeMillis();
		// TODO:license 验证
		// TODO:系统运行环境检查
		// TODO:启动shutdown监听

		if (logger.isDebugEnabled()) {
			logger.debug("startServer load beans start.");
		}
		try {
			// 装载服务的SpringBean
			context = new ClassPathXmlApplicationContext(getSpringBeanName(), false);
			new SpringBeanUtil().setApplicationContext(context);// init spring beans.
		} catch (Throwable e) {
			e.printStackTrace();
			if (logger.isErrorEnabled()) {
				logger.error("startServer", e);
			}
			stopServer();
		}
		// 将本对象注入到Spring容器，提供给容器管理类使用
		if (logger.isDebugEnabled()) {
			logger.debug("startServer register current bean to spring.");
		}
		if (logger.isDebugEnabled()) {
			logger.debug("startServer spring starting.");
		}
		try {
			context.refresh();
			context.start();
			SimpleBeanDefinitionRegistry registry = new SimpleBeanDefinitionRegistry();
			GenericBeanDefinition beanDef = new GenericBeanDefinition();
			beanDef.setBeanClass(this.getClass());
			beanDef.setSource(this);
			registry.registerBeanDefinition("SpringStartBean", beanDef);
			List<BeanFactoryPostProcessor> processors = context.getBeanFactoryPostProcessors();
			for (BeanFactoryPostProcessor beanFactoryPostProcessor : processors) {
				if (beanFactoryPostProcessor instanceof BeanDefinitionRegistryPostProcessor) {
					((BeanDefinitionRegistryPostProcessor) beanFactoryPostProcessor)
							.postProcessBeanDefinitionRegistry(registry);
					break;
				}
			}
			context.registerShutdownHook();
			running = true;
		} catch (Throwable e) {
			if (logger.isErrorEnabled()) {
				logger.error("startServer", e);
			}
			stopServer();
		}
		long lossTime = System.currentTimeMillis() - temp;
		if (logger.isInfoEnabled()) {
			logger.info("startServer start spring success.lossTime=" + lossTime + "ms");
		}
		// await();
	}

	// private void await(){
	// while(running){
	// synchronized (this) {
	// try {
	// this.wait(1000);
	// } catch (InterruptedException e) {
	// }
	// }
	// }
	// }

	public void stopServer() {
		try {
			if (logger.isInfoEnabled()) {
				logger.info("stopServer...");
			}
			if (context != null) {
				try {
					if (running) {
						context.stop();
					}else {
						System.exit(1);
					}
				} catch (Throwable e) {
					if (logger.isErrorEnabled()) {
						logger.error("stopServer", e);
					}
				} finally {
					try {
						context.close();
					} catch (Throwable e) {
						if (logger.isErrorEnabled()) {
							logger.error("stopServer", e);
						}
					}
				}
			}
			 running = false;
			if (logger.isInfoEnabled()) {
				logger.info("stopServer success.");
			}
		} finally {
			System.exit(1);
		}
	}

	public static void stop() {
		getInstance().stopServer();
	}

	public synchronized static void main(String[] args) throws Exception {
		if (_self != null) {
			return;
		}
		SpringLauncher starter = new SpringLauncher();
		starter.startServer();
		if (args.length <= 0) {
			return;
		}
		String utilClass = args[0].trim();
		if (utilClass.isEmpty()) {
			return;
		}
		String[] newArgs = null;
		if (args.length > 1) {
			newArgs = new String[args.length - 1];
			System.arraycopy(args, 1, newArgs, 0, newArgs.length);
		} else {
			newArgs = new String[0];
		}
		try {
			Class<?> c = Class.forName(utilClass);
			Method mainMethod = c.getDeclaredMethod("main", String[].class);
			mainMethod.invoke(c.newInstance(), (Object) newArgs);
			_self = starter;
		} catch (Throwable e) {
			if (logger.isErrorEnabled()) {
				logger.error("main", e);
			}
			starter.stopServer();
		}
	}
}
