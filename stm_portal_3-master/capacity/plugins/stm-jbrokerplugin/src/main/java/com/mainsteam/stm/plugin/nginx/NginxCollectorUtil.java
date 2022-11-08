

package com.mainsteam.stm.plugin.nginx;


import com.mainsteam.stm.plugin.parameter.JBrokerParameter;

public class NginxCollectorUtil {

	
	private static NginxCollector getCollector(JBrokerParameter obj) {
		String ip = obj.getIp();
		String pagename = obj.getNiginxBo().getPageName();
		int port = obj.getPort();
		long timeout = obj.getTimeout();
		String url = ip+":"+port+"/"+pagename;
		return NginxCollector.getInstance(url,pagename,timeout);
		}
	public static boolean isConnect(JBrokerParameter obj) {
		String ip = obj.getIp();
		int port = obj.getPort();
		String pagename = obj.getNiginxBo().getPageName();
		int timeout = obj.getTimeout();
		String url = ip+":"+port+"/"+pagename;
		NginxCollector connInfo = new NginxCollector(url, pagename, timeout);
		String state = connInfo.getAvailability();
		if ("1".equals(state)) {
			return true;
		} else {
			return false;
		}
		
	}
	/**
	 * 返回状态页面全部HTML数据
	 * @return
	 */
	public static String getStatusHtml(JBrokerParameter oneObj){
		return getCollector(oneObj).getStatusHtml();
	}
	/**
	 * 服务可用性
	 * @param oneObj
	 * @return
	 */
	public static String getOc4jAvailability(JBrokerParameter oneObj) {
		return getCollector(oneObj).getAvailability();
	}
	public static String getIp(JBrokerParameter oneObj) {
		return getCollector(oneObj).getIp();
	}
}
