package com.mainsteam.stm.plugin.apache;

import com.mainsteam.stm.plugin.parameter.JBrokerParameter;

public class ApacheCollectorUtil {

private static ApacheCollector getCollector(JBrokerParameter obj) {
		String ip = obj.getIp();
		int port = obj.getPort();
		boolean isSsl = obj.getApacheBo().isSSL();
		String urlParam = obj.getApacheBo().getUrlParam();
		String url = (isSsl?"https" : "http")+"://"+ip+":"+port+"/"+urlParam;
		String username = obj.getUsername();
		String password = obj.getPassword();
		long timeout = obj.getTimeout();
		return ApacheCollector.getInstance(url, username, password,com.mainsteam.stm.plugin.apache.ApacheCollector.ApacheVersion.Apache2, timeout);
	} 

	public static boolean isConnect(JBrokerParameter obj) {
		boolean isSsl = obj.getApacheBo().isSSL();
		String ip = obj.getIp();
		int port = obj.getPort();
		String urlParam = obj.getApacheBo().getUrlParam();
		String url = (isSsl ? "https" : "http") + "://" + ip + ":" + port + "/"
				+ urlParam;
		String username = obj.getUsername();
		String password = obj.getPassword();
		int timeout = obj.getTimeout();
		com.mainsteam.stm.plugin.apache.ApacheCollector.ApacheVersion version = com.mainsteam.stm.plugin.apache.ApacheCollector.ApacheVersion.Apache2;
		ApacheCollector connInfo = new ApacheCollector(url, username, password,
				version, timeout);
		String state = connInfo.getAvailability();
		if ("1".equals(state)) {
			return true;
		} else {
			return false;
		}
}
	public static String getVersion(JBrokerParameter oneObj){
		String version="Apache2.2.22";
		return version;
	}
	/**
	 * 服务可用性
	 * @param oneObj
	 * @return
	 */
	public static String getOc4jAvailability(JBrokerParameter oneObj) {
		return getCollector(oneObj).getAvailability();
	}
	
	/**
	 * 返回状态页面全部HTML数据
	 * @return
	 */
	public static String getStatusHtml(JBrokerParameter oneObj){
		return getCollector(oneObj).getStatusHtml();
	}
	
	/**
	 * apache运行时间
	 * @param oneObj
	 * @return
	 */
	public static String getServerUpTime(JBrokerParameter oneObj) {
		return getCollector(oneObj).getServerUpTime();
	}
}
