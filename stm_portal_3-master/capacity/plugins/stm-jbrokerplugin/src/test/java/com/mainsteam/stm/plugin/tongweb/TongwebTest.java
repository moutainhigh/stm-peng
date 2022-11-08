package com.mainsteam.stm.plugin.tongweb;

import java.util.HashMap;
import java.util.Map;

import javax.management.MBeanServerConnection;
import javax.management.remote.JMXConnector;
import javax.management.remote.JMXConnectorFactory;
import javax.management.remote.JMXServiceURL;

import org.junit.Test;

import com.mainsteam.stm.plugin.parameter.JBrokerParameter;

public class TongwebTest {
	private  JBrokerParameter getConn () {
		JBrokerParameter obj=new JBrokerParameter();
		String ip="172.16.7.61";
		int port=7200;
		String userName="twnt";
		String password="twnt";
		String jmxUrl="service:jmx:rmi:///jndi/rmi://"+ip+":"+port+"/server";
		String credentials[]=new String[]{userName,password};
		Map<String, String []> env=new HashMap<String, String[]>();
		env.put(JMXConnector.CREDENTIALS, credentials);
		MBeanServerConnection mBeanServerConnection=null;
		try {
			JMXServiceURL jmxServiceURL=new JMXServiceURL(jmxUrl);
			JMXConnector jmxConnector = JMXConnectorFactory.connect(jmxServiceURL,env);
			mBeanServerConnection=jmxConnector.getMBeanServerConnection();
			obj.setmBeanServerConnection(mBeanServerConnection);
		} catch (Exception e) {
			e.printStackTrace();
		}
		return obj;
	}
	@Test
	public void testgetVersion(){
		String result=new TongwebCollector().getVersion(getConn());
		System.out.println("服务器版本-->"+result);
	}
	@Test
	public void testgetOsName(){
		String result=new TongwebCollector().getOsName(getConn());
		System.out.println("操作系统名称-->"+result);
	}
	@Test
	public void testgetVmName(){
		String result=new TongwebCollector().getJvmName(getConn());
		System.out.println("jvm实现名称-->"+result);
	}
	
	@Test
	public void testgetJvmVersion(){
		String result=new TongwebCollector().getJvmVersion(getConn());
		System.out.println("jvm实现版本-->"+result);
	}
	
	@Test
	public void testgetStartTime(){
		String result=new TongwebCollector().getStartTime(getConn());
		System.out.println("服务器启动时间-->"+result);
	}
	@Test
	public void testgetMaxHeap(){
		String result=new TongwebCollector().getMaxHeap(getConn());
		System.out.println("最大堆大小-->"+result);
	}
	@Test
	public void testgetUsedHeap(){
		String result=new TongwebCollector().getUsedHeap(getConn());
		System.out.println("当前正在使用的堆的大小-->"+result);
	}
	@Test
	public void testgetUsedHeapSizePercent(){
		String result=new TongwebCollector().getUsedHeapSizePercent(getConn());
		System.out.println("当前正在使用的堆内存百分比-->"+result);
	}
	@Test
	public void testgetArch(){
		String result=new TongwebCollector().getArch(getConn());
		System.out.println("操作系统体系结构-->"+result);
	}
	@Test
	public void testFreePhysicalMemorySize(){
		String result=new TongwebCollector().getFreePhysicalMemorySize(getConn());
		System.out.println("空闲物理内存大小-->"+result);
	}
	@Test
	public void testTotalSwapSpaceSize(){
		String result=new TongwebCollector().getTotalSwapSpaceSize(getConn());
		System.out.println("总交换区大小-->"+result);
	}
	@Test
	public void testFreeSwapSpaceSize(){
		String result=new TongwebCollector().getFreeSwapSpaceSize(getConn());
		System.out.println("空闲交换区大小-->"+result);
	}
	@Test
	public void testgetCurrentThreadCpuTime(){
		String result=new TongwebCollector().getCurrentThreadCpuTime(getConn());
		System.out.println("线程占用 CPU 时间-->"+result);
	}
	@Test
	public void testgetDeadlockedThreads(){
		String result=new TongwebCollector().getDeadlockedThreads(getConn());
		System.out.println("处于死锁状态的线程 ID 列表-->"+result);
	}
	@Test
	public void testgetThreadCount(){
		String result=new TongwebCollector().getThreadCount(getConn());
		System.out.println("当前活动的线程总数，包括守护线程和非守护线程-->"+result);
	}
	@Test
	public void testgetTotalStartedThreadCount(){
		String result=new TongwebCollector().getTotalStartedThreadCount(getConn());
		System.out.println("自 JVM 启动以来创建和启动的线程总数-->"+result);
	}
}
