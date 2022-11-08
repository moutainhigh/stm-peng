package com.mainsteam.stm.portal.netflow.web.action.three;

import java.io.IOException;
import java.lang.reflect.InvocationTargetException;

import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;

import com.mainsteam.stm.export.pdf.PDFHelper;
import com.mainsteam.stm.platform.web.action.BaseAction;
import com.mainsteam.stm.portal.netflow.bo.ApplicationChartDataBo;
import com.mainsteam.stm.portal.netflow.bo.ApplicationPageBo;
import com.mainsteam.stm.portal.netflow.web.action.common.DownUtil;
import com.mainsteam.stm.portal.netflow.web.action.common.NetFlowUtil;
import com.mainsteam.stm.portal.netflow.web.adapter.CircularAdapter;
import com.mainsteam.stm.portal.netflow.web.adapter.LineAdapter;
import com.mainsteam.stm.portal.netflow.web.adapter.TableAdapter;
import com.mainsteam.stm.portal.netflow.web.adapter.TableAdapter.ShowColumn;
import com.mainsteam.stm.portal.netflow.web.adapter.TableAdapter.ShowColumn.ShowColumnType;
import com.mainsteam.stm.util.StringUtil;

@Controller
@RequestMapping("netflow/three/export")
public class AppExportAction {

	public static final String APP_TERMINAL_TABLE_THREE = "APP_TERMINAL_TABLE_THREE";
	public static final String APP_TERMINAL_CHART_THREE = "APP_TERMINAL_CHART_THREE";
	public static final String APP_SESSION_TABLE_THREE = "APP_SESSION_TABLE_THREE";
	public static final String APP_SESSION_CHART_THREE = "APP_SESSION_CHART_THREE";
	public static final String APP_PROTOCOL_TABLE_THREE = "APP_PROTOCOL_TABLE_THREE";
	public static final String APP_IP_GROUP_TABLE_THREE = "APP_IP_GROUP_TABLE_THREE";
	public static final String APP_IP_GROUP_CHART_THREE = "APP_IP_GROUP_CHART_THREE";

	@RequestMapping("app")
	public void app(HttpServletResponse resp, String type) throws IOException,
			NoSuchMethodException, SecurityException, IllegalAccessException,
			IllegalArgumentException, InvocationTargetException {
		HttpSession session = BaseAction.getHttpServletRequest().getSession();
		PDFHelper pdf = new PDFHelper("应用流量");
		// 终端
		ApplicationChartDataBo terminalChart = (ApplicationChartDataBo) session
				.getAttribute(APP_TERMINAL_CHART_THREE);
		ApplicationPageBo terminalBo = (ApplicationPageBo) session
				.getAttribute(APP_TERMINAL_TABLE_THREE);
		pdf.addLine("终端折线图(" + NetFlowUtil.getSortName(terminalBo.getSort())
				+ ")", "时间", "流量(MB)", new LineAdapter().adapter(terminalChart));
		pdf.addTable(new TableAdapter().adapter("终端流量数据",
				getColumnTermainl(type), terminalBo.getRows()));
		// 会话
		ApplicationChartDataBo sessionChart = (ApplicationChartDataBo) session
				.getAttribute(APP_SESSION_CHART_THREE);
		ApplicationPageBo sessionBo = (ApplicationPageBo) session
				.getAttribute(APP_SESSION_TABLE_THREE);
		pdf.addLine("会话折线图(" + NetFlowUtil.getSortName(sessionBo.getSort())
				+ ")", "时间", "流量(MB)", new LineAdapter().adapter(sessionChart));
		pdf.addTable(new TableAdapter().adapter("会话流量数据",
				getColumnSession(type), sessionBo.getRows()));
		// 协议
		ApplicationPageBo protocolBo = (ApplicationPageBo) session
				.getAttribute(APP_PROTOCOL_TABLE_THREE);
		pdf.addCircular(
				"协议环形图(" + NetFlowUtil.getSortName(protocolBo.getSort()) + ")",
				new CircularAdapter().adapter("proto_name",
						protocolBo.getSort(), protocolBo.getRows()));
		pdf.addTable(new TableAdapter().adapter("协议流量数据", getColumnProto(type),
				protocolBo.getRows()));
		// IP分组
		ApplicationChartDataBo ipGroupChart = (ApplicationChartDataBo) session
				.getAttribute(APP_IP_GROUP_CHART_THREE);
		ApplicationPageBo ipGroupBo = (ApplicationPageBo) session
				.getAttribute(APP_IP_GROUP_TABLE_THREE);
		pdf.addLine(
				"IP分组折线图("
						+ NetFlowUtil.getSortName(StringUtil.isNull(ipGroupBo
								.getSort()) ? ipGroupBo.getAppBo().getSort()
								: ipGroupBo.getSort()) + ")", "时间", "流量(MB)",
				new LineAdapter().adapter(ipGroupChart));
		pdf.addTable(new TableAdapter().adapter("IP分组流量数据",
				getColumnIpGroup(type), ipGroupBo.getRows()));
		DownUtil.down(resp, "terminal.pdf", pdf.generate());
	}

