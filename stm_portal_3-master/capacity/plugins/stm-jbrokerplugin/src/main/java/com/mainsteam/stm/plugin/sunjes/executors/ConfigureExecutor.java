package com.mainsteam.stm.plugin.sunjes.executors;

import com.mainsteam.stm.plugin.sunjes.amx.AMXManager;
import com.mainsteam.stm.plugin.sunjes.util.SunJESConnInfo;

/**
 * ConfigureExecutor
 */
public class ConfigureExecutor extends BaseExecutor {

	
	   private static final String CONFIG_SYSTEM_PROPERTY = "-config.system-property.";
	   private static final String HTTP_SERVICE_PORT = ".http-service.http-listener.http-listener-1.port";

	/**
     * 资源显示名称
     * 
     * @param manager AMXManager
     * @return Matric
     * @throws Exception 异常
     */
    public String getResDisplayName(final AMXManager manager) {
        // String port = getHttpPort(manager);
        return m_connInfo.getIp() + "_" + m_connInfo.getPort() 
                + "_" + this.m_connInfo.getInstanceName();
    }

    /**
     * GET HTTP PORT
     * @param manager AMXManager
     * @return HTTP PORT
     */
    public String getHttpPort(final AMXManager manager) {
        String t_InstanceName = this.m_connInfo.getInstanceName();
        String t_httpPort = manager.getConfigDottedNameValue(
                t_InstanceName + HTTP_SERVICE_PORT).toString();
        if (t_httpPort.indexOf("${") > -1) {
            t_httpPort = t_httpPort.substring(2, t_httpPort.length() - 1);
            t_httpPort = manager.getConfigDottedNameValue(t_InstanceName + CONFIG_SYSTEM_PROPERTY + t_httpPort).toString();
        }
        return t_httpPort;
    }
    
    /**
     * Constructors.
     * @param source 连接信息
     */
    public ConfigureExecutor(final SunJESConnInfo source) {
        this.m_connInfo = source;
    }

    /**
     * SUNJES版本
     * 
     * @param manager AMXManager
     * @return Matric
     * @throws PluginException 
     */
    public String getVersion(final AMXManager manager) {
        String t_version = manager.getAppVersion().toString();
        return t_version;
    }

 

}
