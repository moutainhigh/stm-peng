package com.mainsteam.stm.plugin.fusioncompute.bo;

/**
 * The connection information class preparation
 * 
 * @author yuanlb
 * @2016年1月14日 上午10:56:16
 */

public class ConnectionInfo {
    public final String serverIp, port, username, password;

    public ConnectionInfo(String serverIp, String port, String username,
            String password) {
        super();
        this.serverIp = serverIp;
        this.port = port;
        this.username = username;
        this.password = password;
    }
}
