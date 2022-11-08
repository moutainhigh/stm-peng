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
import com.mainsteam.stm.portal.netflow.api.IApplicationApi;
import com.mainsteam.stm.portal.netflow.bo.ApplicationChartDataBo;
import com.mainsteam.stm.portal.netflow.bo.ApplicationConditionBo;
import com.mainsteam.stm.portal.netflow.bo.ApplicationModelBo;
import com.mainsteam.stm.portal.netflow.bo.ApplicationPageBo;
import com.mainsteam.stm.portal.netflow.web.action.common.DownUtil;
import com.mainsteam.stm.portal.netflow.web.action.three.AppExportAction;
import com.mainsteam.stm.portal.netflow.web.adapter.CircularAdapter;
import com.mainsteam.stm.portal.netflow.web.adapter.LineAdapter;
import com.mainsteam.stm.portal.netflow.web.adapter.TableAdapter;
import com.mainsteam.stm.portal.netflow.web.adapter.TableAdapter.ShowColumn;
import com.mainsteam.stm.portal.netflow.web.adapter.TableAdapter.ShowColumn.ShowColumnType;
import com.mainsteam.stm.portal.netflow.web.vo.ApplicationConditionVo;
import com.mainsteam.stm.util.StringUtil;

/**
 * <li>文件名称: User.java</li> <li>公　　司: 武汉美新翔盛科技有限公司</li> <li>版权所有:
 * 版权所有(C)2019-2020</li> <li>修改记录: ...</li> <li>内容摘要: ...</li> <li>其他说明: ...</li>
 * 
 * @version ms.stm
 * @since 2019年6月14日
 * @author ziwenwen
 */
@Controller
@RequestMapping("/netflow/application")
public class ApplicationAction extends BaseAction {

	@Autowired
	private IApplicationApi iappApi;

	/**
	 * 增加一个用户
	 * 
	 * @param userVo
	 * @return 插入成功的条数
	 */
	@RequestMapping("/getapplication")
	public JSONObject getApplication(
			ApplicationConditionVo applicationConditionVo) {
		ApplicationConditionBo apcBo = toDeviceToApplicationConditionBo(applicationConditionVo);
		BaseAction.getHttpServletRequest().getSession()
				.setAttribute(APP_NETFLOW_TABLE_QUERY, apcBo);
		ApplicationPageBo applicationPageBo = iappApi.getAllApplication(apcBo);
		List<String> appids = new ArrayList<String>();
		List<ApplicationModelBo> apps = (List<ApplicationModelBo>) applicationPageBo
				.getRows();
		for (ApplicationModelBo bo : apps) {
			appids.add(bo.getApp_id());
		}
		apcBo.setApplicationsIp(appids);
		applicationPageBo.setAppBo(apcBo);
		BaseAction.getHttpServletRequest().getSession()
				.setAttribute(APP_NETFLOW_TABLE, applicationPageBo);
		return toSuccess(applicationPageBo);
	}

	@RequestMapping("/getprotocolbyapp")
	public JSONObject getProtocolByApp(
			ApplicationConditionVo applicationConditionVo) {
		ApplicationConditionBo acBo = toDeviceToApplicationConditionBo(applicationConditionVo);
		BaseAction.getHttpServletRequest().getSession()
				.setAttribute(APP_PROTO_NETFLOW_TABLE_THREE, acBo);
		ApplicationPageBo ApplicationPageBo = iappApi.getProtocolByApp(acBo);
		BaseAction
				.getHttpServletRequest()
				.getSession()
				.setAttribute(AppExportAction.APP_PROTOCOL_TABLE_THREE,
						ApplicationPageBo);
		return toSuccess(ApplicationPageBo);
	}

	@RequestMapping("/getapplicationchart")
	public JSONObject applicationChart(String applicationConditionVo) {
		ApplicationConditionVo applicationCondition = JSON.parseObject(
				applicationConditionVo, ApplicationConditionVo.class);
		ApplicationConditionBo acBo = toDeviceToApplicationConditionBo(applicationCondition);
		ApplicationChartDataBo applicationChartDataBo = iappApi
				.getApplicationChartData(acBo);
		BaseAction.getHttpServletRequest().getSession()
				.setAttribute(APP_NETFLOW_CHART, applicationChartDataBo);
		return toSuccess(applicationChartDataBo);
	}

