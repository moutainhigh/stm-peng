package com.mainsteam.stm.state.ext.process.strategy;

import com.mainsteam.stm.caplib.dict.MetricTypeEnum;
import com.mainsteam.stm.common.metric.MetricDataService;
import com.mainsteam.stm.common.metric.obj.MetricData;
import com.mainsteam.stm.dsl.expression.MapContext;
import com.mainsteam.stm.dsl.expression.OCELContext;
import com.mainsteam.stm.dsl.expression.OCELExpression;
import com.mainsteam.stm.metric.obj.CustomMetricThreshold;
import com.mainsteam.stm.state.ext.process.bean.HasChangedResult;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.concurrent.ConcurrentHashMap;

/**
 * 用于特定阈值表达式计算,例如Has Changed
 */
public abstract class AbstractSpecifiedMetricCompute {

    private static final Log logger = LogFactory.getLog(AbstractSpecifiedMetricCompute.class);

    @Autowired
    private MetricDataService metricDataService;
    //缓存指标历史值
    private ConcurrentHashMap<String, String> historyMetricData = new ConcurrentHashMap<>();

    public Object computeHasChanged(OCELExpression ocelExpression, String metricId, String metricData, long instanceId, MetricTypeEnum metricType,
                                    boolean isCustom) throws Exception{
        try{

            String key = instanceId + metricId;
            OCELContext pre = new MapContext();
            OCELContext cur = new MapContext();
            String realMetricId = isCustom ? CustomMetricThreshold.PLACEHOLDER : metricId;
            cur.put(realMetricId, metricData);
            String preMetricData = historyMetricData.get(key);
            if(preMetricData !=null){
                pre.put(realMetricId, preMetricData);
            }else{
                MetricData histMetricData;
                if(metricType == MetricTypeEnum.PerformanceMetric){
                    histMetricData = metricDataService.getMetricPerformanceData(instanceId, metricId);
                }else{
                    histMetricData = metricDataService.getMetricInfoData(instanceId, metricId);
                }
                if(histMetricData !=null && histMetricData.getData() !=null) {
                    preMetricData = histMetricData.getData()[0];
                    pre.put(realMetricId, preMetricData);
                }else {
                    if(logger.isDebugEnabled()){
                        logger.debug("can't query info metric data from db("+instanceId+"/"+metricId+"), so don't compute metric state");
                    }
                    historyMetricData.put(key, metricData);
                    return null;
                }
            }
            Object evaluate = ocelExpression.evaluate(pre, cur);
            historyMetricData.put(key, metricData);
            if(evaluate !=null && evaluate instanceof Boolean){
                Boolean b = (Boolean) evaluate;
                return new HasChangedResult(b, preMetricData);
            }
            return null;
        }catch (Exception e){
            throw e;
        }
    }

}
