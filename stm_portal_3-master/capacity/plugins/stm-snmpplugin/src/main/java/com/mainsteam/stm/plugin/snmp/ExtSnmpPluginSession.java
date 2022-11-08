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

import java.util.*;
import java.util.concurrent.ConcurrentHashMap;

import static com.mainsteam.stm.plugin.snmp.SnmpParamConst.*;

/**
 * Created by Xiaopf on 2017/4/28.
 */
public class ExtSnmpPluginSession implements PluginSession, MetricBufferCallback {

    private static final Log logger = LogFactory.getLog(ExtSnmpPluginSession.class);

    private static final String SNMP_UNIT_KEY = "SNMPUnit";
    private final ConcurrentHashMap<String, List<String>> metricMap = new ConcurrentHashMap<String, List<String>>(20);
    private final Map<String, String> snmpParameters = new HashMap<String, String>(12);

    private SnmpRequest snmpWalkRequest;
    private SnmpRequest snmpGetRequest;
    private SnmpRequest snmpSetRequest;

    @Override
    public void init(PluginInitParameter pluginInitParameter) throws PluginSessionRunException {
        Parameter[] initParameters = pluginInitParameter.getParameters();
        for (Parameter parameter : initParameters) {
            snmpParameters.put(parameter.getKey(), parameter.getValue());
        }
        snmpParameters.put(PDU_TYPE, String.valueOf(getSnmpMethodCode(SnmpParamConst.GET_BULK)));
        snmpWalkRequest = new SnmpWalkRequestFactory().getSnmpRequest(snmpParameters, this);
        snmpGetRequest = new SnmpGetRequestFactory().getSnmpRequest(snmpParameters, this);
    }

    @Override
    public PluginResultSet execute(PluginExecutorParameter<?> pluginExecutorParameter, PluginSessionContext pluginSessionContext)
            throws PluginSessionRunException {

        PluginResultSet result = new PluginResultSet();
        if (pluginExecutorParameter instanceof PluginArrayExecutorParameter) {
            String metricId = pluginSessionContext.getMetricId();
            List<String> oidList = null;
            String snmpMethod = null;
            synchronized (metricMap) {
                if(metricMap.containsKey(metricId)) {
                    oidList = metricMap.get(metricId);
                }
            }
            try {
                PluginArrayExecutorParameter arrayP = (PluginArrayExecutorParameter) pluginExecutorParameter;
                Parameter[] parameters = arrayP.getParameters();
                if(null == oidList) {
                    oidList = new ArrayList<>(parameters.length-1);
                    List<String> counter32 = null;
                    List<String> variableList = null;
                    List<String> typeList = null;
                    List<String> prefixList = null;
                    List<String> suffixList = null;

                    for (Parameter parameter : parameters) {
                        String key = parameter.getKey();
                        switch (key) {
                            case METHOD:
                                snmpMethod = StringUtils.trim(parameter.getValue());
                                break;
                            case VARIABLE:
                                if(null == variableList)
                                    variableList = new ArrayList<>(parameters.length/5);
                                variableList.add(parameter.getValue());
                                break;
                            case TYPE:
                                if(null == typeList)
                                    typeList = new ArrayList<>(parameters.length/5);
                                typeList.add(parameter.getValue());
                                break;
                            case PREFIX:
                                if(null == prefixList)
                                    prefixList = new ArrayList<>(parameters.length/5);
                                prefixList.add(parameter.getValue());
                                break;
                            case SUFFIX:
                                if(null == suffixList)
                                    suffixList = new ArrayList<>(parameters.length/5);
                                suffixList.add(parameter.getValue());
                                break;
                            default:
                                if(StringUtils.isBlank(key) || StringUtils.equals(key, COUNTER32) ||
                                        StringUtils.equals(key, COUNTER64)) {

                                    if((Integer.valueOf(snmpParameters.get(SNMPPLUGIN_SNMP_VERSION)) == SNMP_VERSION_1) &&
                                            StringUtils.equals(key, COUNTER64)) //SNMP V1 不支持64 bit
                                        continue;
                                    if(StringUtils.equals(key, COUNTER32)) {
                                        if(null == counter32)
                                            counter32 = new ArrayList<String>(parameters.length/2);
                                        counter32.add(parameter.getValue());
                                        continue;
                                    }
                                    oidList.add(parameter.getValue());
                                }
                        }

                    }
                    if(null != counter32){
                        oidList.addAll(counter32);
                        counter32 = null;
                    }

                    if(StringUtils.equals(snmpMethod, SET)) {
                        for (int i = 0; i < prefixList.size(); ++i) {
                            oidList.add(prefixList.get(i) + "." + suffixList.get(i));
                        }
                        oidList.addAll(variableList);
                        oidList.addAll(typeList);
                        variableList = null;
                        typeList = null;
                        prefixList = null;
                        suffixList = null;
                    }
                }else {
                    boolean isExist = false;
                    for (Parameter parameter : parameters) {
                        if(isExist)
                            break;
                        String key = parameter.getKey();
                        switch (key) {
                            case METHOD:
                                isExist = true;
                                snmpMethod = StringUtils.trim(parameter.getValue());
                                break;
                        }
                    }
                }

                parameters = null;
                int pduType = getSnmpMethodCode(snmpMethod);
                Map<String, String> keyMap = new HashMap<>(2);
                keyMap.put(METRIC_ID, metricId);
                List<List<String>> resultList = serviceSnmpRequest(oidList, pduType, keyMap);
                if(null != resultList) {
                    for(List<String> row : resultList) {
                        String[] rowArray = new String[row.size()];
                        row.toArray(rowArray);
                        result.addRow(rowArray);
                    }
                }
            } catch (Exception e) {
                if(logger.isErrorEnabled()) {
                    logger.error(e.getMessage(), e);
                }
            }
        }
        result.setExtraValue(SNMP_UNIT_KEY, snmpParameters.get(SNMPPLUGIN_SNMP_VERSION));
        return result;
    }