	@RequestMapping("/getterminalbyapp")
	public JSONObject getTerminalByDevice(
			ApplicationConditionVo applicationConditionVo) {
		ApplicationConditionBo apcBo = toDeviceToApplicationConditionBo(applicationConditionVo);
		BaseAction.getHttpServletRequest().getSession()
				.setAttribute(APP_TERMAINL_NETFLOW_TABLE_THREE, apcBo);
		ApplicationPageBo applicationPageBo = iappApi.getTerminalsByApp(apcBo);
		List<String> terminalsIp = new ArrayList<String>();
		List<ApplicationModelBo> dnfs = (List<ApplicationModelBo>) applicationPageBo
				.getRows();
		for (ApplicationModelBo bo : dnfs) {
			terminalsIp.add(bo.getTerminal_name());
		}
		apcBo.setTerminalsIp(terminalsIp);
		applicationPageBo.setAppBo(apcBo);
		BaseAction
				.getHttpServletRequest()
				.getSession()
				.setAttribute(AppExportAction.APP_TERMINAL_TABLE_THREE,
						applicationPageBo);
		return toSuccess(applicationPageBo);
	}

	@RequestMapping("/getterminalchart")
	public JSONObject terminalChartOnColumByDevice(String applicationConditionVo) {
		ApplicationConditionVo applicationCondition = JSON.parseObject(
				applicationConditionVo, ApplicationConditionVo.class);
		ApplicationConditionBo apcBo = toDeviceToApplicationConditionBo(applicationCondition);
		ApplicationChartDataBo applicationChartDataBo = iappApi
				.getTerminalChartonDataByApp(apcBo);
		applicationChartDataBo.setSortColum(applicationCondition.getSort());
		BaseAction
				.getHttpServletRequest()
				.getSession()
				.setAttribute(AppExportAction.APP_TERMINAL_CHART_THREE,
						applicationChartDataBo);
		return toSuccess(applicationChartDataBo);
	}

	@RequestMapping("/getsessionbyapp")
	public JSONObject getsessionByDevice(
			ApplicationConditionVo applicationConditionVo) {
		ApplicationConditionBo apcBo = toDeviceToApplicationConditionBo(applicationConditionVo);
		BaseAction.getHttpServletRequest().getSession()
				.setAttribute(APP_SESSION_NETFLOW_TABLE_THREE, apcBo);
		ApplicationPageBo applicationPageBo = iappApi.getSessionsByApp(apcBo);
		List<ApplicationModelBo> sessions = (List<ApplicationModelBo>) applicationPageBo
				.getRows();
		List<Map<String, String>> sessionips = new ArrayList<Map<String, String>>();
		for (ApplicationModelBo bo : sessions) {
			Map<String, String> currentSession = new HashMap<String, String>();
			currentSession.put(bo.getSrc_ip(), bo.getDst_ip());
			sessionips.add(currentSession);
		}
		apcBo.setSessionips(sessionips);
		applicationPageBo.setAppBo(apcBo);
		BaseAction
				.getHttpServletRequest()
				.getSession()
				.setAttribute(AppExportAction.APP_SESSION_TABLE_THREE,
						applicationPageBo);
		return toSuccess(applicationPageBo);
	}

	@RequestMapping("/getsessionchartbyapp")
	public JSONObject getsessionChartOnColumByDevice(
			String applicationConditionVo) {
		ApplicationConditionVo applicationCondition = JSON.parseObject(
				applicationConditionVo, ApplicationConditionVo.class);
		ApplicationConditionBo apcBo = toDeviceToApplicationConditionBo(applicationCondition);
		ApplicationChartDataBo applicationChartDataBo = iappApi
				.getSessionChartonDataByApp(apcBo);
		applicationChartDataBo.setSortColum(applicationCondition.getSort());
		BaseAction
				.getHttpServletRequest()
				.getSession()
				.setAttribute(AppExportAction.APP_SESSION_CHART_THREE,
						applicationChartDataBo);
		return toSuccess(applicationChartDataBo);
	}