	private ShowColumn[] getColumnSession(String type) {
		switch (type) {
		case "1":
			ShowColumn[] data1 = {
					new ShowColumn("src_ip", "源IP", ShowColumnType.OTHER),
					new ShowColumn("dst_ip", "目的IP", ShowColumnType.OTHER),
					new ShowColumn("in_flows", "流入流量", ShowColumnType.FLOW),
					new ShowColumn("out_flows", "流出流量", ShowColumnType.FLOW),
					new ShowColumn("total_flows", "总流量", ShowColumnType.FLOW),
					new ShowColumn("in_speed", "流入速率", ShowColumnType.FLOW_RATE),
					new ShowColumn("out_speed", "流出速率",
							ShowColumnType.FLOW_RATE),
					new ShowColumn("total_speed", "总速率",
							ShowColumnType.FLOW_RATE),
					new ShowColumn("flowPctge", "占比", ShowColumnType.PERCENTAGE) };
			return data1;
		case "2":
			ShowColumn[] data2 = {
					new ShowColumn("src_ip", "源IP", ShowColumnType.OTHER),
					new ShowColumn("dst_ip", "目的IP", ShowColumnType.OTHER),
					new ShowColumn("in_packages", "流入包数", ShowColumnType.PACKET),
					new ShowColumn("out_packages", "流出包数",
							ShowColumnType.PACKET),
					new ShowColumn("total_packages", "总包数",
							ShowColumnType.PACKET),
					new ShowColumn("packetInSpeed", "流入速率",
							ShowColumnType.PACKET_ROTE),
					new ShowColumn("packetOutSpeed", "流出速率",
							ShowColumnType.PACKET_ROTE),
					new ShowColumn("packetTotalSpeed", "总速率",
							ShowColumnType.PACKET_ROTE),
					new ShowColumn("packetPctge", "占比",
							ShowColumnType.PERCENTAGE) };
			return data2;
		case "3":
			ShowColumn[] data3 = {
					new ShowColumn("src_ip", "源IP", ShowColumnType.OTHER),
					new ShowColumn("dst_ip", "目的IP", ShowColumnType.OTHER),
					new ShowColumn("connectNumberIn", "流入连接数",
							ShowColumnType.CONNECT),
					new ShowColumn("connectNumberOut", "流出连接数",
							ShowColumnType.CONNECT),
					new ShowColumn("connectNumberTotal", "总连接数",
							ShowColumnType.CONNECT),
					new ShowColumn("connectNumberInSpeed", "流入速率",
							ShowColumnType.CONNECT_ROTE),
					new ShowColumn("connectNumberOutSpeed", "流出速率",
							ShowColumnType.CONNECT_ROTE),
					new ShowColumn("connectNumberTotalSpeed", "总速率",
							ShowColumnType.CONNECT_ROTE),
					new ShowColumn("connectPctge", "占比",
							ShowColumnType.PERCENTAGE) };
			return data3;
		}
		return null;
	}

