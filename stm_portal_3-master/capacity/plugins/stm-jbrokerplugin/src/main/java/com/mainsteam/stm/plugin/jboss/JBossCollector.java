package com.mainsteam.stm.plugin.jboss;

import java.io.IOException;
import java.net.URL;
import java.text.DateFormat;
import java.text.NumberFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Set;

import javax.management.Attribute;
import javax.management.AttributeList;
import javax.management.InstanceNotFoundException;
import javax.management.MBeanServerConnection;
import javax.management.MalformedObjectNameException;
import javax.management.ObjectName;
import javax.management.ReflectionException;
import javax.management.j2ee.statistics.BoundedRangeStatistic;
import javax.management.j2ee.statistics.CountStatistic;
import javax.management.remote.JMXConnector;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.jboss.management.j2ee.statistics.JVMStatsImpl;

import com.mainsteam.stm.plugin.parameter.JBrokerParameter;

public class JBossCollector{
	
	private static final String WEB_APP_SESSION = "webAppSession";
	private static final String WEB_APP = "webApp";
	private static final String NAME_SUN_MICROSYSTEMS_INC = "name=Sun Microsystems Inc.";
	private static final String NAME_ORACLE_CORPORATION = "name=Oracle Corporation";
	
	private static final String ATTRIBUTE_SERVER="server";
	private static final String ATTRIBUTE_SERVERINFO="serverInfo";
	private static final String ATTRIBUTE_SYSTEMPROPERTIES="systemProperties";
	private static final String ATTRIBUTE_THREADPOOL="threadPool";
	private static final String ATTRIBUTE_INUSECONNECTION="inUseConnection";
	private static final String ATTRIBUTE_SERVERPROVIDER="serverProvider";
	private static final String ATTRIBUTE_SERVERCONFIG="serverConfig";
	
	private static final String OBJECTNAME_JVM="jvm";
	private static final String OBJECTNAME_THREAD="thread";
	private static final String OBJECTNAME_CONNECTOR="connector";
	private static final String OBJECTNAME_JMS="jms";
	private static final String OBJECTNAME_JDBC="jdbc";
	
	private static JMXParser parser;
	private static final Log debug = LogFactory.getLog(JBossCollector.class);
	
	public JBossCollector(){
		if(parser == null)
			parser = new JMXParser();
	}
	
	/**
	 * ??????????????? ???????????? ??????????????????????????? ?????????JVM????????? ???????????? JBoss??????
	 * @param one
	 * @return
	 */
	public String findServer(JBrokerParameter one){
		
		AttributeList attributes = getCommonAttributes(ATTRIBUTE_SERVER, one.getIp(), one.getPort());
		if(attributes != null && attributes.size() > 0){
			StringBuffer sb = new StringBuffer();
			//???xml???????????????
			for(Object obj : attributes){
				Attribute att = (Attribute)obj;
				sb.append("<").append(att.getName()).append(">");
				if("BuildDate".equals(att.getName())){
					String str = att.getValue().toString();
					DateFormat format = DateFormat.getDateTimeInstance(DateFormat.LONG, DateFormat.LONG, Locale.US);
					SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
					try {
						debug.info("FormatDate ---- " + str);
						Date date = format.parse(str);
						String result = sdf.format(date);
						sb.append(result);
					} catch (ParseException e) {
						SimpleDateFormat df = new SimpleDateFormat("MMMMM dd yyyy", Locale.US);
						try {
							Date date = df.parse(str);
							String result = sdf.format(date);
							sb.append(result);
						} catch (ParseException e1) {
							debug.warn("JBoss Formats <" + att.getName() + ">Attribute Failing -- " + str);
							sb.append(str);
						}
					}
					
				}else if("StartDate".equals(att.getName())){
					Date date = (Date)att.getValue();
					SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
					String result = format.format(date);
					sb.append(result);
					
				}else{
					if((att.getValue()+"").equals("true")){
						sb.append("1");
					}
					else if((att.getValue()+"").equals("false")){
						sb.append("0");
					}
					else{
					sb.append(att.getValue());
					}
				}
				sb.append("</").append(att.getName()).append(">");
				
			}
			return sb.toString();
		}else
			return "<Started>0</Started>";
	}
	