	@RequestMapping("/getipgbyapp")
	public JSONObject getIPGByDevice(
			ApplicationConditionVo applicationConditionVo) {
		ApplicationConditionBo apcBo = toDeviceToApplicationConditionBo(applicationConditionVo);
		BaseAction.getHttpServletRequest().getSession()
				.setAttribute(APP_IP_GROUP_NETFLOW_TABLE_THREE, apcBo);
		ApplicationPageBo applicationPageBo = iappApi.getIPGsByApp(apcBo);
		List<String> ipgsIp = new ArrayList<String>();
		List<ApplicationModelBo> ipgs = (List<ApplicationModelBo>) applicationPageBo
				.getRows();
		for (ApplicationModelBo bo : ipgs) {
			ipgsIp.add(bo.getIpgroup_id());
		}
		apcBo.setIpgsIp(ipgsIp);
		applicationPageBo.setAppBo(apcBo);
		BaseAction
				.getHttpServletRequest()
				.getSession()
				.setAttribute(AppExportAction.APP_IP_GROUP_TABLE_THREE,
						applicationPageBo);
		return toSuccess(applicationPageBo);
	}

	@RequestMapping("/getipgchartbyapp")
	public JSONObject getIPGChartDataByApp(String applicationConditionVo) {
		ApplicationConditionVo applicationCondition = JSON.parseObject(
				applicationConditionVo, ApplicationConditionVo.class);
		ApplicationConditionBo apcBo = toDeviceToApplicationConditionBo(applicationCondition);
		ApplicationChartDataBo applicationChartDataBo = iappApi
				.getipgChartDataByApp(apcBo);
		applicationChartDataBo.setSortColum(applicationCondition.getSort());
		BaseAction
				.getHttpServletRequest()
				.getSession()
				.setAttribute(AppExportAction.APP_IP_GROUP_CHART_THREE,
						applicationChartDataBo);
		return toSuccess(applicationChartDataBo);
	}

	public ApplicationConditionBo toDeviceToApplicationConditionBo(
			ApplicationConditionVo applicationConditionVo) {
		if (applicationConditionVo == null)
			return null;
		ApplicationConditionBo applicationConditionBo = new ApplicationConditionBo();
		if (null != applicationConditionVo.getTimePerid()) {
			applicationConditionBo.setTimePerid(applicationConditionVo
					.getTimePerid().trim());
		}
		applicationConditionBo
				.setDeviceIp(applicationConditionVo.getDeviceIp());
		applicationConditionBo.setEndtime(applicationConditionVo.getEndtime());
		if (applicationConditionVo.getRecordCount() != null) {
			applicationConditionBo.setRecordCount(new Long(
					applicationConditionVo.getRecordCount()).longValue());
		} else {
			applicationConditionBo.setRecordCount(new Long(0).longValue());
		}
		if (applicationConditionVo.getRowCount() != null) {
			applicationConditionBo.setRowCount(new Long(applicationConditionVo
					.getRowCount()).longValue());
		} else {
			applicationConditionBo.setRowCount(new Long(0).longValue());
		}
		applicationConditionBo.setSort(applicationConditionVo.getSort());
		applicationConditionBo.setOrder(applicationConditionVo.getOrder());
		applicationConditionBo.setStartRow(new Long(applicationConditionVo
				.getStartRow()).longValue());
		applicationConditionBo.setStarttime(applicationConditionVo
				.getStarttime());
		applicationConditionBo.setShowpagination(applicationConditionVo
				.getShowpagination());
		if (applicationConditionVo.getApp_id() != null) {
			applicationConditionBo
					.setApp_id(applicationConditionVo.getApp_id());
		}
		applicationConditionBo.setAllapplicationflows(applicationConditionVo
				.getAllapplicationflows());
		applicationConditionBo.setAllprotoflows(applicationConditionVo
				.getAllprotoflows());
		applicationConditionBo.setApplicationsIp(applicationConditionVo
				.getApplicationsIp());
		applicationConditionBo
				.setProtosIp(applicationConditionVo.getProtosIp());
		applicationConditionBo.setTableSubfixTime(applicationConditionVo
				.getTableSubfixTime());
		applicationConditionBo
				.setTimepart(applicationConditionVo.getTimepart());
		applicationConditionBo.setTerminalsIp(applicationConditionVo
				.getTerminalsIp());
		applicationConditionBo.setAllterminalsFlows(applicationConditionVo
				.getAllterminalsFlows());
		applicationConditionBo.setAllsessionFlows(applicationConditionVo
				.getAllsessionFlows());
		applicationConditionBo.setSessionips(applicationConditionVo
				.getSessionips());
		applicationConditionBo.setAllipgsFlows(applicationConditionVo
				.getAllipgsFlows());
		applicationConditionBo.setIpgsIp(applicationConditionVo.getIpgsIp());
		applicationConditionBo.setIf_id(applicationConditionVo.getIf_id());
		if (null != applicationConditionVo.getApp_name()) {
			applicationConditionBo.setApp_name(applicationConditionVo
					.getApp_name());
		}
		return applicationConditionBo;
	}

