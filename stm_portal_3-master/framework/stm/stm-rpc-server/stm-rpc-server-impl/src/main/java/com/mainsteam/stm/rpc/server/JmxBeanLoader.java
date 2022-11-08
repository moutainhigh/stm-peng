/**
 * 
 */
package com.mainsteam.stm.rpc.server;

import java.io.IOException;
import java.util.Map;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.context.ApplicationListener;
import org.springframework.context.event.ContextRefreshedEvent;

import com.mainsteam.stm.route.physical.PhysicalServer;

/**
 * @author ziw
 * 
 */
public class JmxBeanLoader implements
		ApplicationListener<ContextRefreshedEvent> {

	private static final Log logger = LogFactory.getLog(JmxBeanLoader.class);

	private JmxBeanBeanPostProcessors jmxBeanBeanPostProcessors;

	private OCRPCServerImpl server;

	private PhysicalServer physicalServer;

	/**
	 * 
	 */
	public JmxBeanLoader() {
		if (logger.isInfoEnabled()) {
			logger.info("JmxBeanLoader construct over.");
		}
	}

	/**
	 * @param physicalServer
	 *            the physicalServer to set
	 */
	public final void setPhysicalServer(PhysicalServer physicalServer) {
		this.physicalServer = physicalServer;
	}
	
	

	/**
	 * @param jmxBeanBeanPostProcessors
	 *            the jmxBeanBeanPostProcessors to set
	 */
	public final void setJmxBeanBeanPostProcessors(
			JmxBeanBeanPostProcessors jmxBeanBeanPostProcessors) {
		this.jmxBeanBeanPostProcessors = jmxBeanBeanPostProcessors;
	}

	/**
	 * @param server
	 *            the server to set
	 */
	public final void setServer(OCRPCServerImpl server) {
		this.server = server;
	}

	@Override
	public synchronized void onApplicationEvent(ContextRefreshedEvent event) {
		if (logger.isTraceEnabled()) {
			logger.trace("register jmx beans start.");
		}
		Map<Class<?>, Object> mbeanObjMap = jmxBeanBeanPostProcessors
				.getMbeanObjMap();
		if (mbeanObjMap != null && mbeanObjMap.size() > 0) {
			for (Class<?> f : mbeanObjMap.keySet()) {
				Object bean = mbeanObjMap.get(f);
				this.server.registerObjectSerivce(bean, f);
				if (logger.isInfoEnabled()) {
					logger.info("register jmx bean for " + f + " Implment by "
							+ bean.getClass());
				}
			}
		}
		if (server.isStarted()) {
			if (logger.isWarnEnabled()) {
				logger.warn("jmx server has started. ");
			}
			return;
		}
		jmxBeanBeanPostProcessors.clear();
		try {
			server.startServer();
			physicalServer.setConfig(server.getServerIp(),
					server.getServerPort());
			physicalServer.startServer();
		} catch (IOException e) {
			if (logger.isErrorEnabled()) {
				logger.error("fail to startServer", e);
			}
			throw new RuntimeException(e);
		}
		if (logger.isTraceEnabled()) {
			logger.trace("register jmx beans end.");
		}
	}
}
