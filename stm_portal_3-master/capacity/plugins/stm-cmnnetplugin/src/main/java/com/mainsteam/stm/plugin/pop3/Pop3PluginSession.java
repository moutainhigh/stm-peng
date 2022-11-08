package com.mainsteam.stm.plugin.pop3;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.apache.commons.lang.StringUtils;
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

public class Pop3PluginSession implements PluginSession{
	private static final Log logger = LogFactory
			.getLog(Pop3PluginSession.class);
	private static final String COMMAND_POP3 = "COMMAND";
	private static final String POP3_PLUGIN_IP = "IP";
	private static final String POP3_PLUGIN_PORT = "pop3Port";
	private static final String POP3_PLUGIN_USERNAME = "pop3Username";
	private static final String POP3_PLUGIN_USERPASSWORD = "pop3Userpassword";
	private Pop3Bo pop3Bo;
	public Pop3PluginSession(){
		this.pop3Bo=new Pop3Bo();
	}
	private String anyClass=null;
	private Class<?> thisClass=null;
	private Object object=null;
	private void getPop3Collector(){
		 try {
			thisClass = Class.forName(anyClass);
			object = thisClass.newInstance();
		} catch (ClassNotFoundException e) {
			logger.error(e);
		}
		 catch (InstantiationException e) {
			 logger.error(e);
		} catch (IllegalAccessException e) {
			logger.error(e);
		}
	}
	

	public Pop3Bo getPop3Bo() {
		return pop3Bo;
	}

	public void setPop3Bo(Pop3Bo pop3Bo) {
		this.pop3Bo = pop3Bo;
	}

	@Override
	public void init(PluginInitParameter initParameters)
			throws PluginSessionRunException {
		Parameter[] parameters=initParameters.getParameters();
		for(int i=0;i<parameters.length;i++){
			switch (parameters[i].getKey()) {
			case POP3_PLUGIN_IP:
				pop3Bo.setIp(parameters[i].getValue());
				break;
			case POP3_PLUGIN_PORT:
				pop3Bo.setPort(parameters[i].getValue());
				break;
			case POP3_PLUGIN_USERNAME:
				pop3Bo.setUserName(parameters[i].getValue());
				break;
			case POP3_PLUGIN_USERPASSWORD:
				pop3Bo.setUserPassword(parameters[i].getValue());
				break;

			default:
				break;
			}
		}
	}

	@Override
	public void destory() {
		Pop3Collector.close();
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
			PluginSessionContext context) throws PluginSessionRunException {
		if (logger.isTraceEnabled()) {
			logger.trace("PluginSession execute start");
		}
		if (executorParameter instanceof PluginArrayExecutorParameter) {

			PluginArrayExecutorParameter arrayP = (PluginArrayExecutorParameter) executorParameter;
			Parameter[] parameters = arrayP.getParameters();
			List<String> cmds = null;
			if(parameters != null){
				cmds = new ArrayList<String>(parameters.length);
			}else{
				throw new RuntimeException("未发现采集命令");
			}
			Map<String, String> paramMap = new HashMap<String, String>(2, 0.5f);
			for (Parameter parameter : parameters) {
				logger.info("plugin excute[" + parameter.getKey() + ":" + parameter.getValue() + "]");
				if (parameter.getKey().equalsIgnoreCase(COMMAND_POP3)) {
					cmds.add(parameter.getValue());
				} else {
					paramMap.put(parameter.getKey(), parameter.getValue());
				}
			}
			
			Set<String> keySet = paramMap.keySet();
			Iterator<String> iterator = keySet.iterator();
			while (iterator.hasNext()) {
				String key = iterator.next();
				for(int i = 0; i < cmds.size(); i++){
					String cmd = cmds.get(i);
					cmd = StringUtils.replaceOnce(cmd, "${" + key + "}", paramMap.get(key));
					cmds.set(i, cmd);
				}
			}
			
			String result;
			PluginResultSet resultSet = new PluginResultSet();
			int index = 0;
			if(null==object){
				anyClass = StringUtils.substringBeforeLast(cmds.get(0), "."); //截取到类
				getPop3Collector();
			}
			for(String cmd : cmds){
				try{
					result = this.executeCMD(cmd);
					if(StringUtils.isNotBlank(result))
						resultSet.putValue(0, index++, result);
				}catch(Exception e){
					throw new RuntimeException(e);
				}
			}
			return resultSet;
		} else {
			return null;
		}
	}
	private String executeCMD(String cmd) throws InstantiationException, IllegalAccessException, 
	NoSuchMethodException, SecurityException, IllegalArgumentException,
	InvocationTargetException, ClassNotFoundException {

						if(StringUtils.isNotBlank(cmd)) {
						String anyMethod = StringUtils.substringAfterLast(cmd, "."); //截取到方法
						Method method = thisClass.getMethod(anyMethod, Pop3Bo.class);
						Object resultObj=null;
						try {
							resultObj = method.invoke(object, this.pop3Bo);
							return resultObj.toString();
						} catch (Exception e) {
							logger.error(e.getMessage(),e);
						}
						}
						return null;
	}

	@Override
	public boolean check(PluginInitParameter initParameters)
			throws PluginSessionRunException {
		return false;
	}
}
