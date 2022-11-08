package com.mainsteam.stm.plugin.weblogic;

public class WeblogicConstant {
	public static final String SERVER_RUNTIMES="ServerRuntimes";
	public static final String DOMAINRUNTIME_SERVICE_MBEAN="com.bea:Name=DomainRuntimeService,Type=weblogic.management.mbeanservers.domainruntime.DomainRuntimeServiceMBean";
	
	public static final String DOMAIN_CONFIGURATION="DomainConfiguration";
	/**
	 * 可用性，“1”表示可用，“-1”表示不可用
	 */
	public static final String AVAILABLE="1";
	public static final String NO_AVAILABLE="0";
}
