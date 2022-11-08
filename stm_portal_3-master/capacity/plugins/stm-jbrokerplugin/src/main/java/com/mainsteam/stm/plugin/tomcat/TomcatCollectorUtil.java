package com.mainsteam.stm.plugin.tomcat;

import java.util.List;
import java.util.Map;

import com.mainsteam.stm.plugin.parameter.JBrokerParameter;

public class TomcatCollectorUtil{
	
	//Tomcat 大版本
	private static final String VERSION_5X = "5";
	private static final String VERSION_6X = "6";
	private static final String VERSION_7X = "7";
	private static final String VERSION_8X = "8";
			

	private static TomcatCollector getCollector(JBrokerParameter tomcatBo) {
		String ip = tomcatBo.getIp();
		int port = tomcatBo.getPort();
		String url = "http://"+ip+":"+port+"/manager/status";
		String username = tomcatBo.getUsername();
		String version = tomcatBo.getTomcatVersion();
		String password = tomcatBo.getPassword();
		TomcatCollector.TomcatVersion tomcatVersion = getTomcatVersion(version);
		return TomcatCollector.getInstance(url, username, password, tomcatVersion);
	} 
	
	
	public static TomcatCollector.TomcatVersion getTomcatVersion(String version){
		TomcatCollector.TomcatVersion tomcatVersion = null;
		if(version != null){
			if(version.contains(VERSION_5X)){
				tomcatVersion = TomcatCollector.TomcatVersion.Tomcat5x;
			}else if(version.contains(VERSION_6X)){
				tomcatVersion = TomcatCollector.TomcatVersion.Tomcat6x;
			}else if(version.contains(VERSION_7X)){
				tomcatVersion = TomcatCollector.TomcatVersion.Tomcat7x;
			}else if(version.contains(VERSION_8X)){
				tomcatVersion = TomcatCollector.TomcatVersion.Tomcat8x;
			}
		}
		return tomcatVersion;
	}
	
	/*public boolean isAppOnline(String ip, Map<String, String> connInfos,
			AppErrorCode retValue) {
		
		String url = "http://"+ip+":"+connInfos.get("appPort")+"/manager/status";
		String username = connInfos.get("appUser");
		String password = connInfos.get("appPassword");
		TomcatCollector.TomcatVersion version = getTomcatVersion(connInfos.get("appVersion"));
		TomcatCollector connInfo = new TomcatCollector(url, username, password, version);
		String state = connInfo.getAvailability();
		if("1".equals(state)){
			return true;
		}else{
			if("-1".equals(state)){
				retValue.setErrorCode(AppErrorCode.ERROR_401);
			}else{
				retValue.setErrorCode(AppErrorCode.ERROR_404);
			}
			return false;
		}
	}*/
	
	/**
	 * 服务可用性
	 * @param oneObj
	 * @return
	 */
	public static String getOc4jAvailability(JBrokerParameter tomcatBo) {
		return getCollector(tomcatBo).getAvailability();
	}
	
	public String getStatusHtml(JBrokerParameter tomcatBo){
		return getCollector(tomcatBo).getStatusHtml();
	}
	
	/**
	 * JVM
	 * @param oneObj
	 * @param version
	 * @return
	 */
	public Map<String,List<String>> getJVmSubResource(JBrokerParameter tomcatBo){
		return getCollector(tomcatBo).getJVmSubResource(getTomcatVersion(tomcatBo.getTomcatVersion()));
	}
	
	/**
	 * JVM-Memory Pool
	 * @param oneObj
	 * @param version
	 * @return
	 */
	public String getJVMMemoryPool(JBrokerParameter tomcatBo){
		return getCollector(tomcatBo).getJVMMemoryPool(getTomcatVersion(tomcatBo.getTomcatVersion()));
	}
	
	/**
	 * JVM-Type
	 * @param oneObj
	 * @param version
	 * @return
	 */
	public String getJVMType(JBrokerParameter tomcatBo){
		return getCollector(tomcatBo).getJVMType(getTomcatVersion(tomcatBo.getTomcatVersion()));
	}
	
	/**
	 * JVM-Initial
	 * @param oneObj
	 * @param version
	 * @return
	 */
	public String getJVMInitial(JBrokerParameter tomcatBo){
		return getCollector(tomcatBo).getJVMInitial(getTomcatVersion(tomcatBo.getTomcatVersion()));
	}
	
	/**
	 * JVM-Total
	 * @param oneObj
	 * @param version
	 * @return
	 */
	public String getJVMTotal(JBrokerParameter tomcatBo){
		return getCollector(tomcatBo).getJVMTotal(getTomcatVersion(tomcatBo.getTomcatVersion()));
	}
	
	/**
	 * JVM-Used
	 * @param oneObj
	 * @param version
	 * @return
	 */
	public String getJVMUsed(JBrokerParameter tomcatBo){
		return getCollector(tomcatBo).getJVMUsed(getTomcatVersion(tomcatBo.getTomcatVersion()));
	}
	
	/**
	 * JVM-Maximum
	 * @param oneObj
	 * @param version
	 * @return
	 */
	public String getJVMMaximum(JBrokerParameter tomcatBo){
		return getCollector(tomcatBo).getJVMMaximum(getTomcatVersion(tomcatBo.getTomcatVersion()));
	}
	
}
