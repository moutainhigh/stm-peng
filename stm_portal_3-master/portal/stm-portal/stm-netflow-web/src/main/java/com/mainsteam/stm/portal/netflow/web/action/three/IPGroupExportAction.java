package com.mainsteam.stm.portal.netflow.web.action.three;

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
@RequestMapping("netflow/three/export")
public class IPGroupExportAction {

	public static final String IP_GROUP_APP_TABLE_THREE = "IP_GROUP_APP_TABLE_THREE";
	public static final String IP_GROUP_APP_CHART_THREE = "IP_GROUP_APP_CHART_THREE";
	public static final String IP_GROUP_SESSION_TABLE_THREE = "IP_GROUP_SESSION_TABLE_THREE";
	public static final String IP_GROUP_SESSION_CHART_THREE = "IP_GROUP_SESSION_CHART_THREE";
	public static final String IP_GROUP_TERMINAL_TABLE_THREE = "IP_GROUP_TERMINAL_TABLE_THREE";
	public static final String IP_GROUP_PROTOCOL_TABLE_THREE = "IP_GROUP_PROTOCOL_TABLE_THREE";
	public static final String IP_GROUP_TOS_TABLE_THREE = "IP_GROUP_TOS_TABLE_THREE";
	public static final String IP_GROUP_TOS_CHART_THREE = "IP_GROUP_TOS_CHART_THREE";

	@RequestMapping("IPGroup")
	public void IPGroup(HttpServletResponse resp, String type)
			throws IOException, NoSuchMethodException, SecurityException,
			IllegalAccessException, IllegalArgumentException,
			InvocationTargetException {
		HttpSession session = BaseAction.getHttpServletRequest().getSession();
		PDFHelper pdf = new PDFHelper("IP????????????");
		// ??????
		NetflowCharWrapper appChart = (NetflowCharWrapper) session
				.getAttribute(IP_GROUP_APP_CHART_THREE);
		pdf.addLine(
				"???????????????(" + NetFlowUtil.getSortName(appChart.getSortColumn())
						+ ")", "??????", "??????(MB)",
				new LineAdapter().adapter(appChart));
		NetflowPageBo appBo = (NetflowPageBo) session
				.getAttribute(IP_GROUP_APP_TABLE_THREE);
		pdf.addTable(new TableAdapter().adapter("??????????????????", getColumnApp(type),
				appBo.getRows()));
		// ??????
		NetflowCharWrapper sessionChart = (NetflowCharWrapper) session
				.getAttribute(IP_GROUP_SESSION_CHART_THREE);
		pdf.addLine(
				"???????????????("
						+ NetFlowUtil.getSortName(sessionChart.getSortColumn())
						+ ")", "??????", "??????(MB)",
				new LineAdapter().adapter(sessionChart));
		NetflowPageBo sessionBo = (NetflowPageBo) session
				.getAttribute(IP_GROUP_SESSION_TABLE_THREE);
		pdf.addTable(new TableAdapter().adapter("??????????????????",
				getColumnSession(type), sessionBo.getRows()));
		// ??????
		NetflowPageBo terminalBo = (NetflowPageBo) BaseAction
				.getHttpServletRequest().getSession()
				.getAttribute(IP_GROUP_TERMINAL_TABLE_THREE);
		pdf.addCircular(
				"???????????????("
						+ NetFlowUtil.getSortName(terminalBo.getParamBo()
								.getSort()) + ")", new CircularAdapter()
						.adapter("terminalIp", terminalBo.getParamBo()
								.getSort(), terminalBo.getRows()));
		pdf.addTable(new TableAdapter().adapter("??????????????????",
				getColumnTermainl(type), terminalBo.getRows()));
		// ??????
		NetflowPageBo protocolBo = (NetflowPageBo) BaseAction
				.getHttpServletRequest().getSession()
				.getAttribute(IP_GROUP_PROTOCOL_TABLE_THREE);
		pdf.addCircular(
				"???????????????("
						+ NetFlowUtil.getSortName(protocolBo.getParamBo()
								.getSort()) + ")", new CircularAdapter()
						.adapter("name", protocolBo.getParamBo().getSort(),
								protocolBo.getRows()));
		pdf.addTable(new TableAdapter().adapter("??????????????????", getColumnProto(type),
				protocolBo.getRows()));
		// tos
		NetflowCharWrapper tosChart = (NetflowCharWrapper) BaseAction
				.getHttpServletRequest().getSession()
				.getAttribute(IP_GROUP_TOS_CHART_THREE);
		pdf.addLine(
				"???????????????(" + NetFlowUtil.getSortName(tosChart.getSortColumn())
						+ ")", "??????", "??????(MB)",
				new LineAdapter().adapter(tosChart));
		NetflowPageBo tosBo = (NetflowPageBo) BaseAction
				.getHttpServletRequest().getSession()
				.getAttribute(IP_GROUP_TOS_TABLE_THREE);
		pdf.addTable(new TableAdapter().adapter("TOS????????????", getColumnTos(type),
				tosBo.getRows()));
		DownUtil.down(resp, "IPGroup.pdf", pdf.generate());
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

	private ShowColumn[] getColumnTermainl(String type) {
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

	private ShowColumn[] getColumnProto(String type) {
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
}
