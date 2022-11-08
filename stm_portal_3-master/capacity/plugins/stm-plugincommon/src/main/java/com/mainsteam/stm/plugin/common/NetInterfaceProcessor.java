package com.mainsteam.stm.plugin.common;

import com.mainsteam.stm.pluginprocessor.*;
import com.mainsteam.stm.pluginsession.PluginSessionContext;
import com.mainsteam.stm.pluginsession.exception.PluginSessionRunException;
import org.apache.commons.jexl2.Expression;
import org.apache.commons.jexl2.JexlArithmetic;
import org.apache.commons.jexl2.JexlEngine;
import org.apache.commons.lang.StringUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import java.math.BigDecimal;
import java.util.*;

/**专门针对网络设备Counter32和Counter42混合采集的处理器，其他模型慎用
 * Created by Xiaopf on 2017/5/8.
 */
public class NetInterfaceProcessor implements PluginResultSetProcessor {

    private static final Log logger = LogFactory.getLog(NetInterfaceProcessor.class);

    private static final String SNMP_UNIT_KEY = "SNMPUnit";
    private static final String LAST_COLLECT_TIME = "lastCollectTime";
    private static final String LAST_RESULT_SET = "lastResultSet";
    private static final String CHGV = "CHGV";
    private static final String CHG = "CHG";
    private static final String CHGRA = "CHGRA";
    private static final String CHGR = "CHGR";
    private static final String ROUND_CHG = "ROUND_CHG";
    private static final String ROUND_CHGV = "ROUND_CHGV";
    private static final String FUNCTION = "FUNCTION";
    private static final String ROUND_PREFIX = "ROUND_";
    private static final String ZERO = "0";
    private static final int SCALE = 2;

    private static final int SNMP_VERSION_1 = 0; // snmp version 1

    private static final String COUNTER64 = "COUNTER64";
    private static final String COUNTER32 = "COUNTER32";

    private static final JexlEngine jexl;
    private static final String MILLISECONDS = "1000";
    private static final int FastEthernet100 = 100000000; //百兆网口
    private static final int FastEthernet10 = 10000000;//十兆网口
    private static final int COUNTER64_COLUMNS = 8;//Counter64总列数
    private static final String DEFAULT_VALUE = "0";
    private static final int DISCARDS_COLUMNS = 2;//丢包数总列数

    static {
        JexlArithmetic jexlArithmetic = new JexlArithmetic(false) {
            @Override
            public Object bitwiseXor(Object left, Object right) {
                return Math.pow(Double.parseDouble(left.toString()), Double.parseDouble(right.toString()));
            }
        };
        jexl = new JexlEngine(null, jexlArithmetic, null, null);
        jexl.setCache(512);
        jexl.setLenient(false); // do not treat null as zero
        jexl.setSilent(false); // throw exception
    }

