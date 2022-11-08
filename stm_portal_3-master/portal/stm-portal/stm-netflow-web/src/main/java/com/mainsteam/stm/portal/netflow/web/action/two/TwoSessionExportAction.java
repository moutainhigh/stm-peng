package com.mainsteam.stm.portal.netflow.web.action.two;

import java.io.IOException;
import java.lang.reflect.InvocationTargetException;

import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;

import com.mainsteam.stm.export.pdf.PDFHelper;
import com.mainsteam.stm.platform.web.action.BaseAction;
import com.mainsteam.stm.portal.netflow.bo.SessionPageBo;
import com.mainsteam.stm.portal.netflow.web.action.common.DownUtil;
import com.mainsteam.stm.portal.netflow.web.action.common.NetFlowUtil;
import com.mainsteam.stm.portal.netflow.web.action.three.SessionExportAction;
import com.mainsteam.stm.portal.netflow.web.adapter.CircularAdapter;
import com.mainsteam.stm.portal.netflow.web.adapter.TableAdapter;
import com.mainsteam.stm.portal.netflow.web.adapter.TableAdapter.ShowColumn;
import com.mainsteam.stm.portal.netflow.web.adapter.TableAdapter.ShowColumn.ShowColumnType;

@Controller
@RequestMapping("netflow/two/export")
public class TwoSessionExportAction {

	@RequestMapping("session")
	public void session(HttpServletResponse resp, String type)
			throws IOException, NoSuchMethodException, SecurityException,
			IllegalAccessException, IllegalArgumentException,
			InvocationTargetException {
		HttpSession session = BaseAction.getHttpServletRequest().getSession();
		PDFHelper pdf = new PDFHelper("会话流量");
		// 协议
		SessionPageBo protocolBo = (SessionPageBo) session
				.getAttribute(SessionExportAction.SESSION_PROTOCOL_TABLE_THREE);
		pdf.addCircular(
				"协议环形图(" + NetFlowUtil.getSortName(protocolBo.getSort()) + ")",
				new CircularAdapter().adapter("proto_name",
						protocolBo.getSort(), protocolBo.getRows()));
		pdf.addTable(new TableAdapter().adapter("协议流量数据",
				getColumnProtocol(type), protocolBo.getRows()));
		// 应用
		SessionPageBo appBo = (SessionPageBo) session
				.getAttribute(SessionExportAction.SESSION_APP_TABLE_THREE);
		pdf.addCircular("应用环形图(" + NetFlowUtil.getSortName(appBo.getSort())
				+ ")", new CircularAdapter().adapter("app_name",
				appBo.getSort(), appBo.getRows()));
		pdf.addTable(new TableAdapter().adapter("应用流量数据", getColumnApp(type),
				appBo.getRows()));
		DownUtil.down(resp, "terminal.pdf", pdf.generate());
	}

	private ShowColumn[] getColumnApp(String type) {
		switch (type) {
		case "1":
			ShowColumn[] data1 = {
					new ShowColumn("app_name", "应用名称", ShowColumnType.OTHER),
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
					new ShowColumn("app_name", "应用名称", ShowColumnType.OTHER),
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
					new ShowColumn("app_name", "应用名称", ShowColumnType.OTHER),
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

	private ShowColumn[] getColumnProtocol(String type) {
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
}