    private List<List<String>> serviceSnmpRequest(List<String> oids, int pduType, Map<String, String> parameters) {
        switch (pduType){
            case PDU.GETBULK:
            case PDU.GETNEXT:
                return snmpWalkRequest.sendMessage(parameters, oids);
            case PDU.GET:
                return snmpGetRequest.sendMessage(parameters, oids);
            case PDU.SET:
                if(null == snmpSetRequest)
                    snmpSetRequest = new SnmpSetRequestFactory().getSnmpRequest(snmpParameters, this);
                return snmpSetRequest.sendMessage(null, oids);
        }
        return null;
    }

    @Override
    public void bufferMetrics(String key, List<String> OIDs) {
        if(logger.isInfoEnabled()) {
            logger.info("Starts to cache metric info, " + key + ":" + OIDs);
        }
        metricMap.putIfAbsent(key, OIDs);
    }

    @Override
    public boolean check(PluginInitParameter pluginInitParameter) throws PluginSessionRunException {
        return false;
    }

    @Override
    public void destory() {
        if(null != snmpWalkRequest) {
            snmpWalkRequest.close();
            snmpWalkRequest = null;
        }
        if(null != snmpGetRequest) {
            snmpGetRequest.close();
            snmpGetRequest = null;
        }
        if(null != snmpSetRequest) {
            snmpSetRequest.close();
            snmpSetRequest = null;
        }
        metricMap.clear();
        snmpParameters.clear();
    }

    @Override
    public void reload() {

    }

    @Override
    public boolean isAlive() {
        return true;
    }

    private int getSnmpMethodCode(String method) {
        int pduMethodCode = PDU.GETBULK;
        switch (method) {
            case GET_NEXT:
            case WALK:
            case GET_BULK:
            case GET_TABLE:
            case GET_SUBTREE:
                if(Integer.valueOf(snmpParameters.get(SNMPPLUGIN_SNMP_VERSION)) == SNMP_VERSION_1)
                    pduMethodCode = PDU.GETNEXT;
                break;
            case GET:
                pduMethodCode = PDU.GET;
                break;
            case SET:
                pduMethodCode = PDU.SET;
                break;
        }
        return pduMethodCode;
    }

}
