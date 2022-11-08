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
import com.mainsteam.stm.portal.netflow.api.ISessionApi;
import com.mainsteam.stm.portal.netflow.bo.SessionBo;
import com.mainsteam.stm.portal.netflow.bo.SessionChartDataBo;
import com.mainsteam.stm.portal.netflow.bo.SessionConditionBo;
import com.mainsteam.stm.portal.netflow.bo.SessionPageBo;
import com.mainsteam.stm.portal.netflow.web.action.common.DownUtil;
import com.mainsteam.stm.portal.netflow.web.action.three.SessionExportAction;
import com.mainsteam.stm.portal.netflow.web.adapter.CircularAdapter;
import com.mainsteam.stm.portal.netflow.web.adapter.LineAdapter;
import com.mainsteam.stm.portal.netflow.web.adapter.TableAdapter;
import com.mainsteam.stm.portal.netflow.web.adapter.TableAdapter.ShowColumn;
import com.mainsteam.stm.portal.netflow.web.adapter.TableAdapter.ShowColumn.ShowColumnType;
import com.mainsteam.stm.portal.netflow.web.vo.SessionConditionVo;

/**
 * <li>文件名称: User.java</li> <li>公　　司: 武汉美新翔盛科技有限公司</li> <li>版权所有:
 * 版权所有(C)2019-2020</li> <li>修改记录: ...</li> <li>内容摘要: ...</li> <li>其他说明: ...</li>
 * 
 * @version ms.stm
 * @since 2019年6月14日
 * @author ziwenwen
 */
@Controller
@RequestMapping("/netflow/session")
public class SessionAction extends BaseAction {

	@Autowired
	private ISessionApi sessionApi;

	@RequestMapping("/getsessions")
	public JSONObject getsessionByDevice(SessionConditionVo sessionConditionVo) {
		SessionConditionBo scBo = toSessionConditionBo(sessionConditionVo);
		BaseAction.getHttpServletRequest().getSession()
				.setAttribute(SESSION_NETFLOW_TABLE_QUERY, scBo);
		SessionPageBo sessionPageBo = sessionApi.getAllSession(scBo);
		List<SessionBo> sessions = (List<SessionBo>) sessionPageBo.getRows();
		List<Map<String, String>> sessionips = new ArrayList<Map<String, String>>();
		for (SessionBo bo : sessions) {
			Map<String, String> currentSession = new HashMap<String, String>();
			currentSession.put(bo.getSrc_ip(), bo.getDst_ip());
			sessionips.add(currentSession);
		}
		scBo.setSessionips(sessionips);
		sessionPageBo.setScBo(scBo);
		BaseAction.getHttpServletRequest().getSession()
				.setAttribute(SESSION_NETFLOW_TABLE, sessionPageBo);
		return toSuccess(sessionPageBo);
	}

	@RequestMapping("/getsessionchartdata")
	public JSONObject getsessionChartOnColumByDevice(String sessionCondition) {
		SessionConditionVo sessionConditionVo = JSON.parseObject(
				sessionCondition, SessionConditionVo.class);
		SessionConditionBo scBo = toSessionConditionBo(sessionConditionVo);
		SessionChartDataBo sessionChartDataBo = sessionApi
				.getSessionChartData(scBo);
		BaseAction.getHttpServletRequest().getSession()
				.setAttribute(SESSION_NETFLOW_CHART, sessionChartDataBo);
		return toSuccess(sessionChartDataBo);
	}

	@RequestMapping("/getprotocolbysession")
	public JSONObject getProtocolByApp(SessionConditionVo sessionConditionVo) {
		SessionConditionBo scBo = toSessionConditionBo(sessionConditionVo);
		BaseAction.getHttpServletRequest().getSession()
				.setAttribute(SESSION_PROTO_TABLE_THREE, scBo);
		SessionPageBo sessionPageBo = sessionApi.getProtocolBySession(scBo);
		List<SessionBo> sessions = (List<SessionBo>) sessionPageBo.getRows();
		List<String> protos = new ArrayList<String>();
		for (SessionBo session : sessions) {
			protos.add(session.getProto());
		}
		scBo.setProtosIp(protos);
		sessionPageBo.setScBo(scBo);
		BaseAction
				.getHttpServletRequest()
				.getSession()
				.setAttribute(SessionExportAction.SESSION_PROTOCOL_TABLE_THREE,
						sessionPageBo);
		return toSuccess(sessionPageBo);
	}

	@RequestMapping("/getprotocolchartbysession")
	public JSONObject getProtoChart(String sessionCondition) {
		SessionConditionVo sessionConditionVo = JSON.parseObject(
				sessionCondition, SessionConditionVo.class);
		SessionConditionBo scBo = toSessionConditionBo(sessionConditionVo);
		SessionChartDataBo sessionChartDataBo = sessionApi
				.getProtocolChartDataBySession(scBo);
		return toSuccess(sessionChartDataBo);
	}

