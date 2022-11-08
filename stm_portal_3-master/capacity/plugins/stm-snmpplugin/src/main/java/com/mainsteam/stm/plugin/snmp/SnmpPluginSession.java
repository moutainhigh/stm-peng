package com.mainsteam.stm.plugin.snmp;

import com.mainsteam.stm.pluginsession.PluginResultSet;
import com.mainsteam.stm.pluginsession.PluginSession;
import com.mainsteam.stm.pluginsession.PluginSessionContext;
import com.mainsteam.stm.pluginsession.exception.PluginSessionRunException;
import com.mainsteam.stm.pluginsession.parameter.Parameter;
import com.mainsteam.stm.pluginsession.parameter.PluginArrayExecutorParameter;
import com.mainsteam.stm.pluginsession.parameter.PluginExecutorParameter;
import com.mainsteam.stm.pluginsession.parameter.PluginInitParameter;
import org.apache.commons.lang.StringUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.snmp4j.PDU;
import org.snmp4j.mp.SnmpConstants;

import java.io.IOException;
import java.util.*;

/**
 * @author xiaop_000
 * This class has been deprecated,use ExtSnmpPluginSession instead
 */
@Deprecated
public class SnmpPluginSession implements PluginSession {

    public static final String SNMPPLUGIN_SNMP_TIMEOUT = "snmpTimeout";
    public static final String SNMPPLUGIN_SNMP_RETRY = "snmpRetry";
    public static final String SNMPPLUGIN_SNMP_VERSION = "snmpVersion";
    public static final String SNMPPLUGIN_COMMUNITY = "community";
    public static final String SNMPPLUGIN_PORT = "snmpPort";
    public static final String SNMPPLUGIN_IP = "IP";
    public static final String SNMPPLUGIN_SNMP_SECURITYNAME = "securityName";
    public static final String SNMPPLUGIN_SNMP_SECURITYLEVEL = "securityLevel";
    public static final String SNMPPLUGIN_SNMP_AUTHPROTOCOL = "authProtocol";
    public static final String SNMPPLUGIN_SNMP_AUTHPASSPHRASE = "authPassphrase";
    public static final String SNMPPLUGIN_SNMP_PRIVACYPROTOCOL = "privacyProtocol";
    public static final String SNMPPLUGIN_SNMP_PRIVACYPASSPHRASE = "privacyPassphrase";
    private static final String SNMP_UNIT_KEY = "SNMPUnit";
    private static final String COUNTER64 = "COUNTER64";
    private static final String COUNTER32 = "COUNTER32";
    private static final String METHOD = "method";
    private static final String WALK = "walk";
    private static final String GET_NEXT = "getNext";
    private static final String GET_BULK = "getBulk";
    private static final String GET_TABLE = "getTable";
    private static final String GET_SUBTREE = "getSubtree";
    private static final String SET = "set";
    private static final Log logger = LogFactory.getLog(SnmpPluginSession.class);
    private static final int SNMP_VERSION_1 = 0; // snmp version 1
    private static final int SNMP_VERSION_2C = 1;
    private static final String[] COUNTERS = {"1.3.6.1.2.1.31.1.1.1.6", "1.3.6.1.2.1.31.1.1.1.10"};
    private final SnmpAgent agent = new SnmpAgent();
    private SnmpParameter snmpParameter;
    private int timeout = 30000;//超时时间
    private int retry = 3;//重试次数
    private int changeSNMPVersion = -1;

    public SnmpPluginSession() {
    }

