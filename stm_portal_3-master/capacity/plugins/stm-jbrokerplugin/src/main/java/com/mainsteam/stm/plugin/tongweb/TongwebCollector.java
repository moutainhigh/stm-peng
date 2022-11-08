package com.mainsteam.stm.plugin.tongweb;

import javax.management.MBeanServerConnection;
import javax.management.MalformedObjectNameException;
import javax.management.ObjectName;
import javax.management.openmbean.CompositeDataSupport;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import com.mainsteam.stm.plugin.parameter.JBrokerParameter;


public class TongwebCollector {
	
	private Log log=LogFactory.getLog(TongwebCollector.class);
	
	private static String THREAD="monitor:name=thread-system,group=jvm";
	private static String RUNTIME="monitor:name=runtime,group=jvm";
	private static String MEMORY="monitor:name=memory,group=jvm";
	private static String OS="monitor:name=operating-system,group=jvm";
	private static String SERVER="TWNT:type=Server";
	private static MBeanServerConnection getConn(JBrokerParameter obj){
		MBeanServerConnection mbsc=obj.getmBeanServerConnection();
		return mbsc;
	};
	private ObjectName getObjectName(String resource){
			try {
				ObjectName objectName = new ObjectName(resource);
				return objectName;
			} catch (MalformedObjectNameException e) {
				log.error("failed to get objectName-->"+resource,e);
			}
			return null;
		
	}
	/*主资源：----->   开始*/
	public int availability(JBrokerParameter obj){
		return 1;
	}
	