	@RequestMapping("/getappbysession")
	public JSONObject getAppBySession(SessionConditionVo sessionConditionVo) {
		SessionConditionBo scBo = toSessionConditionBo(sessionConditionVo);
		BaseAction.getHttpServletRequest().getSession()
				.setAttribute(SESSION_APP_EXPORT_TABLE_THREE, scBo);
		SessionPageBo sessionPageBo = sessionApi.getAppBySession(scBo);
		List<String> appids = new ArrayList<String>();
		List<SessionBo> apps = (List<SessionBo>) sessionPageBo.getRows();
		for (SessionBo bo : apps) {
			appids.add(bo.getApp_id());
		}
		scBo.setApplicationsIp(appids);
		sessionPageBo.setScBo(scBo);
		BaseAction
				.getHttpServletRequest()
				.getSession()
				.setAttribute(SessionExportAction.SESSION_APP_TABLE_THREE,
						sessionPageBo);
		return toSuccess(sessionPageBo);
	}

	@RequestMapping("/getappchartbysession")
	public JSONObject getAppChartBySession(String sessionCondition) {
		SessionConditionVo sessionConditionVo = JSON.parseObject(
				sessionCondition, SessionConditionVo.class);
		SessionConditionBo scBo = toSessionConditionBo(sessionConditionVo);
		SessionChartDataBo sessionChartDataBo = sessionApi
				.getAppChartDataBySession(scBo);
		return toSuccess(sessionChartDataBo);
	}

	public SessionConditionBo toSessionConditionBo(
			SessionConditionVo sessionConditionVo) {
		if (sessionConditionVo == null)
			return null;
		SessionConditionBo sessionConditionBo = new SessionConditionBo();
		if (null != sessionConditionVo.getTimePerid()) {
			sessionConditionBo.setTimePerid(sessionConditionVo.getTimePerid()
					.trim());
		}
		sessionConditionBo.setDeviceIp(sessionConditionVo.getDeviceIp());
		sessionConditionBo.setEndtime(sessionConditionVo.getEndtime());
		if (sessionConditionVo.getRecordCount() != null) {
			sessionConditionBo.setRecordCount(new Long(sessionConditionVo
					.getRecordCount()).longValue());
		} else {
			sessionConditionBo.setRecordCount(new Long(0).longValue());
		}
		if (sessionConditionVo.getRowCount() != null) {
			sessionConditionBo.setRowCount(new Long(sessionConditionVo
					.getRowCount()).longValue());
		} else {
			sessionConditionBo.setRowCount(new Long(0).longValue());
		}
		sessionConditionBo.setSort(sessionConditionVo.getSort());
		sessionConditionBo.setOrder(sessionConditionVo.getOrder());
		sessionConditionBo.setStartRow(new Long(sessionConditionVo
				.getStartRow()).longValue());
		sessionConditionBo.setStarttime(sessionConditionVo.getStarttime());
		sessionConditionBo.setShowpagination(sessionConditionVo
				.getShowpagination());
		if (sessionConditionVo.getApp_id() != null) {
			sessionConditionBo.setApp_id(sessionConditionVo.getApp_id());
		}
		sessionConditionBo.setAllapplicationflows(sessionConditionVo
				.getAllapplicationflows());
		sessionConditionBo.setAllprotoflows(sessionConditionVo
				.getAllprotoflows());
		sessionConditionBo.setApplicationsIp(sessionConditionVo
				.getApplicationsIp());
		sessionConditionBo.setProtosIp(sessionConditionVo.getProtosIp());
		sessionConditionBo.setTableSubfixTime(sessionConditionVo
				.getTableSubfixTime());
		sessionConditionBo.setTimepart(sessionConditionVo.getTimepart());
		sessionConditionBo.setTerminalsIp(sessionConditionVo.getTerminalsIp());
		sessionConditionBo.setAllterminalsFlows(sessionConditionVo
				.getAllterminalsFlows());
		sessionConditionBo.setAllsessionFlows(sessionConditionVo
				.getAllsessionFlows());
		sessionConditionBo.setSessionips(sessionConditionVo.getSessionips());
		sessionConditionBo
				.setAllipgsFlows(sessionConditionVo.getAllipgsFlows());
		sessionConditionBo.setIpgsIp(sessionConditionVo.getIpgsIp());
		sessionConditionBo.setIf_id(sessionConditionVo.getIf_id());
		sessionConditionBo
				.setCurrentSrcIp(sessionConditionVo.getCurrentSrcIp());
		sessionConditionBo
				.setCurrentDstIp(sessionConditionVo.getCurrentDstIp());
		if (null != sessionConditionVo.getAllsessionPackets()) {
			sessionConditionBo.setAllsessionPackets(sessionConditionVo
					.getAllsessionPackets());
		}
		if (null != sessionConditionVo.getAllsessionConnects()) {
			sessionConditionBo.setAllsessionConnects(sessionConditionVo
					.getAllsessionConnects());
		}

		return sessionConditionBo;
	}

