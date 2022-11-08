package com.mainsteam.stm.portal.netflow.web.action;

import java.io.IOException;
import java.lang.reflect.InvocationTargetException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.mainsteam.stm.export.pdf.PDFHelper;
import com.mainsteam.stm.platform.web.action.BaseAction;
import com.mainsteam.stm.portal.netflow.api.ITerminalApi;
import com.mainsteam.stm.portal.netflow.bo.TerminalBo;
import com.mainsteam.stm.portal.netflow.bo.TerminalChartDataBo;
import com.mainsteam.stm.portal.netflow.bo.TerminalConditionBo;
import com.mainsteam.stm.portal.netflow.bo.TerminalPageBo;
import com.mainsteam.stm.portal.netflow.web.action.common.DownUtil;
import com.mainsteam.stm.portal.netflow.web.action.three.ExportAction;
import com.mainsteam.stm.portal.netflow.web.adapter.CircularAdapter;
import com.mainsteam.stm.portal.netflow.web.adapter.LineAdapter;
import com.mainsteam.stm.portal.netflow.web.adapter.TableAdapter;
import com.mainsteam.stm.portal.netflow.web.adapter.TableAdapter.ShowColumn;
import com.mainsteam.stm.portal.netflow.web.adapter.TableAdapter.ShowColumn.ShowColumnType;
import com.mainsteam.stm.portal.netflow.web.vo.TerminalConditionVo;

/**
 * <li>文件名称: User.java</li> <li>公　　司: 武汉美新翔盛科技有限公司</li> <li>版权所有:
 * 版权所有(C)2019-2020</li> <li>修改记录: ...</li> <li>内容摘要: ...</li> <li>其他说明: ...</li>
 * 
 * @version ms.stm
 * @since 2019年6月14日
 * @author ziwenwen
 */
@Controller
@RequestMapping("/netflow/terminal")
public class TerminalAction extends BaseAction {

	@Autowired
	private ITerminalApi iterminalApi;

	@RequestMapping("/getallterminals")
	public JSONObject getAllTerminals(TerminalConditionVo terminalConditionVo) {
		TerminalConditionBo tcBo = toDeviceToTerminalConditionBo(terminalConditionVo);
		BaseAction.getHttpServletRequest().getSession()
				.setAttribute(TERMINAL_NETFLOW_TABLE_QUERY, tcBo);
		TerminalPageBo terminalPageBo = iterminalApi.getAllTerminal(tcBo);
		List<String> terminalsIp = new ArrayList<String>();
		List<TerminalBo> dnfs = (List<TerminalBo>) terminalPageBo.getRows();
		for (TerminalBo bo : dnfs) {
			terminalsIp.add(bo.getTerminal_name());
		}
		tcBo.setTerminalsIp(terminalsIp);
		terminalPageBo.setTcBo(tcBo);
		BaseAction.getHttpServletRequest().getSession()
				.setAttribute(TERMINAL_NETFLOW_TABLE, terminalPageBo);
		return toSuccess(terminalPageBo);
	}

	@RequestMapping("/getallterminalschart")
	public JSONObject terminalChartOnColumByDevice(String terminalConditionVo) {
		TerminalConditionVo terminalCondition = JSON.parseObject(
				terminalConditionVo, TerminalConditionVo.class);
		TerminalConditionBo tcBo = toDeviceToTerminalConditionBo(terminalCondition);
		TerminalChartDataBo terminalChartDataBo = iterminalApi
				.getTerminalChartData(tcBo);
		BaseAction.getHttpServletRequest().getSession()
				.setAttribute(TERMINAL_NETFLOW_CHART, terminalChartDataBo);
		return toSuccess(terminalChartDataBo);
	}

	@RequestMapping("/getappbyterminal")
	public JSONObject getApplicationByTerminal(
			TerminalConditionVo terminalConditionVo) {
		TerminalConditionBo tcBo = toDeviceToTerminalConditionBo(terminalConditionVo);
		BaseAction.getHttpServletRequest().getSession()
				.setAttribute(TERMINAL_APP_TABLE_THREE, tcBo);
		TerminalPageBo terminalPageBo = iterminalApi.getAppByTerminal(tcBo);
		List<String> appids = new ArrayList<String>();
		List<TerminalBo> apps = (List<TerminalBo>) terminalPageBo.getRows();
		for (TerminalBo bo : apps) {
			appids.add(bo.getApp_id());
		}
		tcBo.setApplicationsIp(appids);
		terminalPageBo.setTcBo(tcBo);
		BaseAction
				.getHttpServletRequest()
				.getSession()
				.setAttribute(ExportAction.TERMINAL_TABLE_THREE, terminalPageBo);
		return toSuccess(terminalPageBo);
	}

