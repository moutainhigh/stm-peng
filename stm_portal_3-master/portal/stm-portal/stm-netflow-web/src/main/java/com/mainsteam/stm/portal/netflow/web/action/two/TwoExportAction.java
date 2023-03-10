package com.mainsteam.stm.portal.netflow.web.action.two;

import java.io.IOException;
import java.lang.reflect.InvocationTargetException;

import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;

import com.mainsteam.stm.export.pdf.PDFHelper;
import com.mainsteam.stm.platform.web.action.BaseAction;
import com.mainsteam.stm.portal.netflow.bo.NetflowCharWrapper;
import com.mainsteam.stm.portal.netflow.bo.NetflowPageBo;
import com.mainsteam.stm.portal.netflow.web.action.common.DownUtil;
import com.mainsteam.stm.portal.netflow.web.action.common.NetFlowUtil;
import com.mainsteam.stm.portal.netflow.web.adapter.CircularAdapter;
import com.mainsteam.stm.portal.netflow.web.adapter.LineAdapter;
import com.mainsteam.stm.portal.netflow.web.adapter.TableAdapter;
import com.mainsteam.stm.portal.netflow.web.adapter.TableAdapter.ShowColumn;
import com.mainsteam.stm.portal.netflow.web.adapter.TableAdapter.ShowColumn.ShowColumnType;

@Controller
@RequestMapping("netflow/two/export")
public class TwoExportAction {

	public static final String INTERFACE_APP_TABLE_TWO = "INTERFACE_APP_TABLE_TWO";
	public static final String INTERFACE_APP_CHART_TWO = "INTERFACE_APP_CHART_TWO";
	public static final String INTERFACE_TERMINAL_TABLE_TWO = "INTERFACE_TERMINAL_TABLE_TWO";
	public static final String INTERFACE_SESSION_TABLE_TWO = "INTERFACE_SESSION_TABLE_TWO";
	public static final String INTERFACE_SESSION_CHART_TWO = "INTERFACE_SESSION_CHART_TWO";
	public static final String INTERFACE_PROTOCOL_TABLE_TWO = "INTERFACE_PROTOCOL_TABLE_TWO";
	public static final String INTERFACE_NEXT_TABLE_TWO = "INTERFACE_NEXT_TABLE_TWO";
	public static final String INTERFACE_NEXT_CHART_TWO = "INTERFACE_NEXT_CHART_TWO";
	public static final String INTERFACE_TOS_TABLE_TWO = "INTERFACE_TOS_TABLE_TWO";
	public static final String INTERFACE_TOS_CHART_TWO = "INTERFACE_TOS_CHART_TWO";
	public static final String INTERFACE_IP_GROUP_TABLE_TWO = "INTERFACE_IP_GROUP_TABLE_TWO";
	public static final String INTERFACE_IP_GROUP_CHART_TWO = "INTERFACE_IP_GROUP_CHART_TWO";

