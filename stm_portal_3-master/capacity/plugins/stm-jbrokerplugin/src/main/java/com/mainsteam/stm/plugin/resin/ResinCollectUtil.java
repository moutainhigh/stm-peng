package com.mainsteam.stm.plugin.resin;

import com.mainsteam.stm.plugin.parameter.JBrokerParameter;

public class ResinCollectUtil {
	public static String getAvailability(JBrokerParameter obj) { 
		ResinCollector collector = getCollector(obj);
		return collector.getAvailability();
	}

	public static String getResDisplayName(JBrokerParameter obj) { 
		ResinCollector collector = getCollector(obj);
		return collector.getResDisplayName();
	}

	public static String getOS(JBrokerParameter obj) { 
		ResinCollector collector = getCollector(obj);
		return collector.getOS();
	}

	public static String getHomeDir(JBrokerParameter obj) { 
		ResinCollector collector = getCollector(obj);
		return collector.getHomeDir();
	}

	public static String getHttpPort(JBrokerParameter obj) { 
		ResinCollector collector = getCollector(obj);
		return collector.getHttpPort();
	}

	public static String getThreadPoolOverView(JBrokerParameter obj) { 
		ResinCollector collector = getCollector(obj);
		return collector.getThreadPoolOverView();
	}

	public static String getJdbcConnDetail(JBrokerParameter obj) { 
		ResinCollector collector = getCollector(obj);
		return collector.getJdbcConnDetail();
	}

	public static String getWebAppDetail(JBrokerParameter obj) { 
		ResinCollector collector = getCollector(obj);
		return collector.getWebAppDetail();
	}

	public static String getHostName(JBrokerParameter obj) { 
		ResinCollector collector = getCollector(obj);
		return collector.getHostName();
	}

	public static String getVersion(JBrokerParameter obj) { 
		ResinCollector collector = getCollector(obj);
		return collector.getVersion();
	}

	private static ResinCollector getCollector(JBrokerParameter obj) {
		return new ResinCollector(obj);
	}
	
}