	@RequestMapping("/getappchartbyterminal")
	public JSONObject getApplicationChartByTerminal(String terminalConditionVo) {
		TerminalConditionVo terminalCondition = JSON.parseObject(
				terminalConditionVo, TerminalConditionVo.class);
		TerminalConditionBo tcBo = toDeviceToTerminalConditionBo(terminalCondition);
		TerminalChartDataBo terminalChartDataBo = iterminalApi
				.getAppChartDataByTerminal(tcBo);
		return toSuccess(terminalChartDataBo);
	}

	@RequestMapping("/getsessionbyterminal")
	public JSONObject getsessionByTerminal(
			TerminalConditionVo terminalConditionVo) {
		TerminalConditionBo tcBo = toDeviceToTerminalConditionBo(terminalConditionVo);
		BaseAction.getHttpServletRequest().getSession()
				.setAttribute(TERMINAL_SESSION_TABLE_THREE, tcBo);
		TerminalPageBo terminalPageBo = iterminalApi
				.getSessionsByTerminal(tcBo);
		List<TerminalBo> sessions = (List<TerminalBo>) terminalPageBo.getRows();
		List<Map<String, String>> sessionips = new ArrayList<Map<String, String>>();
		for (TerminalBo bo : sessions) {
			Map<String, String> currentSession = new HashMap<String, String>();
			currentSession.put(bo.getSrc_ip(), bo.getDst_ip());
			sessionips.add(currentSession);
		}
		tcBo.setSessionips(sessionips);
		terminalPageBo.setTcBo(tcBo);
		BaseAction.getHttpServletRequest().getSession()
				.setAttribute(ExportAction.SESSION_TABLE_THREE, terminalPageBo);
		return toSuccess(terminalPageBo);
	}

	@RequestMapping("/getsessionchartbyterminal")
	public JSONObject getsessionChartOnColumByDevice(String terminalConditionVo) {
		TerminalConditionVo terminalCondition = JSON.parseObject(
				terminalConditionVo, TerminalConditionVo.class);
		TerminalConditionBo tcBo = toDeviceToTerminalConditionBo(terminalCondition);
		TerminalChartDataBo terminalChartDataBo = iterminalApi
				.getSessionChartonDataByTerminal(tcBo);
		terminalChartDataBo.setSortColum(terminalCondition.getSort());
		BaseAction
				.getHttpServletRequest()
				.getSession()
				.setAttribute(ExportAction.SESSION_CHART_THREE,
						terminalChartDataBo);
		return toSuccess(terminalChartDataBo);
	}

	@RequestMapping("/getprotocolbyterminal")
	public JSONObject getProtocolByTerminal(
			TerminalConditionVo terminalConditionVo) {
		TerminalConditionBo tcBo = toDeviceToTerminalConditionBo(terminalConditionVo);
		BaseAction.getHttpServletRequest().getSession()
				.setAttribute(TERMINAL_PROTO_TABLE_THREE, tcBo);
		TerminalPageBo terminalPageBo = iterminalApi
				.getProtocolByTerminal(tcBo);
		List<TerminalBo> protos = (List<TerminalBo>) terminalPageBo.getRows();
		List<String> protoList = new ArrayList<String>();
		for (TerminalBo bo : protos) {
			protoList.add(bo.getProto());
		}
		tcBo.setProtosIp(protoList);
		terminalPageBo.setTcBo(tcBo);
		BaseAction
				.getHttpServletRequest()
				.getSession()
				.setAttribute(ExportAction.PROTOCOL_TABLE_THREE, terminalPageBo);
		return toSuccess(terminalPageBo);
	}

	@RequestMapping("/getprotochartbyterminal")
	public JSONObject getProtoChartByTerminal(String terminalConditionVo) {
		TerminalConditionVo terminalCondition = JSON.parseObject(
				terminalConditionVo, TerminalConditionVo.class);
		TerminalConditionBo tcBo = toDeviceToTerminalConditionBo(terminalCondition);
		TerminalChartDataBo terminalChartDataBo = iterminalApi
				.getProtocolChartDataByTerminal(tcBo);
		terminalChartDataBo.setSortColum(tcBo.getSort());
		BaseAction
				.getHttpServletRequest()
				.getSession()
				.setAttribute(ExportAction.PROTOCOL_CHART_THREE,
						terminalChartDataBo);
		return toSuccess(terminalChartDataBo);
	}

