package com.mainsteam.stm.plugin.snmp;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.snmp4j.smi.*;
import org.snmp4j.util.*;

import java.io.UnsupportedEncodingException;
import java.util.*;

import static com.mainsteam.stm.plugin.snmp.SnmpParamConst.METRIC_ID;
import static com.mainsteam.stm.plugin.snmp.SnmpParamConst.PDU_TYPE;

/**
 * Created by Xiaopf on 2017/5/2.
 */
public class SnmpWalkRequestImpl extends AbstractSnmpRequest {

    private static final Log logger = LogFactory.getLog(SnmpWalkRequestImpl.class);

    private static final String OID_SEPARATOR = ":";

    private PDUFactory factory;

    private MetricBufferCallback metricBufferCallback;

    public SnmpWalkRequestImpl(Map<String, String> config) {
        super(config);
        factory = new DefaultPDUFactory(Integer.valueOf(config.get(PDU_TYPE)));
    }

    /**
     * 需要处理两种特殊情况：
     * 一、OID类似a.b.c...:d.e.c...这种以：分割符串联的OID组，这种需要拆分出来，然后一起发送到snmp代理端，然后根据返回值，解析出正确的可采集到值的OID
     * 以指标ID的形式缓存起来，下次采集直接使用缓存的OID即可，可提高部分性能（解析和查询）
     * 二、OID以Counter32和Counter64这种混合方式查询,这种方式将一次性把所有的OID都walk回来，在特定的处理器处理
     * @param parameters
     * @param OIDs
     * @return
     */
    @Override
    public List<List<String>> sendMessage(Map<String,String> parameters, List<String> OIDs) {

        TableUtils tableUtils = new TableUtils(snmp, factory);
        if(null != OIDs && !OIDs.isEmpty()) {
            List<OID> OIDArray = new ArrayList<>(OIDs.size());
            for(String oidStr : OIDs) {
                try{
                    OID oid = new OID(oidStr);
                    OIDArray.add(oid);
                }catch (NumberFormatException e) {
                    //大部分网络模型都存在a.b.c.d:d.e.d这样多个OID拼接的情况
                    if(logger.isInfoEnabled()) {
                        logger.info("current OID should split with ':' " + oidStr);
                    }
                    String[] subOIDs = SnmpUtil.splitOIDs(oidStr, OID_SEPARATOR);
                    for(String str : subOIDs) {
                        try{
                            OID oid = new OID(str);
                            OIDArray.add(oid);
                        }catch (Exception e1) {
                            if(logger.isErrorEnabled()) {
                                logger.error("create OID still occurs error:" + str, e);
                            }
                            continue;
                        }
                    }
                }
            }
            OID[] data = new OID[OIDArray.size()];
            OIDArray.toArray(data);

            List<TableEvent> tableEvents = tableUtils.getTable(target, data, null, null);
            if(null == tableEvents || tableEvents.isEmpty()) {
                if(logger.isWarnEnabled()) {
                    logger.warn("current oid can't walk any value :" + OIDArray);
                }
            }else {
                return handleMessage(tableEvents, (data.length != OIDs.size()), OIDs, parameters);
            }
         }
        return null;
    }

    @Override
    public <T> List<List<String>> handleMessage(T... t) {
        List<TableEvent> tableEvents = (List<TableEvent>)t[0];
        final List<List<String>> resultList = new ArrayList<>(tableEvents.size());
        Iterator<TableEvent> iterator = tableEvents.iterator();
        boolean isCached = (Boolean)t[1];
        int[] columnIndex = null; //用来对每列的行数进行计数，用来判断那列的值为空，如果为空，则下次不再用这个oid进行查询
        while (iterator.hasNext()) {
            final TableEvent event = iterator.next();
            VariableBinding[] variableBindings = event.getColumns();
            if(null != variableBindings) {
                if(isCached && null == columnIndex) {
                    columnIndex = new int[variableBindings.length];
                }
                List<String> columns = new ArrayList<String>(variableBindings.length);
                columns.add(event.getIndex().toString());
                for(int i = 0; i < variableBindings.length; i++) {
                    try{
                        VariableBinding variableBinding = variableBindings[i];
                        if(null != variableBinding) {
                            columns.add(SnmpUtil.format(variableBinding.getOid(), variableBinding.getVariable(), false));
                            if(isCached)
                                columnIndex[i]++;
                        }else {
                            columns.add(null);
                        }
                    }catch (Exception e) {
                        //如果遍历一行数据出错，那么将抛掉整行数据，防止行数不对应，导致后面的计算出现异常，实际上就是扔掉本次采集数据
                        if(logger.isErrorEnabled()) {
                            logger.error(e.getMessage(), e);
                        }
                        if(isCached) {
                            for(int j = 0; j < columnIndex.length; j++) {
                                if(columnIndex[j] != 0) {
                                    columnIndex[j]--;
                                }
                            }
                        }
                        columns = null;
                        break;
                    }
                }
                if(null != columns && !columns.isEmpty()) {
                    resultList.add(columns);
                }
            }else {
                if(logger.isWarnEnabled()) {
                    logger.warn(event.getErrorMessage());
                }
            }
        }

        if(isCached) {
            List<String> source = (List<String>)t[2];
            int cursor = 0;//标志columnIndex移动位置
            List<Integer> removeColumn = new ArrayList<Integer>(columnIndex.length);
            for(int k = 0; k < source.size(); k++) {
                String[] subOIDList = SnmpUtil.splitOIDs(source.get(k), OID_SEPARATOR);
                if(null != subOIDList && subOIDList.length > 1) {
                    boolean isExistNotNull = false;
                    for (int i = 0; i < subOIDList.length; i++) {
                        cursor++;
                        if(columnIndex[cursor-1] != 0) {
                            if(!isExistNotNull){
                                isExistNotNull = true;
                                continue;
                            }
                        }
                        if((i == (subOIDList.length-1)) && !isExistNotNull){
                            break;
                        }
                        subOIDList[i] = null;
                        removeColumn.add(cursor);//resultList包含一列索引列，故这里不是cursor-1
                    }
                    isExistNotNull = false;
                    for(String str : subOIDList) {
                        if(null != str) {
                            source.set(k, str);
                            break;
                        }
                    }
                }else {
                    cursor++;
                }
            }
            List<List<String>> result = new ArrayList<>(resultList.size());
            for(List<String> row : resultList) {
                List<String> copyOfRow = new ArrayList<>(Arrays.asList(new String[row.size()]));
                Collections.copy(copyOfRow, row);
                for(int i = removeColumn.size()-1; i >= 0; i--){
                    copyOfRow.remove(removeColumn.get(i).intValue());
                }
                result.add(copyOfRow);
            }
            if(!source.isEmpty()){
                Map<String, String> keyMap = (Map<String, String>)t[3];
                metricBufferCallback.bufferMetrics(keyMap.get(METRIC_ID), source);
            }
            return result;
        }
        return resultList;
    }

    @Override
    public void setMetricBufferCallback(MetricBufferCallback callback) {
        this.metricBufferCallback = callback;
    }

}
