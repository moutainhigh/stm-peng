package com.mainsteam.stm.webService.performance;

import javax.xml.bind.annotation.*;
import java.io.Serializable;
import java.util.List;

/**
 * Created by Xiaopf on 2017/4/5.
 */
@XmlAccessorType(XmlAccessType.NONE)
@XmlRootElement(name = "performances")
public class PerformanceDataBean implements Serializable {
    private static final long serialVersionUID = 1L;
    @XmlElement(name = "errorCode", required = true)
    private String errorCode; //错误代码，200表示正常
    @XmlElement(name = "errorMsg", required = true)
    private String errorMsg; //错误信息
    @XmlElementWrapper(name = "datas")
    @XmlElement(name = "data", required = true)
    private List<PerformanceInfo> data; //数据对象

    public PerformanceDataBean() {
        super();
    }

    public PerformanceDataBean(String errorCode, String errorMsg) {
        super();
        this.errorCode = errorCode;
        this.errorMsg = errorMsg;
    }

    public String getErrorCode() {
        return errorCode;
    }

    public void setErrorCode(String errorCode) {
        this.errorCode = errorCode;
    }

    public String getErrorMsg() {
        return errorMsg;
    }

    public void setErrorMsg(String errorMsg) {
        this.errorMsg = errorMsg;
    }

    public List<PerformanceInfo> getData() {
        return data;
    }

    public void setData(List<PerformanceInfo> data) {
        this.data = data;
    }

    @Override
    public String toString() {
        return "PerformanceDataBean{" +
                "errorCode='" + errorCode + '\'' +
                ", errorMsg='" + errorMsg + '\'' +
                ", data=" + data +
                '}';
    }

}

