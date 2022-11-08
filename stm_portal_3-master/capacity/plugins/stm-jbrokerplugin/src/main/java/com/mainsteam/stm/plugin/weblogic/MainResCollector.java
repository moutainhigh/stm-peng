package com.mainsteam.stm.plugin.weblogic;

import java.text.SimpleDateFormat;
import java.util.Date;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import com.mainsteam.stm.plugin.parameter.JBrokerParameter;

import weblogic.management.runtime.ServerRuntimeMBean;


/**
 * 采集Weblogic主资源指标
 * @author lij
 *
 */
public class MainResCollector {
	private static final Log debug = LogFactory.getLog(MainResCollector.class);
	private static final double S_PERCENT = 100.0;
    private static final double S_B_1024 = 1024;
	/**
	 * 获取可用性，“1”表示可用，“-1”表示不可用
	 * @param oneobj
	 * @return
	 */
	public static String getAvailability(JBrokerParameter oneobj){
		String state = WeblogicJMXcollectUtil.getMainResValue(oneobj, "State");
		if(debug.isInfoEnabled()) {
			debug.info("WebLogic Availability:" + state + ";ip/port:" + oneobj.getIp() + "/" + oneobj.getPort()
					+ ";user is " + oneobj.getUsername() + ",instance is " + oneobj.getWeblogicBo().getInstancename());
		}
		if(ServerRuntimeMBean.RUNNING.equals(state) || ServerRuntimeMBean.ADMIN.equals(state)){
			return WeblogicConstant.AVAILABLE;
        } else {
            return WeblogicConstant.NO_AVAILABLE;
        }
	}
	/**
	 * 最高JMS服务数
	 * @param oneobj
	 * @return
	 */
	public static String getHighestJMSServerCount(JBrokerParameter oneobj) {
		return WeblogicJMXcollectUtil.getMainResValue(oneobj, "JMSRuntime@#JMSServersHighCount");
	}
	/**
	 * 当前连接数
	 * @param oneobj
	 * @return
	 */
	public static String getCurConnCount(JBrokerParameter oneobj) {
		return WeblogicJMXcollectUtil.getMainResValue(oneobj, "JMSRuntime@#ConnectionsCurrentCount");
	}
	/**
	 * 最高连接数
	 * @param oneobj
	 * @return
	 */
	public static String getHighestConnCount(JBrokerParameter oneobj) {
		return WeblogicJMXcollectUtil.getMainResValue(oneobj, "JMSRuntime@#ConnectionsHighCount");
	}
	/**
	 * 使用堆大小
	 * @param oneobj
	 * @return
	 */
	public static String getUsedHeapSize(JBrokerParameter oneobj) {
		String[] strs = WeblogicJMXcollectUtil.getMainResValueList(oneobj, "JVMRuntime", "HeapFreeCurrent@#HeapSizeCurrent");
		String result = "";
		if(strs != null && strs.length == 2){
			try{
				Long t_heapFreeCurrent = Long.valueOf(strs[0]);
				Long t_heapSizeCurrent = Long.valueOf(strs[1]);
				long t_heapUsedSize = t_heapSizeCurrent - t_heapFreeCurrent;
				double t_heapUsedSizeValue = t_heapUsedSize / S_B_1024 / S_B_1024;
				result = String.valueOf(t_heapUsedSizeValue);
			}catch(NumberFormatException e){
				debug.debug(e.getMessage(), e);
			}
		}
        return result;
	}
	/**
	 * 总线程数
	 * @param oneobj
	 * @return
	 */
	public static String getTotalThreadCount(JBrokerParameter oneobj) {
		return WeblogicJMXcollectUtil.getMainResValue(oneobj, "ThreadPoolRuntime@#ExecuteThreadTotalCount");
	}
	/**
	 * 当前JMS服务数
	 * @param oneobj
	 * @return
	 */
	public static String getCurJMSServerCount(JBrokerParameter oneobj) {
		return WeblogicJMXcollectUtil.getMainResValue(oneobj, "JMSRuntime@#JMSServersCurrentCount");
	}
	/**
	 * Server名称
	 * @param oneobj
	 * @return
	 */
	public static String getServerName(JBrokerParameter oneobj) {
		return WeblogicJMXcollectUtil.getMainResValue(oneobj, "Name");
	}
	/**
	 * Server显示名称
	 * @param oneobj
	 * @return
	 */
	public static String getServerDisplayName(JBrokerParameter oneobj) {
		return WeblogicJMXcollectUtil.getMainResValue(oneobj, "Name");
	}
	/**
	 * 版本
	 * @param oneobj
	 * @return
	 */
	public static String getWeblogicVersion(JBrokerParameter oneobj) {
		String version = WeblogicJMXcollectUtil.getMainResValue(
				oneobj, "WeblogicVersion");
		try {
			//这样子处理是因为，获取到的version中有空格，资源模型只截取第一个空格之前的内容
			String[] strs = version.split("\\s");
			StringBuffer sb = new StringBuffer();
			for (String str : strs) {
				if (str.length() == 0) {
					//此时后面的内容为编译日期之内的东西可以不管
					break;
				}
				sb.append(str);
				sb.append("_");
			}
			sb.delete(sb.length() - 1, sb.length());
			version = sb.toString();
		} catch (Exception e) {
			debug.debug(e.getMessage(), e);
		}
		return version;
	}
	/**
	 * 主机操作系统
	 * @param oneobj
	 * @return
	 */
	public static String getOperationSystem(JBrokerParameter oneobj) {
		return WeblogicJMXcollectUtil.getMainResValue(oneobj, "JVMRuntime@#OSName");
	}
	/**
	 * Serverdomain名称
	 * @param oneobj
	 * @return
	 */
	public static String getDomainName(JBrokerParameter oneobj) {
		String domainname=WeblogicJMXcollectUtil.getMainResValueBySpecisMBeanServer(oneobj, WeblogicConstant.DOMAIN_CONFIGURATION, "Name");
		return domainname;
	}
	/**
	 * 当前堆大小
	 * @param oneobj
	 * @return
	 */
	public static String getCurHeapSize(JBrokerParameter oneobj) {
		String result = "";
		String tempvalue = WeblogicJMXcollectUtil.getMainResValue(oneobj, "JVMRuntime@#HeapSizeCurrent");
		try{
			long tempvalueL = Long.valueOf(tempvalue);
			double tempvalueD = tempvalueL / S_B_1024/ S_B_1024;
			result = String.valueOf(tempvalueD);
		}catch(NumberFormatException e){
			debug.debug(e.getMessage(), e);
		}
		return result;
	}
	/**
	 * 空闲堆空间
	 * @param oneobj
	 * @return
	 */
	public static String getHeapFreeCurrent(JBrokerParameter oneobj){
		String result = "";
		String tempvalue = WeblogicJMXcollectUtil.getMainResValue(oneobj, "JVMRuntime@#HeapFreeCurrent");
		try{
			long tempvalueL = Long.valueOf(tempvalue);
			double tempvalueD = tempvalueL / S_B_1024/ S_B_1024;
			result = String.valueOf(tempvalueD);
		}catch(NumberFormatException e){
			debug.debug(e.getMessage(), e);
		}
		return result;
	}
	/**
	 * 堆空间使用百分比
	 * @param oneobj
	 * @return
	 */
	public static String getHeapFreePercent(JBrokerParameter oneobj){
		return WeblogicJMXcollectUtil.getMainResValue(oneobj, "JVMRuntime@#HeapFreePercent");
	}
	/**
	 * 资源显示名称
	 * @param oneobj
	 * @return
	 */
	public static String getResDisplayName(JBrokerParameter oneobj) {
		String result = WeblogicJMXcollectUtil.getMainResValue(oneobj, "Name");
		if(result != null && !"".equals(result)){
			StringBuffer sb = new StringBuffer(oneobj.getIp());
			sb.append("_");
			sb.append(oneobj.getPort());
			sb.append("_");
			sb.append(result);
			result = sb.toString();
		}
		return result;
	}
	/**
	 * 提交成功的事务耗费的时间
	 * @param oneobj
	 * @return
	 */
	public static String getSuccessTransTime(JBrokerParameter oneobj) {
		return WeblogicJMXcollectUtil.getMainResValue(oneobj, "JTARuntime@#SecondsActiveTotalCount");
	}
	/**
	 * 备用线程数
	 * @param oneobj
	 * @return
	 */
	public static String getStandbyThreadCount(JBrokerParameter oneobj) {
		return WeblogicJMXcollectUtil.getMainResValue(oneobj, "ThreadPoolRuntime@#StandbyThreadCount");
	}
	/**
	 * 总连接数
	 * @param oneobj
	 * @return
	 */
	public static String getTotalConnCount(JBrokerParameter oneobj) {
		return WeblogicJMXcollectUtil.getMainResValue(oneobj, "JMSRuntime@#ConnectionsTotalCount");
	}
	/**
	 * 总JMS服务数
	 * @param oneobj
	 * @return
	 */
	public static String getTotalJMSServerCount(JBrokerParameter oneobj) {
		return WeblogicJMXcollectUtil.getMainResValue(oneobj, "JMSRuntime@#JMSServersTotalCount");
	}
	/**
	 * 空闲线程数
	 * @param oneobj
	 * @return
	 */
	public static String getIdleThreadCount(JBrokerParameter oneobj) {
		return WeblogicJMXcollectUtil.getMainResValue(oneobj, "ThreadPoolRuntime@#ExecuteThreadIdleCount");
	}
	/**
	 * JVM内存利用率
	 * @param oneobj
	 * @return
	 */
	public static String getJVMMEMRate(JBrokerParameter oneobj) {
		String[] strs = WeblogicJMXcollectUtil.getMainResValueList(oneobj, "JVMRuntime", "HeapFreeCurrent@#HeapSizeCurrent");
		if(strs != null && strs.length == 2){
			try{
				Long t_heapFreeCurrent = Long.valueOf(strs[0]);
				Long t_heapSizeCurrent = Long.valueOf(strs[1]);
				long t_heapUsedSize = t_heapSizeCurrent - t_heapFreeCurrent;
		        double t_utilization = S_PERCENT * t_heapUsedSize / t_heapSizeCurrent;
		        return String.valueOf(t_utilization);
			}catch(NumberFormatException e){
				debug.error(e.getMessage(), e);
			}
		}
		return "";
	}
	/**
	 * server显示名称
	 * @param oneobj
	 * @return
	 */
	public static String getServerDisplayNam(JBrokerParameter oneobj) {
		return WeblogicJMXcollectUtil.getMainResValue(oneobj, "Name");
	}
	/**
	 * 打开的Socket数
	 * @param oneobj
	 * @return
	 */
	public static String getSocketsOpenedTotalCount(JBrokerParameter oneobj){
		return WeblogicJMXcollectUtil.getMainResValue(oneobj, "SocketsOpenedTotalCount");
	}
	/**
	 * 重启次数
	 * @param oneobj
	 * @return
	 */
	public static String getRestartsTotalCount(JBrokerParameter oneobj){
		return WeblogicJMXcollectUtil.getMainResValue(oneobj, "RestartsTotalCount");
	}
	/**
	 * 超时回滚的事务数
	 * @param oneobj
	 * @return
	 */
	public static String getRolledBackTimeoutTransCount(JBrokerParameter oneobj) {
		return WeblogicJMXcollectUtil.getMainResValue(oneobj, "JTARuntime@#TransactionRolledBackTimeoutTotalCount");
	}
	/**
	 * 多播消息丢失数(每分钟)
	 * @param oneobj
	 * @return
	 */
	public static String getMulticastMessagesLostCount(JBrokerParameter oneobj) {
		String MulticastMessagesLostCount=WeblogicJMXcollectUtil.getMainResValue(oneobj, "ClusterRuntime@#MulticastMessagesLostCount");
		return MulticastMessagesLostCount;
	}
	/**
	 * 资源出错回滚的事务数
	 * @param oneobj
	 * @return
	 */
	public static String getRolledBackResourceTransCount(JBrokerParameter oneobj) {
		return WeblogicJMXcollectUtil.getMainResValue(oneobj, "JTARuntime@#TransactionRolledBackResourceTotalCount");
	}
	/**
	 * 应用程序出错回滚的事务数
	 * @param oneobj
	 * @return
	 */
	public static String getRolledBackAppTransCount(JBrokerParameter oneobj) {
		return WeblogicJMXcollectUtil.getMainResValue(oneobj, "JTARuntime@#TransactionRolledBackAppTotalCount");
	}
	/**
	 * 丢弃的事务数
	 * @param oneobj
	 * @return
	 */
	public static String getAbandonedTransCount(JBrokerParameter oneobj) {
		return WeblogicJMXcollectUtil.getMainResValue(oneobj, "JTARuntime@#TransactionAbandonedTotalCount");
	}
	/**
	 * 多播消息重发数(每分钟)
	 * @param oneobj
	 * @return
	 */
	public static String getResendRequestsCount(JBrokerParameter oneobj) {
		return WeblogicJMXcollectUtil.getMainResValue(oneobj, "ClusterRuntime@#ResendRequestsCount");
	}
	/**
	 * 存活服务器数量
	 * @param oneobj
	 * @return
	 */
	public static String getClusterAliveServerCount(JBrokerParameter oneobj) {
		return WeblogicJMXcollectUtil.getMainResValue(oneobj, "ClusterRuntime@#AliveServerCount");
	}
	/**
	 * 系统出错回滚的事务数
	 * @param oneobj
	 * @return
	 */
	public static String getRolledBackSystemTransCount(JBrokerParameter oneobj) {
		return WeblogicJMXcollectUtil.getMainResValue(oneobj, "JTARuntime@#TransactionRolledBackSystemTotalCount");
	}
	/**
	 * JTA名称
	 * @param oneobj
	 * @return
	 */
	public static String getJTAName(JBrokerParameter oneobj) {
		return WeblogicJMXcollectUtil.getMainResValue(oneobj, "JTARuntime@#Name");
	}
	/**
	 * 集群名称
	 * @param oneobj
	 * @return
	 */
	public static String getClusterName(JBrokerParameter oneobj) {
		return WeblogicJMXcollectUtil.getMainResValue(oneobj, "ClusterRuntime@#Name");
	}
	/**
	 * 队列长度
	 * @param oneobj
	 * @return
	 */
	public static String getQueueLength(JBrokerParameter oneobj){
		return WeblogicJMXcollectUtil.getMainResValue(oneobj, "ThreadPoolRuntime@#QueueLength");
	}
	/**
	 * 吞吐量
	 * @param oneobj
	 * @return
	 */
	public static String getThroughput(JBrokerParameter oneobj){
		return WeblogicJMXcollectUtil.getMainResValue(oneobj, "ThreadPoolRuntime@#Throughput");
	}
	/**
	 * 服务器激活时间
	 * @param oneobj
	 * @return
	 */
	public static String getActivationTime(JBrokerParameter oneobj){
		String activationTime = WeblogicJMXcollectUtil.getMainResValue(oneobj, "ActivationTime");
		Date date = new Date(Long.parseLong(activationTime));
		SimpleDateFormat sd = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
		return sd.format(date);
	}
}