    @Override
    public void process(ResultSet resultSet, ProcessParameter processParameter, PluginSessionContext context)
            throws PluginSessionRunException {

        if(null == resultSet && resultSet.getRowLength() <=0)
            return;
        else {
            Object lastRuntimeValue = context.getRuntimeValue(context.getResourceInstanceId() + "-" + context.getMetricId());

            Map<String, Object> tempMap = new HashMap<String, Object>(2, 0.5f);
            tempMap.put(LAST_COLLECT_TIME, context.getMetricCollectTime());
            ResultSet lastResultSet = null;
            lastResultSet = (ResultSet) resultSet.clone();
            tempMap.put(LAST_RESULT_SET, lastResultSet);
            Object snmpVersionCurObj = resultSet.getExtraValue(SNMP_UNIT_KEY);
            tempMap.put(SNMP_UNIT_KEY, snmpVersionCurObj);
            context.setRuntimeValue(context.getResourceInstanceId() + "-" + context.getMetricId(), tempMap);
            lastResultSet = null;

            if (lastRuntimeValue == null) { // first collecting
                //首次计算不需要将为空的值设置为空
                resultSet.clear();
                return;
            } else {
                Map<String, Object> lastMap = (HashMap<String, Object>) lastRuntimeValue;
                Object snmpVersionObj = lastMap.get(SNMP_UNIT_KEY);
                if(null != snmpVersionObj && null != snmpVersionCurObj) {
                    /*
                    如果前后两次采集使用了不同的snmp版本，那么将丢弃本次采集数据。就以v1和v2而言，v1只支持counter32的数据，
                    v2则counter32和counter64都支持，也可能只支持v1,那么两次值的差值则无法预测，只能简单的扔掉当前数据
                     */
                    if(!StringUtils.equals(snmpVersionObj.toString(), snmpVersionCurObj.toString())) {
                        if(logger.isWarnEnabled()) {
                            logger.warn("{" + context.getResourceInstanceId() + ":" + context.getMetricId() +
                                    "} resultSet has to be cleared, cause snmp version changed.");
                        }
                        resultSet.clear();
                        return;
                    }
                }

                ResultSet preResultSet = (ResultSet) lastMap.get(LAST_RESULT_SET);
                long preSeconds = ((Date) lastMap.get(LAST_COLLECT_TIME)).getTime();
                long currentSeconds = context.getMetricCollectTime().getTime();
                BigDecimal interval = new BigDecimal(currentSeconds - preSeconds).divide(new BigDecimal(MILLISECONDS));

                String function = processParameter.getParameterValueByKey(FUNCTION).getValue();
                boolean isSnmpV1 = false;
                if(null != snmpVersionCurObj) {
                    isSnmpV1 = (Integer.valueOf(snmpVersionCurObj.toString())).intValue() == SNMP_VERSION_1;
                }
                List<List<String>> resultList = new ArrayList(resultSet.getRowLength());

                String[] modString = new String[2];
                modString[0] = processParameter.getParameterValueByKey(COUNTER32).getValue();
                modString[1] = processParameter.getParameterValueByKey(COUNTER64).getValue();
                Expression counter32Exp = jexl.createExpression(modString[0].trim());
                Expression counter64Exp = jexl.createExpression(modString[1].trim());
                BigDecimal counter32Mod = new BigDecimal(counter32Exp.evaluate(null).toString());
                BigDecimal counter64Mod = new BigDecimal(counter64Exp.evaluate(null).toString());
                for (int row = 0; row < resultSet.getRowLength(); ++row) {
                    try{

                        // 最后两列为带宽值，需要根据带宽的值来判断使用counter32位还是counter64位
                        List<String> rowList = new ArrayList<>(resultSet.getColumnLength()-1);
                        rowList.add(resultSet.getValue(row, 0));
                        for (int column = 1; column < resultSet.getColumnLength()-1; ++column) {//后两列为带宽值

                            if (function.startsWith(ROUND_PREFIX)) {
                                int ifSpeedIndex = resultSet.getColumnLength()-1;
                                int ifHighSpeedIndex = resultSet.getColumnLength();
                                if(isSnmpV1) {
                                    ifSpeedIndex = ifSpeedIndex/2+1;
                                    ifHighSpeedIndex = ifHighSpeedIndex/2+2;
                                }
                                String ifSpeedStr = resultSet.getValue(row, ifSpeedIndex); //ifTable
                                String ifHighSpeedStr = resultSet.getValue(row, ifHighSpeedIndex); //ifXTable
                                BigDecimal ifSpeed = null;
                                if(null !=ifHighSpeedStr) {
                                    ifSpeed = new BigDecimal(ifHighSpeedStr);
                                }else if(null != ifSpeedStr) {
                                    ifSpeed = new BigDecimal(ifSpeedStr);
                                }else {
                                    if(logger.isInfoEnabled()) {
                                        StringBuffer stringBuffer = new StringBuffer(200);
                                        stringBuffer.append("{");
                                        stringBuffer.append(context.getResourceInstanceId());
                                        stringBuffer.append(":");
                                        stringBuffer.append(context.getMetricId());
                                        stringBuffer.append("}, row index:");
                                        stringBuffer.append(row);
                                        stringBuffer.append(" can't find ifSpeed, so remove current row.");
                                        logger.info(stringBuffer.toString());
                                        stringBuffer = null;
                                    }
                                    rowList = null;
                                    break;
                                }
                                ifSpeed = (ifSpeed.compareTo(new BigDecimal(FastEthernet10)) < 0 ? ifSpeed.multiply(new BigDecimal(1000000)) : ifSpeed);

                                for(int j = 0; j < COUNTER64_COLUMNS; j++) { //取前8列，按照固定顺序而言，前8列为counter64，后面均为counter32数据
                                    String value = null;
                                    String preValue = null;
                                    boolean useCounter64 = true;
                                    if(ifSpeed.compareTo(new BigDecimal(FastEthernet100)) < 0 && !isSnmpV1) {//十兆接口使用counter32，百兆接口以上（含百兆）使用counter64
                                        value = resultSet.getValue(row, column+j+8);
                                        preValue = preResultSet.getValue(row, column+j+8);
                                        useCounter64 = false;
                                    }else {
                                        value = resultSet.getValue(row, column+j);
                                        preValue = preResultSet.getValue(row, column+j);
                                        if((null == value || null == preValue) && !isSnmpV1){
                                            value = resultSet.getValue(row, column+j+8);
                                            preValue = preResultSet.getValue(row, column+j+8);
                                            useCounter64 = false;
                                        }
                                        if(isSnmpV1)
                                            useCounter64 = false;
                                    }
                                    if(null == value || null == preValue){
                                        rowList.add(DEFAULT_VALUE);
                                        continue;
                                    }
                                    BigDecimal currentBD = new BigDecimal(value);
                                    BigDecimal preBD = new BigDecimal(preValue);
                                    while (currentBD.compareTo(preBD) < 0) {
                                        if(useCounter64)
                                            currentBD = currentBD.add(counter64Mod);
                                        else
                                            currentBD = currentBD.add(counter32Mod);
                                    }
                                    rowList.add(calculate(function, currentBD, preBD, interval));
                                }
                                for (int i = 0; i < DISCARDS_COLUMNS; i++) { //最后两行为丢包数
                                    String value = resultSet.getValue(row, column+(isSnmpV1?8:16)+i);
                                    String preValue = preResultSet.getValue(row, column+(isSnmpV1?8:16)+i);
                                    if(null == value || null == preValue)
                                        rowList.add(DEFAULT_VALUE);
                                    else {
                                        BigDecimal currentBD = new BigDecimal(value);
                                        BigDecimal preBD = new BigDecimal(preValue);
                                        while (currentBD.compareTo(preBD) < 0) {
                                            currentBD = currentBD.add(counter32Mod);
                                        }
                                        rowList.add(calculate(function, currentBD, preBD, interval));
                                    }

                                }
                                rowList.add(String.valueOf(ifSpeed));
                                break;
                            }
                        }
                        if(null != rowList && !rowList.isEmpty())
                            resultList.add(rowList);
                    }catch (Exception e) {
                        StringBuffer stringBuffer = new StringBuffer(200);
                        stringBuffer.append("{");
                        stringBuffer.append(context.getResourceInstanceId());
                        stringBuffer.append(":");
                        stringBuffer.append(context.getMetricId());
                        stringBuffer.append("}, row index:");
                        stringBuffer.append(row);
                        stringBuffer.append(" occurs exception in NetInterfaceProcessor:");
                        stringBuffer.append(e.getMessage());
                        logger.error(stringBuffer.toString(), e);
                        stringBuffer = null;
                        continue;
                    }
                }
                preResultSet = null;
                counter32Exp = null;
                counter64Exp = null;
                counter32Mod = null;
                counter64Mod = null;
                resultSet.clear();
                ResultSetMetaInfo metaInfo = resultSet.getResultMetaInfo();
                String[] columnNames = metaInfo.getColumnNames();
                metaInfo.removeAllColumnNames();

                for(int i = 0; i < columnNames.length; i++) {
                    if(i < 9 || (i > 16 & i < 20)) {
                        metaInfo.addColumnName(columnNames[i]);
                    }
                }
                for(List<String> rows : resultList) {
                    String[] rowsArray = new String[rows.size()];
                    resultSet.addRow(rows.toArray(rowsArray));
                }
                columnNames = null;
                resultList = null;

            }
        }

    }

    private String calculate(String function, BigDecimal currentValue, BigDecimal preValue, BigDecimal interval) {
        String value = null;
        switch (function) {
            case ROUND_CHG: // 翻转变化量
            case CHG: // 变化量
                value = currentValue.subtract(preValue).toString();
                break;
            case ROUND_CHGV: // 翻转的变化速率
            case CHGV: // 变化速率
                value = currentValue.subtract(preValue).divide(interval, SCALE, BigDecimal.ROUND_HALF_EVEN).toString();
                break;
            case CHGR: // 变化比率
                value = preValue.compareTo(new BigDecimal(ZERO)) == 0 ? ZERO : currentValue.subtract(preValue)
                        .divide(preValue, SCALE, BigDecimal.ROUND_HALF_EVEN).toString();
                break;
            case CHGRA: // 绝对比率
                value = preValue.compareTo(new BigDecimal(ZERO)) == 0 ? ZERO : currentValue.subtract(preValue)
                        .divide(preValue, SCALE, BigDecimal.ROUND_HALF_EVEN).abs().toString();
                break;
        }
        return value;
    }
}
