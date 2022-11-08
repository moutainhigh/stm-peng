package com.mainsteam.stm.plugin.common;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.apache.commons.lang.StringUtils;

import com.mainsteam.stm.pluginprocessor.ParameterValue;
import com.mainsteam.stm.pluginprocessor.PluginResultSetProcessor;
import com.mainsteam.stm.pluginprocessor.ProcessParameter;
import com.mainsteam.stm.pluginprocessor.ResultSet;
import com.mainsteam.stm.pluginsession.PluginSessionContext;
import com.mainsteam.stm.pluginsession.exception.PluginSessionRunException;

public class ApacheProcessor implements PluginResultSetProcessor {
	private static String TOTAL_TRAFFIC = "Totalttraffic";
	private static String REQUEST_SEC = "requestsSec";

	@Override
	public void process(ResultSet paramResultSet,
			ProcessParameter paramProcessParameter,
			PluginSessionContext paramPluginSessionContext)
			throws PluginSessionRunException {
		if (null != paramResultSet) {
			ParameterValue traffic = paramProcessParameter
					.getParameterValueByKey(TOTAL_TRAFFIC);
			ParameterValue reqSec = paramProcessParameter
					.getParameterValueByKey(REQUEST_SEC);
			String requestSec[] = paramResultSet.getColumn(0);
			String req = requestSec[0];
			String proReq = StringUtils.trimToEmpty(req);
			if (null != traffic
					&& StringUtils.containsIgnoreCase(traffic.getKey(),
							TOTAL_TRAFFIC)) {
				String ps = "[a-zA-Z]+";
				String reg = "\\d+\\S\\d*";
				Pattern pattern1 = Pattern.compile(ps);
				Pattern pattern2 = Pattern.compile(reg);
				Matcher matcher1 = pattern1.matcher(proReq);
				Matcher matcher2 = pattern2.matcher(proReq);
				if (matcher1.find() && matcher2.find()) {
					String unit = matcher1.group();
					Double amount = Double.valueOf(matcher2.group());
					if (StringUtils.equalsIgnoreCase(unit, "GB")) {
						amount = amount * 1024;
						paramResultSet.putValue(0, 0, String.valueOf(amount));
					}
					else {
						paramResultSet.putValue(0, 0, String.valueOf(amount));
					}
				}
			}
			if (null != reqSec
					&& StringUtils.containsIgnoreCase(reqSec.getKey(),
							REQUEST_SEC)) {
				String result = StringUtils.substringBeforeLast(req, ".");
				if (StringUtils.isBlank(result)) {
					proReq = 0 + proReq;
					paramResultSet.putValue(0, 0, proReq);
				}
			}

		}
	}

}