    @Override
    public PluginResultSet execute(
            PluginExecutorParameter<?> executorParameter,
            PluginSessionContext arg1) throws PluginSessionRunException {

        PluginResultSet result = new PluginResultSet();
        if (executorParameter instanceof PluginArrayExecutorParameter) {
            try {
                PluginArrayExecutorParameter arrayP = (PluginArrayExecutorParameter) executorParameter;
                Parameter[] parameters = arrayP.getParameters();
                List<String> oidList = new ArrayList<String>(parameters.length);
                List<String> variableList = new ArrayList<String>(parameters.length);
                List<String> typeList = new ArrayList<String>(parameters.length);
                List<String> prefixList = new ArrayList<String>(parameters.length);
                List<String> suffixList = new ArrayList<String>(parameters.length);

                String snmpMethod = null;
                /**
                 * snmp v2默认使用counter64的oid, snmp v1默认使用counter32的oid;目前需要做的是当采用snmp v2时，有些设备采用counter64的oid无法采集到值，
                 * 这时需要能自动使用counter32的oid继续采集值。需要在session在保存当前使用的snmp版本，主要目的在于选择snmp的oid。
                 */
                for (Parameter parameter : parameters) {
                    if (parameter.getKey().equals(METHOD)) {
                        snmpMethod = parameter.getValue();
                    } else if (parameter.getKey().equals("Variable")) {
                        variableList.add(parameter.getValue());
                    } else if (parameter.getKey().equals("Type")) {
                        typeList.add(parameter.getValue());
                    } else if (parameter.getKey().equals("Prefix")) {
                        prefixList.add(parameter.getValue());
                    } else if (parameter.getKey().equals("Suffix")) {
                        suffixList.add(parameter.getValue());
                    } else {
                        String oid = parameter.getValue();

                        if (StringUtils.equals(parameter.getKey(), COUNTER32) && (changeSNMPVersion == SNMP_VERSION_1)) {
                            oidList.add(oid);
                        } else if (StringUtils.equals(parameter.getKey(), COUNTER64) && changeSNMPVersion == SNMP_VERSION_2C) {
                            oidList.add(oid);
                        } else if (StringUtils.isBlank(parameter.getKey())) {
                            oidList.add(oid);
                        }
                    }
                }

                for (int i = 0; i < prefixList.size(); ++i) {
                    oidList.add(prefixList.get(i) + "." + suffixList.get(i));
                }

                if (logger.isDebugEnabled()) {
                    logger.debug(Arrays.toString(oidList.toArray()));
                    logger.debug(Arrays.toString(typeList.toArray()));
                    logger.debug(Arrays.toString(variableList.toArray()));
                    logger.debug(Arrays.toString(prefixList.toArray()));
                    logger.debug(Arrays.toString(suffixList.toArray()));
                }
                logger.info("malachi **** oidList " + Arrays.toString(oidList.toArray()));
                logger.info("malachi **** typeList " + Arrays.toString(typeList.toArray()));
                logger.info("malachi **** variableList " + Arrays.toString(variableList.toArray()));
                logger.info("malachi **** prefixList " + Arrays.toString(prefixList.toArray()));
                logger.info("malachi **** suffixList " + Arrays.toString(suffixList.toArray()));

                snmpParameter.setOids(oidList.toArray(new String[oidList.size()]));
                snmpParameter.setTypes(typeList.toArray(new String[typeList.size()]));
                snmpParameter.setVariables(variableList.toArray(new String[variableList.size()]));

                this.snmpParameter.setPduCode(getSnmpMethodCode(snmpMethod));
                long currentMill = System.currentTimeMillis();
                Object resultObj = agent.getSnmpResult(this.snmpParameter);
                long afterMill = System.currentTimeMillis();
                if (logger.isDebugEnabled()) {
                    logger.debug("Snmp operation costs " + (afterMill - currentMill) + "ms. Reference parameter is "
                            + this.snmpParameter);
                }
                if (snmpParameter.getPduCode() == PDU.SET) {
                    Map<String, List<String>> resultMap = (Map<String, List<String>>) resultObj;
                    int row = 0;
                    for (String oid : oidList) {
                        if (resultMap.get(oid) != null) {
                            result.putValue(row, 0, resultMap.get(oid).get(0));
                        }
                        row++;
                    }
                } else {
                    createResultSet(resultObj, getSnmpMethodCode(snmpMethod), oidList, result);
                }


            } catch (Exception e) {
                if (logger.isErrorEnabled()) {
                    logger.error("snmp execute error. address:" + this.snmpParameter.ip + "/" + this.snmpParameter.port
                            + ". " + e.getMessage(), e);
                }
            }
        }

        if (logger.isDebugEnabled()) {
            logger.debug("Snmp session execute end.ResultSet size is " + result.getRowLength() + ".ResultSet is " + result + "\r\nReference parameters is [" +
                    this.snmpParameter + "]");
        }

        return result;
    }