	@RequestMapping("interface")
	public void interfaces(HttpServletResponse resp, String type)
			throws IOException, NoSuchMethodException, SecurityException,
			IllegalAccessException, IllegalArgumentException,
			InvocationTargetException {
		HttpSession session = BaseAction.getHttpServletRequest().getSession();
		PDFHelper pdf = new PDFHelper("????????????");
		// ??????
		NetflowPageBo terminalBo = (NetflowPageBo) session
				.getAttribute(INTERFACE_TERMINAL_TABLE_TWO);
		pdf.addCircular(
				"???????????????("
						+ NetFlowUtil.getSortName(terminalBo.getParamBo()
								.getSort()) + ")", new CircularAdapter()
						.adapter("terminalIp", terminalBo.getParamBo()
								.getSort(), terminalBo.getRows()));
		pdf.addTable(new TableAdapter().adapter("??????????????????",
				getColumnTerminal(type), terminalBo.getRows()));
		// ??????
		NetflowCharWrapper appChart = (NetflowCharWrapper) session
				.getAttribute(INTERFACE_APP_CHART_TWO);
		pdf.addLine(
				"???????????????(" + NetFlowUtil.getSortName(appChart.getSortColumn())
						+ ")", "??????", "??????(MB)",
				new LineAdapter().adapter(appChart));
		NetflowPageBo appBo = (NetflowPageBo) session
				.getAttribute(INTERFACE_APP_TABLE_TWO);
		pdf.addTable(new TableAdapter().adapter("??????????????????", getColumnApp(type),
				appBo.getRows()));
		// ??????
		NetflowCharWrapper sessionChart = (NetflowCharWrapper) session
				.getAttribute(INTERFACE_APP_CHART_TWO);
		pdf.addLine(
				"???????????????("
						+ NetFlowUtil.getSortName(sessionChart.getSortColumn())
						+ ")", "??????", "??????(MB)",
				new LineAdapter().adapter(sessionChart));
		NetflowPageBo sessionBo = (NetflowPageBo) session
				.getAttribute(INTERFACE_SESSION_TABLE_TWO);
		pdf.addTable(new TableAdapter().adapter("??????????????????",
				getColumnSession(type), sessionBo.getRows()));
		// ??????
		NetflowPageBo protocolBo = (NetflowPageBo) session
				.getAttribute(INTERFACE_PROTOCOL_TABLE_TWO);
		pdf.addCircular(
				"???????????????("
						+ NetFlowUtil.getSortName(protocolBo.getParamBo()
								.getSort()) + ")", new CircularAdapter()
						.adapter("name", protocolBo.getParamBo().getSort(),
								protocolBo.getRows()));
		pdf.addTable(new TableAdapter().adapter("??????????????????",
				getColumnProtocol(type), protocolBo.getRows()));
		// ?????????
		NetflowCharWrapper nextChart = (NetflowCharWrapper) session
				.getAttribute(INTERFACE_NEXT_CHART_TWO);
		pdf.addLine(
				"??????????????????(" + NetFlowUtil.getSortName(nextChart.getSortColumn())
						+ ")", "??????", "??????(MB)",
				new LineAdapter().adapter(nextChart));
		NetflowPageBo nextBo = (NetflowPageBo) session
				.getAttribute(INTERFACE_NEXT_TABLE_TWO);
		pdf.addTable(new TableAdapter().adapter("?????????????????????", getColumnNext(type),
				nextBo.getRows()));
		// tos
		NetflowCharWrapper tosChart = (NetflowCharWrapper) session
				.getAttribute(INTERFACE_TOS_CHART_TWO);
		pdf.addLine(
				"Tos?????????(" + NetFlowUtil.getSortName(tosChart.getSortColumn())
						+ ")", "??????", "??????(MB)",
				new LineAdapter().adapter(tosChart));
		NetflowPageBo tosBo = (NetflowPageBo) session
				.getAttribute(INTERFACE_TOS_TABLE_TWO);
		pdf.addTable(new TableAdapter().adapter("Tos????????????", getColumnTos(type),
				tosBo.getRows()));
		// ip??????
		NetflowCharWrapper ipGroupChart = (NetflowCharWrapper) session
				.getAttribute(INTERFACE_IP_GROUP_CHART_TWO);
		pdf.addLine(
				"IP???????????????("
						+ NetFlowUtil.getSortName(ipGroupChart.getSortColumn())
						+ ")", "??????", "??????(MB)",
				new LineAdapter().adapter(ipGroupChart));
		NetflowPageBo ipGroupBo = (NetflowPageBo) session
				.getAttribute(INTERFACE_IP_GROUP_TABLE_TWO);
		pdf.addTable(new TableAdapter().adapter("IP??????????????????",
				getColumnIpGroup(type), ipGroupBo.getRows()));
		DownUtil.down(resp, "interface.pdf", pdf.generate());
	}

