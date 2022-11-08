/**
 * 
 */
package com.mainsteam.stm.plugin.snmptest;

import java.io.IOException;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.mainsteam.stm.pluginsession.PluginResultSet;
import com.mainsteam.stm.pluginsession.PluginSession;
import com.mainsteam.stm.pluginsession.PluginSessionContext;
import com.mainsteam.stm.pluginsession.exception.PluginSessionRunException;
import com.mainsteam.stm.pluginsession.parameter.Parameter;
import com.mainsteam.stm.pluginsession.parameter.PluginArrayExecutorParameter;
import com.mainsteam.stm.pluginsession.parameter.PluginExecutorParameter;
import com.mainsteam.stm.pluginsession.parameter.PluginInitParameter;

/**
 * @author fevergreen
 * 
 */
public class SnmpPluginSession implements PluginSession {
	
	private static final Logger logger = LoggerFactory.getLogger(SnmpPluginSession.class);

	private String ip;

	private int port;

	private String keyword;

	/**
	 * 
	 */
	public SnmpPluginSession() {
	}


	@Override
	public void init(PluginInitParameter initP) {
		Parameter[] initParameters = initP.getParameters();
		for (int i = 0; i < initParameters.length; i++) {
			switch (initParameters[i].getKey()) {
			case "IP":
				this.ip = initParameters[i].getValue();
				break;
			case "Port":
				this.port = Integer.parseInt(initParameters[i].getValue());
				break;
			case "keyword":
				this.keyword = initParameters[i].getValue();
				break;
			default:
				System.out.println("warn:unkown initparameter "
						+ initParameters[i].getKey() + "="
						+ initParameters[i].getValue());
				break;
			}
		}
	}

	@Override
	public void destory() {
		// TODO Auto-generated method stub

	}

	@Override
	public void reload() {
		// TODO Auto-generated method stub

	}

	@Override
	public boolean isAlive() {
		return true;
	}

	@Override
	public PluginResultSet execute(
			PluginExecutorParameter<?> executorParameter,
			PluginSessionContext arg1)  throws PluginSessionRunException {
		PluginResultSet result = null;
		if (executorParameter instanceof PluginArrayExecutorParameter) {
			PluginArrayExecutorParameter arrayP = (PluginArrayExecutorParameter) executorParameter;
			String[] oids = arrayP.getParametersValue();
			SnmpAgent agent = SnmpAgent.getInstance();
			SnmpParameter p = new SnmpParameter();
			p.setIp(ip);
			p.setPort(port);
			p.setPassword(keyword);
			p.setOids(oids);
			try {
				Map<String, List<String>> oidResultValueMaps = agent
						.getSnmpResult(p);
				result = new PluginResultSet();
				int column = 0;
				for(Entry<String, List<String>> entry:oidResultValueMaps.entrySet()){
					String key =entry.getKey();
					List<String> list = entry.getValue();
					int row = 0;
					for(String v:list){
						if(logger.isInfoEnabled()){
							logger.info(key+":"+v);
						}
						result.putValue(row, column, v);
						row++;
					}
					column++;
				}
					
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
		return result;
	}


	@Override
	public boolean check(PluginInitParameter arg0)
			throws PluginSessionRunException {
		// TODO Auto-generated method stub
		return false;
	}
}