    @Override
    public void init(PluginInitParameter initP) {
        Parameter[] initParameters = initP.getParameters();
        snmpParameter = new SnmpParameter();

        for (int i = 0; i < initParameters.length; i++) {
            switch (initParameters[i].getKey()) {
                case SNMPPLUGIN_IP:
                    this.snmpParameter.setIp(initParameters[i].getValue());
                    break;
                case SNMPPLUGIN_PORT:
                    try {
                        this.snmpParameter.setPort(Integer.parseInt(initParameters[i].getValue()));
                    } catch (NumberFormatException e2) {
                        logger.error(e2.getMessage(), e2);
                        this.snmpParameter.setPort(161);
                    }
                    break;
                case SNMPPLUGIN_COMMUNITY:
                    this.snmpParameter.setCommunity(initParameters[i].getValue());
                    break;
                case SNMPPLUGIN_SNMP_VERSION:
                    try {
                        this.snmpParameter.setVersion(Integer.parseInt(initParameters[i].getValue()));
                    } catch (Exception e1) {
                        logger.error(e1.getMessage(), e1);
                        this.snmpParameter.setVersion(SnmpConstants.version2c);
                    }
                    break;

                case SNMPPLUGIN_SNMP_SECURITYNAME:
                    this.snmpParameter.setSecurityName(initParameters[i].getValue());
                    break;
                case SNMPPLUGIN_SNMP_SECURITYLEVEL:
                    try {
                        this.snmpParameter.setSecurityLevel(Integer.parseInt(initParameters[i].getValue()));
                    } catch (NumberFormatException e) {
                        logger.error(e.getMessage(), e);
                    }
                    break;
                case SNMPPLUGIN_SNMP_AUTHPROTOCOL:
                    this.snmpParameter.setAuthProtocol(initParameters[i].getValue());
                    break;
                case SNMPPLUGIN_SNMP_AUTHPASSPHRASE:
                    this.snmpParameter.setAuthPassphrase(initParameters[i].getValue());
                    break;
                case SNMPPLUGIN_SNMP_PRIVACYPROTOCOL:
                    this.snmpParameter.setPrivacyProtocol(initParameters[i].getValue());
                    break;
                case SNMPPLUGIN_SNMP_PRIVACYPASSPHRASE:
                    this.snmpParameter.setPrivacyPassphrase(initParameters[i].getValue());
                    break;
                case SNMPPLUGIN_SNMP_TIMEOUT:
                    try {
                        this.snmpParameter.setSnmpTimeout(Integer.parseInt(initParameters[i].getValue()));
                    } catch (NumberFormatException e) {
                        logger.error(e.getMessage(), e);
                        this.snmpParameter.setSnmpTimeout(this.timeout);
                    }
                    break;
                case SNMPPLUGIN_SNMP_RETRY:
                    try {
                        this.snmpParameter.setSnmpRetry(Integer.parseInt(initParameters[i].getValue()));
                    } catch (NumberFormatException e) {
                        logger.error(e.getMessage(), e);
                        this.snmpParameter.setSnmpRetry(this.retry);
                    }
                    break;
                default:
                    if (logger.isWarnEnabled()) {
                        logger.warn("warn:unkown initparameter "
                                + initParameters[i].getKey() + "="
                                + initParameters[i].getValue());
                    }
                    break;
            }
        }

        /**
         * 需要尝试几个counter64的oid，然后才能确定使用snmp v2c以上的版本
         */
        if (this.snmpParameter.getVersion() > SNMP_VERSION_1) {
//			SnmpAgent agent = SnmpAgent.getInstance();
            this.snmpParameter.setOids(COUNTERS);
            this.snmpParameter.setPduCode(PDU.GETBULK);
            try {
                Object resultObj = agent.getSnmpResult(this.snmpParameter);
                @SuppressWarnings("unchecked")
                Map<String, List<String>> oidResultValueMaps = (Map<String, List<String>>) resultObj;
                boolean flag = false;
                for (String oid : COUNTERS) {
                    if (oidResultValueMaps.get(oid) == null || oidResultValueMaps.get(oid).isEmpty()) {
                        flag = true;
                        break;
                    }
                }
                if (flag) {
                    this.changeSNMPVersion = SNMP_VERSION_1;
                } else
                    this.changeSNMPVersion = SNMP_VERSION_2C;
            } catch (Exception e) {
                if (logger.isWarnEnabled()) {
                    logger.warn(e.getMessage(), e);
                }
            }

        } else {
            this.changeSNMPVersion = SNMP_VERSION_1;
        }

    }