	/**
	 * ??????????????????
	 * JVM??????????????? ??????????????? ??????????????? ????????? ?????????????????? ?????????????????? ???????????? ????????? JVM?????? Java?????? ??????????????? ?????????????????????
	 * @param one
	 * @return
	 */
	public String findServerInfo(JBrokerParameter one){
		
		AttributeList attributes = getCommonAttributes(ATTRIBUTE_SERVERINFO, one.getIp(), one.getPort());
		StringBuffer sb = new StringBuffer();
		if(attributes != null && attributes.size() > 0){
			//???xml???????????????
			long freeMemory = 0l; //????????????
			long totalMemory = 0l; // ?????????
			for(Object obj : attributes){
				Attribute att = (Attribute)obj;
				sb.append("<").append(att.getName()).append(">");
				if("FreeMemory".equals(att.getName())){
					freeMemory = (Long)att.getValue();
					sb.append(freeMemory);
				}else if("TotalMemory".equals(att.getName())){
					totalMemory = (Long)att.getValue();
					sb.append(totalMemory);
				}else{
					sb.append(att.getValue());
				}
				sb.append("</").append(att.getName()).append(">");
				
			}
			//???????????? ?????????????????????????????????
			long usedMemory = totalMemory - freeMemory;
			double memoryUtil = 0d;
			String memoryUtilStr = "0";
			if(totalMemory != 0 && usedMemory != 0){
				memoryUtil = (double)usedMemory/(double)totalMemory;
				memoryUtil = memoryUtil * 100;
				NumberFormat percentFormat = NumberFormat.getNumberInstance();
				percentFormat.setMaximumFractionDigits(2);
				memoryUtilStr = percentFormat.format(memoryUtil);
			}
			sb.append("<MemoryUtil>").append(memoryUtilStr).append("</MemoryUtil>").append("<usedMemory>")
				.append(usedMemory).append("</usedMemory>");
		}
		return sb.toString();
	}
	
	/**
	 * ??????????????????
	 * @param one
	 * @return ???????????????
	 */
	public String findSystemProperties(JBrokerParameter one){
		AttributeList attributes = getCommonAttributes(ATTRIBUTE_SYSTEMPROPERTIES, one.getIp(), one.getPort());
		StringBuffer sb = new StringBuffer();
		if(attributes != null && attributes.size() > 0){
			for(Object obj : attributes){
				Attribute att = (Attribute)obj;
				sb.append("<").append(att.getName()).append(">");
				sb.append(att.getValue());
				sb.append("</").append(att.getName()).append(">");
				
			}
		}
		return sb.toString();
	}
	
	/**
	 * ??????????????????
	 *  ??????????????? ??????????????? ????????? ?????????????????? ???????????? ????????? ??????????????? ???????????????
	 * @param one
	 * @return
	 */
	public String findThreadPool(JBrokerParameter one){
		AttributeList attributes = getCommonAttributes(ATTRIBUTE_THREADPOOL, one.getIp(), one.getPort());
		StringBuffer sb = new StringBuffer();
		if(attributes != null && attributes.size() > 0){
			for(Object obj : attributes){
				Attribute att = (Attribute)obj;
				sb.append("<").append(att.getName()).append(">");
				sb.append(att.getValue());
				sb.append("</").append(att.getName()).append(">");
				
			}
		}
		return sb.toString();
	}
	
	/**
	 * ??????????????????
	 * ???????????????????????????
	 * @param one
	 * @return
	 */
	public String findInUseConnection(JBrokerParameter one){
		AttributeList attributes = getCommonAttributes(ATTRIBUTE_INUSECONNECTION, one.getIp(), one.getPort());
		StringBuffer sb = new StringBuffer();
		if(attributes != null && attributes.size() > 0){
			for(Object obj : attributes){
				Attribute att = (Attribute)obj;
				sb.append("<").append(att.getName()).append(">");
				sb.append(att.getValue());
				sb.append("</").append(att.getName()).append(">");
				
			}
		}
		return sb.toString();
		
	}
	
