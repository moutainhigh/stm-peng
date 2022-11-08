package com.mainsteam.stm.plugin.oracleas;


import javax.management.remote.JMXConnector;

import com.mainsteam.stm.plugin.parameter.JBrokerParameter;

public class OracleASCollectorUtil {
	public OracleASCollectorUtil(){}

	private static OracleASCollector getCollector(JBrokerParameter obj) {
		String oc4jInstanceName = obj.getOracleasBo().getOc4jInstanceName();
		JMXConnector connection=obj.getOracleasBo().getConnection();
		JMXConnector clusterConnection=obj.getOracleasBo().getClusterConnection();
		return OracleASCollector.getInstance(oc4jInstanceName,connection,clusterConnection);
	}

	public static String getServerName(JBrokerParameter obj) {
		return getCollector(obj).getServerName();
	}

	public static String getInstallDirectory(JBrokerParameter obj) {
		return getCollector(obj).getInstallDirectory();
	}

	public static String getServerVersion(JBrokerParameter obj) {
		return getCollector(obj).getServerVersion();
	}

	public static String getAvailability(JBrokerParameter obj) {
		return getCollector(obj).getAvailability();
	}

	public static String getOc4jAvailability(JBrokerParameter obj) {
		String result=getCollector(obj).getOc4jAvailability();
		return result;
	}
	
	public static boolean check(JBrokerParameter obj) {
		return getCollector(obj).check();
	}

	public static float getJvmMemRate(JBrokerParameter obj) {
		return getCollector(obj).getJvmMemRate();
	}

//	public static Object getCpuRate(String ip, String port, String login,
//			String password, String oc4jInstanceName) {
//		return getCollector(oneObj).getCpuRate();
//	}

	public static long getFreeJDBCConnCnt(JBrokerParameter obj) {
		return getCollector(obj).getFreeJDBCConnCnt();
	}

	public static long getOpendJDBCConnCnt(JBrokerParameter obj) {
		return getCollector(obj).getOpendJDBCConnCnt();
	}

	public static long getJDBCConnCnt(JBrokerParameter obj) {
		return getCollector(obj).getJDBCConnCnt();
	}

	public static long getActiveTxCnt(JBrokerParameter obj) {
		return getCollector(obj).getActiveTxCnt();
	}

	public static long getCommitTxCnt(JBrokerParameter obj) {
		return getCollector(obj).getCommitTxCnt();
	}

	public static long getRollbackTxCnt(JBrokerParameter obj) {
		return getCollector(obj).getRollbackTxCnt();
	}

	public static long getActiveHandlerCnt(JBrokerParameter obj) {
		return getCollector(obj).getActiveHandlerCnt();
	}

	public static long getActiveConnCnt(JBrokerParameter obj) {
		return getCollector(obj).getActiveConnCnt();
	}

	public static float getTotalMem(JBrokerParameter obj) {
		return getCollector(obj).getTotalMem();
	}

	public static float getHeapUtilRatio(JBrokerParameter obj) {
		return getCollector(obj).getHeapUtilRatio();
	}

	public static String getConnPoolNames(JBrokerParameter obj) {
		return getCollector(obj).getConnPoolNames();
	}

	public static String getConnPoolSizes(JBrokerParameter obj) {
		return getCollector(obj).getConnPoolSizes();
	}

	public static String getThreedPoolNames(JBrokerParameter obj) {
		return getCollector(obj).getThreedPoolNames();
	}

	public static String getThreedPoolSizes(JBrokerParameter obj) {
		return getCollector(obj).getThreedPoolSizes();
	}
	
}
