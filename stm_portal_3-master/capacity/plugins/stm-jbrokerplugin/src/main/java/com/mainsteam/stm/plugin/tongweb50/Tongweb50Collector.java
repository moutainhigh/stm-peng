package com.mainsteam.stm.plugin.tongweb50;

import java.io.IOException;
import java.text.DecimalFormat;
import java.util.HashMap;
import java.util.Map;

import javax.management.AttributeNotFoundException;
import javax.management.InstanceNotFoundException;
import javax.management.MBeanException;
import javax.management.MBeanServerConnection;
import javax.management.MalformedObjectNameException;
import javax.management.ObjectName;
import javax.management.ReflectionException;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import com.mainsteam.stm.plugin.parameter.JBrokerParameter;

public class Tongweb50Collector {

	private Map<String, ObjectName> map = new HashMap<String, ObjectName>();

	private DecimalFormat df = new DecimalFormat("#.##");

	public Tongweb50Collector() {
		try {
			map.put("MBeanServerDelegate", new ObjectName(
					"JMImplementation:type=MBeanServerDelegate"));
			map.put("Engine",
					new ObjectName("com.tongtech.tongweb:type=Engine"));
			map.put("http-listener",
					new ObjectName(
							"com.tongtech.tongweb:type=http-listener,id=http-listener-1,config=server,category=config"));
			map.put("keep-alive",
					new ObjectName(
							"com.tongtech.tongweb:type=keep-alive,config=server,category=config"));
			map.put("options",
					new ObjectName(
							"com.tongtech.tongweb:type=options,config=server,category=config"));
			map.put("web-contalner",
					new ObjectName(
							"com.tongtech.tongweb:type=web-container,config=server,category=config"));
			map.put("ClassLoading", new ObjectName(
					"java.lang:type=ClassLoading"));
			map.put("Threading", new ObjectName("java.lang:type=Threading"));
			map.put("OperatingSystem", new ObjectName(
					"java.lang:type=OperatingSystem"));
		} catch (MalformedObjectNameException e) {
			log.error(e);
		}
	}

	private Log log = LogFactory.getLog(Tongweb50Collector.class);

	public int availability(JBrokerParameter obj) {
		if (getConn(obj) != null) {
			return 1;
		}
		return 0;
	}

	public String getJDKVersion(JBrokerParameter obj) {
		try {
			return (String) getConn(obj).getAttribute(
					map.get("MBeanServerDelegate"), "ImplementationVersion");
		} catch (AttributeNotFoundException | InstanceNotFoundException
				| MBeanException | ReflectionException | IOException e) {
			log.error(e);
		}
		return null;
	}

	public String getPath(JBrokerParameter obj) {
		try {
			return (String) getConn(obj).getAttribute(map.get("Engine"),
					"baseDir");
		} catch (AttributeNotFoundException | InstanceNotFoundException
				| MBeanException | ReflectionException | IOException e) {
			log.error(e);
		}
		return null;
	}

	public String getServerPort(JBrokerParameter obj) {
		try {
			return (String) getConn(obj).getAttribute(map.get("http-listener"),
					"port");
		} catch (AttributeNotFoundException | InstanceNotFoundException
				| MBeanException | ReflectionException | IOException e) {
			log.error(e);
		}
		return null;
	}

	public String getMaxConnections(JBrokerParameter obj) {
		try {
			return (String) getConn(obj).getAttribute(map.get("keep-alive"),
					"max-connections");
		} catch (AttributeNotFoundException | InstanceNotFoundException
				| MBeanException | ReflectionException | IOException e) {
			log.error(e);
		}
		return null;
	}

	public String getThreadCount(JBrokerParameter obj) {
		try {
			return (String) getConn(obj).getAttribute(map.get("keep-alive"),
					"thread-count");
		} catch (AttributeNotFoundException | InstanceNotFoundException
				| MBeanException | ReflectionException | IOException e) {
			log.error(e);
		}
		return null;
	}

	public String getTimeoutInSeconds(JBrokerParameter obj) {
		try {
			return (String) getConn(obj).getAttribute(map.get("keep-alive"),
					"timeout-in-seconds");
		} catch (AttributeNotFoundException | InstanceNotFoundException
				| MBeanException | ReflectionException | IOException e) {
			log.error(e);
		}
		return null;
	}

	public String getHttpVersion(JBrokerParameter obj) {
		try {
			return (String) getConn(obj).getAttribute(map.get("options"),
					"version");
		} catch (AttributeNotFoundException | InstanceNotFoundException
				| MBeanException | ReflectionException | IOException e) {
			log.error(e);
		}
		return null;
	}