	/**
	 * ??????????????????
	 * ?????????????????? ??????????????? ?????? ????????? ????????????????????? ?????????????????? ??????????????????
	 * @param one
	 * @return
	 */
	public String findServerProvider(JBrokerParameter one){
		AttributeList attributes = getCommonAttributes(ATTRIBUTE_SERVERPROVIDER, one.getIp(), one.getPort());
		StringBuffer sb = new StringBuffer();
		if(attributes != null && attributes.size() > 0){
			for(Object obj : attributes){
				Attribute att = (Attribute)obj;
				sb.append("<").append(att.getName()).append(">");
				sb.append(att.getValue());
				sb.append("</").append(att.getName()).append(">");
				
			}
		}
		return sb.toString();
		
	}
	
	/**
	 * ??????????????????
	 * JVM???????????? JVM?????????????????? ??????????????? ???????????????
	 * @param one
	 * @return
	 */
	public String findJVM(JBrokerParameter one){
		Map<String, String[]> map = parser.getJmxMap();
		String[] objectNames = map.get(OBJECTNAME_JVM);
		JMXConnector connector = (JMXConnector)JMXConnectionFactory.getConnection(one.getIp(), one.getPort());
		if(objectNames != null && objectNames.length == 2){
			if(connector != null){
				MBeanServerConnection mBeanServer;
				ObjectName object;
				try {
					mBeanServer = connector.getMBeanServerConnection();
					object = new ObjectName(objectNames[0]);
					Set<ObjectName> set = mBeanServer.queryNames(object, null);
					if(set != null){
						Iterator<ObjectName> iterator = set.iterator();
						while(iterator.hasNext()){
							ObjectName objectName = iterator.next();
							String objectNameStr = objectName.getCanonicalName(); 
							if(objectNameStr.contains("name=localhost") || objectNameStr.contains(NAME_ORACLE_CORPORATION) ||objectNameStr.contains(NAME_SUN_MICROSYSTEMS_INC)){
								object = objectName;
								break;
							}
						}
					}
				} catch (MalformedObjectNameException e) {
					e.printStackTrace();
					debug.error("URL Error!"+e.getMessage(), e);
					return null;
				} catch (IOException e){
					e.printStackTrace();
					debug.error("IO Error!"+e.getMessage(), e);
					return null;
				} 
				
				try {
					AttributeList attributes = mBeanServer.getAttributes(object, objectNames[1].split(","));
					StringBuffer sb = new StringBuffer();
					if(attributes != null && attributes.size() > 0){
						for(Object obj : attributes){
							Attribute att = (Attribute)obj;
							if("stats".equals(att.getName())){
								JVMStatsImpl stats = (JVMStatsImpl)att.getValue();
								CountStatistic count = stats.getUpTime();
								long startTime = count.getStartTime();
								// JVM????????????
								Date startDate = new Date(startTime);
								DateFormat dFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
								String startDateStr = dFormat.format(startDate);
								sb.append("<StartDate>").append(startDateStr).append("</StartDate>");
								// JVM??????????????????
								long lastTime = count.getLastSampleTime();
								Date lastDate = new Date(lastTime);
								String lastTimeStr = dFormat.format(lastDate);
								sb.append("<LastSampleTime>").append(lastTimeStr).append("</LastSampleTime>");
								//?????????  ?????????
								BoundedRangeStatistic rangeStatistic = (BoundedRangeStatistic)stats.getStatistic("HeapSize");
								long lowerHeap = rangeStatistic.getLowWaterMark();
								long upperHeap = rangeStatistic.getHighWaterMark();
								sb.append("<MiniumHeap>").append(lowerHeap).append("</MiniumHeap>");
								sb.append("<MaxHeap>").append(upperHeap).append("</MaxHeap>");
							}
						}
					}
					return sb.toString();
				} catch (InstanceNotFoundException e) {
					debug.error("No ObjectName --- " + object.toString()+e.getMessage(), e);
				} catch (ReflectionException e) {
					debug.error("Java Reflect Error!"+e.getMessage(), e);
				} catch (IOException e) {
					debug.error("IO Error!"+e.getMessage(), e);
				} 
			}else{
				throw new RuntimeException("get JMX connection pool object failed!");
			}
		}else{
			debug.error("JBoss JMX get ObjectName or Attribute Failing");
		}
		return null;
	}
	