	@RequestMapping("/gettosbyterminal")
	public JSONObject getTosByTerminal(TerminalConditionVo terminalConditionVo) {
		TerminalConditionBo tcBo = toDeviceToTerminalConditionBo(terminalConditionVo);
		BaseAction.getHttpServletRequest().getSession()
				.setAttribute(TERMINAL_TOS_TABLE_THREE, tcBo);
		TerminalPageBo terminalPageBo = iterminalApi.getTosByTerminal(tcBo);
		List<TerminalBo> toss = (List<TerminalBo>) terminalPageBo.getRows();
		List<String> tosList = new ArrayList<String>();
		for (TerminalBo bo : toss) {
			tosList.add(bo.getTos());
		}
		tcBo.setTosids(tosList);
		terminalPageBo.setTcBo(tcBo);
		BaseAction.getHttpServletRequest().getSession()
				.setAttribute(ExportAction.TOS_TABLE_THREE, terminalPageBo);
		return toSuccess(terminalPageBo);
	}

	@RequestMapping("/gettoschartbyterminal")
	public JSONObject getTosChartByTerminal(String terminalConditionVo) {
		TerminalConditionVo terminalCondition = JSON.parseObject(
				terminalConditionVo, TerminalConditionVo.class);
		TerminalConditionBo tcBo = toDeviceToTerminalConditionBo(terminalCondition);
		TerminalChartDataBo terminalChartDataBo = iterminalApi
				.getTosChartDataByTerminal(tcBo);
		terminalChartDataBo.setSortColum(tcBo.getSort());
		BaseAction
				.getHttpServletRequest()
				.getSession()
				.setAttribute(ExportAction.TOS_CHART_THREE, terminalChartDataBo);
		return toSuccess(terminalChartDataBo);
	}

	public TerminalConditionBo toDeviceToTerminalConditionBo(
			TerminalConditionVo terminalConditionVo) {
		if (terminalConditionVo == null)
			return null;
		TerminalConditionBo terminalConditionBo = new TerminalConditionBo();
		if (null != terminalConditionVo.getTimePerid()) {
			terminalConditionBo.setTimePerid(terminalConditionVo.getTimePerid()
					.trim());
		}
		terminalConditionBo.setDeviceIp(terminalConditionVo.getDeviceIp());
		terminalConditionBo.setEndtime(terminalConditionVo.getEndtime());
		if (terminalConditionVo.getRecordCount() != null) {
			terminalConditionBo.setRecordCount(new Long(terminalConditionVo
					.getRecordCount()).longValue());
		} else {
			terminalConditionBo.setRecordCount(new Long(0).longValue());
		}
		if (terminalConditionVo.getRowCount() != null) {
			terminalConditionBo.setRowCount(new Long(terminalConditionVo
					.getRowCount()).longValue());
		} else {
			terminalConditionBo.setRowCount(new Long(0).longValue());
		}
		terminalConditionBo.setSort(terminalConditionVo.getSort());
		terminalConditionBo.setOrder(terminalConditionVo.getOrder());
		terminalConditionBo.setStartRow(new Long(terminalConditionVo
				.getStartRow()).longValue());
		terminalConditionBo.setStarttime(terminalConditionVo.getStarttime());
		terminalConditionBo.setShowpagination(terminalConditionVo
				.getShowpagination());
		if (terminalConditionVo.getApp_id() != null
				|| "".equals(terminalConditionVo.getApp_id())) {
			terminalConditionBo.setApp_id(terminalConditionVo.getApp_id());
		}
		terminalConditionBo.setAllapplicationflows(terminalConditionVo
				.getAllapplicationflows());
		terminalConditionBo.setAllprotoflows(terminalConditionVo
				.getAllprotoflows());
		terminalConditionBo.setApplicationsIp(terminalConditionVo
				.getApplicationsIp());
		terminalConditionBo.setProtosIp(terminalConditionVo.getProtosIp());
		terminalConditionBo.setTableSubfixTime(terminalConditionVo
				.getTableSubfixTime());
		terminalConditionBo.setTimepart(terminalConditionVo.getTimepart());
		terminalConditionBo
				.setTerminalsIp(terminalConditionVo.getTerminalsIp());
		terminalConditionBo.setAllterminalsFlows(terminalConditionVo
				.getAllterminalsFlows());
		terminalConditionBo.setAllsessionFlows(terminalConditionVo
				.getAllsessionFlows());
		terminalConditionBo.setSessionips(terminalConditionVo.getSessionips());
		terminalConditionBo.setAllipgsFlows(terminalConditionVo
				.getAllipgsFlows());
		terminalConditionBo.setIpgsIp(terminalConditionVo.getIpgsIp());
		terminalConditionBo.setIf_id(terminalConditionVo.getIf_id());
		terminalConditionBo.setTerminal_name(terminalConditionVo
				.getTerminal_name());
		terminalConditionBo.setTos(terminalConditionVo.getTos());
		terminalConditionBo.setTos_name(terminalConditionVo.getTos_name());
		terminalConditionBo.setTosids(terminalConditionVo.getTosids());
		return terminalConditionBo;
	}

