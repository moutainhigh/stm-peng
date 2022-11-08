package com.mainsteam.stm.plugin.weblogic;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

import com.mainsteam.stm.plugin.parameter.JBrokerParameter;

public class JMSResCollector {
	private static final String[] TYPES = {"", "", ""};
	/**
	 * JMS连接名称
	 * @param oneobj
	 * @return
	 */
	public static String getConnName(JBrokerParameter oneobj) {
		return WeblogicJMXcollectUtil.getChildResValueBySpecisMBeanServer(oneobj, "JMSRuntime@#Connections@#Name", TYPES);
	}
	/**
	 * 当前会话数
	 * @param oneobj
	 * @return
	 */
	public static String getSessionsCurrentCount(JBrokerParameter oneobj) {
		return WeblogicJMXcollectUtil.getChildResValueBySpecisMBeanServer(oneobj, "JMSRuntime@#Connections@#SessionsCurrentCount", TYPES);
	}
	/**
	 * JMS 服务名称
	 * @param oneobj
	 * @return
	 */
	public static String getServerName(JBrokerParameter oneobj) {
		return WeblogicJMXcollectUtil.getChildResValueBySpecisMBeanServer(oneobj, "JMSRuntime@#JMSServers@#Name", TYPES);
	}
	/**
	 * 当前字节数
	 * @param oneobj
	 * @return
	 */
	public static String getBytesCurrentCount(JBrokerParameter oneobj) {
		return WeblogicJMXcollectUtil.getChildResValueBySpecisMBeanServer(oneobj, "JMSRuntime@#JMSServers@#BytesCurrentCount", TYPES);
	}
	/**
	 * 当前消息数
	 * @param oneobj
	 * @return
	 */
	public static String getMessagesCurrentCount(JBrokerParameter oneobj) {
		return WeblogicJMXcollectUtil.getChildResValueBySpecisMBeanServer(oneobj, "JMSRuntime@#JMSServers@#MessagesCurrentCount", TYPES);
	}
	/**
	 * 挂起的消息数
	 * @param oneobj
	 * @return
	 */
	public static String getMessagesPendingCount(JBrokerParameter oneobj) {
		return WeblogicJMXcollectUtil.getChildResValueBySpecisMBeanServer(oneobj, "JMSRuntime@#JMSServers@#MessagesPendingCount", TYPES);
	}
	/**
	 * 接收到的消息数
	 * @param oneobj
	 * @return
	 */
	public static String getMessagesReceivedCount(JBrokerParameter oneobj) {
		return WeblogicJMXcollectUtil.getChildResValueBySpecisMBeanServer(oneobj, "JMSRuntime@#JMSServers@#MessagesReceivedCount", TYPES);
	}
	/**
	 * JMS服务健康状态
	 * @param oneobj
	 * @return
	 */
	public static String getHealthState(JBrokerParameter oneobj) {
	  String str = WeblogicJMXcollectUtil.getChildResValueBySpecisMBeanServer(oneobj, "JMSRuntime@#JMSServers@#HealthState", TYPES);
	  String[] healths = str.split("\n");
	  Pattern pattern = Pattern.compile("State:(.+?),");
	  StringBuffer sb = new StringBuffer();
	  String temp = "";
	  for(String health : healths){
		  Matcher matcher = pattern.matcher(health);
		  if(matcher.find()){
			  temp = matcher.group(1);
			  sb.append(health.split("=")[0]);
			  sb.append("=");
			  sb.append(temp);
			  sb.append("\n");
		  }else{
			  sb.append(health);
			  sb.append("\n");
		  }
	  }
	  return sb.toString();
	}
}
