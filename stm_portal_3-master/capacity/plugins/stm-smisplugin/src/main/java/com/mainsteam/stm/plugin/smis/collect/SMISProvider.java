package com.mainsteam.stm.plugin.smis.collect;

public class SMISProvider {
    public static final String IP = "IP";
    public static final String PORT = "smisPort";
    public static final String NAMESPACE = "namespace";
    public static final String USERNAME = "username";
    public static final String PASSWORD = "password";
    public static final String PROTOCOL = "protocol";
    public static final String VENDOR = "vendor";
    public static final String SMISIP = "SMISIP";

    public static final String VENDOR_HITACHI = "HITACHI";
    public static final String VENDOR_IBM_TSSVC = "IBMV7000";
    public static final String VENDOR_IBMLSI = "LSI";
    public static final String VENDOR_IBMTSDS = "TSDS";
    public static final String VENDOR_HP = "HPQMSA2312sa";
    public static final String VENDOR_HP3PAR = "HP3Par";
    public static final String VENDOR_HPMSA = "HPMSA";
    public static final String VENDOR_HPEVA = "HPEVA";
    public static final String VENDOR_HUAWEI = "HUAWEI";
    public static final String VENDOR_HUAWEIV = "HUAWEI2000";
    public static final String VENDOR_NETAPP_ONTAP = "NetAppONTAP";
    public static final String VENDOR_DELL = "Dell";
    public static final String VENDOR_EMC = "EMC";
    public static final String VENDOR_EMC_UNITY = "EMC680";
    public static final String VENDOR_MacroSAN = "MacroSAN";//宏衫存储
    public static final String VENDOR_Sugon = "Sugon";//曙光
    
    private String ip, port, nameSpace, username, password, protocol, vendor;
    private String smisip = "-1";

    public String getSmisip() {
		return smisip;
	}

	public void setSmisip(String smisip) {
		this.smisip = smisip;
	}

	public String getIp() {
        return ip;
    }

    public void setIp(String ip) {
        this.ip = ip;
    }

    public String getPort() {
        return port;
    }

    public void setPort(String port) {
        this.port = port;
    }

    public String getNameSpace() {
        return nameSpace;
    }

    public void setNameSpace(String nameSpace) {
        this.nameSpace = nameSpace;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getProtocol() {
        return protocol;
    }

    public void setProtocol(String protocol) {
        this.protocol = protocol;
    }

    public String getVendor() {
        return vendor;
    }

    public void setVendor(String vendor) {
        this.vendor = vendor;
    }

    @Override
    public int hashCode() {
        final int prime = 31;
        int result = 1;
        result = prime * result + ((ip == null) ? 0 : ip.hashCode());
        result = prime * result + ((nameSpace == null) ? 0 : nameSpace.hashCode());
        result = prime * result + ((password == null) ? 0 : password.hashCode());
        result = prime * result + ((port == null) ? 0 : port.hashCode());
        result = prime * result + ((protocol == null) ? 0 : protocol.hashCode());
        result = prime * result + ((username == null) ? 0 : username.hashCode());
        result = prime * result + ((vendor == null) ? 0 : vendor.hashCode());
        return result;
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj)
            return true;
        if (obj == null)
            return false;
        if (getClass() != obj.getClass())
            return false;
        SMISProvider other = (SMISProvider) obj;
        if (ip == null) {
            if (other.ip != null)
                return false;
        } else if (!ip.equals(other.ip))
            return false;
        if (nameSpace == null) {
            if (other.nameSpace != null)
                return false;
        } else if (!nameSpace.equals(other.nameSpace))
            return false;
        if (password == null) {
            if (other.password != null)
                return false;
        } else if (!password.equals(other.password))
            return false;
        if (port == null) {
            if (other.port != null)
                return false;
        } else if (!port.equals(other.port))
            return false;
        if (protocol == null) {
            if (other.protocol != null)
                return false;
        } else if (!protocol.equals(other.protocol))
            return false;
        if (username == null) {
            if (other.username != null)
                return false;
        } else if (!username.equals(other.username))
            return false;
        if (vendor == null) {
            if (other.vendor != null)
                return false;
        } else if (!vendor.equals(other.vendor))
            return false;
        return true;
    }

    @Override
    public String toString() {
        return "SMISProvider [ip=" + ip + ", port=" + port + ", nameSpace=" + nameSpace + ", username=" + username + ", password=" + password + ", protocol=" + protocol
                + ", vendor=" + vendor + "]";
    }

}
