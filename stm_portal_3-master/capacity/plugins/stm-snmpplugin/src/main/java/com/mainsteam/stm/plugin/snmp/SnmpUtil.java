package com.mainsteam.stm.plugin.snmp;

import org.apache.commons.lang.StringUtils;
import org.snmp4j.smi.OID;
import org.snmp4j.smi.OctetString;
import org.snmp4j.smi.Variable;
import org.snmp4j.util.SimpleVariableTextFormat;
import org.snmp4j.util.VariableTextFormat;

import java.io.UnsupportedEncodingException;

/**
 * Created by Xiaopf on 2017/5/2.
 */
public class SnmpUtil {

    private static final VariableTextFormat variableTextFormat = new SimpleVariableTextFormat();

    public static String format(OID oid, Variable variable, boolean withOID) {
        if(variable instanceof OctetString) {
            if(((OctetString) variable).isPrintable()) //如果是中文，则不会格式化数据
                return variableTextFormat.format(oid, variable, false);
            else {
                try {
                    return new String(((OctetString) variable).getValue(), "UTF-8");
                } catch (UnsupportedEncodingException e) {
                    return new String(((OctetString) variable).getValue());
                } catch (Throwable throwable) {
                    return variableTextFormat.format(oid, variable, false);
                }
            }
        }else {
            return variableTextFormat.format(oid, variable, false);
        }
    }

    public static String[] splitOIDs(String OIDSequences, String regex) {
        try{
            if(StringUtils.isNotBlank(OIDSequences) && StringUtils.isNotBlank(regex)) {
                return OIDSequences.split(regex);
            }
        }catch (Exception e){
            return null;
        }
        return null;
    }

    public static int LengthOfSplitOIDs(String OIDSequences, String regex) {
        if(StringUtils.isNotBlank(OIDSequences) && StringUtils.isNotBlank(regex)) {
            String[] result = splitOIDs(OIDSequences, regex);
            return (result == null) ? 0 : result.length;
        }
        return 0;
    }

}
