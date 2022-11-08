package com.mainsteam.stm.plugin.snmp;

import java.util.Map;

/**
 * Created by Xiaopf on 2017/5/3.
 */
public class SnmpGetRequestFactory implements SnmpRequestFactory {

    @Override
    public SnmpRequest getSnmpRequest(Map<String, String> config, ExtSnmpPluginSession session) {
        SnmpGetRequestImpl snmpGetRequest = new SnmpGetRequestImpl(config);
        snmpGetRequest.setMetricBufferCallback(session);
        return snmpGetRequest;
    }
}