	private static final String TERMINAL_NETFLOW_CHART = "TERMINAL_NETFLOW_CHART";
	private static final String TERMINAL_NETFLOW_TABLE = "TERMINAL_NETFLOW_TABLE";
	private static final String TERMINAL_NETFLOW_TABLE_QUERY = "TERMINAL_NETFLOW_TABLE_QUERY";
	private static final String TERMINAL_APP_TABLE_THREE = "TERMINAL_APP_TABLE_THREE";
	private static final String TERMINAL_SESSION_TABLE_THREE = "TERMINAL_SESSION_TABLE_THREE";
	private static final String TERMINAL_TOS_TABLE_THREE = "TERMINAL_TOS_TABLE_THREE";
	private static final String TERMINAL_PROTO_TABLE_THREE = "TERMINAL_PROTO_TABLE_THREE";

	@RequestMapping("exportProto")
	public void exportProto(HttpServletResponse resp, String type)
			throws IOException, NoSuchMethodException, SecurityException,
			IllegalAccessException, IllegalArgumentException,
			InvocationTargetException {
		TerminalConditionBo tcBo = (TerminalConditionBo) BaseAction
				.getHttpServletRequest().getSession()
				.getAttribute(TERMINAL_PROTO_TABLE_THREE);
		tcBo.setStartRow(0);
		tcBo.setRowCount(Long.MAX_VALUE);
		TerminalPageBo page = iterminalApi.getProtocolByTerminal(tcBo);
		PDFHelper pdf = new PDFHelper("终端流量");
		pdf.addTable(new TableAdapter().adapter("协议", getColumnProto(type),
				page.getRows()));
		DownUtil.down(resp, "terminal_proto.pdf", pdf.generate());
	}

	@RequestMapping("exportTos")
	public void exportTos(HttpServletResponse resp, String type)
			throws IOException, NoSuchMethodException, SecurityException,
			IllegalAccessException, IllegalArgumentException,
			InvocationTargetException {
		TerminalConditionBo tcBo = (TerminalConditionBo) BaseAction
				.getHttpServletRequest().getSession()
				.getAttribute(TERMINAL_TOS_TABLE_THREE);
		tcBo.setStartRow(0);
		tcBo.setRowCount(Long.MAX_VALUE);
		TerminalPageBo page = iterminalApi.getTosByTerminal(tcBo);
		PDFHelper pdf = new PDFHelper("终端流量");
		pdf.addTable(new TableAdapter().adapter("TOS", getColumnTos(type),
				page.getRows()));
		DownUtil.down(resp, "terminal_tos.pdf", pdf.generate());
	}

	@RequestMapping("exportSession")
	public void exportSession(HttpServletResponse resp, String type)
			throws IOException, NoSuchMethodException, SecurityException,
			IllegalAccessException, IllegalArgumentException,
			InvocationTargetException {
		TerminalConditionBo tcBo = (TerminalConditionBo) BaseAction
				.getHttpServletRequest().getSession()
				.getAttribute(TERMINAL_SESSION_TABLE_THREE);
		tcBo.setStartRow(0);
		tcBo.setRowCount(Long.MAX_VALUE);
		TerminalPageBo page = iterminalApi.getSessionsByTerminal(tcBo);
		PDFHelper pdf = new PDFHelper("终端流量");
		pdf.addTable(new TableAdapter().adapter("会话", getColumnSession(type),
				page.getRows()));
		DownUtil.down(resp, "terminal_session.pdf", pdf.generate());
	}