	/**
	 * ??????????????????
	 * ???????????? HomeDir
	 * @param one
	 * @return
	 */
	public String findServerConfig(JBrokerParameter one){
		AttributeList attributes = getCommonAttributes(ATTRIBUTE_SERVERCONFIG, one.getIp(), one.getPort());
		StringBuffer sb = new StringBuffer();
		if(attributes != null && attributes.size() > 0){
			for(Object obj : attributes){
				Attribute att = (Attribute)obj;
				if("JBossHome".equals(att.getName())){
					URL url = (URL)att.getValue();
					String str = url.getPath().substring(1);
					sb.append("<HomeDir>").append(str).append("</HomeDir>");
				}else{
					sb.append("<").append(att.getName()).append(">");
					sb.append(att.getValue());
					sb.append("</").append(att.getName()).append(">");
				}
				
			}
		}
		return sb.toString();
	}
	
	/**
	 * ???????????????
	 * JBoss??????????????? JBoss????????????????????? JBoss??????????????????
	 * @param one
	 * @return
	 */
	public String findThread(JBrokerParameter one){
		Map<String, String[]> map = parser.getJmxMap();
		String[] objectNames = map.get(OBJECTNAME_THREAD);
		JMXConnector connector = (JMXConnector)JMXConnectionFactory.getConnection(one.getIp(), one.getPort());
		if(objectNames != null && objectNames.length == 2){
			if(connector != null){
				MBeanServerConnection mBeanServer;
				ObjectName object;
				try {
					mBeanServer = connector.getMBeanServerConnection();
				} catch (IOException e) {
					debug.error("IO Error"+e.getMessage(), e);
					return null;
				}
				
				try {
					object = new ObjectName(objectNames[0]);
					Set<ObjectName> set = mBeanServer.queryNames(object, null);
					if(set != null){
						Iterator<ObjectName> iterator = set.iterator();
						while(iterator.hasNext()){
							ObjectName objectName = iterator.next();
							String objectNameStr = objectName.getCanonicalName(); 
							if(objectNameStr.contains("name=http")){
								object = objectName;
								break;
							}
						}
					}
				} catch (MalformedObjectNameException e) {
					debug.error("URL Error"+e.getMessage(), e);
					return null;
				} catch (IOException e){
					debug.error("IO Error"+e.getMessage(), e);
					return null;
				}
				
				try {
					AttributeList attributes = mBeanServer.getAttributes(object, objectNames[1].split(","));
					StringBuffer sb = new StringBuffer();
					if(attributes != null && attributes.size() > 0){
						for(Object obj : attributes){
							Attribute att = (Attribute)obj;
							sb.append("<").append(att.getName()).append(">").append(att.getValue()).append("</")
								.append(att.getName()).append(">");
						}
					}
					return sb.toString();
				} catch (InstanceNotFoundException e) {
					debug.error("No ObjectName --- " + object.toString() + e.getMessage(), e);
				} catch (ReflectionException e) {
					debug.error("Java reflect Error" + e.getMessage(), e);
				} catch (IOException e) {
					debug.error("IO Error" + e.getMessage(), e);
				}finally{
				}
			}else{
				throw new RuntimeException("get JMX connection pool Object Fail");
			}
		}else{
			debug.error("JBoss JMX get ObjectName or Attribute Fail");
		}
		return null;
	}
	
