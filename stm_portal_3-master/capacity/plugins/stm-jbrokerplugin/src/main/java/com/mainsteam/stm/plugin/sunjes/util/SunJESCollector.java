package com.mainsteam.stm.plugin.sunjes.util;

import java.util.Map;

import com.mainsteam.stm.plugin.sunjes.amx.AMXManager;
import com.mainsteam.stm.plugin.sunjes.executors.ConfigureExecutor;
import com.mainsteam.stm.plugin.sunjes.executors.HttpThreadExecutor;
import com.mainsteam.stm.plugin.sunjes.executors.JdbcPoolExecutor;
import com.mainsteam.stm.plugin.sunjes.executors.JvmExecutor;
import com.mainsteam.stm.plugin.sunjes.executors.ServerAvailityExecutor;
import com.mainsteam.stm.plugin.sunjes.executors.ThreadPoolExecutor;
import com.mainsteam.stm.plugin.sunjes.executors.TransactionsExecutor;
import com.mainsteam.stm.plugin.sunjes.executors.WebAppExecutor;

public class SunJESCollector {
	private AMXManager amxManager;
	private SunJESConnInfo connInfo;
	
	public SunJESCollector(Map<String, String> params) {
		connInfo = CommonUtils.getSunJESConnInfo(params);
		amxManager = new AMXManager(connInfo);
	}
	
	public SunJESCollector(SunJESConnInfo connInfo) {
		this.connInfo = connInfo;
		amxManager = new AMXManager(connInfo);
	}
	
	/**
	 * 服务器可用性
	 * @return
	 */
	public String getAvailability() {
		try {
			return new ServerAvailityExecutor(connInfo).getAvailability(amxManager);
		} catch(Exception ex) {
			return "0";
		}
	}
	
	/**
	 * JVM允许使用的最大堆大小
	 * @return
	 */
	public String getJvmUpperboundHeapSize() {
		try {
			return new JvmExecutor(connInfo).getJvmUpperboundHeapSize(amxManager);
		} catch (Exception e) {
			// TODO: handle exception
			return "0";
		}
	}
	
	/**
	 * 版本
	 * @return
	 */
	public String getVersion() {
		try {
			return new ConfigureExecutor(connInfo).getVersion(amxManager);
		} catch (Exception e) {
			// TODO: handle exception
			return "";
		}
	}
	
	/**
	 * 5分钟平均负载
	 * @return
	 */
	public String getAvgLoad5m() {
		try {
			return new HttpThreadExecutor(connInfo).getAvgLoad5m(amxManager);
		} catch (Exception e) {
			// TODO: handle exception
			return "0";
		}
	}
	
	/**
	 * 回滚事务数
	 * @return
	 */
	public String getRolledbackTxcnt() {
		try {
			return new TransactionsExecutor(connInfo).getRolledbackTransactionCount(amxManager);
		} catch (Exception e) {
			// TODO: handle exception
			return "0";
		}
	}
	
	/**
	 * 当前活动事务数
	 * @return
	 */
	public String getActiveTxCnt() {
		try {
			return new TransactionsExecutor(connInfo).getActiveTransactionCount(amxManager);
		} catch (Exception e) {
			return "0";
		}
	}

	
	/**
	 * 15分钟平均负载
	 * @return
	 */
	public String getAvgLoad15m() {
		try {
			return new HttpThreadExecutor(connInfo).getAvgLoad15m(amxManager);
		} catch (Exception e) {
			return "0";
		}
	}
	
	/**
	 * 已提交的事务数目
	 * @return
	 */
	public String getCommittedTxCnt() {
		try {
			return new TransactionsExecutor(connInfo).getCommittedTransactionCount(amxManager);
		} catch (Exception e) {
			return "0";
		}
	}
	
	/**
	 * 1分钟平均负载
	 * @return
	 */
	public String getAvgLoad1m() {
		try {
			return new HttpThreadExecutor(connInfo).getAvgLoad1m(amxManager);
		} catch (Exception e) {
			return "0";
		}
	}
	
	/**
	 * JVM运行时已分配的堆大小
	 * @return
	 */
	public String getJvmCommittedHeapSize() {
		try {
			return new JvmExecutor(connInfo).getJvmCommittedHeapSize(amxManager);
		} catch (Exception e) {
			return "0";
		}
	}
	
	/**
	 * Http 端口
	 * @return
	 */
	public String getHttpPort() {
		try {
			return new ConfigureExecutor(connInfo).getHttpPort(amxManager);
		} catch (Exception e) {
			return "";
		}
	}
	
	/**
	 * 资源显示名称
	 * @return
	 */
	public String getResDisplayName() {
		try {
			return new ConfigureExecutor(connInfo).getResDisplayName(amxManager);
		} catch (Exception e) {
			return "";
		}
	}
	
	/**
	 * 最大线程数量
	 * @return
	 */
	public String getMaxThreadCount() {
		try {
			return new HttpThreadExecutor(connInfo).getMaxThreadCount(amxManager);
		} catch (Exception e) {
			return "0";
		}
	}
	
	/**
	 * JVM运行时已使用堆大小
	 * @return
	 */
	public String getJvmUsedHeapSize() {
		try {
			return new JvmExecutor(connInfo).getJvmUsedHeapSize(amxManager);
		} catch (Exception e) {
			return "0";
		}
	}
	
	/**
	 * JVM内存利用率
	 * @return
	 */
	public String getJVMMEMRate() {
		try {
			return new JvmExecutor(connInfo).getJvmMEMRate(amxManager);
		} catch (Exception e) {
			return "0";
		}
	}
	
	/**
	 * 取得JDBC指标
	 * @return
	 */
	public String getJdbcResult() {
		try {
			 String result=new JdbcPoolExecutor(connInfo).getJdbcResult(amxManager);
			 return result;
		} catch (Exception e) {
			return "";
		}
	}
	
	/**
	 * 取得线程池指标
	 * @return
	 */
	public String getTreadResult() {
		try {
			 String threadresult=new ThreadPoolExecutor(connInfo).getThreadResult(amxManager);
			 return threadresult;
		} catch (Exception e) {
			return "";
		}
	}
	
	/**
	 * 取得Web应用指标
	 * @return
	 */
	public String getWebAppResult() {
		try {
			return new WebAppExecutor(connInfo).getWebAppResult(amxManager);
		} catch (Exception e) {
			return "";
		}
	}
	
}
 
