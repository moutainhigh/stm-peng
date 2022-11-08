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
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * <p>This processor deals with the time part of "ls" command result in the Unix-like system.</p>
 * <p>Supported Time format till now:</p>
 * <ul>
 * <li>MMM d HH:mm -- May 25 10:09</li>
 * <li>MMM d yyyy -- Apr 1 2014</li>
 * <li>MMM d HH:mm:ss yyyy -- Apr 18 13:42:05 2014</li>
 * </ul>
 *
 * @author lich
 * @version 4.2.0
 * @since 4.2.0
 */
public class HostTimeProcessor implements PluginResultSetProcessor {

    /**
     * the column name to process
     */
    public static final String COLUMN_NAME = "ColumnName";

    private static final Pattern TIME_PATTERN = Pattern.compile("\\S+\\s+\\d{1,2}\\s+\\d{2}:\\d{2}");
    private static final String TIME_FORMAT = "MMM d HH:mm yyyy";
    private static final Pattern YEAR_PATTERN = Pattern.compile("\\S+\\s+\\d{1,2}\\s+\\d{4}");
    private static final String YEAR_FORMAT = "MMM d yyyy";
    private static final Pattern FULL_PATTERN = Pattern.compile("\\S+\\s+\\d{1,2}\\s+\\d{2}:\\d{2}:\\d{2}\\s+\\d{4}");
    private static final String FULL_FORMAT = "MMM d HH:mm:ss yyyy";
    private static final String REGULAR_FORMAT = "yyyy-MM-dd HH:mm:ss";
    private static final Log LOGGER = LogFactory.getLog(HostTimeProcessor.class);

    @Override
    public void process(ResultSet resultSet, ProcessParameter parameter,
                        PluginSessionContext context) throws PluginSessionRunException {
        LOGGER.debug("process entry.");
        String columnName = parameter.getParameterValueByKey(COLUMN_NAME).getValue();
        int columnIndex = resultSet.getResultMetaInfo().indexOfColumnName(columnName);
        SimpleDateFormat regularFormat = new SimpleDateFormat(REGULAR_FORMAT, Locale.ENGLISH);
        SimpleDateFormat fullFormat = new SimpleDateFormat(FULL_FORMAT, Locale.ENGLISH);
        SimpleDateFormat yearFormat = new SimpleDateFormat(YEAR_FORMAT, Locale.ENGLISH);
        SimpleDateFormat timeFormat = new SimpleDateFormat(TIME_FORMAT, Locale.ENGLISH);
        for (int row = 0; row < resultSet.getRowLength(); ++row) {
            String value = resultSet.getValue(row, columnIndex);
            Date date = null;
            if (LOGGER.isDebugEnabled())
                LOGGER.debug("deal with value:" + value);
            Matcher matcher = FULL_PATTERN.matcher(value);
            if (LOGGER.isDebugEnabled())
                LOGGER.debug("deal with value:" + value + ", try " + FULL_PATTERN);
            if (matcher.find()) {
                try {
                    date = fullFormat.parse(matcher.group(0));
                } catch (ParseException ignored) {
                }
            }
            matcher = YEAR_PATTERN.matcher(value);
            if (LOGGER.isDebugEnabled())
                LOGGER.debug("deal with value:" + value + ", try " + YEAR_PATTERN);
            if (date == null && matcher.find()) {
                try {
                    date = yearFormat.parse(matcher.group(0));
                } catch (ParseException ignored) {
                }
            }
            matcher = TIME_PATTERN.matcher(value);
            if (LOGGER.isDebugEnabled())
                LOGGER.debug("deal with value:" + value + ", try " + TIME_PATTERN);
            if (date == null && matcher.find()) {
                try {
                    date = timeFormat.parse(matcher.group(0) + " " + Calendar.getInstance().get(Calendar.YEAR));
                    if (date.after(new Date())) {
                        date = timeFormat.parse(matcher.group(0) + " " + (Calendar.getInstance().get(Calendar.YEAR) - 1));
                    }
                } catch (ParseException ignored) {
                }
            }
            if (date != null) {
                resultSet.putValue(row, columnIndex, regularFormat.format(date));
            }
        }
    }
}