	private ShowColumn[] getColumnTerminal(String type) {
		switch (type) {
		case "1":
			ShowColumn[] data1 = {
					new ShowColumn("terminalIp", "????????????", ShowColumnType.OTHER),
					new ShowColumn("flowIn", "????????????", ShowColumnType.FLOW),
					new ShowColumn("flowOut", "????????????", ShowColumnType.FLOW),
					new ShowColumn("flowTotal", "?????????", ShowColumnType.FLOW),
					new ShowColumn("speedIn", "????????????", ShowColumnType.FLOW_RATE),
					new ShowColumn("speedOut", "????????????", ShowColumnType.FLOW_RATE),
					new ShowColumn("speedTotal", "?????????",
							ShowColumnType.FLOW_RATE),
					new ShowColumn("flowPctge", "??????", ShowColumnType.PERCENTAGE) };
			return data1;
		case "2":
			ShowColumn[] data2 = {
					new ShowColumn("terminalIp", "????????????", ShowColumnType.OTHER),
					new ShowColumn("packetIn", "????????????", ShowColumnType.PACKET),
					new ShowColumn("packetOut", "????????????", ShowColumnType.PACKET),
					new ShowColumn("packetTotal", "?????????", ShowColumnType.PACKET),
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
					new ShowColumn("terminalIp", "????????????", ShowColumnType.OTHER),
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

	private ShowColumn[] getColumnProtocol(String type) {
		switch (type) {
		case "1":
			ShowColumn[] data1 = {
					new ShowColumn("name", "????????????", ShowColumnType.OTHER),
					new ShowColumn("flowIn", "????????????", ShowColumnType.FLOW),
					new ShowColumn("flowOut", "????????????", ShowColumnType.FLOW),
					new ShowColumn("flowTotal", "?????????", ShowColumnType.FLOW),
					new ShowColumn("speedIn", "????????????", ShowColumnType.FLOW_RATE),
					new ShowColumn("speedOut", "????????????", ShowColumnType.FLOW_RATE),
					new ShowColumn("speedTotal", "?????????",
							ShowColumnType.FLOW_RATE),
					new ShowColumn("flowPctge", "??????", ShowColumnType.PERCENTAGE) };
			return data1;
		case "2":
			ShowColumn[] data2 = {
					new ShowColumn("name", "????????????", ShowColumnType.OTHER),
					new ShowColumn("packetIn", "????????????", ShowColumnType.PACKET),
					new ShowColumn("packetOut", "????????????", ShowColumnType.PACKET),
					new ShowColumn("packetTotal", "?????????", ShowColumnType.PACKET),
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
					new ShowColumn("name", "????????????", ShowColumnType.OTHER),
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

	private ShowColumn[] getColumnNext(String type) {
		switch (type) {
		case "1":
			ShowColumn[] data1 = {
					new ShowColumn("nextHop", "?????????IP", ShowColumnType.OTHER),
					new ShowColumn("flowIn", "????????????", ShowColumnType.FLOW),
					new ShowColumn("flowOut", "????????????", ShowColumnType.FLOW),
					new ShowColumn("flowTotal", "?????????", ShowColumnType.FLOW),
					new ShowColumn("speedIn", "????????????", ShowColumnType.FLOW_RATE),
					new ShowColumn("speedOut", "????????????", ShowColumnType.FLOW_RATE),
					new ShowColumn("speedTotal", "?????????",
							ShowColumnType.FLOW_RATE),
					new ShowColumn("flowPctge", "??????", ShowColumnType.PERCENTAGE) };
			return data1;
		case "2":
			ShowColumn[] data2 = {
					new ShowColumn("nextHop", "?????????IP", ShowColumnType.OTHER),
					new ShowColumn("packetIn", "????????????", ShowColumnType.PACKET),
					new ShowColumn("packetOut", "????????????", ShowColumnType.PACKET),
					new ShowColumn("packetTotal", "?????????", ShowColumnType.PACKET),
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
					new ShowColumn("nextHop", "?????????IP", ShowColumnType.OTHER),
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

	private ShowColumn[] getColumnTos(String type) {
		switch (type) {
		case "1":
			ShowColumn[] data1 = {
					new ShowColumn("name", "????????????", ShowColumnType.OTHER),
					new ShowColumn("flowIn", "????????????", ShowColumnType.FLOW),
					new ShowColumn("flowOut", "????????????", ShowColumnType.FLOW),
					new ShowColumn("flowTotal", "?????????", ShowColumnType.FLOW),
					new ShowColumn("speedIn", "????????????", ShowColumnType.FLOW_RATE),
					new ShowColumn("speedOut", "????????????", ShowColumnType.FLOW_RATE),
					new ShowColumn("speedTotal", "?????????",
							ShowColumnType.FLOW_RATE),
					new ShowColumn("flowPctge", "??????", ShowColumnType.PERCENTAGE) };
			return data1;
		case "2":
			ShowColumn[] data2 = {
					new ShowColumn("name", "????????????", ShowColumnType.OTHER),
					new ShowColumn("packetIn", "????????????", ShowColumnType.PACKET),
					new ShowColumn("packetOut", "????????????", ShowColumnType.PACKET),
					new ShowColumn("packetTotal", "?????????", ShowColumnType.PACKET),
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
					new ShowColumn("name", "????????????", ShowColumnType.OTHER),
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
					new ShowColumn("name", "IP????????????", ShowColumnType.OTHER),
					new ShowColumn("flowIn", "????????????", ShowColumnType.FLOW),
					new ShowColumn("flowOut", "????????????", ShowColumnType.FLOW),
					new ShowColumn("flowTotal", "?????????", ShowColumnType.FLOW),
					new ShowColumn("speedIn", "????????????", ShowColumnType.FLOW_RATE),
					new ShowColumn("speedOut", "????????????", ShowColumnType.FLOW_RATE),
					new ShowColumn("speedTotal", "?????????",
							ShowColumnType.FLOW_RATE),
					new ShowColumn("flowPctge", "??????", ShowColumnType.PERCENTAGE) };
			return data1;
		case "2":
			ShowColumn[] data2 = {
					new ShowColumn("name", "IP????????????", ShowColumnType.OTHER),
					new ShowColumn("packetIn", "????????????", ShowColumnType.PACKET),
					new ShowColumn("packetOut", "????????????", ShowColumnType.PACKET),
					new ShowColumn("packetTotal", "?????????", ShowColumnType.PACKET),
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
					new ShowColumn("name", "IP????????????", ShowColumnType.OTHER),
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

	private ShowColumn[] getColumnApp(String type) {
		switch (type) {
		case "1":
			ShowColumn[] data1 = {
					new ShowColumn("name", "????????????", ShowColumnType.OTHER),
					new ShowColumn("flowIn", "????????????", ShowColumnType.FLOW),
					new ShowColumn("flowOut", "????????????", ShowColumnType.FLOW),
					new ShowColumn("flowTotal", "?????????", ShowColumnType.FLOW),
					new ShowColumn("speedIn", "????????????", ShowColumnType.FLOW_RATE),
					new ShowColumn("speedOut", "????????????", ShowColumnType.FLOW_RATE),
					new ShowColumn("speedTotal", "?????????",
							ShowColumnType.FLOW_RATE),
					new ShowColumn("flowPctge", "??????", ShowColumnType.PERCENTAGE) };
			return data1;
		case "2":
			ShowColumn[] data2 = {
					new ShowColumn("name", "????????????", ShowColumnType.OTHER),
					new ShowColumn("packetIn", "????????????", ShowColumnType.PACKET),
					new ShowColumn("packetOut", "????????????", ShowColumnType.PACKET),
					new ShowColumn("packetTotal", "?????????", ShowColumnType.PACKET),
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
					new ShowColumn("name", "????????????", ShowColumnType.OTHER),
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

	private ShowColumn[] getColumnSession(String type) {
		switch (type) {
		case "1":
			ShowColumn[] data1 = {
					new ShowColumn("srcIp", "???IP", ShowColumnType.OTHER),
					new ShowColumn("dstIp", "??????IP", ShowColumnType.OTHER),
					new ShowColumn("flowIn", "????????????", ShowColumnType.FLOW),
					new ShowColumn("flowOut", "????????????", ShowColumnType.FLOW),
					new ShowColumn("flowTotal", "?????????", ShowColumnType.FLOW),
					new ShowColumn("speedIn", "????????????", ShowColumnType.FLOW_RATE),
					new ShowColumn("speedOut", "????????????", ShowColumnType.FLOW_RATE),
					new ShowColumn("speedTotal", "?????????",
							ShowColumnType.FLOW_RATE),
					new ShowColumn("flowPctge", "??????", ShowColumnType.PERCENTAGE) };
			return data1;
		case "2":
			ShowColumn[] data2 = {
					new ShowColumn("srcIp", "???IP", ShowColumnType.OTHER),
					new ShowColumn("dstIp", "??????IP", ShowColumnType.OTHER),
					new ShowColumn("packetOut", "????????????", ShowColumnType.PACKET),
					new ShowColumn("packetTotal", "?????????", ShowColumnType.PACKET),
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
					new ShowColumn("srcIp", "???IP", ShowColumnType.OTHER),
					new ShowColumn("dstIp", "??????IP", ShowColumnType.OTHER),
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
