package com.mainsteam.stm.pluginsession;

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

import com.mainsteam.stm.errorcode.CapcityErrorCodeConstant;
import com.mainsteam.stm.pluginsession.exception.PluginSessionRunException;
import com.mainsteam.stm.pluginsession.parameter.JBrokerParameter;
import com.mainsteam.stm.pluginsession.parameter.Parameter;
import com.mainsteam.stm.pluginsession.parameter.PluginArrayExecutorParameter;
import com.mainsteam.stm.pluginsession.parameter.PluginExecutorParameter;
import com.mainsteam.stm.pluginsession.parameter.PluginInitParameter;

/**
 * 基础session，所有的session都继承自这个类
 * 
 * @author xiaop_000
 *
 */
public abstract class BaseBrokerSession implements PluginSession {

	private static final String PORT = "Port";
	private static final String IP = "IP";
	private static final String COMMAND = "COMMAND";
	private static final String UUID = "uuid";
	private static final Log logger = LogFactory.getLog(BaseBrokerSession.class);
	// 两个参数
	private static final int DOUBLE_PARAMS = 2;

	private JBrokerParameter parameter;

	public BaseBrokerSession() {
		this.parameter = new JBrokerParameter();
	}

	// 在具体的session中实现
	@Override
	public void init(PluginInitParameter initParameters)
			throws PluginSessionRunException {
	}

	@Override
	public void destory() {
	}

	@Override
	public void reload() {
	}

	// 在具体的session中实现
	@Override
	public boolean isAlive() {
		return true;
	}

	@Override
	public PluginResultSet execute(
			PluginExecutorParameter<?> executorParameter,
			PluginSessionContext context) throws PluginSessionRunException {
		if (executorParameter instanceof PluginArrayExecutorParameter) {

			PluginArrayExecutorParameter arrayP = (PluginArrayExecutorParameter) executorParameter;
			Parameter[] parameters = arrayP.getParameters();
			List<String> cmds = null;
			if (parameters != null) {
				cmds = new ArrayList<String>(parameters.length);
			} else {
				throw new PluginSessionRunException(
						CapcityErrorCodeConstant.ERR_CAPCITY_COLLECT_NULLCOMMAND,
						"没有采集命令");
			}
			Map<String, String> paramMap = new HashMap<String, String>(2, 0.5f);
			for (Parameter parameter : parameters) {
				logger.info("plugin excute[" + parameter.getKey() + ":"
						+ parameter.getValue() + "]");
				if (parameter.getKey().equalsIgnoreCase(COMMAND)) {
					cmds.add(parameter.getValue());
				} else {
					paramMap.put(parameter.getKey(), parameter.getValue());
				}
			}

			if (cmds.isEmpty()) { // 没有SQL查询语句，如果发现参数是ip+port的话，直接拼接后返回
				if (paramMap.size() == DOUBLE_PARAMS) {
					Set<String> keySet = paramMap.keySet();
					if (keySet.contains(IP) && keySet.contains(PORT)) {
						PluginResultSet resultSet = new PluginResultSet();
						resultSet.putValue(0, 0, this.parameter.getIp() + ":"
								+ this.parameter.getPort());
						return resultSet;
					}
				}

			}

			Set<String> keySet = paramMap.keySet();
			Iterator<String> iterator = keySet.iterator();
			while (iterator.hasNext()) {
				String key = iterator.next();
				for (int i = 0; i < cmds.size(); i++) {
					String cmd = cmds.get(i);
					if(StringUtils.equals(key, UUID)) {
						this.parameter.setUuid(paramMap.get(key));
					}else{
						cmd = StringUtils.replaceOnce(cmd, "${" + key + "}",
								paramMap.get(key));
						cmds.set(i, cmd);
						
					}
				}
			}

			String result;
			PluginResultSet resultSet = new PluginResultSet();
			int index = 0;
			for (String cmd : cmds) {
				try {
					result = this.executeCMD(cmd);
					if (StringUtils.isNotBlank(result))
						resultSet.putValue(0, index++, result);
				} catch (Exception e) {
					throw new PluginSessionRunException(
							CapcityErrorCodeConstant.ERR_CAPCITY_COLLECT_FAILED,
							"采集异常", e);
				}
			}
			return resultSet;
		} else {
			return null;
		}

	}

