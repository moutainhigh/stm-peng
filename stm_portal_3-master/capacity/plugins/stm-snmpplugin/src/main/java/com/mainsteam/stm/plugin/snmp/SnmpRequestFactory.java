package com.mainsteam.stm.plugin.snmp;

import java.util.Map;

/**
 * Created by Xiaopf on 2017/5/3.
 */
public interface SnmpRequestFactory {

    SnmpRequest getSnmpRequest(Map<String, String> config, ExtSnmpPluginSession session);

}
