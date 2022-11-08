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
	 * 服务可用性 发布日期 发布时操作系统版本 发布时JVM版本号 启动时间 JBoss版本
	 * @param one
	 * @return
	 */
	public String findServer(JBrokerParameter one){
		
		AttributeList attributes = getCommonAttributes(ATTRIBUTE_SERVER, one.getIp(), one.getPort());
		if(attributes != null && attributes.size() > 0){
			StringBuffer sb = new StringBuffer();
			//以xml格式返回值
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
	 * 采集以下指标
	 * JVM内存利用率 已使用内存 空闲的内存 主机名 主机操作系统 操作系统版本 最大内存 总内存 JVM名称 Java版本 活动线程数 活动线程组数量
	 * @param one
	 * @return
	 */
	public String findServerInfo(JBrokerParameter one){
		
		AttributeList attributes = getCommonAttributes(ATTRIBUTE_SERVERINFO, one.getIp(), one.getPort());
		StringBuffer sb = new StringBuffer();
		if(attributes != null && attributes.size() > 0){
			//以xml格式返回值
			long freeMemory = 0l; //空闲内存
			long totalMemory = 0l; // 总内存
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
			//分别计算 已使用内存、内存利用率
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
	 * 采集以下指标
	 * @param one
	 * @return 系统状态码
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
	 * 采集以下指标
	 *  线程池名称 线程组名称 锁模式 最大队列大小 队列大小 池数量 最大池大小 最小池大小
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
	 * 采集以下指标
	 * 正在使用中的连接数
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
	 * 采集以下指标
	 * 服务器提供商 服务器版本 父类 对象名 事件提供者状态 状态可管理性 是否提供统计
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
	 * 采集以下指标
	 * JVM启动时间 JVM最后采样时间 最大堆大小 最小堆大小
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
								// JVM启动时间
								Date startDate = new Date(startTime);
								DateFormat dFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
								String startDateStr = dFormat.format(startDate);
								sb.append("<StartDate>").append(startDateStr).append("</StartDate>");
								// JVM最后采样时间
								long lastTime = count.getLastSampleTime();
								Date lastDate = new Date(lastTime);
								String lastTimeStr = dFormat.format(lastDate);
								sb.append("<LastSampleTime>").append(lastTimeStr).append("</LastSampleTime>");
								//最大堆  最小堆
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
	 * 采集以下指标
	 * 实例名称 HomeDir
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
	 * 采集以下值
	 * JBoss最大线程数 JBoss当前繁忙的线程 JBoss当前线程个数
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
	 * 采集Web服务端口 
	 * 连接协议 连接编码格式 连接策略 连接最大http头寸 连接转发端口 连接端口 连接数 连接超时时间 存活时间 最小备用线程数 最大备用线程数 最大传输数 最大请求存活数
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
								if("minSpareThreads-maxSpareThreads".contains(att.getName())){// 最小备用线程数 最大备用线程数
									if(null == attValue)
										attValue = "0";
								//连接编码格式、 连接策略
								}else if("URIEncoding-strategy".contains(att.getName())){
									if(null == attValue)
										attValue = "未设置";
								}else if("keepAliveTimeout".equals(att.getName())){
									if(null == attValue)
										attValue = "不支持";
									else if("-1".equals(attValue.toString())){
										attValue = "永远存活";
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
	 * 采集以下指标
	 * JMS当前连接数 JMS总连接数 JMS最高连接数 JMS允许的最大连接数
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
	 * 采集以下指标
	 * JDBC连接池可用状态 活动的连接数 连接池名称 最大连接数 最小连接数
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
					 * 首先取得所有的ObjectName遍历
					 */
					Set<ObjectName> set = mBeanServer.queryNames(object, null);
					if(set != null){
						Iterator<ObjectName> iterator = set.iterator();
						StringBuffer sb = new StringBuffer();
						//将符合条件的AttributeList放入List中，最后再遍历list取值
						List<AttributeList> list = new ArrayList<AttributeList>();
						while(iterator.hasNext()){
							ObjectName objectName = iterator.next();
							try {
								AttributeList attributes = mBeanServer.getAttributes(objectName, objectNames[1].split(","));
								if(attributes != null && attributes.size() > 0){
									for(Object obj : attributes){
										Attribute att = (Attribute)obj;
										/**
										 * 取出ManagedConnectionFactoryName的值，这个值又是另一个ObjectName，需要通过取得该ObjectName的中一个
										 * 名为ConnectionInterface的值来判断该DS是否为JDBC数据源
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
	 * 采集以下指标
	 * Web应用可用状态 应用名称 上下文根目录 当前总会话数 活动的会话数 最大活动会话数
	 * 
	 * @param one
	 * @return
	 */
	public String findWebApp(JBrokerParameter one){
		Map<String, String[]> map = parser.getJmxMap();
		/**
		 * 根据JBoss_jmx.xml配置中读取中webApp和webAppSession这两个ObjectName。因为采集的指标分别存在这两个ObjectNam的属性中，所以大概思路如下：
		 * 首先分别遍历这两类ObjectName中的属性，存入两个Map中，key为ObjectName，value为AttributeList
		 * 再次循环比较比较Map中的key(ObjectName)取出在AttributeList
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
				
				//比较结果
				Map<String, AttributeList> sessionMap = new HashMap<String, AttributeList>();
				Map<String, AttributeList> appMap = new HashMap<String, AttributeList>();
				
				while(iterator.hasNext()){
					try{
						
						String tempObject = iterator.next();
						ObjectName realObject = new ObjectName(tempObject);
						
						Set<ObjectName> set = mBeanServer.queryNames(realObject, null);
						Iterator<ObjectName> objectIterator = set.iterator();
						//将符合条件的AttributeList放入List中，最后再遍历list取值
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
	 * 获取公共属性
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
