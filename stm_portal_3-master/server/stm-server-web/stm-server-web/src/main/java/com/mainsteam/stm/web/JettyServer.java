/**
 * 
 */
package com.mainsteam.stm.web;

import java.io.File;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.net.InetAddress;
import java.net.InetSocketAddress;
import java.net.Socket;
import java.net.SocketAddress;
import java.net.URLDecoder;
import java.net.UnknownHostException;
import java.util.EnumSet;
import java.util.Properties;

import javax.servlet.DispatcherType;
import javax.servlet.ServletContext;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.eclipse.jetty.server.Connector;
import org.eclipse.jetty.server.Server;
import org.eclipse.jetty.server.nio.SelectChannelConnector;
import org.eclipse.jetty.servlet.FilterHolder;
import org.eclipse.jetty.servlet.ServletHolder;
import org.eclipse.jetty.webapp.WebAppContext;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.BeansException;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;
import org.springframework.context.ApplicationContextException;
import org.springframework.context.ApplicationListener;
import org.springframework.context.event.ContextRefreshedEvent;
import org.springframework.core.io.ClassPathResource;
import org.springframework.core.io.support.PropertiesLoaderUtils;
import org.springframework.util.ClassUtils;
import org.springframework.util.ObjectUtils;
import org.springframework.web.context.ConfigurableWebApplicationContext;
import org.springframework.web.context.ContextLoader;
import org.springframework.web.context.WebApplicationContext;

import com.mainsteam.stm.route.logic.LogicServer;
import com.mainsteam.stm.util.OSUtil;
import com.mainsteam.stm.util.PropertiesFileUtil;
import com.mainsteam.stm.web.connector.RouteableConnector;

/**
 * @author ziw
 * 
 */