	/**
	 * ??????Web???????????? 
	 * ???????????? ?????????????????? ???????????? ????????????http?????? ?????????????????? ???????????? ????????? ?????????????????? ???????????? ????????????????????? ????????????????????? ??????????????? ?????????????????????
	 * @param one
	 * @return
	 */
	public String findConnector(JBrokerParameter one){
		Map<String, String[]> map = parser.getJmxMap();
		String[] objectNames = map.get(OBJECTNAME_CONNECTOR);
		JMXConnector connector = (JMXConnector)JMXConnectionFactory.getConnection(one.getIp(), one.getPort());
		if(objectNames != null && objectNames.length == 2){
			if(connector != null){
				MBeanServerConnection mBeanServer;
				ObjectName object;
				try {
					mBeanServer = connector.getMBeanServerConnection();
				} catch (IOException e) {
					debug.error("IO Error" + e.getMessage(), e);
					return null;
				}
				
				try {
					object = new ObjectName(objectNames[0]);
					Set<ObjectName> set = mBeanServer.queryNames(object, null);
					if(set != null){
						Iterator<ObjectName> iterator = set.iterator();
						StringBuffer sb = new StringBuffer();
						boolean flag = false;
						AttributeList attributes = null;
						while(iterator.hasNext()){
							ObjectName objectName = iterator.next();
							try {
								attributes = mBeanServer.getAttributes(objectName, objectNames[1].split(","));
								if(attributes != null && attributes.size() > 0){
									
									for(Object obj : attributes){
										Attribute att = (Attribute)obj;
										if("protocol".equals(att.getName())){
											String protocol = att.getValue().toString();
											if(protocol != null && protocol.toUpperCase().contains("HTTP")){
												flag = true;
												break;
											}
										}
									}
									
								}
								
							} catch (InstanceNotFoundException e) {
								debug.error("No ObjectName --- " + object.toString() + e.getMessage(), e);
							} catch (ReflectionException e) {
								e.printStackTrace();
								debug.error("Java reflect Error" + e.getMessage(), e);
							} catch (IOException e) {
								e.printStackTrace();
								debug.error("IO Error" + e.getMessage(), e);
							}
							if(flag)
								break;
						}
						if(attributes != null){
							for(Object obj : attributes){
								Attribute att = (Attribute)obj;
								Object attValue = att.getValue();
								if("minSpareThreads-maxSpareThreads".contains(att.getName())){// ????????????????????? ?????????????????????
									if(null == attValue)
										attValue = "0";
								//????????????????????? ????????????
								}else if("URIEncoding-strategy".contains(att.getName())){
									if(null == attValue)
										attValue = "?????????";
								}else if("keepAliveTimeout".equals(att.getName())){
									if(null == attValue)
										attValue = "?????????";
									else if("-1".equals(attValue.toString())){
										attValue = "????????????";
									}else
										attValue = attValue + "s";
								}
								sb.append("<").append(att.getName()).append(">").append(attValue.toString()).append("</")
									.append(att.getName()).append(">");
							}
						}
						return sb.toString();
					}
				} catch (MalformedObjectNameException e) {
					debug.error("URL Error" + e.getMessage(), e);
					return null;
				} catch (IOException e){
					debug.error("IO Error" + e.getMessage(), e);
					return null;
				}finally{
				}
				
			}else{
				throw new RuntimeException("get JMX connection pool object Fail");
			}
		}else{
			debug.error("get ObjectName or Attribute failed");
		}
		return null;
	}
	