	private ShowColumn[] getColumnProto(String type) {
		switch (type) {
		case "1":
			ShowColumn[] data1 = {
					new ShowColumn("proto_name", "协议名称", ShowColumnType.OTHER),
					new ShowColumn("in_flows", "流入流量", ShowColumnType.FLOW),
					new ShowColumn("out_flows", "流出流量", ShowColumnType.FLOW),
					new ShowColumn("total_flows", "总流量", ShowColumnType.FLOW),
					new ShowColumn("in_speed", "流入速率", ShowColumnType.FLOW_RATE),
					new ShowColumn("out_speed", "流出速率",
							ShowColumnType.FLOW_RATE),
					new ShowColumn("total_speed", "总速率",
							ShowColumnType.FLOW_RATE),
					new ShowColumn("flowPctge", "占比", ShowColumnType.PERCENTAGE) };
			return data1;
		case "2":
			ShowColumn[] data2 = {
					new ShowColumn("proto_name", "协议名称", ShowColumnType.OTHER),
					new ShowColumn("in_packages", "流入包数", ShowColumnType.PACKET),
					new ShowColumn("out_packages", "流出包数",
							ShowColumnType.PACKET),
					new ShowColumn("total_packages", "总包数",
							ShowColumnType.PACKET),
					new ShowColumn("packetInSpeed", "流入速率",
							ShowColumnType.PACKET_ROTE),
					new ShowColumn("packetOutSpeed", "流出速率",
							ShowColumnType.PACKET_ROTE),
					new ShowColumn("packetTotalSpeed", "总速率",
							ShowColumnType.PACKET_ROTE),
					new ShowColumn("packetPctge", "占比",
							ShowColumnType.PERCENTAGE) };
			return data2;
		case "3":
			ShowColumn[] data3 = {
					new ShowColumn("proto_name", "协议名称", ShowColumnType.OTHER),
					new ShowColumn("connectNumberIn", "流入连接数",
							ShowColumnType.CONNECT),
					new ShowColumn("connectNumberOut", "流出连接数",
							ShowColumnType.CONNECT),
					new ShowColumn("connectNumberTotal", "总连接数",
							ShowColumnType.CONNECT),
					new ShowColumn("connectNumberInSpeed", "流入速率",
							ShowColumnType.CONNECT_ROTE),
					new ShowColumn("connectNumberOutSpeed", "流出速率",
							ShowColumnType.CONNECT_ROTE),
					new ShowColumn("connectNumberTotalSpeed", "总速率",
							ShowColumnType.CONNECT_ROTE),
					new ShowColumn("connectPctge", "占比",
							ShowColumnType.PERCENTAGE) };
			return data3;
		}
		return null;
	}

	private ShowColumn[] getColumnTermainl(String type) {
		switch (type) {
		case "1":
			ShowColumn[] data1 = {
					new ShowColumn("terminal_name", "终端名称",
							ShowColumnType.OTHER),
					new ShowColumn("in_flows", "流入流量", ShowColumnType.FLOW),
					new ShowColumn("out_flows", "流出流量", ShowColumnType.FLOW),
					new ShowColumn("total_flows", "总流量", ShowColumnType.FLOW),
					new ShowColumn("in_speed", "流入速率", ShowColumnType.FLOW_RATE),
					new ShowColumn("out_speed", "流出速率",
							ShowColumnType.FLOW_RATE),
					new ShowColumn("total_speed", "总速率",
							ShowColumnType.FLOW_RATE),
					new ShowColumn("flowPctge", "占比", ShowColumnType.PERCENTAGE) };
			return data1;
		case "2":
			ShowColumn[] data2 = {
					new ShowColumn("terminal_name", "终端名称",
							ShowColumnType.OTHER),
					new ShowColumn("in_packages", "流入包数", ShowColumnType.PACKET),
					new ShowColumn("out_packages", "流出包数",
							ShowColumnType.PACKET),
					new ShowColumn("total_packages", "总包数",
							ShowColumnType.PACKET),
					new ShowColumn("packetInSpeed", "流入速率",
							ShowColumnType.PACKET_ROTE),
					new ShowColumn("packetOutSpeed", "流出速率",
							ShowColumnType.PACKET_ROTE),
					new ShowColumn("packetTotalSpeed", "总速率",
							ShowColumnType.PACKET_ROTE),
					new ShowColumn("packetPctge", "占比",
							ShowColumnType.PERCENTAGE) };
			return data2;
		case "3":
			ShowColumn[] data3 = {
					new ShowColumn("terminal_name", "终端名称",
							ShowColumnType.OTHER),
					new ShowColumn("connectNumberIn", "流入连接数",
							ShowColumnType.CONNECT),
					new ShowColumn("connectNumberOut", "流出连接数",
							ShowColumnType.CONNECT),
					new ShowColumn("connectNumberTotal", "总连接数",
							ShowColumnType.CONNECT),
					new ShowColumn("connectNumberInSpeed", "流入速率",
							ShowColumnType.CONNECT_ROTE),
					new ShowColumn("connectNumberOutSpeed", "流出速率",
							ShowColumnType.CONNECT_ROTE),
					new ShowColumn("connectNumberTotalSpeed", "总速率",
							ShowColumnType.CONNECT_ROTE),
					new ShowColumn("connectPctge", "占比",
							ShowColumnType.PERCENTAGE) };
			return data3;
		}
		return null;
	}