	@RequestMapping("exportApp")
	public void exportApp(HttpServletResponse resp, String type)
			throws IOException, NoSuchMethodException, SecurityException,
			IllegalAccessException, IllegalArgumentException,
			InvocationTargetException {
		TerminalConditionBo tcBo = (TerminalConditionBo) BaseAction
				.getHttpServletRequest().getSession()
				.getAttribute(TERMINAL_APP_TABLE_THREE);
		tcBo.setStartRow(0);
		tcBo.setRowCount(Long.MAX_VALUE);
		TerminalPageBo page = iterminalApi.getAppByTerminal(tcBo);
		PDFHelper pdf = new PDFHelper("终端流量");
		pdf.addTable(new TableAdapter().adapter("应用", getColumnApp(type),
				page.getRows()));
		DownUtil.down(resp, "terminal_app.pdf", pdf.generate());
	}

	@RequestMapping("exportAll")
	public void exportAll(HttpServletResponse resp, String type)
			throws IOException, NoSuchMethodException, SecurityException,
			IllegalAccessException, IllegalArgumentException,
			InvocationTargetException {
		HttpSession session = BaseAction.getHttpServletRequest().getSession();
		TerminalConditionBo tcBo = (TerminalConditionBo) session
				.getAttribute(TERMINAL_NETFLOW_TABLE_QUERY);
		tcBo.setStartRow(0);
		tcBo.setRowCount(Long.MAX_VALUE);
		TerminalPageBo terminalPageBo = iterminalApi.getAllTerminal(tcBo);
		List<String> terminalsIp = new ArrayList<String>();
		@SuppressWarnings("unchecked")
		List<TerminalBo> dnfs = (List<TerminalBo>) terminalPageBo.getRows();
		for (TerminalBo bo : dnfs) {
			terminalsIp.add(bo.getTerminal_name());
		}
		tcBo.setTerminalsIp(terminalsIp);
		terminalPageBo.setTcBo(tcBo);
		PDFHelper pdf = new PDFHelper("终端流量");
		pdf.addTable(new TableAdapter().adapter("终端流量数据",
				getColumnTerminal(type), terminalPageBo.getRows()));
		DownUtil.down(resp, "terminal.pdf", pdf.generate());
	}

	@RequestMapping("export")
	public void export(HttpServletResponse resp, String type)
			throws IOException, NoSuchMethodException, SecurityException,
			IllegalAccessException, IllegalArgumentException,
			InvocationTargetException {
		HttpSession session = BaseAction.getHttpServletRequest().getSession();
		PDFHelper pdf = new PDFHelper("终端流量");
		TerminalChartDataBo chart = (TerminalChartDataBo) session
				.getAttribute(TERMINAL_NETFLOW_CHART);
		pdf.addLine("终端流量数据折线图", "时间", "流量(MB)",
				new LineAdapter().adapter(chart));
		TerminalPageBo page = (TerminalPageBo) session
				.getAttribute(TERMINAL_NETFLOW_TABLE);
		pdf.addCircular("终端流量数据环形图", new CircularAdapter().adapter(
				"terminal_name", page.getTcBo().getSort(), page.getRows()));
		pdf.addTable(new TableAdapter().adapter("终端流量数据",
				getColumnTerminal(type), page.getRows()));
		DownUtil.down(resp, "terminal.pdf", pdf.generate());
	}

	private ShowColumn[] getColumnSession(String type) {
		switch (type) {
		case "1":
			ShowColumn[] data1 = {
					new ShowColumn("src_ip", "源IP", ShowColumnType.OTHER),
					new ShowColumn("dst_ip", "源IP", ShowColumnType.OTHER),
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
					new ShowColumn("dst_ip", "源IP", ShowColumnType.OTHER),
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
					new ShowColumn("dst_ip", "源IP", ShowColumnType.OTHER),
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

	private ShowColumn[] getColumnTos(String type) {
		switch (type) {
		case "1":
			ShowColumn[] data1 = {
					new ShowColumn("tos_name", "协议名称", ShowColumnType.OTHER),
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
					new ShowColumn("tos_name", "协议名称", ShowColumnType.OTHER),
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
					new ShowColumn("tos_name", "协议名称", ShowColumnType.OTHER),
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
}
