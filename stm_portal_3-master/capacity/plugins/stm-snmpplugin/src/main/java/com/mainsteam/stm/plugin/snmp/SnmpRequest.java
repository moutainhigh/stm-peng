package com.mainsteam.stm.plugin.snmp;

import java.util.List;
import java.util.Map;

/**
 * Created by Xiaopf on 2017/5/3.
 */
public interface SnmpRequest {
    /**
     * 获取snmp数据
     * @param parameters
     * @param OIDs
     */
    public abstract List<List<String>> sendMessage(Map<String, String> parameters, List<String> OIDs);

    /**
     * 处理返回数据
     * @param T
     * @param <T>
     * @return
     */
    public abstract <T> List<List<String>> handleMessage(T... T);

    /**
     * snmp实例清理
     */
    public abstract void close();

    /**
     * 缓存指标回调接口注册
     * @param callback
     */
    public abstract void setMetricBufferCallback(MetricBufferCallback callback);

}
