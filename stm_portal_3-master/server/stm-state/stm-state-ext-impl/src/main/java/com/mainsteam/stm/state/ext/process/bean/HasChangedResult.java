package com.mainsteam.stm.state.ext.process.bean;

/**
 * has changed表达式计算结果返回值
 */
public class HasChangedResult {

    private Boolean result;

    private String preMetricData;

    public HasChangedResult(Boolean result, String preMetricData) {
        this.result = result;
        this.preMetricData = preMetricData;
    }

    public Boolean getResult() {
        return result;
    }

    public String getPreMetricData() {
        return preMetricData;
    }
}
