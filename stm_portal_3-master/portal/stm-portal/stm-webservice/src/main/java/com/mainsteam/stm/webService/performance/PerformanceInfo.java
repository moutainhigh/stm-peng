package com.mainsteam.stm.webService.performance;

import javax.xml.bind.annotation.*;
import java.io.Serializable;

@XmlType(propOrder = {"ip", "name", "value"})
@XmlAccessorType(XmlAccessType.NONE)
@XmlRootElement(name = "performance")
public class PerformanceInfo implements Serializable {
    private static final long serialVersionUID = 1L;
    @XmlAttribute(name = "name")
    private String name;
    @XmlAttribute(name = "ip")
    private String ip;
    @XmlAttribute(name = "value")
    private String value;

    public PerformanceInfo() {
        super();
    }

    public PerformanceInfo(String name, String ip, String value) {
        this.name = name;
        this.ip = ip;
        this.value = value;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getIp() {
        return ip;
    }

    public void setIp(String ip) {
        this.ip = ip;
    }

    public String getValue() {
        return value;
    }

    public void setValue(String value) {
        this.value = value;
    }
}
