package com.mainsteam.stm.portal.netflow.web.action.two;

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
import com.mainsteam.stm.portal.netflow.web.action.three.AppExportAction;
import com.mainsteam.stm.portal.netflow.web.adapter.CircularAdapter;
import com.mainsteam.stm.portal.netflow.web.adapter.LineAdapter;
import com.mainsteam.stm.portal.netflow.web.adapter.TableAdapter;
import com.mainsteam.stm.portal.netflow.web.adapter.TableAdapter.ShowColumn;
import com.mainsteam.stm.portal.netflow.web.adapter.TableAdapter.ShowColumn.ShowColumnType;

@Controller
@RequestMapping("netflow/two/export")
public class TwoAppExportAction {

	@RequestMapping("app")
	public void session(HttpServletResponse resp, String type)
			throws IOException, NoSuchMethodException, SecurityException,
			IllegalAccessException, IllegalArgumentException,
			InvocationTargetException {
		HttpSession session = BaseAction.getHttpServletRequest().getSession();
		PDFHelper pdf = new PDFHelper("应用流量");
		// 终端
		ApplicationChartDataBo terminalChart = (ApplicationChartDataBo) session
				.getAttribute(AppExportAction.APP_TERMINAL_CHART_THREE);
		pdf.addLine(
				"终端折线图("
						+ NetFlowUtil.getSortName(terminalChart.getSortColum())
						+ ")", "时间", "流量(MB)",
				new LineAdapter().adapter(terminalChart));
		ApplicationPageBo terminalBo = (ApplicationPageBo) session
				.getAttribute(AppExportAction.APP_TERMINAL_TABLE_THREE);
		pdf.addTable(new TableAdapter().adapter("终端流量数据",
				getColumnTerminal(type), terminalBo.getRows()));
		// 会话
		ApplicationChartDataBo sessionChart = (ApplicationChartDataBo) session
				.getAttribute(AppExportAction.APP_SESSION_CHART_THREE);
		pdf.addLine(
				"会话折线图(" + NetFlowUtil.getSortName(sessionChart.getSortColum())
						+ ")", "时间", "流量(MB)",
				new LineAdapter().adapter(sessionChart));
		ApplicationPageBo sessionBo = (ApplicationPageBo) session
				.getAttribute(AppExportAction.APP_SESSION_TABLE_THREE);
		pdf.addTable(new TableAdapter().adapter("会话流量数据",
				getColumnSession(type), sessionBo.getRows()));
		// IP分组
		ApplicationChartDataBo ipGroupChart = (ApplicationChartDataBo) session
				.getAttribute(AppExportAction.APP_IP_GROUP_CHART_THREE);
		pdf.addLine(
				"IP分组折线图("
						+ NetFlowUtil.getSortName(ipGroupChart.getSortColum())
						+ ")", "时间", "流量(MB)",
				new LineAdapter().adapter(ipGroupChart));
		ApplicationPageBo ipGroupBo = (ApplicationPageBo) session
				.getAttribute(AppExportAction.APP_IP_GROUP_TABLE_THREE);
		pdf.addTable(new TableAdapter().adapter("IP分组流量数据",
				getColumnIpGroup(type), ipGroupBo.getRows()));
		// 协议
		ApplicationPageBo protocolBo = (ApplicationPageBo) session
				.getAttribute(AppExportAction.APP_PROTOCOL_TABLE_THREE);
		pdf.addCircular(
				"协议环形图(" + NetFlowUtil.getSortName(protocolBo.getSort()) + ")",
				new CircularAdapter().adapter("proto_name",
						protocolBo.getSort(), protocolBo.getRows()));
		pdf.addTable(new TableAdapter().adapter("协议流量数据", getColumnProto(type),
				protocolBo.getRows()));
		DownUtil.down(resp, "app.pdf", pdf.generate());
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

	private ShowColumn[] getColumnIpGroup(String type) {
		switch (type) {
		case "1":
			ShowColumn[] data1 = {
					new ShowColumn("ipgroup_name", "ip分组名称",
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
					new ShowColumn("ipgroup_name", "ip分组名称",
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
					new ShowColumn("ipgroup_name", "ip分组名称",
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

	private ShowColumn[] getColumnTerminal(String type) {
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
}