	public String getDefaultType(JBrokerParameter obj) {
		try {
			return (String) getConn(obj).getAttribute(map.get("options"),
					"default-type");
		} catch (AttributeNotFoundException | InstanceNotFoundException
				| MBeanException | ReflectionException | IOException e) {
			log.error(e);
		}
		return null;
	}

	public String getParameterEncoding(JBrokerParameter obj) {
		try {
			return (String) getConn(obj).getAttribute(map.get("web-contalner"),
					"parameter-encoding");
		} catch (AttributeNotFoundException | InstanceNotFoundException
				| MBeanException | ReflectionException | IOException e) {
			log.error(e);
		}
		return null;
	}

	public String getResponseEncoding(JBrokerParameter obj) {
		try {
			return (String) getConn(obj).getAttribute(map.get("web-contalner"),
					"response-encoding");
		} catch (AttributeNotFoundException | InstanceNotFoundException
				| MBeanException | ReflectionException | IOException e) {
			log.error(e);
		}
		return null;
	}

	public String getLoadedClassCount(JBrokerParameter obj) {
		try {
			return (String) getConn(obj).getAttribute(map.get("ClassLoading"),
					"LoadedClassCount").toString();
		} catch (AttributeNotFoundException | InstanceNotFoundException
				| MBeanException | ReflectionException | IOException e) {
			log.error(e);
		}
		return null;
	}

	public String getTotalLoadedClassCount(JBrokerParameter obj) {
		try {
			return (String) getConn(obj).getAttribute(map.get("ClassLoading"),
					"TotalLoadedClassCount").toString();
		} catch (AttributeNotFoundException | InstanceNotFoundException
				| MBeanException | ReflectionException | IOException e) {
			log.error(e);
		}
		return null;
	}

	public String getUnLoadedClassCount(JBrokerParameter obj) {
		try {
			return (String) getConn(obj).getAttribute(map.get("ClassLoading"),
					"UnloadedClassCount").toString();
		} catch (AttributeNotFoundException | InstanceNotFoundException
				| MBeanException | ReflectionException | IOException e) {
			log.error(e);
		}
		return null;
	}

	public String getDaemonThreadCount(JBrokerParameter obj) {
		try {
			return getConn(obj).getAttribute(map.get("Threading"),
					"DaemonThreadCount").toString();
		} catch (AttributeNotFoundException | InstanceNotFoundException
				| MBeanException | ReflectionException | IOException e) {
			log.error(e);
		}
		return null;
	}

	public String getPeakThreadCount(JBrokerParameter obj) {
		try {
			return (String) getConn(obj).getAttribute(map.get("Threading"),
					"PeakThreadCount").toString();
		} catch (AttributeNotFoundException | InstanceNotFoundException
				| MBeanException | ReflectionException | IOException e) {
			log.error(e);
		}
		return null;
	}

	public String getNowThreadCount(JBrokerParameter obj) {
		try {
			return (String) getConn(obj).getAttribute(map.get("Threading"),
					"ThreadCount").toString();
		} catch (AttributeNotFoundException | InstanceNotFoundException
				| MBeanException | ReflectionException | IOException e) {
			log.error(e);
		}
		return null;
	}

	public String getTotalStartedThreadCount(JBrokerParameter obj) {
		try {
			return (String) getConn(obj).getAttribute(map.get("Threading"),
					"TotalStartedThreadCount").toString();
		} catch (AttributeNotFoundException | InstanceNotFoundException
				| MBeanException | ReflectionException | IOException e) {
			log.error(e);
		}
		return null;
	}

	public String appCpuRate(JBrokerParameter obj) {
		try {
			String r = df.format(getConn(obj).getAttribute(
					map.get("OperatingSystem"), "ProcessCpuLoad"));
			if (r != null && r.startsWith("-")) {
				return "0";
			}
			return r;
		} catch (AttributeNotFoundException | InstanceNotFoundException
				| MBeanException | ReflectionException | IOException e) {
			log.error(e);
		}
		return null;
	}

	public String appMemRate(JBrokerParameter obj) {
		try {
			Double t = Double.parseDouble(getConn(obj).getAttribute(
					map.get("OperatingSystem"), "TotalPhysicalMemorySize")
					.toString());
			Double f = Double.parseDouble(getConn(obj).getAttribute(
					map.get("OperatingSystem"), "FreePhysicalMemorySize")
					.toString());
			return df.format(100.0 - (t / f));
		} catch (AttributeNotFoundException | InstanceNotFoundException
				| MBeanException | ReflectionException | IOException e) {
			log.error(e);
		}
		return null;
	}

	private static MBeanServerConnection getConn(JBrokerParameter obj) {
		MBeanServerConnection mbsc = obj.getmBeanServerConnection();
		return mbsc;
	};
}