	public IApplicationApi getIappApi() {
		return iappApi;
	}

	public void setIappApi(IApplicationApi iappApi) {
		this.iappApi = iappApi;
	}

	private static final String APP_NETFLOW_CHART = "APP_NETFLOW_CHART";
	private static final String APP_NETFLOW_TABLE = "APP_NETFLOW_TABLE";
	private static final String APP_NETFLOW_TABLE_QUERY = "APP_NETFLOW_TABLE_QUERY";
	private static final String APP_TERMAINL_NETFLOW_TABLE_THREE = "APP_TERMAINL_NETFLOW_TABLE_THREE";
	private static final String APP_SESSION_NETFLOW_TABLE_THREE = "APP_SESSION_NETFLOW_TABLE_THREE";
	private static final String APP_IP_GROUP_NETFLOW_TABLE_THREE = "APP_IP_GROUP_NETFLOW_TABLE_THREE";
	private static final String APP_PROTO_NETFLOW_TABLE_THREE = "APP_PROTO_NETFLOW_TABLE_THREE";

	@RequestMapping("exportProto")
	public void exportProto(HttpServletResponse resp, String type)
			throws IOException, NoSuchMethodException, SecurityException,
			IllegalAccessException, IllegalArgumentException,
			InvocationTargetException {
		ApplicationConditionBo acBo = (ApplicationConditionBo) BaseAction
				.getHttpServletRequest().getSession()
				.getAttribute(APP_PROTO_NETFLOW_TABLE_THREE);
		acBo.setStartRow(0);
		acBo.setRowCount(Long.MAX_VALUE);
		ApplicationPageBo page = iappApi.getProtocolByApp(acBo);
		PDFHelper pdf = new PDFHelper("应用流量");
		pdf.addTable(new TableAdapter().adapter("协议", getColumnProto(type),
				page.getRows()));
		DownUtil.down(resp, "app_proto.pdf", pdf.generate());
	}

	@RequestMapping("exportIpGroup")
	public void exportIpGroup(HttpServletResponse resp, String type)
			throws IOException, NoSuchMethodException, SecurityException,
			IllegalAccessException, IllegalArgumentException,
			InvocationTargetException {
		ApplicationConditionBo apcBo = (ApplicationConditionBo) BaseAction
				.getHttpServletRequest().getSession()
				.getAttribute(APP_IP_GROUP_NETFLOW_TABLE_THREE);
		apcBo.setStartRow(0);
		apcBo.setRowCount(Long.MAX_VALUE);
		ApplicationPageBo page = iappApi.getIPGsByApp(apcBo);
		PDFHelper pdf = new PDFHelper("应用流量");
		pdf.addTable(new TableAdapter().adapter("IP分组", getColumnIpGroup(type),
				page.getRows()));
		DownUtil.down(resp, "app_ip_group.pdf", pdf.generate());
	}

