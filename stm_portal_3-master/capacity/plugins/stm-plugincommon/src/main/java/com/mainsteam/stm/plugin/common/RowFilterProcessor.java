package com.mainsteam.stm.plugin.common;

import com.mainsteam.stm.pluginprocessor.*;
import com.mainsteam.stm.pluginsession.PluginResultSet;
import com.mainsteam.stm.pluginsession.PluginSessionContext;
import com.mainsteam.stm.pluginsession.exception.PluginSessionRunException;
import com.mainsteam.stm.util.MacFilterUtil;
import com.mainsteam.stm.util.NetworkTool;
import org.apache.commons.lang.StringUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import java.util.*;

public class RowFilterProcessor implements PluginResultSetProcessor {

    private static final Log logger = LogFactory.getLog(RowFilterProcessor.class);

    public static final String FILTER_COLUMN = "FilterColumn";
    public static final String RESERVED_VALUES = "ReservedValues";
    public static final String REMOVE_VALUES = "RemoveValues";
    public static final String MAC_ADDRESS = "MacAddress";
    public static final String SEPARATOR = ",";

    public static void main(String[] argv) throws Exception {

        PluginResultSet pluginResultSet = new PluginResultSet();
        pluginResultSet.putValue(0, 0, "0");
        //pluginResultSet.putValue(0, 1, "626");
        // pluginResultSet.putValue(0, 2, "3.3719347344E11");
        // pluginResultSet.putValue(0, 3, "200");
        // pluginResultSet.putValue(1, 0, "100");
        //pluginResultSet.putValue(1, 0, "lPM@7.");
        //pluginResultSet.putValue(2, 0, "0:3:ba:6e:7e:d");
        //pluginResultSet.putValue(3, 0, " 6c 50 4d 40 37 2e   ");
        // pluginResultSet.putValue(1, 2, "100");
        // pluginResultSet.putValue(1, 3, "100");
        // pluginResultSet.putValue(2, 0, "100");
        //pluginResultSet.putValue(2, 1, "626");
        // pluginResultSet.putValue(2, 2, "100");
        // pluginResultSet.putValue(2, 3, "100");
        // pluginResultSet.putValue(3, 0, "100");
        // pluginResultSet.putValue(3, 1, "100");
        // pluginResultSet.putValue(3, 2, "0");
        // pluginResultSet.putValue(3, 3, "0");

        List<String> list = new ArrayList<String>();
        list.add("NICPhysAddress");

        ResultSetMetaInfo metaInfo = new ResultSetMetaInfo(list);
        ResultSet resultset = new ResultSet(pluginResultSet, metaInfo);

        RowFilterProcessor processor = new RowFilterProcessor();
        ProcessParameter parameter = new ProcessParameter();
        ParameterValue param = new ParameterValue();
        param.setKey("MacAddress");
        // param.setValue("SELECT (ifSpeed1_ifSpeed2!=0?(ifSpeed1_ifSpeed2<500000?(ifSpeed1_ifSpeed2*1000000):ifSpeed1_ifSpeed2):(ifSpeed2_ifSpeed1<500000?(ifSpeed2_ifSpeed1*1000000):ifSpeed2_ifSpeed1)) as ifSpeed");
        // param.setValue("SELECT ((sum(ifInDiscards)/(sum(ifInUcastPkts_ifHCInUcastPkts)+sum(ifInNucastPkts+ifInDiscards+ifInErrors)))*100) as inDiscardsRate");
        // param.setValue("SELECT differ_count(ifInUcastPkts_ifHCInUcastPkts)");
        param.setValue("");
        parameter.addParameter(param);
        processor.process(resultset, parameter, null);
        System.out.println(resultset);

    }

