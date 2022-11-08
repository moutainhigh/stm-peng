package com.mainsteam.stm.caplib.resource;

import com.mainsteam.stm.caplib.dict.OperatorEnum;
import com.mainsteam.stm.caplib.dict.PerfMetricStateEnum;
import com.mainsteam.stm.common.metric.obj.MetricStateEnum;

public class ThresholdDef implements java.io.Serializable {

    /**
     *
     */
    private static final long serialVersionUID = 4565347214696827483L;
    @Deprecated
    private OperatorEnum operator;
    @Deprecated
    private String defaultvalue;
    @Deprecated
    private PerfMetricStateEnum state;

    private String expression;

    public ThresholdDef() {

    }

    /**
     * 获取操作符
     *
     * @return
     */
    @Deprecated
    public OperatorEnum getOperator() {
        return operator;
    }

    @Deprecated
    public void setOperator(OperatorEnum operator) {
        this.operator = operator;
    }

    /**
     * 获取默认值
     *
     * @return
     */
    @Deprecated
    public String getDefaultvalue() {
        return defaultvalue;
    }

    @Deprecated
    public void setDefaultvalue(String defaultvalue) {
        this.defaultvalue = defaultvalue;
    }

    /**
     * 获取状态
     *
     * @return
     */
    @Deprecated
    public PerfMetricStateEnum getState() {
        return state;
    }

    @Deprecated
    public void setState(PerfMetricStateEnum state) {
        this.state = state;
    }

    public String getThresholdExpression() {
        return expression;
    }

    public void setThresholdExpression(String expression) {
        this.expression = expression;
    }
}
