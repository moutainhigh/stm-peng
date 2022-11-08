package com.mainsteam.stm.state.ext.process.strategy;

/**
 * Created by Xiaopf on 2017/7/12.
 * 指标计算状态接口
 */
public interface MetricCompute {

    <T> Object compute(T... args) throws Exception;

}