    @Override
    public void process(ResultSet resultSet, ProcessParameter parameter,
                        PluginSessionContext context) throws PluginSessionRunException {
        ParameterValue[] parameterValues = parameter.listParameterValues();
        Set<String> columns = new HashSet<>();
        Set<String> reserve = new HashSet<>();
        Set<String> remove = new HashSet<>();
        for (ParameterValue parameterValue : parameterValues) {
            switch (parameterValue.getKey()) {
                case FILTER_COLUMN:
                    addValues(columns, parameterValue.getValue());
                    break;
                case RESERVED_VALUES:
                    addValues(reserve, parameterValue.getValue().toLowerCase());
                    break;
                case REMOVE_VALUES:
                    addValues(remove, parameterValue.getValue().toLowerCase());
                    break;
                case MAC_ADDRESS:
                    filterMac(resultSet, parameterValue.getValue());
                    return;
                default:
                    if (logger.isWarnEnabled()) {
                        logger.warn("Invalid Process Parameter Key " + parameterValue.getKey() + " for RowFilterProcessor.");
                    }
            }
        }


        if (!reserve.isEmpty()) {
            ResultSet clonedResult = (ResultSet) resultSet.clone();
            resultSet.clear();
            ResultSetMetaInfo metaInfo = clonedResult.getResultMetaInfo();
            for (int columnIndex = 0; columnIndex < metaInfo.getColumnLength(); ++columnIndex) {
                if (columns.contains(metaInfo.getColumnName(columnIndex))) {
                    for (int rowIndex = 0; rowIndex < clonedResult.getRowLength(); ++rowIndex) {
                        String value = StringUtils.trimToEmpty(clonedResult.getValue(rowIndex, columnIndex));
                        if (reserve.contains(value.toLowerCase())) {
                            resultSet.addRow(clonedResult.getRow(rowIndex));
                        }
                    }
                }
            }
        } else if (!remove.isEmpty()) {
            ResultSetMetaInfo metaInfo = resultSet.getResultMetaInfo();
            for (int columnIndex = 0; columnIndex < metaInfo.getColumnLength(); ++columnIndex) {
                if (columns.contains(metaInfo.getColumnName(columnIndex))) {
                    for (int rowIndex = 0; rowIndex < resultSet.getRowLength(); ++rowIndex) {
                        String value = StringUtils.trimToEmpty(resultSet.getValue(rowIndex, columnIndex));
                        if (remove.contains(value.toLowerCase())) {
                            resultSet.removeRow(rowIndex);
                            --rowIndex;
                        }
                    }
                }
            }
        }
    }


    private void addValues(Set<String> set, String valueString) {
        String[] values = valueString.split(SEPARATOR);
        for (String value : values) {
            set.add(value.trim());
        }
    }

    /**
     * Mac地址过滤，若column为null或者空字符串，默认转换最后一列的值，
     * 否则按照逗号分隔依次过滤
     */
    private void filterMac(ResultSet resultSet, String column) {
//		if(logger.isWarnEnabled()) {
//			logger.warn("Starts to filter MacAddress ResultSet --->" + resultSet.toString());
//		}
        List<Integer> columnIndexs = new ArrayList<Integer>(1);
        if (StringUtils.isNotBlank(column)) {
            String[] columns = column.split(SEPARATOR);
            for (String col : columns) {
                columnIndexs.add(resultSet.getResultMetaInfo().indexOfColumnName(col));
            }
        } else
            columnIndexs.add(resultSet.getColumnLength() - 1);

        for (Integer columnIndex : columnIndexs) {
            String[] columnValues = resultSet.getColumn(columnIndex);
            Set<String> hashSet = new HashSet<String>();
            for (String str : columnValues) {
                //先统一格式化再过滤重复及不用可的MacAddress
                try {
                    str = NetworkTool.neatenMac(str);
                } catch (Exception e) {
                    logger.error("MacAddress format error, [" + str + "]." + e.getMessage(), e);
                }
                if (StringUtils.isNotBlank(str))
                    hashSet.add(str);
            }
            //需要保存columnName
            if (resultSet.getResultMetaInfo() != null) {
                String currentColumnName = resultSet.getResultMetaInfo().getColumnName(columnIndex);
                resultSet.removeColumn(columnIndex);
                if (StringUtils.isNotBlank(currentColumnName)) {
                    if (resultSet.getResultMetaInfo().getColumnLength() <= columnIndex)
                        resultSet.getResultMetaInfo().addColumnName(currentColumnName);
                    else
                        resultSet.getResultMetaInfo().setColumnName(columnIndex, currentColumnName);
                }

            }
//			if(logger.isWarnEnabled()) {
//				logger.warn("Starts to filter MacAddress(HashSet)--->" + hashSet.toString());
//			}
            hashSet = MacFilterUtil.filterMac(hashSet);
//			if(logger.isWarnEnabled()) {
//				logger.warn("After filtered MacAddress(HashSet)--->" + hashSet.toString());
//			}
            Iterator<String> iterator = hashSet.iterator();
            int row = 0;
            while (iterator.hasNext()) {
                String next = iterator.next();
                resultSet.putValue(row++, columnIndex, next);
            }
            for (int i = 0; i < resultSet.getRowLength(); ++i) {
                if (StringUtils.isBlank(resultSet.getValue(i, columnIndex))) {
                    resultSet.removeRow(i);
                    --i;
                }
            }

        }
//		if(logger.isWarnEnabled()) {
//			logger.warn("The final MacAddress ResultSet --->" + resultSet.toString());
//		}
    }

}
