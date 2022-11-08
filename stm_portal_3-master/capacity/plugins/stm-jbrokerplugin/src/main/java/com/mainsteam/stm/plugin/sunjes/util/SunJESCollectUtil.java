package com.mainsteam.stm.plugin.sunjes.util;

import org.apache.commons.lang.StringUtils;

import com.mainsteam.stm.plugin.parameter.JBrokerParameter;

public class SunJESCollectUtil {
	/**
	 * 服务器可用性
	 * 
	 * @return
	 */
	public static String getAvailability(JBrokerParameter oneobj) {
		return getSunJesCollector(oneobj).getAvailability();
	}

	/**
	 * JVM允许使用的最大堆大小
	 * 
	 * @return
	 */
	public static String getJvmUpperboundHeapSize(JBrokerParameter oneobj) {
		return getSunJesCollector(oneobj).getJvmUpperboundHeapSize();
	}

	/**
	 * 版本
	 * 
	 * @return
	 */
	public static String getVersion(JBrokerParameter oneobj) {
		return getSunJesCollector(oneobj).getVersion();
	}

	/**
	 * 5分钟平均负载
	 * 
	 * @return
	 */
	public static String getAvgLoad5m(JBrokerParameter oneobj) {
		return getSunJesCollector(oneobj).getAvgLoad5m();
	}

	/**
	 * 回滚事务数
	 * 
	 * @return
	 */
	public static String getRolledbackTxcnt(JBrokerParameter oneobj) {
		return getSunJesCollector(oneobj).getRolledbackTxcnt();
	}

	/**
	 * 当前活动事务数
	 * 
	 * @return
	 */
	public static String getActiveTxCnt(JBrokerParameter oneobj) {
		return getSunJesCollector(oneobj).getActiveTxCnt();
	}

	/**
	 * 15分钟平均负载
	 * 
	 * @return
	 */
	public static String getAvgLoad15m(JBrokerParameter oneobj) {
		return getSunJesCollector(oneobj).getAvgLoad15m();
	}

	/**
	 * 已提交的事务数目
	 * 
	 * @return
	 */
	public static String getCommittedTxCnt(JBrokerParameter oneobj) {
		return getSunJesCollector(oneobj).getCommittedTxCnt();
	}

	/**
	 * 1分钟平均负载
	 * 
	 * @return
	 */
	public static String getAvgLoad1m(JBrokerParameter oneobj) {
		return getSunJesCollector(oneobj).getAvgLoad1m();
	}

	/**
	 * JVM运行时已分配的堆大小
	 * 
	 * @return
	 */
	public static String getJvmCommittedHeapSize(JBrokerParameter oneobj) {
		return getSunJesCollector(oneobj).getJvmCommittedHeapSize();
	}

	/**
	 * Http 端口
	 * 
	 * @return
	 */
	public static String getHttpPort(JBrokerParameter oneobj) {
		return getSunJesCollector(oneobj).getHttpPort();
	}

	/**
	 * 资源显示名称
	 * 
	 * @return
	 */
	public static String getResDisplayName(JBrokerParameter oneobj) {
		return getSunJesCollector(oneobj).getResDisplayName();
	}

	/**
	 * 最大线程数量
	 * 
	 * @return
	 */
	public static String getMaxThreadCount(JBrokerParameter oneobj) {
		return getSunJesCollector(oneobj).getMaxThreadCount();
	}

	/**
	 * JVM运行时已使用堆大小
	 * 
	 * @return
	 */
	public static String getJvmUsedHeapSize(JBrokerParameter oneobj) {
		return getSunJesCollector(oneobj).getJvmUsedHeapSize();
	}

	/**
	 * JVM内存利用率
	 * 
	 * @return
	 */
	public static String getJVMMEMRate(JBrokerParameter oneobj) {
		return getSunJesCollector(oneobj).getJVMMEMRate();
	}

	/**
	 * 取得JDBC指标
	 * 
	 * @return
	 */
	public static String getJdbcResult(JBrokerParameter oneobj) {
		String jdbcresult = getSunJesCollector(oneobj).getJdbcResult();
		return jdbcresult;
	}

	/**
	 * 取得线程池指标
	 * 
	 * @return
	 */
	public static String getTreadResult(JBrokerParameter oneobj) {
		String threadresult = getSunJesCollector(oneobj).getTreadResult();
		return threadresult;
	}

	/**
	 * 取得Web应用指标
	 * 
	 * @return
	 */
	public static String getWebAppResult(JBrokerParameter oneobj) {
		return getSunJesCollector(oneobj).getWebAppResult();
	}

	private static SunJESCollector getSunJesCollector(JBrokerParameter oneobj) {
		return new SunJESCollector(CommonUtils.getSunJESConnInfo(oneobj));
	}

	public static boolean check(JBrokerParameter oneobj) {
		String ip = oneobj.getIp();
		int port = oneobj.getPort();
		String instanceName = oneobj.getSunjesBo().getInstanceName();
		String userName = oneobj.getUsername();
		String password = oneobj.getPassword();
		SunJESConnInfo connInfo = new SunJESConnInfo(ip, port, instanceName,
				userName, password);
		SunJESCollector collector = new SunJESCollector(connInfo);
		String avail = collector.getAvailability();
		if (StringUtils.equals(avail, "1")) {
			return true;
		} else {
			return false;
		}
	}
}
