package com.mainsteam.stm.plugin.snmp;

import java.util.List;

/**
 * Created by Xiaopf on 2017/5/4.
 */
public interface MetricBufferCallback {
    /**
     * 需要将一些特别的指标的OID缓存起来，避免将来解析以及多少尝试调用snmp接口，提升性能
     * @param key
     * @param OIDs
     */
    public void bufferMetrics(String key, List<String> OIDs);

}