	/**
	 * ??????????????????
	 * JMS??????????????? JMS???????????? JMS??????????????? JMS????????????????????????
	 * @param one
	 * @return
	 */
	public String findJMS(JBrokerParameter one){
		Map<String, String[]> map = parser.getJmxMap();
		String[] objectNames = map.get(OBJECTNAME_JMS);
		JMXConnector connector = (JMXConnector)JMXConnectionFactory.getConnection(one.getIp(), one.getPort());
		if(objectNames != null && objectNames.length == 2){
			if(connector != null){
				MBeanServerConnection mBeanServer;
				ObjectName object;
				try {
					mBeanServer = connector.getMBeanServerConnection();
				} catch (IOException e) {
					e.printStackTrace();
					debug.error("IO Error", e);
					return null;
				}
				
				try {
					object = new ObjectName(objectNames[0]);
					Set<ObjectName> set = mBeanServer.queryNames(object, null);
					if(set != null){
						Iterator<ObjectName> iterator = set.iterator();
						StringBuffer sb = new StringBuffer();
						while(iterator.hasNext()){
							ObjectName objectName = iterator.next();
							if(objectName.toString().toLowerCase().contains("name=jms")){
								object = objectName;
								break;
							}
						}
						try {
							AttributeList attributes = mBeanServer.getAttributes(object, objectNames[1].split(","));
							if(attributes != null && attributes.size() > 0){
								
								for(Object obj : attributes){
									Attribute att = (Attribute)obj;
									sb.append("<").append(att.getName()).append(">").append(att.getValue()).append("</")
										.append(att.getName()).append(">");
								}
							}
							
						} catch (InstanceNotFoundException e) {
							e.printStackTrace();
							debug.error("No ObjectName --- " + object.toString(), e);
						} catch (ReflectionException e) {
							debug.error("Java reflect error" + e.getMessage(), e);
						} catch (IOException e) {
							debug.error("IO Error" + e.getMessage(), e);
						}
						return sb.toString();
					}
				} catch (MalformedObjectNameException e) {
					debug.error("URL Error" + e.getMessage(), e);
					return null;
				} catch (NullPointerException e) {
					debug.error("Null pointer exception" + e.getMessage(), e);
					return null;
				} catch (IOException e){
					debug.error("IO exception" + e.getMessage(), e);
					return null;
				} 
				
			}else{
				throw new RuntimeException("get JMX connction pool object exception");
			}
		}else{
			debug.error("get ObjectName or Attribute");
		}
		return null;
	}
	
	/**
	 * ??????????????????
	 * JDBC????????????????????? ?????????????????? ??????????????? ??????????????? ???????????????
	 * @param one
	 * @return
	 */
	public String findJDBC(JBrokerParameter one) {
		Map<String, String[]> map = parser.getJmxMap();
		String[] objectNames = map.get(OBJECTNAME_JDBC);
		JMXConnector connector = (JMXConnector)JMXConnectionFactory.getConnection(one.getIp(), one.getPort());
		if(objectNames != null && objectNames.length == 2){
			if(connector != null){
				MBeanServerConnection mBeanServer;
				ObjectName object;
				try {
					mBeanServer = connector.getMBeanServerConnection();
					object = new ObjectName(objectNames[0]);
					/**
					 * ?????????????????????ObjectName??????
					 */
					Set<ObjectName> set = mBeanServer.queryNames(object, null);
					if(set != null){
						Iterator<ObjectName> iterator = set.iterator();
						StringBuffer sb = new StringBuffer();
						//??????????????????AttributeList??????List?????????????????????list??????
						List<AttributeList> list = new ArrayList<AttributeList>();
						while(iterator.hasNext()){
							ObjectName objectName = iterator.next();
							try {
								AttributeList attributes = mBeanServer.getAttributes(objectName, objectNames[1].split(","));
								if(attributes != null && attributes.size() > 0){
									for(Object obj : attributes){
										Attribute att = (Attribute)obj;
										/**
										 * ??????ManagedConnectionFactoryName?????????????????????????????????ObjectName????????????????????????ObjectName????????????
										 * ??????ConnectionInterface??????????????????DS?????????JDBC?????????
										 */
										if("ManagedConnectionFactoryName".equals(att.getName())){
											ObjectName temp = (ObjectName)att.getValue();
											try {
												String connectionInterface = (String)mBeanServer.getAttribute(temp, "ManagedConnectionFactoryClass");
												if(connectionInterface != null && connectionInterface.contains("org.jboss.resource.adapter.jdbc.")){
													list.add(attributes);
												}
												
											} catch (Exception e) {
												debug.error("Attribute Error." + e.getMessage(), e);
												continue;
											} 
										}
									}
								}
								
							} catch (InstanceNotFoundException e) {
								debug.error("No ObjectName --- " + object.toString() + e.getMessage(), e);
							} catch (ReflectionException e) {
								debug.error("Java reflect Error" + e.getMessage(), e);
							} catch (IOException e) {
								debug.error("IO Error" + e.getMessage(), e);
							} 
							
						}
						if(list.size() > 0){
							int index = 0;
							for(AttributeList attList : list){
								sb.append(index).append("\t");
								for(Object obj : attList){
									Attribute att = (Attribute)obj;
									if(att.getName().equals("StateString")){
										if(att.getValue().equals("Started"))
											sb.append("1").append("\t");
										else {
											sb.append("0").append("\t");
										}
									}
									else{
									sb.append(att.getValue()).append("\t");
									}
								}
								sb.append("\n");
							}
						}
						return sb.toString();
					}
				} catch (MalformedObjectNameException e) {
					debug.error("URL Error" + e.getMessage(), e);
					return null;
				} catch (IOException e){
					debug.error("IO Error" + e.getMessage(), e);
					return null;
				} 
				
			}else{
				throw new RuntimeException("get JMX connection pool object failed");
			}
		}else{
			debug.error("get ObjectName or Attribute failed");
		}
		return null;
	}
	
