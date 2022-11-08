package com.mainsteam.stm.webService.dcim;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;

@XmlAccessorType(XmlAccessType.FIELD)
@XmlRootElement(name = "Dcim",namespace="http://www.mainsteam.com/Dcim")
public class Dcim {

    @XmlElement(name = "arg0", required = true)
    private String testID;
    @XmlElement(name = "arg1", required = true)
    private String testName;
    @XmlElement(name = "arg2", required = true)
    private String testStatus;


    public String getTestID() {
        return testID;
    }

    public void setTestID(String testID) {
        this.testID = testID;
    }

    public String getTestName() {
        return testName;
    }

    public void setTestName(String testName) {
        this.testName = testName;
    }

    public String getTestStatus() {
        return testStatus;
    }

    public void setTestStatus(String testStatus) {
        this.testStatus = testStatus;
    }
}