	@RequestMapping("exportSession")
	public void exportSession(HttpServletResponse resp, String type)
			throws IOException, NoSuchMethodException, SecurityException,
			IllegalAccessException, IllegalArgumentException,
			InvocationTargetException {
		ApplicationConditionBo apcBo = (ApplicationConditionBo) BaseAction
				.getHttpServletRequest().getSession()
				.getAttribute(APP_SESSION_NETFLOW_TABLE_THREE);
		apcBo.setStartRow(0);
		apcBo.setRowCount(Long.MAX_VALUE);
		ApplicationPageBo page = iappApi.getSessionsByApp(apcBo);
		PDFHelper pdf = new PDFHelper("应用流量");
		pdf.addTable(new TableAdapter().adapter("会话", getColumnSession(type),
				page.getRows()));
		DownUtil.down(resp, "app_session.pdf", pdf.generate());
	}

	@RequestMapping("exportTermainl")
	public void exportTermainl(HttpServletResponse resp, String type)
			throws IOException, NoSuchMethodException, SecurityException,
			IllegalAccessException, IllegalArgumentException,
			InvocationTargetException {
		ApplicationConditionBo apcBo = (ApplicationConditionBo) BaseAction
				.getHttpServletRequest().getSession()
				.getAttribute(APP_TERMAINL_NETFLOW_TABLE_THREE);
		apcBo.setStartRow(0);
		apcBo.setRowCount(Long.MAX_VALUE);
		ApplicationPageBo page = iappApi.getTerminalsByApp(apcBo);
		PDFHelper pdf = new PDFHelper("应用流量");
		pdf.addTable(new TableAdapter().adapter("终端", getColumnTerminal(type),
				page.getRows()));
		DownUtil.down(resp, "app_termainl.pdf", pdf.generate());
	}

	@RequestMapping("exportAll")
	public void exportAll(HttpServletResponse resp, String type)
			throws IOException, NoSuchMethodException, SecurityException,
			IllegalAccessException, IllegalArgumentException,
			InvocationTargetException {
		ApplicationConditionBo apcBo = (ApplicationConditionBo) BaseAction
				.getHttpServletRequest().getSession()
				.getAttribute(APP_NETFLOW_TABLE_QUERY);
		apcBo.setStartRow(0);
		apcBo.setRowCount(Long.MAX_VALUE);
		ApplicationPageBo applicationPageBo = iappApi.getAllApplication(apcBo);
		List<String> appids = new ArrayList<String>();
		List<ApplicationModelBo> apps = (List<ApplicationModelBo>) applicationPageBo
				.getRows();
		for (ApplicationModelBo bo : apps) {
			appids.add(bo.getApp_id());
		}
		apcBo.setApplicationsIp(appids);
		applicationPageBo.setAppBo(apcBo);
		PDFHelper pdf = new PDFHelper("应用流量");
		pdf.addTable(new TableAdapter().adapter("应用流量数据", getColumnApp(type),
				applicationPageBo.getRows()));
		DownUtil.down(resp, "app.pdf", pdf.generate());
	}

	@RequestMapping("export")
	public void export(HttpServletResponse resp, String type)
			throws IOException, NoSuchMethodException, SecurityException,
			IllegalAccessException, IllegalArgumentException,
			InvocationTargetException {
		HttpSession session = BaseAction.getHttpServletRequest().getSession();
		PDFHelper pdf = new PDFHelper("应用流量");
		ApplicationChartDataBo chart = (ApplicationChartDataBo) session
				.getAttribute(APP_NETFLOW_CHART);
		pdf.addLine("应用流量数据折线图", "时间", "流量(MB)",
				new LineAdapter().adapter(chart));
		ApplicationPageBo page = (ApplicationPageBo) session
				.getAttribute(APP_NETFLOW_TABLE);
		pdf.addCircular("应用流量数据环形图", new CircularAdapter().adapter("app_name",
				StringUtil.isNull(page.getSort()) ? page.getAppBo().getSort()
						: page.getSort(), page.getRows()));
		pdf.addTable(new TableAdapter().adapter("应用流量数据", getColumnApp(type),
				page.getRows()));
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
}