	/**
	 * ??????????????????
	 * Web?????????????????? ???????????? ?????????????????? ?????????????????? ?????????????????? ?????????????????????
	 * 
	 * @param one
	 * @return
	 */
	public String findWebApp(JBrokerParameter one){
		Map<String, String[]> map = parser.getJmxMap();
		/**
		 * ??????JBoss_jmx.xml??????????????????webApp???webAppSession?????????ObjectName?????????????????????????????????????????????ObjectNam??????????????????????????????????????????
		 * ???????????????????????????ObjectName???????????????????????????Map??????key???ObjectName???value???AttributeList
		 * ????????????????????????Map??????key(ObjectName)?????????AttributeList
		 */
		String[] webApp = map.get(WEB_APP);
		String[] webAppSession = map.get(WEB_APP_SESSION);
		JMXConnector connector = (JMXConnector)JMXConnectionFactory.getConnection(one.getIp(), one.getPort());
		if(webApp != null && webApp.length == 2 && webAppSession != null && webAppSession.length == 2){
			if(connector != null){
				MBeanServerConnection mBeanServer;
				try {
					mBeanServer = connector.getMBeanServerConnection();
				} catch (IOException e) {
					debug.error("IO Error" + e.getMessage(), e);
					return null;
				}
				Map<String, String> objectMap = new HashMap<String, String>();
				objectMap.put(webApp[0], webApp[1]);
				objectMap.put(webAppSession[0], webAppSession[1]);
				Set<String> keySet = objectMap.keySet();
				Iterator<String> iterator = keySet.iterator();
				
				//????????????
				Map<String, AttributeList> sessionMap = new HashMap<String, AttributeList>();
				Map<String, AttributeList> appMap = new HashMap<String, AttributeList>();
				
				while(iterator.hasNext()){
					try{
						
						String tempObject = iterator.next();
						ObjectName realObject = new ObjectName(tempObject);
						
						Set<ObjectName> set = mBeanServer.queryNames(realObject, null);
						Iterator<ObjectName> objectIterator = set.iterator();
						//??????????????????AttributeList??????List?????????????????????list??????
						while(objectIterator.hasNext()){
							ObjectName objTemp = objectIterator.next();
							try {
								AttributeList attributes = mBeanServer.getAttributes(objTemp, objectMap.get(tempObject).split(","));
								if(objTemp.getCanonicalName().contains("type=Manager")){
									sessionMap.put(objTemp.getCanonicalName(), attributes);
								}else{
									appMap.put(objTemp.getCanonicalName(), attributes);
								}
								
							} catch (InstanceNotFoundException e) {
								debug.error("No ObjectName --- " + e.getMessage(), e);
							} catch (ReflectionException e) {
								debug.error("Java reflect error" + e.getMessage(), e);
							} catch (IOException e) {
								debug.error("IO Error" + e.getMessage(), e);
							}
							
						}
					
					} catch (MalformedObjectNameException e) {
						debug.error("URL Error" + e.getMessage(), e);
					} catch (IOException e){
						debug.error("IO Error" + e.getMessage(), e);
					}
				}
				StringBuffer sb = new StringBuffer();
				if(sessionMap.size() > 0 && appMap.size() > 0){
					Set<String> outSet = appMap.keySet();
					Iterator<String> outIterator = outSet.iterator();
					int index = 0;
					while(outIterator.hasNext()){
						String outObjectStr = outIterator.next();
						Set<String> innerSet = sessionMap.keySet();
						Iterator<String> innerIterator = innerSet.iterator();
						while(innerIterator.hasNext()){
							String name = "";
							String innerObjectStr = innerIterator.next();
							String[] tempArray = innerObjectStr.split(",");
							if(tempArray != null){
								String host = "localhost";
								String path = "/";
								for(String str : tempArray){
									if(str.contains("host")){
										host = str.split("=")[1];
									}else if(str.contains("path")){
										path = str.split("=")[1];
									}
								}
								name = "//" + host + path;
							}
							if(outObjectStr.contains(name)){
								AttributeList outAttribute = appMap.get(outObjectStr);
								AttributeList innnerAttribute = sessionMap.get(innerObjectStr);
								sb.append(index).append("\t");
								for(Object obj : outAttribute){
									Attribute att = (Attribute)obj;
									if("path".equals(att.getName())){
										if(att.getValue() != null){
											String str = (String)att.getValue();
											if(!"".equals(str)){
												String context = str.substring(str.lastIndexOf("/")+1);
												sb.append(context).append("\t");
												sb.append(str).append("\t");
											}else{
												sb.append("ROOT\t").append("/\t");
											}
										}
									}else
										sb.append(att.getValue()).append("\t");
								}
								for(Object obj : innnerAttribute){
									Attribute att = (Attribute)obj;
									sb.append(att.getValue()).append("\t");
								}
								
								sb.append("\n");
								index++;
								break;
							}
						}
					}
				}
				return sb.toString();
				
				
			}else{
				throw new RuntimeException("get JMX connection pool object failed!");
			}
		}else{
			debug.error("get ObjectName or Attribute failed!");
		}
		return null;
	}
	
