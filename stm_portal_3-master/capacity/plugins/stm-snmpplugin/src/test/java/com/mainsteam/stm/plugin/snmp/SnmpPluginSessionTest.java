package com.mainsteam.stm.plugin.snmp;

import com.mainsteam.stm.obj.TimePeriod;
import com.mainsteam.stm.plugin.common.NetInterfaceProcessor;
import com.mainsteam.stm.pluginprocessor.ProcessParameter;
import com.mainsteam.stm.pluginprocessor.ResultSet;
import com.mainsteam.stm.pluginprocessor.ResultSetMetaInfo;
import com.mainsteam.stm.pluginsession.PluginResultSet;
import com.mainsteam.stm.pluginsession.PluginSession;
import com.mainsteam.stm.pluginsession.PluginSessionContext;
import com.mainsteam.stm.pluginsession.parameter.Parameter;
import com.mainsteam.stm.pluginsession.parameter.PluginArrayExecutorParameter;
import com.mainsteam.stm.pluginsession.parameter.PluginInitParameter;

import java.util.*;
import java.util.concurrent.TimeUnit;

public class SnmpPluginSessionTest {

    public static void main(String[] args) throws Exception {
        PluginArrayExecutorParameter executorParameter = new PluginArrayExecutorParameter();
//		if(args != null && args.length == 3) {
        ParameterValue[] pvs = new ParameterValue[21];

        for (int i = 0; i < pvs.length; i++) {
            pvs[i] = new ParameterValue();
        }
        pvs[0].setKey("method");
        pvs[0].setValue("getBulk");
        pvs[1].setKey("COUNTER64");
        pvs[1].setValue("1.3.6.1.2.1.31.1.1.1.6");
        pvs[2].setKey("COUNTER64");
        pvs[2].setValue("1.3.6.1.2.1.31.1.1.1.7");
        pvs[3].setKey("COUNTER64");
        pvs[3].setValue("1.3.6.1.2.1.31.1.1.1.8");
        pvs[4].setKey("COUNTER64");
        pvs[4].setValue("1.3.6.1.2.1.31.1.1.1.9");
        pvs[5].setKey("COUNTER64");
        pvs[5].setValue("1.3.6.1.2.1.31.1.1.1.10");
        pvs[6].setKey("COUNTER64");
        pvs[6].setValue("1.3.6.1.2.1.31.1.1.1.11");
        pvs[7].setKey("COUNTER64");
        pvs[7].setValue("1.3.6.1.2.1.31.1.1.1.12");
        pvs[8].setKey("COUNTER64");
        pvs[8].setValue("1.3.6.1.2.1.31.1.1.1.13");

        pvs[9].setKey("COUNTER32");
        pvs[9].setValue("1.3.6.1.2.1.2.2.1.10");
        pvs[10].setKey("COUNTER32");
        pvs[10].setValue("1.3.6.1.2.1.2.2.1.11");
        pvs[11].setKey("COUNTER32");
        pvs[11].setValue("1.3.6.1.2.1.31.1.1.1.2");
        pvs[12].setKey("COUNTER32");
        pvs[12].setValue("1.3.6.1.2.1.31.1.1.1.3");
        pvs[13].setKey("COUNTER32");
        pvs[13].setValue("1.3.6.1.2.1.2.2.1.16");
        pvs[14].setKey("COUNTER32");
        pvs[14].setValue("1.3.6.1.2.1.2.2.1.17");
        pvs[15].setKey("COUNTER32");
        pvs[15].setValue("1.3.6.1.2.1.31.1.1.1.4");
        pvs[16].setKey("COUNTER32");
        pvs[16].setValue("1.3.6.1.2.1.31.1.1.1.5");
        pvs[17].setKey("COUNTER32");
        pvs[17].setValue("1.3.6.1.2.1.2.2.1.13");
        pvs[18].setKey("COUNTER32");
        pvs[18].setValue("1.3.6.1.2.1.2.2.1.19");
        pvs[19].setKey("COUNTER32");
        pvs[19].setValue("1.3.6.1.2.1.2.2.1.5");
        pvs[20].setKey("COUNTER32");
        pvs[20].setValue("1.3.6.1.2.1.2.2.1.151");




        executorParameter.setParameters(pvs);

        PluginSessionContext context = new PluginSessionContext() {
            private Map<String, Object> map = new HashMap<String, Object>();
            @Override
            public String getResourceId() {
                return "CiscoSwitch";
            }

            @Override
            public long getResourceInstanceId() {
                return 10000;
            }

            @Override
            public String getMetricId() {
                return "discardsRate";
            }

            @Override
            public Date getMetricCollectTime() {
                return new Date();
            }

            @Override
            public void setRuntimeValue(String s, Object o) {
                map.put(s, o);
            }

            @Override
            public Object getRuntimeValue(String s) {
                return map.get(s);
            }

            @Override
            public void setThreadLocalRuntimeValue(String s, Object o) {

            }

            @Override
            public Object getThreadLocalRuntimeValue(String s) {
                return null;
            }
        };
        PluginInitParameter initParameters = new PluginInitParameter() {
            public Parameter[] getParameters() {
                ParameterValue[] pvs = new ParameterValue[6];
                for (int i = 0; i < pvs.length; i++) {
                    pvs[i] = new ParameterValue();
                }
                pvs[0].setKey("IP");
                pvs[0].setValue("192.168.13.20");
                pvs[1].setKey("snmpPort");
                pvs[1].setValue("161");
                pvs[2].setKey("community");
                pvs[2].setValue("public");
                pvs[3].setKey("snmpVersion");
                pvs[3].setValue("1");
                pvs[4].setKey("snmpRetry");
                pvs[4].setValue("1");
                pvs[5].setKey("snmpTimeout");
                pvs[5].setValue("10000");
                return pvs;
            }

            public String getParameterValueByKey(String key) {
                return null;
            }
        };

        PluginSession session = new ExtSnmpPluginSession();
        session.init(initParameters);
        long pre = System.currentTimeMillis();
        PluginResultSet pluginResultSet = session.execute(executorParameter, context);
        long curr = System.currentTimeMillis();
        System.err.println("Costs time:" + (curr - pre) + " ms.");
        System.out.println(pluginResultSet);

//        NetInterfaceProcessor netInterfaceProcessor = new NetInterfaceProcessor();
//
//        String columnStr = "ifIndex,ifHCInOctets,ifHCInUcastPkts,ifHCInMulticastPkts,ifHCInBroadcastPkts,ifHCOutOctets,ifHCOutUcastPkts,ifHCOutMulticastPkts,ifHCOutBroadcastPkts" +
//                ",ifInOctets,ifInUcastPkts,ifInMulticastPkts,ifInBroadcastPkts,ifOutOctets,ifOutUcastPkts,ifOutMulticastPkts,ifOutBroadcastPkts,ifInDiscards,ifOutDiscards,ifSpeed,ifHighSpeed";
//        String[] columns = columnStr.split("\\,");
//        List<String> columnList = new ArrayList<String>();
//        for(String str : columns) {
//            columnList.add(str);
//        }
//        ResultSetMetaInfo metaInfo = new ResultSetMetaInfo(columnList);
//        ResultSet resultSet = new ResultSet(pluginResultSet, metaInfo);
//
//        ProcessParameter processParameter = new ProcessParameter();
//        com.mainsteam.stm.pluginprocessor.ParameterValue parameterValue = new com.mainsteam.stm.pluginprocessor.ParameterValue();
//        parameterValue.setKey("COLUMNINDEX");
//        parameterValue.setValue("ifIndex");
//        com.mainsteam.stm.pluginprocessor.ParameterValue parameterValue1 = new com.mainsteam.stm.pluginprocessor.ParameterValue();
//        parameterValue1.setKey("FUNCTION");
//        parameterValue1.setValue("ROUND_CHG");
//        com.mainsteam.stm.pluginprocessor.ParameterValue parameterValue2 = new com.mainsteam.stm.pluginprocessor.ParameterValue();
//        parameterValue2.setKey("COUNTER32");
//        parameterValue2.setValue("2^32");
//        com.mainsteam.stm.pluginprocessor.ParameterValue parameterValue3 = new com.mainsteam.stm.pluginprocessor.ParameterValue();
//        parameterValue3.setKey("COUNTER64");
//        parameterValue3.setValue("2^64");
//
//        processParameter.addParameter(parameterValue);
//        processParameter.addParameter(parameterValue1);
//        processParameter.addParameter(parameterValue2);
//        processParameter.addParameter(parameterValue3);
//
        TimeUnit.SECONDS.sleep(10);
//
        long pre1 = System.currentTimeMillis();
        PluginResultSet pluginResultSet1 = session.execute(executorParameter, context);
        long curr1 = System.currentTimeMillis();
        System.err.println("Costs time:" + (curr1 - pre1) + " ms.");
        System.out.println(pluginResultSet1);

        //ResultSet resultSet1 = new ResultSet(pluginResultSet1, metaInfo);
        //netInterfaceProcessor.process(resultSet1, processParameter, context);

        //System.out.println(resultSet1);

    }


    //assertNotNull(pluginResultSet);
    //assertTrue(pluginResultSet.getRowLength() > 1);
    //assertTrue(pluginResultSet.getColumnLength() == 5);
//		assertTrue(pluginResultSet.getValue(0, 0).equals("4"));
//		assertTrue(pluginResultSet.getValue(0, 1).equals("1"));
//		assertTrue(pluginResultSet.getValue(0, 2).equals("1"));

//	}
}

class ParameterValue implements Parameter {

    private String key;
    private String value;

    public ParameterValue() {
    }

    public String getKey() {
        return key;
    }

    public void setKey(String key) {
        this.key = key;
    }

    public String getValue() {
        return value;
    }

    public void setValue(String value) {
        this.value = value;
    }

    @Override
    public String getType() {
        return null;
    }
}
