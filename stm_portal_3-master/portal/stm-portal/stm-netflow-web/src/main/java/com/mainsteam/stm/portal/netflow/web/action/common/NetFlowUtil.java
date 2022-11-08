package com.mainsteam.stm.portal.netflow.web.action.common;

public class NetFlowUtil {

	public static String getSortName(String sort) {
		if (sort == null) {
			return "";
		}
		switch (sort) {
		case "in_flows":
		case "flowIn":
			return "流入流量";
		case "out_flows":
		case "flowOut":
			return "流出流量";
		case "total_flows":
		case "flowTotal":
			return "总流量";
		case "in_packages":
		case "packetIn":
			return "流入包数";
		case "out_packages":
		case "packetOut":
			return "流出包数";
		case "total_package":
		case "packetTotal":
			return "总包数";
		case "in_speed":
		case "speedIn":
			return "流入速率";
		case "out_speed":
		case "speedOut":
			return "流出速率";
		case "total_speed":
		case "speedTotal":
			return "总速率";
		case "flows_rate":
		case "flowPctge":
			return "占比";
		}
		return "";
	}
}
