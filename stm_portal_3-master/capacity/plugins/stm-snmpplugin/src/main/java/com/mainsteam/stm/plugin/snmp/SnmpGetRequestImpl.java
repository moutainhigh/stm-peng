package com.mainsteam.stm.plugin.snmp;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.snmp4j.PDU;
import org.snmp4j.ScopedPDU;
import org.snmp4j.event.ResponseEvent;
import org.snmp4j.mp.SnmpConstants;
import org.snmp4j.smi.Null;
import org.snmp4j.smi.OID;
import org.snmp4j.smi.Variable;
import org.snmp4j.smi.VariableBinding;
import org.snmp4j.util.SimpleVariableTextFormat;
import org.snmp4j.util.VariableTextFormat;

import java.io.IOException;
import java.util.*;

import static com.mainsteam.stm.plugin.snmp.SnmpParamConst.METRIC_ID;

/**
 * Created by Xiaopf on 2017/5/2.
 */
public class SnmpGetRequestImpl extends AbstractSnmpRequest {

    private static final Log logger = LogFactory.getLog(SnmpGetRequestImpl.class);
    private static final String OID_SEPARATOR = ":";

    private MetricBufferCallback metricBufferCallback;

    public SnmpGetRequestImpl(Map<String, String> config) {
        super(config);
    }

    /**
     * GET方式不支持COUNTER32和COUNTER64混用
     * @param parameters
     * @param OIDs
     * @return
     */
    @Override
    public List<List<String>> sendMessage(Map<String,String> parameters, List<String> OIDs) {

        PDU pdu = (target.getVersion() == SnmpConstants.version3) ? (new ScopedPDU()) : (new PDU());
        pdu.setType(PDU.GET);

        for(String oid : OIDs) {
            OID OID = null;
            String[] subOIDs = null;
            try{
                OID = new OID(oid);
            }catch (NumberFormatException e) {
                //大部分网络模型都存在a.b.c.d:d.e.d这样多个OID拼接的情况
                if(logger.isInfoEnabled()) {
                    logger.info("current OID should split with ':' " + oid);
                }
                subOIDs = SnmpUtil.splitOIDs(oid, OID_SEPARATOR);
            }
            if(null != subOIDs) {
                for(String subOIDStr : subOIDs) {
                    try{
                        pdu.add(new VariableBinding(new OID(subOIDStr)));
                    }catch (Exception e){
                        if(logger.isErrorEnabled()){
                            logger.error("sub OID still occurs error:" + subOIDStr, e);
                        }
                        continue;
                    }
                }
            }else {
                pdu.add(new VariableBinding(OID));
            }
        }
        try {
            ResponseEvent responseEvent = snmp.send(pdu, target);
            if(null != responseEvent) {
                PDU resultPDU = responseEvent.getResponse();
                if(null != responseEvent.getError()){
                    if(logger.isErrorEnabled()){
                        logger.error("error OID:" + OIDs, responseEvent.getError());
                    }
                    return null;
                }
                if(PDU.noError != resultPDU.getErrorStatus()) {
                    if(logger.isErrorEnabled()) {
                        logger.error(resultPDU.getErrorStatus() + ":" + resultPDU.getErrorStatusText() + ",error OID:" + OIDs);
                    }
                    return null;
                }
                if(null != resultPDU) {
                    return handleMessage(resultPDU.getVariableBindings(), (OIDs.size() != pdu.size()), OIDs, parameters);
                }
            }
        } catch (IOException e) {
            if(logger.isErrorEnabled()) {
                logger.error(e.getMessage() + ",error OID:" + OIDs, e);
            }
        }
        return null;
    }

    @Override
    public <T> List<List<String>> handleMessage(T... t) {

        Vector<? extends VariableBinding> variableBindings = (Vector<? extends VariableBinding>)t[0];
        List<List<String>> rows = new ArrayList<>(1);
        List<String> columns = new ArrayList<>(variableBindings.size());
        List<String> keepOIDs = null;
        boolean isCached = (Boolean)t[1];
        List<String> originalArray = null;
        if(isCached) {
            originalArray = (List<String>)t[2];
        }
        for(VariableBinding variableBinding : variableBindings) {
            Variable variable = variableBinding.getVariable();
            if(!(variable instanceof Null)){
                columns.add(SnmpUtil.format(variableBinding.getOid(), variableBinding.getVariable(), false));
            }else{
                columns.add(null);
            }
            if(isCached) {
                if(null == keepOIDs)
                    keepOIDs = new ArrayList<>(variableBindings.size());
                keepOIDs.add(variableBinding.getOid().toString());
            }
        }
        if(isCached && !columns.isEmpty()) {
            int cursor = -1; //遍历所在位置
            for(int i = 0; i < originalArray.size(); i++) {
                String oidStr = originalArray.get(i);
                int splitCount = SnmpUtil.LengthOfSplitOIDs(oidStr, OID_SEPARATOR);
                cursor = i;
                if(splitCount > 1) {
                    boolean isExistNotNull = false;
                    while (splitCount > 0) {
                        String value = columns.get(cursor);
                        if(null != value) {
                            if(!isExistNotNull) {
                                isExistNotNull = true;
                                splitCount--;
                                cursor++;
                                continue;
                            }
                        }
                        if(splitCount == 1 && !isExistNotNull) {
                            break;
                        }
                        columns.remove(cursor);
                        keepOIDs.remove(cursor);
                        splitCount--;
                    }
                }
            }
            Map<String, String> keyMap = (Map<String, String>)t[3];
            metricBufferCallback.bufferMetrics(keyMap.get(METRIC_ID), keepOIDs);
        }
        rows.add(columns);
        return rows;
    }

    @Override
    public void setMetricBufferCallback(MetricBufferCallback callback) {
        this.metricBufferCallback = callback;
    }
}
