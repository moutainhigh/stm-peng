package com.mainsteam.stm.plugin.redis;

import java.util.HashMap;
import java.util.Map;

import redis.clients.jedis.Jedis;

import com.mainsteam.stm.pluginsession.PluginResultSet;
import com.mainsteam.stm.pluginsession.PluginSession;
import com.mainsteam.stm.pluginsession.PluginSessionContext;
import com.mainsteam.stm.pluginsession.exception.PluginSessionRunException;
import com.mainsteam.stm.pluginsession.parameter.Parameter;
import com.mainsteam.stm.pluginsession.parameter.PluginArrayExecutorParameter;
import com.mainsteam.stm.pluginsession.parameter.PluginExecutorParameter;
import com.mainsteam.stm.pluginsession.parameter.PluginInitParameter;

public class RedisPluginSession implements PluginSession {

	private String ip;
	private int port;
	private String auth;

	Map<String, String> map = new HashMap<String, String>();

	@Override
	public boolean check(PluginInitParameter parameter)
			throws PluginSessionRunException {
		try {
			String ip = parameter.getParameterValueByKey("IP");
			int port = Integer.parseInt(parameter
					.getParameterValueByKey("redisPort"));
			Jedis jedis = new Jedis(ip, port);
			String auth = parameter.getParameterValueByKey("auth");
			if (auth != null && !"".equals(auth)) {
				jedis.auth(auth);
			}
			jedis.info();
			jedis.close();
		} catch (Exception e) {
			return false;
		}
		return true;
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
						resultSet.putValue(0, index++, map.get(p.getValue()));
					} else if ("AVAILABILITY".equals(p.getKey().toUpperCase())) {
						String r = map.get(p.getValue());
						resultSet.putValue(0, index++,
								r != null && !"".equals(r) ? "1" : "0");
					}
				}
			}
		}
		return resultSet;
	}

	@Override
	public void init(PluginInitParameter parameter)
			throws PluginSessionRunException {
		Parameter[] initParameters = parameter.getParameters();
		for (int i = 0; i < initParameters.length; i++) {
			switch (initParameters[i].getKey()) {
			case "IP":
				this.ip = initParameters[i].getValue();
				break;
			case "redisPort":
				this.port = Integer.parseInt(initParameters[i].getValue());
				break;
			case "auth":
				this.auth = initParameters[i].getValue();
				break;
			}
		}
		this.getInfo();
	}

	@Override
	public boolean isAlive() {
		return map.size() > 0;
	}

	@Override
	public void reload() {
		this.getInfo();
	}

	public void getInfo() {
		Jedis jedis = new Jedis(this.ip, this.port);
		if (this.auth != null & !"".equals(this.auth)) {
			jedis.auth(this.auth);
		}
		String info = jedis.info();
		jedis.close();
		String[] lines = info.split("\r\n");
		for (String line : lines) {
			if ("".equals(line) || line.startsWith("#")) {
				continue;
			}
			String[] res = line.split(":");
			map.put(res[0], res.length >= 2 ? res[1] : null);
		}
	}
}
