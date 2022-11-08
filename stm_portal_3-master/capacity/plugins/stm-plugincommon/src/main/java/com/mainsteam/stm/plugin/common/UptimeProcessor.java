package com.mainsteam.stm.plugin.common;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.apache.commons.lang.StringUtils;

import com.mainsteam.stm.pluginprocessor.PluginResultSetProcessor;
import com.mainsteam.stm.pluginprocessor.ProcessParameter;
import com.mainsteam.stm.pluginprocessor.ResultSet;
import com.mainsteam.stm.pluginsession.PluginSessionContext;

public class UptimeProcessor implements PluginResultSetProcessor {

	@Override
	public void process(ResultSet resultSet, ProcessParameter parameter,
			PluginSessionContext context) {
		if (null == resultSet || resultSet.getColumnLength() < 1) {
			return;
		}
		String value = "";
		String[] values = resultSet.getColumn(resultSet.getColumnLength() - 1);
		if (null != values && values.length > 0) {
			value = values[0];
		}
		resultSet.clearRows();
		// 32 days, 23:58
		value = StringUtils.replace(value, " ", "");
		long seconds = 0;

		Pattern pdhm = Pattern.compile("(\\d+)\\s*day\\S*,\\s*(\\d+)[:](\\d+)");
		
		Matcher mdhm = pdhm.matcher(value);
		
		while (mdhm.find()) {
			long days = Long.valueOf(mdhm.group(1));
			long hours = Long.valueOf(mdhm.group(2));
			long min = Long.valueOf(mdhm.group(3));
			seconds = days * 86400 + hours * 3600 + min * 60;
			break;
		}
		// 换第二种方法匹配
		if (seconds == 0) {
			Pattern pm = Pattern.compile("(\\d+)\\s*min");
			Matcher mm = pm.matcher(value);
			while (mm.find()) {
				int min = Integer.valueOf(mm.group(1));
				seconds = min * 60;
				break;
			}
		}
		if (seconds == 0) {
			Pattern phm = Pattern.compile("(\\d+)[:](\\d+)");
			Matcher mhm = phm.matcher(value);
			while (mhm.find()) {
				int hour = Integer.valueOf(mhm.group(1));
				int min = Integer.valueOf(mhm.group(2));
				seconds = hour * 3600 + min * 60;
				break;
			}
		}
	
		resultSet.putValue(0, 0, String.valueOf(seconds));
		return;
	}

	public static void main(String[] argv) {

		{
			Pattern p = Pattern
					.compile("up\\s*([^,]+[,]*[^,]*),\\s*\\d+\\s*user");
			Matcher m = p
					.matcher(" 05:21:23 up 32 days, 23:58,  2 users,  load average: 0.09, 0.10, 0.06");
			String myStr = "";
			while (m.find()) {
				myStr = m.group(1);
				System.out.println(myStr);
			}
			Pattern pMy = Pattern.compile("(\\d+)\\s*days,\\s*(\\d+)[:](\\d+)");
			Matcher mMy = pMy.matcher(myStr);
			long seconds = 0;
			while (mMy.find()) {
				System.out.println("days:" + mMy.group(1) + ",hours:"
						+ mMy.group(2) + ",mins" + mMy.group(3));
				int days = Integer.valueOf(mMy.group(1));
				int hours = Integer.valueOf(mMy.group(2));
				int min = Integer.valueOf(mMy.group(3));
				seconds = days * 86400 + hours * 3600 + min * 60;
				break;
			}
			System.out.println(String.valueOf(seconds));
		}
		//自己添加---------------------------------------------------
		{
			Pattern p = Pattern
					.compile("up\\s*([^,]+[,]*[^,]*),\\s*\\d+\\s*user");
			Matcher m = p
					.matcher(" 05:21:23 up 1 day(s), 20:48,  2 users,  load average: 0.09, 0.10, 0.06");
			String myStr = "";
			while (m.find()) {
				myStr = m.group(1);
			}
			Pattern pMy = Pattern.compile("(\\d+)\\s*day\\S*,\\s*(\\d+)[:](\\d+)");
			Matcher mMy = pMy.matcher(myStr);
			long seconds = 0;
			while (mMy.find()) {
				System.out.println("day(s):" + mMy.group(1) + ",hour(s):"
						+ mMy.group(2) + ",min(s)" + mMy.group(3));
				int days = Integer.valueOf(mMy.group(1));
				int hours = Integer.valueOf(mMy.group(2));
				int min = Integer.valueOf(mMy.group(3));
				seconds = days * 86400 + hours * 3600 + min * 60;
				break;
			}
			System.out.println(String.valueOf("day(s)="+seconds));
		}
		//自己添加---------------------------------------------------
		
		{
			Pattern p = Pattern
					.compile("up\\s*([^,]+[,]*[^,]*),\\s*\\d+\\s*user");
			Matcher m = p
					.matcher(" 07:07:39 up 18 min,  3 users,  load average: 0.14, 0.54, 0.33");
			String myStr = "";
			while (m.find()) {
				myStr = m.group(1);
			}

			Pattern pMy = Pattern.compile("(\\d+)\\s*min");
			Matcher mMy = pMy.matcher(myStr);
			long seconds = 0;
			while (mMy.find()) {
				int min = Integer.valueOf(mMy.group(1));
				seconds = min * 60;
				break;
			}
			System.out.println(String.valueOf(seconds));
		}

		// "12:12:58 up 20:29,  1 user,  load average: 0.00, 0.00, 0.00"
		{
			Pattern p = Pattern
					.compile("up\\s*([^,]+[,]*[^,]*),\\s*\\d+\\s*user");
			Matcher m = p
					.matcher(" 12:12:58 up 20:29,  1 user,  load average: 0.00, 0.00, 0.00");
			String myStr = "";
			while (m.find()) {
				myStr = m.group(1);
				System.out.println(myStr);
			}

			Pattern pMy = Pattern.compile("(\\d+)[:](\\d+)");
			Matcher mMy = pMy.matcher(myStr);
			long seconds = 0;
			while (mMy.find()) {
				System.out.println("hour:" + mMy.group(1) + ",mins:"
						+ mMy.group(2));
				int hour = Integer.valueOf(mMy.group(1));
				int min = Integer.valueOf(mMy.group(2));
				seconds = hour * 3600 + min * 60;
				break;
			}
			System.out.println(String.valueOf(seconds));
		}
	}

}
