package com.mainsteam.stm.plugin.common;

import com.mainsteam.stm.pluginprocessor.PluginResultSetProcessor;
import com.mainsteam.stm.pluginprocessor.ProcessParameter;
import com.mainsteam.stm.pluginprocessor.ResultSet;
import com.mainsteam.stm.pluginsession.PluginSessionContext;
import com.mainsteam.stm.pluginsession.exception.PluginSessionRunException;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * Created by Lich on 2016/12/21.
 */
public class DateFormatProcessor implements PluginResultSetProcessor {

    public static final String COLUMN = "Column";
    public static final String ORIGINAL = "Original";
    public static final String TARGET = "Target";

    private static final Log LOGGER = LogFactory.getLog(DateFormatProcessor.class);

    @Override
    public void process(ResultSet resultSet, ProcessParameter parameter, PluginSessionContext context) throws PluginSessionRunException {
        SimpleDateFormat originalFormat = new SimpleDateFormat(parameter.getParameterValueByKey(ORIGINAL).getValue());
        SimpleDateFormat targetFormat = new SimpleDateFormat(parameter.getParameterValueByKey(TARGET).getValue());
        int column = resultSet.getResultMetaInfo().indexOfColumnName(parameter.getParameterValueByKey(COLUMN).getValue());
        for (int row = 0; row < resultSet.getRowLength(); ++row) {
            String original = resultSet.getValue(row, column);
            if (original != null) {
                try {
                    Date date = originalFormat.parse(original);
                    resultSet.putValue(row, column, targetFormat.format(date));
                } catch (ParseException ignored) {
                }
            }
        }
    }
}
