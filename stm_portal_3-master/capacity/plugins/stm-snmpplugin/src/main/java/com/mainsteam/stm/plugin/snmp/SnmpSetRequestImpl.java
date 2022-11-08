package com.mainsteam.stm.plugin.snmp;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.snmp4j.PDU;
import org.snmp4j.ScopedPDU;
import org.snmp4j.event.ResponseEvent;
import org.snmp4j.mp.SnmpConstants;
import org.snmp4j.smi.*;
import org.snmp4j.util.SimpleVariableTextFormat;
import org.snmp4j.util.VariableTextFormat;

import java.io.IOException;
import java.util.*;

/**
 * Created by Xiaopf on 2017/5/3.
 */
public class SnmpSetRequestImpl extends AbstractSnmpRequest {

    private static final Log logger = LogFactory.getLog(SnmpSetRequestImpl.class);
    private static final String OCTET_STRING = "OctetString";
    private static final String COUNTER32 = "Counter32";
    private static final String COUNTER64 = "Counter64";
    private static final String INTEGER32 = "Integer32";
    private static final String GAUGE32 = "Gauge32";
    private MetricBufferCallback metricBufferCallback;

    public SnmpSetRequestImpl(Map<String, String> config) {
        super(config);
    }

    @Override
    public List<List<String>> sendMessage(Map<String,String> metricId, List<String> OIDs) {
        PDU pdu = null;
        try{
            List<String> oidArray = OIDs.subList(0, OIDs.size()/3);
            List<String> variables = OIDs.subList(OIDs.size()/3, OIDs.size()*2);
            List<String> types = OIDs.subList(OIDs.size()/3*2, OIDs.size());
            pdu = createPDU(target.getVersion(), oidArray, types, variables);
        }catch (Exception e){
            if(logger.isErrorEnabled()) {
                logger.error(e.getMessage() + ", set operation occurs error:" + OIDs, e);
            }
            return null;
        }
        ResponseEvent responseEvent = null;
        try {
            responseEvent = snmp.send(pdu, target);
        } catch (IOException e) {
            if(logger.isErrorEnabled()) {
                logger.error(e.getMessage() + ", sending data occurs error:" + OIDs, e);
            }
            return null;
        }
        if (responseEvent.getError() != null){
            if(logger.isErrorEnabled()) {
                logger.error("snmp set response exception:" + OIDs, responseEvent.getError());
            }
            return null;
        }
        PDU response = responseEvent.getResponse();
        if (response.getErrorStatus() != PDU.noError){
            if(logger.isErrorEnabled()) {
                logger.error(response.getErrorStatus() + ":" + response.getErrorStatusText() + ":" + OIDs);
            }
            return null;
        }

        return handleMessage(response.getVariableBindings());

    }

    @Override
    public <T> List<List<String>> handleMessage(T... T) {
        Vector<? extends VariableBinding> variableBindings = (Vector<? extends VariableBinding>)T[0];
        List<List<String>> rows = new ArrayList<>(1);
        List<String> columns = new ArrayList<>(variableBindings.size());
        for (VariableBinding variableBinding : variableBindings) {
            if (!variableBinding.isException()) {
                Variable variable = variableBinding.getVariable();
                if(!(variable instanceof Null)) {
                    columns.add(SnmpUtil.format(variableBinding.getOid(), variableBinding.getVariable(), false));
                }else {
                    if(logger.isWarnEnabled()) {
                        logger.warn("set operation return Null object:" + variableBinding.getOid().toString());
                    }
                }
            }
            rows.add(columns);
        }
        return rows;
    }

    @Override
    public void setMetricBufferCallback(MetricBufferCallback callback) {
        metricBufferCallback = callback;
    }

    private PDU createPDU(int pduType, List<String> OIDs, List<String> types, List<String> variables) {
        try{
            PDU pdu = (pduType == SnmpConstants.version3) ? (new ScopedPDU()) : (new PDU());
            pdu.setType(PDU.SET);
            for (int i = 0; i < OIDs.size(); ++i) {
                String oid = OIDs.get(i);
                String variable = variables.get(i);
                String type = types.get(i);
                pdu.add(new VariableBinding(new OID(oid), getVariable(variable, type)));
            }
            return pdu;
        }catch (Exception e){
            throw e;
        }
    }

    private Variable getVariable(String variable, String type) {
        switch (type) {
            case OCTET_STRING:
                return new OctetString(variable);
            case COUNTER32:
                return new Counter32(Long.valueOf(variable));
            case COUNTER64:
                return new Counter64(Long.valueOf(variable));
            case INTEGER32:
                return new Integer32(Integer.valueOf(variable));
            case GAUGE32:
                return new Gauge32(Long.valueOf(variable));
            default:
                throw new IllegalArgumentException("Invalid type: " + type);
        }
    }
}