	private ShowColumn[] getColumnIpGroup(String type) {
		switch (type) {
		case "1":
			ShowColumn[] data1 = {
					new ShowColumn("ipgroup_name", "IP分组名称",
							ShowColumnType.OTHER),
					new ShowColumn("in_flows", "流入流量", ShowColumnType.FLOW),
					new ShowColumn("out_flows", "流出流量", ShowColumnType.FLOW),
					new ShowColumn("total_flows", "总流量", ShowColumnType.FLOW),
					new ShowColumn("in_speed", "流入速率", ShowColumnType.FLOW_RATE),
					new ShowColumn("out_speed", "流出速率",
							ShowColumnType.FLOW_RATE),
					new ShowColumn("total_speed", "总速率",
							ShowColumnType.FLOW_RATE),
					new ShowColumn("flowPctge", "占比", ShowColumnType.PERCENTAGE) };
			return data1;
		case "2":
			ShowColumn[] data2 = {
					new ShowColumn("ipgroup_name", "IP分组名称",
							ShowColumnType.OTHER),
					new ShowColumn("in_packages", "流入包数", ShowColumnType.PACKET),
					new ShowColumn("out_packages", "流出包数",
							ShowColumnType.PACKET),
					new ShowColumn("total_packages", "总包数",
							ShowColumnType.PACKET),
					new ShowColumn("packetInSpeed", "流入速率",
							ShowColumnType.PACKET_ROTE),
					new ShowColumn("packetOutSpeed", "流出速率",
							ShowColumnType.PACKET_ROTE),
					new ShowColumn("packetTotalSpeed", "总速率",
							ShowColumnType.PACKET_ROTE),
					new ShowColumn("packetPctge", "占比",
							ShowColumnType.PERCENTAGE) };
			return data2;
		case "3":
			ShowColumn[] data3 = {
					new ShowColumn("ipgroup_name", "IP分组名称",
							ShowColumnType.OTHER),
					new ShowColumn("connectNumberIn", "流入连接数",
							ShowColumnType.CONNECT),
					new ShowColumn("connectNumberOut", "流出连接数",
							ShowColumnType.CONNECT),
					new ShowColumn("connectNumberTotal", "总连接数",
							ShowColumnType.CONNECT),
					new ShowColumn("connectNumberInSpeed", "流入速率",
							ShowColumnType.CONNECT_ROTE),
					new ShowColumn("connectNumberOutSpeed", "流出速率",
							ShowColumnType.CONNECT_ROTE),
					new ShowColumn("connectNumberTotalSpeed", "总速率",
							ShowColumnType.CONNECT_ROTE),
					new ShowColumn("connectPctge", "占比",
							ShowColumnType.PERCENTAGE) };
			return data3;
		}
		return null;
	}
}