	/**
	 * 服务器版本
	 * @param obj
	 * @return
	 */
public String getVersion(JBrokerParameter obj){
	try {
			String value = (String)getConn(obj). getAttribute(getObjectName(SERVER), 
					"serverInfo");
			//System.out.println(data);
			//String value=data.get("current").toString();
			//System.out.println(value);
			return value;
			}
			
		 catch (Exception e) {
			log.error("no attribute!",e);
		} 
	return null;
}
/**
 * 操作系统名称
 * @param obj
 * @return
 */
public String getOsName(JBrokerParameter obj){
try {
	CompositeDataSupport data = (CompositeDataSupport)getConn(obj). getAttribute(getObjectName(OS), 
				"OSName");
		System.out.println(data);
		String value=data.get("current").toString();
		System.out.println(value);
		return value;
		}
		
	 catch (Exception e) {
		log.error("no attribute!",e);
	} 
return null;
}
	/*主资源：----->   结束*/
	/*子资源：----->jvm基本信息   开始*/
public String getJvmBasicID(JBrokerParameter obj){
	return "Jvm";
}
	/**
	 * JVM 实现名称
	 * @param obj
	 * @return
	 */
public String getJvmName(JBrokerParameter obj){
	try {
			CompositeDataSupport data = (CompositeDataSupport)getConn(obj). getAttribute(getObjectName(RUNTIME), 
					"VmName");
			String value=data.get("current").toString();
			System.out.println(value);
			return value;
			}
			
		 catch (Exception e) {
			log.error("no attribute!",e);
		} 
	return null;
}
/**
 * JVM 实现版本 
 * @return
 */

public String getJvmVersion(JBrokerParameter obj){
		try {
			CompositeDataSupport data = (CompositeDataSupport)getConn(obj). getAttribute(getObjectName(RUNTIME), 
					"VmVersion");
			System.out.println(data);
			String value=data.get("current").toString();
			System.out.println(value);
			return value;
		}  catch (Exception e) {
			log.error("no attribute!",e);
		}
		
	return null;
}

/**
 * 服务器启动时间 单位ms
 * @return
 */

public String getStartTime(JBrokerParameter obj){
		try {
			CompositeDataSupport data = (CompositeDataSupport)getConn(obj). getAttribute(getObjectName(RUNTIME), 
					"StartTime");
			System.out.println(data);
			String value=data.get("count").toString();
			System.out.println(value);
			return value;
		}  catch (Exception e) {
			log.error("no attribute!",e);
		}
	return null;
}
/*子资源：----->jvm基本信息   结束*/

/*子资源：----->jvm内存   开始*/
/**
 * 最大堆大小 byte
 * @return
 */

public String getMaxHeap(JBrokerParameter obj){
	
		try {
			CompositeDataSupport data = (CompositeDataSupport) getConn(obj).getAttribute(getObjectName(MEMORY),"MaxHeap");
			String value=data.get("count").toString();
			System.out.println(data);
			return value;
		} catch (Exception e) {
			log.error("no attribute!",e);
	}
	return null;
	}

/**
 * 当前正在使用的堆的大小 byte
 * @return
 */

public String getUsedHeap(JBrokerParameter obj){
		try {
			CompositeDataSupport data = (CompositeDataSupport) getConn(obj).getAttribute(getObjectName(MEMORY),"UsedHeap");
			System.out.println(data);
			String value=data.get("count").toString();
			return value;
		} catch (Exception e) {
			log.error("no attribute!",e);
	}
	return null;
	}

/**
 * 正在使用的堆内存百分比
 * @return
 */

public String getUsedHeapSizePercent(JBrokerParameter obj){
		try {
			CompositeDataSupport data = (CompositeDataSupport) getConn(obj).getAttribute(getObjectName(MEMORY),"UsedHeapSizePercent");
			System.out.println(data);
			String value=data.get("current").toString();
			return value;
		} catch (Exception e) {
			log.error("no attribute!",e);
	}
	return null;
	}

/*子资源：----->jvm内存   结束*/

/*子资源：----->jvm操作系统  开始*/
/**
 * 操作系统体系结构
 * @return
 */

public String getArch(JBrokerParameter obj){
		try {
			CompositeDataSupport data = (CompositeDataSupport) getConn(obj).getAttribute(getObjectName(OS),"Arch");
			System.out.println(data);
			String value=data.get("current").toString();
			return value;
		} catch (Exception e) {
			log.error("no attribute!",e);
	}
	return null;
	}
/**
 * 空闲物理内存大小 byte
 * @return
 */

public String getFreePhysicalMemorySize(JBrokerParameter obj){
		try {
			CompositeDataSupport data = (CompositeDataSupport) getConn(obj).getAttribute(getObjectName(OS),"FreePhysicalMemorySize");
			System.out.println(data);
			String value=data.get("count").toString();
			return value;
		} catch (Exception e) {
			log.error("no attribute!",e);
	}
	return null;
	}
/**
 * 总交换区大小 byte
 * @return
 */

public String getTotalSwapSpaceSize(JBrokerParameter obj){
		try {
			CompositeDataSupport data = (CompositeDataSupport) getConn(obj).getAttribute(getObjectName(OS),"TotalSwapSpaceSize");
			System.out.println(data);
			String value=data.get("count").toString();
			return value;
		} catch (Exception e) {
			log.error("no attribute!",e);
	}
	return null;
	}
/**
 * 空闲交换区大小 byte
 * @return
 */

public String getFreeSwapSpaceSize(JBrokerParameter obj){
		try {
			CompositeDataSupport data = (CompositeDataSupport) getConn(obj).getAttribute(getObjectName(OS),"FreeSwapSpaceSize");
			System.out.println(data);
			String value=data.get("count").toString();
			return value;
		} catch (Exception e) {
			log.error("no attribute!",e);
	}
	return null;
	}
/*子资源：----->jvm操作系统  开始*/
/*子资源：----->jvm线程  开始*/
/**
 * 线程占用 CPU 时间  nanosecond
 * @return
 */

public String getCurrentThreadCpuTime(JBrokerParameter obj){
		try {
			CompositeDataSupport data = (CompositeDataSupport) getConn(obj).getAttribute(getObjectName(THREAD),"CurrentThreadCpuTime");
			System.out.println(data);
			String value=data.get("count").toString();
			return value;
		} catch (Exception e) {
			log.error("no attribute!",e);
	}
	return null;
	}

/**
 * 处于死锁状态的线程 ID 列表
 * @return
 */

public String getDeadlockedThreads(JBrokerParameter obj){
		try {
			CompositeDataSupport data = (CompositeDataSupport) getConn(obj).getAttribute(getObjectName(THREAD),"DeadlockedThreads");
			System.out.println(data);
			String value=data.get("current").toString();
			return value;
		} catch (Exception e) {
			log.error("no attribute!",e);
	}
	return null;
	}

/**
 * 当前活动的线程总数，包括守护线程和非守护线程
 * @return
 */

public String getThreadCount(JBrokerParameter obj){
		try {
			CompositeDataSupport data = (CompositeDataSupport) getConn(obj).getAttribute(getObjectName(THREAD),"ThreadCount");
			System.out.println(data);
			String value=data.get("count").toString();
			return value;
		} catch (Exception e) {
			log.error("no attribute!",e);
	}
	return null;
	}

/**
 * 自 JVM 启动以来创建和启动的线程总数
 * @return
 */

public String getTotalStartedThreadCount(JBrokerParameter obj){
		try {
			CompositeDataSupport data = (CompositeDataSupport) getConn(obj).getAttribute(getObjectName(THREAD),"TotalStartedThreadCount");
			System.out.println(data);
			String value=data.get("count").toString();
			return value;
		} catch (Exception e) {
			log.error("no attribute!",e);
	}
	return null;
	}
}