	/**
	 * ??????????????????
	 * @param jmxId
	 * @return
	 */
	public AttributeList getCommonAttributes(String jmxId, String ip, int port){
		Map<String, String[]> map = parser.getJmxMap();
		String[] objectNames = map.get(jmxId);
		JMXConnector connector = (JMXConnector) JMXConnectionFactory.getConnection(ip, port);
		if(objectNames != null && objectNames.length == 2){
			if(connector != null){
				MBeanServerConnection mBeanServer;
				ObjectName object;
				try {
					mBeanServer = connector.getMBeanServerConnection();
					object = new ObjectName(objectNames[0]);
				} catch (MalformedObjectNameException e) {
					debug.error("URL Error" + e.getMessage(), e);
					return null;
				} catch (IOException e) {
					debug.error("IO Error" + e.getMessage(), e);
					return null;
				}
				try {
					AttributeList attributes = mBeanServer.getAttributes(object, objectNames[1].split(","));
					return attributes;
				} catch (InstanceNotFoundException e) {
					debug.error("No ObjectName --- " + object.toString() + e.getMessage(), e);
				} catch (ReflectionException e) {
					debug.error("Java reflect error!" + e.getMessage(), e);
				} catch (IOException e) {
					debug.error("IO error" + e.getMessage(), e);
				} 
			}else{
				throw new RuntimeException("get JMX connection pool object failed!");
			}
		}else{
			debug.error("get ObjectName or Attribute failed!");
		}
		return null;
	}
	
}
