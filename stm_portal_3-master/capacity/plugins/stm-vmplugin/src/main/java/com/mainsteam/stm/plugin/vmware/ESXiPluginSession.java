package com.mainsteam.stm.plugin.vmware;

import java.net.URL;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import com.mainsteam.stm.errorcode.CapcityErrorCodeConstant;
import com.mainsteam.stm.plugin.vmware.util.VmwareBeansCachedecorator;
import com.mainsteam.stm.pluginsession.BaseBrokerSession;
import com.mainsteam.stm.pluginsession.exception.PluginSessionRunException;
import com.mainsteam.stm.pluginsession.parameter.Parameter;
import com.mainsteam.stm.pluginsession.parameter.PluginInitParameter;
import com.vmware.vim25.VimPortType;
import com.vmware.vim25.mo.ServiceInstance;

/**
 * 虚拟化采集插件
 * 
 * @author xiaop_000
 *
 */
public class ESXiPluginSession extends BaseBrokerSession {

	private static final String PROTOCOL = "https";

	private static final String SDK_PATH = "/sdk";

	private static final Log logger = LogFactory
			.getLog(ESXiPluginSession.class);

	private static final String CONNECTION_HOST = "IP";

	private static final String CONNECTION_USERNAME = "username";

	private static final String CONNECTION_PASSWORD = "password";

	/**
	 * session是否失效
	 */
	private boolean isAlive;
	/**
	 * 重试次数，默认重连两次
	 */
	private int retryTimes = 1;

	private ServiceInstance serviceInstance;

	public ESXiPluginSession() {
		super();
	}

	// 在具体的session中实现
	@Override
	public void init(PluginInitParameter init) throws PluginSessionRunException {

		Parameter[] initParameters = init.getParameters();

		for (int i = 0; i < initParameters.length; i++) {
			switch (initParameters[i].getKey()) {
			case CONNECTION_HOST:
				super.getParameter().setIp(initParameters[i].getValue());
				break;
			case CONNECTION_USERNAME:
				super.getParameter().setUsername(initParameters[i].getValue());
				break;
			case CONNECTION_PASSWORD:
				super.getParameter().setPassword(initParameters[i].getValue());
				break;
			default:
				if (logger.isWarnEnabled()) {
					logger.warn("warn:unkown initparameter "
							+ initParameters[i].getKey() + "="
							+ initParameters[i].getValue());
				}
				break;
			}
		}

		this.createConnection(0);
		super.getParameter().setConnection(this.serviceInstance);
		this.isAlive = true;

	}

	@Override
	public void destory() {
		if (null != serviceInstance) {
			try {
				serviceInstance.getServerConnection().logout();
				VmwareBeansCachedecorator.destoryCache(serviceInstance);
			} catch (Exception e) {
				logger.error(e.getMessage(), e);
			}
		}
		serviceInstance = null;
		this.isAlive = false;
	}

	@Override
	public void reload() {

	}

	// 在具体的session中实现
	@Override
	public boolean isAlive() {
		try {
			if (serviceInstance != null
					&& null != serviceInstance.getServerClock()) {
				this.isAlive = true;
			} else {
				this.isAlive = false;
			}
		} catch (Exception e) {
			this.isAlive = false;
			// logger.error(e.getMessage(), e);
		}
		return this.isAlive;

	}

	private void createConnection(int currentRetry)
			throws PluginSessionRunException {

		for (int cnt = currentRetry; cnt < retryTimes; cnt++) {
			URL url = null;
			try {
				url = new URL(PROTOCOL, super.getParameter().getIp(),
						SDK_PATH);
				serviceInstance = new ServiceInstance(url, super.getParameter()
						.getUsername(), super.getParameter().getPassword(),
						true);
				if (null != serviceInstance) {
					serviceInstance.getServerConnection().getVimService().getWsc().setReadTimeout(600000);//10分钟超时时间
					VmwareBeansCachedecorator.destoryCache(serviceInstance);
					VmwareBeansCachedecorator.initCache(serviceInstance);
					break;
				}
			} catch (Exception e) {
				serviceInstance = null;
				logger.error(url, e);
			}
		}

		if (serviceInstance == null) {
			throw new PluginSessionRunException(
					CapcityErrorCodeConstant.ERR_CAPCITY_CONNECTION_FAILED,
					"Vcenter连接失败，IP为【" + super.getParameter().getIp()
							+ "】，用户为【" + super.getParameter().getUsername()
							+ "】");
		}
	}

	@Override
	public boolean check(PluginInitParameter initParameters)
			throws PluginSessionRunException {
		return false;
	}
}
