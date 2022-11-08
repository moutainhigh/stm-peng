package com.mainsteam.stm.plugin.memcached;

import java.util.HashMap;
import java.util.Map;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import com.mainsteam.stm.pluginsession.PluginResultSet;
import com.mainsteam.stm.pluginsession.PluginSession;
import com.mainsteam.stm.pluginsession.PluginSessionContext;
import com.mainsteam.stm.pluginsession.exception.PluginSessionRunException;
import com.mainsteam.stm.pluginsession.parameter.Parameter;
import com.mainsteam.stm.pluginsession.parameter.PluginArrayExecutorParameter;
import com.mainsteam.stm.pluginsession.parameter.PluginExecutorParameter;
import com.mainsteam.stm.pluginsession.parameter.PluginInitParameter;
import com.danga.MemCached.MemCachedClient;
import com.danga.MemCached.SockIOPool;

public class MemcachedPluginSession implements PluginSession{
	private String ip;
	private String port;
	/**
	 * 是否连接
	 */
	private boolean isAlived = false;
	private static final Log logger = LogFactory
			.getLog(MemcachedPluginSession.class);
	protected MemCachedClient mcc = new MemCachedClient(); 
	Map<String, String> map = new HashMap<String, String>();
	
	public MemcachedPluginSession() {
		
	}
	
	@Override
	public boolean check(PluginInitParameter parameter)
			throws PluginSessionRunException {
		
		this.init(parameter);
		return this.isAlived;
	}

	@Override
	public void destory() {
		
		
	}

	@Override
	public PluginResultSet execute(PluginExecutorParameter<?> parameter,
			PluginSessionContext context) throws PluginSessionRunException {
		PluginResultSet resultSet = null;
		if (parameter instanceof PluginArrayExecutorParameter) {
			PluginArrayExecutorParameter arrayP = (PluginArrayExecutorParameter) parameter;
			Parameter[] parameters = arrayP.getParameters();
			if (parameters != null) {
				int index = 0;
				resultSet = new PluginResultSet();
				for (Parameter p : parameters) {
					if ("NAME".equals(p.getKey().toUpperCase())) {
						resultSet.putValue(0, index++, mcc.stats());
					}
				}
			}
		}
		return resultSet;
	}

	@Override
	public void init(PluginInitParameter parameter) throws PluginSessionRunException {
		Parameter[] initParameters =  parameter.getParameters();
		for(int i=0;i < initParameters.length; i++){
			switch (initParameters[i].getKey()) {
			case "IP":
				this.ip = initParameters[i].getValue();
				break;

			case "MemcachedPort":
				this.port = initParameters[i].getValue();
				break;
			}
		}
		try {
			String addrs =this.ip+":"+this.port;
			String[] addr = {addrs}; 
			SockIOPool pool = SockIOPool.getInstance();
			pool.setServers(addr);
	        // 初始化并启动连接池  
	        pool.initialize();  
			this.isAlived = true;
		} catch (Exception e) {
			if (logger.isWarnEnabled()) {
				logger.warn(e.getMessage(), e);
			}
			this.isAlived = false;
			throw e;
		}
		
	}

	@Override
	public boolean isAlive() {
	
		return isAlived;
	}

	@Override
	public void reload() {
	
		
	}
	

}
