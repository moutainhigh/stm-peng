package com.mainsteam.stm.plugin.http.collector;

import java.util.Map;

/**
 * @author lich
 */
public interface HttpCollector {
    void init(Map<String, String> initMap) throws Exception;

    void close();
}
