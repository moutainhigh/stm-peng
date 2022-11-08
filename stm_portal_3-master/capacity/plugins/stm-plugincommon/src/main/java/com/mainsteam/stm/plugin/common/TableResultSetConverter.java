package com.mainsteam.stm.plugin.common;

import java.util.ArrayList;
import java.util.List;

import com.alibaba.fastjson.JSON;
import org.apache.commons.lang.StringUtils;

import com.mainsteam.stm.pluginprocessor.ParameterValue;
import com.mainsteam.stm.pluginprocessor.PluginResultSetProcessor;
import com.mainsteam.stm.pluginprocessor.ProcessParameter;
import com.mainsteam.stm.pluginprocessor.ResultSet;
import com.mainsteam.stm.pluginprocessor.ResultSetMetaInfo;
import com.mainsteam.stm.pluginsession.PluginResultSet;
import com.mainsteam.stm.pluginsession.PluginSessionContext;

/**
 * 将结果集转化为表格JSONString
 *
 * @author xiaop_000, lich
 * @version 7.3.1
 * @since 4.0
 */
public class TableResultSetConverter implements PluginResultSetProcessor {

    @Override
    public void process(ResultSet resultSet, ProcessParameter parameter,
                        PluginSessionContext context) {

        if (null == resultSet || resultSet.isEmpty()) {
            return;
        }

//        if (null == parameter) {
//            if (resultSet.getColumnLength() == 0) {
//                return;
//            } else {
//                // String[] columns = resultSet.getColumn(0);
//            }
//        }

		/*
         * 返回顺序： 按照结果集的metaInfo结果集的顺寻来返回，例如 pid 用户 实际驻留内存 虚拟内存 cpu利用率 内存利用率 状态
		 * 启动时间 命令
		 */
//        ResultSetMetaInfo metaInfo = resultSet.getResultMetaInfo();
//        String pasteColumns = metaInfo.toString();
//        resultSet.getResultMetaInfo().removeAllColumnNames();
//        resultSet.getResultMetaInfo().addColumnName(pasteColumns);
//
//        List<String> listRow = new ArrayList<String>();
//        listRow.add(pasteColumns);
//        for (int i = 0; i < resultSet.getRowLength(); i++) {
//            String[] row = resultSet.getRow(i);
//            String rowValue = StringUtils.join(row, ",");
//            listRow.add(rowValue);
//        }
//        resultSet.clearRows();
//        for (String row : listRow) {
//            resultSet.addRow(new String[]{row});
//        }
        int row = resultSet.getRowLength();
        String[][] content = new String[row + 1][];
        content[0] = resultSet.getResultMetaInfo().getColumnNames();
        for (int i = 1; i <= row; i++) {
            content[i] = resultSet.getRow(i - 1);
        }
        String jsonString = JSON.toJSONString(content);
        resultSet.getResultMetaInfo().removeAllColumnNames();
        resultSet.clear();
        resultSet.putValue(0, 0, jsonString);
    }

    public static void main(String[] args) {

        PluginResultSet pluginSet = new PluginResultSet();
        List<String> list = new ArrayList<String>();
        list.add("pid");
        list.add("user");
        list.add("command");
        ResultSetMetaInfo meta = new ResultSetMetaInfo(list);
        ResultSet resultSet = new ResultSet(pluginSet, meta);
        resultSet.putValue(0, 0, "2");
        resultSet.putValue(0, 1, "root1");
        resultSet.putValue(0, 2, "[migration/0]");
        resultSet.putValue(1, 0, "3");
        resultSet.putValue(1, 1, "root2");
        resultSet.putValue(1, 2, "[ksoftirqd/0]");
        resultSet.putValue(2, 0, "4");
        resultSet.putValue(2, 1, "root3");
        resultSet.putValue(2, 2, "[watchdog/0]");

        ProcessParameter paramter = new ProcessParameter();

        ParameterValue value1 = new ParameterValue();
        value1.setKey("ValueColumnTitle");
        value1.setValue("user");
        paramter.addParameter(value1);

        ParameterValue value2 = new ParameterValue();
        value2.setKey("InstPropertyKey");
        value2.setValue("processId=6");
        paramter.addParameter(value2);

        TableResultSetConverter converter = new TableResultSetConverter();
        converter.process(resultSet, paramter, null);
        System.out.println(resultSet);
    }

}
