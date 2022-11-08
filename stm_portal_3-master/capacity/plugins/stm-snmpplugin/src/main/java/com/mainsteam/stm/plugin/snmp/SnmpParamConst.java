package com.mainsteam.stm.plugin.snmp;

/**
 * Created by Xiaopf on 2017/4/28.
 */
public class SnmpParamConst {

    static final String SNMPPLUGIN_SNMP_TIMEOUT = "snmpTimeout";
    static final String SNMPPLUGIN_SNMP_RETRY = "snmpRetry";
    static final String SNMPPLUGIN_SNMP_VERSION = "snmpVersion";
    static final String SNMPPLUGIN_COMMUNITY = "community";
    static final String SNMPPLUGIN_PORT = "snmpPort";
    static final String SNMPPLUGIN_IP = "IP";
    static final String SNMPPLUGIN_SNMP_SECURITYNAME = "securityName";
    static final String SNMPPLUGIN_SNMP_SECURITYLEVEL = "securityLevel";
    static final String SNMPPLUGIN_SNMP_AUTHPROTOCOL = "authProtocol";
    static final String SNMPPLUGIN_SNMP_AUTHPASSPHRASE = "authPassphrase";
    static final String SNMPPLUGIN_SNMP_PRIVACYPROTOCOL = "privacyProtocol";
    static final String SNMPPLUGIN_SNMP_PRIVACYPASSPHRASE = "privacyPassphrase";

    static final String AES256 = "aes256";
    static final String AES192 = "aes192";
    static final String AES128 = "aes128";
    static final String _3DES = "3des";
    static final String DES = "des";
    static final String SHA = "sha";
    static final String MD5 = "md5";

    static final String PDU_TYPE = "pduType";

    static final String METHOD = "method";
    static final String WALK = "walk";
    static final String GET_NEXT = "getNext";
    static final String GET_BULK = "getBulk";
    static final String GET_TABLE = "getTable";
    static final String GET_SUBTREE = "getSubtree";
    static final String SET = "set";
    static final String GET = "get";

    static final String VARIABLE = "Variable";
    static final String TYPE = "Type";
    static final String PREFIX = "Prefix";
    static final String SUFFIX = "Suffix";

    static final int SNMP_VERSION_1 = 0; // snmp version 1

    static final String COUNTER64 = "COUNTER64";
    static final String COUNTER32 = "COUNTER32";

    static final String METRIC_ID = "metricId";

}
