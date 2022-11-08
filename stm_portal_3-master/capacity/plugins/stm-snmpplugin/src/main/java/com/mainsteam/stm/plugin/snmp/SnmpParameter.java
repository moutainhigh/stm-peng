/**
 *
 */
package com.mainsteam.stm.plugin.snmp;

/**
 * @author xiaop_000
 */
@Deprecated
public class SnmpParameter {

    public String ip;
    /**
     * SNMP端口
     */
    public int port = 161;
    /**
     * snmp版本 1,2c,3
     */
    public int version = 1;
    /**
     * OID
     */
    private String[] oids;
    private String[] variables;
    private String[] types;
    /**
     * 1和2c版本的团体字名
     */
    private String community = "public";
    /**
     * USM username(用户名)
     */
    private String securityName;
    /**
     * 认证级别（包括不认证不加密，认证但不加密，认证且加密）
     */
    private int securityLevel = 3;
    /**
     * 认证协议
     */
    private String authProtocol;
    /**
     * 认证密钥
     */
    private String authPassphrase;
    /**
     * 加密协议
     */
    private String privacyProtocol;
    /**
     * 加密密钥
     */
    private String privacyPassphrase;
    /**
     * 重试次数
     */
    private int snmpRetry = 1;
    /**
     * 连接超时时间
     */
    private int snmpTimeout = 3000;
    /**
     * 采集方式代码
     */
    private int pduCode;

    public SnmpParameter() {
    }

    public String[] getOids() {
        return oids;
    }

    public void setOids(String[] oids) {
        this.oids = oids;
    }

    public String getIp() {
        return ip;
    }

    public void setIp(String ip) {
        this.ip = ip;
    }

    public int getPort() {
        return port;
    }

    public void setPort(int port) {
        this.port = port;
    }

    public int getVersion() {
        return version;
    }

    public void setVersion(int version) {
        this.version = version;
    }

    public String getSecurityName() {
        return securityName;
    }

    public void setSecurityName(String securityName) {
        this.securityName = securityName;
    }

    public int getSecurityLevel() {
        return securityLevel;
    }

    public void setSecurityLevel(int securityLevel) {
        this.securityLevel = securityLevel;
    }

    public String getAuthProtocol() {
        return authProtocol;
    }

    public void setAuthProtocol(String authProtocol) {
        this.authProtocol = authProtocol;
    }

    public String getPrivacyProtocol() {
        return privacyProtocol;
    }

    public void setPrivacyProtocol(String privacyProtocol) {
        this.privacyProtocol = privacyProtocol;
    }

    public String getAuthPassphrase() {
        return authPassphrase;
    }

    public void setAuthPassphrase(String authPassphrase) {
        this.authPassphrase = authPassphrase;
    }

    public String getPrivacyPassphrase() {
        return privacyPassphrase;
    }

    public void setPrivacyPassphrase(String privacyPassphrase) {
        this.privacyPassphrase = privacyPassphrase;
    }

    public int getSnmpRetry() {
        return snmpRetry;
    }

    public void setSnmpRetry(int snmpRetry) {
        this.snmpRetry = snmpRetry;
    }

    public int getSnmpTimeout() {
        return snmpTimeout;
    }

    public void setSnmpTimeout(int snmpTimeout) {
        this.snmpTimeout = snmpTimeout;
    }

    public String getCommunity() {
        return community;
    }

    public void setCommunity(String community) {
        this.community = community;
    }

    public int getPduCode() {
        return pduCode;
    }

    public void setPduCode(int pduCode) {
        this.pduCode = pduCode;
    }

    @Override
    public String toString() {
        StringBuffer sb = new StringBuffer();
        try {
            sb.append("ip/port:").append(this.ip).append("/").append(port).append(";version:").append(this.version)
                    .append(";").append("oids:");
            if (this.oids != null) {
                for (String oid : this.oids) {
                    sb.append(oid).append(";");
                }
            }
        } catch (Exception e) {
            return null;
        }
        return sb.toString();
    }

    public String[] getVariables() {
        return variables;
    }

    public void setVariables(String[] variables) {
        this.variables = variables;
    }

    public String[] getTypes() {
        return types;
    }

    public void setTypes(String[] types) {
        this.types = types;
    }
}
