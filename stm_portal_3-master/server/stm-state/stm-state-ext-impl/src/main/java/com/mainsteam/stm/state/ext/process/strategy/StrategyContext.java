package com.mainsteam.stm.state.ext.process.strategy;

/**
 * Created by Xiaopf on 2017/7/12.
 * 指标计算策略管理类
 */
@Deprecated
public class StrategyContext {

    final MetricCompute metricComputeAlgorithm;

    public StrategyContext(MetricCompute algorithm) {
        this.metricComputeAlgorithm = algorithm;
    }

    public <T> T  dispather(T... T) throws Exception {
        return (T) metricComputeAlgorithm.compute(T);
    }
}