	private String executeCMD(String cmd) throws PluginSessionRunException {

		if(StringUtils.isNotBlank(cmd)) {
	
			String anyClass = StringUtils.substringBeforeLast(cmd, "."); //截取到类
			String anyMethod = StringUtils.substringAfterLast(cmd, "."); //截取到方法
			
			Class<?> thisClass;
			try {
				thisClass = this.getClass().getClassLoader().loadClass(anyClass);
			} catch (ClassNotFoundException e) {
				if(logger.isErrorEnabled()){
					logger.error(e.getMessage() + ".Middleware collects failed,cause of can't find "
							+ "the class and method:" + cmd, e);
				}
				throw new PluginSessionRunException(CapcityErrorCodeConstant.ERR_CAPCITY_COLLECT_NULLCOMMAND, e);
			}
			
			Object object;
			try {
				object = thisClass.newInstance();
			} catch (Exception e) {
				if(logger.isErrorEnabled()){
					logger.error(e.getMessage() + ". The class [" + cmd + "] instantiation failed. Maybe this"
							+ "class has not null constructor method or can't access the constructor method.", e);
				}
				throw new PluginSessionRunException(CapcityErrorCodeConstant.ERR_CAPCITY_ERROR_COMMAND, e);
			} 
			
			Method method;
			try {
				method = thisClass.getMethod(anyMethod, JBrokerParameter.class);
			} catch (NoSuchMethodException e) {
				if(logger.isErrorEnabled()){
					logger.error(e.getMessage() + ".Middleware class method error,cause of can't find "
							+ "the method of class :" + cmd, e);
				}
				throw new PluginSessionRunException(CapcityErrorCodeConstant.ERR_CAPCITY_COLLECT_NULLCOMMAND, e);
			} catch (SecurityException e) {
				if(logger.isErrorEnabled()){
					logger.error(e.getMessage() + ". Cause of java security,the failed class and method:" + cmd, e);
				}
				throw new PluginSessionRunException(CapcityErrorCodeConstant.ERR_CAPCITY_JAVA_SECURITY, e);
			}
			
			Object resultObj;
			try {
				resultObj = method.invoke(object, this.parameter);
			} catch (IllegalAccessException e) {
				if(logger.isErrorEnabled()){
					logger.error(e.getMessage() + ".Can't access the method of class[" + cmd + "]", e);
				}
				throw new PluginSessionRunException(CapcityErrorCodeConstant.ERR_CAPCITY_ACCESS_METHOD, e);
				
			} catch (IllegalArgumentException e) {
				/*
				 * 如果该方法是实例方法，且指定对象参数不是声明底层方法的类或接口（或其中的子类或实现程序）的实例；
				 * 如果实参和形参的数量不相同；如果基本参数的解包转换失败；如果在解包后，无法通过方法调用转换将参数值转换为相应的形参类型。
				 */
				if(logger.isErrorEnabled()) {
					logger.error(e.getMessage() + ". Invoke method failed ["+ cmd + "]", e);
				}
				throw new PluginSessionRunException(CapcityErrorCodeConstant.ERR_CAPCITY_METHOD_OF_PARAMS, e);
			} catch (InvocationTargetException e) {
				if(logger.isErrorEnabled()){
					logger.error(e.getMessage() + ". Invoke method error.[" + cmd + "]", e);
				}
				Throwable throwable = e.getTargetException();
				if(throwable instanceof PluginSessionRunException)
					throw (PluginSessionRunException)throwable;
				else
					throw new PluginSessionRunException(CapcityErrorCodeConstant.ERR_CAPCITY_REFLECT_FAILED, e);
			}
			
			return resultObj.toString();
		
		}
	
		return null;
	}

	public JBrokerParameter getParameter() {
		return parameter;
	}

	public void setParameter(JBrokerParameter parameter) {
		this.parameter = parameter;
	}

}
