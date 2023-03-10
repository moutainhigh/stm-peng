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
		PDFHelper pdf = new PDFHelper("????????????");
		// ??????
		ApplicationChartDataBo terminalChart = (ApplicationChartDataBo) session
				.getAttribute(APP_TERMINAL_CHART_THREE);
		ApplicationPageBo terminalBo = (ApplicationPageBo) session
				.getAttribute(APP_TERMINAL_TABLE_THREE);
		pdf.addLine("???????????????(" + NetFlowUtil.getSortName(terminalBo.getSort())
				+ ")", "??????", "??????(MB)", new LineAdapter().adapter(terminalChart));
		pdf.addTable(new TableAdapter().adapter("??????????????????",
				getColumnTermainl(type), terminalBo.getRows()));
		// ??????
		ApplicationChartDataBo sessionChart = (ApplicationChartDataBo) session
				.getAttribute(APP_SESSION_CHART_THREE);
		ApplicationPageBo sessionBo = (ApplicationPageBo) session
				.getAttribute(APP_SESSION_TABLE_THREE);
		pdf.addLine("???????????????(" + NetFlowUtil.getSortName(sessionBo.getSort())
				+ ")", "??????", "??????(MB)", new LineAdapter().adapter(sessionChart));
		pdf.addTable(new TableAdapter().adapter("??????????????????",
				getColumnSession(type), sessionBo.getRows()));
		// ??????
		ApplicationPageBo protocolBo = (ApplicationPageBo) session
				.getAttribute(APP_PROTOCOL_TABLE_THREE);
		pdf.addCircular(
				"???????????????(" + NetFlowUtil.getSortName(protocolBo.getSort()) + ")",
				new CircularAdapter().adapter("proto_name",
						protocolBo.getSort(), protocolBo.getRows()));
		pdf.addTable(new TableAdapter().adapter("??????????????????", getColumnProto(type),
				protocolBo.getRows()));
		// IP??????
		ApplicationChartDataBo ipGroupChart = (ApplicationChartDataBo) session
				.getAttribute(APP_IP_GROUP_CHART_THREE);
		ApplicationPageBo ipGroupBo = (ApplicationPageBo) session
				.getAttribute(APP_IP_GROUP_TABLE_THREE);
		pdf.addLine(
				"IP???????????????("
						+ NetFlowUtil.getSortName(StringUtil.isNull(ipGroupBo
								.getSort()) ? ipGroupBo.getAppBo().getSort()
								: ipGroupBo.getSort()) + ")", "??????", "??????(MB)",
				new LineAdapter().adapter(ipGroupChart));
		pdf.addTable(new TableAdapter().adapter("IP??????????????????",
				getColumnIpGroup(type), ipGroupBo.getRows()));
		DownUtil.down(resp, "terminal.pdf", pdf.generate());
	}

	private ShowColumn[] getColumnSession(String type) {
		switch (type) {
		case "1":
			ShowColumn[] data1 = {
					new ShowColumn("src_ip", "???IP", ShowColumnType.OTHER),
					new ShowColumn("dst_ip", "??????IP", ShowColumnType.OTHER),
					new ShowColumn("in_flows", "????????????", ShowColumnType.FLOW),
					new ShowColumn("out_flows", "????????????", ShowColumnType.FLOW),
					new ShowColumn("total_flows", "?????????", ShowColumnType.FLOW),
					new ShowColumn("in_speed", "????????????", ShowColumnType.FLOW_RATE),
					new ShowColumn("out_speed", "????????????",
							ShowColumnType.FLOW_RATE),
					new ShowColumn("total_speed", "?????????",
							ShowColumnType.FLOW_RATE),
					new ShowColumn("flowPctge", "??????", ShowColumnType.PERCENTAGE) };
			return data1;
		case "2":
			ShowColumn[] data2 = {
					new ShowColumn("src_ip", "???IP", ShowColumnType.OTHER),
					new ShowColumn("dst_ip", "??????IP", ShowColumnType.OTHER),
					new ShowColumn("in_packages", "????????????", ShowColumnType.PACKET),
					new ShowColumn("out_packages", "????????????",
							ShowColumnType.PACKET),
					new ShowColumn("total_packages", "?????????",
							ShowColumnType.PACKET),
					new ShowColumn("packetInSpeed", "????????????",
							ShowColumnType.PACKET_ROTE),
					new ShowColumn("packetOutSpeed", "????????????",
							ShowColumnType.PACKET_ROTE),
					new ShowColumn("packetTotalSpeed", "?????????",
							ShowColumnType.PACKET_ROTE),
					new ShowColumn("packetPctge", "??????",
							ShowColumnType.PERCENTAGE) };
			return data2;
		case "3":
			ShowColumn[] data3 = {
					new ShowColumn("src_ip", "???IP", ShowColumnType.OTHER),
					new ShowColumn("dst_ip", "??????IP", ShowColumnType.OTHER),
					new ShowColumn("connectNumberIn", "???????????????",
							ShowColumnType.CONNECT),
					new ShowColumn("connectNumberOut", "???????????????",
							ShowColumnType.CONNECT),
					new ShowColumn("connectNumberTotal", "????????????",
							ShowColumnType.CONNECT),
					new ShowColumn("connectNumberInSpeed", "????????????",
							ShowColumnType.CONNECT_ROTE),
					new ShowColumn("connectNumberOutSpeed", "????????????",
							ShowColumnType.CONNECT_ROTE),
					new ShowColumn("connectNumberTotalSpeed", "?????????",
							ShowColumnType.CONNECT_ROTE),
					new ShowColumn("connectPctge", "??????",
							ShowColumnType.PERCENTAGE) };
			return data3;
		}
		return null;
	}

	private ShowColumn[] getColumnProto(String type) {
		switch (type) {
		case "1":
			ShowColumn[] data1 = {
					new ShowColumn("proto_name", "????????????", ShowColumnType.OTHER),
					new ShowColumn("in_flows", "????????????", ShowColumnType.FLOW),
					new ShowColumn("out_flows", "????????????", ShowColumnType.FLOW),
					new ShowColumn("total_flows", "?????????", ShowColumnType.FLOW),
					new ShowColumn("in_speed", "????????????", ShowColumnType.FLOW_RATE),
					new ShowColumn("out_speed", "????????????",
							ShowColumnType.FLOW_RATE),
					new ShowColumn("total_speed", "?????????",
							ShowColumnType.FLOW_RATE),
					new ShowColumn("flowPctge", "??????", ShowColumnType.PERCENTAGE) };
			return data1;
		case "2":
			ShowColumn[] data2 = {
					new ShowColumn("proto_name", "????????????", ShowColumnType.OTHER),
					new ShowColumn("in_packages", "????????????", ShowColumnType.PACKET),
					new ShowColumn("out_packages", "????????????",
							ShowColumnType.PACKET),
					new ShowColumn("total_packages", "?????????",
							ShowColumnType.PACKET),
					new ShowColumn("packetInSpeed", "????????????",
							ShowColumnType.PACKET_ROTE),
					new ShowColumn("packetOutSpeed", "????????????",
							ShowColumnType.PACKET_ROTE),
					new ShowColumn("packetTotalSpeed", "?????????",
							ShowColumnType.PACKET_ROTE),
					new ShowColumn("packetPctge", "??????",
							ShowColumnType.PERCENTAGE) };
			return data2;
		case "3":
			ShowColumn[] data3 = {
					new ShowColumn("proto_name", "????????????", ShowColumnType.OTHER),
					new ShowColumn("connectNumberIn", "???????????????",
							ShowColumnType.CONNECT),
					new ShowColumn("connectNumberOut", "???????????????",
							ShowColumnType.CONNECT),
					new ShowColumn("connectNumberTotal", "????????????",
							ShowColumnType.CONNECT),
					new ShowColumn("connectNumberInSpeed", "????????????",
							ShowColumnType.CONNECT_ROTE),
					new ShowColumn("connectNumberOutSpeed", "????????????",
							ShowColumnType.CONNECT_ROTE),
					new ShowColumn("connectNumberTotalSpeed", "?????????",
							ShowColumnType.CONNECT_ROTE),
					new ShowColumn("connectPctge", "??????",
							ShowColumnType.PERCENTAGE) };
			return data3;
		}
		return null;
	}

	private ShowColumn[] getColumnTermainl(String type) {
		switch (type) {
		case "1":
			ShowColumn[] data1 = {
					new ShowColumn("terminal_name", "????????????",
							ShowColumnType.OTHER),
					new ShowColumn("in_flows", "????????????", ShowColumnType.FLOW),
					new ShowColumn("out_flows", "????????????", ShowColumnType.FLOW),
					new ShowColumn("total_flows", "?????????", ShowColumnType.FLOW),
					new ShowColumn("in_speed", "????????????", ShowColumnType.FLOW_RATE),
					new ShowColumn("out_speed", "????????????",
							ShowColumnType.FLOW_RATE),
					new ShowColumn("total_speed", "?????????",
							ShowColumnType.FLOW_RATE),
					new ShowColumn("flowPctge", "??????", ShowColumnType.PERCENTAGE) };
			return data1;
		case "2":
			ShowColumn[] data2 = {
					new ShowColumn("terminal_name", "????????????",
							ShowColumnType.OTHER),
					new ShowColumn("in_packages", "????????????", ShowColumnType.PACKET),
					new ShowColumn("out_packages", "????????????",
							ShowColumnType.PACKET),
					new ShowColumn("total_packages", "?????????",
							ShowColumnType.PACKET),
					new ShowColumn("packetInSpeed", "????????????",
							ShowColumnType.PACKET_ROTE),
					new ShowColumn("packetOutSpeed", "????????????",
							ShowColumnType.PACKET_ROTE),
					new ShowColumn("packetTotalSpeed", "?????????",
							ShowColumnType.PACKET_ROTE),
					new ShowColumn("packetPctge", "??????",
							ShowColumnType.PERCENTAGE) };
			return data2;
		case "3":
			ShowColumn[] data3 = {
					new ShowColumn("terminal_name", "????????????",
							ShowColumnType.OTHER),
					new ShowColumn("connectNumberIn", "???????????????",
							ShowColumnType.CONNECT),
					new ShowColumn("connectNumberOut", "???????????????",
							ShowColumnType.CONNECT),
					new ShowColumn("connectNumberTotal", "????????????",
							ShowColumnType.CONNECT),
					new ShowColumn("connectNumberInSpeed", "????????????",
							ShowColumnType.CONNECT_ROTE),
					new ShowColumn("connectNumberOutSpeed", "????????????",
							ShowColumnType.CONNECT_ROTE),
					new ShowColumn("connectNumberTotalSpeed", "?????????",
							ShowColumnType.CONNECT_ROTE),
					new ShowColumn("connectPctge", "??????",
							ShowColumnType.PERCENTAGE) };
			return data3;
		}
		return null;
	}

	private ShowColumn[] getColumnIpGroup(String type) {
		switch (type) {
		case "1":
			ShowColumn[] data1 = {
					new ShowColumn("ipgroup_name", "IP????????????",
							ShowColumnType.OTHER),
					new ShowColumn("in_flows", "????????????", ShowColumnType.FLOW),
					new ShowColumn("out_flows", "????????????", ShowColumnType.FLOW),
					new ShowColumn("total_flows", "?????????", ShowColumnType.FLOW),
					new ShowColumn("in_speed", "????????????", ShowColumnType.FLOW_RATE),
					new ShowColumn("out_speed", "????????????",
							ShowColumnType.FLOW_RATE),
					new ShowColumn("total_speed", "?????????",
							ShowColumnType.FLOW_RATE),
					new ShowColumn("flowPctge", "??????", ShowColumnType.PERCENTAGE) };
			return data1;
		case "2":
			ShowColumn[] data2 = {
					new ShowColumn("ipgroup_name", "IP????????????",
							ShowColumnType.OTHER),
					new ShowColumn("in_packages", "????????????", ShowColumnType.PACKET),
					new ShowColumn("out_packages", "????????????",
							ShowColumnType.PACKET),
					new ShowColumn("total_packages", "?????????",
							ShowColumnType.PACKET),
					new ShowColumn("packetInSpeed", "????????????",
							ShowColumnType.PACKET_ROTE),
					new ShowColumn("packetOutSpeed", "????????????",
							ShowColumnType.PACKET_ROTE),
					new ShowColumn("packetTotalSpeed", "?????????",
							ShowColumnType.PACKET_ROTE),
					new ShowColumn("packetPctge", "??????",
							ShowColumnType.PERCENTAGE) };
			return data2;
		case "3":
			ShowColumn[] data3 = {
					new ShowColumn("ipgroup_name", "IP????????????",
							ShowColumnType.OTHER),
					new ShowColumn("connectNumberIn", "???????????????",
							ShowColumnType.CONNECT),
					new ShowColumn("connectNumberOut", "???????????????",
							ShowColumnType.CONNECT),
					new ShowColumn("connectNumberTotal", "????????????",
							ShowColumnType.CONNECT),
					new ShowColumn("connectNumberInSpeed", "????????????",
							ShowColumnType.CONNECT_ROTE),
					new ShowColumn("connectNumberOutSpeed", "????????????",
							ShowColumnType.CONNECT_ROTE),
					new ShowColumn("connectNumberTotalSpeed", "?????????",
							ShowColumnType.CONNECT_ROTE),
					new ShowColumn("connectPctge", "??????",
							ShowColumnType.PERCENTAGE) };
			return data3;
		}
		return null;
	}
}