    @Override
    public void destory() {
        try {
            agent.close();
        } catch (IOException e) {
            logger.error(e);
        }
    }

    @Override
    public void reload() {
        // DO noting
    }

    @Override
    public boolean isAlive() {
        return true;
    }

    private int getSnmpMethodCode(String method) {
        int pduMethodCode = PDU.GET;
        if (method != null) {
            switch (method) {
                case GET_NEXT:
                    pduMethodCode = PDU.GETNEXT;
                    break;
                case WALK:
                    pduMethodCode = SnmpAgent.PDU_CODE_WALK;
                    break;
                case GET_BULK:
                    if (this.snmpParameter.getVersion() != SNMP_VERSION_1)
                        pduMethodCode = PDU.GETBULK;
                    else
                        pduMethodCode = SnmpAgent.PDU_CODE_WALK;
                    break;
                case GET_TABLE:
                    pduMethodCode = SnmpAgent.GETTABLE;
                    break;
                case GET_SUBTREE:
                    pduMethodCode = SnmpAgent.GETSUBTREE;
                    break;
                case SET:
                    pduMethodCode = PDU.SET;
                default:
                    break;
            }
        }
        return pduMethodCode;
    }

    @SuppressWarnings("unchecked")
    private void createResultSet(Object resultObj, int snmpMethod, List<String> oidsList, PluginResultSet result) {
        if (snmpMethod == SnmpAgent.GETTABLE) {
            List<List<String>> datas = (List<List<String>>) resultObj;
            for (List<String> row : datas) {
                String[] rows = new String[row.size()];
                row.toArray(rows);
                result.addRow(rows);
            }
        } else {
            int totalRows = 1; // 主要是用于处理在oid walk不到值得时候设置为空进行计算
            Map<String, List<String>> oidResultValueMaps = (Map<String, List<String>>) resultObj;
            if (snmpMethod != PDU.GET) {
                List<String> indexValues = oidResultValueMaps.get(SnmpAgent.INDEX_COLUMN_NAME);
                if (indexValues != null) {
                    for (int i = 0; i < indexValues.size(); i++) {
                        result.putValue(i, 0, indexValues.get(i));
                    }
                }
            }
            totalRows = result.getRowLength();
            //数据格式需要按照传入的OID顺序进行返回
            Map<String, Integer> hashMap = new HashMap<String, Integer>();
            int column = result.getColumnLength();
            for (String oid : oidsList) {
                List<String> list = (List<String>) oidResultValueMaps.get(oid);
                if (list != null) {
                    int row = 0;
                    for (String str : list) {
                        result.putValue(row, column, str);
                        row++;
                        if (column == 1) {
                            Integer value = hashMap.get(str);
                            if (value == null)
                                hashMap.put(str, 1);
                            else
                                hashMap.put(str, value + 1);
                        }
                    }
                } else { //如果根据oid没有查找到值的话，一般情况下都是oid有问题，默认设置为null
                    if (logger.isWarnEnabled()) {
                        logger.warn("snmp can't walk any values. The oid is [" + oid + "]. IP/port:" +
                                this.snmpParameter.getIp() + "/" + this.snmpParameter.getPort());
                    }
                    for (int i = 0; i < totalRows; i++) {
                        result.putValue(i, column, null);
                    }

                }
                column++;
            }
        }
        result.setExtraValue(SNMP_UNIT_KEY, changeSNMPVersion);

    }

    @Override
    public boolean check(PluginInitParameter initParameters)
            throws PluginSessionRunException {
        return false;
    }
}