	public ISessionApi getSessionApi() {
		return sessionApi;
	}

	public void setSessionApi(ISessionApi sessionApi) {
		this.sessionApi = sessionApi;
	}

	private static final String SESSION_NETFLOW_CHART = "SESSION_NETFLOW_CHART";
	private static final String SESSION_NETFLOW_TABLE = "SESSION_NETFLOW_TABLE";
	private static final String SESSION_NETFLOW_TABLE_QUERY = "SESSION_NETFLOW_TABLE_QUERY";
	private static final String SESSION_PROTO_TABLE_THREE = "SESSION_PROTO_TABLE_THREE";
	private static final String SESSION_APP_EXPORT_TABLE_THREE = "SESSION_APP_EXPORT_TABLE_THREE";

	@RequestMapping("exportApp")
	public void exportApp(HttpServletResponse resp, String type)
			throws IOException, NoSuchMethodException, SecurityException,
			IllegalAccessException, IllegalArgumentException,
			InvocationTargetException {
		SessionConditionBo scBo = (SessionConditionBo) BaseAction
				.getHttpServletRequest().getSession()
				.getAttribute(SESSION_APP_EXPORT_TABLE_THREE);
		scBo.setStartRow(0);
		scBo.setRowCount(Long.MAX_VALUE);
		SessionPageBo page = sessionApi.getAppBySession(scBo);
		PDFHelper pdf = new PDFHelper("会话流量");
		pdf.addTable(new TableAdapter().adapter("应用", getColumnApp(type),
				page.getRows()));
		DownUtil.down(resp, "session_app.pdf", pdf.generate());
	}

	@RequestMapping("exportProto")
	public void exportProto(HttpServletResponse resp, String type)
			throws IOException, NoSuchMethodException, SecurityException,
			IllegalAccessException, IllegalArgumentException,
			InvocationTargetException {
		SessionConditionBo scBo = (SessionConditionBo) BaseAction
				.getHttpServletRequest().getSession()
				.getAttribute(SESSION_PROTO_TABLE_THREE);
		scBo.setStartRow(0);
		scBo.setRowCount(Long.MAX_VALUE);
		SessionPageBo page = sessionApi.getProtocolBySession(scBo);
		PDFHelper pdf = new PDFHelper("会话流量");
		pdf.addTable(new TableAdapter().adapter("协议", getColumnProtocol(type),
				page.getRows()));
		DownUtil.down(resp, "session_proto.pdf", pdf.generate());
	}

	@RequestMapping("exportAll")
	public void exportAll(HttpServletResponse resp, String type)
			throws IOException, NoSuchMethodException, SecurityException,
			IllegalAccessException, IllegalArgumentException,
			InvocationTargetException {
		SessionConditionBo scBo = (SessionConditionBo) BaseAction
				.getHttpServletRequest().getSession()
				.getAttribute(SESSION_NETFLOW_TABLE_QUERY);
		scBo.setStartRow(0);
		scBo.setRowCount(Long.MAX_VALUE);
		SessionPageBo sessionPageBo = sessionApi.getAllSession(scBo);
		List<SessionBo> sessions = (List<SessionBo>) sessionPageBo.getRows();
		List<Map<String, String>> sessionips = new ArrayList<Map<String, String>>();
		for (SessionBo bo : sessions) {
			Map<String, String> currentSession = new HashMap<String, String>();
			currentSession.put(bo.getSrc_ip(), bo.getDst_ip());
			sessionips.add(currentSession);
		}
		scBo.setSessionips(sessionips);
		sessionPageBo.setScBo(scBo);
		PDFHelper pdf = new PDFHelper("会话流量");
		pdf.addTable(new TableAdapter().adapter("会话流量数据",
				getColumnSession(type), sessionPageBo.getRows()));
		DownUtil.down(resp, "session.pdf", pdf.generate());
	}

	@RequestMapping("export")
	public void export(HttpServletResponse resp, String type)
			throws IOException, NoSuchMethodException, SecurityException,
			IllegalAccessException, IllegalArgumentException,
			InvocationTargetException {
		HttpSession session = BaseAction.getHttpServletRequest().getSession();
		PDFHelper pdf = new PDFHelper("会话流量");
		SessionChartDataBo chart = (SessionChartDataBo) session
				.getAttribute(SESSION_NETFLOW_CHART);
		pdf.addLine("会话流量数据折线图", "时间", "流量(MB)",
				new LineAdapter().adapter(chart));
		SessionPageBo page = (SessionPageBo) session
				.getAttribute(SESSION_NETFLOW_TABLE);
		pdf.addCircular("会话流量数据环形图", new CircularAdapter().adapter(
				new String[] { "src_ip", "dst_ip" }, page.getSort(),
				page.getRows()));
		pdf.addTable(new TableAdapter().adapter("会话流量数据",
				getColumnSession(type), page.getRows()));
		DownUtil.down(resp, "session.pdf", pdf.generate());
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
