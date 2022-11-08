package com.mainsteam.stm.plugin.snmp;

import java.util.Map;

/**
 * Created by Xiaopf on 2017/5/3.
 */
public class SnmpSetRequestFactory implements SnmpRequestFactory {

    @Override
    public SnmpRequest getSnmpRequest(Map<String, String> config, ExtSnmpPluginSession session) {
        return new SnmpSetRequestImpl(config);
    }
}