public class JettyServer implements ApplicationContextAware,
		ApplicationListener<ContextRefreshedEvent> {

	private static final Log logger = LogFactory.getLog(JettyServer.class);

	public static final String WEBAPP_DIR = "WEBAPP_DIR";

	public static final String JETTY_SERVER_CONFIG = "jetty_server.properties";

	private String contextPath = "/";

	private String defaultResourceBase = "../webapp";

	private String defaultResourceTempBase = "../webwork";

	private String webXmlPath = "/WEB-INF/web.xml";

	private Server server;

	private int port = 0;

	private WebAppContext webapp;

	private ApplicationContext applicationContext;

	private boolean started = false;
	
	private LogicServer logicServer;

	/**
	 * Name of the class path resource (relative to the ContextLoader class)
	 * that defines ContextLoader's default strategy names.
	 */
	private static final String DEFAULT_STRATEGIES_PATH = "ContextLoader.properties";

	private static final Properties defaultStrategies;

	static {
		// Load default strategy implementations from properties file.
		// This is currently strictly internal and not meant to be customized
		// by application developers.
		try {
			ClassPathResource resource = new ClassPathResource(
					DEFAULT_STRATEGIES_PATH, ContextLoader.class);
			defaultStrategies = PropertiesLoaderUtils.loadProperties(resource);
		} catch (IOException ex) {
			throw new IllegalStateException(
					"Could not load 'ContextLoader.properties': "
							+ ex.getMessage());
		}
	}

	/**
	 * 
	 */
	public JettyServer() {
	}
	

	public void setLogicServer(LogicServer logicServer) {
		this.logicServer = logicServer;
	}



	/**
	 * @return the port
	 */
	public final int getPort() {
		return port;
	}

	private void applyHandle() {
		String webappRootPath = OSUtil.getEnv(WEBAPP_DIR, defaultResourceBase);
		File resourceBaseFile = new File(webappRootPath);
		if (!resourceBaseFile.exists() || resourceBaseFile.isFile()) {
			resourceBaseFile.mkdirs();
			if (logger.isInfoEnabled()) {
				logger.info("applyHandle make jetty's resourceBase dir="
						+ resourceBaseFile.getAbsolutePath());
			}
		}
		File defaultResourceTempBaseFile = new File(defaultResourceTempBase);
		if (!defaultResourceTempBaseFile.exists()
				|| defaultResourceTempBaseFile.isFile()) {
			defaultResourceTempBaseFile.mkdirs();
			if (logger.isInfoEnabled()) {
				logger.info("applyHandle make jetty's work dir="
						+ defaultResourceTempBaseFile.getAbsolutePath());
			}
		}
		try {
			webappRootPath = URLDecoder.decode(
					resourceBaseFile.getAbsolutePath(), "UTF-8");
		} catch (UnsupportedEncodingException e) {
		}
		webapp = new WebAppContext();
		webapp.setContextPath(contextPath);
		webapp.setResourceBase(webappRootPath);
		webXmlPath = webappRootPath + '/' + webXmlPath;
		File webXmlFile = new File(webXmlPath);
		try {
			webXmlPath = URLDecoder.decode(webXmlFile.getAbsolutePath(),
					"UTF-8");
		} catch (UnsupportedEncodingException e) {
		}
		webapp.setDescriptor(webXmlPath);
		webapp.setParentLoaderPriority(true);
		webapp.setTempDirectory(defaultResourceTempBaseFile);
		ServletContext context = webapp.getServletContext();
		ConfigurableWebApplicationContext webApplicationContext = (ConfigurableWebApplicationContext) createWebApplicationContext();
		webApplicationContext
				.setConfigLocation("classpath*:META-INF/services/noting-*-beans.xml");
		webApplicationContext.setParent(this.applicationContext);
//		webApplicationContext.start();
		configureAndRefreshWebApplicationContext(webApplicationContext, context);
		context.setAttribute(
				WebApplicationContext.ROOT_WEB_APPLICATION_CONTEXT_ATTRIBUTE,
				webApplicationContext);
		server.setHandler(webapp);
		if (logger.isInfoEnabled()) {
			StringBuilder b = new StringBuilder();
			b.append("webappRootPath=").append(webappRootPath);
			b.append(" webXmlPath=").append(webXmlPath);
			b.append(" contextPath=").append(contextPath);
			logger.info("applyHandle ");
		}
	}

	protected Class<?> determineContextClass() {
		String contextClassName = defaultStrategies
				.getProperty(WebApplicationContext.class.getName());
		try {
			return ClassUtils.forName(contextClassName,
					ContextLoader.class.getClassLoader());
		} catch (ClassNotFoundException ex) {
			throw new ApplicationContextException(
					"Failed to load default context class [" + contextClassName
							+ "]", ex);
		}
	}

	protected WebApplicationContext createWebApplicationContext() {
		Class<?> contextClass = determineContextClass();
		if (!ConfigurableWebApplicationContext.class
				.isAssignableFrom(contextClass)) {
			throw new ApplicationContextException("Custom context class ["
					+ contextClass.getName() + "] is not of type ["
					+ ConfigurableWebApplicationContext.class.getName() + "]");
		}
		return (ConfigurableWebApplicationContext) BeanUtils
				.instantiateClass(contextClass);
	}

	public void addFilter(FilterHolder holder, String pathSec,
			EnumSet<DispatcherType> dispatches) {
		webapp.addFilter(holder, pathSec, dispatches);
	}

	public void addFilter(ServletHolder holder, String pathSec,
			EnumSet<DispatcherType> dispatches) {
		webapp.addServlet(holder, pathSec);
	}

	public synchronized void startServer() {
		// TODO do noting.
	}
	
	private Connector createJettyHttpConnector(){
		Properties p = PropertiesFileUtil
				.getProperties(JETTY_SERVER_CONFIG);
		if (p != null) {
			String portCfg = p.getProperty("port");
			if (portCfg != null && !portCfg.equals("")) {
				try {
					port = Integer.parseInt(portCfg);
				} catch (Exception e) {
					if (logger.isErrorEnabled()) {
						logger.error("startServer", e);
					}
				}
			}
		}
		if (logger.isInfoEnabled()) {
			logger.info("JettyServer init start.port=" + port);
		}
		if (port <= 0) {
			if (logger.isErrorEnabled()) {
				logger.error("JettyServer's http port must be more than 0.");
			}
			return null;
		}
		Socket s = new Socket();
		SocketAddress endpoint;
		try {
			endpoint = new InetSocketAddress(InetAddress.getLocalHost(),
					port);
			s.connect(endpoint);
			s.close();
			throw new RuntimeException(
					"JettyServer's port has been used.port=" + port);
		} catch (UnknownHostException e1) {
		} catch (IOException e) {
		}
		SelectChannelConnector connector = new SelectChannelConnector();
		connector.setPort(port);
		return connector;
	}
	
	private Connector createRouteableConnecor(){
		return new RouteableConnector(logicServer);
	}
	  
	private Connector[] createConnectors(){
		Connector[] connectors = null;
		Connector httpConnector = createJettyHttpConnector();
		if(httpConnector == null){
			connectors = new Connector[1];
			connectors[0] = createRouteableConnecor();
		}else{
			connectors = new Connector[2];
			connectors[0] = httpConnector;
			connectors[1] = createRouteableConnecor();
		}
		return connectors;
	}

	public synchronized void startServer0() {
		try {
			if (server != null) {
				if (server.isStopped()) {
					server.start();
				}
				return;
			}
			server = new Server();
			server.setConnectors(createConnectors());
			applyHandle();
			server.start();
			if (logger.isInfoEnabled()) {
				logger.info("JettyServer listened on " + port);
			}
		} catch (Exception e) {
			if (logger.isErrorEnabled()) {
				logger.error("startServer", e);
			}
			started = false;
		}
		if (logger.isInfoEnabled()) {
			logger.info("JettyServer init end");
		}
	}

	protected void configureAndRefreshWebApplicationContext(
			ConfigurableWebApplicationContext wac, ServletContext sc) {
		if (ObjectUtils.identityToString(wac).equals(wac.getId())) {
			// The application context id is still set to its original default
			// value
			// -> assign a more useful id based on available information
		} else {
			// Generate default id...
			if (sc.getMajorVersion() == 2 && sc.getMinorVersion() < 5) {
				// Servlet <= 2.4: resort to name specified in web.xml, if any.
				wac.setId(ConfigurableWebApplicationContext.APPLICATION_CONTEXT_ID_PREFIX
						+ ObjectUtils.getDisplayString(sc
								.getServletContextName()));
			} else {
				wac.setId(ConfigurableWebApplicationContext.APPLICATION_CONTEXT_ID_PREFIX
						+ ObjectUtils.getDisplayString(sc.getContextPath()));
			}
		}

		// customizeContext(sc, wac);
		wac.refresh();
	}

	public synchronized void stopServer() {
		if (server != null) {
			if (logger.isInfoEnabled()) {
				logger.info("stop JettyServer at port=" + port);
			}
			try {
				server.stop();
				server = null;
			} catch (Exception e) {
				if (logger.isErrorEnabled()) {
					logger.error("stop", e);
				}
			}
		}
	}

	@Override
	public void setApplicationContext(ApplicationContext applicationContext)
			throws BeansException {
		this.applicationContext = applicationContext;
	}

	@Override
	public synchronized void onApplicationEvent(ContextRefreshedEvent event) {
		if (started) {
			return;
		}
		started = true;
		new Thread(new Runnable() {
			public void run() {
				try {
					Thread.sleep(10000);
				} catch (InterruptedException e) {
					e.printStackTrace();
				}
				startServer0();
			}
		}, "JettyServerStartThread").start();
	}
}
