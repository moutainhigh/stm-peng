package com.mainsteam.stm.plugin.common;

import com.mainsteam.stm.errorcode.CapcityErrorCodeConstant;
import com.mainsteam.stm.pluginprocessor.*;
import com.mainsteam.stm.pluginsession.PluginResultSet;
import com.mainsteam.stm.pluginsession.PluginSessionContext;
import com.mainsteam.stm.pluginsession.exception.PluginSessionRunException;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.regex.PatternSyntaxException;

public class ColumnSplitProcessor implements PluginResultSetProcessor {

    private static final Log logger = LogFactory
            .getLog(ColumnSplitProcessor.class);

    private static final String REGULAR = "Regular";
    private static final String SPLIT = "SPLIT";
    private static final String AS = "AS";
    private static final String TO = "TO";
    private static final String COMMA = ",";

    @Override
    public void process(ResultSet resultSet, ProcessParameter parameter,
                        PluginSessionContext context) throws PluginSessionRunException {
        ParameterValue[] parameterValues = parameter.listParameterValues();
        for (ParameterValue parameterValue : parameterValues) {
            switch (parameterValue.getKey()) {
                case REGULAR:
                    regularSplit(resultSet, parameterValue.getValue());
                    break;
                default:
                    if (logger.isWarnEnabled()) {
                        logger.warn("Unsupported action : "
                                + parameterValue.getKey());
                    }
            }
        }
    }

    private void regularSplit(ResultSet resultSet, String valueString)
            throws PluginSessionRunException {
        // SPILIT column AS "regex" TO column1,column2
        if (!valueString.startsWith(SPLIT)) {
            throw new PluginSessionRunException(
                    CapcityErrorCodeConstant.ERR_CAPCITY_CONVERT_NULLPARAMETER,
                    valueString + " should start with " + SPLIT);
        }
        int ASPos = valueString.indexOf(AS);
        if (ASPos < 0) {
            throw new PluginSessionRunException(
                    CapcityErrorCodeConstant.ERR_CAPCITY_CONVERT_NULLPARAMETER,
                    "Can not find " + AS + " in " + valueString);
        }
        int TOPos = valueString.indexOf(TO);
        if (TOPos < 0) {
            throw new PluginSessionRunException(
                    CapcityErrorCodeConstant.ERR_CAPCITY_CONVERT_NULLPARAMETER,
                    "Can not find " + TO + " in " + valueString);
        }
        String sColumn = valueString.substring(SPLIT.length(), ASPos).trim();
        String regex = valueString.substring(ASPos + AS.length(), TOPos).trim();
        String[] tColumns = valueString.substring(TOPos + TO.length()).trim()
                .split(COMMA);
        ResultSetMetaInfo metaInfo = resultSet.getResultMetaInfo();
        if (metaInfo.indexOfColumnName(sColumn) < 0) {
            throw new PluginSessionRunException(
                    CapcityErrorCodeConstant.ERR_CAPCITY_CONVERT_NOTITLE,
                    "No such column : " + sColumn);
        }
        String[] sources = resultSet.getColumn(sColumn);
        for (String tColumn : tColumns) {
            metaInfo.addColumnName(tColumn);
        }
        try {
            Pattern pattern = Pattern.compile(regex);
            int row = 0;
            for (String source : sources) {
                if (source != null) {
                    Matcher matcher = pattern.matcher(source);
                    while (matcher.find()) {
                        int cnt = matcher.groupCount();
                        if (cnt != tColumns.length) {
                            throw new PluginSessionRunException(
                                    CapcityErrorCodeConstant.ERR_CAPCITY_CONVERT_NOTITLE,
                                    "The group count don't equal to the target column number : "
                                            + valueString);
                        }
                        for (int i = 1; i <= cnt; ++i) {
                            resultSet.putValue(row, tColumns[i - 1],
                                    matcher.group(i));
                        }
                    }
                }
                row++;
            }
            resultSet.removeColumnByTitle(sColumn);
        } catch (PatternSyntaxException e) {
            throw new PluginSessionRunException(
                    CapcityErrorCodeConstant.ERR_CAPCITY_CONVERT_NULLPARAMETER,
                    e);
        }
    }

    public static void main(String[] args) throws PluginSessionRunException {
        PluginResultSet pluginResultSet = new PluginResultSet();
        pluginResultSet.addRow(new String[]{"CLARiiON+0_0_3", "{32768}"});
        pluginResultSet.addRow(new String[]{"CLARiiON+0_0_1", "{2}"});
        pluginResultSet.addRow(new String[]{"CLARiiON+0_0_2", "{2}"});
        pluginResultSet.addRow(new String[]{"CLARiiON+0_0_0", "{2}"});
        ResultSetMetaInfo metaInfo = new ResultSetMetaInfo();
        metaInfo.addColumnName("DeviceID");
        metaInfo.addColumnName("OperationalStatus");
        ResultSet resultSet = new ResultSet(pluginResultSet, metaInfo);
        ProcessParameter parameter = new ProcessParameter();
        ParameterValue parameterValue = new ParameterValue();
        parameterValue.setKey("Regular");
        parameterValue.setValue("SPLIT OperationalStatus AS [{,](2)[},] TO Status");
        parameter.addParameter(parameterValue);
        ColumnSplitProcessor processor = new ColumnSplitProcessor();
        processor.process(resultSet, parameter, null);
        System.out.println(resultSet);


    }

}
