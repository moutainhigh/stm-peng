package com.mainsteam.stm.plugin.sunjes.util;

import java.text.DecimalFormat;
import java.util.Map;

import com.mainsteam.stm.plugin.parameter.JBrokerParameter;

public class CommonUtils {

	public static double getTwoDecimalResult(double d) {
		DecimalFormat format = new DecimalFormat("#.##");
		String dStr = format.format(d);
		return Double.parseDouble(dStr);
	}

	public static double convertByteToMByte(double parseDouble) {
		double d = parseDouble/(1024*1024);
		return getTwoDecimalResult(d);
	}
	
	public static SunJESConnInfo getSunJESConnInfo(JBrokerParameter obj) {
		String ip = obj.getIp();
		int port = obj.getPort();
		String userName = obj.getUsername();
		String password = obj.getPassword();
		String instanceName = obj.getSunjesBo().getInstanceName();
		return new SunJESConnInfo(ip, port, instanceName, userName, password);
	}


	public static SunJESConnInfo getSunJESConnInfo(Map<String, String> params) {
		String ip = params.get("ip");
		int port = Integer.parseInt(params.get("appPort"));
		String userName = params.get("appUser");
		String password = params.get("appPassword");
		String instanceName = params.get("instanceName");
		return new SunJESConnInfo(ip, port, instanceName, userName, password);
	}
}
